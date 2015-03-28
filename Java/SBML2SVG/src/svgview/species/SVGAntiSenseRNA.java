package svgview.species;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import model.specie.MAntiSenseRNA;
import model.specie.gene.MRegion;

import org.w3c.dom.Document;

import svgview.shapes.SVGPolygon;
import svgview.shapes.SVGShape;

public class SVGAntiSenseRNA extends SVGRNA {
	MAntiSenseRNA mrna;
	
	public SVGAntiSenseRNA(Document svgDoc, MAntiSenseRNA mrna) {
		super(svgDoc, mrna);
		this.mrna = mrna;		
	}

	@Override
	protected SVGShape getSlantedRect(double x, double y, double width, double height) {
	    SVGShape rect = new SVGPolygon ( "" +
	    		(x) + " " + (y) + " " +	    		
	    		(x + width  - height) + " " + (y) + " " +	    		
  			    (x + width) + " " + (y + height) + " " +	  
			    (x + height) + " " + (y + height) + " " + 
			    (x) + " " + (y)
  			    );
		return rect;
	}
	
	
	protected double getRegionPosX(MRegion mr, Rectangle2D rect){
		return rect.getX() + (rect.getWidth()-rect.getHeight()) * mr.getPos();
	}
	
	public Point2D getLinkAnchor(int type) {
		Point2D p = null;
		
	    int h = mrna.getHomodimer();	    
	    double heightEach = mrna.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = mrna.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
	    
	    if ( widthEach <= heightEach ) 
	    	return new Point2D.Double( mrna.getBounds().getCenterX(), mrna.getBounds().getCenterY() );	    
	    
		switch (type) {
		case SVGSpecie.ANCHOR_N:
			p = new Point2D.Double(mrna.getBounds().getX() + (widthEach-heightEach)/2,
								   mrna.getBounds().getY() );
			break;
		case SVGSpecie.ANCHOR_NNE:	
			p = new Point2D.Double(mrna.getBounds().getX() + 3*(widthEach-heightEach)/4 ,
                    			   mrna.getBounds().getY() );
			break;			
		case SVGSpecie.ANCHOR_NE:
			p = new Point2D.Double(mrna.getBounds().getX() + (widthEach-heightEach) ,
                    			   mrna.getBounds().getY() );
			break;
		case SVGSpecie.ANCHOR_ENE:
			p = new Point2D.Double(mrna.getBounds().getX() + widthEach - 3*heightEach/4,
								   mrna.getBounds().getY() + heightEach/4 );
			break;				
		case SVGSpecie.ANCHOR_E:
			p = new Point2D.Double(mrna.getBounds().getX() + widthEach - heightEach/2 ,
		                           mrna.getBounds().getY() + heightEach/2 );
			break;
		case SVGSpecie.ANCHOR_ESE:
			p = new Point2D.Double(mrna.getBounds().getX() + widthEach - heightEach/4,
					   			   mrna.getBounds().getY() + 3*heightEach/4 );
			break;				
		case SVGSpecie.ANCHOR_SE:
			p = new Point2D.Double(mrna.getBounds().getX() + widthEach ,
					   			   mrna.getBounds().getY() + heightEach );
			break;				
		case SVGSpecie.ANCHOR_SSE:
			p = new Point2D.Double(mrna.getBounds().getX() + heightEach + 3*(widthEach-heightEach)/4 ,
					   			   mrna.getBounds().getY() + heightEach );
			break;				
		case SVGSpecie.ANCHOR_S:
			p = new Point2D.Double(mrna.getBounds().getX() + heightEach + (widthEach-heightEach)/2,
					               mrna.getBounds().getY() + heightEach );
			break;
		case SVGSpecie.ANCHOR_SSW:
			p = new Point2D.Double(mrna.getBounds().getX() + heightEach + (widthEach-heightEach)/4 ,
					   			   mrna.getBounds().getY() + heightEach );
			break;				
		case SVGSpecie.ANCHOR_SW:
			p = new Point2D.Double(mrna.getBounds().getX() + heightEach ,
								   mrna.getBounds().getY() + heightEach );
			break;				
		case SVGSpecie.ANCHOR_WSW:
			p = new Point2D.Double(mrna.getBounds().getX() + 3*heightEach/4,
	   				   			   mrna.getBounds().getY() + 3*heightEach/4 );
			break;			
		case SVGSpecie.ANCHOR_W:
			p = new Point2D.Double(mrna.getBounds().getX() + heightEach/2 ,
                    			   mrna.getBounds().getY() + heightEach/2 );
			break;
		case SVGSpecie.ANCHOR_WNW:
			p = new Point2D.Double(mrna.getBounds().getX() + heightEach/4,
     			   				   mrna.getBounds().getY() + heightEach/4 );
			break;			
		case SVGSpecie.ANCHOR_NW:
			p = new Point2D.Double(mrna.getBounds().getX() ,
								   mrna.getBounds().getY() );
			break;			
		case SVGSpecie.ANCHOR_NNW:
			p = new Point2D.Double(mrna.getBounds().getX() + (widthEach-heightEach)/4 ,
	   				   			   mrna.getBounds().getY() );
			break;					
		}
		
		return p;
	}	
	

	
}
