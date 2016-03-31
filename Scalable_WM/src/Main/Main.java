/**
 * @author: Deepayan Bhowmik
 *
 * E-mail: d.bhowmik@hw.ac.uk
 * Copyright reserved
 *
 */

package Main;

//import Embedding.preProcessEmbed;

import DSPnMath.ErrorCalculation;
import Embedding.Embedding;
import Embedding.imagePull;
import ImageVideoRW.Imread;
import ImageVideoRW.streamReadWrite;
import Watermark.Watermark;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import javax.swing.JOptionPane;


public class Main {
    public static final int HEADER_SIZE = 10;
    public static void main(String[] args) {               
        ReadParameter param = new ReadParameter();
        wmImage image = new wmImage();
        wmVideo video = new wmVideo();
        help usage = new help();
        
        String[] imageName = {"bt1", "b11"};
        
        for(int j=0; j<imageName.length; j++){
            prepParaFile("parameterFile\\paraFile.prm", imageName[j]);
            param.readParameterFile("parameterFile\\parameter.prm");
            //----------- Code Stream generation -----------------------------------
            String extension = param.getInName().substring(param.getInName().length()-3, param.getInName().length());
            //----------- Create header for the code stream ------------------------
            Main m = new Main();
            m.createHeader(extension);
            //------------ Watermark Embedding -------------------------------------
            if(extension.compareTo("pgm")==0){ // Image watermarking
                image.wmImage();
            } else if(extension.compareTo("yuv")==0){ // Video watermarking
                video.wmVideo();
            } else { // Error and go to help menu
                usage.usageHelp(1);
            }
            //----------------------------------------------------------------------
            //--------- Code extraction --------------------------------------------

            String codeFileName = param.getCodeStreamName();
            String outName = param.getOutName();
            String inName = param.getInName();

            BitPlaneDiscard ca = new BitPlaneDiscard();
            for(double rate=Embedding.minRate; rate<=Embedding.maxRate; rate +=(Embedding.maxRate-Embedding.minRate)/5){
                //---------- Watermark extraction ----------------------------------
                if(rate > Embedding.maxRate) rate = Embedding.maxRate;
                pull(codeFileName, outName+Math.floor(rate)+".pgm", inName, rate);

                //--------- Bit plane discarding -------------------------------
                ca.BitPlaneDiscard(outName+Math.floor(rate)+".pgm");
                double[] planeDiscard = {1, 2, 3, 4, 5, 6, 7};
                wmImageExt imExt = new wmImageExt();
                imExt.wmImageExt(outName+Math.floor(rate)+".pgm", 0);
                for(int i=0; i<planeDiscard.length; i++){
                    //--------- Watermark detection and authentication ---------
                    imExt.wmImageExt(outName+Math.floor(rate)+"_"+(i+1)+".pgm", planeDiscard[i]);
                }

//                //----------- JPEG 2000 ----------------------------------------
//                double[] bitRate = {4.0, 2.0, 1.0, 0.75, 0.5, 0.33, 0.25, 0.2, 0.16, 0.145 ,0.133, 0.125};
//                imExt.wmImageExt(outName+Math.floor(rate)+".pgm", 8);
//                for(int i=0; i<bitRate.length; i++){
//                    //--------- JPEG2000 compression ---------------------------
//                    decodeJPEG2000(outName+Math.floor(rate)+".pgm", bitRate[i]);
//                    //--------- Watermark detection and authentication ---------
//                    imExt.wmImageExt("code\\outJ2K.pgm", bitRate[i]);
//                }
        }

            if((Embedding.maxRate-Embedding.minRate)<0.05) break;
        }
        System.out.println("------------ End of program -------------");        
    }



    public static void pull(String codeStream, String outName, String inName, double rate){
        //---------- Read the header from the codestream -----------------------
        /*---- Header structure --
         *            [0] = 1: image, 2: video
         *            [1] = width;
         *            [2] = height;
         *            [3] = watermark length;
         *            [4] = Number of frames;
         *            [5] = Decomposition Level;
         *            [6] = Wavelet Name: 1-HR, 2-9/7, 3-5/3, 4-MH
         *            [7] = depth;
         *            [8] = packet size;
         *            [9] = no of packets;
        */
        streamReadWrite rw = new streamReadWrite();
        header = rw.streamRead1D(codeStream, HEADER_SIZE, 0);
        //---------- Image / Video pull ----------------------------------------
        imagePull pull = new imagePull();
        if(header[0]==1){
            pull.imagePull(codeStream, outName, header, HEADER_SIZE, rate);
            //----------- JPEG 2000 Encoding -----------------------------------
            encodeJPEG2000(outName);
            //----------- PSNR calculation -------------------------------------
            ErrorCalculation error = new ErrorCalculation();
            Imread im = new Imread();
            double[][] orgIm = im.Imread(inName);
            double[][] wmIm = im.Imread(outName);
            System.out.println("------------" + inName  + "--------------");
            System.out.format("Rate: %2.3f -- PSNR: %3.2f %n", rate, error.PSNR(orgIm, wmIm));

            //======================================================================
            //---------------- Save result in file ---------------------------------
            // Stream to write file
            FileOutputStream fout;
            String resultFile = "result\\" + inName.substring(0, inName.length()-4)+ "_st.txt";
            try
            {
                fout = new FileOutputStream (resultFile,true);
                new PrintStream(fout).println("------------" + inName  + "--------------");
                new PrintStream(fout).format("Rate: %2.3f -- PSNR: %3.2f %n", rate, error.PSNR(orgIm, wmIm));
                // Close our output stream
                fout.close();
            }
            // Catches any error conditions
            catch (IOException e)
            {
                    System.err.println("Error: " + e.getMessage());
                    System.exit(-1);
            }
            //---------------- Save result in file ---------------------------------
            //======================================================================

        } else if(header[0]==2){
            System.out.println("Video File pull");
        }
    }

