package net.idea.restnet.c.freemarker;

import java.util.Map;

import net.idea.restnet.i.freemarker.IFreeMarkerSupport;

public class FreeMarkerSupport implements IFreeMarkerSupport {
	
	protected boolean htmlbyTemplate = false;

	public String getTemplateName() {
		return null;
	}

	public boolean isHtmlbyTemplate() {
		return htmlbyTemplate;
	}

	public void setHtmlbyTemplate(boolean htmlbyTemplate) {
		this.htmlbyTemplate = htmlbyTemplate;
	}
	
	public void configureTemplateMap(Map<String, Object> map) {
		map.put("username",null);
        map.put("creator",getClass().getName());
	}

	
}
