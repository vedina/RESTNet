package net.idea.restnet.u.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.db.aalocal.DBRole;
import net.idea.restnet.db.aalocal.user.CreateUserCredentials;
import net.idea.restnet.db.aalocal.user.CreateUserRole;
import net.idea.restnet.db.aalocal.user.IDBConfig;
import net.idea.restnet.db.aalocal.user.IUser;
import net.idea.restnet.u.UserRegistration;

public class CreateRegistration extends AbstractUpdate<IUser,UserRegistration>  implements IDBConfig {
	protected CreateUserCredentials createUser;
	protected CreateUserRole createRole;
	public CreateRegistration(IUser user,UserRegistration reg, String dbname) {
		this(user,reg,new DBRole("user", "Any user"),dbname);
	}
	public CreateRegistration(IUser user,UserRegistration reg, DBRole role, String dbname) {
		super();
		setObject(reg);
		setGroup(user);
		createUser = new CreateUserCredentials(user);
		createRole = new CreateUserRole(role,user);
		setDatabaseName(dbname);
	}
	@Override
	public String[] getSQL() throws AmbitException {
		return new String[] {
			createUser.getSQL()[0],
			createRole.getSQL()[0],
			createRole.getSQL()[1],
			String.format("insert into `%s`.user_registration (user_name,created,code,status) values (?,now(),?,'commenced')",getDatabaseName())
		};
	}

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		switch (index) {
		case 0: return createUser.getParameters(0);
		case 1: return createRole.getParameters(0);
		case 2: return createRole.getParameters(1);	
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
	
	@Override
	public void setDatabaseName(String name) {
		createUser.setDatabaseName(name);
		createRole.setDatabaseName(name);
	}
	@Override
	public String getDatabaseName() {
		return createUser.getDatabaseName();
	}
}
