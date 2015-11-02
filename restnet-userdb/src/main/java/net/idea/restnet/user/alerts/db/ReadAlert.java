package net.idea.restnet.user.alerts.db;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.EQCondition;
import net.idea.modbcum.q.query.AbstractQuery;
import net.idea.restnet.b.Alert.RecurrenceFrequency;
import net.idea.restnet.c.exception.InvalidAlertException;
import net.idea.restnet.c.exception.InvalidUserException;
import net.idea.restnet.user.DBUser;

public class ReadAlert extends AbstractQuery<DBUser, DBAlert, EQCondition, DBAlert> implements IQueryRetrieval<DBAlert> {
    protected String sql = "select idquery,name,query,qformat,rfrequency,rinterval,iduser,created,username,sent from alert join user using(iduser) where username is not null ";
    protected Set<RecurrenceFrequency> frequency;

    public Set<RecurrenceFrequency> getFrequency() {
	return frequency;
    }

    public void setFrequency(Set<RecurrenceFrequency> frequency) {
	this.frequency = frequency;
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = 888018870900333768L;

    public ReadAlert(int id) {
	this(new DBAlert(id));
    }

    public ReadAlert(DBAlert alert) {
	super();
	setValue(alert);
    }

    @Override
    public String getSQL() throws AmbitException {
	StringBuilder b = new StringBuilder();
	b.append(sql);

	if (getFieldname() != null) {
	    if (getFieldname().getID() > 0) {
		b.append(" and ");
		b.append(DBAlert._fields.iduser.getCondition());
	    } else if (getFieldname().getUserName() != null) {
		b.append(" and username=? ");
	    } else
		throw new InvalidUserException();
	}
	if (getValue() != null) {
	    if (getValue().getID() <= 0)
		throw new InvalidAlertException();
	    b.append(" and ");
	    b.append(DBAlert._fields.idquery.getCondition());
	}
	String freqSQL = frequency == null ? null : getFrequencySQL(frequency);
	if (freqSQL != null)
	    b.append(String.format(" and (%s\n", freqSQL));
	b.append(" \norder by iduser");
	return b.toString();
    }

    public static String getFrequencySQL(Set<RecurrenceFrequency> frequency) {
	StringBuilder b = null;
	Iterator<RecurrenceFrequency> i = frequency.iterator();
	String d = "";
	while (i.hasNext()) {
	    if (b == null)
		b = new StringBuilder();
	    b.append(d);
	    b.append(String.format(" rfrequency='%s'", i.next().name()));
	    d = " or ";
	}
	if (b == null)
	    return null;
	b.append(")");
	return b.toString();
    }

    @Override
    public List<QueryParam> getParameters() throws AmbitException {
	List<QueryParam> params = new ArrayList<QueryParam>();

	if (getFieldname() != null) {
	    if (getFieldname().getID() > 0)
		params.add(new QueryParam<Integer>(Integer.class, getFieldname().getID()));
	    else if (getFieldname().getUserName() != null)
		params.add(new QueryParam<String>(String.class, getFieldname().getUserName()));
	    else
		throw new InvalidUserException();
	}
	if (getValue() != null) {
	    if (getValue().getID() <= 0)
		throw new InvalidAlertException();
	    params.add(new QueryParam<Integer>(Integer.class, getValue().getID()));
	}
	return params;
    }

    // new DBGroup(getValue().getGroupType());
    @Override
    public DBAlert getObject(ResultSet rs) throws AmbitException {
	try {
	    DBAlert alert = new DBAlert();
	    for (DBAlert._fields field : DBAlert._fields.values())
		field.setParam(alert, rs);

	    return alert;
	} catch (Exception x) {
	    throw new AmbitException(x);
	}
    }

    @Override
    public boolean isPrescreen() {
	return false;
    }

    @Override
    public double calculateMetric(DBAlert object) {
	return 1;
    }

}
