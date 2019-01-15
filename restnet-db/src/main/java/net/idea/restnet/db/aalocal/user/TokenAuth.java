package net.idea.restnet.db.aalocal.user;

import java.util.ArrayList;
import java.util.List;

import org.restlet.data.ChallengeScheme;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;

/**
 * Verifies if token is valid
 * 
 * @author nina
 * 
 */
public class TokenAuth extends AbstractAuth {
	public static final ChallengeScheme APIKEY = new ChallengeScheme("APIKEY", "APIKEY", "Access token authentication");
	/**
	 * 
	 */
	private static final long serialVersionUID = -7099610858192657006L;
	protected final String sql = "select user_name from %sapps a join %suser_registration r on a.username=r.user_name where status='confirmed' and token = ? and enabled = 1 and expire >= now()";

	public TokenAuth() {
		super();
		setFieldname(null);
		setValue(null);
	}
	@Override
	public double calculateMetric(String object) {
		return 1;
	}

	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {

		if (getValue() == null)
			throw new AmbitException("Empty parameters");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class, getValue()));
		return params;
	}

	@Override
	public String getSQL() throws AmbitException {
		return String.format(sql, databaseName == null ? "" : String.format("`%s`.", databaseName),
				databaseName == null ? "" : String.format("`%s`.", databaseName));
	}

}
