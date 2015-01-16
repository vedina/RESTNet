package net.idea.restnet.db.test.user;

import java.sql.ResultSet;

import junit.framework.Assert;
import net.idea.restnet.db.CreateDatabaseProcessor;
import net.idea.restnet.db.aalocal.CreateUsersDatabaseProcessor;
import net.idea.restnet.db.aalocal.policy.PolicyQuery;
import net.idea.restnet.db.test.QueryTest;
import net.idea.restnet.i.aa.RESTPolicy;

import org.restlet.data.Method;

public class PolicyQueryTest extends QueryTest<PolicyQuery> {

    @Override
    protected PolicyQuery createQuery() throws Exception {
	PolicyQuery q = new PolicyQuery();
	RESTPolicy p = new RESTPolicy();
	p.setUri("/dataset");
	q.setFieldname(p);
	q.setValue("admin");
	q.setMethod(Method.GET);
	return q;
    }

    @Override
    protected void verify(PolicyQuery query, ResultSet rs) throws Exception {
	int count = 0;
	while (rs.next()) {
	    Boolean record = query.getObject(rs);
	    Assert.assertTrue(record);
	    Assert.assertEquals("/dataset", (rs.getString("resource")));
	    Assert.assertEquals(2, rs.getInt(3));
	    count++;
	}
	Assert.assertEquals(1, count);
    }

    @Override
    protected CreateDatabaseProcessor getDBCreateProcessor() {
	return new CreateUsersDatabaseProcessor();
    }

    @Override
    public String getDBTables() {
	return "src/test/resources/net/idea/restnet/db/test/tables.xml";
    }

}
