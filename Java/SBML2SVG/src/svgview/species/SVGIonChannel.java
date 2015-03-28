package svgview.species;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import model.specie.protein.MIonChannel;
import model.specie.protein.MResidue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import svgcontroller.SBML2SVGException;
import svgview.SVGConfig;
import svgview.shapes.SVGComplexShape;
import svgview.shapes.SVGShape;
import svgview.shapes.SVGText;
import svgview.util.SVGTextRenderer;

public class SVGIonChannel extends SVGProtein {
//	public static final int HOMODIMER_WIDTH = 6;
//	public static final int HOMODIMER_HEIGHT = 6;
//	public static final int CORNER_RADIUS = 5;
	public static final int ACTIVITY_MARGIN = 20;
	private boolean active = false;
	private MIonChannel mic;
	private Document svgDoc;
		
	private double x;
	private double y;
	private double width;
	private double height;
	
	public SVGIonChannel(Document svgDoc , MIonChannel mic) {
		super(svgDoc, mic);
		this.svgDoc = svgDoc;
		this.mic = mic;
		this.active = mic.getActivity().equalsIgnoreCase("active");
		this.x = mic.getBounds().getX();
		this.y = mic.getBounds().getY();
		this.width = mic.getBounds().getWidth();
		this.height = mic.getBounds().getHeight();
		this.buildSVGShape();
	}
	
	protected Point.Double getAngleOnRect(Rectangle2D r, double leftWidth, double angle) {
//		double cx = r.getCenterX();
		double cx = r.getX() + leftWidth/2.0;
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
//			return new Point2D.Double(cx + signX*r.getWidth()/2 , cy - hy*r.getHeight()/2 );
			if (signX==1) {
				return new Point2D.Double(r.getMaxX(), cy - hy*r.getHeight()/2 );
			} else {
				return new Point2D.Double(r.getX(), cy - hy*r.getHeight()/2 );
			}
		}
		
		hx = Math.cos(angle) * radius;
//		return new Point2D.Double(cx + hx*r.getWidth()/2, cy - signY*r.getHeight()/2);
		return new Point2D.Double(cx + hx*leftWidth/2, cy - signY*r.getHeight()/2);
	}

	public SVGShape buildSVGShape() {
    	SVGComplexShape complex = new SVGComplexShape();
    	complex.setAttribute("id", mic.getIdAlias());
    			
    	if ( !SVGConfig.omitJavascript )
	    	complex.setAttribute("onclick", "infoWindow(\"" + mic.getIdAlias() + "\"," +
					 "\"" + mic.getName() + "\","+
					 "\"" + "Ion Channel" + "\"" +
					 ");");
		
	    int h = mic.getHomodimer();	    
	    double heightEach = this.height - HOMODIMER_HEIGHT * (h-1);
	    double widthEach = this.width - HOMODIMER_WIDTH * (h-1);    	

	    
    	double leftWidth;
    	if (active) {
    		leftWidth = widthEach-ACTIVITY_MARGIN*2;
    	} else {
    		leftWidth = widthEach-ACTIVITY_MARGIN;
    	}
    	
	    this.center = new Point2D.Double( mic.getBounds().getX() + leftWidth/2,
				  						  mic.getBounds().getY() + heightEach/2);

    	
    	if (leftWidth < 0) {
    		System.err.println("Warning: " + mic.getIdAlias() + "'s width too low!");
    	} else {
    		SVGShape shp;
    		for (int hc = h-1 ; hc>=0 ; hc--) {
    		
	        	shp = getRoundedRect(x + HOMODIMER_WIDTH*hc, 
	        						 y + HOMODIMER_HEIGHT*hc, 
	        						 leftWidth, 
	        						 heightEach );
	        	complex.add(shp);
	        	
	        	shp = getRoundedRect(x + HOMODIMER_WIDTH*hc + widthEach - ACTIVITY_MARGIN ,
									 y + HOMODIMER_HEIGHT*hc, 
									 ACTIVITY_MARGIN, 
									 heightEach );    
	        	complex.add(shp);    
        	
    		}
        	
    	}
    	    
    	Rectangle2D.Double rect = new Rectangle2D.Double(
    			mp.getBounds().getX(), mp.getBounds().getY(), 
    			widthEach, heightEach);
	    if ((mp.getResidues()!=null)) {	    	
	    	SVGShape res, resText;
		    for (MResidue mres : mp.getResidues()) {		    
		    	Point2D.Double p = getAngleOnRect(rect, leftWidth, mres.getAngle());
		    	res = getResidue( mres , p );
		    	complex.add(res);		    	
		    	resText = new SVGText( SVGTextRenderer.getInstance().drawTextCentered(svgDoc, p.x, p.y , mres.getState(), 9 ));
		    	complex.add(resText);
		    }    	   
	    }    
	    
	    addBindingRegions(complex, rect, true);
	    addStructuralState(complex, rect);
	    
	    Element text = SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
	    		center.x , 
	    		center.y , 
	    		mic.getName(),
	    		12);    
	    complex.add(new SVGText(text));
	        	
		complex.add( getInfoUnit( 
				new Rectangle2D.Double(
						  this.getBBox().getX(),
						  this.getBBox().getY(), 
						  widthEach, heightEach ) ) );	
		
	    this.shape = complex;
    	return complex;
	}

	protected SVGShape buildSVGShapeSBGN() throws SBML2SVGException {
		return super.buildSVGShapeSBGN();
	}

}
