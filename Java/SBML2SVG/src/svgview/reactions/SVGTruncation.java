package svgview.reactions;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Vector;

import model.reaction.MTruncation;
import model.specie.MAddedSpeciesLink;

import org.w3c.dom.Document;

import svgcontroller.SBML2SVGException;
import svgview.SVGConfig;
import svgview.shapes.SVGComplexShape;
import svgview.shapes.SVGLine;
import svgview.shapes.SVGPath;
import svgview.shapes.SVGReactionSquare;
import svgview.shapes.SVGShape;
import svgview.shapes.Segment;
import svgview.species.SVGSpecie;
import svgview.util.SVGUtil;

public class SVGTruncation extends SVGGenericReaction {	
	MTruncation mtrunc;
	
	public SVGTruncation(Document svgDoc, MTruncation mtrunc, Map<String, SVGSpecie> species) {
		super(svgDoc, mtrunc, species);
		this.mtrunc = mtrunc;
		this.mr = this.mtrunc;
	}

	protected SVGShape buildSVGShape_() {
		return null;
	}
	
	protected SVGShape buildSVGShape() throws SBML2SVGException {
		squareShape 	= new SVGComplexShape();
		lines 			= new SVGComplexShape();
		linesBackground = new SVGComplexShape();
		arrows 			= new SVGComplexShape();
		shape 			= new SVGComplexShape();
		texts 			= new SVGComplexShape();
		
		String idr = mr.getReactants().get(0).getMs().getIdAlias();
		String idp0 = mr.getProducts().get(0).getMs().getIdAlias();			
		String idp1 = mr.getProducts().get(1).getMs().getIdAlias();
		SVGSpecie reac = this.species.get(idr);
		SVGSpecie prod0 = this.species.get(idp0);
		SVGSpecie prod1 = this.species.get(idp1);
		int[] arm = mtrunc.getArms();
		int rectangleIndex = mtrunc.getTShapeIndex();
				
		// Primero calcular punto de union, basado en los vectores R->P0, y R->P1
		Point2D centerR = reac.getCenter();
		Point2D centerP0 = prod0.getCenter();
		Point2D centerP1 = prod1.getCenter();
		Point2D epJoin = editPoints.lastElement();
		
		Point2D pjoin = new Point2D.Double(
							// R->P0
							(centerP1.getX() - centerR.getX()) * epJoin.getY() + 
							(centerP0.getX() - centerR.getX()) * epJoin.getX() +
							centerR.getX(), 
							// R->P1
							(centerP1.getY() - centerR.getY()) * epJoin.getY() + 
							(centerP0.getY() - centerR.getY()) * epJoin.getX() +
							centerR.getY()
				);

		// Rama0
		Point2D porigR;
		if (mtrunc.getReactants().get(0).getLinkAnchor() != null) {
			porigR = reac.getLinkAnchor( mtrunc.getReactants().get(0).getLinkAnchor() );
		} else {
			porigR = reac.getSVGShape().intersection( new Segment( pjoin, centerR ) );
		}
		
		AffineTransform at1 = getEditPointTransform(new Segment(pjoin, porigR));		
		Point2D pp;
		for(int i=0; i < arm[0]; i++) {
			pp = editPoints.get(i);
			at1.transform(pp, pp);
//			complex.add( getEditPoints(pp, "blue")  );			
		}
		
		// Rama1
		Point2D pdestP0;
		if (mtrunc.getProducts().get(0).getLinkAnchor() != null) {
			pdestP0 = prod0.getLinkAnchor( mtrunc.getProducts().get(0).getLinkAnchor() );
		} else {
			pdestP0 = prod0.getSVGShape().intersection( new Segment( pjoin, centerP0 ) );
		}
		
		
		at1 = getEditPointTransform(new Segment(pjoin, pdestP0));		
		for(int i=arm[0]; i < arm[0]+arm[1]; i++) {
			pp = editPoints.get(i);
			at1.transform(pp, pp);
//			complex.add( getEditPoints(pp, "blue")  );
		}			
		
//		// Rama2
		Point2D pdestP1;
		if (mtrunc.getProducts().get(1).getLinkAnchor() != null) {
			pdestP1 = prod1.getLinkAnchor( mtrunc.getProducts().get(1).getLinkAnchor() );
		} else {
			pdestP1 = prod1.getSVGShape().intersection( new Segment( pjoin, centerP1 ) );
		}
		
		at1 = getEditPointTransform(new Segment(pjoin, pdestP1));		
		for(int i=arm[0]+arm[1]; i < arm[0]+arm[1]+arm[2]; i++) {
			pp = editPoints.get(i);
			at1.transform(pp, pp);
//			complex.add( getEditPoints(pp, "blue")  );
		}	
		
		// Primeros y ultimos de cada rama
		if (arm[0] != 0) {
			this.lines.composeWith( new SVGLine(porigR.getX(), porigR.getY(), 
									 editPoints.elementAt(arm[0]-1).getX(), editPoints.elementAt(arm[0]-1).getY()) );
			this.linesBackground.composeWith( 
					new SVGLine( new Segment(editPoints.elementAt(arm[0]-1), porigR).trim(10) ) 
					) ;				
			this.lines.composeWith( new SVGLine(editPoints.elementAt(0).getX(), editPoints.elementAt(0).getY() ,  
					 pjoin.getX(), pjoin.getY() ) );
			this.linesBackground.composeWith( 
					new SVGLine( new Segment(editPoints.elementAt(0), pjoin) ) 
					) ;				
		} else {
			this.lines.composeWith( new SVGLine(porigR.getX(), porigR.getY(), pjoin.getX(), pjoin.getY()) );
			this.linesBackground.composeWith( 
					new SVGLine( new Segment(pjoin,porigR).trim(10) ) 
					) ;				
		}
		
		Point2D p1, p2;
		for(int i=0; i < arm[0]-1; i++) {
			p1 = editPoints.elementAt(i);
			p2 = editPoints.elementAt(i+1);
			this.lines.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
			this.linesBackground.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
		}
			

		// Dibujar Rama1
		if (arm[1] != 0) {
			this.arrows.composeWith( getArrow(editPoints.elementAt(arm[0]+arm[1]-1),
									 		  pdestP0 ) );
			this.linesBackground.composeWith( 
					new SVGLine( new Segment(editPoints.elementAt(arm[0]+arm[1]-1), pdestP0).trim(10) ) 
					) ;				
			this.lines.composeWith( new SVGLine(pjoin.getX(), pjoin.getY(), 
									 editPoints.elementAt(arm[0]).getX(), editPoints.elementAt(arm[0]).getY() ) );
			this.linesBackground.composeWith( 
					new SVGLine( new Segment(editPoints.elementAt(arm[0]), pjoin) ) 
					) ;	
		} else {
			this.arrows.composeWith( getArrow(pjoin, pdestP0) );
		}
		
		for(int i=arm[0]; i < arm[0]+arm[1]-1; i++) {
			p1 = editPoints.elementAt(i);
			p2 = editPoints.elementAt(i+1);
			this.lines.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
			this.linesBackground.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
		}
		
		// Dibujar Rama2
		if (arm[2] != 0) {
			this.arrows.composeWith( getArrow(editPoints.elementAt(arm[0]+arm[1]+arm[2]-1),					 
					 						  pdestP1) );
			this.linesBackground.composeWith( 
				new SVGLine( new Segment(editPoints.elementAt(arm[0]+arm[1]+arm[2]-1), pdestP1).trim(10) ) 
				) ;	
			
			this.lines.composeWith( new SVGLine(pjoin.getX(), pjoin.getY(),
								 editPoints.elementAt(arm[0]+arm[1]).getX(), 
								 editPoints.elementAt(arm[0]+arm[1]).getY() ) );
			this.linesBackground.composeWith( 
				new SVGLine( new Segment(editPoints.elementAt(arm[0]+arm[1]), pjoin) ) 
				);				
		} else {
			this.arrows.composeWith( getArrow(pjoin, pdestP1) );
			this.linesBackground.composeWith( 
				new SVGLine( new Segment(pjoin, pdestP1).trim(10) ) 
				);	
		}
		
		for(int i=arm[0]+arm[1]; i < arm[0]+arm[1]+arm[2]-1; i++) {
			p1 = editPoints.elementAt(i);
			p2 = editPoints.elementAt(i+1);
			p1 = editPoints.elementAt(i);
			p2 = editPoints.elementAt(i+1);
			this.lines.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
			this.linesBackground.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
		}

		// SVGShape rect;
		Point2D pAddR, pAddP;
		if (arm[0] == 0) {
			
			pAddR = porigR;
			pAddP = pjoin;
			
		} else {
			if (rectangleIndex == 0) {			
				// Primer segmento
				pAddR = editPoints.elementAt(0);
				pAddP = pjoin;
//				complex.add( getRectangleBetween(pAddR, pAddP, "N") );
				
			} else if (rectangleIndex == arm[0]) {
				// Ultimo segmento
				pAddR = porigR;
				pAddP = editPoints.elementAt(arm[0]-1);
//				complex.add( getRectangleBetween(pAddR, pAddP, "N") );
			} else {
				pAddP = editPoints.elementAt(rectangleIndex-1);
				pAddR = editPoints.elementAt(rectangleIndex);				
//				complex.add( getRectangleBetween(pAddR, pAddP, "N"));	
			}	
		}
		
		
		// Colocar nombre de la reacción
		if (arm[0] != 0) {
			this.texts.composeWith( getReactionText( porigR, 
										  editPoints.elementAt(arm[0]-1), 
										  mtrunc.getReactants().get(0).getLinkAnchor() ) );			
		} else {
			this.texts.composeWith( getReactionText( porigR, 
										  pjoin, 
										  mtrunc.getReactants().get(0).getLinkAnchor() ) );			
		}

		Point2D rectangleCenter = new Segment( pAddR, pAddP).getCenter();

		for (MAddedSpeciesLink spc : mtrunc.getReactantLinks()) {					
			buildAddedReactant(spc, pAddR, rectangleCenter);
		}					
		for (MAddedSpeciesLink spc : mtrunc.getProductLinks()) {				
			buildAddedProduct(spc, pAddP, rectangleCenter);
		}	

		SVGShape square = getTruncationRectangleBetween(pAddR, pAddP);
		this.squareShape = square ;		
		
		buildModifications();
		buildBooleanLogicGates();
		
		lines.setAttribute("stroke", SVGUtil.getHexColor( mtrunc.getLine().getColor() ) );
		lines.setAttribute("stroke-width", "" + mtrunc.getLine().getWidth() );
		lines.setAttribute("stroke-linecap", "butt");
		lines.setAttribute("stroke-linejoin", "round");
		
		linesBackground.setAttribute("fill", "none");
		linesBackground.setAttribute("stroke", SVGConfig.reactionBackgroundColor);
		linesBackground.setAttribute("stroke-width", "" + (mtrunc.getLine().getWidth() + 4.0) );
		
		this.shape.composeWith( linesBackground );		
		this.shape.composeWith( lines );				
		this.shape.composeWith( arrows );
		this.shape.composeWith( squareShape );		
		this.shape.composeWith( texts );
				
		this.shape.setAttribute("id", mr.getId()); // shp.setAttribute("style", "text-rendering: auto;");		
	
		if ( !SVGConfig.omitJavascript )			
			this.shape.setAttribute("onclick", "infoWindow(\"" + mr.getId() + "\"," +
					 "\"" + mr.getId() + "\","+
					 "\"" + "Truncation" + "\"" +
					 ");");
		this.shape.setAttribute("stroke", "black");
		this.shape.setAttribute("stroke-linecap", "round");
		
		return this.shape;
	}
	
