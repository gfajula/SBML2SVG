package svgview.species.gene;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import model.specie.gene.MGene;
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
import svgview.species.SVGSpecie;
import svgview.util.SVGTextRenderer;
import svgview.util.SVGUtil;

public class SVGGene extends SVGSpecie {
	protected MGene mg;
	protected Document svgDoc;
	protected Point2D.Double center;
	protected SVGShape shape;
	
	public SVGGene(Document svgDoc, MGene mg) {
		super(svgDoc, mg);
		this.mg = mg;
		this.svgDoc = svgDoc;
		this.bounds = mg.getBounds();
		
	}

	@Override
	public Document getDocument() {		
		return svgDoc;
	}

	protected SVGShape getRectSBGN(double x, double y, double width, double height) {
		SVGShape rect = new SVGSemiRoundRectangle(x, y, width, height);
	    Color c = mg.getUsualView().getPaint().getColor();    
	    rect.setAttribute("fill", SVGUtil.getHexColor(c));
//	    rect.setAttributeNS (null, "fill-opacity", "0.5");
	    rect.setAttribute("stroke", "black");
	    rect.setAttribute("stroke-width", "1");
	    
	    if ( this.mg.isHypothetical() ) {
	    	rect.setAttribute ("style", "stroke-dasharray: 6, 3;");
	    }
		return rect;
	}
	
	protected SVGShape getClonedRectSBGN(double x, double y, double width, double height) throws SBML2SVGException {
		SVGShape rect = new SVGSemiRoundRectangle(x, y, width,  height );
		SVGShape background = new SVGSemiRoundRectangle(x, y, width,  height );
		SVGShape cloneStripe = new SVGSemiRoundRectangle(x , y+ 3*height/4, width,  height/4 );
				
		
		SVGComplexShape shp = new SVGComplexShape();
		
		Color c = this.getMspecies().getUsualView().getPaint().getColor();  
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
		
	    if ( this.getMspecies().isHypothetical() ) {
	    	rect.setAttribute ("style", "stroke-dasharray: 6, 3;");
	    }
		
		return shp;
	}

	
	protected SVGShape getRect(double x, double y, double width, double height) {
		SVGShape rect = new SVGRectangle(x, y, width, height);
	    Color c = mg.getUsualView().getPaint().getColor();    
	    rect.setAttribute("fill", SVGUtil.getHexColor(c));
//	    rect.setAttributeNS (null, "fill-opacity", "0.5");
	    rect.setAttribute("stroke", "black");
	    rect.setAttribute("stroke-width", "1");
	    
	    if ( this.mg.isHypothetical() ) {
	    	rect.setAttribute ("style", "stroke-dasharray: 6, 3;");
	    }
		return rect;
	}

	
	
	public SVGShape buildSVGShape() {
		SVGComplexShape complex = new SVGComplexShape();
		complex.setAttribute("id", mg.getIdAlias());
		complex.setAttribute("style", "text-rendering: auto;");
		
		if ( !SVGConfig.omitJavascript )
	    	complex.setAttribute("onclick", "infoWindow(\"" + mg.getIdAlias() + "\"," +
					 "\"" + mg.getName() + "\","+
					 "\"" + "Gene" + "\"" +
					 ");");		
		
	    int h = mg.getHomodimer();	    
	    double heightEach = mg.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = mg.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
	    this.center = new Point2D.Double( mg.getBounds().getX() + widthEach/2,
				  mg.getBounds().getY() + heightEach/2);
	    
	    SVGShape shp;
	    for (int hc = h-1 ; hc>=0 ; hc--) {	    		    	
	    	shp = this.getRect(
	    				 mg.getBounds().getX() + HOMODIMER_WIDTH*hc  , 
	    				 mg.getBounds().getY() + HOMODIMER_HEIGHT*hc  , 
	    				 widthEach , 
	    				 heightEach 
	    		);  	    	
	    	
	    	complex.add(shp);	    	    	
	    }		
		
	    addRegions(complex, mg.getBounds());
	    
//	    complex.add( new SVGText( SVGTextRenderer.drawText(svgDoc, 
//							    		mg.getBounds().getX() + 
//							    		widthEach / 2 , 
//							    		mg.getBounds().getY() + 
//							    		heightEach / 2 , 
//							    		mg.getName() )));   
		complex.add( new SVGText(
				SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
		    		center.x , center.y, 
		    		mg.getName() ,
		    		12 )));
		
