package net.idea.restnet.rdf.ns;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import net.idea.restnet.c.task.ClientResourceWrapper;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.RDFDefaultErrorHandler;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;

public class OT {
    public enum OTClass {
	Compound, Conformer, Dataset, DataEntry, Feature, ModelConfidenceFeature {
	    @Override
	    public OntClass createOntClass(OntModel model) {
		OntClass feature = Feature.getOntClass(model);
		OntClass stringFeature = model.createClass(getNS());
		feature.addSubClass(stringFeature);
		return stringFeature;
	    }

	    @Override
	    public void assignType(OntModel model, Individual individual) {
		super.assignType(model, individual);
		individual.addOntClass(Feature.getOntClass(model));
	    }
	},
	ModelPredictionFeature {
	    @Override
	    public OntClass createOntClass(OntModel model) {
		OntClass feature = Feature.getOntClass(model);
		OntClass stringFeature = model.createClass(getNS());
		feature.addSubClass(stringFeature);
		return stringFeature;
	    }

	    @Override
	    public void assignType(OntModel model, Individual individual) {
		super.assignType(model, individual);
		individual.addOntClass(Feature.getOntClass(model));
	    }
	},
	NumericFeature {
	    @Override
	    public OntClass createOntClass(OntModel model) {
		OntClass feature = Feature.getOntClass(model);
		OntClass stringFeature = model.createClass(getNS());
		feature.addSubClass(stringFeature);
		return stringFeature;
	    }
	},
	NominalFeature {
	    @Override
	    public OntClass createOntClass(OntModel model) {
		OntClass feature = Feature.getOntClass(model);
		OntClass stringFeature = model.createClass(getNS());
		feature.addSubClass(stringFeature);
		return stringFeature;
	    }
	},
	StringFeature {
	    @Override
	    public OntClass createOntClass(OntModel model) {
		OntClass feature = Feature.getOntClass(model);
		OntClass stringFeature = model.createClass(getNS());
		feature.addSubClass(stringFeature);
		return stringFeature;
	    }

	},
	TupleFeature {
	    @Override
	    public OntClass createOntClass(OntModel model) {
		OntClass feature = Feature.getOntClass(model);
		OntClass tupleFeature = model.createClass(getNS());
		feature.addSubClass(tupleFeature);
		return tupleFeature;
	    }

	},
	FeatureValue, Algorithm, Model, Parameter, Validation, ValidationInfo, Task, ErrorReport;
	public String getNS() {
	    return String.format(_NS, toString());
	}

	public OntClass getOntClass(OntModel model) {
	    OntClass c = model.getOntClass(getNS());
	    return (c == null) ? createOntClass(model) : c;
	}

	public OntClass createOntClass(OntModel model) {
	    return model.createClass(getNS());
	}

	public void assignType(OntModel model, Individual individual) {
	    individual.addOntClass(getOntClass(model));
	}

    };

    /**
     * <p>
     * The RDF model that holds the vocabulary terms
     * </p>
     */
    private static Model m_model = ModelFactory.createDefaultModel();
    /**
     * <p>
     * The namespace of the vocabalary as a string ({@value})
     * </p>
     */
    protected static final String _NS = "http://www.opentox.org/api/1.1#%s";
    public static final String NS = String.format(_NS, "");

    public static String getURI() {
	return NS;
    }

    /**
     * <p>
     * The namespace of the vocabalary as a resource
     * </p>
     */
    public static final Resource NAMESPACE = m_model.createResource(NS);

    /**
     * Object properties
     */
    public enum OTProperty {
	dataEntry, compound, feature, values, hasSource, conformer, model, parameters, report, algorithm, dependentVariables, independentVariables, predictedVariables, trainingDataset, validationReport, validation, hasValidationInfo, validationModel, validationPredictionDataset, validationTestDataset,
	// Nominal features
	acceptValue, error, smarts, confidenceOf;

	public Property createProperty(OntModel jenaModel) {
	    Property p = jenaModel.getObjectProperty(String.format(_NS, toString()));
	    return p != null ? p : jenaModel.createObjectProperty(String.format(_NS, toString()));
	}

