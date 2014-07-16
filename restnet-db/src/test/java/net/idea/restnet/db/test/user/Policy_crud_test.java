package net.idea.restnet.db.test.user;

import java.math.BigInteger;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.restnet.db.CreateDatabaseProcessor;
import net.idea.restnet.db.aalocal.CreateUsersDatabaseProcessor;
import net.idea.restnet.db.aalocal.DBRole;
import net.idea.restnet.db.aalocal.policy.CreatePolicy;
import net.idea.restnet.db.aalocal.policy.DeletePolicy;
import net.idea.restnet.db.test.CRUDTest;
import net.idea.restnet.i.aa.IRESTPolicy;
import net.idea.restnet.i.aa.RESTPolicy;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

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
		return null;
	}

	@Override
	protected IQueryUpdate<DBRole, IRESTPolicy<Integer>> createQueryNew()
			throws Exception {
		CreatePolicy q = new CreatePolicy();
		RESTPolicy p = new RESTPolicy();
		p.setAllowDELETE(false);
		p.setAllowGET(true);
		p.setAllowPOST(false);
		p.setAllowPUT(false);
		p.setRole("user");
		p.setUri("http://localhost:8080/ambit2/dataset");
		q.setObject(p);
		return q;
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
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",String.format("SELECT count(*) c from policy "));
		Assert.assertEquals(new BigInteger("3"),table.getValue(0,"c"));
		c.close();	
		
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
