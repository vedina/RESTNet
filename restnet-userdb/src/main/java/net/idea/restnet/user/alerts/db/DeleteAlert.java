package net.idea.restnet.user.alerts.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.restnet.user.DBUser;

/**
 * Removes an author of a given protocol. Does not delete the user itself.
 * @author nina
 *
 */
public class DeleteAlert  extends  AbstractAlertUpdate<DBUser>  {
	protected static final String sql_iduser = "DELETE from alert where %s %s %s";
	protected static final String sql_username = "DELETE alert from alert,user where %s %s %s and alert.iduser=user.iduser";
	
	public DeleteAlert(DBAlert alert,DBUser user) {
		super(alert);
		setGroup(user);
	}
	public DeleteAlert() {
		this(null,null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		
		if (getObject()!=null)
			params.add(new QueryParam<Integer>(Integer.class, getObject().getID()));	
		
		if (getGroup()!=null)
			if (getGroup().getID()>0)
				params.add(new QueryParam<Integer>(Integer.class, getGroup().getID()));
			else if (getGroup().getUserName()!=null)
				params.add(new QueryParam<String>(String.class, getGroup().getUserName()));
			
		if (params.size()==0) throw new AmbitException("Both alert and user are missing!");
		return params;
		
	}

	public String[] getSQL() throws AmbitException {
		String alert = null;
		String user = null;
		String sql = sql_iduser;
		if (getObject()!=null && getObject().getID()>0 ) alert = DBAlert._fields.idquery.getCondition();
		
		if (getGroup()!=null)
			if (getGroup().getID()>0 ) user = DBAlert._fields.iduser.getCondition();
			else if (getGroup().getUserName()!=null) { user = " username=? "; sql = sql_username;}
		
		return new String[] {String.format(sql, 
				alert==null?"":alert,
				alert!=null && user!=null?" and ":"",
				user==null?"":user				
				)};
	}
	public void setID(int index, int id) {
			
	}
}