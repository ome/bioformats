//
// LegacyND2Reader.java
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
import java.util.Arrays;
import loci.formats.*;

public class LegacyND2Reader extends FormatReader {

  private static ReflectedUniverse r = createReflectedUniverse();
  private static ReflectedUniverse createReflectedUniverse() {
    r = null;
    try {
      r = new ReflectedUniverse();
      r.exec("import ND_to_Image6D");
    }
    catch (ReflectException e) {
      LogTools.trace(e);
    }
    return r;
  }

  // -- Constructor --

  public LegacyND2Reader() { super("Nikon ND2 (Legacy)", "nd2"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return false;
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    int[] zct = FormatTools.getZCTCoords(this, no);

    r.setVar("z", zct[0]);
    r.setVar("c", zct[1]);
    r.setVar("t", zct[2]);
    r.setVar("series", series);

    if (core.pixelType[series] == FormatTools.UINT8) {
      r.setVar("pix", buf);
      try {
        r.exec("helper.copyNd2ImageByte(pix, c, z, t, series)");
      }
      catch (ReflectException e) {
        if (debug) LogTools.trace(e);
      }
    }
    else {
      int size = getRGBChannelCount() * core.sizeX[series] * core.sizeY[series];
      short[] s = new short[size];
      r.setVar("s", s);
      try {
        r.exec("helper.copyNd2ImageShort(s, c, z, t, series)");
      }
      catch (ReflectException e) {
        if (debug) LogTools.trace(e);
      }
      for (int j=0; j<s.length; j++) {
        buf[j*2] = (byte) ((s[j] >> 8) & 0xff);
        buf[j*2 + 1] = (byte) (s[j] & 0xff);
      }
    }

    if (getRGBChannelCount() > 1) {
      int b = FormatTools.getBytesPerPixel(core.pixelType[series]);
      for (int i=0; i<buf.length; i+=b*getRGBChannelCount()) {
        for (int j=0; j<b; j++) {
          byte t = buf[i + j];
          buf[i + j] = buf[i + (getRGBChannelCount() - 1)*b + j];
          buf[i + (getRGBChannelCount() - 1)*b + j] = t;
        }
      }
    }

    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    if (currentId != null) {
      try {
        r.exec("helper.closeNd2()");
      }
      catch (ReflectException e) { }
    }
    super.close();
  }

  /* @see loci.formats.IFormatHandler#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    try {
      System.loadLibrary("Nd2SdkWrapperI6D");
    }
    catch (UnsatisfiedLinkError e) {
      return false;
    }
    return true;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("LegacyND2Reader.initFile(" + id + ")");
    super.initFile(id);

    String parent =
      new Location(id).getAbsoluteFile().getParentFile().getAbsolutePath();
    String relPath = new Location(id).getPath();
    relPath = relPath.substring(relPath.lastIndexOf(File.separator) + 1);
    if (!parent.endsWith(File.separator)) parent += File.separator;
    r.setVar("parent", parent);
    r.setVar("relPath", relPath);
    try {
      r.exec("helper = new ND_to_Image6D()");
      r.exec("success = helper.openNd2(parent, relPath)");
      boolean success = ((Boolean) r.getVar("success")).booleanValue();
      if (!success) {
        throw new FormatException("Failed to open ND2 file.");
      }

      // populate core metadata

      r.setVar("name", "points");
      r.exec("seriesCount = helper.getNd2Param(name)");
      core = new CoreMetadata(((Integer) r.getVar("seriesCount")).intValue());
      r.setVar("name", "width");
      r.exec("val = helper.getNd2Param(name)");
      Arrays.fill(core.sizeX, ((Integer) r.getVar("val")).intValue());
      if (core.sizeX[0] % 2 != 0) Arrays.fill(core.sizeX, core.sizeX[0] + 1);
      r.setVar("name", "height");
      r.exec("val = helper.getNd2Param(name)");
      Arrays.fill(core.sizeY, ((Integer) r.getVar("val")).intValue());
      r.setVar("name", "zstacks");
      r.exec("val = helper.getNd2Param(name)");
      Arrays.fill(core.sizeZ, ((Integer) r.getVar("val")).intValue());
      r.setVar("name", "channels");
      r.exec("val = helper.getNd2Param(name)");
      Arrays.fill(core.sizeC, ((Integer) r.getVar("val")).intValue());
      r.setVar("name", "timeslices");
      r.exec("val = helper.getNd2Param(name)");
      Arrays.fill(core.sizeT, ((Integer) r.getVar("val")).intValue());
      r.setVar("name", "bpp");
      r.exec("val = helper.getNd2Param(name)");
      Arrays.fill(core.pixelType, ((Integer) r.getVar("val")).intValue() == 8 ?
        FormatTools.UINT8 : FormatTools.UINT16);
      r.setVar("name", "color");
      r.exec("val = helper.getNd2Param(name)");
      Arrays.fill(core.rgb, ((Integer) r.getVar("val")).intValue() > 1);
      Arrays.fill(core.indexed, false);

      if (core.rgb[0]) {
        Arrays.fill(core.sizeC, ((Integer) r.getVar("val")).intValue());
      }
    }
    catch (ReflectException e) {
      throw new FormatException(e);
    }

    Arrays.fill(core.imageCount, core.sizeZ[0] * core.sizeT[0] *
      (core.rgb[0] ? 1 : core.sizeC[0]));
    Arrays.fill(core.interleaved, true);
    Arrays.fill(core.currentOrder, "XYCZT");
    Arrays.fill(core.littleEndian, false);

    MetadataStore store = getMetadataStore();
    for (int i=0; i<core.sizeX.length; i++) {
      store.setImage("Series " + i, null, null, new Integer(i));
    }
    FormatTools.populatePixels(store, this);
    for (int i=0; i<core.sizeX.length; i++) {
      for (int j=0; j<core.sizeC[i]; j++) {
        store.setLogicalChannel(j, null, null, null, null, null, null, null,
          null, null, null, null, null, null, null, null, null, null, null,
          null, null, null, null, null, new Integer(i));
      }
    }
  }

}
