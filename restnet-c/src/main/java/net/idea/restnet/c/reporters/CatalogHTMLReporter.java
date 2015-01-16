package net.idea.restnet.c.reporters;

import java.io.Writer;
import java.util.Iterator;

import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.html.HTMLBeauty;

import org.restlet.Request;
import org.restlet.data.Reference;

/**
 * HTML reporter for {@link AlgorithmResource}
 * 
 * @author nina
 * 
 * @param <T>
 */
public class CatalogHTMLReporter<T> extends CatalogURIReporter<T> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 7644836050657868159L;
    protected HTMLBeauty htmlBeauty;
    protected boolean headless = false;
    protected String title;
    protected long record = 0;
    protected boolean singleItem = false;

    public boolean isSingleItem() {
	return singleItem;
    }

    public void setSingleItem(boolean singleItem) {
	this.singleItem = singleItem;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public boolean isHeadless() {
	return headless;
    }

    public void setHeadless(boolean headless) {
	this.headless = headless;
    }

    public HTMLBeauty getHtmlBeauty() {
	return htmlBeauty;
    }

    public void setHtmlBeauty(HTMLBeauty htmlBeauty) {
	this.htmlBeauty = htmlBeauty;
    }

    public CatalogHTMLReporter(Request request, ResourceDoc doc, boolean headless) {
	this(request, doc, null, false);
    }

    public CatalogHTMLReporter(Request request, ResourceDoc doc, HTMLBeauty htmlbeauty, boolean headless) {
	super(request, doc);
	this.htmlBeauty = htmlbeauty;
	this.headless = headless;
    }

    @Override
    public void header(Writer w, Iterator<T> query) {
	record = 0;
	if (headless)
	    return;
	try {
	    super.header(w, query);
	    if (htmlBeauty == null)
		htmlBeauty = new HTMLBeauty();

	    htmlBeauty.writeHTMLHeader(output, htmlBeauty.getTitle(), getRequest(), getDocumentation());// ,"<meta http-equiv=\"refresh\" content=\"10\">");
	} catch (Exception x) {

	}

	Reference uri = getRequest().getResourceRef().clone();
	uri.setQuery(null);

	try {
	    if (printAsTable()) {
		if (getTitle() != null)
		    w.write(String.format(
			    "<div class='ui-widget' style='margin-top:18px'><p><strong>%ss</strong></p></div>",
			    getTitle()));
		w.write(printPageNavigator());

	    } else {
		if (getTitle() != null)
		    w.write(String.format(
			    "<div class='ui-widget' style='margin-top:18px'><p><strong>%ss</strong></p></div>",
			    getTitle()));
		w.write(printPageNavigator());
	    }
	} catch (Exception x) {
	    x.printStackTrace();
	} finally {
	    try {
		w.write("<div class='.ui-widget'>\n");
	    } catch (Exception x) {
	    }
	}

    }

    protected boolean printAsTable() {
	return false;
    }

    protected String printPageNavigator() {
	if (singleItem || headless)
	    return "";
	int page = htmlBeauty.getPage();
	long pageSize = htmlBeauty.getPageSize();
	return htmlBeauty.getPaging(page, page - 5, page, pageSize);
    }

    public void processItem(T item, Writer output) {
	try {
	    output.write((htmlBeauty).printWidget(renderItemTitle(item), renderItem(item)));
	    record++;
	} catch (Exception x) {
	    x.printStackTrace();
	}
    }

    public String renderItemTitle(T item) {
	return String.format("%d.", record + 1);
    }

    public String renderItem(T item) {
	String uri = super.getURI(item).trim();
	return (String.format("<a href='%s'>%s</a>", uri, item));
    }

    @Override
    public void footer(Writer output, Iterator<T> query) {
	try {
	    output.write("</div");
	} catch (Exception x) {
	}
	if (headless)
	    return;
	try {
	    if (htmlBeauty == null)
		htmlBeauty = new HTMLBeauty();
	    htmlBeauty.writeHTMLFooter(output, "", getRequest());
	    output.flush();
	} catch (Exception x) {

	}
    }
}
