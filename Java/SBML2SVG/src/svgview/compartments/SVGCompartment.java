package svgview.compartments;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import model.ECompartment;
import model.MCompartment;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import svgcontroller.SBML2SVGException;
import svgview.SVGPaintable;
import svgview.shapes.SVGComplexShape;
import svgview.shapes.SVGCustomShape;
import svgview.shapes.SVGEllipse;
import svgview.shapes.SVGRectangle;
import svgview.shapes.SVGShape;
import svgview.shapes.SVGText;
import svgview.shapes.Segment;
import svgview.util.SVGTextRenderer;
import svgview.util.SVGUtil;
import celldesignerparse_4_0.commondata.DoubleLine;

public class SVGCompartment implements SVGPaintable {
	public static final String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
	public static final int CORNER_RADIUS = 20;
	public static final int OPEN_CORNER_RADIUS = 34;
	public static final double BEZIER_FACTOR = 0.55228474983; 	// Para aproximar esquinas redondeadas con curvas de Bezier
																// equivale a 4*(sqrt(2)-1)/3
	private Dimension size;
	
	private MCompartment mc;
	private Document svgDoc;
	
	public SVGCompartment(Document svgDoc , MCompartment mc, Dimension size) {
		this.svgDoc = svgDoc;
		this.mc = mc;		
		this.size = size;
	}
	
	private Element getOuterRect(double x, double y, double width, double height, double thickness ) {
		return getRect(x, y, width, height, thickness, false);		
	}
	
	private Element getInnerRect(double x, double y, double width, double height, double thickness ) {
		return getRect(x, y, width, height, thickness, true);		
	}

	private Element getOuterOval(double x, double y, double width, double height, double thickness ) {
		Element oval = this.svgDoc.createElementNS (svgNS, "ellipse");
		double stroke = ((DoubleLine)mc.getLine()).getOuterWidth();;
		Color c = mc.getPaint().getColor();
		oval.setAttribute("cx", ""+ (x + width/2) );
		oval.setAttribute("cy", ""+ (y + height/2 ) );
		oval.setAttribute("rx", "" + (width/2 ) );
		oval.setAttribute("ry", "" + (height/2 ) );
//		oval.setAttribute("stroke", SVGUtil.getHexColor(c));
		oval.setAttribute("style", "fill: none;stroke:" + SVGUtil.getHexColor(c) + ";" +
				                   "stroke-width:"+stroke+ ";" );
		return oval;
	}	
	
	private Element getInnerOval(double x, double y, double width, double height, double thickness ) {
		Element oval = this.svgDoc.createElementNS (svgNS, "ellipse");
		Color c = mc.getPaint().getColor();
		oval.setAttribute("cx", ""+ (x + width/2) );
		oval.setAttribute("cy", ""+ (y + height/2 ) );
		oval.setAttribute("rx", "" + (width/2 - thickness) );
		oval.setAttribute("ry", "" + (height/2 - thickness) );
//		oval.setAttribute("stroke", SVGUtil.getHexColor(c));
		oval.setAttribute("style", "fill: none;stroke:" + SVGUtil.getHexColor(c) + ";" +
				                   "stroke-width:1;" );
		return oval;
	}
	
	private Element getFillOval(double x, double y, double width, double height, double thickness ) {
		Element oval = this.svgDoc.createElementNS (svgNS, "ellipse");
		Color c = mc.getPaint().getColor();
		oval.setAttribute("cx", ""+ (x + width/2) );
		oval.setAttribute("cy", ""+ (y + height/2 ) );
		oval.setAttribute("rx", "" + (width - thickness)/2 );
		oval.setAttribute("ry", "" + (height - thickness)/2 );
//		oval.setAttribute("stroke", SVGUtil.getHexColor(c));
		oval.setAttribute("style", "fill: none; stroke-opacity:0.249;stroke:" + SVGUtil.getHexColor(c) + ";" +
				                   "stroke-width:" + thickness + ";");
		return oval;
	}
	
	
	private Element getFill(double x, double y, double width, double height) {		
		Element fill = this.svgDoc.createElementNS (svgNS, "rect");
		Color c = mc.getPaint().getColor();
		fill.setAttributeNS (null, "fill", SVGUtil.getHexColor(c));
		fill.setAttributeNS (null, "style", "fill-opacity: 0.498; opacity:0.498; stroke:none;");
		fill.setAttributeNS (null, "width", Double.toString(width));
		fill.setAttributeNS (null, "height", Double.toString(height));
		fill.setAttributeNS (null, "x", Double.toString(x));
		fill.setAttributeNS (null, "y", Double.toString(y));
		return fill;
	}
	
