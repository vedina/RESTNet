package net.idea.restnet.aa.opensso.users;

import java.io.Writer;
import java.util.Iterator;

import net.idea.restnet.aa.opensso.OpenSSOServicesConfig;
import net.idea.restnet.aa.opensso.OpenSSOUser;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.html.HTMLBeauty;

import org.restlet.Request;

/**
 * Generates HTML output for {@link AllAlgorithmsResource}
 * 
 * @author nina
 * 
 */
public class OpenSSOUserHTMLReporter extends OpenSSOUsersURIReporter {

    /**
	 * 
	 */
    private static final long serialVersionUID = 7544605965468875232L;
    protected boolean collapsed = false;
    protected final static String securecookie_warning = "Secure cookie is used to transfer the OpenSSO token within a web browser.<br>You will only be able to access protected resources via web browser, if accessing via https://";
    protected final static String cookie_warning = "HTTP cookie is used to transfer the OpenSSO token between web browser and the server.";
    protected HTMLBeauty htmlBeauty;

    public OpenSSOUserHTMLReporter(Request ref, ResourceDoc doc) {
	this(ref, doc, null);
    }

    public OpenSSOUserHTMLReporter(Request ref, ResourceDoc doc, HTMLBeauty htmlBeauty) {
	super(ref, doc);
	this.htmlBeauty = htmlBeauty;
    }

    @Override
    public void header(Writer output, Iterator<OpenSSOUser> query) {
	try {
	    if (htmlBeauty == null)
		htmlBeauty = new HTMLBeauty();
	    htmlBeauty.writeHTMLHeader(output, htmlBeauty.getTitle(), getRequest(), getDocumentation());
	} catch (Exception x) {

	}
    }

    public void processItem(OpenSSOUser item, Writer output) {
	try {
	    output.write("\n");
	    output.write("<table width='80%' id='users' border='0' cellpadding='0' cellspacing='1'>\n");
	    output.write("<tbody>");

	    output.write(String.format("<tr><th align='right'>OpenSSO service:&nbsp;</th><td>%s</td></tr>\n",
		    OpenSSOServicesConfig.getInstance().getOpenSSOService()));

	    if (item.getToken() == null) {
		output.write("<form method='post' action='?method=post'>\n");

		output.write(String
			.format("<tr><th align='right'>%s</th><td><input type='text' size='40' name='%s' value=''></td></tr>\n",
				"User name:&nbsp;", "user"));
		output.write(String
			.format("<tr><th align='right'>%s</th><td><input type='password' size='40' name='%s' value=''></td></tr>\n",
				"Password:&nbsp;", "password"));
		output.write(String
			.format("<tr><td title=''></td><td><input type=CHECKBOX name='subjectid_secure' %s>Use secure cookie for the OpenSSO token</option></td></tr>",
				item.isUseSecureCookie() ? "SELECTED CHECKED" : ""));
		output.write("<tr><td></td><td><input align='bottom' type=\"submit\" value=\"Log in\"></td></tr>\n");
		output.write("</form>");
		output.write("</tbody></table>\n");
	    } else {
		output.write("<form method='post' action='?method=delete'>");
		output.write(String.format("<tr><th align='right'>%s</th><td>%s</td></tr>\n", "User name:&nbsp;",
			item.getUsername()));
		output.write(String.format("<tr><th align='right'>%s</th><td>%s</td></tr>\n", "Token:&nbsp;", item
			.getToken().toString()));

		String warning = String.format("<h5><font color='red'>%s</font></h5>",
			item.isUseSecureCookie() ? securecookie_warning : cookie_warning);
		output.write(String.format("<tr><td></td><td>%s</td></tr>", warning));

		output.write("<tr><td></td><td><input align='bottom' type=\"submit\" value=\"Log out\"></td></tr>\n");
		output.write("</form>");

		output.write("</tbody></table>\n");
		output.write(String.format("<hr><a href='%s/bookmark/%s'>My workspace</a>\n",
			getRequest().getRootRef(), item.getUsername(), item.getUsername()));

	    }
	    output.write("\n");
	} catch (Exception x) {
	    x.printStackTrace();
	}
    };

    @Override
    public void footer(Writer output, Iterator<OpenSSOUser> query) {
	try {
	    if (htmlBeauty == null)
		htmlBeauty = new HTMLBeauty();
	    htmlBeauty.writeHTMLFooter(output, "OpenSSO User", getRequest());
	    output.flush();
	} catch (Exception x) {

	}
    }
}
