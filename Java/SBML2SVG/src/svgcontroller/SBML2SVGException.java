package svgcontroller;

/**
 * Clase que modela los errores específicos al funcionamiento
 * del conversor SBML a SVG
 * 
 * @author Guillermo Fajula Leal
 * 
 * 
 */
public class SBML2SVGException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2402309140324319704L;

	/**
	 * Constructor estándar, sin mensaje específico
	 */
	public SBML2SVGException() {
		super();
	}

	/**
	 * Constructor con mensaje de error.
	 *
	 * @param message mensaje describiendo el error que se ha producido.
	 */
	public SBML2SVGException(String message) {
		super(message);
	}

}
