package net.idea.restnet.i.task;

import java.util.List;

import org.restlet.data.Reference;



public class TaskResult {
	protected String uri;
	protected boolean newResource = true;
	protected List<String> policies = null;

	public List<String> getPolicy() {
		return policies;
	}
	public void setPolicy(List<String> policies) {
		this.policies = policies;
	}
	public boolean isNewResource() {
		return newResource;
	}
	public void setNewResource(boolean newResource) {
		this.newResource = newResource;
	}
	public String getUri() {
		return uri;
	}
	public Reference getReference() {
		return uri==null?null:new Reference(uri);
	}
	public void setUri(String uri) {
		this.uri = uri;
	}

	public TaskResult(String uri) {
		setUri(uri);
	}
	public TaskResult(String uri,boolean newResource) {
		setUri(uri);
		setNewResource(newResource);
	}

	@Override
	public String toString() {
		return uri;
	}
}
