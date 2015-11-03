package net.idea.restnet.sparql;

import java.io.OutputStream;

import org.restlet.Request;
import org.restlet.data.MediaType;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;



public class StreamSPARQLReporter extends AbstractSPARQLReporter<OutputStream> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 5609996394531534127L;

    public StreamSPARQLReporter(Model model, MediaType mediaType, Request request) {
	super(model, mediaType, request);
    }

    @Override
    public void processResults(Query query, ResultSet results, OutputStream output) throws Exception {

    	/*
	if (getMediaType().equals(MediaType.APPLICATION_RDF_XML))
	    ResultSetFormatter.outputAsRDF(output, "RDF/XML", results);
	else if (getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE))
	    ResultSetFormatter.outputAsRDF(output, "TURTLE", results);
	else if (getMediaType().equals(MediaType.TEXT_RDF_N3))
	    ResultSetFormatter.outputAsRDF(output, "N3", results);
	else if (getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES))
	    ResultSetFormatter.outputAsRDF(output, "N-TRIPLE", results);
	
	else 
	*/
	if (getMediaType().equals(MediaType.APPLICATION_SPARQL_RESULTS_XML))
	    ResultSetFormatter.outputAsXML(output, results);
	else if (getMediaType().equals(MediaType.TEXT_CSV))
	    ResultSetFormatter.outputAsCSV(output, results);
	else if (getMediaType().equals(MediaType.APPLICATION_SPARQL_RESULTS_JSON))
	    ResultSetFormatter.outputAsJSON(output, results);
	else if (getMediaType().equals(MediaType.TEXT_PLAIN))
	    ResultSetFormatter.out(output, results, query);
	else
	    ResultSetFormatter.outputAsXML(output, results);
    }
}
