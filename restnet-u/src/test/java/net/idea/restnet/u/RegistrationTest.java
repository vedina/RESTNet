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

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

public class RegistrationTest extends CRUDTest<IUser,UserRegistration>  {

	@Override
	protected IQueryUpdate<IUser, UserRegistration> createQuery()
			throws Exception {
		IUser user = new TestUser();
		user.setUserName("test");
		UserRegistration reg = new UserRegistration("XYZ");
		CreateRegistration q =  new CreateRegistration(user, reg);
		q.setDatabaseName(getDatabase());
		return q;
	}

	@Override
	protected void createVerify(IQueryUpdate<IUser, UserRegistration> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT code from user_registration where user_name='test' and code='XYZ'"));
		
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
		IUser user = new TestUser();
		user.setUserName("admin");
		UserRegistration reg = new UserRegistration("ABCDEF");
		ConfirmRegistration q =  new ConfirmRegistration(user, reg);
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
	public void testDelete() throws Exception {
	
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
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	protected void updateVerify(IQueryUpdate<IUser, UserRegistration> query)
			throws Exception {
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
