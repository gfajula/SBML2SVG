package svgview.util;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFrame;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.swing.JSVGCanvas;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import svgview.SVGConfig;



/**
 * @author guille
 *
 */
public final class SVGTextRenderer {
	public static SVGTextRenderer singleton = null;
	
	public static final int LINE_HALF = 4;
	public static final int LINE_HEIGHT = 17; // Font size + 5 ?
	public static final double SUB_OFFSET_FACTOR = 3.0;	// pa dividir la altura
	private static final String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
	
	private final JSVGCanvas canvas;
	
	private SVGTextRenderer(JSVGCanvas g) {
		super();
		this.canvas = g;
	}
	
	public static SVGTextRenderer getInstance() {
		return singleton;
	}
	
	public static SVGTextRenderer initialize(JSVGCanvas g) {
		if (singleton == null) {
			singleton = new SVGTextRenderer(g);
		}		
		return singleton;
	}
	
	private static Map<String, String> specialCharacters;
	
	static {
		specialCharacters = new HashMap<String,String>(); 
		
		specialCharacters.put("_plus" , "+");		
		specialCharacters.put("_minus" , "-");
		specialCharacters.put("_slash" , "/");
		specialCharacters.put("_underscore" , "_");
		specialCharacters.put("_space" , " ");
		specialCharacters.put("_Alpha" , "\u0391");
		specialCharacters.put("_Beta" , "\u0392");
		specialCharacters.put("_Gamma" , "\u0393");
		specialCharacters.put("_Delta" , "\u0394");
		specialCharacters.put("_Epsilon" , "\u0395");
		specialCharacters.put("_Zeta" , "\u0396");
		specialCharacters.put("_Eta" , "\u0397");
		specialCharacters.put("_Theta" , "\u0398");
		specialCharacters.put("_Iota" , "\u0399");
		specialCharacters.put("_Kappa" , "\u039a");
		specialCharacters.put("_Lambda" , "\u039b");
		specialCharacters.put("_Mu" , "\u039c");
		specialCharacters.put("_Nu" , "\u039d");
		specialCharacters.put("_Xi" , "\u039e");
		specialCharacters.put("_Omicron" , "\u039f");
		specialCharacters.put("_Pi" , "\u03a0");
		specialCharacters.put("_Rho" , "\u03a1");
		specialCharacters.put("_Sigma" , "\u03a3");
		specialCharacters.put("_Tau" , "\u03a4");
		specialCharacters.put("_Upsilon" , "\u03a5");
		specialCharacters.put("_Phi" , "\u03a6");
		specialCharacters.put("_Chi" , "\u03a7");
		specialCharacters.put("_Psi" , "\u03a8");
		specialCharacters.put("_Omega" , "\u03a9");
		specialCharacters.put("_alpha" , "\u03b1");
		specialCharacters.put("_beta" , "\u03b2");
		specialCharacters.put("_gamma" , "\u03b3");
		specialCharacters.put("_delta" , "\u03b4");
		specialCharacters.put("_epsilon" , "\u03b5");
		specialCharacters.put("_zeta" , "\u03b6");
		specialCharacters.put("_eta" , "\u03b7");
		specialCharacters.put("_theta" , "\u03b8");
		specialCharacters.put("_iota" , "\u03b9");
		specialCharacters.put("_kappa" , "\u03ba");
		specialCharacters.put("_lambda" , "\u03bb");
		specialCharacters.put("_mu" , "\u03bc");
		specialCharacters.put("_nu" , "\u03bd");
		specialCharacters.put("_xi" , "\u03be");
		specialCharacters.put("_omicron" , "\u03bf");
		specialCharacters.put("_pi" , "\u03c0");
		specialCharacters.put("_rho" , "\u03c1");
		specialCharacters.put("_sigma" , "\u03c3");
		specialCharacters.put("_tau" , "\u03c4");
		specialCharacters.put("_upsilon" , "\u03c5");
		specialCharacters.put("_phi" , "\u03c6");
		specialCharacters.put("_chi" , "\u03c7");
		specialCharacters.put("_psi" , "\u03c8");
		specialCharacters.put("_omega" , "\u03c9");
		
	}
	
//	private static String renderSpecialCharacters(String text) {
//		StringBuffer result = new StringBuffer();
//		int i=0;
//		int j, k;
//		while (i<text.length()) {
//			j = text.indexOf("_", i );
//			
//			if (j<0) {
//			   result.append( text.substring(i) );
//			   break;
//			} 
//			
//			k = text.indexOf("_", j+1);			
//			if (k<0) {
//				k=text.length();
//			}
//			
//			String clave = text.substring(j,k);
//			String cambio = specialCharacters.get(clave);
//			if (cambio!=null) {
//				result.append(text.substring(i,j));
//				result.append(cambio);
//				i=k+1;
//				continue;
//			} 
//			
//			result.append(text.substring(i,k));
//			i = k;
//			
//			
//		}
//		
//		return new String(result);
//	}
	
