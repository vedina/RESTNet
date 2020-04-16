package net.idea.restnet.db.facet;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.facet.IFacet;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;
import org.restlet.data.Reference;

/**
 * Generates URI for {@link IFacet}
 * 
 * @author nina
 * 
 * @param <Q>
 */
public class FacetURIReporter<Q extends IQueryRetrieval<IFacet>> extends QueryURIReporter<IFacet, Q> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 8868430033131766579L;

    public FacetURIReporter() {
	this(null);
    }

    public FacetURIReporter(Request baseRef) {
	super(baseRef, null);
    }

    @Override
    public String getURI(String ref, IFacet item) {
	Reference root = getBaseReference();
	
	return item.getResultsURL(root.toString());
    }

}