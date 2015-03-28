package svgview.shapes;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SVGRectangle extends SVGSimpleShape { 
	protected double x;
	protected double y;
	protected double width;
	protected double height;
	protected double rx = 0 ;
	protected double ry = 0 ;
		
	public SVGRectangle( Rectangle2D bounds ) {
		this( bounds.getX(), bounds.getY(), bounds.getWidth() , bounds.getHeight() );
	}
	
	public SVGRectangle(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public SVGRectangle(double x, double y, double width, double height, String style) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.setAttribute("style", style);
	}
	
	public SVGRectangle(double x, double y, double width, double height, double rx, double ry) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		if ((rx>0) && (ry>0)) {
			this.rx = rx;
			this.ry = ry;
		}
	}
	
	public SVGRectangle(double x, double y, double width, double height, 
					    double rx, double ry, String style) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		if ((rx>0) && (ry>0)) {
			this.rx = rx;
			this.ry = ry;
		}
		this.setAttribute("style", style);
	}
	
	@Override
	public Point2D.Double intersection(Segment l) {
		// Intersectar 4 lados y coger el punto más cercano.
		// Teniendo en cuenta esquinas redondeadas
		Point2D.Double[] p = new Point2D.Double[8];
		
		Segment s1 = new Segment(x+rx, y, x+width-rx, y);
		Segment s2 = new Segment(x+width, y+ry, x+width, y+height-ry);
		Segment s3 = new Segment(x+rx, y+height, x+width-rx, y+height);
		Segment s4 = new Segment(x, y+ry, x, y+height-ry);		

		p[0] = s1.intersection(l);
		p[1] = s2.intersection(l);
		p[2] = s3.intersection(l);
		p[3] = s4.intersection(l);
		
		// Rounded corners
		SVGEllipse e1, e2, e3, e4;
		
		if ((rx>0) && (ry>0)) {
			e1 = new SVGEllipse(x+rx, y+ry, rx, ry);
			e2 = new SVGEllipse(x+rx, y+height-ry, rx, ry);
			e3 = new SVGEllipse(x+width-rx, y+ry, rx, ry);
			e4 = new SVGEllipse(x+width-rx, y+height-ry, rx, ry);
			p[4] = e1.intersection(l);
			p[5] = e2.intersection(l);
			p[6] = e3.intersection(l);
			p[7] = e4.intersection(l);
		}				
		
		Point2D.Double resultado = null; 
		double distancia = l.getLongitude();
		
		// buscar el punto intersecado mas cercano
		// al origen del segmento.
		for (int i=0; i<8; i++) {
			if ((p[i]!=null) && ((resultado == null) || 
				(p[i].distance(l.getP1()) < distancia))) { 
				resultado = p[i]; 
				distancia = p[i].distance(l.getP1());
			} 
		}
	
		return resultado;
	}
	
	public void svgPaint(Element docParent) {
		Document svgDoc = docParent.getOwnerDocument();
		Element rect = svgDoc.createElementNS (svgNS, "rect");

		addAttributes(rect);
	    
	    rect.setAttributeNS (null, "x", java.lang.Double.toString(x));
	    rect.setAttributeNS (null, "y", java.lang.Double.toString(y));
	    rect.setAttributeNS (null, "width", java.lang.Double.toString(width));
	    rect.setAttributeNS (null, "height", java.lang.Double.toString(height));
	    if ((rx>0)&&(ry>0)) {
	    	rect.setAttributeNS (null, "rx", java.lang.Double.toString(rx));	    
	    	rect.setAttributeNS (null, "ry", java.lang.Double.toString(ry));
	    }
		
	    // TODO:
	    try {
		    if ( width  < 0 || height < 0) {
		    	throw new Exception("rect con width negativo!!!!");
		    }
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	    
		docParent.appendChild(rect);
		
	}
	
	public double getCenterX(){
		return x + width/2;
	}
	
	public double getCenterY(){
		return y + height/2;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SVGRectangle r = new SVGRectangle(5, 5, 10, 10);
		System.out.println(r.intersection(  new Segment(0, 10, 100, 10 )));
		
	}

}
