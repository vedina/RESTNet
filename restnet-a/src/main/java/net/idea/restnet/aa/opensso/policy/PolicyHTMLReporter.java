package net.idea.restnet.aa.opensso.policy;

import java.io.Writer;
import java.util.Iterator;

import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.html.HTMLBeauty;

import org.restlet.Request;
import org.restlet.data.Form;

/**
 * HTML rendering of policies
 * 
 * @author nina
 * 
 */
public class PolicyHTMLReporter extends PolicyURIReporter {
    protected boolean collapsed = false;
    protected HTMLBeauty htmlBeauty;

    public HTMLBeauty getHtmlBeauty() {
	return htmlBeauty;
    }

    public void setHtmlBeauty(HTMLBeauty htmlBeauty) {
	this.htmlBeauty = htmlBeauty;
    }

    protected int records = 0;
    /**
	 * 
	 */
    private static final long serialVersionUID = -1395741382892340760L;

    public PolicyHTMLReporter(Request ref, boolean collapsed, ResourceDoc doc) {
	this(ref, collapsed, doc, null);

    }

    public PolicyHTMLReporter(Request ref, boolean collapsed, ResourceDoc doc, HTMLBeauty htmlBeauty) {
	super(ref, doc);
	this.collapsed = collapsed;
	this.htmlBeauty = htmlBeauty;
    }

    /*
     * PolicyParser parser = new PolicyParser(policies.get(input));
     */

    @Override
    public void header(Writer output, Iterator<Policy> query) {
	super.header(output, query);

	try {
	    if (htmlBeauty == null)
		htmlBeauty = new HTMLBeauty();
	    htmlBeauty.writeHTMLHeader(output, "Policy", getRequest(), getDocumentation());
	    if (collapsed) {
		output.write("<h4>Create a new policy</h4>");
		output.write("<form method='POST' action=''>");
		output.write("<table>");
		output.write("<tr>");
		output.write("<td>URI</td>");
		output.write("<td>");
		output.write(String.format("<input type=\"text\" name='uri' title='%s' size=\"60\">",
			"URI to create policy for"));
		output.write("</td>");
		output.write("<td>");

		output.write("<select name='type'>");
		for (CallablePolicyCreator._type matcher : CallablePolicyCreator._type.values())
		    output.write(String.format("<option title='%s' value='%s' %s>%s</option>",
			    "The policy will allow access to a " + matcher.name() + " only\n", matcher.toString(),
			    CallablePolicyCreator._type.user.equals(matcher) ? "selected" : "", matcher));
		output.write("</select>");
		output.write("</td>");
		output.write("<td>");

		output.write(String.format("<input type=\"text\" name='name' title='%s' size=\"60\">",
			"User (OpenTox user) or group name (one of member or partner)"));

		output.write("</td>");
		output.write("</tr>");
		// methods
		output.write("<tr>");
		output.write("<td>Methods</td>");
		output.write("<td>");
		for (CallablePolicyCreator._method matcher : CallablePolicyCreator._method.values())
		    output.write(String.format("<input type=CHECKBOX name='%s' %s>%s", matcher.name(), matcher.name(),
			    matcher.name()));
		output.write("</td>");
		output.write("</tr>");
		output.write("<tr><td colspan='2' align='left'><input type='submit' value='Submit'></td></tr>");
		output.write("</table>");

		output.write("</form>");
		output.write("<hr>");

		Form form = request.getResourceRef().getQueryAsForm();
		String uri = form.getFirstValue("search");
		if (uri != null)
		    output.write(String.format("<h4>Policies for <a href='%s'>%s</a></h4>", uri, uri));
	    } else
		output.write("<h6>Use ?search=<URI> , or a search box above to retrieve policies of a given URI</h6>");
	} catch (Exception x) {

	}
    }

    public void processItem(Policy item, Writer output) {
	try {
	    records++;
	    String t = super.getURI(item);
	    output.write(String.format("Policy:&nbsp;<a href='%s'>%s</a>&nbsp;", t, item.getId()));

	    if (!collapsed) {
		output.write("<form action='?method=DELETE' method='POST'><input type='submit' value='Delete policy'></a>");
		output.write("<h3>Policy explanation</h3>");
		if (item.getXml() != null) {
		    PolicyParser parser = new PolicyParser(item.getXml());
		    output.write(parser.getHTML());
		}
		output.write("<h5>&nbsp;&nbsp;&nbsp;XML</h5>");
		output.write(String.format("&nbsp;&nbsp;&nbsp;<textarea rows='10' cols='80'>%s</textarea>",
			item.getXml() == null ? "Policy XML Not retrieved!" : item.getXml()));
	    }
	    output.write("<br>");
	} catch (Exception x) {
	    x.printStackTrace();
	}
    };

    @Override
    public void footer(Writer output, Iterator<Policy> query) {
	try {
	    output.write("</tbody></table>");

	    output.write(String.format(records == 0 ? "<h4>%sCreate a new policy for this resource.</h4>"
		    : "<h4>%d policies found.</h4>", records == 0 ? "Not found! " : records));

	    output.write(String.format("<a href='%s'>Back</a>", getRequest().getResourceRef().getParentRef()));
	    if (htmlBeauty == null)
		htmlBeauty = new HTMLBeauty();
	    htmlBeauty.writeHTMLFooter(output, OpenSSOPoliciesResource.resource, getRequest());
	    output.flush();
	} catch (Exception x) {

	}

    }
}
