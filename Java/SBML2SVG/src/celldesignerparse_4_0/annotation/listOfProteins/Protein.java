package celldesignerparse_4_0.annotation.listOfProteins;

import java.util.Vector;

import org.sbml.libsbml.XMLNode;

import celldesignerparse_4_0.commondata.Entity;

public class Protein {
	private Entity entity;
	private Vector<ModificationResidue> modificationResidues;
	private Vector<BindingRegion> bindingRegions;
	private String notesContent;
		
	public Protein(XMLNode node) {
		entity = new Entity(node);
		for (int i=0;i<node.getNumChildren();i++){
			if (node.getChild(i).getName().compareToIgnoreCase("listOfModificationResidues")==0){
				modificationResidues = new Vector<ModificationResidue>();
				for (int j=0;j<node.getChild(i).getNumChildren();j++){
					modificationResidues.addElement(new ModificationResidue(node.getChild(i).getChild(j)));
				}
			} else if (node.getChild(i).getName().compareToIgnoreCase("listOfBindingRegions")==0){
				bindingRegions = new Vector<BindingRegion>();
				for (int j=0;j<node.getChild(i).getNumChildren();j++){
					bindingRegions.addElement(
							new BindingRegion(
									node.getChild(i).getChild(j)
								)
							);
				}
			} else if (node.getChild(i).getName().equalsIgnoreCase("notes") ) {
				XMLNode n = node.getChild(i);
				if (n.getChild("html") != null) {
					notesContent = n.getChild("html").getChild("body").toXMLString().replaceAll("</?body>", "");
				}
			}
		}
	}
	
	public String toString(){
		String result = "Protein: "+entity+"\n";
		if (modificationResidues!=null){
			for (ModificationResidue mr : modificationResidues){
				result+=mr.toString();
			}
		}
		return result;
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
	
	public Vector<ModificationResidue> getResidues(){
		return modificationResidues;
	}
	
	public Vector<BindingRegion> getBindingRegions(){
		return bindingRegions;
	}

	public String getNotesContent() {
		return notesContent;
	}
	
}
