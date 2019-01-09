package net.idea.restnet.user.app.db;

import net.idea.modbcum.q.update.AbstractUpdate;

public abstract class AbstractAppUpdate<T> extends AbstractUpdate<T, DBUApp> {
	public AbstractAppUpdate(DBUApp target) {
		setObject(target);
	}
}
