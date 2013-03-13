package es.weso.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryException;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;

import de.fuberlin.wiwiss.pubby.ResourceDescription.Value;
import es.weso.util.Conf;

/**
 * 
 * @author <a href="http://alejandro-montes.appspot.com">Alejandro Montes
 *         García</a>
 * 
 */
public class Organization {

	private static Logger log = Logger.getLogger(Organization.class);

	/**
	 * Gets the data of an {@link Organization} from the cache or performing the
	 * appropiate queries
	 * 
	 * @param properties
	 *            The {@link de.fuberlin.wiwiss.pubby.ResourceDescription
	 *            ResourceDescription} as a {@link Map}
	 * @return A {@link Map} with the {@link Organization} metadata
	 */
	public Map<String, String> getOrganizationInfo(
			Map<String, List<Value>> properties) {
		log.info("Retrieving organization info");
		Map<String, String> info = new HashMap<String, String>();
		if (properties.containsKey(Conf.getVocab("ref-dbpedia"))) {
			getImage(properties.get(Conf.getVocab("ref-dbpedia")).get(0)
					.getNode().toString(), info);
		}
		if (properties.containsKey(Conf.getVocab("foaf.homepage"))) {
			String homepage = properties.get(Conf.getVocab("foaf.homepage"))
					.get(0).toString();
			info.put("homepage", homepage);
		}
		return info;
	}

	/**
	 * Gets the image of the {@link Organization}
	 * 
	 * @param ref_dbpedia
	 *            The dbpedia resource corresponding this {@link Organization}
	 * @param info
	 *            The {@link Map} of properties to be returned
	 */
	private void getImage(String ref_dbpedia, Map<String, String> info) {
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(
				AddrUtil.getAddresses(Conf.getConfig("cache.server")));
		try {
			MemcachedClient memcachedClient = builder.build();
			checkCacheForImg(info, ref_dbpedia, memcachedClient);
			memcachedClient.shutdown();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			log.error("Cannot connect with the cache, trying to query DBpedia");
			String image = tryQuery(ref_dbpedia);
			if (image != null && image.trim().length() > 0) {
				info.put("orgImage", image);
			}
		}
	}

	/**
	 * Checks the cache for an image of an {@link Organization} and stores it if
	 * it was not cached
	 * 
	 * @param ref_dbpedia
	 *            The dbpedia resource corresponding this {@link Organization}
	 * @param info
	 *            The {@link Map} of properties to be returned
	 * @param memcachedClient
	 *            The {@link MemcachedClient}
	 */
	private void checkCacheForImg(Map<String, String> info, String ref_dbpedia,
			MemcachedClient memcachedClient) {
		String key = "logo" + ref_dbpedia;
		log.info("Getting " + key + " from cache");
		String image;
		try {
			image = memcachedClient.get(key);
			if (image == null) {
				log.info(key + " is not in the cache, trying to query DBpedia");
				image = tryQuery(ref_dbpedia);
				if (image != null && image.trim().length() > 0) {
					log.info("DBpedia query was sucessful, storing " + image
							+ " in the cache");
					info.put("orgImage", image);
					memcachedClient.set(key, 2592000, image);
				}
			} else {
				info.put("orgImage", image);
			}
		} catch (TimeoutException e) {
			log.error(e.getMessage(), e);
			log.error("Cache timed out, trying to query DBpedia");
			image = tryQuery(ref_dbpedia);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
			log.error("Trying to query DBpedia");
			image = tryQuery(ref_dbpedia);
		} catch (MemcachedException e) {
			log.error(e.getMessage(), e);
			log.error("Trying to query DBpedia");
			image = tryQuery(ref_dbpedia);
		}
	}

	/**
	 * Tries to query the DBpedia for an image of an {@link Organization}
	 * 
	 * @param ref_dbpedia
	 *            The dbpedia resource corresponding this {@link Organization}
	 * @return The url of the logo of the {@link Organization}, an empty string
	 *         if the query fails
	 */
	private String tryQuery(String ref_dbpedia) {
		String queryStr = Conf.getQuery("org.image", ref_dbpedia);
		log.info("Trying query " + queryStr);
		String image = "";
		try {
			Query query = QueryFactory.create(queryStr, Syntax.syntaxARQ);
			QueryExecution qexec = QueryExecutionFactory.sparqlService(
					Conf.getConfig("dbpedia"), query);
			qexec.setTimeout(5000);
			ResultSet rs = qexec.execSelect();
			while (rs.hasNext()) {
				QuerySolution qs = rs.nextSolution();
				image = qs.getResource("img").toString();
				if (image.contains("logo")) {
					break;
				}
			}
		} catch (QueryException e) {
			log.error(e.getMessage(), e);
			log.error("DBpedia query failed");
		}
		return image;
	}
}
