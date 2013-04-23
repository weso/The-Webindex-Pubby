package es.weso.model;

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
 * @version 1.0
 * @since 27/03/2013
 */
public class RankMap implements Serializable {

	private static final long serialVersionUID = 8538805584620127235L;
	private HashMap<String, Rank> data;
	
	public RankMap() {
		data = new HashMap<String, Rank>();
	}
	
	/**
	 * Builds a {@link RankMap} from a {@link ResultSet}
	 * 
	 * @param rs
	 *            The {@link ResultSet} to take the ranks from
	 */
	public RankMap(ResultSet rs) {
		data = new HashMap<String, Rank>();
		int i = 0;
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			String val = "" + qs.getLiteral("value").getDouble();
			BigDecimal big = new BigDecimal(val);
			big = big.setScale(2, RoundingMode.HALF_UP);
			data.put(qs.getLiteral("code").getString().toUpperCase(), new Rank(
					++i, big.doubleValue()));
		}
	}

	public HashMap<String, Rank> getData() {
		return data;
	}
	
	public void setData(HashMap<String, Rank> data) {
		this.data = data;
	}
}
