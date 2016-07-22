package net.idea.restnet.db;

import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.util.logging.Level;

import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;
import net.idea.modbcum.p.batch.AbstractBatchProcessor;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.c.task.ClientResourceWrapper;
import net.idea.restnet.i.task.TaskResult;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.representation.ObjectRepresentation;

public abstract class CallableQueryProcessor<Target, Result, USERID> extends CallableProtectedTask<USERID> {
    protected AbstractBatchProcessor batch;
    protected Target target;
    protected Reference sourceReference;
    // protected AmbitApplication application;
    protected Context context;
    protected String configFile = "conf/restnet-db.pref";

    public String getConfigFile() {
	return configFile;
    }

    public void setConfigFile(String configFile) {
	this.configFile = configFile;
    }

    public CallableQueryProcessor(Form form, Context context, USERID token) {
	this(null, form, context, token);
    }

    public CallableQueryProcessor(Reference applicationRootReference, Form form, Context context, USERID token) {
	super(token);
	processForm(applicationRootReference, form);
	this.context = context;
    }

    protected void processForm(Reference applicationRootReference, Form form) {
	Object dataset = form.getFirstValue("dataset_uri");
	String[] xvars = form.getValuesArray("feature_uris[]");
	Reference datasetURI = dataset == null ? null : new Reference(dataset.toString());
	if ((dataset != null) && (xvars != null))
	    try {

		for (String xvar : xvars) {
		    String[] xx = xvar.split("\n");
		    for (String x : xx)
			if (!x.trim().equals(""))
			    datasetURI.addQueryParameter("feature_uris[]", x);
		}

	    } catch (Exception x) {

	    }
	this.sourceReference = dataset == null ? null : datasetURI;
    }

    @Override
    public TaskResult doCall() throws Exception {

	Context.getCurrentLogger().info("Start()");
	Connection connection = null;
	try {
	    DBConnection dbc = new DBConnection(context, getConfigFile());
	    connection = dbc.getConnection();
	    try {
		target = createTarget(sourceReference);
	    } catch (Exception x) {
		target = (Target) sourceReference;
	    }

	    batch = createBatch(target);

	    if (batch != null) {
		batch.setCloseConnection(false);
		batch.setProcessorChain(createProcessors());
		try {
		    if ((connection == null) || connection.isClosed())
			throw new Exception("SQL Connection unavailable ");
		    batch.setConnection(connection);
		    batch.open();
		} catch (Exception x) {
		    connection = null;
		}
		/*
		 * batch.addPropertyChangeListener(AbstractBatchProcessor.
		 * PROPERTY_BATCHSTATS,new PropertyChangeListener(){ public void
		 * propertyChange(PropertyChangeEvent evt) {
		 * context.getLogger().info(evt.getNewValue().toString());
		 * 
		 * } });
		 */
		IBatchStatistics stats = runBatch(target);
	    }
	    return createReference(connection);
	} catch (Exception x) {

	    java.io.StringWriter stackTraceWriter = new java.io.StringWriter();
	    x.printStackTrace(new PrintWriter(stackTraceWriter));
	    Context.getCurrentLogger().severe(stackTraceWriter.toString());
	    throw x;
	} finally {
	    Context.getCurrentLogger().info("Done");
	    try {
		connection.close();
	    } catch (Exception x) {
		Context.getCurrentLogger().warning(x.getMessage());
	    }
	}
	/*
	 * try { //connection = application.getConnection((Request)null); //if
	 * (connection.isClosed()) connection =
	 * application.getConnection((Request)null); return
	 * createReference(connection); } catch (Exception x) {
	 * x.printStackTrace(); throw x; } finally { try { connection.close(); }
	 * catch (Exception x) {} }
	 */

    }

    protected IBatchStatistics runBatch(Target target) throws Exception {
	return batch.process(target);
    }

    protected AbstractBatchProcessor createBatch(Target target) throws Exception {
	if (target == null)
	    throw new Exception("Empty input");
	/*
	 * if (target instanceof AbstractStructureQuery) { DbReaderStructure
	 * reader = new DbReaderStructure(); reader.setHandlePrescreen(true);
	 * return reader; } else return new
	 * RDFStructuresReader(target.toString());
	 */
	throw new Exception("Notimplemented");
    }

    protected abstract Target createTarget(Reference reference) throws Exception;

    protected abstract TaskResult createReference(Connection connection) throws Exception;

    protected abstract ProcessorsChain<Result, IBatchStatistics, IProcessor> createProcessors() throws Exception;

    // protected abstract QueryURIReporter createURIReporter(Request request);

    public static Object getQueryObject(Reference reference, Reference applicationRootReference) throws Exception {

	if (!applicationRootReference.isParent(reference)) {
		logger.log(Level.WARNING, String.format("Remote reference %s %s",
				applicationRootReference, reference));
		return null;
	}	

	ObjectRepresentation<Serializable> repObject = null;
	ClientResourceWrapper resource = null;
	try {
	    resource = new ClientResourceWrapper(reference);
	    resource.setMethod(Method.GET);
	    resource.get(MediaType.APPLICATION_JAVA_OBJECT);
	    if (resource.getStatus().isSuccess()) {
		repObject = new ObjectRepresentation<Serializable>(resource.getResponseEntity());
		Serializable object = repObject.getObject();
		repObject.setObject(null);
		return object;
	    }
	    return reference;
	} catch (Exception x) {
	    throw x;
	} finally {
	    try {
		if (repObject != null)
		    repObject.release();
	    } catch (Exception x) {
	    }
	    try {
		if (resource != null)
		    resource.release();
	    } catch (Exception x) {
	    }
	}
    }
}
