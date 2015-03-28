package svgview.species;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import model.specie.protein.MReceptor;
import model.specie.protein.MResidue;

import org.w3c.dom.Document;

import svgview.SVGConfig;
import svgview.shapes.SVGComplexShape;
import svgview.shapes.SVGPolygon;
import svgview.shapes.SVGShape;
import svgview.shapes.SVGText;
import svgview.util.SVGTextRenderer;
import svgview.util.SVGUtil;

public class SVGReceptor extends SVGProtein {
	public static final int HOMODIMER_WIDTH = 6;
	public static final int HOMODIMER_HEIGHT = 6;
	public static final int ACTIVITY_MARGIN = 4;
	public static final double ACTIVITY_MARGIN_INNER = ACTIVITY_MARGIN-0.5;
	private boolean active = false;
	private MReceptor mr;
	private Document svgDoc;
	private SVGPolygon poly = null;
	
	public SVGReceptor(Document svgDoc , MReceptor mr) {
		super(svgDoc, mr);
		this.svgDoc = svgDoc;
		this.mr = mr;
		this.active = mr.getActivity().equalsIgnoreCase("active");
		this.bounds = mr.getBounds();
		this.center = new Point2D.Double();
	}
	
	@Override
	public Document getDocument() {
		return this.svgDoc;
	}
	
