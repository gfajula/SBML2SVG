package model.builder;

import model.builder.CD4.CellDesigner4ModelBuilder;
import model.builder.SBML.StandardSBMLModelBuilder;

import org.sbml.libsbml.SBMLDocument;

import svgview.SVGOutput;
import celldesignerparse_4_0.CellDesignerDocument;
import celldesignerparse_4_0.CellDesignerReader;

/**
 * Clase que construye el <code>ModelBuilder</code> adecuado
 * al tipo de documento SBML que se le pase.
 * 
 * @author Guillermo Fajula Leal
 *
 */
public class ModelBuilderFactory {

	/**
	 * Obtiene el <code>ModelBuilder</code>
	 * 
	 * @param sbmlDoc documento a convertir en <code>Model</code>
	 * 
	 * @return <code>ModelBuilder</code>
	 */
	public static  ModelBuilder getModelBuilder(SBMLDocument sbmlDoc) {
		
		if (CellDesignerReader.isCellDesignerVersion4(sbmlDoc)) {			
			// Build CellDesignerModel out of SBMLDocument
			CellDesignerDocument cdDoc = new CellDesignerDocument(sbmlDoc);
			SVGOutput.printStatistic("ModelVersion" , "CellDesigner 4");
			return new CellDesigner4ModelBuilder(cdDoc.getModel());
		} else if (CellDesignerReader.isCellDesignerVersion2(sbmlDoc)) {			
			// Build CellDesignerModel out of SBMLDocument
			CellDesignerDocument cdDoc = new CellDesignerDocument(sbmlDoc);
			SVGOutput.printStatistic("ModelVersion" , "CellDesigner 2");
			SVGOutput.printVerbose("Versión no soportada. Es posible que se produzcan errores.");
			return new CellDesigner4ModelBuilder(cdDoc.getModel());
		} else {
			return new StandardSBMLModelBuilder(sbmlDoc.getModel());
		}	
	}

}
