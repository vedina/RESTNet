package net.idea.restnet.user.alerts.notification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.idea.restnet.b.Alert;
import net.idea.restnet.b.User;
import net.idea.restnet.u.mail.INotificationUtility;
import net.idea.restnet.u.mail.Notification;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;


/**
 * Simple alert notification. The message formatting is minimal, just URIs.
 */
public class SimpleNotificationEngine<ITEM> implements INotificationEngine {
    public String notificationSubject = "Alert Updates";

    public String getNotificationSubject() {
	return notificationSubject;
    }

    public void setNotificationSubject(String notificationSubject) {
	this.notificationSubject = notificationSubject;
    }

    protected static Logger log = Logger.getLogger(SimpleNotificationEngine.class.getName());
    private INotificationUtility utility;
    protected Reference root = null;

    /**
     * Uses the default configuration from the config file
     */
    public SimpleNotificationEngine(Reference root, String configFile) throws IOException {
	this(new Notification(configFile));
	this.root = root;
    }

    /**
     * @param utility
     *            implements the dependencies needed by the engine
     */
    public SimpleNotificationEngine(INotificationUtility utility) {
	this.utility = utility;
    }

    @Override
    public boolean sendAlerts(User user, List<? extends Alert> alerts, String token) throws Exception {
	String email = user.getEmail();
	if (email == null)
	    return false;

	StringBuilder content = new StringBuilder();
	for (Alert alert : alerts) {
	    // System.out.println(user.getEmail());
	    List<String> results = queryAlert(user, alert, token);
	    if ((results == null) || (results.size() == 0))
		continue;
	    // content.append(alert.getQuery());
	    content.append("\r\n");
	    for (String result : results) {
		content.append(result);
		content.append("\r\n");
	    }
	    content.append("\r\n");
	}
	if (content.length() > 0) {
	    content.append("You have been sent this email because you have signed up to receive " + notificationSubject
		    + "\r\n");
	    // System.out.println(email + content);
	    utility.sendNotification(email, notificationSubject, content.toString(), MediaType.TEXT_PLAIN.toString());
	    return true;
	}
	return false;

    }

    protected String formatURL(String url) {
	return url;
    }

    protected List<String> retrieve(Reference ref) throws Exception {
	HttpClient cli = new DefaultHttpClient();
	HttpGet httpGet = new HttpGet(ref.toString());
	httpGet.addHeader("Accept", "application/uri-list");
	httpGet.addHeader("Accept-Charset", "utf-8");
	InputStream in = null;
	try {
	    HttpResponse response = cli.execute(httpGet);
	    HttpEntity entity = response.getEntity();
	    in = entity.getContent();
	    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
		List<String> urls = new ArrayList<String>();
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = r.readLine()) != null) {
		    urls.add(formatURL(line.trim()));
		}
		return urls;
	    } else
		throw new IOException(String.format("Error reading URL %s\n%s", ref.toString(),
			response.getStatusLine()));
	} catch (Exception x) {
	    throw x;
	} finally {
	    try {
		if (in != null)
		    in.close();
	    } catch (Exception x) {
	    }
	    try {
		cli.getConnectionManager().shutdown();
	    } catch (Exception x) {
	    }
	}
    }

    protected List<String> retrieveByRIAP(Reference ref) throws Exception {
	ClientResource cr = null;
	Representation repr = null;
	try {
	    cr = new ClientResource(ref);
	    repr = cr.get(MediaType.TEXT_URI_LIST);
	    if (org.restlet.data.Status.SUCCESS_OK.equals(cr.getStatus())) {
		List<String> urls = new ArrayList<String>();
		BufferedReader r = new BufferedReader(new InputStreamReader(repr.getStream()));
		String line = null;
		while ((line = r.readLine()) != null) {
		    urls.add(formatURL(line.trim()));
		}
		return urls;
	    }
	} catch (ResourceException x) {
	    if (x.getStatus().equals(org.restlet.data.Status.CLIENT_ERROR_NOT_FOUND)) {
		// skip, this is ok
	    } else
		log.log(Level.WARNING, String.format("Error reading URL %s\n%s", ref.toString(), cr.getStatus()), x);
	    x.printStackTrace();
	} catch (Exception x) {
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
	return null;
    }

    protected List<String> queryAlert(User user, Alert alert, String token) throws Exception {
	switch (alert.getQuery().getType()) {
	case FREETEXT:
	    Reference ref = new Reference(String.format("%s%s?%s", root, alert.getTitle(), alert.getQuery()
		    .getContent()));
	    ref.addQueryParameter("modifiedSince", Long.toString(alert.getSentAt() / 1000));

	    if ("riap".equals(ref.getScheme()))
		return retrieveByRIAP(ref);
	    else
		return retrieve(ref);
	default:
	    throw new IllegalArgumentException("Unsupported alert type: " + alert.getQuery().getType());
	}
    }

}
