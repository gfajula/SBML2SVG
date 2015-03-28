package svgview;

public class SVGOutput {
	private static boolean firstJsonvalue = true;
	
	public static void printVerbose(String msg){
		if (SVGConfig.verbose) System.out.println(msg);
	}
	
	public static void printStatistic(String variable, String value) {
		if (!SVGConfig.jsonOutput) {
			System.out.println( variable + ": " + value );
			return;
		}
		if ( !firstJsonvalue ) System.out.print(", "); 
		System.out.println( "\"" + variable + "\": \"" + value + "\"");	
		firstJsonvalue = false;
	}
		
}
