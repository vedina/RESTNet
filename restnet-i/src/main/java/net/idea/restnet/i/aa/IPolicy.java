package net.idea.restnet.i.aa;

import java.io.Serializable;

public interface IPolicy<ID> extends Serializable {
	public String getUri();
	public void setUri(String uri);
	public ID getId();
	public void setId(ID id);
}
