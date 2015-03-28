package svgview.reactions;

import svgview.SVGPaintable;

public interface SVGReaction extends SVGPaintable {

	public String getType();
	public String getDashArray();
}
