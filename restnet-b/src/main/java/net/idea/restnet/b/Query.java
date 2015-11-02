package net.idea.restnet.b;

public class Query  {

	public enum QueryType {FREETEXT, SPARQL};
	protected QueryType type;
	protected String content;
	
	public Query() {
		this(null);
	}
	public Query(String content) {
		this(content,QueryType.FREETEXT);
	}
	public Query(String content, QueryType type) {
		setContent(content);
		setType(type);
	}
	public QueryType getType() {
		return type;
	}
	public void setType(QueryType type) {
		this.type = type;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
