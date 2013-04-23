package es.weso.model;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * Representation of an observation
 * 
 * @author <a href="http://alejandro-montes.appspot.com">Alejandro Montes
 *         García</a>
 * @version 1.0
 * @since 04/04/2013
 */
@XmlRootElement
public class Observation {

	private String uri;
	private String label;
	private String countryName, countryUri, indicatorName, indicatorUri, year;
	private double value;
	
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getCountryUri() {
		return countryUri;
	}

	public void setCountryUri(String countryUri) {
		this.countryUri = countryUri;
	}

	public String getIndicatorName() {
		return indicatorName;
	}

	public void setIndicatorName(String indicatorName) {
		this.indicatorName = indicatorName;
	}

	public String getIndicatorUri() {
		return indicatorUri;
	}

	public void setIndicatorUri(String indicatorUri) {
		this.indicatorUri = indicatorUri;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((countryName == null) ? 0 : countryName.hashCode());
		result = prime * result
				+ ((countryUri == null) ? 0 : countryUri.hashCode());
		result = prime * result
				+ ((indicatorName == null) ? 0 : indicatorName.hashCode());
		result = prime * result
				+ ((indicatorUri == null) ? 0 : indicatorUri.hashCode());
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((year == null) ? 0 : year.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Observation other = (Observation) obj;
		if (countryName == null) {
			if (other.countryName != null)
				return false;
		} else if (!countryName.equals(other.countryName))
			return false;
		if (countryUri == null) {
			if (other.countryUri != null)
				return false;
		} else if (!countryUri.equals(other.countryUri))
			return false;
		if (indicatorName == null) {
			if (other.indicatorName != null)
				return false;
		} else if (!indicatorName.equals(other.indicatorName))
			return false;
		if (indicatorUri == null) {
			if (other.indicatorUri != null)
				return false;
		} else if (!indicatorUri.equals(other.indicatorUri))
			return false;
		if (Double.doubleToLongBits(value) != Double
				.doubleToLongBits(other.value))
			return false;
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
		return true;
	}

}
