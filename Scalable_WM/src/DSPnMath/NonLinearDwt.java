/*
 * NonLinearDwt.java
 *
 *
 * @author Deepayan Bhowmik
 *
 */

package DSPnMath;

 /**
     * This is to perform linear forward Discrete Wavelet Transform
  */

public class NonLinearDwt {
    
    double[][] intermediateCoeff;
    double[][] a;
    double[][] b;
    double[][] c;
    double[][] d;
    double[][] p;
    
    public double[][] NonLinearDwt(double[][] image, int[] size, int level, String wavelet_name) {
        double[][] coefficient = image; 
        int T = 10;
        if(wavelet_name.compareTo("MH")==0){
            coefficient = MHaarDwt(image,size,level);
        } else if(wavelet_name.compareTo("MQ")==0){
            coefficient = MQCDwt(image,size,level);
        } else if(wavelet_name.compareTo("2DSAL")==0){
            coefficient = SAL2DDwt(image,size,level,T);
        } else if(wavelet_name.compareTo("CISL")==0){
            coefficient = cislMQCDwt(image,size,level);
        }
               
        return coefficient;        
    }
    
    
    //Perform 2D NS Morphological Haar transform with median as update for 
    //'level' decomposition level
    public double[][] MHaarDwt(double[][] image, int[] size, int level){
        MatrixFunction matrix = new MatrixFunction();
        
        double[][] coefficient = image;
        int width = size[0];
        int height = size[1];
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
            
            
            int wIndex = 0;
            int hIndex = 0;
 
            for(int m=0;m<width;m=m+2){
                hIndex = 0;
                for(int n=0;n<height;n=n+2){
                    a[wIndex][hIndex] = intermediateCoeff[m][n];
                    b[wIndex][hIndex] = intermediateCoeff[m][n+1];
                    c[wIndex][hIndex] = intermediateCoeff[m+1][n];
                    d[wIndex][hIndex] = intermediateCoeff[m+1][n+1];
                    
                    d[wIndex][hIndex] = d[wIndex][hIndex] - (c[wIndex][hIndex] + b[wIndex][hIndex] - a[wIndex][hIndex]);                    
                    d[wIndex][hIndex] = d[wIndex][hIndex] / 2;
                    
                    p[wIndex][hIndex] = a[wIndex][hIndex] - d[wIndex][hIndex];
                    c[wIndex][hIndex] = c[wIndex][hIndex] - p[wIndex][hIndex];
                    b[wIndex][hIndex] = b[wIndex][hIndex] - p[wIndex][hIndex];
                    
                    med[0][wIndex][hIndex] = b[wIndex][hIndex] - d[wIndex][hIndex];
                    med[1][wIndex][hIndex] = c[wIndex][hIndex] - d[wIndex][hIndex];
                    med[2][wIndex][hIndex] = b[wIndex][hIndex] + c[wIndex][hIndex];
                    med[3][wIndex][hIndex] = 0.0;
                    
                    //Finding median value calculation and update a
                    double[] medMatrix = {med[0][wIndex][hIndex],med[1][wIndex][hIndex],med[2][wIndex][hIndex],med[3][wIndex][hIndex]};     
                    a[wIndex][hIndex] = a[wIndex][hIndex] + matrix.Median1D(medMatrix);
                      
                    hIndex++;
                }
                wIndex++;
            }
            
            for(j=0;j<a.length;j++){
                for(k=0;k<a[0].length;k++){
                  //  intermediateCoeff[j][k] = a[j][k] * 2;
                    intermediateCoeff[j][k] = a[j][k];
                    intermediateCoeff[j][k+a[0].length] = b[j][k];
                    intermediateCoeff[j+a.length][k] = c[j][k];
                    intermediateCoeff[j+a.length][k+a[0].length] = d[j][k];
                }
            }
                                
            //replace the coefficients with intermediate coefficients 
            for(j=0;j<width;j++){
                for(k=0;k<height;k++){
                    coefficient[j][k] = intermediateCoeff[j][k];
                } //End of inner for loop
            } //End of for loop
            
            width = width/2;
            height = height/2;          
        }       
        return coefficient;       
    }
    
    
    
