package svgview;

import java.awt.Dimension;
import java.util.Date;
import java.util.HashMap;

import model.MCompartment;
import model.layer.MTextLayer;
import model.reaction.MReaction;
import model.specie.MSpeciesComplex;
import model.specie.MSpeciesSimple;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.swing.JSVGCanvas;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import svgcontroller.SBML2SVGException;
import svgcontroller.SVGController;
import svgview.compartments.SVGCompartment;
import svgview.reactions.SVGReaction;
import svgview.reactions.SVGReactionFactory;
import svgview.shapes.SVGTextLayer;
import svgview.species.SVGComplex;
import svgview.species.SVGDegraded;
import svgview.species.SVGSpecie;
import svgview.species.SVGSpecieFactory;
import svgview.util.SVGScripts;
import svgview.util.SVGTextRenderer;
import svgview.util.quadtree.QuadTreeElement;
import svgview.util.quadtree.StaticQuadTree;

/**
 * Clase que soporta la 'vista' del sistema. 
 * Dibuja el documento SVG, y lo pinta sobre el JSVGCanvas
 * 
 * @author guille
 *
 */
public class SVGView  {	
    private static final String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
	private SVGController controller;
	private model.Model model;
	private Document document; 							// The SVG document    
	protected JSVGCanvas svgCanvas;    
    private Dimension diagramSize;
    protected HashMap<String,SVGSpecie> svgSpecies = new HashMap<String,SVGSpecie>();
    protected HashMap<String,SVGReaction> svgReactions = new HashMap<String,SVGReaction>();    
        
    public SVGView(SVGController cnt ) throws SBML2SVGException {
    	this.controller = cnt;
    	this.model = cnt.getModel();
    	this.diagramSize = model.getDiagramSize();
    	
    	Date d0 = new Date();
		buildSVG();
		SVGOutput.printStatistic( "InitTime" , ""+ (new Date().getTime() - d0.getTime()) );
				
		// System.gc();
    }
    
    /**
     * Construye el documento SVG a partir del Model
     * @throws SBML2SVGException 
     */
    private void buildSVG() throws SBML2SVGException {	  
	    StaticQuadTree qtree = null;
    	
    	document = SVGDOMImplementation.getDOMImplementation().
	    				createDocument (svgNS, "svg", null);	  
	    
    	// Initialize TextRenderer
	    SVGTextRenderer.initialize( this.getCanvas() );
    	// Get a reference to the <svg> element
    	document.setXmlVersion("1.1");
    	document.setXmlStandalone(true);
	    Element root = document.getDocumentElement ();
	    root.setAttribute("width", ""+this.diagramSize.getWidth());
	    root.setAttribute("height", ""+this.diagramSize.getHeight());
	    root.setAttribute("viewBox", "0 0 "+ 
	    							 this.diagramSize.getWidth() + " " + 
	    							 this.diagramSize.getHeight() );
	    root.setAttribute("preserveAspectRatio","xMidYMid meet");	    
	    root.setAttribute("font-family", SVGConfig.font );
	    root.setAttribute("version", "1.1");
	    
	    if ( !SVGConfig.omitJavascript ) {
		    root.setAttributeNS (null, "onmouseup", "endDrag();");
		    root.setAttributeNS (null, "onmousemove", "doDrag(evt, 0);");
	    	SVGScripts.svgAdd(root);	    
	    }
	    
	    addTitle(root);
	    //addDefs(root);
	    addBackground(root);
	    addDebugText(root);	    
	    
	    Element wrapper = document.createElementNS (svgNS, "g");
	    wrapper.setAttribute("class", "wrapper");
	    root.appendChild( wrapper );

	    paintCompartments(wrapper);	  
	    
	    paintComplexes(wrapper);	    

	    if ( SVGConfig.saveQuadTree ) {
	    	qtree = new StaticQuadTree(this.diagramSize.width, this.diagramSize.height);
	    }
	    paintSpecies(qtree, wrapper);	    
	    paintReactions(wrapper);		    
	    paintTextLayers(wrapper);
	    

    	if ( SVGConfig.saveQuadTree ) {
		    // DEBUG QuadTree
		    // qtree.doIndex();
		    qtree.saveToFile( SVGConfig.quadtreeFile, false , model.getJSONHierarchy() );
    	}
    	
	    if ( !SVGConfig.omitJavascript ) {
	    	addInfoWindow( wrapper );
	    }
	    
	    // Una vez realizado el dibujado se pueden eliminar las estructuras
	    // auxiliares
	    svgSpecies.clear();
	    svgReactions.clear();
	    
    }

