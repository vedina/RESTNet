/* DeleteUser.java
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

package net.idea.restnet.u.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.db.aalocal.user.IDBConfig;
import net.idea.restnet.db.aalocal.user.IUser;
import net.idea.restnet.u.RegistrationStatus;
import net.idea.restnet.u.UserRegistration;

public class UpdateRegistrationStatus extends AbstractUpdate<IUser, UserRegistration> implements IDBConfig {

    public static final String delete_sql = "update %s%suser_registration set status=?, confirmed=now() where user_name=?";

    public UpdateRegistrationStatus(IUser user, RegistrationStatus status) {
	super();
	UserRegistration ur = new UserRegistration();
	ur.setStatus(status);
	setObject(ur);
	setGroup(user);
    }

    public UpdateRegistrationStatus(IUser user) {
	this(user, RegistrationStatus.disabled);
    }

    public UpdateRegistrationStatus() {
	this(null);
    }

    public List<QueryParam> getParameters(int index) throws AmbitException {
	List<QueryParam> params = new ArrayList<QueryParam>();
	params.add(new QueryParam<String>(String.class, getObject().getStatus() == null ? RegistrationStatus.disabled
		.name() : getObject().getStatus().name()));
	params.add(new QueryParam<String>(String.class, getGroup().getUserName()));
	return params;

    }

    public String[] getSQL() throws AmbitException {
	return new String[] { String.format(delete_sql, databaseName == null ? "" : databaseName,
		databaseName == null ? "" : ".") };
    }

    public void setID(int index, int id) {

    }

    protected String databaseName = null;

    @Override
    public void setDatabaseName(String name) {
	databaseName = name;
    }

    @Override
    public String getDatabaseName() {
	return databaseName;
    }
}
