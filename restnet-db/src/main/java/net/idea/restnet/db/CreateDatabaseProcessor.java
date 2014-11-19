package net.idea.restnet.db;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.idea.modbcum.i.config.Preferences;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.AbstractDBProcessor;

/**
 * Creates tables in an existing database. If the database does not exist, or has tables, the call will fail.
 * Note the behaviour is changed from ambit-db!
 * @author nina
 *
 */
public abstract class CreateDatabaseProcessor  extends AbstractDBProcessor<String,String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5622845003941051768L;

	
	@Override
	public String process(String database) throws AmbitException {
		try {
			if (!dbExists(database)) 
				throw new AmbitException(
						String.format("Database `%s` does not exist. \nPlease create the database and grant privileges.",database));
						
			List<String> tables = tablesExists(database);
	        if (tables.size()==0)
	        	createTables(database);
	        else if (!tables.contains("version")) { //
	        	dropTables(database,tables);
	        	createTables(database);
	        } else throw new AmbitException(String.format("Empty database `%s` is expected, but it has %d tables!",database,tables));
		} catch (AmbitException x) {
			throw x;
		} catch (Exception x) {
			x.printStackTrace();
			throw new AmbitException(x);
		}
		
        try {
        	Preferences.setProperty(Preferences.DATABASE, database.toString());
        	Preferences.saveProperties(getClass().getName());
        } catch (Exception x) {
        	throw new AmbitException(x);
        }
        return database;
    }
	
    public void createTables(String newDB) throws SQLException, FileNotFoundException {
        try {
        	//URL url = this.getClass().getClassLoader().getResource(getSQLFile());
        	//if (url ==null) throw new FileNotFoundException(String.format("Can't find %s!",url.getFile()));
        	
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(getSQLFile());
                  
            if (in == null) throw new FileNotFoundException(String.format("Can't find %s!",getSQLFile()));
            
                Statement t = connection.createStatement();
                t.execute(String.format("USE `%s`",newDB));
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = null;                         
                StringBuffer table = new StringBuffer();
                String delimiter = ";";
                while (true) {

                        line = reader.readLine();
                        if (line == null) break;
                        if (line.toUpperCase().startsWith("DELIMITER")) {
                            delimiter = line.substring(line.indexOf("DELIMITER")+10).trim();
                            logger.log(Level.FINE,table.toString());
                            //t.execute(table.toString());
                            table = new StringBuffer();                             
                            continue;
                        }
                        
                        if (line.trim().toUpperCase().startsWith("END "+delimiter)) {
                            table.append("END");
                            delimiter = ";";
                            int ok = t.executeUpdate(table.toString());
                            logger.log(Level.FINE,table.toString());
                            table = new StringBuffer();                            
                            continue;
                        }                        
                        if (line == null) break;
                        if (line.trim().equals("")) continue;
                        if (line.indexOf("--") == 0) continue;
                        table.append(line);
                        table.append("\n");
                        if (line.indexOf(delimiter) >= 0) {
                            logger.log(Level.FINE,table.toString());
                            t.executeUpdate(table.toString());
                            
                            logger.log(Level.FINE,table.toString());
                            table = new StringBuffer();
                        }

                }
                in.close();
                reader.close();
                
                t.close();
        } catch (IOException x) {
            throw new SQLException(x.getMessage());
        }
        finally {
        	
        }
    }
    
    
    public abstract String getSQLFile();
    protected abstract String getVersion();
  
    public boolean isSameVersion(String dbVersion) throws Exception {
    	return getVersion().equals(dbVersion);
    }
    
	public boolean dbExists(String dbname) throws Exception {
		boolean ok = false;
		ResultSet rs = null;
		Statement st = null;
		try {
    	
			st = connection.createStatement();
			rs = st.executeQuery("show databases");
			while (rs.next()) {
				if (dbname.equals(rs.getString(1))) {
					ok = true;
					//break; there was smth wrong with not scrolling through all records
				}
			}
			
		} catch (Exception x) {
			throw x;
		} finally {
			try {if (rs != null) rs.close();} catch (Exception x) {}
			try {if (st != null) st.close();} catch (Exception x) {}
		}
		return ok;
	}	
	

	public List<String> tablesExists(String dbname) throws Exception {
		int tables = 0;
		ResultSet rs = null;
		Statement st = null;
		List<String> table_names = new ArrayList<String>();
		try {
			st = connection.createStatement();
			rs = st.executeQuery(String.format("Use `%s`",dbname)); //just in case
		} catch (Exception x) {
			throw x;			
		} finally {
			try {if (rs != null) rs.close();} catch (Exception x) {}
			try {if (st != null) st.close();} catch (Exception x) {}
		}			
		try {
			st = connection.createStatement();
			rs = st.executeQuery("show full tables where Table_Type != 'VIEW'");
			while (rs.next()) {
				tables++;
				table_names.add(rs.getString(1));
			}
			
		} catch (Exception x) {
			throw x;
		} finally {
			try {if (rs != null) rs.close();} catch (Exception x) {}
			try {if (st != null) st.close();} catch (Exception x) {}
		}
		return table_names;
	}		
	public void dropTables(String dbname,List<String> table_names) throws Exception {
			dropTables(dbname, table_names.toArray(new String[table_names.size()]));
	}
	public void dropTables(String dbname,String[] table_names) throws Exception {
		Statement st = null;
		try {
			st = connection.createStatement();
			st.addBatch(String.format("Use `%s`",dbname)); //just in case
			st.addBatch("SET FOREIGN_KEY_CHECKS = 0");
			for (String table : table_names) {
				String sql = String.format("drop table `%s`",table);
				st.addBatch(sql); 
			}
			st.addBatch("SET FOREIGN_KEY_CHECKS = 1");
			st.executeBatch();
		} catch (Exception x) {
			throw x;			
		} finally {
			try {if (st != null) st.close();} catch (Exception x) {}
		}			
	
	}
	/**
	 * Assumes table version exists!
	 * @param dbname
	 * @return
	 * @throws Exception
	 */
	public String getDbVersion(String dbname) throws Exception {
		String version = null;
		ResultSet rs = null;
		Statement st = null;
		
		try {
			st = connection.createStatement();
			rs = st.executeQuery(String.format("Use `%s`",dbname)); //just in case
		} catch (Exception x) {
			throw x;			
		} finally {
			try {if (rs != null) rs.close();} catch (Exception x) {}
			try {if (st != null) st.close();} catch (Exception x) {}
		}			
		try {
			st = connection.createStatement();
			rs = st.executeQuery("select concat(idmajor,'.',idminor) from version");
			while (rs.next()) {
				version = rs.getString(1);
			}
			
		} catch (Exception x) {
			throw x;
		} finally {
			try {if (rs != null) rs.close();} catch (Exception x) {}
			try {if (st != null) st.close();} catch (Exception x) {}
		}
		return version;
	}		

}
