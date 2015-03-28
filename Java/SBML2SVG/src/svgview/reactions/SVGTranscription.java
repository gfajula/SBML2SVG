package svgview.reactions;

import java.awt.geom.Point2D;
import java.util.Map;

import model.reaction.MReaction;

import org.w3c.dom.Document;

import svgview.shapes.SVGShape;
import svgview.species.SVGSpecie;

public class SVGTranscription extends SVGGenericReaction {
	public SVGTranscription(Document svgDoc, MReaction mr, Map<String, SVGSpecie> species){
		super(svgDoc, mr, species);
		this.svgDoc = svgDoc;
		this.mr = mr;			
	}
	
	public String getDashArray() {
		return "10 4 2 4 2 4";
	}	
	
	protected SVGShape getRectangleBetween(Point2D p1, Point2D p2){
		return getRectangleBetween(p1, p2, "");
	}
	
	public String getType() {
		return "Transcription";
	}

	@Override
	protected boolean showsSquare() {
		return true;
	}
}
