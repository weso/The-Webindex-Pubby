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

public class Organization {

	public Map<String, String> getOrganizationInfo(
			Map<String, List<Value>> properties) {
		Map<String, String> info = new HashMap<String, String>();
		if (properties.containsKey(Vocab.getVocab("ref-dbpedia"))) {
			String ref_dbpedia = properties.get(Vocab.getVocab("ref-dbpedia"))
					.get(0).getNode().toString();
			MemcachedClientBuilder builder = new XMemcachedClientBuilder(
					AddrUtil.getAddresses("localhost:11211"));
			try {
				MemcachedClient memcachedClient = builder.build();
				checkCacheForImg(info, ref_dbpedia, memcachedClient);
				memcachedClient.shutdown();
			} catch (IOException e) {
				// TODO Log error
			}
		}
		if (properties.containsKey(Vocab.getVocab("foaf.homepage"))) {
			String homepage = properties.get(Vocab.getVocab("foaf.homepage"))
					.get(0).toString();
			info.put("homepage", homepage);
		}
		return info;
	}

	private void checkCacheForImg(Map<String, String> info, String ref_dbpedia,
			MemcachedClient memcachedClient) {
		String key = "logo" + ref_dbpedia;
		String image;
		try {
			image = memcachedClient.get(key);
			if (image == null) {
				image = tryQuery(ref_dbpedia);
				if (image != null && image.trim().length() > 0) {
					info.put("orgImage", image);
					memcachedClient.set(key, 2592000, image);
				}
			} else {
				info.put("orgImage", image);
			}
		} catch (TimeoutException e) {
			image = tryQuery(ref_dbpedia);
		} catch (InterruptedException e) {
			image = tryQuery(ref_dbpedia);
		} catch (MemcachedException e) {
			image = tryQuery(ref_dbpedia);
		}
	}

	private String tryQuery(String ref_dbpedia) {
		String queryStr = QueriesLoader.getQuery("org.image", ref_dbpedia);
		String image = "";
		try {
			Query query = QueryFactory.create(queryStr, Syntax.syntaxARQ);
			QueryExecution qexec = QueryExecutionFactory.sparqlService(
					"http://dbpedia.org/sparql", query);
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
			// TODO log error
		}
		return image;
	}
}
