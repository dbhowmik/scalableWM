/*
 * Extract.java
 *
 * @author Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 */

package extract;

import java.io.*;
import javax.swing.*;
import Embedding.*;

//Extract program deocde then watermarked images to extract the watermark and 
// then authenticate with orginal one
public class Extract {
       
    public void extract(String filename, String imageName) {
       // This main function is the collection of functions to perform extraction procedure 
        ReadParameter parameter = new ReadParameter();
        Imread image = new Imread();
        Watermark watermark = new Watermark();
        Dwt wavelet = new Dwt();
        Decoding decode = new Decoding();
        Authentication compare = new Authentication();
        Rescaling reScale = new Rescaling();
        
        
        // Read parameter file and set all data to go
        parameter.readEmbedParameter(filename);

        // getting watermark value
        watermark.Watermark();
        
        //Reading watermarked image       
        double[][] watermarkedImage = image.Imread(imageName);    
        int[] wmSize = image.Size();
       
        double[][] dwtWmImageCoeff = reScale.Rescaling(watermarkedImage,wmSize);
        
        // Forward wavelet transform
    //    double[][] dwtWmImageCoeff = wavelet.dwt(rescaledImage,wmSize,parameter.getDecompLevel(),parameter.getWaveletName());
        
        // Perform extraction operation
        decode.Decoding(dwtWmImageCoeff,reScale.orSize,parameter.getDecompLevel());
        
        // Perform authentication
        compare.Authentication();
        
        // Save the result in result file
        saveFile(filename, imageName);
        
        // Output and end of the extraction program
        // JOptionPane.showMessageDialog(null, "Hamming distance: " + compare.getHammDist());
        
        
    } //End of extract main
    
    
     public void saveFile(String FileName, String ImageName){ //Function to save PSNR and RMS value inside the file
        FileOutputStream coeffOutFile;	        
        Authentication auth = new Authentication();
        
        String outFileString;        
        outFileString = FileName.substring(0,FileName.length()-4) + "_result.txt";
        	    
        try
        {
	    // Open an output stream
	    coeffOutFile = new FileOutputStream (outFileString, true);
            new PrintStream(coeffOutFile).println (ImageName + " " + auth.getHammDist()); // Saving image coeff value
          // Close our output stream
	    coeffOutFile.close();		
        }
	// Catches any error conditions
        catch (IOException e)
	{
            JOptionPane.showMessageDialog(null,"Unable to write to file");
            System.exit(-1);
	}
        
    }
    
} // End of the class
