/*
 * NonLinearIdwt.java
 *
 * @author Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 */

package Embedding;


public class NonLinearIdwt {
    
    double[][] intermediateCoeff;
    double[][] a;
    double[][] b;
    double[][] c;
    double[][] d;
    double[][] p;
    
    
    public double[][] NonLinearIdwt(double[][] coefficient, int[] size, int level, String wavelet_name) {
        NonLinearIdwt nonLinear = new NonLinearIdwt();
        double[][] reconstructedImage = coefficient;
        
         
        if(wavelet_name.compareTo("MH")==0){
            reconstructedImage = nonLinear.MHaarIdwt(coefficient,size,level);
        } else if(wavelet_name.compareTo("MQ")==0){
            reconstructedImage = nonLinear.MQCIdwt(coefficient,size,level);
        }
         
         return reconstructedImage;
    }
    
     //Perform 2D NS Morphological Haar inverse transform with median as update for 
    //'level' decomposition level
    public double[][] MHaarIdwt(double[][] coefficient, int[] size, int level){
        MatrixFunction matrix = new MatrixFunction();
        
        double[][] reconstructed_image = coefficient;
        int width = size[1];
        int height = size[0];
        
        width = (int)(width/(Math.pow(2,(level-1))));
        height = (int)(height/(Math.pow(2,(level-1))));
        
        int wIndex, hIndex;
        int j,k;
        
        for(int i=0;i<level;i++){
            intermediateCoeff = new double[width][height];
            for(j=0;j<width;j++){
                for(k=0;k<height;k++){
                    intermediateCoeff[j][k] = coefficient[j][k];
                } //End of inner for loop
            } //End of for loop
            
            
            double[][][] med = new double[4][width/2][height/2];
            a = new double[width/2][height/2];
            b = new double[width/2][height/2];
            c = new double[width/2][height/2];
            d = new double[width/2][height/2];
            p = new double[width/2][height/2];
                      
            
            for(wIndex=0;wIndex<width/2;wIndex++){
                for(hIndex=0;hIndex<height/2;hIndex++){
                    a[wIndex][hIndex] = intermediateCoeff[wIndex][hIndex] / 2;                    
                    b[wIndex][hIndex] = intermediateCoeff[wIndex][hIndex + height/2];
                    c[wIndex][hIndex] = intermediateCoeff[wIndex + width/2][hIndex];
                    d[wIndex][hIndex] = intermediateCoeff[wIndex + width/2][hIndex + height/2];
                                        
                    med[0][wIndex][hIndex] = b[wIndex][hIndex] - d[wIndex][hIndex];
                    med[1][wIndex][hIndex] = c[wIndex][hIndex] - d[wIndex][hIndex];
                    med[2][wIndex][hIndex] = b[wIndex][hIndex] + c[wIndex][hIndex];
                    med[3][wIndex][hIndex] = 0.0;
                    
                    //Finding median value calculation and update a
                    double[] medMatrix = {med[0][wIndex][hIndex],med[1][wIndex][hIndex],med[2][wIndex][hIndex],med[3][wIndex][hIndex]};     
                    a[wIndex][hIndex] = a[wIndex][hIndex] - matrix.Median1D(medMatrix);
                  
                    p[wIndex][hIndex] = a[wIndex][hIndex] - d[wIndex][hIndex];
                    c[wIndex][hIndex] = c[wIndex][hIndex] + p[wIndex][hIndex];
                    b[wIndex][hIndex] = b[wIndex][hIndex] + p[wIndex][hIndex];
                    
                    d[wIndex][hIndex] = d[wIndex][hIndex]*2 + (c[wIndex][hIndex] + b[wIndex][hIndex] - a[wIndex][hIndex]);                     
                } // End of inner for loop              
            } // End of outer for loop
            
            
            wIndex = 0;            
            for(j=0;j<width;j=j+2){
                hIndex = 0;
                for(k=0;k<height;k=k+2){
                    intermediateCoeff[j][k] = a[wIndex][hIndex];
                    intermediateCoeff[j][k+1] = b[wIndex][hIndex];
                    intermediateCoeff[j+1][k] = c[wIndex][hIndex];
                    intermediateCoeff[j+1][k+1] = d[wIndex][hIndex];
                                       
                    hIndex++;
                }
                wIndex++;
            }
         
            //replace the coefficients with intermediate coefficients 
            for(j=0;j<width;j++){
                for(k=0;k<height;k++){
                    reconstructed_image[j][k] = intermediateCoeff[j][k];
                } //End of inner for loop
            } //End of for loop
            
            width = width*2;
            height = height*2;          
        }       
        return reconstructed_image;       
    }
    
    
    //Declearing public variables 
    double[][] interCoeffMQC;
    double[][] a1, a2;
    double[][] b1, b2;
    
