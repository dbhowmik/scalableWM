/*
 * Embedding.java
 *
 * @author Deepayan Bhowmik
 */

package Embedding;

// This part of the programm actually organise all embedding procedures
public class Embedding {
   
    public double[][] Embedding(double[][] imageCoeff, int[] size, int level) {
      
      ReadParameter parameter = new ReadParameter();
 //     DirectCoeffModification embeddingDirect = new DirectCoeffModification();
      QuantisationEmbedding embeddingQnt = new QuantisationEmbedding();
      
      double[][] modifiedImageCoeff = imageCoeff; // Be ready for modified coeffs
    
      //Getting all regions of interest i.e. selected subbands for embedding
      int xROI = size[1]/(int)(Math.pow(2,level-1));
      int yROI = size[0]/(int)(Math.pow(2,level-1));
      double[][] coeffROI = new double[xROI][yROI]; // Selected subband coefficients
      double[][] modifyROI = new double[xROI][yROI]; // Be ready for modified coeffs
      // Get it separated
      for(int i=0; i<xROI; i++){
          for(int j=0; j<yROI; j++){
              coeffROI[i][j] = modifiedImageCoeff[i][j]; 
          }
      }    
      
      // Deal with selected methods only
   //   if (parameter.getEmbedProcedure().compareTo("DR") == 0){
          // Direct coefficient modification is selected
   //       modifyROI = embeddingDirect.DirectCoeffModification(coeffROI);
          
   //   } else if(parameter.getEmbedProcedure().compareTo("QN") == 0){
          // Quantisation method to follow
          modifyROI = embeddingQnt.QuantisationEmbedding(coeffROI);       
          
   //   } //End of if statement
   
      // Get back modified coefficients and put it in right order 
      for(int m=0; m<xROI; m++){
          for(int n=0; n<yROI; n++){
              modifiedImageCoeff[m][n] = modifyROI[m][n];
          }
      }
      
      //Return modified coefficients
      return modifiedImageCoeff;
    }
    
}


