package es.weso.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;

/**
 *
 * @author Alejandro Montes García
 * @since 24/10/12 - 11:21
 */
public class QueriesLoader {

    private static Properties properties;

    private static final String FILE_NAME = "queries.properties";

	/**
	 * Gets a query from the queries file. The queries are stored in a
	 * properties file, if they require any argument, those are specified in the
	 * file with {i}, being i a number from zero to the maximum number of
	 * arguments
	 * 
	 * @param queryName
	 *            The name of the query
	 * @param args
	 *            (Optional) The arguments that the query is taking
	 * @return The query with the {i} arguments replaced for the actual ones
	 */
    public static String getQuery(String queryName, String... args) {
        if (properties == null) {
            properties = new Properties();
            try {
                properties.load(getLocalStream(FILE_NAME));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String query = properties.getProperty(queryName);
        int i = 0;
        for (String arg : args) {
            query = query.replace("{" + i + "}", arg.trim());
            i++;
        }
        return query;
    }

	/**
	 * Gets a query from the queries file. The queries are stored in a
	 * properties file, the argument is specified in the file with {0}
	 * 
	 * @param queryName
	 *            The name of the query
	 * @param argsList
	 *            The possible values of the argument, those will be joint with
	 *            an <tt>UNION</tt> clause, that works like an <tt>OR</tt>.
	 * @return The query with the {0} argument replaced with the possible values
	 *         it might take
	 */
	public static String getQuery(String queryName, Collection<String> argsList) {
        if (properties == null) {
            properties = new Properties();
            try {
                properties.load(getLocalStream(FILE_NAME));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String query = properties.getProperty(queryName);
        if(argsList.isEmpty()) {
            return query.replace("{0}", "0");
        }
        String[] clauses = query.split("\\.");
        for(String clause : clauses) {
            if(clause.contains("{0}")) {
                StringBuilder union = new StringBuilder();
                String condition = clause.subSequence(0, clause.indexOf("'")).toString();
                boolean first = true;
                for(String arg : argsList) {
                    if(!first) {
                       union.append(" UNION ");
                    } else {
                        first = false;
                    }
                    union = union.append("{ ").append(condition).append("'").append(arg).append("' }");
                }
                query = query.replace(clause, union);
                break;
            }
        }
        return query;
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
