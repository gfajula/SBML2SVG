package model.builder.SBML;

import java.util.Vector;

import model.MCompartment;
import model.MLine;
import model.Model;
import model.builder.ModelBuilder;
import model.layout.Layouter;
import model.reaction.MModification;
import model.reaction.MReaction;
import model.specie.MAddedSpeciesLink;
import model.specie.MSpeciesLink;
import model.specie.MSpeciesSimple;

import org.sbml.libsbml.Compartment;
import org.sbml.libsbml.ListOfSpecies;
import org.sbml.libsbml.Reaction;
import org.sbml.libsbml.Species;

import svgcontroller.SBML2SVGException;
import svgview.SVGConfig;
import svgview.SVGOutput;
import celldesignerparse_4_0.commondata.SingleLine;

/**
 * Clase que construye un <code>ModelBuilder</code> que
 * interpreta un SBML standard, sin contemplar anotaciones. 
 * 
 * @author Guillermo Fajula Leal
 *
 */
public class StandardSBMLModelBuilder implements ModelBuilder {
	private org.sbml.libsbml.Model sbmlModel;
	
	public StandardSBMLModelBuilder(org.sbml.libsbml.Model sbmlModel){
		this.sbmlModel = sbmlModel;
	}
	
	@Override
	/**
	 * Construye el modelo de objetos de un diagrama para un SBML 
	 * sin contemplar anotaciones.
	 * 
	 * @throws SBML2SVGException
	 */
	public Model buildModel() throws SBML2SVGException {
		SVGOutput.printStatistic("ModelVersion" , "SBML Standard");
		// Inicializar Model
		Model model = new model.Model();

		model.setName( sbmlModel.getName() );
		
		initializeCompartments(sbmlModel, model);
		checkCompartmentsHierarchy(sbmlModel, model);
		
		initializeSpecies(sbmlModel, model);
		initializeReactions(sbmlModel, model);
		
		
		if ( SVGConfig.layout_type == Layouter.LAYOUT_ORGANIC ) {
			Layouter.doOrganicLayout( model );
		} else if ( SVGConfig.layout_type == Layouter.LAYOUT_ORTHOGONAL ) {
			Layouter.doOrthogonalLayout( model );
		}
		
		return model;
	}
	
	/**
	 * Inicializar 'Compartments' de un SBML estándar
	 * 
	 * @param sbmlModel modelo de SBML estándar
	 */
	private void initializeCompartments(org.sbml.libsbml.Model sbmlModel, Model model) {
		
		for (int i=0 ; i<sbmlModel.getNumCompartments() ; i++){	
			
			Compartment comp = sbmlModel.getCompartment(i);			
			MCompartment mc = new MCompartment(comp.getId(), comp.getId(), 
											   comp.getName(), comp.getOutside() ,
											   "", null, null, null, null );
			model.addCompartment(mc);
		}

	}
	
	/**
	 * Inicializar reacciones de un SBML estándar
	 * 
	 * @param sbmlModel modelo de SBML estándar
	 * @throws SBML2SVGException 
	 */
	private void initializeReactions(org.sbml.libsbml.Model sbmlModel, Model model) throws SBML2SVGException {
		for (int i=0;i<sbmlModel.getNumReactions();i++){	
			Reaction reac = sbmlModel.getReaction(i);
			
			Vector<MSpeciesLink> mreactants = new Vector<MSpeciesLink>();
			Vector<MSpeciesLink> mproducts = new Vector<MSpeciesLink>();
			Vector<MAddedSpeciesLink> mAddedReactants = new Vector<MAddedSpeciesLink>();
			Vector<MAddedSpeciesLink> mAddedProducts = new Vector<MAddedSpeciesLink>();
			Vector<MModification> mModifications = new Vector<MModification>();
			
			
			// Si no hay ningun reactivo, inventar nuevo Degraded
			if ( reac.getListOfReactants().size() == 0 ) {
				mreactants.addElement(
						model.createExtraDegraded( sbmlModel ) 
						);
			} else {
				for (int j=0; j<reac.getListOfReactants().size(); j++) {
					if (j==0) {								
						mreactants.addElement(				
								new MSpeciesLink(
									model.getSpecieByAlias(	reac.getListOfReactants().get(j).getSpecies() ) ,
									null ) 
								);
					} else {
						mAddedReactants.addElement(
								new MAddedSpeciesLink(
										model.getSpecieByAlias(	reac.getListOfReactants().get(j).getSpecies() ) ,
										null,
										new MLine() )					
						);
					}
					
				}
			}
			
			// Si no hay ningun producto, inventar nuevo Degraded
			if ( reac.getListOfProducts().size() == 0 ) {
				mproducts.addElement(
						model.createExtraDegraded( sbmlModel ) 
						);
			} else {
				for (int j=0; j<reac.getListOfProducts().size(); j++) {
					if (j==0) {
						mproducts.addElement(				
								new MSpeciesLink(
									model.getSpecieByAlias(	reac.getProduct(j).getSpecies() ) ,
									null ) 
								);
					} else {
						mAddedProducts.addElement(
								new MAddedSpeciesLink(
										model.getSpecieByAlias( reac.getProduct(j).getSpecies() ) ,
										null,
										new MLine() )				
						);
					}				
				}
			}
			
			for (int j=0; j<reac.getListOfModifiers().size(); j++) {
				String type = SBO.modificationType( reac.getModifier(j).getSBOTermID() );
				MModification newModification = 							
						new MModification(
							// model.getSpecieByAlias(	reac.getProduct(j).getSpecies() ),
						    model.getSpecieByAlias(	reac.getModifier(j).getSpecies() ),
						    type,
							null,
							new SingleLine(),
							"",
							null
							);
				newModification.setSboTerm(reac.getModifier(j).getSBOTermID() );
				mModifications.addElement( newModification );
			}
			
			MReaction newReaction = ReactionFactory.buildReaction(
					reac,
					reac.getId(),
					mproducts,
					mreactants,
					mAddedProducts,		
					mAddedReactants,					
					mModifications,		// No modifications
					reac.getReversible() && reac.isSetReversible()		// No reversible por defecto 
			);
			newReaction.setSboTerm( reac.getSBOTermID() );
			model.addReaction( newReaction );	
			
		}
		
		
		
	}
	

	/**
	 * Inicializar 'species' de un SBML estándar
	 * 
	 * @param sbmlModel modelo de SBML estándar
	 */
	private void initializeSpecies(org.sbml.libsbml.Model sbmlModel, Model model) {
		
		ListOfSpecies los = sbmlModel.getListOfSpecies();
		Species spc; MSpeciesSimple mss;
		for (int i = 0 ; i < los.size() ; i++ ){
			spc = (Species)los.get(i);
			
			mss  = SpecieFactory.buildSpecies(spc, sbmlModel, model);
			model.addSimpleAlias(mss, null);
		}
	}
	



	/**
	 * Comprobar la coherencia de la jerarquía de 'compartments' del SBML, y asegurar 
	 * una estructura de árbol para la jerarquía
	 * 
	 * @param sbmlModel  modelo de SBML estándar
	 * @throws SBML2SVGException Excepción con el mensaje del error encontrado
	 */
	private void checkCompartmentsHierarchy(org.sbml.libsbml.Model sbmlModel, Model model) throws SBML2SVGException {
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





}
