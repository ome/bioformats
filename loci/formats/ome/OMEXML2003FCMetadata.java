//
// OMEXML2003FCMetadata.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by curtis via MetadataAutogen on Mar 20, 2008 12:34:36 PM CDT
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.ome;

import org.openmicroscopy.xml.*;
import org.openmicroscopy.xml.st.*;
import java.util.List;
import loci.formats.LogTools;

/**
 * A metadata store implementation for constructing and manipulating OME-XML
 * DOMs for the 2003-FC version of OME-XML. It requires the
 * org.openmicroscopy.xml package to compile (part of ome-java.jar).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/ome/OMEXML2003FCMetadata.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/ome/OMEXML2003FCMetadata.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 */
public class OMEXML2003FCMetadata extends OMEXMLMetadata {

  // -- OMEXMLMetadata API methods --

  /* @see OMEXMLMetadata#dumpXML() */
  public String dumpXML() {
    if (root == null) return null;
    try { return ((OMENode) root).writeOME(false); }
    catch (javax.xml.transform.TransformerException exc) {
      LogTools.trace(exc);
    }
    catch (java.io.IOException exc) { LogTools.trace(exc); }
    catch (org.xml.sax.SAXException exc) { LogTools.trace(exc); }
    catch (javax.xml.parsers.ParserConfigurationException exc) {
      LogTools.trace(exc);
    }
    return null;
  }

  // -- MetadataRetrieve API methods --

  // - Entity counting -

