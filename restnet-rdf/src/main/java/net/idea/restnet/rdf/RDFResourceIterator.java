package net.idea.restnet.rdf;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.restlet.routing.Template;

public class RDFResourceIterator extends RDFObjectIterator<RDFNode> {

    public RDFResourceIterator(OntModel model, String topObject) {
	super(model, topObject);
    }

    @Override
    protected Resource createRecord() {
	return null;
    }

    @Override
    protected Template createTemplate() {
	return null;
    }

    @Override
    protected void parseObjectURI(RDFNode uri, RDFNode record) {

    }

    @Override
    protected RDFNode parseRecord(RDFNode newEntry, RDFNode record) {
	return newEntry;
    }
}
