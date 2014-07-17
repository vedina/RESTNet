package net.idea.restnet.i.aa;

public interface IRESTPolicy<ID> extends IPolicy<ID> {
	
	public boolean isAllowGET();
	public void setAllowGET(Boolean allowGET);
	public boolean isAllowPOST() ;
	public void setAllowPOST(Boolean allowPOST);
	public boolean isAllowPUT();
	public void setAllowPUT(Boolean allowPUT);
	public boolean isAllowDELETE();
	public void setAllowDELETE(Boolean allowDELETE);
	String toJSON(String baseref);
	String getRole();
	void setRole(String rolename);
	String[] splitURI(String uri)  throws Exception;
	
	String getPolicyURI(String baseRef);
}
