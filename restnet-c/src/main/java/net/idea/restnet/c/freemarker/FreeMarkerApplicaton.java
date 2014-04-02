package net.idea.restnet.c.freemarker;

import net.idea.restnet.c.TaskApplication;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.ext.freemarker.ContextTemplateLoader;

import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;

public class FreeMarkerApplicaton<USERID> extends TaskApplication<USERID> implements IFreeMarkerApplication {
	   private Configuration configuration;
	   
		protected String versionShort = "";
		@Override
		public String getVersionShort() {
			return versionShort;
		}

		public void setVersionShort(String versionShort) {
			this.versionShort = versionShort;
		}
		protected String versionLong = "";

	    @Override
		public String getVersionLong() {
			return versionLong;
		}

		public void setVersionLong(String versionLong) {
			this.versionLong = versionLong;
		}

	    
		public Configuration getConfiguration() {
			return configuration;
		}

		public void setConfiguration(Configuration configuration) {
			this.configuration = configuration;
		}
		
		protected void initFreeMarkerConfiguration() {
			configuration = new Configuration();
			
	        ContextTemplateLoader templatesLoader = new ContextTemplateLoader(getContext(),"war:///WEB-INF/templates/");
	        TemplateLoader[] loaders = new TemplateLoader[] { templatesLoader};
	        MultiTemplateLoader mtl = new MultiTemplateLoader(loaders);
	        configuration.setTemplateLoader(mtl);
	        configuration.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER); 
		}
}
