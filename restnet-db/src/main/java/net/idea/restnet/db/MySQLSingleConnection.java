package net.idea.restnet.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.restlet.Context;

public class MySQLSingleConnection extends DBConnection {

	
	public MySQLSingleConnection(Context context, String configFile) {
		super(context, configFile);
	}

	public synchronized Connection getConnection(String connectionURI) throws AmbitException , SQLException{
		DriverManager.registerDriver (new com.mysql.jdbc.Driver());
		return DriverManager.getConnection(connectionURI);		
	}
}