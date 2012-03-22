package net.idea.restnet.sparql;

import java.io.File;
import java.io.Serializable;

import net.idea.restnet.c.TaskApplication;

import org.restlet.resource.ResourceException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.OWL;


/**
 * Can be queried by ?subject=""&predicate=""&object=""
 * @author nina
 *
 * @param <T>
 */
public class TDBOntologyResource<T extends Serializable> extends AbstractOntologyResource {


	
	public TDBOntologyResource() {
		super();
	}

	protected Model createOntologyModel(boolean init) throws ResourceException {
		try {
			String directory =  ((TaskApplication)getApplication()).getProperty(SPARQLEndpointConfig.tdb.name());
			File dir  = new File(directory);
			if (!dir.exists()) {
				dir.mkdir();
				try {
					new File(directory+"/fixed.opt").createNewFile();
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
			Model ontology = TDBFactory.createModel(directory) ;
			if (init && (ontology.size()==0)) readOntologies(ontology);
			ontology.setNsPrefix( "ot", "http://www.opentox.org/api/1.1#");
			ontology.setNsPrefix( "ota", "http://www.opentox.org/algorithmTypes.owl#" );
			ontology.setNsPrefix( "otee", "http://www.opentox.org/echaEndpoints.owl#" );
			ontology.setNsPrefix( "owl", OWL.NS );
			ontology.setNsPrefix( "dc", DC.NS );
			ontology.setNsPrefix( "bx", "http://purl.org/net/nknouf/ns/bibtex#" );
			ontology.setNsPrefix( "bo", "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#" );		
			/*
			if (ontology == null) ontology = TDBFactory.createModel(directory) ;
			if (ontology.size()==0) readOntologies();
			*/
			
			return ontology;
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(x);
		}
	}

	/*
	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		Model ontology = createOntologyModel(false);
		try {
		if (ontology!=null) deleteModel(ontology);
		} catch (Exception x) {
			
		} finally {
			ontology.close();

		}
		return get(variant);
	}
	*/

}
