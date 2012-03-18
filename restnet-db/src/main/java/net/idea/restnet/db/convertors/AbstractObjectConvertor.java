package net.idea.restnet.db.convertors;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.r.QueryReporter;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;

/**
 * 
 * @author nina
 * Creates entire object and then converts it to a representation, no streaming
 * @param <T>
 * @param <Q>
 */
public abstract  class AbstractObjectConvertor<T,Q extends IQueryRetrieval<T>,Output> 
									extends QueryRepresentationConvertor<T,Q,Output>  {
	
	public AbstractObjectConvertor(QueryReporter<T,Q,Output> reporter) {
		this(reporter,MediaType.TEXT_PLAIN,null);
	}
	public AbstractObjectConvertor(QueryReporter<T,Q,Output> reporter,MediaType media) {
		this(reporter,media,null);
	}
	public AbstractObjectConvertor(QueryReporter<T,Q,Output> reporter,MediaType media,String fileNamePrefix) {
		super(reporter,media,fileNamePrefix);
		if (this.reporter != null) ((QueryReporter<T,Q,Output>)this.reporter).setMaxRecords(5000);
	}	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6126693410309179856L;
	
	public abstract Representation process(Output doc) throws AmbitException; 
	protected abstract Output createOutput(Q query) throws AmbitException;
	
	@Override
	public Representation process(Q query) throws Exception {
		reporter.setOutput(createOutput(query));
		Representation r =  process(reporter.process(query));
		try { reporter.close(); } catch (Exception x) {}
		return r;
	};

}