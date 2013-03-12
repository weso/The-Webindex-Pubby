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
import es.weso.util.QueriesLoader;
import es.weso.util.Vocab;

public class Region {
	public String[] getCountryCodes(Map<String, List<Value>> properties) {
		Map<String, String> isoCodeConverter = getIsoCodeEquivalences();
		List<Value> countries = properties.get(Vocab
				.getVocab("has-country"));
		String[] countryCodes = new String[countries.size()];
		int i = 0;
		for (Value country : countries) {
			countryCodes[i++] = isoCodeConverter.get(fromUriToISO3(country
					.getNode().getURI()));
		}
		return countryCodes;
	}

	private Map<String, String> getIsoCodeEquivalences() {
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(
				AddrUtil.getAddresses("localhost:11211"));
		Map<String, String> isoCodeConverter;
		try {
			MemcachedClient memcachedClient = builder.build();
			isoCodeConverter = checkCache(memcachedClient);
			memcachedClient.shutdown();
		} catch (IOException e) {
			isoCodeConverter = tryQuery();
		}
		return isoCodeConverter;
	}

	private Map<String, String> checkCache(MemcachedClient memcachedClient) {
		String key = "isoCodes";
		Map<String, String> isoCodeConverter;
		try {
			isoCodeConverter = memcachedClient.get(key);
			if (isoCodeConverter == null) {
				isoCodeConverter = tryQuery(memcachedClient, key);
			}
		} catch (TimeoutException e) {
			isoCodeConverter = tryQuery();
		} catch (InterruptedException e) {
			isoCodeConverter = tryQuery();
		} catch (MemcachedException e) {
			isoCodeConverter = tryQuery();
		}
		return isoCodeConverter;
	}

	private Map<String, String> tryQuery() {
		Map<String, String> isoCodeConverter;
		try {
			isoCodeConverter = performISOCodeQuery();
		} catch (QueryException e) {
			isoCodeConverter = new HashMap<String, String>();
			// TODO Log error
		}
		return isoCodeConverter;
	}

	private Map<String, String> tryQuery(MemcachedClient client, String key)
			throws TimeoutException, InterruptedException, MemcachedException {
		Map<String, String> isoCodeConverter;
		try {
			isoCodeConverter = performISOCodeQuery();
			client.set(key, 2592000, isoCodeConverter);
		} catch (QueryException e) {
			isoCodeConverter = new HashMap<String, String>();
			// TODO Log error
		}
		return isoCodeConverter;
	}

	private Map<String, String> performISOCodeQuery() {
		String queryStr = QueriesLoader.getQuery("codes");
		Query query = QueryFactory.create(queryStr, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				"http://data.webfoundation.org/sparql", query);
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

	private String fromUriToISO3(String uri) {
		String[] splittedUri = uri.split("/");
		return splittedUri[splittedUri.length - 1];
	}
}
