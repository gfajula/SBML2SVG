package svgview.shapes;

import java.awt.geom.Point2D;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SVGPolygon extends SVGSimpleShape {
	private Vector<Point2D> vertices;
	
	protected SVGPolygon() {
		
	}
	
	public SVGPolygon(Vector<Point2D> vertices) {
		super();		
		if (vertices.size() > 2) {
		   this.vertices = vertices;
		} else {
			new Exception("El poligono necesita al menos 3 puntos.").printStackTrace();
		}
	}
	
	public SVGPolygon(Vector<Point2D> vertices, String style) {
		this(vertices);
		this.setAttribute("style", style);
	}
	
	public SVGPolygon(String strVertices) {
		this.vertices = new Vector<Point2D>();
		String[] numeros = strVertices.split("[ ,;]+");
		if (numeros.length > 5) {
			try {
				for(int i=0; i<numeros.length; i++) {
					this.vertices.add( 
							new Point2D.Double(java.lang.Double.parseDouble(numeros[i]),
											   java.lang.Double.parseDouble(numeros[++i])
											  )
							);
				}
			} catch (Exception e) {
				System.err.println("Error leyendo cadena con vertices del poligono.");
				e.printStackTrace();			
			}
		} else {			
			new Exception("El poligono necesita al menos 3 puntos.").printStackTrace();
		}
	}
	
	public SVGPolygon(String strVertices, String style) {
		this(strVertices);
		this.setAttribute("style", style);
	}
	
	@Override
	public Point2D.Double intersection(Segment l) {
		
		Point2D.Double resultado = null; 
		double distancia = l.getLongitude();
		
		Point2D.Double p;
		int size = vertices.size();
		for (int i=0; i<size; i++) {
			Segment s = new Segment(vertices.elementAt(i), vertices.elementAt( (i+1)%size ));
			p = s.intersection(l);
			if ((p!=null) && ((resultado == null) || (p.distance(l.getP1()) < distancia))) { 
				resultado = p; distancia = p.distance(l.getP1());
			} 
		}
		
		return resultado;		
	}

	@Override
	public void svgPaint(Element docParent) {
		Document svgDoc = docParent.getOwnerDocument();
		Element polygon = svgDoc.createElementNS(svgNS, "polygon");
		addAttributes(polygon);
		
		StringBuffer puntos=new StringBuffer("");
		for (Point2D p : vertices) {
			puntos.append( java.lang.Double.toString(p.getX()));
			puntos.append(",");
			puntos.append( java.lang.Double.toString(p.getY()));
			puntos.append(" ");
		}
		polygon.setAttributeNS (null, "points" , puntos.toString() );
		
		
		
		docParent.appendChild(polygon);
	}

	public Vector<Point2D> getVertices() {
		return vertices;
	}

	protected void setVertices(Vector<Point2D> vertices) {
		this.vertices = vertices;
	}

//	public static void main(String[] args) {
//		SVGPolygon pol = new SVGPolygon("1,3.5   4 3, 4 6,    4, 7 ");
//	}
}
