package net.idea.restnet.c.html;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

import net.idea.restnet.c.AbstractResource;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.resource.TaskResource;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Reference;

public class HTMLBeauty {
	protected String searchURI = null;
	protected String searchTitle = "Search";
	protected String supportEmail = "support@email.com";
	protected String supportName = "Support";
	protected String developedBy = "";
	protected String logoLeft = "logoLeft.png";
	protected String logoHeader = null;
	protected String logoRight =  "logoRight.png";

	private boolean isMsie7;

	//parameters - TODO - refactor as a separate class
	protected String searchQuery;
	protected int page;
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}

	protected long pageSize;
	
	public long getPageSize() {
		return pageSize;
	}
	public void setPageSize(long pageSize) {
		this.pageSize = pageSize;
	}

	
	public HTMLBeauty() {
		this(null);

	};
	public HTMLBeauty(String searchURI) {
		super();
		setSearchURI(searchURI==null?"/":searchURI);
	};
	
	public String getSearchURI() {
		return searchURI;
	}
	public void setSearchURI(String searchURI) {
		this.searchURI = searchURI;
	}
	public String getSearchTitle() {
		return searchTitle;
	}
	public void setSearchTitle(String searchTitle) {
		this.searchTitle = searchTitle;
	}
	public String getSupportEmail() {
		return supportEmail;
	}
	public void setSupportEmail(String supportEmail) {
		this.supportEmail = supportEmail;
	}
	public String getSupportName() {
		return supportName;
	}
	public void setSupportName(String supportName) {
		this.supportName = supportName;
	}
	public String getDevelopedBy() {
		return developedBy;
	}
	public void setDevelopedBy(String developedBy) {
		this.developedBy = developedBy;
	}
	public String getLogoLeft() {
		return logoLeft;
	}
	public void setLogoLeft(String logoLeft) {
		this.logoLeft = logoLeft;
	}
	public String getLogoHeader() {
		return logoHeader;
	}
	public void setLogoHeader(String logoHeader) {
		this.logoHeader = logoHeader;
	}
	public String getLogoRight() {
		return logoRight;
	}
	public void setLogoRight(String logoRight) {
		this.logoRight = logoRight;
	}
	
	
	public boolean isMsie7() {
		return isMsie7;
	}
	protected void setMsie7(boolean isMsie7) {
		this.isMsie7 = isMsie7;
	}

	protected static String jsGoogleAnalytics = null;
	
	public void writeHTMLHeader(Writer w,String title,Request request,ResourceDoc doc) throws IOException {
		writeHTMLHeader(w, title, request,"",doc);
	}

	public void writeHTMLHeader(Writer w,String title,Request request,String meta,ResourceDoc doc) throws IOException {

		writeTopHeader(w, title, request, meta,doc);
		writeSearchForm(w, title, request, meta,null);
		w.write("<div id='content'>\n");
	}

	public String jsGoogleAnalytics() {
		if (jsGoogleAnalytics==null) try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					getClass().getClassLoader().getResourceAsStream("net/idea/restnet/config/googleanalytics.js"))
			);
			StringBuilder b = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
            	b.append(line);
            	b.append('\n');
            }
            jsGoogleAnalytics = b.toString();
			reader.close();
			
		} catch (Exception x) { jsGoogleAnalytics = null;}
		return jsGoogleAnalytics;
	}
	
	public void writeTopHeader(Writer w,String title,Request request,String meta,ResourceDoc doc) throws IOException {
		Reference baseReference = request==null?null:request.getRootRef();
		
		// Determine if the request is made by Microsoft Internet Explorer 7, as many elements on the page break on it.
		if (request!=null) {
			isMsie7 = request.getClientInfo().getAgent().toLowerCase().indexOf("msie 7.")>=0?true:false;
		} else {
			isMsie7 = false;
		}
		
		w.write(
				"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n"
				);
		
		w.write(String.format("<html %s %s %s>",
				"xmlns=\"http://www.w3.org/1999/xhtml\"",
				"xmlns:dc=\"http://purl.org/dc/elements/1.1/\"",
				"xmlns:ot=\"http://opentox.org/api/1.1/\"")
				);
		
		w.write(String.format("<head> <meta property=\"dc:creator\" content=\"%s\"/> <meta property=\"dc:title\" content=\"%s\"/>",
				request.getResourceRef(),
				title
				));
		
		Reference ref = request.getResourceRef().clone();
		ref.addQueryParameter("media", Reference.encode("application/rdf+xml"));
		w.write(String.format("<link rel=\"meta\" type=\"application/rdf+xml\" title=\"%s\" href=\"%s\"/>\n",
				title,
				ref
				)); 
		w.write(String.format("<link rel=\"meta\" type=\"text/n3\" title=\"%s\" href=\"%s\"/>\n",
				title,
				ref
				)); 
		
		w.write(String.format("<link rel=\"primarytopic\" type=\"application/rdf+xml\" href=\"%s\"/>",
				ref
				)); 		
		w.write(String.format("<link rel=\"primarytopic\" type=\"text/n3\" href=\"%s\"/>",
				ref
				)); 			
		w.write(String.format("<title>%s</title>\n",title));
		
		w.write(String.format("<script type=\"text/javascript\" src=\"%s/jquery/jquery-1.7.1.min.js\"></script>\n",baseReference));
		w.write(String.format("<script type=\"text/javascript\" src=\"%s/jquery/jquery-ui-1.8.18.custom.min.js\"></script>\n",baseReference));
		w.write(String.format("<script type=\"text/javascript\" src=\"%s/jquery/jquery.MultiFile.pack.js\"></script>\n",baseReference));
		
		// Google +1 button rendering code (the button is placed elsewhere)
		w.write("<script type='text/javascript'>" +
				"(function() {" +
				"var po = document.createElement('script'); po.type = 'text/javascript'; po.async = true;" +
				"po.src = 'https://apis.google.com/js/plusone.js';" +
				"var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(po, s);" +
				"})();" +
				"</script>"
		);

		// Facebook JavaScript SDK
		w.write("<div id=\"fb-root\"></div>\n" +
				"<script>(function(d, s, id) {\n" +
				"var js, fjs = d.getElementsByTagName(s)[0];\n" +
				"if (d.getElementById(id)) return;\n" +
				"js = d.createElement(s); js.id = id;\n" +
				"js.src = \"//connect.facebook.net/en_GB/all.js#xfbml=1\";\n" +
				"fjs.parentNode.insertBefore(js, fjs);\n" +
				"}(document, 'script', 'facebook-jssdk'));</script>\n"
		);
		
		// Twitter JS
		w.write("<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id))" +
				"{js=d.createElement(s);js.id=id;js.src=\"//platform.twitter.com/widgets.js\";" +
				"fjs.parentNode.insertBefore(js,fjs);}}(document,\"script\",\"twitter-wjs\");</script>\n"
		);

		// LinkedIn
		w.write("<script src='http://platform.linkedin.com/in.js' type='text/javascript'></script>\n");
		
		//w.write(String.format("<script type=\"text/javascript\" src=\"%s/jquery/jquery.tablesorter.min.js\"></script>\n",baseReference));
				
		w.write(String.format("<link href=\"%s/style/ambit.css\" rel=\"stylesheet\" type=\"text/css\">\n",baseReference));
		w.write(String.format("<link href=\"%s/style/jquery-ui-1.8.18.custom.css\" rel=\"stylesheet\" type=\"text/css\">\n",baseReference));
		
		w.write("<meta name=\"robots\" content=\"index,follow\"><META NAME=\"GOOGLEBOT\" CONTENT=\"index,FOLLOW\">\n");
		w.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n");
		w.write(String.format("<script type=\"text/javascript\" src=\"%s/jme/jme.js\"></script>\n",baseReference));
		w.write("<script>$(function() {$( \".accordion\" ).accordion({autoHeight: false,navigation: true});});</script>");
		// Don't style the submit button with jQ if the browser is MSIE 7.
		if (!isMsie7) w.write("<script>$(function() {$(\"#submit\").button();});</script>");
		//w.write("<script>$(function() {$( \".tabs\" ).tabs({event: \"mouseover\",cache: true, ajaxOptions: {error: function( xhr, status, index, anchor ) {$( anchor.hash ).html(status );}}});});</script>");
		w.write("<script>$(function() {$( \".tabs\" ).tabs({cache: true});});</script>");
		w.write("<script>$(function() {$( \"#selectable\" ).selectable();});</script>");
		w.write("<script type='text/javascript'>function hideDiv(divId) {\n$('#'+divId).hide();}</script>\n");
		w.write("<script type='text/javascript'>function toggleDiv(divId) {\n" +
				"$('#'+divId).toggle();\n" +
				"if ($('#'+divId+'_toggler').hasClass('togglerPlus')) {\n" +
				"$('#'+divId+'_toggler').removeClass('togglerPlus');\n" +
				"$('#'+divId+'_toggler').addClass('togglerMinus');\n" +
				"} else if ($('#'+divId+'_toggler').hasClass('togglerMinus')) {\n" +
				"$('#'+divId+'_toggler').removeClass('togglerMinus');\n" +
				"$('#'+divId+'_toggler').addClass('togglerPlus');\n" +
				"}\n" +
				"}</script>\n"
		);
		w.write(meta);
		w.write("</head>\n");
		w.write("<body>");
		w.write(String.format("<link rel=\"stylesheet\" href=\"%s/style/tablesorter.css\" type=\"text/css\" media=\"screen\" title=\"Flora (Default)\">\n",baseReference));
		w.write("\n");
		
		StringBuilder header = new StringBuilder();
		header.append(String.format("<a href='#'><img class='logo_top-left' src='%s/images/%s' alt='%s'></a>\n",
				baseReference,logoLeft,logoLeft));
		if (logoHeader!=null)
			header.append(String.format("<div class='logo_header'><img src='%s/images/%s' alt='%s' title='%s'></div>",
					baseReference,getTitle(),getTitle()));
		else
			header.append(String.format("<div class='logo_header'>%s</div>",
					baseReference,getTitle()));
		if (logoRight!=null)
		header.append(String.format("<a href='#'><img class='logo_top-right' src='%s/images/%s' alt='%s'></a>\n",
				baseReference,logoRight,logoRight));
		
		
		w.write(String.format("<div id='wrap'><div id='header'>%s</div>\n",header));
		w.write(
				"<div id='inner-wrap'>\n" +
				"\t<div id='left'>\n");
		

		w.write(
				"\t\t<div id='menu'>\n" +
				"\t\t\t<ul id='navmenu'>\n");
				
		try{
			w.write(menuItems(baseReference));
		} catch (Exception x) {
			x.printStackTrace();
		}
		w.write(
				"\t\t\t</ul>\n" +
				"\t\t</div>\n"); // div id='menu'
	
		// Apply style for the hovered buttons sans (!) the currently selected one.
		// There are better ways to do it, but this should be okay for now.
		// However, this breaks MSIE 7. Moreover, this browser gets crazy even if
		// the change is implemented purely with simple CSS a:hover, and for this
		// reason, we simply disable the mousever effect for it.
		if (!isMsie7) {
			w.write(String.format(
					"<script>\n" +

					"$('a.selectable').mouseover(function () { $(this).addClass('hovered');    } );\n" +
					"$('a.selectable').mouseout(function  () { $(this).removeClass('hovered'); } );\n" +

					"</script>\n"
			));
		}
			
		//followed by the search form
		
		/*
		left = "";
		middle = "";
		right = String.format("<a href='%s/%s'>%s</a>",
				baseReference.toString(),
				getLoginLink(),
				request.getClientInfo().getUser()==null?"Login":"My account");
		writeDiv3(w, left, middle, right);
		*/

	}
	
	public String menuItems(Reference baseReference) {
		StringBuilder b = new StringBuilder();
		b.append(printMenuItem("/openssouser","Login", baseReference.toString(),"10","Login"));
		b.append(printMenuItem("/admin","Admin", baseReference.toString(),"10","Jobs list"));
		b.append(printMenuItem("/sparql","SPARQL", baseReference.toString(),"10","SPARQL"));
		
		b.append(printMenuItem(TaskResource.resource,"Tasks", baseReference.toString(),"10","Jobs list"));
		return b.toString();
	}
	public void writeTopLinks(Writer w,String title,Request request,String meta,ResourceDoc doc, Reference baseReference) throws IOException {
		w.write(String.format("<a href='%s%s'>Tasks</a>&nbsp;",TaskResource.resource,baseReference));
	}
	

	protected Form getParams(Form params,Request request) {
		if (params == null) 
			if (Method.GET.equals(request.getMethod()))
				params = request.getResourceRef().getQueryAsForm();
			//if POST, the form should be already initialized
			else 
				params = request.getEntityAsForm();
		return params;
	}

	public void writeSearchForm(Writer w,String title,Request request ,String meta,Method method) throws IOException {
		writeSearchForm(w, title, request, meta, method,null);
	}
	public void writeSearchForm(Writer w,String title,Request request ,String meta,Method method,Form params) throws IOException {

		Reference baseReference = request.getRootRef();
		try {
			w.write(searchMenu(baseReference,getParams(params,request)));
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			w.write("</div>\n");
		}
	}
	protected String getLogoURI(String root) {
		return String.format("%s/images/ambit-logo.png",root==null?"":root);
	}

	protected String getHomeURI() {
		return "/";
	}

	protected String searchMenu(Reference baseReference,Form form)  {
		String pageSize = "10";
		String structure = null;
		try {
			if ((form != null) && (form.size()>0)) {
				searchQuery = form.getFirstValue(AbstractResource.search_param)==null?"":form.getFirstValue(AbstractResource.search_param);
				pageSize = form.getFirstValue("pagesize")==null?"10":form.getFirstValue("pagesize");
				structure = form.getFirstValue("structure");
			}
		} catch (Exception x) {
			searchQuery = "";
			pageSize = "10";
		}
		String hint = "";
		
			return
		   String.format(		
		   "<div class='search ui-widget'>\n"+
		   "<p title='%s'>%s</p>\n"+
		   "<form method='GET' action='%s%s?pagesize=10'>\n"+
		   "<table width='200px'>\n"+
		   "<tr><td colspan='2'><input type='text' name='search' size='20' value='%s' tabindex='0' title='Enter search query'></td></tr>\n"+
		   "<tr><td>Number of hits</td><td align='left'><input type='text' size='3' name='pagesize' value='%s'></td></tr>\n"+
		   "<tr><td colspan='2' align='center'><input type='submit' id='submit' tabindex='4'  value='Search'/></td></tr>\n"+
		   "</table>\n"+			   
		   "</form> \n"+
		   "</div>\n",
		   hint,
		   getSearchTitle(),
		   baseReference,
		   getSearchURI(),
		   searchQuery==null?"":searchQuery,
		   pageSize
		   );
	}

	public void writeHTMLFooter(Writer output,String title,Request request) throws IOException {
		//div ui-widget
		output.write("\n</div>\n"); 
		//div id=content
		output.write("\n</div>\n"); 
		//div inner-wrap
		output.write("\n</div>\n");
		// Push the footer downwards, so that we don't accidentally step on it.
		output.write("\n<div class='pusher'></div>");
		//div id=wrap
		output.write("\n</div>\n"); 
		//footer
		
		output.write(String.format(
					"\n<div id='footer'>\n" +
					"<table class='footerTable'>\n<tr>\n" +
					"<td>\n" +
					"<a class='email' href='mailto:%s'>%s</a>\n" +
					"</td>\n" +
					"<td style='text-align: right;'>\n" +
					"Developed by %s\n" +
					"</td>\n" +
					"</tr>\n</table>\n" +
					"</div>\n",
					supportEmail,supportName,developedBy)
		);
		output.write(jsGoogleAnalytics()==null?"":jsGoogleAnalytics());
		output.write("\n</body>");
		output.write("</html>");

	}
	public String getLoginLink() {
		return "opentoxuser";
	}
	public String getTitle() {
		return "RESTNet";
	}
	
	
	public String printWidgetHeader(String header) {
		return	String.format(
				"<div class=\"ui-widget \" style=\"margin-top: 20px; padding: 0 .7em;\">\n"+
				"<div class=\"ui-widget-header ui-corner-top\"><p>%s</p></div>\n",header);
	}
	public String printWidgetFooter() {
		return	String.format("</div>\n");
	}
	public String printWidgetContentHeader(String style) {
		return	String.format("<div class=\"ui-widget-content ui-corner-bottom %s\">\n",style);
	}
	public String printWidgetContentFooter() {
		return	String.format("</div>\n");
	}	
	public String printWidgetContentContent(String content) {
		return
		String.format("<p>%s</p>\n",content);
	}	
	public String printWidgetContent(String content,String style) {
		return String.format("%s\n%s\n%s",
				printWidgetContentHeader(style),
				printWidgetContentContent(content),
				printWidgetContentFooter());
	}
	
	
	public String printWidget(String header,String content,String style) {
		return String.format("%s\n%s\n%s",
				printWidgetHeader(header),
				printWidgetContent(content,style),
				printWidgetFooter());

	}
	
	public String printWidget(String header,String content) {
		return String.format("%s\n%s\n%s",
				printWidgetHeader(header),
				printWidgetContent(content,""),
				printWidgetFooter());

	}	
	
	protected String printMenuItem(String relativeURI,String title,String baseReference,String pagesize) {
		return printMenuItem(relativeURI, title, baseReference, pagesize,"");
	}
	protected String printMenuItem(String relativeURI,String title,String baseReference,String pagesize,String hint) {
		return String.format("\t\t\t\t<li><a class='%s' title='%s' href='%s%s%s%s'>%s</a></li>\n",
				(getSearchURI()!=null) && getSearchURI().equals(relativeURI)?"selected":"selectable",
				hint==null?title:hint,
				baseReference,relativeURI,
				pagesize==null?"":"?pagesize=",
				pagesize==null?"":pagesize,
				title);
	}
	
	public String getPaging(int page, int start, int last, long pageSize) {

		// Having a constant number of pages display on top is convenient for the users and provides more consistent
		// overall look. But this would require the function to define different input parameters. In order to not
		// break it, implement a workaround, by calculating how many pages the caller (likely) intended to be shown.
		int total = last - start;

		// Normalization
		start = start<0?0:start; // don't go beyond first page
		last = start + total;

		String search = searchQuery==null?"":Reference.encode(searchQuery);

		String url = "<li><a class='%s' href='?page=%d&pagesize=%d&search=%s'>%s</a></li>";

		StringBuilder b = new StringBuilder(); 
		b.append("<div><ul id='hnavlist'>");

		// Disable this for the time being as it seems to not fit well into the overall look.
		//b.append(String.format("<li id='pagerPages'>Pages</li>"));

		// Display "first" and "previous" for the first page as inactive.
		if (page > 0) {
			b.append(String.format(url, "pselectable", 0, pageSize, search,  "&lt;&lt;"));
			b.append(String.format(url, "pselectable", page-1, pageSize, search,  "&lt;"));
		} else {
			b.append(String.format("<li class='inactive'>&lt;&lt;</li>"));
			b.append(String.format("<li class='inactive'>&lt;</li>"));
		}

		// Display links to pages. Pages are counted from zero! Hence why we display "i+1".
		for (int i=start; i<= last; i++)
			b.append(String.format(url, i==page?"current":"pselectable", i, pageSize, search,  i+1)); 
		b.append(String.format(url, "pselectable", page+1, pageSize, search,  "&gt;"));
		b.append("</ul></div><br>");

		// Apply style for the hovered buttons sans (!) the currently selected one.
		// There are better ways to do it, but this should be okay for now.
		// However, this breaks MSIE 7. Moreover, this browser gets crazy even if
		// the change is implemented purely with simple CSS a:hover, and for this
		// reason, we simply disable the mousever effect for it.
		if (!isMsie7()) {
			b.append(String.format(
					"<script>\n" +

					"$('a.pselectable').mouseover(function () { $(this).addClass('phovered');    } );\n" +
					"$('a.pselectable').mouseout(function  () { $(this).removeClass('phovered'); } );\n" +

					"</script>\n"
			));
		}

		return b.toString();
	}
}
