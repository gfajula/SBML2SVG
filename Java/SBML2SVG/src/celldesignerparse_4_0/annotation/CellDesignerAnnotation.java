package celldesignerparse_4_0.annotation;

import java.awt.Dimension;
import java.util.Vector;

import org.sbml.libsbml.XMLNode;

import celldesignerparse_4_0.annotation.listOfAntiSenseRNAs.AntiSenseRNA;
import celldesignerparse_4_0.annotation.listOfCompartmentAliases.CompartmentAlias;
import celldesignerparse_4_0.annotation.listOfGenes.Gene;
import celldesignerparse_4_0.annotation.listOfIncludedSpecies.Species;
import celldesignerparse_4_0.annotation.listOfLayers.Layer;
import celldesignerparse_4_0.annotation.listOfProteins.Protein;
import celldesignerparse_4_0.annotation.listOfRNAs.RNA;
import celldesignerparse_4_0.annotation.listOfSpeciesAliases.SpeciesAlias;

/**
 * Clase que recopila la informacion añadida por CellDesigner
 * en las 'Annotation' del SBML.
 * (ver documentación de CellDesigner)
 * 
 * @author Guillermo Fajula Leal
 *
 */
public class CellDesignerAnnotation extends XMLNode{
	private String modelVersion;
	private Dimension modelDisplay;
	private Vector<Species> listOfIncludedSpecies;
	private Vector<CompartmentAlias> listOfCompartmentAliases;
	private Vector<SpeciesAlias> listOfComplexSpeciesAliases;
	private Vector<SpeciesAlias> listOfSpeciesAliases;
	private Vector<Protein> listOfProteins;
	private Vector<Gene> listOfGenes;
	private Vector<RNA> listOfRNAs;
	private Vector<AntiSenseRNA> listOfAntiSenseRNAs;
	private Vector<Layer> listOfLayers;
	
	/**
	 * Constructor por defecto, toma el nodo XML y lo parsea
	 * 
	 * @param annotation
	 */
	public CellDesignerAnnotation(XMLNode annotation) {
		super(annotation);
		initializeFields();
	}

	/**
	 * Parsea el nodo <code>annotation</code> y extrae la informacion
	 * extra añadida por CellDesigner
	 */
	private void initializeFields() {
		XMLNode cellDesignerElementsParent;
		if ( this.getChild(0).getName().equals("extension") ) {
			cellDesignerElementsParent = this.getChild(0);
		} else {
			cellDesignerElementsParent = this;
		}
			
			
		for (int i=0;i<cellDesignerElementsParent.getNumChildren();i++){
			if (cellDesignerElementsParent.getChild(i).getName().equalsIgnoreCase("modelVersion") ){
				modelVersion = cellDesignerElementsParent.getChild(i).getChild(0).toString();
			} else if (cellDesignerElementsParent.getChild(i).getName().equalsIgnoreCase("modelDisplay") ){
				modelDisplay = new Dimension(Integer.parseInt(cellDesignerElementsParent.getChild(i).getAttributes().getValue("sizeX")),
						Integer.parseInt(cellDesignerElementsParent.getChild(i).getAttributes().getValue("sizeY")));
			} else if (cellDesignerElementsParent.getChild(i).getName().equalsIgnoreCase("listOfIncludedSpecies") ){
				inicializeListOfIncludedSpecies(cellDesignerElementsParent.getChild(i));
			} else if (cellDesignerElementsParent.getChild(i).getName().equalsIgnoreCase("listOfCompartmentAliases") ){
				inicializeListOfCompartmentAliases(cellDesignerElementsParent.getChild(i));
			} else if (cellDesignerElementsParent.getChild(i).getName().equalsIgnoreCase("listOfComplexSpeciesAliases") ){
				inicializeListOfComplexSpeciesAliases(cellDesignerElementsParent.getChild(i));
			} else if (cellDesignerElementsParent.getChild(i).getName().equalsIgnoreCase("listOfSpeciesAliases") ){
				inicializeListOfSpeciesAliases(cellDesignerElementsParent.getChild(i));
			} else if (cellDesignerElementsParent.getChild(i).getName().equalsIgnoreCase("listOfProteins") ){
				inicializeListOfProteins(cellDesignerElementsParent.getChild(i));
			} else if (cellDesignerElementsParent.getChild(i).getName().equalsIgnoreCase("listOfGenes") ){
				inicializeListOfGenes(cellDesignerElementsParent.getChild(i));
			} else if (cellDesignerElementsParent.getChild(i).getName().equalsIgnoreCase("listOfRNAs") ){
				inicializeListOfRNAs(cellDesignerElementsParent.getChild(i));
			} else if (cellDesignerElementsParent.getChild(i).getName().equalsIgnoreCase("listOfAntisenseRNAs") ){
				inicializeListOfAntisenseRNAs(cellDesignerElementsParent.getChild(i));
			} else if (cellDesignerElementsParent.getChild(i).getName().equalsIgnoreCase("listOfLayers") ){
				inicializeListOfLayers(cellDesignerElementsParent.getChild(i));
			} 
		}
	}

