package net.idea.restnet.db.test.user;

import java.sql.ResultSet;

import junit.framework.Assert;
import net.idea.restnet.db.CreateDatabaseProcessor;
import net.idea.restnet.db.aalocal.CreateUsersDatabaseProcessor;
import net.idea.restnet.db.aalocal.policy.ReadPolicy;
import net.idea.restnet.db.test.QueryTest;
import net.idea.restnet.i.aa.IRESTPolicy;

public class PolicyReadTest extends QueryTest<ReadPolicy> {

	@Override
	protected ReadPolicy createQuery() throws Exception {
		ReadPolicy q = new ReadPolicy();
		return q;
	}

	@Override
	protected void verify(ReadPolicy query, ResultSet rs) throws Exception {
		int count = 0;
		while (rs.next()) {
			IRESTPolicy<Integer> record = query.getObject(rs);
			Assert.assertNotNull(record.getId());
			Assert.assertNotNull(record.getUri());
			Assert.assertNotNull(record.getRole());
			count++;
		}
		Assert.assertEquals(2,count);
		
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
