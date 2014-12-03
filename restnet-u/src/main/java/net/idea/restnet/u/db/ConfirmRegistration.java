package net.idea.restnet.u.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.db.aalocal.user.IDBConfig;
import net.idea.restnet.db.aalocal.user.IUser;
import net.idea.restnet.u.UserRegistration;

public class ConfirmRegistration extends AbstractUpdate<IUser,UserRegistration>  implements IDBConfig {
	private String sql = "update `%s`.user_registration set confirmed=now(),status='confirmed' where code=? and status='commenced' and date_add(created,interval ? hour)>=now()";
	protected int hoursValid = 48;
	
	public int getHoursValid() {
		return hoursValid;
	}

	public void setHoursValid(int hoursValid) {
		this.hoursValid = hoursValid;
	}

	public ConfirmRegistration(UserRegistration reg) {
		this(48,reg);
	}
	
	public ConfirmRegistration(Integer hoursValid,UserRegistration reg) {
		super();
		setObject(reg);
		setHoursValid(hoursValid);
	}
	@Override
	public String[] getSQL() throws AmbitException {
		return new String[] {
			String.format(sql, getDatabaseName())
		};
	}

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		params1.add(new QueryParam<String>(String.class,  getObject().getConfirmationCode()));
		params1.add(new QueryParam<Integer>(Integer.class, getHoursValid()));
		return params1;
	}

	@Override
	public void setID(int index, int id) {
		
	}

	@Override
	public boolean returnKeys(int index) {
		return true;
	}
	
	protected String databaseName = null;
	@Override
	public void setDatabaseName(String name) {
		databaseName = name;
	}
	@Override
	public String getDatabaseName() {
		return databaseName;
	}
}