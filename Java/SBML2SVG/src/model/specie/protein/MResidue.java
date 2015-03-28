package model.specie.protein;

import celldesignerparse_4_0.Constants;

public class MResidue {
	private double angle;
	private String state;
	private String id;
	private String name;
	private String side;
	
	public MResidue(String id, String name, double angle, String side, String state){
		this.id = id;
		this.name = name;
		this.angle = angle;
		this.side = side;
		this.state = state;
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
	
	public String getSide(){
		return side;
	}
	
	public String getName(){
		return name;
	}
	
	public String getId(){
		return id;
	}
	
	public double getAngle(){
		return angle;
	}

	/*public String getSimplifiedState() {
		if (state==null) return "";
		if (state.length()<=0) return "";
		return (""+state.charAt(0)).toUpperCase();
	}*/
}
