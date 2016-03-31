/*
 * EmbedVideo.java
 *
 * @author Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 */

package Extract;
import Main.*;
import ImageVideoRW.streamReadWrite;
import java.io.*;
import javax.swing.*;

public class DecodeVideo {
                
// Watermark extraction
    public void DecodeVideo(String orgFileName, String wmFileName, int[] orgSize, int level, String tSubband, String subband) {
        ReadParameter parameter = new ReadParameter();                
        streamReadWrite intRW = new streamReadWrite();
        Decoding decode = new Decoding();

        double tempHD = 0, tempSim = 0;
               
        //Reading test video
        File inputWmVidFile = new File(wmFileName);
        if(inputWmVidFile.exists()== false){
            JOptionPane.showMessageDialog(null, "Watermarked Video file does not exist. Try Again.", "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
                
        double[][] originalVideoY = new double[orgSize[0]][orgSize[1]];
        double[][] watermarkedVideoY = new double[orgSize[0]][orgSize[1]];
                
        for(int offset=0; offset<parameter.getNumFrame(); offset++){
            //------------------------------------------------------------------
            watermarkedVideoY = intRW.IntermediateFrameRead(wmFileName, orgSize, offset);
            //------------------------------------------------------------------
            
            //------------------------------------------------------------------
            System.out.println("Watermark Extraction: frame no = " + offset);
            
            //-------- Perform extraction operation ----------------------------
//            if(tSubband.compareTo("tLLL") ==0 && (offset%8)==0){
//                decode.Decoding(watermarkedVideoY, originalVideoY, level, subband, tSubband, offset);
//            } else if(tSubband.compareTo("tLLL_LLH") ==0 && (offset%4)==0){
//                decode.Decoding(watermarkedVideoY, originalVideoY, level, subband, tSubband, offset);
//            } else if(tSubband.compareTo("tLLL_LLH_LH") ==0 && (offset%2)==0){
//                decode.Decoding(watermarkedVideoY, originalVideoY, level, subband, tSubband, offset);
//            } else if(tSubband.compareTo("tLLL_LLH_LH_H") ==0){
//                decode.Decoding(watermarkedVideoY, originalVideoY, level, subband, tSubband, offset);
//            } else if(tSubband.compareTo("tLLH") ==0 && (offset%4)==0 && (offset%8)!=0){
//                decode.Decoding(watermarkedVideoY, originalVideoY, level, subband, tSubband, offset);
//            } else if(tSubband.compareTo("tLLH_LH") ==0 && (offset%2)==0 && (offset%8)!=0){
//                decode.Decoding(watermarkedVideoY, originalVideoY, level, subband, tSubband, offset);
//            } else if(tSubband.compareTo("tLLH_LH_H") ==0 && (offset%8)!=0){
//                decode.Decoding(watermarkedVideoY, originalVideoY, level, subband, tSubband, offset);
//            } else if(tSubband.compareTo("tLH") ==0 && (offset%2)==0 && (offset%4)!=0){
//                decode.Decoding(watermarkedVideoY, originalVideoY, level, subband, tSubband, offset);
//            } else if(tSubband.compareTo("tLH_H") ==0 && (offset%4)!=0){
//                decode.Decoding(watermarkedVideoY, originalVideoY, level, subband, tSubband, offset);
//            } else if(tSubband.compareTo("tH") ==0 && (offset%2)==1){
//                decode.Decoding(watermarkedVideoY, originalVideoY, level, subband, tSubband, offset);
//            } else {
//                Decoding.HammDist = 0;
//                Decoding.simMeas = 100;
//            }
            //-------- Robusteness results stored in fie -----------------------
            tempHD += decode.getHammDist();
            tempSim += decode.getSimMeas();
            saveFile(wmFileName, offset, decode.getHammDist(), decode.getSimMeas());
        }
        //----------------------------------------------------------------------
        double avgHD = tempHD / parameter.getNumFrame();
        double avgSim = tempSim / parameter.getNumFrame();
        saveFile(wmFileName, -1, avgHD, avgSim);
        //----------------------------------------------------------------------
    }
        
                   
    public void saveFile(String FileName, int ind, double HDist, double sim){ //Function to save PSNR and RMS value inside the file
        FileOutputStream coeffOutFile;	
        String outFileString;        
        outFileString = FileName.substring(0,FileName.length()-4) + "_rb.txt";
        try
        {         
            if(ind==0){
                coeffOutFile = new FileOutputStream (outFileString);
            } else{
                coeffOutFile = new FileOutputStream (outFileString,true);
            } 	                
            new PrintStream(coeffOutFile).println (+ ind + " " + HDist + " " + sim);
            coeffOutFile.close();
        } catch (IOException e)
        {
            JOptionPane.showMessageDialog(null,"Unable to write to file");
            System.exit(-1);
        }
    }
                    
}
