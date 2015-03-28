package model.specie;

import java.awt.geom.Point2D;
import java.util.Vector;

import model.MLine;

public class MAddedSpeciesLink extends MSpeciesLink {
	protected MLine line;
	// protected Point2D joint ; // punto de union a la reaccion
	protected int joint; 	// EditPoint de la reacción, por el que se une
	
	public MAddedSpeciesLink(MSpecies ms, String linkAnchor, MLine line) {
		super(ms, linkAnchor);
		this.line = line;
	}
	
	public MLine getLine() {
		return line;
	}
	
	public void addEditPoint( Point2D ep ) {
		this.getEditPoints().add( ep );
	}

	public Vector<Point2D> getEditPoints() {
		if (editPoints==null) {
			editPoints = new Vector<Point2D>();
		} 
		return editPoints;
	}

	public int getJoint() {
		return joint;
	}

	public void setJoint(int joint) {
		this.joint = joint;
	}
	
}
