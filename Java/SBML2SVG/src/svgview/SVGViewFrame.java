package svgview;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.io.File;

import javax.swing.DebugGraphics;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import model.layout.Layouter;

import org.apache.batik.bridge.UpdateManagerEvent;
import org.apache.batik.bridge.UpdateManagerListener;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;

import svgcontroller.SBML2SVGException;
import svgcontroller.SVGController;

/***
 * Clase Swing para visualizar los diagramas SVG 
 * 
 * @author guille
 *
 */
public class SVGViewFrame extends JFrame {
	private static final long serialVersionUID = 1045729816288512670L;						// The SVG document 
	protected JSVGCanvas svgCanvas;
    private JScrollPane jsp;
    private JMenuBar mb;
    private JStatusBar jsb;
    private Dimension diagramSize;
    private String prevPath;
    private SVGController ctrl;
    static String sbmlFile = null;

	private void close() {		
		this.svgCanvas = null;
		jsp.getParent().remove( jsp );
	}
	
	protected void loadNewFile(String absolutePath) throws SBML2SVGException {
		this.svgCanvas = null;
		this.jsp.getViewport().removeAll();		
		this.ctrl.loadNewFile( absolutePath );
		
		refreshView();
	}
	
    public void refreshView() {   	
    	this.diagramSize = this.ctrl.getSVGView().getDimension() ;
    	this.svgCanvas = this.ctrl.getSVGView().getCanvas();   	
    	
    	/* Valores para intentar reducir la huella en memoria */
//    	this.svgCanvas.setAutoscrolls(false);
//    	this.svgCanvas.setDoubleBuffered( false );
//    	this.svgCanvas.setDoubleBufferedRendering( false );
//    	this.svgCanvas.setDebugGraphicsOptions( DebugGraphics.NONE_OPTION );
//    	this.svgCanvas.setDisableInteractions( true );
//    	this.svgCanvas.setEnableImageZoomInteractor( false );
//    	this.svgCanvas.setEnablePanInteractor( false );
//    	this.svgCanvas.setEnableRotateInteractor( false );
//    	this.svgCanvas.setEnableZoomInteractor( false );
//    	this.svgCanvas.setUseUnixTextSelection( false );
//    	this.svgCanvas.setProgressivePaint(false);
//    	SVGOutput.printVerbose( this.svgCanvas.getSize() );
    	/**/
    	
    	this.ctrl.getSVGView().drawToCanvas();
    	if ( SVGConfig.omitJavascript ) {
    		this.svgCanvas.setDocumentState( JSVGCanvas.ALWAYS_STATIC );
    	} else {
    		this.svgCanvas.setDocumentState( JSVGCanvas.ALWAYS_DYNAMIC );
    	}

    	setSVGCanvasListeners();   	
    
    	this.jsp.setViewportView( this.svgCanvas ) ;
    	jsp.setPreferredSize(svgCanvas.getPreferredSize()); //new Dimension(800, 600));
        
    	this.update( this.getGraphics() );    	
    }
    
    public SVGViewFrame(SVGController cnt) {
    	this.ctrl = cnt;
    	
    	this.diagramSize = cnt.getSVGView().getDimension() ;
    	
    	this.svgCanvas = cnt.getSVGView().getCanvas();
    	this.svgCanvas.setAnimationLimitingNone();
    	/* Valores para intentar reducir la huella en memoria */
//    	this.svgCanvas.setAutoscrolls(false);
//    	this.svgCanvas.setDoubleBuffered( false );
//    	this.svgCanvas.setDoubleBufferedRendering( false );
//    	this.svgCanvas.setDebugGraphicsOptions( DebugGraphics.NONE_OPTION );
//    	this.svgCanvas.setDisableInteractions( true );
//    	this.svgCanvas.setEnableImageZoomInteractor( false );
//    	this.svgCanvas.setEnablePanInteractor( false );
//    	this.svgCanvas.setEnableRotateInteractor( false );
//    	this.svgCanvas.setEnableZoomInteractor( false );
//    	this.svgCanvas.setUseUnixTextSelection( false );
//    	this.svgCanvas.setProgressivePaint(false);
//    	System.out.println( this.svgCanvas.getSize() );
    	/**/
    	
    	System.out.println( svgCanvas.isLightweight() );    	
    	cnt.getSVGView().drawToCanvas();
    	//Runtime.getRuntime().gc();
    	
    	if ( SVGConfig.omitJavascript ) {
    		this.svgCanvas.setDocumentState( JSVGCanvas.ALWAYS_STATIC );
    	} else {
    		this.svgCanvas.setDocumentState( JSVGCanvas.ALWAYS_DYNAMIC );
    	}
    	
    	initMenuBar();
    	
    	setSVGCanvasListeners();   	
    
    	initializeJScrollPane();
    	
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add("Center", jsp);
        panel.add("South", jsb = new JStatusBar());
        this.getContentPane().add(panel);
        this.pack();
        this.setSize(800, 600);      
        
    	System.out.println( this.svgCanvas.getSize() );
    	//Runtime.getRuntime().gc();
    }

