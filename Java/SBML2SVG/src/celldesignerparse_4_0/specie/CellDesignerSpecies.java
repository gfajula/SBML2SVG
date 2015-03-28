package celldesignerparse_4_0.specie;

import java.util.Vector;

import org.sbml.libsbml.Species;
import org.sbml.libsbml.XMLNode;

import celldesignerparse_4_0.commondata.SpeciesIdentity;

public class CellDesignerSpecies extends Species{
	private String positionToCompartment;
	private SpeciesIdentity speciesIdentity;
	private Vector<Catalyzed> listOfCatalyzedReactions;
	private String notesContent;
	
	public CellDesignerSpecies(Species species){
		super(species);
		initialize();
	}
	
	private void initialize() {
		XMLNode node = getAnnotation();
		
		// Adaptación a XMLs creados con CellDesigner 4.1 :		
		if (node.getChild(0).getName().equals("extension")) {
			node = node.getChild(0);
		}
		
		if (node==null) return;
		for (int i=0;i<node.getNumChildren();i++){
			if (node.getChild(i).getName().compareToIgnoreCase("positionToCompartment")==0){
				positionToCompartment = node.getChild(i).getChild(0).getCharacters();
			} else if (node.getChild(i).getName().compareToIgnoreCase("speciesIdentity")==0){
				speciesIdentity = new SpeciesIdentity(node.getChild(i));
			} else if (node.getChild(i).getName().compareToIgnoreCase("listOfCatalyzedReactions")==0){
				listOfCatalyzedReactions = new Vector<Catalyzed>();
				for (int j=0;j<node.getChild(i).getNumChildren();j++){
					listOfCatalyzedReactions.addElement(new Catalyzed(node.getChild(i).getChild(j)));
				}
			}
		}
		
		node = getNotes();
		if ( node != null ) {
			if (node.getChild("html") != null) {
				notesContent = node.getChild("html").getChild("body").toXMLString().replaceAll("</?body>", "").trim();
			}						
		}
		
	}
	
	public String toString(){
		String result = "CDSpecie: "+getPositionToCompartment()+" si: "+speciesIdentity+"\n";
		if (listOfCatalyzedReactions!=null){
			for (Catalyzed c : listOfCatalyzedReactions){
				result+=c.toString()+"\n";
			}
		}
		return result;
	}

	public String getPositionToCompartment() {
		return positionToCompartment;
	}
	
	public SpeciesIdentity getSpeciesIdentity(){
		return speciesIdentity;
	}
	
	public Vector<Catalyzed> getListOfCatalyzedReactions(){
		return listOfCatalyzedReactions;
	}

	public String getNotesContent() {
		return notesContent;
	}
}
