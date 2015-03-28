package model.builder.CD4;

import java.util.Vector;

import model.reaction.MBooleanLogicGate;
import model.reaction.MCatalysis;
import model.reaction.MDissociation;
import model.reaction.MHeterodimerAssociation;
import model.reaction.MInhibition;
import model.reaction.MKnownTransitionOmitted;
import model.reaction.MModification;
import model.reaction.MNegativeInfluence;
import model.reaction.MPhysicalStimulation;
import model.reaction.MPositiveInfluence;
import model.reaction.MReaction;
import model.reaction.MStateTransition;
import model.reaction.MTranscription;
import model.reaction.MTranscriptionalActivation;
import model.reaction.MTranscriptionalInhibition;
import model.reaction.MTranslation;
import model.reaction.MTranslationalActivation;
import model.reaction.MTranslationalInhibition;
import model.reaction.MTransport;
import model.reaction.MTruncation;
import model.reaction.MUnimplementedReaction;
import model.reaction.MUnknownCatalysis;
import model.reaction.MUnknownInhibition;
import model.reaction.MUnknownNegativeInfluence;
import model.reaction.MUnknownPositiveInfluence;
import model.reaction.MUnknownTransition;
import model.specie.MAddedSpeciesLink;
import model.specie.MSpeciesLink;
import celldesignerparse_4_0.Constants;
import celldesignerparse_4_0.reaction.CellDesignerReaction;

public class MCD4ReactionFactory {

