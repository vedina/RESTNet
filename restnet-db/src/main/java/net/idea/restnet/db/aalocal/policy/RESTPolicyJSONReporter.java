package net.idea.restnet.db.aalocal.policy;

import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.i.facet.IFacet;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.i.aa.IRESTPolicy;

import org.restlet.data.Reference;

public class RESTPolicyJSONReporter<Q extends IQueryRetrieval<IRESTPolicy>> extends QueryReporter<IRESTPolicy, Q, Writer> {
	protected QueryURIReporter<IFacet, IQueryRetrieval<IFacet>> uriReporter;
	protected String jsonp = null;
	protected String comma = null;
	protected String baseRef;
	
	public RESTPolicyJSONReporter(String baseRef) {
		this(baseRef,null);
	}
	public RESTPolicyJSONReporter(String baseRef, String jsonp) {
		super();
		this.jsonp = jsonp;
		this.baseRef = baseRef;
	}
	@Override
	public void open() throws DbAmbitException {
		
	}

	@Override
	public void header(Writer output, Q query) {
		try {
			if (jsonp!=null) {
				output.write(jsonp);
				output.write("(");
			}
			output.write("{\"policy\": [");

		} catch (Exception x) {}
		
	}

	@Override
	public void footer(Writer output, Q query) {
		try {
			output.write("\n]\n}");
			
			if (jsonp!=null) {
				output.write(");");
			}
			output.flush();
			} catch (Exception x) {}
	}

	@Override
	public Object processItem(IRESTPolicy item) throws Exception {
		try {
			if (comma!=null) getOutput().write(comma);
			output.write(item.toJSON(baseRef));
			comma = ",";
		} catch (Exception x) {
			x.printStackTrace();
		}
		return item;
	}

}