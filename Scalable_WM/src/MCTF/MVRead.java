package MCTF;

import Main.*;
import java.io.*;
import javax.swing.*;

/**
 * @author: Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 */
public class MVRead {
    public static int[] YmvRead;

    public void mvRead(String inputMVFile, int offset, int[] size){
        ReadParameter param = new ReadParameter();        

        // Declear variable to read video file
        FileInputStream mvFileStream = null;
        BufferedInputStream bufferMVStream = null;
        DataInputStream mvStream = null;

        // M, N are the size of Blocks to be searched
        int M = param.getBlSize()[0];
        int N = param.getBlSize()[1];

        int mvInfoLength = param.getMVinfoLength();     
        YmvRead = new int[(size[0]*mvInfoLength/M) * (size[1]/N)];
     
        try {
            mvFileStream = new FileInputStream(inputMVFile);     
            bufferMVStream = new BufferedInputStream(mvFileStream);    
            mvStream = new DataInputStream(bufferMVStream);
            mvStream.skipBytes(offset*(YmvRead.length)*4);

            for(int Yi=0; Yi<YmvRead.length; Yi++){
                YmvRead[Yi] = mvStream.readInt();
            }

            mvStream.close();
            bufferMVStream.close();
            mvFileStream.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Can not read MV value from: " + inputMVFile, "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        } // End of Catch        
    }

    public int[] getYmv(){
        return YmvRead;
    }

}
