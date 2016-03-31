/*
 * DirectCoeffExtract.java
 *
 * @author Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 */

package extract;
import Embedding.*;

public class DirectCoeffExtract {
    public static double[] countZero, countOne;
     
 // Do watermark extraction for direct coefficient modification   
    public void DirectCoeffExtract(double[][] wmCoeff, double[][] orgCoeff) {
        ReadParameter embedParameter = new ReadParameter();
        DirectCoeffExtract decode = new DirectCoeffExtract();
        Watermark orgWM = new Watermark();
         
        countZero = new double[orgWM.getWatermark().length];
        countOne = new double[orgWM.getWatermark().length];
   
        // get selected image coefficients to be modified
        int xSub = orgCoeff.length/2; 
        int ySub = orgCoeff[0].length/2;
        int i,j;  
       
        //Declare original image variables to fit different subbands of interest
        double[][] orgSubbandLL = new double[xSub][ySub];
        double[][] orgSubbandLH = new double[xSub][ySub];
        double[][] orgSubbandHL = new double[xSub][ySub];
        double[][] orgSubbandHH = new double[xSub][ySub];
        
        // Get the subband values accordingly
        for(i=0;i<xSub;i++){
            for(j=0;j<ySub;j++){
                orgSubbandLL[i][j] = orgCoeff[i][j];
                orgSubbandLH[i][j] = orgCoeff[i][j+ySub];
                orgSubbandHL[i][j] = orgCoeff[i+xSub][j];
                orgSubbandHH[i][j] = orgCoeff[i+xSub][j+ySub];
            }
        }
        
        //Declare watermarked image variables to fit different subbands of interest
        double[][] wmSubbandLL = new double[xSub][ySub];
        double[][] wmSubbandLH = new double[xSub][ySub];
        double[][] wmSubbandHL = new double[xSub][ySub];
        double[][] wmSubbandHH = new double[xSub][ySub];
         
        // Get the subband values for watermarked image accordingly
        for(i=0;i<xSub;i++){
            for(j=0;j<ySub;j++){
                wmSubbandLL[i][j] = wmCoeff[i][j];
                wmSubbandLH[i][j] = wmCoeff[i][j+ySub];
                wmSubbandHL[i][j] = wmCoeff[i+xSub][j];
                wmSubbandHH[i][j] = wmCoeff[i+xSub][j+ySub];
            }
        }
           
        //Calculate the threshold values depending on Adaptive or manual thresholoding mode
        embedParameter.selectThresholdValue(orgSubbandLL,orgSubbandLH,orgSubbandHL,orgSubbandHH);
        // get final threshold values for the subbands 
        double[] threshold = embedParameter.getThresholdValue();
        
        // Get Aplha value
        double[] alphaValue = embedParameter.getAlphaValue();
        
        double[][] tempCollectionWm;
        // Extract watermarks depending upon the subband choice
        if(embedParameter.getSubbandChoice().compareTo("LF") == 0){
            // Embed in low low subband only
            decode.wmExtraction(orgSubbandLL,wmSubbandLL,threshold[0],embedParameter.getEquationChoice(),alphaValue[0]);
         
        } else if(embedParameter.getSubbandChoice().compareTo("HF") == 0){
            // Embed in High frequency subbands
            decode.wmExtraction(orgSubbandLH,wmSubbandLH,threshold[1],embedParameter.getEquationChoice(),alphaValue[1]);         
            decode.wmExtraction(orgSubbandHL,wmSubbandHL,threshold[2],embedParameter.getEquationChoice(),alphaValue[2]);     
            decode.wmExtraction(orgSubbandHH,wmSubbandHH,threshold[3],embedParameter.getEquationChoice(),alphaValue[3]);
   
        } else if(embedParameter.getSubbandChoice().compareTo("AF") == 0){
            // Embed in all subband
            decode.wmExtraction(orgSubbandLL,wmSubbandLL,threshold[0],embedParameter.getEquationChoice(),alphaValue[0]);
            decode.wmExtraction(orgSubbandLH,wmSubbandLH,threshold[1],embedParameter.getEquationChoice(),alphaValue[1]);                
            decode.wmExtraction(orgSubbandHL,wmSubbandHL,threshold[2],embedParameter.getEquationChoice(),alphaValue[2]);        
            decode.wmExtraction(orgSubbandHH,wmSubbandHH,threshold[3],embedParameter.getEquationChoice(),alphaValue[3]);

        }
        
    }
    
    
    //Extracts watermark here
    public void wmExtraction(double[][] orgCoeff, double[][] wmCoeff, double threshold, String formulaChoice, double alpha){
        WatermarkCollection wmCollection = new WatermarkCollection();
        
        double diffCoeff;
        double wmEstimation;
        int i,j, wmValueIndex = 0;
        for(i=0;i<orgCoeff.length;i++){
            for(j=0;j<orgCoeff[0].length;j++){
                //Identify modified coefficients using orginal image coefficients if they have greater value than the threshold
                if(orgCoeff[i][j] > threshold){
                    wmValueIndex++;
                }
            }
        }
        
        double[] wmValue = new double[wmValueIndex];
              
        int wmCount = 0;
        for(i=0;i<orgCoeff.length;i++){
            for(j=0;j<orgCoeff[0].length;j++){
                //Identify modified coefficients using orginal image coefficients if they have greater value than the threshold
                if(orgCoeff[i][j] > threshold){
                    //Get the difference
                    diffCoeff = wmCoeff[i][j] - orgCoeff[i][j];
                   
                // Select the right formula
                    if(formulaChoice.compareTo("E1") == 0){    
                        
                        wmEstimation = diffCoeff / (alpha * orgCoeff[i][j]);                       
                       //Get the right watermark value
                        if(wmEstimation > 0.5){
                            wmValue[wmCount++] = 1.0;
                        } else{
                            wmValue[wmCount++] = 0.0;
                        }
                        
                    } else if(formulaChoice.compareTo("E2") == 0){
                        wmEstimation = diffCoeff / (alpha * Math.pow(orgCoeff[i][j],2));
                       //Get the right watermark value
                        if(wmEstimation > 0.5){
                            wmValue[wmCount++] = 1.0;
                        } else{
                            wmValue[wmCount++] = 0.0;
                        }
                        
                    } // End of formula choice
                } // End of threshold check
            } // End of inner loop
        } // End of outer loop
      
        //Passed watermark values to get the most supported watermark
        double[][] wmOneBlock = wmCollection.WatermarkCollection(wmValue);
        MejorityCount(wmOneBlock);          
    }
    
    
    //Calculate majority count for ones and zeros
     public void MejorityCount(double[][] collectedWM){
         
         for(int i=0; i<collectedWM.length; i++){
             for(int j=0; j<collectedWM[0].length; j++){   
                 
                 if(collectedWM[i][j] == 1.0){
                     countOne[j]++; //Increment '1' count
                 } else if(collectedWM[i][j] == 0.0){
                     countZero[j]++; //Increment '0' count
                 } 
             }
         }   
                            
     }   // End of function
     
     
     public double[] getCountOne(){
         return countOne;
     }

     public double[] getCountZero(){
         return countZero;
     }
       
} //End of the class