	protected SVGParagraph makeSVGParagraphSplitText(StringBuffer sbText, Font font, long maxWidth) {		
		SVGParagraph paragraph = new SVGParagraph();	
		
		FontMetrics metrics = this.canvas.getFontMetrics(font);

		int beginning = 0;
		int i = 0;

		SVGTextLine line;
		TSpan span;
		while (i<sbText.length()) {			
			if ( sbText.charAt(i) == '\n') {
				// Nueva linea ( si es necesario )
				if ( !sbText.substring(0, i ).trim().equals("") ){
					line = new SVGTextLine();
					span = new TSpan( metrics );
					span.add( sbText.substring(beginning, i ) );
					line.add( span );
					paragraph.add( line);
				}
				
				i = i+1;
				beginning = i;				
			} else {
				// Comprobar longitud
				if ( metrics.stringWidth( sbText.substring(beginning, i+1) ) > maxWidth ) {
					// Buscar por donde partir
					int pos = splitLineToWidth( sbText.substring(beginning, i ), maxWidth, metrics );
					if ( pos > 0) {
						// Partir
						line = new SVGTextLine();
						span = new TSpan( metrics );
						span.add( sbText.substring(beginning, beginning+pos ) );
						line.add( span );
						paragraph.add( line);
						i = beginning+pos+1;
						beginning = i;
					} else {
						// Partir forzadamente
						pos = forceSplitLineToWidth(sbText.substring(beginning, i ), maxWidth, metrics );
						line = new SVGTextLine();
						span = new TSpan( metrics );
						span.add( sbText.substring(beginning, beginning+pos ) );
						line.add( span );
						paragraph.add( line);
						i = beginning+pos;
						beginning = i;
					}
				} else {
					// longitud esta aun dentro del marco
					i++;
				}
			}
		}					
		return paragraph;
	}
		
	protected SVGParagraph makeSVGParagraph(StringBuffer sbText, Font font) {		
		SVGParagraph paragraph = new SVGParagraph();		
		
		FontMetrics metrics = this.canvas.getFontMetrics(font);
		
		int i = 0;
		int j, k;
 
		SVGTextLine line = new SVGTextLine();
		TSpan span = new TSpan(metrics);
		paragraph.add(line);
		line.add(span);
		int maxLineWidth = 0;
		while (i<sbText.length()) {			
			// Comprobar caracter especial.
			j = sbText.indexOf("_", i );			
			if (j<0) {
			   span.add( sbText.substring(i) );
			   break;
			} 
			
			k = sbText.indexOf("_", j+1);			
			if (k<0) {
				k = sbText.length();
			} 
			
			// Hay un caracter especial
			// Añadir texto hasta '_'
			span.add(sbText.substring(i,j));
			
			String clave = sbText.substring(j,k);
			if (clave.equals("_br")) {
				// nueva línea
				maxLineWidth = Math.max(maxLineWidth, line.getWidth() );
				line = new SVGTextLine();
				span = new TSpan(span); // nuevo span con mismos flags.				
				line.add(span);
				paragraph.add(line);
				i=k+1;
				continue;
			} else if(clave.equals("_sub")) {
				// nuevo span, conmutar estado de subscript (como hace version actual de CellDesigner)
				span = new TSpan(span);
				span.subscript = !span.subscript;				
				line.add(span);				
				i=k+1;
				continue;				
			} else if(clave.equals("_endsub")) {
				// nuevo span, conmutar estado de subscript (como hace version actual de CellDesigner)
				span = new TSpan(span);
				span.subscript = !span.subscript;				
				line.add(span);				
				i=k+1;
				continue;						
			} else if(clave.equals("_super")) {
				// nuevo span, conmutar estado de superscript 
				span = new TSpan(span);
				span.superscript = !span.superscript;				
				line.add(span);				
				i=k+1;
				continue;										
			} else if(clave.equals("_endsuper")) {
				span = new TSpan(span);
				span.superscript = !span.superscript;				
				line.add(span);				
				i=k+1;
				continue;				
			} 
			
			String cambio = specialCharacters.get(clave);
			if (cambio!=null) {
//				span.text.append(text.substring(i,j));
				span.add(cambio);
				i=k+1;
				continue;
			}
			
			span.add(sbText.substring(j,k));
			i = k;			
		}	
				
//		// Corregir spans vacios
//		for( SVGTextLine l : paragraph.lines ) {
//			if ( l.spans.size() == 1 ) {
//				if ( l.spans.get(0).text.toString().equals("") ) {
//					l.spans.clear();
//				}
//			}			
//		}
		
		return paragraph;
	}



/** */
private int splitLineToWidth(String line, long maxWidth, FontMetrics metrics) {
	int i = line.lastIndexOf(" ");
	
	while ( i>0 ) {
		if ( metrics.stringWidth( line.substring(0,i) ) < maxWidth ) {
			return i;
		}
		i = line.substring(0,i).lastIndexOf(" ");
	}
	
	return -1;
}

/** */
private int forceSplitLineToWidth(String line, long maxWidth, FontMetrics metrics) {
	int i = line.length()-1;
	
	while ( i>0 ) {
		if ( metrics.stringWidth( line.substring(0,i) ) < maxWidth ) {
			return i;
		}
		i--;
	}
	
	return -1;
}


