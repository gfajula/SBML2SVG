package model.builder.CD4;

import java.util.Vector;

import model.MCompartment;
import model.MLine;
import model.Model;
import model.builder.ModelBuilder;
import model.layer.MTextLayer;
import model.reaction.MBooleanLogicGate;
import model.reaction.MModification;
import model.reaction.MReaction;
import model.specie.MAddedSpeciesLink;
import model.specie.MInformationUnit;
import model.specie.MSpeciesComplex;
import model.specie.MSpeciesLink;
import model.specie.MSpeciesSimple;

import org.sbml.libsbml.Compartment;

import svgcontroller.SBML2SVGException;
import celldesignerparse_4_0.CellDesignerModel;
import celldesignerparse_4_0.annotation.InformationUnit;
import celldesignerparse_4_0.annotation.listOfCompartmentAliases.CompartmentAlias;
import celldesignerparse_4_0.annotation.listOfLayers.Layer;
import celldesignerparse_4_0.annotation.listOfLayers.LayerSpeciesAlias;
import celldesignerparse_4_0.annotation.listOfSpeciesAliases.SpeciesAlias;
import celldesignerparse_4_0.commondata.SpeciesIdentity;
import celldesignerparse_4_0.compartment.CellDesignerCompartment;
import celldesignerparse_4_0.reaction.CellDesignerReaction;
import celldesignerparse_4_0.reaction.Modification;
import celldesignerparse_4_0.reaction.Product;
import celldesignerparse_4_0.reaction.ProductLink;
import celldesignerparse_4_0.reaction.Reactant;
import celldesignerparse_4_0.reaction.ReactantLink;
import celldesignerparse_4_0.specie.CellDesignerSpecies;

/**
 * Clase que construye un <code>ModelBuilder</code> que
 * interpreta un SBML con anotaciones de CellDesigner 4. 
 * 
 * @author Guillermo Fajula Leal
 *
 */
public class CellDesigner4ModelBuilder  implements ModelBuilder {
	protected CellDesignerModel cdm;
		
	public CellDesigner4ModelBuilder(CellDesignerModel cdm) {
		super();
		this.cdm = cdm;
	}

	/**
	 * Construye el modelo de objetos de un diagrama para un SBML de CellDesigner. 
	 * 
	 * @throws SBML2SVGException
	 */
	public Model buildModel() throws SBML2SVGException {
		
		// Inicializar Model
		Model model = new model.Model();
		
		initializeCD4Compartments(cdm, model);
		
		initializeCD4SpeciesComplex(cdm, model);
		initializeCD4SpeciesSimple(cdm, model);
		
		
		initializeCD4Reactions(cdm, model);
		
		initializeLayers( cdm , model );
		
		model.setDiagramSize( cdm.getAnnotation().getDimension() );
		model.setTransformEditPoints( true );
		
		return model;
	}
	
	private void initializeLayers(CellDesignerModel cdm, Model model) {
		
		if ( cdm.getAnnotation().getListOfLayers() == null ) {
			return;
		}
		
		for ( Layer layer : cdm.getAnnotation().getListOfLayers() ) {
			if ( layer.getListOfTexts() != null) {
				for (LayerSpeciesAlias textLayer : layer.getListOfTexts() ) {
					
					MTextLayer mTextLayer = new MTextLayer();
					mTextLayer.setFontSize( textLayer.getFontSize() );
					mTextLayer.setText( textLayer.getLayerNotes()  );
					mTextLayer.setX( textLayer.getX() );
					mTextLayer.setY( textLayer.getY() );
					mTextLayer.setTextColor( textLayer.getPaint().getColor() );
					mTextLayer.setBounds( textLayer.getBounds() );
					
					if ( textLayer.getTargetType().equalsIgnoreCase("reaction") ) {
						mTextLayer.setReaction( model.getReactionById( textLayer.getTargetId() ));
					} else if ( textLayer.getTargetType().equalsIgnoreCase("species") ) {
						mTextLayer.setSpecie( model.getSpecieByAlias( textLayer.getTargetId() )  );
					}
					
					model.addTextLayer( mTextLayer );
				}
			}	
		}
		
	}

