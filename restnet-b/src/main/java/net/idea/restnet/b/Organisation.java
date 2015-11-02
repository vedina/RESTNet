package net.idea.restnet.b;


public class Organisation<ID> extends Group<ID> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4413344939565184769L;
	public Organisation() {
		this(null);
	}
	public Organisation(ID resourceURL) {
		setResourceID(resourceURL);
	}

}
