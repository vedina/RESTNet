package net.idea.restnet.db.test.app;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.Method;

import junit.framework.Assert;
import net.idea.restnet.db.CreateDatabaseProcessor;
import net.idea.restnet.db.aalocal.CreateUsersDatabaseProcessor;
import net.idea.restnet.db.test.DbUnitTest;
import net.idea.restnet.i.task.TaskResult;
import net.idea.restnet.user.DBUser;
import net.idea.restnet.user.app.CallableTokenCreator;
import net.idea.restnet.user.app.db.DBUApp;
import net.idea.restnet.user.db.ReadUser;

public class CallableTokenCreatorTest extends DbUnitTest {

	@Test
	public void testCreateToken() throws Exception {
		setUpDatabase(dbFile);
		IDatabaseConnection c = getConnection();
		IDatabaseConnection c1 = getConnection();

		Form form = new Form();
		form.add(ReadUser.fields.username.name(), "test");

		try {
			DBUser user = new DBUser();
			user.setUserName("test");
			DBUApp app = new DBUApp(user);
			CallableTokenCreator callable = new CallableTokenCreator(Method.POST, app,  form, "/ambit2",
					c.getConnection(), null, "aalocal_test");
			TaskResult task = callable.call();
			Assert.assertEquals("/ambit2/myaccount/apps", task.getUri());

			ITable table = c1.createQueryTable("EXPECTED",
					String.format("SELECT token from apps where username='test'"));
			Assert.assertEquals(2, table.getRowCount());

		} catch (Exception x) {
			throw x;
		} finally {
			try {
				c.close();
			} catch (Exception x) {
			}
			try {
				c1.close();
			} catch (Exception x) {
			}
		}
	}

	protected String dbFile = "src/test/resources/net/idea/restnet/db/test/aalocal.xml";

	@Override
	protected CreateDatabaseProcessor getDBCreateProcessor() {
		return new CreateUsersDatabaseProcessor();
	}

	@Override
	public String getDBTables() {
		return "src/test/resources/net/idea/restnet/db/test/tables.xml";
	}

}
