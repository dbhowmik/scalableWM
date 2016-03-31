/*
 * Decoding.java
 *
 *
 * @author Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 *
 */

package Extract;

import Main.ReadParameter;
import Watermark.Watermark;

public class Decoding {     
    public static double normHammDist, simMeas;
    public void Decoding(double[] wmCoeff) {
        //----------------------------------------------------------------------
        ReadParameter param = new ReadParameter();                    
        //----------------------------------------------------------------------
        normHammDist = 0;
        simMeas = 0;        
        //----------------------------------------------------------------------
        Watermark wm = new Watermark();
        wm.Watermark();
        int[] orgWm = wm.getWatermark();
        int depth = param.getDepth();
        int extWm = -1;

        //---- Tree formation and watermark detection --------------------------
        int totalWmCount = 0, wmCount = 0, HammDist = 0;
        int extOrgWmSum = 0, extWmSum = 0, orgWmSum = 0;
                
        for(int i=0; i<wmCoeff.length; i++){
            extWm = formTreeExt(Math.round(wmCoeff[i]), depth);            
            //-------- Calculate Hamming Distance and similarity measure -------
            HammDist += orgWm[wmCount]^extWm;           
            extOrgWmSum += orgWm[wmCount]*extWm;
            extWmSum += Math.pow(extWm,2);            
            orgWmSum += Math.pow(orgWm[wmCount],2);
            totalWmCount++;
            //------------ Repeat the watermark code ---------------------------
            if(++wmCount>=orgWm.length) wmCount=0;
        }
  
        //------  Authentication metrics ---------------------------------------
        //---------- Hamming Distance ------------------------------------------
        normHammDist = (double)HammDist/totalWmCount;
        if(normHammDist < 0.001){
            HammDist = 0;
        }
        
        //----------- Similarity Measure in percentage -------------------------
        simMeas = extOrgWmSum/Math.sqrt((double)extWmSum*orgWmSum) * 100.00 ;
    }


    public int formTreeExt(double Scoeff, int depth){        
        int wm = -1;
        int coeff = (int)Math.abs(Scoeff);
        int Q_factor = (int)Math.pow(2,(depth-1));
        //---------- Tree formation --------------------------------------------
        int tree = 0;
        int tempTree = 0;
        for(int i=depth; i>0; i--){
            tempTree = ((int)Math.floor(coeff/Q_factor))%2;
            tree += tempTree * Math.pow(2,i-1);
            Q_factor /=2;
        }       
        //----------------- Extract the watermark ------------------------------
        Q_factor = (int)Math.pow(2,(depth-1));
        if((int)Math.floor(tree/Q_factor)%2==0){ // Case: 0xx
            if((int)Math.floor(tree/(Q_factor/2))%2==0){ // Case: EZ = 00x
                wm = 0;
            } else { // Case: 01x
                if((int)Math.floor(tree/(Q_factor/4))%2==0){ // Case: CZ = 010
                    wm = 0;
                } else{ // Case: WO = 011
                    wm = 1;
                }
            }
        } else { // Case: 1xx
            if((int)Math.floor(tree/(Q_factor/2))%2==1){ // Case: EO = 11x
                wm = 1;
            } else { // Case: 10x
                if((int)Math.floor(tree/(Q_factor/4))%2==1){ // Case CO = 101
                    wm = 1;
                } else{ // Case: WZ = 100
                    wm = 0;
                }
            }
        }        
        //---------------- Returun the watermark value -------------------------
        return wm;
    }

    public double getHammDist(){
        return normHammDist;
    }

    public double getSimMeas(){
        return simMeas;
    }
    
} //End of the class
