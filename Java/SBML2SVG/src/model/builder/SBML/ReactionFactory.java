package model.builder.SBML;

import java.util.Vector;

import model.reaction.MDissociation;
import model.reaction.MHeterodimerAssociation;
import model.reaction.MKnownTransitionOmitted;
import model.reaction.MModification;
import model.reaction.MReaction;
import model.reaction.MStateTransition;
import model.specie.MAddedSpeciesLink;
import model.specie.MSpeciesLink;

import org.sbml.libsbml.Reaction;
import org.sbml.libsbml.libsbml;

import svgcontroller.SBML2SVGException;
import celldesignerparse_4_0.commondata.SingleLine;

public class ReactionFactory {


	public static MReaction buildReaction(
			Reaction reac,
			String id,
			Vector<MSpeciesLink> mproducts, Vector<MSpeciesLink> mreactants,
			Vector<MAddedSpeciesLink> mAddedProducts,
			Vector<MAddedSpeciesLink> mAddedReactants,
			Vector<MModification> mModifications,
		    boolean reversible) throws SBML2SVGException {
		
		MReaction newReaction;
		
		if ( reac.getSBOTermID().equals( SBO.ASSOCIATION ) &&
			 ( mreactants.size() + mAddedReactants.size() > 1 ) ) {
			newReaction = 
				new MHeterodimerAssociation(
						id,
						mproducts,
						mreactants,
						mAddedProducts,		
						mAddedReactants,							
						null,		// No editpoints
						new SingleLine(),  // Standard line
						mModifications,		// No modifications
						null,		// No boolean gates
						0,
						reversible		// Not reversible
				);
		} else if ( reac.getSBOTermID().equals( SBO.DISSOCIATION ) &&
					 ( mproducts.size() + mAddedProducts.size() > 1 ) ) {
			newReaction = 
				new MDissociation(
						id,
						mproducts,
						mreactants,
						mAddedProducts,		
						mAddedReactants,							
						null,		// No editpoints
						new SingleLine(),  // Standard line
						mModifications,		// No modifications
						null,		// No boolean gates
						0,
						reversible		// Not reversible
				);
		}else if ( reac.getSBOTermID().equals( SBO.OMITTED ) ) {
			newReaction = 
				new MKnownTransitionOmitted(
						id,
						mproducts,
						mreactants,
						mAddedProducts,		
						mAddedReactants,							
						null,		// No editpoints
						new SingleLine(),  // Standard line
						mModifications,		// No modifications
						null,		// No boolean gates
						0,
						reversible		// Not reversible
				);
		} else {
//			reaccion por defecto
			newReaction = 
					new MStateTransition(
					id,
					mproducts,
					mreactants,
					mAddedProducts,		
					mAddedReactants,							
					null,		// No editpoints
					new SingleLine(),  // Standard line
					mModifications,		// No modifications
					null,		// No boolean gates
					0,
					reversible		// Not reversible
			);
		}
		
		if ( reac.getKineticLaw() != null )
			newReaction.setKineticLaw(  libsbml.writeMathMLToString( reac.getKineticLaw().getMath())   );

		return newReaction;
	}
	
}
