package model.specie;

import java.awt.geom.Rectangle2D;
import java.util.Vector;

import celldesignerparse_4_0.annotation.listOfSpeciesAliases.Tag;
import celldesignerparse_4_0.commondata.View;


public abstract class MSpeciesSimple extends MSpecies {
	
	public MSpeciesSimple(String idAlias, String id, String name, String compartment, int homodimer,
			String activity, Rectangle2D bounds, String viewState,
			View usualView, View briefView, Vector<Tag> tags, String notes) {
		super(idAlias, id, name, compartment, homodimer, activity, bounds, viewState, usualView, briefView, tags, notes);
	}

	public abstract String getType();
	
	
}
