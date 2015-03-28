package svgview.species;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import model.specie.MIon;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import svgcontroller.SBML2SVGException;
import svgview.SVGConfig;
import svgview.shapes.SVGCircle;
import svgview.shapes.SVGComplexShape;
import svgview.shapes.SVGCustomShape;
import svgview.shapes.SVGShape;
import svgview.shapes.SVGText;
import svgview.util.SVGTextRenderer;
import svgview.util.SVGUtil;

public class SVGIon extends SVGSpecie {
	public static final int HOMODIMER_WIDTH = 6;
	public static final int HOMODIMER_HEIGHT = 6;
	private MIon mion;
	private Document svgDoc;
	protected Point2D.Double center;
	protected SVGShape shape;
	
	
	public SVGIon(Document svgDoc, MIon mion) {
		super(svgDoc, mion);
		this.svgDoc = svgDoc;
		this.mion = mion;
		this.bounds = mion.getBounds();
		this.buildSVGShape();
	}
	@Override
	public Document getDocument() {
		return this.svgDoc;
	}
		
	protected SVGShape getCircle(double cx, double cy, double radius) {	    
		SVGShape circle = new SVGCircle(cx, cy, radius);	    
	    Color c = mion.getUsualView().getPaint().getColor();
	    circle.setAttribute("fill", SVGUtil.getHexColor(c));
	    circle.setAttribute("stroke", "black");	   
	    if ( this.mion.isHypothetical() ) {
	    	circle.setAttribute ("style", "stroke-dasharray: 6, 3;");
	    }
		return circle;
	}
	
	
	public Point2D getLinkAnchor(int type) {
		Point2D p = null;
		
	    int h = mion.getHomodimer();	    
	    double heightEach = mion.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = mion.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
	    double radius = Math.min(widthEach, heightEach) / 2;
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
	
	protected SVGShape buildSVGShape() {
		SVGComplexShape complex = new SVGComplexShape();
    	complex.setAttribute("id", mion.getIdAlias());
    	
	    int h = mion.getHomodimer();	
		if (SVGConfig.SBGNMode) {
			h = Math.min( mion.getHomodimer(), 2 ); 
		}
		
	    double heightEach = mion.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = mion.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
	    double radius = Math.min(widthEach, heightEach) / 2;
	    
	    this.center = new Point2D.Double( mion.getBounds().getCenterX() ,
				  	                      mion.getBounds().getY() + radius);
 
	    SVGShape shp;
	    for (int hc = h-1 ; hc>=0 ; hc--) {
	    	
	    	if (this.isClone()) {
	    		shp = getClonedCircle(center.x + HOMODIMER_WIDTH*hc , 
    					center.y + HOMODIMER_HEIGHT*hc ,
    					radius );
	    	} else {
	    		shp = getCircle(center.x + HOMODIMER_WIDTH*hc , 
    					center.y + HOMODIMER_HEIGHT*hc ,
    					radius );
	    	}
		    	
	    	complex.add(shp);
	    }	   	    
	    	    
	    Element text = SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
															    	  center.x , center.y , 
															    	  mion.getName(), 12 );   
	    
	    if (text!=null) complex.add( new SVGText(text));
	    
//	    Element bound = getRect(mion.getBounds().getX(), mion.getBounds().getY(), mion.getBounds().getWidth(), mion.getBounds().getHeight());
//	    svgSpecie.appendChild(bound);
	    
	    
		complex.add( getInfoUnit( 
				new Rectangle2D.Double(
						  this.getBBox().getX(),
						  this.getBBox().getY(), 
						  widthEach, heightEach ) ) );	
	    
	    this.shape = complex;	    
	    
	    return complex;		
	}

	private SVGShape getClonedCircle(double cx, double cy, double r ) {
		SVGComplexShape shape = new SVGComplexShape();
		
		SVGShape circle = new SVGCircle(cx, cy, r );	    
	    Color c = mion.getUsualView().getPaint().getColor();
	    circle.setAttribute("fill", SVGUtil.getHexColor(c));
	    circle.setAttribute("stroke", "none");
		
	    shape.add( circle );		
		
		Element path = svgDoc.createElementNS(svgNS, "path");
	    path.setAttributeNS (null, "d", 
	    		"M" + Double.toString( cx - r*0.8978 ) + " " + Double.toString( cy+r*1/2 ) + " " +
	    		"L" + Double.toString( cx - r*0.8978 ) + " " + Double.toString( cy+r*1/2 ) + " " +
	    		"C" + Double.toString( cx - r*0.709819048 ) + " " + Double.toString( cy + r*0.798902326 ) + " " +
	    			  Double.toString( cx - r*0.378952381 ) + " " + Double.toString( cy + r ) + " " +
	    			  Double.toString( cx ) + " " + Double.toString( cy + r ) + " " +
	    		
	    		"C" + Double.toString( cx + r*0.370139535 ) + " " + Double.toString( cy + r ) + " " +
  			  		  Double.toString( cx + r*0.693311628 ) + " " + Double.toString( cy + r*0.798902326 ) + " " +
  			  		  Double.toString( cx + r*0.866218605 ) + " " + Double.toString( cy+r*1/2 ) + " " +
	    		
	    		"Z"
	    		);
		SVGShape cloneMarker = new SVGCustomShape( path ); 
	    
		shape.add( cloneMarker );
		cloneMarker.setAttribute("stroke", "none");
		cloneMarker.setAttribute ("fill", "#C4C4C4" );
		
		circle = new SVGCircle(cx, cy, r );	    
	    circle.setAttribute("fill", "none" );
	    circle.setAttribute("stroke", "black");	   
	    if ( this.mion.isHypothetical() ) {
	    	circle.setAttribute ("style", "stroke-dasharray: 6, 3;");
	    }
	    
	    shape.add( circle );
	    
		return shape;
	}
	
	@Override
	public void svgPaint(Element docParent) throws SBML2SVGException {
		if (mion.getViewState().equalsIgnoreCase("brief")) {
			;;; // do nothing
		} else {
			super.svgPaint(docParent);
		}
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

	@Override
	protected Point2D getPointOnRect(Rectangle2D rect, double angle) {
		double radius = Math.min( rect.getWidth(), rect.getHeight() ) / 2;
		
		/* Calcular punto sobre el circulo*/
		Point2D p = new Point2D.Double( radius * Math.cos( angle ) + rect.getCenterX(),
									     radius * Math.sin( angle ) + rect.getCenterY() );
		
		return p;
		
	}

}
