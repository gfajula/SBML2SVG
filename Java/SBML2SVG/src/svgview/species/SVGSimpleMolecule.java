package svgview.species;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import model.specie.MSimpleMolecule;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import svgview.SVGConfig;
import svgview.shapes.SVGCircle;
import svgview.shapes.SVGComplexShape;
import svgview.shapes.SVGCustomShape;
import svgview.shapes.SVGEllipse;
import svgview.shapes.SVGShape;
import svgview.shapes.SVGText;
import svgview.shapes.Segment;
import svgview.util.SVGTextRenderer;
import svgview.util.SVGUtil;

public class SVGSimpleMolecule extends SVGSpecie {
	private MSimpleMolecule msm;	
	private Document svgDoc;
	protected Point2D.Double center; 
	SVGShape shape;
	
	public SVGSimpleMolecule(Document svgDoc , MSimpleMolecule msm) {
		super(svgDoc, msm);
		this.svgDoc = svgDoc;
		this.msm = msm;
		this.bounds = msm.getBounds();
		center = new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
	}
	
	@Override
	public SVGShape getSVGShape() {
		if (shape == null) return buildSVGShape();
		return shape;				
	}
	 
	public SVGShape buildSVGShape() {
		if ( SVGConfig.SBGNMode ) return buildSVGShapeSBGN();
		
		SVGComplexShape complex = new SVGComplexShape();
		
		complex.setAttribute("id", msm.getIdAlias()); // shp.setAttribute("style", "text-rendering: auto;");
		if ( !SVGConfig.omitJavascript )
			complex.setAttribute("onclick", "infoWindow(\"" + msm.getIdAlias() + "\"," +
								 "\"" + msm.getName() + "\","+
								 "\"" + "Unknown" + "\"" +
								 ");");
    	Color c = msm.getUsualView().getPaint().getColor();
//    	double fillOpacity = Math.round(c.getAlpha()*1000.0 / 255.0 ) / 1000.0;
    	
	    int h = msm.getHomodimer();	    
	    double heightEach = msm.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = msm.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
	    
	    this.center.x = msm.getBounds().getX() + widthEach/2;
	    this.center.y = msm.getBounds().getY() + heightEach/2;
	    
	    SVGShape ellipse;
	    for (int hc = h-1 ; hc>=0 ; hc--) {	  
	    	ellipse = new SVGEllipse(msm.getBounds().getX() + widthEach / 2 + HOMODIMER_WIDTH*hc, 
	    							 msm.getBounds().getY() + heightEach/2 + HOMODIMER_HEIGHT*hc, 
	    							 widthEach/2, heightEach/2);
		    
	    	ellipse.setAttribute("fill", SVGUtil.getHexColor(c));
//	    	ellipse.setAttribute("fill-opacity", Double.toString(fillOpacity));
	    	ellipse.setAttribute("stroke", "black");
		    if ( this.msm.isHypothetical() ) {
		    	ellipse.setAttribute ("style", "stroke-dasharray: 6, 3;");
		    }
		    complex.add(ellipse);
	    }	
	    
	    
	    // TO DO: encapsular en metodo
		complex.add( new SVGText(
	    				SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
	    						msm.getBounds().getX() + 
					    		widthEach / 2 , 
					    		msm.getBounds().getY() + 
					    		heightEach / 2 , 
					    		msm.getName() ,
					    		12 ))
	    		);
    	
		complex.add( getInfoUnit( 
				new Rectangle2D.Double(
						  this.getBBox().getX(),
						  this.getBBox().getY(), 
						  widthEach, heightEach ) ) );	
		
		this.shape = complex;
		return complex; 
	}
	
	public Point2D getLinkAnchor(int type) {
		Point2D p = null;
		
	    int h = msm.getHomodimer();	    
	    double heightEach = msm.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = msm.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
	    
	    if ( SVGConfig.SBGNMode ) {
	    	heightEach = Math.min( heightEach,widthEach  );
	    	widthEach = heightEach;
	    }
	    
	    
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
			angle = Math.PI;
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
				x + widthEach/2 * Math.cos( angle ),
				y - heightEach/2 * Math.sin( angle )
			);
		
		return p;
	}	
		
	public Document getDocument() {
		return this.svgDoc;
	}
	
	@Override
	public Rectangle2D getBBox() {
		return this.bounds;
	}

	@Override
	public Point2D getCenter() {
		return center;
	}
	
	protected SVGShape buildSVGShapeSBGN() {
		SVGComplexShape complex = new SVGComplexShape();
    	complex.setAttribute("id", msm.getIdAlias());
    	
	    int h = Math.min( msm.getHomodimer(), 2 ); 
				
	    double heightEach = msm.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = msm.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
	    double radius = Math.min(widthEach, heightEach) / 2;
	    
	    this.center = new Point2D.Double( msm.getBounds().getCenterX() ,
	    		msm.getBounds().getCenterY()  );
 
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
															    	  msm.getName(), 12 );   
	    
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
	
	protected SVGShape getCircle(double cx, double cy, double radius) {	    
		SVGShape circle = new SVGCircle(cx, cy, radius);	    
	    Color c = msm.getUsualView().getPaint().getColor();
	    circle.setAttribute("fill", SVGUtil.getHexColor(c));
	    circle.setAttribute("stroke", "black");	   
	    
	    if ( this.msm.isHypothetical() ) {
	    	circle.setAttribute ("style", "stroke-dasharray: 6, 3;");
	    }
	    
		return circle;
	}

	private SVGShape getClonedCircle(double cx, double cy, double r ) {
		SVGComplexShape shape = new SVGComplexShape();
		
		SVGShape circle = new SVGCircle(cx, cy, r );	    
	    Color c = msm.getUsualView().getPaint().getColor();
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
	    if ( this.msm.isHypothetical() ) {
	    	circle.setAttribute ("style", "stroke-dasharray: 6, 3;");
	    }
	    
	    shape.add( circle );
	    
		return shape;
	}
	
	protected Point2D getPointOnRect(Rectangle2D rect, double angle ) {
		/* Calcular punto de interseccion con el angulo dado */
		SVGShape frame;
		if ( SVGConfig.SBGNMode ) {
			frame = new SVGCircle( 0, 0, Math.min(rect.getWidth()/2, rect.getHeight()/2) );
		} else {
			frame = new SVGEllipse( 0, 0, rect.getWidth()/2, rect.getHeight()/2 );			
		}
		// Punto externo al rectangulo
		Point2D pExt= new Point2D.Double( 100000 * Math.cos( angle ) , 100000 * Math.sin( angle ) );
		Segment s = new Segment( pExt.getX(), pExt.getY() , 0, 0 );
		Point2D pointAtAngle = frame.intersection( s );
		Point2D p = new Point2D.Double( rect.getCenterX() + pointAtAngle.getX(),
				                rect.getCenterY() + pointAtAngle.getY() );
		
		return p;
	}
}
