package de.fuberlin.wiwiss.pubby.servlets;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

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
import es.weso.model.cacheable.Rank;
import es.weso.model.cacheable.SerializableRank;
import es.weso.sparql.QueriesLoader;

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
		Map<String, List<Value>> properties = resourceDescription.asMap();
		String type = properties
				.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type").get(0)
				.getLocalName();
		if (type.equalsIgnoreCase("country")) {
			// Meter en el contexto los datos que sean necesarios
			getCountryData(context, properties);
			context.put("type", "country");
		} else {
			context.put("type", ".");
		}

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

	private void getCountryData(Context context,
			Map<String, List<Value>> properties) {
		String name = properties
				.get("http://www.w3.org/2000/01/rdf-schema#label").get(0)
				.getNode().toString();

		MemcachedClientBuilder builder = new XMemcachedClientBuilder(
				AddrUtil.getAddresses("localhost:11211"));

		try {
			MemcachedClient memcachedClient = builder.build();
			checkCacheForFlag(name, memcachedClient, context);
			name = name.replace("\"", "").replaceAll("@\\w{2}", "");
			chechCacheForRank(context, name, "global", memcachedClient);
			chechCacheForRank(context, name, "readiness", memcachedClient);
			chechCacheForRank(context, name, "web", memcachedClient);
			chechCacheForRank(context, name, "institutionalInfrastructure",
					memcachedClient);
			chechCacheForRank(context, name, "comunicationsInfrastructure",
					memcachedClient);
			chechCacheForRank(context, name, "webContent", memcachedClient);
			chechCacheForRank(context, name, "webUse", memcachedClient);
			chechCacheForRank(context, name, "politicalImpact", memcachedClient);
			chechCacheForRank(context, name, "economicImpact", memcachedClient);
			chechCacheForRank(context, name, "impact", memcachedClient);
			chechCacheForRank(context, name, "socialImpact", memcachedClient);
			memcachedClient.shutdown();
		} catch (IOException e) {
			// TODO Log error
		}
	}

	private void checkCacheForFlag(String countryName,
			MemcachedClient memcachedClient, Context context) {
		String key = "flag" + countryName.replaceAll(" ", "");
		String flag;
		try {
			flag = memcachedClient.get(key);
			if (flag == null) {
				flag = performFlagQuery(countryName);
				memcachedClient.set(key, 2592000, flag);
			}
		} catch (TimeoutException e) {
			// TODO Log errors
			flag = performFlagQuery(countryName);
		} catch (InterruptedException e) {
			flag = performFlagQuery(countryName);
		} catch (MemcachedException e) {
			flag = performFlagQuery(countryName);
		}
		context.put("flagSrc", flag);
	}

	private String performFlagQuery(String countryName) {
		String queryStr = QueriesLoader.getQuery("flag", countryName);
		Query query = QueryFactory.create(queryStr, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				"http://dbpedia.org/sparql", query);
		ResultSet rs = qexec.execSelect();
		String src = "";
		while (rs.hasNext()) {
			QuerySolution qs = rs.nextSolution();
			src = qs.getResource("flag").toString();
			if(src.toLowerCase().contains("flag")) {
				break;
			}
		}
		return src;
	}

	private void chechCacheForRank(Context context, String name, String type,
			MemcachedClient memcachedClient) {
		String queryStr = QueriesLoader.getQuery(type);
		String key = "Query" + queryStr.hashCode();
		SerializableRank rank;
		try {
			rank = memcachedClient.get(key);
			if (rank == null) {
				rank = performRankQuery(queryStr);
				memcachedClient.set(key, 2592000, rank);
			}
		} catch (TimeoutException e) {
			// TODO Log errors
			rank = performRankQuery(queryStr);
		} catch (InterruptedException e) {
			rank = performRankQuery(queryStr);
		} catch (MemcachedException e) {
			rank = performRankQuery(queryStr);
		}
		parseRank(context, rank, name, type);
	}

	private void parseRank(Context context, SerializableRank rank,
			String countryName, String type) {
		Rank r = rank.getData().get(countryName);
		context.put(type + "Rank", r.getPosition());
		context.put(type + "Score", r.getValue());
	}

	private SerializableRank performRankQuery(String queryStr) {
		Query query = QueryFactory.create(queryStr, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				"http://data.webfoundation.org/sparql", query);
		return new SerializableRank(qexec.execSelect());
	}
}
