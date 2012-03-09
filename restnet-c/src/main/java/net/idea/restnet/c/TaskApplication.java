package net.idea.restnet.c;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import net.idea.restnet.c.task.TaskStorage;
import net.idea.restnet.i.task.ICallableTask;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.i.task.Task;
import net.idea.restnet.i.task.TaskResult;

import org.restlet.Application;
import org.restlet.data.Reference;

public class TaskApplication<USERID> extends Application {
	/**
	 * Properties specific to the application, loaded by config file
	 */
	private Properties properties = null;
	protected String configFile = "www.properties";

	protected ITaskStorage<USERID> taskStorage;
	public TaskApplication() {
		super();
		taskStorage = createTaskStorage();
	}
	
	protected TaskStorage<USERID> createTaskStorage() {
		return new TaskStorage<USERID>(getName(),getLogger());
	}
	
	public ITaskStorage<USERID> getTaskStorage() {
		return taskStorage;
	}

	public void setTaskStorage(ITaskStorage<USERID> taskStorage) {
		this.taskStorage = taskStorage;
	}
	/*
	public Iterator<Task<Reference,USERID>> getTasks() {
		return taskStorage.getTasks();
	}
	*/

	@Override
	protected void finalize() throws Throwable {
		taskStorage.removeTasks();
		super.finalize();
	}
	@Override
	public synchronized void stop() throws Exception {
		taskStorage.shutdown(1,TimeUnit.MILLISECONDS);
		super.stop();
	}
	
	
	public void removeTasks() {
		taskStorage.removeTasks();
	}

	public synchronized Task<TaskResult,USERID> addTask(String taskName, 
			ICallableTask callable, 
			Reference baseReference, USERID user) {
		return taskStorage.addTask(taskName,callable,baseReference,user);
	}
	public synchronized Task<TaskResult,USERID> findTask(String id) {
		return taskStorage.findTask(id);
	}
	/*
	@Override
	public ApplicationInfo getApplicationInfo(Request request, Response response) {
	        ApplicationInfo result = super.getApplicationInfo(request, response);

	        DocumentationInfo docInfo = new DocumentationInfo(
	                "TaskApplication");
	        docInfo.setTitle("Task application");
	        result.setDocumentation(docInfo);

	        return result;
    }
    */
	protected synchronized void loadProperties()  {
		try {
		if (properties == null) {
			properties = new Properties();
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(configFile);
			properties.load(in);
			in.close();		
		}
		} catch (Exception x) {
			properties = null;
		}
	}
	
	public synchronized String getProperty(String key) {
		try {
			if (properties==null) loadProperties();
			return properties.getProperty(key);  	
		} catch (Exception x) {
			return null;
		}
	}
	
	
	public String getConfigFile() {
		return configFile;
	}
	/**
	 * Config file to load properties
	 * @param configFile
	 */
	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}
}
