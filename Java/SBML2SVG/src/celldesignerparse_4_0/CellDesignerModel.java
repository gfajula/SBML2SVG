package celldesignerparse_4_0;

import java.util.Hashtable;

import org.sbml.libsbml.Compartment;
import org.sbml.libsbml.Model;
import org.sbml.libsbml.Reaction;
import org.sbml.libsbml.Species;

import celldesignerparse_4_0.annotation.CellDesignerAnnotation;
import celldesignerparse_4_0.compartment.CellDesignerCompartment;
import celldesignerparse_4_0.reaction.CellDesignerReaction;
import celldesignerparse_4_0.specie.CellDesignerSpecies;

/**
 * Clase que extiende la funcionalidad de org.sbml.libsbml.Model
 * para considerar las anotaciones de CellDesigner
 * 
 * @author Guillermo Fajula Leal
 *
 */
public class CellDesignerModel extends Model{
	private CellDesignerAnnotation cda;
	
	private Hashtable<String, CellDesignerCompartment> cdc = new Hashtable<String, CellDesignerCompartment>();
	private Hashtable<String, CellDesignerSpecies> cds = new Hashtable<String, CellDesignerSpecies>();
	private Hashtable<String, CellDesignerReaction> cdr = new Hashtable<String, CellDesignerReaction>();
	
	/**
	 * Constructor, recopila la informacion de la anotacion de CellDesigner
	 * sobre los 'compartments', 'species' y 'reactions'
	 * 
	 * @param model 
	 */
	public CellDesignerModel(Model model) {
		super(model);
		cda = new CellDesignerAnnotation(super.getAnnotation());
		for (int i=0;i<super.getNumCompartments();i++){
			cdc.put(super.getCompartment(i).getId(), new CellDesignerCompartment(super.getCompartment(i)));
		}
		for (int i=0;i<super.getNumSpecies();i++){
			cds.put(super.getSpecies(i).getId(), new CellDesignerSpecies(super.getSpecies(i)));
		}
		for (int i=0;i<super.getNumReactions();i++){
			cdr.put(super.getReaction(i).getName(), new CellDesignerReaction(super.getReaction(i)));
		}
	}
	
	/** 
	 * Método que da acceso a la informacion extra que CellDesigner
	 * añade al SBML en el nodo <code>annotation</code>, pero devolviendo
	 * un objeto de tipo <code>CellDesignerAnnotation</code>
	 */
	public CellDesignerAnnotation getAnnotation(){
		return cda;
	}
	
	/**
	 * Metodo que redefine el método de la clase padre, pero que devuelve
	 * un objeto <code>CellDesignerCompartment</code>
	 *  
	 * @param arg0
	 */
	@Override
	public CellDesignerCompartment getCompartment(long arg0){
		Compartment compartment = super.getCompartment(arg0);
		if (cdc.containsKey(compartment.getId())){
			return cdc.get(compartment.getId());
		}
		CellDesignerCompartment c = new CellDesignerCompartment(compartment);
		cdc.put(compartment.getName(), c);
		return c;
	}
	
	/**
	 * Metodo que redefine el método de la clase padre, pero que devuelve
	 * un objeto <code>CellDesignerCompartment</code>
	 *  
	 * @param arg0
	 */
	@Override
	public CellDesignerCompartment getCompartment(String arg0){
		return cdc.get(arg0);
	}	
	
	/**
	 * Metodo que redefine el método de la clase padre, pero que devuelve
	 * un objeto <code>CellDesignerSpecies</code>
	 *  
	 * @param arg0
	 */
	@Override
	public CellDesignerSpecies getSpecies(long arg0){
		Species species = super.getSpecies(arg0);
		if (cds.containsKey(species.getId())){
			return cds.get(species.getId());
		}
		CellDesignerSpecies c = new CellDesignerSpecies(species);
		cds.put(species.getId(), c);
		return c;
	}
	
	/**
	 * Metodo que redefine el método de la clase padre, pero que devuelve
	 * un objeto <code>CellDesignerSpecies</code>
	 *  
	 * @param arg0
	 */
	@Override
	public CellDesignerSpecies getSpecies(String arg0){
		return cds.get(arg0);
	}
	
	/**
	 * Metodo que redefine el método de la clase padre, pero que devuelve
	 * un objeto <code>CellDesignerReaction</code>
	 *  
	 * @param arg0
	 */
	@Override
	public CellDesignerReaction getReaction(long arg0){
		Reaction reaction = super.getReaction(arg0);
		if (cdr.containsKey(reaction.getId())){
			return cdr.get(reaction.getId());
		}
		CellDesignerReaction r = new CellDesignerReaction(reaction);
		cdr.put(reaction.getId(), r);
		return r;
	}
	
	/**
	 * Metodo que redefine el método de la clase padre, pero que devuelve
	 * un objeto <code>CellDesignerReaction</code>
	 *  
	 * @param arg0
	 */
	@Override
	public CellDesignerReaction getReaction(String arg0){
		return cdr.get(arg0);
	}
}
