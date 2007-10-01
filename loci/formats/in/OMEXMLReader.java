//
// OMEXMLReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

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

import java.io.*;
import java.util.*;
import java.util.zip.*;
import loci.formats.*;
import loci.formats.codec.Base64Codec;
import loci.formats.codec.CBZip2InputStream;

/**
 * OMEXMLReader is the file format reader for OME-XML files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/OMEXMLReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/OMEXMLReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class OMEXMLReader extends FormatReader {

  // -- Constants --

  private static final String NO_OME_JAVA_MSG =
    "The Java OME-XML library is required to read OME-XML files. Please " +
    "obtain ome-java.jar from http://loci.wisc.edu/ome/formats.html";

  // -- Static fields --

  private static boolean noOME = false;

  static {
    try {
      Class.forName("org.openmicroscopy.xml.OMENode");
    }
    catch (Throwable t) {
      noOME = true;
      if (debug) LogTools.trace(t);
    }
  }

  // -- Fields --

  /** Number of bits per pixel. */
  protected int[] bpp;

  /** Offset to each plane's data. */
  protected Vector[] offsets;

  /** String indicating the compression type. */
  protected String[] compression;

  // -- Constructor --

  /** Constructs a new OME-XML reader. */
  public OMEXMLReader() { super("OME-XML", "ome"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return new String(block, 0, 5).equals("<?xml");
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    in.seek(((Integer) offsets[series].get(no)).intValue());

    byte[] b;
    if (no < getImageCount() - 1) {
      b = new byte[((Integer) offsets[series].get(no + 1)).intValue() -
        ((Integer) offsets[series].get(no)).intValue()];
    }
    else {
      b = new byte[(int) (in.length() -
        ((Integer) offsets[series].get(no)).intValue())];
    }
    in.read(b);
    String data = new String(b);
    b = null;

    // retrieve the compressed pixel data

    int dataStart = data.indexOf(">") + 1;
    String pix = data.substring(dataStart);
    if (pix.indexOf("<") > 0) {
      pix = pix.substring(0, pix.indexOf("<"));
    }
    data = null;

    Base64Codec e = new Base64Codec();
    byte[] pixels = e.base64Decode(pix);
    pix = null;

    if (compression[series].equals("bzip2")) {
      byte[] tempPixels = pixels;
      pixels = new byte[tempPixels.length - 2];
      System.arraycopy(tempPixels, 2, pixels, 0, pixels.length);

      ByteArrayInputStream bais = new ByteArrayInputStream(pixels);
      CBZip2InputStream bzip = new CBZip2InputStream(bais);
      pixels = new byte[core.sizeX[series] * core.sizeY[series] * bpp[series]];
      for (int i=0; i<pixels.length; i++) {
        pixels[i] = (byte) bzip.read();
      }
      tempPixels = null;
      bais.close();
      bais = null;
      bzip = null;
    }
    else if (compression[series].equals("zlib")) {
      try {
        Inflater decompressor = new Inflater();
        decompressor.setInput(pixels, 0, pixels.length);
        pixels = new byte[core.sizeX[series]*core.sizeY[series]*bpp[series]];
        decompressor.inflate(pixels);
        decompressor.end();
      }
      catch (DataFormatException dfe) {
        throw new FormatException("Error uncompressing zlib data.");
      }
    }
    buf = pixels;
    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("OMEXMLReader.initFile(" + id + ")");
    if (noOME) throw new FormatException(NO_OME_JAVA_MSG);
    super.initFile(id);

    in = new RandomAccessStream(id);
    ReflectedUniverse r = new ReflectedUniverse();
    try {
      r.exec("import loci.formats.ome.OMEXMLMetadata");
      r.exec("import org.openmicroscopy.xml.OMENode");
      r.exec("omexmlMeta = new OMEXMLMetadata()");
    }
    catch (ReflectException exc) {
      throw new FormatException(exc);
    }

    r.setVar("ome", null);
    try {
      File f = new File(Location.getMappedId(id));
      f = f.getAbsoluteFile();
      String path = f.getPath().toLowerCase();
      if (f.exists() && path.endsWith(".ome")) {
        r.setVar("f", f);
        r.exec("ome = new OMENode(f)");
      }
      else {
        byte[] b = new byte[(int) in.length()];
        long oldFp = in.getFilePointer();
        in.seek(0);
        in.read(b);
        in.seek(oldFp);
        r.setVar("s", new String(b));
        r.exec("ome = new OMENode(s)");
        b = null;
      }
    }
    catch (ReflectException exc) {
      throw new FormatException(exc);
    }
    try {
      r.exec("omexmlMeta.setRoot(ome)");
    }
    catch (ReflectException exc) {
      throw new FormatException(exc);
    }

    status("Determining endianness");

    in.skipBytes(200);

    int numDatasets = 0;
    Vector endianness = new Vector();
    Vector bigEndianPos = new Vector();

    byte[] buf = new byte[1];

    while (in.getFilePointer() < in.length()) {
      // read a block of 8192 characters, looking for the "BigEndian" pattern
      buf = new byte[8192];
      boolean found = false;
      while (!found) {
        if (in.getFilePointer() < in.length()) {
          int read = in.read(buf, 9, 8183);
          String test = new String(buf);

          int ndx = test.indexOf("BigEndian");
          if (ndx != -1) {
            found = true;
            String endian = test.substring(ndx + 11).trim();
            if (endian.startsWith("\"")) endian = endian.substring(1);
            endianness.add(new Boolean(!endian.toLowerCase().startsWith("t")));
            bigEndianPos.add(new Long(in.getFilePointer() - read - 9 + ndx));
            numDatasets++;
          }
        }
        else if (numDatasets == 0) {
          throw new FormatException("Pixel data not found.");
        }
        else found = true;
      }
    }

    offsets = new Vector[numDatasets];

    for (int i=0; i<numDatasets; i++) {
      offsets[i] = new Vector();
    }

    status("Finding image offsets");

    // look for the first BinData element in each series

    for (int i=0; i<numDatasets; i++) {
      in.seek(((Long) bigEndianPos.get(i)).longValue());
      boolean found = false;
      buf = new byte[8192];
      in.read(buf, 0, 14);

      while (!found) {
        if (in.getFilePointer() < in.length()) {
          int numRead = in.read(buf, 14, 8192-14);

          String test = new String(buf);

          int ndx = test.indexOf("<Bin");
          if (ndx == -1) {
            byte[] b = buf;
            System.arraycopy(b, 8192 - 15, buf, 0, 14);
          }
          else {
            while (!((ndx != -1) && (ndx != test.indexOf("<Bin:External")) &&
              (ndx != test.indexOf("<Bin:BinaryFile"))))
            {
              ndx = test.indexOf("<Bin", ndx+1);
            }
            found = true;
            numRead += 14;
            offsets[i].add(new Integer(
              (int) in.getFilePointer() - (numRead - ndx)));
          }
          test = null;
        }
        else {
          throw new FormatException("Pixel data not found");
        }
      }
    }

    in.seek(0);

    for (int i=0; i<numDatasets; i++) {
      if (i == 0) {
        buf = new byte[((Integer) offsets[i].get(0)).intValue()];
      }
      else {
        // look for the next Image element

        boolean found = false;
        buf = new byte[8192];
        in.read(buf, 0, 14);
        while (!found) {
          if (in.getFilePointer() < in.length()) {
            in.read(buf, 14, 8192-14);

            String test = new String(buf);

            int ndx = test.indexOf("<Image ");
            if (ndx == -1) {
              byte[] b = buf;
              System.arraycopy(b, 8192 - 15, buf, 0, 14);
              b = null;
            }
            else {
              found = true;
              in.seek(in.getFilePointer() - (8192 - ndx));
            }
            test = null;
          }
          else {
            throw new FormatException("Pixel data not found");
          }
        }

        int bufSize = (int) (((Long) offsets[i].get(0)).longValue() -
          in.getFilePointer());
        buf = new byte[bufSize];
      }
      in.read(buf);
    }
    buf = null;

    status("Populating metadata");

    core = new CoreMetadata(numDatasets);

    bpp = new int[numDatasets];
    compression = new String[numDatasets];

    int oldSeries = getSeries();

    try {
      r.exec("omexmlMeta.setRoot(ome)");
    }
    catch (ReflectException exc) {
      throw new FormatException(exc);
    }
    for (int i=0; i<numDatasets; i++) {
      setSeries(i);

      core.littleEndian[i] = ((Boolean) endianness.get(i)).booleanValue();

      Integer w = null, h = null, t = null, z = null, c = null;
      String pixType = null;
      try {
        r.setVar("ndx", i);
        w = (Integer) r.exec("omexmlMeta.getSizeX(ndx)");
        h = (Integer) r.exec("omexmlMeta.getSizeY(ndx)");
        t = (Integer) r.exec("omexmlMeta.getSizeT(ndx)");
        z = (Integer) r.exec("omexmlMeta.getSizeZ(ndx)");
        c = (Integer) r.exec("omexmlMeta.getSizeC(ndx)");
        pixType = (String) r.exec("omexmlMeta.getPixelType(ndx)");
        core.currentOrder[i] =
          (String) r.exec("omexmlMeta.getDimensionOrder(ndx)");
      }
      catch (ReflectException exc) {
        throw new FormatException(exc);
      }
      core.sizeX[i] = w.intValue();
      core.sizeY[i] = h.intValue();
      core.sizeT[i] = t.intValue();
      core.sizeZ[i] = z.intValue();
      core.sizeC[i] = c.intValue();
      core.rgb[i] = false;
      core.interleaved[i] = false;
      core.indexed[i] = false;
      core.falseColor[i] = false;

      String type = pixType.toLowerCase();
      if (type.endsWith("16")) {
        bpp[i] = 2;
        core.pixelType[i] = FormatTools.UINT16;
      }
      else if (type.endsWith("32")) {
        bpp[i] = 4;
        core.pixelType[i] = FormatTools.UINT32;
      }
      else if (type.equals("float")) {
        bpp[i] = 4;
        core.pixelType[i] = FormatTools.FLOAT;
      }
      else {
        bpp[i] = 1;
        core.pixelType[i] = FormatTools.UINT8;
      }

      // calculate the number of raw bytes of pixel data that we are expecting
      int expected = core.sizeX[i] * core.sizeY[i] * bpp[i];

      // find the compression type and adjust 'expected' accordingly
      in.seek(((Integer) offsets[i].get(0)).intValue());
      buf = new byte[256];
      in.read(buf);
      String data = new String(buf);

      int compressionStart = data.indexOf("Compression") + 13;
      int compressionEnd = data.indexOf("\"", compressionStart);
      if (compressionStart != -1 && compressionEnd != -1) {
        compression[i] = data.substring(compressionStart, compressionEnd);
      }
      else compression[i] = "none";

      expected /= 2;

      in.seek(((Integer) offsets[i].get(0)).intValue());

      int planes = core.sizeZ[i] * core.sizeC[i] * core.sizeT[i];

      searchForData(expected, planes);
      core.imageCount[i] = offsets[i].size();
      if (core.imageCount[i] < planes) {
        // hope this doesn't happen too often
        in.seek(((Integer) offsets[i].get(0)).intValue());
        searchForData(0, planes);
        core.imageCount[i] = offsets[i].size();
      }
      buf = null;
    }
    setSeries(oldSeries);
    Arrays.fill(core.orderCertain, true);

    // populate assigned metadata store with the
    // contents of the internal OME-XML metadata object
    MetadataStore store = getMetadataStore();

    MetadataRetrieve omexmlMeta = null;
    try {
      omexmlMeta = (MetadataRetrieve) r.getVar("omexmlMeta");
    }
    catch (ReflectException e) {
      if (debug) LogTools.trace(e);
    }

    String xml = MetadataTools.getOMEXML(omexmlMeta);
    MetadataTools.convertMetadata(xml, store);
  }

  // -- Helper methods --

  /** Searches for BinData elements, skipping 'safe' bytes in between. */
  private void searchForData(int safe, int numPlanes) throws IOException {
    int iteration = 0;
    boolean found = false;
    if (offsets[series].size() > 1) {
      Object zeroth = offsets[series].get(0);
      offsets[series].clear();
      offsets[series].add(zeroth);
    }

    in.skipBytes(1);
    while (((in.getFilePointer() + safe) < in.length()) &&
      (offsets[series].size() < numPlanes))
    {
      in.skipBytes(safe);

      // look for next BinData element
      found = false;
      byte[] buf = new byte[8192];
      while (!found) {
        if (in.getFilePointer() < in.length()) {
          int numRead = in.read(buf, 20, buf.length - 20);
          String test = new String(buf);

          // datasets with small planes could have multiple sets of pixel data
          // in this block
          int ndx = test.indexOf("<Bin");
          while (ndx != -1) {
            found = true;
            if (numRead == buf.length - 20) numRead = buf.length;
            offsets[series].add(new Integer(
              (int) in.getFilePointer() - (numRead - ndx)));
            ndx = test.indexOf("<Bin", ndx+1);
          }
          test = null;
        }
        else {
          found = true;
        }
      }
      buf = null;

      iteration++;
    }
  }

}
