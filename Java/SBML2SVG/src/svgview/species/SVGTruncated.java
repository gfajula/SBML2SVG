package svgview.species;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import model.specie.protein.MResidue;
import model.specie.protein.MTruncated;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import svgcontroller.SBML2SVGException;
import svgview.SVGConfig;
import svgview.shapes.SVGComplexShape;
import svgview.shapes.SVGShape;
import svgview.shapes.SVGText;
import svgview.shapes.SVGTruncatedShape;
import svgview.util.SVGTextRenderer;
import svgview.util.SVGUtil;

public class SVGTruncated extends SVGProtein {
	public static final double NEG_CORNER_RADIUS = 1.5 ;	
	public static final int CORNER_RADIUS = 10;	 
	
	public SVGTruncated(Document svgDoc , MTruncated mp) {
		super(svgDoc, mp);		
	}
	
	
	protected SVGShape getDashedRect(double x, double y, double width, double height) {
		SVGShape rect = new SVGTruncatedShape(x,// - ACTIVITY_MARGIN ,
											  y,// - ACTIVITY_MARGIN , 
											  width,// + ACTIVITY_MARGIN*2 , 
											  height,// + ACTIVITY_MARGIN*2 , 
											  CORNER_RADIUS,// + ACTIVITY_MARGIN,
											  ACTIVITY_MARGIN);	    
	    rect.setAttribute("fill", "none");
//	    rect.setAttribute("fill-opacity", "0.5");
	    rect.setAttribute("style", "stroke-dasharray: 4, 2;");
	    rect.setAttribute("stroke", "black");
	    rect.setAttribute("stroke-width", "1");
	    
		return rect;
	}
	
	protected SVGShape getDashedFill(double x, double y, double width, double height) {
		SVGShape rect = new SVGTruncatedShape(x,// - ACTIVITY_MARGIN + 1,
											  y,// - ACTIVITY_MARGIN + 1, 
											  width,// + ACTIVITY_MARGIN*2 - 2, 
											  height,// + ACTIVITY_MARGIN*2 - 2, 
											  CORNER_RADIUS,// + ACTIVITY_MARGIN - 1,
											  ACTIVITY_MARGIN - 1);	    
	    rect.setAttribute("stroke-width", "1");
	    rect.setAttribute("fill", "white");
	    rect.setAttribute("stroke", "none");
	    
		return rect;
	}
	
	protected SVGShape getRoundedRect(double x, double y, double width, double height) {
		SVGShape rect = new SVGTruncatedShape(x, y, width, height, CORNER_RADIUS, 0);	    
	    Color c = mp.getUsualView().getPaint().getColor();    
	    rect.setAttribute("fill", SVGUtil.getHexColor(c));
	    rect.setAttribute("stroke", "black");
	    rect.setAttribute("stroke-width", "1");
	    if ( this.mp.isHypothetical() ) {
	    	rect.setAttribute ("style", "stroke-dasharray: 6, 3;");
	    }
		return rect;
	}
	
