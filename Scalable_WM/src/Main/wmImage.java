/**
 * @author: Deepayan Bhowmik
 *
 * E-mail: d.bhowmik@hw.ac.uk
 * Copyright reserved
 *
 */

package Main;

import DSPnMath.DWT;
import Embedding.Embedding;
import ImageVideoRW.Imread;
import ImageVideoRW.streamReadWrite;
import Watermark.Watermark;

public class wmImage {
    public void wmImage(){
        ReadParameter param = new ReadParameter();        
        //----------------- Read Watermark information -------------------------
        Watermark wm = new Watermark();
        wm.Watermark();
        //----------------- Read image and wavelet transform -------------------
        Imread image = new Imread();
        double[][] originalIm = image.Imread(param.getInName());
        //-------- DWT ---------
        DWT wavelet = new DWT();
        int level = param.getDecompLevel();
        int[] size = image.Size();
        double[][] coeff = wavelet.dwt(originalIm, size, level, param.getWaveletName());

        //---------- LL subband to be encoded ----------------------------------
        int selectWidth = (int)(size[0]/Math.pow(2, level));
        int selectHeight = (int)(size[1]/Math.pow(2, level));
        double[] codeCoeff = new double[selectWidth*selectHeight];
        int k = 0;
        for(int i=0; i<selectWidth; i++){
            for(int j=0; j<selectHeight; j++){
                codeCoeff[k++] = coeff[i][j];
            }
        }        
        //--------------- Get rest of the codes in one block -------------------
        double[] restCodeCoeff = new double[size[0]*size[1] - selectWidth*selectHeight];
        k = 0;
        for(int i=selectWidth; i<coeff.length; i++){
            for(int j=0; j<coeff[0].length; j++){
                restCodeCoeff[k++] = coeff[i][j];
            }
        }
        
        for(int i=0; i<selectWidth; i++){
            for(int j=selectHeight; j<coeff[0].length; j++){
                restCodeCoeff[k++] = coeff[i][j];
            }
        }
        //-------------------Watermark coding-----------------------------------
        Embedding embed = new Embedding();
        embed.Embedding(codeCoeff);
        //----------- put other image value in the stream -----------------------
        streamReadWrite stream = new streamReadWrite();
        stream.IntermediateFrameWrite1D(restCodeCoeff, param.getCodeStreamName(), false);
    }
}
