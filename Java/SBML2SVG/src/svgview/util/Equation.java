package svgview.util;

public class Equation {
	
   public static double[] quadratic(double a, double b, double c) {
	   if (a==0) {
		   if (b==0) {
			   new Exception("Coeficientes no válidos en la ecuación.").printStackTrace();
			   return new double[]{}; 
		   } else {
			   // única solución a ec. de primer grado
			   return new double[]{ c/b };
		   }		   
	   }
	   
	   double delta = b*b - 4*a*c;
	   
//	   System.err.println(new double[]{}.length);
	   
	   if (delta<0) {
		   // No tiene soluciones reales
		   return new double[]{}; 
	   } else if (delta==0) {
		   // Una única solución
		   return new double[]{ -b / (2*a) };
	   } else {
		   // dos soluciones
		   double x1 = (-b + Math.sqrt(delta) ) / (2*a) ;
		   double x2 = (-b - Math.sqrt(delta) ) / (2*a) ;
		   return new double[] { x1, x2 };
	   }
	   
   }
    
   
 /**
 * @param args
 */
public static void main(String[] args) {
	   System.out.println( "4x^2 + 15x + 8 :: " + quadratic(4,15,8)[0] + ";"+ quadratic(4,15,8)[1] );
	   System.out.println( " 15x + 8 :: " + quadratic(0,15,8)[0] + ";" );
   }
}
