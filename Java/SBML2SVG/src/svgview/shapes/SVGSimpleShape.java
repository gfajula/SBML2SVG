package svgview.shapes;

import java.awt.geom.Point2D;
import java.util.HashMap;

import org.w3c.dom.Element;

/**
 * Clase abstracta que modela una forma simple dibujable en SVG.
 * 
 * @author Guillermo Fajula Leal
 * 
 */
public abstract class SVGSimpleShape implements SVGShape {
	protected HashMap<String, String> attributes = new HashMap<String, String>();
	
	@Override
	public SVGShape composeWith(SVGShape shp) {
		SVGComplexShape newShape = new SVGComplexShape();
		newShape.add(this);
		newShape.add(shp);
		return newShape;
	}

	@Override
	public abstract Point2D.Double intersection(Segment l);

	@Override
	public abstract void svgPaint(Element docParent);
		
	public void setAttribute(String name, String value){
	   attributes.put(name, value);	
	}
	
	protected void addAttributes(Element elem){
		for(String s : attributes.keySet() ) {
			elem.setAttribute(s, attributes.get(s));
		}
	}

}
