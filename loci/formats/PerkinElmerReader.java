//
// PerkinElmerReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

package loci.formats;

import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

/**
 * PerkinElmerReader is the file format reader for PerkinElmer files.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class PerkinElmerReader extends FormatReader {

  // -- Fields --

  /** Number of images. */
  protected int numImages;

  /** Helper reader. */
  protected TiffReader tiff;

  /** Tiff files to open. */
  protected String[] files;


  // -- Constructor --

  /** Constructs a new PerkinElmer reader. */
  public PerkinElmerReader() {
    super("PerkinElmer", new String[] {"csv", "htm", "tim", "zpo"});
    tiff = new TiffReader();
  }


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a PerkinElmer file. */
  public boolean isThisType(byte[] block) {
    return false;
  }

  /** Determines the number of images in the given PerkinElmer file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numImages;
  }

  /** Obtains the specified image from the given PerkinElmer file. */
  public BufferedImage open(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }
    return tiff.open(files[no], 0);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    currentId = null;
  }

  /** Initializes the given PerkinElmer file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    // get the working directory
    File tempFile = new File(id);
    File workingDir = tempFile.getParentFile();
    if (workingDir == null) workingDir = new File(".");
    String workingDirPath = workingDir.getPath() + File.separator;
    String[] ls = workingDir.list();

    // check if we have any of the required header file types

    int timPos = -1;
    int csvPos = -1;
    int zpoPos = -1;
    int htmPos = -1;
    int filesPt = 0;
    files = new String[ls.length];

    String tempFileName = tempFile.getName();
    int dot = tempFileName.lastIndexOf(".");
    String check = dot < 0 ? tempFileName : tempFileName.substring(0, dot);

    // locate appropriate .tim, .csv, .zpo, .htm and .tif files

     for (int i=0; i<ls.length; i++) {
       // make sure that the file has a name similar to the name of the
       // specified file
       if (ls[i].startsWith(check)) {
         if (timPos == -1) {
           if (ls[i].toLowerCase().endsWith(".tim")) timPos = i;
         }
         if (csvPos == -1) {
           if (ls[i].toLowerCase().endsWith(".csv")) csvPos = i;
         }
         if (zpoPos == -1) {
           if (ls[i].toLowerCase().endsWith(".zpo")) zpoPos = i;
         }
         if (htmPos == -1) {
           if (ls[i].toLowerCase().endsWith(".htm")) htmPos = i;
         }

         if (ls[i].toLowerCase().endsWith(".tif") ||
           ls[i].toLowerCase().endsWith(".tiff"))
         {
           files[filesPt] = workingDirPath + ls[i];
           filesPt++;
         }
       }
     }

     String[] tempFiles = files;
     files = new String[filesPt];
     System.arraycopy(tempFiles, 0, files, 0, filesPt);

     numImages = files.length;
     FileReader read;
     char[] data;
     StringTokenizer t;

     // highly questionable metadata parsing

     // we always parse the .tim and .htm files if they exist, along with
     // either the .csv file or the .zpo file

     if (timPos != -1) {
       tempFile = new File(workingDir, ls[timPos]);
       read = new FileReader(tempFile);
       data = new char[(int) tempFile.length()];
       read.read(data);
       t = new StringTokenizer(new String(data));
       int tNum = 0;
       // can ignore "Zero x" and "Extra int"
       String[] hashKeys = {"Number of Wavelengths/Timepoints", "Zero 1",
         "Zero 2", "Number of slices", "Extra int", "Calibration Unit",
         "Pixel Size Y", "Pixel Size X", "Image Width", "Image Length",
         "Origin X", "SubfileType X", "Dimension Label X", "Origin Y",
         "SubfileType Y", "Dimension Label Y", "Origin Z",
         "SubfileType Z", "Dimension Label Z"};

       // there are 9 additional tokens, but I don't know what they're for

       while (t.hasMoreTokens() && tNum<hashKeys.length) {
         metadata.put(hashKeys[tNum], t.nextToken());
         tNum++;
       }
     }

     if (csvPos != -1) {
       tempFile = new File(workingDir, ls[csvPos]);
       read = new FileReader(tempFile);
       data = new char[(int) tempFile.length()];
       read.read(data);
       t = new StringTokenizer(new String(data));
       int tNum = 0;
       String[] hashKeys = {"Calibration Unit", "Pixel Size X", "Pixel Size Y",
         "Z slice space"};
       int pt = 0;
       while (t.hasMoreTokens()) {
         if (tNum < 7) { String temp = t.nextToken(); }
         else if ((tNum > 7 && tNum < 12) || (tNum > 12 && tNum < 18) ||
           (tNum > 18 && tNum < 22)) {
           String temp = t.nextToken();
         }
         else if (pt < hashKeys.length) {
           metadata.put(hashKeys[pt], t.nextToken());
           pt++;
         }
         else {
           metadata.put(t.nextToken() + t.nextToken(),
             t.nextToken());
         }
         tNum++;
       }
     }
     else if (zpoPos != -1) {
       tempFile = new File(workingDir, ls[zpoPos]);
       read = new FileReader(tempFile);
       data = new char[(int) tempFile.length()];
       read.read(data);
       t = new StringTokenizer(new String(data));
       int tNum = 0;
       while (t.hasMoreTokens()) {
         metadata.put("Z slice #" + tNum + " position", t.nextToken());
         tNum++;
       }
     }

     // be aggressive about parsing the HTML file, since it's the only one that
     // explicitly defines the number of wavelengths and timepoints

     if (htmPos != -1) {
       // ooh, pretty HTML

       tempFile = new File(workingDir, ls[htmPos]);
       read = new FileReader(tempFile);
       data = new char[(int) tempFile.length()];
       read.read(data);

       String regex = "<p>|</p>|<br>|<hr>|<b>|</b>|<HTML>|<HEAD>|</HTML>|" +
         "</HEAD>|<h1>|</h1>|<HR>|</body>";

       // use reflection to avoid dependency on Java 1.4-specific split method
       Class c = String.class;
       String[] tokens = new String[0];
       try {
         Method split = c.getMethod("split", new Class[] {c});
         tokens = (String[]) split.invoke(new String(data),
           new Object[] {regex});
       }
       catch (Throwable e) { }

       for (int j=0; j<tokens.length; j++) {
         if (tokens[j].indexOf("<") != -1) tokens[j] = "";
       }

       int slice = 0;
       for (int j=0; j<tokens.length-1; j+=2) {
         if (tokens[j].indexOf("Wavelength") != -1) {
           metadata.put("Camera Data " + tokens[j].charAt(13), tokens[j]);
           j--;
         }
         else if (!tokens[j].trim().equals("")) {
           metadata.put(tokens[j], tokens[j+1]);
         }
       }
     }
     else {
       throw new FormatException("Valid header files not found.");
     }

     String details = (String) metadata.get("Experiment details:");
     // parse details to get number of wavelengths and timepoints

     t = new StringTokenizer(details);
     int tokenNum = 0;
     String timePoints = "1";
     String wavelengths = "1";
     int numTokens = t.countTokens();
     while (t.hasMoreTokens()) {
       if (tokenNum == numTokens - 6) {
         wavelengths = (String) t.nextToken();
       }
       else if (tokenNum == numTokens - 4) {
         timePoints = (String) t.nextToken();
       }
       else {
         String temp = t.nextToken();
       }
       tokenNum++;
     }

     // initialize OME-XML

     if (ome != null) {
       // populate Dimensions element
       String pixelSizeX = (String) metadata.get("Pixel Size X");
       String pixelSizeY = (String) metadata.get("Pixel Size Y");
       OMETools.setDimensions(ome, new Float(pixelSizeX),
         new Float(pixelSizeY), null, null, null);

       // populate Image element
       String time = (String) metadata.get("Finish Time:");
       time = time.substring(1).trim();
       OMETools.setCreationDate(ome, time);

       // populate Pixels element
       String sizeX = (String) metadata.get("Image Width");
       String sizeY = (String) metadata.get("Image Length");
       String sizeZ = (String) metadata.get("Number of slices");
       OMETools.setPixels(ome,
         new Integer(sizeX), // SizeX
         new Integer(sizeY), // SizeY
         new Integer(sizeZ), // SizeZ
         new Integer(wavelengths), // SizeC
         new Integer(timePoints), // SizeT
         null, // PixelType
         null, // BigEndian
         "XYCTZ"); // DimensionOrder

       // populate StageLabel element
       String originX = (String) metadata.get("Origin X");
       String originY = (String) metadata.get("Origin Y");
       String originZ = (String) metadata.get("Origin Z");
       OMETools.setStageLabel(ome, null,
         new Float(originX), new Float(originY), new Float(originZ));
     }
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new PerkinElmerReader().testRead(args);
  }

}
