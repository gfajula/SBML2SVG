package svgview;

import model.layout.Layouter;

/**
 * Clase que contiene los distintos valores configurables de la aplicacion 
 * 
 * @author Guillermo Fajula Leal
 *
 */
public class SVGConfig {
	
	public static final String DEFAULT_PATH = "/media/DATA/_guillermo/desarrollo/PFC/samples/";
	public static final int defaultFillColorR = 240;
	public static final int defaultFillColorG = 240;
	public static final int defaultFillColorB = 245;
	public static boolean debugMode = true;
	public static boolean showReactionNames = true;
	public static String font = "Arial";
	public static String reactionBackgroundColor = "white";
	public static boolean omitJavascript = true;
	public static boolean saveQuadTree = false;
	public static String quadtreeFile = "/home/guille/qtree.json";
	public static String encoding = "utf-8"; // utf-8 | Unicode
	public static boolean SBGNMode = true;
	public static boolean verbose = false;
	public static boolean jsonOutput = true;
	
	

	
//	public static int layout_type = Layouter.LAYOUT_ORGANIC ;
	public static int layout_type = Layouter.LAYOUT_ORTHOGONAL ; // Layout por defecto
	
}