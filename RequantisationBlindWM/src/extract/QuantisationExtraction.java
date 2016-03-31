/*
 * QuantisationExtraction.java
 *
 * @author Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 */

package extract;
import Embedding.*;
import javax.swing.*;


public class QuantisationExtraction {
    public static double[] countZero, countOne;
    static double[][] wmSubbandLL, wmSubbandLH, wmSubbandHL, wmSubbandHH;
    
    // Do watermark extraction for quantisation coefficient modification
    public void QuantisationExtraction(double[][] wmCoeff) {
        
        ReadParameter embedParameter = new ReadParameter();
        QuantisationExtraction quantisation = new QuantisationExtraction();
        Watermark orgWM = new Watermark();
        
        countZero = new double[orgWM.getWatermark().length];
        countOne = new double[orgWM.getWatermark().length];
        
        // get selected image coefficients to be modified
        int xSub = wmCoeff.length/2; 
        int ySub = wmCoeff[0].length/2;
        int i,j;  
        
        //Declare watermarked image variables to fit different subbands of interest
        wmSubbandLL = new double[xSub][ySub];
        wmSubbandLH = new double[xSub][ySub];
        wmSubbandHL = new double[xSub][ySub];
        wmSubbandHH = new double[xSub][ySub];
         
        // Get the subband values for watermarked image accordingly
        for(i=0;i<xSub;i++){
            for(j=0;j<ySub;j++){
                wmSubbandLL[i][j] = wmCoeff[i][j];
                wmSubbandLH[i][j] = wmCoeff[i][j+ySub];
                wmSubbandHL[i][j] = wmCoeff[i+xSub][j];
                wmSubbandHH[i][j] = wmCoeff[i+xSub][j+ySub];
            }
        }
        
        // Place for watermark extraction from modified coefficients        
        if(embedParameter.getQntChoice().compareTo("RS") == 0){
            // Do raster scanning and extract watermark
            quantisation.rasterScanExt();
            
        } else if (embedParameter.getQntChoice().compareTo("MF") == 0){
            // Extract watermark using Median filtering procedure
            quantisation.medianFiltExt();
            
        } // End of if condition
        
    } // End of the function
    
    // Store the subband value for the use of other function
    public double[][] getSubbandLL(){
        return wmSubbandLL;
    }
    
    public double[][] getSubbandLH(){
        return wmSubbandLH;
    }
    
    public double[][] getSubbandHL(){
        return wmSubbandHL;
    }
    
    public double[][] getSubbandHH(){
        return wmSubbandHH;
    }
    
    
    // Raster scanning type watermark extraction
    public void rasterScanExt(){
        ReadParameter embedPara = new ReadParameter();
        QuantisationExtraction subband = new QuantisationExtraction();
        int i,j,k,p;
        double[] modCoeffStream;
        //Modification in selected subband only
        if(embedPara.getSubbandChoice().compareTo("LF") == 0){
            //Embed in low low subband only
            //Initialise the coefficient stream to be modified
            modCoeffStream = new double[subband.getSubbandLL().length * subband.getSubbandLL()[0].length]; 
            k=0;
            for(i=0; i<subband.getSubbandLL().length; i++){
                for(j=0; j<subband.getSubbandLL()[0].length; j++){
                    modCoeffStream[k++] = subband.getSubbandLL()[i][j];
                }
            }
            
            // Extract watermark
            subband.rasterExtract(modCoeffStream);
            
        } else if(embedPara.getSubbandChoice().compareTo("HF") == 0){
            //Embed in high frequency subband only
            //Initialise the coefficient stream to be modified
            modCoeffStream = new double[subband.getSubbandLL().length * subband.getSubbandLL()[0].length * 3]; 
            k=0;
            
            // Make it for three high frequency subbands
            for(p=0; p<3; p++){
                for(i=0; i<subband.getSubbandLL().length; i++){
                    for(j=0; j<subband.getSubbandLL()[0].length; j++){
                        if(p==0){ //Get LH Subband values
                            modCoeffStream[k++] = subband.getSubbandLH()[i][j];
                            
                        } else if(p==1){ //Get HL Subband values
                            modCoeffStream[k++] = subband.getSubbandHL()[i][j];
                            
                        } else if(p==2){ //Get HH Subband values
                            modCoeffStream[k++] = subband.getSubbandHH()[i][j];
                            
                        } // End of if-else condition                       
                    } // End of inner loop
                } 
            } // End of outer loop
            
            
            // Extract watermark
            subband.rasterExtract(modCoeffStream);
            
        } else if(embedPara.getSubbandChoice().compareTo("AF") == 0){
            //Embed in all subbands
            //Initialise the coefficient stream to be modified
            modCoeffStream = new double[subband.getSubbandLL().length * subband.getSubbandLL()[0].length * 4];
            k=0;
            
            // Make it for all frequency subbands
            for(p=0; p<4; p++){
                for(i=0; i<subband.getSubbandLL().length; i++){
                    for(j=0; j<subband.getSubbandLL()[0].length; j++){
                        if(p==0){ //Get LL Subband values
                            modCoeffStream[k++] = subband.getSubbandLL()[i][j];
                            
                        } else if(p==1){ //Get LH Subband values
                            modCoeffStream[k++] = subband.getSubbandLH()[i][j];
                            
                        } else if(p==2){ //Get HL Subband values
                            modCoeffStream[k++] = subband.getSubbandHL()[i][j];
                            
                        } else if(p==3){ //Get HH Subband values
                            modCoeffStream[k++] = subband.getSubbandHH()[i][j];
                            
                        } // End of if-else condition                        
                    } // End of inner loop
                } 
            } // End of outer loop
                
            // Extract watermark
            subband.rasterExtract(modCoeffStream);
            
        } // End subband choice if-else condition

    }
    
