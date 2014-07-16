package net.idea.restnet.i.aa;

import net.idea.restnet.i.tools.JSONUtils;


public class RESTPolicy implements IRESTPolicy<Integer>{
	protected enum _fields {
		id,
		uri,
		resource,
		role,
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
	protected String role;
	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getPolicyURI(String baseRef) {
		return String.format("%s/%d",baseRef==null?"":baseRef,getId());
	}

	protected String resource;
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
		return resource;
	}

	@Override
	public void setUri(String uri) {
		this.resource = uri;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String toJSON(String baseref) {
		StringBuilder b = new StringBuilder();
		b.append("{");

		b.append("\n\t");
		b.append(JSONUtils.jsonQuote(_fields.id.name()));		
		b.append(": ");
		b.append(getId());

		b.append(",\n\t");
		b.append(JSONUtils.jsonQuote(_fields.uri.name()));		
		b.append(": ");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(getPolicyURI(baseref))));
		
		b.append(",\n\t");
		b.append(JSONUtils.jsonQuote(_fields.resource.name()));		
		b.append(": ");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(getUri())));

		b.append(",\n\t");
		b.append(JSONUtils.jsonQuote(_fields.role.name()));		
		b.append(": ");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(getRole())));
		
		b.append(",\n\t");
		b.append(JSONUtils.jsonQuote(_fields.methods.name()));		
		b.append(": [");
		b.append("{");
			b.append(JSONUtils.jsonQuote(_fields.get.name()));
			b.append(":");b.append(isAllowGET());
		b.append("},");
		b.append("{");
			b.append(JSONUtils.jsonQuote(_fields.post.name()));
			b.append(":");b.append(isAllowPOST());
		b.append("},");
		b.append("{");
			b.append(JSONUtils.jsonQuote(_fields.put.name()));
			b.append(":");b.append(isAllowPUT());
		b.append("},");
		b.append("{");
			b.append(JSONUtils.jsonQuote(_fields.delete.name()));
			b.append(":");b.append(isAllowDELETE());
		b.append("}");		
		b.append("]\t\n\t}");
		return b.toString();
	}
	
	@Override
	public String toString() {
		return toJSON(null);
	}

}
