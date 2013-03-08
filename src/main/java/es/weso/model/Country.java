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
import es.weso.util.QueriesLoader;
import es.weso.util.VocabLoader;

public class Country {

	public Map<String, String> getCountryData(
			Map<String, List<Value>> properties) {
		Map<String, String> context = new HashMap<String, String>();
		String name = properties.get(VocabLoader.getVocab("rdfs.label")).get(0)
				.getNode().toString();

		String contry_code = properties.get(VocabLoader.getVocab("iso-alpha2"))
				.get(0).getNode().toString();

		String lat = properties.get(VocabLoader.getVocab("geo.lat")).get(0)
				.getNode().getLiteralValue().toString();

		String lon = properties.get(VocabLoader.getVocab("geo.long")).get(0)
				.getNode().getLiteralValue().toString();

		context.put("lat", lat);
		context.put("lon", lon);
		context.put("country_code", contry_code);
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(
				AddrUtil.getAddresses("localhost:11211"));

		try {
			MemcachedClient memcachedClient = builder.build();
			checkCacheForFlag(name, memcachedClient, context);
			name = name.replace("\"", "").replaceAll("@\\w{2}", "");
			String[] ranks = { "global", "readiness", "web",
					"institutionalInfrastructure",
					"comunicationsInfrastructure", "webContent", "webUse",
					"politicalImpact", "economicImpact", "impact",
					"socialImpact" };
			for (String rank : ranks) {
				checkCacheForRank(context, name, rank, memcachedClient);
			}
			memcachedClient.shutdown();
		} catch (IOException e) {
			// TODO Log error
		}
		return context;
	}

	private void checkCacheForFlag(String countryName,
			MemcachedClient memcachedClient, Map<String, String> context) {
		String key = "flag" + countryName.replaceAll(" ", "");
		String flag;
		try {
			flag = memcachedClient.get(key);
			if (flag == null) {
				try {
					flag = performFlagQuery(countryName);
					memcachedClient.set(key, 2592000, flag);
				} catch (QueryException e) {
					flag = "";
					// TODO Log error
				}
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

	private void checkCacheForRank(Map<String, String> context, String name,
			String type, MemcachedClient memcachedClient) {
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

	private void parseRank(Map<String, String> context, SerializableRank rank,
			String countryName, String type) {
		Rank r = rank.getData().get(countryName);
		context.put(type + "Rank", "" + r.getPosition());
		context.put(type + "Score", r.getValue());
	}

	private SerializableRank performRankQuery(String queryStr) {
		Query query = QueryFactory.create(queryStr, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				"http://data.webfoundation.org/sparql", query);
		return new SerializableRank(qexec.execSelect());
	}

}
