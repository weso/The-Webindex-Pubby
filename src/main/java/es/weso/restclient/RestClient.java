package es.weso.restclient;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import es.weso.model.Country;
import es.weso.util.Conf;

public class RestClient {

	public Country getCountry(String year, String country) {
		String endpoint = Conf.getConfig("web.services.url") + "country/"
				+ year + "/" + country + ".xml";
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
}