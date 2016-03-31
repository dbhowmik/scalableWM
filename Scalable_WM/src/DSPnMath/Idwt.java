/*
 * Idwt.java
 *
 *
 * @author Deepayan Bhowmik
 * 
 */

package DSPnMath;

public class Idwt {
    public static double[][] reconstructed_image;
    public double[][] Idwt(double[][] coefficient, int[] size, int level, String wavelet_name) {
        LinearIdwt linear = new LinearIdwt();
        NonLinearIdwt nonLinear = new NonLinearIdwt();
        Lifting97 lift = new Lifting97();
        
        //Wavelet transformation have separate module for linear and non linear.
        if(wavelet_name.compareTo("MH")==0 || wavelet_name.compareTo("MQ")==0 || wavelet_name.compareTo("2DSAL")==0 || wavelet_name.compareTo("CISL")==0){
            reconstructed_image = nonLinear.NonLinearIdwt(coefficient,size,level,wavelet_name);
        } else if(wavelet_name.compareTo("97")==0) {
            reconstructed_image = lift.lifting97IWT(coefficient, size, level);
        } else {
            reconstructed_image = linear.LinearIdwt(coefficient,size,level,wavelet_name);
        }
                        
     return reconstructed_image;       
    }
    
}
