package net.idea.restnet.sparql;

import org.restlet.resource.ResourceException;

import com.hp.hpl.jena.rdf.model.Model;

/**

 */
public class SDBOntologyResource extends AbstractOntologyResource {

	@Override
	protected Model createOntologyModel(boolean init) throws ResourceException {
		/*
		File dir  = new File(directory);
		if (!dir.exists()) {
			dir.mkdir();
			try {
				new File(directory+"/fixed.opt").createNewFile();
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
		Model ontology = SDB.
		if (init && (ontology.size()==0)) readOntologies(ontology);
		ontology.setNsPrefix( "ot", "http://www.opentox.org/api/1.1#");
		ontology.setNsPrefix( "ota", "http://www.opentox.org/algorithmTypes.owl#" );
		ontology.setNsPrefix( "otee", "http://www.opentox.org/echaEndpoints.owl#" );
		ontology.setNsPrefix( "owl", OWL.NS );
		ontology.setNsPrefix( "dc", DC.NS );
		ontology.setNsPrefix( "bx", "http://purl.org/net/nknouf/ns/bibtex#" );
		ontology.setNsPrefix( "bo", "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#" );	
		*/	
		/*
		if (ontology == null) ontology = TDBFactory.createModel(directory) ;
		if (ontology.size()==0) readOntologies();
		*/
		//return ontology;
		return null;
	}
}
