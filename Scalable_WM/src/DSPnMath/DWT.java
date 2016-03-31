/*
 * DWT.java
 *
 * */

package DSPnMath;

/**
 *
 * @author Deepayan Bhowmik
 */
public class DWT {
    
    public static double[][] coefficient;
    
    /**
     * This is to perform forward Discrete Wavelet Transform
     */
    public double[][] dwt(double[][] image, int[] size, int level, String wavelet_name){
        LinearDwt linear = new LinearDwt();
        NonLinearDwt nonLinear = new NonLinearDwt();
        Lifting97 lift = new Lifting97();
        
        //Wavelet transformation have separate module for linear and non linear.
        
        if(wavelet_name.compareTo("MH")==0 || wavelet_name.compareTo("MQ")==0 || wavelet_name.compareTo("2DSAL")==0 || wavelet_name.compareTo("CISL")==0){
            coefficient = nonLinear.NonLinearDwt(image,size,level,wavelet_name);
        } else if(wavelet_name.compareTo("97")==0) {
            coefficient = lift.lifting97FWT(image, size, level);
        } else {
            coefficient = linear.LinearDwt(image,size,level,wavelet_name);
        }
        
        //Perform Forward DWT operation
        return coefficient; 
    }
    
}
