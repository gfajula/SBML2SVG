package celldesignerparse_4_0.annotation.listOfLayers;

import java.awt.geom.Rectangle2D;

import org.sbml.libsbml.XMLNode;

import celldesignerparse_4_0.commondata.Paint;

public class LayerSpeciesAlias {
	private String layerNotes = null;
	private double fontSize = 8;
	private Paint paint;
	private Rectangle2D bounds;
	private String targetType = "";
	private String targetId = null;
	private double x = -1;
	private double y = -1;
	
	public LayerSpeciesAlias(XMLNode node) {
		targetId = node.getAttributes().getValue("targetId");
		targetType = node.getAttributes().getValue("targetType");
		try {
			x = Double.parseDouble( node.getAttributes().getValue("x") );
			y = Double.parseDouble( node.getAttributes().getValue("y") );
		} catch ( NumberFormatException e ) {			
		}
		
		for (int i=0;i<node.getNumChildren();i++){
			if (node.getChild(i).getName().equalsIgnoreCase("layerNotes") ){
				layerNotes = node.getChild(i).getChild(0).getCharacters();
			} else if (node.getChild(i).getName().equalsIgnoreCase("bounds") ){
				bounds = new Rectangle2D.Double(
						Double.parseDouble(node.getChild(i).getAttributes().getValue("x")),
						Double.parseDouble(node.getChild(i).getAttributes().getValue("y")),
						Double.parseDouble(node.getChild(i).getAttributes().getValue("w")),
						Double.parseDouble(node.getChild(i).getAttributes().getValue("h")));
			} else if (node.getChild(i).getName().equalsIgnoreCase("font") ){
				try {
					fontSize =
						Double.parseDouble(node.getChild(i).getAttributes().getValue("size")) ;
				} catch ( NumberFormatException e ) {			
				}
						
			} else if (node.getChild(i).getName().equalsIgnoreCase("paint") ){
				paint = new Paint(node.getChild(i));
			}
		}
		
	}

	public String getLayerNotes() {
		return layerNotes;
	}

	public void setLayerNotes(String layerNotes) {
		this.layerNotes = layerNotes;
	}

	public double getFontSize() {
		return fontSize;
	}

	public void setFontSize(double fontSize) {
		this.fontSize = fontSize;
	}

	public Paint getPaint() {
		return paint;
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}

	public Rectangle2D getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle2D bounds) {
		this.bounds = bounds;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

}
