package svgview.species;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import model.specie.protein.MBindingRegion;
import model.specie.protein.MProtein;
import model.specie.protein.MResidue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import svgcontroller.SBML2SVGException;
import svgview.SVGConfig;
import svgview.shapes.SVGCircle;
import svgview.shapes.SVGComplexShape;
import svgview.shapes.SVGEllipse;
import svgview.shapes.SVGRectangle;
import svgview.shapes.SVGSemiRoundRectangle;
import svgview.shapes.SVGShape;
import svgview.shapes.SVGText;
import svgview.shapes.Segment;
import svgview.util.SVGTextRenderer;
import svgview.util.SVGUtil;

public class SVGProtein extends SVGSpecie {
	public static final int HOMODIMER_WIDTH = 6;
	public static final int HOMODIMER_HEIGHT = 6;
	public static final int RESIDUE_RADIUS = 8;
	public static final int CORNER_RADIUS = 5;
	public static final double NEG_CORNER_RADIUS = 1 ; //CORNER_RADIUS * 0.414213 ;
	public static final int ACTIVITY_MARGIN = 4;
	public static final int STATE_MAX_RX = 25;
	protected boolean active = false;
	protected MProtein mp;
	protected Document svgDoc;
	protected Point2D.Double center;
	protected SVGShape shape;
	
	public SVGProtein(Document svgDoc , MProtein mp) {
		super( svgDoc, mp);
		this.svgDoc = svgDoc;
		this.mp = mp;
		this.bounds = mp.getBounds();
		this.center = new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
		this.active = mp.getActivity().equalsIgnoreCase("active");
		
		// Lazy creation.
		// shape = buildSVGShape();
	}
	
	@Override
	public Point2D getCenter() {
		return this.center;
	}



	protected SVGShape getDashedRect(double x, double y, double width, double height) {		
	    SVGShape rect = new SVGRectangle(x, y, width, height,
							    		CORNER_RADIUS + ACTIVITY_MARGIN, 
							    		CORNER_RADIUS + ACTIVITY_MARGIN );
	    rect.setAttribute ("style", "stroke-dasharray: 4, 2;");
	    rect.setAttribute ("fill", "none");
	    rect.setAttribute ("stroke", "black");
	    rect.setAttribute ("stroke-width", "1");	    		
		return rect;
	}

	

	
	protected SVGShape getDashedFill(double x, double y, double width, double height) {		
		SVGShape rect = new SVGRectangle(x+1, y+1, width-2, height-2, 
										CORNER_RADIUS + ACTIVITY_MARGIN - 1,
										CORNER_RADIUS + ACTIVITY_MARGIN - 1);
	    rect.setAttribute("fill", "white");
	    rect.setAttribute("stroke", "none");
	    rect.setAttribute("stroke-width", "1"); // Hay que restar el stroke-width a la altura/anchura
	    		
		return rect;
	}
	
