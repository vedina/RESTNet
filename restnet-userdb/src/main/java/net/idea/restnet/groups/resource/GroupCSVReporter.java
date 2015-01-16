package net.idea.restnet.groups.resource;

import java.io.IOException;
import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.r.QueryReporter;
import net.toxbank.client.resource.Group;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Reference;

public class GroupCSVReporter<Q extends IQueryRetrieval<Group>> extends QueryReporter<Group, Q, Writer> {
    /**
	 * 
	 */
    private static final long serialVersionUID = -4566136103208284105L;

    protected Request request;

    public Request getRequest() {
	return request;
    }

    public void setRequest(Request request) {
	this.request = request;
    }

    protected Reference baseReference;

    public Reference getBaseReference() {
	return baseReference;
    }

    protected GroupCSVReporter(Reference baseRef) {
	this.baseReference = baseRef;
    }

    public GroupCSVReporter(Request request) {
	this(request == null ? null : request.getRootRef());
	setRequest(request);
    }

    protected GroupCSVReporter() {
    }

    @Override
    public Object processItem(Group item) throws Exception {
	try {
	    output.write(String.format("%s\r\n", item.getTitle()));
	    output.flush();
	} catch (IOException x) {
	    Context.getCurrentLogger().severe(x.getMessage());
	}
	return null;
    }

    public void footer(Writer output, Q query) {
    };

    public void header(Writer output, Q query) {
	try {
	    output.write("Title\r\n");
	} catch (Exception x) {

	}
    };

    public void open() throws DbAmbitException {

    }

    @Override
    public void close() throws Exception {
	setRequest(null);
	super.close();
    }

    @Override
    public String getFileExtension() {
	return "csv";
    }
}