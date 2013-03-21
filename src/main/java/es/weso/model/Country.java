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
import es.weso.model.cacheable.Rank;
import es.weso.model.cacheable.SerializableRank;
import es.weso.util.Conf;

/**
 * 
 * @author <a href="http://alejandro-montes.appspot.com">Alejandro Montes
 *         García</a>
 * 
 */
public class Country {

	private static Logger log = Logger.getLogger(Country.class);

	/**
	 * Gets the data of a {@link Country} from the cache or performing the
	 * appropiate queries
	 * 
	 * @param properties
	 *            The {@link de.fuberlin.wiwiss.pubby.ResourceDescription
	 *            ResourceDescription} as a {@link Map}
	 * @return A {@link Map} of the {@link Country} metadata
	 */
	public Map<String, String> getCountryData(
			Map<String, List<Value>> properties) {
		log.info("Getting country data");
		Map<String, String> context = new HashMap<String, String>();
		String name = properties.get(Conf.getVocab("rdfs.label")).get(0)
				.getNode().toString();
		String contry_code = properties.get(Conf.getVocab("iso-alpha2")).get(0)
				.getNode().toString();
		String lat = properties.get(Conf.getVocab("geo.lat")).get(0).getNode()
				.getLiteralValue().toString();
		String lon = properties.get(Conf.getVocab("geo.long")).get(0).getNode()
				.getLiteralValue().toString();
		context.put("lat", lat);
		context.put("lon", lon);
		context.put("country_code", contry_code);
		getCachedData(context, name);
		return context;
	}

	/**
	 * Saves the data that can be cached into the {@link Map} to be returned
	 * 
	 * @param context
	 *            The {@link Map} of properties to be returned
	 * @param name
	 *            The name of the country
	 */
	private void getCachedData(Map<String, String> context, String name) {
		String[] ranks = { "global", "readiness", "web",
				"institutionalInfrastructure", "comunicationsInfrastructure",
				"webContent", "webUse", "politicalImpact", "economicImpact",
				"impact", "socialImpact" };

		MemcachedClientBuilder builder = new XMemcachedClientBuilder(
				AddrUtil.getAddresses(Conf.getConfig("cache.server")));
		try {
			MemcachedClient memcachedClient = builder.build();
			checkCacheForFlag(name, memcachedClient, context);
			name = name.replace("\"", "").replaceAll("@\\w{2}", "");
			for (String rank : ranks) {
				checkCacheForRank(context, name, rank, memcachedClient);
			}
			memcachedClient.shutdown();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			log.error("Cannot connect with the cache, trying to query endpoints");
			context.put("flag", tryQuery(name));
			for (String rank : ranks) {
				parseRank(context, performRankQuery(Conf.getQuery(rank)), name,
						rank);
			}
		}
	}

	/**
	 * Checks if a flag is cached and if not, stores it
	 * 
	 * @param countryName
	 *            The name of the flag's country
	 * @param memcachedClient
	 *            The {@link MemcachedClient}
	 * @param context
	 *            The {@link Map} of properties to be returned
	 */
	private void checkCacheForFlag(String countryName,
			MemcachedClient memcachedClient, Map<String, String> context) {
		String key = "flag" + countryName.replaceAll(" ", "");
		log.info("Getting " + key + " from cache");
		String flag;
		try {
			flag = memcachedClient.get(key);
			if (flag == null) {
				log.info(key + " is not in the cache, trying to query DBpedia");
				flag = tryQuery(countryName, memcachedClient, key);
			}
		} catch (TimeoutException e) {
			log.error(e.getMessage(), e);
			log.error("Cache timed out, trying to query DBpedia");
			flag = tryQuery(countryName);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
			log.error("Trying to query DBpedia");
			flag = tryQuery(countryName);
		} catch (MemcachedException e) {
			log.error(e.getMessage(), e);
			log.error("Trying to query DBpedia");
			flag = tryQuery(countryName);
		}
		context.put("flagSrc", flag);
	}

