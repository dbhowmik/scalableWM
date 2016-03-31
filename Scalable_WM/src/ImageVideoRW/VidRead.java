/*
 * VidRead.java
 *
 *
 * @author Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 */

package ImageVideoRW;

import Main.*;
import java.io.*;
import javax.swing.*;



public class VidRead {
    public static double[][] YvideoRead;
    public static double[][] UvideoRead;  
    public static double[][] VvideoRead; 
    
    public void VidRead(String inputVidFile, int offset) {
        VidRead read = new VidRead();
        // Declear variable to read video file 
        FileInputStream videoStream = null;
        BufferedInputStream bufferVideoStream = null;
        DataInputStream pixelStream = null;        
        
        YvideoRead = new double[YSize()[0]][YSize()[1]];
        UvideoRead = new double[UVSize()[0]][UVSize()[1]]; 
        VvideoRead = new double[UVSize()[0]][UVSize()[1]];
        try {
            videoStream = new FileInputStream(inputVidFile);        
        // BufferedInputStream is added for fast reading.
            bufferVideoStream = new BufferedInputStream(videoStream);
        // Grabbing pixel value    
            pixelStream = new DataInputStream(bufferVideoStream);
            
            pixelStream.skipBytes(offset*(read.YSize()[0]*read.YSize()[1] + 2*read.UVSize()[0]*read.UVSize()[1]));
        /* Original video information reading starts here */
                       
            for(int Yj=0; Yj<YSize()[1]; Yj++){
                for(int Yi=0; Yi<YSize()[0]; Yi++){
                    YvideoRead[Yi][Yj] = (double)pixelStream.readUnsignedByte();                            
                }
            }
                       
            for(int Uj=0; Uj<UVSize()[1]; Uj++){
                for(int Ui=0; Ui<UVSize()[0]; Ui++){
                    UvideoRead[Ui][Uj] = (double)pixelStream.readUnsignedByte();                          
                }
            }
            
            for(int Vj=0; Vj<UVSize()[1]; Vj++){
                for(int Vi=0; Vi<UVSize()[0]; Vi++){
                    VvideoRead[Vi][Vj] = (double)pixelStream.readUnsignedByte();                           
                }
            }
                                        
            pixelStream.close();
            bufferVideoStream.close();
            videoStream.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Can not read pixel value from " + inputVidFile, "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);      
        } // End of Catch
    }
    
    
    
    // Calculate and store the video size.
    public int[] YSize() {
        ReadParameter param = new ReadParameter();
        int[] size = new int[2];
        size[0] = param.getVidWidth();
        size[1] = param.getVidHeight();
    
        return size;
    }

    public int[] UVSize() {
        ReadParameter param = new ReadParameter();
        int[] size = new int[2];
    
   //     if(param.getVidFormat()=="420"){
            size[0] = param.getVidWidth()/2;
            size[1] = param.getVidHeight()/2;
  //      } else{
  //          size[0] = param.getVidWidth();
  //          size[1] = param.getVidHeight();
  //      }
    
        return size;
    }
    
    public double[][] getYpixel(){
        return YvideoRead;
    }
    
    public double[][] getUpixel(){
        return UvideoRead;
    }
    
    public double[][] getVpixel(){
        return VvideoRead;
    }
}
