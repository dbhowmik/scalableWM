/*
 * Imwrite.java
 *
 *
 * @author Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 *
 */

package ImageVideoRW;

import java.io.*;
import javax.swing.*;


public class Imwrite { // Class for image save. 
    
    
    public Imwrite() {
       // testing area and left for future use.
    }
    
    // Function to save image in .pgm format. Other format can be included. 
    public void savePGM(double[][] image, int[] size, String imageName){
        
        // Variable initialisation 
        int width = size[0];
        int height = size[1];
        byte[] pixel = new byte[width * height];
        
        //Convert all double type data into byte with 1D array called pixels
         for (int j = 0; j < width; j++) {
            for (int i = 0; i < height; i++) {     
                //Trancation and rounding between 0-255
                if(image[j][i]>255){
                    image[j][i] = 255.0;
                }                
                if(image[j][i]<0){
                    image[j][i] = 0.0;
                }
               
                //Convert the data in byte type
                pixel[width*i + j] = (byte)image[j][i];
         }
 	}
        
        // Writing data to image file starts here
        
        FileOutputStream imageFile; 
        DataOutputStream dataStream;
        
        try {
                imageFile = new FileOutputStream(imageName);
                dataStream = new DataOutputStream(imageFile);
                
                //Write image identity
                dataStream.writeBytes("P5\n");
                //Write comment
                dataStream.writeBytes("#Created by WEBCAM s/w. Image name: " + imageName + " @Copyright Deepayan Bhowmik#\n");
                //Put image width and height
                dataStream.writeBytes(width + " " + height + "\n");
                //Put max value of the pixels
                dataStream.writeBytes("255\n");
                
                //Put actual pixel values
                dataStream.write(pixel,0,width*height);
      
        } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Image writing failed", "ERROR", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
        }

        
    } // End of write PGM image
    
}
