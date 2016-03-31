/**
 * @author: Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 *
 */

package Extract;
import Main.*;
import Wavelet.*;
import MCTF.*;
import java.io.*;

public class preProcessExtract {
    public static int wv1Level, tLevel, wv2Level;
    int[] wv1size = new int[2];
    int[] orgSize = new int[2];
    int[] tSize = new int[2];
    int[] wv2size = new int[2];
    String outVideoWv2Org = null;

    public void preProcessExtract(String filename, String[] subband, String[] tSubband, String folderName){

    }

   public void DecodeWm(String inWmVideoName, String tSubband, String subband){
        MotionCompensation MCTF = new MotionCompensation();
        MotionEstimation ME = new MotionEstimation();
        WaveletAnal decomp = new WaveletAnal();
        DecodeVideo decode = new DecodeVideo();

        ReadParameter param = new ReadParameter();

        String outTempVideoName = null;
        String outVideoWv2Wm = null;
        //--------------------------------------------------------------
        //1st wavelet decomposition in 2D+t+2D framework.
        //--------------------------------------------------------------
        System.out.println("Watermarked test video: 1st 2D decomp in progress... Total no of level: " + wv1Level);


        //----------------------------------------------------------------------
        wv1size[0] = param.getVidWidth();
        wv1size[1] = param.getVidHeight();
        orgSize = wv1size;
        //----------------------------------------------------------------------
//        if(wv2Level==0){
//            tSize[0] = (int)(wv1size[0] / Math.pow(2, wv1Level-1));
//            tSize[1] = (int)(wv1size[1] / Math.pow(2, wv1Level-1));
//        } else {
            tSize[0] = (int)(wv1size[0] / Math.pow(2, wv1Level));
            tSize[1] = (int)(wv1size[1] / Math.pow(2, wv1Level));
      //  }
        //----------------------------------------------------------------------
        wv2size = tSize;
        //----------------------------------------------------------------------

        //--------------------------------------------------------------
        String inVideoName = inWmVideoName;
        String outVideoWv1 = inWmVideoName.substring(0, inWmVideoName.length()-4) + "_wv1.tmp";
        decomp.WaveletAnal(inVideoName, outVideoWv1, wv1size, orgSize, wv1Level, 1); // 1 = wavelet number to identify
        //--------------------------------------------------------------
        //--------------------------------------------------------------
        //Temporal analysis
        //--------------------------------------------------------------
        for(int i=1; i<=tLevel; i++){
            if(i==1){
                inVideoName = outVideoWv1;
            } else{
                inVideoName = inWmVideoName.substring(0, inWmVideoName.length()-4) + "_0" + (i-1) + "t.tmp";
            }
            outTempVideoName = inWmVideoName.substring(0, inWmVideoName.length()-4) + "_0" + i + "t.tmp";

            //Motion estimation only for blind watermarking algorithm
           // if(param.getEmbedProcedure().compareTo("DR")!=0){
                ME.MotionEstimation(inVideoName, i, tSize, orgSize);
            //}
           // MCTF.MotionComp4Ext(inVideoName, outTempVideoName, tSize, orgSize, i);
            MCTF.MotionCompensation(inVideoName, outTempVideoName, tSize, orgSize, i);

            System.out.println("--------------------------------------");
            System.out.println("Watermarked test video: Level " + i + " temporal filter analysis done. ");
            System.out.println("--------------------------------------");

            if(i>1 && i<=tLevel){
                DeleteFile(inWmVideoName.substring(0, inWmVideoName.length()-4) + "_0" + (i-1) + "t.tmp");
            }
            if(i>=tLevel){
                DeleteFile(outVideoWv1);
            }
        }
        //--------------------------------------------------------------
        //--------------------------------------------------------------
        //2nd wavelet decomposition in 2D+t+2D framework.
        //--------------------------------------------------------------
        System.out.println("Watermarked test video: 2nd 2D decomp in progress... Total no of level: " + wv2Level);

        if(tLevel==0){
            inVideoName = outVideoWv1;
        } else {
            inVideoName = inWmVideoName.substring(0, inWmVideoName.length()-4) + "_0" + tLevel + "t.tmp";
        }

        outVideoWv2Wm = inWmVideoName.substring(0, inWmVideoName.length()-4) + "_wv2.tmp";

        decomp.WaveletAnal(inVideoName, outVideoWv2Wm, wv2size, orgSize, wv2Level, 2); // 2 = wavelet number to identify
        DeleteFile(inVideoName);
        //--------------------------------------------------------------
        //--------------------------------------------------------------
        // Watermark Extraction procedure
        //--------------------------------------------------------------
        System.out.println("Watermark extraction for test video: " + inWmVideoName);
        decode.DecodeVideo(outVideoWv2Org, outVideoWv2Wm, orgSize, (wv1Level+wv2Level), tSubband, subband);
        //--------------------------------------------------------------
   }

    public void DeleteFile(String fileName){
        try{
            File inputFile = new File(fileName);
            if(!inputFile.exists()){
                System.err.println("File: " + fileName + " does not exist.");
                System.exit(-1);
            }

            if (inputFile.delete()){
                System.err.println("** Deleted " + fileName + " **");
            } else{
                System.err.println("Failed to delete " + fileName);
            }

        } catch (SecurityException e) {
             System.err.println("Unable to delete " + fileName + "(" + e.getMessage() + ")");
             System.exit(-1);
        }
    }
    
}
