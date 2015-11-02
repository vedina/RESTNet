package net.idea.restnet.b;

import java.util.ArrayList;
import java.util.Collection;

public class BeanResourceSet<ID,T> extends ArrayList<T>  implements IBeanResource<ID> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5374523068165504570L;
	private ID resourceID;
	private String title;

	public BeanResourceSet(int initialCapacity) {
		super(initialCapacity);
	}
	public BeanResourceSet() {
		super();
	}
	public BeanResourceSet(Collection<? extends T> c) {
		super(c);
	}
	@Override
	public void setResourceID(ID resourceURL) {
		this.resourceID = resourceURL;
		
	}

	@Override
	public ID getResourceID() {
		return resourceID;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getTitle() {
		return title;
	}
	@Override
	public String toString() {
		return getResourceID()==null?super.toString():getResourceID().toString();
	}
}