	    complex.add( getInfoUnit(
	    		new Rectangle2D.Double( this.getMspecies().getBounds().getX() , 
	    								this.getMspecies().getBounds().getY(), 
	    								widthEach, 
	    								heightEach )
	    		) );
		
		return complex;
	}
	
	public SVGShape buildSVGShapeSBGN() throws SBML2SVGException {
		SVGComplexShape complex = new SVGComplexShape();
		complex.setAttribute("id", mg.getIdAlias());
		complex.setAttribute("style", "text-rendering: auto;");
		
		if ( !SVGConfig.omitJavascript )
	    	complex.setAttribute("onclick", "infoWindow(\"" + mg.getIdAlias() + "\"," +
					 "\"" + mg.getName() + "\","+
					 "\"" + "Gene" + "\"" +
					 ");");		
		
	    int h = Math.min( mg.getHomodimer(), 2 ); 
		
	    double heightEach = mg.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = mg.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
	    this.center = new Point2D.Double( mg.getBounds().getX() + widthEach/2,
				  mg.getBounds().getY() + heightEach/2);
	    
	    SVGShape shp;
	    for (int hc = h-1 ; hc>=0 ; hc--) {	    
	    	if ( this.isClone() ) {
		    	shp = this.getClonedRectSBGN(
		    			mg.getBounds().getX() + HOMODIMER_WIDTH*hc  , 
		    			mg.getBounds().getY() + HOMODIMER_HEIGHT*hc  , 
		    			widthEach , 
		    			heightEach 
		    		);	    		
	    	} else {
		    	shp = this.getRectSBGN(
		    				 mg.getBounds().getX() + HOMODIMER_WIDTH*hc  , 
		    				 mg.getBounds().getY() + HOMODIMER_HEIGHT*hc  , 
		    				 widthEach , 
		    				 heightEach 
		    		);  	    	
	    	}
	    	
	    	complex.add(shp);	    	    	
	    }		
		
	    addRegions(complex, mg.getBounds());
	    
//	    complex.add( new SVGText( SVGTextRenderer.drawText(svgDoc, 
//							    		mg.getBounds().getX() + 
//							    		widthEach / 2 , 
//							    		mg.getBounds().getY() + 
//							    		heightEach / 2 , 
//							    		mg.getName() )));   
		complex.add( new SVGText(
				SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
		    		center.x , center.y, 
		    		mg.getName() ,
		    		12 )));
		
	    complex.add( getInfoUnit(
	    		new Rectangle2D.Double( this.getMspecies().getBounds().getX() , 
	    								this.getMspecies().getBounds().getY(), 
	    								widthEach, 
	    								heightEach )
	    		) );
		
		return complex;
	}
	
