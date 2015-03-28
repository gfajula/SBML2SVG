package model.layout;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Date;
import java.util.HashMap;

import model.MCompartment;
import model.Model;
import model.reaction.MModification;
import model.reaction.MReaction;
import model.specie.MAddedSpeciesLink;
import model.specie.MSpecies;
import model.specie.MSpeciesLink;
import model.specie.MSpeciesSimple;
import svgview.SVGOutput;
import svgview.shapes.Segment;
import y.base.DataMap;
import y.base.Edge;
import y.base.Node;
import y.base.YList;
import y.geom.YPoint;
import y.geom.YRectangle;
import y.layout.LayoutGraph;
import y.layout.grouping.Grouping;
import y.layout.organic.OrganicLayouter;
import y.layout.orthogonal.OrthogonalGroupLayouter;
import celldesignerparse_4_0.reaction.EditPoint;

/**
 * Clase que realiza el layout sobre el modelo.
 * El tipo de layout se establece en la clase <code>SVGConfig</code>
 * 
 * @author Guillermo Fajula Leal
 *
 */
public class Layouter {
	
	public static int LAYOUT_ORGANIC = 0;
	public static int LAYOUT_ORTHOGONAL = 1;
	
	public static final int SPECIE_MARGIN = 20;
	public static final int COMPARTMENT_INSET = 50;
	public static final int CANVAS_MARGIN = 20;
	public static final double GRAVITY = 1;
	
	// Lista de Nodos - Species
	private static HashMap <MSpecies, Node> speciesMap;
	private static HashMap <MReaction, LayoutReaction> reactionMap;
	private static HashMap <MCompartment, Node> compartmentMap;
	
	// Para Benchmark
	private static Date d0 = new Date();
	
	/**
	 * Metodo que elimina la informacion auxiliar del layout, para
	 * permitir usarlo de nuevo.
	 */
	public static void cleanLayouter() {
		speciesMap.clear();
		reactionMap.clear();
		compartmentMap.clear();
	}
	
	/**
	 * Aplica un layout organico.
	 * 
	 * @param model <code>Model</code> al que aplicar un layout 'organico'
	 */
	public static void doOrganicLayout( Model model ) {
		model.resetEditPoints();
		model.setTransformEditPoints( false );
		LayoutGraph graph = prepareGraph( model );
		
		OrganicLayouter layouter = new OrganicLayouter();

		layouter.setFinalTemperature( 0.1 );
		
		// La longitud debería depender del numero total de species,
		// para dar el suficiente espacio
//		layouter.setPreferredEdgeLength(25);
//		
		layouter.setGravityFactor( GRAVITY );
		layouter.setMaximumDuration(5000);
		layouter.setIterationFactor( 100000 );
		layouter.setObeyNodeSize( true );
//		layouter.setRepulsion( 1 );
//		layouter.setParallelEdgeLayouterEnabled( true );
		layouter.setActivateTreeBeautifier( true );
//		layouter.setActivateDeterministicMode( false );
		layouter.setSubgraphLayouterEnabled( true );
//		layouter.setPreferredEdgeLength( 200 );
		layouter.setGroupNodePolicy( OrganicLayouter.LAYOUT_GROUPS_POLICY );
		
		layouter.doLayout( graph );

		
		updateModelLayoutInfo( model, graph );
		
		SVGOutput.printStatistic("LayoutType" , "organic");
		SVGOutput.printStatistic( "LayoutTime" , "" + (new Date().getTime() - d0.getTime()) );
		SVGOutput.printStatistic( "FinalTemperature" , ""+layouter.getFinalTemperature() );
		cleanLayouter();
	}
	
	/**
	 * Aplica un layout ortogonal.
	 * 
	 * @param model <code>Model</code> al que aplicar un layout 'ortogonal'
	 */
	public static void doOrthogonalLayout( Model model ) {
		model.resetEditPoints();
		model.setTransformEditPoints( false );
		LayoutGraph graph = prepareGraph( model );
		
		
//		OrthogonalLayouter layouter = new OrthogonalLayouter();
		OrthogonalGroupLayouter layouter = new OrthogonalGroupLayouter();
//		layouter.setMaximumDuration(10000);
//		layouter.setIterationFactor( 10000 );
		
		layouter.setGrid( 10 );
		layouter.setParallelEdgeLayouterEnabled( true );
		layouter.setSubgraphLayouterEnabled( true );
		
		layouter.doLayout( graph );
		
		updateModelLayoutInfo( model, graph );
		
		SVGOutput.printStatistic("LayoutType" , "organic");
		SVGOutput.printStatistic( "LayoutTime" , "" + (new Date().getTime() - d0.getTime()) );
		
		cleanLayouter();		
	}
	