//Author of the algorithm Charith Abhayaratne
//PNA 4 CWI Amsterdam The Netherland
//Date 03/10/02
//  Re-write in Java by Deepayan Bhowmik, University of Sheffield
    
    
//a1 b1 a1 b1 a1 b1 a1 b1 a1 b1 a1 b1
//b2 a2 b2 a2 b2 a2 b2 a2 b2 a2 b2 a2
//a1 b1 a1 b1 a1 b1 a1 b1 a1 b1 a1 b1
//b2 a2 b2 a2 b2 a2 b2 a2 b2 a2 b2 a2
//a1 b1 a1 b1 a1 b1 a1 b1 a1 b1 a1 b1
//b2 a2 b2 a2 b2 a2 b2 a2 b2 a2 b2 a2
//a1 b1 a1 b1 a1 b1 a1 b1 a1 b1 a1 b1
//b2 a2 b2 a2 b2 a2 b2 a2 b2 a2 b2 a2
//a1 b1 a1 b1 a1 b1 a1 b1 a1 b1 a1 b1
//b2 a2 b2 a2 b2 a2 b2 a2 b2 a2 b2 a2
//
//all the bs are prediction of the first level of QC
//all the as are updates of the first level of QC 
//all the 2s are prediction of the second level of QC
//all the 1s are updates of the second level of QC 
//
// b1(x,y)=b1(x,y)-P1( a1(x,y), a1(x,y+1), a2(x-1,y) a2(x,y))
// b2(x,y)=b2(x,y)-P1( a1(x,y), a1(x+1,y), a2(x,y-1) a2(x,y))
// a1(x,y)=a1(x,y)+U1( b1(x,y-1), b1(x,y), b2(x-1,y) b2(x,y))
// a2(x,y)=a2(x,y)+U1( b1(x,y), b1(x+1,y), b2(x,y) b2(x,y+1))
//
// a2(x,y)=a2(x,y)-P2( a1(x,y), a1(x+1,y), a1(x,y+1) a1(x+1,y+1))
// a1(x,y)=a1(x,y)+U2( a2(x,y), a2(x-1,y), a2(x,y-1) a2(x-1,y-1))
//
// b2(x,y)=b2(x,y)-P2( b1(x,y), b1(x+1,y), b1(x,y-1) b1(x+1,y-1))
// b1(x,y)=b1(x,y)+U2( b2(x,y), b2(x-1,y), b2(x,y+1) b2(x-1,y+1))
// 
//Median for both prediction and update

  
    //Declearing public variables 
    double[][] interCoeffMQC;
    double[][] a1, a2;
    double[][] b1, b2;
    
    public double[][] MQCDwt(double[][] coefficient, int[] size, int level){    
        int width = size[0];
        int height = size[1];
        int j,k,m,n;
        
        
        for(int i=0;i<level;i++){
            interCoeffMQC = new double[width][height];
            for(j=0;j<width;j++){
                for(k=0;k<height;k++){
                    interCoeffMQC[j][k] = coefficient[j][k];
                } //End of inner for loop
            } //End of for loop
            
            double[] med = new double[4];
            a1 = new double[width/2][height/2];
            b1 = new double[width/2][height/2];
            b2 = new double[width/2][height/2];
            a2 = new double[width/2][height/2];
            
            int wIndex = 0;
            int hIndex = 0;
 
            for(m=0;m<width;m=m+2){
                hIndex = 0;
                for(n=0;n<height;n=n+2){
                    a1[wIndex][hIndex] = interCoeffMQC[m][n];
                    b1[wIndex][hIndex] = interCoeffMQC[m][n+1];
                    b2[wIndex][hIndex] = interCoeffMQC[m+1][n];
                    a2[wIndex][hIndex] = interCoeffMQC[m+1][n+1];
                                
                    hIndex++;
                }
                wIndex++;
            }
            
            double a1R, a2T, a2L, a1B, a1BR, a2LT; 
            double b1L, b2T, b2R, b1B, b1LB, b2RT;
            
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
                    b1[m][n] = b1[m][n] - P1(med); //Prediction by Median value 
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
                    b2[m][n] = b2[m][n] - P1(med); //Prediction by Median value 
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
                    a1[m][n] = a1[m][n] + U1(med); //Update by Median value        
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
                    a2[m][n] = a2[m][n] + U1(med); //Update by Median value 
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
                    a2[m][n] = a2[m][n] - P2(med); //Prediction by Median value 
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
                    a1[m][n] = a1[m][n] + U2(med); //Prediction by Median value 
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
                    b2[m][n] = b2[m][n] - P2(med); //Prediction by Median value 
                }
            }
            
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
                    b1[m][n] = b1[m][n] + U2(med); //Update by Median value 
                                      
                }
            }
            
            for(j=0;j<a1.length;j++){
                for(k=0;k<a1[0].length;k++){
                    interCoeffMQC[j][k] = a1[j][k] * 2;
                    interCoeffMQC[j][k+a1[0].length] = b1[j][k] / Math.pow(2,0.5);
                    interCoeffMQC[j+a1.length][k] = a2[j][k];
                    interCoeffMQC[j+a1.length][k+a1[0].length] = b2[j][k] / Math.pow(2,0.5);
                }
            }
            
          //replace the coefficients with intermediate coefficients 
            for(j=0;j<width;j++){
                for(k=0;k<height;k++){
                    coefficient[j][k] = interCoeffMQC[j][k];
                } //End of inner for loop
            } //End of for loop
            
            width = width/2;
            height = height/2;   
        } //End of the decomposition level for loop
              
        return coefficient;      
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
       
     // Prediction and update functions for Q-MC wavelet     
    public double cislP1(double[] medMatrix){
        MatrixFunction matrix = new MatrixFunction();      
        double medianValue = matrix.cislMedian1D(medMatrix);
        return medianValue;
    }
    
     public double cislU1(double[] medMatrix){
        MatrixFunction matrix = new MatrixFunction();      
        double medianValue = matrix.cislMedian1D(medMatrix) / 2;
        return medianValue;
    }
     
      public double cislP2(double[] medMatrix){
        MatrixFunction matrix = new MatrixFunction();      
        double medianValue = matrix.cislMedian1D(medMatrix);
        return medianValue;
    }
      
       public double cislU2(double[] medMatrix){
        MatrixFunction matrix = new MatrixFunction();      
        double medianValue = matrix.cislMedian1D(medMatrix) / 2;
        return medianValue;
    }   
     
       
    public double[][] cislMQCDwt(double[][] coefficient, int[] size, int level){    
        int width = size[0];
        int height = size[1];
        int j,k,m,n;
        
        
        for(int i=0;i<level;i++){
            interCoeffMQC = new double[width][height];
            for(j=0;j<width;j++){
                for(k=0;k<height;k++){
                    interCoeffMQC[j][k] = coefficient[j][k];
                } //End of inner for loop
            } //End of for loop
            
            double[] med = new double[4];
            a1 = new double[width/2][height/2];
            b1 = new double[width/2][height/2];
            b2 = new double[width/2][height/2];
            a2 = new double[width/2][height/2];
            
            int wIndex = 0;
            int hIndex = 0;
 
            for(m=0;m<width;m=m+2){
                hIndex = 0;
                for(n=0;n<height;n=n+2){
                    a1[wIndex][hIndex] = interCoeffMQC[m][n];
                    b1[wIndex][hIndex] = interCoeffMQC[m][n+1];
                    b2[wIndex][hIndex] = interCoeffMQC[m+1][n];
                    a2[wIndex][hIndex] = interCoeffMQC[m+1][n+1];
                                
                    hIndex++;
                }
                wIndex++;
            }
            
            double a1R, a2T, a2L, a1B, a1BR, a2LT; 
            double b1L, b2T, b2R, b1B, b1LB, b2RT;
            
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
                    b1[m][n] = b1[m][n] - cislP1(med); //Prediction by Median value 
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
                    b2[m][n] = b2[m][n] - cislP1(med); //Prediction by Median value 
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
                    a1[m][n] = a1[m][n] + cislU1(med); //Update by Median value        
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
                    a2[m][n] = a2[m][n] + cislU1(med); //Update by Median value 
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
                    a2[m][n] = a2[m][n] - cislP2(med); //Prediction by Median value 
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
                    a1[m][n] = a1[m][n] + cislU2(med); //Prediction by Median value 
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
                    b2[m][n] = b2[m][n] - cislP2(med); //Prediction by Median value 
                }
            }
            
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
                    b1[m][n] = b1[m][n] + cislU2(med); //Update by Median value 
                                      
                }
            }
            
            for(j=0;j<a1.length;j++){
                for(k=0;k<a1[0].length;k++){
                    interCoeffMQC[j][k] = a1[j][k] * 2;
                    interCoeffMQC[j][k+a1[0].length] = b1[j][k] / Math.pow(2,0.5);
                    interCoeffMQC[j+a1.length][k] = a2[j][k];
                    interCoeffMQC[j+a1.length][k+a1[0].length] = b2[j][k] / Math.pow(2,0.5);
                }
            }
            
          //replace the coefficients with intermediate coefficients 
            for(j=0;j<width;j++){
                for(k=0;k<height;k++){
                    coefficient[j][k] = interCoeffMQC[j][k];
                } //End of inner for loop
            } //End of for loop
            
            width = width/2;
            height = height/2;   
        } //End of the decomposition level for loop
              
        return coefficient;      
    }   
       
