/**
 * @author: Deepayan Bhowmik
 *
 * Department of Electronic and Electrical Engineering
 * University of Sheffield
 * Copyright reserved
 *
 * No part of the code can be copied without permission of the author
 */

package Main;

import java.io.IOException;

public class help {
    public void usageHelp(int errorCode){

        switch(errorCode){
            case 0: System.out.println("# 0: Parameter file does not " +
                                        "exist or file type not supported");
                    break;
            case 1: System.out.println("# 1: Input media not recognised. ");
                    break;
        }

        try {
            System.out.println("\n\n  Press ENTER..");
            System.in.read();
            System.exit(errorCode);
        } catch(IOException exe) {
            System.out.println("Error!!");
        }
       
    }

}