	private void paintTextLayers(Element wrapper) {
		for ( MTextLayer textLayer : model.getTextLayers() ) {
	    	SVGTextLayer svgTextLayer = new SVGTextLayer( this.getDocument(), textLayer);
	    	
	    	// Si el layer se refiere a algun otro elemento, pintar dentro del mismo
	    	if ( textLayer.getReaction() != null ) {
	    		Element reactionElement  = 
	    			document.getElementById( textLayer.getReaction().getId() );
	    		if ( reactionElement != null ) {
	    			svgTextLayer.svgPaint( reactionElement );
	    		}
	    	} else if ( textLayer.getSpecie() != null ) {
	    		Element specieElement  = 
	    			document.getElementById( textLayer.getSpecie().getIdAlias() );
	    		if ( specieElement != null ) {
	    			svgTextLayer.svgPaint( specieElement );
	    		}
	    	} else {
	    	
	    		svgTextLayer.svgPaint( wrapper );
	    	}
	    }
	}

	private void paintReactions(Element wrapper) throws SBML2SVGException {
		for (MReaction mr : model.getReactionsCollection()) {
	    	SVGReaction react = SVGReactionFactory.createSVGReaction(document, mr, svgSpecies, model.isTransformEditPoints() ); 
	    	if (react!=null) react.svgPaint(wrapper);
	    }
	}

	private void paintSpecies(StaticQuadTree qtree, Element wrapper)
			throws SBML2SVGException {
		for (MSpeciesSimple mss : model.getSimpleAliasesCollection()) {  
	    	if ( mss.isIncluded() ) {	    		
	    		// Si pertenece a un complejo, delegar dibujo a su complejo
	    		continue;
	    	}
	    	
	    	SVGSpecie spc = SVGSpecieFactory.createSVGSpecie(document, mss, model);
	    	spc.svgPaint(wrapper);
	    	svgSpecies.put(mss.getIdAlias(), spc);
	    	
	    	if ( SVGConfig.saveQuadTree &&
	    	     !(spc instanceof SVGDegraded) )
		    	qtree.addElement(
		    		new QuadTreeElement(
		    				spc.getBBox().getMinX(), 
		    				spc.getBBox().getMinY(),
		    				spc.getBBox().getWidth(),
		    				spc.getBBox().getHeight(), 
		    				-1, 
		    				mss.getName() + "\n" + 
		    				mss.getId() + "\n" + 
		    				mss.getNotesText() + 
		    				mss.getSboTermText() ) 	    	
		    	);
	    	
	    }
	}

	private void paintComplexes(Element wrapper) throws SBML2SVGException {
		for (MSpeciesComplex msc : model.getComplexAliasesCollection()) {	    	
	    	SVGComplex spc = (SVGComplex) SVGSpecieFactory.createSVGSpecie(document, msc, model);
	    	if ( !msc.isIncluded() ) {
	    		// Especies complejas anidadas delegan el dibujado a su complejo
	    		// Tambien su adicion a las estructuras de controls se realizan desde el
	    		// complejo superior
		    	spc.svgPaint(wrapper);	
		    	
		    	appendComplexToMap( spc, svgSpecies );
	    	} else {

	    	}
	    	
	    }
	}

	private void paintCompartments(Element wrapper) {
		for (MCompartment mcomp : model.getCompartmentsCollection()) {
	    	 if ( mcomp.isDefault() ) continue;
	    	new SVGCompartment(document, mcomp, this.getDimension() ).svgPaint(wrapper); 
	    	
	    }
	}
	 
