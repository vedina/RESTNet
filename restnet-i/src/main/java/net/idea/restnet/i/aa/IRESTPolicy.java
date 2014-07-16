package net.idea.restnet.i.aa;

public interface IRESTPolicy<ID> extends IPolicy<ID> {
	
	public boolean isAllowGET();
	public void setAllowGET(boolean allowGET);
	public boolean isAllowPOST() ;
	public void setAllowPOST(boolean allowPOST);
	public boolean isAllowPUT();
	public void setAllowPUT(boolean allowPUT);
	public boolean isAllowDELETE();
	public void setAllowDELETE(boolean allowDELETE);
	String toJSON(String baseref);
	String getRole();
	void setRole(String rolename);
	String[] splitURI(String uri)  throws Exception;
}
