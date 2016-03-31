
package MCTF;
import ImageVideoRW.streamReadWrite;
import Main.*;
import java.io.*;
import javax.swing.*;


/**
 * @author: Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 *
 */
public class MotionCompensation {    
        
    public void MotionCompensation(String inVideoName, String outVideoName, int[] selectSize, int[] orgSize, int tLevel){
        ReadParameter param = new ReadParameter();        
        streamReadWrite intRW = new streamReadWrite();
        MVRead MV = new MVRead();
        tAnalysis tAnal = new tAnalysis();

        boolean fileStart = true;        
        double[][] refYFrame = new double[orgSize[0]][orgSize[1]];
        double[][] currYFrame = new double[orgSize[0]][orgSize[1]];
        double[][] selectRefYFrame = new double[selectSize[0]][selectSize[1]];
        double[][] selectCurrYFrame = new double[selectSize[0]][selectSize[1]];
        
        File inputVidFile = new File(inVideoName);
        if(inputVidFile.exists()== false){
            JOptionPane.showMessageDialog(null, "Original video file does not exist. Try Again.", "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
        String mvFileName = inVideoName.substring(0, inVideoName.length()-4) + ".mv";
        File MVFile = new File(mvFileName);
        if(MVFile.exists()== false){
            JOptionPane.showMessageDialog(null, "MV file does not exist. Try Again.", "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        int refFrameNo = (int)(Math.pow(2, (tLevel-1)));
        int offsetAdjust = (int)(Math.pow(2, tLevel));

        for(int offset=0; offset<param.getNumFrame(); offset += offsetAdjust){            
            currYFrame = intRW.IntermediateFrameRead(inVideoName, orgSize, offset);
            refYFrame = intRW.IntermediateFrameRead(inVideoName, orgSize, offset+refFrameNo);
            
            MV.mvRead(mvFileName, (int)(offset/offsetAdjust), selectSize);
            int[] mvYFrame = MV.getYmv();

            //Motion compensation...
            System.out.println("Temporal filtering in progress.. Frame no. " + offset + " Frame no. " + (offset+refFrameNo));
            for(int i=0; i<selectSize[0]; i++){
                for(int j=0; j<selectSize[1]; j++){
                    selectCurrYFrame[i][j] = currYFrame[i][j];
                    selectRefYFrame[i][j] = refYFrame[i][j];                    
                }
            }

            double[][] tYLFrame = new double[currYFrame.length][currYFrame[0].length];
            double[][] tYHFrame = new double[refYFrame.length][refYFrame[0].length];

            for(int i=0; i<tYLFrame.length; i++){
                for(int j=0; j<tYLFrame[0].length; j++){
                    tYLFrame[i][j] = currYFrame[i][j];
                    tYHFrame[i][j] = refYFrame[i][j];
                }
            }
            //------------------------------------------------------------------
            tAnal.tAnalysis(selectCurrYFrame, selectRefYFrame, mvYFrame);
            double[][] tYLFrameTmp = tAnal.getLowPass();
            double[][] tYHFrameTmp = tAnal.getHighPass();
            
            for(int i=0; i<selectSize[0]; i++){
                for(int j=0; j<selectSize[1]; j++){
                    tYLFrame[i][j] = tYLFrameTmp[i][j];
                    tYHFrame[i][j] = tYHFrameTmp[i][j];
                }
            }
            //------------------------------------------------------------------
            //Motion estimation and calculate the motion vector
            System.out.println("Decomposed video write in progress.. ");            
                                   
            //================================================
            for(int i=offset;i<(offset+offsetAdjust);i++){
                if(i==0){
                    fileStart = true;
                } else {
                    fileStart = false;
                }

                if(i == offset){
                    intRW.IntermediateFrameWrite(tYLFrame, outVideoName, fileStart);                    
                } else if(i == (offset+refFrameNo)){
                    intRW.IntermediateFrameWrite(tYHFrame, outVideoName, fileStart);                    
                } else {
                    double[][] interYFrame = intRW.IntermediateFrameRead(inVideoName, orgSize, i);
                    intRW.IntermediateFrameWrite(interYFrame, outVideoName, fileStart);                    
                }
            }            //for
        } // for 'offset'

    }

}
