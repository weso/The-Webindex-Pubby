package es.weso.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 
 * @author <a href="http://alejandro-montes.appspot.com">Alejandro Montes
 *         García</a>
 * 
 */
public class Conf {

	private static Properties vocab, config;

	private static final String VOCAB_FILE = "vocab.properties";
	private static final String CONFIG_FILE = "config.properties";

	/**
	 * Gets predicates from the vocabulary file
	 * 
	 * @param propertyName
	 *            The name of the predicate
	 * @return The predicate
	 */
	public static String getVocab(String propertyName) {
		return getPropertyFromFile(VOCAB_FILE, vocab, propertyName);
	}

	/**
	 * Gets troperties from the configuration file
	 * 
	 * @param propertyName
	 *            The name of the property
	 * @return The value of the property
	 */
	public static String getConfig(String propertyName) {
		return getPropertyFromFile(CONFIG_FILE, config, propertyName);
	}

	/**
	 * Gets a property from a file
	 * 
	 * @param fileName
	 *            The name of the properties file
	 * @param prop
	 *            The properties file
	 * @param propertyName
	 *            The name of the property
	 * @return The value of the property
	 */
	private static String getPropertyFromFile(String fileName, Properties prop,
			String propertyName) {
		if (prop == null) {
			prop = new Properties();
			try {
				prop.load(getLocalStream(fileName));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return prop.getProperty(propertyName);
	}

	/**
	 * Opens an input stream
	 * 
	 * @param resourceName
	 *            The name of the resource to open the stream
	 * @return The opened input stream
	 */
	private static InputStream getLocalStream(String resourceName) {
		return Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(resourceName);
	}

}