	/**
	 * Prepara el grafo que el <code>Layouter</code> necesita
	 * para aplicar un layout
	 * 
	 * @param model
	 * @return
	 */
	private static LayoutGraph prepareGraph( Model model ) {
		d0 = new Date();		
		y.layout.LayoutGraph graph = new y.layout.DefaultLayoutGraph(); 
		
		
		speciesMap = new HashMap <MSpecies, Node>();
		reactionMap = new HashMap <MReaction, LayoutReaction>();
		compartmentMap = new HashMap <MCompartment, Node>();		
		
		// Crear grafo yFiles
		
		// Compartments
		for (MCompartment mcomp : model.getCompartmentsCollection() ) {
			Node node = graph.createNode();
			
			graph.setSize( node, 100 , 100);
			
			compartmentMap.put( mcomp, node);
			
		}
			
		// Species
		for (MSpeciesSimple sa : model.getSimpleAliasesCollection() ) {
			
			Node node = graph.createNode();
			graph.setLocation( node, sa.getBounds().getCenterX(), sa.getBounds().getCenterY() );
		    graph.setSize( node, sa.getBounds().getWidth()+SPECIE_MARGIN*2 , sa.getBounds().getHeight() +SPECIE_MARGIN*2);
		    
		    speciesMap.put(sa, node);		    
		}		

		for (MSpecies sc : model.getComplexAliasesCollection() ) {			
			Node node = graph.createNode();
			graph.setLocation( node, sc.getBounds().getCenterX(), sc.getBounds().getCenterY() );
		    graph.setSize( node, sc.getBounds().getWidth()+SPECIE_MARGIN*2 , sc.getBounds().getHeight() +SPECIE_MARGIN*2);
		    
		    speciesMap.put(sc, node);		    
		}		
		
		
		
		for (MReaction reac : model.getReactionsCollection() ) {			
			
			MCompartment reacComp = reac.getCommonCompartment();
 			
			LayoutReaction lr = new LayoutReaction();
			lr.mr = reac;
			lr.comp = reacComp;
			
			// Crear puntos intermedios que son necesarios
			
			if ( (reac.getReactants().size() > 1) || (reac.getReactantLinks().size() > 0) ) {
				lr.editPointR = graph.createNode();
			}
			if ( (reac.getProducts().size() > 1) || (reac.getProductLinks().size() > 0) ) {
				lr.editPointP = graph.createNode();
			}
			if ( reac.getModifications().size() > 0 ) {
				lr.center = graph.createNode();
				graph.setSize( lr.center, 9 , 9 );	
			}
			
			
			Node firstPoint; // Punto al que conectan el/los reactivo/s
			if ( lr.editPointR != null ) {
				firstPoint = lr.editPointR;
			} else if ( lr.center != null ) {
				firstPoint = lr.center;
			} else if ( lr.editPointP != null ) {
				firstPoint = lr.editPointP;
			} else {
				// Conexion directa al producto
				firstPoint = speciesMap.get( reac.getProducts().get(0).getMs() );
			}
	
			Node lastPoint; // Punto al que conectan el/los producto/s
			if ( lr.editPointP != null ) {
				lastPoint = lr.editPointP;
			} else if ( lr.center != null ) {
				lastPoint = lr.center;
			} else if ( lr.editPointR != null ) {
				lastPoint = lr.editPointR;
			} else {
				// Conexion directa al reactivo
				lastPoint = speciesMap.get( reac.getReactants().get(0).getMs() );
			}			
			
			// Conectar puntos intermedios en el grafo
			if ( lr.center != null && lr.editPointR!= null && lr.editPointP != null ) {
				lr.epR2center = graph.createEdge( lr.editPointR , lr.center );
				lr.center2epP = graph.createEdge( lr.center , lr.editPointP );
			} else if ( lr.center != null ) {
				if ( lr.editPointR != null ) {
					lr.epR2center = graph.createEdge( lr.editPointR , lr.center );
				} else if ( lr.editPointP != null ) {
					lr.center2epP = graph.createEdge( lr.center , lr.editPointP );
				}
			} else if ( lr.editPointR!= null && lr.editPointP != null ) {
				lr.epR2center = graph.createEdge( lr.editPointR , lr.editPointP );
			}
			
			
			
			lr.reacEdges.put( reac.getReactants().get(0) , 
							  graph.createEdge(					
								speciesMap.get( reac.getReactants().get(0).getMs() ) , 
								firstPoint				
							  )
							);	
			
			// Si solo hay un eje directo reactivo-producto, no hay que añadir ejes de producto
			if ( lr.center != null || lr.editPointR != null || lr.editPointP != null ) {
				lr.prodEdges.put( reac.getProducts().get(0) , 
						  graph.createEdge(		
						    lastPoint,
							speciesMap.get( reac.getProducts().get(0).getMs() ) 
												
						  )
						);				
			}
	
			for ( int i = 1 ; i < reac.getReactants().size() ; i++ ) {
				MSpeciesLink lnk = reac.getReactants().get( i );
				lr.reacEdges.put( 
						lnk,
						graph.createEdge(						
								speciesMap.get( lnk.getMs() ),
								lr.editPointR
							) );
				
			}
			for (MAddedSpeciesLink lnk : reac.getReactantLinks() ) {
				lr.reacEdges.put( 
						lnk,
						graph.createEdge(						
								speciesMap.get( lnk.getMs() ),
								lr.editPointR
							) );
			}				
			
			for ( int i = 1 ; i < reac.getProducts().size() ; i++ ) {
				MSpeciesLink lnk = reac.getProducts().get( i );
				lr.prodEdges.put( 
						lnk,
						graph.createEdge(						
								speciesMap.get( lnk.getMs() ),
								lr.editPointP
							) );
				
			}
			
			for (MAddedSpeciesLink lnk : reac.getProductLinks() ) {
				lr.prodEdges.put( 
						lnk,
						graph.createEdge(
								lr.editPointP,
								speciesMap.get( lnk.getMs() )
							) );
			}				
				
			for (MModification mod : reac.getModifications() ) {
				lr.modEdges.put( mod,
							graph.createEdge(						
							speciesMap.get( mod.getSpecie() ),
							lr.center )
						);
			}
			
			reactionMap.put( reac, lr );
		}
		
		/*
		 * Información adicional para el layout organico jerarquico (por compartments)
		 */
		DataMap parentsIdMap = graph.createNodeMap();
		DataMap nodesIdMap = graph.createNodeMap();
		DataMap groupsIdMap = graph.createNodeMap();
		DataMap insetsMap = graph.createNodeMap();
		
		Insets inset = new Insets( COMPARTMENT_INSET,COMPARTMENT_INSET,COMPARTMENT_INSET,COMPARTMENT_INSET);
		// Compartments = Grupos
		for ( MCompartment comp : compartmentMap.keySet() ) {	
			Node node = compartmentMap.get( comp );
			groupsIdMap.set( node, true );
			nodesIdMap.set( node, node );
			insetsMap.set( node, inset );
			
			Node parentCompartmentNode = compartmentMap.get( comp.getOutsideCompartment() );
			if ( parentCompartmentNode != null ) {
				parentsIdMap.set ( node,  parentCompartmentNode );
			}

		}
		
		for ( MSpecies spc : speciesMap.keySet() ) {
			Node node = speciesMap.get( spc );
			Node compartmentNode = compartmentMap.get( spc.getCompartment() );
			nodesIdMap.set( node, node );
			parentsIdMap.set ( node,  compartmentNode );
		}		
		
		for( LayoutReaction layReac : reactionMap.values() ) {
			if (layReac.editPointR!=null) nodesIdMap.set( layReac.editPointR, layReac.editPointR );
			if (layReac.editPointP!=null) nodesIdMap.set( layReac.editPointP, layReac.editPointP );
			if (layReac.center !=null) {
				nodesIdMap.set( layReac.center, layReac.center );
			}
			
			// Si la reaccion ocurre en un determinado Compartment, asociarla al mismo 
			if ( layReac.comp != null ) {
				Node compartmentNode = compartmentMap.get( layReac.comp );
				if (layReac.editPointR!=null) parentsIdMap.set ( layReac.editPointR,  compartmentNode );
				if (layReac.editPointP!=null) parentsIdMap.set ( layReac.editPointP,  compartmentNode );
				if (layReac.center !=null) parentsIdMap.set ( layReac.center,  compartmentNode );
			} else {
				// Si no, colocarla en el default
				Node compartmentNode = compartmentMap.get( model.getDefaultCompartment() );
				if (layReac.editPointR!=null) parentsIdMap.set ( layReac.editPointR,  compartmentNode );
				if (layReac.editPointP!=null) parentsIdMap.set ( layReac.editPointP,  compartmentNode );
				if (layReac.center !=null) parentsIdMap.set ( layReac.center,  compartmentNode );
			}
			
		}

		
		
		// Asociar DataProviders al grafo antes de hacerle el layout
		graph.addDataProvider( Grouping.GROUP_NODE_INSETS_DPKEY, insetsMap );
		graph.addDataProvider( Grouping.PARENT_NODE_ID_DPKEY, parentsIdMap );
		graph.addDataProvider( Grouping.NODE_ID_DPKEY, nodesIdMap );
		graph.addDataProvider( Grouping.GROUP_DPKEY, groupsIdMap );
		

		return graph;
	}

