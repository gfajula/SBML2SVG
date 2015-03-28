package svgview.reactions;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.Vector;

import model.layout.Layouter;
import model.reaction.MBooleanLogicGate;
import model.reaction.MModification;
import model.reaction.MReaction;
import model.specie.MAddedSpeciesLink;
import model.specie.MSpeciesLink;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import svgcontroller.SBML2SVGException;
import svgview.SVGConfig;
import svgview.shapes.SVGArrow;
import svgview.shapes.SVGCircle;
import svgview.shapes.SVGComplexShape;
import svgview.shapes.SVGCustomShape;
import svgview.shapes.SVGLine;
import svgview.shapes.SVGPolygon;
import svgview.shapes.SVGReactionSquare;
import svgview.shapes.SVGShape;
import svgview.shapes.SVGText;
import svgview.shapes.Segment;
import svgview.species.SVGSpecie;
import svgview.util.SVGScripts;
import svgview.util.SVGTextRenderer;
import svgview.util.SVGUtil;
import celldesignerparse_4_0.reaction.EditPoint;

public abstract class SVGGenericReaction implements SVGReaction {
	protected SVGShape shape;
	protected MReaction mr;
	protected Document svgDoc;
	protected Map<String, SVGSpecie> species;
	protected Point2D porig;
	protected Point2D pdest;
	protected Point2D reactionCenter;
	protected Vector<Point2D> editPoints;
	protected SVGReactionSquare square;
	protected SVGSpecie spr;
	protected SVGSpecie spp;
	
	protected SVGShape squareShape = new SVGComplexShape();; 
	protected SVGShape lines = new SVGComplexShape();
	protected SVGShape linesBackground = new SVGComplexShape();
	protected SVGShape arrows = new SVGComplexShape();
	protected SVGShape texts = new SVGComplexShape();
	
	protected String styleColor;
	protected String styleLineWidth;
			
	public SVGGenericReaction(Document svgDoc, MReaction mr, Map<String, SVGSpecie> species){
		this.svgDoc = svgDoc;
		this.mr = mr;
		this.species = species;
		this.editPoints = mr.getEditPointsCopy();
	}
	
	@Override
	public void svgPaint(Element docParent) {
		SVGShape s = getSVGShape();
		if (s!=null) s.svgPaint(docParent);
	}
	
