//
// IFormatWriterI.java
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

package loci.ice.formats;

import Ice.Current;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.io.IOException;
import loci.formats.FormatException;
import loci.formats.ImageWriter;

/**
 * Server-side Ice wrapper for client/server
 * {@link loci.formats.IFormatWriter} objects.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats-ice/src/loci/ice/formats/IFormatWriterI.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats-ice/src/loci/ice/formats/IFormatWriterI.java">SVN</a></dd></dl>
 *
 * @author Hidayath Ansari mansari at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class IFormatWriterI extends _IFormatWriterDisp {

  // -- Fields --

  private ImageWriter writer;

  // -- Constructor --

  public IFormatWriterI() {
    writer = new ImageWriter();
  }

  // -- IFormatWriterI methods --

  public void setMetadataRetrieve(loci.formats.meta.MetadataRetrieve r) {
    writer.setMetadataRetrieve(r);
  }

  // -- IFormatWriter methods --

  public void setId(String id, Current current) {
    try {
      writer.setId(id);
    }
    catch (FormatException exc) {
      exc.printStackTrace();
    }
    catch (IOException exc) {
      exc.printStackTrace();
    }
  }

  public void saveImage(Image image, boolean last, Current current)
    throws FormatException, IOException
  {
    writer.saveImage(image, last);
  }

  public void saveImage(Image image, int series, boolean lastInSeries,
    boolean last, Current current) throws FormatException, IOException
  {
    writer.saveImage(image, series, lastInSeries, last);
  }

  public void saveBytes1(byte[] bytes, boolean last, Current current) {
    try {
      writer.saveBytes(bytes, last);
    }
    catch (FormatException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void saveBytes2(byte[] bytes, int series, boolean lastInSeries,
    boolean last, Current current)
  {
    try {
      writer.saveBytes(bytes, series, lastInSeries, last);
    }
    catch (FormatException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  public boolean canDoStacks(Current current) {
    return writer.canDoStacks();
  }

  public void setMetadataRetrieve(IMetadataPrx r, Current current) {
    // CTR TODO
    //writer.setMetadataRetrieve(r);
  }

  public IMetadata getMetadataRetrieve(Current current)
  {
    // CTR TODO
    //return writer.getMetadataRetrieve();
    return null;
  }

  public void setColorModel(ColorModel cm, Current current) {
    writer.setColorModel(cm);
  }

  public ColorModel getColorModel(Current current) {
    return writer.getColorModel();
  }

  public void setFramesPerSecond(int rate, Current current) {
    writer.setFramesPerSecond(rate);
  }

  public int getFramesPerSecond(Current current) {
    return writer.getFramesPerSecond();
  }

  public String[] getCompressionTypes(Current current) {
    return writer.getCompressionTypes();
  }

  public int[] getPixelTypes(Current current) {
    return writer.getPixelTypes();
  }

  public boolean isSupportedType(int type, Current current) {
    return writer.isSupportedType(type);
  }

  public void setCompression(String compress, Current current) {
    try {
      writer.setCompression(compress);
    }
    catch (FormatException e) {
      e.printStackTrace();
    }
  }

  public void close(Current current) {
    try {
      writer.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getFormat(Current current) {
    return writer.getFormat();
  }

  public String[] getSuffixes(Current current) {
    return writer.getSuffixes();
  }

  public boolean isThisType(String name, Current current) {
    return writer.isThisType(name);
  }

}
