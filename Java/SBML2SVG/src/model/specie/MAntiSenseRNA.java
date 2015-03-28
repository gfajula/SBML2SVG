package model.specie;

import java.awt.geom.Rectangle2D;
import java.util.Vector;

import model.specie.gene.MRegion;
import celldesignerparse_4_0.CellDesignerModel;
import celldesignerparse_4_0.annotation.listOfAntiSenseRNAs.AntiSenseRNA;
import celldesignerparse_4_0.annotation.listOfSpeciesAliases.SpeciesAlias;
import celldesignerparse_4_0.annotation.listOfSpeciesAliases.Tag;
import celldesignerparse_4_0.commondata.SpeciesIdentity;
import celldesignerparse_4_0.commondata.View;
import celldesignerparse_4_0.specie.CellDesignerSpecies;

public class MAntiSenseRNA extends MRNA {

	public MAntiSenseRNA(String idAlias, String id, String name, String compartment, int homodimer,
			String activity, Rectangle2D bounds, String viewState,
			View usualView, View briefView, Vector<Tag> tags, String notes) {
		super(idAlias, id, name, compartment, homodimer, activity, bounds, viewState, usualView,
				briefView, tags, notes);
	}
	
	public MAntiSenseRNA(AntiSenseRNA asr, SpeciesAlias sa, CellDesignerModel cdm ) {		
		this(
		    sa.getAliasId(),
			asr.getId(),
			asr.getName(),
			( cdm.getSpecies(sa.getId()) != null )?
					cdm.getSpecies(sa.getId()).getCompartment() : "default",
			getHomodimer(sa, cdm),
			sa.getActivity(),
			sa.getBounds(),
			sa.getViewState(),
			sa.getUsualView(),
			sa.getBriefView(),
			sa.getTags(),
			( cdm.getSpecies(sa.getId()) != null )?
					cdm.getSpecies(sa.getId()).getNotesContent() :""
		);
		
		
		CellDesignerSpecies spe = cdm.getSpecies(sa.getId());
		if (spe!=null){
			SpeciesIdentity speId = spe.getSpeciesIdentity();
			if (speId!=null){
				this.regions = MRegion.getMRegionsFromRegions(asr.getRegions(), speId);				
			}
		} else {
			this.regions = MRegion.getMRegionsFromRegions(cdm, asr.getRegions(), sa.getId(), asr.getId());			
		}	
	}


	@Override
	public String getType() {
		return "AntisenseRNA";
	}
	
}
