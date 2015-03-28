package svgview.shapes;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * Clase de utilidad para modelar segmentos y operaciones habituales sobre ellos
 * como escalados o recortados por uno de los extremos
 * 
 * @author Guillermo Fajula Leal
 *
 */
public class Segment extends java.awt.geom.Line2D.Double implements Cloneable {

	private static final long serialVersionUID = 8366582023510641271L;

	/**
	 * Constructor por defecto
	 * 
	 * @param p1
	 * @param p2
	 */
	public Segment(Point2D p1, Point2D p2) {
		super(p1, p2);
	}
	
	/**
	 * Constructor usando las magnitudes sobre los ejes x e y
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public Segment(double x1, double y1, double x2, double y2) {
		super(x1, y1, x2, y2);
	}
	
	/**
	 * Invertir el segmento
	 * 
	 * @return
	 */
	public Segment getInverted() {
		return new Segment(this.getP2(), this.getP1());
	}
	
	/**
	 * Obtiene el punto del segmento equidistante a 
	 * los dos extremos del mismo
	 * 
	 * @return punto intermedio
	 */
	public Point2D getCenter() {
		return new Point2D.Double((x1+x2)/2, (y1+y2)/2);
	}
	
	/**
	 * Obtiene la longitud del segmento
	 * 
	 * @return longitud segmento
	 */
	public double getLongitude() {	
		return Math.hypot(x2-x1, y2-y1);
	}
	
	/**
	 * Obtiene la pendiente del segmento (altura/anchura)
	 * 
	 * @return Pendiente del segmento
	 */
	public double getSlope() {	
		if (x2==x1) {
			new Exception("Cant compute vertical slope." + x1 + "-" + x2).printStackTrace();
			return java.lang.Double.POSITIVE_INFINITY;					
		}
		return (y2-y1) / (x2-x1); 
	}
	
	/**
	 * Obtiene la pendiente del segmento en radianes, o lo que es lo mismo,
	 * el angulo del mismo sobre el eje X
	 * 
	 * @return angulo sobre el eje X
	 */
	public double getAngle() {
		return Math.atan2(y2-y1, x2-x1);
	}
	
	/**
	 * Calcula si otro segmento interseca a este, y devuelve
	 * en su caso el punto de intersección. 
	 * 
	 * @param l Segmento a testear
	 * @return Punto de intersección, null si no lo hay
	 */
	public Point2D.Double intersection(Segment l) {
		Point2D.Double p;
		
		if (this.x1 == this.x2) {
			// this es vertical			
			if (l.x1 == l.x2) {				
				// rectas paralelas
				if (l.x1==this.x1) {
					// Si estan en la misma recta, devolver punto más cercano, dentro del segmento
					if  ( this.getP1().distance(l.getP1()) < this.getP2().distance(l.getP1()) ) {
						return (Point2D.Double)(this.getP1());
					} else {
						return (Point2D.Double)(this.getP2());
					}
				} else {
					return null;
				}
				
			} else {
				// ec. recta the this :: x = x1 = x2
				double m = l.getSlope();
				double k = l.getY1() - m*l.getX1();
				// sustituyendo x en ec. recta l :: y = m*x1 + k
				p = new Point2D.Double( this.x1 , m*this.x1 + k );
				if ( this.ptSegDist(p) < SVGShape.THRESHOLD ) {
					return p;
				} else {
					return null;
				}								
			}		
			
		} else if (l.x1 == l.x2) {
			// l es vertical (this no lo es)
			// ec. recta the this :: x = x1 = x2
			double m = this.getSlope();
			double k = this.getY1() - m*this.getX1();
			// sustituyendo x en ec. recta this :: y = m*x1 + k
			p = new Point2D.Double( l.x1 , m*l.x1 + k );
			if ( this.ptSegDist(p) < SVGShape.THRESHOLD ) {
				return p;
			} else {
				return null;
			}	
		} else {
			double m1 = this.getSlope();
			double m2 = l.getSlope();
			double k1 = this.getY1() - m1*this.getX1();
			double k2 = l.getY1() - m2*l.getX1();
			
			if (m1==m2) {
				// rectas paralelas
				if (k1==k2) {
					// Si estan en la misma recta, devolver punto m�s cercano, dentro del segmento
					if  ( this.getP1().distance(l.getP1()) < this.getP2().distance(l.getP1()) ) {
						return (Point2D.Double)(this.getP1());
					} else {
						return (Point2D.Double)(this.getP2());
					}
				} else {
					return null;
				}
			} else {
				// ec. rectas => m1x + k1 = m2x + k2
				// (m1-m2) x = k2 - k1
				double px = (k2-k1)/(m1-m2);
				double py = m1*px + k1 ;
				p = new Point2D.Double( px , py );
				if ( this.ptSegDist(p) < SVGShape.THRESHOLD ) {
					return p;
				} else {
					return null;
				}				
			}
		}
		
	}
	
	/**
	 * Recortar el segmento en un factor dado. Se transforma
	 * el segmento conservando el mismo punto inicial (p1)
	 * 
	 * @param factor
	 * @return El segmento actual transformado
	 */
	public Segment scale(double factor) {
		x2 = x1 + (x2-x1)*factor;
		y2 = y1 + (y2-y1)*factor;
		return this;
	}
	
	/**
	 * Recortar un segmento en c unidades.
	 * 
	 * @param c - distancia a recortar al segmento
	 * 
	 * @return nuevo Segment con la nueva longitud
	 */
	public Segment trim(double c) {
		// Calcular Punto recortado en _c_ uds. 
		double angle = this.getInverted().getAngle();
		AffineTransform at = new AffineTransform(); 
		at.translate(x2, y2); at.rotate(angle);
		Point2D p = at.transform( new Point2D.Double(c, 0), null); 
		
		return new Segment(this.getP1(), p);
	}
	
	/**
	 * Crear un nuevo segmento, incluido en el Segment original,
	 * pero con una longitud dada
	 * 
	 * @param length
	 * 
	 * @return nuevo Segment con la nueva longitud
	 */
	public Segment subSegment(double length) {
		// La operación es complementaria al metodo trim
		return this.trim( getLongitude() - length );
	}
	
	
	public String toString( ) {
		return "(" + this.x1 + "," + this.y1 + ") -> (" 
		            + this.x2 + "," + this.y2 + ")";
	}
	

}
