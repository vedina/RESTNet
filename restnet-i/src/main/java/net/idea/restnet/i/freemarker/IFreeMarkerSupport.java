package net.idea.restnet.i.freemarker;

import java.util.Map;

import org.restlet.Request;

public interface IFreeMarkerSupport {
	public String getTemplateName();
	public boolean isHtmlbyTemplate();
	public void setHtmlbyTemplate(boolean htmlbyTemplate);
	public void configureTemplateMap(Map<String, Object> map, Request request, IFreeMarkerApplication app);
}
