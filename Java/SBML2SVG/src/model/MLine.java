package model;

import java.awt.Color;

public class MLine {
	protected Color color;
	protected double width;
	protected String type;
	
	public MLine() {
		super();
		this.color = new Color(0,0,0);
		this.width = 1.0;
		this.type = "Straight";
	}
	
	public MLine(Color color, double width, String type) {
		super();
		this.color = color;
		this.width = width;
		this.type = type;
	}
	public Color getColor() {
		return color;
	}
	public double getWidth() {
		return width;
	}
	public String getType() {
		return type;
	}
}
