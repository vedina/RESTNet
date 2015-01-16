package net.idea.restnet.user.db;

import net.idea.restnet.u.AbstractUpdateCredentials;
import net.idea.restnet.u.UserCredentials;
import net.idea.restnet.user.DBUser;

public class UpdateCredentials extends AbstractUpdateCredentials<DBUser> {

    public UpdateCredentials(UserCredentials c, DBUser ref, String dbname) {
	super(c, ref);
	setDatabaseName(dbname);
    }

    @Override
    public String getUserName(DBUser user) {
	return user.getUserName();
    }

}
