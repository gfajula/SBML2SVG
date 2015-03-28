package celldesignerparse_4_0.reaction;

import java.util.Vector;

import org.sbml.libsbml.Reaction;
import org.sbml.libsbml.XMLNode;

import celldesignerparse_4_0.commondata.SingleLine;


public class CellDesignerReaction extends Reaction{
	private String reactionType;
	private Vector<Reactant> reactants;
	private Vector<Product> products;
	private Vector<ReactantLink> reactantLinks;
	private Vector<ProductLink> productLinks;
	private EditPoints editPoints;
	private int[] arm;
	private int tShapeIndex;
	private int rectangleIndex;
	private SingleLine line;
	private Vector<Modification> listOfModification;

	public CellDesignerReaction(Reaction reaction) {
		super(reaction);
		initialize();
	}

	private void initialize() {		
		XMLNode node = this.getAnnotation();
		if (node==null){
			return;
		}
		
		// Adaptación a XMLs creados con CellDesigner 4.1 :		
		if (node.getChild(0).getName().equals("extension")) {
			node = node.getChild(0);
		}
		
		for (int i=0;i<node.getNumChildren();i++){
			if (node.getChild(i).getName().compareToIgnoreCase("reactionType")==0){
				reactionType = node.getChild(i).getChild(0).getCharacters();
			} else if (node.getChild(i).getName().equalsIgnoreCase("baseReactants") ){
				XMLNode child = node.getChild(i);
				reactants = new Vector<Reactant>();
				for (int j=0;j<child.getNumChildren();j++){
					reactants.addElement(new Reactant(child.getChild(j)));
				}
			} else if (node.getChild(i).getName().equalsIgnoreCase("baseProducts")){
				XMLNode child = node.getChild(i);
				products = new Vector<Product>();
				for (int j=0;j<child.getNumChildren();j++){
					products.addElement(new Product(child.getChild(j)));
				}
			} else if (node.getChild(i).getName().equalsIgnoreCase("listOfReactantLinks")){
				XMLNode child = node.getChild(i);
				reactantLinks = new Vector<ReactantLink>();
				for (int j=0;j<child.getNumChildren();j++){
					reactantLinks.addElement(new ReactantLink(child.getChild(j)));
				}
			} else if (node.getChild(i).getName().equalsIgnoreCase("listOfProductLinks")){
				XMLNode child = node.getChild(i);
				productLinks = new Vector<ProductLink>();
				for (int j=0;j<child.getNumChildren();j++){
					productLinks.addElement(new ProductLink(child.getChild(j)));
				}
			} else if (node.getChild(i).getName().equalsIgnoreCase("connectScheme")){
				try {
					rectangleIndex = Integer.parseInt( node.getChild(i).getAttributes().getValue("rectangleIndex") );
				} catch (NumberFormatException e) {
					rectangleIndex = 0;
				}
			} else if (node.getChild(i).getName().equalsIgnoreCase("editPoints")){
				arm = new int[3];
				if ( !node.getChild(i).getAttributes().getValue("num0").equals("") ) {
					arm[0] = Integer.parseInt(node.getChild(i).getAttributes().getValue("num0"));
				} else {
					arm[0] = 0;
				}
				
				if ( !node.getChild(i).getAttributes().getValue("num1").equals("") ) {
					arm[1] = Integer.parseInt(node.getChild(i).getAttributes().getValue("num1"));
				} else {
					arm[1] = 0;
				}
				
				if ( !node.getChild(i).getAttributes().getValue("num2").equals("") ) {
					arm[2] = Integer.parseInt(node.getChild(i).getAttributes().getValue("num2"));
				} else {
					arm[2] = 0;
				}
				
				if ( !node.getChild(i).getAttributes().getValue("tShapeIndex").equals("") ) {
					tShapeIndex = Integer.parseInt(node.getChild(i).getAttributes().getValue("tShapeIndex"));
				} else {
					tShapeIndex = 0;
				}
				
				editPoints = new EditPoints(node.getChild(i));
				
				
				
			} else if (node.getChild(i).getName().equalsIgnoreCase("line")){
				line = new SingleLine(node.getChild(i));
			} else if (node.getChild(i).getName().equalsIgnoreCase("listOfModification")){
				listOfModification = new Vector<Modification>();
				for (int j=0;j<node.getChild(i).getNumChildren();j++){
					Modification m = new Modification(node.getChild(i).getChild(j));
					listOfModification.addElement(m);
				}
			}
		}
	}

	public int getRectangleIndex() {
		return rectangleIndex;
	}

	public Vector<Modification> getListOfModification(){
		return listOfModification;
	}
	
	public String getReactionType(){
		return reactionType;
	}
	
	public Vector<Reactant> getBaseReactants(){
		return reactants;
	}
	
	public Vector<Product> getBaseProducts(){
		return products;
	}
	
	public Vector<ReactantLink> getReactantLinks(){
		if (reactantLinks==null){
			reactantLinks = new Vector<ReactantLink>(); 
		}
		return reactantLinks;
	}
	
	public Vector<ProductLink> getProductLinks(){
		if (productLinks==null){
			productLinks = new Vector<ProductLink>(); 
		}
		return productLinks;
	}
	
	public EditPoints getEditPoints(){
		return editPoints;
	}
	
	public SingleLine getLineCellDesigner(){
		return line;
	}

	public int[] getArms() {
		return arm;
	}
	
	public int getArm(int i) {
		if (arm!=null) {
			return arm[i];
		} else {
			return 0;
		}
	}

	public int getTShapeIndex() {
		return tShapeIndex;
	}
	
}
