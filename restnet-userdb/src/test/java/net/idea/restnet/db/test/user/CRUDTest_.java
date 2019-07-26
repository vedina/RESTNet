package net.idea.restnet.db.test.user;

import net.idea.restnet.db.CreateDatabaseProcessor;
import net.idea.restnet.db.aalocal.CreateUsersDatabaseProcessor;
import net.idea.restnet.db.test.CRUDTest;

public abstract class CRUDTest_<G, T> extends CRUDTest<G, T> {

	protected CreateDatabaseProcessor getDBCreateProcessor() {
		return new CreateUsersDatabaseProcessor();
	}

	@Override
	public String getDBTables() {
		return "net/idea/restnet/db/test/tables.xml";
	}

}
