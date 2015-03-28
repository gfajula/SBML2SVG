package svgview.util.quadtree;

import java.io.PrintWriter;
import java.util.Date;
import java.util.Vector;


/**
 * QuadTree estático, construido solo una vez, y al que no se añadirán elementos
 * en tiempo de ejecución.
 * 
 * Para equilibrar lo más posible, el número de elementos en cada porción del
 * espacio, cada 4-partición no tiene porqué ser en 4 partes iguales, sino en
 * las 4 áreas que mejor repartan el número de objetos
 * 
 * @author guillermo fajula leal
 *
 */

public class StaticQuadTree {
	public final static int MAX_DEPTH = 8;
	public final static int MAX_PER_BUCKET = 5;
	public final static int ABOVE_LEFT = 0;
	public final static int ABOVE_RIGHT = 1;
	public final static int BELOW_LEFT = 2;
	public final static int BELOW_RIGHT = 3;
	public final static int ABOVE = 4; // ( comparte ABOVE_LEFT y ABOVE_RIGHT )
	public final static int BELOW = 5; // ( comparte BELOW_LEFT y BELOW_RIGHT )
	public final static int LEFT = 6;  // ( comparte ABOVE_LEFT y BELOW_LEFT )
	public final static int RIGHT = 7; // ( comparte ABOVE_RIGHT y BELOW_RIGHT )  
	public final static int CONTAINING = 8;	
	
	private boolean closed = false;
	private Vector<QuadTreeElement> elements;
	private QTArea root;
	
	public static int tries = 0; // estadistica para saber cuantas subareas se han registrado
	
	public StaticQuadTree(int width, int height) {
		this.root = new QTArea(0,0, width, height);
	}
	
	@SuppressWarnings("unchecked")
	public void doIndex() {
		if (this.closed) return;
		System.out.println("Indexing " + this.getElementsSize() + " elements");
		if (this.elements != null) {
			this.root.setElements( (Vector<QuadTreeElement>) elements.clone() );
		}		
		this.root.split(0);
		this.closed = true;
	}
	
	private int getElementsSize() {
		if (this.elements == null) {
			return 0;
		} else {
			return this.elements.size();
		}
	}
	public void addElement( QuadTreeElement elm ){
		if (this.elements == null) {
			this.elements = new Vector<QuadTreeElement>();
		}
		if (!closed) {
			// fix Ids
			elm.fixId( this.elements.size() );
			this.elements.add( elm );			
		}
	}
	
	public String toJSON() {
		if (!closed) {
			return "ERROR: Quadtree not indexed yet!";
		} else {
			StringBuffer json = new StringBuffer();			
			json.append( root.toJSON() );			
			return json.toString();
		}
	}
	
	public QuadTreeElement searchElement(int x, int y) {
		tries = 0;
		Date d0 = new Date();
		QuadTreeElement e = this.root.searchElement(x, y);		
		System.out.println("Buscados " + tries + " nodos en " + (new Date().getTime() - d0.getTime()) + " milisegundos" );
		return e;
	}
	
	public void saveToFile(String file, boolean addVarDefinition, StringBuffer json) {
		try {
	        // FileWriter f = new FileWriter (file);	        
	        PrintWriter writer = new PrintWriter (file, "UTF-8");
	        // Get the created document from the canvas
	        
	        /*
	        if (addVarDefinition) {
	        	writer.print( "var qtree = " + this.toJSON() + ";");
	        } else {
	        	writer.print( this.toJSON() );
	        }
	        */
	        
	        writer.print( json );
	        writer.close();
	        // f.close();
		} catch ( Exception e) {
			e.printStackTrace();
		}
	}
	
}
