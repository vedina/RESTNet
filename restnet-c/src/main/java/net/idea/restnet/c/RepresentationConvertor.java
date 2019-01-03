package net.idea.restnet.c;

import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;

import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.modbcum.r.AbstractRepresentationConvertor;

/**
 * An abstract {@link IProcessor} , converting between arbitrary Content and
 * restlet Representation.
 * 
 * @author nina
 * 
 * @param <Item>
 * @param <ItemList>
 * @param <Output>
 */
public abstract class RepresentationConvertor<Item, Content, Output, R extends Reporter<Content, Output>> extends
	AbstractRepresentationConvertor<Item, Content, Output, Representation, MediaType, R> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 50107090954200157L;

    public RepresentationConvertor(R reporter) {
	super(reporter);
    }

    public RepresentationConvertor(R reporter, MediaType media) {
	this(reporter, media, null);
    }

    public RepresentationConvertor(R reporter, MediaType media, String fileNamePrefix) {
	super(reporter, media, fileNamePrefix);
    }

    public String getLicenseURI() {
	return getReporter() == null ? null : getReporter().getLicenseURI();
    }

    public void setLicenseURI(String uri) {
	if (getReporter() != null)
	    getReporter().setLicenseURI(uri);
    }

    protected void setDisposition(Representation rep) {
	if (getReporter().getFileExtension() != null) {
		Disposition d = new Disposition(Disposition.TYPE_ATTACHMENT);//?
		d.setFilename(String.format("%s.%s", fileNamePrefix == null ? "download" : fileNamePrefix,
			    getReporter().getFileExtension()));
		rep.setDisposition(d);
	}
    }
}
