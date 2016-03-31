/*
 * LinearDwt.java
 *
 * @author Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved 
 */

package Embedding;


public class LinearDwt {
    
     /**
     * This is to perform linear forward Discrete Wavelet Transform
     */
     public double[][] LinearDwt(double[][] image, int[] size, int level, String wavelet_name){
        double[][] coefficient = image; 
        //Load filter value from the filter bank
        FilterBank filter = new FilterBank();
        double[][] qmf = filter.Filter_bank(wavelet_name);
        int nc_x = size[1];
        int nc_y = size[0];
        SamplingFiltering dwt = new SamplingFiltering();
        
        for(int i=0;i<level;i++){
            int[] top_x = {nc_x/2, nc_x};
            int[] bot_x = {0,nc_x/2};
            int[] top_y = {nc_y/2, nc_y};
            int[] bot_y = {0,nc_y/2};
             
            for(int x=0;x<nc_x;x++){
                double[] column = new double[nc_y];
                for(int m=0;m<nc_y;m++){
                    column[m] = coefficient[x][m]; 
                }
                
                double[] temp1 = dwt.Lowpass(qmf[0],column); 
                double[] temp2 = dwt.Highpass(qmf[1],column);
                
                for(int a=bot_y[0];a<bot_y[1];a++){
                    coefficient[x][a]=temp1[a-bot_y[0]];
                }
                
                for(int b=top_y[0];b<top_y[1];b++){
                    coefficient[x][b]=temp2[b-top_y[0]];
                }
                
            }
            
            for(int y=0;y<nc_y;y++){
                double[] row = new double[nc_x];
                for(int n=0;n<nc_x;n++){
                    row[n] = coefficient[n][y]; 
                }
                
                double[] temp3 = dwt.Lowpass(qmf[0],row); 
                double[] temp4 = dwt.Highpass(qmf[1],row);
                
                for(int c=bot_x[0];c<bot_x[1];c++){
                    coefficient[c][y]=temp3[c-bot_x[0]];
                }
                
                for(int d=top_x[0];d<top_x[1];d++){
                    coefficient[d][y]=temp4[d-top_x[0]];
                }
            }
            
            nc_x = nc_x/2;
            nc_y = nc_y/2;
        }
          
        
       
        return coefficient; 
    }
    
}
