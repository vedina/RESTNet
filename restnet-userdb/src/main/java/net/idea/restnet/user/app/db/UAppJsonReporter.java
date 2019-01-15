package net.idea.restnet.user.app.db;

import java.io.IOException;
import java.io.Writer;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Reference;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.json.JSONUtils;
import net.idea.modbcum.r.QueryReporter;

public class UAppJsonReporter<Q extends IQueryRetrieval<DBUApp>> extends QueryReporter<DBUApp, Q, Writer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4631581593581034451L;
	protected Request request;
	protected String comma = null;

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	protected Reference baseReference;

	public UAppJsonReporter(Request request) {
		this.baseReference = (request == null ? null : request.getRootRef());
		setRequest(request);
	}

	public Reference getBaseReference() {
		return baseReference;
	}

	@Override
	public void footer(Writer output, Q query) {
		try {
			output.write("\n]\n}");
		} catch (Exception x) {
		}
	};

	@Override
	public void header(Writer output, Q query) {
		try {
			output.write("{\"apps\": [");
		} catch (Exception x) {
		}

	};

	final String format = "{\"token\" : %s, \"name\":%s , \"referer\" : %s,  \"expire\" : %s , \"enabled\" : %s }";

	@Override
	public Object processItem(DBUApp item) throws Exception {

		try {
			if (comma != null)
				getOutput().write(comma);
			getOutput().write(String.format(format,
					JSONUtils.jsonQuote(JSONUtils.jsonEscape(item.getKey().getToken())),
					JSONUtils.jsonQuote(JSONUtils.jsonEscape(item.getName())),
					item.getReferer()==null?"\"\"":JSONUtils.jsonQuote(JSONUtils.jsonEscape(item.getReferer())),
					item.getExpire(),item.isEnabled())); 
			comma = ",";
		} catch (IOException x) {
			Context.getCurrentLogger().severe(x.getMessage());
		}
		return null;
	}

}
