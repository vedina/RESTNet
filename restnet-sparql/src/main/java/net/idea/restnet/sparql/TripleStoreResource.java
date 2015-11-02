package net.idea.restnet.sparql;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.c.AbstractResource;
import net.idea.restnet.c.StringConvertor;
import net.idea.restnet.c.TaskApplication;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFErrorHandler;
import org.apache.jena.rdf.model.RDFReader;
import org.apache.jena.shared.Lock;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.vocabulary.DC;
import org.apache.jena.vocabulary.OWL;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;


/**
 * 
 * @author nina
 * 
 * @param <T>
 * @param <P>
 */
public class TripleStoreResource extends AbstractResource {
    public static final String resource = "/ontology";
    public static final String resourceKey = "key";
    protected Model model;

    @Override
    protected void doInit() throws ResourceException {
	super.doInit();
	customizeVariants(new MediaType[] { MediaType.TEXT_HTML, MediaType.APPLICATION_SPARQL_RESULTS_XML,
		MediaType.APPLICATION_RDF_XML, MediaType.APPLICATION_RDF_TURTLE, MediaType.TEXT_RDF_N3,
		MediaType.TEXT_RDF_NTRIPLES, MediaType.APPLICATION_SPARQL_RESULTS_JSON, MediaType.TEXT_CSV,
		MediaType.TEXT_PLAIN, MediaType.TEXT_URI_LIST });
    }

    @Override
    public StringConvertor createConvertor(Variant variant) throws AmbitException, ResourceException {

	model = createOntologyModel(true);
	if (MediaType.TEXT_HTML.equals(variant.getMediaType())) {
	    return new StringConvertor(new SPARQL2HTMLReporter(model, variant.getMediaType(), getRequest(),
		    getHTMLBeauty()), variant.getMediaType(), null);
	} else {
	    return new StringConvertor(new StreamSPARQLReporter(model, variant.getMediaType(), getRequest()),
		    variant.getMediaType(), null);
	}
    }

    @Override
    public void close() {
	if (model != null)
	    model.close();
	super.close();
    }

    public String getTDBDirectory() {
	String dir = ((TaskApplication) getApplication()).getProperty(SPARQLEndpointConfig.tdb.name());
	if ((dir == null) || "".equals(dir.trim()) || "${tdb.folder}".equals(dir)) {
	    dir = String.format("%s/tdb", System.getProperty("java.io.tmpdir"));
	    return dir;
	} else
	    return dir.trim();
    }

    @Override
    protected String createQuery(Context context, Request request, Response response) throws ResourceException {
	Form form = getRequest().getResourceRef().getQueryAsForm();
	Object query = form.getFirstValue("query");
	String uri = form.getFirstValue("uri");
	return query == null ? uri == null ? "SELECT ?s ?p ?o where { ?s ?p ?o } \n limit 1000" : String.format(
		"SELECT ?s ?p ?o where { <%s> ?p ?o. } \n", uri) : query.toString();
    }

    @Override
    protected Representation processAndGenerateTask(Method method, Representation entity, Variant variant, boolean async)
	    throws ResourceException {

	return null;
    }

    protected Model createOntologyModel(boolean init) throws ResourceException {
	try {
	    File dir = new File(getTDBDirectory());
	    if (!dir.exists()) {
		dir.mkdir();
		try {
		    new File(dir + "/fixed.opt").createNewFile();
		} catch (Exception x) {
		    x.printStackTrace();
		}
	    }
	    Dataset dataset= TDBFactory.createDataset(dir.getAbsolutePath());
	    Model ontology = dataset.getDefaultModel();
	    if (init && (ontology.size() == 0))
		readOntologies(ontology);
	    ontology.setNsPrefix("ot", "http://www.opentox.org/api/1.1#");
	    ontology.setNsPrefix("ota", "http://www.opentox.org/algorithmTypes.owl#");
	    ontology.setNsPrefix("otee", "http://www.opentox.org/echaEndpoints.owl#");
	    ontology.setNsPrefix("owl", OWL.NS);
	    ontology.setNsPrefix("dc", DC.NS);
	    ontology.setNsPrefix("bx", "http://purl.org/net/nknouf/ns/bibtex#");
	    ontology.setNsPrefix("bo", "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#");

	    return ontology;
	} catch (ResourceException x) {
	    throw x;
	} catch (Exception x) {
	    throw new ResourceException(x);
	}
    }

    protected void readOntologies(Model ontology) {
	String[] owls = new String[] { "isatab.owl", };
	for (String owl : owls)
	    try {
		readOWL(getClass().getClassLoader().getResourceAsStream(String.format("ambit2/rest/owl/%s", owl)),
			ontology);
	    } catch (Exception x) {

	    }

    }

    protected void readOWL(InputStream in, Model model) throws Exception {
	try {
	    model.enterCriticalSection(Lock.WRITE);
	    try {
		RDFReader reader = model.getReader();
		reader.setErrorHandler(new RDFErrorHandler() {

		    @Override
		    public void warning(Exception e) {
			e.printStackTrace();

		    }

		    @Override
		    public void fatalError(Exception e) {
			e.printStackTrace();

		    }

		    @Override
		    public void error(Exception e) {
			e.printStackTrace();

		    }
		});
		reader.read(model, in, null);
		try {
		    model.commit();
		} catch (Exception x) {
		}
	    } catch (Exception x) {
		x.printStackTrace();
		Logger.getLogger(getClass().getName()).severe(x.toString());
	    } finally {
		try {
		    if (in != null)
			in.close();
		} catch (Exception x) {
		}
	    }
	} catch (Exception x) {
	    throw x;
	} finally {
	    model.leaveCriticalSection();
	}
    }

}
