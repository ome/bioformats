//
// IFormatReaderI.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
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

//import loci.ice.formats._IFormatReaderDisp;
//import loci.ice.formats.MetadataRetrievePrx;
//import loci.ice.formats.MetadataStorePrx;
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

  public void setId(String id, Ice.Current current) {
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
    Ice.Current current)
  {
    loci.formats.meta.MetadataStore s = (loci.formats.meta.MetadataStore)
      ((MetadataRetrieveI) retrievePrx.getServant()).getWrappedObject();
    reader.setMetadataStore(s);
  }

  public boolean isThisType(String name, boolean open, Ice.Current current) {
    return reader.isThisType(name, open);
  }

  public int getImageCount(Ice.Current current) {
    return reader.getImageCount();
  }

  public boolean isRGB(Ice.Current current) {
    return reader.isRGB();
  }

  public int getSizeX(Ice.Current current) {
    return reader.getSizeX();
  }

  public int getSizeY(Ice.Current current) {
    return reader.getSizeY();
  }

  public int getSizeZ(Ice.Current current) {
    return reader.getSizeZ();
  }

  public int getSizeC(Ice.Current current) {
    return reader.getSizeC();
  }

  public int getSizeT(Ice.Current current) {
    return reader.getSizeT();
  }

  public int getPixelType(Ice.Current current) {
    return reader.getPixelType();
  }

  public int getEffectiveSizeC(Ice.Current current) {
    return reader.getEffectiveSizeC();
  }

  public int getRGBChannelCount(Ice.Current current) {
    return reader.getRGBChannelCount();
  }

  public boolean isIndexed(Ice.Current current) {
    return reader.isIndexed();
  }

  public boolean isFalseColor(Ice.Current current) {
    return reader.isFalseColor();
  }

  public byte[][] get8BitLookupTable(Ice.Current current) {
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

  public short[][] get16BitLookupTable(Ice.Current current) {
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

  public int[] getChannelDimLengths(Ice.Current current) {
    return reader.getChannelDimLengths();
  }

  public String[] getChannelDimTypes(Ice.Current current) {
    return reader.getChannelDimTypes();
  }

  public int getThumbSizeX(Ice.Current current) {
    return reader.getThumbSizeX();
  }

  public int getThumbSizeY(Ice.Current current) {
    return reader.getThumbSizeY();
  }

  public boolean isLittleEndian(Ice.Current current) {
    return reader.isLittleEndian();
  }

  public String getDimensionOrder(Ice.Current current) {
    return reader.getDimensionOrder();
  }

  public boolean isOrderCertain(Ice.Current current) {
    return reader.isOrderCertain();
  }

  public boolean isInterleaved(Ice.Current current) {
    return reader.isInterleaved();
  }

  public boolean isInterleavedSubC(int subC, Ice.Current current) {
    return reader.isInterleaved(subC);
  }

  public byte[] openBytes(int no, int x, int y, int width, int height,
    Ice.Current current)
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

  public byte[] openThumbBytes(int no, Ice.Current current) {
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

  public void close(boolean fileOnly, Ice.Current current) {
    try {
      reader.close(fileOnly);
    }
    catch (IOException exc) {
      exc.printStackTrace();
    }
  }

  public int getSeriesCount(Ice.Current current) {
    return reader.getSeriesCount();
  }

  public void setSeries(int no, Ice.Current current) {
    reader.setSeries(no);
  }

  public int getSeries(Ice.Current current) {
    return reader.getSeries();
  }

  public void setNormalized(boolean normalize, Ice.Current current) {
    reader.setNormalized(normalize);
  }

  public boolean isNormalized(Ice.Current current) {
    return reader.isNormalized();
  }

  public void setMetadataCollected(boolean collect, Ice.Current current) {
    reader.setMetadataCollected(collect);
  }

  public boolean isMetadataCollected(Ice.Current current) {
    return reader.isMetadataCollected();
  }

  public void setOriginalMetadataPopulated(boolean populate,
    Ice.Current current)
  {
    reader.setOriginalMetadataPopulated(populate);
  }

  public boolean isOriginalMetadataPopulated(Ice.Current current) {
    return reader.isOriginalMetadataPopulated();
  }

  public void setGroupFiles(boolean group, Ice.Current current) {
    reader.setGroupFiles(group);
  }

  public boolean isGroupFiles(Ice.Current current) {
    return reader.isGroupFiles();
  }

  public boolean isMetadataComplete(Ice.Current current) {
    return reader.isMetadataComplete();
  }

  public int fileGroupOption(String id, Ice.Current current) {
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

  public String[] getUsedFiles(Ice.Current current) {
    return reader.getUsedFiles();
  }

  public String getCurrentFile(Ice.Current current) {
    return reader.getCurrentFile();
  }

  public int getIndex(int z, int c, int t, Ice.Current current) {
    return reader.getIndex(z, c, t);
  }

  public int[] getZCTCoords(int index, Ice.Current current) {
    return reader.getZCTCoords(index);
  }

  public void setMetadataFiltered(boolean filter, Ice.Current current) {
    reader.setMetadataFiltered(filter);
  }

  public boolean isMetadataFiltered(Ice.Current current) {
    return reader.isMetadataFiltered();
  }

  public void setMetadataStore(MetadataStorePrx store, Ice.Current current) {
    reader.setMetadataStore(((MetadataStoreI)
      store.getServant()).getWrappedObject());
  }

  public void close(Ice.Current current) {
    try {
      reader.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void closeFile(boolean fileOnly, Ice.Current current) {
    try {
      reader.close(fileOnly);
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public String getFormat(Ice.Current current) {
    return reader.getFormat();
  }

  public String[] getSuffixes(Ice.Current current) {
    return reader.getSuffixes();
  }

}
