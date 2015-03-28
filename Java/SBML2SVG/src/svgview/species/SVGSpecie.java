package svgview.species;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import model.specie.MSpecies;
import model.specie.MTag;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import svgcontroller.SBML2SVGException;
import svgview.SVGConfig;
import svgview.SVGPaintable;
import svgview.shapes.SVGComplexShape;
import svgview.shapes.SVGLine;
import svgview.shapes.SVGPolygon;
import svgview.shapes.SVGRectangle;
import svgview.shapes.SVGShape;
import svgview.shapes.SVGText;
import svgview.shapes.Segment;
import svgview.util.SVGTextRenderer;
import svgview.util.SVGUtil;


public abstract class SVGSpecie implements SVGPaintable   {
	public static final int ANCHOR_N = 0;
	public static final int ANCHOR_NNE = 1;
	public static final int ANCHOR_NE = 2;
	public static final int ANCHOR_ENE = 3;
	public static final int ANCHOR_E = 4;
	public static final int ANCHOR_ESE = 5;
	public static final int ANCHOR_SE = 6;
	public static final int ANCHOR_SSE = 7;
	public static final int ANCHOR_S = 8;
	public static final int ANCHOR_SSW = 9;
	public static final int ANCHOR_SW = 10;
	public static final int ANCHOR_WSW = 11;
	public static final int ANCHOR_W = 12;
	public static final int ANCHOR_WNW = 13;
	public static final int ANCHOR_NW = 14;	
	public static final int ANCHOR_NNW = 15;
	
	
	
	public static final int HOMODIMER_WIDTH = 6;
	public static final int HOMODIMER_HEIGHT = 6;
	private Document svgDoc;
	private MSpecies mspecies;
	private boolean clone;
	protected SVGShape shape;
	protected Rectangle2D bounds;
	protected double widthEach;
	protected double heightEach;
	
	public SVGSpecie(Document svgDoc, MSpecies mspecies) {
		this.svgDoc = svgDoc;
		this.mspecies = mspecies;
	}
	
	public SVGShape getSVGShape() throws SBML2SVGException {
		
		// Lazy creation. Para evitar sobrecarga inútil al instanciar subclases
		if (this.shape == null) {
			if (SVGConfig.SBGNMode) {
				return this.buildSVGShapeSBGN();
			} else {
				return this.buildSVGShape();
			}
		} else {
			return shape;
		}
		
	}
	
	protected abstract SVGShape buildSVGShape() throws SBML2SVGException; 

	protected abstract SVGShape buildSVGShapeSBGN() throws SBML2SVGException;


	/**
	 * Obtiene el IdAlias del elemento del modelo.
	 * 
	 * @return IdAlias
	 */
	public String getIdAlias() {
		return this.getMspecies().getIdAlias();
	}
	
