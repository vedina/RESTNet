package net.idea.restnet.c.freemarker;

import net.idea.restnet.c.TaskApplication;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.ext.freemarker.ContextTemplateLoader;

import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;

public class FreeMarkerApplication<USERID> extends TaskApplication<USERID> implements IFreeMarkerApplication<Configuration> {
	   private Configuration configuration;
	   
		protected String versionShort = "";
		
		protected String profile = "default";

		public String getProfile() {
			return profile;
		}

		public void setProfile(String profile) {
			this.profile = profile;
		}
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

		protected String gaCode = null;

		
		public String getGACode() {
			return gaCode;
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
		
		@Override
		public boolean isEnableEmailVerification() {
			return true;
		}
		
		protected boolean changeLineSeparators = false;
		public boolean isChangeLineSeparators() {
			return changeLineSeparators;
		}

}
