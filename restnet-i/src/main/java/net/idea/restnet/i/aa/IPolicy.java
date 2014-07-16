package net.idea.restnet.i.aa;

public interface IPolicy<ID> {
	public String getUri();
	public void setUri(String uri);
	public ID getId();
	public void setId(ID id);
}
