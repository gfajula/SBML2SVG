package svgview.shapes;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Vector;

public class SVGStimulationArrow extends SVGComplexShape {

	private double lineWidth = 1.0;
	private String color = "#000000";
	protected SVGLine l;
	
	public SVGStimulationArrow(double x1, double y1, double x2, double y2,
			String style) {
		super(style);
		addShapes(x1, y1, x2, y2, false );	
	}

	public SVGStimulationArrow(double x1, double y1, double x2, double y2) {
		super();
		addShapes(x1, y1, x2, y2, false );	
	}

	public SVGStimulationArrow(Point2D p1, Point2D p2) {
		this(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}

	public SVGStimulationArrow(Point2D p1, Point2D p2, String style) {
		this(p1.getX(), p1.getY(), p2.getX(), p2.getY(), style);
	}

//	public SVGArrowTransport(Segment s, String style) {
//		super(s, style);
//		// TODO Auto-generated constructor stub
//	}
//
//	public SVGArrowTransport(Segment s) {
//		super(s);
//		// TODO Auto-generated constructor stub
//	}

	public SVGStimulationArrow(Point2D p1, Point2D p2, boolean doubleTip,
								String color, double lineWidth) {
		super();
		this.color = color;
		this.lineWidth = lineWidth;
		addShapes(p1.getX(), p1.getY(), p2.getX(), p2.getY() , doubleTip );
	}

	private void addShapes(double x1, double y1, double x2, double y2, boolean doubleTip) {
		
		double theta = Math.atan2(y1-y2 , x1-x2 );
		AffineTransform at = new AffineTransform();
		at.translate(x2, y2)    ; at.rotate(theta);
		
		// Vertices del triangulo de la flecha (primer vertice esta en 1,0).
		// Se deja '1' para q no se solape la punta de la flecha con el punto final
		Point2D t0 = new Point2D.Double(1, 0);
		Point2D t1 = new Point2D.Double(6+lineWidth*3, -1*(3.+lineWidth));
		Point2D t2 = new Point2D.Double(6+lineWidth*3, 3.+lineWidth);
		// Punto en el que se une la recta al triangulo;
		Point2D rt = new Point2D.Double(6+lineWidth*3, 0);
		
		// Rotar puntos;
		at.transform(t0, t0);
		at.transform(t1, t1);
		at.transform(t2, t2);
		at.transform(rt, rt);
		
		
		// Trasladar al final de la flecha
//		t0.setLocation(t0.getX() + x2, t0.getY() + y2);
//		t1.setLocation(t1.getX() + x2, t1.getY() + y2);
//		t2.setLocation(t2.getX() + x2, t2.getY() + y2);
//		rt.setLocation(rt.getX() + x2, rt.getY() + y2);		
		
		Vector<Point2D> triangle = new Vector<Point2D>();
		triangle.add(t0);
		triangle.add(t1);
		triangle.add(t2);
		
		SVGPolygon puntaFinal = new SVGPolygon(triangle);		
		puntaFinal.setAttribute("stroke", color);
		puntaFinal.setAttribute("fill", "white");
		
		this.add(puntaFinal);
		
		if (doubleTip) {	
			
			at = new AffineTransform();
			at.translate(x1, y1); at.rotate(Math.PI + theta);
			
			// Vertices del triangulo de la flecha (primer vertice esta en 1,0).
			// Se deja '1' para q no se solape la punta de la flecha con el punto final
//			t0 = new Point2D.Double(1, 0);
//			t1 = new Point2D.Double(16, -5);
//			t2 = new Point2D.Double(16, 5);
			// Punto en el que se une la recta al triangulo;
//			Point2D rt0 = new Point2D.Double(16, 0);			
			// Triangulo, teniendo en cuenta grosor de línea
			t0 = new Point2D.Double(1, 0);
			t1 = new Point2D.Double( 6+lineWidth*3, -1*(3.+lineWidth) );
			t2 = new Point2D.Double( 6+lineWidth*3, 3.+lineWidth);
			Point2D rt0 = new Point2D.Double(6+lineWidth*3, 0);
		
			// Rotar puntos;
			at.transform(t0, t0);
			at.transform(t1, t1);
			at.transform(t2, t2);
			at.transform(rt0, rt0);
		
			
			l = new SVGLine(rt0, rt );
			l.setAttribute("stroke-width", "" + lineWidth);		
			l.setAttribute("stroke", "" + color);
			this.add(l);
			
			triangle = new Vector<Point2D>();
			triangle.add(t0);
			triangle.add(t1);
			triangle.add(t2);
			SVGPolygon puntaInicial = new SVGPolygon(triangle);		
			puntaInicial.setAttribute("stroke", color);
			puntaInicial.setAttribute("fill", "white");
			this.add(puntaInicial);
		
		} else {
			l = new SVGLine(x1, y1, rt.getX(), rt.getY());
			l.setAttribute("stroke-width", "" + lineWidth);
			l.setAttribute("stroke", "" + color);
			
			this.add(l);			
		}	
		

		
	}

}
