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

package loci.formats.in;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;

import ome.xml.model.primitives.Timestamp;

import ome.units.quantity.Length;

/**
 * ImarisTiffReader is the file format reader for
 * Bitplane Imaris 3 files (TIFF variant).
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class ImarisTiffReader extends BaseTiffReader {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(ImarisTiffReader.class);

  // -- Constructor --

  /** Constructs a new Imaris TIFF reader. */
  public ImarisTiffReader() {
    super("Bitplane Imaris 3 (TIFF)", "ims");
    suffixSufficient = false;
    suffixNecessary = true;
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    // hack up the IFDs
    //
    // Imaris TIFFs store a thumbnail in the first IFD; each of the remaining
    // IFDs defines a stack of tiled planes.
    // MinimalTiffReader.initFile(String) removes thumbnail IFDs.

    LOGGER.info("Verifying IFD sanity");

    IFDList tmp = new IFDList();

    for (IFD ifd : ifds) {
      long[] byteCounts = ifd.getStripByteCounts();
      long[] offsets = ifd.getStripOffsets();

      for (int i=0; i<byteCounts.length; i++) {
        IFD t = new IFD(ifd);
        t.putIFDValue(IFD.TILE_BYTE_COUNTS, byteCounts[i]);
        t.putIFDValue(IFD.TILE_OFFSETS, offsets[i]);
        tmp.add(t);
      }
    }

    String comment = ifds.get(0).getComment();

    LOGGER.info("Populating metadata");

    CoreMetadata m = core.get(0, 0);

    m.sizeC = ifds.size();
    m.sizeZ = tmp.size() / getSizeC();
    m.sizeT = 1;
    m.sizeX = (int) ifds.get(0).getImageWidth();
    m.sizeY = (int) ifds.get(0).getImageLength();

    ifds = tmp;
    m.imageCount = getSizeC() * getSizeZ();
    m.dimensionOrder = "XYZCT";
    m.interleaved = false;
    m.rgb = getImageCount() != getSizeZ() * getSizeC() * getSizeT();
    m.pixelType = ifds.get(0).getPixelType();

    LOGGER.info("Parsing comment");

    // likely an INI-style comment, although we can't be sure

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      String description = null, creationDate = null;
      final List<Double> emWave = new ArrayList<Double>();
      final List<Double> exWave = new ArrayList<Double>();
      final List<String> channelNames = new ArrayList<String>();

      if (comment != null && comment.startsWith("[")) {
        // parse key/value pairs
        StringTokenizer st = new StringTokenizer(comment, "\n");
        while (st.hasMoreTokens()) {
          String line = st.nextToken();
          int equals = line.indexOf('=');
          if (equals < 0) continue;
          String key = line.substring(0, equals).trim();
          String value = line.substring(equals + 1).trim();
          addGlobalMeta(key, value);

          if (key.equals("Description")) {
            description = value;
          }
          else if (key.equals("LSMEmissionWavelength") && !value.equals("0")) {
            emWave.add(new Double(value));
          }
          else if (key.equals("LSMExcitationWavelength") && !value.equals("0"))
          {
            exWave.add(new Double(value));
          }
          else if (key.equals("Name") && !currentId.endsWith(value)) {
            channelNames.add(value);
          }
          else if (key.equals("RecordingDate")) {
            value = value.replaceAll(" ", "T");
            creationDate = value.substring(0, value.indexOf('.'));
          }
        }
        metadata.remove("Comment");
      }

      // populate Image data
      store.setImageDescription(description, 0);
      if (creationDate != null) {
        store.setImageAcquisitionDate(new Timestamp(creationDate), 0);
      }

      // populate LogicalChannel data
      for (int i=0; i<emWave.size(); i++) {
        Length emission = FormatTools.getEmissionWavelength(emWave.get(i));
        Length excitation =
          FormatTools.getExcitationWavelength(exWave.get(i));

        if (emission != null) {
          store.setChannelEmissionWavelength(emission, 0, i);
        }
        if (excitation != null) {
          store.setChannelExcitationWavelength(excitation, 0, i);
        }
        store.setChannelName(channelNames.get(i), 0, i);
      }
    }
  }

}
