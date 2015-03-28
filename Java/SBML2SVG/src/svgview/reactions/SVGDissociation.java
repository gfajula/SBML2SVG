package svgview.reactions;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Map;

import model.reaction.MDissociation;
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
import svgview.util.SVGUtil;

public class SVGDissociation extends SVGGenericReaction {
	MDissociation mdis;

	public SVGDissociation(Document svgDoc, MDissociation mdis,
			Map<String, SVGSpecie> species) {
		super(svgDoc, mdis, species);
		this.mdis = mdis;
		this.mr = this.mdis;
	}
	
	/**
	 * Dibuja una Disociación sin tener en cuenta el formato específico
	 * de CD4: Un reactivo, con opcionalmente reactivos añadidos, y los
	 * productos que parten del mismo punto. Los editPoints, sin transformar
	 * se pintan normalmente. Se opta por la notacion SBGN
	 * 
	 * @return
	 * @throws SBML2SVGException
	 */
	protected SVGShape buildSVGShapeUntransformed() throws SBML2SVGException {
		SVGShape shape = super.buildSVGShape();
		
		/*
		 * Los AddedSpeciesLink deben compartir un mismo punto de "joint"
		 */
		
		return shape;
//		squareShape 	= new SVGComplexShape();
//		lines 			= new SVGComplexShape();
//		linesBackground = new SVGComplexShape();
//		arrows 			= new SVGComplexShape();
//		shape 			= new SVGComplexShape();
//		texts 			= new SVGComplexShape();
//		
//		if ( this.mr.getId().equals("dimerisation") ) {
//			System.out.println("Debug");
//		}
//		
//		
//		String idr1 = mr.getReactants().get(0).getMs().getIdAlias();
//		String idp1 = mr.getProducts().get(0).getMs().getIdAlias();	
//				
//		if (mr.getReactants().size() > 1)
//			System.err.println(mr.getId() + " mas de un reac.");
//		if (mr.getProducts().size() > 1)
//			System.err.println(mr.getId() + " mas de un prod.");
//		
//		spr = species.get(idr1);		
//		spp = species.get(idp1);		
//				
//		// Segmento de union entre los species
//		Segment s0 = new Segment( spr.getCenter(), spp.getCenter() );
//		// Check linAnchors		
//		if (mr.getReactants().get(0).getLinkAnchor() != null) {
//			porig = spr.getLinkAnchor( mr.getReactants().get(0).getLinkAnchor() );
//		} else {
//			porig = spr.getSVGShape().intersection(s0.getInverted());
//			if ( porig == null ) {
//				porig = spr.getCenter();
//			}
//		}
//		
//		if (mr.getProducts().get(0).getLinkAnchor() != null) {
//			pdest = spp.getLinkAnchor( mr.getProducts().get(0).getLinkAnchor() );
//		} else {
//			pdest = spp.getSVGShape().intersection(s0);
//			if ( pdest == null ) {
//				pdest = spp.getCenter();
//			}
//			//pdest = spp.getCenter();
//		}
//		
//		Segment s = new Segment( porig, pdest );	
//		
//		Vector<Point2D> editPoints = mr.getEditPoints();	
//		
//		
//		if ((editPoints==null) || (editPoints.size()==0)) {
//			// Caso sin EditPoints. 
//			// El segmento entre reactivo y producto es directo, entre
//			// linkAnchors, o los bordes, en dirección a los centros de los species.						
//			if ((pdest==null)||(porig==null)){
//				System.err.println(mr.getId());
//				return null;
//			}		
//			
//			try {
//				arrows.composeWith( getArrow(porig, pdest, mr.isReversible() ) );	// Si reversible, pintar doble punta			
//				linesBackground.composeWith( 
//						new SVGLine( 
//								new Segment(pdest, porig).trim(8).getInverted().trim(10) 
//								) 
//						);
//				squareShape.composeWith( getRectangleBetween(porig, pdest) );
//				
//			} catch (NullPointerException e) {
//				System.err.println(spr.getClass() + "/" + spp.getClass());
//				e.printStackTrace();			
//			}
//			
//			Point2D pcenter = new Segment(porig, pdest).getCenter();
//			if ( mr.getReactantLinks() != null)
//				for (MAddedSpeciesLink spc : mr.getReactantLinks()) {												
//					buildAddedReactant( spc, porig, pcenter);				
//				}
//			if ( mr.getProductLinks() != null)
//				for (MAddedSpeciesLink spc : mr.getProductLinks()) {												
//					buildAddedProduct( spc, pdest, pcenter );				
//				}			
//		
//		} else {
//			// Caso con EditPoints.
//			// El segmento base sobre el que se calculan los editPoints es el formado entre
//			// los centros de los especies, o los linkAnchors si los hay.
//			// El principio y el fin de la línea que dibuja la reacción se calcula en base
//			// a los segmentos primero y último, intersecados con los bordes de los species
//			// corresponddientes
//			
//			int rectangleIndex = mr.getRectangleIndex() - 1;
//
//			// Reubicar puntos inicial y final, teniendo en cuenta los editpoints
//			// para calcular puntos de intersección		
//			if (mr.getProducts().get(0).getLinkAnchor() == null) {
//				Point2D intersection = spp.intersection(
//								new Segment( editPoints.lastElement() , spp.getCenter() )
//							);
//				
//				
//				if ( intersection != null) {
//					pdest = intersection;
//				
//					// Ajuste para layout Ortogonal
//					if ( SVGConfig.layout_type == Layouter.LAYOUT_ORTHOGONAL &&
//						 !this.mr.isTransformEditPoints()	) {					
//						pdest = orthogonalizeSegment(
//								new Segment( editPoints.lastElement() , intersection ), 
//								spp.getBBox() ).getP2();
//					}
//					
//				} else {
//					pdest = spp.getCenter();
//				}
//				
//			}
//			
//			if (mr.getReactants().get(0).getLinkAnchor() == null) {
//				Point2D intersection = spr.intersection(
//								new Segment( editPoints.firstElement() , spr.getCenter() )
//							);
//				if ( intersection != null) {
//					porig = intersection;
//					// Ajuste para layout Ortogonal
//					if ( SVGConfig.layout_type == Layouter.LAYOUT_ORTHOGONAL &&
//						 !this.mr.isTransformEditPoints()	) {					
//						porig = orthogonalizeSegment(
//								new Segment( editPoints.firstElement() , intersection ), 
//								spr.getBBox() ).getP2();
//					}
//					
//				} else { 
//					porig = spr.getCenter();
//				}
//			}
//			
//			// Dibujar segmentos
//			Segment firstSegment = new Segment( editPoints.firstElement(), porig );
//			Point2D p1, p2;
//			// Primero			
//			if ( mr.isReversible() ) {
//				arrows.composeWith( getArrow( editPoints.firstElement(), porig ) );
//			} else {
//				lines.composeWith( new SVGLine( firstSegment ) );				
//			}
//			linesBackground.composeWith( new SVGLine( firstSegment.trim(10) ) );
//			
//			if (rectangleIndex < 0) {
//				
//				Point2D pcenter = new Segment( porig, editPoints.firstElement() ).getCenter();
//				
//				for (MAddedSpeciesLink spc : mr.getReactantLinks()) {												
//					buildAddedReactant( spc, porig, pcenter);					
//				}						
//				for (MAddedSpeciesLink spc : mr.getProductLinks()) {												
//					buildAddedProduct( spc, editPoints.firstElement(), pcenter );					
//				}
//				
//				squareShape.composeWith( getRectangleBetween( porig, editPoints.firstElement() ) );
//			} 
//						
//			// Intermedios	
//			for(int i=0; i<editPoints.size()-1; i++) {
//				p1 = editPoints.elementAt(i);
//				p2 = editPoints.elementAt(i+1);
//				Segment segment =  new Segment(p1,p2);
//				lines.composeWith( new SVGLine( segment ) );
//				linesBackground.composeWith( new SVGLine( segment ) );
//				
//				if (rectangleIndex == i) {					
//					squareShape.composeWith( getRectangleBetween(p1, p2) );
//					Point2D pcenter = segment.getCenter();
//					
//					for (MAddedSpeciesLink spc : mr.getReactantLinks()) {												
//						buildAddedReactant( spc, p1, pcenter);
//					}						
//					for (MAddedSpeciesLink spc : mr.getProductLinks()) {							
//						buildAddedProduct( spc, p2, pcenter );						
//					}					
//				}	
//				
//				// Debug
//				lines.composeWith( 
//					getDebugEditPoints(p1, "#00ff00" )
//				);				
//			}		
//			
//			lines.composeWith( 
//					getDebugEditPoints(editPoints.lastElement() , "#00ffff" )
//				);
//			
//			// Ultimo
//			arrows.composeWith( getArrow(editPoints.lastElement(), pdest));
//			linesBackground.composeWith( 
//					new SVGLine( 
//							new Segment(editPoints.lastElement(), pdest).trim(10) 
//							) 
//					);
//			if (rectangleIndex >= editPoints.size()-1 ) {
//				 		
//				Point2D pcenter = new Segment( editPoints.lastElement(), pdest ).getCenter();
//						
//				for (MAddedSpeciesLink spc : mr.getReactantLinks()) {												
//					buildAddedReactant( spc, editPoints.lastElement(), pcenter);
//					
//				}						
//				for (MAddedSpeciesLink spc : mr.getProductLinks()) {												
//					buildAddedProduct( spc, pdest, pcenter );					
//				}
//				
//				squareShape.composeWith( getRectangleBetween( editPoints.lastElement(), pdest) );		
//			}			
//		}		
//		
//		
//		lines.setAttribute("stroke", SVGUtil.getHexColor( mr.getLine().getColor() ) );
//		lines.setAttribute("stroke-width", "" + mr.getLine().getWidth() );
//		lines.setAttribute("stroke-linecap", "butt");
//		lines.setAttribute("stroke-linejoin", "round");
////		
//		arrows.setAttribute("stroke", SVGUtil.getHexColor( mr.getLine().getColor() ) );
//		arrows.setAttribute("stroke-width", "" + mr.getLine().getWidth() );
//		
//		buildModifications();
//		buildBooleanLogicGates();
//		this.texts.composeWith( getReactionText() );		
//		
//		linesBackground.setAttribute("fill", "none");
//		linesBackground.setAttribute("stroke", SVGConfig.reactionBackgroundColor );
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
//		shape.setAttribute("stroke", "black");
//		shape.setAttribute("stroke-linecap", "round");
//		
//		String dashArray = this.getDashArray();
//		if (dashArray!=null) {
//			shape.setAttribute("stroke-dasharray", dashArray);
//		}
//		
//		addOnClickEvent();		
//		
//		return shape;
	}
	
