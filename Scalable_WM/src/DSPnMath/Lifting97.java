/**
 * @author: Deepayan Bhowmik
 *
 * 2D CDF 9/7 Wavelet Forward and Inverse Transform (lifting implementation)
 * 
 * This is the same implementation as in JPEG2000 Kakadu
 *
 */



package DSPnMath;

import Embedding.preProcessEmbed;

public class Lifting97 {
    static double[][] imageCopy;
    public double[][] lifting97FWT(double[][] image, int[] size, int level){
        int width = size[0];
        int height = size[1];
       
        for(int i=0;i<level;i++){            
            image = fwt97(image, width, height);        
            width /=2;
            height /=2;
        }       
        return image;
    }

    public double[][] lifting97IWT(double[][] coefficient, int[] size, int level){        
        int width = size[0];
        int height = size[1];

        for(int i=0;i<level-1;i++){
            height /=2;
            width /=2;
        }

        for(int i=0;i<level;i++){
            coefficient = iwt97(coefficient, width, height); // Columns
            //coefficient = iwt97(coefficient, width, height); // Rows

            height *=2;
            width *=2;
        }

        //Round control is used to control rounding up operation in inverse wavelet transform.
        preProcessEmbed roundCon = new preProcessEmbed();
        if(roundCon.getRoundCon() == 0){
            for(int q=0;q<size[0];q++){
                for(int r=0;r<size[1];r++){
                    coefficient[q][r]= Math.round(coefficient[q][r]);
                }
            }  
        }
        return coefficient;
    }


/** Forward Cohen-Daubechies-Feauveau 9 tap / 7 tap wavelet transform performed
 *  on all columns of the 2D n*n matrix signal 'pixel' via lifting.
 *  The returned result is 'pixel', the modified input matrix.
 */

    private double[][] fwt97(double[][] pixel, int width, int height){
        // 9/7 Coefficients:
        double a1 = -1.586134342;
        double a2 = -0.05298011854;
        double a3 = 0.8829110762;
        double a4 = 0.4435068522;
        // Scale coeff:        
        double scale = 1.149604398;

        //Column
        for(int x=0; x<width; x++){
            // Prediction 1
            for(int y=1; y<height-1; y +=2){
                pixel[x][y] += a1*(pixel[x][y-1] + pixel[x][y+1]);                
            }
            pixel[x][height-1] += 2 * a1 * pixel[x][height-2] ;// Symmetric extension
            // Update 1
            for(int y=2; y<height; y +=2){
                pixel[x][y] += a2 * (pixel[x][y-1] + pixel[x][y+1]);                
            }
            pixel[x][0] += 2 * a2 * pixel[x][1]; // Symmetric extension

            // Prediction 2
            for(int y=1; y<height-1; y +=2){
                pixel[x][y] += a3 * (pixel[x][y-1] + pixel[x][y+1]);                
            }
            pixel[x][height-1] += 2 * a3 * pixel[x][height-2];
            // Update 2
            for(int y=2; y<height; y +=2){
                pixel[x][y] += a4 * (pixel[x][y-1] + pixel[x][y+1]);                
            }
            pixel[x][0] += 2 * a4 * pixel[x][1];
        }
               
        // De-interleave
        double[][] tempBank = new double[width][height];
        for(int x=0; x<width; x++){
            for(int y=0;y<height;y++){                
                // simultaneously transpose the matrix when deinterleaving
                if(y%2 == 0){ //Odd
                    tempBank[x][y/2] = scale * pixel[x][y];
                } else { //even
                    tempBank[x][y/2 + height/2] = (1/scale) * pixel[x][y];
                }

            }
        }

        for(int x=0; x<width; x++){
            for(int y=0;y<height;y++){
                pixel[x][y] = tempBank[x][y];
            }
        }

        //Row
        for(int x=0; x<height; x++){
            // Prediction 1
            for(int y=1; y<width-1; y +=2){
                pixel[y][x] += a1*(pixel[y-1][x] + pixel[y+1][x]);
            }
            pixel[width-1][x] += 2 * a1 * pixel[width-2][x] ;// Symmetric extension
            // Update 1
            for(int y=2; y<width; y +=2){
                pixel[y][x] += a2 * (pixel[y-1][x] + pixel[y+1][x]);
            }
            pixel[0][x] += 2 * a2 * pixel[1][x]; // Symmetric extension

            // Prediction 2
            for(int y=1; y<width-1; y +=2){
                pixel[y][x] += a3 * (pixel[y-1][x] + pixel[y+1][x]);
            }
            pixel[width-1][x] += 2 * a3 * pixel[width-2][x];
            // Update 2
            for(int y=2; y<height; y +=2){
                pixel[y][x] += a4 * (pixel[y-1][x] + pixel[y+1][x]);
            }
            pixel[0][x] += 2 * a4 * pixel[1][x];
        }

        // De-interleave      
        for(int x=0; x<width; x++){
            for(int y=0;y<height;y++){
                // k1 and k2 scale the vals
                // simultaneously transpose the matrix when deinterleaving
                if(x%2 == 0){ //Even
                    tempBank[x/2][y] = scale * pixel[x][y];
                } else { //Odd
                    tempBank[x/2 + width/2][y] = (1/scale) * pixel[x][y];
                }

            }
        }

        for(int x=0; x<width; x++){
            for(int y=0;y<height;y++){
                pixel[x][y] = tempBank[x][y];
            }
        }
        
        return pixel;
    }

