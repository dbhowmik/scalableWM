/*
 * MotionEstimation.java
 *
 * @author Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 */

package MCTF;
import ImageVideoRW.streamReadWrite;
import Main.*;
import java.io.*;
import javax.swing.*;

public class MotionEstimation {
    
    
// Watermark embedding for Image    
    public void MotionEstimation(String inFileName, int tLevel, int[] selectSize, int[] orgSize) {
        ReadParameter parameter = new ReadParameter();                               
        SearchAlgo SearchAlgo = new SearchAlgo();
        streamReadWrite intRW = new streamReadWrite();
        
        boolean fileStart = true;
                                
        //Reading original video
        File inputVidFile = new File(inFileName);
        if(inputVidFile.exists()== false){
            JOptionPane.showMessageDialog(null, "Input file does not exist. Try Again.", "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);           
        }

        int refFrameNo = (int)(Math.pow(2, (tLevel-1)));
        int offsetAdjust = (int)(Math.pow(2, tLevel));
                
        for(int offset=0; offset<parameter.getNumFrame(); offset += offsetAdjust){
                        
            //Read current frame
            double[][] currYFrame = intRW.IntermediateFrameRead(inFileName, orgSize, offset);
            
            //Read ref frame
            double[][] refYFrame = intRW.IntermediateFrameRead(inFileName, orgSize, offset+refFrameNo);

            
            double[][] selectCurrYFrame = new double[selectSize[0]][selectSize[1]];
            double[][] selectRefYFrame = new double[selectSize[0]][selectSize[1]];


            for(int i=0; i<selectSize[0]; i++){
                for(int j=0; j<selectSize[1]; j++){                    
                    selectCurrYFrame[i][j] = currYFrame[i][j];
                    selectRefYFrame[i][j] = refYFrame[i][j];
                }
            }

            if(offset==0){
                fileStart = true;
            }
            //Motion estimation and calculate the motion vector
            System.out.println("MV write in progress.. Frame no. " + (offset+refFrameNo));
            SearchAlgo.searchAlgo(selectCurrYFrame, selectRefYFrame,  fileStart, inFileName);
            fileStart = false;            
        }

        System.out.println("Motion Estimation done !!!");
                        
    }

}