	/**
	 * Parsea el nodo <code>ListOfAntiSenseRNAs</code>
	 * @param node
	 */
	private void inicializeListOfAntisenseRNAs(XMLNode node) {
		listOfAntiSenseRNAs = new Vector<AntiSenseRNA>();
		for (int i=0;i<node.getNumChildren();i++){
			listOfAntiSenseRNAs.addElement(new AntiSenseRNA(node.getChild(i)));
		}
	}
	
	/**
	 * Parsea el nodo <code>ListOfRNAs</code>
	 * @param node
	 */
	private void inicializeListOfRNAs(XMLNode node) {
		listOfRNAs = new Vector<RNA>();
		for (int i=0;i<node.getNumChildren();i++){
			listOfRNAs.addElement(new RNA(node.getChild(i)));
		}
	}

	/**
	 * Parsea el nodo <code>ListOfGenes</code>
	 * @param node
	 */
	private void inicializeListOfGenes(XMLNode node) {
		listOfGenes = new Vector<Gene>();
		for (int i=0;i<node.getNumChildren();i++){
			listOfGenes.addElement(new Gene(node.getChild(i)));
		}
	}

	/**
	 * Parsea el nodo <code></code>
	 * @param node
	 */
	private void inicializeListOfProteins(XMLNode node) {
		listOfProteins = new Vector<Protein>();
		for (int i=0;i<node.getNumChildren();i++){
			listOfProteins.addElement(new Protein(node.getChild(i)));
		}
	}
	/**
	 * Parsea el nodo <code></code>
	 * @param node
	 */
	private void inicializeListOfSpeciesAliases(XMLNode node) {
		listOfSpeciesAliases = new Vector<SpeciesAlias>();
		for (int i=0;i<node.getNumChildren();i++){
			listOfSpeciesAliases.addElement(new SpeciesAlias(node.getChild(i)));
		}
	}

	/**
	 * Parsea el nodo <code></code>
	 * @param node
	 */
	private void inicializeListOfComplexSpeciesAliases(XMLNode node) {
		listOfComplexSpeciesAliases = new Vector<SpeciesAlias>();
		for (int i=0;i<node.getNumChildren();i++){
			listOfComplexSpeciesAliases.addElement(new SpeciesAlias(node.getChild(i)));
		}
	}

	/**
	 * Parsea el nodo <code></code>
	 * @param node
	 */
	private void inicializeListOfCompartmentAliases(XMLNode node) {
		listOfCompartmentAliases = new Vector<CompartmentAlias>();
		for (int i=0;i<node.getNumChildren();i++){
			listOfCompartmentAliases.addElement(new CompartmentAlias(node.getChild(i)));
		}
	}

	/**
	 * Parsea el nodo <code></code>
	 * @param node
	 */
	private void inicializeListOfIncludedSpecies(XMLNode node){
		listOfIncludedSpecies = new Vector<Species>();
		for (int i=0;i<node.getNumChildren();i++){
			listOfIncludedSpecies.addElement(new Species(node.getChild(i)));
		}
	}
	
	/**
	 * Parsea el nodo <code></code>
	 * @param node
	 */
	private void inicializeListOfLayers(XMLNode node) {
		listOfLayers = new Vector<Layer>();
		for (int i=0;i<node.getNumChildren();i++){
			Layer layer = new Layer(node.getChild(i));			
			listOfLayers.addElement( layer );
		}
	}
	
	/**
	 * Obtiene la version del diagrama 
	 * 
	 * @param node
	 */
	public String getVersion(){
		return modelVersion;
	}
	
	/**
	 * Obtiene la dimension del diagrama de CellDesginer
	 * 
	 * @param node
	 */
	public Dimension getDimension(){
		return modelDisplay;
	}
	
	/**
	 * Devuelve la colección de <code>IncludedSpecies</code>
	 * 
	 * @return
	 */
	public Vector<Species> getListOfIncludedSpecies(){
		return listOfIncludedSpecies;
	}
	
	/**
	 * Devuelve la colección de <code>CompartmentAlias</code>
	 * 
	 * @return
	 */
	public Vector<CompartmentAlias> getListOfCompartmentAliases(){
		return listOfCompartmentAliases;
	}
	
	/**
	 * Devuelve la colección de <code>ComplexSpeciesAlias</code>
	 * 
	 * @return
	 */
	public Vector<SpeciesAlias> getListOfComplexSpeciesAliases(){
		return listOfComplexSpeciesAliases;
	}
	