	/**
	 * Inicializar reacciones de un SBML de CellDesigner
	 * 
	 * @param sbmlModel modelo de SBML de CellDesigner
	 */
	private void initializeCD4Reactions(CellDesignerModel cdm, Model model) {
		for (int i=0;i<cdm.getNumReactions();i++){			
			CellDesignerReaction cdr = cdm.getReaction(i);

			Vector<MSpeciesLink> mreactants = 
									getReactantMSpecies(model, cdr);
			Vector<MSpeciesLink> mproducts = 
									getProductMSpecies(model, cdr);
			Vector<MAddedSpeciesLink> mreactantLinks = 
									getReactantLinkMSpecies(model, cdr);
			Vector<MAddedSpeciesLink> mproductLinks = 
									getProductLinkMSpecies(model, cdr);
			Vector<MModification> mmodifications = 
									getProductModifications(model, cdr);
			Vector<MBooleanLogicGate> mbooleanGates = 
									getProductBooleanLogicGates(model, cdr, mmodifications);			
			

			MReaction newReaction = MCD4ReactionFactory.buildReaction(
					cdr, 
					mreactants, mproducts, 
					mreactantLinks, mproductLinks, 
					mmodifications, mbooleanGates );
				
			model.addReaction( newReaction );
		}
	}
	
	/**
	 * Inicializar 'compartments' de un SBML de CellDesigner
	 * 
	 * @param cdm modelo de SBML de CellDesigner
	 */
	private void initializeCD4Compartments(CellDesignerModel cdm, Model model) throws SBML2SVGException {
		Vector<CompartmentAlias> lca = cdm.getAnnotation().getListOfCompartmentAliases();
		for (CompartmentAlias ca : lca){
			CellDesignerCompartment cdCompartment = cdm.getCompartment( ca.getCompartment() );			
			MCompartment mc = new MCompartment(ca.getIdAlias(), ca.getCompartment(), 
					cdm.getCompartment(ca.getCompartment()).getName(), cdCompartment.getOutside() ,
					ca.getClassOf(), ca.getBounds(), ca.getNamePoint(), ca.getLine(), ca.getPaint());
			
			mc.setInfo( buildMInformationUnit( ca.getInfo()) );
			
			model.addCompartment(mc);
			
		}
		
		// Si CellDesigner no ha definido ningún compartimento propio, examinar el compartimento estándar.
		if ( model.getCompartmentsCollection().size() == 0 ) {
			for (int i=0 ; i<cdm.getNumCompartments() ; i++){	
				
				Compartment comp = cdm.getCompartment(i);			
				MCompartment mc = new MCompartment(comp.getId(), comp.getId(), 
												   comp.getName(), comp.getOutside() ,
												   "", null, null, null, null );
				model.addCompartment(mc);
			}
		}
		
		checkCompartmentsHierarchy( cdm , model );
	}
	
	/**
	 * Comprobar la coherencia de la jerarquía de 'compartments' del SBML, y asegurar 
	 * una estructura de árbol para la jerarquía
	 * 
	 * @param sbmlModel  modelo de SBML estándar
	 * @throws SBML2SVGException Excepción con el mensaje del error encontrado
	 */
	private void checkCompartmentsHierarchy(celldesignerparse_4_0.CellDesignerModel sbmlModel, Model model) throws SBML2SVGException {
		// Enlazar compartimentos según su jerarquía
		for ( MCompartment mc : model.getCompartmentsCollection() ) {
				mc.setOutsideCompartment( model.getCompartment( mc.getOutside() ) );
		}
		
		Vector <MCompartment> orphans = new Vector<MCompartment>();
		
		for ( MCompartment comp : model.getCompartmentsCollection() ) {
			if ( comp.hasCycles() ) {
				throw new SBML2SVGException("La jerarquía de 'compartments' hace ciclos");
			}
			if ( comp.getOutsideCompartment() == null ) {
				orphans.add( comp );
			}
		}
		
		// Si no hay nodos, o hay mas de un nodo padre, ligar con un compartment 'default'
	    if ( ( model.getCompartmentsCollection().size() == 0 ) || 
	         ( orphans.size() > 1 ) ) {
	    	String newID = model.getNewId(sbmlModel, "default");
	    	MCompartment rootComp = new MCompartment(
	    			 newID, 
	    			 newID, 
					 "", 
					 null, 
					 "DEFAULT", 
					 null,
					 null, 
					 null, 
					 null	    	
	    		);
	    	rootComp.setDefault(true);
	    	model.setDefaultCompartment( rootComp );
	    	model.getCompartments().put( rootComp.getIdAlias(), rootComp );
	    	for ( MCompartment orphan: orphans) {
	    		orphan.setOutsideCompartment( rootComp );
	    	}
	    	
	    } else if ( orphans.size() == 1 ) {
	    	// Si hay una sola raiz de la jerarquia, llamado default, marcar como "default"
	    	orphans.get(0).setDefault( orphans.get(0).getName().equals("default"));	
	    	model.setDefaultCompartment( orphans.get(0) );
	    } else {
	    	System.err.println("Hay algun ciclo en la jerarquia de comparments");
	    	System.exit(-1);
	    }
	}
	
