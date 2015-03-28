package svgview.species;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import model.specie.MSpecies;

import org.w3c.dom.Document;

import svgview.SVGConfig;
import svgview.shapes.SVGComplexShape;
import svgview.shapes.SVGEllipse;
import svgview.shapes.SVGShape;
import svgview.shapes.SVGText;
import svgview.shapes.Segment;
import svgview.util.SVGTextRenderer;
import svgview.util.SVGUtil;

public class SVGUnknown extends SVGSpecie {
	private MSpecies mss;
	private Document svgDoc;
	protected Point2D.Double center; 
	SVGShape shape;
	
	public SVGUnknown(Document svgDoc , MSpecies mss) {
		super(svgDoc, mss);
		this.svgDoc = svgDoc;
		this.mss = mss;
		this.bounds = mss.getBounds();
		center = new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
	}
	
//	@Override
//	public SVGShape getSVGShape() {
//		if (shape == null) return buildSVGShape();
//		return shape;		
//	}
	
	public SVGShape buildSVGShape() {
		SVGComplexShape complex = new SVGComplexShape();
		
		complex.setAttribute("id", mss.getIdAlias()); // shp.setAttribute("style", "text-rendering: auto;");
		if ( !SVGConfig.omitJavascript )
			complex.setAttribute("onclick", "infoWindow(\"" + mss.getIdAlias() + "\"," +
								 "\"" + mss.getName() + "\","+
								 "\"" + "Unknown" + "\"" +
								 ");");
    	Color c = mss.getUsualView().getPaint().getColor();
//    	double fillOpacity = Math.round(c.getAlpha()*1000.0 / 255.0 ) / 1000.0;
    	
	    int h = mss.getHomodimer();	    
	    double heightEach = mss.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = mss.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
	    
	    this.center.x = mss.getBounds().getX() + widthEach/2;
	    this.center.y = mss.getBounds().getY() + heightEach/2;
	    
	    SVGShape ellipse;
	    for (int hc = h-1 ; hc>=0 ; hc--) {	  
	    	ellipse = new SVGEllipse(mss.getBounds().getX() + widthEach / 2 + HOMODIMER_WIDTH*hc, 
		    					 mss.getBounds().getY() + heightEach/2 + HOMODIMER_HEIGHT*hc, 
		    					 widthEach/2, heightEach/2);
		    
	    	ellipse.setAttribute("fill", SVGUtil.getHexColor(c));
//	    	ellipse.setAttribute("fill-opacity", Double.toString(fillOpacity));
	    	ellipse.setAttribute("stroke", "none");

		    complex.add(ellipse);
	    }	
	    
	    
	    // TO DO: encapsular en metodo
		complex.add( new SVGText(
	    				SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
	    						mss.getBounds().getX() + 
					    		widthEach / 2 , 
					    		mss.getBounds().getY() + 
					    		heightEach / 2 , 
					    		mss.getName() ,
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
	

	public Point2D getLinkAnchor(int type) {
		Point2D p = null;
		
	    int h = mss.getHomodimer();	    
	    double heightEach = mss.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = mss.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
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
				x + widthEach/2 * Math.cos( angle ),
				y - heightEach/2 * Math.sin( angle )
			);
		
		return p;
	}
 
	@Override
	protected SVGShape buildSVGShapeSBGN() {
		return buildSVGShape();
	}	
	
	protected Point2D getPointOnRect(Rectangle2D rect, double angle ) {
		/* Calcular punto de interseccion con el angulo dado */
		SVGShape frame;

		frame = new SVGEllipse( 0, 0, rect.getWidth()/2, rect.getHeight()/2 );			

		// Punto externo al rectangulo
		Point2D pExt = new Point2D.Double( 100000 * Math.cos( angle ) , 100000 * Math.sin( angle ) );
		Segment s = new Segment( pExt.getX(), pExt.getY() , 0, 0 );
		Point2D pointAtAngle = frame.intersection( s );
		Point2D p = new Point2D.Double( rect.getCenterX() + pointAtAngle.getX(),
				                rect.getCenterY() + pointAtAngle.getY() );
		
		return p;
	}
		
}
