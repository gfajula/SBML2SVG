package celldesignerparse_4_0.commondata;

import java.awt.BasicStroke;
import java.awt.Stroke;

import org.sbml.libsbml.XMLNode;

public class SingleLine extends Line{
	private double width;
	
	
	public SingleLine() {
		super();
		width = 1.0;
	}
	
	public SingleLine(XMLNode node) {
		super(node);
		try {
			width = Double.parseDouble(node.getAttributes().getValue("width"));
		} catch ( NumberFormatException e ) {
			width = 1.0;
		}
		
	}
	
	public String toString(){
		return "singleLine: "+width;
	}

	public double getWidth(){
		return width;
	}

	
	
	public Stroke getStroke() {
		return new BasicStroke((float) width);
	}
}
