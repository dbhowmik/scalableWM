/*
 * Convolution.java
 *
 *
 * @author Deepayan Bhowmik
 */

package DSPnMath;

// This part of the program do the convolution operation
// Refer ny convolution book for the formula
public class Convolution {
    double[] signal;
    
    public double[] Convolution(double[] signal1, double[] signal2) {
        
        signal = new double[signal1.length+signal2.length-1];
        for(int i=0; i < (signal1.length+signal2.length-1); i++){    
            for(int j=0; j<signal2.length; j++){
                if((j<=i) && ((i-j)<signal1.length) ){
                    signal[i] = signal[i] + signal1[i-j]*signal2[j];    
                    
                }  //End of if loop                            
            } // End of inner for loop
        } // End of outer for loop
            
        return signal;
    } // End of the function
    
} // End of class
