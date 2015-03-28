package svgview.util;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SVGScripts {
	public static final String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
	
	public static String javaArrayToJS(String[] cadenas){
		String jsArray = "new Array(";
		for (int i=0; i<cadenas.length-1 ; i++ ){
			jsArray += "\"" + cadenas[i] + "\",";			
		}
		if (cadenas.length > 0) {
			jsArray += "\"" + cadenas[cadenas.length-1] + "\"";
		}		
			
		jsArray += ")";
		
		return jsArray;
	}
	
	public static void svgAdd(Element docParent) {
		Document svgDoc = docParent.getOwnerDocument();
		Element svgScript = svgDoc.createElementNS(svgNS, "script");
		svgScript.setAttributeNS(null, "type", "text/ecmascript");
		
		String scriptsJS = loadFromFile("./scripts.js");
		
		
		Node cdata = svgDoc.createCDATASection(
				
				"function ocultar(id){" +
				"var elmt = document.getElementById(id);" +
				"elmt.setAttribute (\"visibility\", \"hidden\");" +				
//				"alert(\"Hello from \" + elmt.getAttribute (\"visibility\"));" +
				"}\n" +
				
				"function mostrar(id){" +
				"var elmt = document.getElementById(id);" +
				"elmt.setAttribute (\"visibility\", \"visible\");" +				
				"}\n" +
				 
				scriptsJS
				
		);
		svgScript.appendChild(cdata);
		
	    docParent.appendChild( svgScript );	    
	}

	public static String loadFromFile(String fileName) {
		StringBuffer sb = new StringBuffer();
		char[] b = new char[8192];
		int n;
		
//		java.io.File ff = new java.io.File(".");
//		String s = ff.getAbsolutePath();
		Reader is = null;
		try {
			is = new FileReader(fileName);
			while ( (n=is.read(b)) > 0) {
				sb.append(b, 0, n);
			}	
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if ( is != null)
				try {			
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
		return sb.toString();
	}
		
}
