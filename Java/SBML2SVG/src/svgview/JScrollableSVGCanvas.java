package svgview;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Window;

import javax.swing.Scrollable;

import org.apache.batik.swing.JSVGCanvas;

public class JScrollableSVGCanvas extends JSVGCanvas implements Scrollable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4422882255648289178L;

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return new Dimension(800, 600);
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
		return 10;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
		return 10;
	}
	
    /**
     * this method will be called from constructor of JGVTComponent
     */
    protected Listener createListener() {
        return new ScrollableCanvasSVGListener();
    }
	
    /**
     * This method avoid the problem, that the original methode in 
     * CanvasSVGListener 
     * use pack() methode to rearrange the Swing componentes
     */
	protected class ScrollableCanvasSVGListener extends CanvasSVGListener {
	
	    public void setMySize(Dimension d) {

	        setPreferredSize(d);
	        invalidate();
	        Container p = getParent();
	        while (p != null) {
	            if (p instanceof Window) {
	                Window w = (Window) p;// w.pack();
	                w.validate(); // im Orignial wurde hier pack() aufgerufen
	                break;
	            }
	            p = p.getParent();
	        }
	    }
	
	}
}
