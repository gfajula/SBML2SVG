package svgview.shapes;

import java.awt.geom.Point2D;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import svgview.util.Equation;

/**
 * Clase que genera un Círculo dibujable en SVG.
 * 
 * @author Guillermo Fajula Leal
 * 
 */
public class SVGCircle extends SVGSimpleShape {
	protected double r;
	protected double cx;
	protected double cy;
		
	/** 
	 * Constructor
	 * 
	 * @param cx Coordenada X del centro
	 * @param cy Coordenada Y del centro
	 * @param r	 Radio
	 */
	public SVGCircle(double cx, double cy, double r) {
		super();
		this.r = r;
		this.cx = cx;
		this.cy = cy;
	}
	
	/** 
	 * Constructor
	 * 
	 * @param cx Coordenada X del centro
	 * @param cy Coordenada Y del centro
	 * @param r	 Radio
	 * @param style Estilo aplicable a la forma SVG
	 */
	public SVGCircle(double cx, double cy, double r, String style) {
		super();
		this.r = r;
		this.cx = cx;
		this.cy = cy;
		this.setAttribute("style", style);
	}
	

	@Override
	public Point2D.Double intersection(Segment l) {
		
		if ( Math.abs(l.getX1()-l.getX2()) < THRESHOLD ) {
			// Segmento vertical, con ecuacion en la forma x=x1=x2
			double d1 = r*r - (l.getX1() - cx) * (l.getX1() - cx) ;
			if (d1<0) {
				// no hay interseccion
				return null;
			} else {
				Point2D.Double p1 = new Point2D.Double( l.getX1() , cy + Math.sqrt(d1) );
				Point2D.Double p2 = new Point2D.Double( l.getX1() , cy - Math.sqrt(d1) );
				
				if ( l.ptSegDist(p1) < THRESHOLD ) {
					
					if ( l.ptSegDist(p2) < THRESHOLD ) {
						// Dos intersecciones, se devuelve la más cercana al origen;
						if  ( p1.distance(l.getP1()) < p2.distance(l.getP1()) ) {
							return p1;
						} else {
							return p2;
						}
					} else {
						// Solo p1 intersecta el segmento
						return p1;
					}
					
				} else if ( l.ptSegDist(p2) < THRESHOLD ) {
					return p2;
				} else {
					// ni p1, ni p2 intersectan el segmento.
					return null;
				}

			} 		
				
		} else {
			// Caso general, recta con pendiente no vertical
			
			double m = l.getSlope();
			double kr = l.getY1() - m*l.getX1(); // cte. en ec de la recta.
			
			// ec. recta => y = mx + kr
			
			// sustituir y en ec circ. :: (x-cx)^2 + (y-cy)^2 = r^2 
			
			double kc = kr - cy;
			
			// ec. circ. queda como :: (x-cx)^2 + (m*x + kc)^2 = r^2
			// desarrollando :: (m^2+1)*x^2  +  (2*kc*m-2*cx)*x  + (cx^2 + kc^2 - r^2) = 0 ;
			
			// resolver en x.
			double[] soluciones = Equation.quadratic(m*m+1,  2*kc*m - 2*cx, cx*cx + kc*kc - r*r );
			if (soluciones.length == 0) {
				// no hay soluciones
				return null;
			} else if (soluciones.length == 1) {
				// unica solucion
				double sx = soluciones[0]; 
				Point2D.Double p1 = new Point2D.Double( sx , m*sx + kr );
				if (l.ptSegDist(p1) < THRESHOLD) {
					return p1;
				} else {
					return null;
				}
			} else if (soluciones.length == 2) {
				// dos soluciones
				double sx1 = soluciones[0];
				double sx2 = soluciones[1];
				Point2D.Double p1 = new Point2D.Double( sx1 , m*sx1 + kr );
				Point2D.Double p2 = new Point2D.Double( sx2 , m*sx2 + kr );
				
				if ( l.ptSegDist(p1) < THRESHOLD ) {
					
					if ( l.ptSegDist(p2) < THRESHOLD ) {
						// Dos intersecciones, se devuelve la más cercana al origen;
						if  ( p1.distance(l.getP1()) < p2.distance(l.getP1()) ) {
							return p1;
						} else {
							return p2;
						}
					} else {
						// Solo p1 intersecta el segmento
						return p1;
					}
					
				} else if ( l.ptSegDist(p2) < THRESHOLD ) {
					return p2;
				} else {
					// ni p1, ni p2 intersectan el segmento.
					return null;
				}				
			}			
		}		

		return null;
	}

	@Override
	public void svgPaint(Element docParent) {
		Document svgDoc = docParent.getOwnerDocument(); 
	    Element circle = svgDoc.createElementNS (svgNS, "circle");	    
	    
	    addAttributes(circle);
	    
	    circle.setAttributeNS (null, "r", Double.toString(r));
	    circle.setAttributeNS (null, "cx", Double.toString(cx));
	    circle.setAttributeNS (null, "cy", Double.toString(cy));
	    
		docParent.appendChild(circle);		
	}

	public double getR() {
		return r;
	}

	public double getCx() {
		return cx;
	}

	public double getCy() {
		return cy;
	}
	
	public static void main(String[] args) {
		SVGCircle circle = new SVGCircle(2, 2, 4);
		Segment s = new Segment(-10, -10, 10, 10);
		Point2D p = circle.intersection( s );
		System.out.println( p );		
	}

}
