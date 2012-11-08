package net.idea.restnet.u.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.db.aalocal.user.CreateUser;
import net.idea.restnet.db.aalocal.user.IDBConfig;
import net.idea.restnet.db.aalocal.user.IUser;
import net.idea.restnet.u.UserRegistration;

public class CreateRegistration extends AbstractUpdate<IUser,UserRegistration>  implements IDBConfig {
	protected CreateUser createUser;
	public CreateRegistration(IUser user,UserRegistration reg) {
		super();
		setObject(reg);
		setGroup(user);
		createUser = new CreateUser(user);
		createUser.setDatabaseName(getDatabaseName());
	}
	@Override
	public String[] getSQL() throws AmbitException {
		return new String[] {
			createUser.getSQL()[0],
			"insert into user_registration (user_name,created,code,status) values (?,now(),?,'commenced')"
		};
	}

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		switch (index) {
		case 0: return createUser.getParameters(0);
		default: {
			List<QueryParam> params1 = new ArrayList<QueryParam>();
			params1.add(new QueryParam<String>(String.class,  getGroup().getUserName()));
			params1.add(new QueryParam<String>(String.class,  getObject().getConfirmationCode()));
			return params1;
		}
		}

	}

	@Override
	public void setID(int index, int id) {
		
	}

	@Override
	public boolean returnKeys(int index) {
		return false;
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