	protected SVGShape buildSVGShape() throws SBML2SVGException {
		if (SVGConfig.SBGNMode) return buildSVGShapeSBGN();
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
		int[] arm = mdis.getArms();
		int rectangleIndex = mdis.getTShapeIndex();
				
		// Primero calcular punto de union, basado en los vectores R->P0, y R->P1
		Point2D centerR = reac.getCenter();
		Point2D centerP0 = prod0.getCenter();
		Point2D centerP1 = prod1.getCenter();
		Point2D epJoin = editPoints.lastElement();
		
		// Punto donde confluyen las ramas
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
		
		// Para luego dibujar mejor el circulo, se mantiene referencia al 
		// punto anterior al 'pjoin' 
		Point2D preJoin;

		// Rama0
		Point2D porigR = reac.getSVGShape().intersection( new Segment( pjoin, centerR ) );
		if (mdis.getReactants().get(0).getLinkAnchor() != null) {
			porigR = reac.getLinkAnchor( mdis.getReactants().get(0).getLinkAnchor() );
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
		if (mdis.getProducts().get(0).getLinkAnchor() != null) {
			pdestP0 = prod0.getLinkAnchor( mdis.getProducts().get(0).getLinkAnchor() );
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
		if (mdis.getProducts().get(1).getLinkAnchor() != null) {
			pdestP1 = prod1.getLinkAnchor( mdis.getProducts().get(1).getLinkAnchor() );
		} else {
			pdestP1 = prod1.getSVGShape().intersection( new Segment( pjoin, centerP1 ) );
		}
		
		at1 = getEditPointTransform(new Segment(pjoin, pdestP1));		
		for(int i=arm[0]+arm[1]; i < arm[0]+arm[1]+arm[2]; i++) {
			pp = editPoints.get(i);
			at1.transform(pp, pp);
//			complex.add( getEditPoints(pp, "blue")  );
		}	
		
		// Primeros y últimos de cada rama
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
					);
			preJoin = editPoints.elementAt(0);
		} else {
			this.lines.composeWith( new SVGLine(porigR.getX(), porigR.getY(), pjoin.getX(), pjoin.getY()) );
			this.linesBackground.composeWith( 
					new SVGLine( new Segment(pjoin, porigR).trim(10) ) 
					);
			preJoin = porigR;
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
			this.arrows.composeWith( new SVGArrow(editPoints.elementAt(arm[0]+arm[1]-1).getX(), editPoints.elementAt(arm[0]+arm[1]-1).getY(),
									 pdestP0.getX(), pdestP0.getY() ) );
			
			this.linesBackground.composeWith( 
					new SVGLine( new Segment(editPoints.elementAt(arm[0]+arm[1]-1), pdestP0).trim(10) ) 
					) ;			
			this.lines.composeWith( new SVGLine(pjoin.getX(), pjoin.getY(), 
									 editPoints.elementAt(arm[0]).getX(), editPoints.elementAt(arm[0]).getY() ) );
			this.linesBackground.composeWith( 
					new SVGLine( new Segment(editPoints.elementAt(arm[0]), pjoin) ) 
					);			
		} else {
			this.arrows.composeWith( new SVGArrow(  pjoin, pdestP0,
													false, 
												 	SVGUtil.getHexColor( mr.getLine().getColor() ), 
												 	mdis.getLine().getWidth() ) );
			this.linesBackground.composeWith( 
					new SVGLine( new Segment(pjoin, pdestP0).trim(10) ) 
					);			
		}
		
		for(int i=arm[0]; i < arm[0]+arm[1]-1; i++) {
			p1 = editPoints.elementAt(i);
			p2 = editPoints.elementAt(i+1);
			this.lines.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
			this.linesBackground.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
		}
		
		// Dibujar Rama2
		if (arm[2] != 0) {
			this.arrows.composeWith( new SVGArrow(editPoints.elementAt(arm[0]+arm[1]+arm[2]-1).getX(), 
									 editPoints.elementAt(arm[0]+arm[1]+arm[2]-1).getY(),
									 pdestP1.getX(), pdestP1.getY()) );
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
			this.arrows.composeWith( new SVGArrow( 	pjoin, pdestP1, 
													false, 
												 	SVGUtil.getHexColor( mr.getLine().getColor() ), 
												 	mdis.getLine().getWidth() ) );
			this.linesBackground.composeWith( 
					new SVGLine( new Segment(pjoin, pdestP1).trim(10) ) 
					);	
		}
		
		for(int i=arm[0]+arm[1]; i < arm[0]+arm[1]+arm[2]-1; i++) {
			p1 = editPoints.elementAt(i);
			p2 = editPoints.elementAt(i+1);
			this.lines.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
			this.linesBackground.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
		}

		Point2D pAddR, pAddP;
				
		if (arm[0] == 0) {			
			pAddR = porigR;
			pAddP = pjoin;
			
		} else {
			if (rectangleIndex == 0) {			
				// Primer segmento
				pAddR = editPoints.elementAt(0);
				pAddP = pjoin;				
			} else if (rectangleIndex == arm[0]) {
				// Ultimo segmento
				pAddR = porigR;
				pAddP = editPoints.elementAt(arm[0]-1);				
			} else {
				pAddP = editPoints.elementAt(rectangleIndex-1);
				pAddR = editPoints.elementAt(rectangleIndex);						
			}	
		}
		
		Point2D rectangleCenter = new Segment( pAddR, pAddP).getCenter();

		for (MAddedSpeciesLink spc : mdis.getReactantLinks()) {						
			buildAddedReactant(spc, pAddR, rectangleCenter);
		}
		

		for (MAddedSpeciesLink spc : mdis.getProductLinks()) {
			if ( SVGConfig.SBGNMode ) {
				buildAddedProduct(spc, pjoin, pjoin);
			} else {
				buildAddedProduct(spc, pAddP, rectangleCenter);
			}
		}	
				
		// squareShape = getRectangleBetween(pAddR, pAddP);
		squareShape.composeWith(getRectangleBetween(pAddR, pAddP));

		Point2D circlesCenter;
		if ( SVGConfig.SBGNMode ) {
			circlesCenter = new Segment( preJoin, pjoin).trim( 10 ).getP2();
			this.reactionCenter = circlesCenter;
		} else {
			circlesCenter = pjoin;
		}
		
		SVGShape circle = new SVGCircle(circlesCenter.getX(), circlesCenter.getY(), 5.5).composeWith(
										new SVGCircle(circlesCenter.getX(), circlesCenter.getY(), 3));
		
		circle.setAttribute("fill", "white");
		//this.lines.composeWith( circle );
		
		if (arm[0] != 0) {
			this.texts.composeWith( getReactionText( porigR, 
										  editPoints.elementAt(arm[0]-1), 
										  mdis.getReactants().get(0).getLinkAnchor() ) );			
		} else {
			this.texts.composeWith( getReactionText( porigR, 
										  pjoin, 
										  mdis.getReactants().get(0).getLinkAnchor() ) );			
		}
		
		
		lines.setAttribute("stroke", SVGUtil.getHexColor( mr.getLine().getColor() ) );
		lines.setAttribute("stroke-linecap", "square");
		lines.setAttribute("stroke-linejoin", "round");
		lines.setAttribute("stroke-width", "" + mr.getLine().getWidth() );
		
		buildModifications();
		buildBooleanLogicGates();
		
		linesBackground.setAttribute("fill", "none");
		linesBackground.setAttribute("stroke", SVGConfig.reactionBackgroundColor);
		linesBackground.setAttribute("stroke-width", "" + (mr.getLine().getWidth() + 4.0) );
		
		this.shape.composeWith( linesBackground );		
		this.shape.composeWith( lines );				
		this.shape.composeWith( arrows );
		this.shape.composeWith( circle );
		this.shape.composeWith( squareShape );		
		this.shape.composeWith( texts );
				
		this.shape.setAttribute("id", mr.getId()); // shp.setAttribute("style", "text-rendering: auto;");		

		this.shape.setAttribute("stroke", "black");
		this.shape.setAttribute("stroke-linecap", "round");

		
		addOnClickEvent();		
		
		return this.shape;
	}

