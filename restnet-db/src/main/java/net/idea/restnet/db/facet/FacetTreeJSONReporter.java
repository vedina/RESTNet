package net.idea.restnet.db.facet;

import java.io.Writer;
import java.util.Iterator;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.i.facet.IFacet;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * modelled after flare.json
 * 
 * @author nina
 * 
 * @param <Q>
 */
public class FacetTreeJSONReporter<Q extends IQueryRetrieval<IFacet>> extends
		QueryReporter<IFacet, Q, Writer> {
	protected QueryURIReporter<IFacet, IQueryRetrieval<IFacet>> uriReporter;
	protected String jsonp = null;
	protected ObjectMapper mapper;
	protected ObjectNode rootNode;
	protected ArrayNode arrayNode;

	public FacetTreeJSONReporter(Request baseRef) {
		this(baseRef, null);
	}

	public FacetTreeJSONReporter(Request baseRef, String jsonp) {
		super();
		uriReporter = new FacetURIReporter<IQueryRetrieval<IFacet>>(baseRef);
		this.jsonp = jsonp;
		mapper = new ObjectMapper();

	}

	@Override
	public void open() throws DbAmbitException {

	}

	@Override
	public void header(Writer output, Q query) {
		rootNode = createNode(query.toString(), 1);
		arrayNode = mapper.createArrayNode();
		rootNode.put("children", arrayNode);
	}

	protected ObjectNode createNode(String name, int size) {
		ObjectNode node = (ObjectNode) mapper.createObjectNode();
		node.put("name", name);
		node.put("size", size);
		return node;
	}

	@Override
	public void footer(Writer output, Q query) {
		try {
			output.write(mapper.writerWithDefaultPrettyPrinter()
					.writeValueAsString(rootNode));

		} catch (Exception x) {

		}
	}

	@Override
	public Object processItem(IFacet item) throws Exception {
		try {
			JsonNode categoryNode = null;
			ArrayNode catArrayNode = arrayNode;
			if (item.getSubcategoryTitle() != null) {
				Iterator<JsonNode> i = arrayNode.iterator(); 
				while (i.hasNext()) {
					JsonNode node = i.next();
					if (item.getSubcategoryTitle().equals(node.get("name").asText())) {
						categoryNode = (ObjectNode) node;
						((ObjectNode)categoryNode).put("size", categoryNode.get("size").asInt()+item.getCount());
						break;
					}
				}
				if (categoryNode == null) {
					categoryNode = createNode(item.getSubcategoryTitle(), 0);
					((ObjectNode) categoryNode).put("children",
							mapper.createArrayNode());
					arrayNode.add(categoryNode);
				}
			}
			if (categoryNode != null)
				catArrayNode = (ArrayNode) categoryNode.get("children");

			ObjectNode node = createNode(item.getValue().toString(),
					item.getCount());
			rootNode.put("size", rootNode.get("size").asInt()+item.getCount());
			catArrayNode.add(node);
			/*
			 * String subcategory = null; if ((uriReporter != null) &&
			 * (uriReporter.getBaseReference() != null)) subcategory =
			 * uriReporter.getBaseReference().toString(); output.write(String
			 * .format(
			 * "\n\t{\n\t\"value\":\"%s\",\n\t\"count\":%d,\n\t\"uri\":\"%s\",\n\t\"subcategory\":%s%s%s\n\t}"
			 * , item.getValue(), item.getCount(), uriReporter .getURI(item),
			 * subcategory == null ? "" : "\"",
			 * item.getSubCategoryURL(subcategory), subcategory == null ? "" :
			 * "\""));
			 */
		} catch (Exception x) {
			x.printStackTrace();
		}
		return item;
	}
}