package celldesignerparse_4_0.specie;

import org.sbml.libsbml.XMLNode;

public class Catalyzed {
	private String reaction;
	
	public Catalyzed(XMLNode node) {
		reaction = node.getAttributes().getValue("reaction");
	}
	
	public String toString(){
		return "catalyzed: reaction: "+reaction;
	}
}
