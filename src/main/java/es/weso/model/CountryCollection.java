package es.weso.model;

import java.util.Collection;
import java.util.HashSet;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
/**
 * Class that automatically maps a {@link Collection} of {@link Country Countries}
 * 
 * @author <a href="http://alejandro-montes.appspot.com">Alejandro Montes
 *         García</a>
 * @version 1.0
 * @since 25/04/2013
 */
public class CountryCollection {

	private Collection<Country> countries;

	public CountryCollection() {
		this.countries = new HashSet<Country>();
	}

	public Collection<Country> getCountries() {
		return countries;
	}

	public void setCountries(Collection<Country> countries) {
		this.countries = countries;
	}

}
