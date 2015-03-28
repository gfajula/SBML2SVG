package celldesignerparse_4_0.commondata;

import org.sbml.libsbml.XMLNode;

public class Region {
	public static final int MODIFICATION_SITE = 0;
	public static final int CODING_REGION = 1;
	public static final int REGULATORY_REGION = 2;
	public static final int TRANSCRIPTION_STARTING_SITE_RIGHT = 3;
	public static final int TRANSCRIPTION_STARTING_SITE_LEFT = 4;
	public static final int PROTEIN_BINDING_DOMAIN = 5;
	private String id, name;
	private int type;
	private double size, pos;
	private boolean active;
	
	public Region(XMLNode node) {
		super();
		
		id = node.getAttributes().getValue("id");
		name = node.getAttributes().getValue("name");
		size = Double.parseDouble(node.getAttributes().getValue("size"));
		pos = Double.parseDouble(node.getAttributes().getValue("pos"));
		active = node.getAttributes().getValue("active").equals("true");
		String typeAtt = node.getAttributes().getValue("type");
		if (typeAtt.equals("Modification Site")) {
			type = MODIFICATION_SITE;
		} else if (typeAtt.equals("CodingRegion")) {
			type = CODING_REGION;
		} else if (typeAtt.equals("RegulatoryRegion")) {
			type = REGULATORY_REGION;
		} else if (typeAtt.equals("transcriptionStartingSiteR")) {
			type = TRANSCRIPTION_STARTING_SITE_RIGHT;
		} else if (typeAtt.equals("transcriptionStartingSiteL")) {
			type = TRANSCRIPTION_STARTING_SITE_LEFT;
		} else if (typeAtt.equals("proteinBindingDomain")) {
			type = PROTEIN_BINDING_DOMAIN;
		}

		
		
	}

	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public int getType() {
		return type;
	}

	public double getSize() {
		return size;
	}

	public double getPos() {
		return pos;
	}

	public boolean isActive() {
		return active;
	}
	
	
}
