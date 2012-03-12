package net.idea.restnet.aa.local;

import java.io.Writer;
import java.util.Iterator;

import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.html.HTMLBeauty;

import org.restlet.Request;
import org.restlet.data.Reference;
import org.restlet.security.User;

/**
 * Generates HTML output for {@link AllAlgorithmsResource}
 * @author nina
 *
 */
public class UserLoginHTMLReporter<U extends User> extends UserLoginURIReporter<U> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7544605965468875232L;
	protected boolean collapsed = false;
    protected HTMLBeauty htmlBeauty;
    
    public UserLoginHTMLReporter(Request ref, ResourceDoc doc) {
    	this(ref,doc,null);
    }
	public UserLoginHTMLReporter(Request ref, ResourceDoc doc,HTMLBeauty htmlBeauty) {
		super(ref,doc);
		this.htmlBeauty = htmlBeauty;
	}
	@Override
	public void header(Writer output, Iterator<U> query) {
		try {
			if (htmlBeauty==null) htmlBeauty = new HTMLBeauty();
			htmlBeauty.writeHTMLHeader(output, htmlBeauty.getTitle(), getRequest(),getDocumentation()
					);
			
			
			
		} catch (Exception x) {
			
		}
	}
	public void processItem(U item, Writer output) {
		
		try {
			String redirect = Reference.encode(String.format("%s/login",baseReference));
			if (item.getIdentifier()==null) {
				output.write("<table width='80%' id='users' border='0' cellpadding='0' cellspacing='1'>");
				output.write("<tbody>");
	
				output.write(String.format("<form method='post' action='%s/protected/signin?targetUri=%s'>",baseReference,redirect));
					
				output.write(String.format("<tr><th align='right'>%s</th><td><input type='text' size='40' name='%s' value=''></td></tr>",
							"User name:&nbsp;","login"));
				output.write(String.format("<tr><th align='right'>%s</th><td><input type='password' size='40' name='%s' value=''></td></tr>",
						"Password:&nbsp;","password"));
				output.write("<tr><td></td><td><input align='bottom' type=\"submit\" value=\"Log in\"></td></tr>");
				//output.write(String.format("<tr><th align='right'></th><td><input type='hidden' size='40' name='targetURI' value='%s/login'></td></tr>",baseReference));
				
				output.write("</form>");
				output.write("</tbody></table>");
			} else {
				output.write(String.format("<form method='post' action='%s/protected/signout?targetUri=%s'>",baseReference,redirect));
				output.write(String.format("<tr><th align='right'>%s</th><td>%s</td></tr>","User name:&nbsp;",item.getIdentifier()));

				output.write("<tr><td></td><td><input align='bottom' type=\"submit\" value=\"Log out\"></td></tr>");
				output.write("</form>");
				
				output.write("</tbody></table>");
				output.write(String.format("<hr><a href='%s/bookmark/%s'>My workspace</a>",getRequest().getRootRef(),item.getIdentifier(),item.getIdentifier()));
		     }
		} catch (Exception x) {
			x.printStackTrace();
		}
		
	};
	@Override
	public void footer(Writer output, Iterator<U> query) {
		try {
			if (htmlBeauty==null) htmlBeauty = new HTMLBeauty();
			
			htmlBeauty.writeHTMLFooter(output, "User", getRequest());
			output.flush();
		} catch (Exception x) {
			
		}
	}
}