	/**
	 * Inicializar 'species' complejas de un SBML de CellDesigner
	 * 
	 * @param sbmlModel modelo de SBML de CellDesigner
	 */
	private void initializeCD4SpeciesComplex(CellDesignerModel cdm, Model model) {
		Vector<SpeciesAlias> cal = cdm.getAnnotation().getListOfComplexSpeciesAliases();
		String compartment = model.getDefaultCompartment().getId(); //"default";
		
		boolean included = false; // Es un complejo anidado?
		String complexId = null;   // Si lo es, ID del complejo padre
		
		if ( cal!=null )
		for (SpeciesAlias sa :  cal){			
			included = false;			
			
			//Obtenemos la identidad de un alias
			int homodimer = 1;
			SpeciesIdentity spIdentity = null;
			String notes = null;
			// Obtener specie real, y sacar información de CD de ella
			CellDesignerSpecies specie = cdm.getSpecies(sa.getId());
			if ( specie == null ){
				// Puede no ser encontrada porque la specie, si solo aparece como
				// componente de este complejo, se defina en la lista de 
				// 'listOfIncludedSpecies'				
				
				celldesignerparse_4_0.annotation.listOfIncludedSpecies.Species includedSpecie = 
					cdm.getAnnotation().getSpeciesIncluded(sa.getId());
								
				if ( includedSpecie == null) {
					System.err.println("" + sa.getId() + " no encontrado entre species");
				} else {
					// Es un complejo, anidado en otro complejo.
					spIdentity = includedSpecie.getSpeciesIdentity();					
					included = true;
				}
				
			} else {
				compartment = specie.getCompartment();
				spIdentity = specie.getSpeciesIdentity();
				notes = specie.getNotesContent();
			}
			String name = spIdentity.getValue();
			if ( spIdentity.getState() != null ) {
				homodimer = spIdentity.getState().getHomodimer();
			}
			
			complexId = sa.getComplexSpeciesAlias();
						
			MSpeciesComplex msc = new MSpeciesComplex(sa.getAliasId(), sa.getId(), name, compartment, homodimer,
				sa.getActivity(), sa.getBounds(), sa.getViewState(), sa.getUsualView(), sa.getBriefView(),
				sa.getTags(), notes );
			msc.setIncluded(included);
			msc.setComplexId(complexId);
			msc.setInfo(  buildMInformationUnit(sa.getInfo())  ) ;
			msc.setHypothetical( spIdentity.isHypothetical() );
			model.addComplexAlias(msc);
			
			// Actualizar relacion con MCompartment
			msc.setCompartment( model.getCompartment( msc.getCompartmentId() ) );
		}
		
		// Una vez insertados todos los Complex en el Model, enlazamos los que estén
		// relacionados entre sí
		for ( MSpeciesComplex msc : model.getComplexAliasesCollection() ) {
			if ( msc.getComplexId() != null  && !msc.getComplexId().equals("") ){
				MSpeciesComplex parentComplex = model.getSpeciesComplexByAlias(msc.getComplexId());
				msc.setComplex( parentComplex );
				parentComplex.addSpecieAlias( msc );
			}
		}
		 
	}
	
	
	/**
	 * Inicializar 'species' de un SBML de CellDesigner
	 * 
	 * @param sbmlModel modelo de SBML de CellDesigner
	 */
	private void initializeCD4SpeciesSimple(CellDesignerModel cdm, Model model) {
		Vector<SpeciesAlias> cal = cdm.getAnnotation().getListOfSimpleSpeciesAliases();
		for (SpeciesAlias sa :  cal){
			//Obtenemos la identidad de un alias
			SpeciesIdentity spIdentity = null;
			MSpeciesSimple mss;
			if (cdm.getSpecies(sa.getId())==null){
				spIdentity = cdm.getAnnotation().getSpeciesIncluded(sa.getId()).getSpeciesIdentity();
				mss = MCD4SpecieFactory.buildKindOfSpecie(model, spIdentity, sa, cdm, true);
				// mss = buildKindOfSpecie(model, spIdentity, sa, cdm, true);
			} else {
				spIdentity = cdm.getSpecies(sa.getId()).getSpeciesIdentity();
				mss = MCD4SpecieFactory.buildKindOfSpecie(model, spIdentity, sa, cdm, false);
				// mss = buildKindOfSpecie(model, spIdentity, sa, cdm, false);
			}
			
//			mss.setSboTerm( cdm.getSpecies(sa.getId()).getSBOTermID() )   ;
			model.addSimpleAlias(mss, sa.getComplexSpeciesAlias());
		}
	}
	
