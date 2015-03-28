package svgcreator;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import model.layout.Layouter;
import svgcontroller.SVGController;
import svgview.SVGConfig;
import svgview.SVGOutput;

/**
 * Clase que puede ser usada desde el CLI. 
 * Genera archivos SVG a partir de archivos SBML.
 * 
 * @author Guillermo Fajula Leal
 * 
 * 
 */
public class SVGExport {
	private final static String xmlPath = "/upload_files/";
	
	/**
	 * Punto de entrada al ser llamado desde CLI. 
	 * 
	 * @param args opciones: [-png], generar archivo PNG además del SVG
	 * 						  [-layorg], usar layout orgánico si es necesario
	 * 						  [-layort], usar layout ortogonal si es necesario
	 * 						  [-noreacnames], omitir los nombres de las reacciones
	 */
	public static void main(String[] args) {
		String sourceFile = null;
		String pngFile = null;
		
		if (args.length < 1) {
			System.out.print("Usage: java SVGExport sourcefile [destinationfile]");
				
			System.out.println("Test Run");
			// sourceFile = "/media/DATA/_guillermo/desarrollo/java/SBML2SVG/bin/Carbon_Pure.xml" ;
			sourceFile = "/media/DATA/_guillermo/desarrollo/PFC/batchTesting/Sbmls/adding.xml";
			pngFile = sourceFile + ".png" ;
			
		} else {
			sourceFile = args[0];
		}
		
		loadSBMLLibrary();		
		
		String destinationFile = sourceFile.substring(0 , sourceFile.lastIndexOf(".xml") ) + ".svg";
		if (args.length > 1) {
			if ( !args[1].substring(0,1).equals("-") ) {
				destinationFile = args[1];
			}
		}  
			
		SVGConfig.SBGNMode = false;
		// Si esta la opcion "-png", generar tambien el PNG
		for (String arg: args ) {	

			if ( arg.equalsIgnoreCase("-png") ) {
				pngFile = sourceFile + ".png";
			} else if (  arg.equalsIgnoreCase("-layorg")  ) {
				SVGConfig.layout_type = Layouter.LAYOUT_ORGANIC;
			} else if (  arg.equalsIgnoreCase("-layort")  ) {
				SVGConfig.layout_type = Layouter.LAYOUT_ORTHOGONAL;
			} else if (  arg.equalsIgnoreCase("-noreacnames")  ) {
				SVGConfig.showReactionNames = false;
			} else if (  arg.equalsIgnoreCase("-sbgn")  ) {
				SVGConfig.SBGNMode = true;
			}
		}
		
//		System.out.println( export( "/media/DATA/_guillermo/desarrollo/java/SBML2SVG/bin/addingCurve.xml" , "./addingCurve.xml"+".svg" ) );
		System.err.println( destinationFile );
		System.out.print("{"); 
		SVGOutput.printStatistic("SBMLFile", sourceFile);
		String result = export( sourceFile , destinationFile, pngFile );
		SVGOutput.printStatistic("Result", result);
		System.out.print("}");	
		
		
	}	
	
	/**
	 * Carga libSBML
	 */	
	private static void loadSBMLLibrary() {
		String varname;
		
		if (System.getProperty("mrj.version") != null)
			varname = "DYLD_LIBRARY_PATH"; // We're on a Mac.
		else
			varname = "LD_LIBRARY_PATH"; // We're not on a Mac.

		try {
			System.loadLibrary("sbmlj");
			// For extra safety, check that the jar file is in the classpath.
			Class.forName("org.sbml.libsbml.libsbml");
			SVGOutput.printVerbose("libsbmlj " +
								org.sbml.libsbml.libsbml.getLibSBMLDottedVersion() + 
							   " successfully loaded!");
		} catch (UnsatisfiedLinkError e) {
			SVGOutput.printVerbose( System.getenv("LD_LIBRARY_PATH") );
			System.err
					.println("Error: could not link with the libSBML library."
							+ "  It is likely\nyour " + varname
							+ " environment variable does not include\nthe"
							+ " directory containing the libsbml library file.");
			System.exit(1);
		} catch (ClassNotFoundException e) {
			SVGOutput.printVerbose( System.getenv("LD_LIBRARY_PATH") );
			System.err.println("Error: unable to load the file libsbmlj.jar."
					+ "  It is likely\nyour " + varname + " environment"
					+ " variable or CLASSPATH variable\ndoes not include"
					+ " the directory containing the libsbmlj.jar file.");
			System.exit(1);
		} catch (SecurityException e) {
			SVGOutput.printVerbose( System.getenv("LD_LIBRARY_PATH") );
			System.err.println("Could not load the libSBML library files due to a"
							+ " security exception.");
		} 
	}
	
	
	/**
	 * Genera un archivo SVG a partir de un archivo SBML
	 * 
	 * @param sourcePathfile Ruta del archivo SBML
	 * @param destinationPathfile Destino para el archivo SVG
	 * @return String con información de salida
	 */
	public static String export(String sourcePathfile, String destinationPathfile ) {
		System.out.print("{"); 
		SVGOutput.printStatistic("SBMLFile", sourcePathfile);
		String result = export(sourcePathfile, destinationPathfile, null );
		SVGOutput.printStatistic("Result", result);
		System.out.print("}");
		return result;
	}
	
