package model.builder;

import svgcontroller.SBML2SVGException;
import model.Model;

/**
 * Interfaz de las clases que construyen <code>Model</code> a partir
 * de los documentos. Las clases concretas deben ser creadas mediante
 * la clase <code>ModelBuilderFactory</code> 
 * 
 * @author guille
 *
 */
public interface ModelBuilder {

	/**
	 * Construye el modelo de objetos de un diagrama para un SBML de CellDesigner. 
	 * 
	 * @return <code>Model</code> del diagrama
	 * @throws SBML2SVGException
	 */
	public Model buildModel() throws SBML2SVGException;
	
}
