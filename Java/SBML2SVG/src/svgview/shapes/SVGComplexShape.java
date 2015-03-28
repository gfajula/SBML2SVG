package svgview.shapes;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.HashMap;
import java.util.Vector; 

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SVGComplexShape implements SVGShape {
	protected HashMap<String, String> attributes = new HashMap<String, String>();
	protected Vector<SVGShape> components = new Vector<SVGShape>(); 
	
	public SVGComplexShape() {
		super();
	}
	
	public SVGComplexShape(String style) {
		super();
		this.setAttribute("style", style);
	}
	
	public SVGComplexShape(SVGShape shp) {
		super();
		this.components.add(shp);
	}
	
	public SVGComplexShape(SVGShape shp, String style) {
		super();
		this.setAttribute("style", style);
		this.components.add(shp);
	}
	
	@Override
	public SVGShape composeWith(SVGShape shp) {
		this.components.add(shp);
		return this;
	}

	@Override
	public Double intersection(Segment l) {
		Point2D.Double punto, resultado = null; 
		double distancia = l.getLongitude();
		
		// Se calcula la intersección a esta figura buscando la intersección
		// más cercana al origen, de todas las intersecciones de sus componentes 
		for (SVGShape s : this.components) {
			punto = s.intersection(l);
			if (s!=null) {
				if ((punto!=null) && 
					((resultado == null) || (punto.distance(l.getP1()) < distancia))) { 
					resultado = punto; distancia = punto.distance(l.getP1());
				}
			}
		}
		
		return resultado;
	}

	@Override
	public void svgPaint(Element docParent) {
		// Aplica los estilos al elemento 'g' y después
		// dibuja cada uno de los componentes
		Document svgDoc = docParent.getOwnerDocument(); 
	    Element complex = svgDoc.createElementNS (svgNS, "g");
	    addAttributes(complex);
	    
	    if (this.components.size() <= 0) {
	    	return;
	    } 
	    
		for (SVGShape s : this.components) {
			if ( s!= null) s.svgPaint(complex);
		}		
		
		docParent.appendChild(complex);
	}
	
	public void add(SVGShape shp) {
		if (shp != null) this.components.add(shp);
	}

	public void setAttribute(String name, String value){
		attributes.put(name, value);	
	}
	
	protected void addAttributes(Element elem){
		for(String s : attributes.keySet() ) {
			elem.setAttribute(s, attributes.get(s));
		}
	}
	
	public static void main(String[] args) {
		
	}
}
