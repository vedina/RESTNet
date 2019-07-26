/* DbUnitTest.java
 * Author: nina
 * Date: Jan 7, 2009
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

package net.idea.restnet.db.test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.statement.IBatchStatement;
import org.dbunit.database.statement.IStatementFactory;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import net.idea.modbcum.i.config.ConfigProperties;
import net.idea.restnet.db.CreateDatabaseProcessor;

public abstract class DbUnitTest {
	protected ConfigProperties properties = new ConfigProperties();
	protected static Logger logger = Logger.getLogger(DbUnitTest.class.getName());

	protected String getConfig() {
		return "net/idea/restnet/db/aalocal/aalocal.pref";
	}

	protected String getHost() {
		return properties.getPropertyWithDefault("Host", getConfig(), "localhost");
	}

	protected String getDatabase() {
		return properties.getPropertyWithDefault("database.test", getConfig(), "test");
	}

	protected String getPort() {
		return properties.getPropertyWithDefault("database.test.port", getConfig(), "3306");
	}

	protected String getUser() {
		return properties.getPropertyWithDefault("database.user.test", getConfig(), "guest");
	}

	protected String getPWD() {
		return properties.getPropertyWithDefault("database.user.test.password", getConfig(), "guest");
	}

	protected abstract CreateDatabaseProcessor getDBCreateProcessor();

	@Before
	public void setUp() throws Exception {
		IDatabaseConnection c = getConnection(getHost(), getDatabase(), getPort(), getUser(), getPWD());
		DatabaseConfig config = c.getConfig();
		config.setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, Boolean.TRUE);
		config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
		Connection conn = c.getConnection();
		conn.setAutoCommit(false);
		try {
			CreateDatabaseProcessor db = getDBCreateProcessor();
			db.setConnection(conn);
			if (!db.dbExists(getDatabase())) {
				db.process(getDatabase());
			} else {
				List<String> tables = db.tablesExists(getDatabase());
				if (tables.size() == 0)
					db.process(getDatabase());
				else if (!tables.contains("version")) {
					db.dropTables(getDatabase(), tables);
					db.process(getDatabase());
				} else {
					String dbVersion = db.getDbVersion(getDatabase());
					if (!db.isSameVersion(dbVersion)) {
						db.dropTables(getDatabase(), tables);
						db.process(getDatabase());
					}
				}
			}
			conn.commit();
		} catch (Exception x) {
			conn.rollback();
			throw x;
		} finally {
			c.close();
		}

	}

	protected IDatabaseConnection getConnection(String host, String db, String port, String user, String pass)
			throws Exception {
		System.out.println(String.format("%s\t%s\t%s\t%s", host, db, port, user));
		Class.forName("com.mysql.jdbc.Driver");
		Connection jdbcConnection = DriverManager.getConnection(String.format(
				"jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=UTF8&characterSetResults=UTF-8&profileSQL=%s",
				host, port, db, Boolean.toString(isProfileSQL())), user, pass);
		// SET NAMES utf8
		IDatabaseConnection c = new DatabaseConnection(jdbcConnection);
		DatabaseConfig config = c.getConfig();
		config.setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, Boolean.TRUE);
		config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
		return c;
	}

	protected boolean isProfileSQL() {
		return false;
	}

	protected IDatabaseConnection getConnection() throws Exception {
		return getConnection(getHost(), getDatabase(), getPort(), getUser(), getPWD());
	}

	/**
	 * Returns path to tables.xml
	 * 
	 * @return
	 */
	public abstract String getDBTables();

	public void setUpDatabase(String xmlfile) throws Exception {

		// This ensures all tables as defined in the schema are cleaned up, and
		// is a single place to modify if a schema changes
		String dbtables = getDBTables();
		try (InputStream in = getClass().getClassLoader().getResourceAsStream(dbtables)) {
			initDB(in, DatabaseOperation.DELETE_ALL, true);
		}
		// This will import only records, defined in the xmlfile
		try (InputStream in = getClass().getClassLoader().getResourceAsStream(xmlfile)) {
			initDB(in, DatabaseOperation.INSERT, false);
		}
	}

	private void initDB(InputStream xmlin, DatabaseOperation op, boolean admin) throws Exception {
		Assert.assertNotNull(xmlin);
		IDatabaseConnection connection = admin ? getConnection(getHost(), getDatabase(), getPort(), getUser(), getPWD())
				: getConnection();

		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		builder.setCaseSensitiveTableNames(false);
		IDataSet dataSet = builder.build(xmlin);
		try {
			// DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
			
			op.execute(connection, dataSet);
		} catch (Exception x) {
			logger.log(Level.SEVERE, x.getMessage(), x);
			throw x;
		} finally {
			connection.close();
			if (xmlin != null)
				xmlin.close();
		}
	}

	@Test
	public void emptyTest() {

	}
}
