/*
 * imPreview.java
 *
 * @author Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 */

package Embedding;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;


public class imPreview extends JFrame {
    static BufferedImage image2View = null;    
    
    
    public void imPreview(String imageString) {
        Imread image = new Imread();     
        imPreview imView = new imPreview();
      //  ReadParameter param = new ReadParameter();
        
        //Reading original image       
        double[][] originalImage = image.Imread(imageString);    
        int[] size = image.Size(); 
        
        int width = size[0];
        int height = size[1];       
        int[] pixel = new int[width * height];
        
        //Converting in 1D array of integers
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {     
                pixel[width*j + i] = (int)originalImage[j][i];
         }
 	}
        
        //Convert the integers into Image data type using raster
        InputStream in;       
        image2View = new BufferedImage (width, height, image2View.TYPE_BYTE_GRAY);
        WritableRaster raster = image2View.getRaster();
        raster.setPixels(0,0,width,height,pixel);
       
        //View the image now
        imView.setSize(size[0], size[1]);
        imView.show();        
        imView.setVisible(true);
             
    }
    

    //Call graphics function to view the image
     public void paint(Graphics draw) {
         
          draw.drawImage(image2View,0,0,null);
   }
    
    
}
