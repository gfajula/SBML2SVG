package model;

public class MBase {
	private String sboTerm = null;

	public String getSboTerm() {
		return sboTerm;
	}

	public void setSboTerm(String sboTerm) {
		this.sboTerm = sboTerm;
	}
	
	public String getSboTermText() {
		return (sboTerm==null?"":sboTerm+"");
	}
}