    // Inverse CDF 9/7
    private double[][] iwt97(double[][] pixel, int width, int height){
        // 9/7 inverse coefficients:        
        double a1 = 1.586134342;
        double a2 = 0.05298011854;
        double a3 = -0.8829110762;
        double a4 = -0.4435068522;

        // Scale coeff:
        double scale = 1.149604398;

        // Interleave
        double[][] tempBank = new double[width][height];

        for(int x=0; x<width; x++){
            for(int y=0;y<height;y++){
                tempBank[x][y] = pixel[x][y];
            }
        }

        for(int x=0; x<width; x++){
            for(int y=0;y<height;y++){
                if(x<(width/2)){
                    pixel[x*2][y] = (1/scale) * tempBank[x][y];
                    pixel[x*2+1][y] = scale* tempBank[x+width/2][y];
                } 
            }
        }

        //Row
        for(int x=0; x<height; x++){
            // Update 2
            for(int y=2; y<height; y +=2){
                pixel[y][x] += a4 * (pixel[y-1][x] + pixel[y+1][x]);
            }
            pixel[0][x] += 2 * a4 * pixel[1][x];

            // Prediction 2
            for(int y=1; y<width-1; y +=2){
                pixel[y][x] += a3 * (pixel[y-1][x] + pixel[y+1][x]);
            }
            pixel[width-1][x] += 2 * a3 * pixel[width-2][x];

            // Update 1
            for(int y=2; y<width; y +=2){
                pixel[y][x] += a2 * (pixel[y-1][x] + pixel[y+1][x]);
            }
            pixel[0][x] += 2 * a2 * pixel[1][x]; // Symmetric extension

            // Prediction 1
            for(int y=1; y<width-1; y +=2){
                pixel[y][x] += a1*(pixel[y-1][x] + pixel[y+1][x]);
            }
            pixel[width-1][x] += 2 * a1 * pixel[width-2][x] ;// Symmetric extension

        }


        for(int x=0; x<width; x++){
            for(int y=0;y<height;y++){
                tempBank[x][y] = pixel[x][y];
            }
        }

        for(int x=0; x<width; x++){
            for(int y=0;y<height;y++){
                if(y<(height/2)){
                    pixel[x][y*2] = (1/scale) * tempBank[x][y];
                    pixel[x][y*2+1] = scale* tempBank[x][y+height/2];
                }
            }
        }

        //Column
        for(int x=0; x<width; x++){
            // Update 2
            for(int y=2; y<height; y +=2){
                pixel[x][y] += a4 * (pixel[x][y-1] + pixel[x][y+1]);
            }
            pixel[x][0] += 2 * a4 * pixel[x][1];

             // Prediction 2
            for(int y=1; y<height-1; y +=2){
                pixel[x][y] += a3 * (pixel[x][y-1] + pixel[x][y+1]);
            }
            pixel[x][height-1] += 2 * a3 * pixel[x][height-2];

            // Update 1
            for(int y=2; y<height; y +=2){
                pixel[x][y] += a2 * (pixel[x][y-1] + pixel[x][y+1]);
            }
            pixel[x][0] += 2 * a2 * pixel[x][1]; // Symmetric extension

            // Prediction 1
            for(int y=1; y<height-1; y +=2){
                pixel[x][y] += a1*(pixel[x][y-1] + pixel[x][y+1]);
            }
            pixel[x][height-1] += 2 * a1 * pixel[x][height-2] ;// Symmetric extension
        }

        return pixel;
    }


}
