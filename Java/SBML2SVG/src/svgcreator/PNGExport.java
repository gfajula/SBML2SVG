package svgcreator;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import svgcontroller.SVGController;
import svgview.SVGConfig;

public class PNGExport {
	private final static String xmlPath = "/upload_files/";
	
	public static void main(String[] args) {
		
		
		
		loadSBMLLibrary();		
		
//		SVGController ctrl = new SVGController("/media/PRESARIO_RP/_guillermo/desarrollo/PFC/samples/CellPublisher/carbon.xml");
//		String file = "/media/PRESARIO_RP/_guillermo/desarrollo/PFC/samples/exports/carbon.png";
		
//		SVGController ctrl = new SVGController("/media/PRESARIO_RP/_guillermo/desarrollo/PFC/samples/components40delta1.xml");
//		String file = "/media/PRESARIO_RP/_guillermo/desarrollo/PFC/samples/exports/components40delta1.png";
			
//	    ctrl.saveToPNGFile(file); 		
//		System.out.println( "SVG exportado en " + (new Date().getTime() - d0.getTime()) + " milisegundos" );
		
		new PNGExport().service();
				
	}	
	
	public static void loadSBMLLibrary() {
		String varname;
		
		if (System.getProperty("mrj.version") != null)
			varname = "DYLD_LIBRARY_PATH"; // We're on a Mac.
		else
			varname = "LD_LIBRARY_PATH"; // We're not on a Mac.

		try {
			System.loadLibrary("sbmlj");
			// For extra safety, check that the jar file is in the classpath.
			Class.forName("org.sbml.libsbml.libsbml");
		} catch (UnsatisfiedLinkError e) {
			System.out.println( System.getenv("LD_LIBRARY_PATH") );
			System.err
					.println("Error: could not link with the libSBML library."
							+ "  It is likely\nyour " + varname
							+ " environment variable does not include\nthe"
							+ " directory containing the libsbml library file.");
			System.exit(1);
		} catch (ClassNotFoundException e) {
			System.out.println( System.getenv("LD_LIBRARY_PATH") );
			System.err.println("Error: unable to load the file libsbmlj.jar."
					+ "  It is likely\nyour " + varname + " environment"
					+ " variable or CLASSPATH variable\ndoes not include"
					+ " the directory containing the libsbmlj.jar file.");
			System.exit(1);
		} catch (SecurityException e) {
			System.out.println( System.getenv("LD_LIBRARY_PATH") );
			System.err.println("Could not load the libSBML library files due to a"
							+ " security exception.");
		} 
	}
	
	/*
	 * Exportar archivo PNG
	 */
	public String export(String sourcePathfile, String destinationPathfile) {
		try {
			Date d0 = new Date();
			
			File f = new File( sourcePathfile ); 
			if ( !(f.exists()) ) {
				return "No existe el fichero " + f.getName() ; 
			}
			
			SVGConfig.saveQuadTree = true;
			SVGController ctrl = new SVGController( sourcePathfile );
						
			ctrl.saveToPNGFile( destinationPathfile ); 		
			
			System.out.println( "PNG exportado en " + (new Date().getTime() - d0.getTime()) + " milisegundos" );
			return "Ok";
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
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
						System.out.println( sbmlFile );
						
						if ( rs.getString("sbmlfile")==null || (rs.getString("sbmlfile").equals("") ) || !new File(sbmlFile).exists() ) {
							instruccion = conexion.createStatement();							
							sql = "UPDATE diagram SET status='e', error='" + "Fichero [" + rs.getString("sbmlfile") + "] no existe" +
							      "' where id=" + rs.getInt("id");
						
							instruccion.executeUpdate( sql );	
						} else {
							
						
							SVGConfig.quadtreeFile = sbmlFile + ".qt.json";
							String result = export(sbmlFile, sbmlFile + ".png");
							
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
