package celldesignerparse_4_0.commondata;

import org.sbml.libsbml.XMLNode;

public class Entity {
	private String id;
	private String name;
	private String type;

	public Entity(XMLNode node) {
		id = node.getAttributes().getValue("id");
		name = node.getAttributes().getValue("name");
		type = node.getAttributes().getValue("type");
	}
	
	public String toString(){
		return "Entity: "+id+" "+name+" "+type+"\n";
	}

	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
}
