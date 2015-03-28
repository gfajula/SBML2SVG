package celldesignerparse_4_0;

import java.awt.Font;

/** Clase que define las constantes de los distintos
 * nombres, tipos, valores, etc que usa CellDesigner
 * 
 * @author Guillermo Fajula Leal
 *
 */
public class Constants {
	public static final String PROTEIN = "PROTEIN";
	public static final String SIMPLE_MOLECULE = "SIMPLE_MOLECULE";
	public static final String PROTEIN_GENERIC = "GENERIC";
	public static final String PROTEIN_RECEPTOR = "RECEPTOR";
	public static final String PROTEIN_ION_CHANNEL = "ION_CHANNEL";
	public static final String DEGRADED = "DEGRADED";
	public static final String PHENOTYPE = "PHENOTYPE";
	public static final String GENE = "GENE";
	public static final String RNA = "RNA";
	public static final String ANTISENSE_RNA = "ANTISENSE_RNA";
	public static final String ION = "ION";
	public static final String DRUG = "DRUG";
	public static final double receptorHightFactor = 0.2;
	public static final int roundedRectangleFactor = 10;
	public static final int activationOffset = 4;
	public static final String STATE_TRANSITION = "STATE_TRANSITION";
	public static final double CATALYSIS_RADIUS = 4;
	public static final String TRANSPORT = "TRANSPORT";
	public static final String TRANSCRIPTION = "TRANSCRIPTION";
	public static final String TRANSCRIPTIONAL_ACTIVATION = "TRANSCRIPTIONAL_ACTIVATION";
	public static final String TRANSLATION = "TRANSLATION";
	public static final String KNOWN_TRANSITION_OMITTED = "KNOWN_TRANSITION_OMITTED";
	public static final String UNKNOWN_TRANSITION = "UNKNOWN_TRANSITION";
	public static final String CATALYSIS = "CATALYSIS";
	public static final String INHIBITION = "INHIBITION";
	public static final String RESIDUE_Phosphorylated = "Phosphorylated";
	public static final String RESIDUE_Palmytoylated = "Palmytoylated";
	public static final String RESIDUE_Acetylated = "Acetylated";
	public static final String RESIDUE_Prenylated = "Prenylated";
	public static final String RESIDUE_Ubiqutinated = "Ubiquitinated";
	public static final String RESIDUE_Protonated = "Protonated";
	public static final String RESIDUE_Methylated = "Methylated";
	public static final String RESIDUE_Sulfated = "Sulfated";
	public static final String RESIDUE_Hydroxylated = "Hydroxylated";
	public static final String RESIDUE_empty = "empty";
	public static final String RESIDUE_Glycosylated = "Glycosylated";
	public static final String RESIDUE_DontCare = "Don\'t Care";
	public static final String RESIDUE_Myristoylated = "Myristoylated";
	public static final String RESIDUE_Unknown = "Unknown";
	public static final Font FONT_CELLDESIGNER_BOLD = new Font(null, Font.BOLD, 11);
	public static final Font FONT_CELLDESIGNER_SPECIES = new Font(null, Font.PLAIN, 12);
	public static final Font FONT_CELLDESIGNER_RESIDUES = new Font(null, Font.PLAIN, 10);
	public static final String UNKNOWN = "UNKNOWN";
	public static final String DISSOCIATION = "DISSOCIATION";
	public static final String TRUNCATION = "TRUNCATION";
	public static final String HETERODIMER_ASSOCIATION = "HETERODIMER_ASSOCIATION";
	public static final String TRUNCATED = "TRUNCATED";
	public static final String UNKNOWN_CATALYSIS = "UNKNOWN_CATALYSIS";
	public static final String UNKNOWN_INHIBITION = "UNKNOWN_INHIBITION";
	public static final String TRANSCRIPTIONAL_INHIBITION = "TRANSCRIPTIONAL_INHIBITION";
	public static final String TRANSLATIONAL_ACTIVATION = "TRANSLATIONAL_ACTIVATION";
	public static final String TRANSLATIONAL_INHIBITION = "TRANSLATIONAL_INHIBITION";
	public static final String PHYSICAL_STIMULATION = "PHYSICAL_STIMULATION";
	public static final String NEGATIVE_INFLUENCE = "NEGATIVE_INFLUENCE";
	public static final String UNKNOWN_NEGATIVE_INFLUENCE = "UNKNOWN_NEGATIVE_INFLUENCE";
	public static final String POSITIVE_INFLUENCE = "POSITIVE_INFLUENCE";
	public static final String UNKNOWN_POSITIVE_INFLUENCE = "UNKNOWN_POSITIVE_INFLUENCE";
}
