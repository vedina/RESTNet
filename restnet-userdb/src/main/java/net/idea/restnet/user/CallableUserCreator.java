package net.idea.restnet.user;

import java.net.URL;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.restnet.db.aalocal.DBRole;
import net.idea.restnet.db.aalocal.user.IDBConfig;
import net.idea.restnet.db.update.CallableDBUpdateTask;
import net.idea.restnet.groups.DBOrganisation;
import net.idea.restnet.groups.DBProject;
import net.idea.restnet.groups.user.db.AddGroupsPerUser;
import net.idea.restnet.resources.Resources;
import net.idea.restnet.u.UserCredentials;
import net.idea.restnet.u.UserRegistration;
import net.idea.restnet.u.mail.Notification;
import net.idea.restnet.user.db.CreateUser;
import net.idea.restnet.user.db.DeleteUser;
import net.idea.restnet.user.db.ReadUser;
import net.idea.restnet.user.db.UpdateCredentials;
import net.idea.restnet.user.db.UpdateUser;
import net.idea.restnet.user.resource.UserURIReporter;

import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

public abstract class CallableUserCreator extends
		CallableDBUpdateTask<DBUser, Form, String> implements IDBConfig {
	protected boolean enableEmailVerification = true;
	protected UserURIReporter<IQueryRetrieval<DBUser>> reporter;
	protected DBUser user;
	protected boolean passwordChange;
	protected UserCredentials credentials;
	protected String aadbname;
	protected UserRegistration registration = null;
	protected String subject = "Database User Activation";

	protected abstract String getSender();

	protected abstract String getSenderName();

	protected abstract String getSystemName();

	protected SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy.MM.dd 'at' HH:mm:ss z");

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	protected String emailContent = "Dear %s %s,\n\n"
			+ "Thanks for your interest in %s. Please click on the following link to activate your %s account that you have created on %s:\n"
			+ "%s%s%s?code=%s\n\n"
			+ "If you click the link and it appears to be broken, please copy and paste it into a new browser window.\n\n"
			+ "Please note that your registration will be cancelled automatically if it is not activated within 48 hours "
			+ "(before %s). "
			+ "If you miss this deadline you should start over the registration procedure and obtain a new activation code.\n\n"
			+ "If you did not register an account for accessing %s, please ignore this message and let the request expire on its own.\n\n"
			+ "Yours faithfully,\n" + "%s\n%s\n";

	public CallableUserCreator(Method method, DBUser item,
			UserURIReporter<IQueryRetrieval<DBUser>> reporter, Form input,
			String baseReference, Connection connection, String token,
			boolean passwordChange, boolean enableEmailVerification,
			String usersdbname) {
		super(method, input, connection, token);
		this.reporter = reporter;
		this.user = item;
		this.baseReference = baseReference;
		this.passwordChange = passwordChange;
		this.enableEmailVerification = enableEmailVerification;
		setDatabaseName(usersdbname);
	}

	@Override
	protected DBUser getTarget(Form input) throws Exception {

		if (passwordChange) {
			if (Method.PUT.equals(method)) {
				credentials = new UserCredentials(
						input.getFirstValue("pwdold"),
						input.getFirstValue("pwd1"));

				return user;
			} else
				throw new Exception("User empty");
		} else {
			if (input != null)
				credentials = new UserCredentials(input.getFirstValue("pwd1"),
						input.getFirstValue("pwd2"));
		}
		if (input == null)
			return user;

		if (Method.POST.equals(method)
				&& (input.getFirstValue(ReadUser.fields.email.name()) == null)) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					"e-mail address not specified!");
		}

		DBUser user = new DBUser();
		user.setCredentials(credentials);
		if (Method.PUT.equals(method))
			user.setID(this.user.getID());
		user.setUserName(input.getFirstValue(ReadUser.fields.username.name()));
		user.setFirstname(input.getFirstValue(ReadUser.fields.firstname.name()));
		user.setLastname(input.getFirstValue(ReadUser.fields.lastname.name()));
		user.setTitle(input.getFirstValue(ReadUser.fields.title.name()));
		user.setKeywords(input.getFirstValue(ReadUser.fields.keywords.name()));
		user.setEmail(input.getFirstValue(ReadUser.fields.email.name()));
		if (input.getFirstValue(ReadUser.fields.reviewer.name()) != null) // this
			// is
			// a
			// checkbox,
			// not
			// radio
			// box!
			user.setReviewer(ReadUser.fields.reviewer.name().equals(
					input.getFirstValue(ReadUser.fields.reviewer.name())));
		try {
			user.setHomepage(new URL(input
					.getFirstValue(ReadUser.fields.homepage.name())));
		} catch (Exception x) {
		}
		try {
			user.setWeblog(new URL(input.getFirstValue(ReadUser.fields.weblog
					.name())));
		} catch (Exception x) {
		}

		//
		String organisation = input.getFirstValue("affiliation");
		if ((organisation != null) && !"".equals(organisation.trim())) {
			DBOrganisation org = new DBOrganisation();
			org.setTitle(organisation);
			user.addOrganisation(org);
		}

		String[] values = input.getValuesArray("organisation_uri");
		if (values != null)
			for (String value : values)
				try {
					DBOrganisation org = new DBOrganisation();
					org.setResourceID(new URL(value));
					org.setID(org.parseURI(baseReference));
					if (org.getID() > 0)
						user.addOrganisation(org);
				} catch (Exception x) {
				}

		values = input.getValuesArray("project_uri");
		if (values != null)
			for (String value : values)
				try {
					DBProject org = new DBProject();
					org.setResourceID(value);
					org.setID(org.parseURI(baseReference));
					if (org.getID() > 0)
						user.addProject(org);
				} catch (Exception x) {
				}
		return user;
	}

	protected DBRole getDefaultRole() {
		return new DBRole("user", "Any user");
	}

	@Override
	protected IQueryUpdate<? extends Object, DBUser> createUpdate(DBUser user)
			throws Exception {
		if (passwordChange)
			return new UpdateCredentials(credentials, user, getDatabaseName());
		if (Method.POST.equals(method)) {
			registration = new UserRegistration();
			return new CreateUser(user, registration, getDefaultRole(),
					getDatabaseName());
		} else if (Method.DELETE.equals(method))
			return new DeleteUser(user);
		else if (Method.PUT.equals(method))
			return new UpdateUser(user);
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected String getURI(DBUser user) throws Exception {
		return reporter.getURI(user);
	}

	@Override
	protected Object executeQuery(IQueryUpdate<? extends Object, DBUser> query)
			throws Exception {
		Object result = super.executeQuery(query);

		if (Method.POST.equals(method))
			try {
				DBUser user = query.getObject();
				if ((user.getOrganisations() != null)
						&& (user.getOrganisations().size() > 0)) {
					for (int i = user.getOrganisations().size() - 1; i >= 0; i--)
						if (((DBOrganisation) user.getOrganisations().get(i))
								.getID() <= 0)
							user.getOrganisations().remove(i);
					if (user.getOrganisations().size() > 0) {
						AddGroupsPerUser q = new AddGroupsPerUser(user,
								user.getOrganisations());
						exec.process(q);
					}
				}
				if ((user.getProjects() != null)
						&& (user.getProjects().size() > 0)) {
					for (int i = user.getProjects().size() - 1; i >= 0; i--)
						if (((DBProject) user.getProjects().get(i)).getID() <= 0)
							user.getProjects().remove(i);
					if (user.getProjects().size() > 0) {
						AddGroupsPerUser q = new AddGroupsPerUser(user,
								user.getProjects());
						exec.process(q);
					}
				}
			} catch (Exception x) {
				x.printStackTrace();
			}
		try {
			query.getObject().setRegisteredAt(System.currentTimeMillis());
		} catch (Exception x) {
			query.getObject().setRegisteredAt(0);
		}
		return result;
	}

	@Override
	protected String getURI(DBUser target, Method method) throws Exception {
		if (passwordChange)
			return String.format("%s%s", baseReference, Resources.myaccount);
		else if (Method.POST.equals(method) && registration != null
				&& target != null && target.getEmail() != null) {

			if (enableEmailVerification) {
				Date registeredAt = new Date(target.getRegisteredAt());
				Calendar cal = Calendar.getInstance();
				cal.setTime(registeredAt);
				cal.add(Calendar.DATE, 2);
				Notification notification = new Notification(getConfig());
				notification.sendNotification(target.getEmail(), String.format(
						"%s (%s %s)", subject, target.getFirstname(),
						target.getLastname()), String.format(emailContent,
						target.getFirstname(), target.getLastname(),
						getSystemName(), getSystemName(),
						dateFormat.format(registeredAt), baseReference,
						Resources.register, Resources.confirm,
						registration.getConfirmationCode(),
						dateFormat.format(cal.getTime()), getSystemName(),
						getSenderName(), getSender()), "text/plain");
				return String.format("%s%s%s", baseReference,
						Resources.register, Resources.notify);
			} else
				return super.getURI(target, method);
		} else
			return super.getURI(target, method);

	}

	protected abstract String getConfig();

	@Override
	public String toString() {
		if (passwordChange)
			return String.format("Password change");
		else if (Method.POST.equals(method)) {
			return String.format("Create user");
		} else if (Method.PUT.equals(method)) {
			return String.format("Update user");
		} else if (Method.DELETE.equals(method)) {
			return String.format("Delete user");
		}
		return "Read user";
	}

	@Override
	public void setDatabaseName(String name) {
		aadbname = name;
	}

	@Override
	public String getDatabaseName() {
		return aadbname;
	}
}
