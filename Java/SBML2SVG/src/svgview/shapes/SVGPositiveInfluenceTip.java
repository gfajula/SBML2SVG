package svgview.shapes;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Vector;

public class SVGPositiveInfluenceTip  extends SVGComplexShape {

		private double lineWidth = 1.0;
		private String color = "#000000";
		protected SVGLine l;
		
		public SVGPositiveInfluenceTip(double x1, double y1, double x2, double y2,
				String style) {
			super(style);
			addShapes(x1, y1, x2, y2, false );	
		}

		public SVGPositiveInfluenceTip(double x1, double y1, double x2, double y2) {
			super();
			addShapes(x1, y1, x2, y2, false );	
		}

		public SVGPositiveInfluenceTip(Point2D p1, Point2D p2) {
			this(p1.getX(), p1.getY(), p2.getX(), p2.getY());
		}

		public SVGPositiveInfluenceTip(Point2D p1, Point2D p2, String style) {
			this(p1.getX(), p1.getY(), p2.getX(), p2.getY(), style);
		}

		public SVGPositiveInfluenceTip(Point2D p1, Point2D p2, boolean doubleTip,
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
			
			// Vertices de la punta de la flecha (primer vertice esta en 1,0).
			// Se deja '1' para q no se solape la punta de la flecha con el punto final
			Point2D t0 = new Point2D.Double( 14., -1*(4.5));
			Point2D t1 = new Point2D.Double( 14., 4.5);
//			Point2D t2 = new Point2D.Double( 3.5 , 3.5+lineWidth );
//			Point2D t3 = new Point2D.Double( 3.5 , -1*(3.5+lineWidth) );
			// Punto en el que se une la recta al triangulo;
			Point2D rt = new Point2D.Double( 5 , 0);
						
			// Rotar puntos;
			at.transform(t0, t0);
			at.transform(t1, t1); 
			
			at.transform(rt, rt);
			
			Vector<Point2D> rectangle = new Vector<Point2D>();
			rectangle.add(t0);
			rectangle.add(rt);
			rectangle.add(t1);

			
			SVGPath puntaFinal = new SVGPath( rectangle );		
			puntaFinal.setAttribute("stroke", color);
			puntaFinal.setAttribute("stroke-linecap", "square");
			puntaFinal.setAttribute("fill", "none");
			puntaFinal.setAttribute("stroke-dasharray", "none");
			puntaFinal.setAttribute("stroke-width", "" + (lineWidth+2) );
			
			this.add(puntaFinal);
			
			l = new SVGLine(x1, y1, rt.getX(), rt.getY());
			l.setAttribute("stroke-width", "" + lineWidth);
			l.setAttribute("stroke", "" + color);
			
			this.add(l);			
			

			
		}
	}

