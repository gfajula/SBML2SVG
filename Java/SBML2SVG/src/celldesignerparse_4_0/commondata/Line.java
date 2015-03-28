package celldesignerparse_4_0.commondata;

import java.awt.Color;

import org.sbml.libsbml.XMLNode;

/**
 * 
 * @author Guillermo Fajula Leal
 *
 */
public abstract class Line {
	public double getWidth() {
		return width;
	}

	public String getType() {
		return type;
	}

	protected Color color;
	protected double width;
	protected String type;
	
	public Line() {
		super();
		color = new Color(0, 0, 0);
	}
	
	public Line(XMLNode node){
		String col = node.getAttributes().getValue("color");
		if (col.length()>0){
			color = decodeColor(col);
		}
		this.type = node.getAttributes().getValue("type");
		try {
			this.width = Double.parseDouble(node.getAttributes().getValue("width"));
		} catch(NumberFormatException e) {
			this.width = 1.0;
			// System.err.println("Fallo leyendo line@width: " + node.getAttributes().getValue("width"));
		}
	}
	
	public Color getColor(){
		return color;
	}
	
	private Color decodeColor(String s){
		int[] partial = new int[4];
		partial[0] = toNumber(s.charAt(0))*16;
		partial[0]+= toNumber(s.charAt(1));
		partial[1] = toNumber(s.charAt(2))*16;
		partial[1]+= toNumber(s.charAt(3));
		partial[2] = toNumber(s.charAt(4))*16;
		partial[2]+= toNumber(s.charAt(5));
		partial[3] = toNumber(s.charAt(6))*16;
		partial[3]+= toNumber(s.charAt(7));
		
		return new Color(partial[1], partial[2], partial[3], partial[0]);
	}
	
	private int toNumber(char c){
		if (('0'<=c) && (c<='9')){
			return c-'0';
		} else if (('a'<=c) && ('f'>=c)){
			return 10+(c-'a');
		} else if (('A'<=c) && ('F'>=c)){
			return 10+(c-'A');
		}
		return -1;
	}
}
