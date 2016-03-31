package MCTF;

import Main.*;


/**
 * @author: Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 *
 */
public class tAnalysis {
    double[][] lowPass, highPass;

    public void tAnalysis(double[][] currFrame, double[][] refFrame, int[] mvInfo){
        lowPass = new double[currFrame.length][currFrame[0].length];
        highPass = new double[refFrame.length][refFrame[0].length];

        for(int i=0; i<currFrame.length; i++){
            for(int j=0; j<currFrame[0].length; j++){
                lowPass[i][j] = currFrame[i][j];
                highPass[i][j] = refFrame[i][j];
            }
        }
        
        //----------------------------------------------------------------------
        ReadParameter param = new ReadParameter();
        //----------------------------------------------------------------------
        MCTFvar FrameA[][] = new MCTFvar[currFrame.length][currFrame[0].length];
        MCTFvar FrameB[][] = new MCTFvar[currFrame.length][currFrame[0].length];
        for(int x=0; x<currFrame.length; x++){
            for(int y=0; y<currFrame[0].length; y++){
                FrameA[x][y] = new MCTFvar();
                FrameB[x][y] = new MCTFvar();
            }
        }
     
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
                //MCTF process here
                if(MEi>0){
                    for(int i=0; i<M; i++){ // individual macro block index
                        for(int j=0; j<N; j++){ // of size MxN
                            FrameA[x*M+i][y*N+j].MVxFrameA = MEx+i;
                            FrameA[x*M+i][y*N+j].MVyFrameA = MEy+j;
                            FrameA[x*M+i][y*N+j].MVinterFrameA = MEi;
                            
                            FrameB[MEx+i][MEy+j].connectivityFrameB++;
                            FrameB[MEx+i][MEy+j].sumRefValueFrameB += lowPass[x*M+i][y*N+j];
                        } //for loop end : j
                    } //for loop end : i
                }
            }
        }
        
        //temporal analysis
        for(int i=0; i<refFrame.length; i++){
            for(int j=0; j<refFrame[0].length; j++){
                    if(FrameB[i][j].connectivityFrameB>0){
                    highPass[i][j] -= (FrameB[i][j].sumRefValueFrameB/FrameB[i][j].connectivityFrameB);                    
                }                              
            } //loop end: i
        } //loop end: j
        //----------------------------------------------------------------------
        for(int i=0; i<currFrame.length; i++){
            for(int j=0; j<currFrame[0].length; j++){
                if(FrameA[i][j].MVinterFrameA>0){
                    lowPass[i][j] += (highPass[FrameA[i][j].MVxFrameA][FrameA[i][j].MVyFrameA]/2);
                }
            } //loop end: i
        } //loop end: j
/*
        for(int i=0; i<currFrame.length; i++){
            for(int j=0; j<currFrame[0].length; j++){
                //Normalisation
                lowPass[i][j] *= 1.414;//Math.sqrt(2);
                highPass[i][j] /= 1.414;//Math.sqrt(2);
            } //loop end: i
        } //loop end: j
 */
    }

    public double[][] getLowPass(){
        return lowPass;
    }

    public double[][] getHighPass(){
        return highPass;
    }

}
