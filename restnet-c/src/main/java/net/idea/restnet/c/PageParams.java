package net.idea.restnet.c;

import org.restlet.data.Form;

public class PageParams {

	
	public enum params  {
		source_uri {
			public String getDescription() {
				return "either use ?source_uri=URI, or POST with text/uri-list or RDF representation of the object to be created";
			}
		},
		delay {
			public boolean isMandatory() {
				return false;
			}
			public String getDescription() {
				return "Delay,ms for /algorithm/mockup";
			}			
		},		
		error {
			public boolean isMandatory() {
				return false;
			}
			public String getDescription() {
				return "Error to be thrown by /algorithm/mockup";
			}			
		},			
		resulturi {
			public boolean isMandatory() {
				return false;
			}
			public String getDescription() {
				return "Result URI for /algorithm/mockup";
			}			
		},	
		page {
			public String getDescription() {
				return "Page";
			}
		},
		pagesize {
			public String getDescription() {
				return "Page size";
			}
		};
		public boolean isMandatory() {
			return true;
		}
		public String getDescription() {
			return "either use ?source_uri=URI, or POST with text/uri-list or RDF representation of the object to be created";
		}
		public Object getFirstValue(Form form) {
			return form.getFirstValue(toString());
		}
		public String[] getValuesArray(Form form) {
			return form.getValuesArray(toString());
		}
	};
	
	
	private PageParams() {
	}
	
	
	
}
