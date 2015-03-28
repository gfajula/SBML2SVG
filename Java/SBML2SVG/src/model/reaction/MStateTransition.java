package model.reaction;

import java.util.Vector;

import model.specie.MAddedSpeciesLink;
import model.specie.MSpeciesLink;
import celldesignerparse_4_0.commondata.SingleLine;
import celldesignerparse_4_0.reaction.EditPoints;

public class MStateTransition extends MReaction{

	public MStateTransition(String id, 
									Vector<MSpeciesLink> products,
									Vector<MSpeciesLink> reactants, 
									Vector<MAddedSpeciesLink> productLinks,
									Vector<MAddedSpeciesLink> reactantLinks,
									EditPoints editPoints, 
									SingleLine line, 
									Vector<MModification> modifications, 
									Vector<MBooleanLogicGate> booleanGates,
									int rectangleIndex, boolean reversible ) {
		super(id, products, reactants, productLinks, reactantLinks, editPoints, line,
		  	 modifications, booleanGates, rectangleIndex, reversible);
	}
	
	@Override
	public String getType() {
		return "State Transition";
	}
}
