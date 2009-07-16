//
// TiffReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import loci.common.Location;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.TiffTools;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;

/**
 * TiffReader is the file format reader for regular TIFF files,
 * not of any specific TIFF variant.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/TiffReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/TiffReader.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 */
public class TiffReader extends BaseTiffReader {

  // -- Constants --

  public static final String[] TIFF_SUFFIXES =
    {"tif", "tiff", "tf2", "tf8", "btf"};

  // -- Fields --

  private String companionFile;

  // -- Constructor --

  /** Constructs a new Tiff reader. */
  public TiffReader() {
    super("Tagged Image File Format", TIFF_SUFFIXES);
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getUsedFiles(boolean) */
  public String[] getUsedFiles(boolean noPixels) {
    if (noPixels) {
      return companionFile == null ? null : new String[] {companionFile};
    }
    if (companionFile != null) return new String[] {companionFile, currentId};
    return new String[] {currentId};
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();
    String comment = TiffTools.getComment(ifds.get(0));

    status("Checking comment style");

    if (ifds.size() > 1) core[0].orderCertain = false;

    // check for ImageJ-style TIFF comment
    boolean ij = checkCommentImageJ(comment);
    if (ij) parseCommentImageJ(comment);

    // check for MetaMorph-style TIFF comment
    boolean metamorph = checkCommentMetamorph(comment);
    if (metamorph) parseCommentMetamorph(comment);
    put("MetaMorph", metamorph ? "yes" : "no");

    // check for other INI-style comment
    if (!ij && !metamorph) parseCommentGeneric(comment);

    // check for another file with the same name

    Location currentFile = new Location(currentId).getAbsoluteFile();
    String currentName = currentFile.getName();
    Location directory = currentFile.getParentFile();
    String[] files = directory.list(true);
    for (String file : files) {
      String name = file;
      if (name.indexOf(".") != -1) name = name.substring(0, name.indexOf("."));

      if (currentName.startsWith(name) && !file.equals(currentName)) {
        companionFile = new Location(directory, file).getAbsolutePath();
        break;
      }
    }

    // TODO : parse companion file once loci.parsers package is in place
  }

  // -- Helper methods --

  private boolean checkCommentImageJ(String comment) {
    return comment != null && comment.startsWith("ImageJ=");
  }

  private boolean checkCommentMetamorph(String comment) {
    Object software = TiffTools.getIFDValue(ifds.get(0), TiffTools.SOFTWARE);
    String check = software instanceof String ? (String) software :
      software instanceof String[] ? ((String[]) software)[0] : null;
    return comment != null && software != null &&
      check.indexOf("MetaMorph") != -1;
  }

  private void parseCommentImageJ(String comment)
    throws FormatException, IOException
  {
    int nl = comment.indexOf("\n");
    put("ImageJ", nl < 0 ? comment.substring(7) : comment.substring(7, nl));
    metadata.remove("Comment");

    int z = 1, t = 1;
    int c = getSizeC();

    // parse ZCT sizes
    StringTokenizer st = new StringTokenizer(comment, "\n");
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      int value = 0;
      int eq = token.indexOf("=");
      if (eq != -1 && eq + 1 < token.length()) {
        try {
          value = Integer.parseInt(token.substring(eq + 1));
        }
        catch (NumberFormatException e) {
          if (debug) trace(e);
        }
      }

      if (token.startsWith("channels=")) c = value;
      else if (token.startsWith("slices=")) z = value;
      else if (token.startsWith("frames=")) t = value;
    }
    if (z * c * t == c) {
      t = getImageCount();
    }
    core[0].dimensionOrder = "XYCZT";

    if (z * t * (isRGB() ? 1 : c) == ifds.size()) {
      core[0].sizeZ = z;
      core[0].sizeT = t;
      core[0].sizeC = c;
    }
    else if (ifds.size() == 1 && z * t > ifds.size() &&
      TiffTools.getCompression(ifds.get(0)) == TiffTools.UNCOMPRESSED)
    {
      // file is likely corrupt (missing end IFDs)
      //
      // ImageJ writes TIFF files like this:
      // IFD #0
      // comment
      // all pixel data
      // IFD #1
      // IFD #2
      // ...
      //
      // since we know where the pixel data is, we can create fake
      // IFDs in an attempt to read the rest of the pixels

      IFD firstIFD = ifds.get(0);

      int planeSize = getSizeX() * getSizeY() * getRGBChannelCount() *
        FormatTools.getBytesPerPixel(getPixelType());
      long[] stripOffsets = TiffTools.getStripOffsets(firstIFD);
      long[] stripByteCounts = TiffTools.getStripByteCounts(firstIFD);

      long endOfFirstPlane = stripOffsets[stripOffsets.length - 1] +
        stripByteCounts[stripByteCounts.length - 1];
      long totalBytes = in.length() - endOfFirstPlane;
      int totalPlanes = (int) (totalBytes / planeSize) + 1;

      ifds = new IFDList();
      ifds.ensureCapacity(totalPlanes);
      ifds.set(0, firstIFD);
      for (int i=1; i<totalPlanes; i++) {
        IFD ifd = new IFD(firstIFD);
        ifds.set(i, ifd);
        long[] prevOffsets = TiffTools.getStripOffsets(ifds.get(i - 1));
        long[] offsets = new long[stripOffsets.length];
        offsets[0] = prevOffsets[prevOffsets.length - 1] +
          stripByteCounts[stripByteCounts.length - 1];
        for (int j=1; j<offsets.length; j++) {
          offsets[j] = offsets[j - 1] + stripByteCounts[j - 1];
        }
        ifd.put(new Integer(TiffTools.STRIP_OFFSETS), offsets);
      }

      if (z * c * t == ifds.size()) {
        core[0].sizeZ = z;
        core[0].sizeT = t;
        core[0].sizeC = c;
      }
      else core[0].sizeZ = ifds.size();
      core[0].imageCount = ifds.size();
    }
    else {
      core[0].sizeT = ifds.size();
      core[0].imageCount = ifds.size();
    }
  }

  private void parseCommentMetamorph(String comment) {
    // parse key/value pairs
    StringTokenizer st = new StringTokenizer(comment, "\n");
    while (st.hasMoreTokens()) {
      String line = st.nextToken();
      int colon = line.indexOf(":");
      if (colon < 0) {
        addGlobalMeta("Comment", line);
        continue;
      }
      String key = line.substring(0, colon);
      String value = line.substring(colon + 1);
      addGlobalMeta(key, value);
    }
  }

  private void parseCommentGeneric(String comment) {
    if (comment == null) return;
    String[] lines = comment.split("\n");
    if (lines.length > 1) {
      comment = "";
      for (String line : lines) {
        int eq = line.indexOf("=");
        if (eq != -1) {
          String key = line.substring(0, eq).trim();
          String value = line.substring(eq + 1).trim();
          addGlobalMeta(key, value);
        }
        else if (!line.startsWith("[")) {
          comment += line + "\n";
        }
      }
      addGlobalMeta("Comment", comment);
    }
  }

}