	/**
	 * Metodo Auxiliar para añadir al mapa de <code>SVGSpecies</code> las especies
	 * complejas y, recursivamente, sus componentes.
	 * 
	 * @param spc Specie Compleja a añadir al mapa
	 * @param svgSpeciesMap
	 */
	private void appendComplexToMap(SVGComplex spc,
									 HashMap<String, SVGSpecie> svgSpeciesMap) {		
		
		svgSpecies.put( spc.getIdAlias() , spc);

		for ( String idAlias : spc.getChildren().keySet() ) {
			SVGSpecie child = spc.getChildren().get( idAlias );

			if ( child instanceof SVGComplex ) {
				appendComplexToMap( (SVGComplex)child, svgSpeciesMap);
			} else {
				svgSpecies.put( child.getIdAlias() , child);
			}
		}
		
	}

	
	private void addTitle(Element root) {
		Element title;
	    title = document.createElementNS (svgNS, "title");
	    title.setTextContent( model.getName() );
	    root.appendChild(title);     
	}
	
	private void addBackground(Element root) {
		Element background;
	    background = document.createElementNS (svgNS, "rect");
	    if (!SVGConfig.omitJavascript) {
	    	background.setAttributeNS (null, "onclick", "deselect();");
	    }
	    background.setAttributeNS (null, "fill", "#ffffff");
	    background.setAttributeNS (null, "stroke", "none");
	    background.setAttributeNS (null, "width", Double.toString(diagramSize.getWidth()));
	    background.setAttributeNS (null, "height", Double.toString(diagramSize.getHeight()));
	    background.setAttributeNS (null, "x", "0");
	    background.setAttributeNS (null, "y", "0");
	    root.appendChild(background);     
	}
	
	private void addDebugText(Element root) {
		Element debugText;
		
		debugText = document.createElementNS (svgNS, "text");
		debugText.setAttributeNS (null, "x", "50");
		debugText.setAttributeNS (null, "y", "" + (diagramSize.getHeight() - 20 ) );
		debugText.setAttributeNS (null, "font-size", "12");
		debugText.setTextContent( this.model.getName() );		
		debugText.setAttributeNS (null, "id", "debugText");
		debugText.setAttributeNS (null, "font-weight", "bold");	    
	    root.appendChild(debugText);     
	}
	
//	private void addDefs(Element root) {
//		Element defs;
//		defs = document.createElementNS (svgNS, "defs");
//		Element filter = document.createElementNS (svgNS, "filter");
//		filter.setAttribute("id", "Gaussian_Blur");
//		Element gauss = document.createElementNS (svgNS, "feGaussianBlur");
//		gauss.setAttribute("in", "SourceGraphic");
//		gauss.setAttribute("stdDeviation", "0.5");
//		filter.appendChild(gauss);
//		defs.appendChild(filter);
//		
//		root.appendChild(defs);
//	}
	
