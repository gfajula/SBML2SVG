package svgview.species;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.TreeMap;

import model.Model;
import model.specie.MSpecies;
import model.specie.MSpeciesComplex;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import svgcontroller.SBML2SVGException;
import svgview.SVGConfig;
import svgview.shapes.SVGComplexShape;
import svgview.shapes.SVGPolygon;
import svgview.shapes.SVGShape;
import svgview.shapes.SVGText;
import svgview.shapes.Segment;
import svgview.util.SVGTextRenderer;
import svgview.util.SVGUtil;

public class SVGComplex extends SVGSpecie {
	private boolean brief = false;
	private boolean noborder = false;
	private MSpeciesComplex mc;
	private Document svgDoc;
	private Point2D center;
	private TreeMap<String, SVGSpecie> children;
	private Model model; // referencia al modelo, para poder dibujar subcomponentes
	
	public SVGComplex(Document svgDoc , MSpeciesComplex mc, Model model) throws SBML2SVGException {
		super(svgDoc, mc);
		this.svgDoc = svgDoc;
		this.mc = mc;
		this.model = model;
		this.children = new TreeMap<String, SVGSpecie>();
		this.bounds = mc.getBounds();
		this.brief= mc.getViewState().equalsIgnoreCase("brief");
		this.noborder = mc.getViewState().equalsIgnoreCase("complexnoborder");
		
		//this.shape = buildSVGShape();

	}
	
