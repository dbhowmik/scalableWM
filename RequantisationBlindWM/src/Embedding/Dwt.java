/*
 * Dwt.java
 *
 *
 */

package Embedding;

/**
 *
 * @author Deepayan Bhowmik
 */
public class Dwt {
    
    public static double[][] coefficient;
    
    /**
     * This is to perform forward Discrete Wavelet Transform
     */
    public double[][] dwt(double[][] image, int[] size, int level, String wavelet_name){
        LinearDwt linear = new LinearDwt();
        NonLinearDwt nonLinear = new NonLinearDwt();
        
        //Wavelet transformation have separate module for linear and non linear.
        
        if(wavelet_name.compareTo("D4")==0|| wavelet_name.compareTo("HR")==0 || wavelet_name.compareTo("97")==0 || wavelet_name.compareTo("53")==0){
            coefficient = linear.LinearDwt(image,size,level,wavelet_name);
        } else if(wavelet_name.compareTo("MH")==0 || wavelet_name.compareTo("MQ")==0){
            coefficient = nonLinear.NonLinearDwt(image,size,level,wavelet_name);
        }
        
   
        //Perform Forward DWT operation
        return coefficient; 
    }
    
}
