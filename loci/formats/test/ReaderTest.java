//
// ReaderTest.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.test;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import loci.formats.*;
import org.junit.*;
import org.junit.runner.*;
import static org.junit.Assert.*;

/**
 * Class that provides automated testing of the Bio-Formats library using JUnit.
 * Failed tests are written to a logfile, for easier processing.
 */
public class ReaderTest {

  // -- Fields --

  private static Vector files;
  private static Vector badFiles;
  private static String currentFile;
  private static IFormatReader reader;
  private static FileWriter logFile;

  // -- API methods --

  /** Set the file to process. */
  public static void setFile(String file) {
    currentFile = file;
  }

  /** Determine if the given filename is a "bad" file. */
  public static boolean isBadFile(String file) {
    if (badFiles == null) {
      try {
        badFiles = new Vector();
        BufferedReader in = new BufferedReader(new FileReader("bad-files.txt"));
        while (true) {
          String line = in.readLine();
          if (line == null) break;
          badFiles.add(line);
        }
        in.close();
      }
      catch (IOException io) { }
    }
    return badFiles.contains(file);
  }

  /** Reset the format reader. */
  public static void resetReader() {
    reader = new FileStitcher();
    OMEXMLMetadataStore store = new OMEXMLMetadataStore();
    store.createRoot();
    reader.setMetadataStore(store);
  }

  /** Recursively generate a list of files to test. */
  public static void getFiles(String root, Vector files) {
    if (reader == null) {
      reader = new FileStitcher();
      OMEXMLMetadataStore store = new OMEXMLMetadataStore();
      store.createRoot();
      reader.setMetadataStore(store);
    
      Date date = new Date();
      try {
        logFile = new FileWriter("bioformats-test-" + date.toString() + ".log");
        logFile.flush();
      }
      catch (IOException io) { }
    }
    
    File f = new File(root);
    String[] subs = f.list();
    if (subs != null) {
      for (int i=0; i<subs.length; i++) {
        if (!isBadFile(subs[i])) {
          subs[i] = root + File.separator + subs[i];
          File tmp = new File(subs[i]);
          if (!tmp.isDirectory() && reader.isThisType(subs[i])) {
            files.add(subs[i]);
          }
          else if (tmp.isDirectory()) getFiles(subs[i], files);
        }
      }
    }
  }

  // -- Testing methods --

  /** 
   * Check the SizeX and SizeY dimensions against the actual dimensions of
   * the BufferedImages.
   */
  @Test public void testBufferedImageDimensions() {
   boolean success = true;
    try {
      int imageCount = reader.getImageCount(currentFile);
      int sizeX = reader.getSizeX(currentFile);
      int sizeY = reader.getSizeY(currentFile);
      for (int j=0; j<imageCount; j++) {
        BufferedImage b = reader.openImage(currentFile, j);
        if ((b.getWidth() != sizeX) || (b.getHeight() != sizeY)) {
          success = false;
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      success = false; 
    }
    try {
      if (!success) {
        logFile.write(currentFile + " failed BufferedImage test\n");
        logFile.flush();
      }
    }
    catch (IOException io) { io.printStackTrace(); }
    assertTrue(success);
  }

  /**
   * Check the SizeX and SizeY dimensions against the actual dimensions of
   * the byte array returned by openBytes.
   */
  @Test public void testByteArrayDimensions() {
    boolean success = true;
    try {
      int imageCount = reader.getImageCount(currentFile);
      int sizeX = reader.getSizeX(currentFile);
      int sizeY = reader.getSizeY(currentFile);
      int bytesPerPixel = 
        FormatReader.getBytesPerPixel(reader.getPixelType(currentFile));
      int sizeC = reader.getSizeC(currentFile);
      boolean rgb = reader.isRGB(currentFile);

      int expectedBytes = sizeX * sizeY * bytesPerPixel * (rgb ? sizeC : 1);

      for (int i=0; i<imageCount; i++) {
        byte[] b = reader.openBytes(currentFile, i);
        if (b.length != expectedBytes) success = false; 
      }
    }
    catch (Exception e) { 
      e.printStackTrace();
      success = false; 
    }
    try {
      if (!success) {
        logFile.write(currentFile + " failed byte array test\n");
        logFile.flush();
      }
    }
    catch (IOException io) { io.printStackTrace(); }
    assertTrue(success);
  }

  /**
   * Check the SizeZ, SizeC, and SizeT dimensions against the 
   * total image count.
   */
  @Test public void testImageCount() {
    try {
      int imageCount = reader.getImageCount(currentFile);
      int sizeZ = reader.getSizeZ(currentFile);
      int sizeC = reader.getEffectiveSizeC(currentFile);
      int sizeT = reader.getSizeT(currentFile);
      boolean success = imageCount == sizeZ * sizeC * sizeT;
      try {
        if (!success) {
          logFile.write(currentFile + " failed image count test\n");
          logFile.flush();
        }
      }
      catch (IOException io) { io.printStackTrace(); }
      assertTrue(success);
    }
    catch (Exception e) { 
      try {
        logFile.write(currentFile + " failed image count test\n");
        logFile.flush();
      }
      catch (IOException io) { io.printStackTrace(); }
      assertTrue(false); 
    }
  }

  /**
   * Check that the OME-XML attribute values match the values of the core
   * metadata (Size*, DimensionOrder, etc.).
   */
  @Test public void testOMEXML() {
    try {
      OMEXMLMetadataStore store = 
        (OMEXMLMetadataStore) reader.getMetadataStore(currentFile);
  
      int sizeX = reader.getSizeX(currentFile);
      int sizeY = reader.getSizeY(currentFile);
      int sizeZ = reader.getSizeZ(currentFile);
      int sizeC = reader.getSizeC(currentFile);
      int sizeT = reader.getSizeT(currentFile);
      boolean bigEndian = !reader.isLittleEndian(currentFile);
      String type = 
        FormatReader.getPixelTypeString(reader.getPixelType(currentFile));
      String dimensionOrder = reader.getDimensionOrder(currentFile);

      boolean success =  
        (sizeX == store.getSizeX(null)) &&
        (sizeY == store.getSizeY(null)) &&
        (sizeZ == store.getSizeZ(null)) &&
        (sizeC == store.getSizeC(null)) &&
        (sizeT == store.getSizeT(null)) &&
        (bigEndian == store.getBigEndian(null)) &&
        type.toLowerCase().equals(store.getPixelType(null).toLowerCase()) &&
        dimensionOrder.equals(store.getDimensionOrder(null));
   
      if (!success) {
        try {
          logFile.write(currentFile + " failed OME-XML sanity test\n");
          logFile.flush();
        }
        catch (IOException io) { io.printStackTrace(); }
      }
      assertTrue(success); 
    }
    catch (Exception e) { 
      try {
        logFile.write(currentFile + " failed OME-XML sanity test\n");
        logFile.flush();
      }
      catch (IOException io) { io.printStackTrace(); }
      assertTrue(false); 
    }
  }

  // -- Main method --

  public static void main(String[] args) {
    Vector files = new Vector();  
    ReaderTest.getFiles(args[0], files);
    for (int i=0; i<files.size(); i++) {
      JUnitCore core = new JUnitCore();
      ReaderTest.setFile((String) files.get(i));
      ReaderTest.resetReader();
      Result r = core.run(Request.aClass(ReaderTest.class));
      int total = r.getRunCount();
      int failed = r.getFailureCount();
      float failPercent = (float) (100 * ((double) failed / (double) total));
      System.out.println(files.get(i) + " - " + failed + " failures in " + 
        total + " tests (" + failPercent + "% failed)");
    }
  }

}
