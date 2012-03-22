package net.idea.restnet.sparql;

import java.io.IOException;
import java.io.OutputStream;

import net.idea.modbcum.i.reporter.Reporter;
import net.idea.restnet.c.RepresentationConvertor;

import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;

public class TripleStoreConvertor<Item,Output,R extends Reporter<String,OutputStream>>
									extends RepresentationConvertor<Item,String,OutputStream,R>  {	

	/**
	 * 
	 */
	private static final long serialVersionUID = -4504271614158040711L;
	

	
	public TripleStoreConvertor(R reporter, MediaType media, String fileNamePrefix) {
		super(reporter, media, fileNamePrefix);
	}
	@Override
	public Representation process(final String queryString) throws Exception {
		OutputRepresentation rep =  new OutputRepresentation(getMediaType()) {
			@Override
			public void write(OutputStream output) throws IOException {
				try {
					getReporter().setOutput(output);
					getReporter().process(queryString);
					output.flush();
				} catch (Exception x) {
					
				} finally {
					try {getReporter().close();} catch (Exception x) {}
				}
			}
		};
		return rep;
	}	
	

}
