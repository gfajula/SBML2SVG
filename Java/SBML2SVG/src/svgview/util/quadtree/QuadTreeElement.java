package svgview.util.quadtree;

import org.json.JSONObject;

public class QuadTreeElement {

	private int x;
	private int y;
	private int width;
	private int height;
	private int id;
	private String text;

	public QuadTreeElement(double x, double y, double width, double height,
			int id, String text) {
		super();
		this.x = (int)Math.round(x);
		this.y = (int)Math.round(y);
		this.width = (int)Math.round(width);
		this.height = (int)Math.round(height);
		this.id = id;
		this.text = text;
	}
	
	public QuadTreeElement(int x, int y, int width, int height,
			int id, String text) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.id = id;
		this.text = text;
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getMaxY() {
		return y + height;
	}
	
	public int getMaxX() {
		return x + width;
	}
	
	public int getCenterX() {
		return x + width/2;
	}
	
	public int getCenterY() {
		return y + height/2;
	}
	
	public void fixId(int id) {
		if (this.id<0) this.id = id;
	}
	
	/**
	 * Método para comprobar la situación de este elemento respecto a un
	 * punto.
	 * Hay 9 posibles respuestas:
	 * 	ABOVE_LEFT = 0;
	 *  ABOVE_RIGHT = 1;
	 *  BELOW_LEFT = 2;
	 *  BELOW_RIGHT = 3;
	 *  ABOVE = 4; ( comparte ABOVE_LEFT y ABOVE_RIGHT )
	 *  BELOW = 5; ( comparte BELOW_LEFT y BELOW_RIGHT )
	 *  LEFT = 6;  ( comparte ABOVE_LEFT y BELOW_LEFT )
	 *  RIGHT = 7; ( comparte ABOVE_RIGHT y BELOW_RIGHT )  
	 *  CONTAINING = 8;
	 * 
	 * Nota: Al dividir el espacio en 4 partes, ABOVE_LEFT incluye al punto (x,y<= x,y),
	 * y las otra 3 zonas, lo excluyen
	 * 
	 * @return entero representando la posicion respecto al punto. @see StaticQuadTree
	 */
	public int compareToPoint(int x, int y) {
		if ( x < this.getX() ) {
			// RIGHT
			if ( y < this.getY() ) {
				return StaticQuadTree.BELOW_RIGHT;
			} else {
				if ( y < this.getMaxY() ) {
					return StaticQuadTree.RIGHT;
				} else {
					return StaticQuadTree.ABOVE_RIGHT; 
				}				
			}
		} else {
			if ( x >= this.getMaxX() ) {
				// LEFT
				if ( y < this.getY() ) {
					return StaticQuadTree.BELOW_LEFT;
				} else {
					if ( y < this.getMaxY() ) {
						return StaticQuadTree.LEFT;
					} else {
						return StaticQuadTree.ABOVE_LEFT; 
					}				
				}
			} else {
				// MIDDLE
				if ( y < this.getY() ) {
					return StaticQuadTree.BELOW;
				} else {
					if ( y < this.getMaxY() ) {
						return StaticQuadTree.CONTAINING;
					} else {
						return StaticQuadTree.ABOVE; 
					}				
				}				
			}
		}		
	}
	
	public boolean contains(int x, int y) {
		return !( x < this.x || y < this.y || y > this.getMaxY() || x > this.getMaxX() ); 
	}
	
	public String toJSON() {
		return "{" + 
				"x0:" + this.getX() + "," +
				"y0:" + this.getY() + "," +
				"x1:" + this.getMaxX() + "," +
				"y1:" + this.getMaxY() + "," +		
		        "id:" +  this.getId() + "," + 
		        "txt:" + JSONObject.quote( this.getText() ) + "" + 
		        "}";
	}
	
	public static void main(String[] args) {
		QuadTreeElement elm = new QuadTreeElement(10, 10, 5, 5, 34, "\"Interestingñ!\t\n");
		System.out.println( elm.compareToPoint(15, 15)    );
		System.out.println( elm.toJSON() );
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getId() {
		return id;
	}
	
	public String toString() {
		return this.toJSON();
	}
}