	protected SVGShape getTagsShape() throws SBML2SVGException{
		SVGComplexShape cpx = new SVGComplexShape();
		// Pintar Tags
		if ( this.mspecies.getTags() != null) {
			for ( MTag t : mspecies.getTags() ) {
				SVGShape shp = null;
				
				if ( t.getDirection() == MTag.RIGHT ) {
				    shp = new SVGPolygon (
			    		"" + t.getBounds().getMinX() + " " + t.getBounds().getMinY() + " " +	    		
			    		     (t.getBounds().getMaxX() - t.getBounds().getWidth()/3) + " " + t.getBounds().getMinY() + " " +	    		
			    		     t.getBounds().getMaxX() + " " + t.getBounds().getCenterY() + " " +
			    		     (t.getBounds().getMaxX() - t.getBounds().getWidth()/3) + " " + t.getBounds().getMaxY() + " " +
			    		     t.getBounds().getMinX() + " " + t.getBounds().getMaxY() + " " 			    
					);
				} else if ( t.getDirection() == MTag.LEFT ) {
				    shp = new SVGPolygon (
				    		"" + t.getBounds().getMinX() + " " + t.getBounds().getCenterY() + " " +	    		
				    		     (t.getBounds().getMinX() + t.getBounds().getWidth()/3)  + " " + t.getBounds().getMinY() + " " +
				    		     t.getBounds().getMaxX() + " " + t.getBounds().getMinY() + " " +	    		
				    		     t.getBounds().getMaxX() + " " + t.getBounds().getMaxY() + " " +
				    		     (t.getBounds().getMinX() + t.getBounds().getWidth()/3) + " " + t.getBounds().getMaxY() + " " 				    		   			    
					);					
				} else if ( t.getDirection() == MTag.DOWN ) {
				    shp = new SVGPolygon (
				    		"" + t.getBounds().getMinX() + " " + t.getBounds().getMinY() + " " +	    		
				    		     t.getBounds().getMaxX() + " " + t.getBounds().getMinY() + " " +
				    		     t.getBounds().getMaxX() + " " + (t.getBounds().getMaxY() - t.getBounds().getHeight()/3)  + " " +	    		
				    		     t.getBounds().getCenterX() + " " + t.getBounds().getMaxY() + " " +
				    		     t.getBounds().getMinX() + " " + (t.getBounds().getMaxY() - t.getBounds().getHeight()/3)   + " " 				    		   			    
					);					
				} else if ( t.getDirection() == MTag.UP ){
				    shp = new SVGPolygon (
				    		"" + t.getBounds().getCenterX() + " " + t.getBounds().getMinY() + " " +	    		
				    		     t.getBounds().getMaxX() + " " + (t.getBounds().getMinY() + t.getBounds().getHeight()/3) + " " +	    		
				    		     t.getBounds().getMaxX() + " " + t.getBounds().getMaxY() + " " +
				    		     t.getBounds().getMinX() + " " + t.getBounds().getMaxY() + " " +
				    		     t.getBounds().getMinX() + " " + (t.getBounds().getMinY() + t.getBounds().getHeight()/3) + " " 			    
						);					
				}
				
			    Color c = t.getFramePaint().getColor();    
			    shp.setAttribute("fill", SVGUtil.getHexColor(c));
	//		    rect.setAttributeNS (null, "fill-opacity", "0.5");
			    shp.setAttribute("stroke", "black");
			    shp.setAttribute("stroke-width", "" + t.getLineWidth() );
			    
			    cpx.add(shp);
			    
		    	SVGText text = new SVGText( SVGTextRenderer.getInstance().drawTextBelow(svgDoc, 
			    		t.getBounds().getMinX() + 4, 
			    		t.getBounds().getMinY() + 4,
			    		t.getName(), 
			    		11) );
			    
		    	cpx.add( text );
			    
			    
		    	// Dibujar línea entre Tag y specie
		    	
		    	// Calcular punto de conexión
		    	Segment s;
		    	Point2D p;
		    	SVGLine ln;
		    	
		    	switch( t.getDirection() ) {
		    	case MTag.UP : 
//		    		s = new Segment( mspecies.getBounds().getCenterX(),
//	   						 mspecies.getBounds().getMaxY() + 10,
//	   						 mspecies.getBounds().getCenterX(),
//	   						 mspecies.getBounds().getCenterY() );
				   		p = this.getLinkAnchor(ANCHOR_S);
				   		
				   		ln = new SVGLine(p.getX(), p.getY(), t.getBounds().getCenterX(), t.getBounds().getMinY() );
				   		ln.setAttribute("stroke-width", "1");
				   		ln.setAttribute("stroke", "black");
				   	
				   		cpx.add( ln );	
				   		break;
		    	case MTag.DOWN : 
//		    		s = new Segment( mspecies.getBounds().getCenterX(),
//	   						 mspecies.getBounds().getMinY() - 10,
//	   						 mspecies.getBounds().getCenterX(),
//	   						 mspecies.getBounds().getCenterY() );
				   		p = this.getLinkAnchor(ANCHOR_N);
				   		
				   		ln = new SVGLine(p.getX(), p.getY(), t.getBounds().getCenterX(), t.getBounds().getMaxY() );
				   		ln.setAttribute("stroke-width", "1");
				   		ln.setAttribute("stroke", "black");
				   	
				   		cpx.add( ln );	
				   		break;
		    	case MTag.LEFT : 
		    		s = new Segment( mspecies.getBounds().getMaxX() + 10,
   						 mspecies.getBounds().getCenterY(),
   						 mspecies.getBounds().getCenterX(),
   						 mspecies.getBounds().getCenterY() );
			   		p = this.getSVGShape().intersection(s);
			   		
			   		ln = new SVGLine(p.getX(), p.getY(), t.getBounds().getMinX(), t.getBounds().getCenterY() );
			   		ln.setAttribute("stroke-width", "1");
			   		ln.setAttribute("stroke", "black");
			   	
			   		cpx.add( ln );	
			   		break;
		    	case MTag.RIGHT : 		    		
		    	default :
		    		s = new Segment( mspecies.getBounds().getMinX() - 10,
		    						 mspecies.getBounds().getCenterY(),
		    						 mspecies.getBounds().getCenterX(),
		    						 mspecies.getBounds().getCenterY() );
		    		p = this.getSVGShape().intersection(s);
		    		
		    		ln = new SVGLine(p.getX(), p.getY(), t.getBounds().getMaxX(), t.getBounds().getCenterY() );
		    		ln.setAttribute("stroke-width", "1");
		    		ln.setAttribute("stroke", "black");
		    	
		    		cpx.add( ln );
		    		
		    	}
		    	
			}    
		}
		
		return cpx;
	}
	
	
	public void svgPaint(Element docParent) throws SBML2SVGException {
	    getSVGShape().svgPaint(docParent);
	    getTagsShape().svgPaint(docParent);
	}	
	