	protected SVGShape buildSVGShapeSBGN() throws SBML2SVGException {
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
		int[] arm = mdis.getArms();
		int rectangleIndex = mdis.getTShapeIndex();
				
		// Primero calcular punto de union, basado en los vectores R->P0, y R->P1
		Point2D centerR = reac.getCenter();
		Point2D centerP0 = prod0.getCenter();
		Point2D centerP1 = prod1.getCenter();
		Point2D epJoin = editPoints.lastElement();
		
		// Punto donde confluyen las ramas
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
		Point2D porigR = reac.getSVGShape().intersection( new Segment( pjoin, centerR ) );
		if (mdis.getReactants().get(0).getLinkAnchor() != null) {
			porigR = reac.getLinkAnchor( mdis.getReactants().get(0).getLinkAnchor() );
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
		if (mdis.getProducts().get(0).getLinkAnchor() != null) {
			pdestP0 = prod0.getLinkAnchor( mdis.getProducts().get(0).getLinkAnchor() );
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
		if (mdis.getProducts().get(1).getLinkAnchor() != null) {
			pdestP1 = prod1.getLinkAnchor( mdis.getProducts().get(1).getLinkAnchor() );
		} else {
			pdestP1 = prod1.getSVGShape().intersection( new Segment( pjoin, centerP1 ) );
		}
		
		at1 = getEditPointTransform(new Segment(pjoin, pdestP1));		
		for(int i=arm[0]+arm[1]; i < arm[0]+arm[1]+arm[2]; i++) {
			pp = editPoints.get(i);
			at1.transform(pp, pp);
//			complex.add( getEditPoints(pp, "blue")  );
		}	
		
		// Primeros y últimos de cada rama
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
					);
		} else {
			this.lines.composeWith( new SVGLine(porigR.getX(), porigR.getY(), pjoin.getX(), pjoin.getY()) );
			this.linesBackground.composeWith( 
					new SVGLine( new Segment(pjoin, porigR).trim(10) ) 
					);
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
			this.arrows.composeWith( new SVGArrow(editPoints.elementAt(arm[0]+arm[1]-1).getX(), editPoints.elementAt(arm[0]+arm[1]-1).getY(),
									 pdestP0.getX(), pdestP0.getY() ) );
			
			this.linesBackground.composeWith( 
					new SVGLine( new Segment(editPoints.elementAt(arm[0]+arm[1]-1), pdestP0).trim(10) ) 
					) ;			
			this.lines.composeWith( new SVGLine(pjoin.getX(), pjoin.getY(), 
									 editPoints.elementAt(arm[0]).getX(), editPoints.elementAt(arm[0]).getY() ) );
			this.linesBackground.composeWith( 
					new SVGLine( new Segment(editPoints.elementAt(arm[0]), pjoin) ) 
					);			
		} else {
			this.arrows.composeWith( new SVGArrow(  pjoin, pdestP0,
													false, 
												 	SVGUtil.getHexColor( mr.getLine().getColor() ), 
												 	mdis.getLine().getWidth() ) );
			this.linesBackground.composeWith( 
					new SVGLine( new Segment(pjoin, pdestP0).trim(10) ) 
					);			
		}
		
		for(int i=arm[0]; i < arm[0]+arm[1]-1; i++) {
			p1 = editPoints.elementAt(i);
			p2 = editPoints.elementAt(i+1);
			this.lines.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
			this.linesBackground.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
		}
		
		// Dibujar Rama2
		if (arm[2] != 0) {
			this.arrows.composeWith( new SVGArrow(editPoints.elementAt(arm[0]+arm[1]+arm[2]-1).getX(), 
									 editPoints.elementAt(arm[0]+arm[1]+arm[2]-1).getY(),
									 pdestP1.getX(), pdestP1.getY()) );
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
			this.arrows.composeWith( new SVGArrow( 	pjoin, pdestP1, 
													false, 
												 	SVGUtil.getHexColor( mr.getLine().getColor() ), 
												 	mdis.getLine().getWidth() ) );
			this.linesBackground.composeWith( 
					new SVGLine( new Segment(pjoin, pdestP1).trim(10) ) 
					);	
		}
		
		for(int i=arm[0]+arm[1]; i < arm[0]+arm[1]+arm[2]-1; i++) {
			p1 = editPoints.elementAt(i);
			p2 = editPoints.elementAt(i+1);
			this.lines.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
			this.linesBackground.composeWith( new SVGLine(p1.getX(), p1.getY(), p2.getX(), p2.getY()) );
		}

		Point2D pAddR, pAddP;
		
		if (arm[0] == 0) {			
			pAddR = porigR;
			pAddP = pjoin;
			
		} else {
			if (rectangleIndex == 0) {			
				// Primer segmento
				pAddR = editPoints.elementAt(0);
				pAddP = pjoin;				
			} else if (rectangleIndex == arm[0]) {
				// Ultimo segmento
				pAddR = porigR;
				pAddP = editPoints.elementAt(arm[0]-1);				
			} else {
				pAddP = editPoints.elementAt(rectangleIndex-1);
				pAddR = editPoints.elementAt(rectangleIndex);						
			}	
		}
		
		getRectangleBetween(pAddR, pAddP);
		
		// Calcular posicion del punto, lo que realmente dibuja SBGN
		// en base a la rama 0, pAddP dara el origen base, y pddR la direccion
		// del segmento
		if (arm[0] == 0) {
			pAddR = porigR;
			pAddP = pjoin;			
		} else {
			pAddR = editPoints.elementAt(0);;
			pAddP = pjoin;
		}
						
		pAddR = new Segment(pAddP,pAddR).subSegment(30).getP2();
		
		for (MAddedSpeciesLink spc : mdis.getReactantLinks()) {						
			buildAddedReactant(spc, pAddR, pAddR);
		}		

		for (MAddedSpeciesLink spc : mdis.getProductLinks()) {		
			buildAddedProduct(spc, pjoin, pjoin);			
		}	
				
		Point2D circlesCenter;
		circlesCenter = new Segment( pAddP, pAddR).subSegment(15).getP2();
		this.reactionCenter = circlesCenter;
		
		squareShape = new SVGCircle(circlesCenter.getX(), circlesCenter.getY(), 5.5).composeWith(
										new SVGCircle(circlesCenter.getX(), circlesCenter.getY(), 3));
		
		squareShape.setAttribute("fill", "white");
		//this.lines.composeWith( circle );
		
		if (arm[0] != 0) {
			this.texts.composeWith( getReactionText( porigR, 
										  editPoints.elementAt(arm[0]-1), 
										  mdis.getReactants().get(0).getLinkAnchor() ) );			
		} else {
			this.texts.composeWith( getReactionText( porigR, 
										  pjoin, 
										  mdis.getReactants().get(0).getLinkAnchor() ) );			
		}
		
		
		lines.setAttribute("stroke", SVGUtil.getHexColor( mr.getLine().getColor() ) );
		lines.setAttribute("stroke-linecap", "square");
		lines.setAttribute("stroke-linejoin", "round");
		lines.setAttribute("stroke-width", "" + mr.getLine().getWidth() );
		
		buildModifications();
		buildBooleanLogicGates();
		
		linesBackground.setAttribute("fill", "none");
		linesBackground.setAttribute("stroke", SVGConfig.reactionBackgroundColor);
		linesBackground.setAttribute("stroke-width", "" + (mr.getLine().getWidth() + 4.0) );
		
		this.shape.composeWith( linesBackground );		
		this.shape.composeWith( lines );				
		this.shape.composeWith( arrows );
		this.shape.composeWith( squareShape );		
		this.shape.composeWith( texts );
				
		this.shape.setAttribute("id", mr.getId()); // shp.setAttribute("style", "text-rendering: auto;");		
		this.shape.setAttribute("stroke", "black");
		this.shape.setAttribute("stroke-linecap", "round");

		
		
		addOnClickEvent();		
		
		return this.shape;
	}

	
	protected SVGShape getRectangleBetween(Point2D p1, Point2D p2){
		if ( SVGConfig.SBGNMode ) {
			// Calcular el cuadrado, pero no devolverlo, para que
			// no se dibuje
			getRectangleBetween(p1, p2, "");
			return null;
			
		} else {
			return getRectangleBetween(p1, p2, "");
		}
		
	}
	
