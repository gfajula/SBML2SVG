package svgview.species;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import model.specie.MSpecies;

import org.w3c.dom.Document;

import svgcontroller.SBML2SVGException;
import svgview.shapes.SVGShape;

public class SVGHiddenSpecie extends SVGSpecie {
	SVGSpecie parent;
	
	public SVGHiddenSpecie(Document doc, MSpecies ms, SVGSpecie parent){
		super( doc, ms );
		this.parent = parent;
	}
	
	@Override
	protected SVGShape buildSVGShape() throws SBML2SVGException {
		// No se pinta
		return null;
	}

	@Override
	protected SVGShape buildSVGShapeSBGN() throws SBML2SVGException {
		// No se pinta
		return null;
	}

	@Override
	public Rectangle2D getBBox() {
		return parent.getBBox();
	}

	@Override
	public Point2D getCenter() {
		return parent.getCenter();
	}

	@Override
	public Document getDocument() {
		return parent.getDocument();
	}

	@Override
	public Point2D getLinkAnchor(int type) {
		return parent.getLinkAnchor(type);
	}

	@Override
	public SVGShape getSVGShape() throws SBML2SVGException {

		return parent.getSVGShape();
	}

	/*
	 * No necesita pintarse 
	 */
	@Override
	protected Point2D getPointOnRect(Rectangle2D rect, double angle) {		
		return null;
	}
}
