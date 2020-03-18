/*-
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2019 - 2020 Open Microscopy Environment:
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

package loci.formats.in;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatTools;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffIFDEntry;
import loci.formats.tiff.TiffParser;


/**
 * MikroscanTiffReader is the file format reader for Mikroscan TIFF files.
 */
public class MikroscanTiffReader extends SVSReader {
   
  // -- Constants --
    
  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(MikroscanTiffReader.class);

  /** TIFF image description prefix for Mikroscan TIF files. */
  private static final String MIKROSCAN_IMAGE_DESCRIPTION_PREFIX = "Mikroscan Image"; 

  // -- Constructor --  
  
  /** Constructs a new Mikroscan TIF reader. */
  public MikroscanTiffReader() {
    super("Mikroscan TIFF", new String[] {"tif", "tiff"});
    domains = new String[] {FormatTools.HISTOLOGY_DOMAIN, FormatTools.LM_DOMAIN};
    suffixNecessary = false;
  }

  // -- IFormatReader API methods --
  
  /* (non-Javadoc)
   * @see loci.formats.FormatReader#isThisType(java.lang.String, boolean)
   */
  @Override
  public boolean isThisType(String name, boolean open) {
    boolean isThisType = super.isThisType(name, open);
    if (isThisType && open) {
      try (RandomAccessInputStream stream = new RandomAccessInputStream(name)) {
        TiffParser tiffParser = new TiffParser(stream);
        tiffParser.setDoCaching(false);
        if (!tiffParser.isValidHeader()) {
          return false;
        }
        IFD ifd = tiffParser.getFirstIFD();
        if (ifd == null) {
          return false;
        }
        Object description = ifd.get(IFD.IMAGE_DESCRIPTION);
        if (description != null) {
          String imageDescription = null;

          if (description instanceof TiffIFDEntry) {
            Object value = tiffParser.getIFDValue((TiffIFDEntry) description);
            if (value != null) {
              imageDescription = value.toString();
            }
          }
          else if (description instanceof String) {
            imageDescription = (String) description;
          }
          if (imageDescription != null
              && imageDescription.startsWith(MIKROSCAN_IMAGE_DESCRIPTION_PREFIX)) {
            return true;
          }
        }
        return false;
      }
      catch (IOException e) {
        LOGGER.debug("I/O exception during isThisType() evaluation.", e);
        return false;
      }
    }
    return isThisType;
  }
}
