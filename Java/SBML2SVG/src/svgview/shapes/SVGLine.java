package svgview.shapes;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SVGLine extends SVGSimpleShape {
	protected Segment s; 
	
	public SVGLine(Segment s){
		super();
		this.s = (Segment)s.clone();
	}

	public SVGLine(Segment s, String style){
		super();
		this.s = (Segment)s.clone();
		this.setAttribute("style", style);
	}
	
	public SVGLine(double x1, double y1, double x2, double y2){
		super();
		s = new Segment(x1, y1, x2, y2);
	}
	
	public SVGLine(double x1, double y1, double x2, double y2, String style){
		super();
		s = new Segment(x1, y1, x2, y2);
		this.setAttribute("style", style);
	}
	
	public SVGLine(Point2D p1, Point2D p2) {
		super();
		s = new Segment(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}

	@Override
	public Double intersection(Segment l) {		
		return s.intersection(l);
	}

	@Override
	public void svgPaint(Element docParent) {
		Document svgDoc = docParent.getOwnerDocument();
		Element line = svgDoc.createElementNS (svgNS, "line");

		addAttributes(line);
	    
	    line.setAttributeNS (null, "x1", java.lang.Double.toString(s.getX1()));
	    line.setAttributeNS (null, "y1", java.lang.Double.toString(s.getY1()));
	    line.setAttributeNS (null, "x2", java.lang.Double.toString(s.getX2()));
	    line.setAttributeNS (null, "y2", java.lang.Double.toString(s.getY2()));
		
		docParent.appendChild(line);
	}
	
	

}
