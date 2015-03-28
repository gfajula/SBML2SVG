package svgview.shapes;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.Vector;

/**
 * Clase que se encarga de dibujar flechas.
 * Dibuja una línea desde un origen a un destino, y
 * un triangulo apuntando al punto destino.
 * Si la flecha es bidireccional, ambos extremos dibujan
 * el triangulo-
 * 
 * @author Guillermo Fajula Leal
 *
 */
public class SVGArrow extends SVGComplexShape {	
	private double lineWidth = 1.0;
	private String color = "#000000";
	protected SVGLine l;
	
	/**
	 * Constructor genérico. Indica origen, destino y estilo a aplicar
	 * 
	 * @param x1	   Coordenada X del origen
	 * @param y1	   Coordenada Y del origen
	 * @param x2	   Coordenada X del destino
	 * @param y2	   Coordenada Y del destino
	 * @param style	   Cadena de texto con contenido del atributo 'style' del elemento SVG.
	 */
	public SVGArrow(double x1, double y1, double x2, double y2, String style) {
		super(style);
		addShapes(x1, y1, x2, y2, false);	
	}

	/**
	 * Constructor genérico. Indica origen, destino y aplica
	 * estilo estandar y heredados.
	 * 
	 * @param x1	   Coordenada X del origen
	 * @param y1	   Coordenada Y del origen
	 * @param x2	   Coordenada X del destino
	 * @param y2	   Coordenada Y del destino
	 */
	public SVGArrow(double x1, double y1, double x2, double y2) {
		this(x1, y1, x2, y2, false, "#000000", 1.0);
	}
	
	/**
	 * Constructor para flechas bidireccionales.
	 * 
	 * @param x1	   Coordenada X del origen
	 * @param y1	   Coordenada Y del origen
	 * @param x2	   Coordenada X del destino
	 * @param y2	   Coordenada Y del destino
	 * @param doubleTip		True si la flecha es bidireccional, False, si es simple
	 * @param color			Color para la flecha, tanto la línea como la punta
	 * @param lineWidth		Anchura de la línea
	 */
	public SVGArrow(double x1, double y1, double x2, double y2, boolean doubleTip, String color, double lineWidth ) {
		super();
		this.color = color;
		this.lineWidth = lineWidth;
		addShapes(x1, y1, x2, y2, doubleTip);
	}

	/**
	 * Constructor para flechas bidireccionales. Color estándar (negro)
	 * 
	 * @param p1			 Coordenada X del origen
	 * @param p2			 Coordenada X del destino
	 * @param doubleTip		 True si la flecha es bidireccional, False, si es simple
	 */
	public SVGArrow(Point2D p1, Point2D p2, boolean doubleTip ) {
		this(p1.getX(), p1.getY(), p2.getX(), p2.getY(), doubleTip, "#000000", 1.0);
	}
	
	/**
	 * Constructor para flechas bidireccionales. Color estándar (negro)
	 * 
	 * @param p1			 Coordenada X del origen
	 * @param p2			 Coordenada X del destino
	 * @param doubleTip		 True si la flecha es bidireccional, False, si es simple
	 * @param color			Color para la flecha, tanto la línea como la punta
	 * @param lineWidth		Anchura de la línea
	 */
	public SVGArrow(Point2D p1, Point2D p2, boolean doubleTip, String color, double lineWidth ) {
		this(p1.getX(), p1.getY(), p2.getX(), p2.getY(), doubleTip, color, lineWidth);
		
	}
	
	/**
	 * Constructor resumido. Color estándar (negro)
	 * 
	 * @param p1			 Coordenada X del origen
	 * @param p2			 Coordenada X del destino
	 */
	public SVGArrow(Point2D p1, Point2D p2) {
		this(p1.getX(), p1.getY(), p2.getX(), p2.getY(), false,  "#000000", 1.0);
	}

	/**
	 * Constructor genérico. Indica origen, destino y estilo a aplicar
	 * 
	 * @param p1			 Coordenada X del origen
	 * @param p2			 Coordenada X del destino
	 * @param style			Cadena de texto con contenido del atributo 'style' del elemento SVG.
	 */
	public SVGArrow(Point2D p1, Point2D p2, String style) {
		this(p1.getX(), p1.getY(), p2.getX(), p2.getY(), style);
	}
	
	/**
	 * Método privado que construye la figura de la flecha 
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param doubleTip
	 */
	private void addShapes(double x1, double y1, double x2, double y2, boolean doubleTip) {
		double theta = Math.atan2(y1-y2 , x1-x2 );
		AffineTransform at = new AffineTransform();
		at.translate(x2, y2); at.rotate(theta);
		
		// Vertices del triangulo de la flecha (primer vertice esta en 1,0).
		// Se deja '1' para q no se solape la punta de la flecha con el punto final
		Point2D t0 = new Point2D.Double(1, 0);
		Point2D t1 = new Point2D.Double(13+lineWidth*3, -1*(4+lineWidth));
		Point2D t2 = new Point2D.Double(13+lineWidth*3, 4+lineWidth);
		// Punto en el que se une la recta al triangulo;
		Point2D rt = new Point2D.Double(13+lineWidth*3, 0);
	
		// Rotar puntos;
		at.transform(t0, t0);
		at.transform(t1, t1);
		at.transform(t2, t2);
		at.transform(rt, rt);
		
//		// Trasladar al final de la flecha
		Vector<Point2D> triangle = new Vector<Point2D>();
		triangle.add(t0);
		triangle.add(t1);
		triangle.add(t2);
		
		SVGPolygon puntaFinal = new SVGPolygon(triangle);		
		puntaFinal.setAttribute("stroke", "none");
		puntaFinal.setAttribute("fill", color);

		this.add( puntaFinal );
		
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
			t1 = new Point2D.Double( 13+lineWidth*3, -1*(4+lineWidth) );
			t2 = new Point2D.Double( 13+lineWidth*3, 4+lineWidth);
			Point2D rt0 = new Point2D.Double(15+lineWidth, 0);		

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
			puntaInicial.setAttribute("stroke", "none");
			puntaInicial.setAttribute("fill", color);
			this.add( puntaInicial );
			
		} else {
			l = new SVGLine(x1, y1, rt.getX(), rt.getY());
			l.setAttribute("stroke-width", "" + lineWidth);
			l.setAttribute("stroke", "" + color);
			
			this.add(l);			
		}
		
	}

	/**
	 * Constructor indicando origen y destino mediante objeto <code>Segment</code>.
	 * 
	 * @param s
	 * @param style
	 */
	public SVGArrow(Segment s, String style) {
		this(s.x1, s.y1, s.x2, s.y2, style);
	}


	/**
	 * Constructor indicando origen y destino mediante objeto <code>Segment</code>.
	 * 
	 * @param s
	 * @param style
	 */
	public SVGArrow(Segment s) {
		this(s.x1, s.y1, s.x2, s.y2, false, "#000000", 1.0);
	}	
	
	
	@Override
	public Double intersection(Segment l) {
		return this.l.intersection(l);
	}

}
