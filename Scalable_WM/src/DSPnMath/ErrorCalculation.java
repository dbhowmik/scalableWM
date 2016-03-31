/*
 * ErrorCalculation.java
 *
 *
 * @author Deepayan Bhowmik
 * 
 */

package DSPnMath;

import DSPnMath.MatrixFunction;
import javax.swing.JOptionPane;


// Calculates RMS error and PSNR
public class ErrorCalculation {
  
    public void Error() {
        /* For testing purpose only
        
         */     
    }
    
    // RMS error calculation 
    public double RmsError(double[][] signal1, double[][] signal2){
        //Storing length of the signals for 2D signals
        int nc_signal11 = signal1.length;
        int nc_signal12 = signal1[0].length;
        int nc_signal21 = signal2.length;
        int nc_signal22 = signal2[0].length;
     
        double rms_error = 10e10; // Initialise a big RMS value
        
        // RMS : Root Mean Square error  
        // Formula used:
        // RMS = Sqrt of {(Signal_1 - Signal_2)^2 / (length of signal)}
        
        // Also check for signal dimension consistency
        if((nc_signal11==nc_signal21) && (nc_signal12==nc_signal22)){
            double diff_sum = 0.0;
            for(int i=0;i<nc_signal11;i++){
                for(int j=0;j<nc_signal12;j++){
                    double temp = signal1[i][j] - signal2[i][j];
                    diff_sum = diff_sum + Math.pow(temp,2);
                } // End of inner loop
            } // End of outer loop
            rms_error = Math.sqrt(diff_sum / (nc_signal11*nc_signal12));
            
        } else{
            JOptionPane.showMessageDialog(null, "Input signal dimensions not consistent.");
            rms_error = 10e5;
        }  
        
        
        
        return rms_error; 
    }
    
    
    // Calculate PSNR value
    public double PSNR(double[][] signal1, double[][] signal2){
        double psnr = -10e4; // Initialise PSNR with very low value
        
        // Formula used:
        // psrn = 20 log10 (max(max(signal1),max(signal2))) / RMS;
        // RMS = Root mean squar error
        double rms_error = RmsError(signal1,signal2);
        MatrixFunction func = new MatrixFunction();
        double max = Math.max(func.Max2D(signal1),func.Max2D(signal2));
        psnr = 20 * Math.log10(max/rms_error);
        return psnr;
    }
    
    // Calculate sum of energy and mean of energy value for selected subband
    
   
    public double SumEnergy(double[][] imCoeff){
        double sumEnrg = 0.0;
        double meanSumEnrg = 0.0;
        for(int i=0; i<imCoeff.length; i++){
            for(int j=0; j<imCoeff[0].length; j++){
                sumEnrg = sumEnrg + Math.pow(imCoeff[i][j],2);
            }
        }
        
        meanSumEnrg = sumEnrg / (imCoeff.length * imCoeff[0].length);
        return sumEnrg;
    }
    
    public double ErrorSum(double[][] signal1, double[][] signal2){
        //Storing length of the signals for 2D signals
        int nc_signal11 = signal1.length;
        int nc_signal12 = signal1[0].length;
        int nc_signal21 = signal2.length;
        int nc_signal22 = signal2[0].length;
     
        double rms_error = 10e10; // Initialise a big RMS value
        
        // RMS : Root Mean Square error  
        // Formula used:
        // RMS = Sqrt of {(Signal_1 - Signal_2)^2 / (length of signal)}
        
        // Also check for signal dimension consistency
        if((nc_signal11==nc_signal21) && (nc_signal12==nc_signal22)){
            double diff_sum = 0.0;
            for(int i=0;i<nc_signal11;i++){
                for(int j=0;j<nc_signal12;j++){
                    double temp = signal1[i][j] - signal2[i][j];
                    diff_sum = diff_sum + Math.pow(temp,2);
                } // End of inner loop
            } // End of outer loop
            rms_error = diff_sum;
            
        } else{
            JOptionPane.showMessageDialog(null, "Input signal dimensions not consistent.");
            rms_error = 10e5;
        }  
        
        
        
        return rms_error; 
    }
  
    public static int dataCapacity;
    public void DataCapacity(int count){
        dataCapacity = dataCapacity + count;                
    }
    
    public int GetDataCapacity(){
        return dataCapacity;
    }
    
    public static int bitErrorRate;
    public void BitErrorRate(int bitCount){
        bitErrorRate = bitErrorRate + bitCount;
    }
    
    public int GetBitErrorRate(){
        return bitErrorRate;
    }
    
    
}
