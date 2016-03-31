
package MCTF;

import Main.ReadParameter;


/**
 * @author: Deepayan Bhowmik
 *
 * E-mail: d.bhowmik@sheffield.ac.uk
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 *
 * No part of the code can be copied without permission of the author
 */
public class MEvar {
    ReadParameter param = new ReadParameter();
    int x = 0, y = 0;
    int inter = 1;
    double SAD = 0, distance = 0;   
    double[][] SAD_search = new double[param.getShWindow()*2+1][param.getShWindow()*2+1];
}
