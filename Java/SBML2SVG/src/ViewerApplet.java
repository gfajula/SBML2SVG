import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JApplet;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.EventTarget;


public class ViewerApplet extends JApplet {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 7748124201091592695L;

	private static class MyCanvas extends JSVGCanvas {
        /**
		 * 
		 */
		private static final long serialVersionUID = -5274184314641979079L;

		public BridgeContext getBridgeContext() {
            return bridgeContext;
        }
		
		
    }

    MyCanvas canvas = new MyCanvas();
    
//    protected JSVGCanvas canvas;

    protected Document doc;

    protected Element svg;

    public void init() {
        // Create a new JSVGCanvas.
                
        
        getContentPane().add(canvas);
        URL url = null;
        try {
            // Parse the barChart.svg file into a Document.
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
            
            String svgFile = "Huntington1.svg";
            
            if ( getParameter("svgfile")!=null && !getParameter("svgfile").equals("") ) {
            	svgFile = "svgfiles/" + getParameter("svgfile");
            }
            
            url = new URL(getCodeBase(), svgFile );
            doc = f.createDocument(url.toString());
            
            svg = doc.getDocumentElement();
            
            EventTarget t = (EventTarget)svg; 
            canvas.addMouseListener( 
            		
            		new MouseListener(){

						@Override
						public void mouseClicked(MouseEvent ev) {							
							try {
								// getAppletContext().showDocument(new URL("javascript:greet('" + ev.getX()  + "/" + ev.getY() + "')"));
								AffineTransform at = canvas.getRenderingTransform();
								Point2D pt=null;
								try {
									pt = at.inverseTransform( new Point2D.Double( ev.getX() , ev.getY() ), null );
								} catch (NoninvertibleTransformException e) {									
									e.printStackTrace();
								}
								
								String js = "javascript:svgOnClick(" + pt.getX()  + " , " + pt.getY() + ")";
								System.out.printf( "" + js );
								getAppletContext().showDocument( new URL( js ) );
								
							} catch (MalformedURLException e) {
								e.printStackTrace();
							}
						}

						@Override
						public void mouseEntered(MouseEvent arg0) {
							
						}

						@Override
						public void mouseExited(MouseEvent arg0) {
							
						}

						@Override
						public void mousePressed(MouseEvent arg0) {
							
						}

						@Override
						public void mouseReleased(MouseEvent arg0) {
							
						}
            	
            	
            		}            		
            		            
            );
            
            
        } catch (Exception ex) {
        	System.out.println(ex.getMessage());
        	System.console().printf(url + "/" + ex.getMessage());        	
        }
        
        
    }
    
    public void start() {
        // Display the document.
        canvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
        canvas.setDocument(doc);
        canvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
       
    }
    
    public void destroy() {
        canvas.dispose();
    }
    
    public void stop() {
        // Remove the document.
        canvas.setDocument(null);
    }
    
    public String zoomXXX() {
		Dimension dTotal = new Dimension(
				(int)Math.round( Double.parseDouble( svg.getAttribute("width") ) ),  
				(int)Math.round( Double.parseDouble( svg.getAttribute("height") ) ) );
		Dimension dVista = canvas.getSize();
		double escala = Math.min(dVista.getWidth()/dTotal.getWidth(), dVista.getHeight()/dTotal.getHeight() );
		//System.console().printf( "/" + dVista.getWidth() + "/" + dTotal.getWidth() + "/" + dVista.getHeight() + "/" + dTotal.getHeight() );
		AffineTransform zoom = new AffineTransform();
		zoom.scale(escala, escala);
		canvas.setRenderingTransform(zoom);
		canvas.repaint();
		
		try {
			getAppletContext().showDocument(new URL("javascript:greet(\"\")"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return "" + "/" + dVista.getWidth() + "/" + dTotal.getWidth() + "/" + dVista.getHeight() + "/" + dTotal.getHeight();
    }
}
