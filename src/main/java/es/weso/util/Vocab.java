package es.weso.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Vocab {

	private static Properties properties;

	private static final String FILE_NAME = "vocab.properties";
	
	public static String getVocab(String propertyName) {
        if (properties == null) {
            properties = new Properties();
            try {
                properties.load(getLocalStream(FILE_NAME));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return properties.getProperty(propertyName);
    }
	
	/**
	 * Opens an input stream
	 * 
	 * @param resourceName
	 *            The name of the resource to open the stream
	 * @return The opened input stream
	 */
    private static InputStream getLocalStream(String resourceName) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
    }

}
