package net.idea.restnet.db.reporter;

import java.io.Writer;

import net.idea.modbcum.i.IJSONQueryParams;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.bucket.Bucket;
import net.idea.modbcum.i.json.JSONUtils;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.r.QueryReporter;

public class BucketJSONReporter extends
		QueryReporter<Bucket, IQueryRetrieval<Bucket>, Writer> {
	protected String command;
	protected String subcommand;
	protected long started;
	protected long completed;
	/**
	 * 
	 */
	private static final long serialVersionUID = 3537339785122677311L;
	protected String comma = null;

	public BucketJSONReporter() {
		this("results", null, null);
	}

	public BucketJSONReporter(String command, String subcommand,
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
	public void header(Writer output, IQueryRetrieval<Bucket> query) {
		try {
			output.write("{\n");
			output.write(JSONUtils.jsonQuote(JSONUtils.jsonEscape(command)));
			output.write(": [\n\t");
			if (query instanceof IJSONQueryParams)
				if (((IJSONQueryParams) query).getJsonParams() != null) {
					output.write(JSONUtils.jsonQuote(JSONUtils
							.jsonEscape("params")));
					output.write(":");
					output.write(((IJSONQueryParams) query).getJsonParams());
					output.write(",\n");
				}
			// output.write(JSONUtils.jsonQuote(JSONUtils.jsonEscape(subcommand)));
			// output.write(":[");
		} catch (Exception x) {
			//x.printStackTrace();
		}

	}

	@Override
	public Object processItem(Bucket item) throws Exception {
		try {
			if (comma != null)
				getOutput().write(comma);
			getOutput().write(item.asJSON());
			comma = ",";
		} catch (Exception x) {
			x.printStackTrace();
		}
		return item;
	}

	@Override
	public void footer(Writer output, IQueryRetrieval<Bucket> query) {
		try {
			output.write("\n]");
			// output.write("\n}");
			footerJSON(output);
		} catch (Exception x) {
		}
	}

	public void footerJSON(Writer writer) throws Exception {
		writer.write(",\n\"retrieved_ms\":");
		writer.write(Long.toString(completed));
		writer.write("\n}\n");
	}

}