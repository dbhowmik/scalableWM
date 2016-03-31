/*
 * CAattack.java
 *
 * @author Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 */

package Embedding;
import java.io.*;
import javax.swing.*;
import java.util.*;


public class CAattack {
    
    String[] BitCompress = {"8.0", "1.0", "0.75", "0.5", "0.33", "0.25", "0.18", "0.125"};
    String[] SpatialRes = {"0"};
    
    public void CAattack(String filename) {
        ReadParameter parameter = new ReadParameter();
        parameter.readEmbedParameter(filename);
        String imName = parameter.getWmImageName().substring(0,parameter.getWmImageName().length()-4);
        String codeName = imName + ".j2c";
        
        timeDelay(500);
        for(int i=0; i<SpatialRes.length; i++){
            for(int j=0; j<BitCompress.length; j++){
                
                decodeJPEG2000(codeName, imName + i + j + ".pgm", SpatialRes[i], BitCompress[j]);
            }
            
        }
               
    }
    
    public void decodeJPEG2000(String originalCode, String image2Save, String spatialSc, String qualitySc) {
        
        // This program executes the .exe files of Kakadu software for JPEG2000 
        Runtime runJPEG2000 = Runtime.getRuntime(); // Call runtime environments       
        
       // Decoding process starts here 
        try {
            Process expand1=runJPEG2000.exec("cmd /c start kdu_expand -i " + originalCode + 
                    " -o " + image2Save + " -reduce " + spatialSc + " -rate " + qualitySc);
            
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, "JPEG2000 coding failed", "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);    
        }
    } // End of the function
    
    public void timeDelay(int miliSec){
        try
        {
            Thread.sleep(miliSec); // do nothing for 1000 miliseconds (1 second)
        }
            catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    
    
    
}