	public SVGShape getSVGShape() {		
		if (shape==null) {
			try {
				return buildSVGShape();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return shape;
	}
	
	protected SVGShape buildSVGShape() throws SBML2SVGException {
		squareShape 	= new SVGComplexShape();
		lines 			= new SVGComplexShape();
		linesBackground = new SVGComplexShape();
		arrows 			= new SVGComplexShape();
		shape 			= new SVGComplexShape();
		texts 			= new SVGComplexShape();
				
		String idr1 = mr.getReactants().get(0).getMs().getIdAlias();
		String idp1 = mr.getProducts().get(0).getMs().getIdAlias();	
				
		if (mr.getReactants().size() > 1)
			System.err.println(mr.getId() + " mas de un reac.");
		if (mr.getProducts().size() > 1)
			System.err.println(mr.getId() + " mas de un prod.");
		
		spr = species.get(idr1);		
		spp = species.get(idp1);		
				
		// Segmento de union entre los species
		Segment s0 = new Segment( spr.getCenter(), spp.getCenter() );
		// Check linAnchors		
		if ( (mr.getReactants().get(0).getLinkAnchor() != null) &&
		     (!mr.getReactants().get(0).getLinkAnchor().equals("INACTIVE") ) ){
			porig = spr.getLinkAnchor( mr.getReactants().get(0).getLinkAnchor() );
		} else {
			porig = spr.getSVGShape().intersection(s0.getInverted());
			if ( porig == null ) {
				porig = spr.getCenter();
			}
		}
		
		if ( (mr.getProducts().get(0).getLinkAnchor() != null) &&
			 (!mr.getProducts().get(0).getLinkAnchor().equals("INACTIVE") ) ) {
			pdest = spp.getLinkAnchor( mr.getProducts().get(0).getLinkAnchor() );
		} else {
			pdest = spp.getSVGShape().intersection(s0);
			if ( pdest == null ) {
				pdest = spp.getCenter();
			}
			//pdest = spp.getCenter();
		}
		
		Segment s = new Segment( porig, pdest );	
		
		// Vector<Point2D> editPoints = mr.getEditPointsCopy();	
		Vector<Point2D> editPoints = this.getEditPoints();
		
		
		if ((editPoints==null) || (editPoints.size()==0)) {
			// Caso sin EditPoints. 
			// El segmento entre reactivo y producto es directo, entre
			// linkAnchors, o los bordes, en dirección a los centros de los species.						
			if ((pdest==null)||(porig==null)){
				System.err.println(mr.getId());
				return null;
			}		
			
			try {
				arrows.composeWith( getArrow(porig, pdest, mr.isReversible() ) );	// Si reversible, pintar doble punta			
				linesBackground.composeWith( 
						new SVGLine( 
								new Segment(pdest, porig).trim(8).getInverted().trim(10) 
								) 
						);
				squareShape.composeWith( getRectangleBetween(porig, pdest) );
				
			} catch (NullPointerException e) {
				System.err.println(spr.getClass() + "/" + spp.getClass());
				e.printStackTrace();			
			}
			
			Point2D pcenter = new Segment(porig, pdest).getCenter();
			if ( mr.getReactantLinks() != null)
				for (MAddedSpeciesLink spc : mr.getReactantLinks()) {												
					buildAddedReactant( spc, porig, pcenter);				
				}
			if ( mr.getProductLinks() != null)
				for (MAddedSpeciesLink spc : mr.getProductLinks()) {												
					buildAddedProduct( spc, pdest, pcenter );				
				}			
		
		} else {
			// Caso con EditPoints.
			// El segmento base sobre el que se calculan los editPoints es el formado entre
			// los centros de los especies, o los linkAnchors si los hay.
			// El principio y el fin de la línea que dibuja la reacción se calcula en base
			// a los segmentos primero y último, intersecados con los bordes de los species
			// corresponddientes
			
			int rectangleIndex = mr.getRectangleIndex() - 1;
			
			// Si el SBML viene de CellDesigner hay que transformar los EditPoints.
			// Si no, se toman sus valores absolutos
			if ( this.mr.isTransformEditPoints() ) {
				
				AffineTransform at;
				if ( (mr.getReactants().get(0).getLinkAnchor() != null) &&
				     (mr.getProducts().get(0).getLinkAnchor() != null) ) {
					at = getEditPointTransform(s);
				} else {
					at = getEditPointTransform(s0);
				}		
				
				for(Point2D p : editPoints) {
					p.setLocation( at.transform(p, null) );
				}
				
				// Una vez transformados los puntos, marcar para que no se vuelvan a modificar
				// this.mr.setTransformEditPoints( false );
			}

			// Reubicar puntos inicial y final, teniendo en cuenta los editpoints
			// para calcular puntos de intersección
			
			if (mr.getProducts().get(0).getLinkAnchor() == null) {
				Point2D intersection = spp.intersection(
								new Segment( editPoints.lastElement() , spp.getCenter() )
							);
				
				
				if ( intersection != null) {
					pdest = intersection;
				
					// Ajuste para layout Ortogonal
					if ( SVGConfig.layout_type == Layouter.LAYOUT_ORTHOGONAL &&
						 !this.mr.isTransformEditPoints()	) {					
						pdest = orthogonalizeSegment(
								new Segment( editPoints.lastElement() , intersection ), 
								spp.getBBox() ).getP2();
					}
					
				} else {
					pdest = spp.getCenter();
				}
				
			}
			
			if (mr.getReactants().get(0).getLinkAnchor() == null) {
				Point2D intersection = spr.intersection(
								new Segment( editPoints.firstElement() , spr.getCenter() )
							);
				if ( intersection != null) {
					porig = intersection;
					// Ajuste para layout Ortogonal
					if ( SVGConfig.layout_type == Layouter.LAYOUT_ORTHOGONAL &&
						 !this.mr.isTransformEditPoints()	) {					
						porig = orthogonalizeSegment(
								new Segment( editPoints.firstElement() , intersection ), 
								spr.getBBox() ).getP2();
					}
					
				} else { 
					porig = spr.getCenter();
				}
			}
			
			// Dibujar segmentos
			Segment firstSegment = new Segment( editPoints.firstElement(), porig );
			Point2D p1, p2;
			// Primero			
			if ( mr.isReversible() ) {
				arrows.composeWith( getArrow( editPoints.firstElement(), porig ) );
			} else {
				lines.composeWith( new SVGLine( firstSegment ) );				
			}
			linesBackground.composeWith( new SVGLine( firstSegment.trim(10) ) );
			
			if (rectangleIndex < 0) {
				
				Point2D pcenter = new Segment( porig, editPoints.firstElement() ).getCenter();
				
				for (MAddedSpeciesLink spc : mr.getReactantLinks()) {												
					buildAddedReactant( spc, porig, pcenter);					
				}						
				for (MAddedSpeciesLink spc : mr.getProductLinks()) {												
					buildAddedProduct( spc, editPoints.firstElement(), pcenter );					
				}
				
				squareShape.composeWith( getRectangleBetween( porig, editPoints.firstElement() ) );
			} 
						
			// Intermedios	
			for(int i=0; i<editPoints.size()-1; i++) {
				p1 = editPoints.elementAt(i);
				p2 = editPoints.elementAt(i+1);
				Segment segment =  new Segment(p1,p2);
				lines.composeWith( new SVGLine( segment ) );
				linesBackground.composeWith( new SVGLine( segment ) );
				
				if (rectangleIndex == i) {					
					squareShape.composeWith( getRectangleBetween(p1, p2) );
					Point2D pcenter = segment.getCenter();
					
					for (MAddedSpeciesLink spc : mr.getReactantLinks()) {												
						buildAddedReactant( spc, p1, pcenter);
					}						
					for (MAddedSpeciesLink spc : mr.getProductLinks()) {							
						buildAddedProduct( spc, p2, pcenter );						
					}					
				}	
				
				// Debug
				lines.composeWith( 
					getDebugEditPoints(p1, "#00ff00" )
				);				
			}		
			
			lines.composeWith( 
					getDebugEditPoints(editPoints.lastElement() , "#00ffff" )
				);
			
			// Ultimo
			arrows.composeWith( getArrow(editPoints.lastElement(), pdest));
			linesBackground.composeWith( 
					new SVGLine( 
							new Segment(editPoints.lastElement(), pdest).trim(10) 
							) 
					);
			if (rectangleIndex >= editPoints.size()-1 ) {
				 		
				Point2D pcenter = new Segment( editPoints.lastElement(), pdest ).getCenter();
						
				for (MAddedSpeciesLink spc : mr.getReactantLinks()) {												
					buildAddedReactant( spc, editPoints.lastElement(), pcenter);
					
				}						
				for (MAddedSpeciesLink spc : mr.getProductLinks()) {												
					buildAddedProduct( spc, pdest, pcenter );					
				}
				
				squareShape.composeWith( getRectangleBetween( editPoints.lastElement(), pdest) );		
			}			
		}		
		
		
		lines.setAttribute("stroke", SVGUtil.getHexColor( mr.getLine().getColor() ) );
		lines.setAttribute("stroke-width", "" + mr.getLine().getWidth() );
		lines.setAttribute("stroke-linecap", "butt");
		lines.setAttribute("stroke-linejoin", "round");
//		
		arrows.setAttribute("stroke", SVGUtil.getHexColor( mr.getLine().getColor() ) );
		arrows.setAttribute("stroke-width", "" + mr.getLine().getWidth() );
		
		buildModifications();
		buildBooleanLogicGates();
		this.texts.composeWith( getReactionText() );		
		
		linesBackground.setAttribute("fill", "none");
		linesBackground.setAttribute("stroke", SVGConfig.reactionBackgroundColor );
		linesBackground.setAttribute("stroke-width", "" + (mr.getLine().getWidth() + 4.0) );
		
		shape = shape.composeWith( linesBackground );		
		shape = shape.composeWith( lines );				
		shape = shape.composeWith( arrows );
		shape = shape.composeWith( squareShape );		
		shape = shape.composeWith( texts );
		
		shape.setAttribute("id", mr.getId()); // shp.setAttribute("style", "text-rendering: auto;");
		
		shape.setAttribute("stroke", "black");
		shape.setAttribute("stroke-linecap", "round");

		
		String dashArray = this.getDashArray();
		if (dashArray!=null) {
			shape.setAttribute("stroke-dasharray", dashArray);
		}		
		
		shape.composeWith( getDebugEditPoints( porig, "red") );
		shape.composeWith( getDebugEditPoints( pdest, "red") );
		
		
		addOnClickEvent();					
		
		return shape;
	}

	/**
	 * Añade al elemento SVG un gestor de evento onClick que muestra una pequeña 
	 * ventana de información.
	 * 
	 */
	protected void addOnClickEvent() {
		if ( !SVGConfig.omitJavascript )	
			shape.setAttribute("onclick", 
							   "infoWindowReac(\"" + mr.getId() + "\"," +
							   "\"" + mr.getId() + "\","+
							   "\"" + this.getType() + "\"," +
							   SVGScripts.javaArrayToJS( this.getReactantIDs() ) + "," +
							   SVGScripts.javaArrayToJS( this.getProductIDs() ) +
							   ");");
	}
	
	/*
	 * Obtener el texto con el nombre de la reacción
	 */
	protected SVGShape getReactionText() {
		if ( !SVGConfig.showReactionNames ) return null;
		
		if ( mr.getReactants().get(0).getLinkAnchor() == null || mr.getReactants().get(0).getLinkAnchor().equals("INACTIVE") ) {
			
			if ( editPoints.size() > 0 ) {
				return getReactionText(porig, editPoints.firstElement());
				
			} else {
				return getReactionText(porig, pdest);
			}	
		} else {
			// Si hay anchor en la specie orige, la posicion del texto depende de éste 
			
			String anchor = mr.getReactants().get(0).getLinkAnchor();
			
			if ( editPoints.size() > 0 ) {
				return getReactionText(porig, editPoints.firstElement(), anchor);
				
			} else {
				return getReactionText(porig, pdest, anchor);
			}				
		}			 
		
	}
	
	/**
	 * Generar texto de la reacción, teniendo en cuenta angulo con el que incide la linea de la 
	 * reacción sobre la forma del reactivo
	 * 
	 * @param porig
	 * @param pdest
	 * @return
	 */
	protected SVGShape getReactionText(Point2D porig, Point2D pdest) {
		if ( !SVGConfig.showReactionNames ) return null;
		
		double initialAngle = new Segment( porig, pdest ).getAngle();
		SVGShape text = null;
		
		if (initialAngle >=0 && initialAngle < Math.PI/4.0   ) {
			text = new SVGText(
					   SVGTextRenderer.getInstance().drawTextLineAboveLeft(
							   svgDoc, 
							   porig.getX() + 5, 
							   porig.getY() - 2, 
							   mr.getId(), 
							   12 ) 			
					);	
		} else if ( initialAngle >= Math.PI/4.0 && initialAngle <  Math.PI/2.0 ) {
			text = new SVGText(
					   SVGTextRenderer.getInstance().drawTextLineBelowRight(
							   svgDoc, 
							   porig.getX() - 5, 
							   porig.getY() + 2, 
							   mr.getId(), 
							   12 ) 			
					);
		} else if ( initialAngle >= Math.PI/2.0 && initialAngle < 3*Math.PI/4.0 ) {
			text = new SVGText(
					   SVGTextRenderer.getInstance().drawTextLineBelowLeft(
							   svgDoc, 
							   porig.getX() + 5, 
							   porig.getY() + 2, 
							   mr.getId(), 
							   12 ) 			
					);
		} else if ( initialAngle >= 3*Math.PI/4.0 && initialAngle <= Math.PI ) {
			text = new SVGText(
					   SVGTextRenderer.getInstance().drawTextLineAboveRight(
							   svgDoc, 
							   porig.getX() - 5, 
							   porig.getY() - 2, 
							   mr.getId(), 
							   12 ) 			
					);
		} else if ( initialAngle >= -Math.PI && initialAngle < -3*Math.PI/4.0 ) {
			text = new SVGText(
					   SVGTextRenderer.getInstance().drawTextLineBelowRight(
							   svgDoc, 
							   porig.getX() - 5, 
							   porig.getY() + 2, 
							   mr.getId(), 
							   12 ) 			
					);
		} else if ( initialAngle >= -3*Math.PI/4.0 && initialAngle < -Math.PI/2.0 ) {
			text = new SVGText(
					   SVGTextRenderer.getInstance().drawTextLineAboveLeft(
							   svgDoc, 
							   porig.getX() + 5, 
							   porig.getY() - 2, 
							   mr.getId(), 
							   12 ) 			
					);
		} else if ( initialAngle >= -Math.PI/2.0 && initialAngle < -Math.PI/4.0 ) {
			text = new SVGText(
					   SVGTextRenderer.getInstance().drawTextLineAboveRight(
							   svgDoc, 
							   porig.getX() - 5, 
							   porig.getY() - 2, 
							   mr.getId(), 
							   12 ) 			
					);
		} else if ( initialAngle >= -Math.PI/4.0 && initialAngle < 0 ) {
			text = new SVGText(
					   SVGTextRenderer.getInstance().drawTextLineBelowLeft(
							   svgDoc, 
							   porig.getX() + 5, 
							   porig.getY() + 2, 
							   mr.getId(), 
							   12 ) 			
					);
		}
		
		return text;
	}
	
	/**
	 * Generar texto de la reacción, teniendo en cuenta el anchor del reactivo al que se conecta 
	 * 
	 * @param porig
	 * @param pdest
	 * @param anchor
	 * @return
	 */
	protected SVGShape getReactionText(Point2D porig, Point2D pdest, String anchor) {	
		if ( !SVGConfig.showReactionNames ) return null;
		
		if (anchor == null) {
			return getReactionText( porig, pdest );
		}
		
		double initialSlope = new Segment( porig, pdest ).getAngle();
		SVGShape text = null;

		if (anchor.equals("SSW") ||
			anchor.equals("S") ||
			anchor.equals("SSE") ) {
			if ( initialSlope < Math.PI && initialSlope > Math.PI/2.0 ) {
				text = new SVGText(
						   SVGTextRenderer.getInstance().drawTextLineBelowLeft(
								   svgDoc, 
								   porig.getX() + 5, 
								   porig.getY() + 2, 
								   mr.getId(), 
								   12 ) 			
						);						
			} else {
				text = new SVGText(
						   SVGTextRenderer.getInstance().drawTextLineBelowRight(
								   svgDoc, 
								   porig.getX() - 12, 
								   porig.getY() , 
								   mr.getId(), 
								   12 ) 			
						);						
			}					
			
		} else if (anchor.equals("SW") || anchor.equals("W") || 
				   anchor.equals("WSW") || anchor.equals("WNW") ) {
			if ( initialSlope > -1*Math.PI && initialSlope < Math.PI/-2.0 ) {
				text = new SVGText(
						   SVGTextRenderer.getInstance().drawTextLineBelowRight(
								   svgDoc, 
								   porig.getX() - 8, 
								   porig.getY() + 2, 
								   mr.getId(), 
								   12 ) 			
						);						
			} else {
				text = new SVGText(
						   SVGTextRenderer.getInstance().drawTextLineAboveRight(
								   svgDoc, 
								   porig.getX() - 8, 
								   porig.getY() - 2, 
								   mr.getId(), 
								   12 ) 			
						);						
			}							
		} else if (anchor.equals("ENE") || anchor.equals("E") || 
				   anchor.equals("ESE") ) {
			if ( initialSlope < 0 && initialSlope > Math.PI/-2.0 ) {
				text = new SVGText(
						   SVGTextRenderer.getInstance().drawTextLineBelowLeft(
								   svgDoc, 
								   porig.getX() + 7, 
								   porig.getY() + 2, 
								   mr.getId(), 
								   12 ) 			
						);						
			} else {
				text = new SVGText(
						   SVGTextRenderer.getInstance().drawTextLineAboveLeft(
								   svgDoc, 
								   porig.getX() + 7, 
								   porig.getY() - 4, 
								   mr.getId(), 
								   12 ) 			
						);						
			}					
		
			
		} else if (anchor.equals("NW") || anchor.equals("NNW") || 
				   anchor.equals("N") || anchor.equals("NNE") || 
				   anchor.equals("NE")) {
			if ( initialSlope < 0 && initialSlope > Math.PI/-2.0 ) {
				text = new SVGText(
						   SVGTextRenderer.getInstance().drawTextLineAboveRight(
								   svgDoc, 
								   porig.getX() - 8, 
								   porig.getY() - 2, 
								   mr.getId(), 
								   12 ) 			
						);						
			} else {
				text = new SVGText(
						   SVGTextRenderer.getInstance().drawTextLineAboveLeft(
								   svgDoc, 
								   porig.getX() + 8, 
								   porig.getY() - 2, 
								   mr.getId(), 
								   12 ) 			
						);						
			}						
			
		} else if ( anchor.equals("SE") ) {
			// Aquí voy a cambiar respecto al comportamiento de CellDesigner
			if ( initialSlope < 0 && initialSlope > Math.PI/-2.0 ) {
				text = new SVGText(
						   SVGTextRenderer.getInstance().drawTextLineBelowLeft(
								   svgDoc, 
								   porig.getX() + 8, 
								   porig.getY() + 2, 
								   mr.getId(), 
								   12 ) 			
						);						
			} else {
				text = new SVGText(
						   SVGTextRenderer.getInstance().drawTextLineAboveLeft(
								   svgDoc, 
								   porig.getX() + 8, 
								   porig.getY() - 2, 
								   mr.getId(), 
								   12 ) 			
						);						
			}						
			
		}			
		
		return text;
	}
	
	protected SVGShape getRectangleBetween(Point2D p1, Point2D p2, String sign) {
		SVGShape rect;
		
		Point2D pcenter = new Point2D.Double( (p1.getX()+p2.getX())/2, (p1.getY()+p2.getY())/2 );
			
		SVGReactionSquare sqr = new SVGReactionSquare(p1, p2);
		
		rect = sqr;
		rect.setAttribute("fill", "white");	
		rect.setAttribute("stroke", SVGUtil.getHexColor( mr.getLine().getColor() ) );
		rect.setAttribute("stroke-width", "" + mr.getLine().getWidth() );
		
		
		if (!sign.equals("")) {
			SVGText txt = new SVGText(SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
				    						pcenter.getX() , pcenter.getY(),					    	
				    						sign , 10 ) ) ;
			txt.setAttribute("font-family", "serif");
			txt.setAttribute("font-weight", "bold");
			rect = rect.composeWith( txt ); 
			
		}
		
		this.square = sqr;
		return rect;
	}
	
	// Métodos para redefinir por subclases
//	protected SVGShape getRectangleBetween(Point2D p1, Point2D p2){
//		return getRectangleBetween(p1, p2, "");
//	}
	
	protected SVGShape getRectangleBetween(Point2D p1, Point2D p2){
		this.reactionCenter = new Point2D.Double( (p1.getX()+p2.getX())/2, (p1.getY()+p2.getY())/2 );
		return getRectangleBetween( p1,  p2, "");
	}

	
	
	protected String getMainReactionStyleColor() {
		return "stroke:" + SVGUtil.getHexColor( mr.getLine().getColor() ) + ";";
	}
	
	protected String getMainReactionStyleLineWidth() {
		return "width:" + mr.getLine().getWidth() + ";";
	}

	protected SVGShape getArrow(Point2D porig, Point2D pdest) {

		return new SVGArrow(porig, pdest, false, 
				  			SVGUtil.getHexColor( mr.getLine().getColor() ), 
				  			mr.getLine().getWidth() );
		
	}
	
	protected SVGShape getArrow(Point2D porig, Point2D pdest, boolean reversible) {
		
		return new SVGArrow(porig, pdest, reversible, 
				  			SVGUtil.getHexColor( mr.getLine().getColor() ), 
				  			mr.getLine().getWidth() );
	}
	
	
	protected void buildAddedReactant( MAddedSpeciesLink spc, Point2D linkPoint, Point2D rectangleCenter  ) throws SBML2SVGException {
		SVGSpecie added = species.get( spc.getMs().getIdAlias() ); 
		
		// Calcular punto de conexion hacia la Species
		// Segun haya o no linkAnchor y/o EditPoints
		Segment s;
		if ( spc.getEditPoints().size() == 0 ) {
			s = new Segment(linkPoint, added.getCenter());
		} else {
			s = new Segment(spc.getEditPoints().firstElement() , added.getCenter() );
		}
		
		Point2D panchor;
		if ( spc.getLinkAnchor() == null ) {
			panchor = added.getSVGShape().intersection(s);
			if (panchor == null) {
				panchor = added.getCenter();
			}
		} else {
			panchor = added.getLinkAnchor( spc.getLinkAnchor() );
		}

		this.lines.composeWith( getDebugEditPoints( panchor, "yellow") );

		Segment segment = new Segment(rectangleCenter, linkPoint);
		Point2D end;
		if ( this.mr.isTransformEditPoints() ) {
			// En CD4 punto de union es al 25% del segmento
			// rectangulo - editpoint
			segment.scale(0.25);
			end = segment.getP2();
		} else {
			// En un layout calculado, el punto de union es un
			// editpoint en la linea de la reaccion
			end = this.mr.getEditPoints().get( spc.getJoint() );
			
			this.lines.composeWith( getDebugEditPoints(
					new Point2D.Double( end.getX() + 2 , end.getY() +2 ) , "red" ) ) ;
		}
		
		if ( spc.getLine().getType().equalsIgnoreCase("Curve") ||
			 // Caso especial para CD
			 (	!spc.getLine().getType().equalsIgnoreCase("Straight") &&
				spc.getEditPoints().size() == 0 	 
			 ) ) {			
			
			Point2D middle = new Segment(linkPoint, panchor).getCenter();
			
		    Element path = svgDoc.createElementNS (svgNS, "path");
		    
		    path.setAttributeNS (null, "d", 
		    		"M" + Double.toString( panchor.getX() ) + " " + Double.toString( panchor.getY() ) + " " +
		    		"L" + Double.toString( middle.getX() ) + " " + Double.toString( middle.getY() ) + " " +
		    		"Q" + Double.toString( linkPoint.getX() ) + " " + Double.toString( linkPoint.getY() ) + " " +
		    			  Double.toString( end.getX() ) + " " + Double.toString( end.getY() ) 
		    		);
			
			SVGShape curve = new SVGCustomShape( path );
			curve.setAttribute("fill", "none");
			this.lines.composeWith( curve );		
			
			// White Background
			// recortar inicio del segmento
			panchor = new Segment(linkPoint, panchor).trim(10).getP2();
			
		    path = svgDoc.createElementNS (svgNS, "path");
		    
		    path.setAttributeNS (null, "d", 
		    		"M" + Double.toString( panchor.getX() ) + " " + Double.toString( panchor.getY() ) + " " +
		    		"L" + Double.toString( middle.getX() ) + " " + Double.toString( middle.getY() ) + " " +
		    		"Q" + Double.toString( linkPoint.getX() ) + " " + Double.toString( linkPoint.getY() ) + " " +
		    			  Double.toString( end.getX() ) + " " + Double.toString( end.getY() ) 
		    		);			
			curve = new SVGCustomShape( path );
			curve.setAttribute("fill", "none");
			this.linesBackground.composeWith( curve );
			
		} else {			
			
			if ( spc.getEditPoints().size() == 0  ) {	
				
				// Ajuste para layout Ortogonal
				if ( SVGConfig.layout_type == Layouter.LAYOUT_ORTHOGONAL &&
						 !this.mr.isTransformEditPoints()	) {	
					panchor = orthogonalizeSegment(
							new Segment( end , panchor ), 
								added.getBBox() ).getP2();
				}
				
				this.lines.composeWith( 
						new SVGLine( 
										panchor.getX(), panchor.getY(), 
										end.getX(), end.getY() 
								) 
						);
					
				panchor = new Segment( end, panchor).trim(10).getP2();			
				this.linesBackground.composeWith( new SVGLine( panchor.getX(), panchor.getY(), end.getX(), end.getY() ) );
			} else {
				Point2D prev = panchor;
				
				// Ajuste para layout Ortogonal
				if ( SVGConfig.layout_type == Layouter.LAYOUT_ORTHOGONAL &&
					 !this.mr.isTransformEditPoints()	) {					
					prev = orthogonalizeSegment(
							new Segment( spc.getEditPoints().get(0) , panchor ), 
							added.getBBox() ).getP2();
				}
				
				for(int i=0; i<spc.getEditPoints().size(); i++) {
					this.lines.composeWith( 
							new SVGLine( prev,  spc.getEditPoints().get(i) ) 
							);
					this.linesBackground.composeWith( 
							new SVGLine( prev,  spc.getEditPoints().get(i) ) 
							);					
					
					this.lines.composeWith( getDebugEditPoints(prev, "yellow" ) );
					
					prev = spc.getEditPoints().get(i);
				}

				this.lines.composeWith( 
						new SVGLine( prev,  end ) 
						);
				this.linesBackground.composeWith( 
						new SVGLine( prev,  end ) 
						);
				this.lines.composeWith( getDebugEditPoints(prev, "yellow" ) );
			}
		}	
		
		this.lines.composeWith( getDebugEditPoints( end, "yellow") );
		
		this.texts.composeWith( getReactionText( panchor, linkPoint, spc.getLinkAnchor() ) );
			
	}
	
	protected void buildAddedProduct( MAddedSpeciesLink spc, Point2D linkPoint, Point2D rectangleCenter ) throws SBML2SVGException {
		SVGSpecie added = species.get( spc.getMs().getIdAlias() );
		
		Point2D middle = null;
			
		Segment segment = new Segment(rectangleCenter, linkPoint);

		Point2D start;
		if ( this.mr.isTransformEditPoints() ){
			segment.scale(0.25);
			start = segment.getP2();
		} else {
			// En un layout calculado, el punto de union es un
			// editpoint en la linea de la reaccion
			start = this.mr.getEditPoints().get( spc.getJoint() );
			
			this.lines.composeWith( getDebugEditPoints(
					new Point2D.Double( start.getX() + 2 , start.getY() +2 ) , "red" ) ) ;
		}
		
		this.lines.composeWith( getDebugEditPoints( start, "yellow") );
		
		Segment s;
		if ( spc.getEditPoints().size() == 0 ) {
			s = new Segment(start, added.getCenter() );	
		} else {
			s = new Segment(spc.getEditPoints().lastElement() , added.getCenter() );
		}		
		
		
		Point2D panchor = added.getLinkAnchor( spc.getLinkAnchor() );		
		if ( spc.getLinkAnchor() == null ){
			panchor = added.getSVGShape().intersection(s);;
			if ( panchor == null ) {
				System.err.println("" + mr.getId() + " fallo la interseccion");
				panchor = added.getCenter();
			}
		} else {
			panchor = added.getLinkAnchor( spc.getLinkAnchor() );
		}
		
		this.lines.composeWith( getDebugEditPoints( panchor, "yellow") );
		
		if ( spc.getLine().getType().equalsIgnoreCase("Curve") ||
				 // Caso especial para CD
				 (	!spc.getLine().getType().equalsIgnoreCase("Straight") &&
					spc.getEditPoints().size() == 0 	 
				 ) ) {	
			
			middle = new Segment(linkPoint, panchor).getCenter();
						
		    Element path = svgDoc.createElementNS (svgNS, "path");
		    
		    path.setAttributeNS (null, "d", 
		    		"M" + Double.toString( start.getX() ) + " " + Double.toString( start.getY() ) + " " +
		    		"Q" + Double.toString( linkPoint.getX() ) + " " + Double.toString( linkPoint.getY() ) + " " +
		    			  Double.toString( middle.getX() ) + " " + Double.toString( middle.getY() ) 
		    		);
			
			SVGShape curve = new SVGCustomShape( path );
			curve.setAttribute("fill", "none");
			this.lines.composeWith( curve );			
			this.arrows.composeWith( getArrow(middle, panchor) );

			
		    Element pathCopy = svgDoc.createElementNS (svgNS, "path");
		    
		    pathCopy.setAttributeNS (null, "d", 
		    		"M" + Double.toString( start.getX() ) + " " + Double.toString( start.getY() ) + " " +
		    		"Q" + Double.toString( linkPoint.getX() ) + " " + Double.toString( linkPoint.getY() ) + " " +
		    			  Double.toString( middle.getX() ) + " " + Double.toString( middle.getY() ) 
		    		);			
			this.linesBackground.composeWith( new SVGCustomShape( pathCopy ) );
			this.linesBackground.composeWith( new SVGLine( new Segment(middle, panchor).trim(15) ) );
			
		} else {
			if ( spc.getEditPoints().size() == 0  ) {	
				this.arrows.composeWith( getArrow(start, panchor) );
				this.linesBackground.composeWith( new SVGLine( new Segment(start, panchor).trim(15) ) );
			} else {
				
				Point2D prev = start;
				for(int i=0; i< spc.getEditPoints().size(); i++) {
					this.lines.composeWith( 
							new SVGLine( prev,  spc.getEditPoints().get(i) ) 
							);
					this.linesBackground.composeWith( 
							new SVGLine( prev,  spc.getEditPoints().get(i) ) 
							);					
					
					this.lines.composeWith( getDebugEditPoints(prev, "yellow" ) );
					
					prev = spc.getEditPoints().get(i);
				}
	
				// Ajuste para layout Ortogonal
				if ( SVGConfig.layout_type == Layouter.LAYOUT_ORTHOGONAL &&
					 !this.mr.isTransformEditPoints()	) {					
					panchor = orthogonalizeSegment(
							new Segment( prev , panchor ), 
							added.getBBox() ).getP2();
				}
				
				this.arrows.composeWith( getArrow(prev, panchor) );
				this.linesBackground.composeWith( new SVGLine( new Segment(prev, panchor).trim(15) ) );
				
				this.lines.composeWith( getDebugEditPoints(prev, "yellow" ) );
			}			
			
		}
				
	}
		
	// Para el testeo
	protected SVGShape getDebugEditPoints(Point2D p, String color) {
		if ( !SVGConfig.debugMode ) return null;
		SVGShape puntazo = new SVGCircle(p.getX(), p.getY(), 3.5);
		puntazo.setAttribute("fill", color);		
		return puntazo;
	}
	
	// transforma el punto p, que está basado en un segmento de longitud uno, y
	// sobre el eje X ( 0,0 -> 1,0 ), al punto correspondiente respecto al
	// segmento dado
	protected AffineTransform getEditPointTransform(Segment s) {
		double theta = s.getAngle();
		double lon = s.getLongitude()  ;
		
		AffineTransform at = new AffineTransform();
				
		at.translate(s.getX1(), s.getY1());
		at.rotate(theta); 
		at.scale(lon, lon);
		
		return at;
	}
	
	protected void buildModifications() throws SBML2SVGException{
		if ( mr.getModifications() != null ) 
			for ( MModification mod : mr.getModifications() ) {
				if ( !mod.getType().contains("BOOLEAN_LOGIC_GATE_") ) {				
					buildModification( mod ) ;
				}	
			}		
	}
	
	protected void buildBooleanLogicGates() throws SBML2SVGException{
		if ( mr.getBooleanLogicGates() != null )
			for ( MBooleanLogicGate mbg : mr.getBooleanLogicGates() ) {
				if ( mbg.getType().contains("BOOLEAN_LOGIC_GATE_") ) {
					this.buildBooleanLogicGate( mbg );
				} 
			}			
			
	}	
	
	private void buildBooleanLogicGate(MBooleanLogicGate mbg) throws SBML2SVGException {
		
		// El 1er EditPoint del 'Boolean Logic Gate' es la coordenada central
		EditPoint cep = mbg.getEditPoints().lastElement();
		Point2D c = new Point2D.Double( cep.getXProjection(), cep.getYProjection() );   
		Vector<MModification> mods = mbg.getModifications();
		SVGShape[] lines = new SVGShape[ mods.size() ];
		
//		SVGSpecie spcA = species.get( mbg.getSpecieA().getIdAlias() );
//		SVGSpecie spcB = species.get( mbg.getSpecieB().getIdAlias() );
	
		// Draw all branches
		int idxLine = 0;
		for ( MModification mod : mods) {
			SVGSpecie spc = species.get( mod.getSpecie().getIdAlias() );
			Point2D orig;
			Segment sgm = new Segment( spc.getCenter(), c );
			// Check LinkAnchors
			if (mod.getLinkAnchor()!=null) {
				orig = spc.getLinkAnchor(mod.getLinkAnchor());
			} else {
				orig = spc.getSVGShape().intersection(sgm.getInverted());	
			}
			
			if ( (mod.getEditPoints()==null) ||
			     (mod.getEditPoints().size() < 1)  ) {
				
				// Ramas 
//				lines[idxLine] = new SVGLine( spc.getSVGShape().intersection(sgm.getInverted()) , c);
				lines[idxLine] = new SVGLine( orig , c);
				this.lines.composeWith( lines[idxLine]  );
				this.linesBackground.composeWith( 
						new SVGLine( new Segment(c, orig ).trim(10) )  
						);
				idxLine++;	
				
				// Dibujar nombre de la reacción en [última] rama
				this.texts.composeWith( getReactionText(orig, c, mod.getLinkAnchor() ) );
			} else {
				
				// Se transforman los editpoints sobre la base del segmento desde orig (linkAnchor de specie)
				// y punto de unión de la Boolean Logic Gate
				AffineTransform at = getEditPointTransform(new Segment( orig, c));
				Vector<Point2D> editPoints = new Vector<Point2D>();
				
				for (EditPoint ep : mod.getEditPoints() ) {
					
					editPoints.add( 
							at.transform(new Point2D.Double(ep.getXProjection(), ep.getYProjection()), null)
							);					
				}
				
				// Primer y último segmento
				// Afinar orig y dest
				if (mod.getLinkAnchor()==null) {
					orig = spc.getSVGShape().intersection(
								new Segment(editPoints.firstElement(), spc.getCenter() ) );
				}
								
				SVGComplexShape complexLine = new SVGComplexShape();
				complexLine.add( new SVGLine( new Segment(orig, editPoints.firstElement() ) ) );
				this.linesBackground.composeWith(
						new SVGLine( new Segment(editPoints.firstElement(), orig ).trim(10) )
						);
				
				complexLine.add( new SVGLine( new Segment( editPoints.lastElement(), c).trim(5) ) );
				this.linesBackground.composeWith(
						new SVGLine( new Segment( editPoints.lastElement(), c).trim(10) )
						);
				
				for (int i=0; i<editPoints.size()-1; i++) {
					Segment innerSegment = new Segment(
												editPoints.elementAt(i), 
												editPoints.elementAt(i+1) );
					complexLine.add( 
							new SVGLine( innerSegment ) );
					this.linesBackground.composeWith( 
							new SVGLine( innerSegment )  
							);
				}		
			
				
				lines[idxLine] = complexLine;
				this.lines.composeWith( complexLine );
				idxLine++;
				
				
				// Dibujar nombre de la reacción en [última] rama
				this.texts.composeWith( 
						getReactionText(orig, editPoints.firstElement(), mod.getLinkAnchor() ) 
						);
			}			    
			
		}

		// Linea a la reacción
		Point2D dest;		
		
		if ( this.square == null ) {
			dest = this.reactionCenter;
		} else if (mbg.getTargetLineIndex() != null) {
			dest = this.square.getAnchorPoint( mbg.getTargetLineIndex() );
		} else {
			dest = this.square.getAnchorPoint( c );
		}	
		
		SVGShape ln;
		Segment s;
		
		//  Procesar EndPoints a partir de orig, dest...
		Vector<EditPoint> mbgEp = mbg.getEditPoints();
		if ( (mbgEp==null) || (mbgEp.size()<2) ) {			
			//Segmento final.
			s = new Segment( c, dest);		
			// Segmento final, ligeramente recortado, para no
			// llegar a tocar la reaccion
			ln = new SVGLine(s.trim(3.5));
			this.lines.composeWith( ln );
			this.linesBackground.composeWith( new SVGLine(s.trim(10)) );
		} else {
			AffineTransform at = getEditPointTransform(new Segment( c, dest));
			Vector<Point2D> editPoints = new Vector<Point2D>();
			for ( int i=0; i< (mbgEp.size()-1); i++ ) {
				EditPoint ep = mbgEp.elementAt( i );
				editPoints.add( 
						at.transform(new Point2D.Double(ep.getXProjection(), ep.getYProjection()),null)
						);
			}
						
			// Afinar dest			
			// dest = this.square.getAnchorPoint(editPoints.lastElement()); 
			
			SVGComplexShape complexLine = new SVGComplexShape();
			complexLine.add( new SVGLine( new Segment(c, editPoints.firstElement() ) ) );
			
			linesBackground.composeWith( new SVGLine( new Segment(c, editPoints.firstElement() ) ) );
		
			for (int i=0; i<editPoints.size()-1; i++) {
				s = new Segment(
						editPoints.elementAt(i), 
						editPoints.elementAt(i+1) );
				
				complexLine.add( new SVGLine( s ) );
			}					
			
			// Segmento final, ligeramente recortado, para no
			// llegar a tocar la reaccion
			s = new Segment( editPoints.lastElement(), dest);
			complexLine.add( new SVGLine( s.trim(3.5) ) );
			
			linesBackground.composeWith( new SVGLine( s.trim(10) ) );
			
			ln = complexLine;
			
			this.lines.composeWith( ln );
		}		
		
		// Centro
		SVGCircle circle = new SVGCircle(c.getX(), c.getY(), getBooleaLogicCircleRadius() );
		circle.setAttribute("fill", "white");
		
		this.arrows.composeWith( circle );
		
		this.texts.composeWith( getBooleanLogicText(mbg, c) );
		

		
		// s = s.trim(5);
		if (mbg.getModificationType().equals("INHIBITION")) {
			this.texts.composeWith( getInhibitionSegment(s) );
		} else if (mbg.getModificationType().equals("TRIGGER")) {
			this.texts.composeWith( getTriggerTriangle(s) );
		} else if (mbg.getModificationType().equals("MODULATION")) {
			this.texts.composeWith( getModulationDiamond(s) );
		} else if (mbg.getModificationType().equals("PHYSICAL_STIMULATION")) {
			this.texts.composeWith( getStimulationTriangle(s) );
		} else if (mbg.getModificationType().equals("TRANSLATIONAL_ACTIVATION")) {
			ln.setAttribute("stroke-dasharray", "10 3 2 3");
			for (int i=0; i<lines.length; i++) {
				lines[i].setAttribute("stroke-dasharray", "10 3 2 3");
			}			
			this.texts.composeWith( getActivationAngle(s) );
		} else if (mbg.getModificationType().equals("TRANSLATIONAL_INHIBITION")) {
			ln.setAttribute("stroke-dasharray", "10 3 2 3");
			for (int i=0; i<lines.length; i++) {
				lines[i].setAttribute("stroke-dasharray", "10 3 2 3");
			}		
			this.texts.composeWith( getInhibitionSegment(s) );
		} else if (mbg.getModificationType().equals("TRANSCRIPTIONAL_ACTIVATION")) {
			ln.setAttribute("stroke-dasharray", "10 4 2 4 2 4");
			for (int i=0; i<lines.length; i++) {
				lines[i].setAttribute("stroke-dasharray", "10 4 2 4 2 4");
			}		
			this.texts.composeWith( getActivationAngle(s) );
		} else if (mbg.getModificationType().equals("TRANSCRIPTIONAL_INHIBITION")) {
			ln.setAttribute("stroke-dasharray", "10 4 2 4 2 4");
			for (int i=0; i<lines.length; i++) {
				lines[i].setAttribute("stroke-dasharray", "10 4 2 4 2 4");
			}
			this.texts.composeWith( getInhibitionSegment(s) );
		} else if (mbg.getModificationType().equals("UNKNOWN_CATALYSIS")) {
			ln.setAttribute("stroke-dasharray", "6 6");
			for (int i=0; i<lines.length; i++) {
				lines[i].setAttribute("stroke-dasharray", "6 6");
			}
			this.texts.composeWith( getCatalysisCircle(s) );
		} else if (mbg.getModificationType().equals("UNKNOWN_INHIBITION")) {
			ln.setAttribute("stroke-dasharray", "6 6");
			for (int i=0; i<lines.length; i++) {
				lines[i].setAttribute("stroke-dasharray", "6 6");
			}
			this.texts.composeWith( getInhibitionSegment(s) );
		} else {				
			this.texts.composeWith( getCatalysisCircle(s) );			
		}			
		
	}

	private SVGShape getBooleanLogicText(MBooleanLogicGate mbg, Point2D c ) {
		String str = "";
		if ( mbg.getType().equals("BOOLEAN_LOGIC_GATE_NOT") ) {
			if (SVGConfig.SBGNMode) {
				str = "NOT";
			} else {
				str = "!";
			}
			return new SVGText(
					SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
				    		c.getX(), c.getY(), str , 10 ) 
				   )  ;
		} else if ( mbg.getType().equals("BOOLEAN_LOGIC_GATE_UNKNOWN") ) {
			if (SVGConfig.SBGNMode) {
				str = "?";
			} else {
				str = "?";
			}
			return  new SVGText(
					SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
				    		c.getX(), c.getY(), str , 10 ) 
				   )  ;
		} else if ( mbg.getType().equals("BOOLEAN_LOGIC_GATE_AND") ) {
			if (SVGConfig.SBGNMode) {
				str = "AND";
			} else {
				str = "&";
			}
			return  new SVGText(
					SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
				    		c.getX(), c.getY(), str , 10 ) 
				   )  ;
		} else if ( mbg.getType().equals("BOOLEAN_LOGIC_GATE_OR") ) {
			if (SVGConfig.SBGNMode) {
				str = "OR";
			} else {
				str = "|";
			}
			return  new SVGText(
					SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
				    		c.getX(), c.getY(), str , 10 ) 
				   )  ;
		} 
		
		return null;
	}

	private double getBooleaLogicCircleRadius() {
		if (SVGConfig.SBGNMode) {
			return 12;
		}
		return 6;
	}

	private void buildModification( MModification mod ) throws SBML2SVGException {
		if ( mod.getSpecie() == null ) return; // Sin especie no hay modificación que dibujar.
		SVGSpecie spc = species.get( mod.getSpecie().getIdAlias() );

		// Origen y destino de la rama de la modificación
		Point2D orig = null, dest = null;
		// Origen y Destino en dibujo CD4, para el calculo de los editPoints
		Point2D editPointsOrig, editPointsDest;
		
		// Segmento inicial que une la species modificadora con
		// la reaccion modificada
		Segment s; 
		
		// Check LinkAnchors en origen
		if (mod.getLinkAnchor()!=null) {
			editPointsOrig = (Point2D) spc.getLinkAnchor(mod.getLinkAnchor()).clone();
		} else {
			editPointsOrig = (Point2D) spc.getCenter().clone();
		}
		
		if ( this.square == null ) {
			
			if (mod.getTargetLineIndex() != null) {
				editPointsDest = this.getAnchorPoint( mod.getTargetLineIndex() );
				s = new Segment( editPointsOrig, editPointsDest );
			} else {
				editPointsDest = this.reactionCenter;
				s = new Segment( editPointsOrig, this.reactionCenter );
			}		
			
		} else if (mod.getTargetLineIndex() != null) {
			editPointsDest = this.square.getAnchorPoint( mod.getTargetLineIndex() );		
			s = new Segment( editPointsOrig, editPointsDest );
			
		} else {			
			editPointsDest = this.square.getAnchorPoint( spc.getCenter() );
			s = new Segment( editPointsOrig, this.square.getCenter() );
		}		
						
		if (editPointsDest==null || editPointsOrig==null) {
			throw new SBML2SVGException("Error inesperado calculando Modificacion ("+
					this.mr.getId() + ")"); 				
		}			
		
		SVGShape ln;
		SVGShape tip;
		
		// Dibujar segmentos
		Vector<EditPoint> modEp = mod.getEditPointsCopy();
		if ( (modEp==null) || (modEp.size()==0) ) {			
			// Único segmento final
			if ( this.showsSquare() ) {
				if ( this.square != null && mod.getTargetLineIndex() != null) {
					dest = editPointsDest;
				} else if (this.square != null) {
					// Usar el punto calculado anteriormente
					dest = editPointsDest;
				} else {					
					dest = this.getAnchorPoint( mod.getTargetLineIndex() );
				}
			} else {
				dest = reactionCenter;
			}
			
			if (mod.getLinkAnchor()==null) {
				Segment reactionCenterToSpecies = new Segment( dest, editPointsOrig );
				orig = spc.getSVGShape().intersection( reactionCenterToSpecies );
				if ( orig == null ) orig = editPointsOrig;
			} else {
				orig = editPointsOrig;
			}
			
			s = new Segment( orig, dest ).trim( getCenterReactionMargin() );			
			ln = new SVGLine(s.trim(3.5)) ;
			linesBackground.composeWith( new SVGLine(s.trim(8).getInverted().trim(5)) );
						
		} else {
			
			if ( this.mr.isTransformEditPoints() ) {
				// Se transforman los editpoints sobre la base del segmento desde orig (linkAnchor de specie)
				// y centro de la reacción (linkAnchor de la reacción)				
				AffineTransform at = getEditPointTransform(new Segment( editPointsOrig, editPointsDest ));

				for (EditPoint ep : modEp) {
					ep.moveTo(
							at.transform(
									new Point2D.Double(ep.getXProjection(), 
											           ep.getYProjection()), 
											           null)
							);
				}
			}
			
			// Primer y último segmento
			// Afinar orig y dest
			if (mod.getLinkAnchor()==null) {
				orig = spc.getSVGShape().intersection(
							new Segment( modEp.firstElement().toPoint2D() ,editPointsOrig ) );
				if ( orig == null ) orig = editPointsOrig;
				
				// Ajuste para layout Ortogonal
				if ( SVGConfig.layout_type == Layouter.LAYOUT_ORTHOGONAL &&
					 !this.mr.isTransformEditPoints()	) {					
					orig = orthogonalizeSegment(
							new Segment( modEp.firstElement().toPoint2D() , orig ), 
							spc.getBBox() ).getP2();
				}
				
			} else {
				orig = editPointsOrig;
			}
			
			// Definir el ultimo segmento
			if ( this.showsSquare() ) {
				if ( this.square != null && mod.getTargetLineIndex() != null) {
					dest = editPointsDest;
				} else if (this.square != null) {
					// Usar el punto calculado anteriormente
					dest = editPointsDest;
				} else {
					dest = this.getAnchorPoint( mod.getTargetLineIndex() );
				}
			} else {
				dest = reactionCenter;
			}			
			
			SVGComplexShape complexLine = new SVGComplexShape();
			complexLine.add( new SVGLine( new Segment(orig, modEp.firstElement().toPoint2D() ) ) );
			
			this.linesBackground.composeWith( 
					new SVGLine( 
							new Segment(modEp.firstElement().toPoint2D(),orig ).trim(10)
							) 
					);
			
			
			s = new Segment( modEp.lastElement().toPoint2D(), dest).trim( getCenterReactionMargin() );
			complexLine.add( new SVGLine( s.trim(3.5) ) );	
			
			this.linesBackground.composeWith( new SVGLine( s.trim(10) ) );
		
			// Dibujar segmentos intermedios
			for (int i=0; i<modEp.size()-1; i++) {
				Segment innerSegment = new Segment(
												modEp.elementAt(i).toPoint2D(), 
												modEp.elementAt(i+1).toPoint2D() ); 
				complexLine.add( new SVGLine( innerSegment ) );
				
				this.linesBackground.composeWith( new SVGLine( innerSegment ) );
				
				complexLine.add( 
					getDebugEditPoints(modEp.elementAt(i).toPoint2D(), "#ff0000")
				);
			}		
			complexLine.add( 
					getDebugEditPoints(modEp.elementAt(modEp.size()-1).toPoint2D(), "#ffff00")
				);
			
			ln = complexLine ;
		}
		
		
		
		if (mod.getType().equals("INHIBITION")) {
			tip = getInhibitionSegment(s);
			ln.setAttribute("stroke-dasharray", "none");
		} else if (mod.getType().equals("TRIGGER")) {
			tip = getTriggerTriangle(s);
			ln.setAttribute("stroke-dasharray", "none");
		} else if (mod.getType().equals("MODULATION")) {
			tip = getModulationDiamond(s);
			ln.setAttribute("stroke-dasharray", "none");
		} else if (mod.getType().equals("PHYSICAL_STIMULATION")) {
			tip = getStimulationTriangle(s);
			ln.setAttribute("stroke-dasharray", "none");
		} else if (mod.getType().equals("TRANSLATIONAL_ACTIVATION")) {
			ln.setAttribute("stroke-dasharray", "10 3 2 3");
			tip = getActivationAngle(s);
		} else if (mod.getType().equals("TRANSLATIONAL_INHIBITION")) {
			ln.setAttribute("stroke-dasharray", "10 3 2 3");
			tip = getInhibitionSegment(s);
		} else if (mod.getType().equals("TRANSCRIPTIONAL_ACTIVATION")) {
			ln.setAttribute("stroke-dasharray", "10 4 2 4 2 4");
			tip = getActivationAngle(s);
		} else if (mod.getType().equals("TRANSCRIPTIONAL_INHIBITION")) {
			ln.setAttribute("stroke-dasharray", "10 4 2 4 2 4");
			tip = getInhibitionSegment(s);
		} else if (mod.getType().equals("UNKNOWN_CATALYSIS")) {
			ln.setAttribute("stroke-dasharray", "6 6");
			tip = getCatalysisCircle(s, mod.getLine().getWidth() );
		} else if (mod.getType().equals("UNKNOWN_INHIBITION")) {
			ln.setAttribute("stroke-dasharray", "6 6");
			tip = getInhibitionSegment(s);
		} else {				
			ln.setAttribute("stroke-dasharray", "none");
			tip = getCatalysisCircle(s, mod.getLine().getWidth());
			
		}			
		tip.setAttribute("stroke-width", "" + mod.getLine().getWidth() );
		tip.setAttribute("stroke", SVGUtil.getHexColor( mod.getLine().getColor() ) );
		tip.setAttribute("stroke-dasharray", "none");
		
		this.squareShape = this.squareShape.composeWith( tip );
		
		ln.setAttribute("stroke-width", "" + mod.getLine().getWidth() );
		ln.setAttribute("stroke", SVGUtil.getHexColor( mod.getLine().getColor() ) );
		lines.composeWith( ln );
		
		// Añadir nombre de la reacción
		if ( mod.getEditPoints().size() > 0 ) {
			texts.composeWith( getReactionText( orig, modEp.firstElement().toPoint2D(), mod.getLinkAnchor() ) );			
		} else {
			texts.composeWith( getReactionText( orig, dest, mod.getLinkAnchor() ) );
		}
		
		lines.composeWith( ln ) ;

	}	
	
	SVGShape getCatalysisCircle(Segment s) {
		return getCatalysisCircle( s, 1 ) ;
	}
	
	SVGShape getCatalysisCircle(Segment s, double lineWidth) {
		double theta = s.getAngle();
		AffineTransform at = new AffineTransform();
		at.translate(s.getX2(), s.getY2()); at.rotate(theta);
		// Crear circulo ligeramente desplazado
		Point2D c = new Point2D.Double(-5,0);
		at.transform(c, c);
		SVGCircle circ = new SVGCircle(
								c.getX(), c.getY(),			// Center 
								3 + (lineWidth-1)/2 				// Radio depende de la anchura de la línea
						     );
		circ.setAttribute("fill", "white");
		
		return circ;
	}
	
	SVGShape getStimulationTriangle(Segment s) {
		double theta = s.getAngle();
		AffineTransform at = new AffineTransform();
		at.translate(s.getX2(), s.getY2()); at.rotate(theta);
		// Crear triangulo ligeramente desplazado
		Point2D t0 = new Point2D.Double(-1,0);
		Point2D t1 = new Point2D.Double(-9,4);
		Point2D t2 = new Point2D.Double(-9,-4);		
		at.transform(t0, t0);
		at.transform(t1, t1);
		at.transform(t2, t2);
		Vector<Point2D> vtx = new Vector<Point2D>();
		vtx.add(t0); vtx.add(t1); vtx.add(t2);
		SVGPolygon trn = new SVGPolygon(vtx);
		trn.setAttribute("fill", "white");
		
		return trn;
	}
	
	SVGShape getActivationAngle(Segment s) {
		double theta = s.getAngle();
		AffineTransform at = new AffineTransform();
		at.translate(s.getX2(), s.getY2()); at.rotate(theta);
		// Crear triangulo ligeramente desplazado
		Point2D t0 = new Point2D.Double(2,0);
		Point2D t1 = new Point2D.Double(-4,5);
		Point2D t2 = new Point2D.Double(-4,-5);		
		at.transform(t0, t0);
		at.transform(t1, t1);
		at.transform(t2, t2);
		SVGLine ln0 = new SVGLine(new Segment(t1, t0));
		SVGLine ln1 = new SVGLine(new Segment(t2, t0));		
		
		return ln0.composeWith(ln1);
	}

	SVGShape getModulationDiamond(Segment s) {
		double theta = s.getAngle();
		AffineTransform at = new AffineTransform();
		at.translate(s.getX2(), s.getY2()); at.rotate(theta);
		// Crear triangulo ligeramente desplazado
		Point2D t0 = new Point2D.Double(-1,0);
		Point2D t1 = new Point2D.Double(-8.5,4);
		Point2D t2 = new Point2D.Double(-17,0);
		Point2D t3 = new Point2D.Double(-8.5,-4);		
		
		at.transform(t0, t0); at.transform(t1, t1);
		at.transform(t2, t2); at.transform(t3, t3);
		Vector<Point2D> vtx = new Vector<Point2D>();
		vtx.add(t0); vtx.add(t1); vtx.add(t2); vtx.add(t3);
		SVGPolygon trn = new SVGPolygon(vtx);
		trn.setAttribute("fill", "white");
		
		return trn;
	}

	SVGShape getTriggerTriangle(Segment s) {
		double theta = s.getAngle();
		AffineTransform at = new AffineTransform();
		at.translate(s.getX2(), s.getY2()); at.rotate(theta);
		// Crear triangulo ligeramente desplazado
		Point2D t0 = new Point2D.Double(-1,0);
		Point2D t1 = new Point2D.Double(-15,4.5);
		Point2D t2 = new Point2D.Double(-15,-4.5);	
		Point2D t3 = new Point2D.Double(-20,4.5);
		Point2D t4 = new Point2D.Double(-20,-4.5);
		at.transform(t0, t0); at.transform(t1, t1); at.transform(t2, t2);
		at.transform(t3, t3); at.transform(t4, t4);
		Vector<Point2D> vtx = new Vector<Point2D>();
		vtx.add(t0); vtx.add(t1); vtx.add(t2);
		SVGPolygon trn = new SVGPolygon(vtx);
		trn.setAttribute("fill", "white");
		SVGLine ln = new SVGLine(new Segment(t3, t4));
		return trn.composeWith(ln);
	}
	
	SVGShape getInhibitionSegment(Segment s) {
		double theta = s.getAngle();
		AffineTransform at = new AffineTransform();
		at.translate(s.getX2(), s.getY2()); at.rotate(theta);
		// Crear triangulo ligeramente desplazado
		Point2D t0 = new Point2D.Double(-3.5 ,-5);
		Point2D t1 = new Point2D.Double(-3.5 ,5);		
		at.transform(t0, t0); at.transform(t1, t1); 
		
		
		SVGLine ln = new SVGLine(new Segment(t0, t1));
		ln.setAttribute("stroke-linecap", "butt");
		return ln;
	}
	
	public String getType() {
		return this.mr.getType();
	}
	
	public String getDashArray() {
		return null;
	}
	
	protected String[] getReactantIDs() {
		String[] ids = new String[ mr.getReactants().size() + mr.getReactantLinks().size() ];
		int i = 0;
		for (MSpeciesLink spc : mr.getReactants()) {
			ids[i++] = spc.getMs().getIdAlias();
		}
				
		for (MSpeciesLink spc : mr.getReactantLinks()) {
			ids[i++] = spc.getMs().getIdAlias();
		}
		
		return ids;
	}
	
	protected String[] getProductIDs() {
		String[] ids = new String[ mr.getProducts().size() + mr.getProductLinks().size() ];
		int i = 0;
		for (MSpeciesLink spc : mr.getProducts()) {
			ids[i++] = spc.getMs().getIdAlias();
		}
		
		for (MSpeciesLink spc : mr.getProductLinks()) {
			ids[i++] = spc.getMs().getIdAlias();
		}
		
		return ids;
	}

	public SVGReactionSquare getSquare() {
		return square;
	}

	public boolean isTransformEditPoints() {
		return this.mr.isTransformEditPoints();
	}
	
	/**
	 * Metodo auxiliar, para layouts ortogonales.
	 * Se cambia el punto destino, para que, incidiendo
	 * en la figura, sea lo más perpendicular posible
	 * 
	 * @param s		Segmento con la dirección que incide en la figura
	 * @param shp	Figura
	 * @return		Segmento ajustado
	 */
	protected Segment orthogonalizeSegment( Segment s, Rectangle2D bbox ) {
//		System.out.println( this.mr.getId() + ":" + s.getAngle() );
		Segment newS = s;
		double initialAngle = s.getAngle();
		
		double threshold = 0.0001;
		if (   Math.abs( initialAngle ) < threshold 
		   ||  Math.abs( Math.abs( initialAngle) - Math.PI/2 )< threshold 
		   ||  Math.abs( Math.abs( initialAngle) + Math.PI/2 )< threshold  
		   ||  Math.abs(  Math.abs( initialAngle) - Math.PI )< threshold )  {
//			System.err.println("" + initialAngle + " demasiado perpendicular");
			return newS;
		}
		
		if (initialAngle >0 && initialAngle < Math.PI/4.0   ) {

			newS = new Segment(
					s.getP1(),
					new Point2D.Double( 
							    s.getP2().getX(),
								Math.max( s.getP1().getY(), bbox.getMinY() ) 
							)
				);

		} else if ( initialAngle >= Math.PI/4.0 && initialAngle <  Math.PI/2.0 ) {
			newS = new Segment(
					s.getP1(),
					new Point2D.Double( 
								Math.max( s.getP1().getX(), bbox.getMinX() ) ,
								s.getP2().getY() 
							)
				);

		} else if ( initialAngle >= Math.PI/2.0 && initialAngle < 3*Math.PI/4.0 ) {
			newS = new Segment(
					s.getP1(),
					new Point2D.Double( 
								Math.min( s.getP1().getX(), bbox.getMaxX() ) ,
								s.getP2().getY() 
							)
				);

		} else if ( initialAngle >= 3*Math.PI/4.0 && initialAngle <= Math.PI ) {
			newS = new Segment(
					s.getP1(),
					new Point2D.Double( 
							    s.getP2().getX(),
								Math.max( s.getP1().getY(), bbox.getMinY() ) 
							)
				);

		} else if ( initialAngle >= -Math.PI && initialAngle < -3*Math.PI/4.0 ) {
			newS = new Segment(
					s.getP1(),
					new Point2D.Double( 
							    s.getP2().getX(),
								Math.min( s.getP1().getY(), bbox.getMaxY() ) 
							)
				);

		} else if ( initialAngle >= -3*Math.PI/4.0 && initialAngle < -Math.PI/2.0 ) {
			newS = new Segment(
					s.getP1(),
					new Point2D.Double( 
								Math.min( s.getP1().getX(), bbox.getMaxX() ) ,
								s.getP2().getY() 
							)
				);

		} else if ( initialAngle >= -Math.PI/2.0 && initialAngle < -Math.PI/4.0 ) {
			newS = new Segment(
					s.getP1(),
					new Point2D.Double( 
								Math.max( s.getP1().getX(), bbox.getMinX() ) ,
								s.getP2().getY() 
							)
				);

		} else if ( initialAngle >= -Math.PI/4.0 && initialAngle < 0 ) {
			newS = new Segment(
					s.getP1(),
					new Point2D.Double( 
							    s.getP2().getX(),
								Math.min( s.getP1().getY(), bbox.getMaxY() ) 
							)
				);

		}		
		
		
		return newS;
	}


	/**
	 * Devuelve el punto al que se unen las modificaciones u otra entidad que
	 * enlace con la reacción-
	 * 
	 * @param targetLineIndex
	 * @return
	 */
	public Point2D getAnchorPoint( String targetLineIndex ) {
		Point2D anchor = reactionCenter;
		
		if ( targetLineIndex == null ) return anchor;
		
		int linkAnchor =		
			Integer.parseInt( targetLineIndex.split(",")[1] );		
		
		if ( this.editPoints.size() == 0 ) {
			// Si no hay editpoints, devolver punto central
			return anchor;
		} else {
			Point2D p1 = this.porig;
			Point2D p2 = this.editPoints.firstElement();
			if (linkAnchor == 0) {
				anchor = new Point2D.Double( 
						(p1.getX()+
								p2.getX())/2, 
						(p1.getY()+
								p2.getY())/2 );
			} else if (linkAnchor >= this.editPoints.size() ) {
				p1 = this.editPoints.lastElement();
				p2 = this.pdest;
				anchor = new Point2D.Double( 
						(p1.getX()+
								p2.getX())/2, 
						(p1.getY()+
								p2.getY())/2 );
			} else {
				p1 = this.editPoints.elementAt( linkAnchor - 1);
				p1 = this.editPoints.elementAt( linkAnchor );
				anchor = new Point2D.Double( (p1.getX()+p2.getX())/2, (p1.getY()+p2.getY())/2 );
			}
		}

		return anchor;
	}
	
	/**
	 * Obtiene la distancia que deben mantener los ejes que se unan al
	 * centro de esta reaccion ( cuadrados, circulos, puntos, etc..)	 * 
	 * 
	 */
	protected double getCenterReactionMargin() {
		return 0;
	}
	
	protected abstract boolean showsSquare();

	public Vector<Point2D> getEditPoints() {
		return editPoints;
	}

	public void setEditPoints(Vector<Point2D> editPoints) {
		this.editPoints = editPoints;
	}
	
	
}
