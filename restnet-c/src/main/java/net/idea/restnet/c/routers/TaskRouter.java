package net.idea.restnet.c.routers;

import net.idea.restnet.c.resource.TaskResource;

import org.restlet.Context;

/**
 * OpenTox tasks /task
 * 
 * @return
 */
public class TaskRouter extends MyRouter {
    public TaskRouter(Context context) {
	super(context);
	attachDefault(TaskResource.class);
	attach(TaskResource.resourceID, TaskResource.class);
    }
}
