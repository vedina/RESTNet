package net.idea.restnet.user.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.db.aalocal.user.IDBConfig;
import net.idea.restnet.u.UserCredentials;
import net.idea.restnet.u.UserRegistration;
import net.idea.restnet.u.db.CreateRegistration;
import net.idea.restnet.user.DBUser;


public class CreateUser extends AbstractUpdate<UserCredentials,DBUser> implements IDBConfig {
	protected CreateRegistration registerUser;
	
	public CreateUser(DBUser user, UserRegistration reg, String dbname) {
		super(user);
		registerUser = new CreateRegistration(user, reg,dbname);
		setObject(user);
		setGroup(user.getCredentials());
	}
	@Override
	public void setObject(DBUser object) {
		super.setObject(object);
		if (registerUser!=null) registerUser.setGroup(object);
	}

	@Override
	public String[] getSQL() throws AmbitException {
		String[] sql = registerUser.getSQL();
		String[] newsql = new String[sql.length+1];
		newsql[0] = "insert into user (iduser,username,email,title,firstname,lastname,weblog,homepage,keywords,reviewer) values (?,?,?,?,?,?,?,?,?,?)"; 
		for (int i = 0; i < sql.length; i++)
			newsql[i+1] = sql[i];
		return newsql;
	}

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		switch (index) {
		case 0: {
			params1.add(new QueryParam<Integer>(Integer.class,  null));
			params1.add(new QueryParam<String>(String.class,  getObject().getUserName()));
			params1.add(new QueryParam<String>(String.class,  getObject().getEmail()));
			params1.add(new QueryParam<String>(String.class,  getObject().getTitle()));
			params1.add(new QueryParam<String>(String.class,  getObject().getFirstname()));
			params1.add(new QueryParam<String>(String.class,  getObject().getLastname()));
			params1.add(new QueryParam<String>(String.class,  getObject().getWeblog()==null?null:getObject().getWeblog().toString()));
			params1.add(new QueryParam<String>(String.class,  getObject().getHomepage()==null?null:getObject().getHomepage().toString()));
			params1.add(new QueryParam<String>(String.class,  getObject().getKeywords()==null?"":getObject().getKeywords()));
			params1.add(new QueryParam<Boolean>(Boolean.class,  getObject().isReviewer()));
			return params1;
		}
		default: 
			return registerUser.getParameters(index-1);
		}
	}

	@Override
	public void setID(int index, int id) {
		if (index==0)
			getObject().setID(id);
	}

	@Override
	public boolean returnKeys(int index) {
		return index==0;
	}
	
	@Override
	public void setDatabaseName(String name) {
		registerUser.setDatabaseName(name);
	}
	@Override
	public String getDatabaseName() {
		return registerUser.getDatabaseName();
	}
}
