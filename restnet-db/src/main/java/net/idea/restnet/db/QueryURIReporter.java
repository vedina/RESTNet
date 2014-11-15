package net.idea.restnet.db;

import java.io.IOException;
import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.c.ResourceDoc;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Reference;

/**
 * Reports query results in text/uri-list 
 * @author nina
 *
 * @param <T>
 * @param <Q>
 */
public abstract class QueryURIReporter<T,Q extends IQueryRetrieval<T>>  extends QueryReporter<T,Q,Writer>  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4566136103208284105L;

	
	private String delimiter = "\n";
	
	@Deprecated
	public ResourceDoc getDocumentation() {
		return null;
	}
	protected Request request;
	
	public Request getRequest() {
		return request;
	}
	public void setRequest(Request request) {
		this.request = request;
	}
	protected Reference baseReference;
	
	public Reference getBaseReference() {
		return baseReference;
	}
	protected QueryURIReporter(Reference baseRef,ResourceDoc doc) {
		this(baseRef,null,"\n");
	}
	protected QueryURIReporter(Reference baseRef,ResourceDoc doc,String delimiter) {
		this.baseReference = baseRef;
		this.delimiter = delimiter;
	}
	public QueryURIReporter(Request request,ResourceDoc doc) {
		this(request==null?null:request.getRootRef(),doc,"\n");
		this.request = request;
	}

	public Reference getResourceRef() {
		return request==null?null:request.getResourceRef();
	}
	
	protected QueryURIReporter() {
	}	
	@Override
	public Object processItem(T item) throws Exception {
		try {
			String o = getURI(item);
			if (o != null) 	{
				output.write(o);
				output.write(delimiter);
			}
			output.flush();
		} catch (IOException x) {
			Context.getCurrentLogger().severe(x.getMessage());
		}
		return null;
	}	
	public abstract String getURI(String ref, T item);
	
	public String getURI(T item) {
		String ref = baseReference==null?"":baseReference.toString();
		if (ref.endsWith("/")) ref = ref.substring(0,ref.length()-1);	
		return getURI(ref,item);
	}
	public void footer(Writer output, Q query) {};
	public void header(Writer output, Q query) {};
	
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void close() throws Exception {
		setRequest(null);
		super.close();
}
}
