package svgview.shapes;

import java.awt.geom.Point2D;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import svgview.util.Equation;

public class SVGEllipse extends SVGSimpleShape {
	protected double rx;
	protected double ry;
	protected double cx;
	protected double cy;
	
	public SVGEllipse(double cx, double cy, double rx, double ry) {
		super();
		this.rx = rx;
		this.ry = ry;
		this.cx = cx;
		this.cy = cy;
		this.setAttribute("cx", Double.toString(cx));
		this.setAttribute("cy", Double.toString(cy));		
		this.setAttribute("rx", Double.toString(rx));
		this.setAttribute("ry", Double.toString(ry));
	}
	
	public SVGEllipse(double cx, double cy, double rx, double ry, String style) {
		this(cx, cy, rx, ry);
		this.setAttribute("style", style);
	}
	
	@Override
	public Point2D.Double intersection(Segment l) {
		if (l.getX1() == l.getX2()) {
			// Segmento vertical, con ecuacion en la forma x=x1=x2
			double d1 = ry*ry *( 1 - ((l.getX1() - cx) * (l.getX1() - cx))/(rx*rx)) ;
			if (d1<0) {
				// no hay interseccion
				return null;
			} else {				
				Point2D.Double p1 = new Point2D.Double( l.getX1() , cy + Math.sqrt(d1) );
				Point2D.Double p2 = new Point2D.Double( l.getX1() , cy - Math.sqrt(d1) );
				
				if ( l.ptSegDist(p1) < THRESHOLD ) {
					
					if ( l.ptSegDist(p2) < THRESHOLD ) {
						// Dos intersecciones, se devuelve la m´as cercana al origen;
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
			
			// sustituir y en ec elipse. :: (x-cx)^2/rx^2 + (y-cy)^2/ry^2 = 1 
			
			double kc = kr - cy;
			
			
			// desarrollando :: (m^2+1)*x^2  +  (2*kc*m-2*cx)*x  + (cx^2 + kc^2 - r^2) = 0 ;
			
			// resolver en x.
			double rx2 = rx*rx; 
			double ry2 = ry*ry;
			double[] soluciones = Equation.quadratic( (m*m)/ry2 + 1/rx2 , 
													  2*kc*m/ry2 - 2*cx/rx2, 
													  cx*cx/rx2  + kc*kc/ry2 - 1 );
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
	    Element ellipse = svgDoc.createElementNS (svgNS, "ellipse");	    
	    
	    addAttributes(ellipse);	    
	    
		docParent.appendChild(ellipse);

	}

}
