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

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.restnet.b.AppToken;
import net.idea.restnet.user.DBUser;
import net.idea.restnet.user.app.db.AddApp;
import net.idea.restnet.user.app.db.DBUApp;
import net.idea.restnet.user.app.db.DeleteApp;

public final class UApp_crud_test extends CRUDTest_<DBUser, DBUApp> {
	private final String _NEWTOKEN="eoUt+LnjvETKku6O1Rl4c7IbRAvDKU5bTv+RI5Sldz4=";
	private final String _OLDTOKEN="rQ5Eb6EdH1T4J34+4W66Jh7sTNxvbKYWVKkTJjQPXLA=";
	@Override
	protected IQueryUpdate<DBUser, DBUApp> createQuery() throws Exception {
		DBUser ref = new DBUser();
		ref.setUserName("test");
		DBUApp app = new DBUApp();
		app.setName("app");
		app.setReferer("referer");
		app.setKey(new AppToken(_NEWTOKEN));
		return new AddApp(app, ref);
	}

	@Override
	protected void createVerify(IQueryUpdate<DBUser, DBUApp> query) throws Exception {
		IDatabaseConnection c = getConnection();
		ITable table = c.createQueryTable("EXPECTED", String.format("SELECT token,referer,username from apps where name='app'"));
		Assert.assertEquals(1, table.getRowCount());
		Assert.assertEquals(_NEWTOKEN, table.getValue(0, "token"));
		Assert.assertEquals("test", table.getValue(0, "username"));
		c.close();
	}

	@Override
	protected IQueryUpdate<DBUser, DBUApp> deleteQuery() throws Exception {
		DBUApp app = new DBUApp();
		app.setKey(new AppToken(_OLDTOKEN));
		DBUser user = new DBUser();
		user.setUserName("test");
		return new DeleteApp(app, user);
	}

	@Override
	protected void deleteVerify(IQueryUpdate<DBUser, DBUApp> query) throws Exception {
		IDatabaseConnection c = getConnection();
		ITable table = c.createQueryTable("EXPECTED", String.format("SELECT token FROM apps where token='%s'",_OLDTOKEN));
		Assert.assertEquals(0, table.getRowCount());
		c.close();

	}
	
	@Override
	public void testUpdate() throws Exception {
	}

	@Override
	public void testCreateNew() throws Exception {
	}
	@Override
	protected IQueryUpdate<DBUser, DBUApp> updateQuery() throws Exception {
		return null;
	}

	@Override
	protected void updateVerify(IQueryUpdate<DBUser, DBUApp> query) throws Exception {
	}

	@Override
	protected IQueryUpdate<DBUser, DBUApp> createQueryNew() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<DBUser, DBUApp> query) throws Exception {
		// TODO Auto-generated method stub

	}

}
