package svgview.shapes;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Vector;

public class SVGReactionSquare extends SVGPolygon {
	public static final int ANCHORMARGIN = 0;/*5*/
	private Point2D center;
	private double angle;
	public Segment upper, lower;  // Segmentos donde
	private Point2D p1;
	private Point2D p2; 
	
	public SVGReactionSquare(Point2D p1, Point2D p2, String style) {
		this( p1, p2 );
		this.setAttribute("style", style);
	}
	public SVGReactionSquare(Point2D p1, Point2D p2) {
		super();
		this.p1 = p1; this.p2 = p2;
		center = new Point2D.Double( (p1.getX()+p2.getX())/2, (p1.getY()+p2.getY())/2 );
		
		angle = Math.atan2(p1.getY()-p2.getY() , p1.getX()-p2.getX() );
		
		AffineTransform at = new AffineTransform();
		at.translate(center.getX(), center.getY()); 
		at.rotate(angle);
		
		// Vertices del cuadrado
		Point2D t0 = new Point2D.Double(5, 5);
		Point2D t1 = new Point2D.Double(5, -5);
		Point2D t2 = new Point2D.Double(-5, -5);
		Point2D t3 = new Point2D.Double(-5, 5);
		// Vertices de los segmento upper y lower
		Point2D l0 = new Point2D.Double(-5-ANCHORMARGIN, 5+ANCHORMARGIN);
		Point2D l1 = new Point2D.Double(5+ANCHORMARGIN, 5+ANCHORMARGIN);
		Point2D u0 = new Point2D.Double(-5-ANCHORMARGIN, -5-ANCHORMARGIN);
		Point2D u1 = new Point2D.Double(5+ANCHORMARGIN, -5-ANCHORMARGIN);

		at.transform(t0, t0); at.transform(t1, t1);
		at.transform(t2, t2); at.transform(t3, t3);
		at.transform(u0, u0); at.transform(u1, u1);
		at.transform(l0, l0); at.transform(l1, l1);
		
		this.upper = new Segment(u0, u1);
		this.lower = new Segment(l0, l1);
		
		Vector<Point2D> square = new Vector<Point2D>();
		square.add(t0);
		square.add(t1);
		square.add(t2);
		square.add(t3);
		
		this.setVertices(square);
		this.setAttribute("stroke-dasharray", "none");
	}
	
	public SVGReactionSquare(Vector<Point2D> vertices, Point2D center, String style) {
		super(vertices, style);
		this.center = center;
	}

	public SVGReactionSquare(Vector<Point2D> vertices, Point2D center) {
		super(vertices);
		this.center = center;
	}	
	
	public Point2D getCenter() {
		return center;
	}
	
	public Point2D getAnchorPoint(String targetLineIndex) {
		Point2D p = this.getCenter();
		int linkAnchor =		
			Integer.parseInt( targetLineIndex.split(",")[1] );
		
		
		
		AffineTransform at = new AffineTransform();
		at.translate(center.getX(), center.getY()); 
		at.rotate(angle);
		
		switch(linkAnchor) {
		case 0:			
			p = new Point2D.Double( (this.p1.getX()*(5.0/8.0) + this.p2.getX()*(3.0/8.0) ) , 
					                (this.p1.getY()*(5.0/8.0)+this.p2.getY()*(3.0/8.0) ) );
			return p;
		case 1:
			p = new Point2D.Double( (this.p1.getX()*(3.0/8.0) + this.p2.getX() * (5.0/8.0) ), 
									(this.p1.getY()*(3.0/8.0) + this.p2.getY() * (5.0/8.0) ) );
			return p;			
		case 2: 
			p = new Point2D.Double( 0 , 5 );
			break;
		case 3: 
			p = new Point2D.Double( 0 , -5 );
			break;
		case 4: 
			p = new Point2D.Double( 5 , 5 );
			break;
		case 5: 
			p = new Point2D.Double( -5 , 5 );
			break;
		case 6: 
			p = new Point2D.Double( 5 , -5 );
			break;
		case 7: 
			p = new Point2D.Double( -5 , -5 );
			break;		
		default:
			return null;
		}
		p = at.transform(p, null);
		return p;
	}
	

	@Deprecated
	public Point2D getAnchorPointOld(Point2D origin) {
		Point2D p; double dist;
		
		Segment s = new Segment(origin, this.center);
		Point2D p0 = this.upper.intersection(s);
		Point2D p1 = this.lower.intersection(s);
		
		if ((p0==null) && (p1==null)) {
			// devolver punto más cercano a extremos
			dist = origin.distance(upper.getP1()); 
			p = upper.getP1();
			if (origin.distance(upper.getP2()) < dist ) {
				dist = origin.distance(upper.getP2()) ; 
				p = upper.getP2();
			}
			if (origin.distance(lower.getP1()) < dist ) {
				dist = origin.distance(lower.getP1()) ; 
				p = lower.getP1();
			}
			if (origin.distance(lower.getP2()) < dist ) {
				dist = origin.distance(lower.getP2()) ; 
				p = lower.getP2();
			}	
			
			return p;
			
		} else if (p0==null) {
			return p1;
		} else if (p1==null) {
			return p0;
		} else {
			// devolver punto más cercano
			if (origin.distance(p0) > origin.distance(p1)) {
				return p1;
			} else {
				return p0;
			}
		}
		
	}
	
	/**
	 * De los puntos superior e inferior del cuadrado, devolver el mas cercano.
	 * Asi se respeta mejor la recomendacion SBGN de unir las modificaciones con
	 * uno de los lados paralelos al eje de la reacción.
	 * 
	 * @param origin
	 * @return
	 */
	public Point2D getAnchorPoint(Point2D origin) {
		Point2D p0 = this.upper.scale(0.5).getP2();
		Point2D p1 = this.lower.scale(0.5).getP2();		
		
		// devolver punto más cercano
		if (origin.distance(p0) > origin.distance(p1)) {
			return p1;
		} else {
			return p0;
		}
	
		
	}
}
