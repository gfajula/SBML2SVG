import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.Annotation;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;


public class JSbmlSandbox {

	/**
	 * @param args
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, XMLStreamException {
		SBMLDocument sbmlDoc = new SBMLReader().readSBML("/media/DATA/_guillermo/desarrollo/PFC/samples/components40delta1.xml");
		//CellDesignerDocument cdDoc = new CellDesignerDocument(sbmlDoc);
		
		System.out.println( sbmlDoc.getAnnotationString() );
		
		System.out.println( sbmlDoc.getChildCount() );
		
		Annotation annot = sbmlDoc.getModel().getAnnotation();
		// System.out.println( sbmlDoc.getModel().getAnnotationString() );
			
		System.out.println( annot.getNonRDFannotation() );
		System.out.println( "---" );
		System.out.println( annot );
	}

}