	public Element drawTextCenteredWithinFrame(Document doc, 
			                                   double cx, double cy,
			                                   Rectangle2D frame, 
			                                   String text, int fontSize) {
		if (text.equals("")) return null;
		
		Font font = new Font ( SVGConfig.font , Font.TRUETYPE_FONT, fontSize);
		FontMetrics metrics = this.canvas.getFontMetrics(font);
		
		// StringBuffer sbText = new StringBuffer(text);
				
		double maxWidth = metrics.stringWidth(text);
		double lineAscent = metrics.getAscent();
		// double lineDescent = metrics.getDescent();
		double lineHeight = metrics.getHeight();
		
		//System.out.println("maxLineWidth = " + maxLineWidth);

		Element textElement = doc.createElementNS(svgNS, "g");
		textElement.setAttributeNS (null, "style", "stroke-linejoin:bevel; stroke:none; font-size:" + fontSize + "px; text-anchor: start;");
		
		double startY = cy + (lineHeight)/2.0 ;
		double startX = cx - maxWidth/2 -1;
		
		// Establecer dentro de los límites del marco
		// Si la anchura es de todas maneras mayor que el marco, no
		// se intenta recolocar
		if (maxWidth < frame.getWidth()) {
			if ( startX < frame.getMinX() ) {
				startX = frame.getMinX();
			}
			
			if ( startX+maxWidth > frame.getMaxX() ) {
				startX = frame.getMaxX() - maxWidth - 1;
			}
		}
		
		if ( startY < frame.getMinY() ) {
			startY = frame.getMinY() + lineHeight;
		}
		
		if ( startY + lineAscent > frame.getMaxY() ) {
			startY = frame.getMaxY() ;
		}		
			    				
		Element tspan = doc.createElementNS(svgNS, "text");
		tspan.setAttributeNS (null, "x", Double.toString(startX));		
		tspan.setAttributeNS (null, "y", Double.toString(startY));
				
		tspan.setTextContent( text );
		textElement.appendChild(tspan);	

		return textElement;
	}
	
