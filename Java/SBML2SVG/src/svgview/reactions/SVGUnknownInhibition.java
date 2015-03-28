package svgview.reactions;

import java.util.Map;

import model.reaction.MReaction;

import org.w3c.dom.Document;

import svgview.species.SVGSpecie;

public class SVGUnknownInhibition extends SVGInhibition {

	public SVGUnknownInhibition(Document svgDoc, MReaction mr, Map<String, SVGSpecie> species){
		super(svgDoc, mr, species);
		this.svgDoc = svgDoc;
		this.mr = mr;			
	}
	
	public String getDashArray() {
		return "6,6";
	}	
}
