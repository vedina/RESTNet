package net.idea.restnet.db.aalocal.user;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.restnet.db.reporter.AbstractJSONReporter;
import net.idea.restnet.i.tools.JSONUtils;

/**
 * /admin/role resource
 * 
 * @author nina
 * 
 * @param <Q>
 */
public class RoleJSONReporter<Q extends IQueryRetrieval<String>> extends AbstractJSONReporter<String, Q> {

    /**
	 * 
	 */
    private static final long serialVersionUID = -84798218370161731L;

    public RoleJSONReporter(String baseRef) {
	this(baseRef, null);
    }

    public RoleJSONReporter(String baseRef, String jsonp) {
	super("roles", baseRef, jsonp);
    }

    @Override
    public void open() throws DbAmbitException {

    }

    @Override
    public String item2json(String item) {
	return JSONUtils.jsonQuote(JSONUtils.jsonEscape(item));
    }
}