/*
 * Embedding.java
 *
 * @author Deepayan Bhowmik
 */

package Embedding;


import ImageVideoRW.Imread;
import ImageVideoRW.streamReadWrite;
import Main.Main;
import Watermark.Watermark;
import Main.ReadParameter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;


public class Embedding {
    public static double minRate, maxRate;
    public void Embedding(double[] coeff) {
        ReadParameter param = new ReadParameter();
        //------------------ Get the watermark ---------------------------------
        Watermark wm = new Watermark();
        wm.Watermark();
        int[] orgWm = wm.getWatermark();
        int depth = param.getDepth();
        //------------------ Get the error calculations ------------------------
        //-------- Packet array for 3 passes with intial position indicate number of packet--
        int noOfPackets = 3*(coeff.length/param.getPacketSize());
        double[] packets = new double[noOfPackets];        
        //------------------ Other definations ---------------------------------        
        double[] codePass1 = new double[coeff.length];
        double[] codePass2 = new double[coeff.length];
        double[] codePass3 = new double[coeff.length];
        //---- Symbol creation and Pass 1 --------------------------------------
        int wmCount = 0;
        int packetCount = 0;
        int wmCalcCount = 0;

        //-------------- Encoding Main Pass  -----------------------------------
        for(int i=0; i<coeff.length; i++){
            double tmpCoeff = coeff[i];
            int symbol = formTree(Math.round(tmpCoeff), depth);
            //System.out.println(symbol);
            double[] tmp = enPassMain(symbol,orgWm[wmCount], depth);
            codePass1[i] = tmp[0];
            //System.out.println(codePass1[i]);
          //------- Packet content and update ----------------------------------
            if(((i+1)%param.getPacketSize())==0) {
                packetCount++;
                if(packetCount<packets.length){
                    packets[packetCount] = packets[packetCount-1] + tmp[1];
                }
            } else {
                packets[packetCount] += tmp[1];
            }
            //------- Watermark count update -----------------------------------
            if(++wmCount>=orgWm.length) wmCount=0;
            wmCalcCount++;
        }

        //-------------- Encoding Refinement Pass 1 ----------------------------
        for(int i=0; i<coeff.length; i++){
            int tmpCoeff = (int)codePass1[i];
            double[] tmp = enPassRefinement(tmpCoeff, depth);
            codePass2[i] = tmp[0];           
            //------- Packet content and update ----------------------------------
            if(((i+1)%param.getPacketSize())==0) {
                packetCount++;
                if(packetCount<packets.length){
                    packets[packetCount] = packets[packetCount-1] + tmp[1];
                }
            } else {
                packets[packetCount] += tmp[1];
            }
        }

        //-------------- Encoding Refinement Pass 2 ----------------------------
        for(int i=0; i<coeff.length; i++){
            int tmpCoeff = (int)codePass2[i];
            double[] tmp = enPassRefinement(tmpCoeff, depth);
            codePass3[i] = tmp[0];
            //------- Packet content and update ----------------------------------
            if(((i+1)%param.getPacketSize())==0) {
                packetCount++;
                if(packetCount<packets.length){
                    packets[packetCount] = packets[packetCount-1] + tmp[1];
                }
            } else {
                packets[packetCount] += tmp[1];
            }
        }
        
        //----------- Normalise the packet values to create the resource rate --
        System.out.println("----------------- Resource list -------------------------");
        System.out.println("Image Name: " + param.getInName());
        System.out.println("==============================================");
        System.out.println("     Packet Id      " + "  Embedding Budget");
        System.out.println("==============================================");
        
        for(int i=0; i<packets.length; i++){
            packets[i] /= wmCalcCount;
        }

        minRate = packets[packets.length/3-1];
        maxRate = packets[packets.length-1];
        System.out.format(" Minimum (%d)  --->   %4.3f %n", packets.length/3-1, minRate);
        System.out.format(" Maximum (%d)  --->   %4.3f %n", packets.length-1, maxRate);
        System.out.println("----------------------------------------------");
        System.out.println("Total Packets = " + packets.length);
        System.out.println("==============================================");

        //======================================================================
        //---------------- Save result in file ---------------------------------
        // Stream to write file
        FileOutputStream fout;
        String resultFile = "result\\" + param.getInName().substring(0, param.getInName().length()-4)+ "_rs.txt";
        try
        {
            fout = new FileOutputStream (resultFile);            
            new PrintStream(fout).println("----------------- Resource list -------------------------");
            new PrintStream(fout).println("Image Name: " + param.getInName());
            new PrintStream(fout).println("==============================================");
            new PrintStream(fout).println("     Packet Id      " + "  Embedding Budget");
            new PrintStream(fout).println("==============================================");
            new PrintStream(fout).format(" Minimum (%d)  --->   %4.3f %n", packets.length/3-1, minRate);
            new PrintStream(fout).format(" Maximum (%d)  --->   %4.3f %n", packets.length-1, maxRate);
            new PrintStream(fout).println("----------------------------------------------");
            new PrintStream(fout).println("Total Packets = " + packets.length);
            new PrintStream(fout).println("==============================================");
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

        //-------------- Update the header file with no of packets -------------
        Main m = new Main();
        double[] header = m.getHeader();
        header[8] = param.getPacketSize();
        header[9] = noOfPackets;
        //----------------------------------------------------------------------
        //-------------- Create the code stream --------------------------------
        streamReadWrite stream = new streamReadWrite();
        //-------------- Write header file -------------------------------------
        stream.IntermediateFrameWrite1D(header, param.getCodeStreamName(), true);
        //-------------- Write the packets -------------------------------------
        stream.IntermediateFrameWrite1D(packets, param.getCodeStreamName(), false); 
        //----------------------------------------------------------------------
        //-------------- Save the code stream for Original Coeff  --------------
        stream.IntermediateFrameWrite1D(coeff, param.getCodeStreamName(), false);
        //-------------- Save the code stream for Main Pass  -------------------
        stream.IntermediateFrameWrite1D(codePass1, param.getCodeStreamName(), false);
        //-------------- Save the code stream for refinement Pass 1 ------------
        stream.IntermediateFrameWrite1D(codePass2, param.getCodeStreamName(), false);
        //-------------- Save the code stream for refinement Pass 2 ------------
        stream.IntermediateFrameWrite1D(codePass3, param.getCodeStreamName(), false);
        
    }

    //-------------- Tree and symbol formation ---------------------------------
    public int formTree(double Scoeff, int depth){
        int symbol = 0;
        int sign = 0;
        if(Scoeff<0) sign=1;
        int coeff = (int)Math.abs(Scoeff);
        int Q_factor = (int)Math.pow(2,(depth-1));
        //---------- Find the multiplication factor for each symbol ------------
        int K = (int)Math.floor(coeff/Q_factor);
        //---------- Tree formation --------------------------------------------
        int tree = 0;
        int tempTree = 0;
        for(int i=depth-1; i>0; i--){
            Q_factor /=2;
            tempTree = ((int)Math.floor(coeff/Q_factor))%2;
            tree += tempTree * Math.pow(2,i-1);            
        }
        symbol = (tree*10 + sign)*1000 + K;
        return symbol;
    }

    //-------------- Watermark encoding: Main Pass -----------------------------
    public double[] enPassMain(int symbol, int wm, int depth){        
        int Q_factor = (int)Math.pow(2,(depth-1));
        int K = symbol%1000;
        int sign = (symbol/1000)%10;
        symbol /= 10000;
        double err1 = K;
        double err2 = symbol;
        switch(wm){
            case 0:
                if(K%2==0){ // Tree 0xx
                    if((int)Math.floor(symbol/(Q_factor/2))%2!=0){ // Tree 01x
                        if((int)Math.floor(symbol/(Q_factor/4))%2!=0){ // Tree 011
                            symbol -= Q_factor/4;                      // Make 010
                        }
                    }
                } else {                                // Tree 1xx
                    if((int)Math.floor(symbol/(Q_factor/2))%2!=0){  // Tree 11x
                        symbol -= Q_factor/2;                       // Make 10x
                        if((int)Math.floor(symbol/(Q_factor/4))%2!=0){ // Tree 101
                            symbol -= Q_factor/4;                      // Make 100
                        }
                    } else {                                           // Tree 10x
                        if((int)Math.floor(symbol/(Q_factor/4))%2!=0){ // Tree 101
                            symbol -= Q_factor/4;                      // Make 100
                        }
                    }
                }
                break;
            case 1:
                if(K%2==1){  // Tree 1xx
                    if((int)Math.floor(symbol/(Q_factor/2))%2!=1){  // Tree 10x
                        if((int)Math.floor(symbol/(Q_factor/4))%2!=1){ // Tree 100
                            symbol += Q_factor/4;                      // Make 101
                        }
                    }
                } else {                                // Tree 0xx
                    if((int)Math.floor(symbol/(Q_factor/2))%2!=1){  // Tree 00x
                        symbol += Q_factor/2;                       // Make 01x
                        if((int)Math.floor(symbol/(Q_factor/4))%2!=1){  // Tree 010
                            symbol += Q_factor/4;                       // Make 011
                        }
                    } else {                                            // Tree 01x
                        if((int)Math.floor(symbol/(Q_factor/4))%2!=1){  // Tree 010
                            symbol += Q_factor/4;                       // Make 011
                        }
                    }
                }
                break;
        }
        //----- Square error -------------------
        double err = Math.pow(Math.abs(err1-K)*Q_factor+Math.abs(err2-symbol),2);
        //---------- Reconstruct the symbol for code stream --------------------
        symbol = (symbol*10 + sign)*1000 + K;        
        //------------------- Collecting modified symbol and error -------------
        double[] symNerr = new double[2];
        symNerr[0] = symbol;
        symNerr[1] = err;
        return symNerr;
    }
        
    //-------------- Watermark encoding refinement pass ------------------------
    public double[] enPassRefinement(int symbol, int depth){        
        int Q_factor = (int)Math.pow(2,(depth-1));
        int K = symbol%1000;
        int sign = (symbol/1000)%10;
        symbol /= 10000;
        double err1 = K;
        double err2 = symbol;
        if(K%2==0){ // Tree 0xx
            if((int)Math.floor(symbol/(Q_factor/2))%2==0){ // Tree 00x (EZ)
                symbol += 0;                 // No change required in refinement pass
            } else {                        // Tree 01x
                if((int)Math.floor(symbol/(Q_factor/4))%2==0){ // Tree 010 (CZ->EZ)
                    symbol -= Q_factor/2;                      // Make 010 -> 000
                    symbol += Q_factor/4;                      // Make 000 -> 001 (EZ)
                } else {                // Tree 011 (WO->CO)                    
                    K++;                //symbol += Q_factor;  Make 011 -> 111
                    symbol -= Q_factor/2; // Make 111 -> 101 (CO)
                }
            }
        } else {                                // Tree 1xx
            if((int)Math.floor(symbol/(Q_factor/2))%2==1){  // Tree 11x (EO)
                symbol += 0;     // No change required in refinement pass
            } else {                                           // Tree 10x
                if((int)Math.floor(symbol/(Q_factor/4))%2==1){ // Tree 101 (CO->EO)
                    symbol += Q_factor/2;                      // Make 101 -> 111
                    symbol -= Q_factor/4;                      // Make 111 -> 110 (EO)
                } else {                    // Tree 100 (WZ->CZ)                    
                    K--;                    //symbol -= Q_factor; Make 100 -> 000
                    symbol += Q_factor/2;   // Make 000 -> 010 (CZ)
                }
            }
        }                        
        //----- Square error -------------------
        double err = Math.pow(Math.abs(err1-K)*Q_factor+Math.abs(err2-symbol),2);
        //---------- Reconstruct the symbol for code stream --------------------
        symbol = (symbol*10 + sign)*1000 + K;        
        //------------------- Collecting modified symbol and error -------------
        double[] symNerr = new double[2];
        symNerr[0] = symbol;
        symNerr[1] = err;
        return symNerr;
    }    
        
}


