package net.idea.restnet.c;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.idea.restnet.c.task.TaskStorage;
import net.idea.restnet.i.task.ICallableTask;
import net.idea.restnet.i.task.ITaskApplication;
import net.idea.restnet.i.task.ITaskResult;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.i.task.Task;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.Reference;
import org.restlet.resource.Finder;
import org.restlet.routing.Filter;
import org.restlet.routing.Route;
import org.restlet.routing.Router;
import org.restlet.util.RouteList;
import org.xml.sax.EntityResolver;

public class TaskApplication<USERID> extends Application implements ITaskApplication<USERID> {
	/**
	 * Properties specific to the application, loaded by config file
	 */
	private Properties properties = null;
	protected String configFile = "www.properties";
	protected EntityResolver resolver;

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

	@Override
	public synchronized Task<ITaskResult,USERID> addTask(String taskName, 
			ICallableTask callable, 
			Reference baseReference, USERID user) {
		return taskStorage.addTask(taskName,callable,baseReference,user);
	}
	public synchronized Task<ITaskResult,USERID> findTask(String id) {
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
	
	protected static final String insecure = "insecure";
   	protected boolean isInsecure() {
		try {
			boolean aa = Boolean.parseBoolean(getProperty(insecure));
			if ((getContext()!=null) && 
				(getContext().getParameters()!=null) && 
				(getContext().getParameters().getFirstValue(insecure))!=null)
				aa = Boolean.parseBoolean(getContext().getParameters().getFirstValue(insecure));
			return aa;
		} catch (Exception x) {
			x.printStackTrace();
		}
		return false;
	}   	

	/**
	 * Allow connections to SSL sites without certs (similar to curl -k )
	 * @throws Exception
	 */
	protected void insecureConfig()  {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[]{
		    new X509TrustManager() {
		        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		            return null;
		        }
		        public void checkClientTrusted(
		            java.security.cert.X509Certificate[] certs, String authType) {
		        }
		        public void checkServerTrusted(
		            java.security.cert.X509Certificate[] certs, String authType) {
		        }
		    }
		};

		// Install the all-trusting trust manager
		try {
		    SSLContext sc = SSLContext.getInstance("SSL");
		    sc.init(null, trustAllCerts, new java.security.SecureRandom());
		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
		}
		HttpsURLConnection.setDefaultHostnameVerifier( 
				new HostnameVerifier(){
					public boolean verify(String string,SSLSession ssls) {
						return true;
					}
				});
	}
	

	   public static String printRoutes(Restlet re,String delimiter,StringWriter b) {
		   		
		 		while (re != null) {
		 			
		 			b.append(re.getClass().toString());
		 			b.append('\t');
		 			if (re instanceof Finder) {
		 				b.append(((Finder)re).getTargetClass().getName());
		 				b.append('\n');
		 				re = null;
		 			} else if (re instanceof Filter)
			 			re = ((Filter)re).getNext();
			 		else if (re instanceof Router) {
			 			b.append('\n');
			 			RouteList list = ((Router)re).getRoutes();
			 		 	for (Route r : list) { 
			 		 		
			 		 		b.append(delimiter);
			 		 		b.append(r.getTemplate().getPattern());
			 		 		b.append('\t');
			 		 		b.append(r.getTemplate().getVariableNames().toString());
			 		 		printRoutes(r.getNext(),'\t'+delimiter+r.getTemplate().getPattern(),b);
			 		 	}	
			 		 	
			 			break;
			 		} else {
			 			break;
			 		}
			 		
			 		
		 		}

		 		return b.toString();

		 	}
	   
	   /**
	    * XML DTD schema 
	    * @return
	    */
		public EntityResolver getResolver() {
			return resolver;
		}

	   
}