//	public Element getSVGShapeOld() {
//    	Element svgSpecie = svgDoc.createElementNS(svgNS, "g");
//    	svgSpecie.setAttribute("id", mg.getIdAlias());
//    	svgSpecie.setAttribute("style", "text-rendering: auto;");
//	    
//	    int h = mg.getHomodimer();	    
//	    double heightEach = mg.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
//	    double widthEach = mg.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
//	    this.center = new Point2D.Double( mg.getBounds().getX() + widthEach/2,
//				  						  mg.getBounds().getY() + heightEach/2);
//
//	    for (int hc = h-1 ; hc>=0 ; hc--) {	    		    	
//	    	Element sq = this.getRectOld(
//	    				 mg.getBounds().getX() + HOMODIMER_WIDTH*hc  , 
//	    				 mg.getBounds().getY() + HOMODIMER_HEIGHT*hc  , 
//	    				 widthEach , 
//	    				 heightEach 
//	    		);  
//	    	
//	    	svgSpecie.appendChild(sq);	    	    	
//	    }
//	    
//	    
//	    
//	    Element text = SVGTextRenderer.drawText(svgDoc, 
//	    		mg.getBounds().getX() + 
//	    		widthEach / 2 , 
//	    		mg.getBounds().getY() + 
//	    		heightEach / 2 , 
//	    		mg.getName() );    
//	    
//	    svgSpecie.appendChild (text);	
//	    
//	    return svgSpecie;
//	}

	@Override
	public Rectangle2D getBBox() {
		return this.bounds;
	}

	@Override
	public Point2D getCenter() {
		return center;
	}
	
	protected void addRegions(SVGComplexShape complex, Rectangle2D rect) {
	    if ((mg.getRegions()!=null)) {
	    	SVGShape rgn = null;	    	
	    	for (MRegion mr : mg.getRegions()) {
	    		if ( mr.getType() == MRegion.CODING_REGION ) {
	    			rgn = getCodingRegion(mr, rect);
	    			complex.add(rgn);
	    		} else if ( mr.getType() == MRegion.REGULATORY_REGION ) {
	    			rgn = getRegulatoryRegion(mr, rect);
	    			complex.add(rgn);
	    		} 
	    		
	    		    			    		
	    	}
	    	for (MRegion mr : mg.getRegions()) {
	    		if ( mr.getType() == MRegion.TRANSCRIPTION_STARTING_SITE_LEFT ) {//	    			
	    			rgn = getTranscriptionL(mr, rect);
	    			complex.add(rgn);
	    		} else if ( mr.getType() == MRegion.TRANSCRIPTION_STARTING_SITE_RIGHT ) {
	    			rgn = getTranscriptionR(mr, rect);
	    			complex.add(rgn);
	    		} else if ( mr.getType() == MRegion.MODIFICATION_SITE ) {
	    			rgn = getModificationSite(mr, rect);
	    			complex.add(rgn);
	    		}
	    		   			    		
	    	}
	    	
	    	
	    }
	    
	}
	
	protected SVGShape getCodingRegion(MRegion mr, Rectangle2D rect) {
		if ( SVGConfig.SBGNMode ) return getCodingRegionSBGN( mr, rect );
		SVGComplexShape shp = new SVGComplexShape(
				                new SVGRectangle( rect.getX() + rect.getWidth()*mr.getPos(), rect.getY()-8, rect.getWidth()*mr.getSize(), 16)
				                );
		shp.setAttribute("stroke", "black");
		shp.setAttribute("fill", "white");
		shp.setAttribute("stroke-linejoin", "bevel");
		shp.setAttribute("stroke-width", "1");
		if (mr.getName()!=null && !mr.getName().equals("")) {
			   SVGText resText = new SVGText( 
					SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
							                                       getRegionPosX(mr, rect)+ 
							                                       getRegionWidth(mr, rect)/2, 
							                                       rect.getY()+15,
																   mr.getName(), 
																   9 ));
			   resText.setAttribute("fill", "black");
			   shp.add(resText);
		}
		return shp;
	}

	protected SVGShape getCodingRegionSBGN(MRegion mr, Rectangle2D rect) {
		SVGComplexShape shp = new SVGComplexShape();
				
		SVGShape svgRect = new SVGRectangle( rect.getX() + rect.getWidth()*mr.getPos(), rect.getY()-8, rect.getWidth()*mr.getSize(), 16);

		svgRect.setAttribute("stroke", "black");
		svgRect.setAttribute("fill", "white");
		svgRect.setAttribute("stroke-linejoin", "bevel");
		svgRect.setAttribute("stroke-width", "1");

		shp.add( svgRect );


		SVGText resText = new SVGText( 
				SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
						                                       getRegionPosX(mr, rect)+ 
						                                       getRegionWidth(mr, rect)/2, 
						                                       rect.getY(),
															   "ct:coding", 
															   9 ));
		resText.setAttribute("fill", "black");
		shp.add(resText);
			
		if (mr.getName()!=null && !mr.getName().equals("")) {
			   resText = new SVGText( 
					SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
							                                       getRegionPosX(mr, rect)+ 
							                                       getRegionWidth(mr, rect)/2, 
							                                       rect.getY()+15,
																   mr.getName(), 
																   9 ));
			   resText.setAttribute("fill", "black");
			   shp.add(resText);
		}
		
		return shp;
	}

	
	protected SVGShape getRegulatoryRegion(MRegion mr, Rectangle2D rect) {
		if ( SVGConfig.SBGNMode ) return getRegulatoryRegionSBGN( mr, rect );
		
		SVGComplexShape shp = new SVGComplexShape(
								new SVGRectangle( rect.getX() + rect.getWidth()*mr.getPos(), rect.getY()-8, rect.getWidth()*mr.getSize(), 16)
								);
		shp.setAttribute("stroke", "black");
		shp.setAttribute("fill", "white");
		shp.setAttribute("stroke-linejoin", "bevel");
		shp.setAttribute("stroke-width", "1");
		if (mr.getName()!=null && !mr.getName().equals("")) {
			   SVGText resText = new SVGText( 
					SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
							                                       getRegionPosX(mr, rect)+ 
							                                       getRegionWidth(mr, rect)/2, 
							                                       rect.getY(),
																   mr.getName(), 
																   9 ));
			   resText.setAttribute("fill", "black");
			   shp.add(resText);
		}
		
		return shp;
	}
	
	protected SVGShape getRegulatoryRegionSBGN(MRegion mr, Rectangle2D rect) {
		
		SVGComplexShape shp = new SVGComplexShape(
								new SVGRectangle( rect.getX() + rect.getWidth()*mr.getPos(), rect.getY()-8, rect.getWidth()*mr.getSize(), 16)
								);
		shp.setAttribute("stroke", "black");
		shp.setAttribute("fill", "white");
		shp.setAttribute("stroke-linejoin", "bevel");
		shp.setAttribute("stroke-width", "1");
		
	   SVGText resText = new SVGText( 
				SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
						                                       getRegionPosX(mr, rect)+ 
						                                       getRegionWidth(mr, rect)/2 + 2, 
						                                       rect.getY(),
															   "ct:regulatory", 
															   9 ));
	   resText.setAttribute("fill", "black");
	   shp.add(resText);
			   
		if (mr.getName()!=null && !mr.getName().equals("")) {
			   resText = new SVGText( 
					SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
							                                       getRegionPosX(mr, rect)+ 
							                                       getRegionWidth(mr, rect)/2, 
							                                       rect.getY() + 14 ,
																   mr.getName(), 
																   9 ));
			   resText.setAttribute("fill", "black");
			   shp.add(resText);
		}
		
		return shp;
	}
	
	public Point2D getLinkAnchor(int type) {
		Point2D p = null;
		
	    int h = mg.getHomodimer();	    
	    double heightEach = mg.getBounds().getHeight() - HOMODIMER_HEIGHT*(h-1);
	    double widthEach = mg.getBounds().getWidth() - HOMODIMER_WIDTH*(h-1);
	    
		switch (type) {
		case SVGSpecie.ANCHOR_N:
			p = new Point2D.Double(mg.getBounds().getX() + widthEach/2,
								   mg.getBounds().getY() );
			break;
		case SVGSpecie.ANCHOR_NNE:	
			p = new Point2D.Double(mg.getBounds().getX() + 3*widthEach/4 ,
                    			   mg.getBounds().getY() );
			break;			
		case SVGSpecie.ANCHOR_NE:
			p = new Point2D.Double(mg.getBounds().getX() + widthEach ,
                    			   mg.getBounds().getY() );
			break;
		case SVGSpecie.ANCHOR_ENE:
			p = new Point2D.Double(mg.getBounds().getX() + widthEach ,
								   mg.getBounds().getY() + heightEach/4 );
			break;				
		case SVGSpecie.ANCHOR_E:
			p = new Point2D.Double(mg.getBounds().getX() + widthEach ,
		                           mg.getBounds().getY() + heightEach/2 );
			break;
		case SVGSpecie.ANCHOR_ESE:
			p = new Point2D.Double(mg.getBounds().getX() + widthEach ,
					   			   mg.getBounds().getY() + 3*heightEach/4 );
			break;				
		case SVGSpecie.ANCHOR_SE:
			p = new Point2D.Double(mg.getBounds().getX() + widthEach ,
					   			   mg.getBounds().getY() + heightEach );
			break;				
		case SVGSpecie.ANCHOR_SSE:
			p = new Point2D.Double(mg.getBounds().getX() + 3*widthEach/4 ,
					   			   mg.getBounds().getY() + heightEach );
			break;				
		case SVGSpecie.ANCHOR_S:
			p = new Point2D.Double(mg.getBounds().getX() + widthEach/2,
					               mg.getBounds().getY() + heightEach );
			break;
		case SVGSpecie.ANCHOR_SSW:
			p = new Point2D.Double(mg.getBounds().getX() + widthEach/4 ,
					   			   mg.getBounds().getY() + heightEach );
			break;				
		case SVGSpecie.ANCHOR_SW:
			p = new Point2D.Double(mg.getBounds().getX() ,
								   mg.getBounds().getY() + heightEach );
			break;				
		case SVGSpecie.ANCHOR_WSW:
			p = new Point2D.Double(mg.getBounds().getX() ,
	   				   			   mg.getBounds().getY() + 3*heightEach/4 );
			break;			
		case SVGSpecie.ANCHOR_W:
			p = new Point2D.Double(mg.getBounds().getX() ,
                    			   mg.getBounds().getY() + heightEach/2 );
			break;
		case SVGSpecie.ANCHOR_WNW:
			p = new Point2D.Double(mg.getBounds().getX() ,
     			   				   mg.getBounds().getY() + heightEach/4 );
			break;			
		case SVGSpecie.ANCHOR_NW:
			p = new Point2D.Double(mg.getBounds().getX() ,
								   mg.getBounds().getY() );
			break;			
		case SVGSpecie.ANCHOR_NNW:
			p = new Point2D.Double(mg.getBounds().getX() + widthEach/4 ,
	   				   			   mg.getBounds().getY() );
			break;					
		}
		
		return p;
	}	
	
	protected SVGShape getModificationSite(MRegion mr, Rectangle2D rect) {
		if ( SVGConfig.SBGNMode ) return getModificationSiteSBGN( mr, rect );
		SVGComplexShape shp = new SVGComplexShape();
		shp.setAttribute("stroke", "black");
		shp.setAttribute("stroke-width", "1");
		double cx, cy;
		cx = rect.getX() + rect.getWidth()*mr.getPos();
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
		
		if ( mr.getName()!=null ) {
			   SVGText nameText = new SVGText( 
					SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
																   cx, // getRegionPosX(mr, rect)+ 
							                                       cy - 12, // getRegionWidth(mr, rect)/2, 
							                                       // rect.getY()+6,
																   mr.getName(), 
																   9 ));
			   resText.setAttribute("fill", "black");
			   shp.add(nameText);
		}    	
		
