package net.idea.restnet.u;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.db.aalocal.user.IDBConfig;

public abstract class AbstractUpdateCredentials<U> extends AbstractUpdate<UserCredentials,U> implements IDBConfig {
	protected String databaseName = null;
	@Override
	public void setDatabaseName(String name) {
		databaseName = name;
	}
	@Override
	public String getDatabaseName() {
		return databaseName;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	protected String hash = "md5";
	
	private String sql = "update %s.users set user_pass = %s(?) where user_pass=%s(?) and user_name = ?";

	
	public AbstractUpdateCredentials(UserCredentials c,U ref) {
		super(ref);
		this.setGroup(c);
	}
	public AbstractUpdateCredentials() {
		this(null,null);
	}			
	
	public abstract String getUserName(U user);
	
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getUserName(getObject())==null) throw new AmbitException("Invalid input");
		if (getGroup().getOldpwd()==null) throw new AmbitException("Invalid input");
		if (getGroup().getNewpwd()==null) throw new AmbitException("Invalid input");
		params.add(new QueryParam<String>(String.class, getGroup().getNewpwd()));
		params.add(new QueryParam<String>(String.class, getGroup().getOldpwd()));
		params.add(new QueryParam<String>(String.class, getUserName(getObject())));
		return params;
	}

	public String[] getSQL() throws AmbitException {
		return new String[] { String.format(sql,getDatabaseName(),getHash(),getHash()) };
	}
	public void setID(int index, int id) {
			
	}
} 