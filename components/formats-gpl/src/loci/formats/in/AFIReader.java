/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.xml.BaseHandler;
import loci.common.xml.XMLTools;
import loci.formats.ChannelSeparator;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.UNITS;

import org.xml.sax.Attributes;

/**
 * AFIReader is the file format reader for Aperio AFI files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class AFIReader extends FormatReader {

  // -- Constants --

  private static final int EXTRA_IMAGES = 3;

  // -- Fields --

  private ArrayList<String> pixels = new ArrayList<String>();
  private ChannelSeparator[] reader;

  // -- Constructor --

  /** Constructs a new AFI reader. */
  public AFIReader() {
    super("Aperio AFI", "afi");
    domains = new String[] {FormatTools.HISTOLOGY_DOMAIN};
    hasCompanionFiles = true;
    datasetDescription = "One .afi file and several similarly-named .svs files";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 4;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return true;
  }

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    return reader[0].getOptimalTileWidth();
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    return reader[0].getOptimalTileHeight();
  }

  /* @see loci.formats.IFormatReader#openThumbBytes(int) */
  @Override
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);

    if (getCoreIndex() >= core.size() - EXTRA_IMAGES) {
      reader[0].setCoreIndex(getCoreIndex());
      return reader[0].openThumbBytes(no);
    }

    int coreIndex = getCoreIndex();
    setCoreIndex(core.size() - EXTRA_IMAGES - 1);
    byte[] thumb = FormatTools.openThumbBytes(this, no);
    setCoreIndex(coreIndex);

    return thumb;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (getCoreIndex() >= core.size() - EXTRA_IMAGES) {
      reader[0].setCoreIndex(getCoreIndex());
      return reader[0].openBytes(no, buf, x, y, w, h);
    }

    int[] coords = getZCTCoords(no);
    int channel = coords[1];
    int index = getIndex(coords[0], 0, coords[2]);

    reader[channel].setCoreIndex(getCoreIndex());
    return reader[channel].openBytes(index, buf, x, y, w, h);
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    if (noPixels) {
      return new String[] {currentId};
    }

    String[] files = new String[pixels.size() + 1];
    files[0] = currentId;
    for (int i=0; i<pixels.size(); i++) {
      files[i + 1] = pixels.get(i);
    }
    return files;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      if (reader != null) {
        for (ChannelSeparator r : reader) {
          if (r != null) {
            r.close();
          }
        }
      }
      reader = null;
      pixels.clear();
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    // The AFI file is just simple XML that lists the .svs files from
    // which to read pixel data.  Each .svs corresponds to a single channel;
    // we assemble the channels in the order in which they are stored in
    // the XML.
    //
    // Note that the last two series are identical across the .svs files,
    // so we just use the ones from the first listed file.

    String xml = DataTools.readFile(id);
    XMLTools.parseXML(xml, new AFIHandler());

    String parent = new Location(id).getAbsoluteFile().getParent();
    String[] channelNames = new String[pixels.size()];
    reader = new ChannelSeparator[pixels.size()];

    for (int i=0; i<pixels.size(); i++) {
      String file = pixels.get(i);

      int underscore = file.indexOf('_');
      int fullStop = file.indexOf('.');
      if (underscore >= 0 && fullStop > underscore) {
        channelNames[i] = file.substring(underscore + 1, fullStop);
      }

      pixels.set(i, new Location(parent, file).getAbsolutePath());

      reader[i] = new ChannelSeparator(new SVSReader());
      reader[i].setFlattenedResolutions(hasFlattenedResolutions());
      reader[i].setId(pixels.get(i));
    }

    core = reader[0].getCoreMetadataList();

    for (int i=0; i<core.size() - EXTRA_IMAGES; i++) {
      CoreMetadata c = core.get(i);
      c.sizeC = pixels.size();
      c.imageCount = c.sizeC * c.sizeZ * c.sizeT;
      c.rgb = false;
      if (i == 0) {
        c.resolutionCount = core.size() - EXTRA_IMAGES;
      }
    }

    MetadataStore store = makeFilterMetadata();
    boolean minimalMetadata =
      getMetadataOptions().getMetadataLevel() == MetadataLevel.MINIMUM;
    MetadataTools.populatePixels(store, this, !minimalMetadata);

    String fileID = currentId.substring(
      currentId.lastIndexOf(File.separator) + 1, currentId.lastIndexOf("."));
    for (int i=0; i<getSeriesCount(); i++) {
      store.setImageName(fileID + " - image #" + (i + 1), i);
    }

    if (!minimalMetadata) {
      Length[] emission = new Length[pixels.size()];
      Length[] excitation = new Length[pixels.size()];
      Double[] exposure = new Double[pixels.size()];
      Timestamp[] datestamp = new Timestamp[pixels.size()];
      Length[] physicalSizes = null;
      double magnification = Double.NaN;

      for (int c=0; c<pixels.size(); c++) {
        SVSReader baseReader = (SVSReader) reader[c].getReader();
        emission[c] = baseReader.getEmission();
        excitation[c] = baseReader.getExcitation();
        exposure[c] = baseReader.getExposureTime();
        datestamp[c] = baseReader.getDatestamp();
        physicalSizes = baseReader.getPhysicalSizes();

        if (c == 0) {
          magnification = baseReader.getMagnification();
        }
      }

      String instrument = MetadataTools.createLSID("Instrument", 0);
      String objective = MetadataTools.createLSID("Objective", 0, 0);
      store.setInstrumentID(instrument, 0);
      store.setObjectiveID(objective, 0, 0);
      store.setObjectiveNominalMagnification(magnification, 0, 0);

      for (int i=0; i<getSeriesCount() - EXTRA_IMAGES; i++) {
        if (datestamp[0] != null) {
          store.setImageAcquisitionDate(datestamp[0], i);
        }
        store.setImageInstrumentRef(instrument, i);
        store.setObjectiveSettingsID(objective, i);

        if (i < physicalSizes.length &&
          physicalSizes[i] != null &&
          physicalSizes[i].value(UNITS.MICROMETER).doubleValue() - Constants.EPSILON > 0)
        {
          Length size = physicalSizes[i];
          store.setPixelsPhysicalSizeX(size, i);
          store.setPixelsPhysicalSizeY(size, i);
        }

        for (int c=0; c<channelNames.length; c++) {
          store.setChannelName(channelNames[c], i, c);

          if (emission[c] != null) {
            store.setChannelEmissionWavelength(emission[c], i, c);
          }
          if (excitation[c] != null) {
            store.setChannelExcitationWavelength(excitation[c], i, c);
          }

          store.setPlaneExposureTime(FormatTools.createTime(exposure[c], UNITS.SECOND), i, c);
        }
      }
    }
  }

  // -- Helper class --

  class AFIHandler extends BaseHandler {
    private String currentElement;

    @Override
    public void characters(char[] ch, int start, int length) {
      String value = new String(ch, start, length);

      if (currentElement.equals("Path") && value.trim().length() > 0) {
        pixels.add(value);
      }
    }

    @Override
    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      currentElement = qName;
    }
  }

}