	/**
	 * Metodo auxiliar para crear un MInformationUnit a partir de un celldesigner.InformacionUnit
	 */
	private MInformationUnit buildMInformationUnit( InformationUnit info ) {
		if ( info != null )
			return new MInformationUnit( 
					info.getPrefix().equals("free input")?"":info.getPrefix(), 
					info.getState(), 
					info.getAngle(), 
					info.getLabel() );
		else
			return null;
	}
	

	/**
	 * Obtiene las referencias a 'specie' producto de la reaccion que CellDesigner
	 * modela como <code>productLink</code>
	 * 
	 * @param reaction <code>CellDesignerReaction</code>
	 * @return <code>Vector</code> con las <code>MAddedSpeciesLink</code>
	 */
	private Vector<MAddedSpeciesLink> getProductLinkMSpecies(Model model, CellDesignerReaction reaction) {
		Vector<MAddedSpeciesLink> result = new Vector<MAddedSpeciesLink>();
		for (ProductLink p : reaction.getProductLinks()){
			result.addElement(
					new MAddedSpeciesLink( 
							model.getSpecieByAlias(p.getAlias()),
							p.getLinkAnchor(),
							new MLine( p.getLine().getColor(), 
									   p.getLine().getWidth(), 
									   p.getLine().getType() )	) );
		}
		return result;
	}
	
	/**
	 * Obtiene las referencias a 'specie' producto de la reaccion que CellDesigner
	 * modela como <code>baseProduct</code>
	 * 
	 * @param reaction <code>CellDesignerReaction</code>
	 * @return <code>Vector</code> con las <code>MSpeciesLink</code>
	 */
	private Vector<MSpeciesLink> getProductMSpecies(Model model, CellDesignerReaction reaction) {
		Vector<MSpeciesLink> result = new Vector<MSpeciesLink>();
		if ( reaction.getBaseProducts() != null ) 
			for (Product p : reaction.getBaseProducts()){
				if (p.getAlias()==null || p.getAlias().equals("") ) {
					// compatibility mode
					result.addElement(
							new MSpeciesLink( model.getSpecieByName( p.getSpecies() ), p.getLinkAnchor() ) );
				} else {					
					result.addElement(
							new MSpeciesLink( model.getSpecieByAlias(p.getAlias()) ,								
									p.getLinkAnchor() ) );
				}
			}
		return result;
	}

	/**
	 * Obtiene las referencias a 'specie' reactivo de la reaccion que CellDesigner
	 * modela como <code>reactantLink</code>
	 * 
	 * @param reaction <code>CellDesignerReaction</code>
	 * @return <code>Vector</code> con las <code>MAddedSpeciesLink</code>
	 */
	private Vector<MAddedSpeciesLink> getReactantLinkMSpecies(Model model, CellDesignerReaction reaction) {
		Vector<MAddedSpeciesLink> result = new Vector<MAddedSpeciesLink>();
		for (ReactantLink r : reaction.getReactantLinks()){
			result.addElement(
					new MAddedSpeciesLink( 
							model.getSpecieByAlias(	r.getAlias() ), 
							r.getLinkAnchor(),
							new MLine( r.getLine().getColor(), 
									   r.getLine().getWidth(), 
									   r.getLine().getType() )							
					));
		}
		return result;
	}
	
