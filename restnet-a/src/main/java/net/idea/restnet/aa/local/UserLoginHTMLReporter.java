package net.idea.restnet.aa.local;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.html.HTMLBeauty;

import org.restlet.Request;
import org.restlet.data.Reference;
import org.restlet.security.Role;
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
			String header = "";
			StringWriter writer = new StringWriter();
			writer.write("<table width='80%' id='users' border='0' cellpadding='0' cellspacing='1'>");
			writer.write("<tbody>");			
			if (item.getIdentifier()==null) {
				header = "Sign In";
	
				writer.write(String.format("<form method='post' action='%s/protected/signin?targetUri=%s'>",baseReference,redirect));
					
				writer.write(String.format("<tr><th align='right'>%s</th><td><input type='text' size='40' name='%s' value=''></td></tr>",
							"User name:&nbsp;","login"));
				writer.write(String.format("<tr><th align='right'>%s</th><td><input type='password' size='40' name='%s' value=''></td></tr>",
						"Password:&nbsp;","password"));
				writer.write("<tr><td></td><td><input align='bottom' type=\"submit\" value=\"Log in\"></td></tr>");
				//writer.write(String.format("<tr><th align='right'></th><td><input type='hidden' size='40' name='targetURI' value='%s/login'></td></tr>",baseReference));
				
				writer.write("</form>");

			} else {
				header = String.format("Welcome, %s",item.getIdentifier());
				writer.write(String.format("<form method='post' action='%s/protected/signout?targetUri=%s'>",baseReference,redirect));
				writer.write(String.format("<tr><th align='right'>%s</th><td>%s</td></tr>","User name:&nbsp;",item.getIdentifier()));
				
				for (Role role:getRequest().getClientInfo().getRoles()) {
					writer.write(String.format("<tr><th align='right'>%s</th><td>%s</td></tr>","Role:&nbsp;",role.getDescription()));
				}
				writer.write("<tr><td></td><td><input align='bottom' type=\"submit\" value=\"Log out\"></td></tr>");
				writer.write("</form>");
				
				writer.write("</tbody></table>");
				writer.write(myWorkspaceLinks());
				
		     }
			writer.write("</tbody></table>");
			output.write(htmlBeauty.printWidget(header, writer.toString()));
		} catch (Exception x) {
			x.printStackTrace();
		}
		
	};
	
	protected String myWorkspaceLinks() {
		return String.format("<a href='%s/%s'>My workspace</a>",getRequest().getRootRef(),"myaccount");
	}
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
