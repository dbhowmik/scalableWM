/*
 * Authentication.java
 *
 * @author Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 */

package extract;

import Embedding.*;

//This package authenticate the extracted watermark with original watermark
public class Authentication {
    public static double hammingDistance;
    
    public void Authentication() {
         //Currently calculating the hamming distance
        Watermark orgWater = new Watermark();
        Decoding extWater = new Decoding();
        
        hammingDistance = 10.0e4; // Initialise the value
        double tempDist = 0;
        int lengthWatermark = 0;
        //Formula: normalised hamming distance
        //  HD = sum(W_org XOR W_ext) / Length ; 
        for(int i=0; i<orgWater.getWatermark().length; i++){           
            if((extWater.getExtractedWatermark2Compare()[i]==0) || (extWater.getExtractedWatermark2Compare()[i]==1) ){
                // Xor implementation indirectly
                if(orgWater.getWatermark()[i] == extWater.getExtractedWatermark2Compare()[i]){
                    tempDist = tempDist + 0;
             
                } else{
                    tempDist = tempDist + 1;
                }  // End of inner if else condition 
                
                lengthWatermark++; 
            } //End of external if-loop
        }
        
        
        hammingDistance = tempDist / lengthWatermark;
  
    }
    
    public double getHammDist(){
        return hammingDistance;
    }
    
}
