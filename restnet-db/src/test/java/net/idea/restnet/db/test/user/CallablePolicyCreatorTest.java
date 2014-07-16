package net.idea.restnet.db.test.user;

import java.math.BigInteger;

import junit.framework.Assert;
import net.idea.restnet.db.CreateDatabaseProcessor;
import net.idea.restnet.db.aalocal.CreateUsersDatabaseProcessor;
import net.idea.restnet.db.aalocal.policy.CallablePolicyCreator;
import net.idea.restnet.db.test.DbUnitTest;
import net.idea.restnet.i.aa.RESTPolicy;
import net.idea.restnet.i.task.TaskResult;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.Method;

public class CallablePolicyCreatorTest extends DbUnitTest {
	protected String dbFile = "src/test/resources/net/idea/restnet/db/test/aalocal.xml";

	@Override
	protected CreateDatabaseProcessor getDBCreateProcessor() {
		return new CreateUsersDatabaseProcessor();
	}

	@Override
	public String getDBTables() {
		return "src/test/resources/net/idea/restnet/db/test/tables.xml";
	}
	
	
	@Test
	public void testCreate() throws Exception {
		setUpDatabase(dbFile);
	    IDatabaseConnection c = getConnection();
	    Form form = new Form();
	    form.add(RESTPolicy._fields.role.name(), "user");
	    form.add(RESTPolicy._fields.resource.name(), "/ambit2/model/123");
	    form.add(RESTPolicy._fields.get.name(), "on");
	    form.add(RESTPolicy._fields.post.name(), "on");
		CallablePolicyCreator callable = new CallablePolicyCreator(Method.POST, form, "http://localhost:8080/ambit2", 
				c.getConnection(), null,getDatabase());
		TaskResult task = callable.call();
		c.close();
		c = getConnection();
		ITable table = 	c.createQueryTable("EXPECTED",String.format("SELECT role_name,mget,mput,mpost,mdelete from policy where resource='/model/123'  "));
		Assert.assertEquals(1,table.getRowCount());			
		c.close();
	}

}
