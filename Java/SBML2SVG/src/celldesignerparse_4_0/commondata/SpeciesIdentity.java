package celldesignerparse_4_0.commondata;

import org.sbml.libsbml.XMLNode;

public class SpeciesIdentity {
	private XMLNode node;
	private State state;
	
	public SpeciesIdentity(XMLNode node){
		this.node = node;
	}
	
	public String getClassOf(){
		for (int i=0;i<node.getNumChildren();i++){
			if (node.getChild(i).getName().compareToIgnoreCase("class")==0){
				return node.getChild(i).getChild(0).getCharacters();
			}
		}
		return "";
	}
	
	public String getValue(){
		for (int i=0;i<node.getNumChildren();i++){
			if (node.getChild(i).getName().compareToIgnoreCase("geneReference")==0){
				return node.getChild(i).getChild(0).getCharacters();
			} else if (node.getChild(i).getName().compareToIgnoreCase("proteinReference")==0){
				return node.getChild(i).getChild(0).getCharacters();
			} else if (node.getChild(i).getName().compareToIgnoreCase("rnaReference")==0){
				return node.getChild(i).getChild(0).getCharacters();
			} else if (node.getChild(i).getName().compareToIgnoreCase("antisensernaReference")==0){
				return node.getChild(i).getChild(0).getCharacters();
			} else if (node.getChild(i).getName().compareToIgnoreCase("name")==0){
				return node.getChild(i).getChild(0).getCharacters();
			}
		}
		return "";
	}
	
	public State getState(){
		if (state!=null) return state;
		
		for (int i=0;i<node.getNumChildren();i++){
			if (node.getChild(i).getName().equalsIgnoreCase("state") ){
				return new State(node.getChild(i));
			}
		}
		return null;
	}
	
	public boolean isHypothetical(){
		for (int i=0;i<node.getNumChildren();i++){
			if (node.getChild(i).getName().equalsIgnoreCase("hypothetical") ){
				return node.getChild(i).getChild(0).getCharacters().equalsIgnoreCase("true");
			}
		}
		return false;
	}
	
	public String toString(){
		return "class: "+getClassOf()+" value: "+getValue()+" state: "+getState();
	}
}
