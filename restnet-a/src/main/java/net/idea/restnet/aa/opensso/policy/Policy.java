package net.idea.restnet.aa.opensso.policy;

import java.io.Serializable;

import net.idea.restnet.i.aa.IPolicy;

public class Policy implements Serializable, IPolicy<String> {
    /**
	 * 
	 */
    private static final long serialVersionUID = -2593227354616198381L;
    protected String uri;

    public String getUri() {
	return uri;
    }

    public void setUri(String uri) {
	this.uri = uri;
    }

    protected String id;
    protected String xml;

    public Policy(String id) {
	setId(id);
    }

    public String getXml() {
	return xml;
    }

    public void setXml(String xml) {
	this.xml = xml;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    @Override
    public String toString() {
	return getId();
    }
}
