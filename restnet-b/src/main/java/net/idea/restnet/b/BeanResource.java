package net.idea.restnet.b;


public abstract class BeanResource<ID> implements IBeanResource<ID> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 527851902551389550L;
	private ID resourceID;
	private String title;
	
	public BeanResource(ID resourceURL) {
		setResourceID(resourceURL);
	}

	public BeanResource() {
		this(null);
	}

	public void setResourceID(ID resourceURL) {
		this.resourceID = resourceURL;
	}

	public ID getResourceID() {
		return resourceID;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
	public String toString() {
		return getResourceID()==null?super.toString():getResourceID().toString();
	}
	@Override
	public boolean equals(Object obj) {
		if ((obj!=null) && (obj instanceof IBeanResource)) {
			if (resourceID==null) return this == obj;
			return resourceID.equals(((IBeanResource)obj).getResourceID());
		} else return false;
	}
}