	public Point2D getLinkAnchor(int type) {
		Point2D p = null;
		
	    int h = mp.getHomodimer();	    
	    double heightEach = mp.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = mp.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
	    
		switch (type) {
		case SVGSpecie.ANCHOR_N:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach/2,
								   mp.getBounds().getY() + heightEach*0.2 );
			break;
		case SVGSpecie.ANCHOR_NNE:	
			p = new Point2D.Double(mp.getBounds().getX() + 3*widthEach/4 ,
                    			   mp.getBounds().getY() + heightEach*0.1 );
			break;			
		case SVGSpecie.ANCHOR_NE:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach ,
                    			   mp.getBounds().getY() );
			break;
		case SVGSpecie.ANCHOR_ENE:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach ,
								   mp.getBounds().getY() + heightEach*0.2 );
			break;				
		case SVGSpecie.ANCHOR_E:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach ,
		                           mp.getBounds().getY() + heightEach*0.4 );
			break;
		case SVGSpecie.ANCHOR_ESE:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach ,
					   			   mp.getBounds().getY() + heightEach*0.6 );
			break;				
		case SVGSpecie.ANCHOR_SE:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach ,
					   			   mp.getBounds().getY() + heightEach*0.8 );
			break;				
		case SVGSpecie.ANCHOR_SSE:
			p = new Point2D.Double(mp.getBounds().getX() + 3*widthEach/4 ,
					   			   mp.getBounds().getY() + heightEach*0.9 );
			break;				
		case SVGSpecie.ANCHOR_S:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach/2,
					               mp.getBounds().getY() + heightEach );
			break;
		case SVGSpecie.ANCHOR_SSW:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach/4 ,
					   			   mp.getBounds().getY() + heightEach*0.9 );
			break;				
		case SVGSpecie.ANCHOR_SW:
			p = new Point2D.Double(mp.getBounds().getX() ,
								   mp.getBounds().getY() + heightEach*0.8 );
			break;				
		case SVGSpecie.ANCHOR_WSW:
			p = new Point2D.Double(mp.getBounds().getX() ,
	   				   			   mp.getBounds().getY() + heightEach*0.6 );
			break;			
		case SVGSpecie.ANCHOR_W:
			p = new Point2D.Double(mp.getBounds().getX() ,
                    			   mp.getBounds().getY() + heightEach*0.4 );
			break;
		case SVGSpecie.ANCHOR_WNW:
			p = new Point2D.Double(mp.getBounds().getX() ,
     			   				   mp.getBounds().getY() + heightEach*0.2 );
			break;			
		case SVGSpecie.ANCHOR_NW:
			p = new Point2D.Double(mp.getBounds().getX() ,
								   mp.getBounds().getY() );
			break;			
		case SVGSpecie.ANCHOR_NNW:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach/4 ,
	   				   			   mp.getBounds().getY() + heightEach*0.1 );
			break;					
		}
		
		return p;
	}
	
	private SVGPolygon getPolygon(double x, double y, double width, double height) {
		SVGPolygon shp = new SVGPolygon(Double.toString(x) + ", " + Double.toString(y) + " " +
			Double.toString(x+width/2) + ", " + Double.toString(y+height*0.2) + " " +
			Double.toString(x+width) + ", " + Double.toString(y) + " " +
			Double.toString(x+width) + ", " + Double.toString(y+height*0.8) + " " +
			Double.toString(x+width/2) + ", " + Double.toString(y+height) + " " +
			Double.toString(x) + ", " + Double.toString(y+height*0.8)	    		
			);
	    Color c = mr.getUsualView().getPaint().getColor();    
	    shp.setAttribute("fill", SVGUtil.getHexColor(c));
	    shp.setAttribute("stroke", "black");
	    shp.setAttribute("stroke-width", "1");
	    shp.setAttribute("stroke-linejoin", "round");		
	    
	    if ( this.mr.isHypothetical() ) {
	    	shp.setAttribute ("style", "stroke-dasharray: 6, 3;");
	    }
	    
		return shp;
	}
	
	private SVGShape getDashedPolygon(double x, double y, double width, double height) {
		SVGShape shp = new SVGPolygon(
				Double.toString(x-ACTIVITY_MARGIN) + ", " + Double.toString(y-ACTIVITY_MARGIN) + " " +
	    		Double.toString(x+width/2) + ", " + Double.toString(y+height*0.2-ACTIVITY_MARGIN) + " " +
	    		Double.toString(x+width+ACTIVITY_MARGIN) + ", " + Double.toString(y-ACTIVITY_MARGIN) + " " +
	    		Double.toString(x+width+ACTIVITY_MARGIN) + ", " + Double.toString(y+height*0.8+ACTIVITY_MARGIN) + " " +
	    		Double.toString(x+width/2) + ", " + Double.toString(y+height+ACTIVITY_MARGIN) + " " +
	    		Double.toString(x-ACTIVITY_MARGIN) + ", " + Double.toString(y+height*0.8+ACTIVITY_MARGIN),
	    		"stroke-dasharray: 4, 2;"
				);
		        
		    shp.setAttribute("fill", "none");
		    shp.setAttribute("stroke", "black");
		    shp.setAttribute("stroke-width", "1");
		    shp.setAttribute("stroke-linejoin", "round");		
			
			return shp;		
	}
	

	protected SVGShape getDashedFill(double x, double y, double width, double height) {
		SVGShape shp = new SVGPolygon(
				Double.toString(x-ACTIVITY_MARGIN_INNER) + ", " + Double.toString(y-ACTIVITY_MARGIN_INNER) + " " +
	    		Double.toString(x+width/2) + ", " + Double.toString(y+height*0.2-ACTIVITY_MARGIN_INNER) + " " +
	    		Double.toString(x+width+ACTIVITY_MARGIN_INNER) + ", " + Double.toString(y-ACTIVITY_MARGIN_INNER) + " " +
	    		Double.toString(x+width+ACTIVITY_MARGIN_INNER) + ", " + Double.toString(y+height*0.8+ACTIVITY_MARGIN_INNER) + " " +
	    		Double.toString(x+width/2) + ", " + Double.toString(y+height+ACTIVITY_MARGIN_INNER) + " " +
	    		Double.toString(x-ACTIVITY_MARGIN_INNER) + ", " + Double.toString(y+height*0.8+ACTIVITY_MARGIN_INNER)
				);
		        
		    shp.setAttribute("fill", "white");
		    shp.setAttribute("stroke", "none");
		    shp.setAttribute("stroke-width", "1");
		    shp.setAttribute("stroke-linejoin", "round");		
			
			return shp;		
	}
	

	protected Point.Double getAngleOnPolygon(Rectangle2D.Double r, double angle) {
		Vector<Line2D.Double> polygon = new Vector<Line2D.Double>(); // poligono inscrito en un rectangulo 0,0 -> 1,1;
		polygon.add( new Line2D.Double(0, 0, 0.5, 0.2) );
		polygon.add( new Line2D.Double(0.5, 0.2, 1, 0) );
		polygon.add( new Line2D.Double(1, 0, 1, 0.8) );
		polygon.add( new Line2D.Double(1, 0.8, 0.5, 1) );
		polygon.add( new Line2D.Double(0.5, 1, 0, 0.8) );
		polygon.add( new Line2D.Double(0, 0.8, 0, 0) );
		Point2D.Double p1 = SVGUtil.getIntersectionPoint(polygon, 0.5, 0.5, angle);
		
		if (p1==null) {
			new Exception("Error al calcular punto " + angle + " en el poligono").printStackTrace();				
		}
		
		Point2D.Double p = new Point2D.Double(p1.x * r.getWidth() +r.getX() , p1.y * r.getHeight() + r.getY());
		
		return p;
		
//		double signX, signY;
//		if (angle > Math.PI) {
//			signY = -1;
//		} else signY = 1;
//		
//		if ((angle > Math.PI/2) && (angle < 3*Math.PI/2)) {
//			signX = -1;
//		} else signX = 1;
//		
//		double hx, hy;
//		double radius = Math.sqrt(2);
//		hy = Math.sin(angle) * radius;
//		
//		if ( Math.abs(hy) < 1 ) { 
//			return new Point2D.Double(cx + signX*r.getWidth()/2 , cy - hy*r.getHeight()/2 );
//		}
//		
//		hx = Math.cos(angle) * radius;
//		return new Point2D.Double(cx + hx*r.getWidth()/2, cy - signY*r.getHeight()/2);
	}
	
	protected Point.Double getAngleOnRect(Rectangle2D r, double angle) {
		double cx = r.getCenterX();
		double cy = r.getCenterY();
		double x, y;
		
		double signX, signY;
		if (angle > Math.PI) {
			signY = 1;
		} else signY = -1;
		
		if ((angle > Math.PI/2) && (angle < 3*Math.PI/2)) {
			signX = -1;
		} else signX = 1;		
		
		// Calcular punto sobre el cuadrado de lado 2
		double radius = Math.sqrt(2);
		
		y = -1 * Math.sin(angle) * radius; 
				
		if ( Math.abs(y) < 1 ) { 
			// lado izq o dcho
			x = signX;
			// y = y;
		} else {
			// lado arriba o abajo			
			x = Math.cos(angle) * radius;	
			y = signY;
		}
		
		// Ajustar punto al poligono del receptor
		if ( (Math.abs(x)==1) && (y<0.6) ) {
			return new Point2D.Double(cx + x*r.getWidth()/2, cy + y*r.getHeight()/2);
		}
		
		Point2D.Double intersection; 
		Line2D.Double l1, l2;
		l1 = new Line2D.Double(0, 0, 2*x, 2*y);
		if ( (angle >= 0) && (angle < Math.PI/2 ) ) {
			l2 = new Line2D.Double(-0.01, -0.6, 1, -1 );			 
			intersection = SVGUtil.getIntersectionPoint(
					l1 ,
					l2
					);
		} else if ( (angle >= Math.PI/2) && (angle < Math.PI ) ) {
			l2 = new Line2D.Double( 0.01, -0.6, -1, -1 );
			intersection = SVGUtil.getIntersectionPoint(
					l1 ,
					l2
					);			
		} else if ( (angle >= Math.PI) && (angle < 3*Math.PI/2 ) ) {
			l2 = new Line2D.Double(-1, 0.6, 0.1, 1 );
			intersection = SVGUtil.getIntersectionPoint(
					l1 ,
					l2
					);			
		} else {
			l2 = new Line2D.Double(1, 0.6, -0.1, 1 );
			intersection = SVGUtil.getIntersectionPoint(
					l1 ,
					l2
					);			
		}
		
		if (intersection==null) {
			System.out.println("Error");
		}
		
		return new Point2D.Double(cx + intersection.x*r.getWidth()/2, cy + intersection.y*r.getHeight()/2);
	}
	
	
