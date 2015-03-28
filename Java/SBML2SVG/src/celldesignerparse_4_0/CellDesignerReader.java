package celldesignerparse_4_0;

import org.sbml.libsbml.Model;
import org.sbml.libsbml.SBMLDocument;
import org.sbml.libsbml.SBMLReader;
import org.sbml.libsbml.XMLNamespaces;

/**
 * Clase que lee un SBML con anotaciones de CellDesigner
 * 
 * @author Guillermo Fajula Leal
 *
 */
public class CellDesignerReader extends SBMLReader{
	
	public CellDesignerReader(){
		super();
	}
	
	/**
	 * Método principal, lee un documento SBML, y a partir de él
	 * crea un <code>CellDesignerDocument</code>.
	 * Redefine <code>SBMLReader.readSBML</code>
	 * 
	 * @param file fichero SBML con anotaciones de CellDesigner
	 *
	public CellDesignerDocument readSBML(String file){
		return new CellDesignerDocument(super.readSBML(file));
	}*/
	
	/**
	 * Detecta si el documento SBML está creado con la versión 4 de 
	 * CellDesigner
	 * 
	 * @param doc 
	 * @return <code>true</code> si es de la version 4.
	 *          <code>false</code> en caso contrario
	 */
	public static boolean isCellDesignerVersion4(SBMLDocument doc) {
		Model mdl = doc.getModel();
		XMLNamespaces xmlns = doc.getNamespaces();

		if ( xmlns.getIndex("http://www.sbml.org/2001/ns/celldesigner") < 0 ) {
			return false;
		}
//		
//		if ( xmlns.getIndex("http://www.sbml.org/sbml/level2/version4") < 0 ) {
//			return false;
//		}	
		
		if (mdl.getAnnotation() == null ) return false;
		
		if ( (mdl.getAnnotation().getChild(0).getName().equals("modelVersion")  &&	
		     mdl.getAnnotation().getChild(0).getChild(0).toString().equals("4.0") )  || 
		    		 ( mdl.getAnnotation().getChild(0).getChild(0).getName().equals("modelVersion") &&
		    		 mdl.getAnnotation().getChild(0).getChild(0).getChild(0).toString().equals("4.0") ) )		 {
			return true;
		} 
		
		return false;
	}
	
	/**
	 * Detecta si el documento SBML está creado con la versión 4 de 
	 * CellDesigner
	 * 
	 * @param doc 
	 * @return <code>true</code> si es de la version 4.
	 *          <code>false</code> en caso contrario
	 */
	public static boolean isCellDesignerVersion2(SBMLDocument doc) {
		Model mdl = doc.getModel();
		XMLNamespaces xmlns = doc.getNamespaces();

		if ( xmlns.getIndex("http://www.sbml.org/2001/ns/celldesigner") < 0 ) {
			return false;
		}
		
		if (mdl.getAnnotation() == null ) return false;
		
		if ( (mdl.getAnnotation().getChild(0).getName().equals("modelVersion")  &&	
		     mdl.getAnnotation().getChild(0).getChild(0).toString().equals("2.3") )  || 
		    		 ( mdl.getAnnotation().getChild(0).getChild(0).getName().equals("modelVersion") &&
		    		 mdl.getAnnotation().getChild(0).getChild(0).getChild(0).toString().equals("2.3") ) )		 {
			return true;
		} 
		if ( (mdl.getAnnotation().getChild(0).getName().equals("modelVersion")  &&	
			     mdl.getAnnotation().getChild(0).getChild(0).toString().equals("2.5") )  || 
			    		 ( mdl.getAnnotation().getChild(0).getChild(0).getName().equals("modelVersion") &&
			    		 mdl.getAnnotation().getChild(0).getChild(0).getChild(0).toString().equals("2.5") ) )		 {
				return true;
			} 
		
		return false;
	}

}
