package svgview.util;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

public class SVGUtil {
	
	public static String getHexColor(Color c) {
		String r, g, b;
		
		if (c.getRed()<16) 
			r = "0" + Integer.toHexString(c.getRed());
		 else
			r = Integer.toHexString(c.getRed());
		if (c.getGreen()<16) 
			g = "0" + Integer.toHexString(c.getGreen());
		 else
			g = Integer.toHexString(c.getGreen());		
		if (c.getBlue()<16) 
			b = "0" + Integer.toHexString(c.getBlue());
		 else
			b = Integer.toHexString(c.getBlue());		
		
		return "#" + r + g + b;
	}
	
	/**
	 * Recortar rectangulo un determinado margen horizontal y vertical
	 * @param horiz margen horizontal
	 * @param vert  margen vertical
	 * @return Nuevo Rectangle2D con las dimensiones recortadass
	 */
	public static Rectangle2D getTrimmedRectangle(Rectangle2D rect, double horiz, double vert) {
		return new Rectangle2D.Double(rect.getMinX()+horiz,
									   rect.getMinY()+vert, 
									   rect.getWidth()-2*horiz,
									   rect.getHeight()-2*vert);
	}
	
	public static Point2D.Double getIntersectionPoint(Vector<Line2D.Double> polygon, double cx, double cy, double angle) {
		for (Line2D.Double l : polygon) {
			Point2D.Double p =  getIntersectionPoint(l, cx, cy, angle);
			if (p!=null) return p;
		}
		
		return null;
	}
	
	public static Point2D.Double getIntersectionPoint(Line2D.Double segment1, Line2D.Double segment2) {
		if (!segment1.intersectsLine(segment2)) return null; 
		
		double slope1 = (segment1.y2 - segment1.y1) / (segment1.x2 - segment1.x1);
		double slope2 = (segment2.y2 - segment2.y1) / (segment2.x2 - segment2.x1);
		
		double x = (segment1.y1 - segment2.y1 - slope1*segment1.x1 + slope2*segment2.x1) /
					(slope2 - slope1) ;
		double y = slope1* (x - segment1.x1) + segment1.y1;
		
		return new Point2D.Double(x, y);
		
//		return null;
	}
	
	public static Point2D.Double getIntersectionPoint(Line2D.Double segment, double cx, double cy, double angle) {
		double x,y;
		double tanAngle = Math.tan(angle)*-1; // Invertir pq eje Y va hacia abajo en pantalla
		
		
		if (segment.x2 == segment.x1) {
			x = segment.x1;
			y = tanAngle*(x-cx) + cy;
			
			   
		} else {
			double slope = (segment.y2 - segment.y1) / (segment.x2 - segment.x1);
			double dTheta = tanAngle - slope;
			if (dTheta == 0) return null; // Rectas Paralelas
			x = (segment.y1 - cy + cx*tanAngle - segment.x1*slope) / dTheta;
			y = slope*x + segment.y1 - segment.x1*slope;			
		}
		
		/////////////////////////////////////////////////////////////////
		// Comprobar la direcci�n del angulo respecto al segmento!!!!
		/////////////////////////////////////////////////////////////////
		if ((angle>=0) && (angle<=Math.PI/2)) {
			if ((x<cx) || (y>cy)) return null;
		} else if ((angle>=Math.PI/2) && (angle<=Math.PI)) {
			if ((x>cx) || (y>cy)) return null;
		} else if ((angle>=Math.PI) && (angle<=3*Math.PI/2)) {
			if ((x>cx) || (y<cy)) return null;
		} else if ((angle>=3*Math.PI/2) && (angle<=2*Math.PI)) {
			if ((x<cx) || (y<cy)) return null;
		}
		
		
		// Comprobar que est� contenido en el segmento
		if (((x>=segment.x1) && (x<=segment.x2)) ||
			((x>=segment.x2) && (x<=segment.x1))) {
			if (((y>=segment.y1) && (y<=segment.y2)) ||
	    		((y>=segment.y2) && (y<=segment.y1))) {
				System.out.println("point in line: " + x + ", " + y);
			} else {
				System.out.println("y out of segment " + x);
				return null;	
			}
		} else {
			System.out.println("x out of segment " + x);
			return null;
		}
		
		return new Point2D.Double(x,y);
//		return null;
		
	}
	
	public static void main(String[] args){
		System.out.println(
//				getIntersectionPoint(
//						new Line2D.Double(0,0.8,0.5,1), 
//						0.5, // cx, 
//						0.5, // cy, 
//						4.459733321203526
//						)		
				getIntersectionPoint(
						new Line2D.Double(0, 0.8, 0.5, 1),
						new Line2D.Double(0.5, 0.5 , 0.25, 1)
						)
						
		);
		
	}
}
