package net.idea.restnet.b;

public class ServiceAccount extends BeanResource<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5867256337457676387L;
	private String service;
	private String name;

	public void setService(String service) {
		this.service = service;
	}
	public String getService() {
		return service;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

}
