package celldesignerparse_4_0.annotation.listOfIncludedSpecies;

import org.sbml.libsbml.XMLNode;

import celldesignerparse_4_0.commondata.SpeciesIdentity;

public class Species {
	private String id;
	private String name;
	private String complexSpecies;
	private SpeciesIdentity speciesIdentity;
	private String notesContent;
	
	public Species(XMLNode child) {
		id = child.getAttributes().getValue("id");
		name = child.getAttributes().getValue("name");
		for (int i=0;i<child.getNumChildren();i++){
			if (child.getChild(i).getName().equalsIgnoreCase("annotation")){
				XMLNode n = child.getChild(i);
				for (int j=0;j<n.getNumChildren();j++){
					if (n.getChild(j).getName().compareToIgnoreCase("complexSpecies")==0){
						complexSpecies = n.getChild(j).getChild(0).getCharacters();
					} else if (n.getChild(j).getName().compareToIgnoreCase("speciesIdentity")==0){
						speciesIdentity = new SpeciesIdentity(n.getChild(j));
					}
				}
			} else if (child.getChild(i).getName().equalsIgnoreCase("notes")) {
				XMLNode n = child.getChild(i);
				if (n.getChild("html") != null) {
					notesContent = n.getChild("html").getChild("body").toXMLString().replaceAll("</?body>", "");
				}
			}
		}
		
	}

	public String toString(){
		return "name: "+name+" id: "+id+" complexSpecies: "+complexSpecies+"speciesIdentity:\n"+speciesIdentity.toString()+"\n";
	}

	public String getId() {
		return id;
	}
	
	public String getComplexSpecies() {
		return complexSpecies;
	}

	public SpeciesIdentity getSpeciesIdentity() {
		return speciesIdentity;
	}

	public String getNotesContent() {
		return notesContent;
	}
}
