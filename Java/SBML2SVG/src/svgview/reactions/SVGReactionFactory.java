package svgview.reactions;

import java.util.Map;

import model.reaction.MCatalysis;
import model.reaction.MDissociation;
import model.reaction.MHeterodimerAssociation;
import model.reaction.MInhibition;
import model.reaction.MKnownTransitionOmitted;
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
import model.reaction.MUnknownCatalysis;
import model.reaction.MUnknownInhibition;
import model.reaction.MUnknownNegativeInfluence;
import model.reaction.MUnknownPositiveInfluence;
import model.reaction.MUnknownTransition;

import org.w3c.dom.Document;

import svgview.species.SVGSpecie;

public class SVGReactionFactory {

	public static SVGReaction createSVGReaction(Document svgDoc, MReaction mr, Map<String, SVGSpecie> species, boolean transformEditPoints) {
		
		SVGReaction newSVGReaction;
//		if ( mr.getId().equals("r85")) {
//			System.out.println("STOP");
//		}
		if (mr instanceof MTranscription) {
			newSVGReaction = new SVGTranscription(svgDoc, (MTranscription)mr, species);		
		} else if (mr instanceof MTranslation) {
			newSVGReaction = new SVGTranslation(svgDoc, (MTranslation)mr, species);		
		} else if (mr instanceof MTruncation) {
			newSVGReaction = new SVGTruncation(svgDoc, (MTruncation)mr, species);		
		} else if (mr instanceof MDissociation) {
			newSVGReaction = new SVGDissociation(svgDoc, (MDissociation)mr, species);		
		} else if (mr instanceof MHeterodimerAssociation) {
			newSVGReaction = new SVGHeterodimerAssociation(svgDoc, (MHeterodimerAssociation)mr, species);		
		} else if (mr instanceof MTransport) {
			newSVGReaction = new SVGTransport(svgDoc, mr, species);		
		} else if (mr instanceof MUnknownTransition) {
			newSVGReaction = new SVGUnknownTransition(svgDoc, mr, species);		
		} else if (mr instanceof MKnownTransitionOmitted) {
			newSVGReaction = new SVGKnownTransitionOmitted(svgDoc, mr, species);		
		} else if (mr instanceof MStateTransition) {
			
			
			newSVGReaction = new SVGStateTransition(svgDoc, mr, species);

		} else if (mr instanceof MCatalysis) {
			newSVGReaction = new SVGCatalysis(svgDoc, mr, species);
		} else if (mr instanceof MUnknownCatalysis) {
			newSVGReaction = new SVGUnknownCatalysis(svgDoc, mr, species);
		} else if (mr instanceof MInhibition) {
			newSVGReaction = new SVGInhibition(svgDoc, mr, species);
		} else if (mr instanceof MUnknownInhibition) {
			newSVGReaction = new SVGUnknownInhibition(svgDoc, mr, species);
		} else if (mr instanceof MPhysicalStimulation ) {
			newSVGReaction = new SVGPhysicalStimulation(svgDoc, mr, species);
		} else if (mr instanceof MTranscriptionalActivation) {
			newSVGReaction = new SVGTranscriptionalActivation(svgDoc, mr, species);
		} else if (mr instanceof MTranscriptionalInhibition) {
			newSVGReaction = new SVGTranscriptionalInhibition(svgDoc, mr, species);
		} else if (mr instanceof MTranslationalActivation) {
			newSVGReaction = new SVGTranslationalActivation(svgDoc, mr, species);
		} else if (mr instanceof MTranslationalInhibition) {
			newSVGReaction = new SVGTranslationalInhibition(svgDoc, mr, species);
		} else if (mr instanceof MNegativeInfluence) {
			newSVGReaction = new SVGNegativeInfluence(svgDoc, mr, species);
		} else if (mr instanceof MUnknownNegativeInfluence) {
			newSVGReaction = new SVGUnknownNegativeInfluence(svgDoc, mr, species);
		} else if (mr instanceof MPositiveInfluence) {
			newSVGReaction = new SVGPositiveInfluence(svgDoc, mr, species);
		} else if (mr instanceof MUnknownPositiveInfluence) {
			newSVGReaction = new SVGUnknownPositiveInfluence(svgDoc, mr, species);
		} else {
		
			// fallback: 
			newSVGReaction = new SVGStateTransition(svgDoc, mr, species);
		}
		
		return newSVGReaction;
	}
}
