package de.fuberlin.wiwiss.pubby.servlets;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

import de.fuberlin.wiwiss.pubby.Configuration;
import de.fuberlin.wiwiss.pubby.HypermediaResource;
import de.fuberlin.wiwiss.pubby.MappedResource;
import de.fuberlin.wiwiss.pubby.ResourceDescription;
import de.fuberlin.wiwiss.pubby.ResourceDescription.Value;
import es.weso.dataProviders.DataProvider;
import es.weso.util.Conf;

/**
 * A servlet for serving the HTML page describing a resource. Invokes a Velocity
 * template.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id$
 */
public class PageURLServlet extends BaseURLServlet {

	private static final long serialVersionUID = -96020648404097843L;
	private static Logger log = Logger.getLogger(PageURLServlet.class);

	public boolean doGet(HypermediaResource controller,
			Collection<MappedResource> resources, HttpServletRequest request,
			HttpServletResponse response, Configuration config)
			throws ServletException, IOException {

		Model description = getResourceDescription(resources);

		if (description.size() == 0) {
			return false;
		}

		Velocity.setProperty("velocimacro.context.localscope", Boolean.TRUE);

		ResourceDescription resourceDescription = new ResourceDescription(
				controller, description, config);
		String discoLink = "http://www4.wiwiss.fu-berlin.de/rdf_browser/?browse_uri="
				+ URLEncoder.encode(controller.getAbsoluteIRI(), "utf-8");
		String tabulatorLink = "http://dig.csail.mit.edu/2005/ajar/ajaw/tab.html?uri="
				+ URLEncoder.encode(controller.getAbsoluteIRI(), "utf-8");
		String openLinkLink = "http://linkeddata.uriburner.com/ode/?uri="
				+ URLEncoder.encode(controller.getAbsoluteIRI(), "utf-8");
		VelocityHelper template = new VelocityHelper(getServletContext(),
				response);
		Context context = template.getVelocityContext();
		context.put("project_name", config.getProjectName());
		context.put("project_link", config.getProjectLink());
		context.put("uri", resourceDescription.getURI());
		context.put("server_base", config.getWebApplicationBaseURI());
		context.put("rdf_link", controller.getDataURL());
		context.put("disco_link", discoLink);
		context.put("tabulator_link", tabulatorLink);
		context.put("openlink_link", openLinkLink);
		context.put("sparql_endpoint", getFirstSPARQLEndpoint(resources));
		context.put("title", resourceDescription.getLabel());
		context.put("comment", resourceDescription.getComment());
		context.put("image", resourceDescription.getImageURL());
		context.put("properties", resourceDescription.getProperties());
		getProperties(resourceDescription, context);
		try {
			Model metadata = ModelFactory.createDefaultModel();
			for (MappedResource resource : resources) {
				Resource documentRepresentation = resource.getDataset()
						.addMetadataFromTemplate(metadata, resource,
								getServletContext());
				context.put("metadata", documentRepresentation);
			}

			Map<String, String> nsSet = metadata.getNsPrefixMap();
			nsSet.putAll(description.getNsPrefixMap());
			context.put("prefixes", nsSet.entrySet());
			context.put("blankNodesMap", new HashMap<Resource, String>());
		} catch (Exception e) {
			context.put("metadata", Boolean.FALSE);
		}

		template.renderXHTML("page.vm");
		return true;
	}

	/**
	 * Checks the type of an entity and gets the data from it
	 * 
	 * @author <a href="http://alejandro-montes.appspot.com">Alejandro Montes
	 *         García</a>
	 * @param resourceDescription
	 *            The {@link ResourceDescription} of the entity
	 * @param context
	 *            The {@link Context} where the metadata will be stored
	 */
	private void getProperties(ResourceDescription resourceDescription,
			Context context) {
		Map<String, List<Value>> properties = resourceDescription.asMap();
		String type = properties.get(Conf.getVocab("rdf.type")).get(0)
				.getLocalName();
		DataProvider dp = new DataProvider(properties);
		try {
			Map<String, Object> data = dp.getData(type.toLowerCase());
			for (Map.Entry<String, Object> entry : data.entrySet()) {
				context.put(entry.getKey(), entry.getValue());
			}
		} catch (ReflectiveOperationException e) {
			log.error("Method not found, returning default visualization", e);
			context.put("type", ".");
		} catch (RuntimeException e) {
			log.error(e.getMessage() + "Returning default visualization", e);
			context.put("type", ".");
		}
	}
}
