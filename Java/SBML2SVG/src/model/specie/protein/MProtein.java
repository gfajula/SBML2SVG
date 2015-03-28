package model.specie.protein;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import model.specie.MSpeciesSimple;
import celldesignerparse_4_0.annotation.listOfSpeciesAliases.Tag;
import celldesignerparse_4_0.commondata.Paint;
import celldesignerparse_4_0.commondata.SingleLine;
import celldesignerparse_4_0.commondata.View;

public class MProtein extends MSpeciesSimple{
	//protected int homodimer;
	protected Vector<MResidue> residues;
	protected Vector<MBindingRegion> bindingRegions;
	protected String structuralState;

	// Standard Species
	public MProtein(String id, String name, String compartment ) {
		super( id , id, 
			   name,  compartment , 1, 
			   "inactive",  
//			   new Rectangle2D.Double(Math.random() * 2000, Math.random() *2000, 60, 40), 
			   new Rectangle2D.Double( 0, 0, 60, 40),
			   "usual",			   
			   new View(new Point2D.Double(0,0), new Dimension(0,0), new SingleLine(), new Paint()), 
			   new View(new Point2D.Double(0,0), new Dimension(0,0), new SingleLine(), new Paint()),
			   null, ""
			 );		
		this.homodimer = 1;
		this.bindingRegions = null;
		this.residues = null;
		this.structuralState = null;
	}
	
	public MProtein(String idAlias, String id, String name, String compartment, int homodimer, String activity,
			Rectangle2D bounds, String viewState, View usualView, View briefView, Vector<MResidue> residues, Vector<Tag> tags, String notes) {
		super(idAlias, id, name, compartment, homodimer, activity, bounds, viewState, usualView, briefView, tags, notes);
		this.homodimer = homodimer;
		this.residues = residues;
	}
	
	public MProtein(String idAlias, String id, String name, String compartment, int homodimer, String activity,
			Rectangle2D bounds, String viewState, View usualView, View briefView, 
			Vector<MResidue> residues, Vector<MBindingRegion> bindingRegions, String structuralState, Vector<Tag> tags, String notes) {
		super(idAlias, id, name, compartment, homodimer, activity, bounds, viewState, usualView, briefView, tags, notes);
		this.homodimer = homodimer;
		this.residues = residues;
		this.bindingRegions = bindingRegions;
		this.structuralState = structuralState;
	}

	public int getHomodimer(){
		return homodimer;
	}
	
	public Vector<MResidue> getResidues(){
		return residues;
	}
	
	public Vector<MBindingRegion> getBindingRegions(){
		return bindingRegions;
	}
	
	public String getStructuralState() {
		return structuralState;
	}
	
	@Override
	public String getType() {
		return "Protein";
	}
}
