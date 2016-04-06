package net.idea.restnet.db.facet;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.facet.IFacet;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.c.ChemicalMediaType;
import net.idea.restnet.db.QueryResource;
import net.idea.restnet.db.convertors.OutputWriterConvertor;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

public abstract class FacetResource<FACET extends IFacet<String>, Q extends IQueryRetrieval<FACET>>
		extends QueryResource<Q, FACET> {
	public static final String resource = "/facet";

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] { MediaType.TEXT_HTML,
				MediaType.TEXT_PLAIN, MediaType.TEXT_URI_LIST,
				MediaType.TEXT_CSV, MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_RDF_TURTLE, MediaType.TEXT_RDF_N3,
				ChemicalMediaType.APPLICATION_JSONLD,
				MediaType.TEXT_RDF_NTRIPLES, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JAVASCRIPT,
				MediaType.APPLICATION_JAVA_OBJECT,
				ChemicalMediaType.APPLICATION_FACETJSON

		});
	}

	@Override
	public IProcessor<Q, Representation> createConvertor(Variant variant)
			throws AmbitException, ResourceException {

		String filenamePrefix = getRequest().getResourceRef().getPath();
		if (variant.getMediaType().equals(MediaType.TEXT_CSV)) {
			return new OutputWriterConvertor(
					new FacetCSVReporter(getRequest()), MediaType.TEXT_CSV);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
			return new OutputWriterConvertor(
					new FacetJSONReporter(getRequest()),
					MediaType.APPLICATION_JSON);
		} else if (variant.getMediaType().equals(
				MediaType.APPLICATION_JAVASCRIPT)) {
			String jsonpcallback = getParams().getFirstValue("jsonp");
			if (jsonpcallback == null)
				jsonpcallback = getParams().getFirstValue("callback");
			return new OutputWriterConvertor(new FacetJSONReporter(
					getRequest(), jsonpcallback),
					MediaType.APPLICATION_JAVASCRIPT);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new OutputWriterConvertor(
					new FacetURIReporter(getRequest()),
					MediaType.TEXT_URI_LIST, filenamePrefix);
		} else if (variant.getMediaType().equals(ChemicalMediaType.APPLICATION_FACETJSON)) {
			return new OutputWriterConvertor(
					new FacetTreeJSONReporter(getRequest()),
					ChemicalMediaType.APPLICATION_FACETJSON);			
		} else
			return new OutputWriterConvertor(
					new FacetJSONReporter(getRequest()),
					MediaType.APPLICATION_JSON);
	}

}