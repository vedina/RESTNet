package net.idea.restnet.i.freemarker;


public interface IFreeMarkerApplication<CONFIG> {
	public String getVersionShort();
	public String getVersionLong();
	public CONFIG getConfiguration();
}