	public Point2D getAnchorPoint(String targetLineIndex) {
		return reactionCenter;
//		Point2D anchor = reactionCenter;
//		int linkAnchor =		
//			Integer.parseInt( targetLineIndex.split(",")[1] );		
//		
//		if ( this.editPoints.size() == 0 ) {
//			// Si no hay editpoints, devolver punto central
//			return reactionCenter;
//		} else {
//			Point2D p1 = this.porig;
//			Point2D p2 = this.editPoints.firstElement();
//			if (linkAnchor == 0) {
//				anchor = new Point2D.Double( 
//						(p1.getX()+
//								p2.getX())/2, 
//						(p1.getY()+
//								p2.getY())/2 );
//			} else if (linkAnchor >= this.editPoints.size() ) {
//				p1 = this.editPoints.lastElement();
//				p2 = this.pdest;
//				anchor = new Point2D.Double( 
//						(p1.getX()+
//								p2.getX())/2, 
//						(p1.getY()+
//								p2.getY())/2 );
//			} else {
//				p1 = this.editPoints.elementAt( linkAnchor - 1);
//				p1 = this.editPoints.elementAt( linkAnchor );
//				anchor = new Point2D.Double( (p1.getX()+p2.getX())/2, (p1.getY()+p2.getY())/2 );
//			}
//		}
//
//		return anchor;
	}
	
	@Override
	protected double getCenterReactionMargin() {
		if ( SVGConfig.SBGNMode ){
			// Dar margen a los circulos que marcan la disociación
			return 6;
		} else {
			// En el caso normal, no es necesario dar margen extra
			// al existir el cuadrado de la reacción
			return 0;
		}
	}

	@Override
	protected boolean showsSquare() {
		if (SVGConfig.SBGNMode || !this.isTransformEditPoints()) {
			return false;
		} else {
			return true;
		}
	}
	
	
}
