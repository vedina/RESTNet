package net.idea.restnet.db.test.user;

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
import net.idea.restnet.user.CallableUserCreator;
import net.idea.restnet.user.db.ReadUser;
import net.idea.restnet.user.resource.UserURIReporter;

public class CallableUserCreatorTest extends DbUnitTest {

	@Test
	public void testCreateUser() throws Exception {
		setUpDatabase(dbFile);
		IDatabaseConnection c = getConnection();
		IDatabaseConnection c1 = getConnection();

		Form form = new Form();
		form.add(ReadUser.fields.username.name(), "test1");
		form.add(ReadUser.fields.firstname.name(), "test");
		form.add(ReadUser.fields.lastname.name(), "test");
		form.add(ReadUser.fields.title.name(), "test");
		form.add(ReadUser.fields.keywords.name(), "test");
		form.add(ReadUser.fields.email.name(), "test@example.com");
		form.add("pwd1", "123456");
		form.add("pwd2", "123456");

		try {
			UserURIReporter reporter = new UserURIReporter();
			CallableUserCreator callable = new CallableUserCreator(Method.POST, null, reporter, form, "/",
					c.getConnection(), null, false, false, "aalocal_test") {

				@Override
				protected String getSender() {
					return null;
				}

				@Override
				protected String getSenderName() {
					return null;
				}

				@Override
				protected String getSystemName() {
					return null;
				}

				@Override
				protected String getConfig() {
					return "net/idea/restnet/db/aalocal/aalocal.pref";
				}

			};
			TaskResult task = callable.call();
			System.out.println(task.getUri());

			ITable table = c1.createQueryTable("EXPECTED",
					String.format("SELECT count(*) from user where username='test1'"));
			Assert.assertEquals(1, table.getRowCount());
			table = c1.createQueryTable("EXPECTED",
					String.format("SELECT status from user_registration where user_name='test1'"));
			Assert.assertEquals("commenced", table.getValue(0, "status"));

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
