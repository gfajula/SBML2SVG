package svgview.reactions;

import java.awt.geom.Point2D;
import java.util.Map;

import model.reaction.MReaction;

import org.w3c.dom.Document;

import svgview.shapes.SVGShape;
import svgview.species.SVGSpecie;

public class SVGTranslation extends SVGGenericReaction {
	public SVGTranslation(Document svgDoc, MReaction mr, Map<String, SVGSpecie> species){
		super(svgDoc, mr, species);
		this.svgDoc = svgDoc;
		this.mr = mr;			
	}
	
	public String getDashArray() {
		return "10 3 2 3";
	}
	
	protected SVGShape getRectangleBetween(Point2D p1, Point2D p2){
		return getRectangleBetween(p1, p2, "");
	}

	@Override
	protected boolean showsSquare() {
		return true;
	}
	
}
