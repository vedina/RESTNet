package net.idea.restnet.db;

import net.idea.modbcum.c.DBConnectionConfigurable;
import net.idea.modbcum.i.LoginInfo;
import net.idea.modbcum.i.config.Preferences;

import org.restlet.Context;

public class DBConnection extends DBConnectionConfigurable<Context> {
	public DBConnection(Context context, String configFile) {
		super(context, configFile);
	}

	@Override
	protected void configurefromContext(LoginInfo li, Context context) {

		if (context.getParameters().getFirstValue(Preferences.DATABASE) != null)
			li.setDatabase(context.getParameters().getFirstValue(
					Preferences.DATABASE));
		if (context.getParameters().getFirstValue(Preferences.USER) != null)
			li.setUser(context.getParameters().getFirstValue(Preferences.USER));
		if (context.getParameters().getFirstValue(Preferences.PASSWORD) != null)
			li.setPassword(context.getParameters().getFirstValue(
					Preferences.PASSWORD));
		if (context.getParameters().getFirstValue(Preferences.HOST) != null)
			li.setHostname(context.getParameters().getFirstValue(
					Preferences.HOST));
		if (context.getParameters().getFirstValue(Preferences.PORT) != null)
			li.setPort(context.getParameters().getFirstValue(Preferences.PORT));
		if (context.getParameters().getFirstValue(Preferences.DRIVERNAME) != null)
			li.setDriverClassName(context.getParameters().getFirstValue(
					Preferences.DRIVERNAME));
	}

	protected java.util.logging.Logger getLogger() {
		return Context.getCurrentLogger();
	};

}
