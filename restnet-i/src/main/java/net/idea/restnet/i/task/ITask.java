package net.idea.restnet.i.task;

import java.util.UUID;

public interface ITask<REFERENCE,USERID> {
	void update();
	boolean isDone();
	UUID getUuid();
}
