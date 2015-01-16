package net.idea.restnet.i.task;

import org.restlet.data.Reference;

public interface ITaskApplication<USERID> {
    ITask<ITaskResult, USERID> addTask(String taskName, ICallableTask callable, Reference baseReference, USERID user);

    // see the internal flag in ITask
    ITask<ITaskResult, USERID> addTask(String taskName, ICallableTask callable, Reference baseReference,
	    boolean internal, USERID user);

    ITaskStorage<USERID> getTaskStorage();

    ITask<ITaskResult, USERID> findTask(String id);
}
