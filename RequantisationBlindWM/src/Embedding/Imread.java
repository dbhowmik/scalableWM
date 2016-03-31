/*
 * Imread.java
 *
 * @author Deepayan Bhowmik
 */

package Embedding;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;


public class Imread {
    // Global variable declearations 
    BufferedImage img;
    static int width = 0;
    static int height = 0;
    
    
    /** Creates a new instance of Imread */
    // This is to read image file and return 2D array of pixels in double data type
    // The program now reads only PGM images now. But can be extended to .gif and other formats
    public double[][] Imread(String imageName) {  
        Imread imIO = new Imread();
        // Reading PGM images.
        double[][] readPixels = imIO.getPGM(imageName);
        return readPixels;
    }
    
    // Calculate and store the image size.
    public int[] Size() {
    int[] size = new int[2];
    size[0] = width;
    size[1] = height;
    
    return size;
    }
    
    public double[][] getPGM(String originalImageName){
        
        File imageFile = new File(originalImageName);
        // Declear variable to read image file 
        FileInputStream imageStream = null;
        BufferedInputStream bufferImageStream = null;
        DataInputStream pixelStream = null;
        int[] pixels = new int[65536]; // Image pixel values. Initialisation with a random value  
        
        try {
            imageStream = new FileInputStream(imageFile); 
       
        // BufferedInputStream is added for fast reading.
            bufferImageStream = new BufferedInputStream(imageStream);
        // Grabbing pixel value    
            pixelStream = new DataInputStream(bufferImageStream);

        
        /* Original image information reading starts here */    
            
        // Read image identification
           // Added comment on 27.06.2007 .. bugs are always there
            // Image header reading ... need to improve
        // get the image ID and authenticate    
            if(pixelStream.readLine().compareTo("P5")!=0){
                JOptionPane.showMessageDialog(null, "Image file: " + originalImageName + 
                        " is not a .PGM file", "ERROR", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }
            
         // Check for comment line and then discard it.
            String commentLine = pixelStream.readLine();
            String dimension;
            
         // Note some .pgm files does not have comment line   
            if(commentLine.charAt(0) == '#'){
                dimension = pixelStream.readLine();
                
            } else{
                dimension = commentLine; 
            }
            
         // Get and store the dimensions of the image
            
            int temp = dimension.indexOf(" ");
            String widthStrg = dimension.substring(0,temp);
            width = Integer.parseInt(widthStrg);
                    
            String heightStrg = dimension.substring(temp+1,dimension.length());
            height = Integer.parseInt(heightStrg);
            
          // Get rid of maxvalue and discard it
            pixelStream.readLine();
 
          // Pixel data reading strats here
            pixels = new int[width*height];
            int count = 0;
            while(pixelStream.available() !=0){
                pixels[count++] = pixelStream.readUnsignedByte(); // Read pixel values from Image file
            }
            
           
             
          // Dispose all the resources after using them.
            pixelStream.close();
            bufferImageStream.close();
            imageStream.close();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Can not read pixel value" + imageFile, "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
      
        } // End of Catch
        
        // Convert pixel data in 2D array and double data type
        double[][] imageRead = new double[height][width]; 
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {     
                imageRead[j][i] = (double)pixels[width*j + i];
            }
 	}
        
        return imageRead;
    } // End of getPGM function
   
    // This part of the program is not in use now. 
    // This is to read .gif images and returns double values in 2D array
    public double[][] CompressedImageFormat(String originalImageName){
        
       File imageFile = new File(originalImageName); 
       try {
           img = ImageIO.read(imageFile);
       } catch (IOException e) {
           System.out.println("Image loading failed");
       }
       
       width = img.getWidth();
       height = img.getHeight(); 
       
       int[] pixels = new int[width * height];
       PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, pixels, 0, width); 
       try { 
 	pg.grabPixels(); 
      } catch (InterruptedException e) {
          System.out.println("Pixel grabbing failed");
      }
        
       double[][] image_read = new double[height][width]; 
       for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {     
        image_read[j][i] = (double)((pixels[width*j + i] ) & 0xff);
          }
 	}
       
       return image_read;
    }
    
}
