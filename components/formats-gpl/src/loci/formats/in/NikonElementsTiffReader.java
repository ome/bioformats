/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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
import java.util.Hashtable;

import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.common.xml.XMLTools;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;

import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

/**
 * NikonElementsTiffReader is the file format reader for TIFF files produced
 * by Nikon Elements.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/NikonElementsTiffReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/NikonElementsTiffReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class NikonElementsTiffReader extends BaseTiffReader {

  // -- Constants --

  private static final int NIKON_XML_TAG = 65332;
  private static final int NIKON_XML_TAG_2 = 65333;

  // -- Fields --

  private ND2Handler handler;

  // -- Constructor --

  public NikonElementsTiffReader() {
    super("Nikon Elements TIFF", new String[] {"tif", "tiff"});
    suffixSufficient = false;
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser tp = new TiffParser(stream);
    IFD ifd = tp.getFirstIFD();
    if (ifd == null) return false;
    return ifd.containsKey(NIKON_XML_TAG);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      handler = null;
    }
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    String xml = ifds.get(0).getIFDTextValue(NIKON_XML_TAG).trim();
    if (xml.length() == 0) {
      xml = ifds.get(0).getIFDTextValue(NIKON_XML_TAG_2).trim();
    }
    int open = xml.indexOf("<");
    if (open >= 0) {
      xml = xml.substring(open);
    }
    xml = "<NIKON>" + xml + "</NIKON>";
    xml = XMLTools.sanitizeXML(xml);

    handler = new ND2Handler(core, false, getImageCount());
    try {
      XMLTools.parseXML(xml, handler);

      Hashtable<String, Object> globalMetadata = handler.getMetadata();
      for (String key : globalMetadata.keySet()) {
        addGlobalMeta(key, globalMetadata.get(key));
      }
    }
    catch (IOException e) { }
  }

  /* @see BaseTiffReader#initMetadataStore() */
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    String date = handler.getDate();
    if (date != null) {
      store.setImageAcquisitionDate(new Timestamp(date), 0);
    }

    if (getMetadataOptions().getMetadataLevel() == MetadataLevel.MINIMUM) {
      return;
    }

    PositiveFloat sizeX = FormatTools.getPhysicalSizeX(handler.getPixelSizeX());
    PositiveFloat sizeY = FormatTools.getPhysicalSizeY(handler.getPixelSizeY());
    PositiveFloat sizeZ = FormatTools.getPhysicalSizeZ(handler.getPixelSizeZ());
    if (sizeX != null) {
      store.setPixelsPhysicalSizeX(sizeX, 0);
    }
    if (sizeY != null) {
      store.setPixelsPhysicalSizeY(sizeY, 0);
    }
    if (sizeZ != null) {
      store.setPixelsPhysicalSizeZ(sizeZ, 0);
    }

    String instrument = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrument, 0);
    store.setImageInstrumentRef(instrument, 0);

    ArrayList<Double> exposureTimes = handler.getExposureTimes();
    ArrayList<Double> posX = handler.getXPositions();
    ArrayList<Double> posY = handler.getYPositions();
    ArrayList<Double> posZ = handler.getZPositions();

    for (int i=0; i<getImageCount(); i++) {
      int c = getZCTCoords(i)[1];
      if (c < exposureTimes.size()) {
        store.setPlaneExposureTime(exposureTimes.get(c), 0, i);
      }

      if (i < posX.size()) {
        store.setPlanePositionX(posX.get(i), 0, i);
      }
      if (i < posY.size()) {
        store.setPlanePositionY(posY.get(i), 0, i);
      }
      if (i < posZ.size()) {
        store.setPlanePositionZ(posZ.get(i), 0, i);
      }
    }

    String detector = MetadataTools.createLSID("Detector", 0, 0);
    store.setDetectorID(detector, 0, 0);
    store.setDetectorModel(handler.getCameraModel(), 0, 0);
    store.setDetectorType(getDetectorType("Other"), 0, 0);

    ArrayList<String> channelNames = handler.getChannelNames();
    ArrayList<String> modality = handler.getModalities();
    ArrayList<String> binning = handler.getBinnings();
    ArrayList<Double> speed = handler.getSpeeds();
    ArrayList<Double> gain = handler.getGains();
    ArrayList<Double> temperature = handler.getTemperatures();
    ArrayList<Integer> exWave = handler.getExcitationWavelengths();
    ArrayList<Integer> emWave = handler.getEmissionWavelengths();
    ArrayList<Integer> power = handler.getPowers();
    ArrayList<Hashtable<String, String>> rois = handler.getROIs();
    Double pinholeSize = handler.getPinholeSize();

    for (int c=0; c<getEffectiveSizeC(); c++) {
      if (pinholeSize != null) {
        store.setChannelPinholeSize(pinholeSize, 0, c);
      }
      if (c < channelNames.size()) {
        store.setChannelName(channelNames.get(c), 0, c);
      }
      if (c < modality.size()) {
        store.setChannelAcquisitionMode(
          getAcquisitionMode(modality.get(c)), 0, c);
      }
      if (c < emWave.size()) {
        PositiveInteger em = FormatTools.getEmissionWavelength(emWave.get(c));
        if (em != null) {
          store.setChannelEmissionWavelength(em, 0, c);
        }
      }
      if (c < exWave.size()) {
        PositiveInteger ex = FormatTools.getExcitationWavelength(exWave.get(c));
        if (ex != null) {
          store.setChannelExcitationWavelength(ex, 0, c);
        }
      }
      if (c < binning.size()) {
        store.setDetectorSettingsBinning(getBinning(binning.get(c)), 0, c);
      }
      if (c < gain.size()) {
        store.setDetectorSettingsGain(gain.get(c), 0, c);
      }
      if (c < speed.size()) {
        store.setDetectorSettingsReadOutRate(speed.get(c), 0, c);
      }
      store.setDetectorSettingsID(detector, 0, c);
    }

    if (temperature.size() > 0) {
      store.setImagingEnvironmentTemperature(temperature.get(0), 0);
    }

    Double voltage = handler.getVoltage();
    if (voltage != null) {
      store.setDetectorSettingsVoltage(voltage, 0, 0);
    }

    Double na = handler.getNumericalAperture();
    if (na != null) store.setObjectiveLensNA(na, 0, 0);

    Double mag = handler.getMagnification();
    if (mag != null) store.setObjectiveCalibratedMagnification(mag, 0, 0);

    store.setObjectiveModel(handler.getObjectiveModel(), 0, 0);

    String immersion = handler.getImmersion();
    if (immersion == null) immersion = "Other";
    store.setObjectiveImmersion(getImmersion(immersion), 0, 0);

    String correction = handler.getCorrection();
    if (correction == null || correction.length() == 0) correction = "Other";
    store.setObjectiveCorrection(getCorrection(correction), 0, 0);

    String objective = MetadataTools.createLSID("Objective", 0, 0);
    store.setObjectiveID(objective, 0, 0);
    store.setObjectiveSettingsID(objective, 0);

    Double refractiveIndex = handler.getRefractiveIndex();
    if (refractiveIndex != null) {
      store.setObjectiveSettingsRefractiveIndex(refractiveIndex, 0);
    }

    if (getMetadataOptions().getMetadataLevel() == MetadataLevel.NO_OVERLAYS) {
      return;
    }

    handler.populateROIs(store);
  }

}