	public Element drawTextCentered(Document doc, double cx, double cy, String text, int fontSize) {
		if (text.equals("")) return null;
		
		Font font = new Font ( SVGConfig.font, Font.TRUETYPE_FONT, fontSize);
		FontMetrics metrics = this.canvas.getFontMetrics(font);
		
		StringBuffer sbText = new StringBuffer(text);

		SVGParagraph paragraph = makeSVGParagraph(sbText, font);
				
		double lineAscent = metrics.getAscent();
		double lineDescent = metrics.getDescent();
		double lineHeight = metrics.getHeight();
		double subscriptOffset = lineHeight / SUB_OFFSET_FACTOR;
		
		//System.out.println("maxLineWidth = " + maxLineWidth);

		Element textElement = doc.createElementNS(svgNS, "g");
		textElement.setAttributeNS (null, "style", "stroke-linejoin:bevel; stroke:none; font-size:" + fontSize + "px; text-anchor: start;");
		
		double startY = lineHeight/2 + (lineAscent - lineDescent)/2.0 +  
						cy - paragraph.getHeight()/2.0 ;
		double startX = cx ;
		
		// Traducir a elementos SVG
//		boolean newLine = true;
		for (SVGTextLine l : paragraph.lines) {
//			System.out.print();
			startX = cx - l.getWidth()/2.0;
//			startX = cx;
			if (l.hasSuperscript()) {
				startY += subscriptOffset;
			}
				
			for(TSpan s : l.spans) {
				if (s.text.toString().equals("")) continue;
				
				Element tspan = doc.createElementNS(svgNS, "text");

				tspan.setAttributeNS (null, "x", Double.toString(startX));
				if (s.superscript) {
					tspan.setAttributeNS (null, "y", Double.toString(startY-subscriptOffset));
				} else if (s.subscript) {
					tspan.setAttributeNS (null, "y", Double.toString(startY+subscriptOffset));
				} else {
					tspan.setAttributeNS (null, "y", Double.toString(startY));
				}
				
				tspan.setTextContent(s.text.toString());
				textElement.appendChild(tspan);
				
				startX += s.width; // Divided by 2 because we are centering
		
			}
			
			startY += l.getHeightToNextLine();  //l.getHeight();	
			
//			System.out.println("+++");
		}

		return textElement;
	}	
	
	public Element drawTextAboveLeft(Document doc, double cx, double cy, String text, int fontSize) {
		if (text.equals("")) return null;
		
//		Graphics g = new Graphics();
		Font font = new Font ( SVGConfig.font, Font.TRUETYPE_FONT, fontSize);
		FontMetrics metrics = this.canvas.getFontMetrics(font);
		
		StringBuffer sbText = new StringBuffer(text);

		SVGParagraph paragraph = makeSVGParagraph(sbText, font);
		
		double lineAscent = metrics.getAscent();		
		double lineHeight = metrics.getHeight();
		double subscriptOffset = lineHeight / SUB_OFFSET_FACTOR;
		
		//System.out.println("maxLineWidth = " + maxLineWidth);

		Element textElement = doc.createElementNS(svgNS, "g");
		textElement.setAttributeNS (null, "style", "stroke-linejoin:bevel; stroke:none; font-size:" + fontSize + "px; text-anchor: start;");
		
//		double startY = lineHeight/2 + (lineAscent - lineDescent)/2.0 +  
//						cy - paragraph.getHeight()/2.0 ;
		double startY = cy - paragraph.getHeight() + lineAscent;
		double startX;
		
		// Traducir a elementos SVG
//		boolean newLine = true;
		for (SVGTextLine l : paragraph.lines) {
//			System.out.print();
			startX = cx;
			if (l.hasSuperscript()) {
				startY += subscriptOffset;
			}
				
			for(TSpan s : l.spans) {
				if (s.text.toString().equals("")) continue;
				
				Element tspan = doc.createElementNS(svgNS, "text");

				tspan.setAttributeNS (null, "x", Double.toString(startX));
				if (s.superscript) {
					tspan.setAttributeNS (null, "y", Double.toString(startY-subscriptOffset));
				} else if (s.subscript) {
					tspan.setAttributeNS (null, "y", Double.toString(startY+subscriptOffset));
				} else {
					tspan.setAttributeNS (null, "y", Double.toString(startY));
				}
				
				tspan.setTextContent(s.text.toString());
				textElement.appendChild(tspan);
				
				startX += s.width;
		
			}
			
			startY += l.getHeightToNextLine();  //l.getHeight();	
			
//			System.out.println("+++");
		}
		
		return textElement;
	}	
	