    public static void encodeJPEG2000(String imageName) {
       // This program executes the .exe files of Kakadu software for JPEG2000
        Runtime runJPEG2000 = Runtime.getRuntime(); // Call runtime environments

       // Automatically write coded image with original image name
        String codeName = imageName.substring(0,imageName.length()-4);

       // Coding process starts here
        try {
            runJPEG2000.exec("cmd /c start kdu_compress -i " + imageName + " -o " + codeName + ".j2c -full");

        } catch (Exception e){
            System.err.println("Error: " + e.getMessage());
            System.exit(-1);
        }

    } // End of the function


    public static void decodeJPEG2000(String imName, double bitRate){        
        String imStream = imName.substring(0,imName.length()-4) + ".j2c";
        System.out.println("JPEG2000 decoding starts....");
        String outImName = "code\\outJ2K.pgm";
        // This program executes the .exe files of Kakadu software for JPEG2000
        Runtime runJPEG2000 = Runtime.getRuntime(); // Call runtime environments
        // Decoding process starts here
        try {
            Process decode=runJPEG2000.exec("cmd /c start kdu_expand -i " + imStream +
                " -o " + outImName + " -reduce " + 0 +
                " -rate " + bitRate);

            //------------------------------------------------------------------
            // Wait for the process to finish with error log
            //------------------------------------------------------------------
            InputStream stderr = decode.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            System.out.println("<OUTPUT>");
            while ( (line = br.readLine()) != null)
                System.out.println(line);
            System.out.println("</OUTPUT>");
            int exitVal = decode.waitFor();
            System.out.println("JPEG2000 decoding ends and exit value = " + exitVal);
        } catch (Exception e){
            System.err.println("Error: " + e.getMessage());
            System.exit(-1);
        }

    }


    public static double[] header = new double[HEADER_SIZE];
    public void createHeader(String extension){
        ReadParameter param = new ReadParameter();
        help usage = new help();
        Imread image = new Imread();
        Watermark wm = new Watermark();
        //------------- Writing header file ------------------------------------
        /*---- Header structure -- 
         *            [0] = 1: image, 2: video
         *            [1] = width;
         *            [2] = height;
         *            [3] = watermark length;
         *            [4] = Number of frames;
         *            [5] = Decomposition Level;
         *            [6] = Wavelet Name: 1-HR, 2-9/7, 3-5/3, 4-MH
         *            [7] = depth;
         *            [8] = packet size;
         *            [9] = no of packets;
        */
        
        if(extension.compareTo("pgm")==0){ // Image watermarking
            header[0] = 1;
            //------ Get image size --------------------------------------------
            image.Imread(param.getInName());
            header[1] = image.Size()[0];
            header[2] = image.Size()[1];
        } else if(extension.compareTo("yuv")==0){ // Video watermarking
            header[0] = 2;
            header[1] = param.getVidWidth();
            header[2] = param.getVidHeight();
        } else { // Error and go to help menu
            usage.usageHelp(1);
        }

        //---------- Get the watermark size ------------------------------------
        wm.Watermark();
        header[3] = wm.getWatermarkSize()[0] * wm.getWatermarkSize()[1];
        //---------- Get number of frames --------------------------------------
        header[4] = param.getNumFrame();
        //---------- Get decomposition level (image) ---------------------------
        header[5] = param.getDecompLevel();
        //---------- Get Wavelet name ::: 1-HR, 2-9/7, 3-5/3, 4-MH -------------
        if(param.getWaveletName().compareTo("HR")==0) header[6]=1;
        else if(param.getWaveletName().compareTo("97")==0) header[6]=2;
        else if(param.getWaveletName().compareTo("53")==0) header[6]=3;
        else if(param.getWaveletName().compareTo("MH")==0) header[6]=4;
        //---------- Get depth -------------------------------------------------
        header[7] = param.getDepth();        
    }

    public static void prepParaFile(String inFileName, String inName){
        File inFile = new File(inFileName);
        FileOutputStream outFile = null;
        String tempRead = null;
        String tempReadOut = null;

        //------------------------------------------------------------------
        if(!inFile.exists()){
            JOptionPane.showMessageDialog(null, "Input file does not exist. Try again.", "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
        //----------------------------------------------------------------------
        try{ // Read the file content
            BufferedReader paraInput = new BufferedReader(new FileReader(inFile));
            outFile = new FileOutputStream ("parameterFile\\parameter.prm");

            tempRead = paraInput.readLine();
            tempReadOut = "inName        " + "image\\"+ inName + ".pgm";
            new PrintStream(outFile).println(tempReadOut);

            tempRead = paraInput.readLine();
            tempReadOut = "outName         " + "code\\"+ inName;
            new PrintStream(outFile).println(tempReadOut);

            while(true){
                tempRead = paraInput.readLine();
                if(tempRead == null){
                    break;
                }
                new PrintStream(outFile).println(tempRead); // Saving image coeff value
            }
            //--- Close our output stream ---------------------------------------
            outFile.close();
            paraInput.close();

        } catch(IOException ioException) {
            JOptionPane.showMessageDialog(null,"File write error !!", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
   }

    public double[] getHeader(){
        return header;
    }

}
