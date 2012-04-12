package net.idea.restnet.cli;

import java.net.URL;

public class AbstractResource implements IAbstractResource {
	protected URL resourceURL;
	@Override
	public void setResourceURL(URL resourceURL) {
		this.resourceURL = resourceURL;
	}

	@Override
	public URL getResourceURL() {
		return resourceURL;
	}

}
