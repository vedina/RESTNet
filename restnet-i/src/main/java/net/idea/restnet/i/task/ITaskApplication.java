package net.idea.restnet.i.task;

import org.restlet.data.Reference;

public interface ITaskApplication<USERID> {
	ITask<ITaskResult,USERID> addTask(String taskName, ICallableTask callable, Reference baseReference, USERID user);

}
