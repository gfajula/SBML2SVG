package celldesignerparse_4_0.commondata;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import org.sbml.libsbml.XMLNode;

public class View {
	private Point2D innerPosition;
	private Dimension boxSize;
	private Line line;
	private Paint paint;
	
	public View(Point2D innerPosition, Dimension boxSize, Line line, Paint paint) {
		super();
		this.innerPosition = innerPosition;
		this.boxSize = boxSize;
		this.line = line;
		this.paint = paint;
	}
	
	

	public View(XMLNode node) {
		for (int i=0;i<node.getNumChildren();i++){
			if (node.getChild(i).getName().compareToIgnoreCase("innerPosition")==0){
				innerPosition = new Point2D.Double(
						Double.parseDouble(node.getChild(i).getAttributes().getValue("x")),
						Double.parseDouble(node.getChild(i).getAttributes().getValue("y")));
			} else if (node.getChild(i).getName().compareToIgnoreCase("boxSize")==0){
				boxSize = new Dimension();
				boxSize.setSize(Double.parseDouble(node.getChild(i).getAttributes().getValue("width")),
						Double.parseDouble(node.getChild(i).getAttributes().getValue("height")));
			} else if (node.getChild(i).getName().compareToIgnoreCase("singleLine")==0){
				line = new SingleLine(node.getChild(i));
			} else if (node.getChild(i).getName().compareToIgnoreCase("paint")==0){
				paint = new Paint(node.getChild(i));
			}
		}
	}
	
	public String toString(){
		return "View "+innerPosition+boxSize+line+paint;
	}

	public Paint getPaint() {
		return paint;
	}

	public Line getLine() {
		return line;
	}

}
