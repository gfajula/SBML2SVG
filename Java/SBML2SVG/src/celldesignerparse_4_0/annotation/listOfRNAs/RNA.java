package celldesignerparse_4_0.annotation.listOfRNAs;

import java.util.Vector;

import org.sbml.libsbml.XMLNode;

import celldesignerparse_4_0.commondata.Entity;
import celldesignerparse_4_0.commondata.Region;

public class RNA {
	private Entity entity;
	private Vector<Region> regions;
	
	public RNA(XMLNode node){
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
	
	
	public String getId(){
		return entity.getId();
	}
		
	public String getType(){
		return entity.getType();
	}
	
	public String getName(){
		return entity.getName();
	}
	
	public Vector<Region> getRegions(){
		return regions;
	}
}
