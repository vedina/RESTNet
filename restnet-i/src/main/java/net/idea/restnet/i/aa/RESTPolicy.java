package net.idea.restnet.i.aa;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;

import net.idea.restnet.i.tools.JSONUtils;

public class RESTPolicy implements IRESTPolicy<Integer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7179443591458321559L;

	public enum _fields {
		id, uri, resource, role, methods, get, post, put, delete
	}

	protected boolean allowGET;
	protected boolean allowPOST;
	protected boolean allowPUT;
	protected boolean allowDELETE;
	protected String role;

	public int getLevel(String resource) {
		return StringUtils.countMatches(resource, "/");
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		if (role != null)
			this.role = role;
	}

	public String getPolicyURI(String baseRef) {
		return String.format("%s/%d", baseRef == null ? "" : baseRef, getId());
	}

	protected String resource;
	protected int id;

	public boolean isAllowGET() {
		return allowGET;
	}

	public void setAllowGET(Boolean allowGET) {
		if (allowGET != null)
			this.allowGET = allowGET;
	}

	public boolean isAllowPOST() {
		return allowPOST;
	}

	public void setAllowPOST(Boolean allowPOST) {
		if (allowPOST != null)
			this.allowPOST = allowPOST;
	}

	public boolean isAllowPUT() {
		return allowPUT;
	}

	public void setAllowPUT(Boolean allowPUT) {
		if (allowPUT != null)
			this.allowPUT = allowPUT;
	}

	public boolean isAllowDELETE() {
		return allowDELETE;
	}

	public void setAllowDELETE(Boolean allowDELETE) {
		if (allowDELETE != null)
			this.allowDELETE = allowDELETE;
	}

	@Override
	public String getUri() {
		return resource;
	}

	@Override
	public void setUri(String uri) {
		if (uri != null)
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

	@Override
	public String[] splitURI(String href) throws Exception {
		String[] prefix_resource = { null, null };
		URI uri = new URI(href);
		String[] segments = uri.getPath().split("\\/");
		for (int i = 0; i < segments.length; i++) {
			if (!"".equals(segments[i])) {
				if (prefix_resource[0] == null) {
					// prefix_resource[0] = "//" + uri.getHost() + ":" +
					// uri.getPort() + "/"+segments[i];
					prefix_resource[0] = "/" + segments[i];
				} else {
					if (prefix_resource[1] == null)
						prefix_resource[1] = "/" + segments[i];
					else
						prefix_resource[1] += "/" + segments[i];
				}
			}
		}
		if (prefix_resource[1] == null) {
			prefix_resource[1] = prefix_resource[0];
			prefix_resource[0] = "";
		}
		return prefix_resource;
	}

	public String toJSON(String baseref) {
		StringBuilder b = new StringBuilder();
		b.append("{");

		b.append("\n\t");
		b.append(JSONUtils.jsonQuote(_fields.uri.name()));
		b.append(": ");
		b.append(JSONUtils.jsonQuote(JSONUtils
				.jsonEscape(getPolicyURI(baseref))));

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
		b.append(": {");

		b.append(JSONUtils.jsonQuote(_fields.get.name()));
		b.append(":");
		b.append(isAllowGET());
		b.append(",");

		b.append(JSONUtils.jsonQuote(_fields.post.name()));
		b.append(":");
		b.append(isAllowPOST());
		b.append(",");

		b.append(JSONUtils.jsonQuote(_fields.put.name()));
		b.append(":");
		b.append(isAllowPUT());
		b.append(",");

		b.append(JSONUtils.jsonQuote(_fields.delete.name()));
		b.append(":");
		b.append(isAllowDELETE());

		b.append("}\t\n\t}");
		return b.toString();
	}

	@Override
	public String toString() {
		return getPolicyURI("");
	}

}
