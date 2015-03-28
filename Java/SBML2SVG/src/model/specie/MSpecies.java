package model.specie;

import java.awt.geom.Rectangle2D;
import java.util.Vector;

import model.MBase;
import model.MCompartment;
import celldesignerparse_4_0.annotation.listOfSpeciesAliases.Tag;
import celldesignerparse_4_0.commondata.Paint;
import celldesignerparse_4_0.commondata.SingleLine;
import celldesignerparse_4_0.commondata.View;

public abstract class MSpecies extends MBase {
	protected String idAlias;
	protected String id;
	protected String name;
	protected String compartmentId;
	protected MCompartment compartment;
	protected int homodimer;
	protected String activity;
	protected Rectangle2D bounds;
	protected String viewState;
	protected View usualView;
	protected View briefView;
	protected String complexId;		// Se mantiene un String con el Id, ya que en el momento de la 
	protected MSpeciesComplex msc;		// creación es posible que no exista aún el MSpeciesComplex
	protected Vector<MTag> tags;
	protected String notes;
	protected boolean included = false;	
	protected boolean hypothetical = false;
	protected MInformationUnit info = null;
	protected double structuralStateAngle = Math.PI / 2;
	
	
	public Vector<MTag> getTags() {
		return tags;
	}

	public void setTags(Vector<MTag> tags) {
		this.tags = tags;
	}

	public MSpecies(String idAlias, String id, String name, String compartment, int homodimer, String activity, Rectangle2D bounds,
			String viewState, View usualView, View briefView, Vector<Tag> tags, String notes){
		this.idAlias = idAlias;
		this.id = id;
		this.name = (name==null || name.equals(""))?idAlias:name;
		this.compartmentId = compartment;
		this.homodimer = homodimer;
		this.activity = activity;
		this.bounds = bounds;
		this.viewState = viewState;		
		this.usualView = usualView;	
		this.briefView = briefView;
		this.msc = null;
		this.tags = getMTags(tags);
		this.notes = notes;
		
	}
	
	public void setComplex(MSpeciesComplex msc) {
		this.msc = msc;
	}
	
	public MSpeciesComplex getComplex() {
		return msc;
	}
	
	public MSpecies(MSpecies clone){
		this.idAlias = clone.getIdAlias();
		this.id = clone.getId();
		this.name = clone.getName();
		this.activity = clone.getActivity();
		this.bounds = clone.getBounds();
		this.viewState = clone.getViewState();
		this.usualView = clone.getUsualView();
		this.briefView = clone.getBriefView();
	}
	
	public View getBriefView(){
		return briefView;
	}
	
	public View getUsualView(){
		// 
		if (this.usualView == null) {
			// Crear usual view por defecto.
			this.usualView = new View(null, null, new SingleLine(), new Paint() ); 
		}
		return usualView;
	}
	
	public String getViewState(){
		return viewState;
	}
	
	public Rectangle2D getBounds(){
		return bounds;
	}
	
	public String getActivity(){
		return activity;
	}
	
	public String getName(){
		return name;
	}
	
	public String getIdAlias(){
		return idAlias;
	}
	
	public String getId(){
		return id;
	}
	
	public int getHomodimer(){
		return homodimer;
	}
	
	// abstract public Shape getShape();
	
	public void setBounds(Rectangle2D bounds){
		this.bounds = bounds;
	}
	
	protected static Vector<MTag> getMTags(Vector<Tag> tags) {
		Vector<MTag> mtags = null;
		if (tags != null) {
			mtags = new Vector<MTag>();
			for (Tag t : tags ) {
				mtags.add( 
				   new MTag( t.getName(), t.getDirection(), t.getBounds(), 
						     t.getLineWidth(), t.getFramePaint() ) 
				   );			
			}
		}
		return mtags;
	}

	public String getNotes() {
		return notes;
	}
	
	public String getNotesText() {
		return (notes==null?"":notes+"");
	}
	
	public MCompartment getCompartment() {
		return compartment;
	}

	public void setCompartment(MCompartment compartment) {
		this.compartment = compartment;
	}

	public String getCompartmentId() {
		return compartmentId;
	}

	public boolean isIncluded() {
		return included;
	}

	public void setIncluded(boolean included) {
		this.included = included;
	}

	public String getComplexId() {
		return complexId;
	}

	public void setComplexId(String complexId) {
		this.complexId = complexId;
	}
	
	public abstract String getType();

	public boolean isHypothetical() {
		return hypothetical;
	}

	public void setHypothetical(boolean hypothetical) {
		this.hypothetical = hypothetical;
	}

	public MInformationUnit getInfo() {
		return info;
	}

	public void setInfo(MInformationUnit info) {
		this.info = info;
	}

	public double getStructuralStateAngle() {
		return structuralStateAngle;
	}

	public void setStructuralStateAngle(double structuralStateAngle) {
		this.structuralStateAngle = structuralStateAngle;
	}
}
