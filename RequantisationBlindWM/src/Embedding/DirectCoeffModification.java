/*
 * DirectCoeffModification.java
 *
 * @author Deepayan Bhowmik
 *
 *  Department of Electronic and Electrical Engineering
 *  University of Sheffield
 *  Copyright reserved.
 *
 */

package Embedding;

// This program actually does embedding using direct coefficient modification method
public class DirectCoeffModification {
    
    public double[][] DirectCoeffModification(double[][] imageCoeff) {
        ReadParameter embedParameter = new ReadParameter();
        DirectCoeffModification embed = new DirectCoeffModification();
        
        // get selected image coefficients to be modified
        double[][] modImageCoeff = imageCoeff;
        int xSub = imageCoeff.length/2; 
        int ySub = imageCoeff[0].length/2;
        int i,j;  
       
        //Declare variables to fit different subbands of interest
        double[][] subbandLL = new double[xSub][ySub];
        double[][] subbandLH = new double[xSub][ySub];
        double[][] subbandHL = new double[xSub][ySub];
        double[][] subbandHH = new double[xSub][ySub];
        
        // Get the subband values accordingly
        for(i=0;i<xSub;i++){
            for(j=0;j<ySub;j++){
                subbandLL[i][j] = imageCoeff[i][j];
                subbandLH[i][j] = imageCoeff[i][j+ySub];
                subbandHL[i][j] = imageCoeff[i+xSub][j];
                subbandHH[i][j] = imageCoeff[i+xSub][j+ySub];
            }
        }
        
        //Calculate the threshold values depending on Adaptive or manual thresholoding mode
        embedParameter.selectThresholdValue(subbandLL,subbandLH,subbandHL,subbandHH);
        // get final threshold values for the subbands 
        double[] threshold = embedParameter.getThresholdValue();
        
        // Get Aplha value
        double[] alphaValue = embedParameter.getAlphaValue();
               
        // Modify the coefficients depending upon the subband choice
        if(embedParameter.getSubbandChoice().compareTo("LF") == 0){
            // Embed in low low subband only
            subbandLL = CoeffMod(subbandLL,threshold[0],embedParameter.getEquationChoice(),alphaValue[0]);
            
        } else if(embedParameter.getSubbandChoice().compareTo("HF") == 0){
            // Embed in High frequency subbands
            subbandLH = CoeffMod(subbandLH,threshold[1],embedParameter.getEquationChoice(),alphaValue[1]);
            subbandHL = CoeffMod(subbandHL,threshold[2],embedParameter.getEquationChoice(),alphaValue[2]);
            subbandHH = CoeffMod(subbandHH,threshold[3],embedParameter.getEquationChoice(),alphaValue[3]);
            
        } else if(embedParameter.getSubbandChoice().compareTo("AF") == 0){
            // Embed in all subband
            subbandLL = embed.CoeffMod(subbandLL,threshold[0],embedParameter.getEquationChoice(),alphaValue[0]);
            subbandLH = embed.CoeffMod(subbandLH,threshold[1],embedParameter.getEquationChoice(),alphaValue[1]);
            subbandHL = embed.CoeffMod(subbandHL,threshold[2],embedParameter.getEquationChoice(),alphaValue[2]);
            subbandHH = embed.CoeffMod(subbandHH,threshold[3],embedParameter.getEquationChoice(),alphaValue[3]);
        }
     
        // get all modified coefficients back in right place
        for(i=0;i<xSub;i++){
            for(j=0;j<ySub;j++){
                modImageCoeff[i][j] = subbandLL[i][j];
                modImageCoeff[i][j+ySub] = subbandLH[i][j];
                modImageCoeff[i+xSub][j] = subbandHL[i][j];
                modImageCoeff[i+xSub][j+ySub] = subbandHH[i][j];
            }
        }
        return modImageCoeff; // Return modified coefficient values
    }
    
    //This program modifies coefficients according to the equation
    public double[][] CoeffMod(double[][] coeff, double threshold, String formulaChoice, double alpha){
        double[][] modCoeff = coeff;
        Watermark watermarkFunc = new Watermark();
        //Get watermark values
        double[] watermark = watermarkFunc.getWatermark();
        int k = 0;
        for(int i=0;i<coeff.length;i++){
            for(int j=0;j<coeff[0].length;j++){
                // Coefficients to modified if they have greater value than the threshold
                if(coeff[i][j] > threshold){
                // Select the right formula
                    if(formulaChoice.compareTo("E1") == 0){
                        //Equation 1 followed
                        modCoeff[i][j] = coeff[i][j] + alpha * coeff[i][j] * (watermark[k++]*0.5 + 0.3);                  
                        // Reapeat the watermark once reached at end
                        if(k>=watermark.length){
                            k = 0;
                        }
                    } else if(formulaChoice.compareTo("E2") == 0){
                        // Use equation 2
                        modCoeff[i][j] = coeff[i][j] + alpha * Math.pow(coeff[i][j],2) * (watermark[k++]*0.5 + 0.3);
                        
                        // Repeat the watermark once reached at end
                        if(k>=watermark.length){
                            k = 0;
                        }
                    } // End of formula choice
                } // End of threshold check
            } // End of inner loop
        } // End of outer loop
        
        return modCoeff; // Return modified coefficient values
    }
    
} // End of the class
