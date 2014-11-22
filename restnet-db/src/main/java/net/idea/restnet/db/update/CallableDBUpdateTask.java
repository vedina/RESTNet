package net.idea.restnet.db.update;

import java.sql.Connection;

import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.modbcum.p.ProcessorException;
import net.idea.modbcum.p.UpdateExecutor;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.i.task.TaskResult;

import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

public abstract class CallableDBUpdateTask<Target,INPUT,USERID> extends CallableProtectedTask<USERID> {
	protected String baseReference;
	protected Connection connection;
	protected UpdateExecutor exec;
	protected INPUT input;
	protected Method method;
	protected boolean autoCommit = false;
	protected enum update_command {add,delete};
	
	public boolean isAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}
	public CallableDBUpdateTask(Method method,INPUT input, Connection connection,USERID token) {
		this(method,input,null,connection,token);
	}
	public CallableDBUpdateTask(Method method,INPUT input, String baseReference, Connection connection,USERID token) {
		super(token);
		this.connection = connection;
		this.input = input;
		this.method = method;
		this.baseReference = baseReference;
	}

	protected abstract Target getTarget(INPUT input) throws Exception ;
	protected abstract IQueryUpdate<? extends Object,Target> createUpdate(Target target) throws Exception ;
	protected String getURI(Target target, Method method) throws Exception {
		return getURI(target);
	}
	protected abstract String getURI(Target target) throws Exception;

	
	protected boolean isNewResource() {
		return Method.POST.equals(method);
	}
	
	@Override
	public TaskResult doCall() throws Exception {
		boolean save = connection.getAutoCommit();
		try {
			connection.setAutoCommit(isAutoCommit());
			Target target = getTarget(input);
			IQueryUpdate<? extends Object,Target> q = createUpdate(target);
			if (q!= null) {
				executeQuery(q);
				
				if (!isAutoCommit())
					connection.commit();
				
				if (Method.DELETE.equals(method)) 
					return new TaskResult(getURI(target,Method.DELETE),false);
				else
					return new TaskResult(getURI(target,method),isNewResource());
			} else
				return new TaskResult(getURI(target,method),false);

		} catch (ProcessorException x) {
			if (!isAutoCommit())
				try { connection.rollback();} catch (Exception xx) {}
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x);
		} catch (Exception x) {
			if (!isAutoCommit())
				try {connection.rollback();} catch (Exception xx) {}
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		} finally {
			try {exec.close();} catch (Exception x) {}
			try {connection.setAutoCommit(save);} catch (Exception x) {}
			try {connection.close();} catch (Exception x) {}
		}
	}
	

	protected Object executeQuery(IQueryUpdate<? extends Object,Target> q) throws Exception {
		exec = new UpdateExecutor<IQueryUpdate>();
		exec.setConnection(connection);
		return exec.process(q);
	}

	/**
	 * POST - create entity based on parameters in http header, creates a new entry in the databaseand returns an url to it
	 */
/*	
	public void executeUpdate(Representation entity, T entry, AbstractUpdate updateObject) throws ResourceException {

		Connection c = null;
		//TODO it is inefficient to instantiate executor in all classes
		UpdateExecutor executor = new UpdateExecutor();
		try {
    		DBConnection dbc = new DBConnection(getContext(),getConfigFile());
    		c = dbc.getConnection(getRequest());			

			executor.setConnection(c);
			executor.open();
			executor.process(updateObject);
			
			customizeEntry(entry, c);
			
			QueryURIReporter<T,Q> uriReporter = getURUReporter(getRequest());
			if (uriReporter!=null) {
				getResponse().setLocationRef(uriReporter.getURI(entry));
				getResponse().setEntity(uriReporter.getURI(entry),MediaType.TEXT_HTML);
			}
			getResponse().setStatus(Status.SUCCESS_OK);
			
		} catch (SQLException x) {
			Context.getCurrentLogger().severe(x.getMessage());
			getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN,x,x.getMessage());			
			getResponse().setEntity(null);			
		} catch (ProcessorException x) {
			Context.getCurrentLogger().severe(x.getMessage());
			getResponse().setStatus((x.getCause() instanceof SQLException)?Status.CLIENT_ERROR_FORBIDDEN:Status.SERVER_ERROR_INTERNAL,
					x,x.getMessage());			
			getResponse().setEntity(null);			
		} catch (Exception x) {
			Context.getCurrentLogger().severe(x.getMessage());
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x,x.getMessage());			
			getResponse().setEntity(null);
		} finally {
			try {executor.close();} catch (Exception x) {}
			try {if(c != null) c.close();} catch (Exception x) {}
		}
	}	
	*/
}
