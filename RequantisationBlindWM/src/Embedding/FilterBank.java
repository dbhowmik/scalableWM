/*
 * FilterBank.java
 *
 */

package Embedding;

/**
 *
 * @author Deepayan Bhowmik
 */
public class FilterBank {
    double[][] filter_return;
    /**
     * Creates a new instance of FilterBank
     */
    public double[][] Filter_bank(String filter_select) {
        
         // Filter coefficients are defined for analysis and synthesis filters in proper order
            // [0][] = Low pass analysis filter coeff
            // [1][] = High pass analysis filter coeff
            // [2][] = Low pass sysnthesis filter coeff
            // [3][] = High pass synthesis filter coeff
        
        if(filter_select.compareTo("D4") == 0)
        {  // D4 = Daubechies-4 Filters
                    
            double[][] filterD4 = {{-0.1294095225512603, 0.2241438680420134, 0.8365163037378077, 0.4829629131445341},
                                   {0.4829629131445341, -0.8365163037378077, 0.2241438680420134, 0.1294095225512603},
                                   {0.4829629131445341, 0.8365163037378077, 0.2241438680420134, -0.1294095225512603},
                                   {0.1294095225512603, 0.2241438680420134, -0.8365163037378077, 0.4829629131445341}};
            filter_return = filterD4;
        } else if(filter_select.compareTo("HR") == 0){
            // Haar filter coefficients
            double[][] filterHR = {{0.7071, 0.7071},
                                   {0.7071, -0.7071},
                                   {0.7071, 0.7071},
                                   {-0.7071, 0.7071}};
            filter_return = filterHR;
            
        } else if(filter_select.compareTo("97") == 0){
            // 9/7 biorthogonal
              double[][] filter97 = {{0.026748757411, -0.016864118443, -0.078223266529, 0.266864118443, 0.602949018236, 0.266864118443, -0.078223266529, -0.016864118443, 0.026748757411},
                                     {0, 0.091271763114, -0.057543526229, -0.591271763114, 1.11508705, -0.591271763114, -0.057543526229, 0.091271763114, 0},
                                     {0, -0.091271763114, -0.057543526229, 0.591271763114, 1.11508705, 0.591271763114, -0.057543526229, -0.091271763114, 0},
                                     {0.026748757411, 0.016864118443, -0.078223266529, -0.266864118443, 0.602949018236, -0.266864118443, -0.078223266529, 0.016864118443, 0.026748757411}};  
            
//           
              filter_return = filter97;
            
        } else if(filter_select.compareTo("53") == 0){
            // 5/3 biorthogonal
             double[][] filter53 = {{-0.176776695, 0.353553391, 1.060660172, 0.353553391, -0.176776695},                                    
                                    {0, -0.353553391, 0.707106781, -0.353553391, 0},
                                    {0, 0.353553391, 0.707106781, 0.353553391, 0},
                                    {-0.176776695, -0.353553391, 1.060660172, -0.353553391, -0.176776695}}; 
            
            filter_return = filter53;
            
        }
      
        return filter_return;
    }
    
}
