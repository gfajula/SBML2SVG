package svgview;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.Element;

import svgcontroller.SBML2SVGException;

public interface SVGPaintable {
	public static final String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
	public void svgPaint(Element docParent) throws SBML2SVGException;
}
