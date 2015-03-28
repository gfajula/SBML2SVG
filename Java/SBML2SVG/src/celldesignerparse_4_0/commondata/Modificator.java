package celldesignerparse_4_0.commondata;

import org.sbml.libsbml.XMLNode;

public class Modificator {
	private String residue;
	private String state;
	
	public Modificator(XMLNode child) {
		residue = child.getAttributes().getValue("residue");
		state = child.getAttributes().getValue("state");
	}
	
	public String getResidue(){
		return residue;
	}
	
	public String getState(){
		
		return state;
	}
	
	public String toString(){
		return "residue: "+residue+" state: "+state;
	}
}
