package es.weso.model;

import java.util.Collection;
import java.util.HashSet;

/**
 * Class that automatically maps a {@link Collection} of {@link Observation}s
 * 
 * @author <a href="http://alejandro-montes.appspot.com">Alejandro Montes
 *         García</a>
 * @version 1.0
 * @since 10/04/2013
 */
public class ObservationCollection {

	private Collection<Observation> observations;

	public ObservationCollection() {
		observations = new HashSet<Observation>();
	}

	public Collection<Observation> getObservations() {
		return observations;
	}

	public void setObservations(Collection<Observation> observations) {
		this.observations = observations;
	}

}
