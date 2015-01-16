package net.idea.restnet.user.resource;

import java.io.IOException;
import java.io.Writer;

import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import net.idea.modbcum.p.MasterDetailsProcessor;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.groups.DBOrganisation;
import net.idea.restnet.groups.db.ReadOrganisation;
import net.idea.restnet.user.DBUser;
import net.toxbank.client.resource.Organisation;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Reference;

public class UserCSVReporter<Q extends IQueryRetrieval<DBUser>> extends QueryReporter<DBUser, Q, Writer> {
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
	this.baseReference = request == null ? null : request.getRootRef();
    }

    protected Reference baseReference;

    public Reference getBaseReference() {
	return baseReference;
    }

    public UserCSVReporter(Request request) {
	setRequest(request == null ? null : request);
	getProcessors().clear();
	IQueryRetrieval<DBOrganisation> queryO = new ReadOrganisation(new DBOrganisation());

	MasterDetailsProcessor<DBUser, DBOrganisation, IQueryCondition> orgReader = new MasterDetailsProcessor<DBUser, DBOrganisation, IQueryCondition>(
		queryO) {
	    @Override
	    protected DBUser processDetail(DBUser target, DBOrganisation detail) throws Exception {
		if (target.getID() > 0) {
		    target.addOrganisation(detail);
		}
		return target;
	    }
	};

	getProcessors().add(orgReader);
	processors.add(new DefaultAmbitProcessor<DBUser, DBUser>() {
	    public DBUser process(DBUser target) throws Exception {
		processItem(target);
		return target;
	    };
	});
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
	    output.write(String.format("\"%s\",\"%s\",%s,\"%s\",\"%s\",%s\n", name.toString().trim(), affiliation,
		    item.getUserName() == null ? "" : item.getUserName(),
		    item.getEmail() == null ? "" : item.getEmail(), item.getKeywords(), item.isReviewer() ? "Yes" : ""));
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
	    output.write("Name,Affiliation,user name,email,Keywords,Reviewer\n");
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