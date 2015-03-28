package celldesignerparse_4_0.annotation.listOfSpeciesAliases;

import java.awt.geom.Rectangle2D;
import java.util.Vector;

import org.sbml.libsbml.XMLNode;

import celldesignerparse_4_0.annotation.InformationUnit;
import celldesignerparse_4_0.commondata.Paint;
import celldesignerparse_4_0.commondata.View;

public class SpeciesAlias {
	private String id;
	private String species;
	private String complexSpeciesAlias;
	private String compartmentAlias;
	private String activity;
	private Rectangle2D bounds;
	private String viewState;
	private View usualView;
	private View briefView;
	private Vector<Tag> tags;
	private double structuralStateAngle = Math.PI/2;
	private InformationUnit info;
	
	public SpeciesAlias(XMLNode node) {
		id = node.getAttributes().getValue("id");
		species = node.getAttributes().getValue("species");
		complexSpeciesAlias = node.getAttributes().getValue("complexSpeciesAlias");
		compartmentAlias = node.getAttributes().getValue("compartmentAlias");
		for (int i=0;i<node.getNumChildren();i++){
			if (node.getChild(i).getName().equalsIgnoreCase("activity") ){
				activity = node.getChild(i).getChild(0).getCharacters();
			} else if (node.getChild(i).getName().equalsIgnoreCase("bounds") ){
				bounds = new Rectangle2D.Double(
						Double.parseDouble(node.getChild(i).getAttributes().getValue("x")),
						Double.parseDouble(node.getChild(i).getAttributes().getValue("y")),
						Double.parseDouble(node.getChild(i).getAttributes().getValue("w")),
						Double.parseDouble(node.getChild(i).getAttributes().getValue("h")));
			} else if (node.getChild(i).getName().equalsIgnoreCase("view") ){
				viewState = node.getChild(i).getAttributes().getValue("state");
			} else if (node.getChild(i).getName().equalsIgnoreCase("structuralState") ){
				try{
					structuralStateAngle = Double.parseDouble(
								node.getChild(i).getAttributes().getValue("angle") );
				} catch (NumberFormatException nfe){
					structuralStateAngle = Math.PI/2;
				}
			} else if (node.getChild(i).getName().equalsIgnoreCase("usualView") ){
				usualView = new View(node.getChild(i));
			} else if (node.getChild(i).getName().equalsIgnoreCase("briefView") ){
				briefView = new View(node.getChild(i));
			} else if (node.getChild(i).getName().equalsIgnoreCase("listOfSpeciesTag") ){
				tags = getListOfTags( node.getChild(i) );
				
			} else if (node.getChild(i).getName().equalsIgnoreCase("info") ){
				info = new InformationUnit( node.getChild(i) );
			}
		}
	}
	
	private Vector<Tag> getListOfTags(XMLNode listOfSpeciesTagNode) {
		Vector<Tag> tags = new Vector<Tag>();
		for (int i=0; i < listOfSpeciesTagNode.getNumChildren() ; i++){
			XMLNode speciesTagNode = listOfSpeciesTagNode.getChild(i);
			Tag tag = new Tag(); 
			for (int j=0; j < speciesTagNode.getNumChildren() ; j++){
				XMLNode n = speciesTagNode.getChild(j);
				if ( n.getName().compareToIgnoreCase("KeyInfo")==0) {
					tag.setName( n.getAttributes().getValue("name") ) ;
					String direct = n.getAttributes().getValue("direct");
					if ( direct.equals("LEFT")) {
						tag.setDirection( Tag.LEFT );
					} else if ( direct.equals("RIGHT")) {
						tag.setDirection( Tag.RIGHT );
					} else if ( direct.equals("UP")) {
						tag.setDirection( Tag.UP );
					} else if ( direct.equals("DOWN")) {
						tag.setDirection( Tag.DOWN );
					}
				} else if (n.getName().compareToIgnoreCase("TagBounds")==0){
					tag.setBounds( new Rectangle2D.Double(
							Double.parseDouble( n.getAttributes().getValue("x") ),
							Double.parseDouble( n.getAttributes().getValue("y") ),
							Double.parseDouble( n.getAttributes().getValue("w") ),
							Double.parseDouble( n.getAttributes().getValue("h") ) ) );
				} else if (speciesTagNode.getChild(j).getName().compareToIgnoreCase("TagEdgeLine")==0){
					tag.setLineWidth( Double.parseDouble( n.getAttributes().getValue("width") ) );
				} else if (speciesTagNode.getChild(j).getName().compareToIgnoreCase("TagFramePaint")==0){
					tag.setFramePaint( new Paint( n ) );
				}
			
			}
			
			tags.add( tag );
			
			
			
		}
		
		return tags;
	}

	public String toString(){
		return id+"\n"+ species+"\n"+activity+"\n"+bounds+"\n"+viewState+
		"\n"+usualView+"\n"+briefView;
	}

	public String getId() {
		return species;
	}

	public String getAliasId() {
		return id;
	}

	public String getActivity() {
		return activity;
	}

	public Rectangle2D getBounds() {
		return bounds;
	}

	public String getViewState() {
		return viewState;
	}
	
	public View getUsualView() {
		return usualView;
	}

	public View getBriefView() {
		return briefView;
	}

	public String getComplexSpeciesAlias() {
		return complexSpeciesAlias;
	}

	public Vector<Tag> getTags() {
		return this.tags;
	}

	public String getCompartmentAlias() {
		return compartmentAlias;
	}

	public InformationUnit getInfo() {
		return info;
	}

	public void setInfo( InformationUnit info) {
		this.info = info;
	}

	public double getStructuralStateAngle() {
		return structuralStateAngle;
	}

}
