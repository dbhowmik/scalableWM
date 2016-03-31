/*
 * ReadParameter.java
 *
 *
 * @ author: Deepayan Bhowmik 
 *
 * Copyright reserved
 */

package Embedding;
import javax.swing.*;
import java.io.*;

// This part of the program helps to read parameters used as various input to 
// the program. And store the respective values in appropriate variables 

public class ReadParameter {
    // Declaration of global variables which hold the parameter data
    public static int decompLevel;
    public static String originalImageName, waveletName, watermarkName, wmImageName;
    public static String subbandChoice, embedProcedure, thresholdChoice, equationChoice;
    public static double[] thresholdValue = new double[4];
    public static double[] alphaValue = new double[4];
    public static String qntChoice;
    public static double qntAlphaRS;
    public static double qntQuantMF;
   
    // Reading the parameters from the parameter file
    public void readEmbedParameter(String fileString) {
        File embedParaFile = null;
        
        // Input: parameter file and Check for file existance
        while(true){
            
            embedParaFile = new File(fileString);
            if(embedParaFile.exists()){
                break;
            } else{
                JOptionPane.showMessageDialog(null, "File Does not Exist. Try Again.", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        } // End of while loop
        
        // Read and save the variable values in different arrays
        try{ // Read the file content
            BufferedReader paraInput = new BufferedReader(new FileReader(embedParaFile));
            
            //Discard headers and initial notes
            for(int i=0;i<15;i++){
                paraInput.readLine();
            }
            
            // Get the image name to be watermarked
            originalImageName = paraInput.readLine();
            
            //Discard comment line and border line
            paraInput.readLine();
            paraInput.readLine();
            
            //Get number of decomposition level
            decompLevel = Integer.parseInt(paraInput.readLine());      
            
            //Discard comment line and border line
            paraInput.readLine();
            paraInput.readLine();
            paraInput.readLine();
            
            //Get the wavelet name
            waveletName = paraInput.readLine();
            
            //Discard comment line and border line
            paraInput.readLine();
            paraInput.readLine();
            
            //Get the watermark name
            watermarkName = paraInput.readLine();
           
             //Discard comment line and border line
            paraInput.readLine();
            paraInput.readLine();
            
            //Get watermarked image name to be saved
            wmImageName = paraInput.readLine();
       
            //Discard comment line and border line
            paraInput.readLine();
            paraInput.readLine();
            paraInput.readLine();
            paraInput.readLine();
            paraInput.readLine();
            
            //Get the choice of subband
            subbandChoice = paraInput.readLine();
            
            //Discard comment line and border line
            paraInput.readLine();
            paraInput.readLine();
            
            // Get Embedding procedure
            embedProcedure = paraInput.readLine();
             
            // Collecting parameters for Direct Coefficient modification method
            //Discard comment line and border line
            paraInput.readLine();
            paraInput.readLine();
            paraInput.readLine();
            paraInput.readLine();
            
            // Get thresholding type
            thresholdChoice = paraInput.readLine();
          
            //Discard comment line and border line
            paraInput.readLine();
            paraInput.readLine();
            paraInput.readLine();
            paraInput.readLine();
            paraInput.readLine();
            
            // Get threshold values in case of manual thresholding
            // Threshold value for LL subband 
            thresholdValue[0] = Double.parseDouble(paraInput.readLine());
            
            //Discard comment line and border line
            paraInput.readLine();
            paraInput.readLine();
            
            // Threshold value for LH subband 
            thresholdValue[1] = Double.parseDouble(paraInput.readLine());
            
            //Discard comment line and border line
            paraInput.readLine();
            paraInput.readLine();
            
            // Threshold value for HL subband 
            thresholdValue[2] = Double.parseDouble(paraInput.readLine());
            
            //Discard comment line and border line
            paraInput.readLine();
            paraInput.readLine();
            
            // Threshold value for HH subband 
            thresholdValue[3] = Double.parseDouble(paraInput.readLine());
            
            
            //Discard comment line and border line
            paraInput.readLine();
            paraInput.readLine();
            paraInput.readLine();
            paraInput.readLine();
            
            // Get the equation choice
            equationChoice = paraInput.readLine();
            
            //Discard comment line and border line
            paraInput.readLine();
            paraInput.readLine();
            paraInput.readLine();
            paraInput.readLine();
            
            // Get alpha values, watermark weight value 
            // Alpha value for LL subband 
            alphaValue[0] = Double.parseDouble(paraInput.readLine());
            
            //Discard comment line and border line
            paraInput.readLine();
            paraInput.readLine();
            
            // Alpha value for LH subband 
            alphaValue[1] = Double.parseDouble(paraInput.readLine());
            
            //Discard comment line and border line
            paraInput.readLine();
            paraInput.readLine();
            
            // Alpha value for HL subband 
            alphaValue[2] = Double.parseDouble(paraInput.readLine());
            
            //Discard comment line and border line
            paraInput.readLine();
            paraInput.readLine();
            
            // Alpha value for HH subband 
            alphaValue[3] = Double.parseDouble(paraInput.readLine());
            // End of direct coeff modification parameters

            // This part collects data for Quantisation methods
            //Discard comment line and border line
            paraInput.readLine();
            paraInput.readLine();
            paraInput.readLine();
            paraInput.readLine();
            
            //Get quantisation choice
            qntChoice = paraInput.readLine();
            
            //Discard comment line and border line
            paraInput.readLine();
            paraInput.readLine();
            
            //Get Alpha value for raster scanning
            qntAlphaRS = Double.parseDouble(paraInput.readLine());
            
            //Discard comment line and border line
            paraInput.readLine();
            paraInput.readLine();
            
            //Get Alpha value for raster scanning
            qntQuantMF = Double.parseDouble(paraInput.readLine());
            
            //End of parameter reading    
           
        } catch(IOException ioException) {
            JOptionPane.showMessageDialog(null,"File Error !!", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        
        
    }
    
    //Make different parameter values ready to be read by other parts of the program
    public String getOriginalImageName(){
        return originalImageName;
    }
    
    public int getDecompLevel(){
        return decompLevel;
    }
    
    public String getWaveletName(){
        return waveletName;
    }
     
    public String getWatermarkName(){
        return watermarkName;
    }
      
    public String getWmImageName(){
        return wmImageName;
    }
    
    public String getSubbandChoice(){
        return subbandChoice;
    }
    
    public String getEmbedProcedure(){
        return embedProcedure;
    }
    
    public String getThresholdChoice(){
        return thresholdChoice;
    }
    
    public String getEquationChoice(){
        return equationChoice;
    }
    
    public double[] getThresholdValue(){
        return thresholdValue;
    }
    
    public double[] getAlphaValue(){
        return alphaValue;
    }
    
    public String getQntChoice(){
        return qntChoice;
    }
    
    public double getQntAlphaRS(){
        return qntAlphaRS;
    }
    
    public double getQntQuantMF(){
        return qntQuantMF;
    }
      
    // Get the threshold values. In case of adaptive thresholding, calculate 
    //  and overwrite the threshold values
  
    public void selectThresholdValue(double[][] coeffLL, double[][] coeffLH, double[][] coeffHL, double[][] coeffHH){
        MatrixFunction func = new MatrixFunction(); 
        ReadParameter param = new ReadParameter();
        double maxLL, maxHL, maxLH, maxHH;
        
        if(param.getThresholdChoice().compareTo("AD") == 0){
        // Calculate the adaptive threshold values depending upon the subband choice
            if(param.getSubbandChoice().compareTo("LF") == 0){
                // For low frequency subband
                maxLL = func.Max2D(coeffLL);
                thresholdValue[0] = Math.pow(2,Math.round(Math.log10(maxLL)/Math.log(2.0) - 1));                
                
            } else if(param.getSubbandChoice().compareTo("HF") == 0){
                // High frequency subbands
                maxLH = func.Max2D(coeffLH);
                thresholdValue[1] = Math.pow(2,Math.round(Math.log10(maxLH)/Math.log(2.0) - 1));
                
                maxHL = func.Max2D(coeffHL);
                thresholdValue[2] = Math.pow(2,Math.round(Math.log10(maxLH)/Math.log(2.0) - 1));
                
                maxHH = func.Max2D(coeffHH);
                thresholdValue[3] = Math.pow(2,Math.round(Math.log10(maxHH)/Math.log(2.0) - 1));
                
            } else if(param.getSubbandChoice().compareTo("AF") == 0){
                // All frequency subbands
                maxLL = func.Max2D(coeffLL);
                thresholdValue[0] = Math.pow(2,Math.round(Math.log10(maxLL)/Math.log(2.0) - 1));
                
                maxLH = func.Max2D(coeffLH);
                thresholdValue[1] = Math.pow(2,Math.round(Math.log10(maxLH)/Math.log(2.0) - 1));
                
                maxHL = func.Max2D(coeffHL);
                thresholdValue[2] = Math.pow(2,Math.round(Math.log10(maxLH)/Math.log(2.0) - 1));
                
                maxHH = func.Max2D(coeffHH);
                thresholdValue[3] = Math.pow(2,Math.round(Math.log10(maxHH)/Math.log(2.0) - 1));              
            }
        } else if(param.getThresholdChoice().compareTo("MA") == 0); 
          // In case of manual thresholding .. do nothing but retain the paramter valuse from the file
    } // End of selectThresholh value function
    
    
} // End of the ReadParameter class
