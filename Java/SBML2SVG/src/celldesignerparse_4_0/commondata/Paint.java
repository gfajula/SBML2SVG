package celldesignerparse_4_0.commondata;

import java.awt.Color;

import org.sbml.libsbml.XMLNode;

import svgview.SVGConfig;

public class Paint {
	private Color color;
	private String scheme;
	
	public Paint() {
		super();
		color = new Color(SVGConfig.defaultFillColorR,
						  SVGConfig.defaultFillColorG,
						  SVGConfig.defaultFillColorB);
		scheme = "Color";
	}
	
	public Paint( Color color ) {
		super();
		this.color = color;
	}
	
	public Paint(XMLNode child) {
		color = decodeColor(child.getAttributes().getValue("color"));
		scheme = child.getAttributes().getValue("scheme");
	}
	
	public Color getColor(){
		return color;
	}
	
	public String getScheme(){
		return scheme;
	}
	
	public String toString(){
		return "color: "+color+" scheme: "+scheme;
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

	public void setColor(Color color) {
		this.color = color;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
}