public double[][] SAL2DDwt(double[][] coefficient, int[] size, int level, int T){    
        int width = size[0];
        int height = size[1];
        int j,k,m,n;
        
        
        for(int i=0;i<level;i++){
            interCoeffMQC = new double[width][height];
            for(j=0;j<width;j++){
                for(k=0;k<height;k++){
                    interCoeffMQC[j][k] = coefficient[j][k];
                } //End of inner for loop
            } //End of for loop
            
            double[] med = new double[4];
            a = new double[width/2][height/2];
            b = new double[width/2][height/2];
            c = new double[width/2][height/2];
            d = new double[width/2][height/2];
            
            int wIndex = 0;
            int hIndex = 0;
 
            for(m=0;m<width;m=m+2){
                hIndex = 0;
                for(n=0;n<height;n=n+2){
                    a[wIndex][hIndex] = interCoeffMQC[m][n];
                    b[wIndex][hIndex] = interCoeffMQC[m][n+1];
                    c[wIndex][hIndex] = interCoeffMQC[m+1][n];
                    d[wIndex][hIndex] = interCoeffMQC[m+1][n+1];
                                
                    hIndex++;
                }
                wIndex++;
            }
            
            double cR, bB, aa1, aa2, aa3; 
            double dL, pp, dU, cU, bL, d1, d2, d3, w, map, update;
            
            //d
            //d=d-((c+cR)/2+(b+bB)/2-(a+a1+a2+a3)/4);
            for(m=0;m<width/2;m++){
                for(n=0;n<height/2;n++){                
                    //Calculating values for cR
                    if(n==height/2-1){
                        cR = c[m][n];
                    } else{
                        cR = c[m][n+1];
                    }
                                                           
                    if(m==width/2-1){
                        bB = b[m][n];
                    } else{
                        bB = b[m+1][n];
                    }
                              
                    if(n==height/2-1){
                        aa1 = a[m][n];
                    } else{
                        aa1 = a[m][n+1];
                    }
                    
                    if(m==width/2-1){
                        aa2 = a[m][n];
                    } else{
                        aa2 = a[m+1][n];
                    }
                    
                    if((m==width/2-1)&&(n==height/2-1)){
                        aa3 = a[m][n];
                    } else if((m==width/2-1)&&(n!=height/2-1)){
                        aa3 = a[m][n+1];
                        
                    } else if((m!=width/2-1)&&(n==height/2-1)){
                        aa3 = a[m+1][n];
                        
                    } else{
                        aa3 = a[m+1][n+1];
                    }
                    
                    d[m][n] = d[m][n] - ((c[m][n]+cR)/2 + (b[m][n]+bB)/2 - (a[m][n]+aa1+aa2+aa3)/4); 
                }
            }
            
            //c
            //c=c-p;
            for(m=0;m<width/2;m++){
                for(n=0;n<height/2;n++){                    
                    if(n==0){
                        dL = d[m][n];
                    } else{
                        dL = d[m][n-1];
                    }
                    
                    
                    if(m==width/2-1){
                        aa2 = a[m][n];
                    } else{
                        aa2 = a[m+1][n];
                    }
                    
                    pp = (a[m][n] + aa2)/2 - (d[m][n]+dL)/4;
                    c[m][n] = c[m][n] - pp;     
                }
            }
            
            //b
            //b=b-p;
            for(m=0;m<width/2;m++){
                for(n=0;n<height/2;n++){    
                    if(m==0){
                        dU = d[m][n];
                    } else{
                        dU = d[m-1][n];
                    }
                    
                    if(n==height/2-1){
                        aa1 = a[m][n];
                    } else{
                        aa1 = a[m][n+1];
                    }
                              
                    pp = (a[m][n]+aa1)/2 - (d[m][n]+dU)/4;
                    b[m][n] = b[m][n] - pp;
                }
            }
            
            //a
            //a=a+update;
            for(m=0;m<width/2;m++){
                for(n=0;n<height/2;n++){                    
                    if(m==0){
                        cU = c[m][n];
                    } else{
                        cU = c[m-1][n];
                    }
                    
                    if(n==0){
                        bL = b[m][n];
                    } else{
                        bL = b[m][n-1];
                    }
                    
                    if(n==0){
                        d1 = d[m][n];
                    } else{
                        d1 = d[m][n-1];
                    }
                    
                    if(m==0){
                        d2 = d[m][n];
                    } else{
                        d2 = d[m-1][n];
                    }
                    
                    
                    if((m==0)&&(n==0)){
                        d3 = d[m][n];
                    } else if((m==0)&&(n!=0)){
                        d3 = d[m][n-1];
                        
                    } else if((m!=0)&&(n==0)){
                        d3 = d[m-1][n];
                        
                    } else{
                        d3 = d[m-1][n-1];
                    }
                    
                    w = (c[m][n]+cU)/2 + (b[m][n]+bL)/2 - (d[m][n]+d1+d2+d3)/8;
                    
                    if(Math.abs(w)<=T){
                        map = 1;
                    } else {
                        map = 0;
                    }
                    
                    update = 0.5 * w * map;
                    a[m][n] = a[m][n] + update; 
                }
            }
                                                           
            for(j=0;j<a.length;j++){
                for(k=0;k<a[0].length;k++){
                    interCoeffMQC[j][k] = a[j][k]*2 ;
                    interCoeffMQC[j][k+a[0].length] = b[j][k];
                    interCoeffMQC[j+a.length][k] = c[j][k];
                    interCoeffMQC[j+a.length][k+a[0].length] = d[j][k]/2;
                }
            }
            
          //replace the coefficients with intermediate coefficients 
            for(j=0;j<width;j++){
                for(k=0;k<height;k++){
                    coefficient[j][k] = interCoeffMQC[j][k];
                } //End of inner for loop
            } //End of for loop
            
            width = width/2;
            height = height/2;   
        } //End of the decomposition level for loop
          return coefficient;
      }
        
    
}
