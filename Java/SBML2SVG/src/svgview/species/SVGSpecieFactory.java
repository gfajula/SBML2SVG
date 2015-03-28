package svgview.species;

import model.Model;
import model.specie.MAntiSenseRNA;
import model.specie.MDegraded;
import model.specie.MDrug;
import model.specie.MIon;
import model.specie.MPhenotype;
import model.specie.MRNA;
import model.specie.MSimpleMolecule;
import model.specie.MSpecies;
import model.specie.MSpeciesComplex;
import model.specie.gene.MGene;
import model.specie.protein.MIonChannel;
import model.specie.protein.MProtein;
import model.specie.protein.MReceptor;
import model.specie.protein.MTruncated;

import org.w3c.dom.Document;

import svgcontroller.SBML2SVGException;
import svgview.SVGConfig;
import svgview.species.gene.SVGGene;

public class SVGSpecieFactory {
	
	public static SVGSpecie createSVGSpecie(Document svgDoc, MSpecies ms, Model model) throws SBML2SVGException {
		SVGSpecie specie;
		
		if (ms instanceof MSpeciesComplex) {
			specie = new SVGComplex(svgDoc, (MSpeciesComplex)ms, model);
		} else if (ms instanceof MAntiSenseRNA) {
			specie = new SVGAntiSenseRNA(svgDoc, (MAntiSenseRNA)ms);
		} else if (ms instanceof MRNA) {
			specie = new SVGRNA(svgDoc, (MRNA)ms);
		} else if (ms instanceof MGene) {
			specie = new SVGGene(svgDoc, (MGene)ms);
		} else if (ms instanceof MTruncated) {
			specie = new SVGTruncated(svgDoc, (MTruncated)ms);
		} else if (ms instanceof MIonChannel) {
			specie = new SVGIonChannel(svgDoc, (MIonChannel)ms);
		} else if (ms instanceof MDegraded) {
			specie = new SVGDegraded(svgDoc, (MDegraded)ms);
		} else if (ms instanceof MIon) {
			specie = new SVGIon(svgDoc, (MIon)ms);
		} else if (ms instanceof MPhenotype) {
			specie = new SVGPhenotype(svgDoc, (MPhenotype)ms);
		} else if (ms instanceof MSimpleMolecule) {
			specie = new SVGSimpleMolecule(svgDoc, (MSimpleMolecule)ms);
		} else if (ms instanceof MReceptor) {
			specie = new SVGReceptor(svgDoc, (MReceptor) ms);
		} else if (ms instanceof MProtein) {
			specie = new SVGProtein(svgDoc, (MProtein)ms);
		} else if (ms instanceof MDrug) {
			specie = new SVGDrug(svgDoc, (MDrug)ms);
		} else {		
			specie = new SVGUnknown(svgDoc, ms);
		}
		// Si estamos en modo SBGN, añadir informacion de clon
		if ( SVGConfig.SBGNMode ) {
			specie.setClone( model.isClone( ms.getId() ) );
		}
		
		return specie;
	}
	
}
