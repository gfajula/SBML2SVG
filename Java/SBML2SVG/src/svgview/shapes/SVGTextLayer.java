package svgview.shapes;

import java.awt.geom.Point2D;
import java.util.Vector;

import model.layer.MTextLayer;

import org.w3c.dom.Document;

import svgview.util.SVGTextRenderer;

public class SVGTextLayer extends SVGComplexShape {
	Document doc;
	MTextLayer mText;
	
		
	public SVGTextLayer(Document doc, MTextLayer mText) {
		super();
		this.mText = mText;
		this.doc = doc;
		
		SVGShape frame = new SVGRectangle( mText.getBounds() );
		frame.setAttribute("stroke", "#999999");
		frame.setAttribute("fill", "#ffffff");
		frame.setAttribute("opacity", "0.5");
		frame.setAttribute("fill-opacity", "0.5");
		this.add( frame );
		
		if ( mText.getX() > 0 && mText.getY() > 0 ) {
			Vector<Point2D> vertices = new Vector<Point2D>();
			
			
			if ( mText.getBounds().getMinY() > mText.getY() ) {
				// Layer por debajo
				vertices.add( new Point2D.Double( mText.getBounds().getMinX(), mText.getBounds().getMinY() ) );
				vertices.add( new Point2D.Double( mText.getX(), mText.getY() ) );
				vertices.add( new Point2D.Double( mText.getBounds().getMaxX(), mText.getBounds().getMinY() ) );
				
			} else if ( mText.getBounds().getMaxY() < mText.getY() ) {
				// Layer por encima
				vertices.add( new Point2D.Double( mText.getBounds().getMinX(), mText.getBounds().getMaxY() ) );
				vertices.add( new Point2D.Double( mText.getX(), mText.getY() ) );
				vertices.add( new Point2D.Double( mText.getBounds().getMaxX(), mText.getBounds().getMaxY() ) );			
			} else if ( mText.getBounds().getMinX() > mText.getX() ) {
				// Layer a la derecha
				vertices.add( new Point2D.Double( mText.getBounds().getMinX(), mText.getBounds().getMinY() ) );
				vertices.add( new Point2D.Double( mText.getX(), mText.getY() ) );
				vertices.add( new Point2D.Double( mText.getBounds().getMinX(), mText.getBounds().getMaxY() ) );
			} else {
				vertices.add( new Point2D.Double( mText.getBounds().getMaxX(), mText.getBounds().getMinY() ) );
				vertices.add( new Point2D.Double( mText.getX(), mText.getY() ) );
				vertices.add( new Point2D.Double( mText.getBounds().getMaxX(), mText.getBounds().getMaxY() ) );
			}
			
			SVGShape triangle = new SVGPolygon(vertices);
			triangle.setAttribute("stroke", "none");
			triangle.setAttribute("fill", "#ffffff");
			triangle.setAttribute("opacity", "0.6");
			triangle.setAttribute("fill-opacity", "0.6");
			this.add( triangle );
			SVGShape triangleTip = new SVGPath(vertices);
			triangleTip.setAttribute("stroke", "#999999");
			triangleTip.setAttribute("opacity", "0.6");
			triangleTip.setAttribute("fill", "none");
			this.add( triangleTip );
		}
		
		SVGText text = new SVGText(
						SVGTextRenderer.
							getInstance().
								drawTextInFrame(
										doc, 
										mText.getBounds(), 
										mText.getText(), 
										(int)Math.round( mText.getFontSize() )
										) );
		this.add( text );
	}
	
	

		
		
	
}