	@Override
	public Document getDocument() {
		return svgDoc;
	}
	
	
	public Point2D getLinkAnchor(int type) {
		Point2D p = null;
		
	    int h = mc.getHomodimer();	    
	    double heightEach = mc.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = mc.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
	    
		switch (type) {
		case SVGSpecie.ANCHOR_N:
			p = new Point2D.Double(mc.getBounds().getX() + widthEach/2,
								   mc.getBounds().getY() );
			break;
		case SVGSpecie.ANCHOR_NNE:	
			p = new Point2D.Double(mc.getBounds().getX() + 3*widthEach/4 ,
                    			   mc.getBounds().getY() );
			break;			
		case SVGSpecie.ANCHOR_NE:
			p = new Point2D.Double(mc.getBounds().getX() + widthEach - 2.5,
                    			   mc.getBounds().getY() + 2.5  ) ;
			break;
		case SVGSpecie.ANCHOR_ENE:
			p = new Point2D.Double(mc.getBounds().getX() + widthEach ,
								   mc.getBounds().getY() + heightEach/4 );
			break;				
		case SVGSpecie.ANCHOR_E:
			p = new Point2D.Double(mc.getBounds().getX() + widthEach ,
		                           mc.getBounds().getY() + heightEach/2 );
			break;
		case SVGSpecie.ANCHOR_ESE:
			p = new Point2D.Double(mc.getBounds().getX() + widthEach ,
					   			   mc.getBounds().getY() + 3*heightEach/4 );
			break;				
		case SVGSpecie.ANCHOR_SE:
			p = new Point2D.Double(mc.getBounds().getX() + widthEach - 2.5 ,
					   			   mc.getBounds().getY() + heightEach - 2.5 );
			break;				
		case SVGSpecie.ANCHOR_SSE:
			p = new Point2D.Double(mc.getBounds().getX() + 3*widthEach/4 ,
					   			   mc.getBounds().getY() + heightEach );
			break;				
		case SVGSpecie.ANCHOR_S:
			p = new Point2D.Double(mc.getBounds().getX() + widthEach/2,
					               mc.getBounds().getY() + heightEach );
			break;
		case SVGSpecie.ANCHOR_SSW:
			p = new Point2D.Double(mc.getBounds().getX() + widthEach/4 ,
					   			   mc.getBounds().getY() + heightEach );
			break;				
		case SVGSpecie.ANCHOR_SW:
			p = new Point2D.Double(mc.getBounds().getX() + 2.5 ,
								   mc.getBounds().getY() + heightEach - 2.5 );
			break;				
		case SVGSpecie.ANCHOR_WSW:
			p = new Point2D.Double(mc.getBounds().getX() ,
	   				   			   mc.getBounds().getY() + 3*heightEach/4 );
			break;			
		case SVGSpecie.ANCHOR_W:
			p = new Point2D.Double(mc.getBounds().getX() ,
                    			   mc.getBounds().getY() + heightEach/2 );
			break;
		case SVGSpecie.ANCHOR_WNW:
			p = new Point2D.Double(mc.getBounds().getX() ,
     			   				   mc.getBounds().getY() + heightEach/4 );
			break;			
		case SVGSpecie.ANCHOR_NW:
			p = new Point2D.Double(mc.getBounds().getX() + 2.5 ,
								   mc.getBounds().getY() + 2.5 );
			break;			
		case SVGSpecie.ANCHOR_NNW:
			p = new Point2D.Double(mc.getBounds().getX() + widthEach/4 ,
	   				   			   mc.getBounds().getY() );
			break;					
		}
		
		return p;
	}	
	

//	private Element getDashedPolygon(double x, double y, double width, double height) {
//		Element svgPolygon = svgDoc.createElementNS(svgNS, "polygon");    
////	    svgPolygon.setAttributeNS (null, "fill", SVGUtil.getHexColor(c));
//	    svgPolygon.setAttributeNS (null, "style", "fill:none; stroke-linejoin:miter; stroke-dasharray:2,4,2,4;");
//	    svgPolygon.setAttributeNS (null, "stroke", "black");
//	    svgPolygon.setAttributeNS (null, "stroke-width", "1");
//	    svgPolygon.setAttributeNS (null, "stroke-linejoin", "round");
//	    svgPolygon.setAttributeNS (null, "points" ,
//	    		Double.toString(x) + ", " + Double.toString(y+1) + " " +
//	    		Double.toString(x+1) + ", " + Double.toString(y) + " " +
//	    		Double.toString(x+width-1) + ", " + Double.toString(y) + " " +
//	    		Double.toString(x+width) + ", " + Double.toString(y+1) + " " +
//	    		Double.toString(x+width) + ", " + Double.toString(y+height-1) + " " +
//	    		Double.toString(x+width-1) + ", " + Double.toString(y+height) + " " +
//	    		Double.toString(x+1) + ", " + Double.toString(y+height) + " " +
//	    		Double.toString(x) + ", " + Double.toString(y+height-1)
//	    		);		
//		return svgPolygon;
//	}
	
//	private Element getPolygon(double x, double y, double width, double height) {
//		Element svgPolygon = svgDoc.createElementNS(svgNS, "polygon");
//	    Color c = mc.getUsualView().getPaint().getColor();    
//	    svgPolygon.setAttributeNS (null, "fill", SVGUtil.getHexColor(c));
////	    svgPolygon.setAttributeNS (null, "fill-opacity", "0.5");
//	    svgPolygon.setAttributeNS (null, "stroke", "black");
////	    svgPolygon.setAttributeNS (null, "stroke-width", "1");
//	    svgPolygon.setAttributeNS (null, "stroke-linejoin", "round");
//	    svgPolygon.setAttributeNS (null, "points" ,
//	    		Double.toString(x) + ", " + Double.toString(y+5) + " " +
//	    		Double.toString(x+5) + ", " + Double.toString(y) + " " +
//	    		Double.toString(x+width-5) + ", " + Double.toString(y) + " " +
//	    		Double.toString(x+width) + ", " + Double.toString(y+5) + " " +
//	    		Double.toString(x+width) + ", " + Double.toString(y+height-5) + " " +
//	    		Double.toString(x+width-5) + ", " + Double.toString(y+height) + " " +
//	    		Double.toString(x+5) + ", " + Double.toString(y+height) + " " +
//	    		Double.toString(x) + ", " + Double.toString(y+height-5)
//	    		);		
//		return svgPolygon;
//	}

	private SVGShape getPolygonWithClone(double x, double y, double width, double height) {
		SVGComplexShape shp = new SVGComplexShape();
		
		SVGShape background = getCutCornerRectangle(x, y, width, height, 5);
		Color c = mc.getUsualView().getPaint().getColor();
		background.setAttribute("fill", SVGUtil.getHexColor(c));
		background.setAttribute("stroke", "none");

	    shp.add( background );	
	    
	    SVGShape cloneMarker = getCloneMarker( x, y+ height*3/4, width, height/4 , 5);
	    cloneMarker.setAttribute("fill", "#C4C4C4" );
	    cloneMarker.setAttribute("stroke", "none");
	    
	    shp.add( cloneMarker );
	    
	    SVGShape frame = getCutCornerRectangle(x, y, width, height, 5);
	    frame.setAttribute("fill", "none" );
	    frame.setAttribute("stroke", "#000000");
	    frame.setAttribute("stroke-linejoin", "round");
	    
	    shp.add( frame );
	    
	    return shp;
	}
	
