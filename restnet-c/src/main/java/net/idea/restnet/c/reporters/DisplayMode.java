package net.idea.restnet.c.reporters;

public enum DisplayMode {

    table, singleitem {
	@Override
	public boolean isCollapsed() {
	    return false;
	}
    },
    hierarchy, edit {
	@Override
	public boolean isCollapsed() {
	    return false;
	}
    };
    // for compatibility
    public boolean isCollapsed() {
	return true;
    }
}
