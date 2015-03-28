package svgview.species;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import model.specie.MPhenotype;

import org.w3c.dom.Document;

import svgview.SVGConfig;
import svgview.shapes.SVGComplexShape;
import svgview.shapes.SVGPolygon;
import svgview.shapes.SVGShape;
import svgview.shapes.SVGText;
import svgview.shapes.Segment;
import svgview.util.SVGTextRenderer;
import svgview.util.SVGUtil;


/**
 * Clase que se encarga de dibujar el glifo de un 'Phenotype'
 * 
 * @author Guillermo Fajula Leal
 *
 */
public class SVGPhenotype extends SVGSpecie {
	public static final int HOMODIMER_WIDTH = 6;
	public static final int HOMODIMER_HEIGHT = 6;
	private MPhenotype mp;
	private Document svgDoc;	
    protected Point2D.Double center;
	protected SVGShape shape;
	
	/**
	 * Constructor por defecto.
	 * 
	 * @param svgDoc
	 * @param mp
	 */
	public SVGPhenotype(Document svgDoc, MPhenotype mp) {
		super(svgDoc, mp);
		this.svgDoc = svgDoc;
		this.mp = mp;
		this.mp.getActivity();
		this.bounds = mp.getBounds();
		this.center = new Point2D.Double();
	}
	
	@Override
	public Document getDocument() {  
		return this.svgDoc;
		
	}
	
	
	/**
	 * Construye la forma específica de un 'phenotype'. 
	 * Un hexagono elongado, con borde discontinuo si es hipotetico, continuo en otro 
	 * caso.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	private SVGShape getPhenotypePolygon(double x, double y, double width, double height) {
		SVGShape shp = getHexagon(x, y, width, height);
		
	    Color c = mp.getUsualView().getPaint().getColor();    
	    shp.setAttribute("fill", SVGUtil.getHexColor(c));
	    shp.setAttribute("stroke", "black");
	    shp.setAttribute("stroke-width", "1");
	    shp.setAttribute("stroke-linejoin", "round");		
		
	    if ( this.mp.isHypothetical() ) {
	    	shp.setAttribute ("style", "stroke-dasharray: 6, 3;");
	    }
		
		return shp;
	}

	/**
	 * Construye el hexágono elongado de un 'phenotype', sin estilos añadidos.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	private SVGShape getHexagon(double x, double y, double width, double height) {
		SVGShape shp = new SVGPolygon(
				Double.toString(x+height/2) + ", " + Double.toString(y) + " " +
	    		Double.toString(x+width-height/2) + ", " + Double.toString(y) + " " +
	    		Double.toString(x+width) + ", " + Double.toString(y+height/2) + " " +
	    		Double.toString(x+width-height/2) + ", " + Double.toString(y+height) + " " +
	    		Double.toString(x+height/2) + ", " + Double.toString(y+height) + " " +
	    		Double.toString(x) + ", " + Double.toString(y+height/2) );
		return shp;
	}
	
	/**
	 * Construye la forma SVG que define el 'phenotype'
	 */
	@Override
	public SVGShape buildSVGShape() {
		SVGComplexShape complex = new SVGComplexShape();
		complex.setAttribute("id", mp.getIdAlias());
		
		int h = mp.getHomodimer();	
		if (SVGConfig.SBGNMode) {
			h = Math.min( mp.getHomodimer(), 2 ); 
		}
	    double heightEach = mp.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = mp.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
	    
	    this.center.x = mp.getBounds().getX() + widthEach/2;
	    this.center.y = mp.getBounds().getY() + heightEach/2;
	    
	    SVGShape shp;
	    for (int hc = h-1 ; hc>=0 ; hc--) {
	    	shp = getPhenotypePolygon(mp.getBounds().getX() + HOMODIMER_WIDTH*hc , 
	    									mp.getBounds().getY() + HOMODIMER_HEIGHT*hc ,
	    									widthEach,
	    									heightEach
	    								   );
	    	complex.add(shp);
	    }
	    
		complex.add( new SVGText(
				SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
		    		mp.getBounds().getX() + 
		    		widthEach / 2 , 
		    		mp.getBounds().getY() + 
		    		heightEach / 2 , 
		    		mp.getName() ,
		    		12 ))
		);
    	
