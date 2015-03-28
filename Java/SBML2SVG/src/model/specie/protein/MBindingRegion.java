package model.specie.protein;

public class MBindingRegion {
	private double angle;
	private String id;
	private String name;
	private double size;
	
	public MBindingRegion(String id, String name, double angle, double size){
		this.id = id;
		this.name = name;
		this.angle = angle;
		this.size = size;
	}
	
	public double getSize(){
		return size;
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

}
