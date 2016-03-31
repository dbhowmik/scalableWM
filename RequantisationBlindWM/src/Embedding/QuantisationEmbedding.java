/*
 * QuantisationEmbedding.java
 *
 * @author Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 */

package Embedding;
import javax.swing.*;

// This program do the quantisation embedding
//  Mainly raster scannig and median filtering procedure followed

public class QuantisationEmbedding {
    static double[][] subbandLL, subbandLH, subbandHL, subbandHH;
    
    
    public double[][] QuantisationEmbedding(double[][] imageCoeff) {
        ReadParameter embedParameter = new ReadParameter();
        QuantisationEmbedding quantisation = new QuantisationEmbedding();
        
        // get selected image coefficients to be modified
        double[][] modImageCoeff = imageCoeff;
        int xSub = imageCoeff.length/2; 
        int ySub = imageCoeff[0].length/2;
        int i,j;  
        
        //Declare variables to fit different subbands of interest
        subbandLL = new double[xSub][ySub];
        subbandLH = new double[xSub][ySub];
        subbandHL = new double[xSub][ySub];
        subbandHH = new double[xSub][ySub];
        
        // Get the subband values accordingly
        for(i=0;i<xSub;i++){
            for(j=0;j<ySub;j++){
                subbandLL[i][j] = imageCoeff[i][j];
                subbandLH[i][j] = imageCoeff[i][j+ySub];
                subbandHL[i][j] = imageCoeff[i+xSub][j];
                subbandHH[i][j] = imageCoeff[i+xSub][j+ySub];
            }
        }
       
        // Place for coefficient modification        
 //       if(embedParameter.getQntChoice().compareTo("RS") == 0){
            // Do raster scanning and follow quantisation procedure
            quantisation.rasterScanning();
            
   //     } else if (embedParameter.getQntChoice().compareTo("MF") == 0){
            // Do Median filtering procedure
    //        quantisation.medianFiltering();
            
    //    } // End of if condition
      
        
        // get all modified coefficients back in right place
        for(i=0;i<xSub;i++){
            for(j=0;j<ySub;j++){
                modImageCoeff[i][j] = quantisation.getSubbandLL()[i][j];
                modImageCoeff[i][j+ySub] = quantisation.getSubbandLH()[i][j];
                modImageCoeff[i+xSub][j] = quantisation.getSubbandHL()[i][j];
                modImageCoeff[i+xSub][j+ySub] = quantisation.getSubbandHH()[i][j];
            }
        }
        return modImageCoeff; // Return modified coefficient values 
    } //End of Embedding func
 
    // Store the subband value for the use of other function
    public double[][] getSubbandLL(){
        return subbandLL;
    }
    
    public double[][] getSubbandLH(){
        return subbandLH;
    }
    
    public double[][] getSubbandHL(){
        return subbandHL;
    }
    
