/**
 * @author: Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 *
 */

package Embedding;

import DSPnMath.Idwt;
import ImageVideoRW.Imwrite;
import ImageVideoRW.streamReadWrite;
import java.util.Arrays;

public class imagePull {
    public void imagePull(String codeFileName, String outName, double[] header, int headerSize, double rate){
        streamReadWrite rw = new streamReadWrite();
        int[] imSize = {(int)header[1], (int)header[2]};
        int level = (int)header[5];
        int depth = (int)header[7];
        int packetSize = (int)header[8];
        int noOfPackets = (int)header[9];

        //--------- Read the packet entries ------------------------------------
        double[] packets = new double[noOfPackets];
        packets = rw.streamRead1D(codeFileName, noOfPackets, headerSize);

        //--------- Find the truncation point ----------------------------------
        int rateIndex = packetSize * Math.abs(Arrays.binarySearch(packets, rate)+1);

        if(rateIndex/packetSize > noOfPackets) rateIndex = packetSize*noOfPackets;


        //------ Read code stream ----------------------------------------------                  
        //----------- LL subband to recover ------------------------------------
        int selectWidth = (int)(imSize[0]/Math.pow(2, level));
        int selectHeight = (int)(imSize[1]/Math.pow(2, level));
        double[] coeff = new double[selectWidth*selectHeight];
        double[] symbol = new double[selectWidth*selectHeight];

        if(rateIndex <= coeff.length){ //----- Rate within Main Pass ------------
            //------- Collect data from Original Coeff -------------------------
            coeff = rw.streamRead1D(codeFileName, coeff.length, (headerSize + noOfPackets));
            //------- Collect data from Main Pass  -----------------------------
            symbol = rw.streamRead1D(codeFileName, coeff.length, (headerSize +
                                                        noOfPackets + coeff.length));
            for(int i=0; i<rateIndex; i++){
                int tmp = (int)symbol[i];
                coeff[i] = coeffRecover(tmp,depth);
            }
        } else if((rateIndex>coeff.length) && (rateIndex<= 2*coeff.length)){ //--- Rate within Refinement Pass 1
            //------- Collect data from Main Pass  -----------------------------
            symbol = rw.streamRead1D(codeFileName, coeff.length, (headerSize +
                                                    noOfPackets + coeff.length));
            for(int i=0; i<coeff.length; i++){
                int tmp = (int)symbol[i];
                coeff[i] = coeffRecover(tmp,depth);
            }
            //------- Collect data from refinement Pass 1 ----------------------
            symbol = rw.streamRead1D(codeFileName, coeff.length, (headerSize +
                                                    noOfPackets + 2*coeff.length));
            for(int i=0; i<(rateIndex-coeff.length); i++){
                int tmp = (int)symbol[i];                
                coeff[i] = coeffRecover(tmp,depth);
            }
        } else {    //----------- Rate within Refinement Pass 2 ----------------
            //------- Collect data from Refinement Pass 1 ----------------------
            symbol = rw.streamRead1D(codeFileName, coeff.length, (headerSize +
                                                    noOfPackets + 2*coeff.length));
            for(int i=0; i<coeff.length; i++){
                int tmp = (int)symbol[i];
                coeff[i] = coeffRecover(tmp,depth);
            }
            //------- Collect data from Refinement Pass 2 ----------------------
            symbol = rw.streamRead1D(codeFileName, coeff.length, (headerSize +
                                                    noOfPackets + 3*coeff.length));            
            for(int i=0; i<(rateIndex-2*coeff.length); i++){
                int tmp = (int)symbol[i];
                coeff[i] = coeffRecover(tmp,depth);
            }

        }

        //----------- Recover the 2D structure of the coefficients -------------
        double[][] modifiedCoeff = new double[imSize[0]][imSize[1]];
        int k = 0;
        for(int i=0; i<selectWidth; i++){
            for(int j=0; j<selectHeight; j++){
                modifiedCoeff[i][j] = coeff[k++];
            }
        }

        //---------- Recover rest of the unmodified coefficients ---------------
        double[] restCodeCoeff = new double[imSize[0]*imSize[1] - selectWidth*selectHeight];
        //----------- 3 Pass + 1 original coeff set ----------------------------
        restCodeCoeff = rw.streamRead1D(codeFileName, restCodeCoeff.length,
                                            (headerSize + noOfPackets + 4*selectWidth*selectHeight));
        //--------------- Organising in 2D format ------------------------------
        k=0;
        for(int i=selectWidth; i<imSize[0]; i++){
            for(int j=0; j<imSize[1]; j++){
                modifiedCoeff[i][j] = restCodeCoeff[k++];
            }
        }
        for(int i=0; i<selectWidth; i++){
            for(int j=selectHeight; j<imSize[1]; j++){
                modifiedCoeff[i][j] = restCodeCoeff[k++];
            }
        }
        
        //-------------- IDWT --------------------------------------------------
        Idwt inverse = new Idwt();
        String wvName = null;
        if(header[6]==1) wvName = "HR";
        else if(header[6]==2) wvName = "97";
        else if(header[6]==3) wvName = "53";
        else if(header[6]==4) wvName = "MH";
        double[][] modifiedPixel = inverse.Idwt(modifiedCoeff, imSize, level, wvName);

        //----------- Image write ----------------------------------------------
        Imwrite image = new Imwrite();
        image.savePGM(modifiedPixel, imSize, outName);
    }


    public double coeffRecover(int treeVal, int depth){        
        //-------------- Recovery from tree ------------------------------------
        int symbol = treeVal / 10000;
        int sign = (treeVal/1000)%10;
        int K = treeVal % 1000;

        int Q_factor = (int)Math.pow(2,(depth-1));
        double recon = K*Q_factor;
        int tempTree = 0;
        for(int i=depth-1; i>0; i--){
            Q_factor /=2;
            tempTree = (int)Math.floor(symbol/Q_factor)%2;
            recon += tempTree * Math.pow(2,i-1);
        }
        if(sign>0) recon =(0-recon);
        
        return recon;
    }


}
