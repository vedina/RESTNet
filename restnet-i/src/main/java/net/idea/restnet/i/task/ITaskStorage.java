package net.idea.restnet.i.task;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.restlet.data.Reference;

public interface ITaskStorage<USERID> {
	Iterator<UUID> getTasks();
	void removeTasks();
	Task<ITaskResult,USERID> addTask(String taskName, ICallableTask callable, Reference baseReference,USERID user);
	Task<ITaskResult,USERID> findTask(String id);
	Task<ITaskResult,USERID> findTask(UUID id);
	void shutdown(long timeout,TimeUnit unit) throws Exception;
}
