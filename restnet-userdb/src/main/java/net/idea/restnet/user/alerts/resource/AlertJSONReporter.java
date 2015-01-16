package net.idea.restnet.user.alerts.resource;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.reporter.AbstractJSONReporter;
import net.idea.restnet.user.alerts.db.DBAlert;

import org.restlet.Request;

public class AlertJSONReporter<Q extends IQueryRetrieval<DBAlert>> extends AbstractJSONReporter<DBAlert, Q> {
    /**
	 * 
	 */
    private static final long serialVersionUID = -4566136103208284105L;
    protected QueryURIReporter uriReporter;

    public AlertJSONReporter(Request request) {
	super("alert", (request == null ? null : request.getRootRef().toString()));
	uriReporter = new AlertURIReporter<IQueryRetrieval<DBAlert>>(request);

    }

    private static String format = "\n{\n\t\"uri\":\"%s\",\n\t\"id\": %s,\n\t\"title\": \"%s\",\n\t\"type\": \"%s\",\n\t\"content\": \"%s\",\n\t\"frequency\": \"%s\",\n\t\"interval\": %d,\n\t\"created\": %s,\n\t\"sent\": %s\n}";

    @Override
    public String item2json(DBAlert alert) {
	String uri = alert.getID() > 0 ? uriReporter.getURI(alert) : "";

	return String.format(format, uri, (alert.getID() > 0) ? String.format("\"A%d\"", alert.getID()) : null, alert
		.getTitle() == null ? "" : alert.getTitle(), alert.getQuery().getType().name(), alert.getQuery()
		.getContent() == null ? "" : alert.getQuery().getContent(), alert.getRecurrenceFrequency().name(),
		alert.getRecurrenceInterval(), alert.getCreated(), alert.getSentAt());

    }

    @Override
    public String getFileExtension() {
	return null;
    }
}