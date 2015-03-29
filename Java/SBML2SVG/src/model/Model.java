package model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.TreeMap;
import java.util.Vector;

import model.layer.MTextLayer;
import model.reaction.MBooleanLogicGate;
import model.reaction.MModification;
import model.reaction.MReaction;
import model.specie.MAddedSpeciesLink;
import model.specie.MDegraded;
import model.specie.MSpecies;
import model.specie.MSpeciesComplex;
import model.specie.MSpeciesLink;
import model.specie.MSpeciesSimple;

import org.json.JSONObject;

import celldesignerparse_4_0.commondata.Paint;
import celldesignerparse_4_0.commondata.SingleLine;
import celldesignerparse_4_0.commondata.View;

/**
 * Modelo de un diagrama de un SBML.
 * Este se forma principalment de:
 * - Compartments, que actuan como contenedores de species y reacciones
 * - Species, que participan en las reacciones
 * - SpeciesComplex, que englobando varias Species individuales, participan
 * 					 en una reacción como si fueran una Specie individual
 * - Reactions, que modelan las interacciones entre las species
 * 
 * 
 * @author Guillermo Fajula Leal
 *
 */
public class Model {
	public Model() {
		super();
		this.name = "";
		this.diagramSize = new Dimension(640, 480);
	}

	private String name;
	private MCompartment defaultCompartment = null;
	private TreeMap<String,MCompartment> compartments = new TreeMap<String,MCompartment>();
	private TreeMap<String,MSpeciesComplex> complexAliases = new TreeMap<String,MSpeciesComplex>();
	private TreeMap<String,MSpeciesSimple> simpleAliases = new TreeMap<String,MSpeciesSimple>();
	private Vector<MTextLayer> textLayers = new Vector<MTextLayer>();
	// Lista de Species, por ID real
	private TreeMap<String,Vector<MSpecies>> species = new TreeMap<String,Vector<MSpecies>>();
	private TreeMap<String,MReaction> reactions = new TreeMap<String,MReaction>();
	private Dimension diagramSize;
	private boolean transformEditPoints; // Indica si los editpoints estan en valores absolutos, o 
										   // si son valores relativos a las figuras, como en el caso
										   // de CellDesigner
	
	public Collection<MReaction> getReactionsCollection() {
		return reactions.values();
	}
	
	public Collection<MCompartment> getCompartmentsCollection() {
		return compartments.values();
	}
	
	/**
	 * Obtener el <code>MCompartment</code> con el ID dado.
	 * Si no se encuentra se devuelve 'null' 
	 * 
	 * @param compartmentName
	 * @return compartment
	 */
	public MCompartment getCompartment( String compartmentName ){
		return this.compartments.get( compartmentName );
	}
	
	public Collection<MSpeciesSimple> getSimpleAliasesCollection(){
		return simpleAliases.values();
	}
	
	public Collection<MSpeciesComplex> getComplexAliasesCollection(){
		return complexAliases.values();
	}
	
	public void addCompartment(MCompartment mc){
		compartments.put( mc.getId(), mc);
	}

	public void addComplexAlias(MSpeciesComplex msc) {
		complexAliases.put(msc.getIdAlias(), msc);
		/*
		 * Añadir al listado de Species por ID real.
		 */
		addToRealSpecies(msc);
		
	}

	/**
	 * Añadir MSpeciesSimple (SimpleSpeciesAlias) al modelo 
	 * 
	 * @param mss SimpleSpeciesAlias
	 * @param idComplex ID del complex al que pertenece. null si no pertenece a ninguno
	 */
	public void addSimpleAlias(MSpeciesSimple mss, String idComplex) {
		
		// Añadir al listado de SimpleAliases
		simpleAliases.put(mss.getIdAlias() , mss);
		
		/*
		 * Si forma parte de un complejo, añadirlo al mismo
		 */
		if (idComplex!=null && !idComplex.equals("")) {
			addAliasToComplex(mss, idComplex);
		}
		
		/*
		 * Añadir al listado de Species por ID real.
		 */
		addToRealSpecies(mss);
	}

	/**
	 * Añade esta Species a la lista por id de species reales
	 * 
	 * @param mss
	 */
	private void addToRealSpecies(MSpecies ms) {
		if ( this.species.get( ms.getId() ) == null ) {
			Vector<MSpecies> v = new Vector<MSpecies>();
			v.add(ms);
			this.species.put(ms.getId(), v);
		} else {
			this.species.get( ms.getId() ).add(ms);
		}
	}

	public void addReaction(MReaction reaction){
		reactions.put(reaction.getId(), reaction);
	}
	
