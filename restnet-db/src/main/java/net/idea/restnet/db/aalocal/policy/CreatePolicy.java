package net.idea.restnet.db.aalocal.policy;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.db.aalocal.DBRole;
import net.idea.restnet.db.aalocal.user.IDBConfig;
import net.idea.restnet.i.aa.IRESTPolicy;

public class CreatePolicy extends AbstractUpdate<DBRole, IRESTPolicy<Integer>> implements IDBConfig {

    public static String sql = "insert into %spolicy (idpolicy,role_name,prefix,resource,level,mget,mput,mpost,mdelete) values (null,?,?,?,?,?,?,?,?)";

    public CreatePolicy() {
	this(null);
    }

    public CreatePolicy(IRESTPolicy<Integer> policy) {
	setObject(policy);
    }

    public String[] getSQL() throws AmbitException {
	return new String[] { String.format(sql, databaseName == null ? "" : String.format("`%s`.", databaseName)) };
    }

    public List<QueryParam> getParameters(int index) throws AmbitException {
	if ((getObject() == null) || (getObject().getRole() == null) || (getObject().getUri() == null))
	    throw new AmbitException("Undefined policy");
	try {
	    String[] prefix_resource = getObject().splitURI(getObject().getUri());

	    List<QueryParam> params2 = new ArrayList<QueryParam>();
	    params2.add(new QueryParam<String>(String.class, getObject().getRole()));
	    params2.add(new QueryParam<String>(String.class, prefix_resource[0]));
	    params2.add(new QueryParam<String>(String.class, prefix_resource[1]));
	    params2.add(new QueryParam<Integer>(Integer.class, getObject().getLevel(prefix_resource[1])));
	    params2.add(new QueryParam<Boolean>(Boolean.class, getObject().isAllowGET()));
	    params2.add(new QueryParam<Boolean>(Boolean.class, getObject().isAllowPUT()));
	    params2.add(new QueryParam<Boolean>(Boolean.class, getObject().isAllowPOST()));
	    params2.add(new QueryParam<Boolean>(Boolean.class, getObject().isAllowDELETE()));
	    return params2;
	} catch (Exception x) {
	    throw new AmbitException(x);
	}

    }

    @Override
    public void setID(int index, int id) {
	getObject().setId(id);
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

    @Override
    public boolean returnKeys(int index) {
	return true;
    }
}