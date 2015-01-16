package net.idea.restnet.user.resource;

import java.io.IOException;
import java.io.Writer;
import java.util.UUID;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.user.DBUser;
import net.toxbank.client.resource.Organisation;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Reference;

/**
 * <authors_catalog>
 * 
 * @author nina
 * 
 * @param <Q>
 */
public class UserXMLReporter<Q extends IQueryRetrieval<DBUser>> extends QueryReporter<DBUser, Q, Writer> {
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

    protected UserXMLReporter(Reference baseRef) {
	this.baseReference = baseRef;
    }

    public UserXMLReporter(Request request) {
	this(request == null ? null : request.getRootRef());
	setRequest(request);
    }

    protected UserXMLReporter() {
    }

    @Override
    public Object processItem(DBUser item) throws Exception {
	try {
	    StringBuilder name = new StringBuilder();
	    name.append(item.getTitle() == null ? "" : item.getTitle());
	    name.append(" ");
	    name.append(item.getFirstname() == null ? "" : item.getFirstname());
	    name.append(" ");
	    name.append(item.getLastname() == null ? "" : item.getLastname());
	    String affiliation = "";
	    if (item.getOrganisations() != null)
		for (Organisation org : item.getOrganisations())
		    affiliation = org.getTitle();

	    output.write(String
		    .format("\t<author id='id%s' name='%s' affiliation='%s' contact='%s' url='%s' email='%s' number='%d'/>\r\n",
			    item.getID() > 0 ? item.getID() : UUID.randomUUID().toString(), name.toString().trim(),
			    affiliation, "", item.getHomepage() == null ? "" : item.getHomepage().toExternalForm(),
			    item.getEmail() == null ? "" : item.getEmail(), 1));
	    output.flush();
	} catch (IOException x) {
	    Context.getCurrentLogger().severe(x.getMessage());
	}
	return null;
    }

    public void footer(Writer output, Q query) {
	try {
	    output.write("\r\n</authors_catalog>\r\n");
	    output.write("\r\n</Catalogs>");
	} catch (Exception x) {

	}
    };

    public void header(Writer output, Q query) {
	try {
	    output.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
	    output.write("<Catalogs>");
	    output.write("<authors_catalog>\r\n");
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
	return "xml";
    }
}