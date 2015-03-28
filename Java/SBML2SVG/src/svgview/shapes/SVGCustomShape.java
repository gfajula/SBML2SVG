package svgview.shapes;

import java.awt.geom.Point2D.Double;

import org.w3c.dom.Element;

public class SVGCustomShape extends SVGSimpleShape {
	protected Element element;
	
	public SVGCustomShape( Element element ) {
		this.element = element;
	}

	@Override
	public Double intersection(Segment l) {		
		return null;
	}

	@Override
	public void svgPaint(Element docParent) {
		addAttributes(this.element);
		docParent.appendChild( this.element );
	}

}
