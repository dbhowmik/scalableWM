/*
 * Decoding.java
 *
 * @author Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 */

package extract;

import Embedding.*;


public class Decoding {
    // This code route the extraction procedure depending on the embedding method
    //  Read parameter data and then decide
    
    public static double[] extractedWatermark;  
      
    public void Decoding(double[][] wmImageCoeff, int[] wmSize, int level) {
        //Passing parameters are watermarked image coefficients after DWT,
        //  size of the image and no of decomposition level
        ReadParameter parameter = new ReadParameter();
        Dwt wavelet = new Dwt();
        Imread image = new Imread();
//        DirectCoeffExtract direct = new DirectCoeffExtract();
        QuantisationExtraction quant = new QuantisationExtraction();
        Decoding decode = new Decoding();
        
        
                
        //Getting all regions of interest i.e. selected subbands for embedding
        int xROI = wmSize[1]/(int)(Math.pow(2,level-1));
        int yROI = wmSize[0]/(int)(Math.pow(2,level-1));
        double[][] wmCoeffROI = new double[xROI][yROI]; // Selected subband coefficients
        double[][] orgROI = new double[xROI][yROI]; // Be ready for original image coeffs (in case of non-blind)
    
        // Get it separated
        for(int i=0; i<xROI; i++){
            for(int j=0; j<yROI; j++){
                wmCoeffROI[i][j] = wmImageCoeff[i][j]; 
            }
        }    
              
        
            // A blind type detection
            //Call the quantisation extraction procedure            
        quant.QuantisationExtraction(wmCoeffROI);
        
        //Extract watermark to be compared
        decode.MejorityVote();
    }
    
    //Calculate extratcted watermark based on majority voting rule
     public void MejorityVote(){
         QuantisationExtraction qntExt = new QuantisationExtraction();
         Watermark orgWater = new Watermark();
         ReadParameter param = new ReadParameter();
         
         double[] countZero = new double[orgWater.getWatermark().length]; 
         double[] countOne = new double[orgWater.getWatermark().length];
         
         
         countZero = qntExt.getCountZero();
         countOne = qntExt.getCountOne();
         
     
         //Initialised the extracted watermark 
         extractedWatermark = new double[orgWater.getWatermark().length];
     
        //Set the watermark to be compared with original based on majority decision
             
        for(int a=0; a<countOne.length; a++){
             if(countZero[a] > countOne[a]){
                 extractedWatermark[a] = 0;
                 
             } else if(countZero[a] < countOne[a]){
                 extractedWatermark[a] = 1;
                 
             } else if(countZero[a] == countOne[a]){
                 if(countZero[a] != 0) {
                     // In case of tie ... take a random value
                     extractedWatermark[a] = (double)Math.round(Math.random());
                     
                 } else{ // In case no instance of 1 / 0, full watermark not been used and act aacordingly
                     extractedWatermark[a] = -1.0;
                 } // End of if-else
             }// End of if-else condition
        } // End of for loop
            
     }  // End of function
     
     public double[] getExtractedWatermark2Compare(){
         return extractedWatermark;
     }
    
} //End of the class
