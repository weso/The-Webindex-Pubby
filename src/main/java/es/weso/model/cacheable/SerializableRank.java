package es.weso.model.cacheable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class SerializableRank implements Serializable {

	private static final long serialVersionUID = 8538805584620127235L;
	private HashMap<String, Rank> data;

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
