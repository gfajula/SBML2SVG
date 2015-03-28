package svgview.shapes;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class SVGCatalysisArrow extends SVGComplexShape {

	private double lineWidth = 1.0;
	private String color = "#000000";
	protected SVGLine l;
	
	public SVGCatalysisArrow(double x1, double y1, double x2, double y2,
			String style) {
		super(style);
		addShapes(x1, y1, x2, y2, false );	
	}

	public SVGCatalysisArrow(double x1, double y1, double x2, double y2) {
		super();
		addShapes(x1, y1, x2, y2, false );	
	}

	public SVGCatalysisArrow(Point2D p1, Point2D p2) {
		this(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}

	public SVGCatalysisArrow(Point2D p1, Point2D p2, String style) {
		this(p1.getX(), p1.getY(), p2.getX(), p2.getY(), style);
		// TODO Auto-generated constructor stub
	}

	public SVGCatalysisArrow(Point2D p1, Point2D p2, boolean doubleTip,
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
		
		// Punto en el que se une la recta al triangulo y centro del circulo
		Point2D rt = new Point2D.Double( 4+lineWidth, 0);
					
		// Rotar puntos;
		at.transform(rt, rt);
		
		
		l = new SVGLine(x1, y1, rt.getX(), rt.getY());
		l.setAttribute("stroke-width", "" + lineWidth);
		l.setAttribute("stroke", "" + color);
		
		this.add(l);			
		

		SVGCircle puntaFinal = new SVGCircle(rt.getX(), rt.getY(), 2.5+lineWidth/2 );		
		puntaFinal.setAttribute("stroke", color);
		puntaFinal.setAttribute("fill", "white");
		puntaFinal.setAttribute("stroke-dasharray", "none");

		this.add(puntaFinal);
		
	}
}

