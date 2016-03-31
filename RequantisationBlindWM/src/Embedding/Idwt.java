/*
 * Idwt.java
 *
 *
 * @author Deepayan Bhowmik
 * 
 */

package Embedding;

import java.lang.*;

public class Idwt {
    public static double[][] reconstructed_image;
    public double[][] Idwt(double[][] coefficient, int[] size, int level, String wavelet_name) {
        LinearIdwt linear = new LinearIdwt();
        NonLinearIdwt nonLinear = new NonLinearIdwt();
        
        //Wavelet transformation have separate module for linear and non linear.
        
        if(wavelet_name.compareTo("D4")==0|| wavelet_name.compareTo("HR")==0 || wavelet_name.compareTo("97")==0 || wavelet_name.compareTo("53")==0){
            reconstructed_image = linear.LinearIdwt(coefficient,size,level,wavelet_name);
        } else if(wavelet_name.compareTo("MH")==0 || wavelet_name.compareTo("MQ")==0){
            reconstructed_image = nonLinear.NonLinearIdwt(coefficient,size,level,wavelet_name);
        }
       
     return reconstructed_image;       
    }
    
}
