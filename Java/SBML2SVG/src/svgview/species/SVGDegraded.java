package svgview.species;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import model.specie.MDegraded;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import svgview.SVGConfig;
import svgview.shapes.SVGCircle;
import svgview.shapes.SVGComplexShape;
import svgview.shapes.SVGLine;
import svgview.shapes.SVGShape;
import svgview.util.SVGUtil;

public class SVGDegraded extends SVGSpecie {
	private MDegraded md;
	private Document svgDoc;
	protected Point2D.Double center;
	private SVGShape shape;
	
	
	public SVGDegraded(Document svgDoc, MDegraded md) {
		super(svgDoc, md);
		this.svgDoc = svgDoc;
		this.md = md;
		this.bounds = md.getBounds();
		
		shape = buildSVGShape();
	}
	
	@Override
	public Document getDocument() {
		return this.svgDoc;
	}
	
	protected Element getSlash(double cx, double cy, double radius) {    
	    
	    Element slash = svgDoc.createElementNS (svgNS, "line");
	    slash.setAttributeNS (null, "stroke", "black");
	    slash.setAttributeNS (null, "x1", Double.toString(cx-radius));
	    slash.setAttributeNS (null, "x2", Double.toString(cx+radius));
	    slash.setAttributeNS (null, "y1", Double.toString(cy+radius));
	    slash.setAttributeNS (null, "y2", Double.toString(cy-radius));
	    
	    return slash;
	}
	    
	protected Element getCircle(double cx, double cy, double radius) {    
	    
	    Element circle = svgDoc.createElementNS (svgNS, "circle");	    
	    Color c = md.getUsualView().getPaint().getColor();
	    circle.setAttributeNS (null, "fill", SVGUtil.getHexColor(c));
	    circle.setAttributeNS (null, "stroke", "black");
	    
	    circle.setAttributeNS (null, "r", Double.toString(radius));
	    circle.setAttributeNS (null, "cx", Double.toString(cx));
	    circle.setAttributeNS (null, "cy", Double.toString(cy));
	    
		return circle;
	}
	
	
	public SVGShape getSVGShape() {
		return shape;
	}
	
	public SVGShape buildSVGShape() {
		double radius = Math.min(md.getBounds().getWidth(), md.getBounds().getHeight()) / 2;
		
		SVGComplexShape complex = new SVGComplexShape();
		SVGShape simpleShape;
		
		complex.setAttribute("id", md.getIdAlias()); // shp.setAttribute("style", "text-rendering: auto;");		
		
		if ( !SVGConfig.omitJavascript )
			complex.setAttribute("onclick", "infoWindow(\"" + md.getIdAlias() + "\"," +
					 "\"" + md.getName() + "\","+
					 "\"" + "Degraded" + "\"" +
					 ");");
			
		
		this.center = new Point2D.Double(md.getBounds().getCenterX(), 
										 md.getBounds().getY() + radius );
		simpleShape = new SVGCircle(center.x, center.y, radius-5);
	    Color c = md.getUsualView().getPaint().getColor();
	    simpleShape.setAttribute("fill", SVGUtil.getHexColor(c));
//	    circle.setAttributeNS (null, "stroke", "black");
		complex.add(simpleShape);
		
		simpleShape = new SVGLine(center.x - md.getBounds().getWidth()*1/5, center.y+radius, 
				                  center.x + md.getBounds().getWidth()*1/5, center.y-radius) ;
		complex.setAttribute("stroke", "black");
		complex.add(simpleShape);
		
		this.shape = complex;
		
		return complex;
	}
		
	public Point2D getLinkAnchor(int type) {
		Point2D p = null;
		
	    int h = md.getHomodimer();	    
	    double heightEach = md.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = md.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
	    double radius = Math.min(widthEach, heightEach) / 2 - 5;
	    double x = this.getCenter().getX();
	    double y = this.getCenter().getY();
		double angle = 0;
		
		switch (type) {
		case SVGSpecie.ANCHOR_N:
			angle = 4*Math.PI/8;
			break;
		case SVGSpecie.ANCHOR_NNE:	
			angle = 3*Math.PI/8;
			break;			
		case SVGSpecie.ANCHOR_NE:
			angle = 2*Math.PI/8;
			break;
		case SVGSpecie.ANCHOR_ENE:
			angle = 1*Math.PI/8;
			break;				
		case SVGSpecie.ANCHOR_E:
			angle = 0;
			break;
		case SVGSpecie.ANCHOR_ESE:
			angle = 15*Math.PI/8;
			break;				
		case SVGSpecie.ANCHOR_SE:
			angle = 14*Math.PI/8;
			break;				
		case SVGSpecie.ANCHOR_SSE:
			angle = 13*Math.PI/8;
			break;				
		case SVGSpecie.ANCHOR_S:
			angle = 12*Math.PI/8;
			break;
		case SVGSpecie.ANCHOR_SSW:
			angle = 11*Math.PI/8;
			break;				
		case SVGSpecie.ANCHOR_SW:
			angle = 10*Math.PI/8;
			break;				
		case SVGSpecie.ANCHOR_WSW:
			angle = 9*Math.PI/8;
			break;			
		case SVGSpecie.ANCHOR_W:
			angle = 8*Math.PI/8;
			break;
		case SVGSpecie.ANCHOR_WNW:
			angle = 7*Math.PI/8;
			break;			
		case SVGSpecie.ANCHOR_NW:
			angle = 6*Math.PI/8;
			break;			
		case SVGSpecie.ANCHOR_NNW:
			angle = 5*Math.PI/8;
			break;					
		}
		
		p = new Point2D.Double(
				x + radius * Math.cos( angle ),
				y - radius * Math.sin( angle )
			);
		
		return p;
	}	
	
	@Override
	public Rectangle2D getBBox() {
		return this.bounds;
	}

	@Override
	public Point2D getCenter() {
		return this.center;
	}

	@Override
	protected SVGShape buildSVGShapeSBGN() {
		return buildSVGShape();
	}

	/**
	 * Degraded no aplica 'Infomation Unit'
	 */
	@Override
	protected Point2D getPointOnRect(Rectangle2D rect, double angle) {		
		return null;
	}
	
	
	
}
