package net.idea.restnet.user.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractObjectUpdate;
import net.idea.restnet.user.DBUser;

public class UpdateUser extends AbstractObjectUpdate<DBUser>{
	protected DBUser.fields[] update_fields = {
			DBUser.fields.email,
			DBUser.fields.title,
			DBUser.fields.firstname,
			DBUser.fields.lastname,
			DBUser.fields.homepage,
			DBUser.fields.keywords,
			DBUser.fields.reviewer
	};
	
	private String sql = "update user set %s where iduser = ?";

	
	public UpdateUser(DBUser ref) {
		super(ref);
	}
	public UpdateUser() {
		this(null);
	}			
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		for (DBUser.fields field : update_fields) {
			if (field.getValue(getObject())!=null)
				params.add(field.getParam(getObject()));
		}
		if (params.size()==0) throw new AmbitException("No parameters");
		params.add(new QueryParam<Integer>(Integer.class, getObject().getID()));
		return params;
	}

	public String[] getSQL() throws AmbitException {
		
		StringBuilder b = null;
		for (DBUser.fields field : update_fields) {
			if (field.getValue(getObject())!=null) {
				if (b==null) b = new StringBuilder();
				else b.append(", ");
				b.append(field.getSQL());
			}
		}
		
		return new String[] {String.format(sql, b)};
	}
	public void setID(int index, int id) {
			
	}
}