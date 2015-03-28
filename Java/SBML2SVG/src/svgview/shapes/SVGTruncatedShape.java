package svgview.shapes;

import java.awt.geom.Point2D;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SVGTruncatedShape extends SVGSimpleShape {
	double x, y, width, height, cornerRadius, activityMargin;	
	
	public SVGTruncatedShape(double x, double y, double width, double height,
			double cornerRadius, double activityMargin) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.cornerRadius = cornerRadius;
		this.activityMargin = activityMargin;
	}
	
	public SVGTruncatedShape(double x, double y, double width, double height,
			double cornerRadius, String style, double activityMargin) {
		this(x, y, width, height, cornerRadius, activityMargin);
		this.setAttribute("style", style);
	}

	@Override
	public Point2D.Double intersection(Segment l) {
		// Intersectar 6 lados y coger el punto mas cercano.
		// Teniendo en cuenta esquinas redondeadas
		Point2D.Double[] p = new Point2D.Double[8];
		
		Segment s1 = new Segment(x+cornerRadius, y, x+width, y);
		Segment s2 = new Segment(x+width, y, x+width, y+height*0.6);
		Segment s3 = new Segment(x+width, y+height*0.6, x+width*0.8, y+height*0.4);
		Segment s4 = new Segment(x+width*0.8, y+height*0.4, x+width*0.8, y+height);		
		Segment s5 = new Segment(x+width*0.8, y+height, x+cornerRadius, y+height);
		Segment s6 = new Segment(x, y+cornerRadius, x, y+height-cornerRadius);
		

		p[0] = s1.intersection(l);
		p[1] = s2.intersection(l);
		p[2] = s3.intersection(l);
		p[3] = s4.intersection(l);
		p[4] = s5.intersection(l);
		p[5] = s6.intersection(l);
		
		// Rounded corners
		SVGCircle c1, c2;
		if (cornerRadius>0) {
			c1 = new SVGCircle(x+cornerRadius, y+cornerRadius, cornerRadius);
			c2 = new SVGCircle(x+cornerRadius, y+height-cornerRadius, cornerRadius);

			p[6] = c1.intersection(l);
			p[7] = c2.intersection(l);
		}				
		
		Point2D.Double resultado = null; 
		double distancia = l.getLongitude();
		
		for (int i=0; i<8; i++) {
			if ((p[i]!=null) && ((resultado == null) || (p[i].distance(l.getP1()) < distancia))) { 
				resultado = p[i]; distancia = p[i].distance(l.getP1());
			} 
		}
	
		return resultado;
	}

	@Override
	public void svgPaint(Element docParent) {
		Document svgDoc = docParent.getOwnerDocument();
				
	    Element rect = svgDoc.createElementNS (svgNS, "path");
	    addAttributes(rect);
	    rect.setAttributeNS (null, "d", 
	    		"M" + Double.toString(x+cornerRadius) + " " + Double.toString(y - activityMargin) + " " +
	    		"Q" + Double.toString(x - activityMargin) + " " + Double.toString(y - activityMargin) + " " +
	    			  Double.toString(x - activityMargin) + " " + Double.toString(y+cornerRadius) + " " +
	    		"L" + Double.toString(x - activityMargin) + " " + Double.toString(y+height - cornerRadius) +
	    		"Q" + Double.toString(x - activityMargin) + " " + Double.toString(y+height + activityMargin) + " " +
  			  		  Double.toString(x+cornerRadius) + " " + Double.toString(y+height + activityMargin) + " " +
  			    "L" + Double.toString(x+width*0.8 + activityMargin) + " " + Double.toString(y+height + activityMargin) +	  
  			    "L" + Double.toString(x+width*0.8 + activityMargin) + " " + Double.toString(y+height*0.4 + activityMargin) +
  			    "L" + Double.toString(x+width + activityMargin) + " " + Double.toString(y+height*0.6 + activityMargin) +
  			    "L" + Double.toString(x+width + activityMargin) + " " + Double.toString(y - activityMargin) +
	    		
	    		"Z"
	    		);
		
	    docParent.appendChild(rect);
	}

}
