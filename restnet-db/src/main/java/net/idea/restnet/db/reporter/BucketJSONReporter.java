package net.idea.restnet.db.reporter;

import net.idea.modbcum.i.bucket.Bucket;
import net.idea.modbcum.i.processors.IProcessor;

public class BucketJSONReporter extends AbstractBucketJsonReporter<Bucket> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3537339785122677311L;

	public BucketJSONReporter() {
		this("results", null, null);
	}

	public BucketJSONReporter(String command, String subcommand,
			IProcessor<Bucket, Bucket> processor) {
		super(command,subcommand,processor);
	}
	@Override
	public Bucket transform(Bucket object) {
		return object;
	}



}