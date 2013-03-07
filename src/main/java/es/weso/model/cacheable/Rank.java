package es.weso.model.cacheable;

import java.io.Serializable;

public class Rank implements Serializable {

	private static final long serialVersionUID = -574892962507256140L;
	private int position;
	private String value;

	public Rank(int position, String value) {
		this.position = position;
		this.value = value;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
