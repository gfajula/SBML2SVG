package model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;

import model.specie.MInformationUnit;

import celldesignerparse_4_0.commondata.DoubleLine;
import celldesignerparse_4_0.commondata.Line;
import celldesignerparse_4_0.commondata.Paint;

public class MCompartment extends MBase {
	protected String idAlias;
	protected String id;
	protected String name;
	protected ECompartment classOf;
	protected EPosition position;
	protected Rectangle2D bounds;
	protected Point2D namePoint;
	protected Line line;
	protected Paint paint;
	protected String outside;
	private   MCompartment outsideCompartment = null;
	private   int depth = -1;
	private boolean isDefault = false;
	private MInformationUnit info;
	
	public MCompartment(String idAlias, 
						 String id, 
						 String name, 
						 String outside, 
						 String classOf, 
						 Rectangle2D bounds,
						 Point2D namePoint, 
						 Line line, 
						 Paint paint){
		this.idAlias = idAlias;
		this.id = id;
		this.name = name;
		if ( this.name == null || this.name.equals("") ) {
			this.name = "" + this.idAlias;
		}
		this.outside = outside;
		
		if (classOf.compareToIgnoreCase("SQUARE")==0){
			this.classOf = ECompartment.SQUARE;
		} else if (classOf.compareToIgnoreCase("SQUARE_CLOSEUP_NORTH")==0){
			this.classOf = ECompartment.SQUARE_CLOSEUP_NORTH;
		} else if (classOf.compareToIgnoreCase("SQUARE_CLOSEUP_SOUTH")==0){
			this.classOf = ECompartment.SQUARE_CLOSEUP_SOUTH;
		} else if (classOf.compareToIgnoreCase("OVAL")==0){
			this.classOf = ECompartment.OVAL;
		} else if (classOf.compareToIgnoreCase("SQUARE_CLOSEUP_WEST")==0){
			this.classOf = ECompartment.SQUARE_CLOSEUP_WEST;
		} else if (classOf.compareToIgnoreCase("SQUARE_CLOSEUP_EAST")==0){
			this.classOf = ECompartment.SQUARE_CLOSEUP_EAST;
		} else if (classOf.compareToIgnoreCase("SQUARE_CLOSEUP_NORTHEAST")==0){
			this.classOf = ECompartment.SQUARE_CLOSEUP_NORTHEAST;
		} else if (classOf.compareToIgnoreCase("SQUARE_CLOSEUP_NORTHWEST")==0){
			this.classOf = ECompartment.SQUARE_CLOSEUP_NORTHWEST;
		} else if (classOf.compareToIgnoreCase("SQUARE_CLOSEUP_SOUTHWEST")==0){
			this.classOf = ECompartment.SQUARE_CLOSEUP_SOUTHWEST;
		} else if (classOf.compareToIgnoreCase("SQUARE_CLOSEUP_SOUTHEAST")==0){
			this.classOf = ECompartment.SQUARE_CLOSEUP_SOUTHEAST;			
		} else if (classOf.indexOf("SQUARE")>=0){
			this.classOf = ECompartment.SQUARE;
		} else if (classOf.indexOf("OVAL")>=0){
			this.classOf = ECompartment.OVAL;
		} else if (classOf.indexOf("DEFAULT")>=0){
			this.classOf = ECompartment.DEFAULT_COMPARTMENT;
		}  else {
			// Tipo por defecto
			this.classOf = ECompartment.SQUARE;
		}
		this.bounds = bounds;
		this.namePoint = namePoint;
		this.line = line;
		this.paint = paint;
	}
	
	public String getIdAlias(){
		return idAlias;
	}
	
	public String getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	public ECompartment getClassOf(){
		return classOf;
	}
	
	public Rectangle2D getBounds(){
		if ( bounds == null ) {
			// Bounds por defecto (completamente arbitrario)
			return new Rectangle2D.Double(20 ,20 , 500, 500 );
		}
		
		return bounds;
	}
	
	public Point2D getNamePoint(){	
		return namePoint;
	}
	
	public Line getLine(){
		if (line == null) {
			line = new DoubleLine();
		}
		return line;
	}
	
	public Paint getPaint(){
		if ( paint == null ) {
			// Paint por defecto
			Paint defaultPaint = new Paint();
			defaultPaint.setColor( new Color ( 210, 200 , 50 ) );
			this.paint = defaultPaint;
		}
		return paint;
	}

	public MCompartment getOutsideCompartment() {
		return outsideCompartment;
	}

	public void setOutsideCompartment(MCompartment outsideCompartment) {
		this.outsideCompartment = outsideCompartment;
	}

	public String getOutside() {
		return outside;
	}

	public void setBounds(Rectangle2D bounds) {
		this.bounds = bounds;
	}
	
	/**
	 * Obtiene la profundidad en la jerarquia de compartments
	 * Calculada de forma perezosa
	 * 
	 * @return Profundidad en la jerarquia, siendo la raiz profundidad 0
	 */
	public int getDepth() {
		if ( depth < 0) {
			if ( getOutsideCompartment() == null ) {
				depth = 0;
			} else {
				depth =  1 + getOutsideCompartment().getDepth();
			}
		} 
			
		return depth;		
	}
	
	/**
	 * 
	 */
	public MCompartment getParentWithDepth( int parentDepth ) {
		if ( parentDepth < 0 ) return null;
		
		MCompartment parent = this;
		int depth = this.getDepth();
		
		while ( depth > parentDepth ) {
			parent = parent.getOutsideCompartment();
			depth--;
		}
		
		
		return parent;
	}
	
	public String toString() {
		return this.getId();
	}
	
	public boolean equals( MCompartment another ) {
		return another.getId().equals( this.getId() );
	}
	
	/**
	 * Comprobar si la jerarquia de compartments hace ciclos a partir de este nodo
	 * 
	 * @return
	 */
	public boolean hasCycles() {
		HashSet<String> alreadyVisitedIds = new HashSet<String>(); 

		alreadyVisitedIds.add( this.getId() );
		
		MCompartment cursor = this.getOutsideCompartment();
		while ( cursor != null ) {
			if ( alreadyVisitedIds.contains( cursor.getId() ) ) {
				// Existe un ciclo
				return true;
			}
			alreadyVisitedIds.add( cursor.getId() );
			cursor = cursor.getOutsideCompartment();	
		}
		
		return false;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public MInformationUnit getInfo() {
		return info;
	}

	public void setInfo(MInformationUnit info) {
		this.info = info;
	}
	
}
