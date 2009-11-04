//
// NikonTiffReader.java
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

package loci.formats.in;

import java.io.IOException;
import java.util.Vector;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;

/**
 * NikonTiffReader is the file format reader for Nikon TIFF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/NikonTiffReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/NikonTiffReader.java">SVN</a></dd></dl>
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
  private int magnification;
  private double lensNA, workingDistance, pinholeSize;
  private String correction, immersion;
  private Vector<Double> gain;
  private Vector<Integer> wavelength, emWave, exWave;

  // -- Constructor --

  public NikonTiffReader() {
    super("Nikon TIFF", new String[] {"tif", "tiff"});
    suffixSufficient = false;
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser tp = new TiffParser(stream);
    IFD ifd = tp.getFirstIFD();
    if (ifd == null) return false;
    String software = ifd.getIFDTextValue(IFD.SOFTWARE);
    return software != null && software.toString().indexOf("EZ-C1") != -1;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      physicalSizeX = physicalSizeY = physicalSizeZ = 0;
      dichroicModels = filterModels = laserIDs = null;
      magnification = 0;
      lensNA = workingDistance = pinholeSize = 0;
      correction = immersion = null;
      gain = null;
      wavelength = emWave = exWave = null;
    }
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    filterModels = new Vector<String>();
    dichroicModels = new Vector<String>();
    laserIDs = new Vector<String>();
    gain = new Vector<Double>();
    wavelength = new Vector<Integer>();
    emWave = new Vector<Integer>();
    exWave = new Vector<Integer>();

    // parse key/value pairs in the comment
    String comment = ifds.get(0).getComment();
    metadata.remove("Comment");
    String[] lines = comment.split("\n");

    StringBuffer k = null, v = null;

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
      k = new StringBuffer();
      for (int i=0; i<nTokensInKey; i++) {
        k.append(tokens[i]);
        if (i < nTokensInKey - 1) k.append(" ");
      }
      v = new StringBuffer();
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
        magnification = (int) Double.parseDouble(value);
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
        wavelength.add(new Integer(value.replaceAll("\\D", "")));
      }
      else if (key.startsWith("history laser") && key.endsWith("name")) {
        laserIDs.add(value);
      }
      else if (key.equals("sensor s_params LambdaEx")) {
        for (int i=nTokensInKey; i<tokens.length; i++) {
          exWave.add(new Integer(tokens[i]));
        }
      }
      else if (key.equals("sensor s_params LambdaEm")) {
        for (int i=nTokensInKey; i<tokens.length; i++) {
          emWave.add(new Integer(tokens[i]));
        }
      }

      addGlobalMeta(key, value);
    }
    parseDimensionSizes(dimensionLabels, dimensionSizes);
  }

  /* @see BaseTiffReader#initMetadataStore() */
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this);

    store.setImageDescription("", 0);

    store.setDimensionsPhysicalSizeX(new Double(physicalSizeX), 0, 0);
    store.setDimensionsPhysicalSizeY(new Double(physicalSizeY), 0, 0);
    store.setDimensionsPhysicalSizeZ(new Double(physicalSizeZ), 0, 0);

    String instrumentID = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrumentID, 0);
    store.setImageInstrumentRef(instrumentID, 0);

    String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
    store.setObjectiveID(objectiveID, 0, 0);
    store.setObjectiveSettingsObjective(objectiveID, 0);
    store.setObjectiveNominalMagnification(new Integer(magnification), 0, 0);

    if (correction == null) correction = "Unknown";
    store.setObjectiveCorrection(correction, 0, 0);
    store.setObjectiveLensNA(new Double(lensNA), 0, 0);
    store.setObjectiveWorkingDistance(new Double(workingDistance), 0, 0);
    if (immersion == null) immersion = "Unknown";
    store.setObjectiveImmersion(immersion, 0, 0);

    for (int i=0; i<wavelength.size(); i++) {
      String laser = MetadataTools.createLSID("LightSource", 0, i);
      store.setLightSourceID(laser, 0, i);
      store.setLightSourceModel(laserIDs.get(i), 0, i);
      store.setLaserWavelength(wavelength.get(i), 0, i);
      store.setLaserType("Unknown", 0, i);
      store.setLaserLaserMedium("Unknown", 0, i);
    }

    for (int i=0; i<gain.size(); i++) {
      store.setDetectorGain(gain.get(i), 0, i);
      store.setDetectorType("Unknown", 0, i);
    }

    for (int c=0; c<getEffectiveSizeC(); c++) {
      store.setLogicalChannelPinholeSize(new Double(pinholeSize), 0, c);
      if (c < exWave.size()) {
        store.setLogicalChannelExWave(exWave.get(c), 0, c);
      }
      if (c < emWave.size()) {
        store.setLogicalChannelEmWave(emWave.get(c), 0, c);
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
