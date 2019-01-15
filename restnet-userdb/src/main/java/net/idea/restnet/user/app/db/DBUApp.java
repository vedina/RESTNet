package net.idea.restnet.user.app.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import net.idea.restnet.b.AppToken;
import net.idea.restnet.b.UApp;
import net.idea.restnet.user.DBUser;

public class DBUApp extends UApp<DBUser> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2273146747627423712L;

	protected int ID;

	public DBUApp() {
		super();
	}

	public DBUApp(int id) {
		this(id, null);
	}

	public DBUApp(DBUser user) {
		this(-1, user);
	}
	public DBUApp(int id, DBUser user) {
		super();
		setID(id);
		setUser(user);
	}

	
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}
	
	
	public enum _fields {
		name {

			@Override
			public Object getValue(DBUApp item) {
				return item.getTitle();
			}

			@Override
			public void setValue(DBUApp alert, String value) throws SQLException {
				alert.setName(value);
			}
		},		
		token {

			@Override
			public Object getValue(DBUApp item) {
				return item.getKey().getToken();
			}

			@Override
			public void setValue(DBUApp item, String value) throws SQLException {
				
				item.setKey(new AppToken(value));
			}
		},
		tokentype {

			@Override
			public Object getValue(DBUApp item) {
				return item.getKey().getTokenType();
			}

			@Override
			public void setValue(DBUApp item, String value) throws SQLException {
				if (item.getKey() == null) item.setKey(new AppToken());
				item.getKey().setTokenType(value);
			}
		},		
		referer {

			@Override
			public void setValue(DBUApp item, String value) throws SQLException {
				item.setReferer(value);
			}

			@Override
			public Object getValue(DBUApp item) {
				return item.getReferer();
			}
		},
		scope {

			@Override
			public void setValue(DBUApp item, String value) throws SQLException {
				item.setScope(value);
			}

			@Override
			public Object getValue(DBUApp item) {
				return item.getScope();
			}
		},		
		created {
			@Override
			public void setValue(DBUApp item, String value) throws SQLException {
				item.setCreated(Long.parseLong(value));
			}

			@Override
			public void setParam(DBUApp item, ResultSet rs) throws SQLException {
				try {
					Timestamp date = rs.getTimestamp(name());
					item.setCreated(date.getTime());
				} catch (Exception x) {
					item.setCreated(0L);
				}
			}

			@Override
			public Object getValue(DBUApp item) {
				return item.getCreated();
			}
		},
		expire {
			@Override
			public void setValue(DBUApp item, String value) throws SQLException {
				item.setExpire(Long.parseLong(value));
			}

			@Override
			public void setParam(DBUApp item, ResultSet rs) throws SQLException {
				try {
					Timestamp date = rs.getTimestamp(name());
					item.setExpire(date.getTime());
				} catch (Exception x) {
					item.setExpire(0L);
				}
			}

			@Override
			public Object getValue(DBUApp item) {
				return item.getCreated();
			}
		},		
		username {
			@Override
			public void setValue(DBUApp item, String value) throws SQLException {
				DBUser user = new DBUser(Integer.parseInt(value));
				item.setUser(user);
			}

			@Override
			public void setParam(DBUApp item, ResultSet rs) throws SQLException {
				DBUser user = new DBUser();
				user.setUserName(rs.getString("username"));
				item.setUser(user);
			}

			@Override
			public Object getValue(DBUApp item) {
				return item.getUser();
			}
		},
		enabled {
			@Override
			public void setValue(DBUApp item, String value) throws SQLException {
				item.setEnabled(Integer.parseInt(value)>0);
			}

			@Override
			public void setParam(DBUApp item, ResultSet rs) throws SQLException {
				try {
					item.setEnabled(rs.getBoolean(name()));
				} catch (Exception x) {
					item.setEnabled(true);
				}
			}

			@Override
			public Object getValue(DBUApp item) {
				return item.getCreated();
			}
		};
		public String getCondition() {
			return String.format("%s=?", name());
		}

		public void setParam(DBUApp item, ResultSet rs) throws SQLException {
			setValue(item, rs.getString(name()));
		}

		public abstract void setValue(DBUApp item, String value) throws SQLException;

		public Object getValue(DBUApp item) {
			return null;
		}

		public String getDescription() {
			return toString();
		}

	
	}

}
