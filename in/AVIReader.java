//
// AVIReader.java
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

package loci.formats.in;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Vector;
import loci.formats.*;

/**
 * AVIReader is the file format reader for uncompressed AVI files.
 *
 * Much of this form's code was adapted from Wayne Rasband's AVI Movie Reader
 * plugin for ImageJ (available at http://rsb.info.nih.gov/ij).
 */
public class AVIReader extends FormatReader {

  // -- Fields --

  /** Current file. */
  protected RandomAccessStream in;

  /** Number of images in current AVI movie. */
  private int numImages;

  /** Offset to each plane. */
  private Vector offsets;

  /** Endianness flag. */
  private boolean little = true;

  String type = "error";
  String fcc = "error";
  int size = -1;
  int bigChunkSize;
  long pos;

  // AVI Header chunk fields

  int dwMicroSecPerFrame, dwMaxBytesPerSec, dwReserved1, dwFlags,
    dwTotalFrames, dwInitialFrames, dwStreams, dwSuggestedBufferSize, dwWidth,
    dwHeight, dwScale, dwRate, dwStart, dwLength;

  // Stream Header chunk fields

  String fccStreamType, fccStreamHandler;
  int dwStreamFlags, dwStreamReserved1, dwStreamInitialFrames, dwStreamScale,
    dwStreamRate, dwStreamStart, dwStreamLength, dwStreamSuggestedBufferSize,
    dwStreamQuality, dwStreamSampleSize;

  // Stream Format chunk fields

  int bmpSize, bmpSizeOfBitmap, bmpHorzResolution, bmpVertResolution,
    bmpColorsUsed, bmpColorsImportant, bmpNoOfPixels, bmpWidth, bmpHeight,
    bmpCompression, bmpActualSize, bmpScanLineSize, bmpActualColorsUsed;
  short bmpPlanes, bmpBitsPerPixel;
  boolean bmpTopDown;
  byte[] rawData = null;
  byte[] pr = null;
  byte[] pg = null;
  byte[] pb = null;


  // -- Constructor --

  /** Constructs a new AVI reader. */
  public AVIReader() { super("Audio Video Interleave", "avi"); }


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for an AVI file. */
  public boolean isThisType(byte[] block) {
    return false;
  }

  /** Determines the number of images in the given AVI file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return (!isRGB(id) || !separated) ? numImages :
      (bmpBitsPerPixel / 8) * numImages;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return (bmpBitsPerPixel > 8);
  }

  /** Get the size of the X dimension. */
  public int getSizeX(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return dwWidth;
  }

  /** Get the size of the Y dimension. */
  public int getSizeY(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return bmpHeight;
  }

  /** Get the size of the Z dimension. */
  public int getSizeZ(String id) throws FormatException, IOException {
    return 1;
  }

  /** Get the size of the C dimension. */
  public int getSizeC(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return isRGB(id) ? 3 : 1;
  }

  /** Get the size of the T dimension. */
  public int getSizeT(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numImages;
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return little;
  }

