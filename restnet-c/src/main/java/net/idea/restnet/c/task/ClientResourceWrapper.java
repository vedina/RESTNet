package net.idea.restnet.c.task;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.idea.restnet.i.aa.IAuthToken;

import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

public class ClientResourceWrapper extends ClientResource {
    protected static final String subjectid = "subjectid";
    private static ThreadLocal<IAuthToken> tokenFactory = null;

    public static IAuthToken getTokenFactory() {
	return tokenFactory == null ? null : tokenFactory.get();
    }

    public static void setTokenFactory(IAuthToken token) {
	if (ClientResourceWrapper.tokenFactory == null)
	    ClientResourceWrapper.tokenFactory = new ThreadLocal<IAuthToken>();
	else
	    ClientResourceWrapper.tokenFactory.remove();
	ClientResourceWrapper.tokenFactory.set(token);

    }

    public ClientResourceWrapper(Reference uri) {
	super(uri);
	String token = getToken();
	if (token != null)
	    addToken2Header(token);
    }

    public ClientResourceWrapper(String uri) {
	super(uri);
	String token = getToken();
	if (token != null)
	    addToken2Header(token);
    }

    @Override
    protected void doRelease() throws ResourceException {
	// setTokenFactory(null);
	super.doRelease();
    }

    protected void addToken2Header(String token) {
	Object extraHeaders = getRequest().getAttributes().get("org.restlet.http.headers");
	if (extraHeaders == null)
	    extraHeaders = new Form();
	((Form) extraHeaders).add(subjectid, token);
	getRequest().getAttributes().put("org.restlet.http.headers", extraHeaders);
    }

    protected String getToken() {
	IAuthToken tokenFactory = getTokenFactory();
	return tokenFactory == null ? null : tokenFactory.getToken();
    }

    public static synchronized HttpURLConnection getHttpURLConnection(String uri, String method, String mediaType)
	    throws IOException, MalformedURLException {
	URL url = null;

	try {
	    url = new URL(uri);
	} catch (MalformedURLException x) {
	    throw x;
	}
	HttpURLConnection uc = (HttpURLConnection) url.openConnection();
	uc.addRequestProperty("Accept", mediaType);
	uc.addRequestProperty("Referer", "http://localhost");
	uc.setDoOutput(true);
	uc.setRequestMethod(method);
	IAuthToken tokenFactory = ClientResourceWrapper.getTokenFactory();
	String token = tokenFactory == null ? null : tokenFactory.getToken();
	if (token != null)
	    uc.addRequestProperty("subjectid", token);
	return uc;
    }
}