	/**
	 * Actualiza el <code>Model</code> con la informacion de layout que 
	 * el <code>Layout</code> ha dispuesto en el grafo.
	 * 
	 * @param model
	 * @param graph
	 */
	private static void updateModelLayoutInfo( Model model, LayoutGraph graph ) {
		// Pasar resultado al Model
		
		for ( MCompartment comp : compartmentMap.keySet() ) {			
			Node n = compartmentMap.get( comp );
			YRectangle yRect = graph.getRectangle( n );
			
			// Extraer espacio para el borde del inset
			comp.setBounds( new Rectangle2D.Double (  
					yRect.getX() + COMPARTMENT_INSET/2 , 
					yRect.getY() + COMPARTMENT_INSET/2  , 
					yRect.getWidth() - COMPARTMENT_INSET , 
					yRect.getHeight()  - COMPARTMENT_INSET 			
					) );

		}		
		
	
		for(MSpecies ms: speciesMap.keySet() ) {
			Node n = speciesMap.get(ms);
			ms.setBounds( new Rectangle2D.Double(
					graph.getRectangle(n).x+SPECIE_MARGIN,
					graph.getRectangle(n).y+SPECIE_MARGIN,
					graph.getRectangle(n).width-SPECIE_MARGIN*2,
					graph.getRectangle(n).height-SPECIE_MARGIN*2
					)
			);


		}
		
		
		// Relocalizacion de la reaccion, mediante editpoints
		for (MReaction reac : model.getReactionsCollection() ) {
			
			LayoutReaction lr = reactionMap.get(reac);
			updateReactionLayout( graph, lr );

		}
		
		model.setDiagramSize( 
				new Dimension( graph.getBoundingBox().width + CANVAS_MARGIN,
							   graph.getBoundingBox().height + CANVAS_MARGIN ) );
	}

