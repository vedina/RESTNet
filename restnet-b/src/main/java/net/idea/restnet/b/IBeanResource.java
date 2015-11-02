package net.idea.restnet.b;

import java.io.Serializable;


public interface IBeanResource<ID> extends Serializable  {

	public void setResourceID(ID resourceURL);

	public ID getResourceID();

	public void setTitle(String title);

	public String getTitle();

}
