package net.idea.restnet.u;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.restnet.db.CreateDatabaseProcessor;
import net.idea.restnet.db.aalocal.CreateUsersDatabaseProcessor;
import net.idea.restnet.db.aalocal.user.IUser;
import net.idea.restnet.db.test.CRUDTest;
import net.idea.restnet.db.test.user.TestUser;
import net.idea.restnet.u.db.ConfirmRegistration;
import net.idea.restnet.u.db.CreateRegistration;
import net.idea.restnet.u.db.UpdateRegistrationStatus;

import org.apache.commons.lang3.RandomStringUtils;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

public class RegistrationTest extends CRUDTest<IUser,UserRegistration>  {
	protected String randomcode = RandomStringUtils.randomAlphanumeric(45);
	@Override
	protected IQueryUpdate<IUser, UserRegistration> createQuery()
			throws Exception {
		IUser user = new TestUser();
		user.setUserName("mynewuser");
		user.setPassword("password");
		UserRegistration reg = new UserRegistration(randomcode);
		CreateRegistration q =  new CreateRegistration(user, reg,getDatabase());
		q.setDatabaseName(getDatabase());
		return q;
	}

	@Override
	protected void createVerify(IQueryUpdate<IUser, UserRegistration> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT user_name from users where user_name='mynewuser'"));      
		Assert.assertEquals(1,table.getRowCount());
		
		table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT code from user_registration where user_name='mynewuser' and code='"+randomcode+"'"));
		Assert.assertEquals(1,table.getRowCount());
		c.close();
		
	}
	@Override
	protected IQueryUpdate<IUser, UserRegistration> createQueryNew()
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT code from user_registration where user_name='admin' and code='ABCDEF' and status='confirmed'"));
		Assert.assertEquals(0,table.getRowCount());
		c.close();		

		UserRegistration reg = new UserRegistration("ABCDEF");
		ConfirmRegistration q =  new ConfirmRegistration(12, reg);
		q.setDatabaseName(getDatabase());
		return q;
	}
	

	@Override
	protected void createVerifyNew(IQueryUpdate<IUser, UserRegistration> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT code from user_registration where user_name='admin' and code='ABCDEF' and status='confirmed'"));
		Assert.assertEquals(1,table.getRowCount());
		c.close();
		
	}

	@Override
	public void testUpdate() throws Exception {
	}
	@Override
	protected IQueryUpdate<IUser, UserRegistration> updateQuery()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IQueryUpdate<IUser, UserRegistration> deleteQuery()
			throws Exception {
		IUser user = new TestUser();
		user.setUserName("newuser");
		UpdateRegistrationStatus q =  new UpdateRegistrationStatus(user);
		q.setDatabaseName(getDatabase());
		return q;
	}



	@Override
	protected void updateVerify(IQueryUpdate<IUser, UserRegistration> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT status from user_registration where user_name='newuser' and status='disabled'"));
		Assert.assertEquals(1,table.getRowCount());
		c.close();		
	}

	@Override
	protected void deleteVerify(IQueryUpdate<IUser, UserRegistration> query)
			throws Exception {
		
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
