package model.reaction;

import java.util.Vector;

import model.specie.MSpecies;
import celldesignerparse_4_0.commondata.Line;
import celldesignerparse_4_0.reaction.EditPoint;

public class MBooleanLogicGate {

	protected Vector<MSpecies> species;
	protected String type;
	protected String modificationType;
	protected Vector<EditPoint> editPoints;
	protected Line line;
	protected String targetLineIndex;
	protected Vector<MModification> modifications;
	
	public MBooleanLogicGate( Vector<MSpecies> species, String type, String modificationType, Vector<EditPoint> editPoints, Line line, String targetLineIndex) {
		this.species = species;

		this.type = type;
		this.modificationType = modificationType;
		this.editPoints = editPoints;
		this.line = line;
		this.targetLineIndex = targetLineIndex;		
		this.modifications = new Vector<MModification>();
	}	
	
	public String getType(){
		return type;
	}
	
	public Vector<EditPoint> getEditPoints(){
		return editPoints;
	}
	
	public Line getLine(){
		return line;
	}

	public Vector<MSpecies> getSpecies() {
		return species;
	}

	public String getModificationType() {
		return modificationType;
	}
	
	public String getTargetLineIndex() {
		return targetLineIndex;
	}

	public void addModification(MModification mod) {
		this.modifications.add( mod );
		
	}

	public Vector<MModification> getModifications() {
		return modifications;
	}
}
