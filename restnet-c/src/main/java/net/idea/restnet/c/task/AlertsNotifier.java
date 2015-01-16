package net.idea.restnet.c.task;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.opentox.aa.exception.AAException;
import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

public abstract class AlertsNotifier implements Callable<String> {
    protected OpenSSOToken ssoToken;
    private final Logger logger;

    public AlertsNotifier() {
	super();
	logger = Logger.getLogger(getClass().getName());
    }

    private Properties loadProperties() throws Exception {
	InputStream in = null;
	try {
	    Properties properties = new Properties();
	    in = this.getClass().getClassLoader().getResourceAsStream(getConfig());
	    properties.load(in);
	    return properties;
	} catch (Exception x) {
	    logger.log(Level.SEVERE, "Error reading " + getConfig(), x);
	    throw x;
	} finally {
	    try {
		in.close();
	    } catch (Exception xx) {
	    }
	}
    }

    protected abstract String getConfig();

    protected void login() throws ResourceException, AAException, Exception {

	logger.log(Level.INFO, "Login not implemented ");
    }

    protected void setLoginCredentials(Form headers) throws Exception {
	if (ssoToken == null)
	    return;
	if (ssoToken.getToken() == null)
	    return;
	headers.add("subjectid", ssoToken.getToken());
	logger.log(Level.INFO, ssoToken.getToken());

    }

    /**
     * Sends POST to the /notification resource
     * 
     * @throws ResourceException
     * @throws AAException
     * @throws Exception
     */
    public String call() throws Exception {

	login();
	ClientResource cr = null;
	Representation repr = null;
	try {
	    Form form = new Form();
	    form.add("search", "");

	    cr = new ClientResource("riap://component/admin/notification");
	    Form headers = (Form) cr.getRequest().getAttributes().get("org.restlet.http.headers");
	    if (headers == null) {
		headers = new Form();
		cr.getRequest().getAttributes().put("org.restlet.http.headers", headers);
	    }
	    setLoginCredentials(headers);
	    repr = cr.post(form.getWebRepresentation(), MediaType.TEXT_URI_LIST);
	    return (repr != null) ? repr.getText() : cr.getStatus().toString();
	} catch (ResourceException x) {
	    if (Status.CLIENT_ERROR_NOT_FOUND.equals(x.getStatus())) {
		logger.log(Level.INFO, "No active alerts found");
		return x.getStatus().toString();
	    } else
		throw x;
	} catch (Exception x) {
	    x.printStackTrace();
	    throw x;
	} finally {
	    try {
		repr.release();
	    } catch (Exception x) {
	    }
	    try {
		cr.release();
	    } catch (Exception x) {
	    }
	}
    }
}
