/*
 * ReadParameter.java
 *
 *
 * @ author: Deepayan Bhowmik 
 *
 * Copyright reserved
 */

package Main;

import javax.swing.*;
import java.io.*;

// This part of the program helps to read parameters used as various input to 
// the program. And store the respective values in appropriate variables 

public class ReadParameter {
    // Declaration of global variables which hold the parameter data
    public static int decompLevel;
    public static String waveletName, watermarkName, codeStream;        
    public static String inName, outName;
    public static int startFrame, numFrame, vidWidth, vidHeight;
    public static String MEmode, blSize;
    public static int searchWindow;
    public static double intraThreshold;
    public static int mvInfoLength;
    public static int d2td2Level;
    public static int frameRate;
    public static int depth, packetSize;
    

   
    // Reading the parameters from the parameter file
    public void readParameterFile(String fileString) {
        help usage = new help();

        File embedParaFile = null;               
        // Input: parameter file and Check for file existance                   
        embedParaFile = new File(fileString);
        if(!embedParaFile.exists()){
            usage.usageHelp(0);
        }

        String extension = fileString.substring(fileString.length()-3, fileString.length());

        if(extension.compareTo("prm")!=0){ // Image watermarking
            usage.usageHelp(0);
        }
        
        // Read and save the variable values in different arrays
        try{ // Read the file content
            BufferedReader paraInput = new BufferedReader(new FileReader(embedParaFile));
            
            while(true){
                String tempRead = paraInput.readLine();
                if(tempRead == null){
                    break;
                }
                
                if((tempRead.startsWith("#")==false) && (tempRead.isEmpty()==false)){
                    tempRead = tempRead.trim(); //Get rid of the starting spaces and end spaces
                    char[] tempChar = tempRead.toCharArray();                    
                    String identity="", value="";                    
                    int countHash = 0;
                                                                                                    
                    for(int i=0; i<tempChar.length; i++){                        
                        if(countHash==0){                             
                            if((tempChar[i] != ' ')&&(tempChar[i] != 9)){ //Read identity. Remove space and horizontal tab
                                identity = identity + tempChar[i];
                            } else {
                                countHash++;
                            }
                        }                        
                        if(countHash==1){                             
                            if((tempChar[i] != ' ')&&(tempChar[i] != 9)){ //Read value. Remove space and horizontal tab
                                value = value + tempChar[i];
                            } 
                        }                         
                    }
                    
                    if(value.indexOf('#')!=-1){ //Get rid of comments 
                        value = value.substring(0,value.indexOf('#'));                        
                    }
                     
                    identity = identity.toLowerCase();
                  //  System.out.println(identity);
                    
                    if(identity.compareTo("codestream")==0){
                        codeStream = value;
                    } else if(identity.compareTo("wmname")==0){                        
                        watermarkName = value;
                    } 
                    //-----------------------------------------------------------------------
                    else if(identity.compareTo("level")==0){                         
                        decompLevel = Integer.parseInt(value);
                    } else if(identity.compareTo("wavelet")==0){                        
                        waveletName = value.toUpperCase();
                    }                   
                    //-----------------------------------------------------------------------   
                    else if(identity.compareTo("inname")==0){
                        inName = value;
                    } else if(identity.compareTo("outname")==0){
                        outName = value;
                    } else if(identity.compareTo("startframe")==0){
                        startFrame = Integer.parseInt(value);
                    } else if(identity.compareTo("numframe")==0){
                        numFrame = Integer.parseInt(value);
                    } else if(identity.compareTo("vidwidth")==0){
                        vidWidth = Integer.parseInt(value);
                    } else if(identity.compareTo("vidheight")==0){
                        vidHeight = Integer.parseInt(value);
                    }
                    //-----------------------------------------------------------------------
                    else if(identity.compareTo("memode")==0){
                        MEmode = value;
                    } else if(identity.compareTo("blsize")==0){
                        blSize = value;
                    } else if(identity.compareTo("searchwindow")==0){
                        searchWindow = Integer.parseInt(value);
                    } else if(identity.compareTo("intrathreshold")==0){
                        intraThreshold = Double.parseDouble(value);
                    } else if(identity.compareTo("2dt2d")==0){
                        d2td2Level = Integer.parseInt(value);
                    } else if(identity.compareTo("framerate")==0){
                        frameRate = Integer.parseInt(value);
                    } else if(identity.compareTo("depth")==0){
                        depth = Integer.parseInt(value);
                    } else if(identity.compareTo("packetsize")==0){
                        packetSize = Integer.parseInt(value);
                    }
                }                                                
            }           
        } catch(IOException ioException) {
            JOptionPane.showMessageDialog(null,"File Error !!", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        
        
    }

    //Make different parameter values ready to be read by other parts of the program


    public int getPacketSize(){
        return packetSize;
    }

    public int getDepth(){
        return depth;
    }

    public int getFrameRate(){
        return frameRate;
    }
    public int[] getDecomLevel3D(){
        int[] decompositionLevel = new int[3];
            decompositionLevel[0] = d2td2Level / 100;
            decompositionLevel[1] = (d2td2Level % 100) / 10;
            decompositionLevel[2] = d2td2Level % 10;
        return decompositionLevel;
    }

    public int getMVinfoLength(){
        mvInfoLength = 3; // MV information length is decided as :
                            // int x, int y
                            // int inter code decision
                            // Total 3 integers.
                            // Change the value of 'mvInfoLenght' if this format changes.
        return mvInfoLength;
    }

    public double getIntraThreshold(){
        return intraThreshold;
    }
    
    public int getShWindow(){
        return searchWindow;
    }

    public int[] getBlSize(){
        int[] blSizeValue = new int[2];
        if(blSize.compareTo("2x2")==0){
            blSizeValue[0] = 2;
            blSizeValue[1] = 2;
        } else if(blSize.compareTo("4x4")==0){
            blSizeValue[0] = 4;
            blSizeValue[1] = 4;
        } else if(blSize.compareTo("8x8")==0){
            blSizeValue[0] = 8;
            blSizeValue[1] = 8;
        } else if(blSize.compareTo("16x16")==0){
            blSizeValue[0] = 16;
            blSizeValue[1] = 16;
        }        
        return blSizeValue;
    }


    public String getMEmode(){
        return MEmode;
    }
        
    public int getDecompLevel(){
        return decompLevel;
    }
    
    public String getWaveletName(){
        return waveletName;
    }
     
    public String getWatermarkName(){
        return watermarkName;
    }
      
    public String getCodeStreamName(){
        return codeStream;
    }
    
    public String getInName(){
        return inName;
    }
    
    public String getOutName(){
        return outName;
    }
    
    public int getStartFrame(){
        return startFrame;
    }
    
    public int getNumFrame(){
        return numFrame;
    }

    public int getVidWidth(){
        return vidWidth;
    }
    
    public int getVidHeight(){
        return vidHeight;
    }
          
    
} // End of the ReadParameter class
