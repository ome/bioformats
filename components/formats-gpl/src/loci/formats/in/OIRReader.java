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
import javax.xml.parsers.ParserConfigurationException;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.common.xml.BaseHandler;
import loci.common.xml.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.Timestamp;
import ome.units.UNITS;
import ome.units.quantity.Length;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * OIRReader is the file format reader for Olympus .oir files.
 */
public class OIRReader extends FormatReader {

  // -- Constants --


  // -- Fields --

  private ArrayList<Long> pixelBlocks = new ArrayList<Long>();
  private ArrayList<Channel> channels = new ArrayList<Channel>();
  private ArrayList<Laser> lasers = new ArrayList<Laser>();
  private ArrayList<Detector> detectors = new ArrayList<Detector>();
  private ArrayList<Objective> objectives = new ArrayList<Objective>();
  private Length physicalSizeX;
  private Length physicalSizeY;
  private Length physicalSizeZ;
  private Timestamp acquisitionDate;

  // -- Constructor --

  /** Constructs a new OIR reader. */
  public OIRReader() {
    super("Olympus OIR", "oir");
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int nextPointer = 0;
    int blocksPerChannel = pixelBlocks.size() / channels.size();
    int blocksPerPlane = blocksPerChannel / (getImageCount() / channels.size());

    int[] zct = getZCTCoords(no);
    int newNo = getIndex(zct[0], 0, zct[2]);
    int start = newNo * blocksPerPlane + zct[1];
    int end = start + (blocksPerPlane * channels.size());
    for (int i=start; i<end; i+=channels.size()) {
      Long offset = pixelBlocks.get(i);
      byte[] pixels = readPixelBlock(offset, false);
      if (pixels != null) {
        int length = (int) Math.min(pixels.length, buf.length - nextPointer);
        if (length > 0 && nextPointer < buf.length) {
          System.arraycopy(pixels, 0, buf, nextPointer, length);
          nextPointer += length;
        }
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      pixelBlocks.clear();
      channels.clear();
      lasers.clear();
      detectors.clear();
      objectives.clear();
      physicalSizeX = null;
      physicalSizeY = null;
      physicalSizeZ = null;
      acquisitionDate = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    in.order(true);

    CoreMetadata m = core.get(0);
    m.littleEndian = true;
    m.sizeZ = 1;
    m.sizeC = 1;
    m.sizeT = 1;

    long baseOffset = 16;
    in.seek(baseOffset);
    while (in.readInt() != 0xffffffff);
    in.skipBytes(4);

    // seek past reference image blocks
    while (skipPixelBlock(false));

    readXMLBlock();

    while (skipPixelBlock(true));

    readXMLBlock();

    m.sizeC *= channels.size();
    m.dimensionOrder = "XYCZT";
    m.imageCount = getSizeC() * getSizeZ() * getSizeT();

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    String instrumentID = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrumentID, 0);
    store.setImageInstrumentRef(instrumentID, 0);

    for (int i=0; i<lasers.size(); i++) {
      Laser l = lasers.get(i);
      String lsid = MetadataTools.createLSID("LightSource", 0, i);
      store.setLaserID(lsid, 0, i);
      store.setLaserModel(l.id, 0, i);
      if (l.wavelength != null) {
        store.setLaserWavelength(FormatTools.getWavelength(l.wavelength, null), 0, i);
      }
    }

    for (int i=0; i<detectors.size(); i++) {
      Detector detector = detectors.get(i);
      String lsid = MetadataTools.createLSID("Detector", 0, i);
      store.setDetectorID(lsid, 0, i);
      store.setDetectorOffset(detector.offset, 0, i);
      store.setDetectorVoltage(FormatTools.createElectricPotential(detector.voltage, UNITS.VOLT), 0, i);
      store.setDetectorGain(detector.gain, 0, i);
    }

    for (int i=0; i<objectives.size(); i++) {
      Objective objective = objectives.get(i);
      String lsid = MetadataTools.createLSID("Objective", 0, i);
      store.setObjectiveID(lsid, 0, i);
      store.setObjectiveModel(objective.name, 0, i);
      store.setObjectiveNominalMagnification(objective.magnification, 0, i);
      store.setObjectiveLensNA(objective.na, 0, i);
      store.setObjectiveWorkingDistance(FormatTools.createLength(objective.wd, UNITS.MILLIMETRE), 0, i);

      if (i == 0) {
        store.setObjectiveSettingsID(lsid, 0);
        store.setObjectiveSettingsRefractiveIndex(objective.ri, 0);
      }
    }

    if (acquisitionDate != null) {
      store.setImageAcquisitionDate(acquisitionDate, 0);
    }

    if (physicalSizeX != null) {
      store.setPixelsPhysicalSizeX(physicalSizeX, 0);
    }
    if (physicalSizeY != null) {
      store.setPixelsPhysicalSizeY(physicalSizeY, 0);
    }
    if (physicalSizeZ != null) {
      store.setPixelsPhysicalSizeZ(physicalSizeZ, 0);
    }

    for (int c=0; c<channels.size(); c++) {
      Channel ch = channels.get(c);
      store.setChannelName(ch.name, 0, c);

      for (int d=0; d<detectors.size(); d++) {
        if (detectors.get(d).channelId.equals(ch.id)) {
          store.setDetectorSettingsID(MetadataTools.createLSID("Detector", 0, d), 0, c);
        }
      }

      if (ch.laserIndex >= 0 && ch.laserIndex < lasers.size()) {
        String laserId = MetadataTools.createLSID("LightSource", 0, ch.laserIndex);
        store.setChannelLightSourceSettingsID(laserId, 0, c);
      }
    }
  }

  // -- Helper methods --

  private void readXMLBlock() throws FormatException, IOException {
    // total block length could include multiple strings of XML
    int totalBlockLength = in.readInt();
    long end = in.getFilePointer() + totalBlockLength - 4;
    in.skipBytes(4);

    while (in.getFilePointer() < end) {
      in.skipBytes(36);
      int xmlLength = in.readInt();
      if (xmlLength <= 32) {
        xmlLength = in.readInt();
        String uid = in.readString(xmlLength);
        xmlLength = in.readInt();
      }
      else if (xmlLength < 0 || xmlLength >= (in.length() - in.getFilePointer())) {
        in.seek(in.getFilePointer() - 40);
        xmlLength = in.readInt();
        if (xmlLength <= 0 || xmlLength + in.getFilePointer() > in.length()) {
          return;
        }
        String uid = in.readString(xmlLength);
        xmlLength = in.readInt();
      }

      if (xmlLength <= 32) {
        break;
      }
      String xml = in.readString(xmlLength).trim();
      LOGGER.debug("xml = {}", xml);
      parseXML(xml);
    }
  }

  private void parseXML(String xml) throws FormatException, IOException {
    Element root = null;
    try {
      root = XMLTools.parseDOM(xml).getDocumentElement();
    }
    catch (ParserConfigurationException e) {
      LOGGER.warn("Could not parse XML", e);
      return;
    }
    catch (SAXException e) {
      LOGGER.warn("Could not parse XML", e);
      return;
    }

    if (root != null) {
      String name = root.getNodeName();
      if ("lsmimage:imageProperties".equals(name)) {
        parseImageProperties(root);
      }
      else if ("lsmframe:frameProperties".equals(name)) {
        parseFrameProperties(root);
      }
    }
  }

  private void parseFrameProperties(Element root) {
    CoreMetadata m = core.get(0);

    Element imageDefinition = getFirstChild(root, "commonframe:imageDefinition");
    if (imageDefinition != null) {
      Element width = getFirstChild(imageDefinition, "base:width");
      Element height = getFirstChild(imageDefinition, "base:height");

      if (width != null) {
        m.sizeX = Integer.parseInt(width.getTextContent());
      }
      if (height != null) {
        m.sizeY = Integer.parseInt(height.getTextContent());
      }
    }
  }

  private void parseImageProperties(Element root) throws FormatException {
    CoreMetadata m = core.get(0);

    Element general = getFirstChild(root, "commonimage:general");
    if (general != null) {
      Element creationDate = getFirstChild(general, "base:creationDateTime");
      if (creationDate != null) {
        String date = creationDate.getTextContent();
        acquisitionDate = new Timestamp(date);
      }
    }

    Element lsm = getFirstChild(root, "commonimage:lsm");
    if (lsm != null) {
      NodeList laserNodes = lsm.getElementsByTagName("commonimage:laser");
      for (int i=0; i<laserNodes.getLength(); i++) {
        Element laser = (Element) laserNodes.item(i);
        Element idNode = getFirstChild(laser, "commonimage:id");
        Element nameNode = getFirstChild(laser, "commonimage:name");
        Laser l = new Laser();
        if (idNode != null) {
          l.id = idNode.getTextContent();
        }
        if (nameNode != null) {
          l.name = nameNode.getTextContent();
        }
        lasers.add(l);
      }
    }

    Element imageInfo = getFirstChild(root, "commonimage:imageInfo");
    if (imageInfo != null) {
      Element width = getFirstChild(imageInfo, "commonimage:width");
      Element height = getFirstChild(imageInfo, "commonimage:height");

      if (width != null && getSizeX() == 0) {
        m.sizeX = Integer.parseInt(width.getTextContent());
      }
      if (height != null && getSizeY() == 0) {
        m.sizeY = Integer.parseInt(height.getTextContent());
      }

      NodeList axisNodes = imageInfo.getElementsByTagName("commonimage:axis");
      if (axisNodes != null) {
        for (int i=0; i<axisNodes.getLength(); i++) {
          parseAxis((Element) axisNodes.item(i));
        }
      }

      NodeList channelNodes = imageInfo.getElementsByTagName("commonphase:channel");
      for (int i=0; i<channelNodes.getLength(); i++) {
        Channel c = new Channel();
        Element channelNode = (Element) channelNodes.item(i);

        c.id = channelNode.getAttribute("id");
        int index = Integer.parseInt(channelNode.getAttribute("order")) - 1;

        Element name = getFirstChild(channelNode, "commonphase:name");
        if (name != null) {
          c.name = name.getTextContent();
        }

        Element imageDefinition = getFirstChild(channelNode, "commonphase:imageDefinition");
        if (imageDefinition != null) {
          Element depth = getFirstChild(imageDefinition, "commonphase:depth");
          Element bitCount = getFirstChild(imageDefinition, "commonphase:bitCounts");

          if (depth != null) {
            int bytes = Integer.parseInt(depth.getTextContent());
            m.pixelType = FormatTools.pixelTypeFromBytes(bytes, false, false);
          }
          if (bitCount != null) {
            m.bitsPerPixel = Integer.parseInt(bitCount.getTextContent());
          }
        }

        Element length = getFirstChild(channelNode, "commonphase:length");
        Element pixelUnit = getFirstChild(channelNode, "commonphase:pixelUnit");
        if (length != null) {
          Element xLength = getFirstChild(length, "commonparam:x");
          Element xUnit = getFirstChild(pixelUnit, "commonphase:x");

          if (xLength != null) {
            Double x = DataTools.parseDouble(xLength.getTextContent());
            String unit = null;
            if (xUnit != null) {
              unit = xUnit.getTextContent();
            }
            physicalSizeX = FormatTools.getPhysicalSize(x, unit);
          }

          Element yLength = getFirstChild(length, "commonparam:y");
          Element yUnit = getFirstChild(pixelUnit, "commonphase:y");

          if (yLength != null) {
            Double y = DataTools.parseDouble(yLength.getTextContent());
            String unit = null;
            if (yUnit != null) {
              unit = yUnit.getTextContent();
            }
            physicalSizeY = FormatTools.getPhysicalSize(y, unit);
          }

          Element zLength = getFirstChild(length, "commonparam:z");
          Element zUnit = getFirstChild(pixelUnit, "commonphase:z");

          if (zLength != null) {
            Double z = DataTools.parseDouble(zLength.getTextContent());
            String unit = null;
            if (zUnit != null) {
              unit = zUnit.getTextContent();
            }
            physicalSizeZ = FormatTools.getPhysicalSize(z, unit);
          }
        }

        while (index > channels.size()) {
          channels.add(null);
        }
        if (index == channels.size()) {
          channels.add(c);
        }
        else {
          channels.set(index, c);
        }
      }
    }

    Element acquisition = getFirstChild(root, "commonimage:acquisition");
    if (acquisition != null) {
      Element microscopeConfiguration = getFirstChild(acquisition, "commonimage:microscopeConfiguration");
      if (microscopeConfiguration != null) {
        NodeList objectiveLenses = microscopeConfiguration.getElementsByTagName("commonimage:objectiveLens");

        if (objectiveLenses != null) {
          for (int i=0; i<objectiveLenses.getLength(); i++) {
            Element lens = (Element) objectiveLenses.item(i);
            Objective objective = new Objective();

            Element lensName = getFirstChild(lens, "opticalelement:displayName");
            Element magnification = getFirstChild(lens, "opticalelement:magnification");
            Element na = getFirstChild(lens, "opticalelement:naValue");
            Element wd = getFirstChild(lens, "opticalelement:wdValue");
            Element refraction = getFirstChild(lens, "opticalelement:refraction");

            if (lensName != null) {
              objective.name = lensName.getTextContent();
            }
            if (magnification != null) {
              objective.magnification = DataTools.parseDouble(magnification.getTextContent());
            }
            if (na != null) {
              objective.na = DataTools.parseDouble(na.getTextContent());
            }
            if (wd != null) {
              objective.wd = DataTools.parseDouble(wd.getTextContent());
            }
            if (refraction != null) {
              objective.ri = DataTools.parseDouble(refraction.getTextContent());
            }

            objectives.add(objective);
          }
        }
      }


      Element imagingParam = getFirstChild(acquisition, "commonimage:imagingParam");

      if (imagingParam != null) {
        NodeList axes = imagingParam.getElementsByTagName("commonparam:axis");

        if (axes != null) {
          for (int i=0; i<axes.getLength(); i++) {
            Element dimensionAxis = (Element) axes.item(i);
            if (dimensionAxis.hasAttribute("enable") &&
              dimensionAxis.getAttribute("enable").equals("true") &&
              (!dimensionAxis.hasAttribute("paramEnable") ||
              dimensionAxis.getAttribute("paramEnable").equals("true")))
            {
              parseAxis(dimensionAxis);
            }
          }
        }

        NodeList pmts = imagingParam.getElementsByTagName("lsmparam:pmt");
        if (pmts != null) {
          for (int i=0; i<pmts.getLength(); i++) {
            Element pmt = (Element) pmts.item(i);
            Detector detector = new Detector();

            detector.id = pmt.getAttribute("detectorId");
            detector.channelId = pmt.getAttribute("channelId");

            Element voltage = getFirstChild(pmt, "lsmparam:voltage");
            Element offset = getFirstChild(pmt, "lsmparam:offset");
            Element gain = getFirstChild(pmt,  "lsmparam:gain");

            if (voltage != null) {
              detector.voltage = DataTools.parseDouble(voltage.getTextContent());
            }
            if (offset != null) {
              detector.offset = DataTools.parseDouble(offset.getTextContent());
            }
            if (gain != null) {
              detector.gain = DataTools.parseDouble(gain.getTextContent());
            }

            detectors.add(detector);
          }
        }

        NodeList mainLasers = imagingParam.getElementsByTagName("lsmparam:mainLaser");
        if (mainLasers != null) {
          for (int i=0; i<mainLasers.getLength(); i++) {
            Element mainLaser = (Element) mainLasers.item(i);

            String id = mainLaser.getAttribute("laserDataId");
            Laser currentLaser = null;
            for (int laser=0; laser<lasers.size(); laser++) {
              if (id.startsWith(lasers.get(laser).id)) {
                currentLaser = lasers.get(laser);
                break;
              }
            }
            if (currentLaser == null) {
              continue;
            }

            currentLaser.dataId = id;

            Element power = getFirstChild(mainLaser, "commonparam:power");
            Element transmissivity = getFirstChild(mainLaser, "commonparam:transmissivity");

            if (power != null) {
              currentLaser.power = DataTools.parseDouble(power.getTextContent());
            }
            if (transmissivity != null) {
              currentLaser.transmissivity = DataTools.parseDouble(transmissivity.getTextContent());
            }
          }
        }

      }

      NodeList imagingMainLasers = acquisition.getElementsByTagName("lsmimage:imagingMainLaser");
      if (imagingMainLasers != null) {
        for (int i=0; i<imagingMainLasers.getLength(); i++) {
          Element mainLaser = (Element) imagingMainLasers.item(i);

          String id = mainLaser.getAttribute("id");
          String enable = mainLaser.getAttribute("enable");

          if ("true".equals(enable)) {
            Element wavelength = getFirstChild(mainLaser, "commonimage:wavelength");
            if (wavelength != null) {
              for (Laser l : lasers) {
                if (id.equals(l.dataId)) {
                  l.wavelength = DataTools.parseDouble(wavelength.getTextContent());
                }
              }
            }
          }
        }
      }

      NodeList channelLinkages = acquisition.getElementsByTagName("commonphase:channel");
      if (channelLinkages != null) {
        for (int i=0; i<channelLinkages.getLength(); i++) {
          Element channel = (Element) channelLinkages.item(i);
          String id = channel.getAttribute("id");
          NodeList laserIds = channel.getElementsByTagName("lsmimage:laserDataId");
          // do not link the laser to the channel if multiple linkages are present (e.g. lambda)
          if (laserIds == null || laserIds.getLength() > 1) {
            continue;
          }
          Element laserId = (Element) laserIds.item(0);

          if (id != null && laserId != null) {
            for (Channel ch : channels) {
              if (ch.id.equals(id)) {
                for (int l=0; l<lasers.size(); l++) {
                  Laser laser = lasers.get(l);
                  if (laser.dataId.equals(laserId.getTextContent())) {
                    ch.laserIndex = l;
                    break;
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private void parseAxis(Element dimensionAxis) {
    CoreMetadata m = core.get(0);
    Element axis = getFirstChild(dimensionAxis, "commonparam:axis");
    Element size = getFirstChild(dimensionAxis, "commonparam:maxSize");
    Element start = getFirstChild(dimensionAxis, "commonparam:startPosition");
    Element end = getFirstChild(dimensionAxis, "commonparam:endPosition");
    Element step = getFirstChild(dimensionAxis, "commonparam:step");

    if (axis != null && size != null) {
      String name = axis.getTextContent();

      if (name.equals("ZSTACK")) {
        if (m.sizeZ <= 1) {
          m.sizeZ = Integer.parseInt(size.getTextContent());
        }
      }
      else if (name.equals("TIMELAPSE")) {
        if (m.sizeT <= 1) {
          m.sizeT = Integer.parseInt(size.getTextContent());
        }
      }
      else if (name.equals("LAMBDA")) {
        m.sizeC = Integer.parseInt(size.getTextContent());
        m.moduloC.type = FormatTools.SPECTRA;
        m.moduloC.start = DataTools.parseDouble(start.getTextContent());
        m.moduloC.step = DataTools.parseDouble(step.getTextContent());
        // calculate the end point, as the stored value may be too large
        m.moduloC.end = m.moduloC.start + m.moduloC.step * (m.sizeC - 1);
      }
      else {
        LOGGER.warn("Unhandled axis '{}'", name);
      }
    }
  }

  private Element getFirstChild(Element root, String tag) {
    if (root == null || tag == null) {
      return null;
    }
    NodeList list = root.getElementsByTagName(tag);
    if (list == null || list.getLength() == 0) {
      return null;
    }
    return (Element) list.item(0);
  }

  private boolean skipPixelBlock(boolean store) throws IOException {
    long offset = in.getFilePointer();
    if (offset + 8 >= in.length()) {
      return false;
    }
    int checkLength = in.readInt();
    int check = in.readInt();
    if (check != 3) {
      in.seek(offset);
      if (check == 2) {
        in.seek(offset + checkLength + 8);
        return true;
      }
      return false;
    }

    in.skipBytes(8);
    int uidLength = in.readInt();
    if (checkLength != uidLength + 12) {
      in.seek(offset);
      return false;
    }
    String uid = in.readString(uidLength);
    if (store) {
      LOGGER.debug("pixel uid = {} @ {}", uid, offset);
    }
    if (in.getFilePointer() + 4 >= in.length()) {
      return false;
    }
    if (store) {
      pixelBlocks.add(offset);
    }

    int pixelBytes = in.readInt();
    in.skipBytes(4);

    if (pixelBytes <= 0) {
      if (store) {
        pixelBlocks.remove(pixelBlocks.size() - 1);
      }
      return false;
    }
    in.skipBytes(pixelBytes);
    return true;
  }

  private byte[] readPixelBlock(long offset, boolean skip) throws IOException {
    in.seek(offset);

    int checkLength = in.readInt();
    int check = in.readInt();
    if (check != 3) {
      in.seek(offset);
      return null;
    }

    in.skipBytes(8); // currently unknown

    int uidLength = in.readInt();
    String uid = in.readString(uidLength);
    LOGGER.debug("reading pixel block with uid = {}", uid);
    if (checkLength != uidLength + 12) {
      in.seek(offset);
      return null;
    }
    if (skip) {
      if (in.getFilePointer() + 4 < in.length()) {
        pixelBlocks.add(offset);
      }
      else {
        return null;
      }
    }

    int pixelBytes = in.readInt();

    in.skipBytes(4); // currently unknown

    if (skip) {
      if (pixelBytes <= 0) {
        pixelBlocks.remove(pixelBlocks.size() - 1);
      }
      in.skipBytes(pixelBytes);
      return null;
    }

    byte[] pixels = new byte[pixelBytes];
    in.readFully(pixels);
    return pixels;
  }

  // -- Helper classes --

  class Channel {
    public String id;
    public String name;
    public int laserIndex = -1;
  }

  class Laser {
    public String id;
    public String dataId;
    public String name;
    public Double power;
    public Double transmissivity;
    public Double wavelength;
  }

  class Detector {
    public String id;
    public String channelId;
    public Double voltage;
    public Double offset;
    public Double gain;
  }

  class Objective {
    public String name;
    public Double magnification;
    public Double na;
    public Double wd;
    public Double ri;
  }

}
