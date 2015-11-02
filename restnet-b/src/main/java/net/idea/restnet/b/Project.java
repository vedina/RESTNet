package net.idea.restnet.b;


public class Project<ID> extends Group<ID> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2361353568264093741L;
	public Project() {
		this(null);
	}
	public Project(ID resourceURL) {
		setResourceID(resourceURL);
	}
}
