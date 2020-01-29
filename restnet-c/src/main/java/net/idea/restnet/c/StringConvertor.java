package net.idea.restnet.c;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.reporter.Reporter;

/**
 * Converts query results to string
 * 
 * @author nina
 * 
 * @param <T>
 * @param <Q>
 * @param <R>
 */
public class StringConvertor<T, Q, R extends Reporter<Q, Writer>> extends RepresentationConvertor<T, Q, Writer, R> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6126693410309179856L;

	public StringConvertor(R reporter, MediaType mediaType) throws ResourceException {
		this(reporter, mediaType, null);
	}

	public StringConvertor(R reporter, MediaType mediaType, String fileNamePrefix) throws ResourceException {
		super(reporter, mediaType, fileNamePrefix);
		if (reporter == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	}

	@Override
	public Representation process(Q query) throws Exception {
		if (reporter == null)
			throw new AmbitException("Reporter is null");
		if (query == null)
			throw new AmbitException("Query is null");
		try {
			reporter.setOutput(new StringWriter());
			Writer writer = reporter.process(query);
			writer.flush();
			StringRepresentation rep = new StringRepresentation(writer.toString(), getMediaType(), Language.ENGLISH,
					CharacterSet.UTF_8);
			setDisposition(rep);
			return rep;
		} catch (IOException x) {
			throw new AmbitException(x);
		} finally {
			try {
				reporter.close();
			} catch (Exception x) {
			}
		}

	};

	@Override
	public void open() throws Exception {

	}
}
