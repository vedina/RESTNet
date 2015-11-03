package net.idea.restnet.rdf.test;

import junit.framework.Assert;

import com.hp.hpl.jena.ontology.OntModel;

import net.idea.restnet.rdf.ns.OT;

public class TestRDF {
	@org.junit.Test
	public void test() throws Exception {
		OntModel model = OT.createModel();
		Assert.assertNotNull(model);
		model.close();
	}
}
