package celldesignerparse_4_0.reaction;

import java.awt.geom.Point2D;

import org.sbml.libsbml.XMLNode;

import celldesignerparse_4_0.commondata.Line;
import celldesignerparse_4_0.commondata.SingleLine;

public class ReactantLink extends Reactant{
	protected Point2D targetLineIndex;
	protected Line line;
	
	public ReactantLink(XMLNode node) {
		super(node);
		String s = node.getAttributes().getValue("targetLineIndex");
		String[] split = s.split("[,]");
		targetLineIndex = new Point2D.Double(Integer.parseInt(split[0]),Integer.parseInt(split[1]));
		line = new SingleLine( node.getChild("line") );
		
	}

	public Point2D getTargetLineIndex(){
		return targetLineIndex;
	}
	
	public Line getLine(){
		return line;
	}
}