	/**
	 * Queries the DBpedia for a flag and stores the result in the cache
	 * 
	 * @param countryName
	 *            The name of the flag's country
	 * @param memcachedClient
	 *            The {@link MemcachedClient}
	 * @param key
	 *            The entryname in the cache
	 * @return The url of the flag, an empty String if the query fails
	 * @throws TimeoutException
	 * @throws InterruptedException
	 * @throws MemcachedException
	 */
	private String tryQuery(String countryName,
			MemcachedClient memcachedClient, String key)
			throws TimeoutException, InterruptedException, MemcachedException {
		String flag = "";
		try {
			flag = performFlagQuery(countryName);
			if (flag != null && flag.trim().length() > 0) {
				log.info("DBpedia query was sucessful, storing " + flag
						+ " in the cache");
				memcachedClient.set(key, 2592000, flag);
			}
		} catch (QueryException e) {
			log.error(e.getMessage(), e);
			log.error("DBpedia query failed");
		}
		return flag;
	}

	/**
	 * Queries the DBpedia for a flag
	 * 
	 * @param countryName
	 *            The name of the flag's country
	 * @return The url of the flag, an empty String if the query fails
	 */
	private String tryQuery(String countryName) {
		String flag = "";
		try {
			flag = performFlagQuery(countryName);
		} catch (QueryException e) {
			log.error(e.getMessage(), e);
			log.error("DBpedia query failed");
		}
		return flag;
	}

	/**
	 * Queries the DBpedia for a flag
	 * 
	 * @param countryName
	 *            The name of the flag's country
	 * @return The url of the flag
	 * @throws QueryException
	 */
	private String performFlagQuery(String countryName) {
		String queryStr = Conf.getQuery("flag", countryName);
		log.info("Trying query " + queryStr);
		Query query = QueryFactory.create(queryStr, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				Conf.getConfig("dbpedia"), query);
		qexec.setTimeout(5000);
		String src = "";
		ResultSet rs = qexec.execSelect();
		while (rs.hasNext()) {
			QuerySolution qs = rs.nextSolution();
			src = qs.getResource("flag").toString();
			if (src.toLowerCase().contains("flag")) {
				break;
			}
		}
		return src;
	}

	/**
	 * Checks if a rank is cached and if not, it stores it
	 * 
	 * @param context
	 *            The {@link Map} of properties to be returned
	 * @param name
	 *            The name of the country
	 * @param type
	 *            The type of rank
	 * @param memcachedClient
	 *            The {@link MemcachedClient}
	 */
	private void checkCacheForRank(Map<String, String> context, String name,
			String type, MemcachedClient memcachedClient) {
		String queryStr = Conf.getQuery(type);
		String key = "Query" + queryStr.hashCode();
		log.info("Getting " + key + " from cache");
		SerializableRank rank;
		try {
			rank = memcachedClient.get(key);
			if (rank == null) {
				log.info(key + " is not in the cache, "
						+ "trying to query webfoundation");
				rank = performRankQuery(queryStr);
				if (rank != null && rank.getData().entrySet().size() > 0) {
					log.info("Webfoundation query was sucessful, storing "
							+ rank + " in the cache");
					memcachedClient.set(key, 2592000, rank);
				}
			}
		} catch (TimeoutException e) {
			log.error(e.getMessage(), e);
			log.error("Cache timed out, trying to query webfoundation");
			rank = performRankQuery(queryStr);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
			log.error("Trying to query webfoundation");
			rank = performRankQuery(queryStr);
		} catch (MemcachedException e) {
			log.error(e.getMessage(), e);
			log.error("Trying to query webfoundation");
			rank = performRankQuery(queryStr);
		}
		parseRank(context, rank, name, type);
	}

	/**
	 * Stores the rank of a country in the @{link Map} of properties to be
	 * returned
	 * 
	 * @param context
	 *            The {@link Map} of properties to be returned
	 * @param rank
	 *            The {@link SerializableRank} to get the data from
	 * @param countryName
	 *            The name of the country
	 * @param type
	 *            The type of rank
	 */
	private void parseRank(Map<String, String> context, SerializableRank rank,
			String countryName, String type) {
		Rank r = rank.getData().get(countryName);
		context.put(type + "Rank", "" + r.getPosition());
		context.put(type + "Score", r.getValue());
	}

	/**
	 * Queries webfoundation for a rank
	 * 
	 * @param queryStr
	 *            The query to execute
	 * @return The rank in a way that can be serialized and stored in the cache
	 */
	private SerializableRank performRankQuery(String queryStr) {
		log.info("Trying query " + queryStr);
		Query query = QueryFactory.create(queryStr, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				Conf.getConfig("webfoundation"), query);
		return new SerializableRank(qexec.execSelect());
	}

}
