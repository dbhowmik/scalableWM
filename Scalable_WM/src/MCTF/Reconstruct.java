package MCTF;

import Main.*;

/**
 * @author: Deepayan Bhowmik
 *
 * E-mail: d.bhowmik@sheffield.ac.uk
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 *
 * No part of the code can be copied without permission of the author
 */
public class Reconstruct {    

    public double[][] reconstruct(double[][] currFrame, double[][] refFrame, int[] mvInfo){
        double[][] reconFrame = new double[refFrame.length][refFrame[0].length];
        ReadParameter param = new ReadParameter();
        int M = param.getBlSize()[0];
        int N = param.getBlSize()[1];

        //No of blocks in a given frame
        int Nc_x = currFrame.length/M;
        int Nc_y = currFrame[0].length/N;
        

        for(int x=0; x<Nc_x; x++){ //Macro block index within frame
            for(int y=0; y<Nc_y; y++){ //Macro block index within frame
                int MEx = 0, MEy = 0, MEi = 0;
                for(int info=0; info<param.getMVinfoLength(); info++){
                    if(info == 0 ){
                        MEx = mvInfo[Nc_y*param.getMVinfoLength()*x + param.getMVinfoLength()*y + info];
                    } else if (info == 1){
                        MEy = mvInfo[Nc_y*param.getMVinfoLength()*x + param.getMVinfoLength()*y + info];
                    } else if (info == 2){
                        MEi = mvInfo[Nc_y*param.getMVinfoLength()*x + param.getMVinfoLength()*y + info];
                    }
                }
                             
                //Reconstruct here
                for(int i=0; i<M; i++){ // individual macro block index
                    for(int j=0; j<N; j++){ // of size MxN
                        if(MEi==1){                            
                            reconFrame[x*M+i][y*N+j] = refFrame[MEx+i][MEy+j];
                        } else {
                            reconFrame[x*M+i][y*N+j] = currFrame[MEx+i][MEy+j];
                        }
                    } //for loop end : j
                } //for loop end : i

            }
        }

        return reconFrame;
    }

}