    //Raster scanning original extraction starts here
    public void rasterExtract(double[] modCoeff){
        MatrixFunction matFunc = new MatrixFunction();
        Watermark watermark = new Watermark();
        ReadParameter param = new ReadParameter();
        WatermarkCollection wmCollection = new WatermarkCollection();
               
        //Define the window variable
        double[] window = new double[3];
        int midIndex = 0;
        
        double[] wmValue = new double[(int)Math.floor(modCoeff.length/3.0)]; // Intialise watermark to be extracted
        int wmCount = 0;
        
        int wmIndex = 0; // Watermark index
        for(int i=0; i<modCoeff.length; i++){
            if(((i+1)%3) == 0){ 
                window[0] = modCoeff[i-2];
                window[1] = modCoeff[i-1];
                window[2] = modCoeff[i];
                
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
                for(int b=0;b<qntIndex;b++){
                    if((window[midIndex]>=qntStep[b]) && (window[midIndex]<=qntStep[b+1])){
                        tempIndex = b+1;
                    }
                } //End of for loop
                
                
              // Refer paper by Liehua Xie:
                // "Joint wavelet compression and authentication watermarking"
                //Modify the coefficient according to watermark value               
                switch(tempIndex%2){
                    case 1: // Case 1: index value is odd 
                             
                        if(Math.abs(qntStep[tempIndex] - window[midIndex]) < Math.abs(qntStep[tempIndex - 1] - window[midIndex])){
                            wmValue[wmCount++] = 1;
                        } else if(Math.abs(qntStep[tempIndex] - window[midIndex]) >= Math.abs(qntStep[tempIndex - 1] - window[midIndex])){
                            wmValue[wmCount++] = 0;
                        }
                        
                        break;
                        
                    case 0: // Case 2: Index value is even
                        if(Math.abs(qntStep[tempIndex] - window[midIndex]) < Math.abs(qntStep[tempIndex - 1] - window[midIndex])){
                            wmValue[wmCount++] = 0;
                        } else if(Math.abs(qntStep[tempIndex] - window[midIndex]) >= Math.abs(qntStep[tempIndex - 1] - window[midIndex])){
                            wmValue[wmCount++] = 1;
                        }      
                        break;
                } //End of switch statement
                     
                               
            } // End of if condition
        } // End of for loop
        
        //Passed watermark values to get the most supported watermark
        double[][] wmOneBlock = wmCollection.WatermarkCollection(wmValue);
        MejorityCount(wmOneBlock);    
        
        
    }
    
    
    //Watermark extraction based on median filtering
    public void medianFiltExt(){
        MatrixFunction matFunc = new MatrixFunction();
        Watermark watermark = new Watermark();
        ReadParameter param = new ReadParameter();
        WatermarkCollection wmCollection = new WatermarkCollection();
        QuantisationExtraction subband = new QuantisationExtraction();
        
        
        //Define the windo variable
        double[] window = new double[3];
        int midIndex = 0;       
        int wmIndex = 0; // Watermark index
        
        double[] wmValue = new double[subband.getSubbandHH().length*subband.getSubbandHH()[0].length]; // Intialise watermark to be extracted
        int wmCount = 0;
        
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
                    for(int b=0;b<qntIndex;b++){
                        if((window[midIndex]>=qntStep[b]) && (window[midIndex]<=qntStep[b+1])){
                            tempIndex = b+1;
                        }
                    } //End of for loop
                
                // Refer PhD thesis by Deepa Kundur:
                    // "Multiresolution digital watermarking: Algorithms and Implecations for multimedia signals"  
                    //Modify the coefficient according to watermark value               
                    switch(tempIndex%2){
                    case 1: // Case 1: index value is odd 
                             
                        if(Math.abs(qntStep[tempIndex] - window[midIndex]) < Math.abs(qntStep[tempIndex - 1] - window[midIndex])){
                            wmValue[wmCount++] = 1;
                        } else if(Math.abs(qntStep[tempIndex] - window[midIndex]) >= Math.abs(qntStep[tempIndex - 1] - window[midIndex])){
                            wmValue[wmCount++] = 0;
                        }
                        
                        break;
                        
                    case 0: // Case 2: Index value is even
                        if(Math.abs(qntStep[tempIndex] - window[midIndex]) < Math.abs(qntStep[tempIndex - 1] - window[midIndex])){
                            wmValue[wmCount++] = 0;
                        } else if(Math.abs(qntStep[tempIndex] - window[midIndex]) >= Math.abs(qntStep[tempIndex - 1] - window[midIndex])){
                            wmValue[wmCount++] = 1;
                        }      
                        break;
                } //End of switch statement
                    
                              
                } // End of inner for loop                
            } // End of outer for loop
     
        } else{
            JOptionPane.showMessageDialog(null, "Wrong subband selection" +
                    "\nQuantisation using median filtering only accepts" +
                    "\n 'HF' subband", "ERROR", JOptionPane.ERROR_MESSAGE);
            //Exit
            System.exit(-1);
        } // End of if-else codition 
        
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
    
}