    public double[][] getSubbandHH(){
        return subbandHH;
    }
    
    
    // Raster scanning type quantisation
    public void rasterScanning(){
        ReadParameter embedPara = new ReadParameter();
        QuantisationEmbedding subband = new QuantisationEmbedding();
        int i,j,k,p;
        double[] coeffStream, modCoeffStream;
        //Modification in selected subband only
        if(embedPara.getSubbandChoice().compareTo("LF") == 0){
            //Embed in low low subband only
            //Initialise the coefficient stream to be modified
            coeffStream = new double[subband.getSubbandLL().length * subband.getSubbandLL()[0].length];
            modCoeffStream = coeffStream; 
            k=0;
            for(i=0; i<subband.getSubbandLL().length; i++){
                for(j=0; j<subband.getSubbandLL()[0].length; j++){
                    coeffStream[k++] = subband.getSubbandLL()[i][j];
                }
            }
            
            // Coeff modification
            modCoeffStream = subband.rasterEmbed(coeffStream);
            
            // Write it back in proper order
            k = 0; // Reset k value
            for(i=0; i<subband.getSubbandLL().length; i++){
                for(j=0; j<subband.getSubbandLL()[0].length; j++){
                    subband.getSubbandLL()[i][j] = modCoeffStream[k++];
                }
            }
            
            
        } else if(embedPara.getSubbandChoice().compareTo("HF") == 0){
            //Embed in high frequency subband only
            //Initialise the coefficient stream to be modified
            coeffStream = new double[subband.getSubbandLL().length * subband.getSubbandLL()[0].length * 3];
            modCoeffStream = coeffStream; 
            k=0;
            
            // Make it fir three high frequency subbands
            for(p=0; p<3; p++){
                for(i=0; i<subband.getSubbandLL().length; i++){
                    for(j=0; j<subband.getSubbandLL()[0].length; j++){
                        if(p==0){ //Get LH Subband values
                            coeffStream[k++] = subband.getSubbandLH()[i][j];
                            
                        } else if(p==1){ //Get HL Subband values
                            coeffStream[k++] = subband.getSubbandHL()[i][j];
                            
                        } else if(p==2){ //Get HH Subband values
                            coeffStream[k++] = subband.getSubbandHH()[i][j];
                            
                        } // End of if-else condition                       
                    } // End of inner loop
                } 
            } // End of outer loop
            
            
            // Coeff modification
            modCoeffStream = subband.rasterEmbed(coeffStream);
            
            
            // Write it back in proper order
            k=0; // Reset k value
            //Write back LH Subband
            for(i=0; i<subband.getSubbandLL().length; i++){
                for(j=0; j<subband.getSubbandLL()[0].length; j++){
                    subband.getSubbandLH()[i][j] = modCoeffStream[k++];
                }
            }
            
            //Write back HL Subband
            for(i=0; i<subband.getSubbandLL().length; i++){
                for(j=0; j<subband.getSubbandLL()[0].length; j++){
                    subband.getSubbandHL()[i][j] = modCoeffStream[k++];
                }
            }
            
             //Write back HH Subband
            for(i=0; i<subband.getSubbandLL().length; i++){
                for(j=0; j<subband.getSubbandLL()[0].length; j++){
                    subband.getSubbandHH()[i][j] = modCoeffStream[k++];
                }
            }
            
            
            
        } else if(embedPara.getSubbandChoice().compareTo("AF") == 0){
            //Embed in all subbands
            //Initialise the coefficient stream to be modified
            coeffStream = new double[subband.getSubbandLL().length * subband.getSubbandLL()[0].length * 4];
            modCoeffStream = coeffStream; 
            k=0;
            
            // Make it fir three high frequency subbands
            for(p=0; p<4; p++){
                for(i=0; i<subband.getSubbandLL().length; i++){
                    for(j=0; j<subband.getSubbandLL()[0].length; j++){
                        if(p==0){ //Get LL Subband values
                            coeffStream[k++] = subband.getSubbandLL()[i][j];
                            
                        } else if(p==1){ //Get LH Subband values
                            coeffStream[k++] = subband.getSubbandLH()[i][j];
                            
                        } else if(p==2){ //Get HL Subband values
                            coeffStream[k++] = subband.getSubbandHL()[i][j];
                            
                        } else if(p==3){ //Get HH Subband values
                            coeffStream[k++] = subband.getSubbandHH()[i][j];
                            
                        } // End of if-else condition                        
                    } // End of inner loop
                } 
            } // End of outer loop
            
            
            
            // Coeff modification
            modCoeffStream = subband.rasterEmbed(coeffStream);
            
            
            
            // Write it back in proper order
            k = 0; //Reset k value
            //Write back LL Subband
            for(i=0; i<subband.getSubbandLL().length; i++){
                for(j=0; j<subband.getSubbandLL()[0].length; j++){
                    subband.getSubbandLL()[i][j] = modCoeffStream[k++];
                }
            }
            
            //Write back LH Subband
            for(i=0; i<subband.getSubbandLL().length; i++){
                for(j=0; j<subband.getSubbandLL()[0].length; j++){
                    subband.getSubbandLH()[i][j] = modCoeffStream[k++];
                }
            }
            
             //Write back HL Subband
            for(i=0; i<subband.getSubbandLL().length; i++){
                for(j=0; j<subband.getSubbandLL()[0].length; j++){
                    subband.getSubbandHL()[i][j] = modCoeffStream[k++];
                }
            }
            
             //Write back HH Subband
            for(i=0; i<subband.getSubbandLL().length; i++){
                for(j=0; j<subband.getSubbandLL()[0].length; j++){
                    subband.getSubbandHH()[i][j] = modCoeffStream[k++];
                }
            }
            
        } // End subband choice if-else condition
        
    }
    
    
    // Finding median and then quantise
    public double[] rasterEmbed(double[] coeff){
        MatrixFunction matFunc = new MatrixFunction();
        Watermark watermark = new Watermark();
        ReadParameter param = new ReadParameter();
        
        double[] modCoeff = coeff;
        //Define the windo variable
        double[] window = new double[3];
        int midIndex = 0;
        
        int wmIndex = 0; // Watermark index
        for(int i=0; i<coeff.length; i++){
            if(((i+1)%3) == 0){ 
                window[0] = coeff[i-2];
                window[1] = coeff[i-1];
                window[2] = coeff[i];
                
                // Find max and min of the window
                double maxWindow = matFunc.Max1D(window);
                double minWindow = matFunc.Min1D(window);
                
                //Calculating quantisation step delta
                // Formula: Delta = alpha * (|b1|+|b2|) / 2; and alpha is a tuning parameter.
                double delta = param.getQntAlphaRS() * (Math.abs(minWindow) + Math.abs(maxWindow)) / 2 ; 
                
                //Calculate quantisation step values
                double[] qntStep = new double[(int)Math.round((maxWindow-minWindow)/delta) + 2];
                              
                int qntIndex = 0; //Quantisation index
                qntStep[qntIndex] = minWindow;
                while(qntStep[qntIndex]<maxWindow){
                    qntIndex++;
                    qntStep[qntIndex] = qntStep[qntIndex - 1] + delta;
                } 
                
                midIndex = 1;
                //Find the index value of the median in the scanning window
                for(int a=0;a<3;a++){
                    if((window[a]>minWindow) && (window[a]<maxWindow)){
                        midIndex = a;
                    } //End of if condition                   
                } // End of for loop
                 
                int tempIndex = 1;
                // Find the median position in steps
                for(int b=0;b<=qntIndex;b++){
                    if((window[midIndex]>=qntStep[b]) && (window[midIndex]<=qntStep[b+1])){
                        tempIndex = b+1;
                    }
                } //End of for loop
                
              // Refer paper by Liehua Xie:
                // "Joint wavelet compression and authentication watermarking"
                //Modify the coefficient according to watermark value               
                switch((int)watermark.getWatermark()[wmIndex++]){
                    case 1: // Case 1: Watermark value = 1 
                        if((tempIndex%2) == 1){
                            window[midIndex] = qntStep[tempIndex];
                        } else{
                            window[midIndex] = qntStep[tempIndex - 1];
                        }                          
                        break;
                        
                    case 0: // Case 2: Watermark value = 0
                        if((tempIndex%2) == 0){
                            window[midIndex] = qntStep[tempIndex];
                        } else{
                            window[midIndex] = qntStep[tempIndex - 1];
                        }       
                        break;
                } //End of switch statement
                    
                
                if(wmIndex>=watermark.getWatermark().length){
                    // Reset k once watermark idex reach to end. Continue watermark from start index of the watermark
                    wmIndex = 0; 
                }
                
                //Modify the actual value now
                modCoeff[i-2] = window[0];
                modCoeff[i-1] = window[1];
                modCoeff[i] = window[2];
                
            } // End of if condition
        } // End of for loop
             
        return modCoeff;
    }
    
    
    // Median filtering quantisation  
    public void medianFiltering(){
        MatrixFunction matFunc = new MatrixFunction();
        ReadParameter param = new ReadParameter();
        QuantisationEmbedding subband = new QuantisationEmbedding();
        Watermark watermark = new Watermark();
        
        //Define the windo variable
        double[] window = new double[3];
        int midIndex = 0;       
        int wmIndex = 0; // Watermark index
        
             
        //Check for right subband choice for this method
        if(param.getSubbandChoice().compareTo("HF") == 0){
            //This method is applicable only for high frequency subbands
            
            for(int i=0; i<subband.getSubbandHH().length; i++){
                for(int j=0; j<subband.getSubbandHH()[0].length; j++){                 
                    window[0] = subband.getSubbandLH()[i][j];
                    window[1] = subband.getSubbandHL()[i][j];
                    window[2] = subband.getSubbandHH()[i][j];
                
                    // Find max and min of the window
                    double maxWindow = matFunc.Max1D(window);
                    double minWindow = matFunc.Min1D(window);
               
                    //Calculating quantisation step delta
                    // Formula: Delta = alpha * (b1 - b2) / (2Q-1); and alpha is a tuning parameter & Q = 3 normally
                    double delta = (maxWindow - minWindow) / (2*param.getQntQuantMF() - 1) ; 
                
                    //Calculate quantisation step values
                    double[] qntStep = new double[(int)Math.round((maxWindow-minWindow)/delta) + 2];
                              
                    int qntIndex = 0; //Quantisation index
                    qntStep[qntIndex] = minWindow;
                    while(qntStep[qntIndex]<maxWindow){
                        qntIndex++;
                        qntStep[qntIndex] = qntStep[qntIndex - 1] + delta;
                    } 
                
                    midIndex = 1;
                    //Find the index value of the median in the scanning window
                    for(int a=0;a<3;a++){
                        if((window[a]>minWindow) && (window[a]<maxWindow)){
                            midIndex = a;
                        } //End of if condition                   
                    } // End of for loop
                 
                    int tempIndex = 1;
                    // Find the median position in steps
                    for(int b=0;b<=qntIndex;b++){
                        if((window[midIndex]>=qntStep[b]) && (window[midIndex]<=qntStep[b+1])){
                            tempIndex = b+1;
                        }
                    } //End of for loop
                
                // Refer PhD thesis by Deepa Kundur:
                    // "Multiresolution digital watermarking: Algorithms and Implecations for multimedia signals"  
                    //Modify the coefficient according to watermark value               
                    switch((int)watermark.getWatermark()[wmIndex++]){
                        case 1: // Case 1: Watermark value = 1 
                            if((tempIndex%2) == 1){
                                window[midIndex] = qntStep[tempIndex];
                            } else{
                                window[midIndex] = qntStep[tempIndex - 1];
                            }                          
                            break;
                        
                        case 0: // Case 2: Watermark value = 0
                            if((tempIndex%2) == 0){
                                window[midIndex] = qntStep[tempIndex];
                            } else{
                                window[midIndex] = qntStep[tempIndex - 1];
                            }       
                            break;
                    } //End of switch statement
                    
                
                    if(wmIndex>=watermark.getWatermark().length){
                        // Reset k once watermark idex reach to end. Continue watermark from start index of the watermark
                        wmIndex = 0; 
                    }
                
                    //Modify the actual value now
                    subband.getSubbandLH()[i][j] = window[0];
                    subband.getSubbandHL()[i][j] = window[1];
                    subband.getSubbandHH()[i][j] = window[2];
                                    
                } // End of inner for loop                
            } // End of outer for loop
     
        } else{
            JOptionPane.showMessageDialog(null, "Wrong subband selection" +
                    "\nQuantisation using median filtering only accepts" +
                    "\n 'HF' subband", "ERROR", JOptionPane.ERROR_MESSAGE);
            //Exit
            System.exit(-1);
        } // End of if-else codition 
    }
    
    
    
} // End of the class
