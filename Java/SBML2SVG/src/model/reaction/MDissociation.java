package model.reaction;

import java.util.Vector;

import svgcontroller.SBML2SVGException;

import model.specie.MAddedSpeciesLink;
import model.specie.MSpeciesLink;
import celldesignerparse_4_0.commondata.SingleLine;
import celldesignerparse_4_0.reaction.EditPoints;

public class MDissociation extends MReaction{
	private int[] arm;
	private int tShapeIndex;
	
	public MDissociation(String id, Vector<MSpeciesLink> products,
			Vector<MSpeciesLink> reactants, Vector<MAddedSpeciesLink> productLinks,
			Vector<MAddedSpeciesLink> reactantLinks, EditPoints editPoints,
			SingleLine line, Vector<MModification> modifications,  
	         Vector<MBooleanLogicGate> booleanGates , int rectangleIndex , boolean reversible,
			int[] arm, int tShapeIndex) {
		super(id, products, reactants, productLinks, reactantLinks, editPoints, line,
				modifications, booleanGates, rectangleIndex, reversible);
		this.arm = arm;
		this.tShapeIndex = tShapeIndex;
	}
	
	/**
	 * Constructor 
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
	public MDissociation(String id, Vector<MSpeciesLink> products,
			Vector<MSpeciesLink> reactants, Vector<MAddedSpeciesLink> productLinks,
			Vector<MAddedSpeciesLink> reactantLinks, EditPoints editPoints,
			SingleLine line, Vector<MModification> modifications,  
	         Vector<MBooleanLogicGate> booleanGates , int rectangleIndex , boolean reversible ) throws SBML2SVGException {
		super(id, products, reactants, productLinks, reactantLinks, editPoints, line,
				modifications, booleanGates, rectangleIndex, reversible);
		
		int arm[] = {0,0,0};
		this.arm = arm;
		this.tShapeIndex = 0;
		
		// Establecer un mínimo de productos de 2
		if ( products.size() < 2 ) {
			if ( productLinks.size() > 0 ) {
				MSpeciesLink popped = productLinks.remove(0);
				products.add( popped );
			} else {
				throw new SBML2SVGException("Error: creando MDissociation '" + id + "' con menos de 2 productos");
			}
		}
		
	}
	
	public int[] getArms() {
		return arm;
	}

	public int getTShapeIndex() {
		return tShapeIndex;
	}
	
	public String getType() {
		return "Dissociation";
	}
}
