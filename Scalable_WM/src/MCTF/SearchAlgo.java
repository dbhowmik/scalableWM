
package MCTF;

import Main.*;


/**
 * @author: Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 *
 */
public class SearchAlgo {

    public void searchAlgo(double[][] currFrame, double[][] refFrame, boolean fileStart, String inVideoName){
        ReadParameter parameter = new ReadParameter();
        FSBM fsbm = new FSBM();

        if(parameter.getMEmode().compareTo("fsbm")==0){
            //Only fsbm mode available
            fsbm.fsbm(currFrame, refFrame, fileStart, inVideoName);
        } else if(parameter.getMEmode().compareTo("hvsbm")==0){
            //hvsm mode is not available now.
            System.exit(-1);
        }
        
    }
    

}
