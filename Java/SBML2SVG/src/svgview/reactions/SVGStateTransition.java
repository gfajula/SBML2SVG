package svgview.reactions;

import java.awt.geom.Point2D;
import java.util.Map;

import model.reaction.MReaction;

import org.w3c.dom.Document;

import svgview.shapes.SVGShape;
import svgview.species.SVGSpecie;

public class SVGStateTransition extends SVGGenericReaction {

	public SVGStateTransition(Document svgDoc, MReaction mr, Map<String, SVGSpecie> species){
		super(svgDoc, mr, species);
		this.svgDoc = svgDoc;
		this.mr = mr;			
	}

	protected SVGShape getRectangleBetween(Point2D p1, Point2D p2){
		return super.getRectangleBetween(p1,p2);
	}

	@Override
	protected boolean showsSquare() {
		return true;
	}
	
}
