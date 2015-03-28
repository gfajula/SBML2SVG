package svgview.shapes;

import java.awt.geom.Point2D.Double;

import org.w3c.dom.Element;

public class SVGText extends SVGSimpleShape {
	private Element textElement;
	
	public SVGText(Element text) {
		super();
		this.textElement = text;
	}
	
	@Override
	public Double intersection(Segment l) {
		return null;
	}

	@Override
	public void svgPaint(Element docParent) {
		if (textElement!=null){
			addAttributes(this.textElement);
			docParent.appendChild(textElement);
		}
	}

}