	private void initializeJScrollPane() {
		jsp = new JScrollPane( this.svgCanvas /*cnt.getSVGView().getCanvas()*/ );
		jsp.setDoubleBuffered( false );
		jsp.setDebugGraphicsOptions( DebugGraphics.NONE_OPTION  );
	
    	jsp.setPreferredSize(svgCanvas.getPreferredSize()); //new Dimension(800, 600));
	}
        
    /**
     * Inicializar las opciones del menu de la ventana de visualización del SVG     * 
     */
	private void initMenuBar(){
		this.mb = new JMenuBar();
		
		JMenu m1 = new JMenu("Archivo");
		m1.setMnemonic('a');
		this.mb.add(m1);
		JMenuItem mi11 = new JMenuItem("Abrir");
		mi11.setMnemonic('b');
		m1.add(mi11);
		mi11.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {				   
				   JFileChooser	fc = new JFileChooser( SVGViewFrame.this.getPrevPath() );
				   
				   fc.setFileFilter(
						   new FileFilter(){
							   @Override
							   public String getDescription() {										
									return "Archivo SBML";
							   }
							   public boolean accept(File f){
								   return f.getName().endsWith(".xml") || f.isDirectory();
							   }
						   });
				   int status = fc.showOpenDialog(SVGViewFrame.this);
				   if (status == JFileChooser.APPROVE_OPTION) {
//					  
					   try {
						   SVGViewFrame.this.setPrevPath( fc.getSelectedFile().getAbsolutePath() );
						   SVGViewFrame.this.loadNewFile( fc.getSelectedFile().getAbsolutePath() );
					   } catch (Exception any) {
						   any.printStackTrace();						   
					   }
				   }
				}						
			}
		);
		JMenuItem mi12 = new JMenuItem("Salvar como...");
		mi12.setMnemonic('s');
		mi12.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
				        // Mostrar dialogo para obtener el nombre
				        // con el que salvar el fichero
						 JFileChooser fcSave = new JFileChooser();
						 fcSave.setFileFilter(
								   new FileFilter(){
									   @Override
									   public String getDescription() {										
											return "Archivo SVG";
									   }
									   public boolean accept(File f){
										   return f.getName().endsWith(".svg") || f.isDirectory();
									   }
								   });
						 fcSave.addChoosableFileFilter(
								   new FileFilter(){
									   @Override
									   public String getDescription() {										
											return "Archivo SVGZ (SVG Comprimido)";
									   }
									   public boolean accept(File f){
										   return f.getName().endsWith(".svgz") || f.isDirectory();
									   }
								   });
						 fcSave.addChoosableFileFilter(
								   new FileFilter(){
									   @Override
									   public String getDescription() {										
											return "Archivo PNG";
									   }
									   public boolean accept(File f){
										   return f.getName().endsWith(".png") || f.isDirectory();
									   }
								   });						 
						 
				         int returnVal = fcSave.showSaveDialog(SVGViewFrame.this);

				        // If button "Save" was pressed (not "Cancel")
				         if (returnVal == JFileChooser.APPROVE_OPTION) {
				            // Get the file path and pass it to the method
				            // that will perform saving
				        	 File file = fcSave.getSelectedFile();
				        	 if (file.getName().endsWith(".svgz")) {
				        		 (SVGViewFrame.this).ctrl.saveToSVGZFile(file.getPath());
				        	 } else if (file.getName().endsWith(".svg")) {
				        		 (SVGViewFrame.this).ctrl.saveToSVGFile(file.getPath());
				        	 } else if (fcSave.getFileFilter().getDescription().equals("Archivo SVGZ (SVG Comprimido)")) {
				        		 (SVGViewFrame.this).ctrl.saveToSVGZFile( file.getPath()+".svgz" );
				        	 } else if (fcSave.getFileFilter().getDescription().equals("Archivo PNG")) {
				        		 (SVGViewFrame.this).ctrl.saveToPNGFile( file.getPath()+".png" );
				        	 } else {
				        		 (SVGViewFrame.this).ctrl.saveToSVGFile(file.getPath()+".svg");
				        	 }				             
				        }
						
					}
				});		
		m1.add(mi12);
		JMenuItem mi13 = new JMenuItem("Cerrar");
		mi13.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						SVGViewFrame.this.close();
					}
				});
		
		m1.add(mi13);
		JMenuItem mi14 = new JMenuItem("Salir");
		m1.add(mi14);
		mi14.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SVGViewFrame.this.dispose();
					//System.gc();
					System.exit(0);
				}
			}
		);
		
		JMenu m2 = new JMenu("Ver");
		m2.setMnemonic('v');
		this.mb.add(m2);
		
		JMenuItem m21 = new JMenuItem("Tamaño Original");
		m2.add(m21);
		m21.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						AffineTransform reset = new AffineTransform();
						SVGViewFrame.this.svgCanvas.setRenderingTransform(reset);
					}
				}
			);
		
		JMenuItem m22 = new JMenuItem("Zoom 2x");
		m2.add(m22);
		m22.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
