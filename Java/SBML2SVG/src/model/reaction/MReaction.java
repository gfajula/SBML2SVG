package model.reaction;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Vector;

import model.MBase;
import model.MCompartment;
import model.specie.MAddedSpeciesLink;
import model.specie.MSpeciesLink;
import celldesignerparse_4_0.commondata.SingleLine;
import celldesignerparse_4_0.reaction.EditPoint;
import celldesignerparse_4_0.reaction.EditPoints;

public abstract class MReaction extends MBase {
	protected String id;
	protected Vector<MSpeciesLink> products;
	protected Vector<MAddedSpeciesLink> productLinks;
	protected Vector<MSpeciesLink> reactants;
	protected Vector<MAddedSpeciesLink> reactantLinks;
	protected Vector<Point2D> editPoints;
	protected SingleLine line;
	protected Vector<MModification> modifications;
	protected Vector<MBooleanLogicGate> booleanGates;
	protected int rectangleIndex = 0;
	protected boolean reversible;
	protected String sboTerm = null;
	protected boolean transformEditPoints = false;
	protected String kineticLaw = "";

	public boolean isReversible() {
		return reversible;
	}

	public int getRectangleIndex() {
		return rectangleIndex;
	}

	public MReaction(String id, Vector<MSpeciesLink> products, Vector<MSpeciesLink> reactants, 
			         Vector<MAddedSpeciesLink> productLinks, Vector<MAddedSpeciesLink> reactantLinks, 
			         EditPoints editPoints, SingleLine line, Vector<MModification> modifications, 
			         Vector<MBooleanLogicGate> booleanGates, int rectangleIndex, boolean reversible){
		this.id = id;
		this.products = products;
		this.reactants = reactants;
		this.productLinks = productLinks;
		this.reactantLinks = reactantLinks;
		this.editPoints = new Vector<Point2D>();
		
		if (editPoints!=null) {
			for(EditPoint ep: editPoints.getEditPoints()) {
				this.editPoints.add( new Point2D.Double ( ep.getXProjection(), ep.getYProjection() ));
			}
		}
		
		this.line = line;
		this.modifications = modifications;
		this.booleanGates = booleanGates;
		this.rectangleIndex = rectangleIndex;
		
//		calculateEditPoints();
//		calculateModificationEditPoints();
		
		this.reversible = reversible;
	}
		
	public Vector<MBooleanLogicGate> getBooleanLogicGates(){
		return booleanGates;
	}
	
	public Vector<MModification> getModifications(){
		return modifications;
	}
	
	public String getId(){
		return id;
	}
	
	public Vector<MSpeciesLink> getReactants(){
		return reactants;
	}
	
	public Vector<MSpeciesLink> getProducts(){
		return products;
	}
	
	public Vector<MAddedSpeciesLink> getReactantLinks(){
		return reactantLinks;
	}
	
	public Vector<MAddedSpeciesLink> getProductLinks(){
		return productLinks;
	}
	
	public Vector<Point2D> getEditPoints(){
		return editPoints;
	}
	
	public Vector<Point2D> getEditPointsCopy() {		
		Vector<Point2D> deepCopy = new Vector<Point2D>();
		for(Point2D p : editPoints) {
			deepCopy.add( (Point2D) p.clone() );
		}
		return deepCopy;
	}
	
	public SingleLine getLine() {
		if (this.line == null)
			this.line = new SingleLine();
		return line;
	}

	public String getSboTerm() {
		return sboTerm;
	}

	public String getSboTermText() {
		return (sboTerm==null?"":sboTerm+"");
	}
	
	public void setSboTerm(String sboTerm) {
		this.sboTerm = sboTerm;
	}

	public void setEditPoints(Vector<Point2D> editPoints) {
		this.editPoints = editPoints;
	}

	public void setRectangleIndex(int rectangleIndex) {
		this.rectangleIndex = rectangleIndex;
	}
	
	/**
	 * Calcular el compartment común a todas las species que participan en esta reacción.
	 * Si no hay ningun MCompartment común, se devuelve null, que modela el compartment 'default'.
	 *  
	 * @return MCompartment compartment al que pertenece la reacción
	 */
	public MCompartment getCommonCompartment() {
		// vector con los compartment
		HashMap<String,MCompartment> compartments = getParticipantsCompartments() ;
			
		int minimumDepth = 10000;
		boolean allSame = true;
		MCompartment commonComp = null;
		
		// 1er paso, 
		// Comprobar si actualmente toda la
		// reacción ocurre en el mismo compartment, 
		// y de paso, buscar la minima profundidad común, y 
		for ( MCompartment comp : compartments.values() ) {			
			minimumDepth = Math.min( comp.getDepth() , minimumDepth);
			
			if ( commonComp == null ) {
				commonComp = comp;
			} 
			
			// Comprobar si son todos el mismo compartment
			if ( commonComp != comp) {
				allSame = false; 
			}
		}
		
		if ( allSame ) {
			return commonComp;
		}
				
		// Ir bajando de profundidad hasta que todos coincidan
		
		while ( minimumDepth > 0 ) {
			commonComp = null;
			
			for ( MCompartment comp : compartments.values() ) {	
				if ( commonComp == null ) {
					commonComp = comp.getParentWithDepth( minimumDepth );
				} else {
					if ( !commonComp.equals( comp.getParentWithDepth( minimumDepth ) ) ) {
						// Hay dos que no comparten ancestro en este profundidad. 
						// Subir un nivel
						commonComp = null;
						break;
					}
				}
			}
			if ( commonComp != null) {
				break;
			} else {
				minimumDepth--;				
			}
		}		
		
		return commonComp;
		
	}
	
	public HashMap<String,MCompartment> getParticipantsCompartments() {
		HashMap<String,MCompartment> compartments = new HashMap<String,MCompartment>();
		
		for ( MSpeciesLink specLink : this.getReactants() ) {
			if ( specLink.getMs().getType().equals("Degraded"))  continue;
			MCompartment comp = specLink.getMs().getCompartment();
			if (comp==null) {
				System.err.println( specLink.getMs().getId() + " sin compartimento.");
				
			}
			compartments.put(comp.getId(), comp);
		}
		for ( MSpeciesLink specLink : this.getReactantLinks() ) {
			if ( specLink.getMs().getType().equals("Degraded"))  continue;
			MCompartment comp = specLink.getMs().getCompartment();
			compartments.put(comp.getId(), comp);
		}
		for ( MSpeciesLink specLink : this.getProducts() ) {
			if ( specLink.getMs().getType().equals("Degraded"))  continue;
			MCompartment comp = specLink.getMs().getCompartment();
			compartments.put(comp.getId(), comp);
		}
		for ( MSpeciesLink specLink : this.getProductLinks() ) {
			if ( specLink.getMs().getType().equals("Degraded"))  continue;
			MCompartment comp = specLink.getMs().getCompartment();
			compartments.put(comp.getId(), comp);
		}
		
		return compartments;
	}
	
	public abstract String getType();

	public boolean isTransformEditPoints() {
		return transformEditPoints;
	}

	public void setTransformEditPoints(boolean transformEditPoints) {
		this.transformEditPoints = transformEditPoints;
	}

	public String getKineticLaw() {
		return kineticLaw;
	}

	public void setKineticLaw(String kineticLaw) {
		this.kineticLaw = kineticLaw;
	}


	
}
