package net.idea.restnet.b;

public class UApp<USER extends User<String>> extends BeanResource<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6652028260629988868L;
	protected AppToken key;
	
	public AppToken getKey() {
		return key;
	}

	public void setKey(AppToken key) {
		this.key = key;
	}
	protected String referer;
	protected USER user;
	protected long created;
	protected long expire;
	protected String scope;
	protected boolean enabled;
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public long getExpire() {
		return expire;
	}

	public void setExpire(long expire) {
		this.expire = expire;
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	
	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

	public USER getUser() {
		return user;
	}

	public void setUser(USER user) {
		this.user = user;
	}
	public String getName() {
		return getTitle();
	}
	public void setName(String name) {
		setTitle(name);
	}
}