//						SvgViewFrame.this.jsvgscp.getCanvas().
						AffineTransform zoom = new AffineTransform(2, 0, 0, 2, 0, 0);
						SVGViewFrame.this.svgCanvas.setRenderingTransform(zoom);
					}
				}
			);		
		
		JMenuItem m23 = new JMenuItem("Zoom 4x");
		m2.add(m23);
		m23.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						AffineTransform zoom = new AffineTransform(4, 0, 0, 4, 0, 0);
						SVGViewFrame.this.svgCanvas.setRenderingTransform(zoom);
					}
				}
			);		
		
		JMenuItem m24 = new JMenuItem("Zoom Fit");
		m2.add(m24);
		m24.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Dimension dTotal = SVGViewFrame.this.diagramSize;
						Dimension dVista = SVGViewFrame.this.jsp.getVisibleRect().getSize();
						double escala = Math.min(dVista.getWidth()/dTotal.getWidth(), dVista.getHeight()/dTotal.getHeight() );
						AffineTransform zoom = new AffineTransform();
						zoom.scale(escala, escala);
						SVGViewFrame.this.svgCanvas.setRenderingTransform(zoom);
					}
				}
			);	
		
		m2.addSeparator();
		final JCheckBoxMenuItem m25 = new JCheckBoxMenuItem("Mostrar nombres de reacciones");
		m25.setState( SVGConfig.showReactionNames);
		m25.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {					  
					   SVGConfig.showReactionNames = m25.getState();		   
					   return;
					}						
				}
			);		
		m2.add(m25);
		final JCheckBoxMenuItem m26 = new JCheckBoxMenuItem("Ver Puntos de Debug");
		m26.setState( SVGConfig.debugMode );
		m26.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {					  
					   SVGConfig.debugMode = m26.getState();		   
					   return;
					}						
				}
			);		
		m2.add(m26);
		
		m2.addSeparator();
		JMenuItem m27 = new JMenuItem("Recargar vista");
		m27.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
					   try {							   
						   // (SVGViewFrame.this).ctrl.refreshLayout();
					    	(SVGViewFrame.this).ctrl.refreshView();
					    	(SVGViewFrame.this).refreshView();
					    	
					   } catch (Exception any) {
						   any.printStackTrace();						   
					   }	
					}
				}
			);			
		m2.add(m27);
		
		
		JMenu m3 = new JMenu("Layout");
		m3.setMnemonic('l');
		this.mb.add(m3);
		JMenuItem mi31 = new JMenuItem("Ortogonal");
		mi11.setMnemonic('g');
		m3.add(mi31);
		mi31.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {				   
					   try {
						   SVGConfig.layout_type = Layouter.LAYOUT_ORTHOGONAL ;
						   
						   (SVGViewFrame.this).ctrl.refreshLayout();
						   (SVGViewFrame.this).diagramSize = (SVGViewFrame.this).ctrl.getSVGView().getDimension() ;
					    	
						   (SVGViewFrame.this).svgCanvas = (SVGViewFrame.this).ctrl.getSVGView().getCanvas();
					    	svgCanvas.setDocumentState (JSVGCanvas.ALWAYS_DYNAMIC);

					    	setSVGCanvasListeners();   	
					    	(SVGViewFrame.this).jsp.setViewportView( (SVGViewFrame.this).ctrl.getSVGView().getCanvas() ) ;
					    	jsp.setPreferredSize(svgCanvas.getPreferredSize()); //new Dimension(800, 600));  	
					    	(SVGViewFrame.this).update( (SVGViewFrame.this).getGraphics() );	
					    	(SVGViewFrame.this).refreshView();
					    	
					   } catch (Exception any) {
						   any.printStackTrace();						   
					   }					   
					}					
				}
			);
		JMenuItem mi32 = new JMenuItem("Organic");
		mi11.setMnemonic('g');
		m3.add(mi32);
		mi32.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {				   
					   try {
						   SVGConfig.layout_type = Layouter.LAYOUT_ORGANIC ;
						   (SVGViewFrame.this).ctrl.refreshLayout();
						   (SVGViewFrame.this).diagramSize = (SVGViewFrame.this).ctrl.getSVGView().getDimension() ;
					    	
						   (SVGViewFrame.this).svgCanvas = (SVGViewFrame.this).ctrl.getSVGView().getCanvas();
					    	svgCanvas.setDocumentState (JSVGCanvas.ALWAYS_DYNAMIC);

					    	setSVGCanvasListeners();   	
					    	(SVGViewFrame.this).jsp.setViewportView( (SVGViewFrame.this).ctrl.getSVGView().getCanvas() ) ;
					    	jsp.setPreferredSize(svgCanvas.getPreferredSize()); //new Dimension(800, 600));  	
					    	(SVGViewFrame.this).update( (SVGViewFrame.this).getGraphics() );	
					    	
					    	(SVGViewFrame.this).refreshView();
					   } catch (Exception any) {
						   any.printStackTrace();						   
					   }					   
					}					
				}
			);
		
		JMenu m4 = new JMenu("Estilo");
		m4.setMnemonic('Y');
		this.mb.add(m4);
		final JCheckBoxMenuItem mi41 = new JCheckBoxMenuItem("CellDesigner", !SVGConfig.SBGNMode);
		mi41.setMnemonic('C');
		
		m4.add(mi41);
		final JCheckBoxMenuItem mi42 = new JCheckBoxMenuItem("SBGN", SVGConfig.SBGNMode);		
		mi42.setMnemonic('B');
		
		m4.add(mi42);
		
		mi41.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {		
					   mi42.setState( !mi41.getState() );
					   if ( mi41.getState()  ) SVGConfig.SBGNMode = false;
					   else SVGConfig.SBGNMode = true;
					   return;
					}						
				}
			);

		mi42.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {		
					   mi41.setState( !mi42.getState() );
					   if ( mi41.getState()  ) SVGConfig.SBGNMode = false;
					   else SVGConfig.SBGNMode = true;		   
					   return;
					}						
				}
			);
		
		this.setJMenuBar(this.mb);
	}
    


	public String getPrevPath() {
		return prevPath==null?SVGConfig.DEFAULT_PATH:prevPath;
	}

	public void setPrevPath(String prevPath) {
		this.prevPath = prevPath;
	}
	
	private void setSVGCanvasListeners(){
		
		svgCanvas.addUpdateManagerListener(
				new UpdateManagerListener(){

					@Override
					public void managerResumed(UpdateManagerEvent arg0) {
						jsb.setStatusLabel1("managerResumed...");						
					}

					@Override
					public void managerStarted(UpdateManagerEvent arg0) {
						jsb.setStatusLabel1("managerStarted...");						
					}

					@Override
					public void managerStopped(UpdateManagerEvent arg0) {
						jsb.setStatusLabel1("managerStopped...");						
					}

					@Override
					public void managerSuspended(UpdateManagerEvent arg0) {
						jsb.setStatusLabel1("managerSuspended...");						
					}

					@Override
					public void updateCompleted(UpdateManagerEvent arg0) {						
						jsb.stopProgress();
//						Cursor c = Cursor.WAIT_CURSOR;
				
					}

					@Override
					public void updateFailed(UpdateManagerEvent arg0) {
						jsb.setStatusLabel1("updateFailed..");
						jsb.stopProgress();
					}

					@Override
					public void updateStarted(UpdateManagerEvent arg0) {
						jsb.startProgress();
						jsb.setStatusLabel1("Redrawing...");

					}					
				}
		
		);
		
		
        // Set the JSVGCanvas listeners.
        svgCanvas.addSVGDocumentLoaderListener(new SVGDocumentLoaderAdapter() {
            public void documentLoadingStarted(SVGDocumentLoaderEvent e) {
            	jsb.setStatusLabel1("Document Loading...");
            	jsb.startProgress();
            }
            public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {
            	jsb.setStatusLabel1("Document Loaded.");
            }
        });

        svgCanvas.addGVTTreeBuilderListener(new GVTTreeBuilderAdapter() {
            public void gvtBuildStarted(GVTTreeBuilderEvent e) {
            	jsb.setStatusLabel1("Build Started...");
            }
            public void gvtBuildCompleted(GVTTreeBuilderEvent e) {
            	jsb.setStatusLabel1("Build Done.");                
            }
        });

        svgCanvas.addGVTTreeRendererListener(new GVTTreeRendererAdapter() {
            public void gvtRenderingPrepare(GVTTreeRendererEvent e) {
            	jsb.setStatusLabel1("Rendering Started...");
            	jsb.startProgress();            	
            }
            public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
            	jsb.setStatusLabel1("Rendering Completed...");
            	jsb.stopProgress();
            }
        });
        
    	
    	// Obtain the Window reference when it becomes available
