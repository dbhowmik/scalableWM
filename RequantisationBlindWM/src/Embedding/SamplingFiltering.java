/*
 * SamplingFiltering.java
 *
 * @author Deepayan Bhowmik
 */

package Embedding;

public class SamplingFiltering {
    public void SamplingFiltering() {
     /* This class and function organisaes all the filtering and up / down sampling parameters.
        This particular function is just to check all the values. No operation so far ... Deepayan says so :-)
      */     
       
    }
    
    public double[] Lshift(double[] image){
        double[] imshift = new double[image.length];
        for(int i=1; i<image.length; i++){
            imshift[i-1] = image[i];
        }
        imshift[image.length-1] = image[0];
        return imshift;
    }
    
     public double[] Rshift(double[] image){
        double[] imshift = new double[image.length];
        for(int i=1; i<image.length; i++){
            imshift[i] = image[i-1];
        }
        imshift[0] = image[image.length-1];
        return imshift;
    }
    
    public double[] Highpass(double[] filter_coeff, double[] image){
        double[] pixel_coeff = Downsample(ConvHL(filter_coeff,Lshift(image)));
        return pixel_coeff;
    }
    
     public double[] Lowpass(double[] filter_coeff, double[] image){
        double[] pixel_coeff = Downsample(ConvLH(filter_coeff,image)); 
        return pixel_coeff;      
    }
    
    public double[] InvHighpass(double[] filter_coeff, double[] image){     
        double[] pixel = ConvLH(filter_coeff, Rshift(Upsample(image)));     
        return pixel;
    }
    
    public double[] InvLowpass(double[] filter_coeff, double[] image){
        double[] pixel = ConvHL(filter_coeff,Upsample(image));
        return pixel;      
    }
    
    public double[] ConvLH(double[] filter_coeff, double[] image){
        double[] padded_image = new double[image.length+filter_coeff.length];
        for(int i=0; i<image.length; i++){
            padded_image[i] = image[i];
        }
        
        for(int j=image.length; j<(image.length+filter_coeff.length); j++){
            padded_image[j] = image[j-image.length];
        }
                
        Convolution func = new Convolution();
        double[] padout_coeff = func.Convolution(filter_coeff,padded_image);
             
        double[] pixel_coeff_dw = new double[image.length];
        for(int i=0;i<image.length;i++){
            pixel_coeff_dw[i] = padout_coeff[filter_coeff.length-1+i];
        }
        return pixel_coeff_dw;
    }
    
    public double[] ConvHL(double[] filter_coeff, double[] image){
        double[] padded_image = new double[image.length+filter_coeff.length];
        for(int i=0; i<(filter_coeff.length);i++){
            padded_image[i] = image[(image.length-filter_coeff.length) + i];
        }
          
        for(int j=(filter_coeff.length); j<(image.length+filter_coeff.length);j++){
            padded_image[j] = image[j-(filter_coeff.length)];
        }
        
        Convolution func = new Convolution();
        double[] padout_coeff = func.Convolution(filter_coeff,padded_image);
             
        double[] pixel_coeff_dw = new double[image.length];
        for(int i=0;i<image.length;i++){
            pixel_coeff_dw[i] = padout_coeff[filter_coeff.length+i];
        }
        return pixel_coeff_dw;
    }
    
    public double[] Downsample(double[] image_coeff){
        double[] image_ds = new double[image_coeff.length/2];
        for(int i=0; i<image_coeff.length/2; i++){
            image_ds[i] = image_coeff[i*2];
        }
        return image_ds;
    }
    
     public double[] Upsample(double[] image_coeff){
        double[] image_us = new double[image_coeff.length*2];
        for(int i=0; i<image_coeff.length*2; i++){
            if((i%2)==0){
                image_us[i] = image_coeff[i/2];
            } else{
                image_us[i] = 0;
            }
        }
        return image_us;
    }
    
}
