package es.weso.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.json.simple.JSONValue;

/**
 * A single rank (position and value) that can be serialised
 * 
 * @author <a href="http://alejandro-montes.appspot.com">Alejandro Montes
 *         García</a>
 * @version 1.0
 * @since 27/03/2013
 */
@XmlRootElement
public class Rank implements Serializable {

	private static final long serialVersionUID = -574892962507256140L;
	private int position;
	private double value;

	public Rank() {

	}

	public Rank(int position, double value) {
		this.position = position;
		this.value = value;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("position", position);
		properties.put("value", value);
		return JSONValue.toJSONString(properties);
	}
}
