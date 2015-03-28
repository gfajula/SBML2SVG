package celldesignerparse_4_0;

import org.sbml.libsbml.SBMLDocument;

/**
 * Clase que extiende un <code>SBMLDocument</code> de 'libsbml'
 * con la funcionalidad para obtener la información añadida por
 * CellDesigner a un SBML
 * 
 * @author Guillermo Fajula Leal
 *
 */
public class CellDesignerDocument extends SBMLDocument{
	private CellDesignerModel model;
	
	public CellDesignerDocument(SBMLDocument doc) {
		super(doc);
		model = new CellDesignerModel(doc.getModel());
	}

	@Override
	public CellDesignerModel getModel(){
		return model;
	}
}