	protected SVGShape getRoundedRect(Rectangle2D rect) {
		return getRoundedRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight() );
	}
		
	protected SVGShape getRoundedRect(double x, double y, double width, double height) {		
		SVGShape rect = new SVGRectangle(x, y, width,  height, CORNER_RADIUS, CORNER_RADIUS);
				
	    Color c = mp.getUsualView().getPaint().getColor();    
	    rect.setAttribute ("fill", SVGUtil.getHexColor(c));
	    rect.setAttribute ("stroke", "black");
	    rect.setAttribute ("stroke-width", "1");
		
	    if ( this.mp.isHypothetical() ) {
	    	rect.setAttribute ("style", "stroke-dasharray: 6, 3;");
	    }
		
		return rect;
	}
	
	protected SVGShape getCloneMarker(Rectangle2D rect) throws SBML2SVGException {
		return getCloneMarker(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight() );
	}
	
	protected SVGShape getCloneMarker(double x, double y, double width, double height) throws SBML2SVGException {		
		SVGShape rect = new SVGRectangle(x, y, width,  height, CORNER_RADIUS, CORNER_RADIUS);
		SVGShape background = new SVGRectangle(x, y, width,  height, CORNER_RADIUS, CORNER_RADIUS);
		SVGShape cloneStripe = new SVGSemiRoundRectangle(x , y+ 3*height/4, width,  height/4, CORNER_RADIUS );
				
		
		SVGComplexShape shp = new SVGComplexShape();
		
		Color c = mp.getUsualView().getPaint().getColor();  
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
		
	    if ( this.mp.isHypothetical() ) {
	    	rect.setAttribute ("style", "stroke-dasharray: 6, 3;");
	    }
		
		return shp;
	}
	
	
	protected Point.Double getAngleOnRect2(Rectangle2D r, double angle) {
		double cx = r.getCenterX();
		double cy = r.getCenterY();
		double circleRadius = Math.sqrt( (r.getWidth()/2)*(r.getWidth()/2) + (r.getHeight()/2)*(r.getHeight()/2));
		
		double hx = circleRadius * Math.cos(angle);
		double hy = circleRadius * Math.sin(angle);
		
		double newcx, newcy;
		if (Math.abs(hx) > r.getWidth()/2) {
			if ((angle > Math.PI/2) && (angle < 3*Math.PI/2)) {
				newcx = r.getX();
			} else newcx = r.getMaxX();			
		} else {
			newcx = cx + hx;
		}
			
		if ( Math.abs(hy) > r.getHeight()/2) {
			if (angle > Math.PI) {
				newcy = r.getMaxY();
			} else newcy = r.getY();			
		} else {
			newcy = cy - hy;
		}

		return new Point2D.Double(newcx, newcy);
	}
	
	// Mejor
	// Se calcula el punto que cortaria en el cuadrado de lado 2, centrado en 0.
	// Este cuadrado se circunscribe en un circulo con radio sqrt(2).
	// El punto calculado se escala en anchura y altura al tamaño del rectangulo
	// que define la figura.
	protected Point.Double getAngleOnRect(Rectangle2D r, double angle) {
		double cx = r.getCenterX();
		double cy = r.getCenterY();
		double signX, signY;
		if (angle > Math.PI) {
			signY = -1;
		} else signY = 1;
		
		if ((angle > Math.PI/2) && (angle < 3*Math.PI/2)) {
			signX = -1;
		} else signX = 1;
		
		double hx, hy;
		double radius = Math.sqrt(2);
		
		hy = Math.sin(angle) * radius;
		
		if ( Math.abs(hy) < 1 ) { 
			return new Point2D.Double(cx + signX*r.getWidth()/2 , cy - hy*r.getHeight()/2 );
		}
		
		hx = Math.cos(angle) * radius;
		return new Point2D.Double(cx + hx*r.getWidth()/2, cy - signY*r.getHeight()/2);
	}
	
