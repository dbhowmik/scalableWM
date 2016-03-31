/*
 * Imread.java
 *
 * @author Deepayan Bhowmik
 */

package ImageVideoRW;

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

        // get the image ID and authenticate    
            
            int readTemp;
            String magicNumber = null;            
            String widthString = null;
            String heightString = null;
            String maxValueString = null;
            
            //Check for the magic number 
            while(pixelStream.available() !=0){
                readTemp = pixelStream.readByte();                
                if(readTemp == 9 || readTemp == 10 || readTemp == 13 || readTemp == 32){
                    break;
                }
                     
                if(magicNumber == null){
                    magicNumber =String.valueOf((char)readTemp);
                } else{
                    magicNumber += (char)readTemp; 
                }
            }
            
            if(magicNumber.compareTo("P5")!=0){
                JOptionPane.showMessageDialog(null, "Image file: " + originalImageName + 
                        " is not a .PGM file", "ERROR", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }
            
            
            //Check for comment line and width
            int commentCount = 0;
            while(pixelStream.available() !=0){
                readTemp = pixelStream.readByte();                
                                                
                if((readTemp == 9 || readTemp == 10 || readTemp == 13 || readTemp == 32) && (commentCount ==-1)){
                    break;
                }
                
                if((char)readTemp == '#'){
                    commentCount++;
                } else if (commentCount != 1){
                    commentCount = -1;                    
                    if(readTemp != 9 && readTemp != 10 && readTemp != 13 && readTemp != 32){                        
                        if(widthString == null){
                            widthString =String.valueOf((char)readTemp);
                        } else{
                            widthString += (char)readTemp; 
                        }                        
                    }                    
                }
                                     
            }
            
            //Check for the height 
            while(pixelStream.available() !=0){
                readTemp = pixelStream.readByte();                
                if(readTemp == 9 || readTemp == 10 || readTemp == 13 || readTemp == 32){
                    break;
                }
                     
                if(heightString == null){
                    heightString =String.valueOf((char)readTemp);
                } else{
                    heightString += (char)readTemp; 
                }
            }                                  
            
            width = Integer.parseInt(widthString);
            height = Integer.parseInt(heightString);
              

            //Check for maxValue 
            while(pixelStream.available() !=0){
                readTemp = pixelStream.readByte();                
                if(readTemp == 9 || readTemp == 10 || readTemp == 13 || readTemp == 32){
                    break;
                }
                     
                if(maxValueString == null){
                    maxValueString =String.valueOf((char)readTemp);
                } else{
                    maxValueString += (char)readTemp; 
                }
            }
            
            if(maxValueString.compareTo("255")!=0){
                JOptionPane.showMessageDialog(null, originalImageName + 
                        " is not a gray scale image", "ERROR", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }
                     
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
        double[][] imageRead = new double[width][height]; 
        for (int j = 0; j < width; j++) {
            for (int i = 0; i < height; i++) {     
                imageRead[j][i] = (double)pixels[width*i + j];
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