	/**
	 * Relacionar bidireccionalmente un SimpleAlias/ComplexAlias 
	 * con el ComplexAlias al que pertenece.
	 * 
	 * @param mss
	 * @param idComplex
	 */
	private void addAliasToComplex(MSpeciesSimple mss, String idComplex) {
		
		MSpeciesComplex msc = complexAliases.get( idComplex ) ;
	
		msc.addSpecieAlias(mss);
		mss.setComplex(msc);
		return;		
	}

	public MSpeciesComplex getSpeciesComplexByAlias(String alias) {
		return complexAliases.get(alias);
	}
	
	public MSpeciesComplex getSpeciesComplexById(String id) {
		for ( MSpeciesComplex msc : this.complexAliases.values() ) {
			if ( msc.getId().equals( id ) ) {
				return msc;				
			}
		}
		return null;
	}
	
	public MSpecies getSpecieByName(String name) {
		if ( species.get(name) != null ) {
			return species.get(name).get(0);
		} else {
			return species.get(name).get(0);
		}

	}
	
	public MSpecies getSpecieByAlias(String alias) {
		if ( simpleAliases.get(alias) != null ) {
			return simpleAliases.get(alias);
		} else {
			return complexAliases.get(alias);
		}
	}	
	
	public Vector<MSpecies> getSpeciesByAliases(String[] aliases) {
		Vector<MSpecies> result = new Vector<MSpecies>();
		for (int i=0; i<aliases.length; i++){
			MSpecies spc = getSpecieByAlias(aliases[i]);
			if (spc!=null) result.add(spc);			
		}
		return result;		
	}
	
	/**
	 * Crea un DEGRADED extra para cerrar reacciones con 0 productos o 0 reactivos.
	 * Se le asignará un ID/Nombre compuesto por "x" y un numero, de manera que el ID sea 
	 * unico
	 * 
	 * @param sbmlModel
	 * @return MSpeciesLink que enlace al nuevo MDegraded
	 */
	public MSpeciesLink createExtraDegraded( org.sbml.libsbml.Model sbmlModel ) {
		
		String newId = this.getNewId(sbmlModel, "x" );
		
		MSpeciesSimple newDegraded = 
					new MDegraded( newId, 
								   newId, 
								   "", 
								   getDefaultCompartment().idAlias, 
								  1, 
								  "inactive",
								  new Rectangle2D.Double(0,0,30,30), 
								  "usual", 
								  new View( new Point2D.Double(0,0),
										     new Dimension(30,30) ,
										     new SingleLine(),
										     new Paint( new Color( 255 , 190 , 190 ) ) ),
								  null, 
								  null,
								  null);
		newDegraded.setCompartment( getDefaultCompartment() );
		this.simpleAliases.put(newId , newDegraded );
		
		return new MSpeciesLink( newDegraded, null);
	}
	
	/**
	 * Comprueba si un ID existe en el modelo
	 * 
	 * @param newId
	 * @return <code>true</code> si existe, <code>false</code> en caso contrario
	 */
	private boolean existsIdInSpeciesAliases(String newId) {
		return null != getSpecieByAlias( newId );
	}


	private boolean existsIdInSBMLModel(  org.sbml.libsbml.Model sbmlModel , String id ) {
		
		boolean exists = sbmlModel.getCompartment( id ) != null
							 || sbmlModel.getReaction( id ) != null
							 || sbmlModel.getSpecies( id ) != null 
							 || sbmlModel.getSpeciesType( id ) != null
							 || sbmlModel.getCompartmentType ( id ) != null;
		
		
		return exists;
	}
	
