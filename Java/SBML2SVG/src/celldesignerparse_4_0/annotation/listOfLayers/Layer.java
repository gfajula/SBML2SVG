package celldesignerparse_4_0.annotation.listOfLayers;

import java.util.Vector;

import org.sbml.libsbml.XMLNode;

public class Layer {

	private Vector<LayerSpeciesAlias> listOfTexts;

	public Layer(XMLNode node) {
		for (int i=0;i<node.getNumChildren();i++){
			if (node.getChild(i).getName().equalsIgnoreCase("listOfTexts") ){
				listOfTexts = new Vector<LayerSpeciesAlias>();
				XMLNode lotNode = node.getChild(i); 
				for (int j=0; j<lotNode.getNumChildren(); j++){
					listOfTexts.addElement(
							new LayerSpeciesAlias(
									lotNode.getChild(j)
								)
							);
				}
			}
		}
	}

	public Vector<LayerSpeciesAlias> getListOfTexts() {
		return listOfTexts;
	}

}
