package net.idea.restnet.u;

import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.r.QueryReporter;

import org.restlet.Request;

public class RegistrationJSONReporter extends
	QueryReporter<UserRegistration, IQueryRetrieval<UserRegistration>, Writer> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 3537339785122677311L;
    protected String comma = null;

    public RegistrationJSONReporter(Request request) {
	super();
    }

    @Override
    public void header(Writer output, IQueryRetrieval<UserRegistration> query) {
	try {
	    output.write("{\"confirmation\": [");
	} catch (Exception x) {
	}

    }

    private static String format = "\n\t{\"code\":\"%s\",\"date\":\"%s\",\"status\":\"%s\"}";

    @Override
    public Object processItem(UserRegistration item) throws Exception {
	try {
	    if (comma != null)
		getOutput().write(comma);

	    getOutput().write(
		    String.format(format, item.getConfirmationCode(), item.getTimestamp_confirmed(), item.getStatus()
			    .name()));

	    comma = ",";
	} catch (Exception x) {
	    x.printStackTrace();
	}
	return item;
    }

    @Override
    public void footer(Writer output, IQueryRetrieval<UserRegistration> query) {
	try {
	    output.write("\n]\n}");
	} catch (Exception x) {
	}
    }
}