	/**
	 * Redefinir dibujo del cuadrado de la reacción, para añadir la "N" de forma que sea
	 * transformable junto con la forma del cuadrado
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	protected SVGShape getTruncationRectangleBetween(Point2D p1, Point2D p2) {
		SVGShape rect;
		
		Point2D pcenter = new Point2D.Double( (p1.getX()+p2.getX())/2, (p1.getY()+p2.getY())/2 );
		
		double theta = Math.atan2(p1.getY()-p2.getY() , p1.getX()-p2.getX() );
		
		AffineTransform at = new AffineTransform();
		at.translate(pcenter.getX(), pcenter.getY()); at.rotate(theta);
		
//		// Vertices del cuadrado
//		Point2D t0 = new Point2D.Double(5, 5);
//		Point2D t1 = new Point2D.Double(5, -5);
//		Point2D t2 = new Point2D.Double(-5, -5);
//		Point2D t3 = new Point2D.Double(-5, 5);
		
		Point2D n0 = new Point2D.Double(2, -5);
		Point2D n1 = new Point2D.Double(2, 2.0);
		Point2D n2 = new Point2D.Double(-2, -2.0);
		Point2D n3 = new Point2D.Double(-2, 5);
		
		at.transform(n0, n0);
		at.transform(n1, n1);
		at.transform(n2, n2);
		at.transform(n3, n3);

		Vector<Point2D> nPoints = new Vector<Point2D>();
		nPoints.add(n0);
		nPoints.add(n1);
		nPoints.add(n2);
		nPoints.add(n3);
		
		SVGReactionSquare sqr = new SVGReactionSquare(p1, p2);
		
		SVGPath nCharacter = new SVGPath(nPoints, "fill:none; stroke-linecap:butt;stroke:" + SVGUtil.getHexColor( mr.getLine().getColor() ));
		
		rect = sqr;
		rect.setAttribute("fill", "white");	
		rect.setAttribute("stroke", SVGUtil.getHexColor( mr.getLine().getColor() ) );
		rect.setAttribute("stroke-width", "" + mr.getLine().getWidth() );		

		this.square = sqr;
		
		return rect.composeWith( nCharacter );
		
	}

	@Override
	protected boolean showsSquare() {
		return true;
	}

}