//	private Point.Double getAngleOnRect4(Rectangle2D.Double r, double angle) {
//		double cx = r.getCenterX();
////		double cy = r.getCenterY();
//				
//		if ((angle > Math.PI/4) && (angle < 3*Math.PI/4)) {
//			return new Point2D.Double( cx + r.getWidth()/(2*Math.tan(angle))    , r.getY());
//		} else if ((angle > 3*Math.PI/4) && (angle < 5*Math.PI/4)) {
//			return new Point2D.Double( r.getX() , Math.tan(angle)*r.getHeight()/2 );
//		} else if ((angle > 5*Math.PI/4) && (angle < 7*Math.PI/4)) {
//			return new Point2D.Double( cx + r.getWidth()/(2*Math.tan(angle))    , r.getMaxY());
//		} else {
//			return new Point2D.Double( r.getMaxX() , Math.tan(angle)*r.getHeight()/2 );
//		}		
//		
//	}	
		
	public double getHeight() {
		return this.bounds.getHeight();
	}


	public double getWidth() {
		return this.bounds.getWidth();
	}


	public double getX() {
		return this.bounds.getX();
	}


	public double getY() {
		return this.bounds.getY();
	}


	public void setLocation(double x, double y) {
		this.bounds.setRect( x, y, this.getWidth(), this.getHeight() );		
	}


	public void setSize(double width, double height) {
		this.bounds.setRect( this.getX(), this.getY(), width, height );		
	}

	protected void addResidues( SVGComplexShape complex, Rectangle2D rect, boolean addText ) {
		
	    if ((mp.getResidues()!=null)) {	    	
	    	SVGShape res, resText;
		    for (MResidue mres : mp.getResidues()) {		    
		    	Point2D.Double p = getAngleOnRect(rect, mres.getAngle()  );
		    	res = getResidue( mres , p );
		    	complex.add(res);		    	
		    	resText = new SVGText( SVGTextRenderer.getInstance().drawTextCentered(svgDoc, p.x, p.y , mres.getState(), 9 ));
		    	complex.add(resText);
		    	
		    	// nombre
		    	if (addText)
		    		complex.add( getResidueText(mres, rect ) );
		    	
		    }	    
	    }
		
	}
	
	protected SVGShape getResidue(MResidue mres, Point2D p) {		
		SVGShape circle = new SVGCircle (p.getX(), p.getY(), RESIDUE_RADIUS) ;
		circle.setAttribute("fill", "#ffffff");
		circle.setAttribute("stroke", "black");
		circle.setAttribute("stroke-width", "1");
		
		return circle;
	}
	
	protected void addStructuralState(SVGComplexShape complex, Rectangle2D rect) {
		if (mp.getStructuralState()!=null) {
			double angle = mp.getStructuralStateAngle();
			Point2D p = getAngleOnRect(rect,  angle ); 
			SVGShape elps = new SVGEllipse (p.getX(), p.getY() - 1 , 
											Math.min(STATE_MAX_RX, rect.getWidth()/2),
											RESIDUE_RADIUS) ;
			elps.setAttribute("fill", "#ffffff");
			elps.setAttribute("stroke", "black");
			elps.setAttribute("stroke-width", "1");
			complex.add(elps);
			if (!mp.getStructuralState().equals("")) {
				complex.add( new SVGText(
				   SVGTextRenderer.getInstance().drawTextCentered(
						   svgDoc, 
						   p.getX(), p.getY(), 
						   mp.getStructuralState(), 
						   9 ) )				
				);
			}
		} 
	}
	
	protected void addBindingRegions(SVGComplexShape complex, Rectangle2D rect, boolean addText) {
	    // TO DO: encapsular en metodo
	    if ((mp.getBindingRegions()!=null)) {
	    	SVGShape brgn;
	    	
	    	for (MBindingRegion mbr : mp.getBindingRegions()) {	    		
	    		brgn = getBindingRegion(mbr, rect, addText);
	    		complex.add(brgn);    
	    		

	    	}
	    }
	    
	}
	
	protected SVGShape getBindingRegion(MBindingRegion mbr, Rectangle2D rect, boolean addText) {		
		SVGRectangle svgRect;
		
		Point2D.Double p = getAngleOnRect(rect, mbr.getAngle()  );
		
		if ((mbr.getAngle()>=Math.PI/4  && mbr.getAngle()<=3*Math.PI/4) ||
			(mbr.getAngle()>=5*Math.PI/4  && mbr.getAngle()<=7*Math.PI/4)) {
			// horizontal
			double width = rect.getWidth() * mbr.getSize();
			svgRect = new SVGRectangle (p.getX()-width/2, p.getY()-RESIDUE_RADIUS, 
										width, RESIDUE_RADIUS*2 ) ;
		} else {
			// vertical. No se por qué, 0.42 es el tamaño equivalente al 100% de la altura
			double height = rect.getHeight() * mbr.getSize() * 1.0/0.42;
			if (height>rect.getHeight()) height = rect.getHeight();
			svgRect = new SVGRectangle (p.getX()-RESIDUE_RADIUS, p.getY()-height/2, 
					  RESIDUE_RADIUS*2, height ) ;
		}
		
		svgRect.setAttribute("fill", "#ffffff");
		svgRect.setAttribute("stroke", "black");
		svgRect.setAttribute("stroke-width", "1");
		
    	// nombre
    	if (addText)
    		return svgRect.composeWith( getBindingRegionText( mbr, rect ) );
    	else
    		return svgRect;
	}
	
	public void svgPaint(Element docParent) throws SBML2SVGException {
		
		if (mp.getViewState().equalsIgnoreCase("brief")) {
			;;; // do nothing
		} else {
//			docParent.appendChild( getSVGShape() );
			super.svgPaint(docParent);
		}
		
	}

	public Document getDocument() {
		return this.svgDoc;
	}
	
	public Point2D getLinkAnchor(int type) {
		Point2D p = null;
		
	    int h = mp.getHomodimer();	    
	    double heightEach = mp.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1)  ;
	    double widthEach = mp.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1) ;
	    double x = mp.getBounds().getX() ;
	    double y = mp.getBounds().getY() ;
	    
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
			p = new Point2D.Double(x + widthEach - NEG_CORNER_RADIUS ,
                    			   y + NEG_CORNER_RADIUS );
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
			p = new Point2D.Double(x + NEG_CORNER_RADIUS ,
								   y + NEG_CORNER_RADIUS );
			break;			
		case SVGSpecie.ANCHOR_NNW:
			p = new Point2D.Double(x + widthEach/4 ,
	   				   			   y );
			break;					
		}
		
		return p;
	}
	
	
	
	protected SVGShape buildSVGShapeSBGN() throws SBML2SVGException {

		SVGComplexShape shp = new SVGComplexShape();
				
		shp.setAttribute("id", mp.getIdAlias()); // shp.setAttribute("style", "text-rendering: auto;");
		if ( !SVGConfig.omitJavascript )
			shp.setAttribute("onclick", "infoWindow(\"" + mp.getIdAlias() + "\"," +
					 "\"" + mp.getName() + "\","+
					 "\"" + "Protein" + "\"" +
					 ");");
		
		
	    int h = Math.min( mp.getHomodimer(), 2 );    
	    double heightEach = mp.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = mp.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
	    
	    this.center.x = mp.getBounds().getX() + widthEach/2;
	    this.center.y = mp.getBounds().getY() + heightEach/2;
	    
	    SVGShape simpleShape;

	    
	    Rectangle2D.Double rect = null;
	    
	    for (int hc = h-1 ; hc>=0 ; hc--) {	
	    	
	    	rect = new Rectangle2D.Double(
	    			mp.getBounds().getX() + HOMODIMER_WIDTH*hc,
	    			mp.getBounds().getY() + HOMODIMER_HEIGHT*hc , 
	    			widthEach, heightEach);
	    	
	    	if (this.isClone()) {
	    		simpleShape = this.getCloneMarker( rect );
	    	} else {
	    		simpleShape = this.getRoundedRect( rect );
	    	}
	    				 	    	
	    	shp.add(simpleShape);	   
		
		    addResidues(shp, rect, hc==0);
		    addBindingRegions(shp, rect, hc==0);
		    addStructuralState(shp, rect);	
		   
	    }

	    if (active) {
	      	SVGEllipse ellipse = new SVGEllipse(this.center.x, this.center.y+heightEach/2 ,
	      			                            Math.min(STATE_MAX_RX, rect.getWidth()/2),
	      			                            RESIDUE_RADIUS);
	      	ellipse.setAttribute("stroke", "black");
	      	ellipse.setAttribute("stroke-width", "1");
	      	ellipse.setAttribute("fill", "white");
	      	shp.add( ellipse );
	      	shp.add( new SVGText(
    				SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
    						this.center.x , 
    						this.center.y + heightEach/2,
			    		    "Active",
			    		9 ))
    		);
	    }
	    
	    // TO DO: encapsular en metodo
		shp.add( new SVGText(
	    				SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
	    						-2 +
				    		mp.getBounds().getX() + 
				    		widthEach / 2 , 
				    		mp.getBounds().getY() + 
				    		heightEach / 2 , 
				    		mp.getName() ,
				    		12 ))
	    		);
		
		shp.add( getInfoUnit(rect) );
		
		this.shape = shp;
	    return this.shape;
	}
	
	protected SVGShape buildSVGShape() {
		
		SVGComplexShape shp = new SVGComplexShape();
				
		shp.setAttribute("id", mp.getIdAlias()); // shp.setAttribute("style", "text-rendering: auto;");
		if ( !SVGConfig.omitJavascript )
			shp.setAttribute("onclick", "infoWindow(\"" + mp.getIdAlias() + "\"," +
					 "\"" + mp.getName() + "\","+
					 "\"" + "Protein" + "\"" +
					 ");");
		
		
	    int h = mp.getHomodimer();	    
	    this.heightEach = mp.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    this.widthEach = mp.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
	    
	    this.center.x = mp.getBounds().getX() + widthEach/2;
	    this.center.y = mp.getBounds().getY() + heightEach/2;
	    
	    SVGShape simpleShape;
	    if (active) {
	    	// Primero los bordes
	    	
	    	for (int hc = h-1 ; hc>=0 ; hc--) {	    		
	    		
	    		simpleShape = getDashedRect(
	    				 mp.getBounds().getX() + HOMODIMER_WIDTH*hc - ACTIVITY_MARGIN , 
	    				 mp.getBounds().getY() + HOMODIMER_HEIGHT*hc - ACTIVITY_MARGIN , 
	    				 widthEach + ACTIVITY_MARGIN*2, 
	    				 heightEach + ACTIVITY_MARGIN*2
	    		);
	    		shp.add(simpleShape);	    				    		
	    	}
	    	// Despues los rellenos, para que no se solapen las líneas.
	    	for (int hc = h-1 ; hc>=0 ; hc--) {	
	    		simpleShape = getDashedFill(
	    				 mp.getBounds().getX() + HOMODIMER_WIDTH*hc - ACTIVITY_MARGIN , 
	    				 mp.getBounds().getY() + HOMODIMER_HEIGHT*hc - ACTIVITY_MARGIN , 
	    				 widthEach + ACTIVITY_MARGIN*2, 
	    				 heightEach + ACTIVITY_MARGIN*2
	    		);  	
	    		shp.add(simpleShape);	    		
	    	}	    	
	    }
	    
	    Rectangle2D.Double rect = null;
	    
	    for (int hc = h-1 ; hc >= 0 ; hc--) {	
	    	
	    	rect = new Rectangle2D.Double(
	    			mp.getBounds().getX() + HOMODIMER_WIDTH*hc,
	    			mp.getBounds().getY() + HOMODIMER_HEIGHT*hc , 
	    			widthEach, heightEach);
	    	
	    	simpleShape = this.getRoundedRect( rect );
	    				 	    	
	    	shp.add(simpleShape);	   
		
		    addResidues(shp, rect, hc==0);
		    addBindingRegions(shp, rect, hc==0);
		    addStructuralState(shp, rect);	
		   
	    }

	    shp.add( getLabel() );
		
		shp.add( getInfoUnit( rect ) );
		
		this.shape = shp;
	    return this.shape;
	}


	
	
	protected Point2D getPointOnRect(Rectangle2D rect, double angle ) {
		/* Calcular punto de interseccion con el angulo dado */
		SVGRectangle frame = new SVGRectangle( -rect.getWidth()/2, -rect.getHeight()/2,
												rect.getWidth(), rect.getHeight(), 
												CORNER_RADIUS, CORNER_RADIUS );
		// Punto externo al rectangulo
		Point2D pExt= new Point2D.Double( 100000 * Math.cos( angle ) , 100000 * Math.sin( angle ) );
		Segment s = new Segment( pExt.getX(), pExt.getY() , 0, 0 );
		Point2D pointAtAngle = frame.intersection( s );
		Point2D p = new Point2D.Double( rect.getCenterX() + pointAtAngle.getX(),
				                rect.getCenterY() + pointAtAngle.getY() );
		
		return p;
	}
	


	protected SVGShape getBindingRegionText(MBindingRegion mbr, Rectangle2D rect ) {
		SVGShape s;
		Rectangle2D innerRect = SVGUtil.getTrimmedRectangle( rect, 9.5, 9.5 ); 
		Point2D p = getAngleOnRect(innerRect, mbr.getAngle() );
		s = new SVGText(
				SVGTextRenderer.getInstance().drawTextCenteredWithinFrame(svgDoc, 
			    		p.getX() , p.getY(), innerRect,
			    		mbr.getName() ,
			    		8 ));
		
		return s;
	}

	protected SVGShape getResidueText(MResidue mres, Rectangle2D rect ) {
		SVGShape s;
		Rectangle2D innerRect = SVGUtil.getTrimmedRectangle( rect, 9.5, 9.5 ); 
		Point2D p = getAngleOnRect(innerRect, mres.getAngle() );
		s = new SVGText(
				SVGTextRenderer.getInstance().drawTextCenteredWithinFrame(svgDoc, 
			    		p.getX() , p.getY(), innerRect,
			    		mres.getName() ,
			    		8 ));
		
		return s;
	}
	
	
	@Override
	public Rectangle2D getBBox() {
		return this.bounds;
	}
 

}
