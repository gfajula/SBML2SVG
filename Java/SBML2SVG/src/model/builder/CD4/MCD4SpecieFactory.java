package model.builder.CD4;

import java.util.Vector;

import model.Model;
import model.specie.MAntiSenseRNA;
import model.specie.MDegraded;
import model.specie.MDrug;
import model.specie.MInformationUnit;
import model.specie.MIon;
import model.specie.MPhenotype;
import model.specie.MRNA;
import model.specie.MSimpleMolecule;
import model.specie.MSpeciesSimple;
import model.specie.MUnknown;
import model.specie.gene.MGene;
import model.specie.protein.MBindingRegion;
import model.specie.protein.MIonChannel;
import model.specie.protein.MProtein;
import model.specie.protein.MReceptor;
import model.specie.protein.MResidue;
import model.specie.protein.MTruncated;
import celldesignerparse_4_0.CellDesignerModel;
import celldesignerparse_4_0.Constants;
import celldesignerparse_4_0.annotation.InformationUnit;
import celldesignerparse_4_0.annotation.listOfProteins.BindingRegion;
import celldesignerparse_4_0.annotation.listOfProteins.ModificationResidue;
import celldesignerparse_4_0.annotation.listOfProteins.Protein;
import celldesignerparse_4_0.annotation.listOfSpeciesAliases.SpeciesAlias;
import celldesignerparse_4_0.commondata.Modificator;
import celldesignerparse_4_0.commondata.SpeciesIdentity;
import celldesignerparse_4_0.specie.CellDesignerSpecies;

