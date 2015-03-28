package celldesignerparse_4_0.annotation.listOfProteins;

import org.sbml.libsbml.XMLNode;

public class ModificationResidue {
	private double angle;
	private String id;
	private String name;
	private String side;
	
	public ModificationResidue(XMLNode node) {
		try {
			angle = Double.parseDouble(node.getAttributes().getValue("angle"));
		} catch( NumberFormatException e ) {
			angle = Math.PI;
		}
		id = node.getAttributes().getValue("id");
		name = node.getAttributes().getValue("name");
		side = node.getAttributes().getValue("side");
	}

	public String toString(){
		return "ModificationResidue: "+angle+" "+id+" "+name+" "+side;
	}

	public String getSide() {
		return side;
	}

	public double getAngle() {
		return angle;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}
}
