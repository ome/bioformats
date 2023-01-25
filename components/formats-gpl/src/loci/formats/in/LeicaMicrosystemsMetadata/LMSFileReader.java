/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

package loci.formats.in.LeicaMicrosystemsMetadata;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.slf4j.Logger;

import loci.formats.FormatReader;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.meta.MetadataStore;
import loci.formats.in.MetadataOptions;
import loci.formats.in.LeicaMicrosystemsMetadata.doc.LMSImageXmlDocument;
import loci.formats.in.LeicaMicrosystemsMetadata.doc.LMSXmlDocument;
import loci.formats.in.DynamicMetadataOptions;

/**
 * This class can be extended by readers of Leica Microsystems file formats that
 * include LASX generated XML (e.g. XLEF, LOF)
 * 
 * @author Melissa Linkert melissa at glencoesoftware.com
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public abstract class LMSFileReader extends FormatReader {
  // -- Constants --
  public static final String OLD_PHYSICAL_SIZE_KEY = "leicalif.old_physical_size";
  public static final boolean OLD_PHYSICAL_SIZE_DEFAULT = false;

  // -- Fields --
  public static Logger log;
  public MetadataTempBuffer metaTemp;
  public LMSXmlDocument associatedXmlDoc; //an optional LMS xml file that references the file(s) that are read by this reader

  /** file format in which actual image bytes are stored */
  public enum ImageFormat {
    LOF, TIF, BMP, JPEG, PNG, LIF, UNKNOWN
  }

  // -- Constructor --
  protected LMSFileReader(String format, String suffix) {
    super(format, suffix);
    LMSFileReader.log = LOGGER;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);

    if (!fileOnly){
      metaTemp = null;
    }
  }

  // -- Methods --

  /**
   * Returns the file format in which actual image bytes are stored
   */
  public abstract ImageFormat getImageFormat();

  public List<CoreMetadata> getCore() {
    return core;
  }

  public void setCore(ArrayList<CoreMetadata> core) {
    this.core = core;
  }

  public boolean useOldPhysicalSizeCalculation() {
    MetadataOptions options = getMetadataOptions();
    if (options instanceof DynamicMetadataOptions) {
      return ((DynamicMetadataOptions) options).getBoolean(OLD_PHYSICAL_SIZE_KEY, OLD_PHYSICAL_SIZE_DEFAULT);
    }
    return OLD_PHYSICAL_SIZE_DEFAULT;
  }

  public void addSeriesMeta(String key, Object value) {
    super.addMeta(key, value, getCurrentCore().seriesMetadata);
  }

  public void addSeriesMetaList(String key, Object value) {
    super.addSeriesMetaList(key, value);
  }

  public MetadataStore makeFilterMetadata() {
    return super.makeFilterMetadata();
  }

  /**
   * Extracts metadata from a list of Leica image XML Documents and writes them to
   * the reader's CoreMetadata, {@link MetadataTempBuffer} and MetadataStore
   * 
   * @param docs List of document nodes representing XML documents
   * @throws FormatException
   * @throws IOException
   */
  public void translateMetadata(List<LMSImageXmlDocument> docs) throws FormatException, IOException {
    int len = docs.size();
    metaTemp = new MetadataTempBuffer(len);

    LMSMetadataTranslator translator = new LMSMetadataTranslator(this);
    translator.translateMetadata(docs);
  }

  public void translateMetadata(LMSImageXmlDocument doc) throws FormatException, IOException{
    List<LMSImageXmlDocument> docs = new ArrayList<>();
    docs.add(doc);
    translateMetadata(docs);
  }

  // -- Helper functions --

  /**
   * Checks if file at path exists
   * 
   * @param path whole file path
   */
  public static boolean fileExists(String path) {
    if (path != null && !path.trim().isEmpty()) {
      try {
        File f = new File(path);
        if (f.exists()) {
          return true;
        }
      } catch (Exception e) {
        return false;
      }
    }
    return false;
  }
  
  protected int getTileIndex(int coreIndex) {
    int count = 0;
    for (int tile = 0; tile < metaTemp.tileCount.length; tile++) {
      if (coreIndex < count + metaTemp.tileCount[tile]) {
        return tile;
      }
      count += metaTemp.tileCount[tile];
    }
    return -1;
  }
}
