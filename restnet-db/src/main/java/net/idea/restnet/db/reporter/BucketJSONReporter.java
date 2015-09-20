package net.idea.restnet.db.reporter;

import java.io.IOException;
import java.io.Writer;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

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
		this ("results",null,null);
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
			//output.write(JSONUtils.jsonQuote(JSONUtils.jsonEscape(subcommand)));
			//output.write(":[");
		} catch (Exception x) {
			x.printStackTrace();
		}

	}

	@Override
	public Object processItem(Bucket item) throws Exception {
		try {
			if (comma != null)
				getOutput().write(comma);
			toJSON(item, getOutput());
			comma = ",";
		} catch (Exception x) {
			x.printStackTrace();
		}
		return item;
	}

	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd hh:mm:ss");

	public void toJSON(Bucket item, Writer writer) throws IOException {
		writer.write("\n\t{\n");
		boolean first = true;
		for (int i = 0; i < item.getHeader().length; i++) {
			String header = item.getHeader()[i];
			Object o = item.get(header);

			if (o == null)
				continue;
			if (!first)
				writer.write(",\n");
			first = false;
			writer.write("\t");
			writer.write(JSONUtils.jsonQuote(JSONUtils.jsonEscape(header)));
			writer.write(":");

			if (o instanceof String)
				writer.write(JSONUtils.jsonQuote(JSONUtils.jsonEscape(o
						.toString())));
			else if (o instanceof Date) {
				writer.write(JSONUtils.jsonQuote(sdf.format((Date) o)));
			} else if (o instanceof Timestamp) {
				writer.write(JSONUtils.jsonQuote(sdf.format((Timestamp) o)));
			} else if (o instanceof Number)
				writer.write(o.toString());
			else if (o instanceof List) {
				writer.write("[\n");
				String comma = "";
				for (Object result : (List) o) {
					writer.write(comma);

					writer.write(JSONUtils.jsonQuote(JSONUtils
							.jsonEscape(result.toString())));
					comma = ",\n";
				}
				writer.write("\n]\n");
			} else
				writer.write(JSONUtils.jsonQuote(JSONUtils.jsonEscape(o
						.toString())));
		}
		writer.write("\n\t}");
		writer.flush();
	}

	@Override
	public void footer(Writer output, IQueryRetrieval<Bucket> query) {
		try {
			output.write("\n]");
			//output.write("\n}");
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