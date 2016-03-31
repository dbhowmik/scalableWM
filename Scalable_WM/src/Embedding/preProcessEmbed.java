/**
 * @author: Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 *
 */

package Embedding;
import Main.*;
import Wavelet.*;
import MCTF.*;
import Result.ImperceptibilityResult;
import Watermark.Watermark;
import java.io.*;

public class preProcessEmbed {
    public static int roundControl;    

    public void preProcessEmbed(String filename, String[] subband, String[] tSubband){        
        //----------------------------------------------------------------------
        ReadParameter param = new ReadParameter();
        MotionEstimation ME = new MotionEstimation();
        MotionCompensation MCTF = new MotionCompensation();
        MCTFrecon mctfRecon = new MCTFrecon();
        ImperceptibilityResult stat = new ImperceptibilityResult();
        WaveletAnal decomp = new WaveletAnal();
        WaveletSyn recon = new WaveletSyn();
        EmbedVideo embedVid = new EmbedVideo();              
        Watermark water = new Watermark();        
        //----------------------------------------------------------------------        
        //-------------- Get watermark values ----------------------------------
        water.Watermark();
        //------------------- Get the decomposition levels ---------------------
        int wv1Level = param.getDecomLevel3D()[0];
        int tLevel = param.getDecomLevel3D()[1];
        int wv2Level = param.getDecomLevel3D()[2];
        //----------------------------------------------------------------------
        String inVideoName = null, outTempVideoName = null, mvTempFileName = null, outVideoName = null;
        String outVideoWv1 = null, outVideoWv2 = null, outEmbedVid = null, wmVidName = null, MCTFinVideoName = null;
        //----------------------------------------------------------------------
        //Round control is used to control rounding up operation in inverse wavelet transform.
        roundControl = 1;
        //----------------------------------------------------------------------
        //1st wavelet decomposition in 2D+t+2D framework.
        //----------------------------------------------------------------------
        int[] wv1size = new int[2];
        wv1size[0] = param.getVidWidth();
        wv1size[1] = param.getVidHeight();
        int[] orgSize = {wv1size[0],wv1size[1]};
        //----------------------------------------------------------------------
        int[] tSize = new int[2];
//        if(wv2Level==0){
//            tSize[0] = (int)(wv1size[0] / Math.pow(2, wv1Level-1));
//            tSize[1] = (int)(wv1size[1] / Math.pow(2, wv1Level-1));
//        } else {
            tSize[0] = (int)(wv1size[0] / Math.pow(2, wv1Level));
            tSize[1] = (int)(wv1size[1] / Math.pow(2, wv1Level));
        //}
        
        //----------------------------------------------------------------------
        int[] wv2size = tSize;        
        //----------------------------------------------------------------------
        System.out.println("1st 2D decomposition in progress. Total no of level: " + wv1Level);
        //----------------------------------------------------------------------
        inVideoName = param.getInName();
        outVideoWv1 = param.getInName().substring(0, param.getInName().length()-4) + "_wv1.tmp";
        decomp.WaveletAnal(inVideoName, outVideoWv1, wv1size, orgSize, wv1Level, 1); // 1 = wavelet number to identify        
        //----------------------------------------------------------------------
        //Temporal analysis
        //----------------------------------------------------------------------
        for(int i=1; i<=tLevel; i++){
            if(i==1){
                inVideoName = outVideoWv1;
            } else{
                inVideoName = param.getInName().substring(0, param.getInName().length()-4) + "_0" + (i-1) + "t.tmp";
            }            
            outTempVideoName = param.getInName().substring(0, param.getInName().length()-4) + "_0" + i + "t.tmp";
            //------------------------------------------------------------------
            //------------- Motion estimation and motion compensation ----------
            //------------------------------------------------------------------
            ME.MotionEstimation(inVideoName, i, tSize, orgSize);
            MCTF.MotionCompensation(inVideoName, outTempVideoName, tSize, orgSize, i);
            System.out.println("--------------------------------------");
            System.out.println("Level " + i + " temporal filter analysis done. ");
            System.out.println("--------------------------------------");
            //---------- Deleting temporary files ------------------------------
            if(i>1 && i<=tLevel){
                DeleteFile(param.getInName().substring(0, param.getInName().length()-4) + "_0" + (i-1) + "t.tmp");
            }
            if(i>=tLevel){
                DeleteFile(outVideoWv1);
            }
            //------------------------------------------------------------------
        }        
        //----------------------------------------------------------------------
        //2nd wavelet decomposition in 2D+t+2D framework.
        //----------------------------------------------------------------------        
        System.out.println("2nd 2D decomposition in progress. Total no of level: " + wv2Level);
        //----------------------------------------------------------------------        
        if(tLevel==0){
            inVideoName = outVideoWv1;
        } else {
            inVideoName = param.getInName().substring(0, param.getInName().length()-4) + "_0" + tLevel + "t.tmp";
        }
        //----------------------------------------------------------------------
        outVideoWv2 = param.getInName().substring(0, param.getInName().length()-4) + "_wv2.tmp";
        //-------------- Wavelet decomposition ---------------------------------
        decomp.WaveletAnal(inVideoName, outVideoWv2, wv2size, orgSize, wv2Level, 2); // 2 = wavelet number to identify                
        DeleteFile(inVideoName);
        //----------------------------------------------------------------------
        //----------- Watermark Embedding and video reconstruction -------------
        //----------------------------------------------------------------------
        for(int tempSubChoice = 0; tempSubChoice<tSubband.length; tempSubChoice++){
            for(int subChoice = 0; subChoice< subband.length; subChoice++ ){
                outEmbedVid = param.getInName().substring(0, param.getInName().length()-4) + "_" + tSubband[tempSubChoice] + "_" + subband[subChoice] + ".tmp";
                roundControl = 1;
                //------------------ Watermark embedding -----------------------
                System.out.println("Watermark embedding tChoice = " + tSubband[tempSubChoice] + " and subChoice = " + subband[subChoice]);
                embedVid.EmbedVideo(outVideoWv2, outEmbedVid, orgSize, (wv1Level+wv2Level), tSubband[tempSubChoice], subband[subChoice]);
                //--------------------------------------------------------------
                //-- 2nd 2D synthesis
                //--------------------------------------------------------------
                System.out.println("2nd 2D synthesis in progress... Total no of level: " + wv2Level);
                outVideoName = inVideoName;
                recon.WaveletSyn(outEmbedVid, outVideoName, wv2size, orgSize, wv2Level, 2); // 2 = wavelet number to identify
                DeleteFile(outEmbedVid);
                //--------------------------------------------------------------
                /*
                //----------------- Temp test ----------------------------------
                System.out.println("2nd 2D synthesis in progress... Total no of level: " + wv2Level);
                outVideoName = inVideoName;
                recon.WaveletSyn(outVideoWv2, outVideoName, wv2size, orgSize, wv2Level, 2); // 2 = wavelet number to identify
                //--------------------------------------------------------------
                */
                //Temporal Sysnthesis
                for(int i=tLevel; i>0; i--){
                    if(i==1){
                        outTempVideoName = outVideoWv1;
                        mvTempFileName = outVideoWv1.substring(0, outVideoWv1.length()-4) + ".mv";
                    } else{
                        outTempVideoName = param.getInName().substring(0, param.getInName().length()-4) + "_0" + (i-1) + "t.tmp";
                        mvTempFileName = param.getInName().substring(0, param.getInName().length()-4) + "_0" + (i-1) + "t.mv";
                    }
                    MCTFinVideoName = param.getInName().substring(0, param.getInName().length()-4) + "_0" + i + "t.tmp";

                    mctfRecon.MCTFrecon(MCTFinVideoName, outTempVideoName, mvTempFileName, tSize, orgSize, i);

                    System.out.println("--------------------------------------");
                    System.out.println("Level " + i + " temporal filter synthesis done. ");
                    System.out.println("--------------------------------------");

                    if(i>=1 && i<=tLevel){
                        DeleteFile(param.getInName().substring(0, param.getInName().length()-4) + "_0" + i + "t.tmp");
                    }
                }

                //----------------------------------------------------------------
                //-- 1st 2D synthesis
                //----------------------------------------------------------------
                System.out.println("1st 2D synthesis in progress... Total no of level: " + wv1Level);
                roundControl = 0; // Round control release for final result
          //      wmVidName = param.getInName().substring(0, param.getInName().length()-4) + "_" + tSubband[tempSubChoice] + "_" + subband[subChoice] + "_" + param.getEmbedProcedure() + ".yuv";
                recon.WaveletSyn(outVideoWv1, wmVidName, wv1size, orgSize, wv1Level, 1);
                DeleteFile(outVideoWv1);

                //----------------------------------------------------------------

                //Calculate statistics
                stat.ImperceptibilityResult(wmVidName, wmVidName); //Pos1=Stat File name, Pos2=Wm video name;
            }
        }
        DeleteFile(outVideoWv2);
    }

    public int getRoundCon(){        
        return roundControl;
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
