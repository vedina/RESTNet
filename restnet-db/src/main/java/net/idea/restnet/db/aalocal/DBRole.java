package net.idea.restnet.db.aalocal;

import java.io.Serializable;

import org.restlet.security.Role;

/**
 * Workaround for http://restlet.tigris.org/issues/show_bug.cgi?id=1245 , which
 * is fixed in a later release. equals and hash code copied from
 * https://github.com
 * /restlet/restlet-framework-java/blob/master/modules/org.restlet
 * /src/org/restlet/security/Role.java
 * 
 * @author nina
 * 
 */
public class DBRole extends Role implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -2827153813923997798L;

    public DBRole(String name, String description) {
	super(name, description);
    }

    @Override
    public boolean equals(Object target) {
	boolean result = false;
	if (getName() == null) {
	    return target == null;
	}
	if (target instanceof Role) {
	    Role r = (Role) target;
	    // Test equality of names and child roles.
	    result = getName().equals(r.getName()) && getChildRoles().size() == r.getChildRoles().size();
	    if (result && !getChildRoles().isEmpty()) {
		for (int i = 0; result && i < getChildRoles().size(); i++) {
		    result = getChildRoles().get(i).equals(r.getChildRoles().get(i));
		}
	    }
	}

	return result;
    }

    @Override
    public int hashCode() {
	int result;
	if (getName() == null) {
	    result = super.hashCode();
	} else if (getChildRoles().isEmpty()) {
	    result = getName().hashCode();
	} else {
	    StringBuilder sb = new StringBuilder(getName()).append("(");
	    for (Role role : getChildRoles()) {
		sb.append(role.hashCode()).append("-");
	    }
	    sb.append(")");
	    result = sb.toString().hashCode();
	}
	return result;
    }

}
