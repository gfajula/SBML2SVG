package celldesignerparse_4_0.annotation;

import org.sbml.libsbml.XMLNode;

public class InformationUnit {

	private String prefix="";
	private String state="empty";
	private double angle=0;
	private String label="";
	
	public InformationUnit( XMLNode infoNode ) {
		prefix = infoNode.getAttributes().getValue("prefix");
		state = infoNode.getAttributes().getValue("state");
		angle = Double.parseDouble( infoNode.getAttributes().getValue("angle") );
		label = infoNode.getAttributes().getValue("label");
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
