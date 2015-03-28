package model.reaction;

import java.util.Vector;

import model.MBase;
import model.specie.MSpecies;
import celldesignerparse_4_0.commondata.Line;
import celldesignerparse_4_0.reaction.EditPoint;

public class MModification extends MBase {
	protected MSpecies specie;
	protected String type;
	protected Vector<EditPoint> editPoints;
	protected Line line;
	protected String linkAnchor;
	protected String targetLineIndex;
	
	public MModification(MSpecies specie, 
						 String type, 
						 Vector<EditPoint> editPoints, 
						 Line line, 
						 String linkAnchor, 
						 String targetLineIndex) {
		this.specie = specie;
		this.type = type;
		this.editPoints = editPoints;
		this.line = line;
		this.linkAnchor = linkAnchor;
		this.targetLineIndex = targetLineIndex;
	}
	
	public String getLinkAnchor() {
		if ( this.linkAnchor==null || this.linkAnchor.equals("") || this.linkAnchor.equals("INACTIVE") ) {
			return null;
		} else { 
			return linkAnchor;
		}
	}

	public MSpecies getSpecie(){
		return specie;
	}
	
	public String getType(){
		return type;
	}
	
	public Vector<EditPoint> getEditPoints(){
		if ( this.editPoints == null ) {
			this.editPoints = new Vector<EditPoint>();
		}
		return editPoints;
	}
	
	public Vector<EditPoint> getEditPointsCopy(){
		Vector<EditPoint> deepCopy = new Vector<EditPoint>();
		for(EditPoint p : this.editPoints) {
			deepCopy.add((EditPoint) p.clone() );
		}
		return deepCopy;
	}
	
	public void addEditPoint( EditPoint ep ) {
		this.getEditPoints().add( ep );
	}
	
	public Line getLine(){
		return line;
	}
	
	public String getTargetLineIndex() {
		return targetLineIndex;
	}
}
