package celldesignerparse_4_0.annotation.listOfSpeciesAliases;

import java.awt.geom.Rectangle2D;

import celldesignerparse_4_0.commondata.Paint;

public class Tag {
	public final static int LEFT = 2;
	public final static int RIGHT = 3;
	public final static int UP = 0;
	public final static int DOWN = 1;	

	private String name;
	private int direction = 2;
	private Rectangle2D bounds;
	private double lineWidth = 1;
	private Paint framePaint;

	public Tag() {
		super();
	}
	
	public Tag(String name, int direction, Rectangle2D bounds,
			double lineWidth, Paint framePaint) {
		super();
		this.name = name;
		this.direction = direction;
		this.bounds = bounds;
		this.lineWidth = lineWidth;
		this.framePaint = framePaint;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
		
	}
	public int getDirection() {
		return direction;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public Rectangle2D getBounds() {
		return bounds;
	}
	
	public void setBounds(Rectangle2D bounds) {
		this.bounds = bounds;
	}
	
	public double getLineWidth() {
		return lineWidth;
	}
	
	public void setLineWidth(double lineWidth) {
		this.lineWidth = lineWidth;
	}
	
	public Paint getFramePaint() {
		return framePaint;
	}
	
	public void setFramePaint(Paint framePaint) {
		this.framePaint = framePaint;
	}

		
}
