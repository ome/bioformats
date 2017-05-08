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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;

import loci.common.DateTools;
import loci.common.IniList;
import loci.common.IniParser;
import loci.common.IniTable;
import loci.common.RandomAccessInputStream;
import loci.common.xml.BaseHandler;
import loci.common.xml.XMLTools;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;
import ome.xml.model.primitives.Timestamp;

import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.unit.Unit;
import ome.units.UNITS;

import org.xml.sax.Attributes;

/**
 * FEITiffReader is the file format reader for TIFF files produced by various
 * FEI software.
 */
public class FEITiffReader extends BaseTiffReader {

  // -- Constants --

  public static final int SFEG_TAG = 34680;
  public static final int HELIOS_TAG = 34682;
  public static final int TITAN_TAG = 34683;

  private static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss a";
  private static final double MAG_MULTIPLIER = 0.0024388925;

  // -- Fields --

  private String imageName;
  private String imageDescription;
  private String date;
  private String userName;
  private String microscopeModel;
  private Length stageX, stageY, stageZ;
  private Double sizeX, sizeY, timeIncrement;
  private ArrayList<String> detectors;
  private Double magnification;

  // -- Constructor --

  public FEITiffReader() {
    super("FEI TIFF", new String[] {"tif", "tiff"});
    suffixSufficient = false;
    domains = new String[] {FormatTools.SEM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser tp = new TiffParser(stream);
    IFD ifd = tp.getFirstIFD();
    if (ifd == null) return false;
    return ifd.containsKey(SFEG_TAG) || ifd.containsKey(HELIOS_TAG) || ifd.containsKey(TITAN_TAG);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      imageName = null;
      imageDescription = null;
      date = null;
      userName = null;
      microscopeModel = null;
      stageX = stageY = stageZ = null;
      sizeX = sizeY = timeIncrement = null;
      detectors = null;
      magnification = null;
    }
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  @Override
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    boolean helios = ifds.get(0).containsKey(HELIOS_TAG);
    boolean titan = ifds.get(0).containsKey(TITAN_TAG);

    // Helios etc data might have a stray Titan tag
    if (titan && ifds.get(0).getIFDTextValue(TITAN_TAG).trim().isEmpty()) {
      titan = false;
    }

    // Titan data (always?) has an empty Helios tag as well, so the Titan tag is checked first
    String software = "S-FEG";
    if (titan) {
      software = "Titan";
    }
    else if (helios) {
      software = "Helios NanoLab";
    }
    addGlobalMeta("Software", software);

    int tagKey = SFEG_TAG;
    if (titan) {
      tagKey = TITAN_TAG;
    }
    else if (helios) {
      tagKey = HELIOS_TAG;
    }

    String tag = ifds.get(0).getIFDTextValue(tagKey);
    if (tag == null) {
      return;
    }
    tag = tag.trim();
    if (tag.isEmpty()) {
      return;//fall back to regular reader
    }
    // store metadata for later conversion to OME-XML
    if (tag.startsWith("<")) {
      XMLTools.parseXML(tag, new FEIHandler());
    }
    else {
      IniParser parser = new IniParser();
      IniList ini = parser.parseINI(new BufferedReader(new StringReader(tag)));
      detectors = new ArrayList<String>();

      if (helios) {
        IniTable userTable = ini.getTable("User");
        date = userTable.get("Date") + " " + userTable.get("Time");

        if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
          userName = userTable.get("User");

          IniTable systemTable = ini.getTable("System");
          if (systemTable == null) {
            systemTable = ini.getTable("SYSTEM");
          }
          if (systemTable != null) {
            microscopeModel = systemTable.get("SystemType");
          }

          IniTable beamTable = ini.getTable("Beam");
          if (beamTable != null) {
            String beamTableName = beamTable.get("Beam");
            if (beamTableName != null) {
              beamTable = ini.getTable(beamTableName);
            }
          }

          if (beamTable != null) {
            String beamX = beamTable.get("StageX");
            String beamY = beamTable.get("StageY");
            String beamZ = beamTable.get("StageZ");
            IniTable stageTable = ini.getTable("Stage");

            if (beamX != null) {
              final Double number = Double.valueOf(beamX);
              stageX = new Length(number, UNITS.REFERENCEFRAME);
            }
            else if (stageTable != null) {
              final Double number = Double.valueOf(stageTable.get("StageX"));
              stageX = new Length(number, UNITS.REFERENCEFRAME);
            }

            if (beamY != null) {
              final Double number = Double.valueOf(beamY);
              stageY = new Length(number, UNITS.REFERENCEFRAME);
            }
            else if (stageTable != null) {
              final Double number = Double.valueOf(stageTable.get("StageY"));
              stageY = new Length(number, UNITS.REFERENCEFRAME);
            }

            if (beamZ != null) {
              final Double number = Double.valueOf(beamZ);
              stageZ = new Length(number, UNITS.REFERENCEFRAME);
            }
            else if (stageTable != null) {
              final Double number = Double.valueOf(stageTable.get("StageZ"));
              stageZ = new Length(number, UNITS.REFERENCEFRAME);
            }
          }

          IniTable scanTable = ini.getTable("Scan");
 
          sizeX = new Double(scanTable.get("PixelWidth"));
          sizeY = new Double(scanTable.get("PixelHeight"));
          timeIncrement = new Double(scanTable.get("FrameTime"));
        }
      }
      else {
        IniTable dataTable = ini.getTable("DatabarData");
        imageName = dataTable.get("ImageName");
        imageDescription = dataTable.get("szUserText");

        String magnification = ini.getTable("Vector").get("Magnification");
        sizeX = new Double(magnification) * MAG_MULTIPLIER;
        sizeY = new Double(magnification) * MAG_MULTIPLIER;

        IniTable scanTable = ini.getTable("Vector.Sysscan");
        final Double posX = Double.valueOf(scanTable.get("PositionX"));
        final Double posY = Double.valueOf(scanTable.get("PositionY"));
        stageX = new Length(posX, UNITS.REFERENCEFRAME);
        stageY = new Length(posY, UNITS.REFERENCEFRAME);

        IniTable detectorTable = ini.getTable("Vector.Video.Detectors");
        int detectorCount =
          Integer.parseInt(detectorTable.get("NrDetectorsConnected"));
        for (int i=0; i<detectorCount; i++) {
          detectors.add(detectorTable.get("Detector_" + i + "_Name"));
        }
      }

      // store everything else in the metadata hashtable

      if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
        HashMap<String, String> iniMap = ini.flattenIntoHashMap();
        metadata.putAll(iniMap);
      }
    }
  }

  /* @see BaseTiffReader#initMetadataStore() */
  @Override
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    if (date != null) {
      date = DateTools.formatDate(date, DATE_FORMAT);
      if (date != null) {
        store.setImageAcquisitionDate(new Timestamp(date), 0);
      }
    }

    if (imageName != null) {
      store.setImageName(imageName, 0);
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      if (imageDescription != null) {
        store.setImageDescription(imageDescription, 0);
      }
      if (userName != null) {
        store.setExperimenterID(MetadataTools.createLSID("Experimenter", 0), 0);
        store.setExperimenterLastName(userName, 0);
      }
      if (microscopeModel != null) {
        String instrument = MetadataTools.createLSID("Instrument", 0);
        store.setInstrumentID(instrument, 0);
        store.setImageInstrumentRef(instrument, 0);
        store.setMicroscopeModel(microscopeModel, 0);
      }
      if (detectors != null && detectors.size() > 0) {
        String instrument = MetadataTools.createLSID("Instrument", 0);
        store.setInstrumentID(instrument, 0);
        store.setImageInstrumentRef(instrument, 0);

        for (int i=0; i<detectors.size(); i++) {
          String detectorID = MetadataTools.createLSID("Detector", 0, i);
          store.setDetectorID(detectorID, 0, i);
          store.setDetectorModel(detectors.get(i), 0, i);
          store.setDetectorType(getDetectorType("Other"), 0, i);
        }
      }
      if (magnification != null) {
        store.setObjectiveID(MetadataTools.createLSID("Objective", 0, 0), 0, 0);
        store.setObjectiveNominalMagnification(magnification, 0, 0);
        store.setObjectiveCorrection(getCorrection("Other"), 0, 0);
        store.setObjectiveImmersion(getImmersion("Other"), 0, 0);
      }

      store.setStageLabelX(stageX, 0);
      store.setStageLabelY(stageY, 0);
      store.setStageLabelZ(stageZ, 0);
      store.setStageLabelName("", 0);

      boolean helios = ifds.get(0).containsKey(HELIOS_TAG);
      Unit<Length> unit = UNITS.MICROMETER;
      if (helios) {
        unit = UNITS.METER;
      }
      Length physicalSizeX = FormatTools.getPhysicalSizeX(sizeX, unit);
      Length physicalSizeY = FormatTools.getPhysicalSizeY(sizeY, unit);

      if (physicalSizeX != null) {
        store.setPixelsPhysicalSizeX(physicalSizeX, 0);
      }
      if (physicalSizeY != null) {
        store.setPixelsPhysicalSizeY(physicalSizeY, 0);
      }
      if (timeIncrement != null) {
        store.setPixelsTimeIncrement(new Time(timeIncrement, UNITS.SECOND), 0);
      }
    }
  }

  // -- Helper class --

  class FEIHandler extends BaseHandler {
    private StringBuilder sb;
    private String key, value;
    private Deque<String> parentNames = new ArrayDeque<String>();

    // -- DefaultHandler API methods --

    @Override
    public void characters(char[] data, int start, int len) {
      sb.append(data, start, len);
    }

    @Override
    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      parentNames.push(qName);
      sb = new StringBuilder();
    }

    @Override
    public void endElement(String uri, String localName, String qName)
    {
      processElement(qName);
      if (parentNames.size() > 0) {
        String name = parentNames.peek();
        if (qName.equals(name)) {
          parentNames.pop();
        }
      }
    }

    private void processElement(String qName) {
      String d = sb.toString().trim();
      if (d.isEmpty()) {
        return;
      }
      String parent = parentNames.peek();
      if (parent == null) {
        return;
      }
      if (parent.equals(qName)) {
        parentNames.pop();
        parent = parentNames.peek();
      }
      if (qName.equals("Label")) {
        key = d;
        value = null;
      }
      else if (qName.equals("Value")) {
        value = d;
      }
      else {
        key = parent + " " + qName;
        value = d;
      }

      if (key != null && value != null) {
        addGlobalMeta(key, value);

        if (key.equals("Stage X") || key.equals("StagePosition X")) {
          final Double number = Double.valueOf(value);
          stageX = new Length(number, UNITS.REFERENCEFRAME);
        }
        else if (key.equals("Stage Y") || key.equals("StagePosition Y")) {
          final Double number = Double.valueOf(value);
          stageY = new Length(number, UNITS.REFERENCEFRAME);
        }
        else if (key.equals("Stage Z") || key.equals("StagePosition Z")) {
          final Double number = Double.valueOf(value);
          stageZ = new Length(number, UNITS.REFERENCEFRAME);
        }
        else if (key.equals("Microscope")) {
          microscopeModel = value;
        }
        else if (key.equals("User")) {
          userName = value;
        }
        else if (key.equals("Magnification")) {
          magnification = new Double(value);
        }

        else if (key.endsWith("X") && "PixelSize".equals(parent)) {
          sizeX = new Double(value);
        }
        else if (key.endsWith("Y") && "PixelSize".equals(parent)) {
          sizeY = new Double(value);
        }
      }
    }

  }

}