	private void addInfoWindow(Element root) {
	    
		Element infoWindow =  document.createElementNS (svgNS, "g");
		infoWindow.setAttributeNS (null, "id", "_infoWindow_");
		//infoWindow.setAttributeNS (null, "onclick", "setDebugMessage('event');");
		infoWindow.setAttributeNS (null, "onmousedown", "startDrag(evt);");		
		infoWindow.setAttributeNS (null, "onmouseup", "endDrag();");
		infoWindow.setAttributeNS (null, "onmousemove", "doDrag(evt);");
		infoWindow.setAttributeNS (null, "visibility", "visible");
		infoWindow.setAttributeNS (null, "stroke-width", "2");
		infoWindow.setAttributeNS (null, "transform", "translate(100,100)");
		
	    Element infoWindow1;
	    infoWindow1 = document.createElementNS (svgNS, "rect");	      
	    infoWindow1.setAttributeNS (null, "fill", "#888888");
	    infoWindow1.setAttributeNS (null, "fill-opacity", "0.4");
	    infoWindow1.setAttributeNS (null, "stroke", "none");
	    infoWindow1.setAttributeNS (null, "width", "200");
	    infoWindow1.setAttributeNS (null, "height", "200");
	    infoWindow1.setAttributeNS (null, "x", "35");
	    infoWindow1.setAttributeNS (null, "y", "35");
	    infoWindow1.setAttributeNS (null, "rx", "10");
	    infoWindow1.setAttributeNS (null, "ry", "10");
	    infoWindow.appendChild(infoWindow1);
	    infoWindow1 = document.createElementNS (svgNS, "rect");	   	    
	    infoWindow1.setAttributeNS (null, "fill", "#ffffff");
	    infoWindow1.setAttributeNS (null, "fill-opacity", "0.85");
	    infoWindow1.setAttributeNS (null, "stroke", "#5599bb");
	    infoWindow1.setAttributeNS (null, "width", "200");
	    infoWindow1.setAttributeNS (null, "height", "200");
	    infoWindow1.setAttributeNS (null, "x", "25");
	    infoWindow1.setAttributeNS (null, "y", "25");
	    infoWindow1.setAttributeNS (null, "rx", "10");
	    infoWindow1.setAttributeNS (null, "ry", "10");
	    infoWindow.appendChild(infoWindow1);
	    // historyBack button
	    infoWindow1 = document.createElementNS(svgNS, "polygon");
	    infoWindow1.setAttributeNS (null, "stroke", "#5599bb");
	    infoWindow1.setAttributeNS (null, "fill", "#77bbdd");
	    infoWindow1.setAttributeNS (null, "onclick", "goHistoryBack();");
	    int startX = 150, startY = 36;
	    infoWindow1.setAttributeNS(null, "points", "" + (startX+11) + " " + startY + " " +
											    		(startX+6) + " " + startY + " " +
											    		(startX+6) + " " + (startY-2) + " " +
											    		(startX) + " " + (startY+4) + " " +
											    		(startX+6) + " " + (startY+10) + " " +
											    		(startX+6) + " " + (startY+8) + " " +
											    		(startX+11) + " " + (startY+8)	    
	    							);
	    infoWindow.appendChild(infoWindow1);
	    // historyForward button
	    infoWindow1 = document.createElementNS(svgNS, "polygon");
	    infoWindow1.setAttributeNS (null, "stroke", "#5599bb");
	    infoWindow1.setAttributeNS (null, "fill", "#77bbdd");
	    infoWindow1.setAttributeNS (null, "onclick", "goHistoryForward();");
	    startX = 170;
	    infoWindow1.setAttributeNS(null, "points", "" + startX + " " + startY + " " +
											    		(startX+5) + " " + startY + " " +
											    		(startX+5) + " " + (startY-2) + " " +
											    		(startX+11) + " " + (startY+4) + " " +
											    		(startX+5) + " " + (startY+10) + " " +
											    		(startX+5) + " " + (startY+8) + " " +
											    		startX + " " + (startY+8)	    
	    							);
	    infoWindow.appendChild(infoWindow1);	    
	    
	    // Close button
	    infoWindow1 = document.createElementNS (svgNS, "line");	   	    
	    infoWindow1.setAttributeNS (null, "stroke", "#66aacc");
//	    infoWindow1.setAttributeNS (null, "stroke-width", "2");
	    infoWindow1.setAttributeNS (null, "x1", "215");
	    infoWindow1.setAttributeNS (null, "y1", "35");
	    infoWindow1.setAttributeNS (null, "x2", "205");
	    infoWindow1.setAttributeNS (null, "y2", "45");
	    infoWindow.appendChild(infoWindow1);
	    infoWindow1 = document.createElementNS (svgNS, "line");	   	    
	    infoWindow1.setAttributeNS (null, "stroke", "#66aacc");
	    infoWindow1.setAttributeNS (null, "x1", "215");
	    infoWindow1.setAttributeNS (null, "y1", "45");
	    infoWindow1.setAttributeNS (null, "x2", "205");
	    infoWindow1.setAttributeNS (null, "y2", "35");	    
	    infoWindow.appendChild(infoWindow1);
	    infoWindow1 = document.createElementNS (svgNS, "circle");	   	    
	    infoWindow1.setAttributeNS (null, "fill", "white");
	    infoWindow1.setAttributeNS (null, "fill-opacity", "0");
	    infoWindow1.setAttributeNS (null, "stroke", "#66aacc");
	    infoWindow1.setAttributeNS (null, "stroke-width", "2");
//	    infoWindow1.setAttributeNS (null, "width", "10");
//	    infoWindow1.setAttributeNS (null, "height", "10");
	    infoWindow1.setAttributeNS (null, "cx", "210");
	    infoWindow1.setAttributeNS (null, "cy", "40");
	    infoWindow1.setAttributeNS (null, "r", "8");
	    infoWindow1.setAttributeNS (null, "onclick", "ocultar('_infoWindow_');");	    
	    infoWindow.appendChild(infoWindow1);	    
	    
	    Element infoWindowText =  document.createElementNS (svgNS, "g");
	    infoWindowText.setAttributeNS (null, "id", "_infoWindowText_");
	    infoWindowText.setAttributeNS (null, "style", "text-anchor:start;font-weight:bold;;fill:#334466;");
		infoWindow.appendChild(infoWindowText);
		
		Element infoWindowTextContent =  document.createElementNS (svgNS, "text");
		infoWindowTextContent.setAttributeNS (null, "style", "text-anchor: start;");
		infoWindowTextContent.setAttributeNS (null, "x", "110");
		infoWindowTextContent.setAttributeNS (null, "y", "60");
		infoWindowTextContent.setTextContent("...");
		infoWindowText.appendChild(infoWindowTextContent);
		infoWindowTextContent =  document.createElementNS (svgNS, "text");
		infoWindowTextContent.setAttributeNS (null, "style", "text-anchor: start;");
		infoWindowTextContent.setAttributeNS (null, "x", "110");
		infoWindowTextContent.setAttributeNS (null, "y", "75");		
		infoWindowTextContent.setTextContent("...");
		infoWindowText.appendChild(infoWindowTextContent);
		infoWindowTextContent =  document.createElementNS (svgNS, "text");
		infoWindowTextContent.setAttributeNS (null, "style", "text-anchor: start;");
		infoWindowTextContent.setAttributeNS (null, "x", "110");
		infoWindowTextContent.setAttributeNS (null, "y", "90");		
		infoWindowTextContent.setTextContent("...");
		infoWindowText.appendChild(infoWindowTextContent);
		infoWindowTextContent =  document.createElementNS (svgNS, "text");
		infoWindowTextContent.setAttributeNS (null, "style", "text-anchor: end;");
		infoWindowTextContent.setAttributeNS (null, "x", "100");
		infoWindowTextContent.setAttributeNS (null, "y", "60");
		infoWindowTextContent.setTextContent("Name:");
		infoWindowText.appendChild(infoWindowTextContent);
		infoWindowTextContent =  document.createElementNS (svgNS, "text");
		infoWindowTextContent.setAttributeNS (null, "style", "text-anchor: end;");
		infoWindowTextContent.setAttributeNS (null, "x", "100");
		infoWindowTextContent.setAttributeNS (null, "y", "75");		
		infoWindowTextContent.setTextContent("Type:");
		infoWindowText.appendChild(infoWindowTextContent);
		infoWindowTextContent =  document.createElementNS (svgNS, "text");
		infoWindowTextContent.setAttributeNS (null, "style", "text-anchor: end;");
		infoWindowTextContent.setAttributeNS (null, "x", "100");
		infoWindowTextContent.setAttributeNS (null, "y", "90");		
	    infoWindowTextContent.setTextContent("ID:");
	    infoWindowText.appendChild(infoWindowTextContent);		
		
	    root.appendChild(infoWindow);     	    
	    
	}
	
    public Dimension getDimension() {
    	return this.diagramSize;
    }
    
    /**
     * Obtiene un <code>JSVGCanvas</code> de forma perezosa,
     * de manera que solo se construye si realmente es necesario.
     * 
     * @return
     */
    public JSVGCanvas getCanvas() {
    	if ( this.svgCanvas == null ) {        	
    		svgCanvas = new JSVGCanvas();	            	
    	}
    	return this.svgCanvas;
    }
    
    public void drawToCanvas(){
    	// this.getCanvas().setPreferredSize( this.diagramSize);
    	this.getCanvas().setPreferredSize( new Dimension( 800, 600 ) );
    	if ( SVGConfig.omitJavascript ) {
    		this.svgCanvas.setDocumentState( JSVGCanvas.ALWAYS_STATIC );
    	} else {
    		this.svgCanvas.setDocumentState( JSVGCanvas.ALWAYS_DYNAMIC );
    	}
    	this.getCanvas().setDocument (document);	
    	this.getCanvas().repaint();
    }
    
    
    public Document getDocument() {
    	return this.document;
    }

	public SVGController getController() {
		return controller;
	}

	public void setController(SVGController controller) {
		this.controller = controller;
	}
}
