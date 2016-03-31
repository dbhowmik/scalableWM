/*
 * WatermarkCollection.java
 *
 * @author Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 *
 */

package extract;
import Embedding.*;

//This function deals with extracted watermark realetd issues

public class WatermarkCollection {
        
    //Get the extracted watermark and collect it in proper form. 
    // Strengthen your decision by majority rule
    public double[][] WatermarkCollection(double[] wmRecieved) {
        Watermark orgWater = new Watermark();
     
        double[][] tempWatermark;        
        int wmIndex =0;
        
                
        int wmIndexCount = (int)Math.floor(wmRecieved.length / orgWater.getWatermark().length) + 1;
        
        // Initialise extracted watermark array accordingly
        tempWatermark = new double[wmIndexCount][orgWater.getWatermark().length];
        
        //Initialise tempWatermark value to protect false positive
        for(int a=0;a<tempWatermark.length;a++){
            for(int b=0;b<tempWatermark[0].length;b++){
                tempWatermark[a][b] = -1.0;
            }
        }

        for(int i=0; i<wmIndexCount; i++){
            for(int j=0; j<tempWatermark[0].length; j++){
                //Get the extracted watermarked value arranged
                if((tempWatermark[0].length*i + j) < wmRecieved.length){
                    tempWatermark[i][j] = wmRecieved[tempWatermark[0].length*i + j];
                } else{
                    break;
                }
            }
        }// End of for loop
           
        return tempWatermark;
    }
    
   
}
