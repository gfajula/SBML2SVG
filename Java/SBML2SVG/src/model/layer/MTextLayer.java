package model.layer;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import model.reaction.MReaction;
import model.specie.MSpecies;

public class MTextLayer {
	
	MSpecies specie = null;
	MReaction reaction = null;
	
	private String text = null;
	private double fontSize = 8;
	private Color textColor;
	private Rectangle2D bounds;
//	private String targetType = "";
//	private String targetId = null;
	private double x = -1;
	private double y = -1;
	
	public MTextLayer() {
		super();
	}
	
	public MSpecies getSpecie() {
		return specie;
	}
	public void setSpecie(MSpecies specie) {
		this.specie = specie;
	}
	public MReaction getReaction() {
		return reaction;
	}
	public void setReaction(MReaction reaction) {
		this.reaction = reaction;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public double getFontSize() {
		return fontSize;
	}
	public void setFontSize(double fontSize) {
		this.fontSize = fontSize;
	}
	public Color getTextColor() {
		return textColor;
	}
	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}
	public Rectangle2D getBounds() {
		return bounds;
	}
	public void setBounds(Rectangle2D bounds) {
		this.bounds = bounds;
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
