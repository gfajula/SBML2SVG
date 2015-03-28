package svgview.reactions;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Map;

import model.reaction.MHeterodimerAssociation;
import model.specie.MAddedSpeciesLink;

import org.w3c.dom.Document;

import svgcontroller.SBML2SVGException;
import svgview.SVGConfig;
import svgview.shapes.SVGArrow;
import svgview.shapes.SVGCircle;
import svgview.shapes.SVGComplexShape;
import svgview.shapes.SVGLine;
import svgview.shapes.SVGShape;
import svgview.shapes.Segment;
import svgview.species.SVGSpecie;
import svgview.util.SVGScripts;
import svgview.util.SVGUtil;

public class SVGHeterodimerAssociation extends SVGGenericReaction {
	MHeterodimerAssociation mr;
	public SVGHeterodimerAssociation(Document svgDoc, MHeterodimerAssociation mr,
			Map<String, SVGSpecie> species) {
		super(svgDoc, mr, species);
		this.mr = mr;
	}
	
	protected SVGShape buildSVGShapeStandard() throws SBML2SVGException {
		SVGShape reactionShape = super.buildSVGShape();
		
		int pJoinIdx = this.mr.getReactantLinks().get(0).getJoint();
		Point2D  pJoin = this.mr.getEditPoints().get( pJoinIdx );
		
		SVGShape outcome;
		if ( this.mr.getEditPoints().size() > (pJoinIdx + 1) ) {
			outcome = getSBGNOutcome(pJoin, this.mr.getEditPoints().get( pJoinIdx + 1));			
		} else {
			String idp1 = mr.getProducts().get(0).getMs().getIdAlias();				
			outcome = getSBGNOutcome(pJoin, species.get(idp1).getCenter() );	
		}
		
		reactionShape = reactionShape.composeWith( outcome );
		
		return reactionShape;
	}
	protected SVGShape buildSVGShape() throws SBML2SVGException {
		if (!this.isTransformEditPoints()) return buildSVGShapeStandard();
		if (SVGConfig.SBGNMode) return buildSVGShapeSBGN();
		
		squareShape 	= new SVGComplexShape();
		lines 			= new SVGComplexShape();
		linesBackground = new SVGComplexShape();
		arrows 			= new SVGComplexShape();
		shape 			= new SVGComplexShape();
		texts 			= new SVGComplexShape();		
		
		Point2D porigR0 = null;
		Point2D porigR1 = null;
		
		String idr0 = mr.getReactants().get(0).getMs().getIdAlias();
		String idr1 = mr.getReactants().get(1).getMs().getIdAlias();			
		String idp = mr.getProducts().get(0).getMs().getIdAlias();
		SVGSpecie reac0 = this.species.get(idr0);
		SVGSpecie reac1 = this.species.get(idr1);
		SVGSpecie prod = this.species.get(idp);
		int[] arm = mr.getArms();
		int rectangleIndex = mr.getTShapeIndex();
		
		// Check linAnchors
		//           Las intersecciones se calculan en la fase final del 
		//			 dibujado de la reaccion, y si no hay linkanchors, se
		//           usan los centros para todos los calculos.
		
		if (mr.getReactants().get(0).getLinkAnchor() != null) {
			porigR0 = reac0.getLinkAnchor( mr.getReactants().get(0).getLinkAnchor() );
		} else {
			porigR0 = reac0.getCenter();
		}

		if (mr.getReactants().get(1).getLinkAnchor() != null) {
			porigR1 = reac1.getLinkAnchor( mr.getReactants().get(1).getLinkAnchor() );
		} else {
			porigR1 = reac1.getCenter();
		}
		
		if (mr.getProducts().get(0).getLinkAnchor() != null) {
			pdest = prod.getLinkAnchor( mr.getProducts().get(0).getLinkAnchor() );
		} else {
			pdest = prod.getCenter();
		}
		
		
		// Primero calcular punto de union, basado en los vectores R1->R0, y R1->P
		Point2D centerR0 = reac0.getCenter();
		Point2D centerR1 = reac1.getCenter();
		Point2D centerPr = prod.getCenter();
		Point2D epJoin = editPoints.lastElement();
		
		// ¡¡pjoin depende de los puntos centrales de los componentes, no de los puntos de unión!!
		Point2D pjoin = new Point2D.Double(
							(centerPr.getX() - centerR0.getX()) * epJoin.getY() + 
							(centerR1.getX() - centerR0.getX()) * epJoin.getX() +
							centerR0.getX(), 
							(centerPr.getY() - centerR0.getY()) * epJoin.getY() + 
							(centerR1.getY() - centerR0.getY()) * epJoin.getX() +
							centerR0.getY()
				);
		
		/* Transformar los puntos */
		
		// Rama0		
		AffineTransform at1 = getEditPointTransform(new Segment(pjoin, porigR0));		
		Point2D pp;
		for(int i=0; i < arm[0]; i++) {
			pp = editPoints.get(i);
			at1.transform(pp, pp);			
		}		

		// Rama1
		at1 = getEditPointTransform(new Segment(pjoin, porigR1));		
		for(int i=arm[0]; i < arm[0]+arm[1]; i++) {
			pp = editPoints.get(i);
			at1.transform(pp, pp);
		}	
		
		// Rama2
		at1 = getEditPointTransform(new Segment(pjoin, pdest));		
		for(int i=arm[0]+arm[1]; i < arm[0]+arm[1]+arm[2]; i++) {
			pp = editPoints.get(i);
			at1.transform(pp, pp);
		}	
		
		
		// Primeros y últimos de cada rama
		if (arm[0] != 0) {
			
			if ( mr.getReactants().get(0).getLinkAnchor() == null ) { 
				Point2D tunedPoint = 
					reac0.intersection( new Segment( editPoints.elementAt(arm[0]-1), porigR0 ) );
				if ( tunedPoint != null ) {
					porigR0 = tunedPoint;
				}
			}
			
			this.lines.composeWith( new SVGLine(porigR0, 
									 editPoints.elementAt(arm[0]-1) ) );
			this.linesBackground.composeWith( 
					new SVGLine( 
							new Segment(editPoints.elementAt(arm[0]-1), porigR0).trim(10) 
							) 
					);
			
			this.lines.composeWith( new SVGLine(editPoints.elementAt(0) , pjoin) );
			this.linesBackground.composeWith( 
					new SVGLine( 
							new Segment(editPoints.elementAt(0) , pjoin)
							) 
					);
			
		} else {
			if ( mr.getReactants().get(1).getLinkAnchor() == null ) {
				Point2D tunedPoint = 
					reac0.intersection( new Segment( pjoin, porigR0 ) );
				if ( tunedPoint != null ) {
					porigR0 = tunedPoint;
				}
			}
			this.lines.composeWith( new SVGLine(porigR0, pjoin) );
			
			this.linesBackground.composeWith( 
					new SVGLine( new Segment(pjoin, porigR0).trim(10) ) );
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
			if ( mr.getReactants().get(1).getLinkAnchor() == null ) {
				Point2D tunedPoint = 
					reac1.intersection( new Segment( editPoints.elementAt(arm[0]+arm[1]-1 ), porigR1 ) );
				if ( tunedPoint != null ) {
					porigR1 = tunedPoint;
				}
			}
			this.lines.composeWith( new SVGLine(porigR1 , 
									             editPoints.elementAt(arm[0]+arm[1]-1) ) );
			
			this.linesBackground.composeWith( 
					new SVGLine( 
							new Segment(editPoints.elementAt(arm[0]+arm[1]-1), porigR1).trim(10) 
							) 
					);
			
			this.lines.composeWith( new SVGLine(editPoints.elementAt(arm[0]).getX(), editPoints.elementAt(arm[0]).getY() ,  
					 				 pjoin.getX(), pjoin.getY() ) );
					 				 
			this.linesBackground.composeWith( 
					new SVGLine( 
							new Segment(editPoints.elementAt(arm[0]) , pjoin)
							) 
					);			
		} else {
			Point2D tunedPoint = 
				reac1.intersection( new Segment( pjoin, porigR1 ) );
			if ( tunedPoint != null ) {
				porigR1 = tunedPoint;
			}
			
			this.lines.composeWith( new SVGLine(porigR1 , pjoin ) );
			
			this.linesBackground.composeWith( 
					new SVGLine( new Segment(pjoin, porigR1).trim(10) ) );			
		}
		
		for(int i=arm[0]; i < arm[0]+arm[1]-1; i++) {
			p1 = editPoints.elementAt(i);
			p2 = editPoints.elementAt(i+1);
			this.lines.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
			this.linesBackground.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
		}
		
		// Dibujar Rama2
		if (arm[2] != 0) {
			if ( mr.getProducts().get(0).getLinkAnchor() == null ) {
				Point2D tunedPoint = 
					prod.intersection( new Segment( editPoints.elementAt(arm[0]+arm[1]+arm[2]-1), pdest ) );
				if ( tunedPoint != null ) {
					pdest = tunedPoint;
				}
			}
			this.arrows.composeWith( new SVGArrow(editPoints.elementAt(arm[0]+arm[1]+arm[2]-1),									 
									 pdest,
									 false, 
									 SVGUtil.getHexColor( mr.getLine().getColor() ), 
									 mr.getLine().getWidth() ) );
			
			this.linesBackground.composeWith( 
					new SVGLine( 
							new Segment(editPoints.elementAt(arm[0]+arm[1]+arm[2]-1), pdest).trim(10) 
							) 
					);			
			
			this.lines.composeWith( new SVGLine(editPoints.elementAt(arm[0]+arm[1]).getX(), 
									 editPoints.elementAt(arm[0]+arm[1]).getY() ,  
					 				 pjoin.getX(), pjoin.getY() ) );
			this.linesBackground.composeWith( 
					new SVGLine( 
							new Segment(editPoints.elementAt(arm[0]+arm[1]), pjoin).trim(10) 
							) 
					);					
		} else {
			Point2D tunedPoint = 
				prod.intersection( new Segment( pjoin, pdest ) );
			if ( tunedPoint != null ) {
				pdest = tunedPoint;
			}
			this.arrows.composeWith( new SVGArrow(	pjoin, 
													pdest,
													false, 
												 	SVGUtil.getHexColor( mr.getLine().getColor() ), 
												 	mr.getLine().getWidth() 
												 ) );
			this.linesBackground.composeWith( 
					new SVGLine( new Segment(pjoin, pdest).trim(10) ) );				
		}
		
		for(int i=arm[0]+arm[1]; i < arm[0]+arm[1]+arm[2]-1; i++) {
			p1 = editPoints.elementAt(i);
			p2 = editPoints.elementAt(i+1);
			this.lines.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
			this.linesBackground.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
		}

		// SVGShape rect;
		Point2D pAddR, pAddP;
		if (arm[2] == 0) {
			pAddR = pjoin;
			pAddP = pdest;
			
		} else {
			if (rectangleIndex == 0) {			
				// Primer segmento
				pAddR = pjoin;
				pAddP = editPoints.elementAt(arm[0]+arm[1]);				
			} else if (rectangleIndex == arm[2]) {
				// Ultimo segmento
				pAddR = editPoints.elementAt(arm[0]+arm[1]+arm[2]-1);
				pAddP = pdest;
			} else {
				pAddR = editPoints.elementAt(arm[0]+arm[1]+rectangleIndex-1);
				pAddP = editPoints.elementAt(arm[0]+arm[1]+rectangleIndex);						
			}	
		}
		
		Point2D rectangleCenter = new Segment( pAddR, pAddP).getCenter();
		this.reactionCenter = rectangleCenter;
//		SVGSpecie svgSpc;
		for (MAddedSpeciesLink spc : mr.getReactantLinks()) {						
			buildAddedReactant(spc, pAddR, rectangleCenter);
		}					
		for (MAddedSpeciesLink spc : mr.getProductLinks()) {				
			buildAddedProduct(spc, pAddP, rectangleCenter);
		}	
		
		squareShape =  getRectangleBetween(pAddR, pAddP);
					
		if (arm[1] != 0) {
			this.texts.composeWith( getReactionText( porigR1, 
										  editPoints.elementAt(arm[0]+arm[1]-1), 
										  mr.getReactants().get(1).getLinkAnchor() ) );			
		} else {
			this.texts.composeWith( getReactionText( porigR1, 
										  pjoin, 
										  mr.getReactants().get(1).getLinkAnchor() ) );			
		}
		
		
		SVGCircle circle = new SVGCircle(pjoin.getX(), pjoin.getY(), 3.5);
		circle.setAttribute("stroke", "none");
		circle.setAttribute("fill", "black");
		this.lines.composeWith( circle );
				
		
		lines.setAttribute("stroke", SVGUtil.getHexColor( mr.getLine().getColor() ) );
		lines.setAttribute("stroke-width", "" + mr.getLine().getWidth() );
		lines.setAttribute("stroke-linecap", "butt");
		lines.setAttribute("stroke-linejoin", "round");
		
		buildModifications();
		buildBooleanLogicGates();
		
		linesBackground.setAttribute("fill", "none");
		linesBackground.setAttribute("stroke", SVGConfig.reactionBackgroundColor);
		linesBackground.setAttribute("stroke-width", "" + (mr.getLine().getWidth() + 4.0) );
		
		shape = shape.composeWith( linesBackground );		
		shape = shape.composeWith( lines );				
		shape = shape.composeWith( arrows );
		shape = shape.composeWith( squareShape );		
		shape = shape.composeWith( texts );
		
		shape.setAttribute("id", mr.getId()); // shp.setAttribute("style", "text-rendering: auto;");
		
		if ( !SVGConfig.omitJavascript )	
			shape.setAttribute("onclick", "infoWindowReac(\"" + mr.getId() + "\"," +
					 "\"" + mr.getId() + "\","+
					 "\"" + "Heterodimer Association" + "\"," +
					 SVGScripts.javaArrayToJS( this.getReactantIDs() ) + "," +
					 SVGScripts.javaArrayToJS( this.getProductIDs() ) +
					 ");");
		shape.setAttribute("stroke", "black");
		shape.setAttribute("stroke-linecap", "round");
		
		// this.shape = complex;
		return this.shape;
	}
	
	
	protected SVGShape getSBGNOutcome(Point2D p1, Point2D p2){
		Segment s = new Segment( p1, p2 );
		s = s.subSegment(15);
		SVGShape outcome = new SVGCircle(s.getX2(), s.getY2(), 4);
		outcome.setAttribute("fill", "black");
		this.reactionCenter = s.getP2();
		return outcome;		
	}
	
