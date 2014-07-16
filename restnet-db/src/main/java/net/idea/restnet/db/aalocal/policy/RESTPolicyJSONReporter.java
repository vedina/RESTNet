package net.idea.restnet.db.aalocal.policy;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.restnet.db.reporter.AbstractJSONReporter;
import net.idea.restnet.i.aa.IRESTPolicy;

/**
 *  /admin/restpolicy resource
 * @author nina
 *
 * @param <Q>
<pre>
{"policy": [{
	"uri": "http://localhost:8080/ambit2/admin/restpolicy/1",
	"resource": "/model",
	"role": "ambit_admin",
	"methods": {"get":true,"post":true,"put":true,"delete":true}	
	}
]
}
</pre>
 */
public class RESTPolicyJSONReporter<Q extends IQueryRetrieval<IRESTPolicy>> extends AbstractJSONReporter<IRESTPolicy, Q> {
	
	public RESTPolicyJSONReporter(String baseRef) {
		this(baseRef,null);
	}
	public RESTPolicyJSONReporter(String baseRef, String jsonp) {
		super("policy",baseRef,jsonp);
	}
	@Override
	public void open() throws DbAmbitException {
		
	}
	@Override
	public String item2json(IRESTPolicy item) {
		return item.toJSON(baseRef);
	}
}