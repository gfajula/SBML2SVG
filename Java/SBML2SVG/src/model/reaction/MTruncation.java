package model.reaction;

import java.util.Vector;

import model.specie.MAddedSpeciesLink;
import model.specie.MSpeciesLink;
import celldesignerparse_4_0.commondata.SingleLine;
import celldesignerparse_4_0.reaction.EditPoints;

public class MTruncation extends MReaction{
	private int[] arm;
	private int tShapeIndex;
	public MTruncation(String id, Vector<MSpeciesLink> products,
			Vector<MSpeciesLink> reactants, Vector<MAddedSpeciesLink> productLinks,
			Vector<MAddedSpeciesLink> reactantLinks, EditPoints editPoints, SingleLine line,
			Vector<MModification> modifications,  Vector<MBooleanLogicGate> booleanGates,
			int rectangleIndex, boolean reversible, int[] arm, int tShapeIndex) {
		super(id, products, reactants, productLinks, reactantLinks, editPoints, 
              line, modifications, booleanGates, rectangleIndex, reversible );
		this.arm = arm;
		this.tShapeIndex = tShapeIndex;
	}
	
	public int[] getArms() {
		return arm;
	}

	public int getTShapeIndex() {
		return tShapeIndex;
	}
	
	@Override
	public String getType() {
		return "Truncation";
	}
}