	public String getURI() {
	    return String.format(_NS, toString());
	}
    }

    /**
     * Data properties
     */
    public enum DataProperty {
	value, units, has3Dstructure, hasStatus, percentageCompleted, resultURI, paramScope, paramValue, errorCode, actor, message, errorDetails, errorCause;
	public Property createProperty(OntModel jenaModel) {
	    Property p = jenaModel.getDatatypeProperty(String.format(_NS, toString()));
	    return p != null ? p : jenaModel.createDatatypeProperty(String.format(_NS, toString()));
	}

	public String getURI() {
	    return String.format(_NS, toString());
	}
    };

    /*
     * public static final Property value =
     * m_model.createProperty(String.format(_NS, "value")); public static final
     * Property units = m_model.createProperty(String.format(_NS, "units"));
     * public static final Property has3Dstructure =
     * m_model.createProperty(String.format(_NS, "has3Dstructure")); public
     * static final Property hasStatus =
     * m_model.createProperty(String.format(_NS, "hasStatus")); public static
     * final Property percentageCompleted =
     * m_model.createProperty(String.format(_NS, "percentageCompleted")); public
     * static final Property paramScope =
     * m_model.createProperty(String.format(_NS, "paramScope")); public static
     * final Property paramValue = m_model.createProperty(String.format(_NS,
     * "paramValue"));
     */
    public static OntModel createModel() throws Exception {
	return createModel(OntModelSpec.OWL_DL_MEM);
    }

    public static OntModel createModel(OntModelSpec spec) throws ResourceException {
	OntModel jenaModel = ModelFactory.createOntologyModel(spec, null);

	jenaModel.setNsPrefix("ot", OT.NS);
	jenaModel.setNsPrefix("ota", OTA.NS);
	jenaModel.setNsPrefix("otee", OTEE.NS);
	jenaModel.setNsPrefix("owl", OWL.NS);
	jenaModel.setNsPrefix("dc", DC.NS);
	jenaModel.setNsPrefix("dcterms", DCTerms.NS);
	jenaModel.setNsPrefix("bx", BibTex.NS);
	jenaModel.setNsPrefix("xsd", XSDDatatype.XSD + "#");
	return jenaModel;
    }

