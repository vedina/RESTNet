package net.idea.restnet.sparql;

import java.io.Writer;
import java.util.List;

import net.idea.restnet.c.html.HTMLBeauty;

import org.apache.jena.query.Query;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.util.PrintUtil;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

public class SPARQL2HTMLReporter extends AbstractSPARQLReporter<Writer> {
    /**
	 * 
	 */
    private static final long serialVersionUID = 558500008620063231L;
    protected HTMLBeauty htmlBeauty;

    public SPARQL2HTMLReporter(Model model, MediaType mediaType, Request request, HTMLBeauty htmlBeauty) {
	super(model, mediaType, request);
	this.htmlBeauty = htmlBeauty;
    }

    @Override
    public void header(Writer writer, String query) {
	try {
	    htmlBeauty.writeHTMLHeader(writer, "TDB", request, null);
	    writer.write(String.format("<h3>Ontology service&nbsp;%s triples</h3>", model == null ? 0 : model.size()));
	    writer.write(String
		    .format("<a href=\"javascript:toggleDiv('%s');\" style=\"background-color: #fff; padding: 5px 10px;\">New resource</a>\n",
			    "input"));
	    writer.write(String
		    .format("&nbsp;<a href=\"javascript:toggleDiv('%s');\" style=\"background-color: #fff; padding: 5px 10px;\">SPARQL</a>\n",
			    "sparql"));

	    writer.write("<div class=\"ui-widget\" style='display: none;' id='input'><p>\n");
	    writer.write("<FORM action='' method='post'>"
		    + "<FIELDSET><LEGEND>Import RDF data into Ontology service</LEGEND>"
		    + "<label for=\"uri\">URL</label>" + "<input name=\"uri\" size=\"120\" tabindex=\"4\">"
		    + "</FIELDSET>" + "<INPUT name=\"run\" type=\"submit\" value=\"SUBMIT\" tabindex=\"7\">"
		    + "</FORM>");
	    writer.write("</p></div>\n");

	    writer.write("<div class=\"ui-widget\" style='display: none;' id='sparql'><p>\n");

	    writer.write(String.format("<FORM action='' method='get'>" + "<FIELDSET><LEGEND>SPARQL</LEGEND>"
		    + "<TEXTAREA name=\"query\" rows=\"10\" cols=\"120\" tabindex=\"1\">", request.getRootRef()));
	    writer.flush();
	    writer.write(query);
	    writer.write("</TEXTAREA>" + "</FIELDSET><INPUT name=\"run\" type=\"submit\" tabindex=\"2\">");
	    // writer.write(jsTableSorter("results","pager"));

	    writer.write("</FORM>");
	    writer.write("</p></div>\n");

	    writer.write(htmlBeauty.printWidgetHeader("Results"));
	    writer.write(htmlBeauty.printWidgetContentHeader(""));
	    writer.write("<p>");
	    writer.write("<table class='tablesorter'>");

	} catch (Exception x) {
	    x.printStackTrace();
	}
    }

    @Override
    public void processResults(Query query, ResultSet results, Writer output) throws Exception {
	header(output, query.toString());
	try {
	    super.processResults(query, results, output);
	} catch (Exception x) {
	    x.printStackTrace();
	}
	footer(output, "");
    }

    @Override
    public void processItem(QuerySolution solution, List<String> vars, Writer writer) throws Exception {
	writer.write("<tr>");
	for (int i = 0; i < vars.size(); i++) {
	    RDFNode node = solution.get(vars.get(i));
	    writer.write("<td>");
	    if (node != null) {
		String value = node.isLiteral() ? ((Literal) node).getString() : PrintUtil.print(node);
		if ((value.indexOf("<") >= 0) && (value.indexOf(">") >= 0))// some
									   // xml
									   // inside
		    writer.write(String.format("<textarea readonly width='100%%'>%s</textarea>", value));
		else if (value.startsWith("http"))
		    writer.write(String.format("%s&nbsp;<a href='?uri=%s'>?</a>", value, Reference.encode(value)));
		else if ("doi".equals(vars.get(i)))
		    writer.write(String.format("&nbsp;<a href='http://dx.doi.org/%s'>%s</a>", value, value));
		else
		    writer.write(value);
	    }
	    writer.write("</td>");
	}
	writer.write("</tr>");
	writer.flush();
    }

    @Override
    public void processVars(List<String> vars, Writer writer) throws Exception {
	writer.write("<thead><tr>");
	for (int i = 0; i < vars.size(); i++) {
	    writer.write("<th>");
	    writer.write(vars.get(i));
	    writer.write("</th>");
	}
	writer.write("</tr></thead>");
	writer.write("<tbody>");
    }

    @Override
    public void footer(Writer writer, String query) {
	try {
	    writer.write("</tbody>");
	    writer.write("</table>");
	    writer.write("</p>");
	    writer.write(htmlBeauty.printWidgetContentFooter());
	    writer.write(htmlBeauty.printWidgetFooter());
	    htmlBeauty.writeHTMLFooter(writer, "", request);
	    output.flush();
	} catch (Exception x) {
	    x.printStackTrace();
	}
    }
}
