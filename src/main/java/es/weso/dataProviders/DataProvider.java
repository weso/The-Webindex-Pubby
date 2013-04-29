package es.weso.dataProviders;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.fuberlin.wiwiss.pubby.ResourceDescription.Value;
import es.weso.model.Country;
import es.weso.util.Conf;

/**
 * Provides additional data to the page servlet
 * 
 * @author <a href="http://alejandro-montes.appspot.com">Alejandro Montes
 *         García</a>
 * @version 1.0
 * @since 24/04/2013
 */
public class DataProvider {

	private static Logger log = Logger.getLogger(DataProvider.class);
	private Map<String, List<Value>> properties;

	public DataProvider(Map<String, List<Value>> properties) {
		this.properties = properties;
	}

	/**
	 * Gets the data of a entity of the given type
	 * 
	 * @param type
	 *            The type of the entity
	 * 
	 * @return The data of the entity
	 * @throws ReflectiveOperationException
	 */
	public Map<String, Object> getData(String type)
			throws ReflectiveOperationException {
		Method dataRetriever = this.getClass().getMethod(type + "Data");
		Map<String, Object> data = (Map<String, Object>) dataRetriever
				.invoke(this);
		data.put("type", type);
		return data;
	}

	/**
	 * Gets the data of a {@link Country}
	 * 
	 * @return The data of the {@link Country}
	 */
	public Map<String, Object> countryData() {
		String countryCode = properties.get(Conf.getVocab("iso-alpha2")).get(0)
				.getNode().toString().toLowerCase();
		RestClient client = new RestClient();
		Map<String, Object> context = new HashMap<String, Object>();
		Country country = client.getCountry("2011",
				countryCode.replaceAll("\"", ""));
		context.put("lat", country.getLat());
		context.put("lon", country.getLon());
		context.put("country_code", country.getCode_iso_alpha2());
		getRank(context, country, "global");
		getRank(context, country, "readiness");
		getRank(context, country, "web");
		getRank(context, country, "institutionalInfrastructure");
		getRank(context, country, "comunicationsInfrastructure");
		getRank(context, country, "webUse");
		getRank(context, country, "webContent");
		getRank(context, country, "impact");
		getRank(context, country, "socialImpact");
		getRank(context, country, "economicImpact");
		getRank(context, country, "politicalImpact");
		context.put("observations", country.getObservations());
		return context;
	}

	/**
	 * Gets the rank and the score of a {@link Country}
	 * 
	 * @param context
	 *            The {@link Map} where the metadata will be stored
	 * @param country
	 *            The {@link Country} to get the ranks from
	 * @param rankName
	 *            The name of the rank
	 */
	private void getRank(Map<String, Object> context, Country country,
			String rankName) {
		context.put(rankName + "Rank", country.getRanks().get(rankName)
				.getPosition());
		context.put(rankName + "Score", country.getRanks().get(rankName)
				.getValue());
	}

	/**
	 * Gets the <tt>ISO 3166-1 alpha-2</tt> codes of the countries in this
	 * region
	 * 
	 * @return An array with the <tt>ISO 3166-1 alpha-2</tt> codes of the
	 *         countries in this region
	 */
	public Map<String, Object> regionData() {
		String regionName = properties.get(Conf.getVocab("rdfs.label")).get(0)
				.getNode().getLiteral().toString(false).replaceAll("@en", "");
		log.info("Retrieving countries from region " + regionName);
		RestClient client = new RestClient();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("countries", client.getCountriesFromRegion(regionName));
		return data;
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

	/**
	 * Gets the data of an {@link Organization} from the cache or performing the
	 * appropiate queries
	 * 
	 * @return A {@link Map} with the {@link Organization} metadata
	 */
	public Map<String, Object> organizationData() {
		log.info("Retrieving organization info");
		Map<String, Object> info = new HashMap<String, Object>();
		if (properties.containsKey(Conf.getVocab("foaf.homepage"))) {
			String homepage = properties.get(Conf.getVocab("foaf.homepage"))
					.get(0).toString();
			info.put("homepage", homepage);
		}
		return info;
	}

	/**
	 * Gets the data of a {@link Person} from the data provided by the
	 * properties parameter
	 * 
	 * @return A {@link Map} with the {@link Person} metadata
	 */
	public Map<String, Object> personData() {
		log.info("Getting person info");
		Map<String, Object> data = new HashMap<String, Object>();
		addProperty(properties, data, "name", Conf.getVocab("foaf.name"));
		addProperty(properties, data, "foaf_title", Conf.getVocab("foaf.title"));
		addProperty(properties, data, "family_name",
				Conf.getVocab("foaf.family_name"));
		addProperty(properties, data, "homepage",
				Conf.getVocab("foaf.homepage"));
		return data;
	}

	/**
	 * Adds a property to the {@link Map} to be returned if it exists
	 * 
	 * @param properties
	 *            The {@link de.fuberlin.wiwiss.pubby.ResourceDescription
	 *            ResourceDescription} as a {@link Map}
	 * @param data
	 *            The {@link Map} to be returned
	 * @param propName
	 *            The name of the property in the velocity template
	 * @param vocab
	 *            The predicate to get the value of the property
	 */
	private void addProperty(Map<String, List<Value>> properties,
			Map<String, Object> data, String propName, String vocab) {
		if (properties.containsKey(vocab)) {
			data.put(propName, properties.get(vocab).get(0).getNode()
					.toString().replace("\"", ""));
		}
	}

}