	/**
	 * Devuelve la colección de <code>SimpleSpeciesAlias</code>
	 * 
	 * @return
	 */
	public Vector<SpeciesAlias> getListOfSimpleSpeciesAliases(){
		return listOfSpeciesAliases;
	}
	
	/**
	 * Devuelve la colección de <code>Protein</code>
	 * 
	 * @return
	 */
	public Vector<Protein> getListOfProteins(){
		return listOfProteins;
	}
	
	/**
	 * Devuelve la colección de <code>Gene</code>
	 * 
	 * @return
	 */
	public Vector<Gene> getListOfGenes(){
		return listOfGenes;
	}
	
	/**
	 * Devuelve la colección de <code>RNA</code>
	 * 
	 * @return
	 */
	public Vector<RNA> getListOfRNAs(){
		return listOfRNAs;
	}
	
	/**
	 * Devuelve la colección de <code>AntiSenseRNA</code>
	 * 
	 * @return
	 */
	public Vector<AntiSenseRNA> getListOfAntiSenseRNAs(){
		return listOfAntiSenseRNAs;
	}
	
	/**
	 * Obtiene una cadena con una representacion de las caracteristicas
	 * de este <code>CellDesignerAnnotation</code>
	 * ( Sólo útil en tiempo de desarrollo )
	 * 
	 * @return
	 */
	public String toStringSpecial(){
		String result = "version: "+getVersion()+"\ndimension: "+getDimension()+"\n";
		for (Species s:getListOfIncludedSpecies()){
			result+=s.toString();
		}
		for (CompartmentAlias ca:listOfCompartmentAliases){
			result+=ca.toString();
		}
		for (SpeciesAlias sa : getListOfComplexSpeciesAliases()){
			result+=sa.toString();
		}
		for (SpeciesAlias sa : getListOfSimpleSpeciesAliases()){
			result+=sa.toString();
		}
		for (Protein p : getListOfProteins()){
			result+=p.toString();
		}
		for (Gene e : getListOfGenes()){
			result+=e.toString();
		}
		for (RNA e : getListOfRNAs()){
			result+=e.toString();
		}
		for (AntiSenseRNA e : getListOfAntiSenseRNAs()){
			result+=e.toString();
		}
		return result;
	}

	/**
	 * Obtiene el objeto <code>Protein</code> especificado por el Id 
	 * 
	 * @param id identificador de la proteína
	 * 
	 * @return Objeto <code>Protein</code> o <code>null</code> si no se encontró nada
	 */
	public Protein getProtein(String id) {
		for (Protein p : getListOfProteins()){
			if (p.getId().compareToIgnoreCase(id)==0){
				return p;
			}
		}
		return null;
	}
	
	/**
	 * Obtiene el objeto <code>Protein</code> especificado por el Id 
	 * 
	 * @param id identificador de la proteína
	 * 
	 * @return Objeto <code>Protein</code> o <code>null</code> si no se encontró nada
	 */
	public Gene getGene(String id) {
		for (Gene g : getListOfGenes()){
			if (g.getId().compareToIgnoreCase(id)==0){
				return g;
			}
		}
		return null;
	}
	
	/**
	 * Obtiene el objeto <code>RNA</code> especificado por el Id 
	 * 
	 * @param id identificador de la RNA
	 * 
	 * @return Objeto <code>RNA</code> o <code>null</code> si no se encontró nada
	 */
	public RNA getRNA(String id) {
		for (RNA r : getListOfRNAs()){
			if (r.getId().compareToIgnoreCase(id)==0){
				return r;
			}
		}
		return null;
	}
	
	/**
	 * Obtiene el objeto <code>AntiSenseRNA</code> especificado por el Id 
	 * 
	 * @param id identificador del 'AntiSenseRNA'
	 * 
	 * @return Objeto <code>AntiSenseRNA</code> o <code>null</code> si no se encontró nada
	 */
	public AntiSenseRNA getAntiSenseRNA(String id) {
		for (AntiSenseRNA r : getListOfAntiSenseRNAs()){
			if (r.getId().compareToIgnoreCase(id)==0){
				return r;
			}
		}
		return null;
	}

	/**
	 * Obtiene el objeto <code>Species</code> dentro del grupo listOfIncludedSpecies
	 * con el id especificado
	 * 
	 * @param id identificador del 'Species'
	 * 
	 * @return Objeto <code>Species</code> o <code>null</code> si no se encontró nada
	 */
	public Species getSpeciesIncluded(String id) {
		for (Species s : listOfIncludedSpecies){			
			if (s.getId().compareToIgnoreCase(id)==0){
				return s;
			}
		}
		return null;
	}

	/**
	 * 
	 * @return colección de <code>Layer</code>
	 */
	public Vector<Layer> getListOfLayers() {
		return listOfLayers;
	}
	
}