	public Element drawTextAboveCentered(Document doc, double cx, double cy, String text, int fontSize) {
		if (text.equals("")) return null;
		
//		Graphics g = new Graphics();
		Font font = new Font ( SVGConfig.font, Font.TRUETYPE_FONT, fontSize);
		FontMetrics metrics = this.canvas.getFontMetrics(font);
		
		StringBuffer sbText = new StringBuffer(text);

		SVGParagraph paragraph = makeSVGParagraph(sbText, font);
				
		double maxLineWidth = paragraph.getMaxWidth();
		double lineAscent = metrics.getAscent();
		// double lineDescent = metrics.getDescent();
		double lineHeight = metrics.getHeight();
		double subscriptOffset = lineHeight / SUB_OFFSET_FACTOR;
		
		Element textElement = doc.createElementNS(svgNS, "g");
		textElement.setAttributeNS (null, "style", "stroke-linejoin:bevel; stroke:none; font-size:" + fontSize + "px; text-anchor: start;");
		
//		double startY = lineHeight/2 + (lineAscent - lineDescent)/2.0 +  
//						cy - paragraph.getHeight()/2.0 ;
		double startY = cy - paragraph.getHeight() + lineAscent;
		double startX;
		
		// Traducir a elementos SVG
//		boolean newLine = true;
		for (SVGTextLine l : paragraph.lines) {
//			System.out.print();
			startX = cx - maxLineWidth/2.0;
			if (l.hasSuperscript()) {
				startY += subscriptOffset;
			}
				
			for(TSpan s : l.spans) {
				if (s.text.toString().equals("")) continue;
				
				Element tspan = doc.createElementNS(svgNS, "text");

				tspan.setAttributeNS (null, "x", Double.toString(startX));
				if (s.superscript) {
					tspan.setAttributeNS (null, "y", Double.toString(startY-subscriptOffset));
				} else if (s.subscript) {
					tspan.setAttributeNS (null, "y", Double.toString(startY+subscriptOffset));
				} else {
					tspan.setAttributeNS (null, "y", Double.toString(startY));
				}
				
				tspan.setTextContent(s.text.toString());
				textElement.appendChild(tspan);
				
				startX += s.width;
		
			}
			
			startY += l.getHeightToNextLine();  //l.getHeight();	
			
//			System.out.println("+++");
		}

//		Element textBound = doc.createElementNS (svgNS, "rect");
//		 	       
//		textBound.setAttributeNS (null, "fill", "yellow");
//		textBound.setAttributeNS (null, "fill-opacity", "0.25");
//		textBound.setAttributeNS (null, "stroke", "none");
//		
//		textBound.setAttributeNS (null, "width", Double.toString(maxLineWidth));
//		textBound.setAttributeNS (null, "height", Double.toString(paragraph.getHeight()));
//		textBound.setAttributeNS (null, "x", Double.toString(cx - maxLineWidth/2));
//		textBound.setAttributeNS (null, "y", Double.toString(cy - paragraph.getHeight() ));
//		
//		textElement.appendChild (textBound);
//		
//		textBound = doc.createElementNS (svgNS, "rect");
//	       
//		textBound.setAttributeNS (null, "fill", "blue");
//		textBound.setAttributeNS (null, "fill-opacity", "0.25");
//		textBound.setAttributeNS (null, "stroke", "none");
//		
//		textBound.setAttributeNS (null, "width", Double.toString(maxLineWidth));
//		textBound.setAttributeNS (null, "height", Double.toString(lineDescent));
//		textBound.setAttributeNS (null, "x", Double.toString(cx - maxLineWidth/2));
//		textBound.setAttributeNS (null, "y", Double.toString(cy - lineDescent ));
//		
//		textElement.appendChild (textBound);
		
		return textElement;
	}	

	
	