public class MCD4SpecieFactory {

   
   /**
    * Construir objeto de tipo 'specie' adecuada a la 'specie' del modelo
    * SBML de CellDesigner. Si no se puede generar el tipo de 'specie' en
    * particular, se genera una proteína.
    * 
    * @param si identidad de la 'specie' en el modelo de SBML de CellDesigner
    * @param sa alias de la 'specie'  en el modelo de SBML de CellDesigner
    * @param cdm modelo de SBML de CellDesigner
    * @param included
    * @return 'specie' del tipo requerido
    */
   public static MSpeciesSimple buildKindOfSpecie( Model model, 
                                       SpeciesIdentity si, 
                                       SpeciesAlias sa, 
                                       CellDesignerModel cdm, 
                                       boolean included ) {
      // Con la SpeciesIdentity podemos saber su tipo celldesigner para ser proteina, gen, etc...
      int homodimer = 1;
      
      // Referencia al species
      CellDesignerSpecies spe = cdm.getSpecies(sa.getId());
      // Si es exclusivamente un componente de un complejo, solo aparecera dentro de 
      // 'listOfIncludedSpecies'
      celldesignerparse_4_0.annotation.listOfIncludedSpecies.Species includedSpecie;
      
      String notes = spe!=null?spe.getNotesContent():null;
      String compartment = model.getDefaultCompartment().getIdAlias();
      SpeciesIdentity speId;
      if (spe!=null){
         speId = spe.getSpeciesIdentity();
         if (speId!=null){
            if (speId.getState()!=null){
               homodimer = speId.getState().getHomodimer();
            }
         }
         compartment = spe.getCompartment();
      } else if ( !included ) {
         System.err.println("" + sa.getId() + " no encontrado!!!!!" + sa.getClass() );
      } else {
         // Puede no ser encontrada porque la specie, si solo aparece como
         // componente de este complejo, se defina en la lista de 
         // 'listOfIncludedSpecies'          
         
         includedSpecie = cdm.getAnnotation().getSpeciesIncluded(sa.getId());
                     
         if ( includedSpecie == null) {
            System.err.println("" + sa.getId() + " no encontrado entre species");
         } else {
            // Es un complejo, anidado en otro complejo.
            speId = includedSpecie.getSpeciesIdentity();             
            included = true;
         }
         
         if ( si.getState() != null ) 
            homodimer = si.getState().getHomodimer();       
      }
      
//    homodimer = si.getState().getHomodimer();
      
      MSpeciesSimple newSpecie;
      
      if (si.getClassOf().equalsIgnoreCase(Constants.PROTEIN) ){
         Protein protein = cdm.getAnnotation().getProtein(si.getValue());
         newSpecie = buildKindOfProtein(protein, sa, cdm, si);
      } else if (si.getClassOf().equalsIgnoreCase(Constants.SIMPLE_MOLECULE) ){
         newSpecie =  new MSimpleMolecule(sa.getAliasId(), sa.getId(), si.getValue(), compartment, homodimer, 
               sa.getActivity(), sa.getBounds(), sa.getViewState(), 
               sa.getUsualView(), sa.getBriefView(), sa.getTags(), notes );
      } else if (si.getClassOf().equalsIgnoreCase(Constants.DEGRADED) ){
         newSpecie =  new MDegraded(sa.getAliasId(), sa.getId(), si.getValue(), compartment, homodimer, 
               sa.getActivity(), sa.getBounds(), sa.getViewState(), sa.getUsualView(), 
               sa.getBriefView(), sa.getTags(), notes );
      } else if (si.getClassOf().equalsIgnoreCase(Constants.PHENOTYPE) ){
         newSpecie =  new MPhenotype(sa.getAliasId(), sa.getId(), si.getValue(), compartment, homodimer, 
               sa.getActivity(), sa.getBounds(), sa.getViewState(), sa.getUsualView(), 
               sa.getBriefView(), sa.getTags(), notes );
      } else if (si.getClassOf().equalsIgnoreCase(Constants.GENE) ){
         newSpecie =  new MGene( cdm.getAnnotation().getGene(si.getValue()), sa, cdm );
      } else if (si.getClassOf().equalsIgnoreCase(Constants.ANTISENSE_RNA) ){
         newSpecie =  new MAntiSenseRNA( cdm.getAnnotation().getAntiSenseRNA(si.getValue()), sa, cdm );
      } else if (si.getClassOf().equalsIgnoreCase(Constants.RNA) ){
         
         
         newSpecie =  new MRNA( cdm.getAnnotation().getRNA(si.getValue()), sa, cdm );                 
      } else if (si.getClassOf().equalsIgnoreCase(Constants.ION) ){
         newSpecie =  new MIon(sa.getAliasId(), sa.getId(), si.getValue(), compartment, homodimer,
               sa.getActivity(), sa.getBounds(), sa.getViewState(), sa.getUsualView(), 
               sa.getBriefView(), sa.getTags(), notes );
      } else if (si.getClassOf().equalsIgnoreCase(Constants.DRUG) ){
         newSpecie =  new MDrug(sa.getAliasId(), sa.getId(), si.getValue(), compartment, homodimer,
               sa.getActivity(), sa.getBounds(), sa.getViewState(), sa.getUsualView(), 
               sa.getBriefView(), sa.getTags(), notes );
      } else if (si.getClassOf().equalsIgnoreCase(Constants.UNKNOWN) ){
         newSpecie =  new MUnknown(sa.getAliasId(), sa.getId(), si.getValue(), compartment, homodimer,
               sa.getActivity(), sa.getBounds(), sa.getViewState(), sa.getUsualView(), 
               sa.getBriefView(), sa.getTags(), notes );
      } else {       
         //En caso de que se desconozca el tipo, se crea una proteina
         Protein protein = cdm.getAnnotation().getProtein(si.getValue());
         newSpecie = buildKindOfProtein(protein, sa, cdm, si);
      }
      
      // datos adicionales
      newSpecie.setStructuralStateAngle( sa.getStructuralStateAngle() );
      
      // Actualizar relacion con MCompartment
      if ( model.getCompartment( newSpecie.getCompartmentId() ) == null ) {
         newSpecie.setCompartment( model.getDefaultCompartment() );
      } else {
         newSpecie.setCompartment( model.getCompartment( newSpecie.getCompartmentId() ) );
      }

      newSpecie.setIncluded(included);
      
      if ( spe != null ) {
         newSpecie.setSboTerm( spe.getSBOTermID() );  
      }  
      
      newSpecie.setHypothetical( si.isHypothetical() );
      newSpecie.setInfo( buildMInformationUnit(sa.getInfo()) );
      
      return newSpecie;
   }
   
