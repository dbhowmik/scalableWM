/**
 * @author: Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 */

package Result;

import Main.*;
import DSPnMath.ErrorCalculation;
import ImageVideoRW.VidRead;
import java.io.*;
import javax.swing.*;

public class ImperceptibilityResult {
    public void ImperceptibilityResult(String fileName, String wmVidName){
        ReadParameter parameter = new ReadParameter();
        VidRead read = new VidRead();
        ErrorCalculation error = new ErrorCalculation();
        
        double RMSError, PSNR, tempRMS = 0, tempPSNR = 0;        
        for(int offset=0; offset<parameter.getNumFrame(); offset++){
            //Read original video
            read.VidRead(parameter.getInName(),offset);
            double[][] Yoriginal = read.getYpixel();

            //Read watermarked video
            //read.VidRead(parameter.getOutName(),offset);
            read.VidRead(wmVidName,offset);
            double[][] Ywatermarked = read.getYpixel();

            //Calculate RMS Error
            RMSError = error.RmsError(Yoriginal,Ywatermarked);
            PSNR = error.PSNR(Yoriginal,Ywatermarked);

            tempRMS += RMSError;
            tempPSNR += PSNR;
            //Write down RMS and PSNR Value in a file to store
            saveStatFile(fileName, RMSError, PSNR, offset);
        }//End of rms value read

        double averageRMS = tempRMS / parameter.getNumFrame();
        double averagePSNR = tempPSNR / parameter.getNumFrame();
        saveStatFile(fileName, averageRMS, averagePSNR, -1);
    }

    public void saveStatFile(String FileName, double RMSE, double PSNR, int ind){ //Function to save PSNR and RMS value inside the file
        FileOutputStream coeffOutFile;
        String outFileString;

        outFileString = FileName.substring(0,FileName.length()-4) + "_Stat.txt";

        try
        {
            // Open an output stream
            if(ind==0){
                coeffOutFile = new FileOutputStream (outFileString);
            } else{
                coeffOutFile = new FileOutputStream (outFileString,true);
            }
           
            new PrintStream(coeffOutFile).println (+ ind + " " + RMSE + " # " + PSNR);
            
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
