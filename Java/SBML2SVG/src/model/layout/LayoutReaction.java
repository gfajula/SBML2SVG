package model.layout;

import java.util.HashMap;

import model.MCompartment;
import model.reaction.MModification;
import model.reaction.MReaction;
import model.specie.MSpeciesLink;
import y.base.Edge;
import y.base.Node;

class LayoutReaction {


	MReaction mr;
	MCompartment comp;
	HashMap<MModification, Edge> modEdges;
	HashMap<MSpeciesLink, Edge> reacEdges;
	HashMap<MSpeciesLink, Edge> prodEdges;
	
	
	Node editPointR = null;
	Node editPointP = null;
	Node center = null;
	
	Edge r2epR = null;
	Edge epR2center = null;
	Edge center2epP = null;
	Edge epP2P = null;

	public LayoutReaction() {
		reacEdges = new HashMap<MSpeciesLink, Edge>();
		prodEdges = new HashMap<MSpeciesLink, Edge>();
		modEdges = new HashMap<MModification, Edge>();
	}

}