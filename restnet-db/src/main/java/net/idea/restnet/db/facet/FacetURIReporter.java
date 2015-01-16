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
	/*
	 * if (item instanceof EndpointCompoundFacet) {
	 * 
	 * EndpointCompoundFacet q = (EndpointCompoundFacet) item;
	 * 
	 * String cmpURI = ""; if ((q.getDataset()!=null) &&
	 * (q.getDataset().getIdchemical()>0 ||
	 * q.getDataset().getIdstructure()>0)) cmpURI =
	 * String.format("&%s=%s",OpenTox
	 * .params.compound_uri,URLEncoder.encode(cmpReporter
	 * .getURI(q.getDataset())));
	 * 
	 * return q.getResultsURL(root.toString(),cmpURI); } else if (item
	 * instanceof DatasetStructureQLabelFacet) { DatasetStructureQLabelFacet
	 * q = (DatasetStructureQLabelFacet) item; return
	 * q.getResultsURL(root.toString()); } else if (item instanceof
	 * DatasetConsensusLabelFacet) { DatasetConsensusLabelFacet q =
	 * (DatasetConsensusLabelFacet) item; return
	 * q.getResultsURL(root.toString()); } else if (item instanceof
	 * DatasetStrucTypeFacet) { DatasetStrucTypeFacet q =
	 * (DatasetStrucTypeFacet) item; return
	 * q.getResultsURL(root.toString());
	 * 
	 * } else if (item instanceof PropertyDatasetFacet) {
	 * PropertyDatasetFacet<Property,SourceDataset> q =
	 * (PropertyDatasetFacet<Property,SourceDataset>) item; return
	 * String.format(
	 * "%s/dataset/%d?feature_uris[]=%s/dataset/%s/feature&feature_uris[]=%s/feature/%s&property=%s/feature/%s&search=%s"
	 * , root,q.getDataset().getId(), root,q.getDataset().getId(),
	 * root,q.getProperty().getId(), root,q.getProperty().getId(),
	 * item.getValue()); } else if (item instanceof BookmarksByTopicFacet) {
	 * BookmarksByTopicFacet q = (BookmarksByTopicFacet) item; return
	 * String.format("%s%s/%s?hasTopic=%s", root, BookmarkResource.resource,
	 * q.getCreator(), Reference.encode(item.getValue().toString())); } else
	 */
	return item.getResultsURL(root.toString());
    }

}