
package MCTF;
import Main.*;
import java.io.*;
import javax.swing.*;


/**
 * @author: Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 */
public class FSBM {
    
    public void fsbm(double[][] currFrame, double[][] refFrame, boolean fileStart, String inVideoName){
        ReadParameter param = new ReadParameter();        
        // M, N are the size of MEvar to be searched
        int M = param.getBlSize()[0];
        int N = param.getBlSize()[1];

        //No of blocks in a given frame
        int Nc_x = currFrame.length/M;
        int Nc_y = currFrame[0].length/N;

        //Defining the variables for each block
        MEvar macroBlock[][] = new MEvar[Nc_x][Nc_y];
        for(int x=0; x<Nc_x; x++){ //Macro block index within frame
            for(int y=0; y<Nc_y; y++){ //Macro block index within frame
                macroBlock[x][y] = new MEvar();
            }
        }

        //Search window size -p to p {(2p+1)x(2p+1) search area}
        int p = param.getShWindow();
        double SAD = 0;
                
        for(int x=0; x<Nc_x; x++){ //Macro block index within frame
            for(int y=0; y<Nc_y; y++){ //Macro block index within frame                          
                macroBlock[x][y].SAD = 1.0e10;

                for(int c=-p; c<=p; c++){ //Search block index
                    for(int l=-p; l<=p; l++){ //Search block index
                        SAD=0;
                        if((x*M+c)>0 && (x*M+c+M-1)<refFrame.length && (y*N+l)>0 && (y*N+l+N-1)<refFrame[0].length){
                            for(int i=0; i<M; i++){ // individual macro block index
                                for(int j=0; j<N; j++){ // of size MxN
                                    //SAD = Sum Absolute Difference
                                    SAD += Math.abs(currFrame[x*M+i][y*N+j]-refFrame[x*M+i+c][y*N+j+l]);
                                } //for loop end : j
                            } //for loop end : i
                            macroBlock[x][y].SAD_search[c+p][l+p] = SAD;
                            if(SAD<macroBlock[x][y].SAD){
                                macroBlock[x][y].x = x*M+c;
                                macroBlock[x][y].y = y*N+l;
                                macroBlock[x][y].SAD = SAD;
                                macroBlock[x][y].distance = Math.sqrt(Math.pow((c),2)+Math.pow((l),2));
                                
                            } else if(SAD==macroBlock[x][y].SAD){ //in case same matching, minimum distance considered
                                double tempDistance = Math.sqrt(Math.pow((c),2)+Math.pow((l),2));
                                if(tempDistance<macroBlock[x][y].distance){
                                    macroBlock[x][y].x = x*M+c;
                                    macroBlock[x][y].y = y*N+l;
                                    macroBlock[x][y].SAD = SAD;
                                    macroBlock[x][y].distance = tempDistance;
                                }
                            } 
                        }
                    } //for loop end : l
                } //for loop end : c
                
                //Decide on inter / intra coding
                if(macroBlock[x][y].SAD>param.getIntraThreshold()){
                    macroBlock[x][y].inter = 0;
                } else {
                    macroBlock[x][y].inter = 1;
                }
            } //for loop end : y
        } //for loop end : x
             
        for(int x=0; x<Nc_x; x++){ //Macro block index within frame
            for(int y=0; y<Nc_y; y++){ //Macro block index within frame                
                writeMV(macroBlock[x][y].SAD_search, macroBlock[x][y].SAD, macroBlock[x][y].x, macroBlock[x][y].y, macroBlock[x][y].inter, fileStart, inVideoName);
                fileStart = false;
            }
        }
/*
        for(int x=0; x<Nc_x; x++){ //Macro block index within frame
            for(int y=0; y<Nc_y; y++){ //Macro block index within frame
                writeMV(macroBlock[x][y].x, macroBlock[x][y].y, macroBlock[x][y].inter, fileStart, inVideoName);
                fileStart = false;                
            }
        }
        
*/
    }

    public void writeMV(double[][] SAD_search, double SAD, int x, int y, int interMode, boolean fileStart, String inVideoName){
        // Writing data to motion vector file starts here
        FileOutputStream mvFile;
        DataOutputStream mvStream;
        String mvFileName = inVideoName.substring(0, inVideoName.length()-4) + ".mv";

      //  FileOutputStream mvFileASCII;
       // FileOutputStream mvSADFileASCII;
      //  String mvFileNameASCII = inVideoName.substring(0, inVideoName.length()-4) + "_ASCII.mv";
      //  String mvSADFileNameASCII = inVideoName.substring(0, inVideoName.length()-4) + "_ASCII_SAD.mv";
        
        try {
            if(fileStart){
                mvFile = new FileOutputStream(mvFileName);
             //   mvFileASCII = new FileOutputStream(mvFileNameASCII);
             //   mvSADFileASCII = new FileOutputStream(mvSADFileNameASCII);
            } else{
                mvFile = new FileOutputStream(mvFileName, true);
             //   mvFileASCII = new FileOutputStream(mvFileNameASCII, true);
             //   mvSADFileASCII = new FileOutputStream(mvSADFileNameASCII, true);
            }
            mvStream = new DataOutputStream(mvFile);
            mvStream.writeInt(x);
            mvStream.writeInt(y);
            mvStream.writeInt(interMode);


           // new PrintStream(mvFileASCII).println (x + " " + y + " " + interMode + " " + SAD);
            
          /*  for(int i=0; i<SAD_search.length; i++){
                for(int j=0; j<SAD_search[0].length; j++){
                    new PrintStream(mvSADFileASCII).println (i + " " + j + " " + SAD_search[i][j]);
                }
            }*/

            mvStream.close();
            mvFile.close();
          //  mvFileASCII.close();
          //  mvSADFileASCII.close();

         } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Video writing failed", "ERROR", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
         }
                
    }
    
}
