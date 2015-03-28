package celldesignerparse_4_0.annotation.listOfAntiSenseRNAs;

import org.sbml.libsbml.XMLNode;

import celldesignerparse_4_0.annotation.listOfRNAs.RNA;

/**
 * AntiSenseRNA. 
 * Hereda directamente de RNA, ya que son idénticas.
 * 
 * @author guille
 *
 */
public class AntiSenseRNA extends RNA {

	public AntiSenseRNA(XMLNode node) {
		super(node);
	}

}
