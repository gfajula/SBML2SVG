package svgview.species;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import model.specie.MDrug;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import svgview.SVGConfig;
import svgview.shapes.SVGComplexShape;
import svgview.shapes.SVGEllipse;
import svgview.shapes.SVGRectangle;
import svgview.shapes.SVGShape;
import svgview.shapes.SVGText;
import svgview.shapes.Segment;
import svgview.util.SVGTextRenderer;
import svgview.util.SVGUtil;

public class SVGDrug extends SVGSpecie {
	protected final static double DOUBLE_LINE_DISTANCE = 4;
	protected final static double SIN_QUARTER_PI = 0.707106781;
	private MDrug mdrug;
	private Document svgDoc;
	protected Point2D.Double center;
	protected SVGShape shape;
	
	public SVGDrug(Document svgDoc, MDrug mdrug) {
		super(svgDoc, mdrug);
		this.svgDoc = svgDoc;
		this.mdrug = mdrug;
		this.bounds = mdrug.getBounds();
		this.center = new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());

	}

	@Override
	public Rectangle2D getBBox() {
		return this.bounds;
	}


	@Override
	public Document getDocument() {
		return this.svgDoc;
	}

	protected SVGShape getRoundedRect(double x, double y, double width, double height) {		
		SVGShape rect = new SVGRectangle(x, y, width,  height, 
										 width/5,
										 height/2);
				
	    Color c = mdrug.getUsualView().getPaint().getColor();    
	    rect.setAttribute ("fill", SVGUtil.getHexColor(c));
	    rect.setAttribute ("stroke", "black");
	    rect.setAttribute ("stroke-width", "1");
	    
		SVGShape innerRect = new SVGRectangle(x + DOUBLE_LINE_DISTANCE, 
											  y + DOUBLE_LINE_DISTANCE,
											  width - DOUBLE_LINE_DISTANCE*2,
											  height - DOUBLE_LINE_DISTANCE*2,
											  width/5 - DOUBLE_LINE_DISTANCE ,
											  height/2 - DOUBLE_LINE_DISTANCE 
											  );
		innerRect.setAttribute ("fill", "none");
		innerRect.setAttribute ("stroke", "black");
		innerRect.setAttribute ("stroke-width", "1");
	    
		rect = rect.composeWith(innerRect);
	    if ( this.mdrug.isHypothetical() ) {
	    	rect.setAttribute ("style", "stroke-dasharray: 6, 3;");
	    }
		return rect;
	}
	
	protected SVGShape buildSVGShape() {
		SVGComplexShape complex = new SVGComplexShape();
    	complex.setAttribute("id", mdrug.getIdAlias());
    	
	    int h = mdrug.getHomodimer();	    
	    double heightEach = mdrug.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = mdrug.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
	    
	    this.center.x = mdrug.getBounds().getX() + widthEach/2;
	    this.center.y = mdrug.getBounds().getY() + heightEach/2;
 
	    SVGShape shp;
	    for (int hc = h-1 ; hc>=0 ; hc--) {
	    	shp = getRoundedRect(mdrug.getBounds().getX() + HOMODIMER_WIDTH*hc , 
	    						 mdrug.getBounds().getY() + HOMODIMER_HEIGHT*hc ,
	    						 widthEach,
	    						 heightEach );
	    	
	    	complex.add(shp);
	    }	   	    
	    	    
	    Element text = SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
															    	  center.x , center.y , 
															    	  mdrug.getName(), 12 );   
	    
	    if (text!=null) complex.add( new SVGText(text));
	    
		complex.add( getInfoUnit( 
				new Rectangle2D.Double(
						  this.getBBox().getX(),
						  this.getBBox().getY(), 
						  widthEach, heightEach ) ) );	
	    
	    
	    this.shape = complex;
	    
	    return complex;		
	}

	protected SVGShape buildSVGShapeSBGN() {
		SVGComplexShape complex = new SVGComplexShape();
    	complex.setAttribute("id", mdrug.getIdAlias());
    	
	    int h = Math.min( mdrug.getHomodimer(), 2 ); 
			    
	    double heightEach = mdrug.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = mdrug.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
	    
	    this.center.x = mdrug.getBounds().getX() + widthEach/2;
	    this.center.y = mdrug.getBounds().getY() + heightEach/2;
 
	    SVGShape shp;
	    for (int hc = h-1 ; hc>=0 ; hc--) {
	    	shp = getEllipse( this.center.x + HOMODIMER_WIDTH*(hc) , 
	    					  this.center.y + HOMODIMER_HEIGHT*(hc) ,
	    						 widthEach/2,
	    						 heightEach/2 );

	    	complex.add(shp);
	    }	   	    
	    	    
	    Element text = SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
															    	  center.x , center.y , 
															    	  mdrug.getName(), 12 );   
	    
	    complex.add( new SVGText(text));

		complex.add( getInfoUnit( 
				new Rectangle2D.Double(
						  this.getBBox().getX(),
						  this.getBBox().getY(), 
						  widthEach, heightEach ) ) );	
		
	    this.shape = complex;
	    
	    return complex;		
	}

	
	@Override
	public Point2D getCenter() {
		return this.center;
	}
	
	public Point2D getLinkAnchor(int type) {
		Point2D p = null;
		
	    int h = mdrug.getHomodimer();	    
	    double heightEach = mdrug.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1)  ;
	    double widthEach = mdrug.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1) ;
	    double x = mdrug.getBounds().getX() ;
	    double y = mdrug.getBounds().getY() ;
	    
		switch (type) {
		case SVGSpecie.ANCHOR_N:
			p = new Point2D.Double(x + widthEach/2,
								   y );
			break;
		case SVGSpecie.ANCHOR_NNE:	
			p = new Point2D.Double(x + widthEach/2 +  widthEach*3/20,
                    			   y );
			break;			
		case SVGSpecie.ANCHOR_NE:
			p = new Point2D.Double(x + 4*widthEach/5 ,
                    			   y   );
			break;
		case SVGSpecie.ANCHOR_ENE:
			p = new Point2D.Double(x + widthEach - (1-SIN_QUARTER_PI)*widthEach/5  ,
								   y + (1-SIN_QUARTER_PI)*heightEach/2 );
			break;				
		case SVGSpecie.ANCHOR_E:
			p = new Point2D.Double(x + widthEach ,
		                           y + heightEach/2 );
			break;
		case SVGSpecie.ANCHOR_ESE:
			p = new Point2D.Double(x + widthEach - (1-SIN_QUARTER_PI)*widthEach/5 ,
					   			   y + heightEach - (1-SIN_QUARTER_PI)*heightEach/2 ) ;
			break;				
		case SVGSpecie.ANCHOR_SE:
			p = new Point2D.Double(x + 4*widthEach/5  ,
					   			   y + heightEach  );
			break;				
		case SVGSpecie.ANCHOR_SSE:
			p = new Point2D.Double(x + widthEach/2 +  widthEach*3/20,
					   			   y + heightEach );
			break;				
		case SVGSpecie.ANCHOR_S:
			p = new Point2D.Double(x + widthEach/2,
					               y + heightEach );
			break;
		case SVGSpecie.ANCHOR_SSW:
			p = new Point2D.Double(x - widthEach/2 - widthEach*3/20  ,
					   			   y + heightEach );
			break;				
		case SVGSpecie.ANCHOR_SW:
			p = new Point2D.Double(x + 1*widthEach/5 ,
								   y + heightEach - 0 );
			break;				
		case SVGSpecie.ANCHOR_WSW:
			p = new Point2D.Double(x + (1-SIN_QUARTER_PI)*widthEach/5,
	   				   			   y + heightEach - (1-SIN_QUARTER_PI)*heightEach/2 ) ;
			break;			
		case SVGSpecie.ANCHOR_W:
			p = new Point2D.Double(x ,
                    			   y + heightEach/2 );
			break;
		case SVGSpecie.ANCHOR_WNW:
			p = new Point2D.Double(x + (1-SIN_QUARTER_PI)*widthEach/5 ,
     			   				   y + (1-SIN_QUARTER_PI)*heightEach/2 ) ;
			break;			
		case SVGSpecie.ANCHOR_NW:
			p = new Point2D.Double(x + 1*widthEach/5 ,
								   y + 0 );
			break;			
		case SVGSpecie.ANCHOR_NNW:
			p = new Point2D.Double(x + widthEach/2 - widthEach*3/20 ,
	   				   			   y );
			break;					
		}
		
		return p;
	}

	private SVGShape getEllipse(double cx, double cy, double width, double height){
		SVGShape ellipse = new SVGEllipse( cx , cy,	 width, height);
		Color c = mdrug.getUsualView().getPaint().getColor();    
		ellipse.setAttribute ("fill", SVGUtil.getHexColor(c));
		ellipse.setAttribute ("stroke", "none");
		ellipse.setAttribute ("stroke-width", "1");
	    
		return ellipse;
	}

	@Override
	protected Point2D getPointOnRect(Rectangle2D rect, double angle) {
		/* Calcular punto de interseccion con el angulo dado */
		SVGShape frame;
		if ( SVGConfig.SBGNMode ) {
			frame = new SVGRectangle( -rect.getWidth()/2, -rect.getHeight()/2,
									   rect.getWidth(), rect.getHeight(), 
									   rect.getWidth()/5, rect.getHeight()/2);
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
