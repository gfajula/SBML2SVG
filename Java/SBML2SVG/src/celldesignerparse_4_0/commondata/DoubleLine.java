package celldesignerparse_4_0.commondata;

import java.awt.BasicStroke;
import java.awt.Stroke;

import org.sbml.libsbml.XMLNode;

/**
 * 
 * 
 * @author Guillermo Fajula Leal
 *
 */
public class DoubleLine extends Line{
	private double thickness;
	private double outerWidth;
	private double innerWidth;
	
	public DoubleLine(XMLNode node) {
		this();
		thickness = Double.parseDouble(node.getAttributes().getValue("thickness"));
		outerWidth = Double.parseDouble(node.getAttributes().getValue("outerWidth"));
		innerWidth = Double.parseDouble(node.getAttributes().getValue("innerWidth"));
	}
	
	// Default constructor
	public DoubleLine() {
		super();
		thickness = 10;
		outerWidth = 2;
		innerWidth = 1;
	}
	
	public String toString(){
		return "thickness:" +thickness+" outerWidth: "+outerWidth+" innerWidth: "+innerWidth;
	}

	public double getThickness(){
		return thickness;
	}
	
	public Stroke getInnerStroke() {
		return new BasicStroke((float)innerWidth);
	}
	
	public Stroke getOuterStroke() {
		return new BasicStroke((float)outerWidth);
	}
	
	public double getInnerWidth() {
		return innerWidth;
	}
	
	public double getOuterWidth() {
		return outerWidth;
	}	
	
}