	private Element getFillRect(double x, double y, double width, double height, double thickness ) {
		double ix, iy, iwidth, iheight, innerCornerRadius;
	    
		double cornerRadius;
	    if ( mc.getClassOf().equals(ECompartment.SQUARE) ) {
	    	cornerRadius = CORNER_RADIUS;
	    	innerCornerRadius = cornerRadius - thickness/2;
	    } else {
	    	cornerRadius = OPEN_CORNER_RADIUS;
	    	innerCornerRadius = cornerRadius - thickness;
	    }		
			
		ix = x + thickness;
		iy = y + thickness;
		iwidth = width - thickness*2;
		iheight = height - thickness*2;
		
		Element rect = this.svgDoc.createElementNS (svgNS, "path");
		Color c = mc.getPaint().getColor();
		rect.setAttributeNS (null, "fill", SVGUtil.getHexColor(c));
		rect.setAttributeNS (null, "style", "fill-opacity: 0.498; opacity:0.498; stroke:none;");
		rect.setAttributeNS (null, "d", 
				"M" + Double.toString(x+cornerRadius) + " " + Double.toString(y) + " " +    
				"L" + Double.toString(x+width-cornerRadius) + " " + Double.toString(y) + " " +
				
				"C" + Double.toString(x+width-cornerRadius*(1-BEZIER_FACTOR)) + " " + Double.toString(y) + " " +
				      Double.toString(x+width) + " " + Double.toString(y+cornerRadius*(1-BEZIER_FACTOR)) + " " +
				      Double.toString(x+width) + " " + Double.toString(y+cornerRadius) + " " +
			    "L" + Double.toString(x+width) + " " + Double.toString(y+height-cornerRadius) + " " +
				
			    "C" + Double.toString(x+width) + " " + Double.toString(y+height-cornerRadius*(1-BEZIER_FACTOR)) + " " +
			      	  Double.toString(x+width-cornerRadius*(1-BEZIER_FACTOR)) + " " + Double.toString(y+height) + " " +
			      	  Double.toString(x+width-cornerRadius) + " " + Double.toString(y+height) + " " +
			    
			    "L" + Double.toString(x+cornerRadius) + " " + Double.toString(y+height) + " " +  
			    
			    "C" + Double.toString(x+cornerRadius*(1-BEZIER_FACTOR)) + " " + Double.toString(y+height) + " " +
		      	  	  Double.toString(x) + " " + Double.toString(y+height-cornerRadius*(1-BEZIER_FACTOR)) + " " +
		      	  	  Double.toString(x) + " " + Double.toString(y+height-cornerRadius) + " " +			    
			      
		      	"L" + Double.toString(x) + " " + Double.toString(y+cornerRadius) + " " +

			    "C" + Double.toString(x) + " " + Double.toString(y+cornerRadius*(1-BEZIER_FACTOR)) + " " +
	      	  	      Double.toString(x+cornerRadius*(1-BEZIER_FACTOR)) + " " + Double.toString(y) + " " +
	      	  	      Double.toString(x+cornerRadius) + " " + Double.toString(y) + " " +
	      	  	      
  				"M" + Double.toString(ix+innerCornerRadius) + " " + Double.toString(iy) + " " +   
  				
  				"C" + Double.toString(ix+innerCornerRadius*(1-BEZIER_FACTOR)) + " " + Double.toString(iy) + " " +
  					  Double.toString(ix) + " " + Double.toString(iy+innerCornerRadius*(1-BEZIER_FACTOR)) + " " +
    	  	          Double.toString(ix) + " " + Double.toString(iy+innerCornerRadius) + " " +    	  	          
    	  	          
  				
				"L" + Double.toString(ix) + " " + Double.toString(iy+iheight-innerCornerRadius) + " " +
				
				"C" + Double.toString(ix) + " " + Double.toString(iy+iheight-innerCornerRadius*(1-BEZIER_FACTOR)) + " " +
					  Double.toString(ix+innerCornerRadius*(1-BEZIER_FACTOR)) + " " + Double.toString(iy+iheight) + " " +
					  Double.toString(ix+innerCornerRadius) + " " + Double.toString(iy+iheight) + " " +
					  
				"L" + Double.toString(ix+iwidth-innerCornerRadius) + " " + Double.toString(iy+iheight) + " " +

				"C" + Double.toString(ix+iwidth-innerCornerRadius*(1-BEZIER_FACTOR)) + " " + Double.toString(iy+iheight) + " " +
				      Double.toString(ix+iwidth) + " " + Double.toString(iy+iheight-innerCornerRadius*(1-BEZIER_FACTOR)) + " " +
				      Double.toString(ix+iwidth) + " " + Double.toString(iy+iheight-innerCornerRadius) + " " +
				   
				"L" + Double.toString(ix+iwidth) + " " + Double.toString(iy+innerCornerRadius) + " " +
				
				"C" + Double.toString(ix+iwidth) + " " + Double.toString(iy+innerCornerRadius*(1-BEZIER_FACTOR)) + " " +
					  Double.toString(ix+iwidth-innerCornerRadius*(1-BEZIER_FACTOR)) + " " + Double.toString(iy) + " " +
					  Double.toString(ix+iwidth-innerCornerRadius) + " " + Double.toString(iy) + " " +
				
				"Z"
		);
		
		return rect;
	}
	
