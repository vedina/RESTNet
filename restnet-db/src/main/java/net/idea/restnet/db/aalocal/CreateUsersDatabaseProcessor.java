package net.idea.restnet.db.aalocal;

import net.idea.restnet.db.CreateDatabaseProcessor;

public class CreateUsersDatabaseProcessor extends CreateDatabaseProcessor {

	public final static String version = "2.0";

	/**
	 */
	private static final long serialVersionUID = -335737998721944578L;
	public static final String SQLFile = "net/idea/restnet/db/aalocal/sql/users.sql";

	public CreateUsersDatabaseProcessor() {
		super();
	}

	protected String getVersion() {
		return version;
	}

	@Override
	public synchronized String getSQLFile() {
		return SQLFile;
	}

}
