package es.weso.dataProviders;

import java.util.Collection;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import es.weso.model.Country;
import es.weso.model.CountryCollection;
import es.weso.util.Conf;

/**
 * Client to get data from the wi-queries project
 * 
 * @author <a href="http://alejandro-montes.appspot.com">Alejandro Montes
 *         García</a>
 * @version 1.0
 * @since 20/04/2013
 */
public class RestClient {

	public Country getCountry(String year, String country) {
		String endpoint = Conf.getConfig("web.services.url") + "version" + year
				+ "/country/" + country + ".xml";
		Client client = Client.create();
		WebResource webResource = client.resource(endpoint);
		ClientResponse response = webResource.accept("application/xml").get(
				ClientResponse.class);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed. HTTP error code: "
					+ response.getStatus());
		}
		return response.getEntity(Country.class);
	}

	public Collection<Country> getCountriesFromRegion(String regionName) {
		String endpoint = Conf.getConfig("web.services.url") + regionName
				+ "/countries.xml";
		Client client = Client.create();
		WebResource webResource = client.resource(endpoint);
		ClientResponse response = webResource.accept("application/xml").get(
				ClientResponse.class);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed. HTTP error code: "
					+ response.getStatus());
		}
		CountryCollection cc = response.getEntity(CountryCollection.class);
		return cc.getCountries();
	}
}
