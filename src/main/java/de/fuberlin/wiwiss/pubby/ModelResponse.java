package de.fuberlin.wiwiss.pubby;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.NoSuchElementException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.shared.JenaException;

import de.fuberlin.wiwiss.pubby.negotiation.ContentTypeNegotiator;
import de.fuberlin.wiwiss.pubby.negotiation.MediaRangeSpec;
import de.fuberlin.wiwiss.pubby.negotiation.PubbyNegotiator;
import de.fuberlin.wiwiss.pubby.servlets.RequestParamHandler;
import es.weso.model.cacheable.SerializableCountry;
import es.weso.util.Conf;

/**
 * Calls into Joseki to send a Jena model over HTTP. This gives us content
 * negotiation and all the other tricks supported by Joseki for free. This has
 * to be in the Joseki package because some required methods are not visible.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id: ModelResponse.java,v 1.1 2007/02/07 13:49:24 cyganiak Exp $
 */
public class ModelResponse {
	private final Model model;
	private final HttpServletRequest request;
	private final HttpServletResponse response;

	public ModelResponse(Model model, HttpServletRequest request,
			HttpServletResponse response) {

		// Handle ?output=format request parameter
		RequestParamHandler handler = new RequestParamHandler(request);
		if (handler.isMatchingRequest()) {
			request = handler.getModifiedRequest();
		}

		this.model = model;
		this.request = request;
		this.response = response;
	}

	public void serve() {
		// Error hendling is still quite a mess here.
		try {
			doResponseModel();
		} catch (IOException ioEx) {
			throw new RuntimeException(ioEx);
		} catch (JenaException jEx) {
			try {
				response.sendError(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"JenaException: " + jEx.getMessage());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void doResponseModel() throws IOException {
		response.addHeader("Vary", "Accept");
		ContentTypeNegotiator negotiator = PubbyNegotiator.getDataNegotiator();
		MediaRangeSpec bestMatch = negotiator.getBestMatch(
				request.getHeader("Accept"), request.getHeader("User-Agent"));
		if (bestMatch == null) {
			response.setStatus(406);
			response.setContentType("text/plain");
			ServletOutputStream out = response.getOutputStream();
			out.println("406 Not Acceptable: The requested data format is not supported.");
			out.println("Supported formats are RDF/XML, Turtle, N3, and N-Triples.");
			return;
		}
		response.setContentType(bestMatch.getMediaType());
		getWriter(bestMatch.getMediaType()).write(model, response);
		response.getOutputStream().flush();
	}

	private ModelWriter getWriter(String mediaType) {
		if ("application/rdf+xml".equals(mediaType)) {
			return new RDFXMLWriter();
		}
		if ("application/x-turtle".equals(mediaType)) {
			return new TurtleWriter();
		}
		if ("text/rdf+n3;charset=utf-8".equals(mediaType)) {
			return new TurtleWriter();
		}
		if ("application/javascript".equals(mediaType)) {
			return new JSONPWriter();
		}
		return new NTriplesWriter();
	}

	private interface ModelWriter {
		void write(Model model, HttpServletResponse response)
				throws IOException;
	}

	private class NTriplesWriter implements ModelWriter {
		public void write(Model model, HttpServletResponse response)
				throws IOException {
			model.getWriter("N-TRIPLES").write(model,
					response.getOutputStream(), null);
		}
	}

	private class TurtleWriter implements ModelWriter {
		public void write(Model model, HttpServletResponse response)
				throws IOException {
			model.getWriter("TURTLE").write(model, response.getOutputStream(),
					null);
		}
	}

	private class RDFXMLWriter implements ModelWriter {
		public void write(Model model, HttpServletResponse response)
				throws IOException {
			RDFWriter writer = model.getWriter("RDF/XML-ABBREV");
			writer.setProperty("showXmlDeclaration", "true");
			// From Joseki -- workaround for the j.cook.up bug.
			writer.setProperty("blockRules", "propertyAttr");
			writer.write(
					model,
					new OutputStreamWriter(response.getOutputStream(), "utf-8"),
					null);
		}
	}

	/**
	 * Writes the response in JSONP
	 * 
	 * @author <a href="http://alejandro-montes.appspot.com">Alejandro Montes
	 *         García</a>
	 * @since 20/03/2013
	 * @version 1.0
	 */
	private class JSONPWriter implements ModelWriter {

		public void write(Model model, HttpServletResponse response)
				throws IOException {
			response.setContentType("application/javascript");
			OutputStream os = response.getOutputStream();
			os.write("callback(".getBytes());
			if (isCountry(model)) {
				try {
					Property p = new PropertyImpl(Conf.getVocab("iso-alpha2"));
					String code = model.listSubjectsWithProperty(p).next()
							.getProperty(p).getString();
					os.write(new SerializableCountry("2011", code).toString()
							.getBytes());
				} catch (NoSuchElementException e) {
					os.write("{\"Error\" : \"Unknown country\"}".getBytes());
				}
			} else {
				os.write("{\"Error\" : \"JSONP is only supported for countries\"}"
						.getBytes());
			}
			os.write(");".getBytes());
			os.close();
		}

		private boolean isCountry(Model model) {
			Property p = new PropertyImpl(Conf.getVocab("rdf.type"));
			ResIterator iter = model.listSubjectsWithProperty(p);
			while (iter.hasNext()) {
				Statement stmt = iter.next().getProperty(p);
				return stmt.getResource().toString().endsWith("/Country");
			}
			return false;
		}

	}
}