	protected SVGShape getAnchorsShape() {
		SVGShape cpx = new SVGComplexShape();
		for (int i=0;i<16;i++) {
			Point2D p = getLinkAnchor( i );
			cpx.composeWith( new SVGRectangle(p.getX()-2, p.getY()-2, 4, 4, "fill:red") );
		}
		
		return cpx;
	}
	
	public Rectangle2D getBBox() {
		return this.bounds;
	}
	public abstract Point2D getCenter();
	public abstract Document getDocument();
	public abstract Point2D getLinkAnchor(int type);
	
	public Point2D getLinkAnchor(String type) {
		if ( type == null) return null;
		if (type.equals("N")) return getLinkAnchor(ANCHOR_N);
		else if(type.equals("NNE")) return getLinkAnchor(ANCHOR_NNE);
		else if(type.equals("NE")) return getLinkAnchor(ANCHOR_NE);
		else if(type.equals("ENE")) return getLinkAnchor(ANCHOR_ENE);
		else if(type.equals("E")) return getLinkAnchor(ANCHOR_E);
		else if(type.equals("ESE")) return getLinkAnchor(ANCHOR_ESE);
		else if(type.equals("SE")) return getLinkAnchor(ANCHOR_SE);
		else if(type.equals("SSE")) return getLinkAnchor(ANCHOR_SSE);
		else if(type.equals("S")) return getLinkAnchor(ANCHOR_S);
		else if(type.equals("SSW")) return getLinkAnchor(ANCHOR_SSW);
		else if(type.equals("SW")) return getLinkAnchor(ANCHOR_SW);
		else if(type.equals("WSW")) return getLinkAnchor(ANCHOR_WSW);
		else if(type.equals("W")) return getLinkAnchor(ANCHOR_W);
		else if(type.equals("WNW")) return getLinkAnchor(ANCHOR_WNW);
		else if(type.equals("NW")) return getLinkAnchor(ANCHOR_NW);	
		else if(type.equals("NNW")) return getLinkAnchor(ANCHOR_NNW);
		else return null;
	}

	public Point2D.Double intersection(Segment l) throws SBML2SVGException {
		if ( this.getBBox().contains( l.getP1() ) && 
				this.getBBox().contains( l.getP2() ) ) {
			return null;
		} else {
			return this.getSVGShape().intersection(l);
		}
	}

	public boolean isClone() {
		return clone && !mspecies.isIncluded();
	}

	public void setClone(boolean clone) {
		this.clone = clone;
	}
	
	protected void setShape( SVGShape shape ) {
		this.shape = shape;
	}

	public MSpecies getMspecies() {
		return mspecies;
	}
	
	/**
	 * Metodo generico para obtener el reactangulo con un 'Information Unit'
	 * 
	 * @param rect, el Bounding Box de la forma sobre la que hay que calcular
	 * 				la posicion del cuadrado
	 * @return
	 */
	protected SVGShape getInfoUnit( Rectangle2D rect ) {
		if ( this.getMspecies().getInfo() == null ) {
			return null;
		}
		Point2D p = getPointOnRect( rect, this.getMspecies().getInfo().getAngle() );
		return getInfoUnit(p);
	}

	/**
	 * 
	 * @param rect, el Bounding Box de la forma sobre la que hay que calcular
	 * 				la posicion del cuadrado
	 * @param angle Ángulo (definido en el MSpecies) sobre el dibujo de 
	 * 				 esta Species 
	 * 				 
	 * @return	punto sobre el borde de la figura, con el angulo dado
	 */
	protected abstract Point2D getPointOnRect(Rectangle2D rect, double angle);

	/**
	 * Dibuja la etiqueta de un Information Unit, centrada en el punto dado 
	 * 
	 * @param p Punto central sobre el que colocar la etiqueta
	 * 
	 * @return
	 */
	protected SVGShape getInfoUnit(Point2D p) {
		SVGComplexShape shp = null;
		if ( this.getMspecies().getInfo() != null &&
		     !this.getMspecies().getInfo().getState().equals("empty")	 ) {
			
			shp = new SVGComplexShape();
								
			double height = 16;
			double width = 70;
			SVGRectangle infoTag = new SVGRectangle( p.getX() - width/2,
					                                 p.getY() - height/2,
													 width, height );
			infoTag.setAttribute("fill", "white");
			infoTag.setAttribute("stroke", "black");
			infoTag.setAttribute("stroke-width", "1");
			shp.add( infoTag );			
			
			shp.add( new SVGText(
    				SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
    						-2 +
    						infoTag.getCenterX() ,
    						infoTag.getCenterY() , 
    						this.getMspecies().getInfo().toString(),
				    		12 ))
    		);
		}
		
		return shp;
	}
	
	protected SVGShape getLabel() {
		return new SVGText(
	    				SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
				    		this.getCenter().getX() , 
				    		this.getCenter().getY() , 
				    		this.getMspecies().getName() ,
				    		12 )
	    		);
	}
	
}
