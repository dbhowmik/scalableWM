/*
 * @author Deepayan Bhowmik  
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 */

package Embedding;

import extract.*;
import javax.swing.*;



/* Main file containing embedding procedure
 * Call all the function sequentially and 
 *  save the watermarked image
 */
public class Main {
   
    public static void main(String[] args) {
        EmbedImage embed = new EmbedImage();
        CAattack content = new CAattack();
        Extract extract = new Extract(); 
        
        String[] BitCompress = {"8.0", "1.0", "0.75", "0.5", "0.33", "0.25", "0.18", "0.125"};
        String[] SpatialRes = {"0"};
        
        String[] parameterFile = {"parameter.txt"};
        String filename = null;
        ReadParameter param = new ReadParameter();
       
        for(int a=0; a<parameterFile.length; a++){
            
            filename = parameterFile[a];            
            //Watermark embedding                     
            embed.EmbedImage(filename);
        
            //Content adaptation attack
            content.CAattack(filename);
            content.timeDelay(1000);    
            //Watermark extraction              
            String imName = param.getWmImageName().substring(0,param.getWmImageName().length()-4);
            
            for(int i=0; i<SpatialRes.length; i++){
                for(int j=0; j<BitCompress.length; j++){
                    if(i==0&j==0){
                        extract.extract(filename, imName + ".pgm");
                        
                    } else{
                        extract.extract(filename, imName + i + j + ".pgm");                        
                    }
                                    
                }           
            }
            
            content.timeDelay(1000);   
        }
        
        
        JOptionPane.showMessageDialog(null, "End of Process");
        
   
    } //End of main function
     
} // End of the class
