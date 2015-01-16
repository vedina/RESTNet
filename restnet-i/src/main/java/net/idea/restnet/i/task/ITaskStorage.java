package net.idea.restnet.i.task;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.restlet.data.Reference;

public interface ITaskStorage<USERID> {
    Iterator<UUID> getTasks();

    void removeTasks();

    ITask<ITaskResult, USERID> addTask(String taskName, ICallableTask callable, Reference baseReference, USERID user,
	    boolean internal);

    ITask<ITaskResult, USERID> findTask(String id);

    ITask<ITaskResult, USERID> findTask(UUID id);

    void shutdown(long timeout, TimeUnit unit) throws Exception;

    Iterator<ITask<Reference, USERID>> filterTasks();
}
