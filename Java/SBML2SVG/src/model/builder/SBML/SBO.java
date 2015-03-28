package model.builder.SBML;

public class SBO {
	// ENTITY
	public static final String PHENOTYPE = "SBO:0000358";
	public static final String SIMPLE_CHEMICAL = "SBO:0000247";
	public static final String UNSPECIFIED_ENTITY = "SBO:0000285";
	
	// MODIFICATIONS
	public static final String MODULATION = "SBO:0000168";
	public static final String STIMULATION = "SBO:0000170";
	public static final String CATALYSIS = "SBO:0000172";
	public static final String INHIBITION = "SBO:0000169";
	public static final String  NECESSARY_STIMULATION = "SBO:0000171";

	// REACTIONS
	public static final String DISSOCIATION = "SBO:0000180";
	public static final String ASSOCIATION = "SBO:0000177";
	public static final String DEGRADATION = "SBO:0000179";
	public static final String OMITTED = "SBO:0000397";
	
	
	
	public static String modificationType( String sboId ) {
		 String type = "";
		 
		 if ( sboId.equals("") ) {
			 // Por comodidad, se toma CATALYSIS por 
			 // la modification 'por defecto' a ser la misma
			 // que toma CellDesigner
			 type="CATALYSIS";
		 } else 
		 if ( sboId.equals("SBO:0000168") ) {
			 type = "MODULATION";
		 } else if ( sboId.equals("SBO:0000170") ) {
			 type = "STIMULATION";
		 } else if ( sboId.equals("SBO:0000172") ) {
			 type = "CATALYSIS";
		 } else if ( sboId.equals("SBO:0000169") ) {
			 type = "INHIBITION";
		 } else if ( sboId.equals("SBO:0000171") ) {
			 // En terminologia SBGN, 'Necessary Stimulation'
			 type = "TRIGGER";
		 } else {
			 
			 type="CATALYSIS";
		 }
		 
		 return type;
	}
}