	private SVGShape getPolygon(double x, double y, double width, double height) {
		SVGShape shp = getCutCornerRectangle(x, y, width, height, 5); 
		
	    Color c = mc.getUsualView().getPaint().getColor();    
	    shp.setAttribute("fill", SVGUtil.getHexColor(c));
	    shp.setAttribute("stroke", "black");
	    shp.setAttribute("stroke-linejoin", "round");
	    if ( this.mc.isHypothetical() ) {
	    	shp.setAttribute ("style", "stroke-dasharray: 6, 3;");
	    }
		return shp;
	}

	private SVGShape getDashedPolygon(double x, double y, double width, double height) {
		SVGShape shp = getCutCornerRectangle(x, y, width, height, 1);				
//	    Color c = mc.getUsualView().getPaint().getColor();    
	    shp.setAttribute("style", "fill:none; stroke-linejoin:miter; stroke-dasharray:2,4,2,4;");
	    shp.setAttribute("stroke", "black");
	    shp.setAttribute("stroke-width", "1");
	    shp.setAttribute("stroke-linejoin", "round");
	    				
		return shp;
	}

	private SVGShape getCutCornerRectangle(double x, double y, 
											double width, double height,
											double cutCorner ) {
		SVGShape shp = new SVGPolygon(Double.toString(x) + ", " + Double.toString(y+cutCorner) + " " +
						    		Double.toString(x+cutCorner) + ", " + Double.toString(y) + " " +
						    		Double.toString(x+width-cutCorner) + ", " + Double.toString(y) + " " +
						    		Double.toString(x+width) + ", " + Double.toString(y+cutCorner) + " " +
						    		Double.toString(x+width) + ", " + Double.toString(y+height-cutCorner) + " " +
						    		Double.toString(x+width-cutCorner) + ", " + Double.toString(y+height) + " " +
						    		Double.toString(x+cutCorner) + ", " + Double.toString(y+height) + " " +
						    		Double.toString(x) + ", " + Double.toString(y+height-cutCorner)
						    	);
		return shp;
	}

	/**
	 * Obtiene un marcador de clone (para dibujo SBGN) con la forma inferior
	 * del rectángulo de esquinas recortadas.
	 * Las coordenadas que debe recibir son las del rectángulo que lo envuelve 
	 * 
	 * @param x		
	 * @param y
	 * @param width
	 * @param height
	 * @param cutCorner La distancia en pixels a las que se recortan las esquinas.
	 * 
	 * @return
	 */
	private SVGShape getCloneMarker(double x, double y, 
									 double width, double height,
									 double cutCorner ) {
		SVGShape shp = new SVGPolygon( 
									Double.toString(x) + ", " + Double.toString(y ) + " " +	
									Double.toString( x+width ) + ", " + Double.toString(y) + " " +
									Double.toString(x+width) + ", " + Double.toString(y+height-cutCorner) + " " +
									Double.toString(x+width-cutCorner) + ", " + Double.toString(y+height) + " " +
									Double.toString(x+cutCorner) + ", " + Double.toString(y+height) + " " +
									Double.toString(x) + ", " + Double.toString(y+height-cutCorner)
		);
		return shp;
	}

	
	private SVGShape getActivityPolygon(double x, double y, double width, double height) {
		
		SVGShape shp = getCutCornerRectangle(x, y, width, height, 8);
						
//	    Color c = mc.getUsualView().getPaint().getColor();    
	    shp.setAttribute("style", "fill:none; stroke-linejoin:miter; stroke-dasharray:4,2,4,2;");
	    shp.setAttribute("stroke", "black");
	    shp.setAttribute("stroke-width", "1");
	    shp.setAttribute("stroke-linejoin", "round");
	    				
		return shp;
	}
	
//	public SVGShape getSVGShape() {
//		return this.shape;
//	}
	public SVGShape buildSVGShapeSBGN() throws SBML2SVGException {
		return buildSVGShapeSBGN( true );
	}
	
