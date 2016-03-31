/**
 * @author: Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 */

package Wavelet;
import ImageVideoRW.streamReadWrite;
import Main.*;
import ImageVideoRW.VidRead;
import DSPnMath.DWT;
import java.io.*;
import javax.swing.*;

public class WaveletAnal {
    public void WaveletAnal(String inFileName, String outFileName, int[] selectSize, int[] orgSize, int level, int waveletNo){
        ReadParameter parameter = new ReadParameter();
        DWT wavelet = new DWT();
        VidRead read = new VidRead();
        streamReadWrite intRW = new streamReadWrite();

        //Reading original video
        File inputVidFile = new File(inFileName);
        if(!inputVidFile.exists()){
            JOptionPane.showMessageDialog(null, "Video file does not exist. Try Again.", "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        double[][] dwtVideoCoeff = new double[selectSize[0]][selectSize[1]];
        double[][] selectOrgVideo = new double[selectSize[0]][selectSize[1]];
        double[][] originalVideoY = new double[orgSize[0]][orgSize[1]];
        
        double[][] decomposedVideoY = new double[orgSize[0]][orgSize[1]];

        boolean fileStart = false, fileStartYUV = false;
        for(int offset=0; offset<parameter.getNumFrame(); offset++){
            //------------------------------------------------------------------
            if(waveletNo == 1){
                //Read video
                read.VidRead(inFileName,offset);
                originalVideoY = read.getYpixel();                
            } else {
                originalVideoY = intRW.IntermediateFrameRead(inFileName, orgSize, offset);                
            }

            for(int i=0; i<orgSize[0]; i++){
                for(int j=0; j<orgSize[1]; j++){
                    decomposedVideoY[i][j] =  originalVideoY[i][j];                    
                }
            }

            
            for(int i=0; i<selectSize[0]; i++){
                for(int j=0; j<selectSize[1]; j++){
                    selectOrgVideo[i][j] =  originalVideoY[i][j];
                }
            }

            System.out.println("Wavelet decomposition: frame no = " + offset);
            // Forward wavelet transform
            dwtVideoCoeff = wavelet.dwt(selectOrgVideo,selectSize,level,parameter.getWaveletName());

            for(int i=0; i<dwtVideoCoeff.length; i++){
                for(int j=0; j<dwtVideoCoeff[0].length; j++){
                    decomposedVideoY[i][j] = dwtVideoCoeff[i][j];
                }
            }
            
            //write video
            if(offset==0){
                fileStart = true;
                fileStartYUV = true;
            }
            //write Y pixels
            intRW.IntermediateFrameWrite(decomposedVideoY,outFileName,fileStart);
            fileStart = false;

            
//            Vidwrite write = new Vidwrite();
//            if(waveletNo == 2){
//                read.VidRead(parameter.getInName(),offset);
//                double[][] reconVideoU = read.getUpixel();
//                double[][] reconVideoV = read.getVpixel();
//                String outYUV = outFileName.substring(0, outFileName.length()-4) + ".yuv";
//                System.out.println("-------- Write --------");
//                //write Y pixels
//                write.Vidwrite(decomposedVideoY, read.YSize(), outYUV, fileStartYUV);
//                fileStartYUV = false;
//                //write UV pixels
//                write.Vidwrite(reconVideoU, read.UVSize(), outYUV, fileStartYUV);
//                write.Vidwrite(reconVideoV, read.UVSize(), outYUV, fileStartYUV);
//
//            }
        }        
    }
}