    public static Model createModel(Model base, InputStream in, MediaType mediaType) throws ResourceException {

	if (MediaType.APPLICATION_RDF_XML.equals(mediaType)) {
	    Model model = base == null ? createModel(OntModelSpec.OWL_DL_MEM) : base;
	    RDFReader reader = model.getReader();
	    /**
	     * When reading RDF/XML the check for reuse of rdf:ID has a memory
	     * overhead, which can be significant for very large files. In this
	     * case, this check can be suppressed by telling ARP to ignore this
	     * error.
	     */
	    reader.setProperty("WARN_REDEFINITION_OF_ID", "EM_IGNORE");

	    model.enterCriticalSection(Lock.WRITE);
	    try {
		reader.setProperty("error-mode", "lax");
		reader.setProperty("WARN_REDEFINITION_OF_ID", "EM_IGNORE");

		reader.setErrorHandler(new RDFDefaultErrorHandler() {
		    @Override
		    public void warning(Exception e) {
			super.warning(e);

		    }
		});
		reader.read(model, in, getJenaFormat(mediaType));
		return model;
	    } catch (Exception x) {
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x.getMessage(), x);
	    } finally {
		model.leaveCriticalSection();

	    }
	} else {
	    Model model = base == null ? createModel(OntModelSpec.OWL_DL_MEM) : base;
	    model.read(in, "", getJenaFormat(mediaType));
	    return model;
	}

    }

    public static Model createModel(Model model, Representation entity, MediaType mediaType) throws ResourceException {
	try {
	    return createModel(model, entity.getStream(), entity.getMediaType() == null ? entity.getMediaType()
		    : entity.getMediaType());
	} catch (IOException x) {
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x.getMessage(), x);
	}
    }

    public static Model createModel(Model model, Reference uri, MediaType mediaType) throws ResourceException,
	    IOException, MalformedURLException {
	// Client httpclient = new Client(Protocol.HTTP);
	// httpclient.setConnectTimeout(300000);
	HttpURLConnection uc = ClientResourceWrapper.getHttpURLConnection(uri.toString(), "GET", mediaType.getName());

	InputStream in = null;

	try {
	    int code = uc.getResponseCode();
	    switch (code) {
	    case HttpURLConnection.HTTP_OK: {
		in = uc.getInputStream();
		if (in == null)
		    throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		return createModel(model, in, mediaType);
	    }
	    case HttpURLConnection.HTTP_NOT_FOUND: {
		throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
	    }
	    default: {
		throw new ResourceException(new Status(code));
	    }
	    }
	} catch (ResourceException x) {
	    throw x;
	} catch (Exception x) {
	    throw new ResourceException(Status.SERVER_ERROR_INTERNAL, x.getMessage(), x);
	} finally {
	    try {
		if (in != null)
		    in.close();
		if (uc != null)
		    uc.disconnect();

	    } catch (Exception x) {
	    }
	}

    }

    public static String getJenaFormat(MediaType mediaType) throws ResourceException {
	if (mediaType.equals(MediaType.APPLICATION_RDF_XML))
	    return "RDF/XML";
	// return "RDF/XML-ABBREV";
	else if (mediaType.equals(MediaType.APPLICATION_RDF_TURTLE))
	    return "TURTLE";
	else if (mediaType.equals(MediaType.TEXT_RDF_N3))
	    return "N3";
	else if (mediaType.equals(MediaType.TEXT_RDF_NTRIPLES))
	    return "N-TRIPLE";
	else
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, String.format("Unsupported format %s",
		    mediaType));
    }

    public static void write(OntModel jenaModel, OutputStream output, MediaType mediaType, boolean isXml_abbreviation)
	    throws IOException {
	try {
	    RDFWriter fasterWriter = null;
	    if (mediaType.equals(MediaType.APPLICATION_RDF_XML)) {
		if (isXml_abbreviation)
		    fasterWriter = jenaModel.getWriter("RDF/XML-ABBREV");// lot
									 // smaller
									 // ...
									 // but
									 // could
									 // be
									 // slower
		else
		    fasterWriter = jenaModel.getWriter("RDF/XML");
		fasterWriter.setProperty("xmlbase", jenaModel.getNsPrefixURI(""));
		fasterWriter.setProperty("showXmlDeclaration", Boolean.TRUE);
		fasterWriter.setProperty("showDoctypeDeclaration", Boolean.TRUE);
	    } else if (mediaType.equals(MediaType.APPLICATION_RDF_TURTLE))
		fasterWriter = jenaModel.getWriter("TURTLE");
	    else if (mediaType.equals(MediaType.TEXT_RDF_N3))
		fasterWriter = jenaModel.getWriter("N3");
	    else if (mediaType.equals(MediaType.TEXT_RDF_NTRIPLES))
		fasterWriter = jenaModel.getWriter("N-TRIPLE");
	    else {
		fasterWriter = jenaModel.getWriter("RDF/XML-ABBREV");
		fasterWriter.setProperty("showXmlDeclaration", Boolean.TRUE);
		fasterWriter.setProperty("showDoctypeDeclaration", Boolean.TRUE); // essential
										  // to
										  // get
										  // XSD
										  // prefixed
		fasterWriter.setProperty("xmlbase", jenaModel.getNsPrefixURI(""));
	    }

	    fasterWriter.write(jenaModel, output, "http://opentox.org/api/1.1");

	} catch (Exception x) {
	    Throwable ex = x;
	    while (ex != null) {
		if (ex instanceof IOException)
		    throw (IOException) ex;
		ex = ex.getCause();
	    }
	    Context.getCurrentLogger().warning(x.getMessage() == null ? x.toString() : x.getMessage());
	} finally {

	    try {
		if (output != null)
		    output.flush();
	    } catch (Exception x) {
		x.printStackTrace();
	    }
	}
    }
}
