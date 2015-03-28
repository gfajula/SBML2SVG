package svgview.shapes;

import java.awt.geom.Point2D;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import svgcontroller.SBML2SVGException;

public class SVGSemiRoundRectangle extends SVGSimpleShape {
	// Factor para simular esquinas redondeadas mediante curvas de bezier cubicas
	protected final static double KAPPA = 0.55228; 
	protected double x;
	protected double y;
	protected double width;
	protected double height;
	protected double cornerRadius = 5 ;
		
	public SVGSemiRoundRectangle(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public SVGSemiRoundRectangle(double x, double y, double width, double height, String style) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.setAttribute("style", style);
	}
	
	public SVGSemiRoundRectangle(double x, double y, double width, double height, double cornerRadius ) throws SBML2SVGException {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		if ( cornerRadius > 0 ) {
			this.cornerRadius = cornerRadius;
		} else {
			throw new SBML2SVGException( "" + this.getClass() + " no fue creado con 'cornerRadius'>0. (En ese caso se recomienda usar SVGRectangle)");
		}
	}
	
	public SVGSemiRoundRectangle(double x, double y, double width, double height, 
					    double cornerRadius, String style) throws SBML2SVGException {
		this( x,y,width,height,cornerRadius);
		this.setAttribute("style", style);
	}
	
	@Override
	public Point2D.Double intersection(Segment l) {
		// Intersectar 4 lados y coger el punto más cercano.
		// Teniendo en cuenta esquinas redondeadas
		Point2D.Double[] p = new Point2D.Double[8];
		
		Segment s1 = new Segment(x, y, x+width, y);
		Segment s2 = new Segment(x+width, y, x+width, y+height-cornerRadius);
		Segment s3 = new Segment(x+cornerRadius, y+height, x+width-cornerRadius, y+height);
		Segment s4 = new Segment(x, y, x, y+height-cornerRadius);		

		p[0] = s1.intersection(l);
		p[1] = s2.intersection(l);
		p[2] = s3.intersection(l);
		p[3] = s4.intersection(l);
		
		// Rounded corners
		SVGCircle e1, e2;
		
		if ( cornerRadius>0 ) {
			e1 = new SVGCircle(x+width-cornerRadius, y+cornerRadius, cornerRadius );
			e2 = new SVGCircle(x+width-cornerRadius, y+height-cornerRadius, cornerRadius );
			p[4] = e1.intersection(l);
			p[5] = e2.intersection(l);
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
		Element rect = svgDoc.createElementNS (svgNS, "path");

		
	    
		
	    rect.setAttributeNS (null, "d", 
	    		"M" + Double.toString( x ) + " " + Double.toString( y ) + " " +
	    		"L" + Double.toString( x ) + " " + Double.toString( y + height - cornerRadius ) +
	    		"C" + Double.toString( x ) + " " + Double.toString( y + height - cornerRadius * (1-KAPPA) ) + " " +
	    			  Double.toString( x + cornerRadius*(1-KAPPA) ) + " " + Double.toString( y + height) + " " +
	    			  Double.toString( x + cornerRadius ) + " " + Double.toString( y + height) + " " +
	    		"L" + Double.toString( x + width - cornerRadius) + " " + Double.toString( y+height ) +
	    		"C" + Double.toString( x + width - cornerRadius*(1-KAPPA) ) + " " + Double.toString( y+height ) + " " +
  			  		  Double.toString( x + width) + " " + Double.toString( y + height - cornerRadius * (1-KAPPA) ) + " " +
  			  		  Double.toString( x + width ) + " " + Double.toString( y + height - cornerRadius) + " " +
  			    "L" + Double.toString( x + width ) + " " + Double.toString( y  ) + " " +

	    		
	    		"Z"
	    		);
		
	    addAttributes(rect);
	    
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
		SVGSemiRoundRectangle r = new SVGSemiRoundRectangle(5, 5, 10, 10);
		System.out.println(r.intersection(  new Segment(0, 10, 100, 10 )));
		
	}

}
