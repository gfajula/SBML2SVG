package model.specie;

import java.awt.geom.Point2D;
import java.util.Vector;

public class MSpeciesLink {
	private MSpecies ms;
	private String linkAnchor;
	protected Vector<Point2D> editPoints;
	
	public MSpeciesLink(MSpecies ms, String linkAnchor) {
		super();
		this.ms = ms;
		this.linkAnchor = linkAnchor;
	}
	
	public MSpecies getMs() {
		return ms;
	}
	public String getLinkAnchor() {
		if (linkAnchor==null || linkAnchor.equals("") ) {
			return null;
		} else {
			return linkAnchor;
		}
	}

	public Vector<Point2D> getEditPoints() {
		if (editPoints==null) {
			editPoints = new Vector<Point2D>();
		} 
		return editPoints;
	}

	public void setLinkAnchor(String linkAnchor) {
		this.linkAnchor = linkAnchor;
	}
	
	
}
