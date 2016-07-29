package net.idea.restnet.db.reporter;

import java.io.Writer;

import net.idea.modbcum.i.IJSONQueryParams;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.bucket.Bucket;
import net.idea.modbcum.i.json.JSONUtils;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.r.QueryReporter;

public abstract class AbstractBucketJsonReporter<T> extends
		QueryReporter<T, IQueryRetrieval<T>, Writer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8489467598826786293L;
	protected String command;
	protected String subcommand;
	protected long started;
	protected long completed;
	protected String comma = null;

	public AbstractBucketJsonReporter() {
		this("results", null, null);
	}

	public AbstractBucketJsonReporter(String command, String subcommand,
			IProcessor<T, T>[] processors) {
		super();
		this.command = command;
		this.subcommand = subcommand;
		if (processors != null) {
			IProcessor itemp = getProcessors().get(0);
			getProcessors().clear();
			for (IProcessor<T, T> processor : processors)
				getProcessors().add(processor);
			getProcessors().add(itemp);
		}
	}

	public abstract Bucket transform(T object);

	@Override
	public Object processItem(T item) throws Exception {
		Bucket bucket = transform(item);
		try {
			if (comma != null)
				getOutput().write(comma);
			getOutput().write(bucket.asJSON());
			comma = ",";
		} catch (Exception x) {
			// x.printStackTrace();
		}
		return item;
	}

	@Override
	public void header(Writer output, IQueryRetrieval<T> query) {
		try {
			if (command != null) {
				output.write("{\n");
				if (query instanceof IJSONQueryParams)
					if (((IJSONQueryParams) query).getJsonParams() != null) {
						output.write(JSONUtils.jsonQuote(JSONUtils
								.jsonEscape("params")));
						output.write(":");
						output.write(((IJSONQueryParams) query).getJsonParams());
						output.write(",\n");
					}

				output.write(JSONUtils.jsonQuote(JSONUtils.jsonEscape(command)));
				output.write(": ");
			}
			output.write("[\n\t");

			// output.write(JSONUtils.jsonQuote(JSONUtils.jsonEscape(subcommand)));
			// output.write(":[");
		} catch (Exception x) {
			// x.printStackTrace();
		}

	}

	@Override
	public void footer(Writer output, IQueryRetrieval<T> query) {
		try {
			output.write("\n]");
			// output.write("\n}");
			if (command != null)
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