	// TODO: Indicar anchura máxima, para partir palabras
	public Element drawTextBelow(Document doc, double cx, double cy, String text, int fontSize) {
		if (text.equals("")) return null;
		
//		Graphics g = new Graphics();
		Font font = new Font (SVGConfig.font, Font.TRUETYPE_FONT, fontSize);
		FontMetrics metrics = this.canvas.getFontMetrics(font);
		
		StringBuffer sbText = new StringBuffer(text);

		SVGParagraph paragraph = makeSVGParagraph(sbText, font);
				
//		double maxLineWidth = paragraph.getMaxWidth();
//		double lineAscent = metrics.getAscent();
//		double lineDescent = metrics.getDescent();
		double lineHeight = metrics.getHeight();
		double subscriptOffset = lineHeight / SUB_OFFSET_FACTOR;
		
		//System.out.println("maxLineWidth = " + maxLineWidth);

		Element textElement = doc.createElementNS(svgNS, "g");
		textElement.setAttributeNS (null, "style", "stroke-linejoin:bevel; stroke:none; font-size:" + fontSize + "px; text-anchor: start;");
		
//		double startY = lineHeight/2 + (lineAscent - lineDescent)/2.0 +  
//						cy - paragraph.getHeight()/2.0 ;
		double startY = cy + paragraph.getHeight() ;
		double startX = cx;
		
		// Traducir a elementos SVG
//		boolean newLine = true;
		for (SVGTextLine l : paragraph.lines) {
			startX = cx;
			if (l.hasSuperscript()) {
				startY += subscriptOffset;
			}
				
			for(TSpan s : l.spans) {
				if (s.text.toString().equals("")) continue;
				
				Element tspan = doc.createElementNS(svgNS, "text");

				tspan.setAttributeNS (null, "x", Double.toString(startX));
				if (s.superscript) {
					tspan.setAttributeNS (null, "y", Double.toString(startY-subscriptOffset));
				} else if (s.subscript) {
					tspan.setAttributeNS (null, "y", Double.toString(startY+subscriptOffset));
				} else {
					tspan.setAttributeNS (null, "y", Double.toString(startY));
				}
				
				tspan.setTextContent(s.text.toString());
				textElement.appendChild(tspan);
				
				startX += s.width;
		
			}
			
			startY += l.getHeightToNextLine();  //l.getHeight();	
			
//			System.out.println("+++");
		}

//		Element textBound = doc.createElementNS (svgNS, "rect");
//		 	       
//		textBound.setAttributeNS (null, "fill", "yellow");
//		textBound.setAttributeNS (null, "fill-opacity", "0.25");
//		textBound.setAttributeNS (null, "stroke", "none");
//		
//		textBound.setAttributeNS (null, "width", Double.toString(maxLineWidth));
//		textBound.setAttributeNS (null, "height", Double.toString(paragraph.getHeight()));
//		textBound.setAttributeNS (null, "x", Double.toString(cx - maxLineWidth/2));
//		textBound.setAttributeNS (null, "y", Double.toString(cy - paragraph.getHeight() ));
//		
//		textElement.appendChild (textBound);
//		
//		textBound = doc.createElementNS (svgNS, "rect");
//	       
//		textBound.setAttributeNS (null, "fill", "blue");
//		textBound.setAttributeNS (null, "fill-opacity", "0.25");
//		textBound.setAttributeNS (null, "stroke", "none");
//		
//		textBound.setAttributeNS (null, "width", Double.toString(maxLineWidth));
//		textBound.setAttributeNS (null, "height", Double.toString(lineDescent));
//		textBound.setAttributeNS (null, "x", Double.toString(cx - maxLineWidth/2));
//		textBound.setAttributeNS (null, "y", Double.toString(cy - lineDescent ));
//		
//		textElement.appendChild (textBound);
		
		return textElement;
	}
	
	
	class TSpan {
		boolean superscript = false;
		boolean subscript = false;
		int height = 0 ;
		int width = 0 ;
		
		FontMetrics fmt;
		
		private StringBuffer text = new StringBuffer();
		
		TSpan(FontMetrics fmt) {
			this.fmt = fmt;
			this.superscript = false;
			this.subscript = false;
			height = fmt.getHeight();
		}	
		
		TSpan(TSpan origen) {
			this.fmt = origen.fmt;
			this.superscript = origen.superscript;
			this.subscript = origen.subscript;
			height = fmt.getHeight();
		}
		
		void add(String newtext) {
			this.text.append(newtext);
			width += fmt.stringWidth(newtext);
			height = fmt.getHeight();
		}
		
		
	}

	class SVGTextLine {	
		int height = SVGTextRenderer.LINE_HEIGHT;
		int width = 0;
		
		Vector<TSpan> spans = new Vector<TSpan>();
		
		void add(TSpan span) {
			this.spans.add(span);
			width += span.width;
		}
		
		int getWidth() {
			int w = 0;
			for (TSpan s : spans) {
				w+=s.width;
			}
			return w;
		}
		