	public SVGShape buildSVGShapeSBGN( boolean showCloneMarker ) throws SBML2SVGException {
		SVGComplexShape complex = new SVGComplexShape(); 
		complex.setAttribute("id", mc.getIdAlias());
		complex.setAttribute("style", "stroke-width:2; stroke-linecap:butt; stroke-linejoin:round;");
    	
	    int h = mc.getHomodimer();	    
		if (SVGConfig.SBGNMode) {
			h = Math.min( mc.getHomodimer(), 2 ); 
		}
		
	    double heightEach = mc.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = mc.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
		
	    this.center = new Point2D.Double( mc.getBounds().getX() + widthEach/2,
	    								  mc.getBounds().getY() + heightEach/2);
	    
		SVGShape shp;
		
		// Pintar actividad
		
		if ( !noborder && this.mc.getActivity().equals("active")) {
			for (int hc = h-1 ; hc>=0 ; hc--) {
				shp = getActivityPolygon(mc.getBounds().getX() + HOMODIMER_WIDTH*hc - 4, 
						mc.getBounds().getY() + HOMODIMER_HEIGHT*hc - 4,
						widthEach + 8,
						heightEach + 8
					   );
				complex.add(shp);
			}
		}
		
		if (!noborder) { 
		    for (int hc = h-1 ; hc>=0 ; hc--) {
		    	if ( showCloneMarker && this.isClone() ) {
			    	shp = getPolygonWithClone(mc.getBounds().getX() + HOMODIMER_WIDTH*hc , 
							  mc.getBounds().getY() + HOMODIMER_HEIGHT*hc ,
							  widthEach,
							  heightEach
				   			 );
		    		
		    	} else {
			    	shp = getPolygon(mc.getBounds().getX() + HOMODIMER_WIDTH*hc , 
									  mc.getBounds().getY() + HOMODIMER_HEIGHT*hc ,
									  widthEach,
									  heightEach
						   			 );
		    	}
		    	complex.add(shp);
		    	
		    	if (this.brief) {
		    		shp = getDashedPolygon(mc.getBounds().getX() + HOMODIMER_WIDTH*hc + 4, 
											mc.getBounds().getY() + HOMODIMER_HEIGHT*hc + 4,
											widthEach - 8,
											heightEach - 8
										   );
		    		complex.add(shp);
		    	}
		    }		

		    Element text;
		    if (brief) {
			    text = SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
			    		mc.getBounds().getX() + 
			    		widthEach / 2 , 
			    		mc.getBounds().getY() + 
			    		heightEach / 2 , 
			    		mc.getName(),
			    		12);    
		    } else {
		    	text = SVGTextRenderer.getInstance().drawTextAboveCentered(svgDoc, 
			    		mc.getBounds().getX() + 
			    		widthEach / 2 , 
			    		mc.getBounds().getY() + 
			    		heightEach - 4, 
			    		mc.getName(), 
			    		12);
		    }
		    
		    if ( text!=null ) complex.add( new SVGText(text));
		}
		
	    
	    
	    // Crear y pintar hijos del complex
	    if (!this.brief) {
	    	if (mc.getSubspecies() != null) {	    	
			    for ( MSpecies ms : mc.getSubspecies() ) {
			    	SVGSpecie svgSpc = SVGSpecieFactory.createSVGSpecie(this.getDocument(), ms, model);
			    	this.children.put(ms.getIdAlias(), svgSpc );
			    	complex.add( svgSpc.getSVGShape() );	    	
			    }
	    	} 
	    } else {
	    	// Si este complejo se pinta de forma abreviada, 
	    	// sus hijos obtienen la forma del complejo, aunque no tienen
	    	// que pintarse 
	    	if (mc.getSubspecies() != null) {	    	
			    for ( MSpecies ms : mc.getSubspecies() ) {
			    	SVGSpecie svgSpc = new SVGHiddenSpecie( svgDoc, ms , this );
			    	this.children.put(ms.getIdAlias(), svgSpc );			    	
			    }
	    	}
	    }
		    
	    complex.add( getInfoUnit(
	    		new Rectangle2D.Double( mc.getBounds().getX() , 
	    								mc.getBounds().getY(), 
	    								widthEach, 
	    								heightEach )
	    		) );
	    
	    this.shape = complex;
	    
		return complex;
	}
	
	@Override
	public Rectangle2D getBBox() {
		return this.bounds;
	}

	@Override
	public Point2D getCenter() {
		return center;
	}

	public TreeMap<String, SVGSpecie> getChildren() {
		return children;
	}

	@Override
	protected SVGShape buildSVGShape() throws SBML2SVGException {
		return buildSVGShapeSBGN( false );
	}

	@Override
	protected Point2D getPointOnRect(Rectangle2D rect, double angle) {
		/* Calcular punto de interseccion con el angulo dado */
		SVGShape frame = getCutCornerRectangle(-rect.getWidth()/2, -rect.getHeight()/2,
												rect.getWidth(), rect.getHeight(), 
												5 );
		// Punto externo al rectangulo
		Point2D pExt= new Point2D.Double( 100000 * Math.cos( angle ) , 100000 * Math.sin( angle ) );
		Segment s = new Segment( pExt.getX(), pExt.getY() , 0, 0 );
		Point2D pointAtAngle = frame.intersection( s );
		Point2D p = new Point2D.Double( rect.getCenterX() + pointAtAngle.getX(),
				                rect.getCenterY() + pointAtAngle.getY() );
		
		return p;
	}

}
