package net.idea.restnet.groups;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.idea.restnet.b.Organisation;
import net.idea.restnet.resources.Resources;

import org.restlet.routing.Template;

public class DBOrganisation extends Organisation implements IDBGroup {
    /**
	 * 
	 */
    private static final long serialVersionUID = 6081839402557578567L;
    protected GroupType groupType = GroupType.ORGANISATION;
    protected int ID;

    public DBOrganisation() {
	this((Integer) null);
    }

    public DBOrganisation(URL resourceURL) {
	this();
	setResourceID(resourceURL);
	this.ID = -1;
    }

    public DBOrganisation(Integer id) {
	super();
	if (id != null)
	    setID(id);
    }

    public DBOrganisation(Organisation p) {
	setTitle(p.getTitle());
	setGroupName(p.getGroupName());
	setResourceID(p.getResourceID());
	this.ID = -1;
    }

    @Override
    public GroupType getGroupType() {
	return groupType;
    }

    @Override
    public void setGroupType(GroupType groupType) {
	this.groupType = groupType;

    }

    @Override
    public int getID() {
	return ID;
    }

    @Override
    public void setID(int iD) {
	this.ID = iD;

    }

    public int parseURI(String baseReference) {
	Template template = new Template(String.format("%s%s/{%s}", baseReference == null ? "" : baseReference,
		Resources.organisation, DBGroup.resourceKey));
	Map<String, Object> vars = new HashMap<String, Object>();
	try {
	    template.parse(getResourceID().toString(), vars);
	    return Integer.parseInt(vars.get(DBGroup.resourceKey).toString().substring(1));
	} catch (Exception x) {
	    return -1;
	}
    }

    @Override
    public String toString() {
	return String.format("<a href='%s' title='%s'>%s</a>", getResourceID(), getTitle() == null ? getResourceID()
		: getTitle(), getTitle() == null ? getResourceID() : getTitle());
    }
}
