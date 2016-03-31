/*
 * Rescaling.java
 *
 * @author Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 *
 */

package extract;
import Embedding.*;

public class Rescaling {
    int[] orSize;
    
    
    public double[][] Rescaling(double[][] wmImage, int[] size) {        
        ReadParameter parameter = new ReadParameter();
        Imread image = new Imread();
        Dwt wavelet = new Dwt();
    
        double[][] originalImage = image.Imread(parameter.getOriginalImageName());    
        orSize = image.Size();
        
        int wSize = orSize[0]/size[0];
        
        
        double[][] intermediate = new double[wmImage.length][wmImage[0].length];
        double[][] rescaledImage = new double[originalImage.length][originalImage[0].length];
        
        if(wSize>1){
            for(int i=0;i<wmImage.length;i++){
                for(int j=0;j<wmImage[0].length;j++){
                    intermediate[i][j] = wmImage[i][j]*Math.pow(2,wSize-1);                    
                }
            }
            
            int level = parameter.getDecompLevel() - (wSize - 1);
            intermediate = wavelet.dwt(intermediate,size,level,parameter.getWaveletName());
            
            for(int m=0;m<intermediate.length;m++){
                for(int n=0;n<intermediate[0].length;n++){
                    rescaledImage[m][n] = intermediate[m][n];                    
                }
            }
            
        } else{
            rescaledImage = wavelet.dwt(wmImage,size,parameter.getDecompLevel(),parameter.getWaveletName());
        }
        
        return rescaledImage;
    }
    
    public int[] getOriginalImageSize(){
        return orSize;
    }
    
}