	/**
	 * Actualiza las reacciones
	 * 
	 * @param graph
	 * @param lr
	 */
	private static void updateReactionLayout( y.layout.LayoutGraph graph, LayoutReaction lr ) {
		
		int numEditPointsToCenter = 0; // EditPoints antes del Centro, para poder calcular el rectangleIndex
		Point2D beforeCenter = new Point2D.Double(); // Mantener rastro del ultimo nodo justo antes del centro. Util para ajustar
		           				      			    // los 2 puntos que envolverán el centro
		Point2D afterCenter = new Point2D.Double();
		
		// Inicialmente el punto anterior al centro es el mismo reactivo
		beforeCenter.setLocation( 
				lr.mr.getReactants().get(0).getMs().getBounds().getCenterX(), 
				lr.mr.getReactants().get(0).getMs().getBounds().getCenterY() );

		// Inicialmente el punto posterior al centro es el mismo producto
		afterCenter.setLocation( 
				lr.mr.getProducts().get(0).getMs().getBounds().getCenterX(), 
				lr.mr.getProducts().get(0).getMs().getBounds().getCenterY() );
		
		
		// Actualizar layout de ramas de reactivos

		MSpeciesLink sLink = lr.mr.getReactants().get(0);
		// Reactivo principal ?
		// ( del nº de EditPoints depende el index de la posicion del rectangulo de la reac. )
		Edge e = lr.reacEdges.get( sLink );
		
		YList editPoints = graph.getPointList( e );
		
		for ( int i = 0; i < editPoints.size() ; i++ ) {
			YPoint p = (YPoint) editPoints.get(i);
			
			lr.mr.getEditPoints().add( 
					new Point2D.Double (
							p.getX(), 
							p.getY()
						)  
				);
			numEditPointsToCenter++;
			beforeCenter.setLocation( p.getX(), p.getY() ); 
		}				
			
		for ( MAddedSpeciesLink aSLink : lr.mr.getReactantLinks() ){
			// Reactivos adicionales	
			e = lr.reacEdges.get( aSLink );
			editPoints = graph.getPointList( e );
			
			for ( int i = 0; i < editPoints.size() ; i++ ) {
				YPoint p = (YPoint) editPoints.get(i);
				
				aSLink.getEditPoints().add( 
						new Point2D.Double (
								p.getX(), 
								p.getY()
							)  
					);
			}
		}			
		
		sLink = lr.mr.getProducts().get(0);
		if ( lr.editPointR != null ) {
			lr.mr.getEditPoints().add( 
					new Point2D.Double(
							graph.getCenterX(lr.editPointR),
							graph.getCenterY(lr.editPointR) 
						)
				);			
			
			for ( MAddedSpeciesLink mas : lr.mr.getReactantLinks() ) {
				// Indicar el editPoint por el que se una a la reaccion
				mas.setJoint( lr.mr.getEditPoints().size() -1 );
			}

			numEditPointsToCenter++;
			beforeCenter.setLocation( graph.getCenterX(lr.editPointR), graph.getCenterY(lr.editPointR) ); 
			
			// Procesar eje entre editPointR y center/editPointP 
			if ( lr.epR2center != null ) {
				editPoints = graph.getPointList( lr.epR2center);
				
				for ( int i = 0; i < editPoints.size() ; i++ ) {
					YPoint p = (YPoint) editPoints.get(i);
					lr.mr.getEditPoints().add( 
							new Point2D.Double (
									p.getX(), 
									p.getY()
								)  
						);
					numEditPointsToCenter++;
					beforeCenter.setLocation( p.getX(), p.getY() ); 
				}
				
//				if ( (editPoints.size() > 0) && lr.center == null ) {
//					// Reducir el indice? 
//				}
			}				
		}
		
		if ( lr.center != null ) {
			Point2D centerPos =  
					new Point2D.Double(
							graph.getCenterX(lr.center),
							graph.getCenterY(lr.center) 
						);
				

			// Buscar afterCenter:
			if ( lr.editPointP != null ) {
				if ( lr.center2epP != null ) {
					editPoints = graph.getPointList( lr.center2epP);
					if ( editPoints.size() >0 ) {
						YPoint p = (YPoint) editPoints.get(0);
						afterCenter.setLocation( p.getX(), p.getY() );
					} else {
						// Si el eje lr.center2epP no tiene editpoints
						// se conecta directamente al editPointP
						afterCenter.setLocation( 
								graph.getCenterX(lr.editPointP),
								graph.getCenterY(lr.editPointP) );
					}
				} else {
					editPoints = graph.getPointList(
								lr.prodEdges.get(lr.mr.getProducts().get(0))
							);
					if ( editPoints.size() >0 ) {
						YPoint p = (YPoint) editPoints.get(0);
						afterCenter.setLocation( p.getX(), p.getY() );
					} else {
						// Si el eje al producto no tiene editpoints
						// se conecta directamente al producto
						// (Ya asignado arriba)
					}
					
				}
			} else {
				// Si no hay editPointP, examinar eje al producto
				editPoints = graph.getPointList(
						lr.prodEdges.get(lr.mr.getProducts().get(0))
					);
				if ( editPoints.size() >0 ) {
					YPoint p = (YPoint) editPoints.get(0);
					afterCenter.setLocation( p.getX(), p.getY() );
				} else {
					// Si el eje al producto no tiene editpoints
					// se conecta directamente al producto
					// (Ya asignado arriba)
				}
			}
			
			double centerMargin = 6 + 6;
			Segment s0 = new Segment( centerPos, beforeCenter ).subSegment( centerMargin );
			Segment s1 = new Segment( centerPos, afterCenter ).subSegment( centerMargin );
			lr.mr.getEditPoints().add( s0.getP2() );
			lr.mr.getEditPoints().add( s1.getP2() );
			
			numEditPointsToCenter++;
			
		} else {
			// no hay punto central (no hay modifications)
			if ( lr.editPointR == null && lr.editPointP == null ) {
				// Hay un eje directo entre editPointR y producto,
				// Colocar el cuadrado en el punto medio de los editpoints:
				// Si el numero de editpoints es impar, se mirará segun
				// cercania a reactivo o producto
				
				
				if ( (lr.mr.getEditPoints().size() % 2 ) == 0 ) {
					numEditPointsToCenter = lr.mr.getEditPoints().size() / 2;
				} else {
					int editPointIndex = lr.mr.getEditPoints().size() / 2 ;
					Point2D p = lr.mr.getEditPoints().get( editPointIndex );
					if (  p.distance( 
							new Point2D.Double(							
									lr.mr.getProducts().get(0).getMs().getBounds().getCenterX() ,
									lr.mr.getProducts().get(0).getMs().getBounds().getCenterX() )   ) 
						   >
						  p.distance( 
							new Point2D.Double(							
									lr.mr.getReactants().get(0).getMs().getBounds().getCenterX() ,
									lr.mr.getReactants().get(0).getMs().getBounds().getCenterX() )   )
							) {
						// El editPoint de enmedio esta mas alejado del producto, asi que
						// ponemos el cuadrado detras de el
						numEditPointsToCenter = editPointIndex + 1;
					} else {
						numEditPointsToCenter = editPointIndex ;
					}
				}
				// retrasar cuadrado si hay editpoints
				//numEditPointsToCenter -= graph.getPointList( lr.epR2center).size() / 2 -1;
			}
		}
		

		lr.mr.setRectangleIndex( numEditPointsToCenter );

		// Procesar eje centro - editPointP
		if ( lr.editPointP != null ) {			
		
			// Procesar eje entre center y editPointP 
			if ( lr.center2epP != null ) {
				editPoints = graph.getPointList( lr.center2epP);
				
				for ( int i = 0; i < editPoints.size() ; i++ ) {
					YPoint p = (YPoint) editPoints.get(i);
					lr.mr.getEditPoints().add( 
							new Point2D.Double (
									p.getX(), 
									p.getY()
								)  
						);
				}
			}				
			
			lr.mr.getEditPoints().add( 
					new Point2D.Double(
							graph.getCenterX(lr.editPointP),
							graph.getCenterY(lr.editPointP) 
						)
				);
			
			// Indicar a los productos extra, de que editPoint salen
			for ( MAddedSpeciesLink mas : lr.mr.getProductLinks() ) {
				mas.setJoint( lr.mr.getEditPoints().size()-1 );
			}
		}		
		
		// Actualizar layout de ramas de productos

		sLink = lr.mr.getProducts().get(0);
		
		// Producto principal 
		// ( del nº de EditPoints depende el index de la posicion del rectangulo de la reac. )
		// Si la reaccion es un eje directo reactivo-producto, esta rama no existe
		if ( lr.center != null || lr.editPointR != null || lr.editPointP != null ) {
			e = lr.prodEdges.get( sLink );
		
			editPoints = graph.getPointList( e );
		
			for ( int i = 0; i < editPoints.size() ; i++ ) {
				YPoint p = (YPoint) editPoints.get(i);
				lr.mr.getEditPoints().add( 
						new Point2D.Double (
								p.getX(), 
								p.getY()
							)  
					);
			}
		}
			
		for ( MAddedSpeciesLink aSLink : lr.mr.getProductLinks() ) {
			// Productos adicionales		
			e = lr.prodEdges.get( aSLink );
			editPoints = graph.getPointList( e );

			for ( int i = 0; i < editPoints.size() ; i++ ) {
				YPoint p = (YPoint) editPoints.get(i);
				
				aSLink.getEditPoints().add( 
						new Point2D.Double (
								p.getX(), 
								p.getY()
							)  
					);
			}		
		}

		
		// Actualizar layout de ramas de modificaciones
		for ( MModification mod : lr.modEdges.keySet() ) {
			
			Edge modEdge = lr.modEdges.get( mod );
			
			YList modEditPoints = graph.getPointList(modEdge);
			
			for ( int i = 0; i < modEditPoints.size() ; i++ ) {
				YPoint p = (YPoint)modEditPoints.get(i);
				mod.addEditPoint ( 
						new EditPoint (
								p.getX(), 
								p.getY()
							)  
					);
			}
			

			
			
		}

	}
	
}