package MCTF;

import Main.*;


/**
 * @author: Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 *
 * No part of the code can be copied without permission of the author
 */
public class tSynthesis {
    double[][] refFrame, currFrame;

    public void tSynthesis(double[][] lowPass, double[][] highPass, int[] mvInfo){
        currFrame = new double[lowPass.length][lowPass[0].length];
        refFrame = new double[highPass.length][highPass[0].length];

        for(int i=0; i<lowPass.length; i++){
            for(int j=0; j<lowPass[0].length; j++){
                currFrame[i][j] = lowPass[i][j];
                refFrame[i][j] = highPass[i][j];
            }
        }
        
        ReadParameter param = new ReadParameter();        
        MCTFvar FrameA[][] = new MCTFvar[highPass.length][highPass[0].length];
        MCTFvar FrameB[][] = new MCTFvar[highPass.length][highPass[0].length];

        for(int x=0; x<highPass.length; x++){
            for(int y=0; y<highPass[0].length; y++){
                FrameA[x][y] = new MCTFvar();
                FrameB[x][y] = new MCTFvar();
            }
        }

        int M = param.getBlSize()[0];
        int N = param.getBlSize()[1];
        //No of blocks in a given frame
        int Nc_x = highPass.length/M;
        int Nc_y = highPass[0].length/N;
        
        // temporal Synthesis
        /*
        for(int i=0; i<highPass.length; i++){
            for(int j=0; j<highPass[0].length; j++){
                //Un-Normalisation
                refFrame[i][j] /= 1.414;  //Math.sqrt(2);
                currFrame[i][j] *= 1.414; //Math.sqrt(2);
            } //loop end: i
        } //loop end: j
        
        */
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
                if(MEi > 0){
                    for(int i=0; i<M; i++){ // individual macro block index
                        for(int j=0; j<N; j++){ // of size MxN
                            FrameA[x*M+i][y*N+j].MVxFrameA = MEx+i;
                            FrameA[x*M+i][y*N+j].MVyFrameA = MEy+j;
                            FrameA[x*M+i][y*N+j].MVinterFrameA = MEi;                            
                        } //for loop end : j                                             
                    } //for loop end : i
                }
            }
        }


        for(int i=0; i<lowPass.length; i++){
            for(int j=0; j<lowPass[0].length; j++){
                if(FrameA[i][j].MVinterFrameA>0){
                    currFrame[i][j] -= (refFrame[FrameA[i][j].MVxFrameA][FrameA[i][j].MVyFrameA]/2);
                }
            } //loop end: i
        } //loop end: j
  
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
                //MCTF process here
                if(MEi>0){
                    for(int i=0; i<M; i++){ // individual macro block index
                        for(int j=0; j<N; j++){ // of size MxN                            
                            FrameB[MEx+i][MEy+j].connectivityFrameB++;
                            FrameB[MEx+i][MEy+j].sumRefValueFrameB += currFrame[x*M+i][y*N+j];
                        } //for loop end : j
                    } //for loop end : i
                }
            }
        }
               
        for(int i=0; i<highPass.length; i++){
            for(int j=0; j<highPass[0].length; j++){
                if(FrameB[i][j].connectivityFrameB>0){                    
                    refFrame[i][j] +=  (FrameB[i][j].sumRefValueFrameB / FrameB[i][j].connectivityFrameB);
                }
            } //loop end: i
        } //loop end: j
        /*
        //Frame swapping due to analysis structure...
        double swapTemp;
        for(int i=0; i<currFrame.length; i++){
            for(int j=0; j<currFrame[0].length; j++){
                swapTemp = currFrame[i][j];
                currFrame[i][j] = refFrame[i][j];
                refFrame[i][j] = swapTemp;
            }
        }*/
    }

    public double[][] getRefFrame(){
        return refFrame;
    }

    public double[][] getCurrFrame(){
        return currFrame;
    }

}
