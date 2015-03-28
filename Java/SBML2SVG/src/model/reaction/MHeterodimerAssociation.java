package model.reaction;

import java.util.Vector;

import svgcontroller.SBML2SVGException;

import model.specie.MAddedSpeciesLink;
import model.specie.MSpeciesLink;
import celldesignerparse_4_0.commondata.SingleLine;
import celldesignerparse_4_0.reaction.EditPoints;

public class MHeterodimerAssociation extends MReaction{
	private int[] arm;
	private int tShapeIndex;	

	/**
	 * Constructor específico, indica explícitamente el array de ramas
	 * 
	 * @param id
	 * @param products
	 * @param reactants
	 * @param productLinks
	 * @param reactantLinks
	 * @param editPoints
	 * @param line
	 * @param modifications
	 * @param booleanGates
	 * @param rectangleIndex
	 * @param reversible
	 * @param arm
	 * @param tShapeIndex
	 */
	public MHeterodimerAssociation(String id, Vector<MSpeciesLink> products,
			Vector<MSpeciesLink> reactants, Vector<MAddedSpeciesLink> productLinks,
			Vector<MAddedSpeciesLink> reactantLinks, EditPoints editPoints,
			SingleLine line, Vector<MModification> modifications,			
	        Vector<MBooleanLogicGate> booleanGates, int rectangleIndex , boolean reversible,
			int[] arm, int tShapeIndex) {
		super(id, products, reactants, productLinks, reactantLinks, editPoints, line,
				modifications, booleanGates, rectangleIndex, reversible);
		this.arm = arm;
		this.tShapeIndex = tShapeIndex;
	}
	
	/**
	 * Constructor compatible con la signatura de MReaction. 
	 * 
	 * @param id
	 * @param products
	 * @param reactants
	 * @param productLinks
	 * @param reactantLinks
	 * @param editPoints
	 * @param line
	 * @param modifications
	 * @param booleanGates
	 * @param rectangleIndex
	 * @param reversible
	 * @throws SBML2SVGException 
	 */
	public MHeterodimerAssociation(String id, Vector<MSpeciesLink> products,
			Vector<MSpeciesLink> reactants, Vector<MAddedSpeciesLink> productLinks,
			Vector<MAddedSpeciesLink> reactantLinks, EditPoints editPoints,
			SingleLine line, Vector<MModification> modifications,			
	        Vector<MBooleanLogicGate> booleanGates, int rectangleIndex , boolean reversible ) throws SBML2SVGException {
		super(id, products, reactants, productLinks, reactantLinks, editPoints, line,
				modifications, booleanGates, rectangleIndex, reversible);
		
		this.setTransformEditPoints( false );
	}

	public int[] getArms() {
		return arm;
	}

	public int getTShapeIndex() {
		return tShapeIndex;
	}

	@Override
	public String getType() {
		return "Heterodimer Association";
	}
	
	
}