	private Element getLine(double x1, double y1, double x2, double y2, double stroke) {
	    Element line = this.svgDoc.createElementNS (svgNS, "line");
	    Color c = mc.getPaint().getColor();
	    line.setAttributeNS (null, "stroke", SVGUtil.getHexColor(c));
	    line.setAttributeNS (null, "stroke-width", Double.toString(stroke));
	    line.setAttributeNS (null, "x1", "" + x1 );
	    line.setAttributeNS (null, "y1", "" + y1 );
	    line.setAttributeNS (null, "x2", "" + x2 );
	    line.setAttributeNS (null, "y2", "" + y2 );
	    
	    
	    return line;
	}
	
	
	private Element getRect(double x, double y, double width, double height, double thickness, boolean inner) {		
	    Element rect = this.svgDoc.createElementNS (svgNS, "rect");
	    Color c = mc.getPaint().getColor();
	    double stroke = 0;
	    if (!inner) {
		    if (mc.getLine() instanceof DoubleLine) {
		    	stroke = ((DoubleLine)mc.getLine()).getOuterWidth();
		    } else {
		    	stroke = 1;
		    }		    
		    
	    } else {	    
		    if (mc.getLine() instanceof DoubleLine) {
		    	stroke = ((DoubleLine)mc.getLine()).getInnerWidth();
		    } else {
		    	stroke = 1;
		    }
		    
	    }	    

	    try {
		    if ( width  < 0 || height < 0) {
		    	throw new SBML2SVGException("SVGCompartment " + this.mc.getIdAlias() +
		    			"(" + this.mc.getId() + ") tiene dimensiones negativas");
		    }
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	    
	    rect.setAttributeNS (null, "fill", "none");
	    rect.setAttributeNS (null, "stroke", SVGUtil.getHexColor(c));
	    
	    rect.setAttributeNS (null, "stroke-width", Double.toString(stroke)); 
	    rect.setAttributeNS (null, "width", Double.toString(width));
	    rect.setAttributeNS (null, "height", Double.toString(height));
	    rect.setAttributeNS (null, "x", Double.toString(x));
	    rect.setAttributeNS (null, "y", Double.toString(y));
	    
	    double cornerRadius;
	    if ( mc.getClassOf().equals(ECompartment.SQUARE) ) {
	    	cornerRadius = CORNER_RADIUS;
	    } else {
	    	cornerRadius = OPEN_CORNER_RADIUS;
	    }
	    
	    
	    if (!inner) {
	    	rect.setAttributeNS (null, "rx", Double.toString(cornerRadius));
		    rect.setAttributeNS (null, "ry", Double.toString(cornerRadius));	
	    } else {
	    	// Los 'compartments' abiertos dibujan sus esquinas de forma distinta:
	    	if ( mc.getClassOf().equals(ECompartment.SQUARE) ) {
		    	rect.setAttributeNS (null, "rx", Double.toString(cornerRadius-thickness/2));
			    rect.setAttributeNS (null, "ry", Double.toString(cornerRadius-thickness/2));
	    	} else {
	    		rect.setAttributeNS (null, "rx", Double.toString(cornerRadius-thickness));
			    rect.setAttributeNS (null, "ry", Double.toString(cornerRadius-thickness));
	    	}
	    }
	    		
		return rect;
	}	

	public SVGShape getSVGShape() {
		SVGComplexShape shape = new SVGComplexShape();
    	
		// System.out.println("Compartment " + mc.getId() + " inside of " + mc.getOutside() );
		
    	double thickness;
    	if (mc.getLine() instanceof DoubleLine) {
    		thickness = ((DoubleLine)mc.getLine()).getThickness();
	    } else {
	    	// Default
	    	thickness = 10;
	    };
	    
		switch (mc.getClassOf()) {
		case OVAL:
			shape.add( getSVGShapeOval( thickness ) );
			break;
		case SQUARE_CLOSEUP_WEST:
			shape.add( getSVGShapeCloseUpWest( thickness ));
			break;
		case SQUARE_CLOSEUP_EAST:
			shape.add(getSVGShapeCloseUpEast( thickness ));
			break;			
		case SQUARE_CLOSEUP_SOUTH:
			shape.add(getSVGShapeCloseUpSouth( thickness ));
			break;
		case SQUARE_CLOSEUP_NORTH:
			shape.add(getSVGShapeCloseUpNorth( thickness ));
			break;
		case SQUARE_CLOSEUP_SOUTHWEST:	
			shape.add(getSVGShapeCloseUpSW( thickness ));
			break;
		case SQUARE_CLOSEUP_NORTHEAST:
			shape.add(getSVGShapeCloseUpNE( thickness ));
			break;
		case SQUARE_CLOSEUP_NORTHWEST:
			shape.add(getSVGShapeCloseUpNW( thickness ));
			break;
		case SQUARE_CLOSEUP_SOUTHEAST:
			shape.add(getSVGShapeCloseUpSE( thickness ));
			break;
		default:
			shape.add(getSVGShapeRect( thickness ));
		}
		
		Element text;
		
		if ( this.mc.getNamePoint() == null ) {
		    text = SVGTextRenderer.getInstance().drawTextCentered(svgDoc, 
		    		mc.getBounds().getX() + 
		    		mc.getBounds().getWidth() / 2 , 
		    		mc.getBounds().getY() +
		    		mc.getBounds().getHeight() - thickness - 9, 
		    		mc.getName(),
		    		12);    
		} else {	    
			text = SVGTextRenderer.getInstance().drawTextBelow(svgDoc, 
				this.mc.getNamePoint().getX() , 
				this.mc.getNamePoint().getY(), 
	    		mc.getName(),
	    		12);
		}
		
		shape.add ( new SVGText( text ) );
	    
		shape.add( getInfoUnit( mc.getBounds() ) );
		
		return shape;
	}
	
	private SVGShape getSVGShapeCloseUpNorth(double thickness) {
    	Element svgSpecie = svgDoc.createElementNS(svgNS, "g");
    	svgSpecie.setAttribute("id", mc.getIdAlias());
    	svgSpecie.setAttribute("style", "text-rendering: auto;");
    	
    	Element fill = getFill(0, mc.getBounds().getY(), 
    						   this.size.getWidth(), thickness );
    	
    	svgSpecie.appendChild( fill );
    	
    	double stroke;
	    if (mc.getLine() instanceof DoubleLine) {
	    	stroke = ((DoubleLine)mc.getLine()).getOuterWidth();
	    } else {
	    	stroke = 1;
	    }	    	
    	Element lineUp = getLine(0, mc.getBounds().getY(),
    			            	this.size.getWidth(), mc.getBounds().getY(),
    			                stroke);
    	svgSpecie.appendChild( lineUp );

	    if (mc.getLine() instanceof DoubleLine) {
	    	stroke = ((DoubleLine)mc.getLine()).getInnerWidth();
	    } else {
	    	stroke = 1;
	    }	    	
    	Element lineDown = getLine(0, mc.getBounds().getY() + thickness,
    			            	this.size.getWidth(), mc.getBounds().getY() + thickness,
    			                stroke);
    	svgSpecie.appendChild( lineDown );    	
    	
    	return new SVGCustomShape( svgSpecie );
	}
	
	private SVGShape getSVGShapeCloseUpSouth(double thickness) {
    	Element svgSpecie = svgDoc.createElementNS(svgNS, "g");
    	svgSpecie.setAttribute("id", mc.getIdAlias());
    	svgSpecie.setAttribute("style", "text-rendering: auto;");
    	
    	Element fill = getFill(0, mc.getBounds().getY() - thickness, 
    						   this.size.getWidth(), thickness );
    	
    	svgSpecie.appendChild( fill );
    	
    	double stroke;
	    if (mc.getLine() instanceof DoubleLine) {
	    	stroke = ((DoubleLine)mc.getLine()).getOuterWidth();
	    } else {
	    	stroke = 1;
	    }	    	
    	Element lineUp = getLine(0, mc.getBounds().getY(),
    			            	this.size.getWidth(), mc.getBounds().getY(),
    			                stroke);
    	svgSpecie.appendChild( lineUp );

	    if (mc.getLine() instanceof DoubleLine) {
	    	stroke = ((DoubleLine)mc.getLine()).getInnerWidth();
	    } else {
	    	stroke = 1;
	    }	    	
    	Element lineDown = getLine(0, mc.getBounds().getY() - thickness,
    			            	this.size.getWidth(), mc.getBounds().getY() - thickness,
    			                stroke);
    	svgSpecie.appendChild( lineDown );    	
    	
    	return new SVGCustomShape( svgSpecie );
	}
	
	private SVGShape getSVGShapeCloseUpWest(double thickness) {
    	Element svgSpecie = svgDoc.createElementNS(svgNS, "g");
    	svgSpecie.setAttribute("id", mc.getIdAlias());
    	svgSpecie.setAttribute("style", "text-rendering: auto;");
    	
    	Element fill = getFill(mc.getBounds().getX(), 0 , 
    						   thickness , this.size.getHeight() );
    	
    	svgSpecie.appendChild( fill );
    	
    	double stroke;
	    if (mc.getLine() instanceof DoubleLine) {
	    	stroke = ((DoubleLine)mc.getLine()).getOuterWidth();
	    } else {
	    	stroke = 1;
	    }	    	
    	Element lineUp = getLine(mc.getBounds().getX(), 0 ,
    			            	 mc.getBounds().getX(), this.size.getHeight(),
    			                stroke);
    	svgSpecie.appendChild( lineUp );

	    if (mc.getLine() instanceof DoubleLine) {
	    	stroke = ((DoubleLine)mc.getLine()).getInnerWidth();
	    } else {
	    	stroke = 1;
	    }	    	
    	Element lineDown = getLine(mc.getBounds().getX() + thickness, 0 ,
           	 					   mc.getBounds().getX() + thickness, this.size.getHeight(),
    			                stroke);
    	svgSpecie.appendChild( lineDown );    	
    	
    	
		return new SVGCustomShape( svgSpecie );
	}	
	
	private SVGShape getSVGShapeCloseUpEast(double thickness) {
    	Element svgSpecie = svgDoc.createElementNS(svgNS, "g");
    	svgSpecie.setAttribute("id", mc.getIdAlias());
    	svgSpecie.setAttribute("style", "text-rendering: auto;");
    	
    	Element fill = getFill(mc.getBounds().getX() - thickness, 0 , 
    						   thickness , this.size.getHeight() );
    	
    	svgSpecie.appendChild( fill );
    	
    	double stroke;
	    if (mc.getLine() instanceof DoubleLine) {
	    	stroke = ((DoubleLine)mc.getLine()).getOuterWidth();
	    } else {
	    	stroke = 1;
	    }	    	
    	Element lineUp = getLine(mc.getBounds().getX(), 0 ,
    			            	 mc.getBounds().getX(), this.size.getHeight(),
    			                stroke);
    	svgSpecie.appendChild( lineUp );

	    if (mc.getLine() instanceof DoubleLine) {
	    	stroke = ((DoubleLine)mc.getLine()).getInnerWidth();
	    } else {
	    	stroke = 1;
	    }	    	
    	Element lineDown = getLine(mc.getBounds().getX() - thickness, 0 ,
           	 					   mc.getBounds().getX() - thickness, this.size.getHeight(),
    			                stroke);
    	svgSpecie.appendChild( lineDown );    	
    	
    	return new SVGCustomShape( svgSpecie );
	}	

	private SVGShape getSVGShapeOval(double thickness) {		
    	Element svgSpecie = svgDoc.createElementNS(svgNS, "g");
    	svgSpecie.setAttribute("id", mc.getIdAlias());
    	svgSpecie.setAttribute("style", "text-rendering: auto;");

	    Element ovf = getFillOval(
	    		 mc.getBounds().getX(), 
				 mc.getBounds().getY(), 
				 mc.getBounds().getWidth(), 
				 mc.getBounds().getHeight(),
				 thickness);
	    svgSpecie.appendChild(ovf);

    	Element ov = getOuterOval(
				 mc.getBounds().getX(), 
				 mc.getBounds().getY(), 
				 mc.getBounds().getWidth(), 
				 mc.getBounds().getHeight(),
				 thickness    				 
		);      	
		svgSpecie.appendChild(ov);
		
		ov =  getInnerOval(
				 mc.getBounds().getX() , 
				 mc.getBounds().getY() , 
				 mc.getBounds().getWidth() , 
				 mc.getBounds().getHeight()  , 
				 thickness
				 );		
		svgSpecie.appendChild(ov);
//	    
	    


	    
	    return new SVGCustomShape(svgSpecie);
	}
	
	private SVGShape getSVGShapeCloseUpSW(double thickness) {		
    	Element svgSpecie = svgDoc.createElementNS(svgNS, "g");
    	svgSpecie.setAttribute("id", mc.getIdAlias());
    	svgSpecie.setAttribute("style", "text-rendering: auto;");
    	
    	
    	
	    Element sqf = getFillRect(
	    		 mc.getBounds().getX(), 
				 mc.getBounds().getY() - size.getHeight(), 
				 mc.getBounds().getWidth() + size.getWidth(), 
				 mc.getBounds().getHeight() + size.getHeight(),
				 thickness);
	    svgSpecie.appendChild(sqf);

    	Element sq = getOuterRect(
				 mc.getBounds().getX(), 
				 mc.getBounds().getY() - size.getHeight(), 
				 mc.getBounds().getWidth() + size.getWidth(), 
				 mc.getBounds().getHeight() + size.getHeight(),
				 thickness    				 
		);      	
		svgSpecie.appendChild(sq);
		
		sq =  getInnerRect(mc.getBounds().getX() + thickness , 
				 mc.getBounds().getY() + thickness - size.getHeight() , 
				 mc.getBounds().getWidth() + size.getWidth() - thickness*2, 
				 mc.getBounds().getHeight() + size.getHeight() - thickness*2 , 
				 thickness
				 );		
		svgSpecie.appendChild(sq);
	    	    
		return new SVGCustomShape( svgSpecie );
	}

	private SVGShape getSVGShapeCloseUpSE(double thickness) {		
    	Element svgSpecie = svgDoc.createElementNS(svgNS, "g");
    	svgSpecie.setAttribute("id", mc.getIdAlias());
    	svgSpecie.setAttribute("style", "text-rendering: auto;");
    	
    	
    	
	    Element sqf = getFillRect(
	    		 mc.getBounds().getX() - size.getWidth(), 
				 mc.getBounds().getY() - size.getHeight(), 
				 mc.getBounds().getWidth() + size.getWidth(), 
				 mc.getBounds().getHeight() + size.getHeight(),
				 thickness);
	    svgSpecie.appendChild(sqf);

    	Element sq = getOuterRect(
				 mc.getBounds().getX() - size.getWidth(), 
				 mc.getBounds().getY() - size.getHeight(), 
				 mc.getBounds().getWidth() + size.getWidth(), 
				 mc.getBounds().getHeight() + size.getHeight(),
				 thickness    				 
		);      	
		svgSpecie.appendChild(sq);
		
		sq =  getInnerRect(mc.getBounds().getX() + thickness - size.getWidth(), 
				 mc.getBounds().getY() + thickness - size.getHeight() , 
				 mc.getBounds().getWidth() + size.getWidth() - thickness*2, 
				 mc.getBounds().getHeight() + size.getHeight() - thickness*2 , 
				 thickness
				 );		
		svgSpecie.appendChild(sq);
	    	 
		return new SVGCustomShape( svgSpecie );
	}	
	
	private SVGShape getSVGShapeCloseUpNE(double thickness) {		
    	Element svgSpecie = svgDoc.createElementNS(svgNS, "g");
    	svgSpecie.setAttribute("id", mc.getIdAlias());
    	svgSpecie.setAttribute("style", "text-rendering: auto;");
    	
    	
    	
	    Element sqf = getFillRect(
	    		 mc.getBounds().getX() - size.getWidth(), 
				 mc.getBounds().getY() , 
				 mc.getBounds().getWidth() + size.getWidth(), 
				 mc.getBounds().getHeight() + size.getHeight(),
				 thickness);
	    svgSpecie.appendChild(sqf);

    	Element sq = getOuterRect(
				 mc.getBounds().getX() - size.getWidth(), 
				 mc.getBounds().getY() , 
				 mc.getBounds().getWidth() + size.getWidth(), 
				 mc.getBounds().getHeight() + size.getHeight(),
				 thickness    				 
		);      	
		svgSpecie.appendChild(sq);
		
		sq =  getInnerRect(mc.getBounds().getX() + thickness  - size.getWidth() , 
				 mc.getBounds().getY() + thickness , 
				 mc.getBounds().getWidth() + size.getWidth() - thickness*2, 
				 mc.getBounds().getHeight() + size.getHeight() - thickness*2 , 
				 thickness
				 );		
		svgSpecie.appendChild(sq);
	        	    
		return new SVGCustomShape( svgSpecie );
	}	
	
	private SVGShape getSVGShapeCloseUpNW(double thickness) {		
    	Element svgSpecie = svgDoc.createElementNS(svgNS, "g");
    	svgSpecie.setAttribute("id", mc.getIdAlias());
    	svgSpecie.setAttribute("style", "text-rendering: auto;");
    	
    	
    	
	    Element sqf = getFillRect(
	    		 mc.getBounds().getX() , 
				 mc.getBounds().getY() , 
				 mc.getBounds().getWidth() + size.getWidth(), 
				 mc.getBounds().getHeight() + size.getHeight(),
				 thickness);
	    svgSpecie.appendChild(sqf);

    	Element sq = getOuterRect(
				 mc.getBounds().getX() , 
				 mc.getBounds().getY() , 
				 mc.getBounds().getWidth() + size.getWidth(), 
				 mc.getBounds().getHeight() + size.getHeight(),
				 thickness    				 
		);      	
		svgSpecie.appendChild(sq);
		
		sq =  getInnerRect(mc.getBounds().getX() + thickness , 
				 mc.getBounds().getY() + thickness , 
				 mc.getBounds().getWidth() + size.getWidth() - thickness*2, 
				 mc.getBounds().getHeight() + size.getHeight() - thickness*2 , 
				 thickness
				 );		
		svgSpecie.appendChild(sq);
	    
		return new SVGCustomShape( svgSpecie );
	}	
	
	private SVGShape getSVGShapeRect(double thickness) {		
    	Element svgSpecie = svgDoc.createElementNS(svgNS, "g");
    	svgSpecie.setAttribute("id", mc.getIdAlias());
    	svgSpecie.setAttribute("style", "text-rendering: auto;");
    	
	    Element sqf = getFillRect(
	    		 mc.getBounds().getX(), 
				 mc.getBounds().getY(), 
				 mc.getBounds().getWidth(), 
				 mc.getBounds().getHeight(),
				 thickness);
	    svgSpecie.appendChild(sqf);

    	Element sq = getOuterRect(
				 mc.getBounds().getX(), 
				 mc.getBounds().getY(), 
				 mc.getBounds().getWidth(), 
				 mc.getBounds().getHeight(),
				 thickness    				 
		);      	
		svgSpecie.appendChild(sq);
		
		sq =  getInnerRect(mc.getBounds().getX() + thickness , 
				 mc.getBounds().getY() + thickness , 
				 mc.getBounds().getWidth() - thickness*2, 
				 mc.getBounds().getHeight() - thickness*2 , 
				 thickness
				 );		
		svgSpecie.appendChild(sq);
	   	    
		return new SVGCustomShape( svgSpecie );
	}
	
	public void svgPaint(Element docParent) {
	    getSVGShape().svgPaint(docParent);	    
	}
	
	public Document getDocument() {
		return this.svgDoc;
	}
	
	/**
	 * Metodo para obtener el reactangulo con un 'Information Unit'
	 * 
	 * @param rect, el Bounding Box de la forma sobre la que hay que calcular
	 * 				la posicion del cuadrado
	 * @return
	 */
	protected SVGShape getInfoUnit( Rectangle2D rect ) {
		if ( mc.getInfo() == null ) {
			return null;
		}
		Point2D p = getPointOnRect( rect, mc.getInfo().getAngle() );
		return getInfoUnit(p);
	}
	
	protected Point2D getPointOnRect(Rectangle2D rect, double angle ) {
		/* Calcular punto de interseccion con el angulo dado */
		SVGShape frame;
			
		if ( mc.getClassOf().equals( ECompartment.OVAL )  ) {
			frame = new SVGEllipse( 0, 0, rect.getWidth()/2, rect.getHeight()/2 );
		} else {
			frame = new SVGRectangle( -rect.getWidth()/2, -rect.getHeight()/2,
					rect.getWidth(), rect.getHeight(), 
					CORNER_RADIUS, CORNER_RADIUS );
		}

		// Punto externo al rectangulo
		Point2D pExt = new Point2D.Double( 100000 * Math.cos( angle ) , 100000 * Math.sin( angle ) );
		Segment s = new Segment( pExt.getX(), pExt.getY() , 0, 0 );
		Point2D pointAtAngle = frame.intersection( s );
		Point2D p = new Point2D.Double( rect.getCenterX() + pointAtAngle.getX(),
				                rect.getCenterY() + pointAtAngle.getY() );
		
		return p;
	}

	/**
	 * Dibuja la etiqueta de un Information Unit, centrada en el punto dado 
	 * 
	 * @param p Punto central sobre el que colocar la etiqueta
	 * 
	 * @return
	 */
	protected SVGShape getInfoUnit(Point2D p) {
		SVGComplexShape shp = null;
		if ( mc.getInfo() != null &&
		     !mc.getInfo().getState().equals("empty")	 ) {
			
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
    						mc.getInfo().toString(),
				    		12 ))
    		);
		}
		
		return shp;
	}
}
