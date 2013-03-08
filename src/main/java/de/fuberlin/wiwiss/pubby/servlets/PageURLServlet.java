package de.fuberlin.wiwiss.pubby.servlets;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

import de.fuberlin.wiwiss.pubby.Configuration;
import de.fuberlin.wiwiss.pubby.MappedResource;
import de.fuberlin.wiwiss.pubby.ResourceDescription;
import de.fuberlin.wiwiss.pubby.ResourceDescription.Value;
import es.weso.model.Country;
import es.weso.util.QueriesLoader;
import es.weso.util.VocabLoader;

/**
 * A servlet for serving the HTML page describing a resource. Invokes a Velocity
 * template.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id$
 */
public class PageURLServlet extends BaseURLServlet {

	private static final long serialVersionUID = 3363621132360159793L;

	public boolean doGet(MappedResource resource, HttpServletRequest request,
			HttpServletResponse response, Configuration config)
			throws ServletException, IOException {

		Model description = getResourceDescription(resource);

		if (description.size() == 0) {
			return false;
		}

		Velocity.setProperty("velocimacro.context.localscope", Boolean.TRUE);

		ResourceDescription resourceDescription = new ResourceDescription(
				resource, description, config);
		String discoLink = "http://www4.wiwiss.fu-berlin.de/rdf_browser/?browse_uri="
				+ URLEncoder.encode(resource.getWebURI(), "utf-8");
		String tabulatorLink = "http://dig.csail.mit.edu/2005/ajar/ajaw/tab.html?uri="
				+ URLEncoder.encode(resource.getWebURI(), "utf-8");
		String openLinkLink = "http://demo.openlinksw.com/rdfbrowser/?uri="
				+ URLEncoder.encode(resource.getWebURI(), "utf-8");
		VelocityHelper template = new VelocityHelper(getServletContext(),
				response);
		Context context = template.getVelocityContext();
		context.put("project_name", config.getProjectName());
		context.put("project_link", config.getProjectLink());
		context.put("uri", resourceDescription.getURI());
		context.put("server_base", config.getWebApplicationBaseURI());
		context.put("rdf_link", resource.getDataURL());
		context.put("disco_link", discoLink);
		context.put("tabulator_link", tabulatorLink);
		context.put("openlink_link", openLinkLink);
		context.put("sparql_endpoint", resource.getDataset().getDataSource()
				.getEndpointURL());
		context.put("title", resourceDescription.getLabel());
		context.put("comment", resourceDescription.getComment());
		context.put("image", resourceDescription.getImageURL());
		context.put("properties", resourceDescription.getProperties());
		getProperties(resourceDescription, context);

		try {
			Model metadata = ModelFactory.createDefaultModel();
			Resource documentRepresentation = resource.getDataset()
					.addMetadataFromTemplate(metadata, resource,
							getServletContext());
			// Replaced the commented line by the following one because the
			// RDF graph we want to talk about is a specific representation
			// of the data identified by the getDataURL() URI.
			// Olaf, May 28, 2010
			// context.put("metadata",
			// metadata.getResource(resource.getDataURL()));
			context.put("metadata", documentRepresentation);

			Map<String, String> nsSet = metadata.getNsPrefixMap();
			nsSet.putAll(description.getNsPrefixMap());
			context.put("prefixes", nsSet.entrySet());
			context.put("blankNodesMap", new HashMap());
		} catch (Exception e) {
			context.put("metadata", Boolean.FALSE);
		}

		template.renderXHTML("page.vm");
		return true;
	}

	/**
	 * Checks the type of an entity and gets the data from it
	 * 
	 * @author Alejandro Montes García <alejandro.montes@weso.es>
	 * @param resourceDescription
	 * @param context
	 */
	private void getProperties(ResourceDescription resourceDescription,
			Context context) {
		Map<String, List<Value>> properties = resourceDescription.asMap();
		String type = properties.get(VocabLoader.getVocab("rdf.type")).get(0)
				.getLocalName();
		if (type.equalsIgnoreCase("country")) {
			Map<String, String> countryProperties = new Country()
					.getCountryData(properties);
			for (Map.Entry<String, String> entry : countryProperties.entrySet()) {
				context.put(entry.getKey(), entry.getValue());
			}
			context.put("type", "country");
		} else if (type.equalsIgnoreCase("region")){
			
			context.put("type", "region");
		} else {
			context.put("type", ".");
		}
	}
}
