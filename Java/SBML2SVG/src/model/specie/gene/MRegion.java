package model.specie.gene;

import java.util.Vector;

import celldesignerparse_4_0.CellDesignerModel;
import celldesignerparse_4_0.Constants;
import celldesignerparse_4_0.annotation.listOfIncludedSpecies.Species;
import celldesignerparse_4_0.commondata.Modificator;
import celldesignerparse_4_0.commondata.Region;
import celldesignerparse_4_0.commondata.SpeciesIdentity;
import celldesignerparse_4_0.commondata.State;

/**
 * Clase que modela las regiones en secuencias de nucleótidos.
 * Usadas por MGene, MRNA y MAntiSenceRNA
 * 
 * @author Guillermo Fajula Leal
 *
 */
public class MRegion {
	public static final int MODIFICATION_SITE = 0;
	public static final int CODING_REGION = 1;
	public static final int REGULATORY_REGION = 2;
	public static final int TRANSCRIPTION_STARTING_SITE_RIGHT = 3;
	public static final int TRANSCRIPTION_STARTING_SITE_LEFT = 4;
	public static final int PROTEIN_BINDING_DOMAIN = 5;
	
	private String id, name, state;
	private int type;
	private double size, pos;
	private boolean active;
	
	/**
	 * Constructor por defecto, a partir de un objeto Region de CellDesigner 
	 * 
	 * @param region
	 * @param state
	 */
	public MRegion(Region region, String state){
		
		
		this.id = region.getId();
		this.name = region.getName();
		this.type = region.getType();
		this.pos = region.getPos();
		this.size = region.getSize();
		this.state = state;
		
		/*
		CellDesignerSpecie spe = cdm.getSpecies(saId);
		if (spe!=null){
			SpeciesIdentity speId = spe.getSpeciesIdentity();
			if (speId!=null){
				residues = getMResiduesFromModificationResidues(protein.getResidues(), speId);
				
				if (speId.getState()!=null){
					structuralState = speId.getState().getStructuralState();
					homodimer = speId.getState().getHomodimer();
				}
			}
		} else {
			residues = getMResiduesFromModificationResidues(cdm, protein.getResidues(), sa.getId(), protein.getId());			
		}		
		*/
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getType() {
		return type;
	}

	public double getSize() {
		return size;
	}

	public double getPos() {
		return pos;
	}

	public boolean isActive() {
		return active;
	}
	
	public String getState(){
		if ((state==null) || (state.length()<=0))
			return "";
		if (state.compareToIgnoreCase(Constants.RESIDUE_Phosphorylated)==0){
			return "P";
		} else if (state.compareToIgnoreCase(Constants.RESIDUE_Palmytoylated)==0){
			return "Pa";
		} else if (state.compareToIgnoreCase(Constants.RESIDUE_Acetylated)==0){
			return "Ac";
		} else if (state.compareToIgnoreCase(Constants.RESIDUE_Prenylated)==0){
			return "Pr";
		} else if (state.compareToIgnoreCase(Constants.RESIDUE_Ubiqutinated)==0){
			return "Ub";
		} else if (state.compareToIgnoreCase(Constants.RESIDUE_Protonated)==0){
			return "H";
		} else if (state.compareToIgnoreCase(Constants.RESIDUE_Methylated)==0){
			return "Me";
		} else if (state.compareToIgnoreCase(Constants.RESIDUE_Sulfated)==0){
			return "S";
		} else if (state.compareToIgnoreCase(Constants.RESIDUE_Hydroxylated)==0){
			return "OH";
		} else if (state.compareToIgnoreCase(Constants.RESIDUE_empty)==0){
			return "";
		} else if (state.compareToIgnoreCase(Constants.RESIDUE_Glycosylated)==0){
			return "G";
		} else if (state.compareToIgnoreCase(Constants.RESIDUE_DontCare)==0){
			return "*";
		} else if (state.compareToIgnoreCase(Constants.RESIDUE_Myristoylated)==0){
			return "My";
		} else if (state.compareToIgnoreCase(Constants.RESIDUE_Unknown)==0){
			return "?";
		}
		return "Error";
	}
	
	//En el caso del resto de proteinas
	public static Vector<MRegion> getMRegionsFromRegions(Vector<Region> v, SpeciesIdentity si){
		Vector<MRegion> result = new Vector<MRegion>();
		if (v!=null){
			for (Region reg : v){
				String state = "";
				if ((si!=null) && (si.getState()!=null)){
					Modificator mod = si.getState().getModificatorByName(reg.getId());
					if (mod!=null){
						state = mod.getState();
					}
				}
				result.addElement(new MRegion(reg, state));						
			}
		}
		return result;
	}
	
	//En el caso de proteinas que esten dentro de complejos
	public static Vector<MRegion> getMRegionsFromRegions(
						CellDesignerModel cdm, 
						Vector<Region> regions, 
						String idSpecie, String idOwner) {
		if (regions==null)
			return null;
		Vector<MRegion> result = new Vector<MRegion>();
		for (Region mr : regions){
			String state = "";
			for (Species s : cdm.getAnnotation().getListOfIncludedSpecies()){
				if (s.getId().compareToIgnoreCase(idSpecie)==0){
					SpeciesIdentity si = s.getSpeciesIdentity();
					
					if ( (si.getClassOf().compareToIgnoreCase("GENE")==0) ||
						 (si.getClassOf().compareToIgnoreCase("RNA")==0) ||
						 (si.getClassOf().compareToIgnoreCase("ANTISENSE_RNA")==0)
					   ) {
						if (si.getValue().compareToIgnoreCase(idOwner)==0){
							State st = si.getState();
							if (st!=null){
								Modificator mbn = st.getModificatorByName(mr.getId());
								if (mbn!=null){
									state = mbn.getState();
								}
							}
						}
					}
				}
			}
			result.addElement(new MRegion(mr, state));
		}
		return result;
	}

}
