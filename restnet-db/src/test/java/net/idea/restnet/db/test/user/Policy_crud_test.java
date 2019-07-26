package net.idea.restnet.db.test.user;

import java.math.BigInteger;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.restnet.db.CreateDatabaseProcessor;
import net.idea.restnet.db.aalocal.CreateUsersDatabaseProcessor;
import net.idea.restnet.db.aalocal.DBRole;
import net.idea.restnet.db.aalocal.policy.CreatePolicy;
import net.idea.restnet.db.aalocal.policy.DeletePolicy;
import net.idea.restnet.db.aalocal.policy.UpdatePolicy;
import net.idea.restnet.db.test.CRUDTest;
import net.idea.restnet.i.aa.IRESTPolicy;
import net.idea.restnet.i.aa.RESTPolicy;

public class Policy_crud_test extends CRUDTest<DBRole, IRESTPolicy<Integer>> {
	@Override
	protected IQueryUpdate<DBRole, IRESTPolicy<Integer>> deleteQuery() throws Exception {
		DeletePolicy q = new DeletePolicy();
		RESTPolicy p = new RESTPolicy();
		p.setId(1);
		q.setObject(p);
		return q;
	}

	@Override
	protected IQueryUpdate<DBRole, IRESTPolicy<Integer>> createQuery() throws Exception {
		CreatePolicy q = new CreatePolicy();
		RESTPolicy p = new RESTPolicy();
		p.setAllowDELETE(false);
		p.setAllowGET(true);
		p.setAllowPOST(false);
		p.setAllowPUT(false);
		p.setRole("user");
		p.setUri("/ambit3/dataset");
		q.setObject(p);
		return q;
	}

	@Override
	protected IQueryUpdate<DBRole, IRESTPolicy<Integer>> createQueryNew() throws Exception {
		CreatePolicy q = new CreatePolicy();
		RESTPolicy p = new RESTPolicy();
		p.setAllowDELETE(false);
		p.setAllowGET(true);
		p.setAllowPOST(false);
		p.setAllowPUT(false);
		p.setRole("user");
		p.setUri("http://localhost:8080/ambit3/dataset/1234?q=10");
		q.setObject(p);
		return q;
	}

	@Override
	protected IQueryUpdate<DBRole, IRESTPolicy<Integer>> updateQuery() throws Exception {
		UpdatePolicy q = new UpdatePolicy();
		RESTPolicy p = new RESTPolicy();
		p.setAllowDELETE(false);
		p.setAllowGET(true);
		p.setAllowPOST(false);
		p.setAllowPUT(false);
		p.setRole("user");
		p.setId(1);
		p.setUri("http://localhost:8080/ambit3/dataset/789?q=10");
		q.setObject(p);
		return q;
	}

	@Override
	protected void createVerify(IQueryUpdate<DBRole, IRESTPolicy<Integer>> query) throws Exception {
		IDatabaseConnection c = getConnection();
		ITable table = c.createQueryTable("EXPECTED", String.format("SELECT count(*) c from policy "));
		Assert.assertEquals(new BigInteger("3"), table.getValue(0, "c"));

		table = c.createQueryTable("EXPECTED",
				String.format("SELECT idpolicy,role_name,prefix,resource from policy where prefix='/ambit3' "));
		Assert.assertEquals(1, table.getRowCount());
		Assert.assertEquals("/dataset", table.getValue(0, "resource"));
		Assert.assertEquals("user", table.getValue(0, "role_name"));
		c.close();

	}

	@Override
	protected void createVerifyNew(IQueryUpdate<DBRole, IRESTPolicy<Integer>> query) throws Exception {
		IDatabaseConnection c = getConnection();
		ITable table = c.createQueryTable("EXPECTED", String.format("SELECT count(*) c from policy "));
		Assert.assertEquals(new BigInteger("3"), table.getValue(0, "c"));

		table = c.createQueryTable("EXPECTED",
				String.format("SELECT idpolicy,role_name,prefix,resource from policy where prefix='/ambit3' "));
		Assert.assertEquals(1, table.getRowCount());
		Assert.assertEquals("/dataset/1234", table.getValue(0, "resource"));
		Assert.assertEquals("user", table.getValue(0, "role_name"));
		c.close();

	}

	@Override
	protected void updateVerify(IQueryUpdate<DBRole, IRESTPolicy<Integer>> query) throws Exception {
		IDatabaseConnection c = getConnection();
		ITable table = c.createQueryTable("EXPECTED", String.format("SELECT count(*) c from policy "));
		Assert.assertEquals(new BigInteger("2"), table.getValue(0, "c"));

		table = c.createQueryTable("EXPECTED",
				String.format("SELECT idpolicy,role_name,prefix,resource from policy where idpolicy=1 "));
		Assert.assertEquals(1, table.getRowCount());
		Assert.assertEquals("/dataset/789", table.getValue(0, "resource"));
		Assert.assertEquals("/ambit3", table.getValue(0, "prefix"));
		Assert.assertEquals("user", table.getValue(0, "role_name"));
		c.close();
	}

	@Override
	protected void deleteVerify(IQueryUpdate<DBRole, IRESTPolicy<Integer>> query) throws Exception {
		IDatabaseConnection c = getConnection();
		ITable table = c.createQueryTable("EXPECTED", String.format("SELECT idpolicy from policy where idpolicy=1"));

		Assert.assertEquals(0, table.getRowCount());
		c.close();

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
