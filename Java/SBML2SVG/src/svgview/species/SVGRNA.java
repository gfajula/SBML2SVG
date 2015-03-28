package svgview.species;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import model.specie.MRNA;
import model.specie.gene.MRegion;

import org.w3c.dom.Document;

import svgcontroller.SBML2SVGException;
import svgview.SVGConfig;
import svgview.shapes.SVGCircle;
import svgview.shapes.SVGComplexShape;
import svgview.shapes.SVGLine;
import svgview.shapes.SVGPolygon;
import svgview.shapes.SVGRectangle;
import svgview.shapes.SVGSemiRoundRectangle;
import svgview.shapes.SVGShape;
import svgview.shapes.SVGText;
import svgview.shapes.Segment;
import svgview.util.SVGTextRenderer;
import svgview.util.SVGUtil;

public class SVGRNA extends SVGSpecie {
	public static final double CORNER_RADIUS = 5 ; 
	public static final double NEG_CORNER_RADIUS = 1 ; //CORNER_RADIUS * 0.414213 ;
	private MRNA mrna;
	private Document svgDoc;
	protected Point2D.Double center;
	protected SVGShape shape;
	protected Vector<MRegion> regions;
	
	public SVGRNA(Document svgDoc, MRNA mrna) {
		super(svgDoc, mrna);
		this.svgDoc = svgDoc;
		this.mrna = mrna;
		this.regions = mrna.getRegions();
		this.bounds = mrna.getBounds();
		this.center = new Point2D.Double();
	}
	
	@Override
	public Document getDocument() {
		return svgDoc;
	}

	protected SVGShape getRNAPolygon(double x, double y, double width, double height) {		
	    SVGShape rect = getSlantedRect(x, y, width, height);
	    Color c = mrna.getUsualView().getPaint().getColor();    
	    rect.setAttribute("fill", SVGUtil.getHexColor(c));
//	    rect.setAttributeNS (null, "fill-opacity", "0.5");
	    rect.setAttribute("stroke", "black");
	    rect.setAttribute("stroke-width", "1");
	    
	    if ( this.mrna.isHypothetical() ) {
	    	rect.setAttribute ("style", "stroke-dasharray: 6, 3;");
	    }
	    
		return rect;
		
	}

	protected  SVGShape getSlantedRect(double x, double y, double width, double height) {
		SVGShape rect = new SVGPolygon (
	    		Double.toString(x+height) + " " + Double.toString(y) + " " +	    		
	    		Double.toString(x+width) + " " + Double.toString(y) + " " +	    		
  			    Double.toString(x+width - height) + " " + Double.toString(y+height) + " " +	  
			    Double.toString(x) + " " + Double.toString(y+height) + " " + 
			    Double.toString(x+height) + " " + Double.toString(y)
  			    );
		return rect;
	}
	
		
	public SVGShape buildSVGShape() {
		SVGComplexShape complex = new SVGComplexShape();
		complex.setAttribute("id", mrna.getIdAlias());
		if ( !SVGConfig.omitJavascript )
			complex.setAttribute("onclick", "infoWindow(\"" + mrna.getIdAlias() + "\"," +
					 "\"" + mrna.getName() + "\","+
					 "\"" + "RNA" + "\"" +
					 ");");		
	    
	    int h = mrna.getHomodimer();	    
	    double heightEach = mrna.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = mrna.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
	    
	    this.center.x = mrna.getBounds().getX() + widthEach/2;
	    this.center.y = mrna.getBounds().getY() + heightEach/2;
	    
	    SVGShape shp;
	    for (int hc = h-1 ; hc>=0 ; hc--) {	  
	    	shp = this.getRNAPolygon(
	    			mrna.getBounds().getX() + HOMODIMER_WIDTH*hc  , 
	    			mrna.getBounds().getY() + HOMODIMER_HEIGHT*hc  , 
	    			widthEach , 
	    			heightEach 
	    		);  
	    	
	    	complex.add(shp);	    	
	    	
	    }
	    
	    addRegions(complex, this.regions , this.bounds);

		complex.add( getLabel() );
		
	    complex.add( getInfoUnit(
	    		new Rectangle2D.Double( this.getMspecies().getBounds().getX() , 
	    								this.getMspecies().getBounds().getY(), 
	    								widthEach, 
	    								heightEach )
	    		) );
	    
		shape = complex;
		return complex;
	}

