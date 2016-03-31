/*
 * EmbedVideo.java
 *
 * @author Deepayan Bhowmik
 *
 * Copyright reserved
 *
 */

package Embedding;
import Main.*;
import ImageVideoRW.streamReadWrite;
import java.io.*;
import javax.swing.*;

public class EmbedVideo {
    
// Watermark embedding for video
    public void EmbedVideo(String inFileName, String outFileName, int[] orgSize, int level, String tSubband, String subband) {
        //----------------------------------------------------------------------
        ReadParameter parameter = new ReadParameter();
        streamReadWrite intRW = new streamReadWrite();
        Embedding embed = new Embedding();
        SADdeltaCal delta = new SADdeltaCal();
        //----------------------------------------------------------------------
        int tempDataCap = 0;       
        //----------------------------------------------------------------------
        //Reading original video
        //----------------------------------------------------------------------
        File inputVidFile = new File(inFileName);
        if(inputVidFile.exists()== false){
            JOptionPane.showMessageDialog(null, "Video file does not exist. Try Again.", "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
        //----------------------------------------------------------------------        
        double[][] originalVideoY = new double[orgSize[0]][orgSize[1]];        
        double[][] embeddedVideoY = new double[orgSize[0]][orgSize[1]];
        double[][] SADembdVideoY = new double[orgSize[0]][orgSize[1]];
        double[][] SADorgVideoY = new double[orgSize[0]][orgSize[1]];
        //----------------------------------------------------------------------
        boolean fileStart = false;
        //----------------------------------------------------------------------
        for(int offset=0; offset<parameter.getNumFrame(); offset++){
            //------------------------------------------------------------------
            originalVideoY = intRW.IntermediateFrameRead(inFileName, orgSize, offset);                            
            //------------------------------------------------------------------
            for(int i=0; i<embeddedVideoY.length; i++){
                for(int j=0; j<embeddedVideoY[0].length; j++){
                    embeddedVideoY[i][j] =  originalVideoY[i][j];
                    SADorgVideoY[i][j] =  originalVideoY[i][j];
                }
            }            

            for(int i=0; i<embeddedVideoY.length; i++){
                for(int j=0; j<embeddedVideoY[0].length; j++){
                    SADembdVideoY[i][j] =  embeddedVideoY[i][j];
                }
            }

            
            //------------------------------------------------------------------
            //---------- Write video -------------------------------------------
            //------------------------------------------------------------------
            if(offset==0){
                fileStart = true;
            }
            //Delta calculation for SAD
            //delta.SADdeltaCal(SADorgVideoY, SADembdVideoY,outFileName, fileStart);
            //------------- Write Y pixels -------------------------------------
            intRW.IntermediateFrameWrite(embeddedVideoY,outFileName,fileStart);
            fileStart = false;
            //------------------------------------------------------------------
        }
        //----------------------------------------------------------------------
        int avgDataCap = tempDataCap / parameter.getNumFrame();
        saveFile(outFileName, -1, avgDataCap);
        //----------------------------------------------------------------------
    }
        

    // File save for watermark data capacity -----------------------------------
    public void saveFile(String FileName, int ind, int capacity){ 
        FileOutputStream coeffOutFile;	
        String outFileString;        
        outFileString = FileName.substring(0,FileName.length()-4) + "_prf.txt";
        try
        {         
            if(ind==0){
                coeffOutFile = new FileOutputStream (outFileString);
            } else{
                coeffOutFile = new FileOutputStream (outFileString,true);
            } 	                
            new PrintStream(coeffOutFile).println (+ ind + " " + capacity);
            coeffOutFile.close();
        } catch (IOException e)
        {
            JOptionPane.showMessageDialog(null,"Unable to write to file");
            System.exit(-1);
        }
    }
    //--------------------------------------------------------------------------
}
