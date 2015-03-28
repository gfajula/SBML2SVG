package model.specie;

public class MInformationUnit {
	private String prefix="";
	private String state="empty";
	private double angle=0;
	private String label="";
	
	public MInformationUnit(String prefix, String state, double angle,
			String label) {
		super();
		this.prefix = prefix;
		this.state = state;
		this.angle = angle;
		this.label = label;
	}

	
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public double getAngle() {
		return angle;
	}
	public void setAngle(double angle) {
		this.angle = angle;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * Obtiene representacion como String
	 * @return
	 */
	public String toString(){
		if ( this.getPrefix() != null &&
		     !this.getPrefix().equals("") ) {
			return this.getPrefix() + ":"+this.getLabel();		
		} else {
			return this.getLabel();			
		}

	}
}
