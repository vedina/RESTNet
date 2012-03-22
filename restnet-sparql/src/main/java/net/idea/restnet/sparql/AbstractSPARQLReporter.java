package net.idea.restnet.sparql;

import java.util.List;

import net.idea.modbcum.r.AbstractReporter;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.Lock;

public class AbstractSPARQLReporter<OUTPUT> extends AbstractReporter<String, OUTPUT> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7565253534132056732L;

	protected MediaType mediaType;
	protected Model model;
	protected Request request;
	
	public MediaType getMediaType() {
		return mediaType;
	}

	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}

	public AbstractSPARQLReporter(Model model,MediaType mediaType, Request request) {
		super();
		this.model = model;
		this.mediaType = mediaType;
		this.request = request;
	}



	@Override
	public String getLicenseURI() {
		return null;
	}


	@Override
	public void setLicenseURI(String uri) {
	}


	@Override
	public OUTPUT process(String queryString) throws Exception {
		QueryExecution qe = null;
		ResultSet results = null;
		try {

			model.enterCriticalSection(Lock.READ) ;
		
			try {
				Query query = QueryFactory.create(queryString,null,Syntax.syntaxARQ);
	
				// Execute the query and obtain results
				qe = QueryExecutionFactory.create(query,model );
				results = qe.execSelect();

				processResults(query, results,output);
			} catch (Exception x) {
	
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x);
			} finally {
				try {qe.close();} catch (Exception x) {}
			}
		} finally {
			if (model!=null) model.leaveCriticalSection() ; 
		}
		return getOutput();
	}	
	
	public void header(OUTPUT output, String query) {
		
	}
	public void footer(OUTPUT output, String query) {
		
	}
	
	public void processResults(Query query, ResultSet results, OUTPUT output) throws Exception {
		List<String> vars = results.getResultVars();
		while (results.hasNext()) {
			processItem(results.next(),vars,output);
		}
	}
	public void processVars(List<String> vars , OUTPUT output) throws Exception {
		
	}
	
	public void processItem(QuerySolution solution, List<String> vars , OUTPUT output) throws Exception {
		
	}
}