    public double[][] MQCIdwt(double[][] reconstructedImage, int[] size, int level){
        int width = size[1];
        int height = size[0];
        
        width = (int)(width/(Math.pow(2,(level-1))));
        height = (int)(height/(Math.pow(2,(level-1))));
      
        int j,k,m,n;
        
        for(int i=0;i<level;i++){
            interCoeffMQC = new double[width][height];
            for(j=0;j<width;j++){
                for(k=0;k<height;k++){
                    interCoeffMQC[j][k] = reconstructedImage[j][k];
                } //End of inner for loop
            } //End of for loop
            
            double[] med = new double[4];
            a1 = new double[width/2][height/2];
            b1 = new double[width/2][height/2];
            b2 = new double[width/2][height/2];
            a2 = new double[width/2][height/2];
            
            int wIndex = 0;
            int hIndex = 0;
 
            for(m=0;m<width/2;m++){
                for(n=0;n<height/2;n++){
                    a1[m][n] = interCoeffMQC[m][n];                    
                    b1[m][n] = interCoeffMQC[m][n + height/2];
                    a2[m][n] = interCoeffMQC[m + width/2][n];
                    b2[m][n] = interCoeffMQC[m + width/2][n + height/2];
                }
            }
            
            double a1R, a2T, a2L, a1B, a1BR, a2LT; 
            double b1L, b2T, b2R, b1B, b1LB, b2RT;
            
            //level2 b1
            //b1(x,y)=b1(x,y)+U2( b2(x,y), b2(x-1,y), b2(x,y+1), b2(x-1,y+1));
            for(m=0;m<width/2;m++){
                for(n=0;n<height/2;n++){      
                    //Calculating values for b2R
                    if(n==height/2-1){
                        b2R = b2[m][n];
                    } else{
                        b2R = b2[m][n+1];
                    }
                    
                    //Calculating values for b2T
                    if(m==0){
                        b2T = b2[m][n];
                    } else{
                        b2T = b2[m-1][n];
                    }
                    
                    //Calculating values for b2RT
                    if((m==0)&&(n==height/2-1)){
                        b2RT = b2[m][n];
                    } else if((m==0)&&(n!=height/2-1)){
                        b2RT = b2[m][n+1];
                        
                    } else if((m!=0)&&(n==height/2-1)){
                        b2RT = b2[m-1][n];
                        
                    } else{
                        b2RT = b2[m-1][n+1];
                    }
                    
                    med[0] = b2[m][n]; 
                    med[1] = b2R;
                    med[2] = b2T;
                    med[3] = b2RT;
                    b1[m][n] = b1[m][n] - U2(med); //Update by Median value 
                                      
                }
            }
            
            //level2 b2
            //b2(x,y)=b2(x,y)-P2( b1(x,y), b1(x+1,y), b1(x,y-1), b1(x+1,y-1));
            for(m=0;m<width/2;m++){
                for(n=0;n<height/2;n++){                                                           
                    //Calculating values for b1B
                    if(m==width/2-1){
                        b1B = b1[m][n];
                    } else{
                        b1B = b1[m+1][n];
                    }
                    
                    //Calculating values for b1L
                    if(n==0){
                        b1L = b1[m][n];
                    } else{
                        b1L = b1[m][n-1];
                    }
                    
                    //Calculating values for b1LB
                    if((m==width/2-1)&&(n==0)){
                        b1LB = b1[m][n];
                    } else if((m==width/2-1)&&(n!=0)){
                        b1LB = b1[m][n-1];
                        
                    } else if((m!=width/2-1)&&(n==0)){
                        b1LB = b1[m+1][n];
                        
                    } else{
                        b1LB = b1[m+1][n-1];
                    }
                    
                    med[0] = b1[m][n]; 
                    med[1] = b1B;
                    med[2] = b1L;
                    med[3] = b1LB;
                    b2[m][n] = b2[m][n] + P2(med); //Prediction by Median value 
                }
            }
            
            //level2 a1 
            //a1(x,y)=a1(x,y)+U2( a2(x,y), a2(x-1,y), a2(x,y-1), a2(x-1,y-1))
            for(m=0;m<width/2;m++){
                for(n=0;n<height/2;n++){       
                    //Calculating values for a2L
                    if(n==0){
                        a2L = a2[m][n];
                    } else{
                        a2L = a2[m][n-1];
                    }
                    
                    //Calculating values for a2T
                    if(m==0){
                        a2T = a2[m][n];
                    } else{
                        a2T = a2[m-1][n];
                    }
                    
                    //Calculating values for a2LT
                    if((m==0)&&(n==0)){
                        a2LT = a2[m][n];
                    } else if((m==0)&&(n!=0)){
                        a2LT = a2[m][n-1];
                        
                    } else if((m!=0)&&(n==0)){
                        a2LT = a2[m-1][n];
                        
                    } else{
                        a2LT = a2[m-1][n-1];
                    }
                    
                    med[0] = a2[m][n]; 
                    med[1] = a2L;
                    med[2] = a2T;
                    med[3] = a2LT;
                    a1[m][n] = a1[m][n] - U2(med); //Prediction by Median value 
                }
            }
            
            //level2 a2 
            //a2(x,y)=a2(x,y)-P2( a1(x,y), a1(x+1,y), a1(x,y+1), a1(x+1,y+1));
            for(m=0;m<width/2;m++){
                for(n=0;n<height/2;n++){                  
                    //Calculating values for a1B
                    if(m==width/2-1){
                        a1B = a1[m][n];
                    } else{
                        a1B = a1[m+1][n];
                    }
                    
                    //Calculating values for a1R
                    if(n==height/2-1){
                        a1R = a1[m][n];
                    } else{
                        a1R = a1[m][n+1];
                    }
                    
                    //Calculating values for a1BR
                    if((m==width/2-1)&&(n==height/2-1)){
                        a1BR = a1[m][n];
                    } else if((m==width/2-1)&&(n!=height/2-1)){
                        a1BR = a1[m][n+1];
                        
                    } else if((m!=width/2-1)&&(n==height/2-1)){
                        a1BR = a1[m+1][n];
                        
                    } else{
                        a1BR = a1[m+1][n+1];
                    }
                    
                    med[0] = a1[m][n]; 
                    med[1] = a1B;
                    med[2] = a1R;
                    med[3] = a1BR;
                    a2[m][n] = a2[m][n] + P2(med); //Prediction by Median value 
                }
            }
            
            //a2
            //a2(x,y)=a2(x,y)+U1( b1(x,y), b1(x+1,y), b2(x,y), b2(x,y+1));
            for(m=0;m<width/2;m++){
                for(n=0;n<height/2;n++){                    
                    //Calculating values for b1B
                    if(m==width/2-1){
                        b1B = b1[m][n];
                    } else{
                        b1B = b1[m+1][n];
                    }
                    
                    //Calculating values for b2R
                    if(n==height/2-1){
                        b2R = b2[m][n];
                    } else{
                        b2R = b2[m][n+1];
                    }
                                        
                    med[0] = b1[m][n]; 
                    med[1] = b1B;
                    med[2] = b2[m][n];
                    med[3] = b2R; 
                    a2[m][n] = a2[m][n] - U1(med); //Update by Median value 
                }
            }
            
            //a1
            //a1(x,y)=a1(x,y)+U1( b1(x,y-1), b1(x,y), b2(x-1,y), b2(x,y));
            for(m=0;m<width/2;m++){
                for(n=0;n<height/2;n++){                    
                    //Calculating values for b1L
                    if(n==0){
                        b1L = b1[m][n];
                    } else{
                        b1L = b1[m][n-1];
                    }
                    
                    //Calculating values for b2T
                    if(m==0){
                        b2T = b2[m][n];
                    } else{
                        b2T = b2[m-1][n];
                    }
                                        
                    med[0] = b1L; 
                    med[1] = b1[m][n];
                    med[2] = b2T;
                    med[3] = b2[m][n];                    
                    a1[m][n] = a1[m][n] - U1(med); //Update by Median value        
                }
            }
            
            //b2
            //b2(x,y)=b2(x,y)-P1( a1(x,y), a1(x+1,y), a2(x,y-1), a2(x,y))
            for(m=0;m<width/2;m++){
                for(n=0;n<height/2;n++){                    
                    //Calculating values for a2L
                    if(n==0){
                        a2L = a2[m][n];
                    } else{
                        a2L = a2[m][n-1];
                    }
                    
                    //Calculating values for a1B
                    if(m==width/2-1){
                        a1B = a1[m][n];
                    } else{
                        a1B = a1[m+1][n];
                    }
                                        
                    med[0] = a1[m][n]; 
                    med[1] = a1B;
                    med[2] = a2L;
                    med[3] = a2[m][n];                   
                    b2[m][n] = b2[m][n] + P1(med); //Prediction by Median value 
                }
            }
            
            //b1
            //b1(x,y)=b1(x,y)-P1( a1(x,y), a1(x,y+1), a2(x-1,y), a2(x,y));
            for(m=0;m<width/2;m++){
                for(n=0;n<height/2;n++){                
                    //Calculating values for a1R
                    if(n==height/2-1){
                        a1R = a1[m][n];
                    } else{
                        a1R = a1[m][n+1];
                    }
                    
                    //Calculating values for a2T
                    if(m==0){
                        a2T = a2[m][n];
                    } else{
                        a2T = a2[m-1][n];
                    }
                    
                    med[0] = a1[m][n]; 
                    med[1] = a1R;
                    med[2] = a2T;
                    med[3] = a2[m][n];                   
                    b1[m][n] = b1[m][n] + P1(med); //Prediction by Median value 
                }
            }
            
            wIndex = 0;            
            for(j=0;j<width;j=j+2){
                hIndex = 0;
                for(k=0;k<height;k=k+2){
                    interCoeffMQC[j][k] = a1[wIndex][hIndex];
                    interCoeffMQC[j][k+1] = b1[wIndex][hIndex];
                    interCoeffMQC[j+1][k] = b2[wIndex][hIndex];
                    interCoeffMQC[j+1][k+1] = a2[wIndex][hIndex];
                                       
                    hIndex++;
                }
                wIndex++;
            }
            
        //replace the coefficients with intermediate coefficients 
            for(j=0;j<width;j++){
                for(k=0;k<height;k++){
                    reconstructedImage[j][k] = interCoeffMQC[j][k];
                } //End of inner for loop
            } //End of for loop
            
            width = width*2;
            height = height*2;          
        } //End of decomposition level for loop           
        
        return reconstructedImage;       
    }
    
     // Prediction and update functions for Q-MC wavelet     
    public double P1(double[] medMatrix){
        MatrixFunction matrix = new MatrixFunction();      
        double medianValue = matrix.Median1D(medMatrix);
        return medianValue;
    }
    
     public double U1(double[] medMatrix){
        MatrixFunction matrix = new MatrixFunction();      
        double medianValue = matrix.Median1D(medMatrix) / 2;
        return medianValue;
    }
     
      public double P2(double[] medMatrix){
        MatrixFunction matrix = new MatrixFunction();      
        double medianValue = matrix.Median1D(medMatrix);
        return medianValue;
    }
      
       public double U2(double[] medMatrix){
        MatrixFunction matrix = new MatrixFunction();      
        double medianValue = matrix.Median1D(medMatrix) / 2;
        return medianValue;
    }
}
