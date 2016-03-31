/**
 * @author: Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 *
 */

package Main;

import DSPnMath.DWT;
import Extract.Decoding;
import ImageVideoRW.Imread;
import Watermark.Watermark;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class wmImageExt {
    public void wmImageExt(String testImName, double bitRate){
        ReadParameter param = new ReadParameter();        
        //----------------- Read Watermark information -------------------------
        Watermark wm = new Watermark();
        wm.Watermark();
        //----------------- Read image and wavelet transform -------------------
        Imread image = new Imread();
        double[][] testIm = image.Imread(testImName);
        //-------- DWT ---------
        DWT wavelet = new DWT();
        int level = param.getDecompLevel();
        int[] size = image.Size();
        double[][] coeff = wavelet.dwt(testIm, size, level, param.getWaveletName());

        //---------- LL subband to be decoded ----------------------------------
        int selectWidth = (int)(size[0]/Math.pow(2, level));
        int selectHeight = (int)(size[1]/Math.pow(2, level));
        double[] codeCoeff = new double[selectWidth*selectHeight];
        int k = 0;
        for(int i=0; i<selectWidth; i++){
            for(int j=0; j<selectHeight; j++){
                codeCoeff[k++] = coeff[i][j];
            }
        }
        //----------------- Decoding -------------------------------------------
        Decoding decode = new Decoding();
        decode.Decoding(codeCoeff);

        System.out.println("JPEG2000 bit rate (bpp): " + bitRate);
        System.out.format("Hamming Distance = %1.3f %n", decode.getHammDist());
        System.out.format("Similarity Measure = %1.3f %n", decode.getSimMeas());

        //======================================================================
        //---------------- Save result in file ---------------------------------
        // Stream to write file
        FileOutputStream fout;
        String resultFile = "result\\" + param.getInName().substring(0, param.getInName().length()-3)+ "txt";
        try
        {
            fout = new FileOutputStream (resultFile,true);
            //new PrintStream(fout).println("  ");
            new PrintStream(fout).println(bitRate + " " + decode.getHammDist() + " " + decode.getSimMeas());
           // new PrintStream(fout).println("JPEG2000 bit rate (bpp): " + bitRate);
           // new PrintStream(fout).format("Hamming Distance = %1.3f %n", decode.getHammDist());
           // new PrintStream(fout).format("Similarity Measure = %1.3f %n", decode.getSimMeas());
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
    }
}