  /* @see loci.formats.meta.MetadataRetrieve#getExperimenterCount() */
  public int getExperimenterCount() {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getExperimenter(i) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getImageCount() */
  public int getImageCount() {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getImage(i) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelCount(int) */
  public int getLogicalChannelCount(int imageIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getLogicalChannel(imageIndex, i) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getChannelComponentCount(int, int) */
  public int getChannelComponentCount(int imageIndex, int logicalChannelIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getChannelComponent(imageIndex, logicalChannelIndex, i) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPixelsCount(int) */
  public int getPixelsCount(int imageIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getPixels(imageIndex, i) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPlaneCount(int, int) */
  public int getPlaneCount(int imageIndex, int pixelsIndex) {
    // NB: Plane unsupported for schema version 2003-FC
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROICount(int) */
  public int getROICount(int imageIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getROI(imageIndex, i) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getInstrumentCount() */
  public int getInstrumentCount() {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getInstrument(i) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorCount(int) */
  public int getDetectorCount(int instrumentIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getDetector(instrumentIndex, i) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLightSourceCount(int) */
  public int getLightSourceCount(int instrumentIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getLightSource(instrumentIndex, i) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getOTFCount(int) */
  public int getOTFCount(int instrumentIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getOTF(instrumentIndex, i) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveCount(int) */
  public int getObjectiveCount(int instrumentIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getObjective(instrumentIndex, i) == null) return i;
    }
    return -1;
  }

  // - Entity retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getExperimenter(int) */
  public Object getExperimenter(int experimenterIndex) {
    return getExperimenterNode(experimenterIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getImage(int) */
  public Object getImage(int imageIndex) {
    return getImageNode(imageIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getImagingEnvironment(int) */
  public Object getImagingEnvironment(int imageIndex) {
    return getImagingEnvironmentNode(imageIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDisplayOptions(int) */
  public Object getDisplayOptions(int imageIndex) {
    return getDisplayOptionsNode(imageIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDisplayOptionsProjection(int) */
  public Object getDisplayOptionsProjection(int imageIndex) {
    return getDisplayOptionsNode(imageIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDisplayOptionsTime(int) */
  public Object getDisplayOptionsTime(int imageIndex) {
    return getDisplayOptionsNode(imageIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getStageLabel(int) */
  public Object getStageLabel(int imageIndex) {
    return getStageLabelNode(imageIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannel(int, int) */
  public Object getLogicalChannel(int imageIndex, int logicalChannelIndex) {
    return getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorSettings(int, int) */
  public Object getDetectorSettings(int imageIndex, int logicalChannelIndex) {
    return getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLightSourceSettings(int, int) */
  public Object getLightSourceSettings(int imageIndex, int logicalChannelIndex) {
    return getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getChannelComponent(int, int, int) */
  public Object getChannelComponent(int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
    return getPixelChannelComponentNode(imageIndex, logicalChannelIndex, channelComponentIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPixels(int, int) */
  public Object getPixels(int imageIndex, int pixelsIndex) {
    return getPixelsNode(imageIndex, pixelsIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDimensions(int, int) */
  public Object getDimensions(int imageIndex, int pixelsIndex) {
    return getDimensionsNode(imageIndex, pixelsIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPlane(int, int, int) */
  public Object getPlane(int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: Plane unsupported for schema version 2003-FC
    return null;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPlaneTiming(int, int, int) */
  public Object getPlaneTiming(int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: PlaneTiming unsupported for schema version 2003-FC
    return null;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getStagePosition(int, int, int) */
  public Object getStagePosition(int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: StagePosition unsupported for schema version 2003-FC
    return null;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROI(int, int) */
  public Object getROI(int imageIndex, int roiIndex) {
    return getDisplayROINode(imageIndex, roiIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getInstrument(int) */
  public Object getInstrument(int instrumentIndex) {
    return getInstrumentNode(instrumentIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetector(int, int) */
  public Object getDetector(int instrumentIndex, int detectorIndex) {
    return getDetectorNode(instrumentIndex, detectorIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLightSource(int, int) */
  public Object getLightSource(int instrumentIndex, int lightSourceIndex) {
    return getLightSourceNode(instrumentIndex, lightSourceIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLaser(int, int) */
  public Object getLaser(int instrumentIndex, int lightSourceIndex) {
    return getLaserNode(instrumentIndex, lightSourceIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getFilament(int, int) */
  public Object getFilament(int instrumentIndex, int lightSourceIndex) {
    return getFilamentNode(instrumentIndex, lightSourceIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getArc(int, int) */
  public Object getArc(int instrumentIndex, int lightSourceIndex) {
    return getArcNode(instrumentIndex, lightSourceIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getOTF(int, int) */
  public Object getOTF(int instrumentIndex, int otfIndex) {
    return getOTFNode(instrumentIndex, otfIndex, false);
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjective(int, int) */
  public Object getObjective(int instrumentIndex, int objectiveIndex) {
    return getObjectiveNode(instrumentIndex, objectiveIndex, false);
  }

  // - Experimenter property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getExperimenterNodeID(int) */
  public String getExperimenterNodeID(int experimenterIndex) {
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, false);
    return experimenter == null ? null : experimenter.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getExperimenterFirstName(int) */
  public String getExperimenterFirstName(int experimenterIndex) {
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, false);
    return experimenter == null ? null : experimenter.getFirstName();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getExperimenterLastName(int) */
  public String getExperimenterLastName(int experimenterIndex) {
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, false);
    return experimenter == null ? null : experimenter.getLastName();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getExperimenterEmail(int) */
  public String getExperimenterEmail(int experimenterIndex) {
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, false);
    return experimenter == null ? null : experimenter.getEmail();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getExperimenterInstitution(int) */
  public String getExperimenterInstitution(int experimenterIndex) {
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, false);
    return experimenter == null ? null : experimenter.getInstitution();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getExperimenterDataDirectory(int) */
  public String getExperimenterDataDirectory(int experimenterIndex) {
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, false);
    return experimenter == null ? null : experimenter.getDataDirectory();
  }

  // - Image property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getImageNodeID(int) */
  public String getImageNodeID(int imageIndex) {
    ImageNode image = getImageNode(imageIndex, false);
    return image == null ? null : image.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getImageName(int) */
  public String getImageName(int imageIndex) {
    ImageNode image = getImageNode(imageIndex, false);
    return image == null ? null : image.getName();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getImageCreationDate(int) */
  public String getImageCreationDate(int imageIndex) {
    ImageNode image = getImageNode(imageIndex, false);
    return image == null ? null : image.getCreated();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getImageDescription(int) */
  public String getImageDescription(int imageIndex) {
    ImageNode image = getImageNode(imageIndex, false);
    return image == null ? null : image.getDescription();
  }

  // - ImagingEnvironment property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getImagingEnvironmentTemperature(int) */
  public Float getImagingEnvironmentTemperature(int imageIndex) {
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, false);
    return imagingEnvironment == null ? null : imagingEnvironment.getTemperature();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getImagingEnvironmentAirPressure(int) */
  public Float getImagingEnvironmentAirPressure(int imageIndex) {
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, false);
    return imagingEnvironment == null ? null : imagingEnvironment.getAirPressure();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getImagingEnvironmentHumidity(int) */
  public Float getImagingEnvironmentHumidity(int imageIndex) {
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, false);
    return imagingEnvironment == null ? null : imagingEnvironment.getHumidity();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getImagingEnvironmentCO2Percent(int) */
  public Float getImagingEnvironmentCO2Percent(int imageIndex) {
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, false);
    return imagingEnvironment == null ? null : imagingEnvironment.getCO2Percent();
  }

  // - DisplayOptions property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getDisplayOptionsNodeID(int) */
  public String getDisplayOptionsNodeID(int imageIndex) {
    DisplayOptionsNode displayOptions = getDisplayOptionsNode(imageIndex, false);
    return displayOptions == null ? null : displayOptions.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDisplayOptionsZoom(int) */
  public Float getDisplayOptionsZoom(int imageIndex) {
    DisplayOptionsNode displayOptions = getDisplayOptionsNode(imageIndex, false);
    return displayOptions == null ? null : displayOptions.getZoom();
  }

  // - DisplayOptionsProjection property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getDisplayOptionsProjectionZStart(int) */
  public Integer getDisplayOptionsProjectionZStart(int imageIndex) {
    DisplayOptionsNode displayOptions = getDisplayOptionsNode(imageIndex, false);
    return displayOptions == null ? null : displayOptions.getZStart();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDisplayOptionsProjectionZStop(int) */
  public Integer getDisplayOptionsProjectionZStop(int imageIndex) {
    DisplayOptionsNode displayOptions = getDisplayOptionsNode(imageIndex, false);
    return displayOptions == null ? null : displayOptions.getZStop();
  }

  // - DisplayOptionsTime property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getDisplayOptionsTimeTStart(int) */
  public Integer getDisplayOptionsTimeTStart(int imageIndex) {
    DisplayOptionsNode displayOptions = getDisplayOptionsNode(imageIndex, false);
    return displayOptions == null ? null : displayOptions.getTStart();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDisplayOptionsTimeTStop(int) */
  public Integer getDisplayOptionsTimeTStop(int imageIndex) {
    DisplayOptionsNode displayOptions = getDisplayOptionsNode(imageIndex, false);
    return displayOptions == null ? null : displayOptions.getTStop();
  }

  // - StageLabel property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getStageLabelName(int) */
  public String getStageLabelName(int imageIndex) {
    StageLabelNode stageLabel = getStageLabelNode(imageIndex, false);
    return stageLabel == null ? null : stageLabel.getName();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getStageLabelX(int) */
  public Float getStageLabelX(int imageIndex) {
    StageLabelNode stageLabel = getStageLabelNode(imageIndex, false);
    return stageLabel == null ? null : stageLabel.getX();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getStageLabelY(int) */
  public Float getStageLabelY(int imageIndex) {
    StageLabelNode stageLabel = getStageLabelNode(imageIndex, false);
    return stageLabel == null ? null : stageLabel.getY();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getStageLabelZ(int) */
  public Float getStageLabelZ(int imageIndex) {
    StageLabelNode stageLabel = getStageLabelNode(imageIndex, false);
    return stageLabel == null ? null : stageLabel.getZ();
  }

  // - LogicalChannel property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelNodeID(int, int) */
  public String getLogicalChannelNodeID(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelName(int, int) */
  public String getLogicalChannelName(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getName();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelSamplesPerPixel(int, int) */
  public Integer getLogicalChannelSamplesPerPixel(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getSamplesPerPixel();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelIlluminationType(int, int) */
  public String getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getIlluminationType();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelPinholeSize(int, int) */
  public Integer getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getPinholeSize();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelPhotometricInterpretation(int, int) */
  public String getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getPhotometricInterpretation();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelMode(int, int) */
  public String getLogicalChannelMode(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getMode();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelContrastMethod(int, int) */
  public String getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getContrastMethod();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelExWave(int, int) */
  public Integer getLogicalChannelExWave(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getExcitationWavelength();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelEmWave(int, int) */
  public Integer getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getEmissionWavelength();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelFluor(int, int) */
  public String getLogicalChannelFluor(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getFluor();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelNdFilter(int, int) */
  public Float getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getNDFilter();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelPockelCellSetting(int, int) */
  public Integer getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex) {
    // NB: PockelCellSetting unsupported for schema version 2003-FC
    return null;
  }

  // - DetectorSettings property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorSettingsDetector(int, int) */
  public Object getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getDetector();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorSettingsOffset(int, int) */
  public Float getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getDetectorOffset();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorSettingsGain(int, int) */
  public Float getDetectorSettingsGain(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getDetectorGain();
  }

  // - LightSourceSettings property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getLightSourceSettingsLightSource(int, int) */
  public Object getLightSourceSettingsLightSource(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getLightSource();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLightSourceSettingsAttenuation(int, int) */
  public Float getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getLightAttenuation();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLightSourceSettingsWavelength(int, int) */
  public Integer getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getLightWavelength();
  }

  // - ChannelComponent property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getChannelComponentIndex(int, int, int) */
  public Integer getChannelComponentIndex(int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
    PixelChannelComponentNode pixelChannelComponent = getPixelChannelComponentNode(imageIndex, logicalChannelIndex, channelComponentIndex, false);
    return pixelChannelComponent == null ? null : pixelChannelComponent.getIndex();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getChannelComponentColorDomain(int, int, int) */
  public String getChannelComponentColorDomain(int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
    PixelChannelComponentNode pixelChannelComponent = getPixelChannelComponentNode(imageIndex, logicalChannelIndex, channelComponentIndex, false);
    return pixelChannelComponent == null ? null : pixelChannelComponent.getColorDomain();
  }

  // - Pixels property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getPixelsNodeID(int, int) */
  public String getPixelsNodeID(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPixelsSizeX(int, int) */
  public Integer getPixelsSizeX(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getSizeX();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPixelsSizeY(int, int) */
  public Integer getPixelsSizeY(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getSizeY();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPixelsSizeZ(int, int) */
  public Integer getPixelsSizeZ(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getSizeZ();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPixelsSizeC(int, int) */
  public Integer getPixelsSizeC(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getSizeC();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPixelsSizeT(int, int) */
  public Integer getPixelsSizeT(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getSizeT();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPixelsPixelType(int, int) */
  public String getPixelsPixelType(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getPixelType();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPixelsBigEndian(int, int) */
  public Boolean getPixelsBigEndian(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.isBigEndian();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPixelsDimensionOrder(int, int) */
  public String getPixelsDimensionOrder(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getDimensionOrder();
  }

  // - Dimensions property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getDimensionsPhysicalSizeX(int, int) */
  public Float getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex) {
    DimensionsNode dimensions = getDimensionsNode(imageIndex, pixelsIndex, false);
    return dimensions == null ? null : dimensions.getPixelSizeX();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDimensionsPhysicalSizeY(int, int) */
  public Float getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex) {
    DimensionsNode dimensions = getDimensionsNode(imageIndex, pixelsIndex, false);
    return dimensions == null ? null : dimensions.getPixelSizeY();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDimensionsPhysicalSizeZ(int, int) */
  public Float getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex) {
    DimensionsNode dimensions = getDimensionsNode(imageIndex, pixelsIndex, false);
    return dimensions == null ? null : dimensions.getPixelSizeZ();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDimensionsTimeIncrement(int, int) */
  public Float getDimensionsTimeIncrement(int imageIndex, int pixelsIndex) {
    DimensionsNode dimensions = getDimensionsNode(imageIndex, pixelsIndex, false);
    return dimensions == null ? null : dimensions.getPixelSizeT();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDimensionsWaveStart(int, int) */
  public Integer getDimensionsWaveStart(int imageIndex, int pixelsIndex) {
    // NB: WaveStart unsupported for schema version 2003-FC
    return null;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDimensionsWaveIncrement(int, int) */
  public Integer getDimensionsWaveIncrement(int imageIndex, int pixelsIndex) {
    DimensionsNode dimensions = getDimensionsNode(imageIndex, pixelsIndex, false);
    return dimensions == null ? null : dimensionsPixelSizeCToInteger(dimensions.getPixelSizeC());
  }

  // - Plane property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getPlaneTheZ(int, int, int) */
  public Integer getPlaneTheZ(int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: TheZ unsupported for schema version 2003-FC
    return null;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPlaneTheC(int, int, int) */
  public Integer getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: TheC unsupported for schema version 2003-FC
    return null;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPlaneTheT(int, int, int) */
  public Integer getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: TheT unsupported for schema version 2003-FC
    return null;
  }

  // - PlaneTiming property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getPlaneTimingDeltaT(int, int, int) */
  public Float getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: DeltaT unsupported for schema version 2003-FC
    return null;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPlaneTimingExposureTime(int, int, int) */
  public Float getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: ExposureTime unsupported for schema version 2003-FC
    return null;
  }

  // - StagePosition property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getStagePositionPositionX(int, int, int) */
  public Float getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: PositionX unsupported for schema version 2003-FC
    return null;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getStagePositionPositionY(int, int, int) */
  public Float getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: PositionY unsupported for schema version 2003-FC
    return null;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getStagePositionPositionZ(int, int, int) */
  public Float getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: PositionZ unsupported for schema version 2003-FC
    return null;
  }

  // - ROI property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getROINodeID(int, int) */
  public String getROINodeID(int imageIndex, int roiIndex) {
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, false);
    return displayROI == null ? null : displayROI.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROIX0(int, int) */
  public Integer getROIX0(int imageIndex, int roiIndex) {
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, false);
    return displayROI == null ? null : displayROI.getX0();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROIY0(int, int) */
  public Integer getROIY0(int imageIndex, int roiIndex) {
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, false);
    return displayROI == null ? null : displayROI.getY0();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROIX1(int, int) */
  public Integer getROIX1(int imageIndex, int roiIndex) {
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, false);
    return displayROI == null ? null : displayROI.getX1();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROIY1(int, int) */
  public Integer getROIY1(int imageIndex, int roiIndex) {
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, false);
    return displayROI == null ? null : displayROI.getY1();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROIZ0(int, int) */
  public Integer getROIZ0(int imageIndex, int roiIndex) {
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, false);
    return displayROI == null ? null : displayROI.getZ0();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROIZ1(int, int) */
  public Integer getROIZ1(int imageIndex, int roiIndex) {
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, false);
    return displayROI == null ? null : displayROI.getZ1();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROIT0(int, int) */
  public Integer getROIT0(int imageIndex, int roiIndex) {
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, false);
    return displayROI == null ? null : displayROI.getT0();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROIT1(int, int) */
  public Integer getROIT1(int imageIndex, int roiIndex) {
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, false);
    return displayROI == null ? null : displayROI.getT1();
  }

  // - Instrument property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getInstrumentNodeID(int) */
  public String getInstrumentNodeID(int instrumentIndex) {
    InstrumentNode instrument = getInstrumentNode(instrumentIndex, false);
    return instrument == null ? null : instrument.getNodeID();
  }

  // - Detector property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorNodeID(int, int) */
  public String getDetectorNodeID(int instrumentIndex, int detectorIndex) {
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, false);
    return detector == null ? null : detector.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorManufacturer(int, int) */
  public String getDetectorManufacturer(int instrumentIndex, int detectorIndex) {
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, false);
    return detector == null ? null : detector.getManufacturer();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorModel(int, int) */
  public String getDetectorModel(int instrumentIndex, int detectorIndex) {
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, false);
    return detector == null ? null : detector.getModel();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorSerialNumber(int, int) */
  public String getDetectorSerialNumber(int instrumentIndex, int detectorIndex) {
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, false);
    return detector == null ? null : detector.getSerialNumber();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorType(int, int) */
  public String getDetectorType(int instrumentIndex, int detectorIndex) {
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, false);
    return detector == null ? null : detector.getType();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorGain(int, int) */
  public Float getDetectorGain(int instrumentIndex, int detectorIndex) {
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, false);
    return detector == null ? null : detector.getGain();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorVoltage(int, int) */
  public Float getDetectorVoltage(int instrumentIndex, int detectorIndex) {
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, false);
    return detector == null ? null : detector.getVoltage();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorOffset(int, int) */
  public Float getDetectorOffset(int instrumentIndex, int detectorIndex) {
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, false);
    return detector == null ? null : detector.getOffset();
  }

  // - LightSource property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getLightSourceNodeID(int, int) */
  public String getLightSourceNodeID(int instrumentIndex, int lightSourceIndex) {
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, false);
    return lightSource == null ? null : lightSource.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLightSourceManufacturer(int, int) */
  public String getLightSourceManufacturer(int instrumentIndex, int lightSourceIndex) {
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, false);
    return lightSource == null ? null : lightSource.getManufacturer();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLightSourceModel(int, int) */
  public String getLightSourceModel(int instrumentIndex, int lightSourceIndex) {
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, false);
    return lightSource == null ? null : lightSource.getModel();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLightSourceSerialNumber(int, int) */
  public String getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex) {
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, false);
    return lightSource == null ? null : lightSource.getSerialNumber();
  }

  // - Laser property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getLaserType(int, int) */
  public String getLaserType(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getType();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLaserLaserMedium(int, int) */
  public String getLaserLaserMedium(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getMedium();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLaserWavelength(int, int) */
  public Integer getLaserWavelength(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getWavelength();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLaserFrequencyMultiplication(int, int) */
  public Integer getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laserFrequencyDoubledToInteger(laser.isFrequencyDoubled());
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLaserTuneable(int, int) */
  public Boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.isTunable();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLaserPulse(int, int) */
  public String getLaserPulse(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getPulse();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLaserPower(int, int) */
  public Float getLaserPower(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getPower();
  }

  // - Filament property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getFilamentType(int, int) */
  public String getFilamentType(int instrumentIndex, int lightSourceIndex) {
    FilamentNode filament = getFilamentNode(instrumentIndex, lightSourceIndex, false);
    return filament == null ? null : filament.getType();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getFilamentPower(int, int) */
  public Float getFilamentPower(int instrumentIndex, int lightSourceIndex) {
    FilamentNode filament = getFilamentNode(instrumentIndex, lightSourceIndex, false);
    return filament == null ? null : filament.getPower();
  }

  // - Arc property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getArcType(int, int) */
  public String getArcType(int instrumentIndex, int lightSourceIndex) {
    ArcNode arc = getArcNode(instrumentIndex, lightSourceIndex, false);
    return arc == null ? null : arc.getType();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getArcPower(int, int) */
  public Float getArcPower(int instrumentIndex, int lightSourceIndex) {
    ArcNode arc = getArcNode(instrumentIndex, lightSourceIndex, false);
    return arc == null ? null : arc.getPower();
  }

  // - OTF property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getOTFNodeID(int, int) */
  public String getOTFNodeID(int instrumentIndex, int otfIndex) {
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, false);
    return otf == null ? null : otf.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getOTFSizeX(int, int) */
  public Integer getOTFSizeX(int instrumentIndex, int otfIndex) {
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, false);
    return otf == null ? null : otf.getSizeX();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getOTFSizeY(int, int) */
  public Integer getOTFSizeY(int instrumentIndex, int otfIndex) {
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, false);
    return otf == null ? null : otf.getSizeY();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getOTFPixelType(int, int) */
  public String getOTFPixelType(int instrumentIndex, int otfIndex) {
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, false);
    return otf == null ? null : otf.getPixelType();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getOTFPath(int, int) */
  public String getOTFPath(int instrumentIndex, int otfIndex) {
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, false);
    return otf == null ? null : otf.getPath();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getOTFOpticalAxisAveraged(int, int) */
  public Boolean getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex) {
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, false);
    return otf == null ? null : otf.isOpticalAxisAverage();
  }

  // - Objective property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveNodeID(int, int) */
  public String getObjectiveNodeID(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveManufacturer(int, int) */
  public String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getManufacturer();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveModel(int, int) */
  public String getObjectiveModel(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getModel();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveSerialNumber(int, int) */
  public String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getSerialNumber();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveCorrection(int, int) */
  public String getObjectiveCorrection(int instrumentIndex, int objectiveIndex) {
    // NB: Correction unsupported for schema version 2003-FC
    return null;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveImmersion(int, int) */
  public String getObjectiveImmersion(int instrumentIndex, int objectiveIndex) {
    // NB: Immersion unsupported for schema version 2003-FC
    return null;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveLensNA(int, int) */
  public Float getObjectiveLensNA(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getLensNA();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveNominalMagnification(int, int) */
  public Integer getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex) {
    // NB: NominalMagnification unsupported for schema version 2003-FC
    return null;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveCalibratedMagnification(int, int) */
  public Float getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getMagnification();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveWorkingDistance(int, int) */
  public Float getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex) {
    // NB: WorkingDistance unsupported for schema version 2003-FC
    return null;
  }

  // -- MetadataStore API methods --

  /* @see loci.formats.meta.MetadataStore#setRoot(Object) */
  public void createRoot() {
    try {
      root = new OMENode();
    }
    catch (javax.xml.transform.TransformerException exc) {
      LogTools.trace(exc);
    }
    catch (java.io.IOException exc) { LogTools.trace(exc); }
    catch (org.xml.sax.SAXException exc) { LogTools.trace(exc); }
    catch (javax.xml.parsers.ParserConfigurationException exc) {
      LogTools.trace(exc);
    }
  }

  /* @see loci.formats.meta.MetadataStore#setRoot(Object) */
  public void setRoot(Object root) {
    if (!(root instanceof OMENode)) {
      throw new IllegalArgumentException(
        "Invalid root type: " + root.getClass().getName() + ". " +
        "This metadata store accepts root objects of type " +
        OMENode.class.getName());
    }
    this.root = (OMENode) root;
  }

  // - Experimenter property storage -

  /* @see loci.formats.meta.MetadataStore#setExperimenterNodeID(String, int) */
  public void setExperimenterNodeID(String nodeID, int experimenterIndex) {
    if (nodeID == null) return;
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, true);
    experimenter.setNodeID(nodeID);
  }

  /* @see loci.formats.meta.MetadataStore#setExperimenterFirstName(String, int) */
  public void setExperimenterFirstName(String firstName, int experimenterIndex) {
    if (firstName == null) return;
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, true);
    experimenter.setFirstName(firstName);
  }

  /* @see loci.formats.meta.MetadataStore#setExperimenterLastName(String, int) */
  public void setExperimenterLastName(String lastName, int experimenterIndex) {
    if (lastName == null) return;
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, true);
    experimenter.setLastName(lastName);
  }

  /* @see loci.formats.meta.MetadataStore#setExperimenterEmail(String, int) */
  public void setExperimenterEmail(String email, int experimenterIndex) {
    if (email == null) return;
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, true);
    experimenter.setEmail(email);
  }

  /* @see loci.formats.meta.MetadataStore#setExperimenterInstitution(String, int) */
  public void setExperimenterInstitution(String institution, int experimenterIndex) {
    if (institution == null) return;
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, true);
    experimenter.setInstitution(institution);
  }

  /* @see loci.formats.meta.MetadataStore#setExperimenterDataDirectory(String, int) */
  public void setExperimenterDataDirectory(String dataDirectory, int experimenterIndex) {
    if (dataDirectory == null) return;
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, true);
    experimenter.setDataDirectory(dataDirectory);
  }

  // - Image property storage -

  /* @see loci.formats.meta.MetadataStore#setImageNodeID(String, int) */
  public void setImageNodeID(String nodeID, int imageIndex) {
    if (nodeID == null) return;
    ImageNode image = getImageNode(imageIndex, true);
    image.setNodeID(nodeID);
  }

  /* @see loci.formats.meta.MetadataStore#setImageName(String, int) */
  public void setImageName(String name, int imageIndex) {
    if (name == null) return;
    ImageNode image = getImageNode(imageIndex, true);
    image.setName(name);
  }

  /* @see loci.formats.meta.MetadataStore#setImageCreationDate(String, int) */
  public void setImageCreationDate(String creationDate, int imageIndex) {
    if (creationDate == null) return;
    ImageNode image = getImageNode(imageIndex, true);
    image.setCreated(creationDate);
  }

  /* @see loci.formats.meta.MetadataStore#setImageDescription(String, int) */
  public void setImageDescription(String description, int imageIndex) {
    if (description == null) return;
    ImageNode image = getImageNode(imageIndex, true);
    image.setDescription(description);
  }

  // - ImagingEnvironment property storage -

  /* @see loci.formats.meta.MetadataStore#setImagingEnvironmentTemperature(Float, int) */
  public void setImagingEnvironmentTemperature(Float temperature, int imageIndex) {
    if (temperature == null) return;
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, true);
    imagingEnvironment.setTemperature(temperature);
  }

  /* @see loci.formats.meta.MetadataStore#setImagingEnvironmentAirPressure(Float, int) */
  public void setImagingEnvironmentAirPressure(Float airPressure, int imageIndex) {
    if (airPressure == null) return;
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, true);
    imagingEnvironment.setAirPressure(airPressure);
  }

  /* @see loci.formats.meta.MetadataStore#setImagingEnvironmentHumidity(Float, int) */
  public void setImagingEnvironmentHumidity(Float humidity, int imageIndex) {
    if (humidity == null) return;
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, true);
    imagingEnvironment.setHumidity(humidity);
  }

  /* @see loci.formats.meta.MetadataStore#setImagingEnvironmentCO2Percent(Float, int) */
  public void setImagingEnvironmentCO2Percent(Float cO2Percent, int imageIndex) {
    if (cO2Percent == null) return;
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, true);
    imagingEnvironment.setCO2Percent(cO2Percent);
  }

  // - DisplayOptions property storage -

  /* @see loci.formats.meta.MetadataStore#setDisplayOptionsNodeID(String, int) */
  public void setDisplayOptionsNodeID(String nodeID, int imageIndex) {
    if (nodeID == null) return;
    DisplayOptionsNode displayOptions = getDisplayOptionsNode(imageIndex, true);
    displayOptions.setNodeID(nodeID);
  }

  /* @see loci.formats.meta.MetadataStore#setDisplayOptionsZoom(Float, int) */
  public void setDisplayOptionsZoom(Float zoom, int imageIndex) {
    if (zoom == null) return;
    DisplayOptionsNode displayOptions = getDisplayOptionsNode(imageIndex, true);
    displayOptions.setZoom(zoom);
  }

  // - DisplayOptionsProjection property storage -

  /* @see loci.formats.meta.MetadataStore#setDisplayOptionsProjectionZStart(Integer, int) */
  public void setDisplayOptionsProjectionZStart(Integer zStart, int imageIndex) {
    if (zStart == null) return;
    DisplayOptionsNode displayOptions = getDisplayOptionsNode(imageIndex, true);
    displayOptions.setZStart(zStart);
  }

  /* @see loci.formats.meta.MetadataStore#setDisplayOptionsProjectionZStop(Integer, int) */
  public void setDisplayOptionsProjectionZStop(Integer zStop, int imageIndex) {
    if (zStop == null) return;
    DisplayOptionsNode displayOptions = getDisplayOptionsNode(imageIndex, true);
    displayOptions.setZStop(zStop);
  }

  // - DisplayOptionsTime property storage -

  /* @see loci.formats.meta.MetadataStore#setDisplayOptionsTimeTStart(Integer, int) */
  public void setDisplayOptionsTimeTStart(Integer tStart, int imageIndex) {
    if (tStart == null) return;
    DisplayOptionsNode displayOptions = getDisplayOptionsNode(imageIndex, true);
    displayOptions.setTStart(tStart);
  }

  /* @see loci.formats.meta.MetadataStore#setDisplayOptionsTimeTStop(Integer, int) */
  public void setDisplayOptionsTimeTStop(Integer tStop, int imageIndex) {
    if (tStop == null) return;
    DisplayOptionsNode displayOptions = getDisplayOptionsNode(imageIndex, true);
    displayOptions.setTStop(tStop);
  }

  // - StageLabel property storage -

  /* @see loci.formats.meta.MetadataStore#setStageLabelName(String, int) */
  public void setStageLabelName(String name, int imageIndex) {
    if (name == null) return;
    StageLabelNode stageLabel = getStageLabelNode(imageIndex, true);
    stageLabel.setName(name);
  }

  /* @see loci.formats.meta.MetadataStore#setStageLabelX(Float, int) */
  public void setStageLabelX(Float x, int imageIndex) {
    if (x == null) return;
    StageLabelNode stageLabel = getStageLabelNode(imageIndex, true);
    stageLabel.setX(x);
  }

  /* @see loci.formats.meta.MetadataStore#setStageLabelY(Float, int) */
  public void setStageLabelY(Float y, int imageIndex) {
    if (y == null) return;
    StageLabelNode stageLabel = getStageLabelNode(imageIndex, true);
    stageLabel.setY(y);
  }

  /* @see loci.formats.meta.MetadataStore#setStageLabelZ(Float, int) */
  public void setStageLabelZ(Float z, int imageIndex) {
    if (z == null) return;
    StageLabelNode stageLabel = getStageLabelNode(imageIndex, true);
    stageLabel.setZ(z);
  }

  // - LogicalChannel property storage -

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelNodeID(String, int, int) */
  public void setLogicalChannelNodeID(String nodeID, int imageIndex, int logicalChannelIndex) {
    if (nodeID == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setNodeID(nodeID);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelName(String, int, int) */
  public void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex) {
    if (name == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setName(name);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelSamplesPerPixel(Integer, int, int) */
  public void setLogicalChannelSamplesPerPixel(Integer samplesPerPixel, int imageIndex, int logicalChannelIndex) {
    if (samplesPerPixel == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setSamplesPerPixel(samplesPerPixel);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelIlluminationType(String, int, int) */
  public void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex) {
    if (illuminationType == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setIlluminationType(illuminationType);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelPinholeSize(Integer, int, int) */
  public void setLogicalChannelPinholeSize(Integer pinholeSize, int imageIndex, int logicalChannelIndex) {
    if (pinholeSize == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setPinholeSize(pinholeSize);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelPhotometricInterpretation(String, int, int) */
  public void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex) {
    if (photometricInterpretation == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setPhotometricInterpretation(photometricInterpretation);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelMode(String, int, int) */
  public void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex) {
    if (mode == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setMode(mode);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelContrastMethod(String, int, int) */
  public void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex) {
    if (contrastMethod == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setContrastMethod(contrastMethod);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelExWave(Integer, int, int) */
  public void setLogicalChannelExWave(Integer exWave, int imageIndex, int logicalChannelIndex) {
    if (exWave == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setExcitationWavelength(exWave);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelEmWave(Integer, int, int) */
  public void setLogicalChannelEmWave(Integer emWave, int imageIndex, int logicalChannelIndex) {
    if (emWave == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setEmissionWavelength(emWave);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelFluor(String, int, int) */
  public void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex) {
    if (fluor == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setFluor(fluor);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelNdFilter(Float, int, int) */
  public void setLogicalChannelNdFilter(Float ndFilter, int imageIndex, int logicalChannelIndex) {
    if (ndFilter == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setNDFilter(ndFilter);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelPockelCellSetting(Integer, int, int) */
  public void setLogicalChannelPockelCellSetting(Integer pockelCellSetting, int imageIndex, int logicalChannelIndex) {
    // NB: PockelCellSetting unsupported for schema version 2003-FC
  }

  // - DetectorSettings property storage -

  /* @see loci.formats.meta.MetadataStore#setDetectorSettingsDetector(Object, int, int) */
  public void setDetectorSettingsDetector(Object detector, int imageIndex, int logicalChannelIndex) {
    if (detector == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setDetector((DetectorNode) detector);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorSettingsOffset(Float, int, int) */
  public void setDetectorSettingsOffset(Float offset, int imageIndex, int logicalChannelIndex) {
    if (offset == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setDetectorOffset(offset);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorSettingsGain(Float, int, int) */
  public void setDetectorSettingsGain(Float gain, int imageIndex, int logicalChannelIndex) {
    if (gain == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setDetectorGain(gain);
  }

  // - LightSourceSettings property storage -

  /* @see loci.formats.meta.MetadataStore#setLightSourceSettingsLightSource(Object, int, int) */
  public void setLightSourceSettingsLightSource(Object lightSource, int imageIndex, int logicalChannelIndex) {
    if (lightSource == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setLightSource((LightSourceNode) lightSource);
  }

  /* @see loci.formats.meta.MetadataStore#setLightSourceSettingsAttenuation(Float, int, int) */
  public void setLightSourceSettingsAttenuation(Float attenuation, int imageIndex, int logicalChannelIndex) {
    if (attenuation == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setLightAttenuation(attenuation);
  }

  /* @see loci.formats.meta.MetadataStore#setLightSourceSettingsWavelength(Integer, int, int) */
  public void setLightSourceSettingsWavelength(Integer wavelength, int imageIndex, int logicalChannelIndex) {
    if (wavelength == null) return;
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannel.setLightWavelength(wavelength);
  }

  // - ChannelComponent property storage -

  /* @see loci.formats.meta.MetadataStore#setChannelComponentIndex(Integer, int, int, int) */
  public void setChannelComponentIndex(Integer index, int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
    if (index == null) return;
    PixelChannelComponentNode pixelChannelComponent = getPixelChannelComponentNode(imageIndex, logicalChannelIndex, channelComponentIndex, true);
    pixelChannelComponent.setIndex(index);
  }

  /* @see loci.formats.meta.MetadataStore#setChannelComponentColorDomain(String, int, int, int) */
  public void setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
    if (colorDomain == null) return;
    PixelChannelComponentNode pixelChannelComponent = getPixelChannelComponentNode(imageIndex, logicalChannelIndex, channelComponentIndex, true);
    pixelChannelComponent.setColorDomain(colorDomain);
  }

  // - Pixels property storage -

  /* @see loci.formats.meta.MetadataStore#setPixelsNodeID(String, int, int) */
  public void setPixelsNodeID(String nodeID, int imageIndex, int pixelsIndex) {
    if (nodeID == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setNodeID(nodeID);
  }

  /* @see loci.formats.meta.MetadataStore#setPixelsSizeX(Integer, int, int) */
  public void setPixelsSizeX(Integer sizeX, int imageIndex, int pixelsIndex) {
    if (sizeX == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setSizeX(sizeX);
  }

  /* @see loci.formats.meta.MetadataStore#setPixelsSizeY(Integer, int, int) */
  public void setPixelsSizeY(Integer sizeY, int imageIndex, int pixelsIndex) {
    if (sizeY == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setSizeY(sizeY);
  }

  /* @see loci.formats.meta.MetadataStore#setPixelsSizeZ(Integer, int, int) */
  public void setPixelsSizeZ(Integer sizeZ, int imageIndex, int pixelsIndex) {
    if (sizeZ == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setSizeZ(sizeZ);
  }

  /* @see loci.formats.meta.MetadataStore#setPixelsSizeC(Integer, int, int) */
  public void setPixelsSizeC(Integer sizeC, int imageIndex, int pixelsIndex) {
    if (sizeC == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setSizeC(sizeC);
  }

  /* @see loci.formats.meta.MetadataStore#setPixelsSizeT(Integer, int, int) */
  public void setPixelsSizeT(Integer sizeT, int imageIndex, int pixelsIndex) {
    if (sizeT == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setSizeT(sizeT);
  }

  /* @see loci.formats.meta.MetadataStore#setPixelsPixelType(String, int, int) */
  public void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex) {
    if (pixelType == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setPixelType(pixelType);
  }

  /* @see loci.formats.meta.MetadataStore#setPixelsBigEndian(Boolean, int, int) */
  public void setPixelsBigEndian(Boolean bigEndian, int imageIndex, int pixelsIndex) {
    if (bigEndian == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setBigEndian(bigEndian);
  }

  /* @see loci.formats.meta.MetadataStore#setPixelsDimensionOrder(String, int, int) */
  public void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex) {
    if (dimensionOrder == null) return;
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, true);
    pixels.setDimensionOrder(dimensionOrder);
  }

  // - Dimensions property storage -

  /* @see loci.formats.meta.MetadataStore#setDimensionsPhysicalSizeX(Float, int, int) */
  public void setDimensionsPhysicalSizeX(Float physicalSizeX, int imageIndex, int pixelsIndex) {
    if (physicalSizeX == null) return;
    DimensionsNode dimensions = getDimensionsNode(imageIndex, pixelsIndex, true);
    dimensions.setPixelSizeX(physicalSizeX);
  }

  /* @see loci.formats.meta.MetadataStore#setDimensionsPhysicalSizeY(Float, int, int) */
  public void setDimensionsPhysicalSizeY(Float physicalSizeY, int imageIndex, int pixelsIndex) {
    if (physicalSizeY == null) return;
    DimensionsNode dimensions = getDimensionsNode(imageIndex, pixelsIndex, true);
    dimensions.setPixelSizeY(physicalSizeY);
  }

  /* @see loci.formats.meta.MetadataStore#setDimensionsPhysicalSizeZ(Float, int, int) */
  public void setDimensionsPhysicalSizeZ(Float physicalSizeZ, int imageIndex, int pixelsIndex) {
    if (physicalSizeZ == null) return;
    DimensionsNode dimensions = getDimensionsNode(imageIndex, pixelsIndex, true);
    dimensions.setPixelSizeZ(physicalSizeZ);
  }

  /* @see loci.formats.meta.MetadataStore#setDimensionsTimeIncrement(Float, int, int) */
  public void setDimensionsTimeIncrement(Float timeIncrement, int imageIndex, int pixelsIndex) {
    if (timeIncrement == null) return;
    DimensionsNode dimensions = getDimensionsNode(imageIndex, pixelsIndex, true);
    dimensions.setPixelSizeT(timeIncrement);
  }

  /* @see loci.formats.meta.MetadataStore#setDimensionsWaveStart(Integer, int, int) */
  public void setDimensionsWaveStart(Integer waveStart, int imageIndex, int pixelsIndex) {
    // NB: WaveStart unsupported for schema version 2003-FC
  }

  /* @see loci.formats.meta.MetadataStore#setDimensionsWaveIncrement(Integer, int, int) */
  public void setDimensionsWaveIncrement(Integer waveIncrement, int imageIndex, int pixelsIndex) {
    if (waveIncrement == null) return;
    DimensionsNode dimensions = getDimensionsNode(imageIndex, pixelsIndex, true);
    dimensions.setPixelSizeC(dimensionsPixelSizeCFromInteger(waveIncrement));
  }

  // - Plane property storage -

  /* @see loci.formats.meta.MetadataStore#setPlaneTheZ(Integer, int, int, int) */
  public void setPlaneTheZ(Integer theZ, int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: TheZ unsupported for schema version 2003-FC
  }

  /* @see loci.formats.meta.MetadataStore#setPlaneTheC(Integer, int, int, int) */
  public void setPlaneTheC(Integer theC, int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: TheC unsupported for schema version 2003-FC
  }

  /* @see loci.formats.meta.MetadataStore#setPlaneTheT(Integer, int, int, int) */
  public void setPlaneTheT(Integer theT, int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: TheT unsupported for schema version 2003-FC
  }

  // - PlaneTiming property storage -

  /* @see loci.formats.meta.MetadataStore#setPlaneTimingDeltaT(Float, int, int, int) */
  public void setPlaneTimingDeltaT(Float deltaT, int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: DeltaT unsupported for schema version 2003-FC
  }

  /* @see loci.formats.meta.MetadataStore#setPlaneTimingExposureTime(Float, int, int, int) */
  public void setPlaneTimingExposureTime(Float exposureTime, int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: ExposureTime unsupported for schema version 2003-FC
  }

  // - StagePosition property storage -

  /* @see loci.formats.meta.MetadataStore#setStagePositionPositionX(Float, int, int, int) */
  public void setStagePositionPositionX(Float positionX, int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: PositionX unsupported for schema version 2003-FC
  }

  /* @see loci.formats.meta.MetadataStore#setStagePositionPositionY(Float, int, int, int) */
  public void setStagePositionPositionY(Float positionY, int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: PositionY unsupported for schema version 2003-FC
  }

  /* @see loci.formats.meta.MetadataStore#setStagePositionPositionZ(Float, int, int, int) */
  public void setStagePositionPositionZ(Float positionZ, int imageIndex, int pixelsIndex, int planeIndex) {
    // NB: PositionZ unsupported for schema version 2003-FC
  }

  // - ROI property storage -

  /* @see loci.formats.meta.MetadataStore#setROINodeID(String, int, int) */
  public void setROINodeID(String nodeID, int imageIndex, int roiIndex) {
    if (nodeID == null) return;
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, true);
    displayROI.setNodeID(nodeID);
  }

  /* @see loci.formats.meta.MetadataStore#setROIX0(Integer, int, int) */
  public void setROIX0(Integer x0, int imageIndex, int roiIndex) {
    if (x0 == null) return;
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, true);
    displayROI.setX0(x0);
  }

  /* @see loci.formats.meta.MetadataStore#setROIY0(Integer, int, int) */
  public void setROIY0(Integer y0, int imageIndex, int roiIndex) {
    if (y0 == null) return;
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, true);
    displayROI.setY0(y0);
  }

  /* @see loci.formats.meta.MetadataStore#setROIX1(Integer, int, int) */
  public void setROIX1(Integer x1, int imageIndex, int roiIndex) {
    if (x1 == null) return;
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, true);
    displayROI.setX1(x1);
  }

  /* @see loci.formats.meta.MetadataStore#setROIY1(Integer, int, int) */
  public void setROIY1(Integer y1, int imageIndex, int roiIndex) {
    if (y1 == null) return;
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, true);
    displayROI.setY1(y1);
  }

  /* @see loci.formats.meta.MetadataStore#setROIZ0(Integer, int, int) */
  public void setROIZ0(Integer z0, int imageIndex, int roiIndex) {
    if (z0 == null) return;
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, true);
    displayROI.setZ0(z0);
  }

  /* @see loci.formats.meta.MetadataStore#setROIZ1(Integer, int, int) */
  public void setROIZ1(Integer z1, int imageIndex, int roiIndex) {
    if (z1 == null) return;
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, true);
    displayROI.setZ1(z1);
  }

  /* @see loci.formats.meta.MetadataStore#setROIT0(Integer, int, int) */
  public void setROIT0(Integer t0, int imageIndex, int roiIndex) {
    if (t0 == null) return;
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, true);
    displayROI.setT0(t0);
  }

  /* @see loci.formats.meta.MetadataStore#setROIT1(Integer, int, int) */
  public void setROIT1(Integer t1, int imageIndex, int roiIndex) {
    if (t1 == null) return;
    DisplayROINode displayROI = getDisplayROINode(imageIndex, roiIndex, true);
    displayROI.setT1(t1);
  }

  // - Instrument property storage -

  /* @see loci.formats.meta.MetadataStore#setInstrumentNodeID(String, int) */
  public void setInstrumentNodeID(String nodeID, int instrumentIndex) {
    if (nodeID == null) return;
    InstrumentNode instrument = getInstrumentNode(instrumentIndex, true);
    instrument.setNodeID(nodeID);
  }

  // - Detector property storage -

  /* @see loci.formats.meta.MetadataStore#setDetectorNodeID(String, int, int) */
  public void setDetectorNodeID(String nodeID, int instrumentIndex, int detectorIndex) {
    if (nodeID == null) return;
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, true);
    detector.setNodeID(nodeID);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorManufacturer(String, int, int) */
  public void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex) {
    if (manufacturer == null) return;
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, true);
    detector.setManufacturer(manufacturer);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorModel(String, int, int) */
  public void setDetectorModel(String model, int instrumentIndex, int detectorIndex) {
    if (model == null) return;
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, true);
    detector.setModel(model);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorSerialNumber(String, int, int) */
  public void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex) {
    if (serialNumber == null) return;
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, true);
    detector.setSerialNumber(serialNumber);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorType(String, int, int) */
  public void setDetectorType(String type, int instrumentIndex, int detectorIndex) {
    if (type == null) return;
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, true);
    detector.setType(type);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorGain(Float, int, int) */
  public void setDetectorGain(Float gain, int instrumentIndex, int detectorIndex) {
    if (gain == null) return;
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, true);
    detector.setGain(gain);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorVoltage(Float, int, int) */
  public void setDetectorVoltage(Float voltage, int instrumentIndex, int detectorIndex) {
    if (voltage == null) return;
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, true);
    detector.setVoltage(voltage);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorOffset(Float, int, int) */
  public void setDetectorOffset(Float offset, int instrumentIndex, int detectorIndex) {
    if (offset == null) return;
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, true);
    detector.setOffset(offset);
  }

  // - LightSource property storage -

  /* @see loci.formats.meta.MetadataStore#setLightSourceNodeID(String, int, int) */
  public void setLightSourceNodeID(String nodeID, int instrumentIndex, int lightSourceIndex) {
    if (nodeID == null) return;
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, true);
    lightSource.setNodeID(nodeID);
  }

  /* @see loci.formats.meta.MetadataStore#setLightSourceManufacturer(String, int, int) */
  public void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex) {
    if (manufacturer == null) return;
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, true);
    lightSource.setManufacturer(manufacturer);
  }

  /* @see loci.formats.meta.MetadataStore#setLightSourceModel(String, int, int) */
  public void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex) {
    if (model == null) return;
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, true);
    lightSource.setModel(model);
  }

  /* @see loci.formats.meta.MetadataStore#setLightSourceSerialNumber(String, int, int) */
  public void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex) {
    if (serialNumber == null) return;
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, true);
    lightSource.setSerialNumber(serialNumber);
  }

  // - Laser property storage -

  /* @see loci.formats.meta.MetadataStore#setLaserType(String, int, int) */
  public void setLaserType(String type, int instrumentIndex, int lightSourceIndex) {
    if (type == null) return;
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laser.setType(type);
  }

  /* @see loci.formats.meta.MetadataStore#setLaserLaserMedium(String, int, int) */
  public void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex) {
    if (laserMedium == null) return;
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laser.setMedium(laserMedium);
  }

  /* @see loci.formats.meta.MetadataStore#setLaserWavelength(Integer, int, int) */
  public void setLaserWavelength(Integer wavelength, int instrumentIndex, int lightSourceIndex) {
    if (wavelength == null) return;
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laser.setWavelength(wavelength);
  }

  /* @see loci.formats.meta.MetadataStore#setLaserFrequencyMultiplication(Integer, int, int) */
  public void setLaserFrequencyMultiplication(Integer frequencyMultiplication, int instrumentIndex, int lightSourceIndex) {
    if (frequencyMultiplication == null) return;
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laser.setFrequencyDoubled(laserFrequencyDoubledFromInteger(frequencyMultiplication));
  }

  /* @see loci.formats.meta.MetadataStore#setLaserTuneable(Boolean, int, int) */
  public void setLaserTuneable(Boolean tuneable, int instrumentIndex, int lightSourceIndex) {
    if (tuneable == null) return;
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laser.setTunable(tuneable);
  }

  /* @see loci.formats.meta.MetadataStore#setLaserPulse(String, int, int) */
  public void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex) {
    if (pulse == null) return;
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laser.setPulse(pulse);
  }

  /* @see loci.formats.meta.MetadataStore#setLaserPower(Float, int, int) */
  public void setLaserPower(Float power, int instrumentIndex, int lightSourceIndex) {
    if (power == null) return;
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laser.setPower(power);
  }

  // - Filament property storage -

  /* @see loci.formats.meta.MetadataStore#setFilamentType(String, int, int) */
  public void setFilamentType(String type, int instrumentIndex, int lightSourceIndex) {
    if (type == null) return;
    FilamentNode filament = getFilamentNode(instrumentIndex, lightSourceIndex, true);
    filament.setType(type);
  }

  /* @see loci.formats.meta.MetadataStore#setFilamentPower(Float, int, int) */
  public void setFilamentPower(Float power, int instrumentIndex, int lightSourceIndex) {
    if (power == null) return;
    FilamentNode filament = getFilamentNode(instrumentIndex, lightSourceIndex, true);
    filament.setPower(power);
  }

  // - Arc property storage -

  /* @see loci.formats.meta.MetadataStore#setArcType(String, int, int) */
  public void setArcType(String type, int instrumentIndex, int lightSourceIndex) {
    if (type == null) return;
    ArcNode arc = getArcNode(instrumentIndex, lightSourceIndex, true);
    arc.setType(type);
  }

  /* @see loci.formats.meta.MetadataStore#setArcPower(Float, int, int) */
  public void setArcPower(Float power, int instrumentIndex, int lightSourceIndex) {
    if (power == null) return;
    ArcNode arc = getArcNode(instrumentIndex, lightSourceIndex, true);
    arc.setPower(power);
  }

  // - OTF property storage -

  /* @see loci.formats.meta.MetadataStore#setOTFNodeID(String, int, int) */
  public void setOTFNodeID(String nodeID, int instrumentIndex, int otfIndex) {
    if (nodeID == null) return;
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, true);
    otf.setNodeID(nodeID);
  }

  /* @see loci.formats.meta.MetadataStore#setOTFSizeX(Integer, int, int) */
  public void setOTFSizeX(Integer sizeX, int instrumentIndex, int otfIndex) {
    if (sizeX == null) return;
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, true);
    otf.setSizeX(sizeX);
  }

  /* @see loci.formats.meta.MetadataStore#setOTFSizeY(Integer, int, int) */
  public void setOTFSizeY(Integer sizeY, int instrumentIndex, int otfIndex) {
    if (sizeY == null) return;
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, true);
    otf.setSizeY(sizeY);
  }

  /* @see loci.formats.meta.MetadataStore#setOTFPixelType(String, int, int) */
  public void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex) {
    if (pixelType == null) return;
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, true);
    otf.setPixelType(pixelType);
  }

  /* @see loci.formats.meta.MetadataStore#setOTFPath(String, int, int) */
  public void setOTFPath(String path, int instrumentIndex, int otfIndex) {
    if (path == null) return;
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, true);
    otf.setPath(path);
  }

  /* @see loci.formats.meta.MetadataStore#setOTFOpticalAxisAveraged(Boolean, int, int) */
  public void setOTFOpticalAxisAveraged(Boolean opticalAxisAveraged, int instrumentIndex, int otfIndex) {
    if (opticalAxisAveraged == null) return;
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, true);
    otf.setOpticalAxisAverage(opticalAxisAveraged);
  }

  // - Objective property storage -

  /* @see loci.formats.meta.MetadataStore#setObjectiveNodeID(String, int, int) */
  public void setObjectiveNodeID(String nodeID, int instrumentIndex, int objectiveIndex) {
    if (nodeID == null) return;
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objective.setNodeID(nodeID);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveManufacturer(String, int, int) */
  public void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex) {
    if (manufacturer == null) return;
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objective.setManufacturer(manufacturer);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveModel(String, int, int) */
  public void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex) {
    if (model == null) return;
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objective.setModel(model);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveSerialNumber(String, int, int) */
  public void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex) {
    if (serialNumber == null) return;
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objective.setSerialNumber(serialNumber);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveCorrection(String, int, int) */
  public void setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex) {
    // NB: Correction unsupported for schema version 2003-FC
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveImmersion(String, int, int) */
  public void setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex) {
    // NB: Immersion unsupported for schema version 2003-FC
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveLensNA(Float, int, int) */
  public void setObjectiveLensNA(Float lensNA, int instrumentIndex, int objectiveIndex) {
    if (lensNA == null) return;
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objective.setLensNA(lensNA);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveNominalMagnification(Integer, int, int) */
  public void setObjectiveNominalMagnification(Integer nominalMagnification, int instrumentIndex, int objectiveIndex) {
    // NB: NominalMagnification unsupported for schema version 2003-FC
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveCalibratedMagnification(Float, int, int) */
  public void setObjectiveCalibratedMagnification(Float calibratedMagnification, int instrumentIndex, int objectiveIndex) {
    if (calibratedMagnification == null) return;
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objective.setMagnification(calibratedMagnification);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveWorkingDistance(Float, int, int) */
  public void setObjectiveWorkingDistance(Float workingDistance, int instrumentIndex, int objectiveIndex) {
    // NB: WorkingDistance unsupported for schema version 2003-FC
  }

  // -- Helper methods --

  // CA/Experimenter+
  private ExperimenterNode getExperimenterNode(int experimenterIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get CA node
    CustomAttributesNode ca = ome.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(ome);
    }
    // get Experimenter node
    ndx = experimenterIndex;
    count = ca.countCAList("Experimenter");
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ExperimenterNode(ca);
    list = ca.getCAList("Experimenter");
    ExperimenterNode experimenter = (ExperimenterNode) list.get(ndx);
    return experimenter;
  }

  // Image+
  private ImageNode getImageNode(int imageIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.countImageList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    return image;
  }

  // Image+/CA/ImagingEnvironment
  private ImagingEnvironmentNode getImagingEnvironmentNode(int imageIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.countImageList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    // get CA node
    CustomAttributesNode ca = image.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(image);
    }
    // get ImagingEnvironment node
    ImagingEnvironmentNode imagingEnvironment = null;
    count = ca.countCAList("ImagingEnvironment");
    if (count >= 1) {
      imagingEnvironment = (ImagingEnvironmentNode) ca.getCAList("ImagingEnvironment").get(0);
    }
    if (imagingEnvironment == null) {
      if (!create) return null;
      imagingEnvironment = new ImagingEnvironmentNode(ca);
    }
    return imagingEnvironment;
  }

  // Image+/CA/DisplayOptions
  private DisplayOptionsNode getDisplayOptionsNode(int imageIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.countImageList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    // get CA node
    CustomAttributesNode ca = image.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(image);
    }
    // get DisplayOptions node
    DisplayOptionsNode displayOptions = null;
    count = ca.countCAList("DisplayOptions");
    if (count >= 1) {
      displayOptions = (DisplayOptionsNode) ca.getCAList("DisplayOptions").get(0);
    }
    if (displayOptions == null) {
      if (!create) return null;
      displayOptions = new DisplayOptionsNode(ca);
    }
    return displayOptions;
  }

  // Image+/CA/StageLabel
  private StageLabelNode getStageLabelNode(int imageIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.countImageList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    // get CA node
    CustomAttributesNode ca = image.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(image);
    }
    // get StageLabel node
    StageLabelNode stageLabel = null;
    count = ca.countCAList("StageLabel");
    if (count >= 1) {
      stageLabel = (StageLabelNode) ca.getCAList("StageLabel").get(0);
    }
    if (stageLabel == null) {
      if (!create) return null;
      stageLabel = new StageLabelNode(ca);
    }
    return stageLabel;
  }

  // Image+/CA/LogicalChannel+
  private LogicalChannelNode getLogicalChannelNode(int imageIndex, int logicalChannelIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.countImageList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    // get CA node
    CustomAttributesNode ca = image.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(image);
    }
    // get LogicalChannel node
    ndx = logicalChannelIndex;
    count = ca.countCAList("LogicalChannel");
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new LogicalChannelNode(ca);
    list = ca.getCAList("LogicalChannel");
    LogicalChannelNode logicalChannel = (LogicalChannelNode) list.get(ndx);
    return logicalChannel;
  }

  // Image+/CA/LogicalChannel+/@PixelChannelComponent+
  private PixelChannelComponentNode getPixelChannelComponentNode(int imageIndex, int logicalChannelIndex, int channelComponentIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.countImageList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    // get CA node
    CustomAttributesNode ca = image.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(image);
    }
    // get LogicalChannel node
    ndx = logicalChannelIndex;
    count = ca.countCAList("LogicalChannel");
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new LogicalChannelNode(ca);
    list = ca.getCAList("LogicalChannel");
    LogicalChannelNode logicalChannel = (LogicalChannelNode) list.get(ndx);
    // get PixelChannelComponent node
    ndx = channelComponentIndex;
    count = logicalChannel.countPixelChannelComponentList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) {
      new PixelChannelComponentNode(ca).setLogicalChannel(logicalChannel);
    }
    list = logicalChannel.getPixelChannelComponentList();
    PixelChannelComponentNode pixelChannelComponent = (PixelChannelComponentNode) list.get(ndx);
    return pixelChannelComponent;
  }

  // Image+/CA/Pixels+
  private PixelsNode getPixelsNode(int imageIndex, int pixelsIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.countImageList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    // get CA node
    CustomAttributesNode ca = image.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(image);
    }
    // get Pixels node
    ndx = pixelsIndex;
    count = ca.countCAList("Pixels");
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new PixelsNode(ca);
    list = ca.getCAList("Pixels");
    PixelsNode pixels = (PixelsNode) list.get(ndx);
    return pixels;
  }

  // Image+/CA/Dimensions
  private DimensionsNode getDimensionsNode(int imageIndex, int pixelsIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.countImageList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    // get CA node
    CustomAttributesNode ca = image.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(image);
    }
    // get Dimensions node
    DimensionsNode dimensions = null;
    count = ca.countCAList("Dimensions");
    if (count >= 1) {
      dimensions = (DimensionsNode) ca.getCAList("Dimensions").get(0);
    }
    if (dimensions == null) {
      if (!create) return null;
      dimensions = new DimensionsNode(ca);
    }
    return dimensions;
  }

  // Image+/CA/DisplayOptions/@DisplayROI+
  private DisplayROINode getDisplayROINode(int imageIndex, int roiIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get Image node
    ndx = imageIndex;
    count = ome.countImageList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new ImageNode(ome);
    list = ome.getImageList();
    ImageNode image = (ImageNode) list.get(ndx);
    // get CA node
    CustomAttributesNode ca = image.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(image);
    }
    // get DisplayOptions node
    DisplayOptionsNode displayOptions = null;
    count = ca.countCAList("DisplayOptions");
    if (count >= 1) {
      displayOptions = (DisplayOptionsNode) ca.getCAList("DisplayOptions").get(0);
    }
    if (displayOptions == null) {
      if (!create) return null;
      displayOptions = new DisplayOptionsNode(ca);
    }
    // get DisplayROI node
    ndx = roiIndex;
    count = displayOptions.countDisplayROIList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) {
      new DisplayROINode(ca).setDisplayOptions(displayOptions);
    }
    list = displayOptions.getDisplayROIList();
    DisplayROINode displayROI = (DisplayROINode) list.get(ndx);
    return displayROI;
  }

  // CA/Instrument+
  private InstrumentNode getInstrumentNode(int instrumentIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get CA node
    CustomAttributesNode ca = ome.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(ome);
    }
    // get Instrument node
    ndx = instrumentIndex;
    count = ca.countCAList("Instrument");
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new InstrumentNode(ca);
    list = ca.getCAList("Instrument");
    InstrumentNode instrument = (InstrumentNode) list.get(ndx);
    return instrument;
  }

  // CA/Instrument+/@Detector+
  private DetectorNode getDetectorNode(int instrumentIndex, int detectorIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get CA node
    CustomAttributesNode ca = ome.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(ome);
    }
    // get Instrument node
    ndx = instrumentIndex;
    count = ca.countCAList("Instrument");
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new InstrumentNode(ca);
    list = ca.getCAList("Instrument");
    InstrumentNode instrument = (InstrumentNode) list.get(ndx);
    // get Detector node
    ndx = detectorIndex;
    count = instrument.countDetectorList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) {
      new DetectorNode(ca).setInstrument(instrument);
    }
    list = instrument.getDetectorList();
    DetectorNode detector = (DetectorNode) list.get(ndx);
    return detector;
  }

  // CA/Instrument+/@LightSource+
  private LightSourceNode getLightSourceNode(int instrumentIndex, int lightSourceIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get CA node
    CustomAttributesNode ca = ome.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(ome);
    }
    // get Instrument node
    ndx = instrumentIndex;
    count = ca.countCAList("Instrument");
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new InstrumentNode(ca);
    list = ca.getCAList("Instrument");
    InstrumentNode instrument = (InstrumentNode) list.get(ndx);
    // get LightSource node
    ndx = lightSourceIndex;
    count = instrument.countLightSourceList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) {
      new LightSourceNode(ca).setInstrument(instrument);
    }
    list = instrument.getLightSourceList();
    LightSourceNode lightSource = (LightSourceNode) list.get(ndx);
    return lightSource;
  }

  // CA/Instrument+/@LightSource+/@!Laser
  private LaserNode getLaserNode(int instrumentIndex, int lightSourceIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get CA node
    CustomAttributesNode ca = ome.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(ome);
    }
    // get Instrument node
    ndx = instrumentIndex;
    count = ca.countCAList("Instrument");
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new InstrumentNode(ca);
    list = ca.getCAList("Instrument");
    InstrumentNode instrument = (InstrumentNode) list.get(ndx);
    // get LightSource node
    ndx = lightSourceIndex;
    count = instrument.countLightSourceList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) {
      new LightSourceNode(ca).setInstrument(instrument);
    }
    list = instrument.getLightSourceList();
    LightSourceNode lightSource = (LightSourceNode) list.get(ndx);
    // get Laser node
    LaserNode laser = null;
    count = lightSource.countLaserListByLightSource();
    if (count >= 1) {
      laser = (LaserNode) lightSource.getLaserListByLightSource().get(0);
    }
    if (laser == null) {
      if (!create) return null;
      laser = new LaserNode(ca);
      laser.setLightSource(lightSource);
    }
    return laser;
  }

  // CA/Instrument+/@LightSource+/@Filament
  private FilamentNode getFilamentNode(int instrumentIndex, int lightSourceIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get CA node
    CustomAttributesNode ca = ome.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(ome);
    }
    // get Instrument node
    ndx = instrumentIndex;
    count = ca.countCAList("Instrument");
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new InstrumentNode(ca);
    list = ca.getCAList("Instrument");
    InstrumentNode instrument = (InstrumentNode) list.get(ndx);
    // get LightSource node
    ndx = lightSourceIndex;
    count = instrument.countLightSourceList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) {
      new LightSourceNode(ca).setInstrument(instrument);
    }
    list = instrument.getLightSourceList();
    LightSourceNode lightSource = (LightSourceNode) list.get(ndx);
    // get Filament node
    FilamentNode filament = null;
    count = lightSource.countFilamentList();
    if (count >= 1) {
      filament = (FilamentNode) lightSource.getFilamentList().get(0);
    }
    if (filament == null) {
      if (!create) return null;
      filament = new FilamentNode(ca);
      filament.setLightSource(lightSource);
    }
    return filament;
  }

  // CA/Instrument+/@LightSource+/@Arc
  private ArcNode getArcNode(int instrumentIndex, int lightSourceIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get CA node
    CustomAttributesNode ca = ome.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(ome);
    }
    // get Instrument node
    ndx = instrumentIndex;
    count = ca.countCAList("Instrument");
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new InstrumentNode(ca);
    list = ca.getCAList("Instrument");
    InstrumentNode instrument = (InstrumentNode) list.get(ndx);
    // get LightSource node
    ndx = lightSourceIndex;
    count = instrument.countLightSourceList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) {
      new LightSourceNode(ca).setInstrument(instrument);
    }
    list = instrument.getLightSourceList();
    LightSourceNode lightSource = (LightSourceNode) list.get(ndx);
    // get Arc node
    ArcNode arc = null;
    count = lightSource.countArcList();
    if (count >= 1) {
      arc = (ArcNode) lightSource.getArcList().get(0);
    }
    if (arc == null) {
      if (!create) return null;
      arc = new ArcNode(ca);
      arc.setLightSource(lightSource);
    }
    return arc;
  }

  // CA/Instrument+/@OTF+
  private OTFNode getOTFNode(int instrumentIndex, int otfIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get CA node
    CustomAttributesNode ca = ome.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(ome);
    }
    // get Instrument node
    ndx = instrumentIndex;
    count = ca.countCAList("Instrument");
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new InstrumentNode(ca);
    list = ca.getCAList("Instrument");
    InstrumentNode instrument = (InstrumentNode) list.get(ndx);
    // get OTF node
    ndx = otfIndex;
    count = instrument.countOTFList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) {
      new OTFNode(ca).setInstrument(instrument);
    }
    list = instrument.getOTFList();
    OTFNode otf = (OTFNode) list.get(ndx);
    return otf;
  }

  // CA/Instrument+/@Objective+
  private ObjectiveNode getObjectiveNode(int instrumentIndex, int objectiveIndex, boolean create) {
    int ndx, count;
    List list;
    // get OME node
    OMENode ome = (OMENode) root;
    // get CA node
    CustomAttributesNode ca = ome.getCustomAttributes();
    if (ca == null) {
      if (!create) return null;
      ca = new CustomAttributesNode(ome);
    }
    // get Instrument node
    ndx = instrumentIndex;
    count = ca.countCAList("Instrument");
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) new InstrumentNode(ca);
    list = ca.getCAList("Instrument");
    InstrumentNode instrument = (InstrumentNode) list.get(ndx);
    // get Objective node
    ndx = objectiveIndex;
    count = instrument.countObjectiveList();
    if (!create && ndx >= count) return null;
    for (int i=count; i<=ndx; i++) {
      new ObjectiveNode(ca).setInstrument(instrument);
    }
    list = instrument.getObjectiveList();
    ObjectiveNode objective = (ObjectiveNode) list.get(ndx);
    return objective;
  }

}