	protected SVGShape buildSVGShapeSBGN() throws SBML2SVGException {
		
		squareShape 	= new SVGComplexShape();
		lines 			= new SVGComplexShape();
		linesBackground = new SVGComplexShape();
		arrows 			= new SVGComplexShape();
		shape 			= new SVGComplexShape();
		texts 			= new SVGComplexShape();		
		
		Point2D porigR0 = null;
		Point2D porigR1 = null;
		
		String idr0 = mr.getReactants().get(0).getMs().getIdAlias();
		String idr1 = mr.getReactants().get(1).getMs().getIdAlias();			
		String idp = mr.getProducts().get(0).getMs().getIdAlias();
		SVGSpecie reac0 = this.species.get(idr0);
		SVGSpecie reac1 = this.species.get(idr1);
		SVGSpecie prod = this.species.get(idp);
		int[] arm = mr.getArms();
		int rectangleIndex = mr.getTShapeIndex();
		
		// Check linAnchors
		//           Las intersecciones se calculan en la fase final del 
		//			 dibujado de la reaccion, y si no hay linkanchors, se
		//           usan los centros para todos los calculos.
		
		if (mr.getReactants().get(0).getLinkAnchor() != null) {
			porigR0 = reac0.getLinkAnchor( mr.getReactants().get(0).getLinkAnchor() );
		} else {
			porigR0 = reac0.getCenter();
		}

		if (mr.getReactants().get(1).getLinkAnchor() != null) {
			porigR1 = reac1.getLinkAnchor( mr.getReactants().get(1).getLinkAnchor() );
		} else {
			porigR1 = reac1.getCenter();
		}
		
		if (mr.getProducts().get(0).getLinkAnchor() != null) {
			pdest = prod.getLinkAnchor( mr.getProducts().get(0).getLinkAnchor() );
		} else {
			pdest = prod.getCenter();
		}
		
		
		// Primero calcular punto de union, basado en los vectores R1->R0, y R1->P
		Point2D centerR0 = reac0.getCenter();
		Point2D centerR1 = reac1.getCenter();
		Point2D centerPr = prod.getCenter();
		 
		Point2D epJoin = editPoints.lastElement();
		
		// ¡¡pjoin depende de los puntos centrales de los componentes, no de los puntos de unión!!
		Point2D pjoin;
		
		if ( this.mr.isTransformEditPoints() ) { 
			pjoin = new Point2D.Double(
							(centerPr.getX() - centerR0.getX()) * epJoin.getY() + 
							(centerR1.getX() - centerR0.getX()) * epJoin.getX() +
							centerR0.getX(), 
							(centerPr.getY() - centerR0.getY()) * epJoin.getY() + 
							(centerR1.getY() - centerR0.getY()) * epJoin.getX() +
							centerR0.getY()
				);
		} else {
			pjoin = epJoin;
		}
		
		// Rama0
		//porigR0 = porigR0!=null?porigR0:reac0.intersection( new Segment( pjoin, centerR0 ) );
		
		AffineTransform at1 = getEditPointTransform(new Segment(pjoin, porigR0));		
		Point2D pp;
		for(int i=0; i < arm[0]; i++) {
			pp = editPoints.get(i);
			at1.transform(pp, pp);			
		}		

		// Rama1
		//porigR1 = porigR1!=null?porigR1:reac1.intersection( new Segment( pjoin, centerR1 ) );

		at1 = getEditPointTransform(new Segment(pjoin, porigR1));		
		for(int i=arm[0]; i < arm[0]+arm[1]; i++) {
			pp = editPoints.get(i);
			at1.transform(pp, pp);
		}	
		
		// Rama2
		//pdestPr = pdestPr!=null?pdestPr:prod.intersection( new Segment( pjoin, centerPr ) );
		at1 = getEditPointTransform(new Segment(pjoin, pdest));		
		for(int i=arm[0]+arm[1]; i < arm[0]+arm[1]+arm[2]; i++) {
			pp = editPoints.get(i);
			at1.transform(pp, pp);
		}	
		
		
		// Primeros y últimos de cada rama
		if (arm[0] != 0) {
			Point2D tunedPoint = 
				reac0.intersection( new Segment( editPoints.elementAt(arm[0]-1), porigR0 ) );
			if ( tunedPoint != null ) {
				porigR0 = tunedPoint;
			}
			
			this.lines.composeWith( new SVGLine(porigR0, 
									 editPoints.elementAt(arm[0]-1) ) );
			this.linesBackground.composeWith( 
					new SVGLine( 
							new Segment(editPoints.elementAt(arm[0]-1), porigR0).trim(10) 
							) 
					);
			
			this.lines.composeWith( new SVGLine(editPoints.elementAt(0) , pjoin) );
			this.linesBackground.composeWith( 
					new SVGLine( 
							new Segment(editPoints.elementAt(0) , pjoin)
							) 
					);
			
		} else {
			Point2D tunedPoint = 
				reac0.intersection( new Segment( pjoin, porigR0 ) );
			if ( tunedPoint != null ) {
				porigR0 = tunedPoint;
			}
			this.lines.composeWith( new SVGLine(porigR0, pjoin) );
			
			this.linesBackground.composeWith( 
					new SVGLine( new Segment(pjoin, porigR0).trim(10) ) );
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
			Point2D tunedPoint = 
				reac1.intersection( new Segment( editPoints.elementAt(arm[0]+arm[1]-1 ), porigR1 ) );
			if ( tunedPoint != null ) {
				porigR1 = tunedPoint;
			}
			this.lines.composeWith( new SVGLine(porigR1 , 
									             editPoints.elementAt(arm[0]+arm[1]-1) ) );
			
			this.linesBackground.composeWith( 
					new SVGLine( 
							new Segment(editPoints.elementAt(arm[0]+arm[1]-1), porigR1).trim(10) 
							) 
					);
			
			this.lines.composeWith( new SVGLine(editPoints.elementAt(arm[0]).getX(), editPoints.elementAt(arm[0]).getY() ,  
					 				 pjoin.getX(), pjoin.getY() ) );
					 				 
			this.linesBackground.composeWith( 
					new SVGLine( 
							new Segment(editPoints.elementAt(arm[0]) , pjoin)
							) 
					);			
		} else {
			Point2D tunedPoint = 
				reac1.intersection( new Segment( pjoin, porigR1 ) );
			if ( tunedPoint != null ) {
				porigR1 = tunedPoint;
			}
			
			this.lines.composeWith( new SVGLine(porigR1 , pjoin ) );
			
			this.linesBackground.composeWith( 
					new SVGLine( new Segment(pjoin, porigR1).trim(10) ) );			
		}
		
		for(int i=arm[0]; i < arm[0]+arm[1]-1; i++) {
			p1 = editPoints.elementAt(i);
			p2 = editPoints.elementAt(i+1);
			this.lines.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
			this.linesBackground.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
		}
		
		// Dibujar Rama2
		if (arm[2] != 0) {
			Point2D tunedPoint = 
				prod.intersection( new Segment( editPoints.elementAt(arm[0]+arm[1]+arm[2]-1), pdest ) );
			if ( tunedPoint != null ) {
				pdest = tunedPoint;
			}
			this.arrows.composeWith( new SVGArrow(editPoints.elementAt(arm[0]+arm[1]+arm[2]-1),									 
									 pdest,
									 false, 
									 SVGUtil.getHexColor( mr.getLine().getColor() ), 
									 mr.getLine().getWidth() ) );
			
			this.linesBackground.composeWith( 
					new SVGLine( 
							new Segment(editPoints.elementAt(arm[0]+arm[1]+arm[2]-1), pdest).trim(10) 
							) 
					);			
			
			this.lines.composeWith( new SVGLine(editPoints.elementAt(arm[0]+arm[1]).getX(), 
									 editPoints.elementAt(arm[0]+arm[1]).getY() ,  
					 				 pjoin.getX(), pjoin.getY() ) );
			this.linesBackground.composeWith( 
					new SVGLine( 
							new Segment(editPoints.elementAt(arm[0]+arm[1]), pjoin).trim(10) 
							) 
					);					
		} else {
			Point2D tunedPoint = 
				prod.intersection( new Segment( pjoin, pdest ) );
			if ( tunedPoint != null ) {
				pdest = tunedPoint;
			}
			this.arrows.composeWith( new SVGArrow(	pjoin, 
													pdest,
													false, 
												 	SVGUtil.getHexColor( mr.getLine().getColor() ), 
												 	mr.getLine().getWidth() 
												 ) );
			this.linesBackground.composeWith( 
					new SVGLine( new Segment(pjoin, pdest).trim(10) ) );				
		}
		
		
		for(int i=arm[0]+arm[1]; i < arm[0]+arm[1]+arm[2]-1; i++) {
			p1 = editPoints.elementAt(i);
			p2 = editPoints.elementAt(i+1);
			this.lines.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
			this.linesBackground.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
		}

		// Calcular posicion del rectangulo
		Point2D pAddR, pAddP;
		if (arm[2] == 0) {
			pAddR = pjoin;
			pAddP = pdest;
			
		} else {
			if (rectangleIndex == 0) {			
				// Primer segmento
				pAddR = pjoin;
				pAddP = editPoints.elementAt(arm[0]+arm[1]);				
			} else if (rectangleIndex == arm[2]) {
				// Ultimo segmento
				pAddR = editPoints.elementAt(arm[0]+arm[1]+arm[2]-1);
				pAddP = pdest;
			} else {
				pAddR = editPoints.elementAt(arm[0]+arm[1]+rectangleIndex-1);
				pAddP = editPoints.elementAt(arm[0]+arm[1]+rectangleIndex);						
			}	
		}
		
		//
		Point2D rectangleCenter = new Segment( pAddR, pAddP).getCenter();
		this.reactionCenter = rectangleCenter;
		getRectangleBetween(pAddR, pAddP);
		
		// Calcular posicion del Outcome, lo que realmente dibuja SBGN
		if (arm[2] == 0) {
			pAddR = pjoin;
			pAddP = pdest;			
		} else {
			pAddR = pjoin;
			pAddP = editPoints.elementAt(arm[0]+arm[1]);
		}
		
		squareShape =  getSBGNOutcome(pAddR, pAddP);

		// Los productos se añaden en SBGN ligeramente por delande del Outcome
		pAddP = new Segment(pAddR, pAddP).subSegment(30).getP2();
		
//		SVGSpecie svgSpc;
		for (MAddedSpeciesLink spc : mr.getReactantLinks()) {						
			buildAddedReactant(spc, pjoin, pjoin);
		}		
				
		for (MAddedSpeciesLink spc : mr.getProductLinks()) {				
			buildAddedProduct(spc, pAddP, pAddP);
		}	
		

					
		if (arm[1] != 0) {
			this.texts.composeWith( getReactionText( porigR1, 
										  editPoints.elementAt(arm[0]+arm[1]-1), 
										  mr.getReactants().get(0).getLinkAnchor() ) );			
		} else {
			this.texts.composeWith( getReactionText( porigR1, 
										  pjoin, 
										  mr.getReactants().get(0).getLinkAnchor() ) );			
		}
		
				
		
		lines.setAttribute("stroke", SVGUtil.getHexColor( mr.getLine().getColor() ) );
		lines.setAttribute("stroke-width", "" + mr.getLine().getWidth() );
		lines.setAttribute("stroke-linecap", "butt");
		lines.setAttribute("stroke-linejoin", "round");
		
		buildModifications();
		buildBooleanLogicGates();
		
		linesBackground.setAttribute("fill", "none");
		linesBackground.setAttribute("stroke", SVGConfig.reactionBackgroundColor);
		linesBackground.setAttribute("stroke-width", "" + (mr.getLine().getWidth() + 4.0) );
		
		shape = shape.composeWith( linesBackground );		
		shape = shape.composeWith( lines );				
		shape = shape.composeWith( arrows );
		shape = shape.composeWith( squareShape );		
		shape = shape.composeWith( texts );
		
		shape.setAttribute("id", mr.getId()); // shp.setAttribute("style", "text-rendering: auto;");
		
		if ( !SVGConfig.omitJavascript )	
			shape.setAttribute("onclick", "infoWindowReac(\"" + mr.getId() + "\"," +
					 "\"" + mr.getId() + "\","+
					 "\"" + "Heterodimer Association" + "\"," +
					 SVGScripts.javaArrayToJS( this.getReactantIDs() ) + "," +
					 SVGScripts.javaArrayToJS( this.getProductIDs() ) +
					 ");");
		shape.setAttribute("stroke", "black");
		shape.setAttribute("stroke-linecap", "round");
		
		// this.shape = complex;
		return this.shape;
	}

//	protected SVGShape buildSVGShapeSBGNOld() throws SBML2SVGException {
//		squareShape 	= new SVGComplexShape();
//		lines 			= new SVGComplexShape();
//		linesBackground = new SVGComplexShape();
//		arrows 			= new SVGComplexShape();
//		shape 			= new SVGComplexShape();
//		texts 			= new SVGComplexShape();		
//		
//		Point2D porigR0 = null;
//		Point2D porigR1 = null;
//		
//		String idr0 = mr.getReactants().get(0).getMs().getIdAlias();
//		String idr1 = mr.getReactants().get(1).getMs().getIdAlias();			
//		String idp = mr.getProducts().get(0).getMs().getIdAlias();
//		SVGSpecie reac0 = this.species.get(idr0);
//		SVGSpecie reac1 = this.species.get(idr1);
//		SVGSpecie prod = this.species.get(idp);
//		int[] arm = mr.getArms();
////		int rectangleIndex = mr.getTShapeIndex();
//		
//		// Check linAnchors
//		
//		if (mr.getReactants().get(0).getLinkAnchor() != null) {
//			porigR0 = reac0.getLinkAnchor( mr.getReactants().get(0).getLinkAnchor() );
//		}		
//
//		if (mr.getReactants().get(1).getLinkAnchor() != null) {
//			porigR1 = reac1.getLinkAnchor( mr.getReactants().get(1).getLinkAnchor() );
//		}
//		
//		if (mr.getProducts().get(0).getLinkAnchor() != null) {
//			pdest = prod.getLinkAnchor( mr.getProducts().get(0).getLinkAnchor() );
//		}
//		
//		
//		// Primero calcular punto de union, basado en los vectores R1->R0, y R1->P
//		Point2D centerR0 = reac0.getCenter();
//		Point2D centerR1 = reac1.getCenter();
//		Point2D centerPr = prod.getCenter();
//		Point2D epJoin = editPoints.lastElement();
//		
//		// ¡¡pjoin depende de los puntos centrales de los componentes, no de los puntos de unión!!
//		Point2D pjoin = new Point2D.Double(
//							(centerPr.getX() - centerR0.getX()) * epJoin.getY() + 
//							(centerR1.getX() - centerR0.getX()) * epJoin.getX() +
//							centerR0.getX(), 
//							(centerPr.getY() - centerR0.getY()) * epJoin.getY() + 
//							(centerR1.getY() - centerR0.getY()) * epJoin.getX() +
//							centerR0.getY()
//				);
//		
//		// Rama0
//		porigR0 = porigR0!=null?porigR0:reac0.getSVGShape().intersection( new Segment( pjoin, centerR0 ) );
//		
//		AffineTransform at1 = getEditPointTransform(new Segment(pjoin, porigR0));		
//		Point2D pp;
//		for(int i=0; i < arm[0]; i++) {
//			pp = editPoints.get(i);
//			at1.transform(pp, pp);			
//		}		
//
//		// Rama1
//		porigR1 = porigR1!=null?porigR1:reac1.getSVGShape().intersection( new Segment( pjoin, centerR1 ) );
//
//		at1 = getEditPointTransform(new Segment(pjoin, porigR1));		
//		for(int i=arm[0]; i < arm[0]+arm[1]; i++) {
//			pp = editPoints.get(i);
//			at1.transform(pp, pp);
//		}	
//		
//		// Rama2
//		pdest = pdest!=null?pdest:prod.getSVGShape().intersection( new Segment( pjoin, centerPr ) );
//		at1 = getEditPointTransform(new Segment(pjoin, pdest));		
//		for(int i=arm[0]+arm[1]; i < arm[0]+arm[1]+arm[2]; i++) {
//			pp = editPoints.get(i);
//			at1.transform(pp, pp);
//		}	
//		
//		// Primeros y últimos de cada rama
//		if (arm[0] != 0) {
//			this.lines.composeWith( new SVGLine(porigR0, 
//									 editPoints.elementAt(arm[0]-1) ) );
//			this.linesBackground.composeWith( 
//					new SVGLine( 
//							new Segment(editPoints.elementAt(arm[0]-1), porigR0).trim(10) 
//							) 
//					);
//			
//			this.lines.composeWith( new SVGLine(editPoints.elementAt(0) , pjoin) );
//			this.linesBackground.composeWith( 
//					new SVGLine( 
//							new Segment(editPoints.elementAt(0) , pjoin)
//							) 
//					);
//			
//		} else {
//			this.lines.composeWith( new SVGLine(porigR0, pjoin) );
//			
//			this.linesBackground.composeWith( 
//					new SVGLine( new Segment(pjoin, porigR0).trim(10) ) );
//		}
//		
//		Point2D p1, p2;
//		for(int i=0; i < arm[0]-1; i++) {
//			p1 = editPoints.elementAt(i);
//			p2 = editPoints.elementAt(i+1);
//			this.lines.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
//			this.linesBackground.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
//		}
//			
//
//		// Dibujar Rama1
//		if (arm[1] != 0) {
//			this.lines.composeWith( new SVGLine(porigR1.getX(), porigR1.getY(), 
//									 editPoints.elementAt(arm[0]+arm[1]-1).getX(), editPoints.elementAt(arm[0]+arm[1]-1).getY()) );
//			
//			this.linesBackground.composeWith( 
//					new SVGLine( 
//							new Segment(editPoints.elementAt(arm[0]+arm[1]-1), porigR1).trim(10) 
//							) 
//					);
//			
//			this.lines.composeWith( new SVGLine(editPoints.elementAt(arm[0]).getX(), editPoints.elementAt(arm[0]).getY() ,  
//					 				 pjoin.getX(), pjoin.getY() ) );
//					 				 
//			this.linesBackground.composeWith( 
//					new SVGLine( 
//							new Segment(editPoints.elementAt(arm[0]) , pjoin)
//							) 
//					);			
//		} else {
//			this.lines.composeWith( new SVGLine(porigR1.getX(), porigR1.getY(), pjoin.getX(), pjoin.getY()) );
//			
//			this.linesBackground.composeWith( 
//					new SVGLine( new Segment(pjoin, porigR1).trim(10) ) );			
//		}
//		
//		for(int i=arm[0]; i < arm[0]+arm[1]-1; i++) {
//			p1 = editPoints.elementAt(i);
//			p2 = editPoints.elementAt(i+1);
//			this.lines.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
//			this.linesBackground.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
//		}
//		
//		// Dibujar Rama2
//		if (arm[2] != 0) {
//			this.arrows.composeWith( new SVGArrow(editPoints.elementAt(arm[0]+arm[1]+arm[2]-1),									 
//									 pdest,
//									 false, 
//									 SVGUtil.getHexColor( mr.getLine().getColor() ), 
//									 mr.getLine().getWidth() ) );
//			
//			this.linesBackground.composeWith( 
//					new SVGLine( 
//							new Segment(editPoints.elementAt(arm[0]+arm[1]+arm[2]-1), pdest).trim(10) 
//							) 
//					);			
//			
//			this.lines.composeWith( new SVGLine(editPoints.elementAt(arm[0]+arm[1]).getX(), 
//									 editPoints.elementAt(arm[0]+arm[1]).getY() ,  
//					 				 pjoin.getX(), pjoin.getY() ) );
//			this.linesBackground.composeWith( 
//					new SVGLine( 
//							new Segment(editPoints.elementAt(arm[0]+arm[1]), pjoin).trim(10) 
//							) 
//					);					
//		} else {
//			this.arrows.composeWith( new SVGArrow(	pjoin, 
//													pdest,
//													false, 
//												 	SVGUtil.getHexColor( mr.getLine().getColor() ), 
//												 	mr.getLine().getWidth() 
//												 ) );
//			this.linesBackground.composeWith( 
//					new SVGLine( new Segment(pjoin, pdest).trim(10) ) );				
//		}
//		
//		for(int i=arm[0]+arm[1]; i < arm[0]+arm[1]+arm[2]-1; i++) {
//			p1 = editPoints.elementAt(i);
//			p2 = editPoints.elementAt(i+1);
//			this.lines.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
//			this.linesBackground.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
//		}
//
//		// Calculo de circulo (siempre en el primer segmento)
//		Point2D pAddR, pAddP;
//
//		
//		Point2D circleCenter = new Segment( pAddR, pAddP).scale(0.15).getP2();
////		SVGSpecie svgSpc;
//		for (MAddedSpeciesLink spc : mr.getReactantLinks()) {						
//			buildAddedReactant(spc, pAddR, circleCenter);
//		}					
//		for (MAddedSpeciesLink spc : mr.getProductLinks()) {				
//			buildAddedProduct(spc, pAddP, circleCenter);
//		}	
//		
//		squareShape =  getSBGNOutcome(pAddR, pAddR);
//		getRectangleBetween(pAddR ,pAddR); // Calcular cuadrado, ya que es usado al calcular editpoints 
//					
//		if (arm[1] != 0) {
//			this.texts.composeWith( getReactionText( porigR1, 
//										  editPoints.elementAt(arm[0]+arm[1]-1), 
//										  mr.getReactants().get(0).getLinkAnchor() ) );			
//		} else {
//			this.texts.composeWith( getReactionText( porigR1, 
//										  pjoin, 
//										  mr.getReactants().get(0).getLinkAnchor() ) );			
//		}
//				
//		
//		lines.setAttribute("stroke", SVGUtil.getHexColor( mr.getLine().getColor() ) );
//		lines.setAttribute("stroke-width", "" + mr.getLine().getWidth() );
//		lines.setAttribute("stroke-linecap", "butt");
//		lines.setAttribute("stroke-linejoin", "round");
//		
//		buildModifications();
//		buildBooleanLogicGates();
//		
//		linesBackground.setAttribute("fill", "none");
//		linesBackground.setAttribute("stroke", SVGConfig.reactionBackgroundColor);
//		linesBackground.setAttribute("stroke-width", "" + (mr.getLine().getWidth() + 4.0) );
//		
//		shape = shape.composeWith( linesBackground );		
//		shape = shape.composeWith( lines );				
//		shape = shape.composeWith( arrows );
//		shape = shape.composeWith( squareShape );		
//		shape = shape.composeWith( texts );
//		
//		shape.setAttribute("id", mr.getId()); // shp.setAttribute("style", "text-rendering: auto;");
//		
//		if ( !SVGConfig.omitJavascript )	
//			shape.setAttribute("onclick", "infoWindowReac(\"" + mr.getId() + "\"," +
//					 "\"" + mr.getId() + "\","+
//					 "\"" + "Heterodimer Association" + "\"," +
//					 SVGScripts.javaArrayToJS( this.getReactantIDs() ) + "," +
//					 SVGScripts.javaArrayToJS( this.getProductIDs() ) +
//					 ");");
//		shape.setAttribute("stroke", "black");
//		shape.setAttribute("stroke-linecap", "round");
//		
//		// this.shape = complex;
//		return this.shape;
//	}
//	
	protected SVGShape getRectangleBetween(Point2D p1, Point2D p2){
		if ( !showsSquare() ) {
			// Calcular el cuadrado, pero no devolverlo, para que
			// no se dibuje
			getRectangleBetween(p1, p2, "");
			return null;
			
		} else {
			return getRectangleBetween(p1, p2, "");
		}
	}

	/**
	 * Obtiene el punto de la reaccion al que los modificadores
	 * se enganchan
	 */
	public Point2D getAnchorPoint(String targetLineIndex) {
		if ( SVGConfig.SBGNMode || this.square==null ) {
			return reactionCenter;
		} else {
			return this.square.getAnchorPoint( targetLineIndex );
		}
	}

	@Override
	protected double getCenterReactionMargin() {
		if ( !showsSquare() ){
			// Dar margen a los circulos que marcan la disociación
			return 5;
		} else {
			// En el caso normal, no es necesario dar margen extra
			// al existir el cuadrado de la reacción
			return 0;
		}
	}

	@Override
	protected boolean showsSquare() {
		if (SVGConfig.SBGNMode || !this.isTransformEditPoints() ) {
			return false;
		} else {
			return true;
		}
	}
}
