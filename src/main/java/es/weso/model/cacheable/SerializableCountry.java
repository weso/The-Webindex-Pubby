package es.weso.model.cacheable;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;

import es.weso.dataProviders.RestClient;
import es.weso.model.Country;

/**
 * Country data in a way that can be serialised that produces JSON
 * 
 * @author <a href="http://alejandro-montes.appspot.com">Alejandro Montes
 *         García</a>
 * @since 20/03/2013
 * @version 1.0
 */
public class SerializableCountry {

	private Map<String, Object> properties;

	public SerializableCountry(String year, String countryCode) {
		Country country = new RestClient().getCountry(year, countryCode);
		properties = new HashMap<String, Object>();
		properties.put("code_iso_alpha2", country.getCode_iso_alpha2());
		properties.put("code_iso_alpha3", country.getCode_iso_alpha3());
		properties.put("lat", country.getLat());
		properties.put("lon", country.getLon());
		properties.put("name", country.getName());
		properties.put("observations", country.getObservations());
		properties.put("ranks", country.getRanks());
		properties.put("region", country.getRegionName());
		properties.put("year", country.getYear());
	}

	@Override
	public String toString() {
		return JSONValue.toJSONString(properties);
	}
}
