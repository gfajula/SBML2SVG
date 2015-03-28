package celldesignerparse_4_0.reaction;

import java.awt.geom.Point2D;


public class EditPoint {

	private double xProjection;
	private double yProjection;
	
	public EditPoint(String data){
		String[] split = data.split("[,]");
		xProjection = Double.parseDouble(split[0]);
		yProjection = Double.parseDouble(split[1]);
	}
	
	public EditPoint(double xProjection, double yProjection) {
		super();
		this.xProjection = xProjection;
		this.yProjection = yProjection;
	}
	
	
	public double getXProjection(){
		return xProjection;
	}
	
	public double getYProjection(){
		return yProjection;
	}
	
	public Point2D toPoint2D() {
		return new Point2D.Double( xProjection, yProjection );
	}
	
	public void moveTo( Point2D p ) {
		this.xProjection = p.getX();
		this.yProjection = p.getY();
	}
	
	public EditPoint clone() {
		return new EditPoint( this.xProjection, this.yProjection);
	}
	
}