	public static MReaction buildReaction(CellDesignerReaction cdr,
											Vector<MSpeciesLink> mreactants ,
											Vector<MSpeciesLink> mproducts ,
											Vector<MAddedSpeciesLink> mreactantLinks ,
											Vector<MAddedSpeciesLink> mproductLinks ,
											Vector<MModification> mmodifications ,
											Vector<MBooleanLogicGate> mbooleanGates 
											) {
		MReaction newReaction;
		if (cdr.getReactionType().compareToIgnoreCase(Constants.STATE_TRANSITION)==0){
			newReaction = new MStateTransition(
					cdr.getId(),
					mproducts,
					mreactants,
					mproductLinks,
					mreactantLinks,
					cdr.getEditPoints(),
					cdr.getLineCellDesigner(),
					mmodifications, mbooleanGates, 
					cdr.getRectangleIndex(), cdr.getReversible()
					);

		} else if (cdr.getReactionType().compareToIgnoreCase(Constants.TRANSPORT)==0){
			newReaction = new MTransport(
					cdr.getId(),
					mproducts,
					mreactants,
					mproductLinks,
					mreactantLinks,
					cdr.getEditPoints(),
					cdr.getLineCellDesigner(),
					mmodifications, mbooleanGates,
					cdr.getRectangleIndex(), cdr.getReversible() );

		} else if (cdr.getReactionType().compareToIgnoreCase(Constants.TRANSLATION)==0){
			newReaction = new MTranslation(						
					cdr.getId(),
					mproducts,
					mreactants,
					mproductLinks,
					mreactantLinks,
					cdr.getEditPoints(),
					cdr.getLineCellDesigner(),
					mmodifications, mbooleanGates,
					cdr.getRectangleIndex(), cdr.getReversible() );

		} else if (cdr.getReactionType().compareToIgnoreCase(Constants.TRANSCRIPTION)==0){
			newReaction = new MTranscription(						
					cdr.getId(),
					mproducts,
					mreactants,
					mproductLinks,
					mreactantLinks,
					cdr.getEditPoints(),
					cdr.getLineCellDesigner(),
					mmodifications, mbooleanGates,
					cdr.getRectangleIndex(), cdr.getReversible() );

		} else if (cdr.getReactionType().compareToIgnoreCase(Constants.KNOWN_TRANSITION_OMITTED)==0){
			newReaction = new MKnownTransitionOmitted(
					cdr.getId(),
					mproducts,
					mreactants,
					mproductLinks,
					mreactantLinks,
					cdr.getEditPoints(),
					cdr.getLineCellDesigner(),
					mmodifications, mbooleanGates,
					cdr.getRectangleIndex(), cdr.getReversible() );

		} else if (cdr.getReactionType().compareToIgnoreCase(Constants.UNKNOWN_TRANSITION)==0){
			newReaction = new MUnknownTransition(
					cdr.getId(),
					mproducts,
					mreactants,
					mproductLinks,
					mreactantLinks,
					cdr.getEditPoints(),
					cdr.getLineCellDesigner(),
					mmodifications, mbooleanGates,
					cdr.getRectangleIndex(), cdr.getReversible() );

		} else if (cdr.getReactionType().compareToIgnoreCase(Constants.DISSOCIATION)==0){
			newReaction = new MDissociation(
					cdr.getId(),
					mproducts,
					mreactants,
					mproductLinks,
					mreactantLinks,
					cdr.getEditPoints(),
					cdr.getLineCellDesigner(),
					mmodifications, mbooleanGates,
					cdr.getRectangleIndex(), cdr.getReversible(),
					cdr.getArms(),
					cdr.getTShapeIndex());

		} else if (cdr.getReactionType().compareToIgnoreCase(Constants.TRUNCATION)==0){
			newReaction = new MTruncation(
					cdr.getId(),
					mproducts,
					mreactants,
					mproductLinks,
					mreactantLinks,
					cdr.getEditPoints(),
					cdr.getLineCellDesigner(),
					mmodifications, mbooleanGates,
					cdr.getRectangleIndex(), cdr.getReversible(),
					cdr.getArms(),
					cdr.getTShapeIndex());

		} else if (cdr.getReactionType().compareToIgnoreCase(Constants.HETERODIMER_ASSOCIATION)==0){
			newReaction = new MHeterodimerAssociation(
					cdr.getId(),
					mproducts,
					mreactants,
					mproductLinks,
					mreactantLinks,
					cdr.getEditPoints(),
					cdr.getLineCellDesigner(),
					mmodifications, mbooleanGates,
					cdr.getRectangleIndex(), cdr.getReversible(),
					cdr.getArms(),
					cdr.getTShapeIndex());

		} else if (cdr.getReactionType().equalsIgnoreCase(Constants.CATALYSIS)){
			newReaction = new MCatalysis(
					cdr.getId(),
					mproducts,
					mreactants,
					mproductLinks,
					mreactantLinks,
					cdr.getEditPoints(),
					cdr.getLineCellDesigner(),
					mmodifications, mbooleanGates, 
					cdr.getRectangleIndex(), cdr.getReversible()
					);

		} else if (cdr.getReactionType().equalsIgnoreCase(Constants.UNKNOWN_CATALYSIS)){
			newReaction = new MUnknownCatalysis(
					cdr.getId(),
					mproducts,
					mreactants,
					mproductLinks,
					mreactantLinks,
					cdr.getEditPoints(),
					cdr.getLineCellDesigner(),
					mmodifications, mbooleanGates, 
					cdr.getRectangleIndex(), cdr.getReversible()
					);

		} else if (cdr.getReactionType().equalsIgnoreCase(Constants.INHIBITION)){
			newReaction = new MInhibition(
					cdr.getId(),
					mproducts,
					mreactants,
					mproductLinks,
					mreactantLinks,
					cdr.getEditPoints(),
					cdr.getLineCellDesigner(),
					mmodifications, mbooleanGates, 
					cdr.getRectangleIndex(), cdr.getReversible()
					);

		} else if (cdr.getReactionType().equalsIgnoreCase(Constants.PHYSICAL_STIMULATION)){
			newReaction = new MPhysicalStimulation (
					cdr.getId(),
					mproducts,
					mreactants,
					mproductLinks,
					mreactantLinks,
					cdr.getEditPoints(),
					cdr.getLineCellDesigner(),
					mmodifications, mbooleanGates, 
					cdr.getRectangleIndex(), cdr.getReversible()
					);
		} else if (cdr.getReactionType().equalsIgnoreCase(Constants.UNKNOWN_INHIBITION)){
			newReaction = new MUnknownInhibition(
					cdr.getId(),
					mproducts,
					mreactants,
					mproductLinks,
					mreactantLinks,
					cdr.getEditPoints(),
					cdr.getLineCellDesigner(),
					mmodifications, mbooleanGates, 
					cdr.getRectangleIndex(), cdr.getReversible()
					);
		} else if (cdr.getReactionType().equalsIgnoreCase(Constants.TRANSCRIPTIONAL_ACTIVATION)){
			newReaction = new MTranscriptionalActivation(
					cdr.getId(),
					mproducts,
					mreactants,
					mproductLinks,
					mreactantLinks,
					cdr.getEditPoints(),
					cdr.getLineCellDesigner(),
					mmodifications, mbooleanGates, 
					cdr.getRectangleIndex(), cdr.getReversible()
					);
		} else if (cdr.getReactionType().equalsIgnoreCase(Constants.TRANSCRIPTIONAL_INHIBITION)){
			newReaction = new MTranscriptionalInhibition(
					cdr.getId(),
					mproducts,
					mreactants,
					mproductLinks,
					mreactantLinks,
					cdr.getEditPoints(),
					cdr.getLineCellDesigner(),
					mmodifications, mbooleanGates, 
					cdr.getRectangleIndex(), cdr.getReversible()
					);
		} else if (cdr.getReactionType().equalsIgnoreCase(Constants.TRANSLATIONAL_ACTIVATION)){
			newReaction = new MTranslationalActivation(
					cdr.getId(),
					mproducts,
					mreactants,
					mproductLinks,
					mreactantLinks,
					cdr.getEditPoints(),
					cdr.getLineCellDesigner(),
					mmodifications, mbooleanGates, 
					cdr.getRectangleIndex(), cdr.getReversible()
					);
		} else if (cdr.getReactionType().equalsIgnoreCase(Constants.TRANSLATIONAL_INHIBITION)){
			newReaction = new MTranslationalInhibition(
					cdr.getId(),
					mproducts,
					mreactants,
					mproductLinks,
					mreactantLinks,
					cdr.getEditPoints(),
					cdr.getLineCellDesigner(),
					mmodifications, mbooleanGates, 
					cdr.getRectangleIndex(), cdr.getReversible()
					);
		} else if (cdr.getReactionType().equalsIgnoreCase(Constants.NEGATIVE_INFLUENCE)){
			newReaction = new MNegativeInfluence(
					cdr.getId(),
					mproducts,
					mreactants,
					mproductLinks,
					mreactantLinks,
					cdr.getEditPoints(),
					cdr.getLineCellDesigner(),
					mmodifications, mbooleanGates, 
					cdr.getRectangleIndex(), cdr.getReversible()
					);
		} else if (cdr.getReactionType().equalsIgnoreCase(Constants.UNKNOWN_NEGATIVE_INFLUENCE)){
			newReaction = new MUnknownNegativeInfluence(
					cdr.getId(),
					mproducts,
					mreactants,
					mproductLinks,
					mreactantLinks,
					cdr.getEditPoints(),
					cdr.getLineCellDesigner(),
					mmodifications, mbooleanGates, 
					cdr.getRectangleIndex(), cdr.getReversible()
					);
		} else if (cdr.getReactionType().equalsIgnoreCase(Constants.POSITIVE_INFLUENCE)){
			newReaction = new MPositiveInfluence(
					cdr.getId(),
					mproducts,
					mreactants,
					mproductLinks,
					mreactantLinks,
					cdr.getEditPoints(),
					cdr.getLineCellDesigner(),
					mmodifications, mbooleanGates, 
					cdr.getRectangleIndex(), cdr.getReversible()
					);
		} else if (cdr.getReactionType().equalsIgnoreCase(Constants.UNKNOWN_POSITIVE_INFLUENCE)){
			newReaction = new MUnknownPositiveInfluence(
					cdr.getId(),
					mproducts,
					mreactants,
					mproductLinks,
					mreactantLinks,
					cdr.getEditPoints(),
					cdr.getLineCellDesigner(),
					mmodifications, mbooleanGates, 
					cdr.getRectangleIndex(), cdr.getReversible()
					);
		} else {
			//fallback:
			newReaction = new MUnimplementedReaction(
					cdr.getId(),
					mproducts,
					mreactants,
					mproductLinks,
					mreactantLinks,
					cdr.getEditPoints(),
					cdr.getLineCellDesigner(),
					mmodifications, mbooleanGates, 
					cdr.getRectangleIndex(), cdr.getReversible()
					);
		}
		newReaction.setSboTerm( cdr.getSBOTermID() );
		
		newReaction.setTransformEditPoints( true );
		
		return newReaction;
	}
}
