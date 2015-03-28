package celldesignerparse_4_0.annotation.listOfCompartmentAliases;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.sbml.libsbml.XMLNode;

import celldesignerparse_4_0.annotation.InformationUnit;
import celldesignerparse_4_0.commondata.DoubleLine;
import celldesignerparse_4_0.commondata.Line;
import celldesignerparse_4_0.commondata.Paint;

/**
 * Clase que modela el nodo <code>comparmentAlias</code> de la
 * annotation de CellDesigner
 * 
 * @author Guillermo Fajula Leal
 *
 */
public class CompartmentAlias {
	private String id;
	private String compartment;
	private String classOf;
	private Rectangle2D bounds;
	private Point2D namePoint;
	private Line line;
	private Paint paint;
	private InformationUnit info;
	
	/**
	 * Constructor por defecto a partir del noso XML
	 * 
	 * @param node
	 */
	public CompartmentAlias(XMLNode node) {
		id = node.getAttributes().getValue("id");
		compartment = node.getAttributes().getValue("compartment");
		for (int i=0;i<node.getNumChildren();i++){
			if (node.getChild(i).getName().equalsIgnoreCase("class") ){
				classOf = node.getChild(i).getChild(0).getCharacters();
			} else if (node.getChild(i).getName().equalsIgnoreCase("bounds") ){
				bounds = new Rectangle2D.Double(
						Double.parseDouble(node.getChild(i).getAttributes().getValue("x")),
						Double.parseDouble(node.getChild(i).getAttributes().getValue("y")),
						Double.parseDouble(node.getChild(i).getAttributes().getValue("w")),
						Double.parseDouble(node.getChild(i).getAttributes().getValue("h")));
			} else if (node.getChild(i).getName().equalsIgnoreCase("point") ){
				bounds = new Rectangle2D.Double(
						Double.parseDouble(node.getChild(i).getAttributes().getValue("x")),
						Double.parseDouble(node.getChild(i).getAttributes().getValue("y")),
						0,0);
			} else if (node.getChild(i).getName().equalsIgnoreCase("namePoint") ){
				namePoint = new Point2D.Double(
						Double.parseDouble(node.getChild(i).getAttributes().getValue("x")),
						Double.parseDouble(node.getChild(i).getAttributes().getValue("y")));
			} else if (node.getChild(i).getName().equalsIgnoreCase("doubleLine") ){
				line = new DoubleLine(node.getChild(i));
			} else if (node.getChild(i).getName().equalsIgnoreCase("paint") ){
				paint = new Paint(node.getChild(i));
			} else if (node.getChild(i).getName().equalsIgnoreCase("info") ){
				info = new InformationUnit( node.getChild(i) );
			}
		}
	}
	
	/**
	 * Representa la informacion de este objeto en forma de cadena.
	 * (Para desarrollo)
	 */
	public String toString(){
		return "id: "+id+"\n"+
		"compartment: "+compartment+"\n"+
		"class: "+classOf+"\n"+
		"bounds: "+bounds+"\n"+
		"namePoint: "+namePoint+"\n"+
		"line: "+line+"\n"+
		"paint: "+paint+"\n";
	}

	/**
	 * @return Atributo IdAlias del <code>CompartmentAlias</code>
	 */
	public String getIdAlias() {
		return id;
	}

	/**
	 * @return ID del <code>Compartment</code> al que representa este <code>CompartmentAlias</code> 
	 */
	public String getCompartment() {
		return compartment;
	}

	/**
	 * 
	 * @return 
	 */
	public String getClassOf() {
		return classOf;
	}

	/**
	 * 
	 * @return rectangulo que limita geometricamente el <code>CompartmentAlias</code>
	 */
	public Rectangle2D getBounds() {
		return bounds;
	}

	/**
	 * 
	 * @return Punto geométrico en que se escribe el nombre del Compartment
	 */
	public Point2D getNamePoint() {
		return namePoint;
	}

	/**
	 * 
	 * @return <code>Line</code> con el que se dibuja este <code>CompartmentAlias</code>
	 */
	public Line getLine() {
		return line;
	}

	/**
	 * 
	 * @return <code>Paint</code> con el que se dibuja este <code>CompartmentAlias</code>
	 */
	public Paint getPaint() {
		return paint;
	}

	public InformationUnit getInfo() {
		return info;
	}
}
