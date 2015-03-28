package celldesignerparse_4_0.annotation.listOfProteins;

import org.sbml.libsbml.XMLNode;

public class BindingRegion {	
	private String id;
	private String name;
	private double angle;
	private double size;
	
	public BindingRegion(XMLNode node) {
		super();		
		
		id = node.getAttributes().getValue("id");
		name = node.getAttributes().getValue("name");
		size = Double.parseDouble(node.getAttributes().getValue("size"));
		angle = Double.parseDouble(node.getAttributes().getValue("angle"));
		
	}
	
	public String toString(){
		return "BindingRegion: "+angle+" "+id+" "+name+" "+size;
	}

	public double getSize() {
		return size;
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
