package svgview.shapes;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class SVGInhibitionTip  extends SVGComplexShape {

		private double lineWidth = 1.0;
		private String color = "#000000";
		protected SVGLine l;
		
		public SVGInhibitionTip(double x1, double y1, double x2, double y2,
				String style) {
			super(style);
			addShapes(x1, y1, x2, y2, false );	
		}

		public SVGInhibitionTip(double x1, double y1, double x2, double y2) {
			super();
			addShapes(x1, y1, x2, y2, false );	
		}

		public SVGInhibitionTip(Point2D p1, Point2D p2) {
			this(p1.getX(), p1.getY(), p2.getX(), p2.getY());
		}

		public SVGInhibitionTip(Point2D p1, Point2D p2, String style) {
			this(p1.getX(), p1.getY(), p2.getX(), p2.getY(), style);
		}

		public SVGInhibitionTip(Point2D p1, Point2D p2, boolean doubleTip,
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
			Point2D t0 = new Point2D.Double( 1.5 + lineWidth, -1*(4+lineWidth));
			Point2D t1 = new Point2D.Double( 1.5 + lineWidth, 4+lineWidth);
			// Punto en el que se une la recta al triangulo;
			Point2D rt = new Point2D.Double( 2 + lineWidth, 0);
						
			// Rotar puntos;
			at.transform(t0, t0);
			at.transform(t1, t1);
			at.transform(rt, rt);
			
			SVGLine puntaFinal = new SVGLine(t0, t1);		
			puntaFinal.setAttribute("stroke", color);
			puntaFinal.setAttribute("stroke-dasharray", "none");
			
			this.add(puntaFinal);
			
			if (doubleTip) {	
				
				at = new AffineTransform();
				at.translate(x1, y1); at.rotate(Math.PI + theta);
				
				// Vertices del triangulo de la flecha (primer vertice esta en 1,0).
				// Se deja '1' para q no se solape la punta de la flecha con el punto final
//				t0 = new Point2D.Double(1, 0);
//				t1 = new Point2D.Double(16, -5);
//				t2 = new Point2D.Double(16, 5);
				// Punto en el que se une la recta al triangulo;
//				Point2D rt0 = new Point2D.Double(16, 0);			
				// Triangulo, teniendo en cuenta grosor de línea				
				t0 = new Point2D.Double( 2, -1*(4+lineWidth) );
				t1 = new Point2D.Double( 2, 4+lineWidth);
				Point2D rt0 = new Point2D.Double(2, 0);

				// Rotar puntos;
				at.transform(t0, t0);
				at.transform(t1, t1);

				at.transform(rt0, rt0);
								
				l = new SVGLine(rt0, rt );
				l.setAttribute("stroke-width", "" + lineWidth);		
				l.setAttribute("stroke", "" + color);
				this.add(l);
				
				SVGLine puntaInicial = new SVGLine( t0, t1 );		
				puntaInicial.setAttribute("stroke", color);
				this.add(puntaInicial);
			
			} else {
				l = new SVGLine(x1, y1, rt.getX(), rt.getY());
				l.setAttribute("stroke-width", "" + lineWidth);
				l.setAttribute("stroke", "" + color);
				
				this.add(l);			
			}	
			

			
		}
	}

