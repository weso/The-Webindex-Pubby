package es.weso.model.cacheable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.apache.log4j.Logger;
import org.json.simple.JSONValue;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryException;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;

import es.weso.util.Conf;

/**
 * Country data in a way that can be serialised
 * 
 * @author <a href="http://alejandro-montes.appspot.com">Alejandro Montes
 *         García</a>
 * @since 20/03/2013
 * @version 1.0
 */
public class SerializableCountry {

	private Map<String, Object> properties;
	private String countryName;
	private transient static Logger log = Logger
			.getLogger(SerializableCountry.class);

	public SerializableCountry(String countryName) {
		if (countryName.startsWith("RDF description of")) {
			countryName = countryName.replace("RDF description of", "").trim();
		}
		this.countryName = countryName;
		checkCache();
	}

	/**
	 * Checks the cache to get the data of a country. If it were not cached,
	 * performs the appropiate queries and stores the data for next calls
	 */
	private void checkCache() {
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(
				AddrUtil.getAddresses(Conf.getConfig("cache.server")));
		try {
			MemcachedClient memcachedClient = builder.build();
			properties = memcachedClient.get(countryName.replace(" ", ""));
			if (properties == null) {
				tryQueryProperties(memcachedClient);
			}
			memcachedClient.shutdown();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			tryQueryProperties();
		} catch (TimeoutException e) {
			log.error(e.getMessage(), e);
			tryQueryProperties();
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
			tryQueryProperties();
		} catch (MemcachedException e) {
			log.error(e.getMessage(), e);
			tryQueryProperties();
		}
	}

	/**
	 * Tries to query the web foundation to get the properties of a country
	 */
	private void tryQueryProperties() {
		log.info("Trying to query WebFoundation");
		try {
			properties = queryProperties();
		} catch (QueryException e) {
			log.error("Properties were not found, will not save data in the cache");
		}
	}

	/**
	 * Tries to query the web foundation to get the properties of a country and
	 * stores them in the cache
	 * 
	 * @param memcachedClient
	 *            The {@link MemcachedClient} that stores the data in the cache
	 * @throws TimeoutException
	 * @throws InterruptedException
	 * @throws MemcachedException
	 */
	private void tryQueryProperties(MemcachedClient memcachedClient)
			throws TimeoutException, InterruptedException, MemcachedException {
		log.info("Properties not found in the cache");
		log.info("Trying to query WebFoundation");
		try {
			properties = queryProperties();
			memcachedClient.add(countryName.replace(" ", ""), 2592000,
					properties);
		} catch (QueryException e) {
			log.error("Properties were not found, will not save data in the cache");
		}
	}

	/**
	 * Performs SPARQL queries to get the properties of a country
	 * 
	 * @return A {@link Map} containing the properties of a country
	 */
	private Map<String, Object> queryProperties() {
		log.info("Querying properties");
		properties = new HashMap<String, Object>();
		properties.put("country", countryName);
		try {
			getData();
			properties.put("webindex", getWebIndex());
		} catch (QueryException e) {
			log.error(e.getMessage(), e);
			log.error("Properties could not be retrieved");
			properties = new HashMap<String, Object>();
			properties.put("Error", "Properties could not be retrieved");
			throw e;
		}
		return properties;
	}

	@Override
	public String toString() {
		return JSONValue.toJSONString(properties);
	}

	/**
	 * Gets the web index properties of a country
	 * 
	 * @return A {@link Map} containing the ranks and scores of a country
	 */
	private Map<String, Map<String, String>> getWebIndex() {
		String[] ranks = { "global", "readiness", "web",
				"institutionalInfrastructure", "comunicationsInfrastructure",
				"webContent", "webUse", "politicalImpact", "economicImpact",
				"impact", "socialImpact" };
		Map<String, Map<String, String>> countryRanking = new HashMap<String, Map<String, String>>();
		for (String rankType : ranks) {
			countryRanking.put(rankType, getRankOfType(rankType));
		}
		return countryRanking;
	}

	/**
	 * Gets the score and position of a country in a specific category
	 * 
	 * @param rankType
	 *            The category that is being measured
	 * @return The score and position of a country in a specific category
	 */
	private Map<String, String> getRankOfType(String rankType) {
		String queryStr = Conf.getQuery(rankType);
		Query query = QueryFactory.create(queryStr, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				Conf.getConfig("webfoundation"), query);
		SerializableRank sr = new SerializableRank(qexec.execSelect());
		Rank individualRank = sr.getData().get(countryName);
		Map<String, String> rank = new HashMap<String, String>();
		rank.put("rank", "" + individualRank.getPosition());
		rank.put("score", individualRank.getValue());
		return rank;
	}

	/**
	 * Gets the metadata of a country, not related to the web index scores
	 */
	private void getData() {
		String queryStr = Conf.getQuery("codeAndRegion", countryName);
		Query query = QueryFactory.create(queryStr, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				Conf.getConfig("webfoundation"), query);
		ResultSet rs = qexec.execSelect();
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			properties.put("code", qs.getLiteral("code").getString());
			properties.put("region", qs.getLiteral("regionName").getString()
					.replace("\"", "").replaceAll("@\\w{2}", ""));
		}
	}
}
