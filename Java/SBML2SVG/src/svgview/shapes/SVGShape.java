package svgview.shapes;

import java.awt.geom.Point2D;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.Element;

import svgview.SVGPaintable;

/**
 * Interfaz de una forma dibujable en SVG.
 * 
 * @author Guillermo Fajula Leal
 * 
 */
public interface SVGShape extends SVGPaintable {
	/**
	 * Namespace de los elementos de un documento SVG
	 */
	public static final String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;	
	/**
	 * Umbral para el testeo de intersecciones.
	 */
	public static final double THRESHOLD = 0.0001;
	
	/**
	 * Componer con otra forma
	 * 
	 * @param shp Forma a unir a esta.
	 * @return La forma resultante de componer las dos
	 */
	public SVGShape composeWith(SVGShape shp);
	
	/**
	 * Metodo para intersectar rectas con una determinada forma, y
	 * asi poder determinar en que punto colocar el extremo de una
	 * flecha.
	 * Cada forma que implemente SVGShape debe definir este metodo.
	 * 
	 * @param l Segmento a comprobar
	 * @return punto en el que el segmento corta la forma, o null si no lo corta
	 */
	public Point2D.Double intersection(Segment l);
	
	/**
	 * Dibujar en el documento.
	 *
	 * @param Element nodo al que se añadiran los nodos del dibujo de esta
	 * 		 figura.
	 */
	public void svgPaint(Element docParent);
	
	/**
	 * Establecer un atributo en esta figura.
	 * 
	 * @param name
	 * @param value
	 */
	public void setAttribute(String name, String value);

	
}