  /**
   * Return a five-character string representing the dimension order
   * within the file.
   */
  public String getDimensionOrder(String id) throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return getSizeC(id) == 3 ? "XYCTZ" : "XYTCZ";
  }

  /** Obtains the specified image from the given AVI file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    int c = (isRGB(id) && separated) ? 3 : 1;
    long fileOff = ((Long) offsets.get(no / c)).longValue();
    in.seek((int) fileOff);

    int len = bmpScanLineSize;

    int pad = bmpScanLineSize - dwWidth*(bmpBitsPerPixel / 8);
    rawData = new byte[bmpActualSize];
    int rawOffset = rawData.length - len;
    int offset = 0;

    for (int i=bmpHeight - 1; i>=0; i--) {
      if (bmpBitsPerPixel == 8) {
        in.read(rawData, rawOffset, len);
      }
      else {
        byte b, g, r;
        for (int j=0; j<dwWidth; j++) {
          b = (byte) in.read();
          g = (byte) in.read();
          r = (byte) in.read();
          rawData[rawOffset + j*3] = r;
          rawData[rawOffset + j*3 + 1] = g;
          rawData[rawOffset + j*3 + 2] = b;
        }
      }

      rawOffset -= (len - pad);
      offset += dwWidth;
    }

    if (separated) {
      byte[] rtn = new byte[rawData.length / 3];
      int j = 0;
      for (int i=(no%3); i<rawData.length; i+=3) {
        rtn[j] = rawData[i];
        j++;
      }
      return rtn;
    }
    else {
      if (rawData.length == dwWidth * bmpHeight * (bmpBitsPerPixel / 8)) {
        return rawData;
      }
      else {
        byte[] t = new byte[dwWidth * bmpHeight * (bmpBitsPerPixel / 8)];
        System.arraycopy(rawData, 0, t, 0, t.length);
        return t;
      }
    }
  }

  /** Obtains the specified image from the given AVI file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    return ImageTools.makeImage(openBytes(id, no), dwWidth, bmpHeight,
      (!isRGB(id) || separated) ? 1 : 3, true);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given AVI file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessStream(id);
    in.order(true);

    offsets = new Vector();

    byte[] list = new byte[4];
    String listString;

    type = readStringBytes();
    size = in.readInt();
    fcc = readStringBytes();

    if (type.equals("RIFF")) {
      bigChunkSize = size;
      if (!fcc.equals("AVI ")) {
        whine("Sorry, AVI RIFF format not found.");
      }
    }
    else {
      whine("Not an AVI file");
    }

    pos = in.getFilePointer();
    long spos = pos;

    while ((in.length() - in.getFilePointer()) > 4) {
      in.read(list);
      in.seek((int) pos);
      listString = new String(list);

      if (listString.equals("JUNK")) {
        type = readStringBytes();
        size = in.readInt();

        if (type.equals("JUNK")) {
          in.skipBytes(size);
        }
      }
      else if (listString.equals("LIST")) {
        spos = in.getFilePointer();
        type = readStringBytes();
        size = in.readInt();
        fcc = readStringBytes();

        in.seek((int) spos);
        if (fcc.equals("hdrl")) {
          type = readStringBytes();
          size = in.readInt();
          fcc = readStringBytes();

          if (type.equals("LIST")) {
            if (fcc.equals("hdrl")) {
              type = readStringBytes();
              size = in.readInt();
              if (type.equals("avih")) {
                spos = in.getFilePointer();

                dwMicroSecPerFrame = in.readInt();
                dwMaxBytesPerSec = in.readInt();
                dwReserved1 = in.readInt();
                dwFlags = in.readInt();
                dwTotalFrames = in.readInt();
                dwInitialFrames = in.readInt();
                dwStreams = in.readInt();
                dwSuggestedBufferSize = in.readInt();
                dwWidth = in.readInt();
                dwHeight = in.readInt();
                dwScale= in.readInt();
                dwRate = in.readInt();
                dwStart = in.readInt();
                dwLength = in.readInt();

                metadata.put("Microseconds per frame",
                  new Integer(dwMicroSecPerFrame));
                metadata.put("Max. bytes per second",
                  new Integer(dwMaxBytesPerSec));
                metadata.put("Total frames", new Integer(dwTotalFrames));
                metadata.put("Initial frames", new Integer(dwInitialFrames));
                metadata.put("Frame width", new Integer(dwWidth));
                metadata.put("Frame height", new Integer(dwHeight));
                metadata.put("Scale factor", new Integer(dwScale));
                metadata.put("Frame rate", new Integer(dwRate));
                metadata.put("Start time", new Integer(dwStart));
                metadata.put("Length", new Integer(dwLength));

                try {
                  in.seek((int) (spos + size));
                }
                catch (Exception e) { }
              }
            }
          }
        }
        else if (fcc.equals("strl")) {
          long startPos = in.getFilePointer();
          long streamSize = size;

          type = readStringBytes();
          size = in.readInt();
          fcc = readStringBytes();

          if (type.equals("LIST")) {
            if (fcc.equals("strl")) {
              type = readStringBytes();
              size = in.readInt();

              if (type.equals("strh")) {
                spos = in.getFilePointer();

                String fccStreamTypeOld = fccStreamType;
                fccStreamType = readStringBytes();
                if (!fccStreamType.equals("vids")) {
                  fccStreamType = fccStreamTypeOld;
                }

                fccStreamHandler = readStringBytes();

                dwStreamFlags = in.readInt();
                dwStreamReserved1 = in.readInt();
                dwStreamInitialFrames = in.readInt();
                dwStreamScale = in.readInt();
                dwStreamRate = in.readInt();
                dwStreamStart = in.readInt();
                dwStreamLength = in.readInt();
                dwStreamSuggestedBufferSize = in.readInt();
                dwStreamQuality = in.readInt();
                dwStreamSampleSize = in.readInt();

                metadata.put("Stream quality", new Integer(dwStreamQuality));
                metadata.put("Stream sample size",
                  new Integer(dwStreamSampleSize));

                try {
                  in.seek((int) (spos + size));
                }
                catch (Exception e) { }
              }

              type = readStringBytes();
              size = in.readInt();
              if (type.equals("strf")) {
                spos = in.getFilePointer();

                bmpSize = in.readInt();
                bmpWidth = in.readInt();
                bmpHeight = in.readInt();
                bmpPlanes = in.readShort();
                bmpBitsPerPixel = in.readShort();
                bmpCompression = in.readInt();
                bmpSizeOfBitmap = in.readInt();
                bmpHorzResolution = in.readInt();
                bmpVertResolution = in.readInt();
                bmpColorsUsed = in.readInt();
                bmpColorsImportant = in.readInt();

                bmpTopDown = (bmpHeight < 0);
                bmpNoOfPixels = bmpWidth * bmpHeight;

                metadata.put("Bitmap compression value",
                  new Integer(bmpCompression));
                metadata.put("Horizontal resolution",
                  new Integer(bmpHorzResolution));
                metadata.put("Vertical resolution",
                  new Integer(bmpVertResolution));
                metadata.put("Number of colors used",
                  new Integer(bmpColorsUsed));
                metadata.put("Bits per pixel", new Integer(bmpBitsPerPixel));

                // scan line is padded with zeros to be a multiple of 4 bytes
                int npad = bmpWidth % 4;
                if (npad > 0) npad = 4 - npad;

                bmpScanLineSize = (bmpWidth + npad) * (bmpBitsPerPixel / 8);

                if (bmpSizeOfBitmap != 0) {
                  bmpActualSize = bmpSizeOfBitmap;
                }
                else {
                  // a value of 0 doesn't mean 0 -- it means we have
                  // to calculate it
                  bmpActualSize = bmpScanLineSize * bmpHeight;
                }

                if (bmpColorsUsed != 0) {
                  bmpActualColorsUsed = bmpColorsUsed;
                }
                else {
                  // a value of 0 means we determine this based on the
                  // bits per pixel
                  if (bmpBitsPerPixel < 16) {
                    bmpActualColorsUsed = 1 << bmpBitsPerPixel;
                  }
                  else {
                    // no palette
                    bmpActualColorsUsed = 0;
                  }
                }

                if (bmpCompression != 0) {
                  whine("Sorry, compressed AVI files not supported.");
                }

                if (!(bmpBitsPerPixel == 8 || bmpBitsPerPixel == 24 ||
                  bmpBitsPerPixel == 32))
                {
                  whine("Sorry, " + bmpBitsPerPixel + " bits per pixel not " +
                    "supported");
                }

                if (bmpActualColorsUsed != 0) {
                  // read the palette
                  pr = new byte[bmpColorsUsed];
                  pg = new byte[bmpColorsUsed];
                  pb = new byte[bmpColorsUsed];

                  for (int i=0; i<bmpColorsUsed; i++) {
                    pb[i] = (byte) in.read();
                    pg[i] = (byte) in.read();
                    pr[i] = (byte) in.read();
                    in.read();
                  }
                }

                in.seek((int) (spos + size));
              }
            }

            spos = in.getFilePointer();
            type = readStringBytes();
            size = in.readInt();
            if (type.equals("strd")) {
              in.skipBytes(size);
            }
            else {
              in.seek(spos);
            }

            spos = in.getFilePointer();
            type = readStringBytes();
            size = in.readInt();
            if (type.equals("strn")) {
              in.skipBytes(size);
            }
            else {
              in.seek(spos);
            }
          }

          try {
            in.seek(startPos + 8 + streamSize);
          }
          catch (Exception e) { }
        }
        else if (fcc.equals("movi")) {
          type = readStringBytes();
          size = in.readInt();
          fcc = readStringBytes();

          if (type.equals("LIST")) {
            if (fcc.equals("movi")) {
              spos = in.getFilePointer();
              type = readStringBytes();
              size = in.readInt();
              fcc = readStringBytes();
              if (!(type.equals("LIST") && fcc.equals("rec "))) {
                in.seek((int) spos);
              }

              spos = in.getFilePointer();
              type = readStringBytes();
              size = in.readInt();

              while (type.substring(2).equals("db") ||
                type.substring(2).equals("dc") ||
                type.substring(2).equals("wb"))
              {
                if (type.substring(2).equals("db") ||
                  type.substring(2).equals("dc"))
                {
                  offsets.add(new Long(in.getFilePointer()));
                  in.skipBytes(bmpHeight * bmpScanLineSize);
                }

                spos = in.getFilePointer();

                type = readStringBytes();
                size = in.readInt();
                if (type.equals("JUNK")) {
                  in.skipBytes(size);
                  spos = in.getFilePointer();
                  type = readStringBytes();
                  size = in.readInt();
                }
              }
              in.seek(spos);
            }
          }
        }
        else {
          // skipping unknown block
          try {
            in.skipBytes(8 + size);
          }
          catch (IllegalArgumentException iae) { }
        }
      }
      else {
        // skipping unknown block
        type = readStringBytes();
        try {
          size = in.readInt();
          in.skipBytes(size);
        }
        catch (Exception iae) { }
      }
      pos = in.getFilePointer();
    }
    numImages = offsets.size();

    initOMEMetadata();
  }

  // -- AVIReader API methods --

  /** Initialize the OME-XML tree. */
  public void initOMEMetadata() throws FormatException, IOException {
    int bps = ((Integer) metadata.get("Bits per pixel")).intValue() / 8;
    int tempBps = bps * 8;
    String pixType = "int" + (tempBps / bps);

    String order = "XY";
    if (bps == 3) order += "CTZ";
    else order += "TCZ";

    getMetadataStore(currentId).setPixels(
      (Integer) metadata.get("Frame width"), // SizeX
      (Integer) metadata.get("Frame height"), // SizeY
      new Integer(1), // SizeZ
      new Integer(bps), // SizeC
      new Integer(numImages), // SizeT
      pixType, // PixelType
      new Boolean(!little), // BigEndian
      order, // DimensionOrder
      null); // Use index 0
  }

  /** Reads a 4-byte String. */
  public String readStringBytes() throws IOException {
    byte[] list = new byte[4];
    in.read(list);
    return new String(list);
  }

  /**
   * Throw a FormatException to apologize for the fact that
   * AVI support is suboptimal.
   */
  private void whine(String msg) throws FormatException {
    throw new FormatException(msg);
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new AVIReader().testRead(args);
  }

}
