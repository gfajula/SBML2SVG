package model.specie;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import celldesignerparse_4_0.annotation.listOfSpeciesAliases.Tag;
import celldesignerparse_4_0.commondata.Paint;
import celldesignerparse_4_0.commondata.SingleLine;
import celldesignerparse_4_0.commondata.View;

public class MPhenotype extends MSpeciesSimple {

	public MPhenotype(String id, String name, String compartment ) {
		super( id , id, 
			   name,  compartment , 1, 
			   "inactive",  
			   new Rectangle2D.Double( 0, 0, 60, 30),
			   "usual",			   
			   new View(new Point2D.Double(0,0), new Dimension(0,0), new SingleLine(), new Paint()), 
			   new View(new Point2D.Double(0,0), new Dimension(0,0), new SingleLine(), new Paint()),
			   null, ""
			 );		
		this.homodimer = 1;
//		this.bindingRegions = null;
//		this.residues = null;
//		this.structuralState = null;
	}
			
	public MPhenotype(String idAlias, String id, String name, String compartment, int homodimer, String activity,
			Rectangle2D bounds, String viewState, View usualView, View briefView, Vector<Tag> tags, String notes) {
		super(idAlias, id, name, compartment, homodimer, activity, bounds, viewState, usualView, briefView, tags, notes);
	}
	
	@Override
	public String getType() {
		return "Phenotype";
	}

}
