package celldesignerparse_4_0.reaction;

import java.util.Vector;

import org.sbml.libsbml.XMLNode;

public class EditPoints {
	Vector<EditPoint> editPoints;
	public EditPoints(XMLNode node){
		String[] split = node.getChild(0).getCharacters().split("[ ]");
		editPoints = new Vector<EditPoint>();
		for (int i=0;i<split.length;i++){
			editPoints.addElement(new EditPoint(split[i]));
		}
	}
	
	public Vector<EditPoint> getEditPoints(){
		return editPoints;
	}
}
