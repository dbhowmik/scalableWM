/**
 * @author: Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 *
 */

package MCTF;
import ImageVideoRW.streamReadWrite;
import Main.*;
import java.io.*;
import javax.swing.*;

public class MCTFrecon {
    
    public void MCTFrecon(String inVideoName, String outVideoName, String mvFileName, int[] selectSize, int[] orgSize, int tLevel){
        ReadParameter param = new ReadParameter();
        MVRead MV = new MVRead();
        tSynthesis tSynth = new tSynthesis();
        streamReadWrite intmRW = new streamReadWrite();

        boolean fileStart = true;
        double[][] LPYFrame = new double[orgSize[0]][orgSize[1]];
        double[][] HPYFrame = new double[orgSize[0]][orgSize[1]];

        double[][] selectLPYFrame = new double[selectSize[0]][selectSize[1]];
        double[][] selectHPYFrame = new double[selectSize[0]][selectSize[1]];

        File inputVidFile = new File(inVideoName);
        if(inputVidFile.exists()== false){
            JOptionPane.showMessageDialog(null, "Original video file does not exist. Try Again.", "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
        
        File MVFile = new File(mvFileName);
        if(MVFile.exists()== false){
            JOptionPane.showMessageDialog(null, "MV file does not exist. Try Again.", "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        int refFrameNo = (int)(Math.pow(2, (tLevel-1)));
        int offsetAdjust = (int)(Math.pow(2, tLevel));

        for(int offset=0; offset<param.getNumFrame(); offset += offsetAdjust){
            //Read ref frame
            LPYFrame = intmRW.IntermediateFrameRead(inVideoName, orgSize, offset);

            //Read current frame            
            HPYFrame = intmRW.IntermediateFrameRead(inVideoName, orgSize, offset+refFrameNo);

            for(int i=0; i<selectSize[0]; i++){
                for(int j=0; j<selectSize[1]; j++){
                    selectLPYFrame[i][j] = LPYFrame[i][j];
                    selectHPYFrame[i][j] = HPYFrame[i][j];
                }
            }

            MV.mvRead(mvFileName, (int)(offset/offsetAdjust), selectSize);
            int[] mvYFrame = MV.getYmv();
        
            //Motion estimation and calculate the motion vector
            System.out.println("MCTF reconstruction in progress.. Frame no. " + offset + " Frame no. " + (offset+refFrameNo));

            tSynth.tSynthesis(selectLPYFrame, selectHPYFrame, mvYFrame);
            double[][] selectRefYFrame = tSynth.getRefFrame();
            double[][] selectCurrYFrame = tSynth.getCurrFrame();

            double[][] currYFrame = new double[LPYFrame.length][LPYFrame[0].length];
            double[][] refYFrame = new double[HPYFrame.length][HPYFrame[0].length];

            for(int i=0; i<refYFrame.length; i++){
                for(int j=0; j<refYFrame[0].length; j++){
                    currYFrame[i][j] = LPYFrame[i][j];
                    refYFrame[i][j] = HPYFrame[i][j];
                }
            }

            for(int i=0; i<selectSize[0]; i++){
                for(int j=0; j<selectSize[1]; j++){
                    refYFrame[i][j] = selectRefYFrame[i][j];
                    currYFrame[i][j] = selectCurrYFrame[i][j];
                }
            }

            //Motion estimation and calculate the motion vector
            System.out.println("Video write in progress.. ");
            //================================================
            for(int i=offset;i<(offset+offsetAdjust);i++){
                if(i==0){
                    fileStart = true;
                } else {
                    fileStart = false;
                }
                
                if(i == offset){
                    intmRW.IntermediateFrameWrite(currYFrame, outVideoName, fileStart);
                    fileStart = false;
                } else if(i == (offset+refFrameNo)){
                    intmRW.IntermediateFrameWrite(refYFrame, outVideoName, fileStart);
                } else {
                    double[][] interYFrame = intmRW.IntermediateFrameRead(inVideoName, orgSize, i);
                    intmRW.IntermediateFrameWrite(interYFrame, outVideoName, fileStart);
                }
                
            } //for i
        } // for 'offset'        
    }

}