	    this.shape = complex;
		return complex;
	}


	@Override
	public Point2D getCenter() {
		return center;
	}
	
	/**
	 * Obtiene el punto de enganche al hexágono que corresponde al punto cardinal dado.
	 * (ver constantes ANCHOR_ en SVGSpecies)
	 */
	public Point2D getLinkAnchor(int type) {
		Point2D p = null;
		
	    int h = mp.getHomodimer();	    
	    double heightEach = mp.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = mp.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
	    
	    if ( widthEach <= heightEach ) 
	    	return new Point2D.Double( mp.getBounds().getCenterX(), mp.getBounds().getCenterY() );	    
	    
		switch (type) {
		case SVGSpecie.ANCHOR_N:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach/2,
								   mp.getBounds().getY() );
			break;
		case SVGSpecie.ANCHOR_NNE:	
			p = new Point2D.Double(mp.getBounds().getX() + heightEach/2 + 3*(widthEach-heightEach)/4 ,
                    			   mp.getBounds().getY() );
			break;			
		case SVGSpecie.ANCHOR_NE:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach - heightEach/2 ,
                    			   mp.getBounds().getY() );
			break;
		case SVGSpecie.ANCHOR_ENE:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach - heightEach/4,
								   mp.getBounds().getY() + heightEach/4 );
			break;				
		case SVGSpecie.ANCHOR_E:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach ,
		                           mp.getBounds().getY() + heightEach/2 );
			break;
		case SVGSpecie.ANCHOR_ESE:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach - heightEach/4,
					   			   mp.getBounds().getY() + 3*heightEach/4 );
			break;				
		case SVGSpecie.ANCHOR_SE:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach - heightEach/2 ,
					   			   mp.getBounds().getY() + heightEach );
			break;				
		case SVGSpecie.ANCHOR_SSE:
			p = new Point2D.Double(mp.getBounds().getX() + heightEach/2 + 3*(widthEach-heightEach)/4 ,
					   			   mp.getBounds().getY() + heightEach );
			break;				
		case SVGSpecie.ANCHOR_S:
			p = new Point2D.Double(mp.getBounds().getX() + widthEach/2,
					               mp.getBounds().getY() + heightEach );
			break;
		case SVGSpecie.ANCHOR_SSW:
			p = new Point2D.Double(mp.getBounds().getX() + heightEach/2 + (widthEach-heightEach)/4 ,
					   			   mp.getBounds().getY() + heightEach );
			break;				
		case SVGSpecie.ANCHOR_SW:
			p = new Point2D.Double(mp.getBounds().getX() + heightEach/2 ,
								   mp.getBounds().getY() + heightEach );
			break;				
		case SVGSpecie.ANCHOR_WSW:
			p = new Point2D.Double(mp.getBounds().getX() + heightEach/4,
	   				   			   mp.getBounds().getY() + 3*heightEach/4 );
			break;			
		case SVGSpecie.ANCHOR_W:
			p = new Point2D.Double(mp.getBounds().getX() ,
                    			   mp.getBounds().getY() + heightEach/2 );
			break;
		case SVGSpecie.ANCHOR_WNW:
			p = new Point2D.Double(mp.getBounds().getX() + heightEach/4,
     			   				   mp.getBounds().getY() + heightEach/4 );
			break;			
		case SVGSpecie.ANCHOR_NW:
			p = new Point2D.Double(mp.getBounds().getX() + heightEach/2 ,
								   mp.getBounds().getY() );
			break;			
		case SVGSpecie.ANCHOR_NNW:
			p = new Point2D.Double(mp.getBounds().getX() + heightEach/2 + (widthEach-heightEach)/4  ,
	   				   			   mp.getBounds().getY() );
			break;					
		}
		
		return p;
	}
 
	/**
	 * La forma SBGN es idéntica a la de Kitano
	 */
	@Override
	protected SVGShape buildSVGShapeSBGN() {
		return buildSVGShape();
	}

	/**
	 * Obtiene el punto que cruza el hexágono, partiendo del centro y con el angulo 
	 * indicado.
	 */
	@Override
	protected Point2D getPointOnRect(Rectangle2D rect, double angle) {		
		/* Calcular punto de interseccion con el angulo dado */
		SVGShape frame = getHexagon( rect.getX(), rect.getY(),
									 rect.getWidth(), rect.getHeight() );
		// Punto externo al rectangulo
		Point2D pExt= new Point2D.Double( 100000 * Math.cos( angle ) , 100000 * Math.sin( angle ) );
		Segment s = new Segment( pExt.getX(), pExt.getY() , 0, 0 );
		Point2D pointAtAngle = frame.intersection( s );
		Point2D p = new Point2D.Double( rect.getCenterX() + pointAtAngle.getX(),
				                rect.getCenterY() + pointAtAngle.getY() );
		return p;
	}

	
}
