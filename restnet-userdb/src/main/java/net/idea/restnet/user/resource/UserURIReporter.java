package net.idea.restnet.user.resource;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.resources.Resources;
import net.idea.restnet.user.DBUser;

import org.restlet.Request;

/**
 * Generates URI for {@link ReferenceResource}
 * @author nina
 *
 * @param <Q>
 */
public class UserURIReporter <Q extends IQueryRetrieval<DBUser>> extends QueryURIReporter<DBUser, Q> {
	String suffix = "";
	

	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8868430033131766579L;
	public UserURIReporter(Request baseRef,String suffix) {
		super(baseRef,null);	
		this.suffix = suffix;
	}
	public UserURIReporter(Request baseRef) {
		this(baseRef,"");
	}
	public UserURIReporter() {
		this(null);
	}	

	@Override
	public String getURI(String ref, DBUser item) {
		StringBuilder b = new StringBuilder();
		b.append(ref);
		b.append(Resources.user);
		b.append("/U");
		b.append(Integer.toString(item.getID()));
		b.append(suffix);
		return b.toString();
		
		//return String.format("%s%s/U%d%s",ref,Resources.user,item.getID(),suffix);
	}

}
