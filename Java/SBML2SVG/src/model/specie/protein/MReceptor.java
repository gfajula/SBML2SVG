package model.specie.protein;

import java.awt.geom.Rectangle2D;
import java.util.Vector;

import celldesignerparse_4_0.annotation.listOfSpeciesAliases.Tag;
import celldesignerparse_4_0.commondata.View;

public class MReceptor extends MProtein{

	public MReceptor(String idAlias, String id, String name, String compartment, int homodimer, String activity,
			Rectangle2D bounds, String viewState, View usualView,
			View briefView, Vector<MResidue> residues, Vector<Tag> tags, String notes) {
		super(idAlias, id, name, compartment, homodimer, activity, bounds, viewState, usualView, briefView,
				residues, tags, notes);
	}
	
	public MReceptor(String idAlias, String id, String name, String compartment, int homodimer, String activity,
			Rectangle2D bounds, String viewState, View usualView,
			View briefView, Vector<MResidue> residues, Vector<MBindingRegion> bindingRegions, 
			String structuralState, Vector<Tag> tags, String notes) {
		super(idAlias, id, name, compartment, homodimer, activity, bounds, viewState, usualView, briefView,
				residues, bindingRegions, structuralState, tags, notes);
	}
	

}
