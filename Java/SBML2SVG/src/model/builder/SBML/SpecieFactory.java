package model.builder.SBML;

import model.Model;
import model.specie.MPhenotype;
import model.specie.MSimpleMolecule;
import model.specie.MSpeciesSimple;
import model.specie.MUnknown;
import model.specie.protein.MProtein;

import org.sbml.libsbml.Species;

public class SpecieFactory {

	/**
	 * Construir 'specie'. 
	 * Si el atributo SBO está presente se distinguiran algunos tipos
	 * de species.
	 * 
	 * @param spc 'specie' en el modelo SBML
	 * @param sbmlModel modelo de SBML estándar
	 * 
	 * @return <code>MSpeciesSimple</code> con 'specie'
	 */
	public static MSpeciesSimple buildSpecies(Species spc, org.sbml.libsbml.Model sbmlModel, Model model){
		MSpeciesSimple newSpecie; 
		
		if ( spc.getSBOTermID()==null || spc.getSBOTermID().equals("") ) {
			newSpecie = new MProtein(spc.getId(), spc.getName(), spc.getCompartment() );
		} else if ( spc.getSBOTermID().equals( SBO.PHENOTYPE ) ){
			newSpecie = new MPhenotype( spc.getId(), spc.getName(), spc.getCompartment() );
		} else if ( spc.getSBOTermID().equals( SBO.UNSPECIFIED_ENTITY ) ){
			newSpecie = new MUnknown( spc.getId(), spc.getName(), spc.getCompartment() );
		} else if ( spc.getSBOTermID().equals( SBO.SIMPLE_CHEMICAL ) ){
			newSpecie = new MSimpleMolecule( spc.getId(), spc.getName(), spc.getCompartment() );
		} else {
			 // Por comodidad, se toma 'Proteina' para las 'species'
			 // 'por defecto' a ser la misma que toma CellDesigner
			 // y 
			newSpecie = new MProtein(spc.getId(), spc.getName(), spc.getCompartment() );
		}
		
		
		// Actualizar relacion con MCompartment
		newSpecie.setCompartment( model.getCompartment( newSpecie.getCompartmentId() ) );
		
		newSpecie.setSboTerm( spc.getSBOTermID() );	
		
		return newSpecie;
	}

}
