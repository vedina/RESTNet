package net.idea.restnet.db.facet;

import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.i.facet.IFacet;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;

public class FacetCSVReporter<Q extends IQueryRetrieval<IFacet>> extends QueryReporter<IFacet, Q, Writer> {
    protected QueryURIReporter<IFacet, IQueryRetrieval<IFacet>> uriReporter;

    public FacetCSVReporter(Request baseRef) {
	super();
	uriReporter = new FacetURIReporter<IQueryRetrieval<IFacet>>(baseRef);
    }

    @Override
    public void open() throws DbAmbitException {

    }

    @Override
    public void header(Writer output, Q query) {
	try {
	    output.write("Name,Count,URI,Subcategory\n");
	} catch (Exception x) {
	}

    }

    @Override
    public void footer(Writer output, Q query) {
	try {
	    output.flush();
	} catch (Exception x) {
	}
    }

    @Override
    public Object processItem(IFacet item) throws Exception {
	try {
	    String subcategory = null;
	    if ((uriReporter != null) && (uriReporter.getBaseReference() != null))
		subcategory = uriReporter.getBaseReference().toString();
	    output.write(String.format("%s,%s,%s,%s\n", item.getValue(), item.getCount(), uriReporter.getURI(item),
		    item.getSubCategoryURL(subcategory)));
	} catch (Exception x) {
	    x.printStackTrace();
	}
	return item;
    }

}