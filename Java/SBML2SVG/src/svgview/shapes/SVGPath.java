package svgview.shapes;

import java.awt.geom.Point2D;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SVGPath extends SVGSimpleShape {
	private Vector<Point2D> vertices;
	
	protected SVGPath() {
		
	}
	
	public SVGPath(Vector<Point2D> vertices) {
		super();		
		if (vertices.size() > 1) {
		   this.vertices = vertices;
		} else {
			new Exception("La ruta necesita al menos 2 puntos.").printStackTrace();
		}
	}
	
	public SVGPath(Vector<Point2D> vertices, String style) {
		this(vertices);
		this.setAttribute("style", style);
	}
	
	public SVGPath(String strVertices) {
		this.vertices = new Vector<Point2D>();
		String[] numeros = strVertices.split("[ ,;]+");
		if (numeros.length > 3) {
			try {
				for(int i=0; i<numeros.length; i++) {
					this.vertices.add( 
							new Point2D.Double(java.lang.Double.parseDouble(numeros[i]),
											   java.lang.Double.parseDouble(numeros[++i])
											  )
							);
				}
			} catch (Exception e) {
				System.err.println("Error leyendo cadena con puntos de la ruta.");
				e.printStackTrace();			
			}
		} else {			
			new Exception("La ruta necesita al menos 2 puntos.").printStackTrace();
		}
	}
	
	public SVGPath(String strVertices, String style) {
		this(strVertices);
		this.setAttribute("style", style);
	}
	
	@Override
	public Point2D.Double intersection(Segment l) {		
		Point2D.Double resultado = null; 		
		return resultado;		
	}

	@Override
	public void svgPaint(Element docParent) {
		Document svgDoc = docParent.getOwnerDocument();
		Element path = svgDoc.createElementNS(svgNS, "path");
		addAttributes(path);
		
		StringBuffer puntos=new StringBuffer("");
		
		Point2D p = vertices.get(0);
		// mover a inicio
		puntos.append("M" + java.lang.Double.toString(p.getX()) + " " + java.lang.Double.toString(p.getY() ) );
		// Recorrer puntos
		for (int i = 1; i<vertices.size(); i++) {
			p = vertices.get(i);
			puntos.append(" L" + java.lang.Double.toString(p.getX()) + " " + java.lang.Double.toString(p.getY() ) );
		}
		
		path.setAttributeNS (null, "d" , puntos.toString() );	
		
		docParent.appendChild(path);
	}

	public Vector<Point2D> getVertices() {
		return vertices;
	}

	protected void setVertices(Vector<Point2D> vertices) {
		this.vertices = vertices;
	}

}
