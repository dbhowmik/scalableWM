/*
 * EmbedImage.java
 *
 *
 * @author Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 *
 */

package Embedding;
import java.io.*;
import javax.swing.*;

public class EmbedImage {
    double psnr, rms_error;
   
    
// Watermark embedding for Image    
    public void EmbedImage(String filename) {
        ReadParameter parameter = new ReadParameter();
        Imread image = new Imread();
        Watermark watermark = new Watermark();
            
        Dwt wavelet = new Dwt();
        Idwt inv_wavelet = new Idwt();

        Embedding embed = new Embedding();
        
        Imwrite im_save = new Imwrite();
        ErrorCalculation error_cal = new ErrorCalculation();
           
        // Read parameter file and set all data to go
        parameter.readEmbedParameter(filename);
        
                
        //Reading original image       
        double[][] originalImage = image.Imread(parameter.getOriginalImageName());    
        int[] size = image.Size(); 
     
        // getting watermark value
        watermark.Watermark();   
        
        // Forward wavelet transform
        double[][] dwtImageCoeff = wavelet.dwt(originalImage,size,parameter.getDecompLevel(),parameter.getWaveletName());
      
        // Watermark Embedding
        double[][] wmImageCoeff = embed.Embedding(dwtImageCoeff,size,parameter.getDecompLevel());
        
        // Inverse wavelet transformation
        double[][] watermarkedPixels = inv_wavelet.Idwt(wmImageCoeff,size,parameter.getDecompLevel(),parameter.getWaveletName());
        
        // Save watermarked image
        im_save.savePGM(watermarkedPixels,size,parameter.getWmImageName());        
    
        //Calculate RMS Error and PSNR value 
        double[][] originalImageCopy = image.Imread(parameter.getOriginalImageName());    
        double[][] watermarkedImage = image.Imread(parameter.getWmImageName());  
        rms_error = error_cal.RmsError(originalImageCopy,watermarkedImage);
        psnr = error_cal.PSNR(originalImageCopy,watermarkedImage);
        encodeJPEG2000();
        
        //Write down RMS and PSNR Value in a file to store
        saveFile(filename);
        
    }
    
    public double getPSNR(){
        return psnr;
    }
    
    public double getRMS(){
        return rms_error;
    }
    
    public void encodeJPEG2000() {
        
        ReadParameter parameter = new ReadParameter();
        String imageName = parameter.getWmImageName();
       // This program executes the .exe files of Kakadu software for JPEG2000 
        Runtime runJPEG2000 = Runtime.getRuntime(); // Call runtime environments
        
       // Automatically write coded image with original image name
        String codeName = imageName.substring(0,imageName.length()-4);
        
       // Coding process starts here 
        try {
            Process fullCode=runJPEG2000.exec("cmd /c start kdu_compress -i " + imageName + " -o " + codeName + ".j2c -full");
            
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, "JPEG2000 coding failed", "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);    
        }
        
    } // End of the function
    
    public void saveFile(String FileName){ //Function to save PSNR and RMS value inside the file
        FileOutputStream coeffOutFile;	
        String outFileString;
        
        outFileString = FileName.substring(0,FileName.length()-4) + "_result.txt";
        	    
        try
        {
	    // Open an output stream
	    coeffOutFile = new FileOutputStream (outFileString);
            new PrintStream(coeffOutFile).println (+ getPSNR() + " " + getRMS()); // Saving image coeff value
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
    
   
    
}
