//
// IFormatReaderI.java
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
import java.io.IOException;
import loci.formats.FormatException;
import loci.formats.ImageReader;

/**
 * Server-side Ice wrapper for client/server
 * {@link loci.formats.IFormatReader} objects.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats-ice/src/loci/ice/formats/IFormatReaderI.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats-ice/src/loci/ice/formats/IFormatReaderI.java">SVN</a></dd></dl>
 *
 * @author Hidayath Ansari mansari at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class IFormatReaderI extends _IFormatReaderDisp {

  // -- Fields --

  private ImageReader reader;

  // -- Constructor --

  public IFormatReaderI() {
    reader = new ImageReader();
  }

  // -- IFormatReader methods --

  public void setId(String id, Current current) {
    try {
      reader.setId(id);
    }
    catch (FormatException exc) {
      exc.printStackTrace();
    }
    catch (IOException exc) {
      exc.printStackTrace();
    }
  }

  public void setRetrieveAsStore(MetadataRetrievePrx retrievePrx,
    Current current)
  {
    loci.formats.meta.MetadataStore s = (loci.formats.meta.MetadataStore)
      ((MetadataRetrieveI) retrievePrx.getServant()).getWrappedObject();
    reader.setMetadataStore(s);
  }

  public boolean isThisType(String name, boolean open, Current current) {
    return reader.isThisType(name, open);
  }

  public int getImageCount(Current current) {
    return reader.getImageCount();
  }

  public boolean isRGB(Current current) {
    return reader.isRGB();
  }

  public int getSizeX(Current current) {
    return reader.getSizeX();
  }

  public int getSizeY(Current current) {
    return reader.getSizeY();
  }

  public int getSizeZ(Current current) {
    return reader.getSizeZ();
  }

  public int getSizeC(Current current) {
    return reader.getSizeC();
  }

  public int getSizeT(Current current) {
    return reader.getSizeT();
  }

  public int getPixelType(Current current) {
    return reader.getPixelType();
  }

  public int getEffectiveSizeC(Current current) {
    return reader.getEffectiveSizeC();
  }

  public int getRGBChannelCount(Current current) {
    return reader.getRGBChannelCount();
  }

  public boolean isIndexed(Current current) {
    return reader.isIndexed();
  }

  public boolean isFalseColor(Current current) {
    return reader.isFalseColor();
  }

  public byte[][] get8BitLookupTable(Current current) {
    try {
      return reader.get8BitLookupTable();
    }
    catch (FormatException exc) {
      exc.printStackTrace();
    }
    catch (IOException exc) {
      exc.printStackTrace();
    }
    return null;
  }

  public short[][] get16BitLookupTable(Current current) {
    try {
      return reader.get16BitLookupTable();
    }
    catch (FormatException exc) {
      exc.printStackTrace();
    }
    catch (IOException exc) {
      exc.printStackTrace();
    }
    return null;
  }

  public int[] getChannelDimLengths(Current current) {
    return reader.getChannelDimLengths();
  }

  public String[] getChannelDimTypes(Current current) {
    return reader.getChannelDimTypes();
  }

  public int getThumbSizeX(Current current) {
    return reader.getThumbSizeX();
  }

  public int getThumbSizeY(Current current) {
    return reader.getThumbSizeY();
  }

  public boolean isLittleEndian(Current current) {
    return reader.isLittleEndian();
  }

  public String getDimensionOrder(Current current) {
    return reader.getDimensionOrder();
  }

  public boolean isOrderCertain(Current current) {
    return reader.isOrderCertain();
  }

  public boolean isInterleaved(Current current) {
    return reader.isInterleaved();
  }

  public boolean isInterleavedSubC(int subC, Current current) {
    return reader.isInterleaved(subC);
  }

  public byte[] openBytes(int no, int x, int y, int width, int height,
    Current current)
  {
    try {
      return reader.openBytes(no, x, y, width, height);
    }
    catch (FormatException exc) {
      exc.printStackTrace();
    }
    catch (IOException exc) {
      exc.printStackTrace();
    }
    return null;
  }

  public byte[] openThumbBytes(int no, Current current) {
    try {
      return reader.openThumbBytes(no);
    }
    catch (FormatException exc) {
      exc.printStackTrace();
    }
    catch (IOException exc) {
      exc.printStackTrace();
    }
    return null;
  }

  public void close(boolean fileOnly, Current current) {
    try {
      reader.close(fileOnly);
    }
    catch (IOException exc) {
      exc.printStackTrace();
    }
  }

  public int getSeriesCount(Current current) {
    return reader.getSeriesCount();
  }

  public void setSeries(int no, Current current) {
    reader.setSeries(no);
  }

  public int getSeries(Current current) {
    return reader.getSeries();
  }

  public void setNormalized(boolean normalize, Current current) {
    reader.setNormalized(normalize);
  }

  public boolean isNormalized(Current current) {
    return reader.isNormalized();
  }

  public void setMetadataCollected(boolean collect, Current current) {
    reader.setMetadataCollected(collect);
  }

  public boolean isMetadataCollected(Current current) {
    return reader.isMetadataCollected();
  }

  public void setOriginalMetadataPopulated(boolean populate,
    Current current)
  {
    reader.setOriginalMetadataPopulated(populate);
  }

  public boolean isOriginalMetadataPopulated(Current current) {
    return reader.isOriginalMetadataPopulated();
  }

  public void setGroupFiles(boolean group, Current current) {
    reader.setGroupFiles(group);
  }

  public boolean isGroupFiles(Current current) {
    return reader.isGroupFiles();
  }

  public boolean isMetadataComplete(Current current) {
    return reader.isMetadataComplete();
  }

  public int fileGroupOption(String id, Current current) {
    try {
      return reader.fileGroupOption(id);
    }
    catch (FormatException exc) {
      exc.printStackTrace();
    }
    catch (IOException exc) {
      exc.printStackTrace();
    }
    return -1;
  }

  public String[] getUsedFiles(Current current) {
    return reader.getUsedFiles();
  }

  public String getCurrentFile(Current current) {
    return reader.getCurrentFile();
  }

  public int getIndex(int z, int c, int t, Current current) {
    return reader.getIndex(z, c, t);
  }

  public int[] getZCTCoords(int index, Current current) {
    return reader.getZCTCoords(index);
  }

  public void setMetadataFiltered(boolean filter, Current current) {
    reader.setMetadataFiltered(filter);
  }

  public boolean isMetadataFiltered(Current current) {
    return reader.isMetadataFiltered();
  }

  public void setMetadataStore(MetadataStorePrx store, Current current) {
    reader.setMetadataStore(((MetadataStoreI)
      store.getServant()).getWrappedObject());
  }

  public void close(Current current) {
    try {
      reader.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void closeFile(boolean fileOnly, Current current) {
    try {
      reader.close(fileOnly);
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public String getFormat(Current current) {
    return reader.getFormat();
  }

  public String[] getSuffixes(Current current) {
    return reader.getSuffixes();
  }

}
