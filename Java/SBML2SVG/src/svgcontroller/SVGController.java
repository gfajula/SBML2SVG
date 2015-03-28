package svgcontroller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

import model.builder.ModelBuilderFactory;
import model.layout.Layouter;

import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.sbml.libsbml.SBMLDocument;
import org.sbml.libsbml.SBMLReader;
import org.w3c.dom.Document;

import svgview.SVGConfig;
import svgview.SVGOutput;
import svgview.SVGView;

/**
 * Clase controlador a partir de la cual se 
 * manejan las funciones de generación de diagramas 
 * 
 * @author Guillermo Fajula Leal
 * 
 */
public class SVGController {
	protected model.Model model;
	private SVGView svgView;
	
	/**
	 * Constructor que genera un controlador para un determinado archivo SBML.
	 * 
	 * @param sFile Ruta al archivo SBML.
	 * @throws SBML2SVGException
	 */
	public SVGController(String sFile) throws SBML2SVGException {		
		loadFile(sFile);
	}

	private void loadFile(String sFile) throws SBML2SVGException {
		File fileToOpen = new File(sFile);
		if (  !fileToOpen.exists() ) {
			System.err.println("No existe el archivo '" + sFile + "'");
			System.exit(-1);
		} else {
			SVGOutput.printStatistic("SbmlFileSize", "" + fileToOpen.length() );
		}
		
		
		// Obtener estadistica de tiempos
		Date d0 = new Date();
		
		SBMLDocument sbmlDoc = new SBMLReader().readSBML(sFile);
		sbmlDoc.printErrors();
		
		this.model = ModelBuilderFactory.getModelBuilder(sbmlDoc).buildModel();
		// Layouter.doOrthogonalLayout(this.model, this.diagramSize);
		// printMemoryStat();
		
		/* Liberar documento SBML de memoria */
		sbmlDoc = null;

		svgView = new SVGView(this);
		SVGOutput.printStatistic("SVGGenerationTime", ""+(new Date().getTime() - d0.getTime()));
		// SVGOutput.printStatistic( "Diagrama generado en " + (new Date().getTime() - d0.getTime()) + " milisegundos" );
		
		SVGOutput.printStatistic( "Reacciones" ,this.model.getReactionsCollection().size() + "");
		SVGOutput.printStatistic( "SimpleSpecies" , this.model.getSimpleAliasesCollection().size() + "");
		SVGOutput.printStatistic( "ComplexSpecies" , this.model.getComplexAliasesCollection().size() + "");
		DecimalFormat df = new DecimalFormat("#.###");
		df.format(0.912385);
		SVGOutput.printStatistic( "SpeciesDensity" , ""+
				df.format( new Integer(this.model.getSimpleAliasesCollection().size() + this.model.getComplexAliasesCollection().size()).floatValue()
						   /
				           ((model.getDiagramSize().height * model.getDiagramSize().width*1.0) / 1000000.0  ) ) ); // " Spc/kilopixel²"
		
		
		
	}
	
	public void loadNewFile(String sFile) throws SBML2SVGException{
		this.setModel( null );
		this.setSvgView( null );
		System.gc();
		printMemoryStat();
		
		loadFile( sFile );
	}

