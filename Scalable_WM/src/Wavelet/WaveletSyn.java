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
import DSPnMath.Idwt;
import java.io.*;
import javax.swing.*;

public class WaveletSyn {

    public void WaveletSyn(String inFileName, String outFileName, int[] selectSize, int[] orgSize, int level, int waveletNo){
        ReadParameter parameter = new ReadParameter();
        Idwt wavelet = new Idwt();
        VidRead read = new VidRead();
        
        streamReadWrite intRW = new streamReadWrite();

        //Reading original video
        File inputVidFile = new File(parameter.getInName());
        if(inputVidFile.exists()== false){
            JOptionPane.showMessageDialog(null, "Video file does not exist. Try Again.", "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        double[][] dwtVideoCoeff = new double[selectSize[0]][selectSize[1]];
        double[][] reconSyn = new double[selectSize[0]][selectSize[1]];
        double[][] reconVideoY = new double[orgSize[0]][orgSize[1]];
        double[][] reconVideoU = new double[orgSize[0]/2][orgSize[1]/2];
        double[][] reconVideoV = new double[orgSize[0]/2][orgSize[1]/2];
        
        boolean fileStart = false;
        for(int offset=0; offset<parameter.getNumFrame(); offset++){
            // Read decomposed data from file
            reconVideoY = intRW.IntermediateFrameRead(inFileName, orgSize, offset);

            for(int i=0; i<selectSize[0]; i++){
                for(int j=0; j<selectSize[1]; j++){
                    dwtVideoCoeff[i][j] = reconVideoY[i][j];
                }
            }            
             
            System.out.println("Wavelet synthesis: frame no = " + offset);
            // Inverse wavelet transform
            reconSyn = wavelet.Idwt(dwtVideoCoeff, selectSize, level, parameter.getWaveletName());
            
            for(int i=0; i<selectSize[0]; i++){
                for(int j=0; j<selectSize[1]; j++){
                    reconVideoY[i][j] = reconSyn[i][j];
                }
            }

            //write video
            if(offset==0){
                fileStart = true;
            }

            if(waveletNo == 1){
                read.VidRead(parameter.getInName(),offset);
                reconVideoU = read.getUpixel();
                reconVideoV = read.getVpixel();
               

            } else {                
                intRW.IntermediateFrameWrite(reconVideoY, outFileName, fileStart);
                fileStart = false;
            }
            
        }
    }

}
