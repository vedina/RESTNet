package net.idea.restnet.db.aalocal.user;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;

/**
 * Verifies if username {link {@link #getFieldname()} and password {link
 * {@link #getValue()} exist.
 * 
 * @author nina
 * 
 */
public class UserAuth extends AbstractAuth {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6017803463536586392L;
	protected final String sql = "select user_name from %susers join %suser_registration using(user_name) where status='confirmed' and user_name = ? and user_pass = md5(?)";

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

		if ((getValue() == null) || (getFieldname() == null))
			throw new AmbitException("Empty parameters");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class, getFieldname()));
		params.add(new QueryParam<String>(String.class, getValue()));
		return params;
	}

	@Override
	public String getSQL() throws AmbitException {
		return String.format(sql, databaseName == null ? "" : String.format("`%s`.", databaseName),
				databaseName == null ? "" : String.format("`%s`.", databaseName));
	}

}
