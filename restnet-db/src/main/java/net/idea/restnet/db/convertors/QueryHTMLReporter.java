package net.idea.restnet.db.convertors;

import java.io.IOException;
import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.html.HTMLBeauty;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;

/**
 * Generates HTML representation of a resource
 * 
 * @author nina
 * 
 * @param <T>
 * @param <Q>
 */
public abstract class QueryHTMLReporter<T, Q extends IQueryRetrieval<T>> extends QueryReporter<T, Q, Writer> {
    protected QueryURIReporter uriReporter;

    protected HTMLBeauty htmlBeauty;

    public HTMLBeauty getHtmlBeauty() {
	return htmlBeauty;
    }

    public void setHtmlBeauty(HTMLBeauty htmlBeauty) {
	this.htmlBeauty = htmlBeauty;
    }

    public QueryURIReporter getUriReporter() {
	return uriReporter;
    }

    public void setUriReporter(QueryURIReporter uriReporter) {
	this.uriReporter = uriReporter;
    }

    protected boolean collapsed = true;
    /**
	 * 
	 */
    private static final long serialVersionUID = 16821411854045588L;

    /**
	 * 
	 */
    public QueryHTMLReporter() {
	this(null, true, null, null);
    }

    public QueryHTMLReporter(Request request, boolean collapsed, ResourceDoc doc) {
	this(request, collapsed, doc, null);
    }

    public QueryHTMLReporter(Request request, boolean collapsed, ResourceDoc doc, HTMLBeauty htmlBeauty) {
	super();
	uriReporter = createURIReporter(request, doc);
	this.collapsed = collapsed;
	processors.clear();
	/*
	 * ValuesReader valuesReader = new ValuesReader(); Profile profile = new
	 * Profile<Property>(); profile.add(Property.getCASInstance());
	 * valuesReader.setProfile(profile); processors.add(valuesReader);
	 */
	processors.add(new DefaultAmbitProcessor<T, T>() {
	    @Override
	    public T process(T target) throws Exception {
		processItem(target);
		return target;
	    };
	});

    }

    protected abstract QueryURIReporter createURIReporter(Request request, ResourceDoc doc);

    protected HTMLBeauty createHTMLBeauty() {
	return new HTMLBeauty();
    }

    @Override
    public void header(Writer w, Q query) {
	try {
	    if (htmlBeauty == null)
		htmlBeauty = createHTMLBeauty();

	    htmlBeauty.writeHTMLHeader(output, htmlBeauty.getTitle(), uriReporter.getRequest(),
		    getUriReporter() == null ? null : getUriReporter().getDocumentation());

	} catch (IOException x) {
	}
    }

    @Override
    public void footer(Writer output, Q query) {
	try {
	    if (htmlBeauty == null)
		htmlBeauty = new HTMLBeauty();
	    htmlBeauty.writeHTMLFooter(output, "", uriReporter.getRequest());

	    output.flush();
	} catch (Exception x) {

	}

    }

    public void open() throws DbAmbitException {

    }

    @Override
    public void close() throws Exception {
	if (uriReporter != null)
	    try {
		uriReporter.close();
		uriReporter = null;
	    } catch (Exception x) {
	    }
	super.close();
    }
}
