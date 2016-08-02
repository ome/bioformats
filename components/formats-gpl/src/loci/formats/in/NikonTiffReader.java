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

import java.io.IOException;
import java.util.Vector;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;
import ome.units.quantity.Length;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;

import ome.units.quantity.Length;
import ome.units.UNITS;

/**
 * NikonTiffReader is the file format reader for Nikon TIFF files.
 */
public class NikonTiffReader extends BaseTiffReader {

  // -- Constants --

  private static final String[] TOP_LEVEL_KEYS = new String[] {
    "document document", "document", "history Acquisition", "history objective",
    "history history", "history laser", "history step", "history",
    "sensor s_params", "sensor", "view"
  };

  // -- Fields --

  private double physicalSizeX, physicalSizeY, physicalSizeZ;
  private Vector<String> filterModels, dichroicModels, laserIDs;
  private Double magnification;
  private double lensNA, workingDistance, pinholeSize;
  private String correction, immersion;
  private Vector<Double> gain;
  private Vector<Double> wavelength, emWave, exWave;

  // -- Constructor --

  public NikonTiffReader() {
    super("Nikon TIFF", new String[] {"tif", "tiff"});
    suffixSufficient = false;
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser tp = new TiffParser(stream);
    IFD ifd = tp.getFirstIFD();
    if (ifd == null) return false;
    String software = ifd.getIFDTextValue(IFD.SOFTWARE);
    return software != null && software.toString().indexOf("EZ-C1") != -1;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      physicalSizeX = physicalSizeY = physicalSizeZ = 0;
      dichroicModels = filterModels = laserIDs = null;
      magnification = null;
      lensNA = workingDistance = pinholeSize = 0;
      correction = immersion = null;
      gain = null;
      wavelength = emWave = exWave = null;
    }
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  @Override
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    if (getMetadataOptions().getMetadataLevel() == MetadataLevel.MINIMUM) {
      return;
    }

    filterModels = new Vector<String>();
    dichroicModels = new Vector<String>();
    laserIDs = new Vector<String>();
    gain = new Vector<Double>();
    wavelength = new Vector<Double>();
    emWave = new Vector<Double>();
    exWave = new Vector<Double>();

    // parse key/value pairs in the comment
    String comment = ifds.get(0).getComment();
    metadata.remove("Comment");
    String[] lines = comment.split("\n");

    String[] dimensionLabels = null, dimensionSizes = null;