	private void printMemoryStat() {
		SVGOutput.printVerbose("" + (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/(1024*1024) + "MB / " 
				+ Runtime.getRuntime().totalMemory()/(1024*1024) + "MB (" +
				100 * Runtime.getRuntime().freeMemory() / Runtime.getRuntime().totalMemory() + "% free)");
	}

	/**
	 * Crear una nueva vista, util p.ej. si se cambian valores de configuración.
	 * Genera una nueva vista.
	 * @throws SBML2SVGException 
	 *  
	 */
	public void refreshView() throws SBML2SVGException {
	    svgView = new SVGView(this);
		svgView.drawToCanvas();
	}
	
	/**
	 * Reorganizar de nuevo el layout del diagrama.
	 * Genera una nueva vista con el layout que se haya configurado.
	 * @throws SBML2SVGException 
	 *  
	 */
	public void refreshLayout() throws SBML2SVGException {
		if ( SVGConfig.layout_type == Layouter.LAYOUT_ORGANIC ) {
			Layouter.doOrganicLayout( this.model );
		} else if ( SVGConfig.layout_type == Layouter.LAYOUT_ORTHOGONAL ) {
			Layouter.doOrthogonalLayout( this.model );
		}
		
	    svgView = new SVGView(this);
		svgView.drawToCanvas();
	}

	/**
	 * Devuelve la vista asociada a este controlador
	 * 
	 * @return vista asociada al controlador
	 */
	public SVGView getSVGView() {
		return svgView;
	}
	
	/**
	 * Generar el conjunto de 'Modification Residues' de una proteina,
	 * para el caso de proteinas que esten dentro de complejos
	 * 
	 * @param cdm modelo de SBML de CellDesigner
	 * @param residues Vector con las 'Modification Residues' en el modelo del SBML de CellDesigner
	 * @param idSpecie ID de la Specie 
	 * @param idProtein ID de la Proteína
	 * @return Vector con las MResidue generadas 
	 */
//	private Vector<MResidue> getMResiduesFromModificationResidues(
//						CellDesignerModel cdm, 
//						Vector<ModificationResidue> residues, 
//						String idSpecie, String idProtein) {
//		if (residues==null)
//			return null;
//		Vector<MResidue> result = new Vector<MResidue>();
//		for (ModificationResidue mr : residues){
//			String state = "";
//			for (Specie s : cdm.getAnnotation().getListOfIncludedSpecies()){
//				if (s.getId().compareToIgnoreCase(idSpecie)==0){
//					SpeciesIdentity si = s.getSpeciesIdentity();
//					
//					if ("PROTEIN".compareToIgnoreCase(si.getClassOf())==0){
//						if (si.getValue().compareToIgnoreCase(idProtein)==0){
//							State st = si.getState();
//							if (st!=null){
//								Modificator mbn = st.getModificatorByName(mr.getId());
//								if (mbn!=null){
//									state = mbn.getState();
//								}
//							}
//						}
//					}
//				}
//			}
//			result.addElement(new MResidue(mr.getId(),
//					mr.getName(),
//					mr.getAngle(),
//					mr.getSide(),
//					state));
//		}
//		return result;
//	}
	
	/**
	 * Obtener el modelo (del diagrama) asociado a este controlador
	 * 
	 * @return modelo del diagrama
	 */
	public model.Model getModel(){
		return this.model;
	}
	
	/**
	 * Exportar el diagrama SVG a un archivo PNG
	 * 
	 * @param outFilename nombre del archivo de salida
	 */
	public void saveToPNGFile (String outFilename) {
		try {
			printMemoryStat();
			PNGTranscoder transcoder = new PNGTranscoder();
			TranscoderInput input = new TranscoderInput(this.getSVGView().getDocument());
			OutputStream ostream;
			ostream = new FileOutputStream( outFilename );
			TranscoderOutput output = new TranscoderOutput(ostream);
			transcoder.transcode(input, output);
			printMemoryStat();
		} catch (FileNotFoundException e1) {
			System.err.println( outFilename + " not found!!!!");
			e1.printStackTrace();
		} catch (TranscoderException e) {		
			e.printStackTrace();
		}		
	}
	
	/**
	 * Salvar el diagrama SVG a un archivo
	 * 
	 * @param outFilename nombre del archivo de salida
	 */
	public void saveToSVGFile (String outFilename) {
		FileWriter f=null;
	    try {	    	
	        // Create the necessary ***IO facilities
	        f = new FileWriter (outFilename);	        
            

	        PrintWriter writer = new PrintWriter (outFilename, SVGConfig.encoding );
	        // PrintWriter writer = new PrintWriter (file, "utf-8");
	        
            // Get the created document from the canvas
            Document svgDoc = this.getSVGView().getDocument() ;
            svgDoc.normalize();
            // And here our SVG content is actually being
            // written to a file
            DOMUtilities.writeDocument (svgDoc, writer);
            writer.close();
            f.close();                    
            SVGOutput.printStatistic("SvgFileSize", "" +  new File(outFilename).length());

//            Source styleSheet = new StreamSource("C:\\SVGStyle.xsl");            
//            Transformer xformer= TransformerFactory.newInstance().newTransformer(styleSheet);            
//            Source source=new DOMSource(svgDoc);
//            Result result=new StreamResult(new FileWriter (file + ".htm"));            
//            xformer.transform(source, result);
            
            ////////
        } catch (IOException ioe) {
            System.err.println("IO problem: " + ioe.toString());
        } 
//        catch (TransformerException e) {
//        	System.err.println("TransformerException problem: " + e.toString());
//        }

    
	}

	/**
	 * Salvar el diagrama SVG a un SVGZ (archivo XML comprimido con GZIP)
	 * 
	 * @param outFilename nombre del archivo de salida
	 */
	public void saveToSVGZFile (String outFilename) {
//		FileWriter f=null;
	    try {	    	            
            GZIPOutputStream out = new GZIPOutputStream(            						
            						new FileOutputStream(
            								outFilename
            								));
            
            OutputStreamWriter osw = new OutputStreamWriter(out , SVGConfig.encoding);
            PrintWriter writer = new PrintWriter (osw);//PrintWriter (out, "Unicode");            
          
            // And here our SVG content is actually being
            // written to a file
            DOMUtilities.writeDocument (this.getSVGView().getDocument(), writer);

            writer.close();
//            f.close();
        } catch (IOException ioe) {
            System.err.println("IO problem: " + ioe.toString());
        } 
    
	}

	public void setModel(model.Model model) {
		this.model = model;
	}

	public void setSvgView(SVGView svgView) {
		this.svgView = svgView;
	}

}
