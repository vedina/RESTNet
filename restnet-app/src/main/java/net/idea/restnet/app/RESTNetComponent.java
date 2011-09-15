package net.idea.restnet.app;

import net.idea.restnet.c.RESTComponent;

import org.restlet.Application;
import org.restlet.Context;

/**
 * This is used as a servlet component instead of the core one, to be able to attach protocols 
 * @author nina
 *
 */
public class RESTNetComponent extends RESTComponent {
		public RESTNetComponent() {
			this(null);
		}
		public RESTNetComponent(Context context,Application[] applications) {
			super(context,applications);
			
		
		}
		public RESTNetComponent(Context context) {
			this(context,new Application[]{new RESTNetApplication()});
		}
		
		

}
