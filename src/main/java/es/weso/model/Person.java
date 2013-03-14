package es.weso.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.fuberlin.wiwiss.pubby.ResourceDescription.Value;
import es.weso.util.Conf;

/**
 * 
 * @author <a href="http://alejandro-montes.appspot.com">Alejandro Montes
 *         García</a>
 * 
 */
public class Person {

	private static Logger log = Logger.getLogger(Person.class);

	/**
	 * Gets the data of a {@link Person} from the data provided by the
	 * properties parameter
	 * 
	 * @param properties
	 *            The {@link de.fuberlin.wiwiss.pubby.ResourceDescription
	 *            ResourceDescription} as a {@link Map}
	 * @return A {@link Map} with the {@link Person} metadata
	 */
	public Map<String, String> getPersonData(Map<String, List<Value>> properties) {
		log.info("Getting person info");
		Map<String, String> data = new HashMap<String, String>();
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
			Map<String, String> data, String propName, String vocab) {
		if (properties.containsKey(vocab)) {
			data.put(propName, properties.get(vocab).get(0).getNode()
					.toString().replace("\"", ""));
		}
	}

}
