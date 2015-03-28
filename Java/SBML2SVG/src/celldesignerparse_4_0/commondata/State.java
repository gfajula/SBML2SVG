package celldesignerparse_4_0.commondata;

import java.util.Vector;

import org.sbml.libsbml.XMLNode;

public class State {
	private XMLNode node;
	private int homodimer;
	private Vector<Modificator> listOfModifications;
	private String structuralState = null;
	
	public State(XMLNode node) {
		this.node = node;
		inicialize();
	}

	private void inicialize(){
		homodimer = 1;
		for (int i=0;i<node.getNumChildren();i++){
			if (node.getChild(i).getName().compareToIgnoreCase("homodimer")==0){
				homodimer = Integer.parseInt(node.getChild(i).getChild(0).getCharacters());
			} else if (node.getChild(i).getName().compareToIgnoreCase("listOfModifications")==0){
				listOfModifications = new Vector<Modificator>();
				for (int j=0;j<node.getChild(i).getNumChildren();j++){
					listOfModifications.addElement(new Modificator(node.getChild(i).getChild(j)));
				}
			} else if (node.getChild(i).getName().compareToIgnoreCase("listOfStructuralStates")==0){
				structuralState = "";
				for (int j=0;j<node.getChild(i).getNumChildren();j++){
					if (node.getChild(i).getChild(j).getName().compareToIgnoreCase("structuralState")==0) {
						structuralState = 
							node.getChild(i).getChild(j).getAttributes().getValue("structuralState");
						
					}
				}
			}
		}
	}
	
	public int getHomodimer(){
		return homodimer;
	}
	
	public Vector<Modificator> getModificators(){
		return listOfModifications;
	}
	
	public Modificator getModificatorByName(String name){
		if (listOfModifications!=null){
			for (Modificator m : listOfModifications){				
				if (m.getResidue().compareToIgnoreCase(name)==0){
					return m;
				}
			}
		}
		return null;
	}
	
	public String toString(){
		String result = "homodimer: "+getHomodimer()+"\nmodificators:\n";
		Vector<Modificator> mm = getModificators();
		if (mm!=null){
			for (Modificator m:mm){
				result+=m.toString()+"\n";
			}
		}
		return result;
	}

	public String getStructuralState() {
		return structuralState;
	}
	
	
}
