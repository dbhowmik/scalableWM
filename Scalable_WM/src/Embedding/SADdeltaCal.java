/**
 * @author: Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 */

package Embedding;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import javax.swing.JOptionPane;
import Main.ReadParameter;

public class SADdeltaCal {

    public void SADdeltaCal(double[][] SADorgVid, double[][] SADembdVid, String outFileName, boolean fileStart){
        ReadParameter param = new ReadParameter();
        // M, N are the size of MEvar to be searched
        int M = param.getBlSize()[0];
        int N = param.getBlSize()[1];

        //No of blocks in a given frame
        int Nc_x = SADorgVid.length/M;
        int Nc_y = SADorgVid[0].length/N;

        //Defining the variables for each block
        DELTAvar macroBlock[][] = new DELTAvar[Nc_x][Nc_y];
        for(int x=0; x<Nc_x; x++){ //Macro block index within frame
            for(int y=0; y<Nc_y; y++){ //Macro block index within frame
                macroBlock[x][y] = new DELTAvar();
            }
        }

        double delta;
        for(int x=0; x<Nc_x; x++){ //Macro block index within frame
            for(int y=0; y<Nc_y; y++){ //Macro block index within frame
                delta = 0;
                for(int i=0; i<M; i++){ // individual macro block index
                    for(int j=0; j<N; j++){ // of size MxN
                        //SAD = Sum Absolute Difference
                        delta += Math.abs(SADorgVid[x*M+i][y*N+j]-SADembdVid[x*M+i][y*N+j]);
                    } //for loop end : j
                } //for loop end : i

                macroBlock[x][y].delta = delta;
            } //for loop end : y
        } //for loop end : x

        for(int x=0; x<Nc_x; x++){ //Macro block index within frame
            for(int y=0; y<Nc_y; y++){ //Macro block index within frame
                writeDelta(macroBlock[x][y].delta, fileStart, outFileName);
                fileStart = false;
            }
        }
        
    }

    public void writeDelta(double delta, boolean fileStart, String inVideoName){
        // Writing data to motion vector file starts here
        FileOutputStream deltaFileASCII;

        String deltaFileNameASCII = inVideoName.substring(0, inVideoName.length()-4) + "_ASCII_delta.mv";

        try {
            if(fileStart){
                deltaFileASCII = new FileOutputStream(deltaFileNameASCII);
            } else{
                deltaFileASCII = new FileOutputStream(deltaFileNameASCII, true);
            }

            new PrintStream(deltaFileASCII).println (delta);
            
            deltaFileASCII.close();
         } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Video writing failed", "ERROR", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
         }

    }
}