		boolean hasSuperscript() {
			boolean superscript=false;
			for (TSpan s : spans) {				
				superscript = superscript || s.superscript;
			}	
			return superscript;
		}

		boolean hasSubscript() {
			boolean subscript=false;
			for (TSpan s : spans) {				
				subscript = subscript || s.superscript;
			}	
			return subscript;			
		}
		
		double getHeight() {
			double h = 0;
			boolean subscript=false, superscript=false;
			for (TSpan s : spans) {
				h = Math.max(h, s.height);
				subscript = subscript || s.subscript;
				superscript = superscript || s.superscript;
			}
			
			double offset = h/SUB_OFFSET_FACTOR;
			if (subscript) h += offset;
			if (superscript) h += offset; 			
			
			return h;
		}
		
		double getHeightToNextLine() {
			double h = 0;
			boolean subscript=false;
			for (TSpan s : spans) {
				h = Math.max(h, s.height);
				subscript = subscript || s.subscript;
//				superscript = superscript || s.superscript;
			}
			
			double offset = h/SUB_OFFSET_FACTOR;
			if (subscript) h += offset;
//			if (superscript) h += offset; 			
			
			return h;			
		}
	}
	
	class SVGParagraph {
		Vector<SVGTextLine> lines = new Vector<SVGTextLine>();
		
		double getHeight() {
			double h = 0;
			for (SVGTextLine l : lines) {
				h += l.getHeight(); 
			}
			
			return h;
		}
		
		double getMaxWidth() {
			double w = 0;
			for (SVGTextLine l : lines) {
				w = Math.max(w, l.getWidth()); 
			}
			return w;
		}
		
		void add(SVGTextLine l) {
			lines.add(l);
		}
		
		
	}

	public Element drawTextLineBelowLeft(Document doc, double cx, double cy, String text, int fontSize) {
		return drawTextLine( doc, cx, cy, text, fontSize, false, true);
	}
	
	public Element drawTextLineBelowRight(Document doc, double cx, double cy, String text, int fontSize) {
		return drawTextLine( doc, cx, cy, text, fontSize, false, false);
	}
	
	public Element drawTextLineAboveLeft(Document doc, double cx, double cy, String text, int fontSize) {
		return drawTextLine( doc, cx, cy, text, fontSize, true, true);
	}
	
	public Element drawTextLineAboveRight(Document doc, double cx, double cy, String text, int fontSize) {
		return drawTextLine( doc, cx, cy, text, fontSize, true, false);
	}	
	
