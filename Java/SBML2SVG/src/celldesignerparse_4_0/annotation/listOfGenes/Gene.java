package celldesignerparse_4_0.annotation.listOfGenes;

import java.util.Vector;

import org.sbml.libsbml.XMLNode;

import celldesignerparse_4_0.commondata.Entity;
import celldesignerparse_4_0.commondata.Region;

/**
 * Clase que modela el nodo <code>gene</code> de la
 * annotation de CellDesigner
 * 
 * @author Guillermo Fajula Leal
 *
 */
public class Gene {
	private Entity entity;
	private Vector<Region> regions;
	
	/**
	 * Constructor por defecto a partir del noso XML
	 * 
	 * @param node
	 */
	public Gene(XMLNode node) {
		entity = new Entity(node);
		for (int i=0;i<node.getNumChildren();i++){
			if (node.getChild(i).getName().compareToIgnoreCase("listOfRegions")==0){
				regions = new Vector<Region>();
				for (int j=0;j<node.getChild(i).getNumChildren();j++){
					regions.addElement(
							new Region(
									node.getChild(i).getChild(j)
								)
							);
				}
			}
		}
	}	
	

	/**
	 * @return Atributo Id del <code>gene</code>
	 */
	public String getId(){
		return entity.getId();
	}
		
	/**
	 * @return Atributo <code>type</code> del <code>gene</code>
	 */
	public String getType(){
		return entity.getType();
	}
	
	/**
	 * @return Atributo <code>name</code> del <code>gene</code>
	 */
	public String getName(){
		return entity.getName();
	}
	
	/**
	 * Obtiene la colección de <code>regions</code> que contiene este <code>gene</code> 
	 * @return Atributo colección de <code>Region</code>
	 */
	public Vector<Region> getRegions(){
		return regions;
	}
	
}