	public SVGShape buildSVGShapeSBGN() throws SBML2SVGException {
		SVGComplexShape complex = new SVGComplexShape();
		complex.setAttribute("id", mrna.getIdAlias());
		if ( !SVGConfig.omitJavascript )
			complex.setAttribute("onclick", "infoWindow(\"" + mrna.getIdAlias() + "\"," +
					 "\"" + mrna.getName() + "\","+
					 "\"" + "RNA" + "\"" +
					 ");");		
	    
	    int h = Math.min( 2,    mrna.getHomodimer() );	    
	    double heightEach = mrna.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = mrna.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
	    
	    this.center.x = mrna.getBounds().getX() + widthEach/2;
	    this.center.y = mrna.getBounds().getY() + heightEach/2;
	    
	    SVGShape shp;
	    for (int hc = h-1 ; hc>=0 ; hc--) {
	    	if ( this.isClone() ) {
		    	shp = this.getClonedRectSBGN(
		    			mrna.getBounds().getX() + HOMODIMER_WIDTH*hc  , 
		    			mrna.getBounds().getY() + HOMODIMER_HEIGHT*hc  , 
		    			widthEach , 
		    			heightEach 
		    		);	    		
	    	} else {
		    	shp = this.getRectSBGN(
		    			mrna.getBounds().getX() + HOMODIMER_WIDTH*hc  , 
		    			mrna.getBounds().getY() + HOMODIMER_HEIGHT*hc  , 
		    			widthEach , 
		    			heightEach 
		    		);  
	    	}
	    	
	    	complex.add(shp);	    	
	    	
	    }
	    
	    addRegions(complex, this.regions , this.bounds);
	    
	    // TO DO: encapsular en metodo
		complex.add( new SVGText(
	    				SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
				    		center.x , center.y, 
				    		mrna.getName() ,
				    		12 ))
	    		);
		
		complex.add( getInfoUnit( 
						new Rectangle2D.Double(
								  this.getBBox().getX(),
								  this.getBBox().getY(), 
								  widthEach, heightEach ) ) );	
		