	public SVGShape buildSVGShape() {		
		SVGComplexShape complex = new SVGComplexShape();		
		complex.setAttribute("id", mp.getIdAlias());	
		if ( !SVGConfig.omitJavascript )
	    	complex.setAttribute("onclick", "infoWindow(\"" + mp.getIdAlias() + "\"," +
					 "\"" + mp.getName() + "\","+
					 "\"" + "Ion Channel" + "\"" +
					 ");");
    	    
	    int h = mp.getHomodimer();	    
	    double heightEach = mp.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = mp.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
	    
	    this.center.x = mp.getBounds().getX() + widthEach/2;
	    this.center.y = mp.getBounds().getY() + heightEach/2;
	    
	    SVGShape shp;
	    if (active) {
	    	// Primero los bordes	    	
	    	for (int hc = h-1 ; hc>=0 ; hc--) {		    		
	    		shp = getDashedRect(	    				
	    				 mp.getBounds().getX() + HOMODIMER_WIDTH*hc , 
	    				 mp.getBounds().getY() + HOMODIMER_HEIGHT*hc , 
	    				 widthEach , 
	    				 heightEach 
	    		);  	
	    		complex.add(shp);		    		
	    	}
	    	// Despues los rellenos, para que no se solapen las l�neas.
	    	for (int hc = h-1 ; hc>=0 ; hc--) {	
	    		shp = getDashedFill(
	    				 mp.getBounds().getX() + HOMODIMER_WIDTH*hc , 
	    				 mp.getBounds().getY() + HOMODIMER_HEIGHT*hc , 
	    				 widthEach , 
	    				 heightEach 
	    		);  	
	    		complex.add(shp);   		
	    	}	    	
	    }	    
	    
	    for (int hc = h-1 ; hc>=0 ; hc--) {	    		    	
	    	shp = this.getRoundedRect(
	    				 mp.getBounds().getX() + HOMODIMER_WIDTH*hc  , 
	    				 mp.getBounds().getY() + HOMODIMER_HEIGHT*hc  , 
	    				 widthEach , 
	    				 heightEach 
	    		);  
	    	
	    	complex.add(shp); 	    	    	
	    }	    
	    
	    Rectangle2D.Double rect = new Rectangle2D.Double(
    			mp.getBounds().getX(), mp.getBounds().getY(), 
    			widthEach, heightEach);
	    
	    // TO DO: encapsular en metodo
	    if ((mp.getResidues()!=null)) {
	    	
	    	SVGShape res, resText;
		    for (MResidue mres : mp.getResidues()) {		    
		    	Point2D.Double p = getAngleOnRect(rect, mres.getAngle()  );
		    	res = getResidue( mres , p );
		    	complex.add(res);		    	
		    	resText = new SVGText( SVGTextRenderer.getInstance().drawTextCentered(svgDoc, p.x, p.y , mres.getState(), 9 ));
		    	complex.add(resText);
		    }	    
	    }
	    
	    addBindingRegions(complex, rect, true);
	    addStructuralState(complex, rect);
	    
	    complex.add( new SVGText( SVGTextRenderer.getInstance().drawTextCentered( svgDoc, 
						    		mp.getBounds().getX() + widthEach / 2 , 
						    		mp.getBounds().getY() + heightEach / 2 , 
						    		mp.getName(), 12)));    
	    
	   	    
	    complex.add( getInfoUnit(
	    		new Rectangle2D.Double( this.getMspecies().getBounds().getX() , 
	    								this.getMspecies().getBounds().getY(), 
	    								widthEach, 
	    								heightEach )
	    		) );
	    
	    this.shape = complex;
	    
	    return complex;
	}
		
	
	public Point2D getLinkAnchor(int type) {
		Point2D p = null;
		
	    int h = mp.getHomodimer();	    
	    double heightEach = mp.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = mp.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
	    
		switch (type) {
		case SVGSpecie.ANCHOR_N:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach/2,
								   mp.getBounds().getY() );
			break;
		case SVGSpecie.ANCHOR_NNE:	
			p = new Point2D.Double(mp.getBounds().getX() + 3*widthEach/4 ,
                    			   mp.getBounds().getY() );
			break;			
		case SVGSpecie.ANCHOR_NE:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach ,
                    			   mp.getBounds().getY() );
			break;
		case SVGSpecie.ANCHOR_ENE:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach ,
								   mp.getBounds().getY() + heightEach*0.3 );
			break;				
		case SVGSpecie.ANCHOR_E:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach ,
		                           mp.getBounds().getY() + heightEach*0.6 );
			break;
		case SVGSpecie.ANCHOR_ESE:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach*0.8 ,
					   			   mp.getBounds().getY() + heightEach*0.4 );
			break;				
		case SVGSpecie.ANCHOR_SE:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach*0.8 ,
					   			   mp.getBounds().getY() + heightEach*0.7 );
			break;				
		case SVGSpecie.ANCHOR_SSE:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach*0.8 ,
					   			   mp.getBounds().getY() + heightEach );
			break;				
		case SVGSpecie.ANCHOR_S:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach/2,
					               mp.getBounds().getY() + heightEach );
			break;
		case SVGSpecie.ANCHOR_SSW:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach/4 ,
					   			   mp.getBounds().getY() + heightEach );
			break;				
		case SVGSpecie.ANCHOR_SW:
			p = new Point2D.Double(mp.getBounds().getX() + NEG_CORNER_RADIUS ,
								   mp.getBounds().getY() + heightEach - NEG_CORNER_RADIUS );
			break;				
		case SVGSpecie.ANCHOR_WSW:
			p = new Point2D.Double(mp.getBounds().getX() ,
	   				   			   mp.getBounds().getY() + 3*heightEach/4 );
			break;			
		case SVGSpecie.ANCHOR_W:
			p = new Point2D.Double(mp.getBounds().getX() ,
                    			   mp.getBounds().getY() + heightEach/2 );
			break;
		case SVGSpecie.ANCHOR_WNW:
			p = new Point2D.Double(mp.getBounds().getX() ,
     			   				   mp.getBounds().getY() + heightEach/4 );
			break;			
		case SVGSpecie.ANCHOR_NW:
			p = new Point2D.Double(mp.getBounds().getX() + NEG_CORNER_RADIUS ,
								   mp.getBounds().getY() + NEG_CORNER_RADIUS );
			break;			
		case SVGSpecie.ANCHOR_NNW:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach/4 ,
	   				   			   mp.getBounds().getY() );
			break;					
		}
		
		return p;
	}
	
	protected Point.Double getAngleOnRect(Rectangle2D r, double angle) {
		double cx = r.getCenterX();
		double cy = r.getCenterY();
		double signX, signY;
		if (angle > Math.PI) {
			signY = -1;
		} else signY = 1;
		
		if ((angle > Math.PI/2) && (angle < 3*Math.PI/2)) {
			signX = -1;
		} else signX = 1;
		
		double hx, hy;
		double radius = Math.sqrt(2);
		hy = Math.sin(angle) * radius;
		
		if ( Math.abs(hy) < 1 ) { 
			if ((signX==1) && (hy<0)) {
				return new Point2D.Double(r.getX() + r.getWidth()*0.8 , cy - hy*r.getHeight()/2 );
			} else
				return new Point2D.Double(cx + signX*r.getWidth()/2 , cy - hy*r.getHeight()/2 );
		}
		
		hx = Math.cos(angle) * radius;
		return new Point2D.Double(cx + hx*r.getWidth()/2, cy - signY*r.getHeight()/2);
	}
	
	public void svgPaint(Element docParent) throws SBML2SVGException {
		
		if (mp.getViewState().equalsIgnoreCase("brief")) {
			;;; // do nothing
		} else {
			super.svgPaint(docParent);
		}
		
	}
	
	
}
