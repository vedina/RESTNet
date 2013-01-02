package net.idea.restnet.user;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.idea.modbcum.i.query.QueryParam;
import net.idea.restnet.db.aalocal.user.IUser;
import net.idea.restnet.u.UserCredentials;
import net.idea.restnet.user.alerts.db.DBAlert;
import net.toxbank.client.Resources;
import net.toxbank.client.resource.User;

import org.restlet.routing.Template;

public class DBUser extends User implements IUser {
	public static final String resourceKey = "key";
	protected UserCredentials credentials = null;
	public UserCredentials getCredentials() {
		return credentials;
	}
	public void setCredentials(UserCredentials credentials) {
		this.credentials = credentials;
	}
	protected List<DBAlert> alerts;
	
	public List<DBAlert> getAlerts() {
		return alerts;
	}
	public void setAlerts(List<DBAlert> alerts) {
		this.alerts = alerts;
	}
	public enum fields {
		iduser {
			@Override
			public Object getValue(DBUser user) {
				return user==null?null:user.getID();
			}
			public QueryParam getParam(DBUser user) {
				return new QueryParam<Integer>(Integer.class, user==null?null:user.getID());
			}
		},
		username {
			@Override
			public Object getValue(DBUser user) {
				return  user==null?null:user.getUserName();
			}
		},
		title {
			@Override
			public Object getValue(DBUser user) {
				return  user==null?null:user.getTitle();
			}	
		},
		firstname {
			@Override
			public Object getValue(DBUser user) {
				return  user==null?null:user.getFirstname();
			}			
		},
		lastname {
			@Override
			public Object getValue(DBUser user) {
				return user==null?null:user.getLastname();
			}
		},
		institute {
			@Override
			public Object getValue(DBUser user) {
				return null;
			}
		},
		weblog {
			@Override
			public Object getValue(DBUser user) {
				return user==null?null:user.getWeblog();
			}			
		},
		email {
			@Override
			public Object getValue(DBUser user) {
				return user==null?null:user.getEmail();
			}			
		},		
		keywords {
			@Override
			public Object getValue(DBUser user) {
				return user==null?null:user.getKeywords();
			}			
		},		
		reviewer {
			@Override
			public Object getValue(DBUser user) {
				return user==null?null:user.isReviewer();
			}			
			public QueryParam getParam(DBUser user) {
				return new QueryParam<Boolean>(Boolean.class, user==null?null:user.isReviewer());
			}
		},				
		homepage {
			@Override
			public Object getValue(DBUser user) {
				return user==null?null:user.getHomepage();
			}			
			@Override
			public String toString() {
				return "WWW";
			}

		};
		public QueryParam getParam(DBUser user) {
			return new QueryParam<String>(String.class, getValue(user).toString());
		}
		public String getHTMLField(DBUser protocol) {
			Object value = getValue(protocol);
			return String.format("<input name='%s' type='text' size='40' value='%s'>\n",
					name(),getDescription(),value==null?"":value.toString());
		}
		@Override
		public String toString() {
			return String.format("%s%s", name().substring(0,1).toUpperCase(),name().substring(1));
		}
		public String getDescription() { return toString();}
		public abstract Object getValue(DBUser user);
			
		public String getSQL() { 
			return String.format("%s = ?", name() );
		}
	}
	protected int id=-1;
	protected String keywords;
	protected boolean reviewer = false;
	
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public boolean isReviewer() {
		return reviewer;
	}
	public void setReviewer(boolean reviewer) {
		this.reviewer = reviewer;
	}
	public int getID() {
		return id;
	}
	public void setID(int id) {
		this.id = id;
	}
	public DBUser(int id) {
		this.id = id;
	}
	public DBUser() {
		this.id = -1;
	}
	public DBUser(URL resourceURL) {
		this.id = -1;
		setResourceURL(resourceURL);
	}
	public DBUser(User p) {
		setTitle(p.getTitle());
		setUserName(p.getUserName());
		setFirstname(p.getFirstname());
		setLastname(p.getLastname());
		setHomepage(p.getHomepage());
		setOrganisations(p.getOrganisations());
		setProjects(p.getProjects());
		setWeblog(p.getWeblog());
		setResourceURL(p.getResourceURL());
		this.id = -1;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 3789186431450997758L;

	/**
	 * Parses its URI and generates ID
	 * @return
	 */
	public int parseURI(String baseReference)  {
		Template template = new Template(String.format("%s%s/{%s}",baseReference==null?"":baseReference,
				Resources.user,resourceKey));
		Map<String, Object> vars = new HashMap<String, Object>();
		try {
			template.parse(getResourceURL().toString(), vars);
			return Integer.parseInt(vars.get(resourceKey).toString().substring(1)); 
		} catch (Exception x) { return -1; }
	}
	
	@Override
	public String toString() {
		return String.format("<a href='%s'>%s%s %s</a>",
				getResourceURL(),
				getTitle()==null?"":getTitle(),
				getFirstname()==null?"":getFirstname(),
				getLastname()==null?"":getLastname()
				);
	}
	
	@Override
	public String getPassword() {
		return credentials==null?null:credentials.getNewpwd();
	}
	@Override
	public void setPassword(String password) {
		credentials = new UserCredentials(null,password);
	}
	
}