		shape = complex;
		return complex;
	}

	public Point2D getLinkAnchor(int type) {
		if ( SVGConfig.SBGNMode ) {
			return getLinkAnchorSBGN(type);
		} else {
			return getLinkAnchorCD(type);
		}
	}
	
	public Point2D getLinkAnchorSBGN(int type) {
		Point2D p = null;
		
	    int h = mrna.getHomodimer();	    
	    double heightEach = mrna.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1)  ;
	    double widthEach = mrna.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1) ;
	    double x = mrna.getBounds().getX() ;
	    double y = mrna.getBounds().getY() ;
	    
		switch (type) {
		case SVGSpecie.ANCHOR_N:
			p = new Point2D.Double(x + widthEach/2,
								   y );
			break;
		case SVGSpecie.ANCHOR_NNE:	
			p = new Point2D.Double(x + 3*widthEach/4 ,
                    			   y );
			break;			
		case SVGSpecie.ANCHOR_NE:
			p = new Point2D.Double(x + widthEach ,
                    			   y );
			break;
		case SVGSpecie.ANCHOR_ENE:
			p = new Point2D.Double(x + widthEach ,
								   y + heightEach/4 );
			break;				
		case SVGSpecie.ANCHOR_E:
			p = new Point2D.Double(x + widthEach ,
		                           y + heightEach/2 );
			break;
		case SVGSpecie.ANCHOR_ESE:
			p = new Point2D.Double(x + widthEach ,
					   			   y + 3*heightEach/4 );
			break;				
		case SVGSpecie.ANCHOR_SE:
			p = new Point2D.Double(x + widthEach - NEG_CORNER_RADIUS ,
					   			   y + heightEach - NEG_CORNER_RADIUS );
			break;				
		case SVGSpecie.ANCHOR_SSE:
			p = new Point2D.Double(x + 3*widthEach/4 ,
					   			   y + heightEach );
			break;				
		case SVGSpecie.ANCHOR_S:
			p = new Point2D.Double(x + widthEach/2,
					               y + heightEach );
			break;
		case SVGSpecie.ANCHOR_SSW:
			p = new Point2D.Double(x + widthEach/4 ,
					   			   y + heightEach );
			break;				
		case SVGSpecie.ANCHOR_SW:
			p = new Point2D.Double(x + NEG_CORNER_RADIUS ,
								   y + heightEach - NEG_CORNER_RADIUS );
			break;				
		case SVGSpecie.ANCHOR_WSW:
			p = new Point2D.Double(x ,
	   				   			   y + 3*heightEach/4 );
			break;			
		case SVGSpecie.ANCHOR_W:
			p = new Point2D.Double(x ,
                    			   y + heightEach/2 );
			break;
		case SVGSpecie.ANCHOR_WNW:
			p = new Point2D.Double(x ,
     			   				   y + heightEach/4 );
			break;			
		case SVGSpecie.ANCHOR_NW:
			p = new Point2D.Double(x  ,
								   y );
			break;			
		case SVGSpecie.ANCHOR_NNW:
			p = new Point2D.Double(x + widthEach/4 ,
	   				   			   y );
			break;					
		}
		
		return p;
	}

	
	public Point2D getLinkAnchorCD(int type) {
		Point2D p = null;
		
	    int h = mrna.getHomodimer();	    
	    double heightEach = mrna.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = mrna.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
	    
	    if ( widthEach <= heightEach ) 
	    	return new Point2D.Double( mrna.getBounds().getCenterX(), mrna.getBounds().getCenterY() );	    
	    
		switch (type) {
		case SVGSpecie.ANCHOR_N:
			p = new Point2D.Double(mrna.getBounds().getX() + heightEach + (widthEach-heightEach)/2,
								   mrna.getBounds().getY() );
			break;
		case SVGSpecie.ANCHOR_NNE:	
			p = new Point2D.Double(mrna.getBounds().getX() + heightEach + 3*(widthEach-heightEach)/4 ,
                    			   mrna.getBounds().getY() );
			break;			
		case SVGSpecie.ANCHOR_NE:
			p = new Point2D.Double(mrna.getBounds().getX() + widthEach ,
                    			   mrna.getBounds().getY() );
			break;
		case SVGSpecie.ANCHOR_ENE:
			p = new Point2D.Double(mrna.getBounds().getX() + widthEach - heightEach/4,
								   mrna.getBounds().getY() + heightEach/4 );
			break;				
		case SVGSpecie.ANCHOR_E:
			p = new Point2D.Double(mrna.getBounds().getX() + widthEach - heightEach/2 ,
		                           mrna.getBounds().getY() + heightEach/2 );
			break;
		case SVGSpecie.ANCHOR_ESE:
			p = new Point2D.Double(mrna.getBounds().getX() + widthEach - 3*heightEach/4,
					   			   mrna.getBounds().getY() + 3*heightEach/4 );
			break;				
		case SVGSpecie.ANCHOR_SE:
			p = new Point2D.Double(mrna.getBounds().getX() + widthEach - heightEach ,
					   			   mrna.getBounds().getY() + heightEach );
			break;				
		case SVGSpecie.ANCHOR_SSE:
			p = new Point2D.Double(mrna.getBounds().getX() + 3*(widthEach-heightEach)/4 ,
					   			   mrna.getBounds().getY() + heightEach );
			break;				
		case SVGSpecie.ANCHOR_S:
			p = new Point2D.Double(mrna.getBounds().getX() + (widthEach-heightEach)/2,
					               mrna.getBounds().getY() + heightEach );
			break;
		case SVGSpecie.ANCHOR_SSW:
			p = new Point2D.Double(mrna.getBounds().getX() + (widthEach-heightEach)/4 ,
					   			   mrna.getBounds().getY() + heightEach );
			break;				
		case SVGSpecie.ANCHOR_SW:
			p = new Point2D.Double(mrna.getBounds().getX() ,
								   mrna.getBounds().getY() + heightEach );
			break;				
		case SVGSpecie.ANCHOR_WSW:
			p = new Point2D.Double(mrna.getBounds().getX() + heightEach/4,
	   				   			   mrna.getBounds().getY() + 3*heightEach/4 );
			break;			
		case SVGSpecie.ANCHOR_W:
			p = new Point2D.Double(mrna.getBounds().getX() + heightEach/2 ,
                    			   mrna.getBounds().getY() + heightEach/2 );
			break;
		case SVGSpecie.ANCHOR_WNW:
			p = new Point2D.Double(mrna.getBounds().getX() + 3*heightEach/4,
     			   				   mrna.getBounds().getY() + heightEach/4 );
			break;			
		case SVGSpecie.ANCHOR_NW:
			p = new Point2D.Double(mrna.getBounds().getX() + heightEach  ,
								   mrna.getBounds().getY() );
			break;			
		case SVGSpecie.ANCHOR_NNW:
			p = new Point2D.Double(mrna.getBounds().getX() + heightEach + (widthEach-heightEach)/4 ,
	   				   			   mrna.getBounds().getY() );
			break;					
		}
		
		return p;
	}	
	

	
	@Override
	public Rectangle2D getBBox() {
		return this.bounds;
	}

	@Override
	public Point2D getCenter() {
		return center;
	}

	protected void addRegions(SVGComplexShape complex, Vector<MRegion> regions, Rectangle2D rect) {
	    if ((regions!=null)) {
	    	SVGShape rgn = null;	    	
	    	for (MRegion mr : regions) {
	    		if ( mr.getType() == MRegion.CODING_REGION ) {
	    			rgn = getCodingRegion(mr, rect);
	    			complex.add(rgn);
	    		} 	    		    			    		
	    	}
	    	
	    	for (MRegion mr : regions) {
	    		if ( mr.getType() == MRegion.PROTEIN_BINDING_DOMAIN) {
	    			rgn = getProteinBindingDomain(mr, rect);
	    			complex.add(rgn);
	    		} 	    		    			    		
	    	}
	    	
	    	for (MRegion mr : regions) {
	    		if ( mr.getType() == MRegion.MODIFICATION_SITE ) {
	    			rgn = getModificationSite(mr, rect);
	    			complex.add(rgn);
	    		}
	    		   			    		
	    	}
	    	
	    	
	    }	    
	}
		
	protected SVGShape getModificationSite(MRegion mr, Rectangle2D rect) {
		SVGComplexShape shp = new SVGComplexShape();
		shp.setAttribute("stroke", "black");
		
		double cx, cy;
		cx = getRegionPosX(mr, rect);
		cy = rect.getY() - 16;
		shp.add( new SVGLine( cx, rect.getY(),
							  cx, rect.getY() - 8		
							) );
		
		SVGCircle lollipop = new SVGCircle( cx , cy , 8   );
		lollipop.setAttribute("fill", "white");
		lollipop.setAttribute("stroke", "rgb(178,178,178)");
		shp.add(lollipop);
		
		SVGText resText = new SVGText( 
							SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
																		   cx, cy,
																		   mr.getState(), 
																		   9 ));
    	shp.add(resText);
		
		