//		SVGText resText = new SVGText( SVGTextRenderer.getInstance().drawTextCentered(svgDoc, p.x, p.y , mres.getState(), 9 ));
//    	shp.add(resText);
    	
		return shp;
	}
	
	protected SVGShape getModificationSiteSBGN(MRegion mr, Rectangle2D rect) {
		
		SVGComplexShape shp = new SVGComplexShape();
		
		shp.add( new SVGRectangle( rect.getX() + rect.getWidth()*mr.getPos(), rect.getY()-8, 
									 20 , 16) );
		shp.setAttribute("stroke", "black");
		shp.setAttribute("fill", "white");
		shp.setAttribute("stroke-linejoin", "bevel");
		shp.setAttribute("stroke-width", "1");
		
	   SVGText resText = new SVGText( 
				SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
						                                       getRegionPosX(mr, rect)+ 10, 
						                                       rect.getY(),
															   mr.getState(), 
															   9 ));
	   resText.setAttribute("fill", "black");
	   shp.add(resText);
			   
		if (mr.getName()!=null && !mr.getName().equals("")) {
			   resText = new SVGText( 
					SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
																   getRegionPosX(mr, rect)+ 10, 
							                                       rect.getY() - 16 ,
																   mr.getName(), 
																   9 ));
			   resText.setAttribute("fill", "black");
			   shp.add(resText);
		}
		
		return shp;
	}
	
	protected SVGShape getTranscriptionL(MRegion mr, Rectangle2D rect) {
		if ( SVGConfig.SBGNMode ) return getTranscriptionSBGN( mr , rect );
		
		SVGComplexShape shp = new SVGComplexShape();
		shp.setAttribute("stroke", "black");
		shp.setAttribute("stroke-linecap", "round");
		shp.setAttribute("stroke-width", "1");
		double leftmostX = rect.getX() + rect.getWidth()*mr.getPos();
		double rightmostX = leftmostX + rect.getWidth()*mr.getSize();
		
		shp.add( new SVGLine( leftmostX, rect.getY(), leftmostX, rect.getY() - 8		
							) );
		shp.add( new SVGLine( leftmostX, rect.getY() - 8 , rightmostX - 1 , rect.getY() - 8	
				            ) );
		Vector<Point2D> vertices = new Vector<Point2D>();
		vertices.add( new Point2D.Double( rightmostX ,
										  rect.getY() - 8
									    ));
		vertices.add( new Point2D.Double( rightmostX - 8 ,
										  rect.getY() - 13
			                            ));
		vertices.add( new Point2D.Double( rightmostX - 8 ,
				  						  rect.getY() - 3
              							));
		SVGPolygon triangle = new SVGPolygon( vertices );
		triangle.setAttribute("stroke", "none");
		triangle.setAttribute("fill", "black");
//		triangle.setAttribute("stroke", "none");
		shp.add( triangle );
		return shp;
	}

	
	protected SVGShape getTranscriptionR(MRegion mr, Rectangle2D rect) {
		if ( SVGConfig.SBGNMode ) return getTranscriptionSBGN( mr , rect );
		
		SVGComplexShape shp = new SVGComplexShape();
		shp.setAttribute("stroke", "black");
		shp.setAttribute("stroke-linecap", "round");
		shp.setAttribute("stroke-width", "1");
		double leftmostX = rect.getX() + rect.getWidth()*mr.getPos();
		double rightmostX = leftmostX + rect.getWidth()*mr.getSize() ;
		
		shp.add( new SVGLine(rightmostX, rect.getY(),
							 rightmostX, rect.getY() - 8		
							) );
		shp.add( new SVGLine( rightmostX, rect.getY() - 8 ,
							  leftmostX + 1 , rect.getY() - 8	
				            ) );
		Vector<Point2D> vertices = new Vector<Point2D>();
		vertices.add( new Point2D.Double( leftmostX ,
										  rect.getY() - 8
									    ));
		vertices.add( new Point2D.Double( leftmostX + 8 ,
										  rect.getY() - 13
			                            ));
		vertices.add( new Point2D.Double( leftmostX + 8 ,
				  						  rect.getY() - 3
              							));
		SVGPolygon triangle = new SVGPolygon( vertices );
		triangle.setAttribute("stroke", "none");
		triangle.setAttribute("fill", "black");
//		triangle.setAttribute("stroke", "none");
		shp.add( triangle );
		return shp;
	}
	
	protected SVGShape getTranscriptionSBGN(MRegion mr, Rectangle2D rect) {
		SVGComplexShape shp = new SVGComplexShape();
				
		SVGShape svgRect = new SVGRectangle( rect.getX() + rect.getWidth()*mr.getPos(), rect.getY()-8, rect.getWidth()*mr.getSize(), 16);

		svgRect.setAttribute("stroke", "black");
		svgRect.setAttribute("fill", "white");
		svgRect.setAttribute("stroke-linejoin", "bevel");
		svgRect.setAttribute("stroke-width", "1");

		shp.add( svgRect );


		String labelText;
		if ( mr.getType() == MRegion.TRANSCRIPTION_STARTING_SITE_LEFT ) {
			labelText = "ct:tssL";
		} else {
			labelText = "ct:tssR";
		}
		
		SVGText resText = new SVGText( 
				SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
						                                       getRegionPosX(mr, rect)+ 
						                                       getRegionWidth(mr, rect)/2, 
						                                       rect.getY(),
						                                       labelText, 
															   9 ));
		resText.setAttribute("fill", "black");
		shp.add(resText);
					
		return shp;
	}

	protected double getRegionPosX(MRegion mr, Rectangle2D rect){
		return rect.getX() + rect.getWidth() * mr.getPos();
	}
	
	protected double getRegionWidth(MRegion mr, Rectangle2D rect){
		return rect.getWidth() * mr.getSize();
	}

	@Override
	public String getIdAlias() {
		return this.mg.getIdAlias();
	}
	
	@Override
	protected Point2D getPointOnRect(Rectangle2D rect, double angle ) {
		/* Calcular punto de interseccion con el angulo dado */
		SVGRectangle frame = new SVGRectangle( -rect.getWidth()/2, -rect.getHeight()/2,
												rect.getWidth(), rect.getHeight()  );
		// Punto externo al rectangulo
		Point2D pExt= new Point2D.Double( 100000 * Math.cos( angle ) , 100000 * Math.sin( angle ) );
		Segment s = new Segment( pExt.getX(), pExt.getY() , 0, 0 );
		Point2D pointAtAngle = frame.intersection( s );
		Point2D p = new Point2D.Double( rect.getCenterX() + pointAtAngle.getX(),
				                rect.getCenterY() + pointAtAngle.getY() );
		
		return p;
	}
}
