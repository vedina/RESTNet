package net.idea.restnet.db.aalocal.policy;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.db.aalocal.DBRole;
import net.idea.restnet.db.aalocal.user.IDBConfig;
import net.idea.restnet.i.aa.IRESTPolicy;

public class DeletePolicy extends AbstractUpdate<DBRole,IRESTPolicy<Integer>> implements IDBConfig{

	@Override
	public String[] getSQL() throws AmbitException {
		return new String[] {
			String.format("delete from %spolicy where idpolicy=?",
					databaseName==null?"":String.format("`%s`.",databaseName))
		};
	}

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = null;
		if (getObject()==null || getObject().getId()<=0) throw new AmbitException("Invalid policy id");
		params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getObject().getId()));
		return params;
	}

	@Override
	public void setID(int index, int id) {

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