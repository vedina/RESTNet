package net.idea.restnet.db.reporter;

import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.bucket.Bucket;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.r.QueryReporter;

public class BucketCSVReporter extends
		QueryReporter<Bucket, IQueryRetrieval<Bucket>, Writer> {
	protected String command;
	protected String subcommand;
	protected boolean writeheader = true;
	protected long started;
	protected long completed;	
	protected String delimiter=",";
	public String getDelimiter() {
		return delimiter;
	}
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3537339785122677311L;

	public BucketCSVReporter() {
		this(null,null,null);
	}
	public BucketCSVReporter(String command, String subcommand,
			IProcessor<Bucket, Bucket> processor) {
		super();
		this.command = command;
		this.subcommand = subcommand;
		if (processor != null) {
			IProcessor itemp = getProcessors().get(0);
			getProcessors().clear();
			getProcessors().add(processor);
			getProcessors().add(itemp);
		}
	}

	@Override
	public Object processItem(Bucket item) throws Exception {
		try {
			if (writeheader) {
				boolean opsCompoundPharmacology = false;
				boolean opsTargetPharmacology = false;
				for (int i = 0; i < item.getHeader().length; i++) {

					if (i > 0)
						getOutput().write(delimiter);

					getOutput().write("\"");
					getOutput().write(item.getHeader()[i]);
					getOutput().write("\"");

				}
				getOutput().write("\n");
				writeheader = false;
			}

			item.toCSV(getOutput(), delimiter);

			getOutput().write("\n");
		} catch (Exception x) {
			x.printStackTrace();
		}
		return item;
	}

	@Override
	public String getFileExtension() {
		return delimiter==","?".csv":".txt";
	}

	@Override
	public void header(Writer output, IQueryRetrieval<Bucket> query) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void footer(Writer output, IQueryRetrieval<Bucket> query) {
		// TODO Auto-generated method stub
		
	}

}
