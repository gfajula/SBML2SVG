package svgview.reactions;

import java.awt.geom.Point2D;
import java.util.Map;

import model.reaction.MReaction;

import org.w3c.dom.Document;

import svgview.shapes.SVGShape;
import svgview.shapes.SVGStimulationArrow;
import svgview.species.SVGSpecie;
import svgview.util.SVGUtil;

public class SVGPhysicalStimulation extends SVGGenericReaction {


	public SVGPhysicalStimulation(Document svgDoc, MReaction mr, Map<String, SVGSpecie> species){
		super(svgDoc, mr, species);
		this.svgDoc = svgDoc;
		this.mr = mr;			
	}

	protected SVGShape getArrow(Point2D porig, Point2D pdest) {
		return getArrow( porig, pdest, false);
	}
	
	protected SVGShape getArrow(Point2D porig, Point2D pdest, boolean reversible) {
		
		return new SVGStimulationArrow(porig, pdest, reversible, 
				  			SVGUtil.getHexColor( mr.getLine().getColor() ), 
				  			mr.getLine().getWidth() );
	}
	
	@Override
	protected SVGShape getRectangleBetween(Point2D p1, Point2D p2, String sign) {		
		super.getRectangleBetween(p1, p2, sign);
		return null;
	}
	

	@Override
	protected boolean showsSquare() {
		return false;
	}

	
}
