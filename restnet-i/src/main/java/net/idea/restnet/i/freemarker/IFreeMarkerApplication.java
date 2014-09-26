package net.idea.restnet.i.freemarker;


public interface IFreeMarkerApplication<CONFIG> {
	public String getVersionShort();
	public String getVersionLong();
	public CONFIG getConfiguration();
	public String getProfile();
	public String getGACode();
	public boolean isEnableEmailVerification();
}
