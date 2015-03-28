package celldesignerparse_4_0.reaction;

import java.util.Vector;

import org.sbml.libsbml.XMLNode;

import celldesignerparse_4_0.commondata.Line;
import celldesignerparse_4_0.commondata.SingleLine;




public class Modification {
	private String type;
	private String modificationType;
	private String modifiers;
	private String aliases;
	private String targetLineIndex;
	private String linkAnchor;
	private Vector<EditPoint> editPoints;
	private Line line;
	
	
	public Modification(XMLNode node) {
		type = node.getAttributes().getValue("type");
		modificationType = node.getAttributes().getValue("modificationType");
		modifiers = node.getAttributes().getValue("modifiers");
		aliases = node.getAttributes().getValue("aliases");
		targetLineIndex = node.getAttributes().getValue("targetLineIndex");
		editPoints = new Vector<EditPoint>();
		String editPointsAtt = node.getAttributes().getValue("editPoints");
		if (!editPointsAtt.equals("")) {
			String[] ep = editPointsAtt.split("[ ]");
			for (int i = 0; i < ep.length; i++) {
				editPoints.addElement(new EditPoint(ep[i]));
			}
		}
		for (int i = 0; i < node.getNumChildren(); i++) {
			if ( node.getChild(i).getName().equalsIgnoreCase("line") ) {
				line = new SingleLine(node.getChild(i));
			} 
		}
		
		XMLNode linkAnchorNode = node.getChild("linkTarget").getChild("linkAnchor");
		if (linkAnchorNode!=null) {
			linkAnchor = linkAnchorNode.getAttributes().getValue("position");
		}
	}
	
	public String getLinkAnchor() {
		return linkAnchor;
	}

	public Line getLine(){
		return line;
	}
	
	public Vector<EditPoint> getEditPoints(){
		return editPoints;
	}
	
	public String getTargetLineIndex(){
		return targetLineIndex;
	}
	
	public String getAliases(){
		return aliases;
	}
	
	public String getModifier(){
		return modifiers;
	}
	
	public String getType(){
		return type;
	}

	public String getModificationType() {
		return modificationType;
	}

}
