package celldesignerparse_4_0.reaction;

import org.sbml.libsbml.XMLNode;

public class Product {
	protected String species;
	protected String alias;
	protected String linkAnchor;
	
	public Product(XMLNode node){
		species = node.getAttributes().getValue("species");
		if (species == null || species.equals("") ) {
			// Compatibility mode
			species = node.getCharacters();
		}		
		alias = node.getAttributes().getValue("alias");
		XMLNode linkAnchorNode = node.getChild("linkAnchor");	
		
		if (linkAnchorNode != null) {
			linkAnchor = linkAnchorNode.getAttributes().getValue("position");
		} else {
			linkAnchor = null;
		}		
	}
	
	public String getLinkAnchor() {
		return linkAnchor;
	}

	public String getAlias(){
		return alias;
	}
	
	public String getSpecies(){
		return species;
	}
}
