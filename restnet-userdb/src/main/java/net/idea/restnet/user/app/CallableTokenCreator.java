package net.idea.restnet.user.app;

import java.sql.Connection;
import java.util.UUID;

import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.restnet.b.AppToken;
import net.idea.restnet.db.aalocal.user.IDBConfig;
import net.idea.restnet.db.update.CallableDBUpdateTask;
import net.idea.restnet.user.app.db.AddApp;
import net.idea.restnet.user.app.db.DBUApp;
import net.idea.restnet.user.app.db.DeleteApp;
import net.idea.restnet.user.db.ReadUser;

public class CallableTokenCreator extends CallableDBUpdateTask<DBUApp, Form, String> implements IDBConfig {
	protected DBUApp item;

	public CallableTokenCreator(Method method, DBUApp item, Form input, String baseReference, Connection connection,
			String token, String usersdbname) {
		super(method, input, baseReference, connection, token);
		this.item = item;
		setDatabaseName(usersdbname);

	}

	protected String aadbname;

	@Override
	public void setDatabaseName(String name) {
		aadbname = name;
	}

	@Override
	public String getDatabaseName() {
		return aadbname;
	}

	@Override
	protected DBUApp getTarget(Form input) throws Exception {
		if (item == null || item.getUser() == null || item.getUser().getUserName() == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		String username = input.getFirstValue(ReadUser.fields.username.name());
		if (username == null || !username.equals(item.getUser().getUserName()))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		if (Method.POST.equals(method)) {
			DBUApp app = new DBUApp();
			app.setKey(new AppToken());
			app.setName(UUID.randomUUID().toString().substring(1, 32));
			app.setUser(item.getUser());
			return app;
		} else if (Method.PUT.equals(method)) {
			String token = input.getFirstValue("token");
			DBUApp app = new DBUApp();
			app.setKey(new AppToken(token));
			app.setUser(item.getUser());
			return app;
		}

		return null;
	}

	@Override
	protected IQueryUpdate<? extends Object, DBUApp> createUpdate(DBUApp target) throws Exception {
		if (Method.POST.equals(method)) {
			AddApp q = new AddApp();
			q.setObject(target);
			q.setGroup(target.getUser());
			return q;
		} else if (Method.PUT.equals(method)) {
			DeleteApp q = new DeleteApp();
			q.setObject(target);
			q.setGroup(target.getUser());
			return q;

		}
		return null;
	}

	@Override
	protected String getURI(DBUApp target) throws Exception {
		return String.format("%s/myaccount/apps", baseReference);
	}

}