	/**
	 * Genera un nuevo ID, que no se repita en el modelo actual
	 * 
	 * @param sbmlModel modelo SBML, para comprobar los IDs del mismo
	 * @param prefix prefijo a usar en el nuevo ID generado
	 * @return nuevo ID
	 */
	public String getNewId( org.sbml.libsbml.Model sbmlModel, String prefix ) {
		
		if ( prefix != null && 
		     !prefix.equals("") && 
		     !existsIdInSBMLModel( sbmlModel, prefix) &&
		     !existsIdInModel( prefix ) ) {	
			return prefix;
		}
		
		int newIdNumber = 1;
		String newId = prefix + newIdNumber;		
		while ( existsIdInSBMLModel( sbmlModel, newId ) ||
				 existsIdInSpeciesAliases( newId ) ) {
			newId = prefix + (++newIdNumber);			
		}
		
		return newId;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Comprueba si un ID, existe en el modelo actual
	 * 
	 * @param sbmlModel modelo SBML, para comprobar los IDs del mismo
	 * @param prefix prefijo a usar en el nuevo ID generado
	 * @return <code>true</code> si existe, <code>false</code> en caso contrario
	 */
	private boolean existsIdInModel( String searchId ) {
		boolean exists = existsIdInSpeciesAliases(searchId) ||
						  this.reactions.get(searchId)!=null ||
						  this.compartments.get(searchId)!=null;
		return exists;
	}

	
	/**
	 * Obtiene el 'compartment' por defecto en este modelo
	 * 
	 * @return 'compartment' por defecto
	 */
	public MCompartment getDefaultCompartment() {
		return defaultCompartment;
	}

	/**
	 * Establece el 'compartment' por defecto en este modelo
	 * 
	 * @param defaultCompartment 'compartment' por defecto
	 */
	public void setDefaultCompartment(MCompartment defaultCompartment) {
		this.defaultCompartment = defaultCompartment;
	}

	
	public TreeMap<String, MCompartment> getCompartments() {
		return compartments;
	}
	
	/**
	 * Obtiene una representación en JSON del modelo
	 *  
	 * @return 
	 */
	public StringBuffer getJSONHierarchy() {
	    StringBuffer jsonHierarchy = new StringBuffer("{hier : {");

	    
	    jsonHierarchy.append("\"compartments\":[");	
	    boolean addComa = false;
	    for (MCompartment mcomp : this.getCompartmentsCollection()) {
	    	if ( mcomp.isDefault() ) continue;
	    	 
	    	if ( addComa ) jsonHierarchy.append(",");	   
	    	jsonHierarchy.append("{\"id\":\"" + mcomp.getIdAlias() + "\"," +
	    						  "\"name\":\"" + mcomp.getName() + "\"," +
	    						  "\"parent\":\"" + mcomp.getOutside() + "\"" +
	    						 "}");
	    	 	
	    	addComa = true;
	    }	  
	    jsonHierarchy.append("]");
	    	    
	    jsonHierarchy.append(",\"complexes\":{");
	    addComa = false;
	    for (MSpeciesComplex msc : this.getComplexAliasesCollection()) {
	    	
	    	if ( addComa ) jsonHierarchy.append(",");	
	    	jsonHierarchy.append(
	    			  JSONObject.quote( msc.getIdAlias() ) + ": {" +	    			
					  "\"name\":\"" + msc.getName() + "\"," +
					  "\"comp\":\"" + msc.getCompartmentId() + "\"" +
					 "}");
	    	    	
	    	addComa = true;
	    }	    
	    jsonHierarchy.append("}");
	    

	    jsonHierarchy.append(",\"species\":{");
	    addComa = false;

	    /* Ahora se itera por la lista ordenada por ID real de species */
	    for ( String specieId : this.species.keySet() ) {
	    	 
	    	
	    			
		    for (MSpecies ms : this.species.get(specieId)  ) {  
		    	if ( ms.isIncluded() ) continue;
		    	if ( ms instanceof MDegraded    ) continue;
		    	
		    	if ( addComa ) jsonHierarchy.append(",");
		    	jsonHierarchy.append(  JSONObject.quote( ms.getIdAlias() ) + ": {" +
		    						   "\"id\":\"" + ms.getId() + "\"," +
						  			   "\"name\":\"" + ms.getName() + "\"," +
						  			   "\"type\":\"" + ms.getType() + "\"," +
						  			   "\"comp\":\"" + ms.getCompartmentId() + "\"," +
						  			   "\"txt\":" + JSONObject.quote( ms.getNotesText( ) ) + "" +
						 "}\n");
		    		    	
		    	addComa = true;
		    }	 
	    }	    
	    
	    jsonHierarchy.append("}");

	    jsonHierarchy.append(",\"reactions\":{");
	    addComa = false;
	    
	    
//	    List reactionsList = model.getReactionsCollection();
//	    sort( reactionsCollection,  )
	    for (MReaction mr : this.getReactionsCollection()) {    	
	    	
	    	if ( addComa ) jsonHierarchy.append(",");
	    	jsonHierarchy.append( JSONObject.quote( mr.getId() ) + ": {" +		  			   
		  			              "\"type\":\"" + mr.getType() + "\"," +
		  			              "\"txt\":\"" + mr.getSboTermText() + "\"," +
		  			              "\"reactants\":[" );
	    	
	    	boolean arrayComa = false;
	    	for ( MSpeciesLink mspc : mr.getReactants() ) {
	    		if ( arrayComa ) jsonHierarchy.append(",");
	    		jsonHierarchy.append(JSONObject.quote( mspc.getMs().getIdAlias() ) );
	    		arrayComa = true;
	    	}
	    	for ( MSpeciesLink mspc : mr.getReactantLinks() ) {
	    		if ( arrayComa ) jsonHierarchy.append(",");
	    		jsonHierarchy.append(JSONObject.quote( mspc.getMs().getIdAlias() ) );
	    		arrayComa = true;
	    	}
	    	jsonHierarchy.append("],\"products\":[");
	    	
	    	arrayComa = false;
	    	for ( MSpeciesLink mspc : mr.getProducts() ) {
//	    		if ( mspc.getMs().getType().equals("Degraded")  ) continue; 
	    		if ( arrayComa ) jsonHierarchy.append(",");
	    		jsonHierarchy.append(JSONObject.quote( mspc.getMs().getIdAlias() ) );
	    		arrayComa = true;
	    	}
	    	for ( MSpeciesLink mspc : mr.getProductLinks() ) {
	    		if ( arrayComa ) jsonHierarchy.append(",");
	    		jsonHierarchy.append(JSONObject.quote( mspc.getMs().getIdAlias() ) );
	    		arrayComa = true;
	    	}
	    	
	    	jsonHierarchy.append("],\"mods\":[");
	    	arrayComa = false;
	    	for ( MModification mmod : mr.getModifications() ) {
	    		if ( arrayComa ) jsonHierarchy.append(",");
	    		jsonHierarchy.append(JSONObject.quote( mmod.getSpecie().getIdAlias() ) );
	    		arrayComa = true;
	    	}
	    	if ( mr.getBooleanLogicGates() != null ) {
		    	for ( MBooleanLogicGate mbool : mr.getBooleanLogicGates() ) {
		    		if ( arrayComa ) jsonHierarchy.append(",");
			    	for ( MModification mmod : mbool.getModifications() ) {
			    		if ( arrayComa ) jsonHierarchy.append(",");
			    		jsonHierarchy.append(JSONObject.quote( mmod.getSpecie().getIdAlias() ) );
			    		arrayComa = true;
			    	}	    		
		    	}
	    	}
	    	jsonHierarchy.append("]");	    	
	    	
	    	jsonHierarchy.append(
	    	
	    	"}");
	    		    	
	    	addComa = true;
	    }
	    jsonHierarchy.append("}");	    

	    jsonHierarchy.append("}}\n");
	    
		return jsonHierarchy;
	}
	
	/**
	 * Antes de realizar un (nuevo) layout, elimina todos los editpoints
	 * ya que son información exclusivamente de layout
	 * 
	 */
	public void resetEditPoints() {
		for ( MReaction mreac : this.getReactionsCollection() ) {
			mreac.getEditPoints().clear();
			
			for ( MSpeciesLink msl : mreac.getReactants() ) {
				msl.setLinkAnchor( null );
			}
			
			for ( MSpeciesLink msl : mreac.getProducts() ) {
				msl.setLinkAnchor( null );
			}
			
			for ( MAddedSpeciesLink mas : mreac.getReactantLinks() ) {
				mas.getEditPoints().clear();
				mas.setLinkAnchor(null);
			}
			for ( MAddedSpeciesLink mas : mreac.getProductLinks() ) {
				mas.getEditPoints().clear();
				mas.setLinkAnchor(null);
			}
			
			for ( MModification mod : mreac.getModifications() ) {
				mod.getEditPoints().clear();
			}
		}
	}
	
	/**
	 * Averigua si una 'species' es 'clon'. Para el dibujo conforme a SBGN.
	 * 
	 * @param id
	 * @return <code>true</code> si existe mas de una instancia del ID, 
	 *          <code>false</code> en caso contrario
	 */
	public boolean isClone( String id ) {
		Vector<MSpecies> spcs = this.species.get( id );
		if ( spcs == null ) {
			return false;
		} else {
			return spcs.size() > 1;
		}
	}

	public Dimension getDiagramSize() {
		return diagramSize;
	}

	public void setDiagramSize(Dimension diagramSize) {
		this.diagramSize = diagramSize;
	}

	public boolean isTransformEditPoints() {
		return transformEditPoints;
	}

	public void setTransformEditPoints(boolean transformEditPoints) {
		for (MReaction mr : this.reactions.values() ) {
			mr.setTransformEditPoints( transformEditPoints );
		}
		this.transformEditPoints = transformEditPoints;
	}
	
	public MReaction getReactionById( String reactionId) {
		return this.reactions.get( reactionId) ;
	}

	public void addTextLayer(MTextLayer mTextLayer) {
		this.textLayers.add( mTextLayer );
		
	}

	public Vector<MTextLayer> getTextLayers() {
		return textLayers;
	}
}
