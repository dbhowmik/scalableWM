/*
 * LinearIdwt.java
 *
 * @author Deepayan Bhowmik
 *
 */

package DSPnMath;

import Embedding.preProcessEmbed;


public class LinearIdwt {
   
    
   
    public double[][] LinearIdwt(double[][] reconstructed_image, int[] size, int level, String wavelet_name) {
        FilterBank filter = new FilterBank();
        double[][] qmf = filter.Filter_bank(wavelet_name);
        int nc_x = (int)Math.round(size[0]/ Math.pow(2.0, (double)(level-1)));
        int nc_y = (int)Math.round(size[1]/ Math.pow(2.0, (double)(level-1)));
        
        SamplingFiltering idwt = new SamplingFiltering();
       
        for(int i=0;i<level;i++){
            int[] top_x = {nc_x/2, nc_x};
            int[] bot_x = {0,nc_x/2};
           
            int[] top_y = {nc_y/2, nc_y};
            int[] bot_y = {0,nc_y/2};
            
            int[] all_x = {0,nc_x};
            int[] all_y = {0,nc_y};
                         
            for(int y=0;y<nc_y;y++){
                double[] temp1 = new double[bot_x[1]];
                for(int c=bot_x[0];c<bot_x[1];c++){
                    temp1[c-bot_x[0]] = reconstructed_image[c][y];
                }
                
                double[] temp2 = new double[top_x[0]];
                for(int d=top_x[0];d<top_x[1];d++){
                    temp2[d-top_x[0]] = reconstructed_image[d][y];
                }
                
                double[] temp3 = idwt.InvLowpass(qmf[2],temp1);        
                double[] temp4 = idwt.InvHighpass(qmf[3],temp2);
                
                for(int n=all_x[0];n<all_x[1];n++){
                    reconstructed_image[n-all_x[0]][y] = temp3[n] + temp4[n];
                }
            }
            
            for(int x=0;x<nc_x;x++){
                double[] temp5 = new double[bot_y[1]];
                for(int a=bot_y[0];a<bot_y[1];a++){
                    temp5[a-bot_y[0]] = reconstructed_image[x][a];
                }
                
                double[] temp6 = new double[top_y[0]];
                for(int b=top_y[0];b<top_y[1];b++){
                    temp6[b-top_y[0]] = reconstructed_image[x][b];
                }
                
                double[] temp7 = idwt.InvLowpass(qmf[2],temp5);
                double[] temp8 = idwt.InvHighpass(qmf[3],temp6);
                
                for(int m=all_y[0];m<all_y[1];m++){
                    reconstructed_image[x][m-all_y[0]] = temp7[m] + temp8[m];
               }                           
            }                        
            nc_x = 2 * nc_x;
            nc_y = 2 * nc_y;
        }

        
        //Round control is used to control rounding up operation in inverse wavelet transform.
        preProcessEmbed roundCon = new preProcessEmbed();

        if(roundCon.getRoundCon() == 0){
            for(int q=0;q<size[0];q++){
                for(int r=0;r<size[1];r++){
                    reconstructed_image[q][r]= Math.round(reconstructed_image[q][r]);
                }
            }
        }
        
     return reconstructed_image;
    }
    
   
    
}
