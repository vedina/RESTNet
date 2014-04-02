package net.idea.restnet.user.resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.idea.restnet.c.html.HTMLBeauty;
import net.idea.restnet.c.resource.CatalogResource;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;
import net.idea.restnet.i.task.ICallableTask;
import net.idea.restnet.resources.Resources;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

public abstract class RegistrationNotifyResource  extends CatalogResource<String> {
	protected List<String> dummyuser = new ArrayList<String>();
	
	public RegistrationNotifyResource() {
		super();
		setHtmlbyTemplate(true);
		dummyuser.add(String.format("%s%s",Resources.register,Resources.notify));
	}
	
	@Override
	protected Iterator<String> createQuery(Context context, Request request,
			Response response) throws ResourceException {
		return dummyuser.iterator();
	}

	@Override
	public String getTemplateName() {
		return "register_notify.ftl";
	}

	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
	
		map.put("searchURI",Resources.register);
		map.put("managerRole", "false");
		map.put("editorRole", "false");
		if (getClientInfo()!=null) {
			if (getClientInfo().getUser()!=null)
				map.put("username", getClientInfo().getUser().getIdentifier());
		}
		map.put("creator","Ideaconsult Ltd.");
    
	}
	
	@Override
	protected Reference getSourceReference(Form form, String model)
			throws ResourceException {
		return null;
	}
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	@Override
	protected Representation put(Representation entity, Variant variant)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	@Override
	protected ICallableTask createCallable(Method method, Form form, String item)
			throws ResourceException {
		return null;
	}
	
	public abstract String getConfigFile() ;
	
	@Override
	protected HTMLBeauty getHTMLBeauty() {
		return null;
	}
	
	
}
