package net.idea.restnet.c.resource;

import java.io.PrintWriter;
import java.io.StringWriter;

import net.idea.restnet.c.exception.RResourceException;
import net.idea.restnet.c.html.HTMLBeauty;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.service.StatusService;

public class RESTNetStatusService extends StatusService {
    protected HTMLBeauty htmlBeauty;

    public HTMLBeauty getHtmlBeauty() {
	return htmlBeauty;
    }

    public void setHtmlBeauty(HTMLBeauty htmlBeauty) {
	this.htmlBeauty = htmlBeauty;
    }

    public RESTNetStatusService() {
	super();
	setContactEmail("jeliazkova.nina@gmail.com");

    }

    @Override
    public Representation getRepresentation(Status status, Request request, Response response) {
	try {
	    boolean wrapInHTML = true;

	    if ((status.getThrowable() != null) && (status.getThrowable() instanceof RResourceException))
		wrapInHTML = ((RResourceException) status.getThrowable()).getVariant().equals(MediaType.TEXT_HTML);
	    else {
		Form headers = (Form) request.getAttributes().get("org.restlet.http.headers");
		String acceptHeader = headers.getValues("accept");
		if (acceptHeader != null)
		    wrapInHTML = acceptHeader.contains("text/html");
	    }

	    if (wrapInHTML) {
		StringWriter w = new StringWriter();

		if (htmlBeauty == null)
		    htmlBeauty = new HTMLBeauty();
		htmlBeauty.writeHTMLHeader(w, status.getReasonPhrase(), request, null);

		StringWriter details = null;

		if (status.getThrowable() != null) {
		    details = new StringWriter();
		    status.getThrowable().printStackTrace(new PrintWriter(details) {
			@Override
			public void print(String s) {
			    super.print(String.format("%s<br>", s));

			}
		    });
		}

		String detailsDiv = details == null ? ""
			: String.format(
				"<a href=\"javascript:toggleDiv('%s');\" style=\"background-color: #fff; padding: 5px 10px;\">Details</a>\n",
				"details");

		String errName = status.getReasonPhrase() ;
		String errDescription = status.getDescription();
		if (Status.CLIENT_ERROR_BAD_REQUEST.equals(status))
		    errName = "Invalid input";
		if (Status.CLIENT_ERROR_UNAUTHORIZED.equals(status)) {
		    errName = "Invalid user name or password";
		    errDescription = "The password you entered is incorrect.";

		}
		if (Status.CLIENT_ERROR_FORBIDDEN.equals(status))
		    errName = "You are not allowed to access this page";

		w.write(String.format(
			"<div class=\"ui-widget \" style=\"margin-top: 20px; padding: 0 .7em;\">\n"
				+ "<div class=\"ui-widget-header ui-corner-top\"><p>Error <a href='%s' target='help'>%s</a></p></div>\n"
				+ "<div class=\"ui-widget-content ui-corner-bottom \">\n"
				+ "<p><label title='%s'>%s</label></p><p>" + "%s\n" + "</p>\n"
				+ "<div class=\"ui-widget\" style='display: none;' id='details'><p>%s</p></div>\n"
				+ "</div></div>\n", status.getUri(), errName, errDescription, status.getReasonPhrase() ,
			detailsDiv, details == null ? "" : details));

		if (htmlBeauty == null)
		    htmlBeauty = new HTMLBeauty();
		htmlBeauty.writeHTMLFooter(w, status.getReasonPhrase() , request);
		return new StringRepresentation(w.toString(), MediaType.TEXT_HTML);
	    } else {
		if ((status.getThrowable() != null) && (status.getThrowable() instanceof RResourceException))
		    return ((RResourceException) status.getThrowable()).getRepresentation();
	    }
	    /*
	     * w.write(String.format(
	     * "ERROR :<br>Code: %d<br>Name: %s<br>URI: %s<br>Description: %s<br>"
	     * , status.getCode(), status.getName(), status.getUri(),
	     * status.getDescription() ));
	     */

	} catch (Exception x) {

	}
	if (status.getThrowable() == null)
	    return new StringRepresentation(status.toString(), MediaType.TEXT_PLAIN);
	else {
	    StringWriter w = new StringWriter();
	    status.getThrowable().printStackTrace(new PrintWriter(w));

	    return new StringRepresentation(w.toString(), MediaType.TEXT_PLAIN);
	}
    }

    @Override
    public Status getStatus(Throwable throwable, Request request, Response response) {
	if (throwable == null)
	    return response.getStatus();
	else if (throwable instanceof ResourceException) {
	    return ((ResourceException) throwable).getStatus();
	} else
	    return new Status(Status.SERVER_ERROR_INTERNAL, throwable);
    }
}
