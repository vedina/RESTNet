package net.idea.restnet.u;

import java.sql.ResultSet;

import junit.framework.Assert;
import net.idea.restnet.db.CreateDatabaseProcessor;
import net.idea.restnet.db.aalocal.CreateUsersDatabaseProcessor;
import net.idea.restnet.db.test.QueryTest;
import net.idea.restnet.u.db.ReadRegistration;

public class ReadRegistrationTest extends QueryTest<ReadRegistration> {

	@Override
	protected ReadRegistration createQuery() throws Exception {
		ReadRegistration reg = new ReadRegistration("TEST");
		reg.setDatabaseName(getDatabase());
		return reg;
	}

	@Override
	protected void verify(ReadRegistration query, ResultSet rs) throws Exception {
		int records = 0;
		while (rs.next()) {
			UserRegistration reg = query.getObject(rs);
			Assert.assertEquals("TEST", reg.getConfirmationCode());
			Assert.assertEquals("confirmed", reg.getStatus().name());
			records++;
		}
		Assert.assertEquals(1, records);

	}

	@Override
	protected CreateDatabaseProcessor getDBCreateProcessor() {
		return new CreateUsersDatabaseProcessor();
	}

	@Override
	public String getDBTables() {
		return "net/idea/restnet/db/test/tables.xml";
	}

}
