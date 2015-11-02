package net.idea.restnet.b;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class User<ID> extends BeanResource<ID> {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5992734627276310940L;

	private String userName;

	private BeanResourceSet<ID,Project<ID>> projects;
	private BeanResourceSet<ID,Organisation<ID>> organisations;
	private String firstname;
	private String lastname;
	private URL homepage;
	private URL weblog;
	private List<ServiceAccount> accounts;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public URL getHomepage() {
		return homepage;
	}

	public void setHomepage(URL homepage) {
		this.homepage = homepage;
	}

	public List<ServiceAccount> getAccounts() {
		return accounts;
	}

	public void addAccount(ServiceAccount account) {
		if (account == null) return; 
		if (accounts == null) accounts = new ArrayList<ServiceAccount>();
		accounts.add(account);
	}

	public User() {}

	public User(ID identifier) {
		setResourceID(identifier);
	}

	public void setProjects(BeanResourceSet<ID,Project<ID>> projects) {
		this.projects = projects;
	}
	
	public void setProjects(List<Project<ID>> projects) {
		this.projects = new BeanResourceSet<ID,Project<ID>>(projects);
	}
		
	public void addProject(Project<ID> project) {
		if (project == null) return;
		if (projects == null) this.projects = new BeanResourceSet<ID,Project<ID>>();
		projects.add(project);		
	}

	public BeanResourceSet<ID,Project<ID>> getProjects() {
		return projects;
	}

	public void setOrganisations(BeanResourceSet<ID,Organisation<ID>> orgs) {
		this.organisations = orgs;
	}

	public void setOrganisations(List<Organisation<ID>> orgs) {
		this.organisations = new BeanResourceSet<ID,Organisation<ID>>(orgs);
	}
	
	public BeanResourceSet<ID,Organisation<ID>> getOrganisations() {
		return organisations;
	}
	public void addOrganisation(Organisation<ID> org) {
		if (org == null) return;
		if (organisations == null) this.organisations = new BeanResourceSet<ID,Organisation<ID>>();
		organisations.add(org);		
	}	

	public void setWeblog(URL weblog) {
		this.weblog = weblog;
	}

	public URL getWeblog() {
		return weblog;
	}
	@Override
	public String toString() {
		return String.format("%s %s %s (%s)", 
				getTitle()==null?"":getTitle(),
				getFirstname()==null?"":getFirstname(),
				getLastname()==null?"":getLastname(),
				getUserName()==null?"":getUserName());
	}
	
	public static final String mailto = "mailto";
	/**
	 * Wrapper to access the first email account
	 * @return
	 */
	public String getEmail() {
		List<ServiceAccount> accounts = getAccounts();
		if ((accounts==null) || (accounts.size()==0)) return null;
		for (ServiceAccount account : accounts) if (mailto.equals(account.getService())) return account.getName();
		return null;
	}
	/**
	 * Wrapper to set the first email account
	 * @param email
	 */
	public void setEmail(String email) {
		List<ServiceAccount> accounts = getAccounts();
		ServiceAccount newAccount = null;
		if (accounts!=null) 
			for (ServiceAccount account : accounts) {
				if (mailto.equals(account.getService()))  {
					account.setName(email);
					newAccount = account;
					break;
				}
			}
		else { 
			newAccount = new ServiceAccount(); 
			newAccount.setName(email);newAccount.setService(mailto);
			addAccount(newAccount);
		}
		if (newAccount!=null)
		try {
			newAccount.setResourceID(String.format("%s:%s", mailto,email));
		} catch (Exception x) { x.printStackTrace();
			newAccount.setResourceID(null); 
		}		

	}
}