//        svgCanvas.addSVGLoadEventDispatcherListener (
//             new SVGLoadEventDispatcherAdapter () {
//               public void svgLoadEventDispatchStarted (
//                     SVGLoadEventDispatcherEvent e) {
//                window = svgCanvas.getUpdateManager ().
//                     getScriptingEnvironment ().createWindow ();
//                
//                Element root = document.getDocumentElement ();
//                SVGMatrix mtrx = ((SVGLocatable) root).getScreenCTM();
//                System.out.println("" + mtrx.getA() +"-" + mtrx.getB() +"-" + mtrx.getC() +"-" + mtrx.getD() +"-" + mtrx.getE() +"-" + mtrx.getF() );
//                mtrx.setC(2.5f);
//                AffineTransform reset = new AffineTransform(2, 0, 0, 2, 0, 0);
//				svgCanvas.setRenderingTransform(reset);
//				svgCanvas.update(svgCanvas.getGraphics());
//				mtrx = ((SVGLocatable) root).getScreenCTM();
//                System.out.println("" + mtrx.getA() +"-" + mtrx.getB() +"-" + mtrx.getC() +"-" + mtrx.getD() +"-" + mtrx.getE() +"-" + mtrx.getF() );
//				
//              }
//           }
//        );
	}

	public static void main(String[] args) {		
		
//		String varname;
//
//		if (System.getProperty("mrj.version") != null)
//			varname = "DYLD_LIBRARY_PATH"; // We're on a Mac.
//		else
//			varname = "LD_LIBRARY_PATH"; // We're not on a Mac.

		try {
			// 
//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");			
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
//			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
			
			loadSBMLLibrary();
			if (args.length < 1) {
//				sbmlFile = "/media/DATA/_guillermo/desarrollo/PFC/batchTesting/Sbmls/components42.xml";
//			    sbmlFile = "/Users/guille/_guillermo/desarrollo/PFC/batchTesting/Sbmls/Alzheimer_disease_amyloid_secretase_pathway_CD4.xml";
//			    sbmlFile = "/Users/guille/_guillermo/desarrollo/PFC/batchTesting/Sbmls/components42.xml";

			} else
				sbmlFile = args[0];
			
		} catch (ClassNotFoundException e) {
			// Si no se encuentra el tema visual, no se hace nada y se usa el default
		} 
		catch (InstantiationException e) {				
			e.printStackTrace();
		} catch (IllegalAccessException e) {				
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {				
			e.printStackTrace();
		}
		
        try {        				
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                	try {
                		
                		SVGViewFrame svf = new SVGViewFrame(new SVGController( sbmlFile ));	                	
	                	svf.addWindowListener( new WindowListener() {
	
							@Override
							public void windowOpened(WindowEvent e) {
						
							}
	
							@Override
							public void windowClosing(WindowEvent e) {							
							}
	
							@Override
							public void windowClosed(WindowEvent e) {
								System.exit(0);
							}
	
							@Override
							public void windowIconified(WindowEvent e) {
						
							}
	
							@Override
							public void windowDeiconified(WindowEvent e) {

							}
	
							@Override
							public void windowActivated(WindowEvent e) {
						
							}
	
							@Override
							public void windowDeactivated(WindowEvent e) {					
							}
	                		
	                	});
	                	svf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	            		svf.setVisible(true);
            		
            		
                	} catch ( SBML2SVGException ex ) {
                		ex.printStackTrace();
                	}            		

                }
            });            
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
		
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
			
			// System.exit(0);
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
}