	/**
	 * 
	 * @param doc
	 * @param cx
	 * @param cy
	 * @param text
	 * @param fontSize
	 * @param above - cy establece la coordenada inferior de la línea de texto. (comportamiento de SVG por defecto).
	 * 				   Si es falso, cy establece la coordenada superior, y la línea de texto estará por debajo de cy
	 * @param leftAligned - si true, el texto se alineará a la izquierda con cx. si no, a la derecha. 
	 * @return <code>Element</code> conteniendo la linea de texto
	 */
	public Element drawTextLine(Document doc, double cx, double cy, String text, int fontSize, 
			                    boolean above, boolean leftAligned) {
		if (text.equals("")) return null;
		
		Font font = new Font (SVGConfig.font, Font.TRUETYPE_FONT, fontSize);
		FontMetrics metrics = this.canvas.getFontMetrics(font);
		
//		StringBuffer sbText = new StringBuffer(text);
				
		double maxWidth = metrics.stringWidth(text);
		double lineAscent = metrics.getAscent();
		double lineDescent = metrics.getDescent();
		double lineHeight = metrics.getHeight();
		
		//System.out.println("maxLineWidth = " + maxLineWidth);

		Element textElement = doc.createElementNS(svgNS, "g");
		textElement.setAttributeNS (null, "style", "stroke-linejoin:bevel; stroke:none; font-size:" + fontSize + "px; text-anchor: start;");
		
		double startY = cy + (lineHeight)/2.0 ;
		double startX;
		
		if (leftAligned) {
			startX = cx; 
		} else {
			startX = cx - maxWidth - 1;
		}
		
		if (above) {
			startY = cy - lineDescent ; // Quitamos el descent para que toda la letra quede por encima
		} else {
			startY = cy + lineAscent;
		}		
			    				
		Element tspan = doc.createElementNS(svgNS, "text");
		tspan.setAttribute("font-family", SVGConfig.font);
		tspan.setAttributeNS (null, "x", Double.toString(startX));		
		tspan.setAttributeNS (null, "y", Double.toString(startY));
				
		tspan.setTextContent( text );
		textElement.appendChild(tspan);	

		return textElement;
	}	

	
	public static void main(String[] arg0) {
//		System.out.println(renderSpecialCharacters("tururu_a_tarara_br_beta__pi"));
		JFrame f = new JFrame();
		f.setVisible(true);
//		SVGTextRenderer.initialize((Graphics2D)f.getGraphics()).		
//			drawText2(null, 0, 0, "c2_stub_1_br_www_br__sub_x_sub_x_alpha__super_h_sub_hhh_super__beta", 10);
		
		Font font = new Font (SVGConfig.font, Font.TRUETYPE_FONT, 100);
		FontMetrics lm = f.getFontMetrics(font);
		System.out.println("Ascent: " + lm.getAscent());
		System.out.println("Descent: " + lm.getDescent());
		System.out.println("Height: " + lm.getHeight());
		System.out.println("Leading: " + lm.getLeading());
		Rectangle2D rect = lm.getStringBounds("Pa", f.getGraphics());
		System.out.println(   rect    +"\n" + lm.stringWidth("Pa") );
		System.out.println( 11 / 2);
		System.out.println( 11 / 2.0 );
		
		String strChars = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnÑñOoPpQqRrSsTtUuVvWwXxYyZz";
		char[] chars = new char[strChars.length()];
		strChars.getChars(0, strChars.length(), chars, 0);
		for (int i = 0; i < strChars.length() ; i++) {
			System.out.println(   "" + chars[i] +" : " + lm.stringWidth("" + chars[i]) );
		}
	}	

	public Element drawTextInFrame(Document doc, Rectangle2D frame, String text, int fontSize) {
		if (text.equals("")) return null;
		
//		Graphics g = new Graphics();
		Font font = new Font ( SVGConfig.font , Font.TRUETYPE_FONT, fontSize );
		FontMetrics metrics = this.canvas.getFontMetrics(font);
		
		StringBuffer sbText = new StringBuffer(text);

		SVGParagraph paragraph = makeSVGParagraphSplitText(sbText, font, Math.round(frame.getWidth() - 7 ) ); // Margen de 7
				
		double lineAscent = metrics.getAscent();
		double lineHeight = metrics.getHeight();
		double subscriptOffset = lineHeight / SUB_OFFSET_FACTOR;
		
		//System.out.println("maxLineWidth = " + maxLineWidth);

		Element textElement = doc.createElementNS(svgNS, "g");
		textElement.setAttributeNS (null, "style", "stroke-linejoin:bevel; stroke:none; font-size:" + (fontSize) + "px; text-anchor: start;kerning:auto;letter-spacing:-0.5px;");
		
//		double startY = lineHeight/2 + (lineAscent - lineDescent)/2.0 +  
//						cy - paragraph.getHeight()/2.0 ;
		double startY = frame.getMinY() + lineAscent + 2 ; // Margen vertical de 2
		double startX = frame.getMinX() + 4; // Margen de 3
		
		// Traducir a elementos SVG
		//		boolean newLine = true;
		for (SVGTextLine l : paragraph.lines) {
			startX = frame.getMinX();
				
			for(TSpan s : l.spans) {
				if (s.text.toString().equals("")) continue;
				
				Element tspan = doc.createElementNS(svgNS, "text");

				tspan.setAttributeNS (null, "x", Double.toString(startX));
				if (s.superscript) {
					tspan.setAttributeNS (null, "y", Double.toString(startY-subscriptOffset));
				} else if (s.subscript) {
					tspan.setAttributeNS (null, "y", Double.toString(startY+subscriptOffset));
				} else {
					tspan.setAttributeNS (null, "y", Double.toString(startY));
				}
				
				tspan.setTextContent(s.text.toString());
				textElement.appendChild(tspan);
				
				startX += s.width;
		
			}
			
			startY += l.getHeightToNextLine();  //l.getHeight();	
			if ( startY > frame.getMaxY() ) break;
//			System.out.println("+++");
		}
		
		return textElement;
	}

}
