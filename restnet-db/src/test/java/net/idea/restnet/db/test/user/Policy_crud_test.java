package net.idea.restnet.db.test.user;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.restnet.db.CreateDatabaseProcessor;
import net.idea.restnet.db.aalocal.CreateUsersDatabaseProcessor;
import net.idea.restnet.db.aalocal.DBRole;
import net.idea.restnet.db.aalocal.policy.DeletePolicy;
import net.idea.restnet.db.aalocal.user.IUser;
import net.idea.restnet.db.test.CRUDTest;
import net.idea.restnet.i.aa.IRESTPolicy;
import net.idea.restnet.i.aa.RESTPolicy;

public class Policy_crud_test  extends CRUDTest<DBRole,IRESTPolicy<Integer>>  {
	@Override
	protected IQueryUpdate<DBRole, IRESTPolicy<Integer>> deleteQuery()
			throws Exception {
		DeletePolicy q = new DeletePolicy();
		RESTPolicy p = new RESTPolicy();
		p.setId(1);
		q.setObject(p);
		return q;
	}

	@Override
	protected IQueryUpdate<DBRole, IRESTPolicy<Integer>> createQuery()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IQueryUpdate<DBRole, IRESTPolicy<Integer>> createQueryNew()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IQueryUpdate<DBRole, IRESTPolicy<Integer>> updateQuery()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void createVerify(IQueryUpdate<DBRole, IRESTPolicy<Integer>> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createVerifyNew(
			IQueryUpdate<DBRole, IRESTPolicy<Integer>> query) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void updateVerify(IQueryUpdate<DBRole, IRESTPolicy<Integer>> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void deleteVerify(IQueryUpdate<DBRole, IRESTPolicy<Integer>> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",String.format("SELECT idpolicy from policy where idpolicy=1"));
		
		Assert.assertEquals(0,table.getRowCount());
		c.close();		
		
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
