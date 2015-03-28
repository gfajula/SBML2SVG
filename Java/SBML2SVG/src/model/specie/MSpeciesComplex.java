package model.specie;

import java.awt.geom.Rectangle2D;
import java.util.Vector;

import celldesignerparse_4_0.annotation.listOfSpeciesAliases.Tag;
import celldesignerparse_4_0.commondata.View;

public class MSpeciesComplex extends MSpecies{
	protected Vector<MSpecies> listOfSpecies;
	
	public MSpeciesComplex(String idAlias, String id, String name, String compartment, int homodimer,
			String activity, Rectangle2D bounds, String viewState,
			View usualView, View briefView, Vector<Tag> tags, String notes) {
		
		super(idAlias, id, name, compartment, homodimer, activity, bounds, viewState, usualView, briefView, tags, notes);
		if ( compartment == null ) System.err.println(idAlias);
	}
	
	public MSpeciesComplex(MSpeciesComplex clone){
		super(clone);
	}

	/** 
	 * Añadir un elemento a la lista de los componentes de este Complejo
	 * 
	 * @param mss
	 */
	public void addSpecieAlias(MSpecies mss) {
		if (listOfSpecies==null){
			listOfSpecies = new Vector<MSpecies>();
		}
		listOfSpecies.addElement(mss);
	}
	
	public Vector<MSpecies> getSubspecies(){
		return listOfSpecies;
	}

	@Override
	public String getType() {
		return "Complex";
	}
}
