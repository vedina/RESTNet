/* ReferenceCRUDTest.java
 * Author: nina
 * Date: Mar 28, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package net.idea.restnet.db.test.user;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.restnet.db.CreateDatabaseProcessor;
import net.idea.restnet.db.aalocal.CreateUsersDatabaseProcessor;
import net.idea.restnet.db.aalocal.user.CreateUser;
import net.idea.restnet.db.aalocal.user.DeleteUser;
import net.idea.restnet.db.aalocal.user.IUser;
import net.idea.restnet.db.aalocal.user.UpdateUser;
import net.idea.restnet.db.test.CRUDTest;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

public class User_crud_test  extends CRUDTest<Object,IUser>  {
	

	@Override
	protected CreateDatabaseProcessor getDBCreateProcessor() {
		return new CreateUsersDatabaseProcessor();
	}
	
	@Override
	public String getDBTables() {
		return "src/test/resources/net/idea/restnet/db/test/tables.xml";
	}
	
	
	@Override
	protected IQueryUpdate<Object,IUser> createQuery() throws Exception {
		IUser ref = new TestUser();
		ref.setUserName("QWERTY");
		ref.setPassword("ASDFG");
		CreateUser q =  new CreateUser(ref);
		q.setDatabaseName(getDatabase());
		return q;
	}

	@Override
	protected void createVerify(IQueryUpdate<Object,IUser> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT * from users where user_name='QWERTY'"));
		
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}

	@Override
	protected IQueryUpdate<Object,IUser> deleteQuery() throws Exception {
		IUser ref = new TestUser();
		ref.setUserName("test");
		DeleteUser q =  new DeleteUser(ref);
		q.setDatabaseName(getDatabase());
		return q;
	}

	@Override
	protected void deleteVerify(IQueryUpdate<Object,IUser> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM users where user_name='test'");
		Assert.assertEquals(0,table.getRowCount());
		c.close();
		
	}

	@Override
	protected IQueryUpdate<Object,IUser> updateQuery() throws Exception {
		IUser ref = new TestUser();
		ref.setUserName("test");
		ref.setPassword("NEW");
		UpdateUser q =  new UpdateUser(ref);
		q.setDatabaseName(getDatabase());
		return q;
	}

	@Override
	protected void updateVerify(IQueryUpdate<Object,IUser> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT user_name FROM users where user_pass=md5('NEW')");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals("test",table.getValue(0,"user_name"));
		c.close();
		
	}

	@Override
	protected IQueryUpdate<Object, IUser> createQueryNew()
			throws Exception {
		return null;
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<Object, IUser> query)
			throws Exception {
		
		
	}
	@Override
	public void testCreateNew() throws Exception {
	}

}
