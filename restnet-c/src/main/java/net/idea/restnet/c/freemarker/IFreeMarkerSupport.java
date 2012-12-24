package net.idea.restnet.c.freemarker;

import java.util.Map;

public interface IFreeMarkerSupport {
	public String getTemplateName();
	public boolean isHtmlbyTemplate();
	public void setHtmlbyTemplate(boolean htmlbyTemplate);
	public void configureTemplateMap(Map<String, Object> map);
}
