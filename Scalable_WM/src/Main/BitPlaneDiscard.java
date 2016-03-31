/**
 * @author: Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 */

package Main;

import DSPnMath.DWT;
import DSPnMath.Idwt;
import ImageVideoRW.Imread;
import ImageVideoRW.Imwrite;

public class BitPlaneDiscard {
    public void BitPlaneDiscard(String imageName){
        String[] planeDiscard = {"1", "2", "3", "4", "5", "6", "7"};

        ReadParameter parameter = new ReadParameter();
        Imread image = new Imread();
        DWT wavelet = new DWT();
        Idwt IDWT = new Idwt();
        Imwrite write = new Imwrite();

        //Reading original image
        double[][] originalImage = image.Imread(imageName);
        int[] size = image.Size();

        double[][] dwtImageCoeff = wavelet.dwt(originalImage,size,parameter.getDecompLevel(),parameter.getWaveletName());

        double[][] qntImageCoeff = new double[dwtImageCoeff.length][dwtImageCoeff[0].length];
        double[][] dqntImageCoeff = new double[dwtImageCoeff.length][dwtImageCoeff[0].length];
        for(int k=0; k<planeDiscard.length; k++){
            double QF = Math.pow(2, Integer.parseInt(planeDiscard[k]));
            for(int i=0; i<dwtImageCoeff.length; i++){
                for(int j=0; j<dwtImageCoeff[0].length; j++){
                    //Quantisation
                    qntImageCoeff[i][j] = Math.floor(dwtImageCoeff[i][j]/QF);

                    //Dequantisation
                    dqntImageCoeff[i][j] = qntImageCoeff[i][j]*QF + (QF-1)/2;
                }
            }
            double[][] quantisedPixels = IDWT.Idwt(dqntImageCoeff,size,parameter.getDecompLevel(),parameter.getWaveletName());

            String qnName = imageName.substring(0, imageName.length()-4) + "_" + planeDiscard[k] + ".pgm";
            // Save watermarked image
            write.savePGM(quantisedPixels,size,qnName);
        }
    }

}

