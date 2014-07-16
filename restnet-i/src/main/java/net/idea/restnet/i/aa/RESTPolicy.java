package net.idea.restnet.i.aa;

import net.idea.restnet.i.tools.JSONUtils;


public class RESTPolicy implements IRESTPolicy<Integer>{
	protected enum _fields {
		id,
		uri,
		methods,
		get,
		post,
		put,
		delete
	}
	
	protected boolean allowGET;
	protected boolean allowPOST;
	protected boolean allowPUT;
	protected boolean allowDELETE;
	protected String uri;
	protected int id;
	public boolean isAllowGET() {
		return allowGET;
	}

	public void setAllowGET(boolean allowGET) {
		this.allowGET = allowGET;
	}

	public boolean isAllowPOST() {
		return allowPOST;
	}

	public void setAllowPOST(boolean allowPOST) {
		this.allowPOST = allowPOST;
	}

	public boolean isAllowPUT() {
		return allowPUT;
	}

	public void setAllowPUT(boolean allowPUT) {
		this.allowPUT = allowPUT;
	}

	public boolean isAllowDELETE() {
		return allowDELETE;
	}

	public void setAllowDELETE(boolean allowDELETE) {
		this.allowDELETE = allowDELETE;
	}

	
	@Override
	public String getUri() {
		return uri;
	}

	@Override
	public void setUri(String uri) {
		this.uri = uri;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String toJSON() {
		StringBuilder b = new StringBuilder();
		b.append("{");

		b.append("\n\t");
		b.append(JSONUtils.jsonQuote(_fields.id.name()));		
		b.append(": ");
		b.append(getId());

		b.append(",\n\t");
		b.append(JSONUtils.jsonQuote(_fields.uri.name()));		
		b.append(": ");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(getUri())));
		
		b.append(",\n\t");
		b.append(JSONUtils.jsonQuote(_fields.methods.name()));		
		b.append(": [");
		b.append("{");
			b.append(JSONUtils.jsonQuote(_fields.get.name()));
			b.append(":");b.append(isAllowGET());
		b.append("},\n");
		b.append("{");
			b.append(JSONUtils.jsonQuote(_fields.post.name()));
			b.append(":");b.append(isAllowPOST());
		b.append("},\n");
		b.append("{");
			b.append(JSONUtils.jsonQuote(_fields.put.name()));
			b.append(":");b.append(isAllowPUT());
		b.append("},\n");
		b.append("{");
			b.append(JSONUtils.jsonQuote(_fields.delete.name()));
			b.append(":");b.append(isAllowDELETE());
		b.append("},\n");		
		b.append("\n]}");
		return b.toString();
	}
	
	@Override
	public String toString() {
		return toJSON();
	}

}
