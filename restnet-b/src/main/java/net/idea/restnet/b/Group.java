package net.idea.restnet.b;



public class Group<ID> extends BeanResource<ID> {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5099115905378243028L;
	private String groupName;
	private String supergroup;

	public String getSuperGroup() {
		return supergroup;
	}

	public void setSuperGroup(String String) {
		this.supergroup = String;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	@Override
	public String toString() {
		return String.format("%s (%s)", 
				getTitle()==null?"":getTitle(),
				getGroupName()==null?"":getGroupName());
	}
}
