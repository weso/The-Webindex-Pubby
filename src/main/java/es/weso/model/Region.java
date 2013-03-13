package es.weso.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

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
public class Region {

	private static Logger log = Logger.getLogger(Region.class);

	/**
	 * Gets the <tt>ISO 3166-1 alpha-2</tt> codes of the countries in this
	 * {@link Region}
	 * 
	 * @param properties
	 *            The {@link de.fuberlin.wiwiss.pubby.ResourceDescription
	 *            ResourceDescription} as a {@link Map}
	 * @return An array with the <tt>ISO 3166-1 alpha-2</tt> codes of the
	 *         countries in this {@link Region}
	 */
	public String[] getCountryCodes(Map<String, List<Value>> properties) {
		log.info("Retrieving countries from region");
		Map<String, String> isoCodeConverter = getIsoCodeEquivalences();
		List<Value> countries = properties.get(Conf.getVocab("has-country"));
		String[] countryCodes = new String[countries.size()];
		int i = 0;
		for (Value country : countries) {
			countryCodes[i++] = isoCodeConverter.get(fromUriToISO3(country
					.getNode().getURI()));
		}
		return countryCodes;
	}

	/**
	 * Gets a {@link Map} containing the <tt>ISO 3166-1 alpha-3</tt> codes of
	 * all the {@link Country countries} in the webindex and their
	 * <tt>ISO 3166-1 alpha-2</tt> equivalents
	 * 
	 * @return The {@link Map} with the <tt>ISO 3166-1 alpha-3</tt> -
	 *         <tt>ISO 3166-1 alpha-2</tt> equivalences
	 */
	private Map<String, String> getIsoCodeEquivalences() {
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(
				AddrUtil.getAddresses(Conf.getConfig("cache.server")));
		Map<String, String> isoCodeConverter;
		try {
			MemcachedClient memcachedClient = builder.build();
			isoCodeConverter = checkCache(memcachedClient);
			memcachedClient.shutdown();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			log.error("Cannot connect with the cache, trying to query webfoundation");
			isoCodeConverter = tryQuery();
		}
		return isoCodeConverter;
	}

	/**
	 * Checks the cache for the {@link Map} of ISO codes equivalences and stores
	 * them if the were not cached
	 * 
	 * @param memcachedClient
	 *            The {@link MemcachedClient}
	 * @return The {@link Map} of ISO codes equivalences
	 */
	private Map<String, String> checkCache(MemcachedClient memcachedClient) {
		String key = "isoCodes";
		log.info("Getting " + key + " from cache");
		Map<String, String> isoCodeConverter;
		try {
			isoCodeConverter = memcachedClient.get(key);
			if (isoCodeConverter == null) {
				log.info(key + " is not in the cache, querying web foundation");
				isoCodeConverter = tryQuery(memcachedClient, key);
			}
		} catch (TimeoutException e) {
			log.error(e.getMessage(), e);
			log.error("Cache timed out, trying to query webfoundation");
			isoCodeConverter = tryQuery();
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
			log.error("Trying to query webfoundation");
			isoCodeConverter = tryQuery();
		} catch (MemcachedException e) {
			log.error(e.getMessage(), e);
			log.error("Trying to query webfoundation");
			isoCodeConverter = tryQuery();
		}
		return isoCodeConverter;
	}

	/**
	 * Queries the webfoundation to get the ISO code equivalences
	 * 
	 * @return The {@link Map} of equivalences, an empty {@link Map} if the
	 *         query fails
	 */
	private Map<String, String> tryQuery() {
		Map<String, String> isoCodeConverter = new HashMap<String, String>();
		try {
			isoCodeConverter = performISOCodeQuery();
		} catch (QueryException e) {
			log.error(e.getMessage(), e);
			log.error("Webfoundation query failed");
		}
		return isoCodeConverter;
	}

	/**
	 * Queries the webfoundation to get the ISO code equivalences and stores the
	 * result in the cache
	 * 
	 * @param memcachedClient
	 *            The {@link MemcachedClient}
	 * @param key
	 *            The entryname in the cache
	 * 
	 * @return The {@link Map} of equivalences, an empty {@link Map} if the
	 *         query fails
	 */
	private Map<String, String> tryQuery(MemcachedClient client, String key)
			throws TimeoutException, InterruptedException, MemcachedException {
		Map<String, String> isoCodeConverter;
		try {
			isoCodeConverter = performISOCodeQuery();
			if (isoCodeConverter != null && isoCodeConverter.size() > 0) {
				log.info("Storing " + isoCodeConverter + " in the cache");
				client.set(key, 2592000, isoCodeConverter);
			}
		} catch (QueryException e) {
			log.error(e.getMessage(), e);
			log.error("Webfoundation query failed");
			isoCodeConverter = new HashMap<String, String>();
		}
		return isoCodeConverter;
	}

	/**
	 * Queries the webfoundation to get the ISO code equivalences
	 * 
	 * @return The {@link Map} of equivalences
	 * @throws QueryException
	 */
	private Map<String, String> performISOCodeQuery() {
		String queryStr = Conf.getQuery("codes");
		log.info("Trying query " + queryStr);
		Query query = QueryFactory.create(queryStr, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				Conf.getConfig("webfoundation"), query);
		ResultSet rs = qexec.execSelect();
		Map<String, String> isoCodeConverter = new HashMap<String, String>();
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			String iso3Code = fromUriToISO3(qs.getResource("resource")
					.toString());
			isoCodeConverter.put(iso3Code, qs.get("code").toString());
		}
		return isoCodeConverter;
	}

	/**
	 * Converts an uri of the type {hostname}/pubby/page/area/country/ESP into a
	 * ISO3 code
	 * 
	 * @param uri
	 *            The uri to be converted
	 * @return The resulting ISO3 code
	 */
	private String fromUriToISO3(String uri) {
		String[] splittedUri = uri.split("/");
		return splittedUri[splittedUri.length - 1];
	}
}