	/**
	 * Genera un archivo SVG a partir de un archivo SBML, y opcionalmente
	 * un archivo PNG
	 * 
	 * @param sourcePathfile Ruta del archivo SBML
	 * @param destinationPathfile Destino para el archivo SVG
	 * @param pngPathfile Destino para el archivo PNG. null si no se quiere generar PNG.
	 * @return String con información de salida
	 */
	public static String export(String sourcePathfile, String destinationPathfile, String pngPathfile ) {
		try {
			Date d0 = new Date();
			
			File f = new File( sourcePathfile ); 
			if ( !(f.exists()) ) {
				return "No existe el fichero " + f.getName() ; 
			}
			
			// Especificar nombre donde se guardara el QTree del diagrama:
			SVGConfig.saveQuadTree = true;
			SVGConfig.quadtreeFile = destinationPathfile.substring(0, destinationPathfile.lastIndexOf(".")) + "" + ".qtree.json"; 
			SVGOutput.printVerbose( SVGConfig.quadtreeFile );
			SVGConfig.omitJavascript = true;
			SVGConfig.debugMode = false;
			// SVGConfig.SBGNMode = true;
			
			SVGController ctrl = new SVGController( sourcePathfile );
			
			ctrl.saveToSVGFile( destinationPathfile );
			
			if (pngPathfile != null) {
				ctrl.saveToPNGFile( pngPathfile );					
			}
			
		    SVGOutput.printStatistic( "ExportTime" , ""+ (new Date().getTime() - d0.getTime()) );
			return "Ok";
		} catch (Exception e) {
			e.printStackTrace();
			return "Excepcion " + e.getClass() +  e.getMessage();
		}
	}
	
	/**
	 * @deprecated
	 * 
	 * Método específico para hacer polling sobre una base de datos local
	 * y generar archivos SVG para los registros nuevos, con campo status='w'
	 * 
	 */
	@Deprecated
	public void service() {
		Connection conexion = null;
		Statement instruccion;
		ResultSet rs = null;
		
        try {
			Class.forName("com.mysql.jdbc.Driver");
			conexion = DriverManager.getConnection("jdbc:mysql://localhost/sbml2svg?user=root&password=mysql1234");
			
			
			while (true) {
				instruccion = conexion.createStatement();
			
				String sql = "SELECT * FROM diagram d where status='w'";
			
				rs = instruccion.executeQuery(sql);
				
				if (rs.first()) { 
					do {
						String sbmlFile = xmlPath + rs.getString("sbmlfile"); 
						
						// Comprobar si existe el fichero fuente
						
						if ( rs.getString("sbmlfile")==null || (rs.getString("sbmlfile").equals("") ) || !new File(sbmlFile).exists() ) {
							instruccion = conexion.createStatement();							
							sql = "UPDATE diagram SET status='e', error='" + "Fichero [" + rs.getString("sbmlfile") + "] no existe" +
							      "' where id=" + rs.getInt("id");
						
							instruccion.executeUpdate( sql );	
						} else {
						
							System.out.println( "Processing " + sbmlFile );
							
							String result = export(sbmlFile, sbmlFile + ".svg");
							
							if ( result.equalsIgnoreCase("OK") ) {
								instruccion = conexion.createStatement();							
								sql = "UPDATE diagram SET status='r'  where id=" + rs.getInt("id");
							
								instruccion.executeUpdate( sql );
							} else {
								instruccion = conexion.createStatement();							
								sql = "UPDATE diagram SET status='e', error='" + result + 
								      "' where id=" + rs.getInt("id");
							
								instruccion.executeUpdate( sql );	
							}
						}
						//System.gc();
						Thread.sleep(10000);
					} while ( rs.next() );
				}
				//System.gc();
				Thread.sleep(30000);
			}
			
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			
			try {
				if (rs!=null) rs.close();
				if (conexion!=null) conexion.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
				
		}
		
	}
	
}