   /**
    * Construye una proteína normal
    * 
    * @param protein proteina descrita en el modelo SBML de CellDesigner
    * @param sa alias de la 'specie'  en el modelo de SBML de CellDesigner
    * @param cdm modelo de SBML de CellDesigner
    * @return objeto <code>MProtein</code> con la proteína 
    */
   private static MProtein buildKindOfProtein(Protein protein, SpeciesAlias sa, CellDesignerModel cdm, SpeciesIdentity speId ){
      CellDesignerSpecies spe = cdm.getSpecies(sa.getId());
      
      if (protein==null){
         String compartment  ="default";
         if (spe != null) {
            compartment = spe.getCompartment();
         }
         return new MProtein(sa.getAliasId(), sa.getId(), "default", compartment, 1,
               sa.getActivity(), sa.getBounds(), sa.getViewState(), sa.getUsualView(), 
               sa.getBriefView(), null, sa.getTags(), null);
      }
      
      Vector<MResidue> residues = null;
      Vector<MBindingRegion> bindingRegions = null;
      String structuralState = null;
      String compartment = "default";
      
      int homodimer = 1;
      if ( speId.getState() != null ) {
         homodimer = speId.getState().getHomodimer() ;
         structuralState = speId.getState().getStructuralState();
      }
      
      // NOTA: Species que son sólo componentes de un COMPLEX, pueden no tener correspondencia
      // en el model SBML simple. Se deberian haber parseado dentro del "listOfIncludedSpecies"
      /** Comprobando 
      if (spe!=null){
         SpeciesIdentity speId = spe.getSpeciesIdentity();
         if (speId!=null){
            residues = getMResiduesFromModificationResidues(protein.getResidues(), speId);
            
            if (speId.getState()!=null){
               structuralState = speId.getState().getStructuralState();
               homodimer = speId.getState().getHomodimer();
            }
         }
         
         compartment = spe.getCompartment();
      } else {
         residues = getMResiduesFromModificationResidues(cdm, protein.getResidues(), sa.getId(), protein.getId());         
      }
      **/ 
      
      /** Nuevo **/
      residues = getMResiduesFromModificationResidues(protein.getResidues(), speId);
      if (spe!=null){ compartment = spe.getCompartment(); }
      /** Fin Nuevo **/
      
      bindingRegions = getMBindingRegionsFromBindingRegions(protein.getBindingRegions());
      if (protein.getType().equalsIgnoreCase(Constants.PROTEIN_GENERIC) ){
         return new MProtein(sa.getAliasId(), sa.getId(), protein.getName(), compartment, homodimer, 
               sa.getActivity(), sa.getBounds(), sa.getViewState(), sa.getUsualView(), sa.getBriefView(), 
               residues, bindingRegions, structuralState, sa.getTags(), protein.getNotesContent() );
      } else if (protein.getType().equalsIgnoreCase(Constants.PROTEIN_RECEPTOR) ){
         return new MReceptor(sa.getAliasId(), sa.getId(), protein.getName(), compartment, homodimer,
               sa.getActivity(), sa.getBounds(), sa.getViewState(), sa.getUsualView(), sa.getBriefView(), 
               residues, bindingRegions, structuralState, sa.getTags(), protein.getNotesContent());
      } else if (protein.getType().equalsIgnoreCase(Constants.PROTEIN_ION_CHANNEL) ){
         return new MIonChannel(sa.getAliasId(), sa.getId(), protein.getName(), compartment, homodimer,
               sa.getActivity(), sa.getBounds(), sa.getViewState(), sa.getUsualView(), sa.getBriefView(), 
               residues, bindingRegions, structuralState, sa.getTags(), protein.getNotesContent());
      } else if (protein.getType().equalsIgnoreCase(Constants.TRUNCATED) ){
         return new MTruncated(sa.getAliasId(), sa.getId(), protein.getName(), compartment, homodimer,
               sa.getActivity(), sa.getBounds(), sa.getViewState(), sa.getUsualView(), sa.getBriefView(), 
               residues, bindingRegions, structuralState, sa.getTags(), protein.getNotesContent());
      }
      //Si no es de ningun tipo se crea una proteina generica
      return new MProtein(sa.getAliasId(), sa.getId(), protein.getName(), compartment, 1, 
            sa.getActivity(), sa.getBounds(), sa.getViewState(), sa.getUsualView(), 
            sa.getBriefView(), residues, sa.getTags(), protein.getNotesContent());
      
      
   }
   
   /**
    * Metodo auxiliar para crear un MInformationUnit a partir de un celldesigner.InformacionUnit
    */
   private static MInformationUnit buildMInformationUnit( InformationUnit info ) {
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
    * Genera el conjunto de 'Binding Regions' de una proteina
    * 
    * @param bindingRegions BindingRegions en el modelo del SBML de CellDesigner
    * @return <code>Vector</code> con las <code>MBindingRegions</code> generadas 
    */
   private static Vector<MBindingRegion> getMBindingRegionsFromBindingRegions(Vector<BindingRegion> bindingRegions) {
      if (bindingRegions==null) return null;
      Vector<MBindingRegion> result = new Vector<MBindingRegion>();
      
      for (BindingRegion br : bindingRegions){     
         result.addElement(new MBindingRegion(br.getId(),
               br.getName(),
               br.getAngle(),
               br.getSize()));
      }     
      
      return result;
   }

   /**
    * Genera el conjunto de 'Modification Residues' de una proteina
    * 
    * @param v Vector con las 'Modification Residues' en el modelo del SBML de CellDesigner
    * @param si Species Identity de la 
    * @return Vector con las <code>MResidue</code> generadas 
    */
   private static Vector<MResidue> getMResiduesFromModificationResidues(Vector<ModificationResidue> v, SpeciesIdentity si){
      Vector<MResidue> result = new Vector<MResidue>();
      if (v!=null){
         for (ModificationResidue mr : v){
            String state = "";
            if ((si!=null) && (si.getState()!=null)){
               Modificator mod = si.getState().getModificatorByName(mr.getId());
               if (mod!=null){
                  state = mod.getState();
               }
            }
            result.addElement(new MResidue(mr.getId(),
                  mr.getName(),
                  mr.getAngle(),
                  mr.getSide(),
                  state));
                  
         }
      }
      return result;
   }
}
