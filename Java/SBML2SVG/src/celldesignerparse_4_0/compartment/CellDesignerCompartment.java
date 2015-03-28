package celldesignerparse_4_0.compartment;

import org.sbml.libsbml.Compartment;

public class CellDesignerCompartment extends Compartment{
	
	/**
	 * Compartment de CellDesigner. Hereda la informacion de
	 * un 'compartment' de SBML y añade la informacion que
	 * CellDesigner aporta en la Annotation
	 * 
	 * @param compartment
	 */
	public CellDesignerCompartment(Compartment compartment) {
		super(compartment);
	}
	
	/**
	 * Obtiene el nombre del 'compartment',  partir del nodo <code>name</code>
	 * del nodo <code>annotation</code> del 'compartment'
	 * 
	 * @return
	 */
	public String getCellDesignerName(){
		if (getAnnotation()==null){
			return "";
		}
		
		for (int i=0;i<getAnnotation().getNumChildren();i++){
			if (getAnnotation().getChild(i).getName().equals("name") ){
				return getAnnotation().getChild(i).getChild(0).getCharacters();
			}
		}
		return "";
	}
	
	public String toString(){
		return getCellDesignerName();
	}
}