//	@Override
//	public SVGShape getSVGShape() {
//		if (shape == null) return buildSVGShape();
//		return shape;		
//	}
	
	public SVGShape buildSVGShape() {
		SVGComplexShape complex = new SVGComplexShape();
		complex.setAttribute("id", mr.getIdAlias());		
		if ( !SVGConfig.omitJavascript )
	    	complex.setAttribute("onclick", "infoWindow('" + mr.getIdAlias() + "'," +
					 "'" + mr.getName() + "'," +
					 "'" + "Receptor" + "'" +
					 ");");
    	
	    int h = mr.getHomodimer();	    
	    double heightEach = mr.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = mr.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
	    
	    this.center.x = mr.getBounds().getX() + widthEach/2;
	    this.center.y = mr.getBounds().getY() + heightEach/2;
    	
	    SVGShape shp;
	    if(active) {
	    	for (int hc = h-1 ; hc>=0 ; hc--) {
		    	shp = getDashedPolygon(mr.getBounds().getX() + HOMODIMER_WIDTH*hc , 
						mr.getBounds().getY() + HOMODIMER_HEIGHT*hc ,
						widthEach,
						heightEach
					   );
		    	complex.add(shp);
	    	}
	    	
	    	for (int hc = h-1 ; hc>=0 ; hc--) {
	    		shp = getDashedFill(mr.getBounds().getX() + HOMODIMER_WIDTH*hc , 
						mr.getBounds().getY() + HOMODIMER_HEIGHT*hc ,
						widthEach,
						heightEach
					   );
	    		complex.add(shp);
	    	}	    	
	    }
	    
	    for (int hc = h-1 ; hc>=0 ; hc--) {
	    	// Mantener el poligono en la variable poly, para usarla
	    	// al calcular puntos sobre la linea del mismo
	    	this.poly = getPolygon(mr.getBounds().getX() + HOMODIMER_WIDTH*hc , 
	    									mr.getBounds().getY() + HOMODIMER_HEIGHT*hc ,
	    									widthEach,
	    									heightEach
	    								   );
	    	
	    	complex.add( this.poly );
	    }
    	
	    
	    Rectangle2D.Double rect = new Rectangle2D.Double(
    			mp.getBounds().getX(), mr.getBounds().getY(), 
    			widthEach, heightEach);
	    if ((mr.getResidues()!=null)) {
	    	SVGShape res, resText;
		    for (MResidue mres : mr.getResidues()) {		    	
		    	Point2D.Double p = getAngleOnRect(rect, mres.getAngle()  );
		    	res = getResidue( mres , p );
		    	complex.add(res);
		    	
		    	resText = new SVGText( SVGTextRenderer.getInstance().drawTextCentered(svgDoc, p.x, p.y , mres.getState(), 9 ));
		    	complex.add(resText);		    	
		    }	    
	    }	
	    
	    addBindingRegions(complex, rect, true);
	    addStructuralState(complex, rect);
		
    	// TO DO: encapsular en metodo
		complex.add( new SVGText(
	    				SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
				    		center.x , center.y, 
				    		mr.getName() ,
				    		12 ))
	    		);
		
	    complex.add( getInfoUnit(
	    		new Rectangle2D.Double( this.getMspecies().getBounds().getX() , 
	    								this.getMspecies().getBounds().getY(), 
	    								widthEach, 
	    								heightEach )
	    		) );
		
		this.shape = complex;
		return complex;
	}
	

}