	/**
	 * Obtiene las referencias a 'specie' producto de la reaccion que CellDesigner
	 * modela como <code>baseReactant</code>
	 * 
	 * @param reaction <code>CellDesignerReaction</code>
	 * @return <code>Vector</code> con las <code>MSpeciesLink</code>
	 */
	private Vector<MSpeciesLink> getReactantMSpecies(Model model, CellDesignerReaction reaction) {
		if ( reaction==null ) return null;
		
		Vector<MSpeciesLink> result = new Vector<MSpeciesLink>();
		
		if ( reaction.getBaseReactants() != null)
			for (Reactant r : reaction.getBaseReactants()){
				if (r.getAlias()==null || r.getAlias().equals("") ) {
					// compatibility mode
					result.addElement(
							new MSpeciesLink( model.getSpecieByName( r.getSpecies() ), r.getLinkAnchor() ) );
				} else {					
					result.addElement(
							new MSpeciesLink(
								model.getSpecieByAlias(	r.getAlias() ) ,
								r.getLinkAnchor() ) );
				}
			}
		return result;
	}

	/**
	 * Obtiene las <code>MModification</code> que afectan a la reacción
	 * 
	 * @param reaction
	 * @return <code>Vector</code> con las <code>MModification</code> de la reacción
	 */
	private Vector<MModification> getProductModifications(Model model, CellDesignerReaction reaction ) {
		Vector<MModification> result = new Vector<MModification>();
		if (reaction.getListOfModification()!=null){
			for (Modification m : reaction.getListOfModification()){
				// Añadir solo las modificaciones normales y 
				// las que tengan correctamente alguna especie
				if ( m.getAliases()!= null && !m.getAliases().equals("") ) {
					if ( !m.getType().contains("BOOLEAN_LOGIC_GATE") ) {
						result.addElement(new MModification(
							model.getSpecieByAlias( m.getAliases() ),
							m.getType(),
							m.getEditPoints(),
							m.getLine(),
							m.getLinkAnchor(),
							m.getTargetLineIndex()
							));
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Obtiene los <code>MBooleanLogicGate</code> de la reaccion, a partir de
	 * las <code>MModification</code> de la reaccion que sean de tipo 
	 * <code>BOOLEAN_LOGIC_GATE</code>
	 * 
	 * @param reaction la <code>CellDesignerReaction</code>
	 * @param mmodifications la lista de modificaciones de la reaccion
	 * @return
	 */
	private Vector<MBooleanLogicGate> getProductBooleanLogicGates( Model model, 
																   CellDesignerReaction reaction, 
																   Vector<MModification> mmodifications ) {
		Vector<MBooleanLogicGate> result = new Vector<MBooleanLogicGate>();
		if (reaction.getListOfModification()!=null){
			String[] aliases;
			for (Modification m : reaction.getListOfModification()){
				// Añadir solo las modificaciones de tipo BOOLEAN_LOGIC_GATE_XXX, 
				// y eliminar participantes de Modifications 
				if ( m.getType().contains("BOOLEAN_LOGIC_GATE") ) {
					aliases = m.getAliases().split(",");
					MBooleanLogicGate mblg = 
						new MBooleanLogicGate(
								model.getSpeciesByAliases( aliases ),
								m.getType(),
								m.getModificationType(),
								m.getEditPoints(),
								m.getLine(),
								m.getTargetLineIndex()
							);
					result.addElement( mblg );
					for (int i=0; i<aliases.length; i++) {						
						moveModificationToBooleanLogicGate(
								mblg,
								mmodifications, 
								aliases[i] );
					}					
				}
			}
		}
		return result;
	}
	
	
	/**
	 * Metodo auxiliar que mueve una <code>MBooleanLogicGate</code> de la lista
	 * de <code>MModification</code> a la lista de <code>MBooleanLogicGate</code>
	 * 
	 * @param mblg
	 * @param mmodifications
	 * @param alias
	 */
	private void moveModificationToBooleanLogicGate(MBooleanLogicGate mblg, Vector<MModification> mmodifications , String alias) {
		for ( MModification mod : mmodifications ) {
			if ( mod.getSpecie().getIdAlias().equals( alias ) ) {
				mblg.addModification( mod );
			}
		}
		mmodifications.removeAll( mblg.getModifications() );
		
	}
	
	
}
