package net.idea.restnet.groups;

import java.util.HashMap;
import java.util.Map;

import net.idea.restnet.b.Project;
import net.idea.restnet.resources.Resources;

import org.restlet.routing.Template;

public class DBProject extends Project<String> implements IDBGroup {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4356100798611119822L;
	protected GroupType groupType = GroupType.PROJECT;

	public GroupType getGroupType() {
		return groupType;
	}

	public void setGroupType(GroupType groupType) {
		this.groupType = groupType;
	}

	protected int ID;

	public DBProject() {
		this((Integer) null);
	}

	public DBProject(String resourceURL) {
		this();
		setResourceID(resourceURL);
		this.ID = -1;
	}

	public DBProject(Integer id) {
		if (id != null)
			setID(id);
	}

	/**
	 * just copy it
	 * 
	 * @param p
	 */
	public DBProject(Project<String> p) {
		setTitle(p.getTitle());
		setGroupName(p.getGroupName());
		setResourceID(p.getResourceID());
		this.ID = -1;
	}

	@Override
	public int getID() {
		return ID;
	}

	@Override
	public void setID(int iD) {
		this.ID = iD;

	}

	@Override
	public String toString() {
		return String.format("<a href='%s' title='%s'>%s</a>", getResourceID(),
				getTitle() == null ? getResourceID() : getTitle(),
				getTitle() == null ? getResourceID() : getTitle());
	}

	/**
	 * Parses its URI and generates ID
	 * 
	 * @return
	 */
	public int parseURI(String baseReference) {
		Template template = new Template(String.format("%s%s/{%s}",
				baseReference == null ? "" : baseReference, Resources.project,
				DBGroup.resourceKey));
		Map<String, Object> vars = new HashMap<String, Object>();
		try {
			template.parse(getResourceID().toString(), vars);
			return Integer.parseInt(vars.get(DBGroup.resourceKey).toString()
					.substring(1));
		} catch (Exception x) {
			return -1;
		}
	}

}
