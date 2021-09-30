
/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.in;

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor; 
import java.lang.reflect.Field; 
import java.lang.reflect.Method; 
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.logging.Level;
//import java.util.logging.Logger;
import static java.lang.Integer.max;

import org.yaml.snakeyaml.events.*;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.reader.ReaderException;

import loci.common.Location;
import loci.common.Constants;
import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.MissingLibraryException;

import ome.xml.model.primitives.PositiveFloat;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.UNITS;

import org.scijava.nativelib.NativeLibraryUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SlideBook7Reader  extends FormatReader {

    public static Logger LOGGER =
        LoggerFactory.getLogger(SlideBook7Reader.class);

    static class StrIntPair
    {
        public String mStr;
        public Integer mInt;
        public StrIntPair(String theStr, Integer theInt)
        {
            mStr = theStr;
            mInt = theInt;
        }

    }

    static class IntIntPair
    {
        public Integer mInt1;
        public Integer mInt2;
        public IntIntPair(Integer theInt1, Integer theInt2)
        {
            mInt1 = theInt1;
            mInt2 = theInt2;
        }

    }

    class ClassDecoder {




        public int Decode(MappingNode inNode)
        {
            return Decode(inNode,0);
        }

        public int Decode(MappingNode inNode, int inStartIndex)
        {
            Class cls = getClass(); 
            String myName = cls.getName();
            String sbName = GetSBClassName();
            Class<?> theTypeInteger = Integer.class;
            Class<?> theTypeFloat = Float.class;
            Class<?> theTypeDouble = Double.class;
            Class<?> theTypeBoolean = Boolean.class;
            Class<?> theTypeString = String.class;
            Class<?> theTypeIntegerVector = Integer[].class;
            Class<?> theTypeFloatVector = Float[].class;
            //SlideBook7Reader.LOGGER.info("Decode: sbName: " + sbName);
            //SlideBook7Reader.LOGGER.info("Decode: myName: " + myName);
            myName = myName.replaceAll(".*\\$","");
            //SlideBook7Reader.LOGGER.info("Decode: after replacement myName: " + myName);


            Field[] fields =  cls.getDeclaredFields();
            HashMap <String,Field> nameToFieldMap = new HashMap<String, Field>();

            for (Field theField:fields) 
            {
                nameToFieldMap.put(theField.getName(),theField);
                //SlideBook7Reader.LOGGER.info("nameToFieldMap.put theField.getName(): " + theField.getName());
            }

            List<NodeTuple> theValueClassList = inNode.getValue();
            int theClassIndex;
            for(theClassIndex = inStartIndex; theClassIndex < theValueClassList.size(); theClassIndex++)
            {
                ScalarNode theKeyNode =  (ScalarNode)theValueClassList.get(theClassIndex).getKeyNode();
                String theStr = theKeyNode.getValue();
                if(theStr.equals("EndClass")) break;
                if(!theStr.equals("StartClass")) continue;
                MappingNode theValueMappingNode;
                        theValueMappingNode = (MappingNode)theValueClassList.get(theClassIndex).getValueNode();
                List<NodeTuple> theValueAttributeList = theValueMappingNode.getValue();
                for(int theAttrIndex = 0; theAttrIndex < theValueAttributeList.size(); theAttrIndex++)
                {
                    ScalarNode theAttrKeyNode = (ScalarNode)theValueAttributeList.get(theAttrIndex).getKeyNode();
                    Node theAttrValueNode  =  theValueAttributeList.get(theAttrIndex).getValueNode();
                    String theAttrName = theAttrKeyNode.getValue();
                    //SlideBook7Reader.LOGGER.info("theAttrName: " + theAttrName);
                    Field theField = nameToFieldMap.get(theAttrName);
                    String theFieldName;
                    if(theField == null && theAttrIndex > 0)
                    {
                        DecodeUnknownString(theAttrName,theAttrValueNode);
                        continue;
                    }

                    if(theAttrValueNode  instanceof ScalarNode)
                    {
                        ScalarNode theAttrValueScalarNode = (ScalarNode)theAttrValueNode ;
                        String theAttrValue = theAttrValueScalarNode.getValue();
                        if(theAttrIndex == 0)
                        {
                            if(!theAttrName.equals("ClassName")) break; // error?
                            if(!theAttrValue.equals(sbName)) break;   // not this class
                            continue;
                        }
                        //if(theTypeName.equals("java.lang.Integer"))
                        if(theField.getType().isAssignableFrom(theTypeInteger))
                        {
                            Integer theVal = Integer.valueOf(theAttrValue);
                            //SlideBook7Reader.LOGGER.info("name: " + theField.getName() + " - value: " + theVal);
                            try {
                                theField.set(this,theVal);
                            } catch (IllegalArgumentException ex) {
                                SlideBook7Reader.LOGGER.info(ClassDecoder.class.getName() + ": " + ex.getMessage());
                            } catch (IllegalAccessException ex) {
                                SlideBook7Reader.LOGGER.info(ClassDecoder.class.getName() + ": " + ex.getMessage());
                            }
                        }
                        else if(theField.getType().isAssignableFrom(theTypeDouble))
                        {
                            Double theVal = Double.valueOf(theAttrValue);
                            try {
                                theField.set(this,theVal);
                            } catch (IllegalArgumentException ex) {
                                SlideBook7Reader.LOGGER.info(ClassDecoder.class.getName() + ": " + ex.getMessage());
                            } catch (IllegalAccessException ex) {
                                SlideBook7Reader.LOGGER.info(ClassDecoder.class.getName() + ": " + ex.getMessage());
                            }
                        }
                        else if(theField.getType().isAssignableFrom(theTypeFloat))
                        {
                            Float theVal = Float.valueOf(theAttrValue);
                            try {
                                theField.set(this,theVal);
                            } catch (IllegalArgumentException ex) {
                                SlideBook7Reader.LOGGER.info(ClassDecoder.class.getName() + ": " + ex.getMessage());
                            } catch (IllegalAccessException ex) {
                                SlideBook7Reader.LOGGER.info(ClassDecoder.class.getName() + ": " + ex.getMessage());
                            }
                        }
                        else if(theField.getType().isAssignableFrom(theTypeBoolean))
                        {
                            try {
                                if(theAttrValue.equals("true"))
                                    theField.set(this,true);
                                else
                                    theField.set(this,false);
                            } catch (IllegalArgumentException ex) {
                                SlideBook7Reader.LOGGER.info(ClassDecoder.class.getName() + ": " + ex.getMessage());
                            } catch (IllegalAccessException ex) {
                                SlideBook7Reader.LOGGER.info(ClassDecoder.class.getName() + ": " + ex.getMessage());
                            }
                        }

                        //else if(theTypeName.equals("java.lang.String"))
                        else if(theField.getType().isAssignableFrom(theTypeString))
                        {
                            theAttrValue = RestoreSpecialCharacters(theAttrValue);
                            try {
                                theField.set(this,theAttrValue);
                            } catch (IllegalArgumentException ex) {
                                SlideBook7Reader.LOGGER.info(ClassDecoder.class.getName() + ": " + ex.getMessage());
                            } catch (IllegalAccessException ex) {
                                SlideBook7Reader.LOGGER.info(ClassDecoder.class.getName() + ": " + ex.getMessage());
                            }
                        }
                    }
                    else if(theAttrValueNode  instanceof SequenceNode)
                    {
                        SequenceNode theAttrValueSequenceNode = (SequenceNode)theAttrValueNode ;
                        List <Node> theScalarNodeList = theAttrValueSequenceNode.getValue();
                        int theListSize = theScalarNodeList.size();
                        if(theListSize <= 1) continue;

                        if(theField.getType().isAssignableFrom(theTypeIntegerVector))
                        {
                            Integer[] theArray = new Integer[theListSize-1];
                            for(int theListNode=0;theListNode<theListSize;theListNode++)
                            {
                                ScalarNode theAttrValueScalarNode = (ScalarNode)theScalarNodeList.get(theListNode);
                                String theAttrValue = theAttrValueScalarNode.getValue();
                                if(theListNode == 0)
                                {
                                    if(Integer.valueOf(theAttrValue) != theListSize-1)
                                    {
                                        SlideBook7Reader.LOGGER.info("theAttrName: " + theAttrName);
                                        SlideBook7Reader.LOGGER.info("theAttrValue: " + theAttrValue);
                                        SlideBook7Reader.LOGGER.info("theListSize: " + theListSize);
                                        SlideBook7Reader.LOGGER.info("Integer.valueOf(theAttrValue) != theListSize");
                                    }
                                    continue;
                                }
                                theArray[theListNode-1] = Integer.valueOf(theAttrValue);
                            }
                            try {
                                theField.set(this,theArray);
                            } catch (IllegalArgumentException ex) {
                                SlideBook7Reader.LOGGER.info(ClassDecoder.class.getName() + ": " + ex.getMessage());
                            } catch (IllegalAccessException ex) {
                                SlideBook7Reader.LOGGER.info(ClassDecoder.class.getName() + ": " + ex.getMessage());
                            }

                        }
                        else if(theField.getType().isAssignableFrom(theTypeFloatVector))
                        {
                            Float[] theArray = new Float[theListSize-1];
                            for(int theListNode=0;theListNode<theListSize;theListNode++)
                            {
                                ScalarNode theAttrValueScalarNode = (ScalarNode)theScalarNodeList.get(theListNode);
                                String theAttrValue = theAttrValueScalarNode.getValue();
                                if(theListNode == 0)
                                {
                                    if(Integer.valueOf(theAttrValue) != theListSize-1)
                                    {
                                        SlideBook7Reader.LOGGER.info("Integer.valueOf(theAttrValue) != theListSize");
                                    }
                                    continue;
                                }

                                theArray[theListNode-1] = Float.valueOf(theAttrValue);
                            }
                            try {
                                theField.set(this,theArray);
                            } catch (IllegalArgumentException ex) {
                                SlideBook7Reader.LOGGER.info(ClassDecoder.class.getName() + ": " + ex.getMessage());
                            } catch (IllegalAccessException ex) {
                                SlideBook7Reader.LOGGER.info(ClassDecoder.class.getName() + ": " + ex.getMessage());
                            }

                        }
                    }
                }
            }
            return theClassIndex +1;
        }

        StrIntPair FindNextClass(MappingNode inNode, int inStartIndex)
        {
            List<NodeTuple> theValueClassList = inNode.getValue();
            int theClassIndex;
            for(theClassIndex = inStartIndex; theClassIndex < theValueClassList.size(); theClassIndex++)
            {
                ScalarNode theKeyNode =  (ScalarNode)theValueClassList.get(theClassIndex).getKeyNode();
                String theStr = theKeyNode.getValue();
                if(theStr.equals("EndClass")) break;
                if(!theStr.equals("StartClass")) continue;
                MappingNode theValueMappingNode;
                        theValueMappingNode = (MappingNode)theValueClassList.get(theClassIndex).getValueNode();
                List<NodeTuple> theValueAttributeList = theValueMappingNode.getValue();
                for(int theAttrIndex = 0; theAttrIndex < theValueAttributeList.size(); theAttrIndex++)
                {
                    ScalarNode theAttrKeyNode = (ScalarNode)theValueAttributeList.get(theAttrIndex).getKeyNode();
                    String theAttrName = theAttrKeyNode.getValue();

                    Node theAttrValueNode  =  theValueAttributeList.get(theAttrIndex).getValueNode();
                    if(theAttrValueNode  instanceof ScalarNode)
                    {
                        ScalarNode theAttrValueScalarNode = (ScalarNode)theAttrValueNode ;
                        String theAttrValue = theAttrValueScalarNode.getValue();
                        if(theAttrIndex == 0)
                        {
                            if(!theAttrName.equals("ClassName")) break; // error?
                            return new StrIntPair(theAttrValue,theClassIndex);
                        }
                    }
                }
            }
            return new StrIntPair("",-1);
        }

        public String GetSBClassName()
        {
            Class cls = getClass(); 
            String myName = cls.getName();
            myName = myName.replaceAll(".*\\$","");
            String sbName = myName.replaceAll(".*\\.","");
            return sbName;
        }

        IntIntPair GetIntegerValue(MappingNode inNode, int inStartIndex,String inKeyname)
        {
            StrIntPair theStrIntPair = GetStringValue(inNode,inStartIndex,inKeyname,false);
            if(theStrIntPair.mInt != -1)
            {
                Integer theVal = Integer.valueOf(theStrIntPair.mStr);
                return new IntIntPair(theVal,theStrIntPair.mInt);
            }
            return new IntIntPair(-1,-1);

        }

        StrIntPair GetStringValue(MappingNode inNode, int inStartIndex,String inKeyname,Boolean inRestoreSpecialValues)
        {
            ScalarNode theKeyNode;
            ScalarNode theValueNode;
            Node theCurrentNode;
            String theKey;
            String theValue;

            List<NodeTuple> theValueClassList = inNode.getValue();
            int theNodeIndex;
            for(theNodeIndex = inStartIndex; theNodeIndex < theValueClassList.size(); theNodeIndex++)
            {
                theKeyNode =  (ScalarNode)theValueClassList.get(theNodeIndex).getKeyNode();
                theKey = theKeyNode.getValue();
                theValueNode =  (ScalarNode)theValueClassList.get(theNodeIndex).getValueNode();
                theValue = theValueNode.getValue();
                if(theKey.equals(inKeyname))
                {
                    if(inRestoreSpecialValues) theValue = RestoreSpecialCharacters(theValue);
                    return new StrIntPair(theValue,theNodeIndex+1);
                }
            }
            return new StrIntPair("",-1);
        }


        protected void DecodeUnknownString(String inUnknownString, Node inAttrValueNode)
        {

        }

        String RestoreSpecialCharacters(String inString)
        {
            String ouString = inString;
            ouString = ouString.replace("_#9;","\t");
            ouString = ouString.replace("_#10;","\n");
            ouString = ouString.replace("_#13;","\r");
            ouString = ouString.replace("_#34;","\"");
            ouString = ouString.replace("_#58;",":");
            ouString = ouString.replace("_#92;","\\");
            ouString = ouString.replace("_#91;","[");
            ouString = ouString.replace("_#93;","]");
            ouString = ouString.replace("_#124;","|");
            ouString = ouString.replace("_#60;","<");
            ouString = ouString.replace("_#62;",">");
            ouString = ouString.replace("_#32;"," ");
            ouString = ouString.replace("__empty","");
            return ouString;
        }

        Integer [] GetIntegerArray(Node inNode,String inLogName, Boolean inFirstIsSize)
        {
            Integer [] theIntegerArray = null;
            String [] theStringArray = null;
            theStringArray = GetStringArray(inNode,inLogName,inFirstIsSize,false);
            if(theStringArray.length <= 0) return theIntegerArray;
            theIntegerArray = new Integer[theStringArray.length];
            for(int theI=0; theI < theStringArray.length; theI++)
            {
                theIntegerArray[theI] = Integer.valueOf(theStringArray[theI]);
            }
            return theIntegerArray;

        }

        Long [] GetLongArray(Node inNode,String inLogName, Boolean inFirstIsSize)
        {
            Long [] theLongArray = null;
            String [] theStringArray = null;
            theStringArray = GetStringArray(inNode,inLogName,inFirstIsSize,false);
            if(theStringArray.length <= 0) return theLongArray;
            theLongArray = new Long[theStringArray.length];
            for(int theI=0; theI < theStringArray.length; theI++)
            {
                theLongArray[theI] = Long.valueOf(theStringArray[theI]);
            }
            return theLongArray;

        }


        Float [] GetFloatArray(Node inNode,String inLogName, Boolean inFirstIsSize)
        {
            Float [] theFloatArray = null;
            String [] theStringArray = null;
            theStringArray = GetStringArray(inNode,inLogName,inFirstIsSize,false);
            if(theStringArray.length <= 0) return theFloatArray;
            theFloatArray = new Float[theStringArray.length];
            for(int theI=0; theI < theStringArray.length; theI++)
            {
                theFloatArray[theI] = Float.valueOf(theStringArray[theI]);
            }
            return theFloatArray;
        }

        Double [] GetDoubleArray(Node inNode,String inLogName, Boolean inFirstIsSize)
        {
            Double [] theDoubleArray = null;
            String [] theStringArray = null;
            theStringArray = GetStringArray(inNode,inLogName,inFirstIsSize,false);
            if(theStringArray.length <= 0) return theDoubleArray;
            theDoubleArray = new Double[theStringArray.length];
            for(int theI=0; theI < theStringArray.length; theI++)
            {
                theDoubleArray[theI] = Double.valueOf(theStringArray[theI]);
            }
            return theDoubleArray;
        }


        String [] GetStringArray(Node inNode,String inLogName, Boolean inFirstIsSize,Boolean inRestoreSpecialValues)
        {
            String [] theArray = null;
            if(! (inNode instanceof SequenceNode)) return theArray;//something wrong
            SequenceNode theAttrValueSequenceNode = (SequenceNode)inNode;
            List <Node> theScalarNodeList = theAttrValueSequenceNode.getValue();
            int theListSize = theScalarNodeList.size();
            if(theListSize < 1) return theArray; //something wrong
            Integer theOff = 0;
            if(inFirstIsSize) theOff = 1;
            theArray = new String[theListSize-theOff];
            for(int theListNode=theOff; theListNode < theListSize; theListNode++)
            { 
                ScalarNode theAttrValueScalarNode = (ScalarNode)theScalarNodeList.get(theListNode);
                String theAttrValue = theAttrValueScalarNode.getValue();
                if(theListNode == 0 && inFirstIsSize)
                {
                    if(Integer.valueOf(theAttrValue) != theListSize)
                    {
                        SlideBook7Reader.LOGGER.info("GetStringArray: List Size mismatch");
                    }
                    continue;
                }
                if(inRestoreSpecialValues) theAttrValue = RestoreSpecialCharacters(theAttrValue);
                theArray[theListNode-theOff] = theAttrValue;
            }
            return theArray;
        }


    }

    class CSBFile70 {
        public static final String kSlideSuffix = ".sldy";
        public static final String kRootDirSuffix = ".dir";
        public static final String kImageDirSuffix = ".imgdir";
        public static final String kBinaryFileSuffix = ".npy";
        public static final String kImageRecordFilename = "ImageRecord.yaml";
        public static final String kChannelRecordFilename = "ChannelRecord.yaml";
        public static final String kAnnotationRecordFilename = "AnnotationRecord.yaml";
        public static final String kMaskRecordFilename = "MaskRecord.yaml";
        public static final String kAuxDataFilename = "AuxData.yaml";
        public static final String kElapsedTimesFilename = "ElapsedTimes.yaml";
        public static final String kSAPositionDataFilename = "SAPositionData.yaml";
        public static final String kStagePositionDataFilename = "StagePositionData.yaml";
        public static final int kNumDigitsInTimepoint = 7;

        public String mSlidePath;

        public CSBFile70(String inSlidePath)
        {
            mSlidePath = inSlidePath;
        }


        public String GetSlideRootDirectory()
        {
            String theRootDirectory = mSlidePath.replaceAll(kSlideSuffix +"$",kRootDirSuffix);
            return theRootDirectory;
        }

        public String [] GetListOfImageGroupTitles() 
        {
            String theRootDirectory = GetSlideRootDirectory();
            File[] theDirectories = new File(theRootDirectory).listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    if(!file.isDirectory()) return false;
                    String theDir = file.getAbsolutePath();
                    if(!theDir.endsWith(kImageDirSuffix)) return false;
                    // check if directroy is empty - no ImageRecord.yaml file or binary files
                    File theImgRecFile = new File(theDir + File.separator + kImageRecordFilename);
                    if(!theImgRecFile.exists()) return false;
                    File [] theFiles = new File(theDir).listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File file) {
                            String thePath = file.getAbsolutePath();
                            if(thePath.endsWith(kBinaryFileSuffix)) return true;
                            else return false;
                        }
                    });
                    if(theFiles.length == 0) return false;
                    return true;
                }
            });
            String []theTitles = new String[theDirectories.length];
            for(int theDir=0;theDir<theDirectories.length;theDir++)
            {
                String thePath = theDirectories[theDir].getAbsolutePath();
                //thePath = thePath.replaceAll(".*"+File.separator,"");
                thePath = thePath.replaceAll("\\\\","/");
                thePath = thePath.replaceAll(".*/","");
                thePath = thePath.replaceAll("\\"+kImageDirSuffix,"");

                theTitles[theDir] = thePath;
            }
            return theTitles;
        }

        public String GetImageGroupDirectory(String inTitle)
        {
            if(inTitle == null) return null;

            String theRootDirectory = GetSlideRootDirectory();
            String theImageGroupDirectory = theRootDirectory + File.separator + inTitle + kImageDirSuffix + File.separator;
            return theImageGroupDirectory;
        }

        public String GetImageDataFile(String inTitle, Integer inChannel, Integer inTimepoint )
        {
            if(inTitle == null) return null;
            String theImageGroupDirectory =  GetImageGroupDirectory(inTitle);
            String thePath = String.format("%s%s%s_Ch%1d_TP%07d%s",theImageGroupDirectory,File.separator,"ImageData",inChannel,inTimepoint,kBinaryFileSuffix);
            return thePath;
        }

        public String GetMaskDataFile(String inTitle, Integer inTimepoint )
        {
            if(inTitle == null) return null;
            String theImageGroupDirectory =  GetImageGroupDirectory(inTitle);
            String thePath = String.format("%s%s%s_TP%07d%s",theImageGroupDirectory,File.separator,"MaskData",inTimepoint,kBinaryFileSuffix);
            return thePath;
        }

        public String GetHistogramDataFile(String inTitle, Integer inChannel, Integer inTimepoint)
        {
            String theImageGroupDirectory =  GetImageGroupDirectory(inTitle);
            String thePath;
            if(inTimepoint >= 0)
                thePath = String.format("%s%s%s_Ch%1d_TP%07d%s",theImageGroupDirectory,File.separator,"HistogramData",inChannel,inTimepoint,kBinaryFileSuffix);
            else // summary
                thePath = String.format("%s%s%s_Ch%1d%s",theImageGroupDirectory,File.separator,"HistogramSummary",inChannel,kBinaryFileSuffix);

            return thePath;
        }
        
        public Integer
        GetChannelIndexOfPath(String inPath)
        {
            int thePos = inPath.lastIndexOf("_Ch");
            if(thePos == -1) return -1;
            String theDigit = inPath.substring(thePos+3,1);
            Integer theChannel = Integer.valueOf(theDigit);
            return theChannel;
        }

        public Integer
        GetTimepointOfPath(String inPath)
        {
            int thePos = inPath.lastIndexOf("_TP");
            if(thePos == -1) return -1;
            String theDigit = inPath.substring(thePos+3,kNumDigitsInTimepoint);
            Integer theTimepoint = Integer.valueOf(theDigit);
            return theTimepoint;
        }



        public String [] GetListOfImageDataFiles(String inTitle)
        {
            return getListOfNpyDataFiles(inTitle,"ImageData");
        }

        public String [] GetListOfMaskDataFiles(String inTitle)
        {
            return getListOfNpyDataFiles(inTitle,"MaskData");

        }

        public String [] GetListOfHistogramDataFiles(String inTitle)
        {
            return getListOfNpyDataFiles(inTitle,"HistogramData");
        }


        public String [] GetListOfHistogramSummaryFiles(String inTitle)
        {
            return getListOfNpyDataFiles(inTitle,"HistogramSummary");
        }

        protected String [] getListOfNpyDataFiles(String inTitle,final String inStartWith)
        {
            String theImageGroupDirectory =  GetImageGroupDirectory(inTitle);

            System.out.println("getListOfNpyDataFiles: theImageGroupDirectory " + theImageGroupDirectory);

            File [] theFiles = new File(theImageGroupDirectory).listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    String thePath = file.getAbsolutePath();
                    if(!thePath.endsWith(kBinaryFileSuffix)) return false;
                    String theName = file.getName();
                    if(!theName.startsWith(inStartWith)) return false;
                    return true;
                }
            });

            String []theFilePaths = new String[theFiles.length];
            System.out.println("getListOfNpyDataFiles: theFiles.length " + theFiles.length);
            for(int theFil=0;theFil<theFiles.length;theFil++)
            {
                theFilePaths[theFil] = theFiles[theFil].getAbsolutePath();
                System.out.println("getListOfNpyDataFiles: theFile " + theFil + theFilePaths[theFil]);
            }

            return theFilePaths;
        }
        
    }

    class CNpyHeader {
        Boolean mLittleEndian;
        Boolean mFortranOrder;
        Integer [] mShape;
        Integer mHeaderSize;
        String mDataType ;
        Integer mBytesPerPixel;
        public Boolean ParseNpyHeader(RandomAccessInputStream inStream)
        {
            try {
                byte[] theBuffer = new byte[1025]; // header buffer
                theBuffer[0] = theBuffer[1024] = '\0';
                boolean theEOL = false;
                for(mHeaderSize =0;mHeaderSize <1024;mHeaderSize ++)
                {
                    theBuffer[mHeaderSize] = inStream.readByte();
                    if((char)theBuffer[mHeaderSize] == '\n')
                    {
                        theEOL = true;
                        break;
                    }
                }
                mHeaderSize++;

                if(!theEOL)
                {
                    System.out.println("No carriage return");
                    return false;
                }
                short theHeaderLen;
                theHeaderLen = ByteArrayToShort(theBuffer,8);
                System.out.println("Header length: " + theHeaderLen);
                String theHeader = new String(theBuffer,10,theHeaderLen);
                System.out.println("Header : " + theHeader);
                int loc1,loc2;
                loc1 = theHeader.indexOf("descr") + 9;
                String theEndianess = theHeader.substring(loc1,loc1+1);
                mLittleEndian = false;
                if(theEndianess.equals("<")) mLittleEndian = true;
                mDataType = theHeader.substring(loc1+1,loc1+3);
                mBytesPerPixel = 2;
                if(mDataType.equals("u2") || mDataType.equals("i2")) mBytesPerPixel = 2;
                else if(mDataType.equals("u4") || mDataType.equals("i4")) mBytesPerPixel = 4;


                loc1 = theHeader.indexOf("fortran_order") + 16;
                String theFortranOrder = theHeader.substring(loc1,loc1+4);
                mFortranOrder = false;
                if(theFortranOrder.equals("True")) mFortranOrder = true;
                loc1 = theHeader.indexOf("(");
                loc2 = theHeader.indexOf(")");
                String theShape = theHeader.substring(loc1+1,loc2);
                String [] theDims = theShape.split(",",10);
                mShape = new Integer[theDims.length];
                for(int theI=0;theI<theDims.length;theI++)
                {
                    String theTrim = theDims[theI].trim();
                    mShape[theI] = Integer.valueOf(theTrim);
                }
            } catch (IOException ex) {
                //Logger.getLogger(CNpyHeader.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            return true;

        }

        short ByteArrayToShort(byte[] bytes,int offset) {
             int theVal = ((bytes[offset+1] & 0xFF) << 8) | 
                    ((bytes[offset+0] & 0xFF) << 0 );
            return (short)theVal;
        }
        int ByteArrayToInt(byte[] bytes,int offset) {
             return ((bytes[offset+3] & 0xFF) << 24) | 
                    ((bytes[offset+2] & 0xFF) << 16) | 
                    ((bytes[offset+1] & 0xFF) << 8 ) | 
                    ((bytes[offset+0] & 0xFF) << 0 );
        }

    }

    class CSBPoint <T> {
       public T mX; 
       public T mY; 
       public T mZ; 
    }

    class CImageData {
        
    }


    class CImageGroup extends ClassDecoder {

        class CMaskPositions {
            Long [] mCompressedSizes;
            Long [] mFileOffsets;
        }

        class CAnnotations {
            ArrayList <CCubeAnnotation70> mCubeAnnotationList;
            ArrayList <CAnnotation70> mBaseAnnotationList;
            ArrayList <CFRAPRegionAnnotation70> mFRAPRegionAnnotationList;
            ArrayList <CUnknownAnnotation70> mUnknownAnnotationList;
            public CAnnotations() {
                mCubeAnnotationList = new ArrayList <CCubeAnnotation70>();
                mBaseAnnotationList = new ArrayList <CAnnotation70>();
                mFRAPRegionAnnotationList = new ArrayList <CFRAPRegionAnnotation70>();
                mUnknownAnnotationList = new ArrayList <CUnknownAnnotation70>();
            }
        }

        class CAuxFloatData {
            String mXmlDescriptor;
            Float [] mFloatData;
        }

        class CAuxDoubleData {
            String mXmlDescriptor;
            Double [] mDoubleData;
        }

        class CAuxSInt32Data {
            String mXmlDescriptor;
            Integer [] mIntegerData;
        }

        class CAuxSInt64Data {
            String mXmlDescriptor;
            Long [] mLongData;
        }

        class CAuxXmlData {
            String mXmlData;
        }



        ArrayList <CImageData>  mImageDataList; // one per time point
        ArrayList <CMaskPositions>  mMaskPosList; // one per time point
        ArrayList <CAnnotations>  mAnnotationList; // one per time point
        CImageRecord70 mImageRecord;
        ArrayList <CChannelRecord70> mChannelRecordList; // one per channel
        ArrayList <CRemapChannelLUT70> mRemapChannelLUTList; // one per channe;
        ArrayList <CAlignManipRecord70> mAlignManipRecordList; // one per channe;
        ArrayList <CRatioManipRecord70> mRatioManipRecordList; // one per channe;
        ArrayList <CFRETManipRecord70> mFRETManipRecList; // one per channe;
        ArrayList <CRemapManipRecord70> mRemapManipRecList; // one per channe;
        ArrayList <CHistogramRecord70> mHistogramRecordList; // one per channe;
        ArrayList <CMaskRecord70> mMaskRecordList; // mNumMasks
        Integer [] mElapsedTimes;
        ArrayList <Integer []> mSAPositionList; // one value per time point
        ArrayList <CSBPoint <Float>> mStagePositions; // one value per time point
        ArrayList <CAuxFloatData> mAuxFloatDataList;
        ArrayList <CAuxDoubleData> mAuxDoubleDataList;
        ArrayList <CAuxSInt32Data> mAuxSInt32DataList;
        ArrayList <CAuxSInt64Data> mAuxSInt64DataList;
        ArrayList <CAuxXmlData> mAuxXmlDataList;

        CSBFile70 mFile;
        String mImageTitle;
        CNpyHeader mNpyHeader;

        public CImageGroup(CSBFile70 inFile, String inImageTitle)
        {
            mFile = inFile;
            mImageTitle = inImageTitle;
        }

        public Boolean Load()
        {
            Boolean theResult = false;

            // load ImageRecord
            SlideBook7Reader.LOGGER.info("CImageGroup: Load");
            theResult = LoadImageRecord();
            SlideBook7Reader.LOGGER.info("LoadImageRecord: result " + theResult);
            if(!theResult) return false;
            
            theResult = LoadChannelRecord();
            SlideBook7Reader.LOGGER.info("LoadChannelRecord: result " + theResult);
            if(!theResult) return false;
            theResult = LoadMaks();
            SlideBook7Reader.LOGGER.info("LoadMaks: result " + theResult);
            if(!theResult) return false;
            theResult = LoadAnnotations();
            SlideBook7Reader.LOGGER.info("LoadAnnotations: result " + theResult);
            if(!theResult) return false;
            theResult = LoadElapsedTimes();
            SlideBook7Reader.LOGGER.info("LoadElapsedTimes: result " + theResult);
            if(!theResult) return false;
            theResult = LoadSAPositions();
            SlideBook7Reader.LOGGER.info("LoadSAPositions: result " + theResult);
            if(!theResult) return false;
            theResult = LoadStagePosition();
            SlideBook7Reader.LOGGER.info("LoadStagePosition: result " + theResult);
            if(!theResult) return false;
            theResult = LoadAuxData();
            SlideBook7Reader.LOGGER.info("LoadAuxData: result " + theResult);
            if(!theResult) return false;

            return true;
        }

        public Boolean LoadImageRecord()
        {
            try {
                InputStream inputStream = new FileInputStream(mFile.GetImageGroupDirectory(mImageTitle) + CSBFile70.kImageRecordFilename);
                Reader inputStreamReader = new InputStreamReader(inputStream);
                Yaml yaml = new Yaml();
                MappingNode theNode = (MappingNode)yaml.compose(inputStreamReader);

                mImageRecord = new CImageRecord70();
                int theLastIndex = mImageRecord.Decode(theNode);
                System.out.println("LoadImageRecord: theLastIndex " + theLastIndex);

                // verify correct number of timepoints/channels in image record
                if(!CountImageDataFiles())
                {
                System.out.println("CountImageDataFiles: Did not succeed " );
                    return false;
                }
                System.out.println("CountImageDataFiles: OK " );

            } catch (FileNotFoundException e) {

                e.printStackTrace();
            }

            return true;

        }

        public Boolean CountImageDataFiles()
        {
            String [] theImageFileNames = mFile.GetListOfImageDataFiles(mImageTitle);
            System.out.println("CountImageDataFiles: theImageFileNames.length " + theImageFileNames.length);
            System.out.println("CountImageDataFiles: mImageRecord.mNumChannels " + mImageRecord.mNumChannels);
            System.out.println("CountImageDataFiles: mImageRecord.mNumTimepoints " + mImageRecord.mNumTimepoints);
            if(theImageFileNames.length == mImageRecord.mNumChannels*mImageRecord.mNumTimepoints) return true; // all in order
            Integer theMaxChannel = 0;
            Integer theMaxTimepoint = 0;
            for(int theFileIndex = 0;  theFileIndex < theImageFileNames.length; theFileIndex++)
            {
                Integer theChannel = mFile.GetChannelIndexOfPath(theImageFileNames[theFileIndex]);
                Integer theTimepoint = mFile.GetTimepointOfPath(theImageFileNames[theFileIndex]);
                theMaxChannel = max(theMaxChannel,theChannel+1);
                theMaxTimepoint = max(theMaxTimepoint,theTimepoint+1);
            }
            System.out.println("CountImageDataFiles: theMaxChannel + theMaxTimepoint " + theMaxChannel + " " + theMaxTimepoint);
            if(theMaxChannel == 0 || theMaxTimepoint ==0)
            {
                System.out.println("CountImageDataFiles: theMaxChannel + theMaxTimepoint " + theMaxChannel + theMaxTimepoint);
                //error
                return false;
            }
            mImageRecord.mNumTimepoints = theMaxTimepoint;
            mImageRecord.mNumChannels = theMaxChannel ;
            // create the ImageData classes for each timepoint
            mImageDataList = new ArrayList <CImageData>();
            for(int theTimepoint = 0; theTimepoint < mImageRecord.mNumTimepoints; theTimepoint++)
            {
                mImageDataList.add(new CImageData());
            }
            System.out.println("CountImageDataFiles: mImageDataList.length " + mImageDataList.size());
            return true;

        }

        public Boolean LoadChannelRecord() 
        {
            try {
                InputStream inputStream = new FileInputStream(mFile.GetImageGroupDirectory(mImageTitle) + CSBFile70.kChannelRecordFilename);
                Reader inputStreamReader = new InputStreamReader(inputStream);
                Yaml yaml = new Yaml();
                MappingNode theNode = (MappingNode)yaml.compose(inputStreamReader);
                int theLastIndex = 0;
                StrIntPair thePair;

                mChannelRecordList = new ArrayList<CChannelRecord70>();
                mRemapChannelLUTList = new ArrayList<CRemapChannelLUT70>();
                mAlignManipRecordList = new ArrayList<CAlignManipRecord70>();
                mRatioManipRecordList = new ArrayList<CRatioManipRecord70>();
                mFRETManipRecList = new ArrayList<CFRETManipRecord70>();
                mRemapManipRecList = new ArrayList<CRemapManipRecord70>();
                mHistogramRecordList = new ArrayList<CHistogramRecord70>();

                for(int theChannel = 0; theChannel < mImageRecord.mNumChannels; theChannel++)
                {

                    CChannelRecord70 theChannelRecord = new CChannelRecord70();
                    mChannelRecordList.add(theChannelRecord);

                    CRemapChannelLUT70 theRemapChannelLUT = new CRemapChannelLUT70();
                    CAlignManipRecord70 theAlignManipRecord70 =  new CAlignManipRecord70();  
                    CRatioManipRecord70 theRatioManipRecord70 =  new CRatioManipRecord70();
                    CFRETManipRecord70 theFRETManipRec70 =  new CFRETManipRecord70();
                    CRemapManipRecord70 theRemapManipRec70 =  new CRemapManipRecord70();
                    CHistogramRecord70 theHistogramRecord70 =  new CHistogramRecord70();


                    theLastIndex = theChannelRecord.Decode(theNode,theLastIndex);
                    System.out.println("theLastIndex: " + theLastIndex);
                    for(;;)
                    {
                        thePair = FindNextClass(theNode,theLastIndex);
                        if(thePair.mInt >= 0)
                        {
                            if(thePair.mStr.equals(theChannelRecord.GetSBClassName()))
                            {
                                theLastIndex = thePair.mInt;
                                break;
                            }
                            else if (thePair.mStr.equals(theRemapChannelLUT.GetSBClassName()))
                            {
                                theLastIndex = theRemapChannelLUT.Decode(theNode,thePair.mInt);
                                mRemapChannelLUTList.add(theRemapChannelLUT);
                            }
                            else if (thePair.mStr.equals(theAlignManipRecord70.GetSBClassName()))
                            {
                                theLastIndex = theAlignManipRecord70.Decode(theNode,thePair.mInt);
                                mAlignManipRecordList.add(theAlignManipRecord70);
                            }
                            else if (thePair.mStr.equals(theRatioManipRecord70.GetSBClassName()))
                            {
                                theLastIndex = theRatioManipRecord70.Decode(theNode,thePair.mInt);
                                mRatioManipRecordList.add(theRatioManipRecord70);
                            }
                            else if (thePair.mStr.equals(theFRETManipRec70.GetSBClassName()))
                            {
                                theLastIndex = theFRETManipRec70.Decode(theNode,thePair.mInt);
                                mFRETManipRecList.add(theFRETManipRec70);
                            }
                            else if (thePair.mStr.equals(theRemapManipRec70.GetSBClassName()))
                            {
                                theLastIndex = theRemapManipRec70.Decode(theNode,thePair.mInt);
                                mRemapManipRecList.add(theRemapManipRec70);
                            }
                            else if (thePair.mStr.equals(theHistogramRecord70.GetSBClassName()))
                            {
                                theLastIndex = theHistogramRecord70.Decode(theNode,thePair.mInt);
                                mHistogramRecordList.add(theHistogramRecord70);
                                // Read the Histograms here
                            }

                        }
                        else break;

                    }
                }

            } catch (FileNotFoundException e) {

                e.printStackTrace();
            }
            return true;
        }

        public Boolean LoadMaks()
        {
            try {
                InputStream inputStream = new FileInputStream(mFile.GetImageGroupDirectory(mImageTitle) + CSBFile70.kMaskRecordFilename);
                Reader inputStreamReader = new InputStreamReader(inputStream);
                Yaml yaml = new Yaml();
                MappingNode theNode = (MappingNode)yaml.compose(inputStreamReader);
                int theLastIndex = 0;
                StrIntPair thePair;
                List<NodeTuple> theValueClassList = theNode.getValue();
                ScalarNode theKeyNode;
                ScalarNode theValueNode;
                Node theCurrentNode;
                String theKey;
                String theValue;
                theKeyNode = (ScalarNode)theValueClassList.get(theLastIndex).getKeyNode();

                theKey = theKeyNode.getValue();
                if(theKey.equals("theNumMasks"))
                {
                    theValueNode =  (ScalarNode)theValueClassList.get(theLastIndex).getValueNode();
                    theValue = theValueNode.getValue();
                    Integer theNumMasks = Integer.valueOf(theValue);
                    mMaskRecordList = new ArrayList<CMaskRecord70>();
                    if(theNumMasks > 0)
                    {
                        theLastIndex = 1;
                        for(int theMask=0; theMask < theNumMasks; theMask++)
                        {
                            CMaskRecord70 theMaskRecord = new CMaskRecord70();
                            theLastIndex = theMaskRecord.Decode(theNode,theLastIndex);
                            mMaskRecordList.add(theMaskRecord);
                        }

                    }
                    else return true;
                    mMaskPosList = new ArrayList<CMaskPositions>();
                    // now loop over  the timepoints
                    for(;theLastIndex< theValueClassList.size();)
                    {
                        IntIntPair theIIPair = GetIntegerValue(theNode,theLastIndex,"theTimepointIndex");
                        Integer theTimePointIndex = theIIPair.mInt1;
                        theLastIndex = theIIPair.mInt2;
                        if(theLastIndex < 0) break;

                        CMaskPositions thePos = new CMaskPositions();

                        theKeyNode =  (ScalarNode)theValueClassList.get(theLastIndex).getKeyNode();
                        theKey = theKeyNode.getValue();
                        if(!theKey.equals("theMaskCompressedSizes")) break ;//something wrong
                        theCurrentNode =  theValueClassList.get(theLastIndex).getValueNode();
                        thePos.mCompressedSizes = GetLongArray(theCurrentNode,"theMaskCompressedSizes",true);
                        theLastIndex++;

                        theKeyNode =  (ScalarNode)theValueClassList.get(theLastIndex).getKeyNode();
                        theKey = theKeyNode.getValue();
                        if(!theKey.equals("theMaskFileOffsets")) break ;//something wrong
                        theCurrentNode =  theValueClassList.get(theLastIndex).getValueNode();
                        thePos.mFileOffsets = GetLongArray(theCurrentNode,"theMaskFileOffsets",true);
                        theLastIndex++;

                        mMaskPosList.add(thePos);

                    }
                }
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            }
            return true;
        } 

        public Boolean LoadAnnotations()
        {
            try {
                InputStream inputStream = new FileInputStream(mFile.GetImageGroupDirectory(mImageTitle) + CSBFile70.kAnnotationRecordFilename);
                Reader inputStreamReader = new InputStreamReader(inputStream);
                Yaml yaml = new Yaml();
                MappingNode theNode = (MappingNode)yaml.compose(inputStreamReader);
                int theLastIndex = 0;
                StrIntPair thePair;
                CDataTableHeaderRecord70 theDataTableHeaderRecord70 = new CDataTableHeaderRecord70();
                theLastIndex = theDataTableHeaderRecord70.Decode(theNode);
                mAnnotationList = new ArrayList<CAnnotations>();
                for(;;)
                {
                    IntIntPair theIIPair = GetIntegerValue(theNode,theLastIndex,"theTimepointIndex");
                    Integer theTimePointIndex = theIIPair.mInt1;
                    theLastIndex = theIIPair.mInt2;
                    if(theLastIndex < 0) break;

                    CAnnotations theAnno = new CAnnotations();

                    theIIPair = GetIntegerValue(theNode,theLastIndex,"theCubeAnnotation70ListSize");
                    Integer theCubeAnnotation70ListSize = theIIPair.mInt1;
                    theLastIndex = theIIPair.mInt2;

                    for(int theAnnotationIndex = 0;theAnnotationIndex < theCubeAnnotation70ListSize;theAnnotationIndex++)
                    {
                        CCubeAnnotation70 theCubeAnnotation70 = new CCubeAnnotation70();
                        theLastIndex = theCubeAnnotation70.Decode(theNode,theLastIndex);
                        theAnno.mCubeAnnotationList.add(theCubeAnnotation70);
                    }

                    theIIPair = GetIntegerValue(theNode,theLastIndex,"theAnnotation70ListSize");
                    Integer theAnnotation70ListSize = theIIPair.mInt1;
                    theLastIndex = theIIPair.mInt2;
                    for(int theAnnotationIndex = 0;theAnnotationIndex < theAnnotation70ListSize;theAnnotationIndex++)
                    {
                        CAnnotation70 theAnnotation70 = new CAnnotation70();
                        theLastIndex = theAnnotation70.Decode(theNode,theLastIndex);
                        theAnno.mBaseAnnotationList.add(theAnnotation70);
                    }

                    theIIPair = GetIntegerValue(theNode,theLastIndex,"theFRAPRegionAnnotation70ListSize");
                    Integer theFRAPRegionAnnotation70ListSize = theIIPair.mInt1;
                    theLastIndex = theIIPair.mInt2;
                    for(int theAnnotationIndex = 0;theAnnotationIndex < theFRAPRegionAnnotation70ListSize;theAnnotationIndex++)
                    {
                        CFRAPRegionAnnotation70 theFRAPRegionAnnotation70 = new CFRAPRegionAnnotation70();
                        theLastIndex = theFRAPRegionAnnotation70.Decode(theNode,theLastIndex);
                        theAnno.mFRAPRegionAnnotationList.add(theFRAPRegionAnnotation70);
                    }

                    theIIPair = GetIntegerValue(theNode,theLastIndex,"theUnknownAnnotation70ListSize");
                    Integer theUnknownAnnotation70ListSize = theIIPair.mInt1;
                    theLastIndex = theIIPair.mInt2;
                    for(int theAnnotationIndex = 0;theAnnotationIndex < theUnknownAnnotation70ListSize;theAnnotationIndex++)
                    {
                        CUnknownAnnotation70 theUnknownAnnotation70 = new CUnknownAnnotation70();
                        theLastIndex = theUnknownAnnotation70.Decode(theNode,theLastIndex);
                        theAnno.mUnknownAnnotationList.add(theUnknownAnnotation70);
                    }
                    mAnnotationList.add(theAnno);

                }

            } catch (FileNotFoundException e) {

                SlideBook7Reader.LOGGER.info("LoadAnnotations: FileNotFoundException loading error: " + e.getMessage());
                e.printStackTrace();
                return false;
            } catch (Exception e) {

                SlideBook7Reader.LOGGER.info("LoadAnnotations: Exception loading error: " + e.getMessage());
                e.printStackTrace();
                return false;
            }

            return true;
        }

        public Boolean LoadElapsedTimes()
        {
            try {
                InputStream inputStream = new FileInputStream(mFile.GetImageGroupDirectory(mImageTitle) + CSBFile70.kElapsedTimesFilename);
                Reader inputStreamReader = new InputStreamReader(inputStream);
                Yaml yaml = new Yaml();
                MappingNode theNode = (MappingNode)yaml.compose(inputStreamReader);
                ScalarNode theKeyNode;
                ScalarNode theValueNode;
                Node theCurrentNode;
                String theKey;
                String theValue;
                int theLastIndex = 0;
                List<NodeTuple> theValueClassList = theNode.getValue();

                theKeyNode =  (ScalarNode)theValueClassList.get(theLastIndex).getKeyNode();
                theKey = theKeyNode.getValue();
                if(!theKey.equals("theElapsedTimes")) return false ;//something wrong
                theCurrentNode =  theValueClassList.get(theLastIndex).getValueNode();
                mElapsedTimes = GetIntegerArray(theCurrentNode,"theElapsedTimesVector",true);
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            }
            return true;
        }

        public Boolean LoadSAPositions()
        {
            try {
                InputStream inputStream = new FileInputStream(mFile.GetImageGroupDirectory(mImageTitle) + CSBFile70.kSAPositionDataFilename);
                Reader inputStreamReader = new InputStreamReader(inputStream);
                Yaml yaml = new Yaml();
                MappingNode theNode = (MappingNode)yaml.compose(inputStreamReader);
                ScalarNode theKeyNode;
                ScalarNode theValueNode;
                Node theCurrentNode;
                String theKey;
                String theValue;
                List<NodeTuple> theValueClassList = theNode.getValue();
                int theLastIndex = 0;

                IntIntPair theIIPair = GetIntegerValue(theNode,theLastIndex,"theImageCount");
                Integer theImageCount = theIIPair.mInt1;
                theLastIndex = theIIPair.mInt2;
                if(theLastIndex < 0) return true;
                mSAPositionList = new ArrayList<Integer []>();

                for(int theImageIndex =0; theImageIndex < theImageCount; theImageIndex++)
                {
                    theKeyNode =  (ScalarNode)theValueClassList.get(theLastIndex).getKeyNode();
                    theKey = theKeyNode.getValue();
                    if(!theKey.equals("theSAPositions")) break ;//something wrong
                    theCurrentNode =  theValueClassList.get(theLastIndex).getValueNode();
                    Integer [] theSAPositionsvector = GetIntegerArray(theCurrentNode,"theSAPositions",true);
                    mSAPositionList.add(theSAPositionsvector);
                    theLastIndex++;
                }
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            }
            return true;
        }

        public Boolean LoadStagePosition()
        {

            try {
                InputStream inputStream = new FileInputStream(mFile.GetImageGroupDirectory(mImageTitle) + CSBFile70.kStagePositionDataFilename);
                Reader inputStreamReader = new InputStreamReader(inputStream);
                Yaml yaml = new Yaml();
                MappingNode theNode = (MappingNode)yaml.compose(inputStreamReader);
                ScalarNode theKeyNode;
                ScalarNode theValueNode;
                Node theCurrentNode;
                String theKey;
                String theValue;
                List<NodeTuple> theValueClassList = theNode.getValue();
                int theLastIndex = 0;

                IntIntPair theIIPair = GetIntegerValue(theNode,theLastIndex,"StructArraySize");
                Integer thePositionCount = theIIPair.mInt1;
                theLastIndex = theIIPair.mInt2;
                if(theLastIndex < 0) return true;
                mStagePositions = new ArrayList<CSBPoint <Float>>();

                theKeyNode =  (ScalarNode)theValueClassList.get(theLastIndex).getKeyNode();
                theKey = theKeyNode.getValue();
                if(!theKey.equals("StructArrayValues")) return false ;//something wrong
                theCurrentNode =  theValueClassList.get(theLastIndex).getValueNode();
                Float[] thePoints = GetFloatArray(theCurrentNode,"theStagePositionData",false);
                for(int theP=0; theP < thePoints.length -2; theP += 3)
                {
                    CSBPoint <Float> thePoint = new CSBPoint<Float>();
                    thePoint.mX = thePoints[theP];
                    thePoint.mY = thePoints[theP+1];
                    thePoint.mZ = thePoints[theP+2];
                    mStagePositions.add(thePoint);
                }

                theLastIndex++;
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            }
            return true;
        }
        public Boolean LoadAuxData()
        {
            try {
                InputStream inputStream = new FileInputStream(mFile.GetImageGroupDirectory(mImageTitle) + CSBFile70.kAuxDataFilename);
                Reader inputStreamReader = new InputStreamReader(inputStream);
                Yaml yaml = new Yaml();
                MappingNode theNode = (MappingNode)yaml.compose(inputStreamReader);
                ScalarNode theKeyNode;
                ScalarNode theValueNode;
                Node theCurrentNode;
                String theKey;
                String theValue;
                List<NodeTuple> theValueClassList = theNode.getValue();
                int theLastIndex = 0;

                IntIntPair theIIPair = GetIntegerValue(theNode,theLastIndex,"theAuxFloatDataTablesSize");
                Integer theTableCount = theIIPair.mInt1;
                theLastIndex = theIIPair.mInt2;
                if(theLastIndex < 0) return true;

                //FLOAT
                mAuxFloatDataList = new ArrayList<CAuxFloatData>();

                for(int theTableIndex =0; theTableIndex < theTableCount; theTableIndex++)
                {
                    CAuxFloatData theAux = new CAuxFloatData();
                    CDataTableHeaderRecord70 theAuxDataTable70 = new CDataTableHeaderRecord70();
                    theLastIndex = theAuxDataTable70.Decode(theNode,theLastIndex);
                    StrIntPair theSIPair =  GetStringValue(theNode,theLastIndex,"theXMLDescriptor",true);
                    theAux.mXmlDescriptor = theSIPair.mStr;
                    theLastIndex = theSIPair.mInt;

                    theKeyNode =  (ScalarNode)theValueClassList.get(theLastIndex).getKeyNode();
                    theKey = theKeyNode.getValue();
                    if(!theKey.equals("theAuxData")) return false ;//something wrong

                    theCurrentNode =  theValueClassList.get(theLastIndex).getValueNode();
                    theAux.mFloatData = GetFloatArray(theCurrentNode,"theAuxFloatData",true);
                    theLastIndex++;
                    mAuxFloatDataList.add(theAux);
                }

                // DOUBLE

                theIIPair = GetIntegerValue(theNode,theLastIndex,"theAuxDoubleDataTablesSize");
                theTableCount = theIIPair.mInt1;
                theLastIndex = theIIPair.mInt2;
                if(theLastIndex < 0) return true;
                mAuxDoubleDataList = new ArrayList<CAuxDoubleData>();

                for(int theTableIndex =0; theTableIndex < theTableCount; theTableIndex++)
                {
                    CAuxDoubleData theAux = new CAuxDoubleData();
                    CDataTableHeaderRecord70 theAuxDataTable70 = new CDataTableHeaderRecord70();
                    theLastIndex = theAuxDataTable70.Decode(theNode,theLastIndex);
                    StrIntPair theSIPair =  GetStringValue(theNode,theLastIndex,"theXMLDescriptor",true);
                    theAux.mXmlDescriptor = theSIPair.mStr;
                    theLastIndex = theSIPair.mInt;

                    theKeyNode =  (ScalarNode)theValueClassList.get(theLastIndex).getKeyNode();
                    theKey = theKeyNode.getValue();
                    if(!theKey.equals("theAuxData")) return false ;//something wrong

                    theCurrentNode =  theValueClassList.get(theLastIndex).getValueNode();
                    theAux.mDoubleData = GetDoubleArray(theCurrentNode,"theAuxDoubleData",true);
                    theLastIndex++;
                    mAuxDoubleDataList.add(theAux);
                }

                // SINT32

                theIIPair = GetIntegerValue(theNode,theLastIndex,"theAuxSInt32DataTablesSize");
                theTableCount = theIIPair.mInt1;
                theLastIndex = theIIPair.mInt2;
                if(theLastIndex < 0) return true;
                mAuxSInt32DataList = new ArrayList<CAuxSInt32Data>();

                for(int theTableIndex =0; theTableIndex < theTableCount; theTableIndex++)
                {
                    CAuxSInt32Data theAux = new CAuxSInt32Data();
                    CDataTableHeaderRecord70 theAuxDataTable70 = new CDataTableHeaderRecord70();
                    theLastIndex = theAuxDataTable70.Decode(theNode,theLastIndex);
                    StrIntPair theSIPair =  GetStringValue(theNode,theLastIndex,"theXMLDescriptor",true);
                    theAux.mXmlDescriptor = theSIPair.mStr;
                    theLastIndex = theSIPair.mInt;

                    theKeyNode =  (ScalarNode)theValueClassList.get(theLastIndex).getKeyNode();
                    theKey = theKeyNode.getValue();
                    if(!theKey.equals("theAuxData")) return false ;//something wrong

                    theCurrentNode =  theValueClassList.get(theLastIndex).getValueNode();
                    theAux.mIntegerData = GetIntegerArray(theCurrentNode,"theAuxSInt32Data",true);
                    theLastIndex++;
                    mAuxSInt32DataList.add(theAux);
                }

                
                // SINT64
                theIIPair = GetIntegerValue(theNode,theLastIndex,"theAuxSInt64DataTablesSize");
                theTableCount = theIIPair.mInt1;
                theLastIndex = theIIPair.mInt2;
                if(theLastIndex < 0) return true;
                mAuxSInt64DataList = new ArrayList<CAuxSInt64Data>();

                for(int theTableIndex =0; theTableIndex < theTableCount; theTableIndex++)
                {
                    CAuxSInt64Data theAux = new CAuxSInt64Data();
                    CDataTableHeaderRecord70 theAuxDataTable70 = new CDataTableHeaderRecord70();
                    theLastIndex = theAuxDataTable70.Decode(theNode,theLastIndex);
                    StrIntPair theSIPair =  GetStringValue(theNode,theLastIndex,"theXMLDescriptor",true);
                    theAux.mXmlDescriptor = theSIPair.mStr;
                    theLastIndex = theSIPair.mInt;

                    theKeyNode =  (ScalarNode)theValueClassList.get(theLastIndex).getKeyNode();
                    theKey = theKeyNode.getValue();
                    if(!theKey.equals("theAuxData")) return false ;//something wrong

                    theCurrentNode =  theValueClassList.get(theLastIndex).getValueNode();
                    theAux.mLongData = GetLongArray(theCurrentNode,"theAuxSInt64Data",true);
                    theLastIndex++;
                    mAuxSInt64DataList.add(theAux);
                }

                // XML
                theIIPair = GetIntegerValue(theNode,theLastIndex,"theAuxSerializedDataTablesSize");
                theTableCount = theIIPair.mInt1;
                theLastIndex = theIIPair.mInt2;
                if(theLastIndex < 0) return true;
                mAuxXmlDataList = new ArrayList<CAuxXmlData>();

                for(int theTableIndex =0; theTableIndex < theTableCount; theTableIndex++)
                {
                    CAuxXmlData theAux = new CAuxXmlData();
                    CDataTableHeaderRecord70 theAuxDataTable70 = new CDataTableHeaderRecord70();
                    theLastIndex = theAuxDataTable70.Decode(theNode,theLastIndex);
                    StrIntPair theSIPair =  GetStringValue(theNode,theLastIndex,"theXMLDescriptor",true);
                    String theXMLDescriptor = theSIPair.mStr;
                    theLastIndex = theSIPair.mInt;

                    theIIPair = GetIntegerValue(theNode,theLastIndex,"theXmlAuxDataSize"); // always 1
                    theLastIndex = theIIPair.mInt2;

                    theSIPair = GetStringValue(theNode,theLastIndex,"theXmlAuxData",true);
                    theAux.mXmlData = theSIPair.mStr;
                    theLastIndex = theSIPair.mInt;
                    mAuxXmlDataList.add(theAux);

                }


            } catch (FileNotFoundException e) {

                e.printStackTrace();
            }
        return true;
        }
        
        public int GetNumChannels()
        {
            return mImageRecord.mNumChannels;
        }

        public int GetNumColumns()
        {
            return mImageRecord.mWidth;
        }

        public int GetNumRows()
        {
            return mImageRecord.mHeight;
        }

        public int GetNumPlanes()
        {
            return mImageRecord.mNumPlanes;
        }

        public int GetNumPositions()
        {
            int theNumStagePositions = mStagePositions.size();
            if(theNumStagePositions <= 1) return 1;
            CSBPoint <Float> thePoint0 = mStagePositions.get(0);
            SlideBook7Reader.LOGGER.info("GetNumPositions: thePoint0 mX=" + thePoint0.mX + ", mY=" + thePoint0.mY);
            int theNumUniquePositions = 1;
            for(int thePosition = 1; thePosition < theNumStagePositions; thePosition++)
            {
                CSBPoint <Float> thePoint1 = mStagePositions.get(thePosition);
                SlideBook7Reader.LOGGER.info("GetNumPositions: thePoint1 mX=" + thePoint1.mX + ", mY=" + thePoint1.mY);
                if(thePoint0.mX.equals(thePoint1.mX) &&
                   thePoint0.mY.equals(thePoint1.mY)) break;

                theNumUniquePositions++;
            }
            SlideBook7Reader.LOGGER.info("GetNumPositions: theNumUniquePositions=" + theNumUniquePositions);

            return theNumUniquePositions;
        }

        public int GetNumTimepoints()
        {
            return mImageRecord.mNumTimepoints;
        }

        public int GetElapsedTime(int inTimepoint)
        {
            return  mElapsedTimes[inTimepoint];
        }

        public int GetBytesPerPixel()
        {
            return 2;
        }

        public String GetName()
        {
            return mImageRecord.mName;
        }

        public String GetInfo()
        {
            return mImageRecord.mInfo;
        }

        public String GetChannelName(int inChannel)
        {
            return mChannelRecordList.get(inChannel).mChannelDef.mName;
        }

        public String GetLensName()
        {
            return mImageRecord.mLensDef.mName;
        }

        public double GetMagnification()
        {
            return mImageRecord.mLensDef.mActualMagnification * mImageRecord.mOptovarDef.mMagnification ;
        }


        public float GetVoxelSize()
        {
            float theSize = mImageRecord.mLensDef.mMicronPerPixel;
            if(mImageRecord.mOptovarDef.mMagnification > 0) theSize /=  mImageRecord.mLensDef.mMicronPerPixel;
            float theXFactor = mChannelRecordList.get(0).mExposureRecord.mXFactor;
            if(theXFactor > 0) theSize *= theXFactor;
            return theSize;
        }
        
        public float GetInterplaneSpacing()
        {
            return mChannelRecordList.get(0).mExposureRecord.mInterplaneSpacing;
        }

        public int GetExposureTime(int inChannel)
        {
            return mChannelRecordList.get(inChannel).mExposureRecord.mExposureTime;
        }

        public float GetXPosition(int inPosition)
        {
            CSBPoint <Float> thePoint = mStagePositions.get(inPosition);
            return thePoint.mX;
        }

        public float GetYPosition(int inPosition)
        {
            CSBPoint <Float> thePoint = mStagePositions.get(inPosition);
            return thePoint.mY;
        }

        public float GetZPosition(int inPosition, int zplane)
        {
            CSBPoint <Float> thePoint = mStagePositions.get(inPosition);
            return thePoint.mZ + GetInterplaneSpacing() * zplane;
        }

    }


    class CAlignManipRecord70 extends ClassDecoder {
        public Integer mStructID;
        public Integer mStructVersion;
        public Integer mByteOrdering;
        public Integer mStructLen;
        public Integer mManipID;
        public Float mXOffset;
        public Float mYOffset;
        public Float mZOffset;
        
    }

    class CAnnotation70 extends ClassDecoder {
        public Integer mGraphicType70;
        public Integer mDependencyType70;
        public String mText;
        public Boolean[] mChannelMask;
        public Integer mGroupId; // timelapse or position capture group
        public Integer mPlaneId; // image plane
        public Integer mSequenceId; // path id
        public Integer mObjectId; // mask or submask id
        public Integer mDependencyRef; // association with object dependencies
        public Integer mVersion;		// Identifies the version of the structure
        public Integer mByteOrdering;
        public CSBPoint <Double> mFieldOffsetMicrons;
        public Double mFieldMicronsPerPixel;
        public Boolean mFieldOffsetSet;
        public CSBPoint <Double> mStageOffsetMicrons;
        public Boolean mStageOffsetSet;
        public Boolean mZStageIncreaseTowardsSample;
        public Double mAuxZStageMicrons;
        public Boolean mAuxZStageMicronsSet;
        public Boolean mAuxZStageIncreaseTowardsSample;
        public Boolean mZStageDirectionsValid;
        public Boolean mStoreMicronPositions;
        public Double mRelativePower;
        public Integer mBorderFillPixels;
        public ArrayList <CSBPoint<Integer> > mVertexes;

        public CAnnotation70()
        {
            mFieldOffsetMicrons = new CSBPoint<Double>();
            mStageOffsetMicrons = new CSBPoint<Double>();


        }

        @Override
        protected void DecodeUnknownString(String inUnknownString, Node inAttrValueNode)
        {
            System.out.println("inUnknownString: "+inUnknownString);
            if(inAttrValueNode  instanceof ScalarNode)
            {
                ScalarNode theAttrValueScalarNode = (ScalarNode)inAttrValueNode ;
                String theAttrValue = theAttrValueScalarNode.getValue();
                if(inUnknownString.equals("mStageOffsetMicrons.mX")) mStageOffsetMicrons.mX = Double.valueOf(theAttrValue);
                else if(inUnknownString.equals("mStageOffsetMicrons.mY")) mStageOffsetMicrons.mY = Double.valueOf(theAttrValue);
                else if(inUnknownString.equals("mFieldOffsetMicrons.mX")) mFieldOffsetMicrons.mX = Double.valueOf(theAttrValue);
                else if(inUnknownString.equals("mFieldOffsetMicrons.mY")) mFieldOffsetMicrons.mY = Double.valueOf(theAttrValue);
            }
            else if(inAttrValueNode  instanceof SequenceNode)
            {
                SequenceNode theAttrValueSequenceNode = (SequenceNode)inAttrValueNode ;
                Integer [] thePoints = GetIntegerArray(inAttrValueNode,"inUnknownString",false);
                mVertexes = new ArrayList<CSBPoint<Integer>>();
                for(int theP=0; theP < thePoints.length - 2; theP += 3)
                {
                    CSBPoint <Integer> thePoint = new CSBPoint<Integer>();
                    thePoint.mX = thePoints[theP];
                    thePoint.mY = thePoints[theP+1];
                    thePoint.mZ = thePoints[theP+2];
                    mVertexes.add(thePoint);
                }
            }
        }

    }

    class CChannelDef70 extends ClassDecoder {
        public Integer mStructID;
        public Integer mStructVersion;
        public Integer mByteOrdering;
        public Integer mStructLen;
        public String mName;
        public String mCameraName;

        public CFluorDef70 mFluorDef;  

        @Override
        public int Decode(MappingNode inNode, int inStartIndex)
        {
            mFluorDef = new CFluorDef70();
            int theLastIndex = super.Decode(inNode,inStartIndex);
            theLastIndex = mFluorDef.Decode(inNode,theLastIndex);
            return theLastIndex;
        }

    }

    class CChannelRecord70 extends ClassDecoder {
        
        public Integer mStructID;
        public Integer mStructVersion;
        public Integer mByteOrdering;
        public Integer mStructLen;
        public Integer mNumPlanes;	
        public Integer mNumManip;
        public Integer mManipPtr;
        public Integer mDataType;
        public Integer mDataTablePtr;	
        public Integer mHistogramTablePtr;
        public Integer mHistogramSummaryPtr;

        public CExposureRecord70 mExposureRecord;
        public CChannelDef70 mChannelDef;

            @Override
        public int Decode(MappingNode inNode, int inStartIndex)
        {
            mExposureRecord = new CExposureRecord70();
            mChannelDef = new CChannelDef70();

            int theLastIndex = super.Decode(inNode,inStartIndex);
            theLastIndex = mExposureRecord.Decode(inNode,theLastIndex);
            theLastIndex = mChannelDef.Decode(inNode,theLastIndex);
            return theLastIndex;
        }

    }

    class CCube extends ClassDecoder {
        public Integer mTopX;
        public Integer mTopY;
        public Integer mTopZ;
        public Integer mBottomX;
        public Integer mBottomY;
        public Integer mBottomZ;
    }

    class CCubeAnnotation70 extends  ClassDecoder {
        public Boolean mIsBackground;
        public Integer mRegionIndex;
        public Boolean mIsFRAP;
        public String mFRAPDevice;
        public Boolean mIsStimulation;
        public Boolean mIsLLS;
        public Boolean mIsNoLabel;
        public String mReservedBuf;
        public Boolean mIsIntSet;
        public Boolean mIsFloatSet;
        public Integer mIntData;
        public Float mFloatData;
        CAnnotation70 mAnn;
        
        @Override
        public int Decode(MappingNode inNode, int inStartIndex)
        {
            mAnn = new CAnnotation70();
            int theLastIndex = super.Decode(inNode,inStartIndex);
            theLastIndex =  mAnn.Decode(inNode,theLastIndex);
            return theLastIndex;
        }
            
    }

    class CDataTableHeaderRecord70 extends ClassDecoder {
        public Integer mStructID;
        public Integer mStructVersion;
        public Integer mByteOrdering;
        public Integer mStructLen;
        public Integer mParentRecordPtr;
        public Integer mChannelIndex;
        public Integer mRows;
        public Integer mColumns;
        public Integer mPlanes;

        public Integer mValueType;
        public Integer mTableType;
        public Integer mTimeBasis;
        public Integer mDescriptorVersion; 
        public Integer mDescriptorSize;
        public Integer mDescriptorFileOffset;
        public Integer mStartTime;
        public Integer mTimeInterval;
        public Integer mTimePointsWritten;
        public Integer mTimePointsTableSize;
        public Integer mNextTableFileOffset;
    }

    class CExposureRecord70 extends ClassDecoder {
        public Integer mStructID;
        public Integer mStructVersion;
        public Integer mByteOrdering;
        public Integer mStructLen;
        public Float mAuxZStartPosition;
        public Integer mExposureTime;
        public Integer mXOffset;
        public Integer mYOffset;
        public Integer mXExtent;
        public Integer mYExtent;
        public Boolean mBinning;
        public Boolean mTimeLapse;
        public Integer mCaptureType;
        public Integer mXFactor;
        public Integer mYFactor;
        public Integer mNumPlanes;
        public Integer mNuTSACSampleSize;	// number of planes per sample in a SA sweep.
        public Boolean mScanning;		// was this taken using a scanning system (e.g., Vivo 2-Photon)?
        public Float mInterplaneSpacing;
        public Float mInitialOffset;
        public Integer mTimeLapseInterval;
        public Integer mCaptureSetId;	// unique capture id
        public Float mXStartPosition;
        public Float mYStartPosition;
        public Float mZStartPosition;
        public Integer mCaptureFlags;
        public Integer mAuxCaptureFlags;
        public Integer mMoveFieldRightSign; // negative if moving stage right decreases stage x values
        public Integer mMoveFieldDownSign;  // negative if moving stage down decreases stage y values
        
    }

    class CFluorDef70 extends ClassDecoder {
        public Integer mStructID;
        public Integer mStructVersion;
        public Integer mByteOrdering;
        public Integer mStructLen;
        
        public String mName;
        public Integer mLaserPowerPos;
        public Integer mCameraBitDepth;
        public Integer mAuxFilterWheel7Pos;
        public Integer mNumExposuresAverage;
        public Float mExcitationLambda;
        public Integer mAuxFilterWheel5Pos;
        public Integer mAuxFilterWheel6Pos;
        public Float mLambda;
        public Integer mTurretPosition;
        public Boolean mUV;
        public Integer mImagingMode;
        public Integer mExcitationWheelPos;
        public Integer mEmissionWheelPos;
        public Integer mLightSource;
        public Boolean mTransmittedModePrompt;
        public Integer mLambdaOptions;
        public Integer mAuxFilterWheel4Pos;
        public Integer mDefaultColor;
        public Integer mChannelType;
        public Integer mLCDPos;
        public Integer mTIRFPos;
        public Float[] mRGBFactor;
        public Integer mFilterSet;
        public Integer mCamera;
        public Integer mOcularPhotoTurretPos;
        public Integer mCameraVideoTurretPos;
        public Integer mIlluminationMode;
        public Integer mAltSourcePosition;
        public Integer mCameraGain;
        public Integer mCameraSpeed;
        public Integer mCameraIntensification;
        public Integer mCameraPort;
        public Integer mCameraParameter1;
        public Integer mNDPos;
        public Integer mHue;
        public Integer mSaturation;
        public Integer mValue;
        public Integer mAuxFilterWheelPos;
        public Integer mDefaultColorDisplay;
        public Integer mAuxNDPos;
        public Integer mAuxFilterWheel2Pos;
        public Integer mAuxFilterWheel3Pos;
        
    }

    class CFRAPRegionAnnotation70 extends ClassDecoder {
        
        public String mXML;
        ArrayList <CCubeAnnotation70> mRegions;
        CAnnotation70 mAnn;

        @Override
        public int Decode(MappingNode inNode, int inStartIndex)
        {
            mAnn = new CAnnotation70();
            int theLastIndex = super.Decode(inNode,inStartIndex);
            theLastIndex =  mAnn.Decode(inNode,theLastIndex);

            IntIntPair theIIPair = GetIntegerValue(inNode,theLastIndex,"theNumRegions");
            Integer theNumRegions = theIIPair.mInt1;
            theLastIndex = theIIPair.mInt2;
            mRegions = new ArrayList<CCubeAnnotation70>();
            for(int theRegionIndex = 0; theRegionIndex < theNumRegions; theRegionIndex++)
            {
                CCubeAnnotation70 theCubeAnnotation70 = new CCubeAnnotation70() ;
                //theCubeAnnotation70.SetDataExtent(mDataExtents);
                theLastIndex = theCubeAnnotation70.Decode(inNode,theLastIndex);
                mRegions.add(theCubeAnnotation70);
            }

            return theLastIndex;
        }
            
    }

    class CFRETManipRecord70 extends ClassDecoder {
        public Integer mStructID;
        public Integer mStructVersion;
        public Integer mByteOrdering;
        public Integer mStructLen;
        public Integer mManipID;
        public Integer mFRETParadigm;
        public Float mFdDd;
        public Float mFaAa;
        public Float mDisplayLow;
        public Float mDisplayHigh;
        public Integer mDisplayNormalization;
        public Float mSignalThreshold;
        public Float mPhaseZero;
        public Float mModZero;
        public Float mDonor1Lifetime;
        public Float mDonor1X;
        public Float mDonor1Y;
        public Float mDonor2Lifetime;
        public Float mTwoLifetimeRatio;
        public Float mMainFrequency;
        public Boolean mPhaseFlatFieldCorrected;
        public Boolean mModulationFlatFieldCorrected;
        public Integer mNumPhases;
        public Integer mDarkValue;
        public Integer mFRETMethod;
        public Float mFRETAddParameter;
        
    }


    class CHistogramRecord70 extends ClassDecoder {
        public Integer mStructID;
        public Integer mStructVersion;
        public Integer mByteOrdering;
        public Integer mStructLen;		// 256 bytes + variable vector of mDataBlockSize bytes (should be less than 64KB)
        public Integer mMin;
        public Integer mMax;
        public Float mMean; // 4-byte float for 'mean' is same as return of CImageSet::ComputeStDev
        public Integer mHistogramType;
        public Integer mNumBins;
        public Integer mDataBlockSize; // number of bytes in the data block that follows this structure
        public Integer mChannelIndex;
        public Integer mImageIndex;
    }




    class CImageRecord70 extends ClassDecoder {

        public Integer mStructID;
        public Integer mStructVersion;
        public Integer mByteOrdering;
        public Integer mStructLen;
        
        public Integer mYear;
        public Integer mMonth;
        public Integer mDay;
        public Integer mHour;
        public Integer mMinute;
        public Integer mSecond;
        public Boolean mImported;
        public Integer mNotesLen;
        public Integer mNotesPtr;
        public Integer mWidth;
        public Integer mHeight;
        public Integer mNumPlanes;
        public Integer mNumChannels;
        public Integer mChannelPtr;
        public Integer mNumTimepoints;
        public Integer mNumMasks;
        public Integer mMaskPtr;
        public Integer mNumViews;
        public Integer mViewPtr;
        public Integer mXYInterpolationFactor;
        public Integer mZInterpolationFactor;
        public Integer mImageGroupIndex;
        public Integer mAnnotationTablePtr;
        public Integer mElapsedTimeTablePtr;
        public Integer mSAPositionTablePtr;
        public Integer mStagePositionTablePtr;
        public Integer mAuxDataTablePtr;
        public Integer mNumAuxDataTables;
        public Integer [] mThumbNail;
        public Integer mElapsedTimeOffset;

        public String mName;
        public String mInfo;
        public String mUniqueId;

        CLensDef70 mLensDef;
        CMainViewRecord70 mMainViewRecord;
        COptovarDef70 mOptovarDef;

            @Override
        public int Decode(MappingNode inNode)
        {
            mLensDef = new CLensDef70();
            mMainViewRecord = new CMainViewRecord70();
            mOptovarDef = new COptovarDef70();

            int theLastIndex = super.Decode(inNode,0);
            theLastIndex = mLensDef.Decode(inNode,theLastIndex);
            theLastIndex = mOptovarDef.Decode(inNode,theLastIndex);
            theLastIndex = mMainViewRecord.Decode(inNode,theLastIndex);

            return theLastIndex;

        }
    }


    class CLensDef70 extends ClassDecoder {
        public Integer mStructID;
        public Integer mStructVersion;
        public Integer mByteOrdering;
        public Integer mStructLen;
        public String mName;
        public Float mNA;
        public Float mdf;
        public Float mMicronPerPixel;
        public Integer mDeprecatedMagnification;
        public Integer mMedium;
        public Boolean mUV;
        public Integer mTurretPosition;
        public Integer mParfocalOffset;
        public Boolean mDefault;
        public Integer mParfocalOffset2;
        public Float mParcentricOffsetX;
        public Float mParcentricOffsetY;
        public Integer mBrightfieldPos;
        public Integer mDarkfieldPos;
        public Integer mDICPos;
        public Integer mPhasePos;
        public Integer mTLFieldDiaphramPos;
        public Integer mTLApertureDiaphramPos;
        public Integer mDICPrismPos;
        public Integer mTopLensPos;
        public Integer mPolarizerPos;
        public String mCameraName;
        public Float mCameraPixelSize;
        public Float mCameraMagnificationChange;
        public Float mActualMagnification;
    }


    class CMainViewRecord70 extends ClassDecoder {
        public Integer mStructID;
        public Integer mStructVersion;
        public Integer mByteOrdering;
        public Integer mStructLen;
        public Integer mViewID;
        public Integer mRedChannel;
        public Integer mGreenChannel;
        public Integer mBlueChannel;
        public Integer mBkgndChannel;
        public Integer[] mLow;
        public Integer[] mHigh;
        public Integer mColorDisplay;
        public Float mPseudoFrom;
        public Float mPseudoTo;
        public Integer mThumbPlane;
        public Integer mViewOptions;
        public Float[] mGamma;
        public Integer[] mHue;
        public Integer[] mSaturation;
        public Integer[] mValue;
        public Integer[] mChannelEnabled;
        public Integer[] mBitDepth;
        public Float mBlendFraction;
        public Integer mThumbTimePoint;
    }

    class CMaskRecord70 extends ClassDecoder {
        public Integer mStructID;
        public Integer mStructVersion;
        public Integer mByteOrdering;
        public Integer mStructLen;
        public String mName;
        public Integer mNumManip;
        public Integer mManipPtr;
        public Integer mMaskDataTablePtr;
        public Integer mPersistentSubmasks;
        public String mCentroidFeature;
        public Integer mCentroidChannel;
    }

    class COptovarDef70 extends ClassDecoder {
        public Integer mStructID;
        public Integer mStructVersion;
        public Integer mByteOrdering;
        public Integer mStructLen;
        public String mName;
        public Float mMagnification;
        public Boolean mDefault;
        public Integer mTurretPosition;
    }


    class CRatioManipRecord70 extends ClassDecoder {
        public Integer mStructID;
        public Integer mStructVersion;
        public Integer mByteOrdering;
        public Integer mStructLen;
        public Integer mManipID;
        public Float mKd;
        public Float mRmin;
        public Float mRmax;
        public Float mBeta;
        public Float mRlow;
        public Float mRhigh;
        public Integer mNumBackground;
        public Integer mDenBackground;
        public Float mExposureFactor;
        public Integer mBackX1;
        public Integer mBackY1;
        public Integer mBackX2;
        public Integer mBackY2;
        public Integer mNumMin;
        public Integer mNumMax;
        public Integer mDenMin;
        public Integer mDenMax;
    }


    class CRemapChannelLUT70 extends ClassDecoder{
        public Double[] mCoefficients;
        public Float[] mValues;
        public Boolean[] mInsideRange;

        public Float mLowDesired;
        public Float mHighDesired;
        public Integer mLowGiven;
        public Integer mHighGiven;
        public Boolean mBuiltTable;
        public Integer mRemapType;
        public String mEquationString; 
        public CRemapPoint[] mPoints;
    }


    class CRemapManipRecord70 extends ClassDecoder {
        public Integer mStructID;
        public Integer mStructVersion;
        public Integer mByteOrdering;
        public Integer mStructLen;
        public Integer mManipID;
        public Integer mRemapType;
        public Integer mNumCalibPoints;
        public Integer mReserved2;
        public Integer mCalibDataPtrLow;
        public Integer mCalibDataPtrHigh;
    }


    class CRemapPoint extends ClassDecoder {
        public Float mGivenValue;
        public Float mDesiredValue;
        public Float mSamplingStdDev;
        public CCube mCube;
    }




    class CSlideRecord70 extends ClassDecoder {

        public Integer mStructVersion;
        public Integer mByteOrdering;
        public Integer mStructLen;
        public Integer mNotesLen;                // Length of notes text
        public Integer mNumImages;
        public Integer mNotesPtr;                // File offset for notes text
        public Integer mImagePtr;                // File offset to an array of ImageRecords
        public Integer mPrefsFileLen;            // File size of inline copy of SlideBookPrefs.dat
        public Integer mPrefsOffset;             // File offset to inline copy of SlideBookPrefs.dat
        public Integer mHardwareFileOffset;      // File offset to inline copy of SlideBookHardwareProperties.dat
        public Integer mHighestCount;            // Image serial number -- KK 10.26.96
        public Integer mUncompactedSpace;        // amount of file that can be compacted
        public Integer mCheckpointNumImages;     // number of CImageRecords written at last checkpoint
        public Integer mCheckpointImagePtr;      // file offset to checkpoint array of ImageRecords
        public Integer mCheckpointMaxImages;     // maximum number of CImageRecords at checkpoint file offset
        public Integer mHardwareFileLen;         // File size of inline copy of SlideBookHardwareProperties.dat
        public Integer mCaptureStatus;           // Current capture status
        public Integer mDemoFlag;
        public String mName;                     // Slide names are limited to 127 characters
        public String mProjectFolder;
        public String mSpecialBuildStr;
        //public List<Integer> mFileVersion;       // rc file version of SlideBook
        public Integer [] mFileVersion;       // rc file version of SlideBook

    }

    class CUnknownAnnotation70 extends ClassDecoder {
        

        CAnnotation70 mAnn;

        @Override
        public int Decode(MappingNode inNode, int inStartIndex)
        {
            mAnn = new CAnnotation70();
            int theLastIndex = super.Decode(inNode,inStartIndex);
            theLastIndex =  mAnn.Decode(inNode,theLastIndex);
            return theLastIndex;
        }
    }


    class DataLoader {
        
        public String mSlidePath;
        public String mErrorMessage;
        public CSBFile70 mFile;
        public CSlideRecord70 mSlideRecord;
        public ArrayList <CImageGroup> mCImageGroupList;
        HashMap <String,RandomAccessInputStream> mPathToStreamMap;
        TreeMap <Integer,String> mCounterToPathMap;
        public static final int kMaxNumberOpenFiles = 100;
        public int mCurrentFileCounter;

        public DataLoader(String inSlidePath)
        {
            mSlidePath = inSlidePath;
            mFile = new CSBFile70(inSlidePath);
            mCImageGroupList = new ArrayList<CImageGroup> ();
            mPathToStreamMap = new HashMap<String,RandomAccessInputStream>();
            mCounterToPathMap = new TreeMap <Integer,String>();
            mCurrentFileCounter = 0;
            mErrorMessage = "";
        }

        public Boolean LoadMetadata()
        {
            try {
                Boolean theResult = ReadSld();
                System.out.println("LoadMetadata: ReadSld result: " + theResult);
                if(!theResult) return false;

                // get list of image directories
                String [] theImageTitles = mFile.GetListOfImageGroupTitles();
                for(int theImageGroupIndex = 0; theImageGroupIndex < theImageTitles.length ; theImageGroupIndex++)
                {
                    CImageGroup theImageGroup = new CImageGroup(mFile,theImageTitles[theImageGroupIndex]);
                    theResult = theImageGroup.Load();
                    System.out.println("LoadMetadata: theImageGroupIndex: " + theImageGroupIndex + "Load:  result: " + theResult);
                    if(theResult) mCImageGroupList.add(theImageGroup);
                }
                return true;
            } catch (Exception e) {
                System.out.println("Could not load file: " + mSlidePath);
                return false;
            }
        }


        public Boolean ReadSld() throws FileNotFoundException
        {
            try {
                Yaml yaml = new Yaml();
                InputStream inputStream = new FileInputStream(mSlidePath);
                Reader inputStreamReader = new InputStreamReader(inputStream);
                MappingNode theNode = (MappingNode)yaml.compose(inputStreamReader);

                mSlideRecord = new CSlideRecord70();
                inputStream.close();
                int theLastIndex = mSlideRecord.Decode(theNode);
                SlideBook7Reader.LOGGER.info("ReadSld(): mByteOrdering: " + mSlideRecord.mByteOrdering);
                return true;
            } catch (final ReaderException e) {
                mErrorMessage += "Could not decode file: " + mSlidePath;
                SlideBook7Reader.LOGGER.info("ReadSld(): " +mErrorMessage);
                return false;
            } catch (Exception e) {
                mErrorMessage += "Could not load file: " + mSlidePath;
                return false;
            }
        }

        public Boolean ReadSld(InputStream inInputStream) throws FileNotFoundException
        {
            Yaml yaml = new Yaml();
            Reader inputStreamReader = new InputStreamReader(inInputStream);
            MappingNode theNode = (MappingNode)yaml.compose(inputStreamReader);

            mSlideRecord = new CSlideRecord70();
            try {
                int theLastIndex = mSlideRecord.Decode(theNode);
                SlideBook7Reader.LOGGER.info("ReadSld(stream): mByteOrdering: " + mSlideRecord.mByteOrdering);
                return true;
            } catch (Exception e) {
                mErrorMessage += "Could not load file: " + mSlidePath;
                return false;
            }
        }
        public int GetNumCaptures()
        {
            return mCImageGroupList.size();
        }
        public CImageGroup GetImageGroup(int inCaptureId)
        {
            return mCImageGroupList.get(inCaptureId);
        }

        public boolean ReadPlane(int inCaptureId, byte[] ouBuf,
            int inPositionIndex, int inTimepointIndex, int inZPlaneIndex, int inChannelIndex) throws IOException
        {
            SlideBook7Reader.LOGGER.info("ReadPlane: inPositionIndex: " + inPositionIndex);
            SlideBook7Reader.LOGGER.info("ReadPlane: inTimepointIndex: " + inTimepointIndex);
            SlideBook7Reader.LOGGER.info("ReadPlane: inZPlaneIndex: " + inZPlaneIndex);
            SlideBook7Reader.LOGGER.info("ReadPlane: inChannelIndex: " + inChannelIndex);
            CImageGroup theImageGroup = GetImageGroup(inCaptureId);
            // because the format is XYCZT , the inPositionIndex is always 0, so we must ignore GetNumPositions
            int theSbTimepointIndex = inTimepointIndex;// inPositionIndex + theImageGroup.GetNumPositions() * inTimepointIndex;
            String thePath = theImageGroup.mFile.GetImageDataFile(theImageGroup.mImageTitle,inChannelIndex,theSbTimepointIndex);
            RandomAccessInputStream theStream;
            theStream = mPathToStreamMap.get(thePath);
            if(theStream == null)
            {
                if(mCounterToPathMap.size() > kMaxNumberOpenFiles)
                {
                    Integer theKeyValue = mCounterToPathMap.firstKey();
                    String theKeyPath = mCounterToPathMap.get(theKeyValue);
                    if(theKeyPath != null)
                    {
                        RandomAccessInputStream theKeyStream = mPathToStreamMap.get(theKeyPath);
                        if(theKeyStream != null)
                        {
                            theKeyStream.close();
                            mCounterToPathMap.remove(theKeyValue);
                            mPathToStreamMap.remove(theKeyPath);
                        }
                    }
                }
                theStream = new RandomAccessInputStream(thePath);
                if(theImageGroup.mNpyHeader == null)
                {
                    theImageGroup.mNpyHeader = new CNpyHeader();
                    boolean theRes = theImageGroup.mNpyHeader.ParseNpyHeader( theStream);
                    if(!theRes) return false;
                }
                mPathToStreamMap.put(thePath,theStream);
                mCounterToPathMap.put(mCurrentFileCounter,thePath);
                mCurrentFileCounter++;
            }
            long thePlaneSize = theImageGroup.GetNumColumns() * theImageGroup.GetNumRows() * theImageGroup.mNpyHeader.mBytesPerPixel;
            SlideBook7Reader.LOGGER.info("ReadPlane: thePlaneSize: " + thePlaneSize);
            long theSeekOffset = theImageGroup.mNpyHeader.mHeaderSize + thePlaneSize * inZPlaneIndex;
            SlideBook7Reader.LOGGER.info("ReadPlane: theSeekOffset: " + theSeekOffset);
            theStream.seek(theSeekOffset);
            theStream.read(ouBuf,0,(int)thePlaneSize);

            int theMax=0;
            int theMin = 1000000;
            /*
            for(int theI=0; theI < thePlaneSize ; theI += 2)
            {
                short theVal = ByteArrayToShort(ouBuf,theI);
                
                if(theVal > theMax) theMax = theVal;
                if(theVal < theMin) theMin = theVal;
            }
            SlideBook7Reader.LOGGER.info("ReadPlane: theMax: " + theMax);
            SlideBook7Reader.LOGGER.info("ReadPlane: theMin: " + theMin);
            */

            // byte swap
            /*
            for(int theI=0; theI < thePlaneSize ; theI += 2)
            {
                byte t = ouBuf[theI];
                ouBuf[theI] = ouBuf[theI+1];
                ouBuf[theI+1] = t;
            }
            */
            return true;
        }
        short ByteArrayToShort(byte[] bytes,int offset) {
             int theVal = ((bytes[offset+1] & 0xFF) << 8) | 
                    ((bytes[offset+0] & 0xFF) << 0 );
            return (short)theVal;
        }

        public void CloseFile()
        {

        }

    }


//public class SlideBook7Reader  extends FormatReader {

	// -- Constants --

	private static final String URL_3I_SLD =
			"http://www.openmicroscopy.org/site/support/bio-formats/formats/3i-slidebook7.html";


    DataLoader mDataLoader;


	// -- Constructor --

	public SlideBook7Reader() {
		super("SlideBook 7 SLD (native)", new String[] {"sldy"});
		domains = new String[] {FormatTools.LM_DOMAIN};
		suffixSufficient = false;
        LOGGER.info(" LOGGER.info SlideBook7Reader: Constructed\n");
        System.out.println("println: SlideBook7Reader: Constructed\n");
	}

	// -- IFormatReader API methods --

	/* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
	public boolean isThisType(RandomAccessInputStream stream) throws IOException {
        //Logger.getLogger("SlideBook7Reader").log(Level.SEVERE, null, "isThisType - stream");
        try {
            System.out.println("SlideBook7Reader: isThisType - entering as stream: ");
            if(mDataLoader == null) return true;
            Boolean res = mDataLoader.ReadSld(stream);
            System.out.println("SlideBook7Reader: isThisType - stream - res: " + res);
            return res;
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }

		return false;
	}

	/* @see loci.formats.IFormatReader#isThisType(String, boolean) */
	public boolean isThisType(String file, boolean open) {
        //Logger.getLogger("SlideBook7Reader").log(Level.SEVERE, null, "isThisType - file: " + file);
        try {
            String mytestfile = file;
            SlideBook7Reader.LOGGER.info("SlideBook7Reader: isThisType - open: " + open);
            // Check the first few bytes to determine if the file can be read by this reader.
            if (!open) return super.isThisType(mytestfile, open); // no file system access

            Location theFileLocation = new Location(mytestfile).getAbsoluteFile();
            mDataLoader  = new DataLoader(theFileLocation.getAbsolutePath());
            Boolean res = mDataLoader.ReadSld();
            System.out.println("SlideBook7Reader: isThisType - file: " + mytestfile + ", res is: " +res );
            return res;
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }


		return false;
	}

	/**
	 * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
	 */
	public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
			throws FormatException, IOException
	{
        int theSeries = getSeries();
        SlideBook7Reader.LOGGER.info("openBytes: theSeries: " + theSeries);
        SlideBook7Reader.LOGGER.info("openBytes: no: " + no);
        SlideBook7Reader.LOGGER.info("openBytes: x: " + x);
        SlideBook7Reader.LOGGER.info("openBytes: y: " + y);
        SlideBook7Reader.LOGGER.info("openBytes: w: " + w);
        SlideBook7Reader.LOGGER.info("openBytes: h: " + h);
		FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

		int[] zct = FormatTools.getZCTCoords(this, no);
		int bpc = FormatTools.getBytesPerPixel(getPixelType());
        int thePlaneSize = FormatTools.getPlaneSize(this);
        //long thePlaneSize = theImageGroup.GetNumColumns() * theImageGroup.GetNumRows() * theImageGroup.mNpyHeader.mBytesPerPixel;
        //SlideBook7Reader.LOGGER.info("openBytes: thePlaneSize: " + thePlaneSize);
        //SlideBook7Reader.LOGGER.info("openBytes: bpc: " + bpc);
		byte[] b = new byte[thePlaneSize*2];

		mDataLoader.ReadPlane(getSeries(),b, 0, zct[2], zct[0], zct[1]);

		int pixel = bpc * getRGBChannelCount();
		int rowLen = w * pixel;
        //SlideBook7Reader.LOGGER.info("openBytes: pixel: " + pixel);
        //SlideBook7Reader.LOGGER.info("openBytes: rowLen: " + rowLen);
        int width = getSizeX();
        //SlideBook7Reader.LOGGER.info("openBytes: width: " + width);
		for (int row=0; row<h; row++) {
			System.arraycopy(b, pixel * ((row + y) * getSizeX() + x), buf,
					row * rowLen, rowLen);
		}

        //SlideBook7Reader.LOGGER.info("openBytes: b: " + b[1000]);
        //SlideBook7Reader.LOGGER.info("openBytes: isRGB: " + isRGB());
		if (isRGB()) {
			int bpp = getSizeC() * bpc;
			int line = w * bpp;
			for (int row=0; row<h; row++) {
				for (int col=0; col<w; col++) {
					int base = row * line + col * bpp;
					for (int bb=0; bb<bpc; bb++) {
						byte blue = buf[base + bpc*(getSizeC() - 1) + bb];
						buf[base + bpc*(getSizeC() - 1) + bb] = buf[base + bb];
						buf[base + bb] = blue;
					}
				}
			}
		}
		return buf;
	}

	// -- Internal FormatReader API methods --
	public void close(boolean fileOnly) throws IOException {
		super.close(fileOnly);
        mDataLoader.CloseFile();
	}

	/* @see loci.formats.FormatReader#initFile(String) */
	protected void initFile(String id) throws FormatException, IOException {
		super.initFile(id);

		try {
            Boolean res; 
            res = mDataLoader.LoadMetadata();
            if(!res)
            {
                Exception e = new Exception("Could not load metadata");
                throw new FormatException("Could not load metadata", e);
            }

			// read basic meta data
			int numCaptures = mDataLoader.GetNumCaptures();
			int[] numPositions = new int[numCaptures];
			int[] numTimepoints = new int[numCaptures];
			int[] numZPlanes = new int[numCaptures];
			int[] numChannels = new int[numCaptures];
			for (int capture=0; capture < numCaptures; capture++) {
                CImageGroup theCurrentImageGroup = mDataLoader.GetImageGroup(capture);

				numPositions[capture] = theCurrentImageGroup.GetNumPositions();
				numTimepoints[capture] = theCurrentImageGroup.GetNumTimepoints() / numPositions[capture];
				numZPlanes[capture] = theCurrentImageGroup.GetNumPlanes();
				numChannels[capture] = theCurrentImageGroup.GetNumChannels();
                SlideBook7Reader.LOGGER.info("capture: "+capture);
                SlideBook7Reader.LOGGER.info("numPositions[capture]: "+numPositions[capture]);
                SlideBook7Reader.LOGGER.info("numTimepoints[capture]: "+numTimepoints[capture]);
                SlideBook7Reader.LOGGER.info("numZPlanes[capture]: "+numZPlanes[capture]);
                SlideBook7Reader.LOGGER.info("numChannels[capture]: "+numChannels[capture]);
			}

			core.clear();

			// set up basic meta data
			for (int capture=0; capture < numCaptures; capture++) {
                CImageGroup theCurrentImageGroup = mDataLoader.GetImageGroup(capture);
				CoreMetadata ms = new CoreMetadata();
				core.add(ms);
				setSeries(capture);
				ms.sizeX = theCurrentImageGroup.GetNumColumns();
				//if (ms.sizeX % 2 != 0) ms.sizeX++;
				ms.sizeY = theCurrentImageGroup.GetNumRows();
                SlideBook7Reader.LOGGER.info("ms.sizeX: "+ms.sizeX);
                SlideBook7Reader.LOGGER.info("ms.sizeY: "+ms.sizeY);
				ms.sizeZ = numZPlanes[capture];
				ms.sizeT = numTimepoints[capture] * numPositions[capture]; 
				ms.sizeC = numChannels[capture];
                SlideBook7Reader.LOGGER.info("ms.sizeT: "+ms.sizeT);
                SlideBook7Reader.LOGGER.info("ms.sizeC: "+ms.sizeC);
				int bytes = theCurrentImageGroup.GetBytesPerPixel();
                SlideBook7Reader.LOGGER.info("initFile: bytes: " + bytes);
				if (bytes % 3 == 0) {
					ms.sizeC *= 3;
					bytes /= 3;
					ms.rgb = true;
				}
				else ms.rgb = false;

				ms.pixelType = FormatTools.pixelTypeFromBytes(bytes, false, true);
                SlideBook7Reader.LOGGER.info("initFile: ms.pixelType: " + ms.pixelType);
				ms.imageCount = ms.sizeZ * ms.sizeT;
				if (!ms.rgb) 
					ms.imageCount *= ms.sizeC;
				ms.interleaved = true;
				ms.littleEndian = true;
				ms.dimensionOrder = "XYCZT";
				ms.indexed = false;
				ms.falseColor = false;
			}
			setSeries(0);

			// fill in meta data
			MetadataStore store = makeFilterMetadata();
			MetadataTools.populatePixels(store, this, true);

			// add extended meta data
			if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
				
				// set instrument information
				String instrumentID = MetadataTools.createLSID("Instrument", 0);
				store.setInstrumentID(instrumentID, 0);

				// set up extended meta data
				for (int capture=0; capture < numCaptures; capture++) {
                    CImageGroup theCurrentImageGroup = mDataLoader.GetImageGroup(capture);
					// link Instrument and Image
					store.setImageInstrumentRef(instrumentID, capture);

					// set image name
					String imageName = theCurrentImageGroup.GetName();
					store.setImageName(imageName, capture);
					// store.setImageName("Image " + (capture + 1), capture);

					// set description
					String imageDescription = theCurrentImageGroup.GetInfo();
					store.setImageDescription(imageDescription, capture);

					// set voxel size per image (microns)
					double voxelsize = theCurrentImageGroup.GetVoxelSize();
                    SlideBook7Reader.LOGGER.info("initFile: voxelsize: " + voxelsize);
					Length physicalSizeX = FormatTools.getPhysicalSizeX(voxelsize);
					Length physicalSizeY = FormatTools.getPhysicalSizeY(voxelsize);
					if (physicalSizeX != null) {
						store.setPixelsPhysicalSizeX(physicalSizeX, capture);
					}
					if (physicalSizeY != null) {
						store.setPixelsPhysicalSizeY(physicalSizeY, capture);
					}
                    SlideBook7Reader.LOGGER.info("initFile: physicalSizeX: " + physicalSizeX);
                    SlideBook7Reader.LOGGER.info("initFile: physicalSizeY: " + physicalSizeY);
					double stepSize = 0;
					if (numZPlanes[capture] > 1) {
						stepSize = theCurrentImageGroup.GetInterplaneSpacing();
					}
                    SlideBook7Reader.LOGGER.info("initFile: stepSize: " + stepSize);

					Length physicalSizeZ = FormatTools.getPhysicalSizeZ(stepSize);
					if (physicalSizeZ != null) {
						store.setPixelsPhysicalSizeZ(physicalSizeZ, capture);
					}

					int imageIndex = 0;
					// if numPositions[capture] > 1 then we have a montage
					for (int timepoint = 0; timepoint < numTimepoints[capture]; timepoint++) {
						int deltaT = theCurrentImageGroup.GetElapsedTime(timepoint);
						for (int position = 0; position < numPositions[capture]; position++) {
							for (int zplane = 0; zplane < numZPlanes[capture]; zplane++) {
								for (int channel = 0; channel < numChannels[capture]; channel++, imageIndex++) {
									// set elapsed time
									store.setPlaneDeltaT(new Time(deltaT, UNITS.MS), capture, imageIndex);

									// set exposure time
									int expTime = theCurrentImageGroup.GetExposureTime(channel);
									store.setPlaneExposureTime(new Time(expTime, UNITS.MS), capture, imageIndex);

									// set tile xy position
									double numberX = theCurrentImageGroup.GetXPosition( position);
									Length positionX = new Length(numberX, UNITS.MICROM);
                                    SlideBook7Reader.LOGGER.info("initFile: positionX: " + numberX);
									store.setPlanePositionX(positionX, capture, imageIndex);
									double numberY = theCurrentImageGroup.GetYPosition(position);
									Length positionY = new Length(numberY, UNITS.MICROM);
									store.setPlanePositionY(positionY, capture, imageIndex);
                                    SlideBook7Reader.LOGGER.info("initFile: positionY: " + numberY);

									// set tile z position
									double positionZ = theCurrentImageGroup.GetZPosition(position, zplane);
									Length zPos = new Length(positionZ, UNITS.MICROM);
									store.setPlanePositionZ(zPos, capture, imageIndex);
                                    SlideBook7Reader.LOGGER.info("initFile: positionZ: " + positionZ);
								}
							}
						}
					}

					// set channel names
					for (int channel = 0; channel < numChannels[capture]; channel++) {
						String theChannelName = theCurrentImageGroup.GetChannelName(channel);
						store.setChannelName(theChannelName.trim(), capture, channel);
					}
				}

				// populate Objective data
				int objectiveIndex = 0;
				for (int capture = 0; capture < numCaptures; capture++) {
                    CImageGroup theCurrentImageGroup = mDataLoader.GetImageGroup(capture);
					// link Objective to Image
					String objectiveID = MetadataTools.createLSID("Objective", 0, objectiveIndex);
					store.setObjectiveID(objectiveID, 0, objectiveIndex);
					store.setObjectiveSettingsID(objectiveID, capture);

					String objective = theCurrentImageGroup.GetLensName();
					if (objective != null) {
						store.setObjectiveModel(objective, 0, objectiveIndex);
					}
					store.setObjectiveCorrection(getCorrection("Other"), 0, objectiveIndex);
					store.setObjectiveImmersion(getImmersion("Other"), 0, objectiveIndex);
					double magnification = theCurrentImageGroup.GetMagnification();
					if (magnification > 0) {
						store.setObjectiveNominalMagnification(magnification, 0, objectiveIndex);
					}
					objectiveIndex++;
				}
			}
		}
		catch (Exception e) {
            e.printStackTrace();
		}
	}
}

