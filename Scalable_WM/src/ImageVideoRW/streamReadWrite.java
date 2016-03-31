
package ImageVideoRW;
import java.io.*;
import javax.swing.*;

/**
 * @author: Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 */
public class streamReadWrite {

    public double[] streamRead1D(String inputStream, int size, int skip){
        double[] codeStream = new double[size];
        FileInputStream inStream = null;
        BufferedInputStream bufferCodeStream = null;
        DataInputStream dataStream = null;

        try {
            inStream = new FileInputStream(inputStream);
            bufferCodeStream = new BufferedInputStream(inStream);
            dataStream = new DataInputStream(bufferCodeStream);
            dataStream.skipBytes(skip*8); //8 bytes for one double
            for(int i=0; i<size; i++){
                codeStream[i] = dataStream.readDouble();
            }
            dataStream.close();
            bufferCodeStream.close();
            inStream.close();
        } catch (IOException e) {
            System.out.println("Can not read code value from: " + inputStream);
            System.exit(-1);
        } // End of Catch

        return codeStream;
    }

    public double[][] IntermediateFrameRead(String inputVidFile, int[] size, int offset){
        double[][] dataFrame = new double[size[0]][size[1]];
        FileInputStream videoStream = null;
        BufferedInputStream bufferVideoStream = null;
        DataInputStream pixelStream = null;

        try {
            videoStream = new FileInputStream(inputVidFile);        
            bufferVideoStream = new BufferedInputStream(videoStream);        
            pixelStream = new DataInputStream(bufferVideoStream);
            
            pixelStream.skipBytes(offset*size[0]*size[1]*8); //8 bytes for one double
            for(int Yi=0; Yi<size[0]; Yi++){
                for(int Yj=0; Yj<size[1]; Yj++){
                    dataFrame[Yi][Yj] = pixelStream.readDouble();
                }
            }
            pixelStream.close();
            bufferVideoStream.close();
            videoStream.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Can not read pixel value from: " + inputVidFile, "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        } // End of Catch
        
        return dataFrame;
    }

    public void IntermediateFrameWrite(double[][] frame, String tempName, boolean fileStart){
        FileOutputStream tempFile;
        DataOutputStream dataStream;

       // FileOutputStream tempFileASCII;
        //String tempNameASCII = tempName.substring(0, tempName.length()-4) + "_ASCII" +  tempName.substring(tempName.length()-4, tempName.length());

        try {
                if(fileStart){
                    tempFile = new FileOutputStream(tempName);
                  //  tempFileASCII = new FileOutputStream(tempNameASCII);
                } else{
                    tempFile = new FileOutputStream(tempName, true);
                   // tempFileASCII = new FileOutputStream(tempNameASCII, true);
                }
                dataStream = new DataOutputStream(tempFile);                

                for (int i = 0; i < frame.length; i++) {
                    for (int j = 0; j < frame[0].length; j++) {
                        dataStream.writeDouble(frame[i][j]);
                     //   new PrintStream(tempFileASCII).println (frame[i][j]);

                    }
                }

                dataStream.close();
                tempFile.close();
               // tempFileASCII.close();
        } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Video writing failed", "ERROR", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
        }
    }


    public void IntermediateFrameWrite1D(double[] codeStream, String tempName, boolean fileStart){
        FileOutputStream tempFile;
        DataOutputStream dataStream;
        try {
                if(fileStart){
                    tempFile = new FileOutputStream(tempName);
                } else{
                    tempFile = new FileOutputStream(tempName, true);
                }
                dataStream = new DataOutputStream(tempFile);

                for (int i = 0; i < codeStream.length; i++) {
                    dataStream.writeDouble(codeStream[i]);
                }
                dataStream.close();
                tempFile.close();
        } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Video writing failed", "ERROR", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
        }
    }


}