//		SVGText resText = new SVGText( SVGTextRenderer.getInstance().drawTextCentered(svgDoc, p.x, p.y , mres.getState(), 9 ));
//    	shp.add(resText);
    	
		return shp;
	}


	protected SVGShape getCodingRegion(MRegion mr, Rectangle2D rect) {
		SVGComplexShape shp = new SVGComplexShape(
				       new SVGRectangle( getRegionPosX(mr, rect), 
										 rect.getY()-12,
										 getRegionWidth(mr, rect), 
										 24 ));
		shp.setAttribute("stroke", "black");
		shp.setAttribute("fill", "white");
		shp.setAttribute("stroke-linejoin", "bevel");
		
		if (mr.getName()!=null && !mr.getName().equals("")) {
		   SVGText resText = new SVGText( 
				SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
						                                       getRegionPosX(mr, rect)+ 
						                                       getRegionWidth(mr, rect)/2, 
						                                       rect.getY()+17,
															   mr.getName(), 
															   9 ));
		   resText.setAttribute("fill", "black");
		   shp.add(resText);
		}
		return shp;
	}
	
	protected SVGShape getProteinBindingDomain(MRegion mr, Rectangle2D rect) {
		SVGShape shp = new SVGRectangle( getRegionPosX(mr, rect),
				                         rect.getY()-8, 
				                         getRegionWidth(mr, rect), 
				                         16 );
		shp.setAttribute("stroke", "black");
		shp.setAttribute("fill", "white");
		shp.setAttribute("stroke-linejoin", "bevel");
		return shp;
	}
	
	protected double getRegionPosX(MRegion mr, Rectangle2D rect){
		return rect.getX() + rect.getHeight() + (rect.getWidth()-rect.getHeight()) * mr.getPos();
	}
	
	protected double getRegionWidth(MRegion mr, Rectangle2D rect){
		return (rect.getWidth()-rect.getHeight()) * mr.getSize();
	}
 

	protected SVGShape getRectSBGN(double x, double y, double width, double height) {
		SVGShape rect = new SVGSemiRoundRectangle(x, y, width, height);
	    Color c = mrna.getUsualView().getPaint().getColor();    
	    rect.setAttribute("fill", SVGUtil.getHexColor(c));
//	    rect.setAttributeNS (null, "fill-opacity", "0.5");
	    rect.setAttribute("stroke", "black");
	    rect.setAttribute("stroke-width", "1");
	    
	    if ( this.mrna.isHypothetical() ) {
	    	rect.setAttribute ("style", "stroke-dasharray: 6, 3;");
	    }
		return rect;
	}

	protected SVGShape getClonedRectSBGN(double x, double y, double width, double height) throws SBML2SVGException {
		SVGShape rect = new SVGSemiRoundRectangle(x, y, width,  height, CORNER_RADIUS );
		SVGShape background = new SVGSemiRoundRectangle(x, y, width,  height, CORNER_RADIUS );
		SVGShape cloneStripe = new SVGSemiRoundRectangle(x , y+ 3*height/4, width,  height/4, CORNER_RADIUS );
				
		
		SVGComplexShape shp = new SVGComplexShape();
		
		Color c = mrna.getUsualView().getPaint().getColor();  
		background.setAttribute("stroke", "none");
		background.setAttribute ("fill", SVGUtil.getHexColor(c));
		
		shp.add( background );
		
		cloneStripe.setAttribute("stroke", "none");
		cloneStripe.setAttribute ("fill", "#C4C4C4" );
		
		shp.add( cloneStripe );
		  
	    rect.setAttribute ("fill", "none");
	    rect.setAttribute ("stroke", "black");
	    rect.setAttribute ("stroke-width", "1");
	    
	    shp.add(rect);
		
	    if ( this.mrna.isHypothetical() ) {
	    	rect.setAttribute ("style", "stroke-dasharray: 6, 3;");
	    }
		
		return shp;

	}

	protected Point2D getPointOnRect(Rectangle2D rect, double angle ) {
		/* Calcular punto de interseccion con el angulo dado */
		SVGShape frame;
		if ( SVGConfig.SBGNMode ) {
			frame = new SVGRectangle( -rect.getWidth()/2, -rect.getHeight()/2,
										rect.getWidth(), rect.getHeight() );
		} else {
			frame = getSlantedRect( -rect.getWidth()/2, -rect.getHeight()/2,
										rect.getWidth(), rect.getHeight() );			
		}
		// Punto externo al rectangulo
		Point2D pExt= new Point2D.Double( 100000 * Math.cos( angle ) , 100000 * Math.sin( angle ) );
		Segment s = new Segment( pExt.getX(), pExt.getY() , 0, 0 );
		Point2D pointAtAngle = frame.intersection( s );
		Point2D p = new Point2D.Double( rect.getCenterX() + pointAtAngle.getX(),
				                rect.getCenterY() + pointAtAngle.getY() );
		
		return p;
	}
}
