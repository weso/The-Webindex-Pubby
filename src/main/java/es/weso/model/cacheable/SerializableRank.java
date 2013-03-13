package es.weso.model.cacheable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

/**
 * A {@link HashMap} of {@link Rank}s that can be serialised
 * 
 * @author <a href="http://alejandro-montes.appspot.com">Alejandro Montes
 *         García</a>
 * 
 */
public class SerializableRank implements Serializable {

	private static final long serialVersionUID = 8538805584620127235L;
	private HashMap<String, Rank> data;

	/**
	 * Builds a {@link SerializableRank} from a {@link ResultSet}
	 * 
	 * @param rs
	 *            The {@link ResultSet} to take the ranks from
	 */
	public SerializableRank(ResultSet rs) {
		data = new HashMap<String, Rank>();
		int i = 0;
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			String val = "" + qs.getLiteral("value").getDouble();
			BigDecimal big = new BigDecimal(val);
			big = big.setScale(2, RoundingMode.HALF_UP);
			data.put(qs.getLiteral("countryName").getString(), new Rank(++i,
					big.toString()));
		}
	}

	public HashMap<String, Rank> getData() {
		return data;
	}
}
