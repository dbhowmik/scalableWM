/*
 * MatrixFunction.java
 *
 *
 * @author Deepayan Bhowmik
 * 
 */
// Part of WEBCAM Software
package DSPnMath;

import java.util.*;
public class MatrixFunction {
    
    /** Creates a new instance of MatrixFunction */
    public void MatrixFunction() {
        /* Test area 
         
         */
    }
    
    // This function finds the maximum value of a 2D matrix
    public double Max2D(double[][] matrix){
        double max = -(10.0e10);
        
        for(int i=0;i<matrix.length;i++){
            for(int j=0;j<matrix[0].length;j++){
                if(max < matrix[i][j]){
                    max = matrix[i][j];
                }
            }
        }    
        return max;
    }
      
    // This function finds the maximum value of a 1D matrix
    public double Max1D(double[] matrix){
        double max = -(10.0e10);
        
        for(int i=0;i<matrix.length;i++){
            if(max < matrix[i]){
                max = matrix[i];
            }
        }    
        return max;
    }
    
    
    // This function finds the minimum value of a 2D matrix
    public double Min2D(double[][] matrix){
        double min = 10.0e10;
        int a =0, b=0;
        for(int i=0;i<matrix.length;i++){
            for(int j=0;j<matrix[0].length;j++){
                if(min > matrix[i][j]){
                    min = matrix[i][j];
                    a = i;
                    b = j;
                }
            }
        }    
        System.out.println(a + " " + b);
        return min;
    }
      
    // This function finds the minimum value of a 1D matrix
    public double Min1D(double[] matrix){
        double min = 10.0e10;
        
        for(int i=0;i<matrix.length;i++){
            if(min > matrix[i]){
                min = matrix[i];
            }
        }    
        return min;
    }
    
    //Find median value of 1D matrix
    
    public double Median1D(double[] matrix){
        
        int mid_1, mid_2;
        int mid;
        double median;
        Arrays.sort(matrix);
        
        if((matrix.length%2)==0){ // In the case of matrix length is even
            mid_1 = matrix.length/2 - 1;
            mid_2 = mid_1+1;
            
            median = (matrix[mid_1] + matrix[mid_2] ) / 2 ;
        } else{ // In the case of matrix length is odd
            mid = (matrix.length+1)/2 - 1;
            median = matrix[mid];
        }
        return median;
    }
    
    public double[][] Dynamic2D(double[][] matrixOrg, double[][] matrixTest){
        MatrixFunction mat = new MatrixFunction();
        double[][] matrixReturn = matrixTest;
        double minOrg = mat.Min2D(matrixOrg);        
        double maxOrg = mat.Max2D(matrixOrg);
        
        for(int i=0; i<matrixTest.length; i++){
            for(int j=0; j<matrixTest[0].length; j++){
                matrixReturn[i][j] = Math.min(maxOrg, Math.max(matrixTest[i][j],minOrg));
            }
        }
                
        return matrixReturn; 
        
    }
    
    public double cislMedian1D(double[] matrix){
        double median;
        double max = Max1D(matrix);
        double min = Min1D(matrix);
        if(min<0){
            median = 0;
        } else{
            median = min;
        }
        return median;
    }
    
}
