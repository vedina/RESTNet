package net.idea.restnet.db.reporter;

import java.io.Writer;

import org.restlet.Context;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.r.QueryReporter;

public abstract class AbstractJSONReporter<T, Q extends IQueryRetrieval<T>> extends QueryReporter<T, Q, Writer> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 139489842739325654L;
    protected String jsonp = null;
    protected String comma = null;
    protected String baseRef;
    protected String field;

    public AbstractJSONReporter(String field, String baseRef) {
	this(field, baseRef, null);
    }

    public AbstractJSONReporter(String field, String baseRef, String jsonp) {
	super();
	this.field = field;
	this.jsonp = jsonp;
	this.baseRef = baseRef;
    }

    @Override
    public void open() throws DbAmbitException {

    }

    @Override
    public void header(Writer output, Q query) {
	try {
	    if (jsonp != null) {
		output.write(jsonp);
		output.write("(");
	    }
	    output.write("{\"");
	    output.write(field);
	    output.write("\": [");

	} catch (Exception x) {
	}

    }

    @Override
    public void footer(Writer output, Q query) {
	try {
	    output.write("\n]\n}");

	    if (jsonp != null) {
		output.write(");");
	    }
	    output.flush();
	} catch (Exception x) {
	}
    }

    @Override
    public Object processItem(T item) throws Exception {
	try {
	    if (comma != null)
		getOutput().write(comma);
	    output.write(item2json(item));
	    comma = ",";
	} catch (Exception x) {
	    Context.getCurrentLogger().severe(x.getMessage());
	}
	return item;
    }

    public abstract String item2json(T item);

}