    for (String line : lines) {
      String[] tokens = line.split("\t");
      line = line.replaceAll("\t", " ");
      int nTokensInKey = 0;
      for (String key : TOP_LEVEL_KEYS) {
        if (line.startsWith(key)) {
          nTokensInKey = key.indexOf(" ") != -1 ? 3 : 2;
          break;
        }
      }
      final StringBuilder k = new StringBuilder();
      for (int i=0; i<nTokensInKey; i++) {
        k.append(tokens[i]);
        if (i < nTokensInKey - 1) k.append(" ");
      }
      final StringBuilder v = new StringBuilder();
      for (int i=nTokensInKey; i<tokens.length; i++) {
        v.append(tokens[i]);
        if (i < tokens.length - 1) v.append(" ");
      }
      String key = k.toString();
      String value = v.toString();

      if (key.equals("document label")) {
        dimensionLabels = value.toLowerCase().split(" ");
      }
      else if (key.equals("document scale")) {
        dimensionSizes = value.split(" ");
      }
      else if (key.startsWith("history Acquisition") &&
        key.indexOf("Filter") != -1)
      {
        filterModels.add(value);
      }
      else if (key.startsWith("history Acquisition") &&
        key.indexOf("Dichroic") != -1)
      {
        dichroicModels.add(value);
      }
      else if (key.equals("history objective Type")) {
        correction = value;
      }
      else if (key.equals("history objective Magnification")) {
        magnification = new Double(value);
      }
      else if (key.equals("history objective NA")) {
        lensNA = Double.parseDouble(value);
      }
      else if (key.equals("history objective WorkingDistance")) {
        workingDistance = Double.parseDouble(value);
      }
      else if (key.equals("history objective Immersion")) {
        immersion = value;
      }
      else if (key.startsWith("history gain")) {
        gain.add(new Double(value));
      }
      else if (key.equals("history pinhole")) {
        pinholeSize = new Double(value.substring(0, value.indexOf(" ")));
      }
      else if (key.startsWith("history laser") && key.endsWith("wavelength")) {
        wavelength.add(new Double(value.replaceAll("\\D", "")));
      }
      else if (key.startsWith("history laser") && key.endsWith("name")) {
        laserIDs.add(value);
      }
      else if (key.equals("sensor s_params LambdaEx")) {
        for (int i=nTokensInKey; i<tokens.length; i++) {
          exWave.add(new Double(tokens[i]));
        }
      }
      else if (key.equals("sensor s_params LambdaEm")) {
        for (int i=nTokensInKey; i<tokens.length; i++) {
          emWave.add(new Double(tokens[i]));
        }
      }

      addGlobalMeta(key, value);
    }
    parseDimensionSizes(dimensionLabels, dimensionSizes);
  }

  /* @see BaseTiffReader#initMetadataStore() */
  @Override
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      store.setImageDescription("", 0);

      Length sizeX = FormatTools.getPhysicalSizeX(physicalSizeX);
      Length sizeY = FormatTools.getPhysicalSizeY(physicalSizeY);
      Length sizeZ = FormatTools.getPhysicalSizeZ(physicalSizeZ);

      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, 0);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, 0);
      }
      if (sizeZ != null) {
        store.setPixelsPhysicalSizeZ(sizeZ, 0);
      }

      String instrumentID = MetadataTools.createLSID("Instrument", 0);
      store.setInstrumentID(instrumentID, 0);
      store.setImageInstrumentRef(instrumentID, 0);

      String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
      store.setObjectiveID(objectiveID, 0, 0);
      store.setObjectiveSettingsID(objectiveID, 0);
      store.setObjectiveNominalMagnification(magnification, 0, 0);

      if (correction == null) correction = "Other";
      store.setObjectiveCorrection(getCorrection(correction), 0, 0);
      store.setObjectiveLensNA(lensNA, 0, 0);
      store.setObjectiveWorkingDistance(new Length(workingDistance, UNITS.MICROMETER), 0, 0);
      if (immersion == null) immersion = "Other";
      store.setObjectiveImmersion(getImmersion(immersion), 0, 0);

      for (int i=0; i<wavelength.size(); i++) {
        String laser = MetadataTools.createLSID("LightSource", 0, i);
        store.setLaserID(laser, 0, i);
        store.setLaserModel(laserIDs.get(i), 0, i);

        Length wave = FormatTools.getWavelength(wavelength.get(i));
        if (wave != null) {
          store.setLaserWavelength(wave, 0, i);
        }
        store.setLaserType(getLaserType("Other"), 0, i);
        store.setLaserLaserMedium(getLaserMedium("Other"), 0, i);
      }

      for (int i=0; i<gain.size(); i++) {
        store.setDetectorID(MetadataTools.createLSID("Detector", 0, i), 0, i);
        store.setDetectorGain(gain.get(i), 0, i);
        store.setDetectorType(getDetectorType("Other"), 0, i);
      }

      for (int c=0; c<getEffectiveSizeC(); c++) {
        store.setChannelPinholeSize(new Length(pinholeSize, UNITS.MICROMETER), 0, c);
        if (c < exWave.size()) {
          Length wave = FormatTools.getExcitationWavelength(exWave.get(c));
          if (wave != null) {
            store.setChannelExcitationWavelength(wave, 0, c);
          }
        }
        if (c < emWave.size()) {
          Length wave = FormatTools.getEmissionWavelength(emWave.get(c));
          if (wave != null) {
            store.setChannelEmissionWavelength(wave, 0, c);
          }
        }
      }

      for (int i=0; i<filterModels.size(); i++) {
        String filter = MetadataTools.createLSID("Filter", 0, i);
        store.setFilterID(filter, 0, i);
        store.setFilterModel(filterModels.get(i), 0, i);
      }

      for (int i=0; i<dichroicModels.size(); i++) {
        String dichroic = MetadataTools.createLSID("Dichroic", 0, i);
        store.setDichroicID(dichroic, 0, i);
        store.setDichroicModel(dichroicModels.get(i), 0, i);
      }
    }
  }

  // -- Helper methods --

  private void parseDimensionSizes(String[] labels, String[] sizes) {
    for (int i=0; i<labels.length; i++) {
      if (labels[i].startsWith("z")) {
        physicalSizeZ = Double.parseDouble(sizes[i]);
      }
      else if (labels[i].equals("x")) {
        physicalSizeX = Double.parseDouble(sizes[i]);
      }
      else if (labels[i].equals("y")) {
        physicalSizeY = Double.parseDouble(sizes[i]);
      }
    }
  }


}
