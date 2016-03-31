/*
 * Watermark.java
 *
 * @author Deepayan Bhowmik
 *
 *  Department of Electronic and Electrical Engineering
 *  University of Sheffield
 *  Copyright reserved.
 *
 */

package Embedding;

import javax.swing.*;
import java.io.*;

/* Creating watermark value. Normally values are in gray scale.
 *  We convert it to binary by setting the threshold of 127
 *   pixel value greater then 128 converted as 1 and below 0
 */
public class Watermark {
    static double[] watermark;  
    static int[] watermarkSize;
    
    // Do the watermark pixel conversion.
    // read only PGM images 
    public void Watermark() {
        Imread image = new Imread();
        ReadParameter para = new ReadParameter();
        
        // Get the watermark name from parameter file
           
      //  System.out.println(para.getWatermarkName());
        //Read PGM file
        double[][] watermark2D = image.Imread(para.getWatermarkName());     
        watermarkSize = image.Size();
        
        watermark = new double[watermarkSize[0]*watermarkSize[1]];
        
        /*Convert the 2D image into 1D and convert 
         *  the gray scale image into binary form by threshold of 127
        `*/
        for(int i=0;i<watermarkSize[1];i++){
            for(int j=0;j<watermarkSize[0];j++){
                if(watermark2D[i][j]>127){
                    watermark[i*watermarkSize[0] + j] = 1;
                } else{
                    watermark[i*watermarkSize[0] + j] = 0;
                } //End of if-else
            } // End of inner loop
        } // End of outer loop
    } // End of function
    
    public double[] getWatermark(){
        return watermark;
    }
    
    public int[] getWatermarkSize(){
        return watermarkSize;
    }
    
} // End of watermark class
