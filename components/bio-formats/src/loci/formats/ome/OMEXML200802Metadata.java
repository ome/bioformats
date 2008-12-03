//
// OMEXML200802Metadata.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2008 UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by melissa via MetadataAutogen on Dec 3, 2008 5:11:46 PM PST
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.ome;

import ome.xml.OMEXMLNode;
import ome.xml.r200802.ome.*;
import ome.xml.r200802.spw.*;
import java.util.List;
import loci.common.LogTools;

/**
 * A metadata store implementation for constructing and manipulating OME-XML
 * DOMs for the 2008-02 version of OME-XML. It requires the
 * ome.xml.r200802 package to compile (part of ome-xml.jar).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/ome/OMEXML200802Metadata.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/ome/OMEXML200802Metadata.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 */
public class OMEXML200802Metadata extends OMEXMLMetadata {

  // -- MetadataRetrieve API methods --

  // - Entity counting -

  /* @see loci.formats.meta.MetadataRetrieve#getChannelComponentCount(int, int) */
  public int getChannelComponentCount(int imageIndex, int logicalChannelIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getChannelComponentNode(imageIndex, logicalChannelIndex, i, false) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorCount(int) */
  public int getDetectorCount(int instrumentIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getDetectorNode(instrumentIndex, i, false) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getExperimentCount() */
  public int getExperimentCount() {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getExperimentNode(i, false) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getExperimenterCount() */
  public int getExperimenterCount() {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getExperimenterNode(i, false) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getExperimenterMembershipCount(int) */
  public int getExperimenterMembershipCount(int experimenterIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getGroupRefNode(experimenterIndex, i, false) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getGroupRefCount(int) */
  public int getGroupRefCount(int experimenterIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getGroupRefNode(experimenterIndex, i, false) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getImageCount() */
  public int getImageCount() {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getImageNode(i, false) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getInstrumentCount() */
  public int getInstrumentCount() {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getInstrumentNode(i, false) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLightSourceCount(int) */
  public int getLightSourceCount(int instrumentIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getLightSourceNode(instrumentIndex, i, false) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelCount(int) */
  public int getLogicalChannelCount(int imageIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getLogicalChannelNode(imageIndex, i, false) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getOTFCount(int) */
  public int getOTFCount(int instrumentIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getOTFNode(instrumentIndex, i, false) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveCount(int) */
  public int getObjectiveCount(int instrumentIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getObjectiveNode(instrumentIndex, i, false) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPixelsCount(int) */
  public int getPixelsCount(int imageIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getPixelsNode(imageIndex, i, false) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPlaneCount(int, int) */
  public int getPlaneCount(int imageIndex, int pixelsIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getPlaneNode(imageIndex, pixelsIndex, i, false) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPlateCount() */
  public int getPlateCount() {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getPlateNode(i, false) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPlateRefCount(int) */
  public int getPlateRefCount(int screenIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getPlateRefNode(screenIndex, i, false) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROICount(int) */
  public int getROICount(int imageIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getROINode(imageIndex, i, false) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getReagentCount(int) */
  public int getReagentCount(int screenIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getReagentNode(screenIndex, i, false) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getScreenCount() */
  public int getScreenCount() {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getScreenNode(i, false) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getScreenAcquisitionCount(int) */
  public int getScreenAcquisitionCount(int screenIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getScreenAcquisitionNode(screenIndex, i, false) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getTiffDataCount(int, int) */
  public int getTiffDataCount(int imageIndex, int pixelsIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getTiffDataNode(imageIndex, pixelsIndex, i, false) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getWellCount(int) */
  public int getWellCount(int plateIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getWellNode(plateIndex, i, false) == null) return i;
    }
    return -1;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getWellSampleCount(int, int) */
  public int getWellSampleCount(int plateIndex, int wellIndex) {
    for (int i=0; i<Integer.MAX_VALUE; i++) {
      if (getWellSampleNode(plateIndex, wellIndex, i, false) == null) return i;
    }
    return -1;
  }

  // - Entity retrieval -

  // - Arc property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getArcType(int, int) */
  public String getArcType(int instrumentIndex, int lightSourceIndex) {
    ArcNode arc = getArcNode(instrumentIndex, lightSourceIndex, false);
    return arc == null ? null : arc.getType();
  }

  // - ChannelComponent property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getChannelComponentColorDomain(int, int, int) */
  public String getChannelComponentColorDomain(int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
    ChannelComponentNode channelComponent = getChannelComponentNode(imageIndex, logicalChannelIndex, channelComponentIndex, false);
    return channelComponent == null ? null : channelComponent.getColorDomain();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getChannelComponentIndex(int, int, int) */
  public Integer getChannelComponentIndex(int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
    ChannelComponentNode channelComponent = getChannelComponentNode(imageIndex, logicalChannelIndex, channelComponentIndex, false);
    return channelComponent == null ? null : channelComponent.getIndex();
  }

  // - Detector property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorGain(int, int) */
  public Float getDetectorGain(int instrumentIndex, int detectorIndex) {
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, false);
    return detector == null ? null : detector.getGain();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorID(int, int) */
  public String getDetectorID(int instrumentIndex, int detectorIndex) {
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

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorOffset(int, int) */
  public Float getDetectorOffset(int instrumentIndex, int detectorIndex) {
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, false);
    return detector == null ? null : detector.getOffset();
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

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorVoltage(int, int) */
  public Float getDetectorVoltage(int instrumentIndex, int detectorIndex) {
    DetectorNode detector = getDetectorNode(instrumentIndex, detectorIndex, false);
    return detector == null ? null : detector.getVoltage();
  }

  // - DetectorSettings property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorSettingsBinning(int, int) */
  public String getDetectorSettingsBinning(int imageIndex, int logicalChannelIndex) {
    DetectorRefNode detectorRef = getDetectorRefNode(imageIndex, logicalChannelIndex, false);
    return detectorRef == null ? null : detectorRef.getBinning();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorSettingsDetector(int, int) */
  public String getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex) {
    DetectorRefNode detectorRef = getDetectorRefNode(imageIndex, logicalChannelIndex, false);
    return detectorRef == null ? null : detectorRef.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorSettingsGain(int, int) */
  public Float getDetectorSettingsGain(int imageIndex, int logicalChannelIndex) {
    DetectorRefNode detectorRef = getDetectorRefNode(imageIndex, logicalChannelIndex, false);
    return detectorRef == null ? null : detectorRef.getGain();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorSettingsOffset(int, int) */
  public Float getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex) {
    DetectorRefNode detectorRef = getDetectorRefNode(imageIndex, logicalChannelIndex, false);
    return detectorRef == null ? null : detectorRef.getOffset();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorSettingsReadOutRate(int, int) */
  public Float getDetectorSettingsReadOutRate(int imageIndex, int logicalChannelIndex) {
    DetectorRefNode detectorRef = getDetectorRefNode(imageIndex, logicalChannelIndex, false);
    return detectorRef == null ? null : detectorRef.getReadOutRate();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDetectorSettingsVoltage(int, int) */
  public Float getDetectorSettingsVoltage(int imageIndex, int logicalChannelIndex) {
    DetectorRefNode detectorRef = getDetectorRefNode(imageIndex, logicalChannelIndex, false);
    return detectorRef == null ? null : detectorRef.getVoltage();
  }

  // - Dimensions property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getDimensionsPhysicalSizeX(int, int) */
  public Float getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getPhysicalSizeX();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDimensionsPhysicalSizeY(int, int) */
  public Float getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getPhysicalSizeY();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDimensionsPhysicalSizeZ(int, int) */
  public Float getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getPhysicalSizeZ();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDimensionsTimeIncrement(int, int) */
  public Float getDimensionsTimeIncrement(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getTimeIncrement();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDimensionsWaveIncrement(int, int) */
  public Integer getDimensionsWaveIncrement(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getWaveIncrement();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDimensionsWaveStart(int, int) */
  public Integer getDimensionsWaveStart(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getWaveStart();
  }

  // - DisplayOptions property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getDisplayOptionsID(int) */
  public String getDisplayOptionsID(int imageIndex) {
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
    ProjectionNode projection = getProjectionNode(imageIndex, false);
    return projection == null ? null : projection.getZStart();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDisplayOptionsProjectionZStop(int) */
  public Integer getDisplayOptionsProjectionZStop(int imageIndex) {
    ProjectionNode projection = getProjectionNode(imageIndex, false);
    return projection == null ? null : projection.getZStop();
  }

  // - DisplayOptionsTime property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getDisplayOptionsTimeTStart(int) */
  public Integer getDisplayOptionsTimeTStart(int imageIndex) {
    TimeNode time = getTimeNode(imageIndex, false);
    return time == null ? null : time.getTStart();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getDisplayOptionsTimeTStop(int) */
  public Integer getDisplayOptionsTimeTStop(int imageIndex) {
    TimeNode time = getTimeNode(imageIndex, false);
    return time == null ? null : time.getTStop();
  }

  // - Experiment property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getExperimentDescription(int) */
  public String getExperimentDescription(int experimentIndex) {
    ExperimentNode experiment = getExperimentNode(experimentIndex, false);
    return experiment == null ? null : experiment.getDescription();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getExperimentID(int) */
  public String getExperimentID(int experimentIndex) {
    ExperimentNode experiment = getExperimentNode(experimentIndex, false);
    return experiment == null ? null : experiment.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getExperimentType(int) */
  public String getExperimentType(int experimentIndex) {
    ExperimentNode experiment = getExperimentNode(experimentIndex, false);
    return experiment == null ? null : experiment.getType();
  }

  // - Experimenter property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getExperimenterEmail(int) */
  public String getExperimenterEmail(int experimenterIndex) {
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, false);
    return experimenter == null ? null : experimenter.getEmail();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getExperimenterFirstName(int) */
  public String getExperimenterFirstName(int experimenterIndex) {
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, false);
    return experimenter == null ? null : experimenter.getFirstName();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getExperimenterID(int) */
  public String getExperimenterID(int experimenterIndex) {
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, false);
    return experimenter == null ? null : experimenter.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getExperimenterInstitution(int) */
  public String getExperimenterInstitution(int experimenterIndex) {
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, false);
    return experimenter == null ? null : experimenter.getInstitution();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getExperimenterLastName(int) */
  public String getExperimenterLastName(int experimenterIndex) {
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, false);
    return experimenter == null ? null : experimenter.getLastName();
  }

  // - ExperimenterMembership property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getExperimenterMembershipGroup(int, int) */
  public String getExperimenterMembershipGroup(int experimenterIndex, int groupRefIndex) {
    GroupRefNode groupRef = getGroupRefNode(experimenterIndex, groupRefIndex, false);
    return groupRef == null ? null : groupRef.getNodeID();
  }

  // - Filament property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getFilamentType(int, int) */
  public String getFilamentType(int instrumentIndex, int lightSourceIndex) {
    FilamentNode filament = getFilamentNode(instrumentIndex, lightSourceIndex, false);
    return filament == null ? null : filament.getType();
  }

  // - GroupRef property retrieval -

  // - Image property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getImageCreationDate(int) */
  public String getImageCreationDate(int imageIndex) {
    ImageNode image = getImageNode(imageIndex, false);
    return image == null ? null : image.getCreationDate();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getImageDefaultPixels(int) */
  public String getImageDefaultPixels(int imageIndex) {
    ImageNode image = getImageNode(imageIndex, false);
    return image == null ? null : image.getDefaultPixels();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getImageDescription(int) */
  public String getImageDescription(int imageIndex) {
    ImageNode image = getImageNode(imageIndex, false);
    return image == null ? null : image.getDescription();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getImageID(int) */
  public String getImageID(int imageIndex) {
    ImageNode image = getImageNode(imageIndex, false);
    return image == null ? null : image.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getImageInstrumentRef(int) */
  public String getImageInstrumentRef(int imageIndex) {
    ImageNode image = getImageNode(imageIndex, false);
    InstrumentNode node = image.getInstrument();
    if (node == null) return null;
    return node.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getImageName(int) */
  public String getImageName(int imageIndex) {
    ImageNode image = getImageNode(imageIndex, false);
    return image == null ? null : image.getName();
  }

  // - ImagingEnvironment property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getImagingEnvironmentAirPressure(int) */
  public Float getImagingEnvironmentAirPressure(int imageIndex) {
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, false);
    return imagingEnvironment == null ? null : imagingEnvironment.getAirPressure();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getImagingEnvironmentCO2Percent(int) */
  public Float getImagingEnvironmentCO2Percent(int imageIndex) {
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, false);
    return imagingEnvironment == null ? null : imagingEnvironment.getCO2Percent();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getImagingEnvironmentHumidity(int) */
  public Float getImagingEnvironmentHumidity(int imageIndex) {
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, false);
    return imagingEnvironment == null ? null : imagingEnvironment.getHumidity();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getImagingEnvironmentTemperature(int) */
  public Float getImagingEnvironmentTemperature(int imageIndex) {
    ImagingEnvironmentNode imagingEnvironment = getImagingEnvironmentNode(imageIndex, false);
    return imagingEnvironment == null ? null : imagingEnvironment.getTemperature();
  }

  // - Instrument property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getInstrumentID(int) */
  public String getInstrumentID(int instrumentIndex) {
    InstrumentNode instrument = getInstrumentNode(instrumentIndex, false);
    return instrument == null ? null : instrument.getNodeID();
  }

  // - Laser property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getLaserFrequencyMultiplication(int, int) */
  public Integer getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getFrequencyMultiplication();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLaserLaserMedium(int, int) */
  public String getLaserLaserMedium(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getLaserMedium();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLaserPulse(int, int) */
  public String getLaserPulse(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getPulse();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLaserTuneable(int, int) */
  public Boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getTuneable();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLaserType(int, int) */
  public String getLaserType(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getType();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLaserWavelength(int, int) */
  public Integer getLaserWavelength(int instrumentIndex, int lightSourceIndex) {
    LaserNode laser = getLaserNode(instrumentIndex, lightSourceIndex, false);
    return laser == null ? null : laser.getWavelength();
  }

  // - LightSource property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getLightSourceID(int, int) */
  public String getLightSourceID(int instrumentIndex, int lightSourceIndex) {
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

  /* @see loci.formats.meta.MetadataRetrieve#getLightSourcePower(int, int) */
  public Float getLightSourcePower(int instrumentIndex, int lightSourceIndex) {
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, false);
    return lightSource == null ? null : lightSource.getPower();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLightSourceSerialNumber(int, int) */
  public String getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex) {
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, false);
    return lightSource == null ? null : lightSource.getSerialNumber();
  }

  // - LightSourceSettings property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getLightSourceSettingsAttenuation(int, int) */
  public Float getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex) {
    LightSourceRefNode lightSourceRef = getLightSourceRefNode(imageIndex, logicalChannelIndex, false);
    return lightSourceRef == null ? null : lightSourceRef.getAttenuation();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLightSourceSettingsLightSource(int, int) */
  public String getLightSourceSettingsLightSource(int imageIndex, int logicalChannelIndex) {
    LightSourceRefNode lightSourceRef = getLightSourceRefNode(imageIndex, logicalChannelIndex, false);
    return lightSourceRef == null ? null : lightSourceRef.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLightSourceSettingsWavelength(int, int) */
  public Integer getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex) {
    LightSourceRefNode lightSourceRef = getLightSourceRefNode(imageIndex, logicalChannelIndex, false);
    return lightSourceRef == null ? null : lightSourceRef.getWavelength();
  }

  // - LogicalChannel property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelContrastMethod(int, int) */
  public String getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getContrastMethod();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelEmWave(int, int) */
  public Integer getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getEmWave();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelExWave(int, int) */
  public Integer getLogicalChannelExWave(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getExWave();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelFluor(int, int) */
  public String getLogicalChannelFluor(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getFluor();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelID(int, int) */
  public String getLogicalChannelID(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelIlluminationType(int, int) */
  public String getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getIlluminationType();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelMode(int, int) */
  public String getLogicalChannelMode(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getMode();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelName(int, int) */
  public String getLogicalChannelName(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getName();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelNdFilter(int, int) */
  public Float getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getNdFilter();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelOTF(int, int) */
  public String getLogicalChannelOTF(int imageIndex, int logicalChannelIndex) {
    OTFRefNode otfRef = getOTFRefNode(imageIndex, logicalChannelIndex, false);
    return otfRef == null ? null : otfRef.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelPhotometricInterpretation(int, int) */
  public String getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getPhotometricInterpretation();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelPinholeSize(int, int) */
  public Float getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : integerToFloat(logicalChannel.getPinholeSize());
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelPockelCellSetting(int, int) */
  public Integer getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getPockelCellSetting();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getLogicalChannelSamplesPerPixel(int, int) */
  public Integer getLogicalChannelSamplesPerPixel(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getSamplesPerPixel();
  }

  // - OTF property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getOTFID(int, int) */
  public String getOTFID(int instrumentIndex, int otfIndex) {
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, false);
    return otf == null ? null : otf.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getOTFObjective(int, int) */
  public String getOTFObjective(int instrumentIndex, int otfIndex) {
    ObjectiveRefNode objectiveRef = getObjectiveRefNode(instrumentIndex, otfIndex, false);
    return objectiveRef == null ? null : objectiveRef.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getOTFOpticalAxisAveraged(int, int) */
  public Boolean getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex) {
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, false);
    return otf == null ? null : otf.getOpticalAxisAveraged();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getOTFPixelType(int, int) */
  public String getOTFPixelType(int instrumentIndex, int otfIndex) {
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, false);
    return otf == null ? null : otf.getPixelType();
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

  // - Objective property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveCalibratedMagnification(int, int) */
  public Float getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getCalibratedMagnification();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveCorrection(int, int) */
  public String getObjectiveCorrection(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getCorrection();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveID(int, int) */
  public String getObjectiveID(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveImmersion(int, int) */
  public String getObjectiveImmersion(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getImmersion();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveIris(int, int) */
  public Boolean getObjectiveIris(int instrumentIndex, int objectiveIndex) {
    // NB: Iris unsupported for schema version 2008-02
    return null;
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveLensNA(int, int) */
  public Float getObjectiveLensNA(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getLensNA();
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

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveNominalMagnification(int, int) */
  public Integer getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getNominalMagnification();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveSerialNumber(int, int) */
  public String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getSerialNumber();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveWorkingDistance(int, int) */
  public Float getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex) {
    ObjectiveNode objective = getObjectiveNode(instrumentIndex, objectiveIndex, false);
    return objective == null ? null : objective.getWorkingDistance();
  }

  // - ObjectiveSettings property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveSettingsCorrectionCollar(int) */
  public Float getObjectiveSettingsCorrectionCollar(int imageIndex) {
    ObjectiveRefNode objectiveRef = getObjectiveRefNode(imageIndex, false);
    return objectiveRef == null ? null : objectiveRef.getCorrectionCollar();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveSettingsMedium(int) */
  public String getObjectiveSettingsMedium(int imageIndex) {
    ObjectiveRefNode objectiveRef = getObjectiveRefNode(imageIndex, false);
    return objectiveRef == null ? null : objectiveRef.getMedium();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveSettingsObjective(int) */
  public String getObjectiveSettingsObjective(int imageIndex) {
    ObjectiveRefNode objectiveRef = getObjectiveRefNode(imageIndex, false);
    return objectiveRef == null ? null : objectiveRef.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getObjectiveSettingsRefractiveIndex(int) */
  public Float getObjectiveSettingsRefractiveIndex(int imageIndex) {
    ObjectiveRefNode objectiveRef = getObjectiveRefNode(imageIndex, false);
    return objectiveRef == null ? null : objectiveRef.getRefractiveIndex();
  }

  // - Pixels property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getPixelsBigEndian(int, int) */
  public Boolean getPixelsBigEndian(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getBigEndian();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPixelsDimensionOrder(int, int) */
  public String getPixelsDimensionOrder(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getDimensionOrder();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPixelsID(int, int) */
  public String getPixelsID(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPixelsPixelType(int, int) */
  public String getPixelsPixelType(int imageIndex, int pixelsIndex) {
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, false);
    return pixels == null ? null : pixels.getPixelType();
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

  // - Plane property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getPlaneTheC(int, int, int) */
  public Integer getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex) {
    PlaneNode plane = getPlaneNode(imageIndex, pixelsIndex, planeIndex, false);
    return plane == null ? null : plane.getTheC();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPlaneTheT(int, int, int) */
  public Integer getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex) {
    PlaneNode plane = getPlaneNode(imageIndex, pixelsIndex, planeIndex, false);
    return plane == null ? null : plane.getTheT();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPlaneTheZ(int, int, int) */
  public Integer getPlaneTheZ(int imageIndex, int pixelsIndex, int planeIndex) {
    PlaneNode plane = getPlaneNode(imageIndex, pixelsIndex, planeIndex, false);
    return plane == null ? null : plane.getTheZ();
  }

  // - PlaneTiming property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getPlaneTimingDeltaT(int, int, int) */
  public Float getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex) {
    PlaneTimingNode planeTiming = getPlaneTimingNode(imageIndex, pixelsIndex, planeIndex, false);
    return planeTiming == null ? null : planeTiming.getDeltaT();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPlaneTimingExposureTime(int, int, int) */
  public Float getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex) {
    PlaneTimingNode planeTiming = getPlaneTimingNode(imageIndex, pixelsIndex, planeIndex, false);
    return planeTiming == null ? null : planeTiming.getExposureTime();
  }

  // - Plate property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getPlateDescription(int) */
  public String getPlateDescription(int plateIndex) {
    PlateNode plate = getPlateNode(plateIndex, false);
    return plate == null ? null : plate.getDescription();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPlateExternalIdentifier(int) */
  public String getPlateExternalIdentifier(int plateIndex) {
    PlateNode plate = getPlateNode(plateIndex, false);
    return plate == null ? null : plate.getExternalIdentifier();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPlateID(int) */
  public String getPlateID(int plateIndex) {
    PlateNode plate = getPlateNode(plateIndex, false);
    return plate == null ? null : plate.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPlateName(int) */
  public String getPlateName(int plateIndex) {
    PlateNode plate = getPlateNode(plateIndex, false);
    return plate == null ? null : plate.getName();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getPlateStatus(int) */
  public String getPlateStatus(int plateIndex) {
    PlateNode plate = getPlateNode(plateIndex, false);
    return plate == null ? null : plate.getStatus();
  }

  // - PlateRef property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getPlateRefID(int, int) */
  public String getPlateRefID(int screenIndex, int plateRefIndex) {
    PlateRefNode plateRef = getPlateRefNode(screenIndex, plateRefIndex, false);
    return plateRef == null ? null : plateRef.getNodeID();
  }

  // - ROI property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getROIID(int, int) */
  public String getROIID(int imageIndex, int roiIndex) {
    ROINode roi = getROINode(imageIndex, roiIndex, false);
    return roi == null ? null : roi.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROIT0(int, int) */
  public Integer getROIT0(int imageIndex, int roiIndex) {
    ROINode roi = getROINode(imageIndex, roiIndex, false);
    return roi == null ? null : roi.getT0();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROIT1(int, int) */
  public Integer getROIT1(int imageIndex, int roiIndex) {
    ROINode roi = getROINode(imageIndex, roiIndex, false);
    return roi == null ? null : roi.getT1();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROIX0(int, int) */
  public Integer getROIX0(int imageIndex, int roiIndex) {
    ROINode roi = getROINode(imageIndex, roiIndex, false);
    return roi == null ? null : roi.getX0();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROIX1(int, int) */
  public Integer getROIX1(int imageIndex, int roiIndex) {
    ROINode roi = getROINode(imageIndex, roiIndex, false);
    return roi == null ? null : roi.getX1();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROIY0(int, int) */
  public Integer getROIY0(int imageIndex, int roiIndex) {
    ROINode roi = getROINode(imageIndex, roiIndex, false);
    return roi == null ? null : roi.getY0();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROIY1(int, int) */
  public Integer getROIY1(int imageIndex, int roiIndex) {
    ROINode roi = getROINode(imageIndex, roiIndex, false);
    return roi == null ? null : roi.getY1();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROIZ0(int, int) */
  public Integer getROIZ0(int imageIndex, int roiIndex) {
    ROINode roi = getROINode(imageIndex, roiIndex, false);
    return roi == null ? null : roi.getZ0();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getROIZ1(int, int) */
  public Integer getROIZ1(int imageIndex, int roiIndex) {
    ROINode roi = getROINode(imageIndex, roiIndex, false);
    return roi == null ? null : roi.getZ1();
  }

  // - Reagent property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getReagentDescription(int, int) */
  public String getReagentDescription(int screenIndex, int reagentIndex) {
    ReagentNode reagent = getReagentNode(screenIndex, reagentIndex, false);
    return reagent == null ? null : reagent.getDescription();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getReagentID(int, int) */
  public String getReagentID(int screenIndex, int reagentIndex) {
    ReagentNode reagent = getReagentNode(screenIndex, reagentIndex, false);
    return reagent == null ? null : reagent.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getReagentName(int, int) */
  public String getReagentName(int screenIndex, int reagentIndex) {
    ReagentNode reagent = getReagentNode(screenIndex, reagentIndex, false);
    return reagent == null ? null : reagent.getName();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getReagentReagentIdentifier(int, int) */
  public String getReagentReagentIdentifier(int screenIndex, int reagentIndex) {
    ReagentNode reagent = getReagentNode(screenIndex, reagentIndex, false);
    return reagent == null ? null : reagent.getReagentIdentifier();
  }

  // - Screen property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getScreenID(int) */
  public String getScreenID(int screenIndex) {
    ScreenNode screen = getScreenNode(screenIndex, false);
    return screen == null ? null : screen.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getScreenName(int) */
  public String getScreenName(int screenIndex) {
    ScreenNode screen = getScreenNode(screenIndex, false);
    return screen == null ? null : screen.getName();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getScreenProtocolDescription(int) */
  public String getScreenProtocolDescription(int screenIndex) {
    ScreenNode screen = getScreenNode(screenIndex, false);
    return screen == null ? null : screen.getProtocolDescription();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getScreenProtocolIdentifier(int) */
  public String getScreenProtocolIdentifier(int screenIndex) {
    ScreenNode screen = getScreenNode(screenIndex, false);
    return screen == null ? null : screen.getProtocolIdentifier();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getScreenReagentSetDescription(int) */
  public String getScreenReagentSetDescription(int screenIndex) {
    ScreenNode screen = getScreenNode(screenIndex, false);
    return screen == null ? null : screen.getReagentSetDescription();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getScreenType(int) */
  public String getScreenType(int screenIndex) {
    ScreenNode screen = getScreenNode(screenIndex, false);
    return screen == null ? null : screen.getType();
  }

  // - ScreenAcquisition property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getScreenAcquisitionEndTime(int, int) */
  public String getScreenAcquisitionEndTime(int screenIndex, int screenAcquisitionIndex) {
    ScreenAcquisitionNode screenAcquisition = getScreenAcquisitionNode(screenIndex, screenAcquisitionIndex, false);
    return screenAcquisition == null ? null : screenAcquisition.getEndTime();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getScreenAcquisitionID(int, int) */
  public String getScreenAcquisitionID(int screenIndex, int screenAcquisitionIndex) {
    ScreenAcquisitionNode screenAcquisition = getScreenAcquisitionNode(screenIndex, screenAcquisitionIndex, false);
    return screenAcquisition == null ? null : screenAcquisition.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getScreenAcquisitionStartTime(int, int) */
  public String getScreenAcquisitionStartTime(int screenIndex, int screenAcquisitionIndex) {
    ScreenAcquisitionNode screenAcquisition = getScreenAcquisitionNode(screenIndex, screenAcquisitionIndex, false);
    return screenAcquisition == null ? null : screenAcquisition.getStartTime();
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

  // - StagePosition property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getStagePositionPositionX(int, int, int) */
  public Float getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex) {
    StagePositionNode stagePosition = getStagePositionNode(imageIndex, pixelsIndex, planeIndex, false);
    return stagePosition == null ? null : stagePosition.getPositionX();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getStagePositionPositionY(int, int, int) */
  public Float getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex) {
    StagePositionNode stagePosition = getStagePositionNode(imageIndex, pixelsIndex, planeIndex, false);
    return stagePosition == null ? null : stagePosition.getPositionY();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getStagePositionPositionZ(int, int, int) */
  public Float getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex) {
    StagePositionNode stagePosition = getStagePositionNode(imageIndex, pixelsIndex, planeIndex, false);
    return stagePosition == null ? null : stagePosition.getPositionZ();
  }

  // - TiffData property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getTiffDataFileName(int, int, int) */
  public String getTiffDataFileName(int imageIndex, int pixelsIndex, int tiffDataIndex) {
    UUIDNode uuid = getUUIDNode(imageIndex, pixelsIndex, tiffDataIndex, false);
    return uuid == null ? null : uuid.getFileName();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getTiffDataFirstC(int, int, int) */
  public Integer getTiffDataFirstC(int imageIndex, int pixelsIndex, int tiffDataIndex) {
    TiffDataNode tiffData = getTiffDataNode(imageIndex, pixelsIndex, tiffDataIndex, false);
    return tiffData == null ? null : tiffData.getFirstC();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getTiffDataFirstT(int, int, int) */
  public Integer getTiffDataFirstT(int imageIndex, int pixelsIndex, int tiffDataIndex) {
    TiffDataNode tiffData = getTiffDataNode(imageIndex, pixelsIndex, tiffDataIndex, false);
    return tiffData == null ? null : tiffData.getFirstT();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getTiffDataFirstZ(int, int, int) */
  public Integer getTiffDataFirstZ(int imageIndex, int pixelsIndex, int tiffDataIndex) {
    TiffDataNode tiffData = getTiffDataNode(imageIndex, pixelsIndex, tiffDataIndex, false);
    return tiffData == null ? null : tiffData.getFirstZ();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getTiffDataIFD(int, int, int) */
  public Integer getTiffDataIFD(int imageIndex, int pixelsIndex, int tiffDataIndex) {
    TiffDataNode tiffData = getTiffDataNode(imageIndex, pixelsIndex, tiffDataIndex, false);
    return tiffData == null ? null : tiffData.getIFD();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getTiffDataNumPlanes(int, int, int) */
  public Integer getTiffDataNumPlanes(int imageIndex, int pixelsIndex, int tiffDataIndex) {
    TiffDataNode tiffData = getTiffDataNode(imageIndex, pixelsIndex, tiffDataIndex, false);
    return tiffData == null ? null : tiffData.getNumPlanes();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getTiffDataUUID(int, int, int) */
  public String getTiffDataUUID(int imageIndex, int pixelsIndex, int tiffDataIndex) {
    UUIDNode uuid = getUUIDNode(imageIndex, pixelsIndex, tiffDataIndex, false);
    return uuid == null ? null : uuid.getCData();
  }

  // - Well property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getWellColumn(int, int) */
  public Integer getWellColumn(int plateIndex, int wellIndex) {
    WellNode well = getWellNode(plateIndex, wellIndex, false);
    return well == null ? null : well.getColumn();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getWellExternalDescription(int, int) */
  public String getWellExternalDescription(int plateIndex, int wellIndex) {
    WellNode well = getWellNode(plateIndex, wellIndex, false);
    return well == null ? null : well.getExternalDescription();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getWellExternalIdentifier(int, int) */
  public String getWellExternalIdentifier(int plateIndex, int wellIndex) {
    WellNode well = getWellNode(plateIndex, wellIndex, false);
    return well == null ? null : well.getExternalIdentifier();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getWellID(int, int) */
  public String getWellID(int plateIndex, int wellIndex) {
    WellNode well = getWellNode(plateIndex, wellIndex, false);
    return well == null ? null : well.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getWellRow(int, int) */
  public Integer getWellRow(int plateIndex, int wellIndex) {
    WellNode well = getWellNode(plateIndex, wellIndex, false);
    return well == null ? null : well.getRow();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getWellType(int, int) */
  public String getWellType(int plateIndex, int wellIndex) {
    WellNode well = getWellNode(plateIndex, wellIndex, false);
    return well == null ? null : well.getType();
  }

  // - WellSample property retrieval -

  /* @see loci.formats.meta.MetadataRetrieve#getWellSampleID(int, int, int) */
  public String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex) {
    WellSampleNode wellSample = getWellSampleNode(plateIndex, wellIndex, wellSampleIndex, false);
    return wellSample == null ? null : wellSample.getNodeID();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getWellSampleIndex(int, int, int) */
  public Integer getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex) {
    WellSampleNode wellSample = getWellSampleNode(plateIndex, wellIndex, wellSampleIndex, false);
    return wellSample == null ? null : wellSample.getIndex();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getWellSamplePosX(int, int, int) */
  public Float getWellSamplePosX(int plateIndex, int wellIndex, int wellSampleIndex) {
    WellSampleNode wellSample = getWellSampleNode(plateIndex, wellIndex, wellSampleIndex, false);
    return wellSample == null ? null : wellSample.getPosX();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getWellSamplePosY(int, int, int) */
  public Float getWellSamplePosY(int plateIndex, int wellIndex, int wellSampleIndex) {
    WellSampleNode wellSample = getWellSampleNode(plateIndex, wellIndex, wellSampleIndex, false);
    return wellSample == null ? null : wellSample.getPosY();
  }

  /* @see loci.formats.meta.MetadataRetrieve#getWellSampleTimepoint(int, int, int) */
  public Integer getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex) {
    WellSampleNode wellSample = getWellSampleNode(plateIndex, wellIndex, wellSampleIndex, false);
    return wellSample == null ? null : wellSample.getTimepoint();
  }

  // -- MetadataStore API methods --

  /* @see loci.formats.meta.MetadataStore#setRoot(Object) */
  public void createRoot() {
    try {
      setRoot(ome.xml.OMEXMLFactory.newOMENode("2008-02"));
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
    clearCachedNodes();
  }

  // - Arc property storage -

  /* @see loci.formats.meta.MetadataStore#setArcType(String, int, int) */
  public void setArcType(String type, int instrumentIndex, int lightSourceIndex) {
    if (type == null) return;
    ArcNode arcNode = getArcNode(instrumentIndex, lightSourceIndex, true);
    arcNode.setType(type);
  }

  // - ChannelComponent property storage -

  /* @see loci.formats.meta.MetadataStore#setChannelComponentColorDomain(String, int, int, int) */
  public void setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
    if (colorDomain == null) return;
    ChannelComponentNode channelComponentNode = getChannelComponentNode(imageIndex, logicalChannelIndex, channelComponentIndex, true);
    channelComponentNode.setColorDomain(colorDomain);
  }

  /* @see loci.formats.meta.MetadataStore#setChannelComponentIndex(Integer, int, int, int) */
  public void setChannelComponentIndex(Integer index, int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
    if (index == null) return;
    ChannelComponentNode channelComponentNode = getChannelComponentNode(imageIndex, logicalChannelIndex, channelComponentIndex, true);
    channelComponentNode.setIndex(index);
  }

  // - Detector property storage -

  /* @see loci.formats.meta.MetadataStore#setDetectorGain(Float, int, int) */
  public void setDetectorGain(Float gain, int instrumentIndex, int detectorIndex) {
    if (gain == null) return;
    DetectorNode detectorNode = getDetectorNode(instrumentIndex, detectorIndex, true);
    detectorNode.setGain(gain);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorID(String, int, int) */
  public void setDetectorID(String id, int instrumentIndex, int detectorIndex) {
    if (id == null) return;
    DetectorNode detectorNode = getDetectorNode(instrumentIndex, detectorIndex, true);
    detectorNode.setNodeID(id);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorManufacturer(String, int, int) */
  public void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex) {
    if (manufacturer == null) return;
    DetectorNode detectorNode = getDetectorNode(instrumentIndex, detectorIndex, true);
    detectorNode.setManufacturer(manufacturer);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorModel(String, int, int) */
  public void setDetectorModel(String model, int instrumentIndex, int detectorIndex) {
    if (model == null) return;
    DetectorNode detectorNode = getDetectorNode(instrumentIndex, detectorIndex, true);
    detectorNode.setModel(model);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorOffset(Float, int, int) */
  public void setDetectorOffset(Float offset, int instrumentIndex, int detectorIndex) {
    if (offset == null) return;
    DetectorNode detectorNode = getDetectorNode(instrumentIndex, detectorIndex, true);
    detectorNode.setOffset(offset);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorSerialNumber(String, int, int) */
  public void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex) {
    if (serialNumber == null) return;
    DetectorNode detectorNode = getDetectorNode(instrumentIndex, detectorIndex, true);
    detectorNode.setSerialNumber(serialNumber);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorType(String, int, int) */
  public void setDetectorType(String type, int instrumentIndex, int detectorIndex) {
    if (type == null) return;
    DetectorNode detectorNode = getDetectorNode(instrumentIndex, detectorIndex, true);
    detectorNode.setType(type);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorVoltage(Float, int, int) */
  public void setDetectorVoltage(Float voltage, int instrumentIndex, int detectorIndex) {
    if (voltage == null) return;
    DetectorNode detectorNode = getDetectorNode(instrumentIndex, detectorIndex, true);
    detectorNode.setVoltage(voltage);
  }

  // - DetectorSettings property storage -

  /* @see loci.formats.meta.MetadataStore#setDetectorSettingsBinning(String, int, int) */
  public void setDetectorSettingsBinning(String binning, int imageIndex, int logicalChannelIndex) {
    if (binning == null) return;
    DetectorRefNode detectorRefNode = getDetectorRefNode(imageIndex, logicalChannelIndex, true);
    detectorRefNode.setBinning(binning);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorSettingsDetector(String, int, int) */
  public void setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex) {
    if (detector == null) return;
    DetectorRefNode detectorRefNode = getDetectorRefNode(imageIndex, logicalChannelIndex, true);
    detectorRefNode.setNodeID(detector);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorSettingsGain(Float, int, int) */
  public void setDetectorSettingsGain(Float gain, int imageIndex, int logicalChannelIndex) {
    if (gain == null) return;
    DetectorRefNode detectorRefNode = getDetectorRefNode(imageIndex, logicalChannelIndex, true);
    detectorRefNode.setGain(gain);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorSettingsOffset(Float, int, int) */
  public void setDetectorSettingsOffset(Float offset, int imageIndex, int logicalChannelIndex) {
    if (offset == null) return;
    DetectorRefNode detectorRefNode = getDetectorRefNode(imageIndex, logicalChannelIndex, true);
    detectorRefNode.setOffset(offset);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorSettingsReadOutRate(Float, int, int) */
  public void setDetectorSettingsReadOutRate(Float readOutRate, int imageIndex, int logicalChannelIndex) {
    if (readOutRate == null) return;
    DetectorRefNode detectorRefNode = getDetectorRefNode(imageIndex, logicalChannelIndex, true);
    detectorRefNode.setReadOutRate(readOutRate);
  }

  /* @see loci.formats.meta.MetadataStore#setDetectorSettingsVoltage(Float, int, int) */
  public void setDetectorSettingsVoltage(Float voltage, int imageIndex, int logicalChannelIndex) {
    if (voltage == null) return;
    DetectorRefNode detectorRefNode = getDetectorRefNode(imageIndex, logicalChannelIndex, true);
    detectorRefNode.setVoltage(voltage);
  }

  // - Dimensions property storage -

  /* @see loci.formats.meta.MetadataStore#setDimensionsPhysicalSizeX(Float, int, int) */
  public void setDimensionsPhysicalSizeX(Float physicalSizeX, int imageIndex, int pixelsIndex) {
    if (physicalSizeX == null) return;
    PixelsNode pixelsNode = getPixelsNode(imageIndex, pixelsIndex, true);
    pixelsNode.setPhysicalSizeX(physicalSizeX);
  }

  /* @see loci.formats.meta.MetadataStore#setDimensionsPhysicalSizeY(Float, int, int) */
  public void setDimensionsPhysicalSizeY(Float physicalSizeY, int imageIndex, int pixelsIndex) {
    if (physicalSizeY == null) return;
    PixelsNode pixelsNode = getPixelsNode(imageIndex, pixelsIndex, true);
    pixelsNode.setPhysicalSizeY(physicalSizeY);
  }

  /* @see loci.formats.meta.MetadataStore#setDimensionsPhysicalSizeZ(Float, int, int) */
  public void setDimensionsPhysicalSizeZ(Float physicalSizeZ, int imageIndex, int pixelsIndex) {
    if (physicalSizeZ == null) return;
    PixelsNode pixelsNode = getPixelsNode(imageIndex, pixelsIndex, true);
    pixelsNode.setPhysicalSizeZ(physicalSizeZ);
  }

  /* @see loci.formats.meta.MetadataStore#setDimensionsTimeIncrement(Float, int, int) */
  public void setDimensionsTimeIncrement(Float timeIncrement, int imageIndex, int pixelsIndex) {
    if (timeIncrement == null) return;
    PixelsNode pixelsNode = getPixelsNode(imageIndex, pixelsIndex, true);
    pixelsNode.setTimeIncrement(timeIncrement);
  }

  /* @see loci.formats.meta.MetadataStore#setDimensionsWaveIncrement(Integer, int, int) */
  public void setDimensionsWaveIncrement(Integer waveIncrement, int imageIndex, int pixelsIndex) {
    if (waveIncrement == null) return;
    PixelsNode pixelsNode = getPixelsNode(imageIndex, pixelsIndex, true);
    pixelsNode.setWaveIncrement(waveIncrement);
  }

  /* @see loci.formats.meta.MetadataStore#setDimensionsWaveStart(Integer, int, int) */
  public void setDimensionsWaveStart(Integer waveStart, int imageIndex, int pixelsIndex) {
    if (waveStart == null) return;
    PixelsNode pixelsNode = getPixelsNode(imageIndex, pixelsIndex, true);
    pixelsNode.setWaveStart(waveStart);
  }

  // - DisplayOptions property storage -

  /* @see loci.formats.meta.MetadataStore#setDisplayOptionsID(String, int) */
  public void setDisplayOptionsID(String id, int imageIndex) {
    if (id == null) return;
    DisplayOptionsNode displayOptionsNode = getDisplayOptionsNode(imageIndex, true);
    displayOptionsNode.setNodeID(id);
  }

  /* @see loci.formats.meta.MetadataStore#setDisplayOptionsZoom(Float, int) */
  public void setDisplayOptionsZoom(Float zoom, int imageIndex) {
    if (zoom == null) return;
    DisplayOptionsNode displayOptionsNode = getDisplayOptionsNode(imageIndex, true);
    displayOptionsNode.setZoom(zoom);
  }

  // - DisplayOptionsProjection property storage -

  /* @see loci.formats.meta.MetadataStore#setDisplayOptionsProjectionZStart(Integer, int) */
  public void setDisplayOptionsProjectionZStart(Integer zStart, int imageIndex) {
    if (zStart == null) return;
    ProjectionNode projectionNode = getProjectionNode(imageIndex, true);
    projectionNode.setZStart(zStart);
  }

  /* @see loci.formats.meta.MetadataStore#setDisplayOptionsProjectionZStop(Integer, int) */
  public void setDisplayOptionsProjectionZStop(Integer zStop, int imageIndex) {
    if (zStop == null) return;
    ProjectionNode projectionNode = getProjectionNode(imageIndex, true);
    projectionNode.setZStop(zStop);
  }

  // - DisplayOptionsTime property storage -

  /* @see loci.formats.meta.MetadataStore#setDisplayOptionsTimeTStart(Integer, int) */
  public void setDisplayOptionsTimeTStart(Integer tStart, int imageIndex) {
    if (tStart == null) return;
    TimeNode timeNode = getTimeNode(imageIndex, true);
    timeNode.setTStart(tStart);
  }

  /* @see loci.formats.meta.MetadataStore#setDisplayOptionsTimeTStop(Integer, int) */
  public void setDisplayOptionsTimeTStop(Integer tStop, int imageIndex) {
    if (tStop == null) return;
    TimeNode timeNode = getTimeNode(imageIndex, true);
    timeNode.setTStop(tStop);
  }

  // - Experiment property storage -

  /* @see loci.formats.meta.MetadataStore#setExperimentDescription(String, int) */
  public void setExperimentDescription(String description, int experimentIndex) {
    if (description == null) return;
    ExperimentNode experimentNode = getExperimentNode(experimentIndex, true);
    experimentNode.setDescription(description);
  }

  /* @see loci.formats.meta.MetadataStore#setExperimentID(String, int) */
  public void setExperimentID(String id, int experimentIndex) {
    if (id == null) return;
    ExperimentNode experimentNode = getExperimentNode(experimentIndex, true);
    experimentNode.setNodeID(id);
  }

  /* @see loci.formats.meta.MetadataStore#setExperimentType(String, int) */
  public void setExperimentType(String type, int experimentIndex) {
    if (type == null) return;
    ExperimentNode experimentNode = getExperimentNode(experimentIndex, true);
    experimentNode.setType(type);
  }

  // - Experimenter property storage -

  /* @see loci.formats.meta.MetadataStore#setExperimenterEmail(String, int) */
  public void setExperimenterEmail(String email, int experimenterIndex) {
    if (email == null) return;
    ExperimenterNode experimenterNode = getExperimenterNode(experimenterIndex, true);
    experimenterNode.setEmail(email);
  }

  /* @see loci.formats.meta.MetadataStore#setExperimenterFirstName(String, int) */
  public void setExperimenterFirstName(String firstName, int experimenterIndex) {
    if (firstName == null) return;
    ExperimenterNode experimenterNode = getExperimenterNode(experimenterIndex, true);
    experimenterNode.setFirstName(firstName);
  }

  /* @see loci.formats.meta.MetadataStore#setExperimenterID(String, int) */
  public void setExperimenterID(String id, int experimenterIndex) {
    if (id == null) return;
    ExperimenterNode experimenterNode = getExperimenterNode(experimenterIndex, true);
    experimenterNode.setNodeID(id);
  }

  /* @see loci.formats.meta.MetadataStore#setExperimenterInstitution(String, int) */
  public void setExperimenterInstitution(String institution, int experimenterIndex) {
    if (institution == null) return;
    ExperimenterNode experimenterNode = getExperimenterNode(experimenterIndex, true);
    experimenterNode.setInstitution(institution);
  }

  /* @see loci.formats.meta.MetadataStore#setExperimenterLastName(String, int) */
  public void setExperimenterLastName(String lastName, int experimenterIndex) {
    if (lastName == null) return;
    ExperimenterNode experimenterNode = getExperimenterNode(experimenterIndex, true);
    experimenterNode.setLastName(lastName);
  }

  // - ExperimenterMembership property storage -

  /* @see loci.formats.meta.MetadataStore#setExperimenterMembershipGroup(String, int, int) */
  public void setExperimenterMembershipGroup(String group, int experimenterIndex, int groupRefIndex) {
    if (group == null) return;
    GroupRefNode groupRefNode = getGroupRefNode(experimenterIndex, groupRefIndex, true);
    groupRefNode.setNodeID(group);
  }

  // - Filament property storage -

  /* @see loci.formats.meta.MetadataStore#setFilamentType(String, int, int) */
  public void setFilamentType(String type, int instrumentIndex, int lightSourceIndex) {
    if (type == null) return;
    FilamentNode filamentNode = getFilamentNode(instrumentIndex, lightSourceIndex, true);
    filamentNode.setType(type);
  }

  // - GroupRef property storage -

  // - Image property storage -

  /* @see loci.formats.meta.MetadataStore#setImageCreationDate(String, int) */
  public void setImageCreationDate(String creationDate, int imageIndex) {
    if (creationDate == null) return;
    ImageNode imageNode = getImageNode(imageIndex, true);
    imageNode.setCreationDate(creationDate);
  }

  /* @see loci.formats.meta.MetadataStore#setImageDefaultPixels(String, int) */
  public void setImageDefaultPixels(String defaultPixels, int imageIndex) {
    if (defaultPixels == null) return;
    ImageNode imageNode = getImageNode(imageIndex, true);
    imageNode.setDefaultPixels(defaultPixels);
  }

  /* @see loci.formats.meta.MetadataStore#setImageDescription(String, int) */
  public void setImageDescription(String description, int imageIndex) {
    if (description == null) return;
    ImageNode imageNode = getImageNode(imageIndex, true);
    imageNode.setDescription(description);
  }

  /* @see loci.formats.meta.MetadataStore#setImageID(String, int) */
  public void setImageID(String id, int imageIndex) {
    if (id == null) return;
    ImageNode imageNode = getImageNode(imageIndex, true);
    imageNode.setNodeID(id);
  }

  /* @see loci.formats.meta.MetadataStore#setImageInstrumentRef(String, int) */
  public void setImageInstrumentRef(String instrumentRef, int imageIndex) {
    if (instrumentRef == null) return;
    ImageNode imageNode = getImageNode(imageIndex, true);
    OMENode ome = (OMENode) root;
    List list = ome.getInstrumentList();
    InstrumentNode node = null;
    for (int i=0; i<list.size(); i++) {
      OMEXMLNode o = (OMEXMLNode) list.get(i);
      if (o.getNodeID().equals(instrumentRef)) node = (InstrumentNode) o;
    }
    if (node == null) {
      node = new InstrumentNode(ome);
      node.setNodeID(instrumentRef);
    }
    InstrumentRefNode ref = new InstrumentRefNode(imageNode);
    ref.setInstrumentNode(node);
  }

  /* @see loci.formats.meta.MetadataStore#setImageName(String, int) */
  public void setImageName(String name, int imageIndex) {
    if (name == null) return;
    ImageNode imageNode = getImageNode(imageIndex, true);
    imageNode.setName(name);
  }

  // - ImagingEnvironment property storage -

  /* @see loci.formats.meta.MetadataStore#setImagingEnvironmentAirPressure(Float, int) */
  public void setImagingEnvironmentAirPressure(Float airPressure, int imageIndex) {
    if (airPressure == null) return;
    ImagingEnvironmentNode imagingEnvironmentNode = getImagingEnvironmentNode(imageIndex, true);
    imagingEnvironmentNode.setAirPressure(airPressure);
  }

  /* @see loci.formats.meta.MetadataStore#setImagingEnvironmentCO2Percent(Float, int) */
  public void setImagingEnvironmentCO2Percent(Float cO2Percent, int imageIndex) {
    if (cO2Percent == null) return;
    ImagingEnvironmentNode imagingEnvironmentNode = getImagingEnvironmentNode(imageIndex, true);
    imagingEnvironmentNode.setCO2Percent(cO2Percent);
  }

  /* @see loci.formats.meta.MetadataStore#setImagingEnvironmentHumidity(Float, int) */
  public void setImagingEnvironmentHumidity(Float humidity, int imageIndex) {
    if (humidity == null) return;
    ImagingEnvironmentNode imagingEnvironmentNode = getImagingEnvironmentNode(imageIndex, true);
    imagingEnvironmentNode.setHumidity(humidity);
  }

  /* @see loci.formats.meta.MetadataStore#setImagingEnvironmentTemperature(Float, int) */
  public void setImagingEnvironmentTemperature(Float temperature, int imageIndex) {
    if (temperature == null) return;
    ImagingEnvironmentNode imagingEnvironmentNode = getImagingEnvironmentNode(imageIndex, true);
    imagingEnvironmentNode.setTemperature(temperature);
  }

  // - Instrument property storage -

  /* @see loci.formats.meta.MetadataStore#setInstrumentID(String, int) */
  public void setInstrumentID(String id, int instrumentIndex) {
    if (id == null) return;
    InstrumentNode instrumentNode = getInstrumentNode(instrumentIndex, true);
    instrumentNode.setNodeID(id);
  }

  // - Laser property storage -

  /* @see loci.formats.meta.MetadataStore#setLaserFrequencyMultiplication(Integer, int, int) */
  public void setLaserFrequencyMultiplication(Integer frequencyMultiplication, int instrumentIndex, int lightSourceIndex) {
    if (frequencyMultiplication == null) return;
    LaserNode laserNode = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laserNode.setFrequencyMultiplication(frequencyMultiplication);
  }

  /* @see loci.formats.meta.MetadataStore#setLaserLaserMedium(String, int, int) */
  public void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex) {
    if (laserMedium == null) return;
    LaserNode laserNode = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laserNode.setLaserMedium(laserMedium);
  }

  /* @see loci.formats.meta.MetadataStore#setLaserPulse(String, int, int) */
  public void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex) {
    if (pulse == null) return;
    LaserNode laserNode = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laserNode.setPulse(pulse);
  }

  /* @see loci.formats.meta.MetadataStore#setLaserTuneable(Boolean, int, int) */
  public void setLaserTuneable(Boolean tuneable, int instrumentIndex, int lightSourceIndex) {
    if (tuneable == null) return;
    LaserNode laserNode = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laserNode.setTuneable(tuneable);
  }

  /* @see loci.formats.meta.MetadataStore#setLaserType(String, int, int) */
  public void setLaserType(String type, int instrumentIndex, int lightSourceIndex) {
    if (type == null) return;
    LaserNode laserNode = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laserNode.setType(type);
  }

  /* @see loci.formats.meta.MetadataStore#setLaserWavelength(Integer, int, int) */
  public void setLaserWavelength(Integer wavelength, int instrumentIndex, int lightSourceIndex) {
    if (wavelength == null) return;
    LaserNode laserNode = getLaserNode(instrumentIndex, lightSourceIndex, true);
    laserNode.setWavelength(wavelength);
  }

  // - LightSource property storage -

  /* @see loci.formats.meta.MetadataStore#setLightSourceID(String, int, int) */
  public void setLightSourceID(String id, int instrumentIndex, int lightSourceIndex) {
    if (id == null) return;
    LightSourceNode lightSourceNode = getLightSourceNode(instrumentIndex, lightSourceIndex, true);
    lightSourceNode.setNodeID(id);
  }

  /* @see loci.formats.meta.MetadataStore#setLightSourceManufacturer(String, int, int) */
  public void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex) {
    if (manufacturer == null) return;
    LightSourceNode lightSourceNode = getLightSourceNode(instrumentIndex, lightSourceIndex, true);
    lightSourceNode.setManufacturer(manufacturer);
  }

  /* @see loci.formats.meta.MetadataStore#setLightSourceModel(String, int, int) */
  public void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex) {
    if (model == null) return;
    LightSourceNode lightSourceNode = getLightSourceNode(instrumentIndex, lightSourceIndex, true);
    lightSourceNode.setModel(model);
  }

  /* @see loci.formats.meta.MetadataStore#setLightSourcePower(Float, int, int) */
  public void setLightSourcePower(Float power, int instrumentIndex, int lightSourceIndex) {
    if (power == null) return;
    LightSourceNode lightSourceNode = getLightSourceNode(instrumentIndex, lightSourceIndex, true);
    lightSourceNode.setPower(power);
  }

  /* @see loci.formats.meta.MetadataStore#setLightSourceSerialNumber(String, int, int) */
  public void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex) {
    if (serialNumber == null) return;
    LightSourceNode lightSourceNode = getLightSourceNode(instrumentIndex, lightSourceIndex, true);
    lightSourceNode.setSerialNumber(serialNumber);
  }

  // - LightSourceSettings property storage -

  /* @see loci.formats.meta.MetadataStore#setLightSourceSettingsAttenuation(Float, int, int) */
  public void setLightSourceSettingsAttenuation(Float attenuation, int imageIndex, int logicalChannelIndex) {
    if (attenuation == null) return;
    LightSourceRefNode lightSourceRefNode = getLightSourceRefNode(imageIndex, logicalChannelIndex, true);
    lightSourceRefNode.setAttenuation(attenuation);
  }

  /* @see loci.formats.meta.MetadataStore#setLightSourceSettingsLightSource(String, int, int) */
  public void setLightSourceSettingsLightSource(String lightSource, int imageIndex, int logicalChannelIndex) {
    if (lightSource == null) return;
    LightSourceRefNode lightSourceRefNode = getLightSourceRefNode(imageIndex, logicalChannelIndex, true);
    lightSourceRefNode.setNodeID(lightSource);
  }

  /* @see loci.formats.meta.MetadataStore#setLightSourceSettingsWavelength(Integer, int, int) */
  public void setLightSourceSettingsWavelength(Integer wavelength, int imageIndex, int logicalChannelIndex) {
    if (wavelength == null) return;
    LightSourceRefNode lightSourceRefNode = getLightSourceRefNode(imageIndex, logicalChannelIndex, true);
    lightSourceRefNode.setWavelength(wavelength);
  }

  // - LogicalChannel property storage -

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelContrastMethod(String, int, int) */
  public void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex) {
    if (contrastMethod == null) return;
    LogicalChannelNode logicalChannelNode = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannelNode.setContrastMethod(contrastMethod);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelEmWave(Integer, int, int) */
  public void setLogicalChannelEmWave(Integer emWave, int imageIndex, int logicalChannelIndex) {
    if (emWave == null) return;
    LogicalChannelNode logicalChannelNode = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannelNode.setEmWave(emWave);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelExWave(Integer, int, int) */
  public void setLogicalChannelExWave(Integer exWave, int imageIndex, int logicalChannelIndex) {
    if (exWave == null) return;
    LogicalChannelNode logicalChannelNode = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannelNode.setExWave(exWave);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelFluor(String, int, int) */
  public void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex) {
    if (fluor == null) return;
    LogicalChannelNode logicalChannelNode = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannelNode.setFluor(fluor);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelID(String, int, int) */
  public void setLogicalChannelID(String id, int imageIndex, int logicalChannelIndex) {
    if (id == null) return;
    LogicalChannelNode logicalChannelNode = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannelNode.setNodeID(id);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelIlluminationType(String, int, int) */
  public void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex) {
    if (illuminationType == null) return;
    LogicalChannelNode logicalChannelNode = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannelNode.setIlluminationType(illuminationType);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelMode(String, int, int) */
  public void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex) {
    if (mode == null) return;
    LogicalChannelNode logicalChannelNode = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannelNode.setMode(mode);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelName(String, int, int) */
  public void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex) {
    if (name == null) return;
    LogicalChannelNode logicalChannelNode = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannelNode.setName(name);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelNdFilter(Float, int, int) */
  public void setLogicalChannelNdFilter(Float ndFilter, int imageIndex, int logicalChannelIndex) {
    if (ndFilter == null) return;
    LogicalChannelNode logicalChannelNode = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannelNode.setNdFilter(ndFilter);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelOTF(String, int, int) */
  public void setLogicalChannelOTF(String otf, int imageIndex, int logicalChannelIndex) {
    if (otf == null) return;
    OTFRefNode otfRefNode = getOTFRefNode(imageIndex, logicalChannelIndex, true);
    otfRefNode.setNodeID(otf);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelPhotometricInterpretation(String, int, int) */
  public void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex) {
    if (photometricInterpretation == null) return;
    LogicalChannelNode logicalChannelNode = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannelNode.setPhotometricInterpretation(photometricInterpretation);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelPinholeSize(Float, int, int) */
  public void setLogicalChannelPinholeSize(Float pinholeSize, int imageIndex, int logicalChannelIndex) {
    if (pinholeSize == null) return;
    LogicalChannelNode logicalChannelNode = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannelNode.setPinholeSize(floatToInteger(pinholeSize));
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelPockelCellSetting(Integer, int, int) */
  public void setLogicalChannelPockelCellSetting(Integer pockelCellSetting, int imageIndex, int logicalChannelIndex) {
    if (pockelCellSetting == null) return;
    LogicalChannelNode logicalChannelNode = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannelNode.setPockelCellSetting(pockelCellSetting);
  }

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelSamplesPerPixel(Integer, int, int) */
  public void setLogicalChannelSamplesPerPixel(Integer samplesPerPixel, int imageIndex, int logicalChannelIndex) {
    if (samplesPerPixel == null) return;
    LogicalChannelNode logicalChannelNode = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannelNode.setSamplesPerPixel(samplesPerPixel);
  }

  // - OTF property storage -

  /* @see loci.formats.meta.MetadataStore#setOTFID(String, int, int) */
  public void setOTFID(String id, int instrumentIndex, int otfIndex) {
    if (id == null) return;
    OTFNode otfNode = getOTFNode(instrumentIndex, otfIndex, true);
    otfNode.setNodeID(id);
  }

  /* @see loci.formats.meta.MetadataStore#setOTFObjective(String, int, int) */
  public void setOTFObjective(String objective, int instrumentIndex, int otfIndex) {
    if (objective == null) return;
    ObjectiveRefNode objectiveRefNode = getObjectiveRefNode(instrumentIndex, otfIndex, true);
    objectiveRefNode.setNodeID(objective);
  }

  /* @see loci.formats.meta.MetadataStore#setOTFOpticalAxisAveraged(Boolean, int, int) */
  public void setOTFOpticalAxisAveraged(Boolean opticalAxisAveraged, int instrumentIndex, int otfIndex) {
    if (opticalAxisAveraged == null) return;
    OTFNode otfNode = getOTFNode(instrumentIndex, otfIndex, true);
    otfNode.setOpticalAxisAveraged(opticalAxisAveraged);
  }

  /* @see loci.formats.meta.MetadataStore#setOTFPixelType(String, int, int) */
  public void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex) {
    if (pixelType == null) return;
    OTFNode otfNode = getOTFNode(instrumentIndex, otfIndex, true);
    otfNode.setPixelType(pixelType);
  }

  /* @see loci.formats.meta.MetadataStore#setOTFSizeX(Integer, int, int) */
  public void setOTFSizeX(Integer sizeX, int instrumentIndex, int otfIndex) {
    if (sizeX == null) return;
    OTFNode otfNode = getOTFNode(instrumentIndex, otfIndex, true);
    otfNode.setSizeX(sizeX);
  }

  /* @see loci.formats.meta.MetadataStore#setOTFSizeY(Integer, int, int) */
  public void setOTFSizeY(Integer sizeY, int instrumentIndex, int otfIndex) {
    if (sizeY == null) return;
    OTFNode otfNode = getOTFNode(instrumentIndex, otfIndex, true);
    otfNode.setSizeY(sizeY);
  }

  // - Objective property storage -

  /* @see loci.formats.meta.MetadataStore#setObjectiveCalibratedMagnification(Float, int, int) */
  public void setObjectiveCalibratedMagnification(Float calibratedMagnification, int instrumentIndex, int objectiveIndex) {
    if (calibratedMagnification == null) return;
    ObjectiveNode objectiveNode = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objectiveNode.setCalibratedMagnification(calibratedMagnification);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveCorrection(String, int, int) */
  public void setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex) {
    if (correction == null) return;
    ObjectiveNode objectiveNode = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objectiveNode.setCorrection(correction);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveID(String, int, int) */
  public void setObjectiveID(String id, int instrumentIndex, int objectiveIndex) {
    if (id == null) return;
    ObjectiveNode objectiveNode = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objectiveNode.setNodeID(id);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveImmersion(String, int, int) */
  public void setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex) {
    if (immersion == null) return;
    ObjectiveNode objectiveNode = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objectiveNode.setImmersion(immersion);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveIris(Boolean, int, int) */
  public void setObjectiveIris(Boolean iris, int instrumentIndex, int objectiveIndex) {
    // NB: Iris unsupported for schema version 2008-02
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveLensNA(Float, int, int) */
  public void setObjectiveLensNA(Float lensNA, int instrumentIndex, int objectiveIndex) {
    if (lensNA == null) return;
    ObjectiveNode objectiveNode = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objectiveNode.setLensNA(lensNA);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveManufacturer(String, int, int) */
  public void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex) {
    if (manufacturer == null) return;
    ObjectiveNode objectiveNode = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objectiveNode.setManufacturer(manufacturer);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveModel(String, int, int) */
  public void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex) {
    if (model == null) return;
    ObjectiveNode objectiveNode = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objectiveNode.setModel(model);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveNominalMagnification(Integer, int, int) */
  public void setObjectiveNominalMagnification(Integer nominalMagnification, int instrumentIndex, int objectiveIndex) {
    if (nominalMagnification == null) return;
    ObjectiveNode objectiveNode = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objectiveNode.setNominalMagnification(nominalMagnification);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveSerialNumber(String, int, int) */
  public void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex) {
    if (serialNumber == null) return;
    ObjectiveNode objectiveNode = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objectiveNode.setSerialNumber(serialNumber);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveWorkingDistance(Float, int, int) */
  public void setObjectiveWorkingDistance(Float workingDistance, int instrumentIndex, int objectiveIndex) {
    if (workingDistance == null) return;
    ObjectiveNode objectiveNode = getObjectiveNode(instrumentIndex, objectiveIndex, true);
    objectiveNode.setWorkingDistance(workingDistance);
  }

  // - ObjectiveSettings property storage -

  /* @see loci.formats.meta.MetadataStore#setObjectiveSettingsCorrectionCollar(Float, int) */
  public void setObjectiveSettingsCorrectionCollar(Float correctionCollar, int imageIndex) {
    if (correctionCollar == null) return;
    ObjectiveRefNode objectiveRefNode = getObjectiveRefNode(imageIndex, true);
    objectiveRefNode.setCorrectionCollar(correctionCollar);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveSettingsMedium(String, int) */
  public void setObjectiveSettingsMedium(String medium, int imageIndex) {
    if (medium == null) return;
    ObjectiveRefNode objectiveRefNode = getObjectiveRefNode(imageIndex, true);
    objectiveRefNode.setMedium(medium);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveSettingsObjective(String, int) */
  public void setObjectiveSettingsObjective(String objective, int imageIndex) {
    if (objective == null) return;
    ObjectiveRefNode objectiveRefNode = getObjectiveRefNode(imageIndex, true);
    objectiveRefNode.setNodeID(objective);
  }

  /* @see loci.formats.meta.MetadataStore#setObjectiveSettingsRefractiveIndex(Float, int) */
  public void setObjectiveSettingsRefractiveIndex(Float refractiveIndex, int imageIndex) {
    if (refractiveIndex == null) return;
    ObjectiveRefNode objectiveRefNode = getObjectiveRefNode(imageIndex, true);
    objectiveRefNode.setRefractiveIndex(refractiveIndex);
  }

  // - Pixels property storage -

  /* @see loci.formats.meta.MetadataStore#setPixelsBigEndian(Boolean, int, int) */
  public void setPixelsBigEndian(Boolean bigEndian, int imageIndex, int pixelsIndex) {
    if (bigEndian == null) return;
    PixelsNode pixelsNode = getPixelsNode(imageIndex, pixelsIndex, true);
    pixelsNode.setBigEndian(bigEndian);
  }

  /* @see loci.formats.meta.MetadataStore#setPixelsDimensionOrder(String, int, int) */
  public void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex) {
    if (dimensionOrder == null) return;
    PixelsNode pixelsNode = getPixelsNode(imageIndex, pixelsIndex, true);
    pixelsNode.setDimensionOrder(dimensionOrder);
  }

  /* @see loci.formats.meta.MetadataStore#setPixelsID(String, int, int) */
  public void setPixelsID(String id, int imageIndex, int pixelsIndex) {
    if (id == null) return;
    PixelsNode pixelsNode = getPixelsNode(imageIndex, pixelsIndex, true);
    pixelsNode.setNodeID(id);
  }

  /* @see loci.formats.meta.MetadataStore#setPixelsPixelType(String, int, int) */
  public void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex) {
    if (pixelType == null) return;
    PixelsNode pixelsNode = getPixelsNode(imageIndex, pixelsIndex, true);
    pixelsNode.setPixelType(pixelType);
  }

  /* @see loci.formats.meta.MetadataStore#setPixelsSizeC(Integer, int, int) */
  public void setPixelsSizeC(Integer sizeC, int imageIndex, int pixelsIndex) {
    if (sizeC == null) return;
    PixelsNode pixelsNode = getPixelsNode(imageIndex, pixelsIndex, true);
    pixelsNode.setSizeC(sizeC);
  }

  /* @see loci.formats.meta.MetadataStore#setPixelsSizeT(Integer, int, int) */
  public void setPixelsSizeT(Integer sizeT, int imageIndex, int pixelsIndex) {
    if (sizeT == null) return;
    PixelsNode pixelsNode = getPixelsNode(imageIndex, pixelsIndex, true);
    pixelsNode.setSizeT(sizeT);
  }

  /* @see loci.formats.meta.MetadataStore#setPixelsSizeX(Integer, int, int) */
  public void setPixelsSizeX(Integer sizeX, int imageIndex, int pixelsIndex) {
    if (sizeX == null) return;
    PixelsNode pixelsNode = getPixelsNode(imageIndex, pixelsIndex, true);
    pixelsNode.setSizeX(sizeX);
  }

  /* @see loci.formats.meta.MetadataStore#setPixelsSizeY(Integer, int, int) */
  public void setPixelsSizeY(Integer sizeY, int imageIndex, int pixelsIndex) {
    if (sizeY == null) return;
    PixelsNode pixelsNode = getPixelsNode(imageIndex, pixelsIndex, true);
    pixelsNode.setSizeY(sizeY);
  }

  /* @see loci.formats.meta.MetadataStore#setPixelsSizeZ(Integer, int, int) */
  public void setPixelsSizeZ(Integer sizeZ, int imageIndex, int pixelsIndex) {
    if (sizeZ == null) return;
    PixelsNode pixelsNode = getPixelsNode(imageIndex, pixelsIndex, true);
    pixelsNode.setSizeZ(sizeZ);
  }

  // - Plane property storage -

  /* @see loci.formats.meta.MetadataStore#setPlaneTheC(Integer, int, int, int) */
  public void setPlaneTheC(Integer theC, int imageIndex, int pixelsIndex, int planeIndex) {
    if (theC == null) return;
    PlaneNode planeNode = getPlaneNode(imageIndex, pixelsIndex, planeIndex, true);
    planeNode.setTheC(theC);
  }

  /* @see loci.formats.meta.MetadataStore#setPlaneTheT(Integer, int, int, int) */
  public void setPlaneTheT(Integer theT, int imageIndex, int pixelsIndex, int planeIndex) {
    if (theT == null) return;
    PlaneNode planeNode = getPlaneNode(imageIndex, pixelsIndex, planeIndex, true);
    planeNode.setTheT(theT);
  }

  /* @see loci.formats.meta.MetadataStore#setPlaneTheZ(Integer, int, int, int) */
  public void setPlaneTheZ(Integer theZ, int imageIndex, int pixelsIndex, int planeIndex) {
    if (theZ == null) return;
    PlaneNode planeNode = getPlaneNode(imageIndex, pixelsIndex, planeIndex, true);
    planeNode.setTheZ(theZ);
  }

  // - PlaneTiming property storage -

  /* @see loci.formats.meta.MetadataStore#setPlaneTimingDeltaT(Float, int, int, int) */
  public void setPlaneTimingDeltaT(Float deltaT, int imageIndex, int pixelsIndex, int planeIndex) {
    if (deltaT == null) return;
    PlaneTimingNode planeTimingNode = getPlaneTimingNode(imageIndex, pixelsIndex, planeIndex, true);
    planeTimingNode.setDeltaT(deltaT);
  }

  /* @see loci.formats.meta.MetadataStore#setPlaneTimingExposureTime(Float, int, int, int) */
  public void setPlaneTimingExposureTime(Float exposureTime, int imageIndex, int pixelsIndex, int planeIndex) {
    if (exposureTime == null) return;
    PlaneTimingNode planeTimingNode = getPlaneTimingNode(imageIndex, pixelsIndex, planeIndex, true);
    planeTimingNode.setExposureTime(exposureTime);
  }

  // - Plate property storage -

  /* @see loci.formats.meta.MetadataStore#setPlateDescription(String, int) */
  public void setPlateDescription(String description, int plateIndex) {
    if (description == null) return;
    PlateNode plateNode = getPlateNode(plateIndex, true);
    plateNode.setDescription(description);
  }

  /* @see loci.formats.meta.MetadataStore#setPlateExternalIdentifier(String, int) */
  public void setPlateExternalIdentifier(String externalIdentifier, int plateIndex) {
    if (externalIdentifier == null) return;
    PlateNode plateNode = getPlateNode(plateIndex, true);
    plateNode.setExternalIdentifier(externalIdentifier);
  }

  /* @see loci.formats.meta.MetadataStore#setPlateID(String, int) */
  public void setPlateID(String id, int plateIndex) {
    if (id == null) return;
    PlateNode plateNode = getPlateNode(plateIndex, true);
    plateNode.setNodeID(id);
  }

  /* @see loci.formats.meta.MetadataStore#setPlateName(String, int) */
  public void setPlateName(String name, int plateIndex) {
    if (name == null) return;
    PlateNode plateNode = getPlateNode(plateIndex, true);
    plateNode.setName(name);
  }

  /* @see loci.formats.meta.MetadataStore#setPlateStatus(String, int) */
  public void setPlateStatus(String status, int plateIndex) {
    if (status == null) return;
    PlateNode plateNode = getPlateNode(plateIndex, true);
    plateNode.setStatus(status);
  }

  // - PlateRef property storage -

  /* @see loci.formats.meta.MetadataStore#setPlateRefID(String, int, int) */
  public void setPlateRefID(String id, int screenIndex, int plateRefIndex) {
    if (id == null) return;
    PlateRefNode plateRefNode = getPlateRefNode(screenIndex, plateRefIndex, true);
    plateRefNode.setNodeID(id);
  }

  // - ROI property storage -

  /* @see loci.formats.meta.MetadataStore#setROIID(String, int, int) */
  public void setROIID(String id, int imageIndex, int roiIndex) {
    if (id == null) return;
    ROINode roiNode = getROINode(imageIndex, roiIndex, true);
    roiNode.setNodeID(id);
  }

  /* @see loci.formats.meta.MetadataStore#setROIT0(Integer, int, int) */
  public void setROIT0(Integer t0, int imageIndex, int roiIndex) {
    if (t0 == null) return;
    ROINode roiNode = getROINode(imageIndex, roiIndex, true);
    roiNode.setT0(t0);
  }

  /* @see loci.formats.meta.MetadataStore#setROIT1(Integer, int, int) */
  public void setROIT1(Integer t1, int imageIndex, int roiIndex) {
    if (t1 == null) return;
    ROINode roiNode = getROINode(imageIndex, roiIndex, true);
    roiNode.setT1(t1);
  }

  /* @see loci.formats.meta.MetadataStore#setROIX0(Integer, int, int) */
  public void setROIX0(Integer x0, int imageIndex, int roiIndex) {
    if (x0 == null) return;
    ROINode roiNode = getROINode(imageIndex, roiIndex, true);
    roiNode.setX0(x0);
  }

  /* @see loci.formats.meta.MetadataStore#setROIX1(Integer, int, int) */
  public void setROIX1(Integer x1, int imageIndex, int roiIndex) {
    if (x1 == null) return;
    ROINode roiNode = getROINode(imageIndex, roiIndex, true);
    roiNode.setX1(x1);
  }

  /* @see loci.formats.meta.MetadataStore#setROIY0(Integer, int, int) */
  public void setROIY0(Integer y0, int imageIndex, int roiIndex) {
    if (y0 == null) return;
    ROINode roiNode = getROINode(imageIndex, roiIndex, true);
    roiNode.setY0(y0);
  }

  /* @see loci.formats.meta.MetadataStore#setROIY1(Integer, int, int) */
  public void setROIY1(Integer y1, int imageIndex, int roiIndex) {
    if (y1 == null) return;
    ROINode roiNode = getROINode(imageIndex, roiIndex, true);
    roiNode.setY1(y1);
  }

  /* @see loci.formats.meta.MetadataStore#setROIZ0(Integer, int, int) */
  public void setROIZ0(Integer z0, int imageIndex, int roiIndex) {
    if (z0 == null) return;
    ROINode roiNode = getROINode(imageIndex, roiIndex, true);
    roiNode.setZ0(z0);
  }

  /* @see loci.formats.meta.MetadataStore#setROIZ1(Integer, int, int) */
  public void setROIZ1(Integer z1, int imageIndex, int roiIndex) {
    if (z1 == null) return;
    ROINode roiNode = getROINode(imageIndex, roiIndex, true);
    roiNode.setZ1(z1);
  }

  // - Reagent property storage -

  /* @see loci.formats.meta.MetadataStore#setReagentDescription(String, int, int) */
  public void setReagentDescription(String description, int screenIndex, int reagentIndex) {
    if (description == null) return;
    ReagentNode reagentNode = getReagentNode(screenIndex, reagentIndex, true);
    reagentNode.setDescription(description);
  }

  /* @see loci.formats.meta.MetadataStore#setReagentID(String, int, int) */
  public void setReagentID(String id, int screenIndex, int reagentIndex) {
    if (id == null) return;
    ReagentNode reagentNode = getReagentNode(screenIndex, reagentIndex, true);
    reagentNode.setNodeID(id);
  }

  /* @see loci.formats.meta.MetadataStore#setReagentName(String, int, int) */
  public void setReagentName(String name, int screenIndex, int reagentIndex) {
    if (name == null) return;
    ReagentNode reagentNode = getReagentNode(screenIndex, reagentIndex, true);
    reagentNode.setName(name);
  }

  /* @see loci.formats.meta.MetadataStore#setReagentReagentIdentifier(String, int, int) */
  public void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex) {
    if (reagentIdentifier == null) return;
    ReagentNode reagentNode = getReagentNode(screenIndex, reagentIndex, true);
    reagentNode.setReagentIdentifier(reagentIdentifier);
  }

  // - Screen property storage -

  /* @see loci.formats.meta.MetadataStore#setScreenID(String, int) */
  public void setScreenID(String id, int screenIndex) {
    if (id == null) return;
    ScreenNode screenNode = getScreenNode(screenIndex, true);
    screenNode.setNodeID(id);
  }

  /* @see loci.formats.meta.MetadataStore#setScreenName(String, int) */
  public void setScreenName(String name, int screenIndex) {
    if (name == null) return;
    ScreenNode screenNode = getScreenNode(screenIndex, true);
    screenNode.setName(name);
  }

  /* @see loci.formats.meta.MetadataStore#setScreenProtocolDescription(String, int) */
  public void setScreenProtocolDescription(String protocolDescription, int screenIndex) {
    if (protocolDescription == null) return;
    ScreenNode screenNode = getScreenNode(screenIndex, true);
    screenNode.setProtocolDescription(protocolDescription);
  }

  /* @see loci.formats.meta.MetadataStore#setScreenProtocolIdentifier(String, int) */
  public void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex) {
    if (protocolIdentifier == null) return;
    ScreenNode screenNode = getScreenNode(screenIndex, true);
    screenNode.setProtocolIdentifier(protocolIdentifier);
  }

  /* @see loci.formats.meta.MetadataStore#setScreenReagentSetDescription(String, int) */
  public void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex) {
    if (reagentSetDescription == null) return;
    ScreenNode screenNode = getScreenNode(screenIndex, true);
    screenNode.setReagentSetDescription(reagentSetDescription);
  }

  /* @see loci.formats.meta.MetadataStore#setScreenType(String, int) */
  public void setScreenType(String type, int screenIndex) {
    if (type == null) return;
    ScreenNode screenNode = getScreenNode(screenIndex, true);
    screenNode.setType(type);
  }

  // - ScreenAcquisition property storage -

  /* @see loci.formats.meta.MetadataStore#setScreenAcquisitionEndTime(String, int, int) */
  public void setScreenAcquisitionEndTime(String endTime, int screenIndex, int screenAcquisitionIndex) {
    if (endTime == null) return;
    ScreenAcquisitionNode screenAcquisitionNode = getScreenAcquisitionNode(screenIndex, screenAcquisitionIndex, true);
    screenAcquisitionNode.setEndTime(endTime);
  }

  /* @see loci.formats.meta.MetadataStore#setScreenAcquisitionID(String, int, int) */
  public void setScreenAcquisitionID(String id, int screenIndex, int screenAcquisitionIndex) {
    if (id == null) return;
    ScreenAcquisitionNode screenAcquisitionNode = getScreenAcquisitionNode(screenIndex, screenAcquisitionIndex, true);
    screenAcquisitionNode.setNodeID(id);
  }

  /* @see loci.formats.meta.MetadataStore#setScreenAcquisitionStartTime(String, int, int) */
  public void setScreenAcquisitionStartTime(String startTime, int screenIndex, int screenAcquisitionIndex) {
    if (startTime == null) return;
    ScreenAcquisitionNode screenAcquisitionNode = getScreenAcquisitionNode(screenIndex, screenAcquisitionIndex, true);
    screenAcquisitionNode.setStartTime(startTime);
  }

  // - StageLabel property storage -

  /* @see loci.formats.meta.MetadataStore#setStageLabelName(String, int) */
  public void setStageLabelName(String name, int imageIndex) {
    if (name == null) return;
    StageLabelNode stageLabelNode = getStageLabelNode(imageIndex, true);
    stageLabelNode.setName(name);
  }

  /* @see loci.formats.meta.MetadataStore#setStageLabelX(Float, int) */
  public void setStageLabelX(Float x, int imageIndex) {
    if (x == null) return;
    StageLabelNode stageLabelNode = getStageLabelNode(imageIndex, true);
    stageLabelNode.setX(x);
  }

  /* @see loci.formats.meta.MetadataStore#setStageLabelY(Float, int) */
  public void setStageLabelY(Float y, int imageIndex) {
    if (y == null) return;
    StageLabelNode stageLabelNode = getStageLabelNode(imageIndex, true);
    stageLabelNode.setY(y);
  }

  /* @see loci.formats.meta.MetadataStore#setStageLabelZ(Float, int) */
  public void setStageLabelZ(Float z, int imageIndex) {
    if (z == null) return;
    StageLabelNode stageLabelNode = getStageLabelNode(imageIndex, true);
    stageLabelNode.setZ(z);
  }

  // - StagePosition property storage -

  /* @see loci.formats.meta.MetadataStore#setStagePositionPositionX(Float, int, int, int) */
  public void setStagePositionPositionX(Float positionX, int imageIndex, int pixelsIndex, int planeIndex) {
    if (positionX == null) return;
    StagePositionNode stagePositionNode = getStagePositionNode(imageIndex, pixelsIndex, planeIndex, true);
    stagePositionNode.setPositionX(positionX);
  }

  /* @see loci.formats.meta.MetadataStore#setStagePositionPositionY(Float, int, int, int) */
  public void setStagePositionPositionY(Float positionY, int imageIndex, int pixelsIndex, int planeIndex) {
    if (positionY == null) return;
    StagePositionNode stagePositionNode = getStagePositionNode(imageIndex, pixelsIndex, planeIndex, true);
    stagePositionNode.setPositionY(positionY);
  }

  /* @see loci.formats.meta.MetadataStore#setStagePositionPositionZ(Float, int, int, int) */
  public void setStagePositionPositionZ(Float positionZ, int imageIndex, int pixelsIndex, int planeIndex) {
    if (positionZ == null) return;
    StagePositionNode stagePositionNode = getStagePositionNode(imageIndex, pixelsIndex, planeIndex, true);
    stagePositionNode.setPositionZ(positionZ);
  }

  // - TiffData property storage -

  /* @see loci.formats.meta.MetadataStore#setTiffDataFileName(String, int, int, int) */
  public void setTiffDataFileName(String fileName, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    if (fileName == null) return;
    UUIDNode uuidNode = getUUIDNode(imageIndex, pixelsIndex, tiffDataIndex, true);
    uuidNode.setFileName(fileName);
  }

  /* @see loci.formats.meta.MetadataStore#setTiffDataFirstC(Integer, int, int, int) */
  public void setTiffDataFirstC(Integer firstC, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    if (firstC == null) return;
    TiffDataNode tiffDataNode = getTiffDataNode(imageIndex, pixelsIndex, tiffDataIndex, true);
    tiffDataNode.setFirstC(firstC);
  }

  /* @see loci.formats.meta.MetadataStore#setTiffDataFirstT(Integer, int, int, int) */
  public void setTiffDataFirstT(Integer firstT, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    if (firstT == null) return;
    TiffDataNode tiffDataNode = getTiffDataNode(imageIndex, pixelsIndex, tiffDataIndex, true);
    tiffDataNode.setFirstT(firstT);
  }

  /* @see loci.formats.meta.MetadataStore#setTiffDataFirstZ(Integer, int, int, int) */
  public void setTiffDataFirstZ(Integer firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    if (firstZ == null) return;
    TiffDataNode tiffDataNode = getTiffDataNode(imageIndex, pixelsIndex, tiffDataIndex, true);
    tiffDataNode.setFirstZ(firstZ);
  }

  /* @see loci.formats.meta.MetadataStore#setTiffDataIFD(Integer, int, int, int) */
  public void setTiffDataIFD(Integer ifd, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    if (ifd == null) return;
    TiffDataNode tiffDataNode = getTiffDataNode(imageIndex, pixelsIndex, tiffDataIndex, true);
    tiffDataNode.setIFD(ifd);
  }

  /* @see loci.formats.meta.MetadataStore#setTiffDataNumPlanes(Integer, int, int, int) */
  public void setTiffDataNumPlanes(Integer numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    if (numPlanes == null) return;
    TiffDataNode tiffDataNode = getTiffDataNode(imageIndex, pixelsIndex, tiffDataIndex, true);
    tiffDataNode.setNumPlanes(numPlanes);
  }

  /* @see loci.formats.meta.MetadataStore#setTiffDataUUID(String, int, int, int) */
  public void setTiffDataUUID(String uuid, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    if (uuid == null) return;
    UUIDNode uuidNode = getUUIDNode(imageIndex, pixelsIndex, tiffDataIndex, true);
    uuidNode.setCData(uuid);
  }

  // - Well property storage -

  /* @see loci.formats.meta.MetadataStore#setWellColumn(Integer, int, int) */
  public void setWellColumn(Integer column, int plateIndex, int wellIndex) {
    if (column == null) return;
    WellNode wellNode = getWellNode(plateIndex, wellIndex, true);
    wellNode.setColumn(column);
  }

  /* @see loci.formats.meta.MetadataStore#setWellExternalDescription(String, int, int) */
  public void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex) {
    if (externalDescription == null) return;
    WellNode wellNode = getWellNode(plateIndex, wellIndex, true);
    wellNode.setExternalDescription(externalDescription);
  }

  /* @see loci.formats.meta.MetadataStore#setWellExternalIdentifier(String, int, int) */
  public void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex) {
    if (externalIdentifier == null) return;
    WellNode wellNode = getWellNode(plateIndex, wellIndex, true);
    wellNode.setExternalIdentifier(externalIdentifier);
  }

  /* @see loci.formats.meta.MetadataStore#setWellID(String, int, int) */
  public void setWellID(String id, int plateIndex, int wellIndex) {
    if (id == null) return;
    WellNode wellNode = getWellNode(plateIndex, wellIndex, true);
    wellNode.setNodeID(id);
  }

  /* @see loci.formats.meta.MetadataStore#setWellRow(Integer, int, int) */
  public void setWellRow(Integer row, int plateIndex, int wellIndex) {
    if (row == null) return;
    WellNode wellNode = getWellNode(plateIndex, wellIndex, true);
    wellNode.setRow(row);
  }

  /* @see loci.formats.meta.MetadataStore#setWellType(String, int, int) */
  public void setWellType(String type, int plateIndex, int wellIndex) {
    if (type == null) return;
    WellNode wellNode = getWellNode(plateIndex, wellIndex, true);
    wellNode.setType(type);
  }

  // - WellSample property storage -

  /* @see loci.formats.meta.MetadataStore#setWellSampleID(String, int, int, int) */
  public void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex) {
    if (id == null) return;
    WellSampleNode wellSampleNode = getWellSampleNode(plateIndex, wellIndex, wellSampleIndex, true);
    wellSampleNode.setNodeID(id);
  }

  /* @see loci.formats.meta.MetadataStore#setWellSampleIndex(Integer, int, int, int) */
  public void setWellSampleIndex(Integer index, int plateIndex, int wellIndex, int wellSampleIndex) {
    if (index == null) return;
    WellSampleNode wellSampleNode = getWellSampleNode(plateIndex, wellIndex, wellSampleIndex, true);
    wellSampleNode.setIndex(index);
  }

  /* @see loci.formats.meta.MetadataStore#setWellSamplePosX(Float, int, int, int) */
  public void setWellSamplePosX(Float posX, int plateIndex, int wellIndex, int wellSampleIndex) {
    if (posX == null) return;
    WellSampleNode wellSampleNode = getWellSampleNode(plateIndex, wellIndex, wellSampleIndex, true);
    wellSampleNode.setPosX(posX);
  }

  /* @see loci.formats.meta.MetadataStore#setWellSamplePosY(Float, int, int, int) */
  public void setWellSamplePosY(Float posY, int plateIndex, int wellIndex, int wellSampleIndex) {
    if (posY == null) return;
    WellSampleNode wellSampleNode = getWellSampleNode(plateIndex, wellIndex, wellSampleIndex, true);
    wellSampleNode.setPosY(posY);
  }

  /* @see loci.formats.meta.MetadataStore#setWellSampleTimepoint(Integer, int, int, int) */
  public void setWellSampleTimepoint(Integer timepoint, int plateIndex, int wellIndex, int wellSampleIndex) {
    if (timepoint == null) return;
    WellSampleNode wellSampleNode = getWellSampleNode(plateIndex, wellIndex, wellSampleIndex, true);
    wellSampleNode.setTimepoint(timepoint);
  }

  // -- Helper methods --

  // Experiment+
  private ExperimentNode experimentNode = null;
  private int experimentNodeExperimentIndex = -1;
  private ExperimentNode getExperimentNode(int experimentIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (experimentNodeExperimentIndex != experimentIndex) match = false;
    if (match) return experimentNode;
    experimentNode = null;
    experimentNodeExperimentIndex = experimentIndex;

    OMENode ome = (OMENode) root;
    // get Experiment+ node
    int count = ome.getExperimentCount();
    if (!create && count <= experimentIndex) return null;
    for (int i=count; i<=experimentIndex; i++) new ExperimentNode(ome);
    List list = ome.getExperimentList();
    experimentNode = (ExperimentNode) list.get(experimentIndex);
    return experimentNode;
  }

  // Experimenter+
  private ExperimenterNode experimenterNode = null;
  private int experimenterNodeExperimenterIndex = -1;
  private ExperimenterNode getExperimenterNode(int experimenterIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (experimenterNodeExperimenterIndex != experimenterIndex) match = false;
    if (match) return experimenterNode;
    experimenterNode = null;
    experimenterNodeExperimenterIndex = experimenterIndex;

    OMENode ome = (OMENode) root;
    // get Experimenter+ node
    int count = ome.getExperimenterCount();
    if (!create && count <= experimenterIndex) return null;
    for (int i=count; i<=experimenterIndex; i++) new ExperimenterNode(ome);
    List list = ome.getExperimenterList();
    experimenterNode = (ExperimenterNode) list.get(experimenterIndex);
    return experimenterNode;
  }

  // Experimenter+/GroupRef+
  private GroupRefNode experimenterGroupRefNode = null;
  private int experimenterGroupRefNodeExperimenterIndex = -1;
  private int experimenterGroupRefNodeGroupRefIndex = -1;
  private GroupRefNode getGroupRefNode(int experimenterIndex, int groupRefIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (experimenterGroupRefNodeExperimenterIndex != experimenterIndex) match = false;
    if (experimenterGroupRefNodeGroupRefIndex != groupRefIndex) match = false;
    if (match) return experimenterGroupRefNode;
    experimenterGroupRefNode = null;
    experimenterGroupRefNodeExperimenterIndex = experimenterIndex;
    experimenterGroupRefNodeGroupRefIndex = groupRefIndex;

    // get Experimenter+ node
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, create);
    if (experimenter == null) return null;
    // get GroupRef+ node
    int count = experimenter.getGroupRefCount();
    if (!create && count <= groupRefIndex) return null;
    for (int i=count; i<=groupRefIndex; i++) new GroupRefNode(experimenter);
    List list = experimenter.getGroupRefList();
    experimenterGroupRefNode = (GroupRefNode) list.get(groupRefIndex);
    return experimenterGroupRefNode;
  }

  // Image+
  private ImageNode imageNode = null;
  private int imageNodeImageIndex = -1;
  private ImageNode getImageNode(int imageIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (imageNodeImageIndex != imageIndex) match = false;
    if (match) return imageNode;
    imageNode = null;
    imageNodeImageIndex = imageIndex;

    OMENode ome = (OMENode) root;
    // get Image+ node
    int count = ome.getImageCount();
    if (!create && count <= imageIndex) return null;
    for (int i=count; i<=imageIndex; i++) new ImageNode(ome);
    List list = ome.getImageList();
    imageNode = (ImageNode) list.get(imageIndex);
    return imageNode;
  }

  // Image+/DisplayOptions
  private DisplayOptionsNode imageDisplayOptionsNode = null;
  private int imageDisplayOptionsNodeImageIndex = -1;
  private DisplayOptionsNode getDisplayOptionsNode(int imageIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (imageDisplayOptionsNodeImageIndex != imageIndex) match = false;
    if (match) return imageDisplayOptionsNode;
    imageDisplayOptionsNode = null;
    imageDisplayOptionsNodeImageIndex = imageIndex;

    // get Image+ node
    ImageNode image = getImageNode(imageIndex, create);
    if (image == null) return null;
    // get DisplayOptions node
    DisplayOptionsNode displayOptions = image.getDisplayOptions();
    if (displayOptions == null) {
      if (create) displayOptions = new DisplayOptionsNode(image);
      else return null;
    }
    imageDisplayOptionsNode = displayOptions;
    return imageDisplayOptionsNode;
  }

  // Image+/DisplayOptions/Projection
  private ProjectionNode imageDisplayOptionsProjectionNode = null;
  private int imageDisplayOptionsProjectionNodeImageIndex = -1;
  private ProjectionNode getProjectionNode(int imageIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (imageDisplayOptionsProjectionNodeImageIndex != imageIndex) match = false;
    if (match) return imageDisplayOptionsProjectionNode;
    imageDisplayOptionsProjectionNode = null;
    imageDisplayOptionsProjectionNodeImageIndex = imageIndex;

    // get Image+/DisplayOptions node
    DisplayOptionsNode displayOptions = getDisplayOptionsNode(imageIndex, create);
    if (displayOptions == null) return null;
    // get Projection node
    ProjectionNode projection = displayOptions.getProjection();
    if (projection == null) {
      if (create) projection = new ProjectionNode(displayOptions);
      else return null;
    }
    imageDisplayOptionsProjectionNode = projection;
    return imageDisplayOptionsProjectionNode;
  }

  // Image+/DisplayOptions/ROI+
  private ROINode imageDisplayOptionsROINode = null;
  private int imageDisplayOptionsROINodeImageIndex = -1;
  private int imageDisplayOptionsROINodeROIIndex = -1;
  private ROINode getROINode(int imageIndex, int roiIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (imageDisplayOptionsROINodeImageIndex != imageIndex) match = false;
    if (imageDisplayOptionsROINodeROIIndex != roiIndex) match = false;
    if (match) return imageDisplayOptionsROINode;
    imageDisplayOptionsROINode = null;
    imageDisplayOptionsROINodeImageIndex = imageIndex;
    imageDisplayOptionsROINodeROIIndex = roiIndex;

    // get Image+/DisplayOptions node
    DisplayOptionsNode displayOptions = getDisplayOptionsNode(imageIndex, create);
    if (displayOptions == null) return null;
    // get ROI+ node
    int count = displayOptions.getROICount();
    if (!create && count <= roiIndex) return null;
    for (int i=count; i<=roiIndex; i++) new ROINode(displayOptions);
    List list = displayOptions.getROIList();
    imageDisplayOptionsROINode = (ROINode) list.get(roiIndex);
    return imageDisplayOptionsROINode;
  }

  // Image+/DisplayOptions/Time
  private TimeNode imageDisplayOptionsTimeNode = null;
  private int imageDisplayOptionsTimeNodeImageIndex = -1;
  private TimeNode getTimeNode(int imageIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (imageDisplayOptionsTimeNodeImageIndex != imageIndex) match = false;
    if (match) return imageDisplayOptionsTimeNode;
    imageDisplayOptionsTimeNode = null;
    imageDisplayOptionsTimeNodeImageIndex = imageIndex;

    // get Image+/DisplayOptions node
    DisplayOptionsNode displayOptions = getDisplayOptionsNode(imageIndex, create);
    if (displayOptions == null) return null;
    // get Time node
    TimeNode time = displayOptions.getTime();
    if (time == null) {
      if (create) time = new TimeNode(displayOptions);
      else return null;
    }
    imageDisplayOptionsTimeNode = time;
    return imageDisplayOptionsTimeNode;
  }

  // Image+/ImagingEnvironment
  private ImagingEnvironmentNode imageImagingEnvironmentNode = null;
  private int imageImagingEnvironmentNodeImageIndex = -1;
  private ImagingEnvironmentNode getImagingEnvironmentNode(int imageIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (imageImagingEnvironmentNodeImageIndex != imageIndex) match = false;
    if (match) return imageImagingEnvironmentNode;
    imageImagingEnvironmentNode = null;
    imageImagingEnvironmentNodeImageIndex = imageIndex;

    // get Image+ node
    ImageNode image = getImageNode(imageIndex, create);
    if (image == null) return null;
    // get ImagingEnvironment node
    ImagingEnvironmentNode imagingEnvironment = image.getImagingEnvironment();
    if (imagingEnvironment == null) {
      if (create) imagingEnvironment = new ImagingEnvironmentNode(image);
      else return null;
    }
    imageImagingEnvironmentNode = imagingEnvironment;
    return imageImagingEnvironmentNode;
  }

  // Image+/LogicalChannel+
  private LogicalChannelNode imageLogicalChannelNode = null;
  private int imageLogicalChannelNodeImageIndex = -1;
  private int imageLogicalChannelNodeLogicalChannelIndex = -1;
  private LogicalChannelNode getLogicalChannelNode(int imageIndex, int logicalChannelIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (imageLogicalChannelNodeImageIndex != imageIndex) match = false;
    if (imageLogicalChannelNodeLogicalChannelIndex != logicalChannelIndex) match = false;
    if (match) return imageLogicalChannelNode;
    imageLogicalChannelNode = null;
    imageLogicalChannelNodeImageIndex = imageIndex;
    imageLogicalChannelNodeLogicalChannelIndex = logicalChannelIndex;

    // get Image+ node
    ImageNode image = getImageNode(imageIndex, create);
    if (image == null) return null;
    // get LogicalChannel+ node
    int count = image.getLogicalChannelCount();
    if (!create && count <= logicalChannelIndex) return null;
    for (int i=count; i<=logicalChannelIndex; i++) new LogicalChannelNode(image);
    List list = image.getLogicalChannelList();
    imageLogicalChannelNode = (LogicalChannelNode) list.get(logicalChannelIndex);
    return imageLogicalChannelNode;
  }

  // Image+/LogicalChannel+/ChannelComponent+
  private ChannelComponentNode imageLogicalChannelChannelComponentNode = null;
  private int imageLogicalChannelChannelComponentNodeImageIndex = -1;
  private int imageLogicalChannelChannelComponentNodeLogicalChannelIndex = -1;
  private int imageLogicalChannelChannelComponentNodeChannelComponentIndex = -1;
  private ChannelComponentNode getChannelComponentNode(int imageIndex, int logicalChannelIndex, int channelComponentIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (imageLogicalChannelChannelComponentNodeImageIndex != imageIndex) match = false;
    if (imageLogicalChannelChannelComponentNodeLogicalChannelIndex != logicalChannelIndex) match = false;
    if (imageLogicalChannelChannelComponentNodeChannelComponentIndex != channelComponentIndex) match = false;
    if (match) return imageLogicalChannelChannelComponentNode;
    imageLogicalChannelChannelComponentNode = null;
    imageLogicalChannelChannelComponentNodeImageIndex = imageIndex;
    imageLogicalChannelChannelComponentNodeLogicalChannelIndex = logicalChannelIndex;
    imageLogicalChannelChannelComponentNodeChannelComponentIndex = channelComponentIndex;

    // get Image+/LogicalChannel+ node
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, create);
    if (logicalChannel == null) return null;
    // get ChannelComponent+ node
    int count = logicalChannel.getChannelComponentCount();
    if (!create && count <= channelComponentIndex) return null;
    for (int i=count; i<=channelComponentIndex; i++) new ChannelComponentNode(logicalChannel);
    List list = logicalChannel.getChannelComponentList();
    imageLogicalChannelChannelComponentNode = (ChannelComponentNode) list.get(channelComponentIndex);
    return imageLogicalChannelChannelComponentNode;
  }

  // Image+/LogicalChannel+/DetectorRef
  private DetectorRefNode imageLogicalChannelDetectorRefNode = null;
  private int imageLogicalChannelDetectorRefNodeImageIndex = -1;
  private int imageLogicalChannelDetectorRefNodeLogicalChannelIndex = -1;
  private DetectorRefNode getDetectorRefNode(int imageIndex, int logicalChannelIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (imageLogicalChannelDetectorRefNodeImageIndex != imageIndex) match = false;
    if (imageLogicalChannelDetectorRefNodeLogicalChannelIndex != logicalChannelIndex) match = false;
    if (match) return imageLogicalChannelDetectorRefNode;
    imageLogicalChannelDetectorRefNode = null;
    imageLogicalChannelDetectorRefNodeImageIndex = imageIndex;
    imageLogicalChannelDetectorRefNodeLogicalChannelIndex = logicalChannelIndex;

    // get Image+/LogicalChannel+ node
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, create);
    if (logicalChannel == null) return null;
    // get DetectorRef node
    DetectorRefNode detectorRef = logicalChannel.getDetectorRef();
    if (detectorRef == null) {
      if (create) detectorRef = new DetectorRefNode(logicalChannel);
      else return null;
    }
    imageLogicalChannelDetectorRefNode = detectorRef;
    return imageLogicalChannelDetectorRefNode;
  }

  // Image+/LogicalChannel+/LightSourceRef
  private LightSourceRefNode imageLogicalChannelLightSourceRefNode = null;
  private int imageLogicalChannelLightSourceRefNodeImageIndex = -1;
  private int imageLogicalChannelLightSourceRefNodeLogicalChannelIndex = -1;
  private LightSourceRefNode getLightSourceRefNode(int imageIndex, int logicalChannelIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (imageLogicalChannelLightSourceRefNodeImageIndex != imageIndex) match = false;
    if (imageLogicalChannelLightSourceRefNodeLogicalChannelIndex != logicalChannelIndex) match = false;
    if (match) return imageLogicalChannelLightSourceRefNode;
    imageLogicalChannelLightSourceRefNode = null;
    imageLogicalChannelLightSourceRefNodeImageIndex = imageIndex;
    imageLogicalChannelLightSourceRefNodeLogicalChannelIndex = logicalChannelIndex;

    // get Image+/LogicalChannel+ node
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, create);
    if (logicalChannel == null) return null;
    // get LightSourceRef node
    LightSourceRefNode lightSourceRef = logicalChannel.getLightSourceRef();
    if (lightSourceRef == null) {
      if (create) lightSourceRef = new LightSourceRefNode(logicalChannel);
      else return null;
    }
    imageLogicalChannelLightSourceRefNode = lightSourceRef;
    return imageLogicalChannelLightSourceRefNode;
  }

  // Image+/LogicalChannel+/OTFRef
  private OTFRefNode imageLogicalChannelOTFRefNode = null;
  private int imageLogicalChannelOTFRefNodeImageIndex = -1;
  private int imageLogicalChannelOTFRefNodeLogicalChannelIndex = -1;
  private OTFRefNode getOTFRefNode(int imageIndex, int logicalChannelIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (imageLogicalChannelOTFRefNodeImageIndex != imageIndex) match = false;
    if (imageLogicalChannelOTFRefNodeLogicalChannelIndex != logicalChannelIndex) match = false;
    if (match) return imageLogicalChannelOTFRefNode;
    imageLogicalChannelOTFRefNode = null;
    imageLogicalChannelOTFRefNodeImageIndex = imageIndex;
    imageLogicalChannelOTFRefNodeLogicalChannelIndex = logicalChannelIndex;

    // get Image+/LogicalChannel+ node
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, create);
    if (logicalChannel == null) return null;
    // get OTFRef node
    OTFRefNode otfRef = logicalChannel.getOTFRef();
    if (otfRef == null) {
      if (create) otfRef = new OTFRefNode(logicalChannel);
      else return null;
    }
    imageLogicalChannelOTFRefNode = otfRef;
    return imageLogicalChannelOTFRefNode;
  }

  // Image+/ObjectiveRef
  private ObjectiveRefNode imageObjectiveRefNode = null;
  private int imageObjectiveRefNodeImageIndex = -1;
  private ObjectiveRefNode getObjectiveRefNode(int imageIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (imageObjectiveRefNodeImageIndex != imageIndex) match = false;
    if (match) return imageObjectiveRefNode;
    imageObjectiveRefNode = null;
    imageObjectiveRefNodeImageIndex = imageIndex;

    // get Image+ node
    ImageNode image = getImageNode(imageIndex, create);
    if (image == null) return null;
    // get ObjectiveRef node
    ObjectiveRefNode objectiveRef = image.getObjectiveRef();
    if (objectiveRef == null) {
      if (create) objectiveRef = new ObjectiveRefNode(image);
      else return null;
    }
    imageObjectiveRefNode = objectiveRef;
    return imageObjectiveRefNode;
  }

  // Image+/Pixels+
  private PixelsNode imagePixelsNode = null;
  private int imagePixelsNodeImageIndex = -1;
  private int imagePixelsNodePixelsIndex = -1;
  private PixelsNode getPixelsNode(int imageIndex, int pixelsIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (imagePixelsNodeImageIndex != imageIndex) match = false;
    if (imagePixelsNodePixelsIndex != pixelsIndex) match = false;
    if (match) return imagePixelsNode;
    imagePixelsNode = null;
    imagePixelsNodeImageIndex = imageIndex;
    imagePixelsNodePixelsIndex = pixelsIndex;

    // get Image+ node
    ImageNode image = getImageNode(imageIndex, create);
    if (image == null) return null;
    // get Pixels+ node
    int count = image.getPixelsCount();
    if (!create && count <= pixelsIndex) return null;
    for (int i=count; i<=pixelsIndex; i++) new PixelsNode(image);
    List list = image.getPixelsList();
    imagePixelsNode = (PixelsNode) list.get(pixelsIndex);
    return imagePixelsNode;
  }

  // Image+/Pixels+/Plane+
  private PlaneNode imagePixelsPlaneNode = null;
  private int imagePixelsPlaneNodeImageIndex = -1;
  private int imagePixelsPlaneNodePixelsIndex = -1;
  private int imagePixelsPlaneNodePlaneIndex = -1;
  private PlaneNode getPlaneNode(int imageIndex, int pixelsIndex, int planeIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (imagePixelsPlaneNodeImageIndex != imageIndex) match = false;
    if (imagePixelsPlaneNodePixelsIndex != pixelsIndex) match = false;
    if (imagePixelsPlaneNodePlaneIndex != planeIndex) match = false;
    if (match) return imagePixelsPlaneNode;
    imagePixelsPlaneNode = null;
    imagePixelsPlaneNodeImageIndex = imageIndex;
    imagePixelsPlaneNodePixelsIndex = pixelsIndex;
    imagePixelsPlaneNodePlaneIndex = planeIndex;

    // get Image+/Pixels+ node
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, create);
    if (pixels == null) return null;
    // get Plane+ node
    int count = pixels.getPlaneCount();
    if (!create && count <= planeIndex) return null;
    for (int i=count; i<=planeIndex; i++) new PlaneNode(pixels);
    List list = pixels.getPlaneList();
    imagePixelsPlaneNode = (PlaneNode) list.get(planeIndex);
    return imagePixelsPlaneNode;
  }

  // Image+/Pixels+/Plane+/PlaneTiming
  private PlaneTimingNode imagePixelsPlanePlaneTimingNode = null;
  private int imagePixelsPlanePlaneTimingNodeImageIndex = -1;
  private int imagePixelsPlanePlaneTimingNodePixelsIndex = -1;
  private int imagePixelsPlanePlaneTimingNodePlaneIndex = -1;
  private PlaneTimingNode getPlaneTimingNode(int imageIndex, int pixelsIndex, int planeIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (imagePixelsPlanePlaneTimingNodeImageIndex != imageIndex) match = false;
    if (imagePixelsPlanePlaneTimingNodePixelsIndex != pixelsIndex) match = false;
    if (imagePixelsPlanePlaneTimingNodePlaneIndex != planeIndex) match = false;
    if (match) return imagePixelsPlanePlaneTimingNode;
    imagePixelsPlanePlaneTimingNode = null;
    imagePixelsPlanePlaneTimingNodeImageIndex = imageIndex;
    imagePixelsPlanePlaneTimingNodePixelsIndex = pixelsIndex;
    imagePixelsPlanePlaneTimingNodePlaneIndex = planeIndex;

    // get Image+/Pixels+/Plane+ node
    PlaneNode plane = getPlaneNode(imageIndex, pixelsIndex, planeIndex, create);
    if (plane == null) return null;
    // get PlaneTiming node
    PlaneTimingNode planeTiming = plane.getPlaneTiming();
    if (planeTiming == null) {
      if (create) planeTiming = new PlaneTimingNode(plane);
      else return null;
    }
    imagePixelsPlanePlaneTimingNode = planeTiming;
    return imagePixelsPlanePlaneTimingNode;
  }

  // Image+/Pixels+/Plane+/StagePosition
  private StagePositionNode imagePixelsPlaneStagePositionNode = null;
  private int imagePixelsPlaneStagePositionNodeImageIndex = -1;
  private int imagePixelsPlaneStagePositionNodePixelsIndex = -1;
  private int imagePixelsPlaneStagePositionNodePlaneIndex = -1;
  private StagePositionNode getStagePositionNode(int imageIndex, int pixelsIndex, int planeIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (imagePixelsPlaneStagePositionNodeImageIndex != imageIndex) match = false;
    if (imagePixelsPlaneStagePositionNodePixelsIndex != pixelsIndex) match = false;
    if (imagePixelsPlaneStagePositionNodePlaneIndex != planeIndex) match = false;
    if (match) return imagePixelsPlaneStagePositionNode;
    imagePixelsPlaneStagePositionNode = null;
    imagePixelsPlaneStagePositionNodeImageIndex = imageIndex;
    imagePixelsPlaneStagePositionNodePixelsIndex = pixelsIndex;
    imagePixelsPlaneStagePositionNodePlaneIndex = planeIndex;

    // get Image+/Pixels+/Plane+ node
    PlaneNode plane = getPlaneNode(imageIndex, pixelsIndex, planeIndex, create);
    if (plane == null) return null;
    // get StagePosition node
    StagePositionNode stagePosition = plane.getStagePosition();
    if (stagePosition == null) {
      if (create) stagePosition = new StagePositionNode(plane);
      else return null;
    }
    imagePixelsPlaneStagePositionNode = stagePosition;
    return imagePixelsPlaneStagePositionNode;
  }

  // Image+/Pixels+/TiffData+
  private TiffDataNode imagePixelsTiffDataNode = null;
  private int imagePixelsTiffDataNodeImageIndex = -1;
  private int imagePixelsTiffDataNodePixelsIndex = -1;
  private int imagePixelsTiffDataNodeTiffDataIndex = -1;
  private TiffDataNode getTiffDataNode(int imageIndex, int pixelsIndex, int tiffDataIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (imagePixelsTiffDataNodeImageIndex != imageIndex) match = false;
    if (imagePixelsTiffDataNodePixelsIndex != pixelsIndex) match = false;
    if (imagePixelsTiffDataNodeTiffDataIndex != tiffDataIndex) match = false;
    if (match) return imagePixelsTiffDataNode;
    imagePixelsTiffDataNode = null;
    imagePixelsTiffDataNodeImageIndex = imageIndex;
    imagePixelsTiffDataNodePixelsIndex = pixelsIndex;
    imagePixelsTiffDataNodeTiffDataIndex = tiffDataIndex;

    // get Image+/Pixels+ node
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, create);
    if (pixels == null) return null;
    // get TiffData+ node
    int count = pixels.getTiffDataCount();
    if (!create && count <= tiffDataIndex) return null;
    for (int i=count; i<=tiffDataIndex; i++) new TiffDataNode(pixels);
    List list = pixels.getTiffDataList();
    imagePixelsTiffDataNode = (TiffDataNode) list.get(tiffDataIndex);
    return imagePixelsTiffDataNode;
  }

  // Image+/Pixels+/TiffData+/UUID
  private UUIDNode imagePixelsTiffDataUUIDNode = null;
  private int imagePixelsTiffDataUUIDNodeImageIndex = -1;
  private int imagePixelsTiffDataUUIDNodePixelsIndex = -1;
  private int imagePixelsTiffDataUUIDNodeTiffDataIndex = -1;
  private UUIDNode getUUIDNode(int imageIndex, int pixelsIndex, int tiffDataIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (imagePixelsTiffDataUUIDNodeImageIndex != imageIndex) match = false;
    if (imagePixelsTiffDataUUIDNodePixelsIndex != pixelsIndex) match = false;
    if (imagePixelsTiffDataUUIDNodeTiffDataIndex != tiffDataIndex) match = false;
    if (match) return imagePixelsTiffDataUUIDNode;
    imagePixelsTiffDataUUIDNode = null;
    imagePixelsTiffDataUUIDNodeImageIndex = imageIndex;
    imagePixelsTiffDataUUIDNodePixelsIndex = pixelsIndex;
    imagePixelsTiffDataUUIDNodeTiffDataIndex = tiffDataIndex;

    // get Image+/Pixels+/TiffData+ node
    TiffDataNode tiffData = getTiffDataNode(imageIndex, pixelsIndex, tiffDataIndex, create);
    if (tiffData == null) return null;
    // get UUID node
    UUIDNode uuid = tiffData.getUUID();
    if (uuid == null) {
      if (create) uuid = new UUIDNode(tiffData);
      else return null;
    }
    imagePixelsTiffDataUUIDNode = uuid;
    return imagePixelsTiffDataUUIDNode;
  }

  // Image+/StageLabel
  private StageLabelNode imageStageLabelNode = null;
  private int imageStageLabelNodeImageIndex = -1;
  private StageLabelNode getStageLabelNode(int imageIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (imageStageLabelNodeImageIndex != imageIndex) match = false;
    if (match) return imageStageLabelNode;
    imageStageLabelNode = null;
    imageStageLabelNodeImageIndex = imageIndex;

    // get Image+ node
    ImageNode image = getImageNode(imageIndex, create);
    if (image == null) return null;
    // get StageLabel node
    StageLabelNode stageLabel = image.getStageLabel();
    if (stageLabel == null) {
      if (create) stageLabel = new StageLabelNode(image);
      else return null;
    }
    imageStageLabelNode = stageLabel;
    return imageStageLabelNode;
  }

  // Instrument+
  private InstrumentNode instrumentNode = null;
  private int instrumentNodeInstrumentIndex = -1;
  private InstrumentNode getInstrumentNode(int instrumentIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (instrumentNodeInstrumentIndex != instrumentIndex) match = false;
    if (match) return instrumentNode;
    instrumentNode = null;
    instrumentNodeInstrumentIndex = instrumentIndex;

    OMENode ome = (OMENode) root;
    // get Instrument+ node
    int count = ome.getInstrumentCount();
    if (!create && count <= instrumentIndex) return null;
    for (int i=count; i<=instrumentIndex; i++) new InstrumentNode(ome);
    List list = ome.getInstrumentList();
    instrumentNode = (InstrumentNode) list.get(instrumentIndex);
    return instrumentNode;
  }

  // Instrument+/Detector+
  private DetectorNode instrumentDetectorNode = null;
  private int instrumentDetectorNodeInstrumentIndex = -1;
  private int instrumentDetectorNodeDetectorIndex = -1;
  private DetectorNode getDetectorNode(int instrumentIndex, int detectorIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (instrumentDetectorNodeInstrumentIndex != instrumentIndex) match = false;
    if (instrumentDetectorNodeDetectorIndex != detectorIndex) match = false;
    if (match) return instrumentDetectorNode;
    instrumentDetectorNode = null;
    instrumentDetectorNodeInstrumentIndex = instrumentIndex;
    instrumentDetectorNodeDetectorIndex = detectorIndex;

    // get Instrument+ node
    InstrumentNode instrument = getInstrumentNode(instrumentIndex, create);
    if (instrument == null) return null;
    // get Detector+ node
    int count = instrument.getDetectorCount();
    if (!create && count <= detectorIndex) return null;
    for (int i=count; i<=detectorIndex; i++) new DetectorNode(instrument);
    List list = instrument.getDetectorList();
    instrumentDetectorNode = (DetectorNode) list.get(detectorIndex);
    return instrumentDetectorNode;
  }

  // Instrument+/LightSource+
  private LightSourceNode instrumentLightSourceNode = null;
  private int instrumentLightSourceNodeInstrumentIndex = -1;
  private int instrumentLightSourceNodeLightSourceIndex = -1;
  private LightSourceNode getLightSourceNode(int instrumentIndex, int lightSourceIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (instrumentLightSourceNodeInstrumentIndex != instrumentIndex) match = false;
    if (instrumentLightSourceNodeLightSourceIndex != lightSourceIndex) match = false;
    if (match) return instrumentLightSourceNode;
    instrumentLightSourceNode = null;
    instrumentLightSourceNodeInstrumentIndex = instrumentIndex;
    instrumentLightSourceNodeLightSourceIndex = lightSourceIndex;

    // get Instrument+ node
    InstrumentNode instrument = getInstrumentNode(instrumentIndex, create);
    if (instrument == null) return null;
    // get LightSource+ node
    int count = instrument.getLightSourceCount();
    if (!create && count <= lightSourceIndex) return null;
    for (int i=count; i<=lightSourceIndex; i++) new LightSourceNode(instrument);
    List list = instrument.getLightSourceList();
    instrumentLightSourceNode = (LightSourceNode) list.get(lightSourceIndex);
    return instrumentLightSourceNode;
  }

  // Instrument+/LightSource+/Arc
  private ArcNode instrumentLightSourceArcNode = null;
  private int instrumentLightSourceArcNodeInstrumentIndex = -1;
  private int instrumentLightSourceArcNodeLightSourceIndex = -1;
  private ArcNode getArcNode(int instrumentIndex, int lightSourceIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (instrumentLightSourceArcNodeInstrumentIndex != instrumentIndex) match = false;
    if (instrumentLightSourceArcNodeLightSourceIndex != lightSourceIndex) match = false;
    if (match) return instrumentLightSourceArcNode;
    instrumentLightSourceArcNode = null;
    instrumentLightSourceArcNodeInstrumentIndex = instrumentIndex;
    instrumentLightSourceArcNodeLightSourceIndex = lightSourceIndex;

    // get Instrument+/LightSource+ node
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, create);
    if (lightSource == null) return null;
    // get Arc node
    ArcNode arc = lightSource.getArc();
    if (arc == null) {
      if (create) arc = new ArcNode(lightSource);
      else return null;
    }
    instrumentLightSourceArcNode = arc;
    return instrumentLightSourceArcNode;
  }

  // Instrument+/LightSource+/Filament
  private FilamentNode instrumentLightSourceFilamentNode = null;
  private int instrumentLightSourceFilamentNodeInstrumentIndex = -1;
  private int instrumentLightSourceFilamentNodeLightSourceIndex = -1;
  private FilamentNode getFilamentNode(int instrumentIndex, int lightSourceIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (instrumentLightSourceFilamentNodeInstrumentIndex != instrumentIndex) match = false;
    if (instrumentLightSourceFilamentNodeLightSourceIndex != lightSourceIndex) match = false;
    if (match) return instrumentLightSourceFilamentNode;
    instrumentLightSourceFilamentNode = null;
    instrumentLightSourceFilamentNodeInstrumentIndex = instrumentIndex;
    instrumentLightSourceFilamentNodeLightSourceIndex = lightSourceIndex;

    // get Instrument+/LightSource+ node
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, create);
    if (lightSource == null) return null;
    // get Filament node
    FilamentNode filament = lightSource.getFilament();
    if (filament == null) {
      if (create) filament = new FilamentNode(lightSource);
      else return null;
    }
    instrumentLightSourceFilamentNode = filament;
    return instrumentLightSourceFilamentNode;
  }

  // Instrument+/LightSource+/Laser
  private LaserNode instrumentLightSourceLaserNode = null;
  private int instrumentLightSourceLaserNodeInstrumentIndex = -1;
  private int instrumentLightSourceLaserNodeLightSourceIndex = -1;
  private LaserNode getLaserNode(int instrumentIndex, int lightSourceIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (instrumentLightSourceLaserNodeInstrumentIndex != instrumentIndex) match = false;
    if (instrumentLightSourceLaserNodeLightSourceIndex != lightSourceIndex) match = false;
    if (match) return instrumentLightSourceLaserNode;
    instrumentLightSourceLaserNode = null;
    instrumentLightSourceLaserNodeInstrumentIndex = instrumentIndex;
    instrumentLightSourceLaserNodeLightSourceIndex = lightSourceIndex;

    // get Instrument+/LightSource+ node
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, create);
    if (lightSource == null) return null;
    // get Laser node
    LaserNode laser = lightSource.getLaser();
    if (laser == null) {
      if (create) laser = new LaserNode(lightSource);
      else return null;
    }
    instrumentLightSourceLaserNode = laser;
    return instrumentLightSourceLaserNode;
  }

  // Instrument+/OTF+
  private OTFNode instrumentOTFNode = null;
  private int instrumentOTFNodeInstrumentIndex = -1;
  private int instrumentOTFNodeOTFIndex = -1;
  private OTFNode getOTFNode(int instrumentIndex, int otfIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (instrumentOTFNodeInstrumentIndex != instrumentIndex) match = false;
    if (instrumentOTFNodeOTFIndex != otfIndex) match = false;
    if (match) return instrumentOTFNode;
    instrumentOTFNode = null;
    instrumentOTFNodeInstrumentIndex = instrumentIndex;
    instrumentOTFNodeOTFIndex = otfIndex;

    // get Instrument+ node
    InstrumentNode instrument = getInstrumentNode(instrumentIndex, create);
    if (instrument == null) return null;
    // get OTF+ node
    int count = instrument.getOTFCount();
    if (!create && count <= otfIndex) return null;
    for (int i=count; i<=otfIndex; i++) new OTFNode(instrument);
    List list = instrument.getOTFList();
    instrumentOTFNode = (OTFNode) list.get(otfIndex);
    return instrumentOTFNode;
  }

  // Instrument+/OTF+/ObjectiveRef
  private ObjectiveRefNode instrumentOTFObjectiveRefNode = null;
  private int instrumentOTFObjectiveRefNodeInstrumentIndex = -1;
  private int instrumentOTFObjectiveRefNodeOTFIndex = -1;
  private ObjectiveRefNode getObjectiveRefNode(int instrumentIndex, int otfIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (instrumentOTFObjectiveRefNodeInstrumentIndex != instrumentIndex) match = false;
    if (instrumentOTFObjectiveRefNodeOTFIndex != otfIndex) match = false;
    if (match) return instrumentOTFObjectiveRefNode;
    instrumentOTFObjectiveRefNode = null;
    instrumentOTFObjectiveRefNodeInstrumentIndex = instrumentIndex;
    instrumentOTFObjectiveRefNodeOTFIndex = otfIndex;

    // get Instrument+/OTF+ node
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, create);
    if (otf == null) return null;
    // get ObjectiveRef node
    ObjectiveRefNode objectiveRef = otf.getObjectiveRef();
    if (objectiveRef == null) {
      if (create) objectiveRef = new ObjectiveRefNode(otf);
      else return null;
    }
    instrumentOTFObjectiveRefNode = objectiveRef;
    return instrumentOTFObjectiveRefNode;
  }

  // Instrument+/Objective+
  private ObjectiveNode instrumentObjectiveNode = null;
  private int instrumentObjectiveNodeInstrumentIndex = -1;
  private int instrumentObjectiveNodeObjectiveIndex = -1;
  private ObjectiveNode getObjectiveNode(int instrumentIndex, int objectiveIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (instrumentObjectiveNodeInstrumentIndex != instrumentIndex) match = false;
    if (instrumentObjectiveNodeObjectiveIndex != objectiveIndex) match = false;
    if (match) return instrumentObjectiveNode;
    instrumentObjectiveNode = null;
    instrumentObjectiveNodeInstrumentIndex = instrumentIndex;
    instrumentObjectiveNodeObjectiveIndex = objectiveIndex;

    // get Instrument+ node
    InstrumentNode instrument = getInstrumentNode(instrumentIndex, create);
    if (instrument == null) return null;
    // get Objective+ node
    int count = instrument.getObjectiveCount();
    if (!create && count <= objectiveIndex) return null;
    for (int i=count; i<=objectiveIndex; i++) new ObjectiveNode(instrument);
    List list = instrument.getObjectiveList();
    instrumentObjectiveNode = (ObjectiveNode) list.get(objectiveIndex);
    return instrumentObjectiveNode;
  }

  // Plate+
  private PlateNode plateNode = null;
  private int plateNodePlateIndex = -1;
  private PlateNode getPlateNode(int plateIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (plateNodePlateIndex != plateIndex) match = false;
    if (match) return plateNode;
    plateNode = null;
    plateNodePlateIndex = plateIndex;

    OMENode ome = (OMENode) root;
    // get Plate+ node
    int count = ome.getPlateCount();
    if (!create && count <= plateIndex) return null;
    for (int i=count; i<=plateIndex; i++) new PlateNode(ome);
    List list = ome.getPlateList();
    plateNode = (PlateNode) list.get(plateIndex);
    return plateNode;
  }

  // Plate+/Well+
  private WellNode plateWellNode = null;
  private int plateWellNodePlateIndex = -1;
  private int plateWellNodeWellIndex = -1;
  private WellNode getWellNode(int plateIndex, int wellIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (plateWellNodePlateIndex != plateIndex) match = false;
    if (plateWellNodeWellIndex != wellIndex) match = false;
    if (match) return plateWellNode;
    plateWellNode = null;
    plateWellNodePlateIndex = plateIndex;
    plateWellNodeWellIndex = wellIndex;

    // get Plate+ node
    PlateNode plate = getPlateNode(plateIndex, create);
    if (plate == null) return null;
    // get Well+ node
    int count = plate.getWellCount();
    if (!create && count <= wellIndex) return null;
    for (int i=count; i<=wellIndex; i++) new WellNode(plate);
    List list = plate.getWellList();
    plateWellNode = (WellNode) list.get(wellIndex);
    return plateWellNode;
  }

  // Plate+/Well+/WellSample+
  private WellSampleNode plateWellWellSampleNode = null;
  private int plateWellWellSampleNodePlateIndex = -1;
  private int plateWellWellSampleNodeWellIndex = -1;
  private int plateWellWellSampleNodeWellSampleIndex = -1;
  private WellSampleNode getWellSampleNode(int plateIndex, int wellIndex, int wellSampleIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (plateWellWellSampleNodePlateIndex != plateIndex) match = false;
    if (plateWellWellSampleNodeWellIndex != wellIndex) match = false;
    if (plateWellWellSampleNodeWellSampleIndex != wellSampleIndex) match = false;
    if (match) return plateWellWellSampleNode;
    plateWellWellSampleNode = null;
    plateWellWellSampleNodePlateIndex = plateIndex;
    plateWellWellSampleNodeWellIndex = wellIndex;
    plateWellWellSampleNodeWellSampleIndex = wellSampleIndex;

    // get Plate+/Well+ node
    WellNode well = getWellNode(plateIndex, wellIndex, create);
    if (well == null) return null;
    // get WellSample+ node
    int count = well.getWellSampleCount();
    if (!create && count <= wellSampleIndex) return null;
    for (int i=count; i<=wellSampleIndex; i++) new WellSampleNode(well);
    List list = well.getWellSampleList();
    plateWellWellSampleNode = (WellSampleNode) list.get(wellSampleIndex);
    return plateWellWellSampleNode;
  }

  // Screen+
  private ScreenNode screenNode = null;
  private int screenNodeScreenIndex = -1;
  private ScreenNode getScreenNode(int screenIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (screenNodeScreenIndex != screenIndex) match = false;
    if (match) return screenNode;
    screenNode = null;
    screenNodeScreenIndex = screenIndex;

    OMENode ome = (OMENode) root;
    // get Screen+ node
    int count = ome.getScreenCount();
    if (!create && count <= screenIndex) return null;
    for (int i=count; i<=screenIndex; i++) new ScreenNode(ome);
    List list = ome.getScreenList();
    screenNode = (ScreenNode) list.get(screenIndex);
    return screenNode;
  }

  // Screen+/PlateRef+
  private PlateRefNode screenPlateRefNode = null;
  private int screenPlateRefNodeScreenIndex = -1;
  private int screenPlateRefNodePlateRefIndex = -1;
  private PlateRefNode getPlateRefNode(int screenIndex, int plateRefIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (screenPlateRefNodeScreenIndex != screenIndex) match = false;
    if (screenPlateRefNodePlateRefIndex != plateRefIndex) match = false;
    if (match) return screenPlateRefNode;
    screenPlateRefNode = null;
    screenPlateRefNodeScreenIndex = screenIndex;
    screenPlateRefNodePlateRefIndex = plateRefIndex;

    // get Screen+ node
    ScreenNode screen = getScreenNode(screenIndex, create);
    if (screen == null) return null;
    // get PlateRef+ node
    int count = screen.getPlateRefCount();
    if (!create && count <= plateRefIndex) return null;
    for (int i=count; i<=plateRefIndex; i++) new PlateRefNode(screen);
    List list = screen.getPlateRefList();
    screenPlateRefNode = (PlateRefNode) list.get(plateRefIndex);
    return screenPlateRefNode;
  }

  // Screen+/Reagent+
  private ReagentNode screenReagentNode = null;
  private int screenReagentNodeScreenIndex = -1;
  private int screenReagentNodeReagentIndex = -1;
  private ReagentNode getReagentNode(int screenIndex, int reagentIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (screenReagentNodeScreenIndex != screenIndex) match = false;
    if (screenReagentNodeReagentIndex != reagentIndex) match = false;
    if (match) return screenReagentNode;
    screenReagentNode = null;
    screenReagentNodeScreenIndex = screenIndex;
    screenReagentNodeReagentIndex = reagentIndex;

    // get Screen+ node
    ScreenNode screen = getScreenNode(screenIndex, create);
    if (screen == null) return null;
    // get Reagent+ node
    int count = screen.getReagentCount();
    if (!create && count <= reagentIndex) return null;
    for (int i=count; i<=reagentIndex; i++) new ReagentNode(screen);
    List list = screen.getReagentList();
    screenReagentNode = (ReagentNode) list.get(reagentIndex);
    return screenReagentNode;
  }

  // Screen+/ScreenAcquisition+
  private ScreenAcquisitionNode screenScreenAcquisitionNode = null;
  private int screenScreenAcquisitionNodeScreenIndex = -1;
  private int screenScreenAcquisitionNodeScreenAcquisitionIndex = -1;
  private ScreenAcquisitionNode getScreenAcquisitionNode(int screenIndex, int screenAcquisitionIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (screenScreenAcquisitionNodeScreenIndex != screenIndex) match = false;
    if (screenScreenAcquisitionNodeScreenAcquisitionIndex != screenAcquisitionIndex) match = false;
    if (match) return screenScreenAcquisitionNode;
    screenScreenAcquisitionNode = null;
    screenScreenAcquisitionNodeScreenIndex = screenIndex;
    screenScreenAcquisitionNodeScreenAcquisitionIndex = screenAcquisitionIndex;

    // get Screen+ node
    ScreenNode screen = getScreenNode(screenIndex, create);
    if (screen == null) return null;
    // get ScreenAcquisition+ node
    int count = screen.getScreenAcquisitionCount();
    if (!create && count <= screenAcquisitionIndex) return null;
    for (int i=count; i<=screenAcquisitionIndex; i++) new ScreenAcquisitionNode(screen);
    List list = screen.getScreenAcquisitionList();
    screenScreenAcquisitionNode = (ScreenAcquisitionNode) list.get(screenAcquisitionIndex);
    return screenScreenAcquisitionNode;
  }

  private void clearCachedNodes() {
    // Experiment+
    experimentNode = null;
    experimentNodeExperimentIndex = -1;
    // Experimenter+
    experimenterNode = null;
    experimenterNodeExperimenterIndex = -1;
    // Experimenter+/GroupRef+
    experimenterGroupRefNode = null;
    experimenterGroupRefNodeExperimenterIndex = -1;
    experimenterGroupRefNodeGroupRefIndex = -1;
    // Image+
    imageNode = null;
    imageNodeImageIndex = -1;
    // Image+/DisplayOptions
    imageDisplayOptionsNode = null;
    imageDisplayOptionsNodeImageIndex = -1;
    // Image+/DisplayOptions/Projection
    imageDisplayOptionsProjectionNode = null;
    imageDisplayOptionsProjectionNodeImageIndex = -1;
    // Image+/DisplayOptions/ROI+
    imageDisplayOptionsROINode = null;
    imageDisplayOptionsROINodeImageIndex = -1;
    imageDisplayOptionsROINodeROIIndex = -1;
    // Image+/DisplayOptions/Time
    imageDisplayOptionsTimeNode = null;
    imageDisplayOptionsTimeNodeImageIndex = -1;
    // Image+/ImagingEnvironment
    imageImagingEnvironmentNode = null;
    imageImagingEnvironmentNodeImageIndex = -1;
    // Image+/LogicalChannel+
    imageLogicalChannelNode = null;
    imageLogicalChannelNodeImageIndex = -1;
    imageLogicalChannelNodeLogicalChannelIndex = -1;
    // Image+/LogicalChannel+/ChannelComponent+
    imageLogicalChannelChannelComponentNode = null;
    imageLogicalChannelChannelComponentNodeImageIndex = -1;
    imageLogicalChannelChannelComponentNodeLogicalChannelIndex = -1;
    imageLogicalChannelChannelComponentNodeChannelComponentIndex = -1;
    // Image+/LogicalChannel+/DetectorRef
    imageLogicalChannelDetectorRefNode = null;
    imageLogicalChannelDetectorRefNodeImageIndex = -1;
    imageLogicalChannelDetectorRefNodeLogicalChannelIndex = -1;
    // Image+/LogicalChannel+/LightSourceRef
    imageLogicalChannelLightSourceRefNode = null;
    imageLogicalChannelLightSourceRefNodeImageIndex = -1;
    imageLogicalChannelLightSourceRefNodeLogicalChannelIndex = -1;
    // Image+/LogicalChannel+/OTFRef
    imageLogicalChannelOTFRefNode = null;
    imageLogicalChannelOTFRefNodeImageIndex = -1;
    imageLogicalChannelOTFRefNodeLogicalChannelIndex = -1;
    // Image+/ObjectiveRef
    imageObjectiveRefNode = null;
    imageObjectiveRefNodeImageIndex = -1;
    // Image+/Pixels+
    imagePixelsNode = null;
    imagePixelsNodeImageIndex = -1;
    imagePixelsNodePixelsIndex = -1;
    // Image+/Pixels+/Plane+
    imagePixelsPlaneNode = null;
    imagePixelsPlaneNodeImageIndex = -1;
    imagePixelsPlaneNodePixelsIndex = -1;
    imagePixelsPlaneNodePlaneIndex = -1;
    // Image+/Pixels+/Plane+/PlaneTiming
    imagePixelsPlanePlaneTimingNode = null;
    imagePixelsPlanePlaneTimingNodeImageIndex = -1;
    imagePixelsPlanePlaneTimingNodePixelsIndex = -1;
    imagePixelsPlanePlaneTimingNodePlaneIndex = -1;
    // Image+/Pixels+/Plane+/StagePosition
    imagePixelsPlaneStagePositionNode = null;
    imagePixelsPlaneStagePositionNodeImageIndex = -1;
    imagePixelsPlaneStagePositionNodePixelsIndex = -1;
    imagePixelsPlaneStagePositionNodePlaneIndex = -1;
    // Image+/Pixels+/TiffData+
    imagePixelsTiffDataNode = null;
    imagePixelsTiffDataNodeImageIndex = -1;
    imagePixelsTiffDataNodePixelsIndex = -1;
    imagePixelsTiffDataNodeTiffDataIndex = -1;
    // Image+/Pixels+/TiffData+/UUID
    imagePixelsTiffDataUUIDNode = null;
    imagePixelsTiffDataUUIDNodeImageIndex = -1;
    imagePixelsTiffDataUUIDNodePixelsIndex = -1;
    imagePixelsTiffDataUUIDNodeTiffDataIndex = -1;
    // Image+/StageLabel
    imageStageLabelNode = null;
    imageStageLabelNodeImageIndex = -1;
    // Instrument+
    instrumentNode = null;
    instrumentNodeInstrumentIndex = -1;
    // Instrument+/Detector+
    instrumentDetectorNode = null;
    instrumentDetectorNodeInstrumentIndex = -1;
    instrumentDetectorNodeDetectorIndex = -1;
    // Instrument+/LightSource+
    instrumentLightSourceNode = null;
    instrumentLightSourceNodeInstrumentIndex = -1;
    instrumentLightSourceNodeLightSourceIndex = -1;
    // Instrument+/LightSource+/Arc
    instrumentLightSourceArcNode = null;
    instrumentLightSourceArcNodeInstrumentIndex = -1;
    instrumentLightSourceArcNodeLightSourceIndex = -1;
    // Instrument+/LightSource+/Filament
    instrumentLightSourceFilamentNode = null;
    instrumentLightSourceFilamentNodeInstrumentIndex = -1;
    instrumentLightSourceFilamentNodeLightSourceIndex = -1;
    // Instrument+/LightSource+/Laser
    instrumentLightSourceLaserNode = null;
    instrumentLightSourceLaserNodeInstrumentIndex = -1;
    instrumentLightSourceLaserNodeLightSourceIndex = -1;
    // Instrument+/OTF+
    instrumentOTFNode = null;
    instrumentOTFNodeInstrumentIndex = -1;
    instrumentOTFNodeOTFIndex = -1;
    // Instrument+/OTF+/ObjectiveRef
    instrumentOTFObjectiveRefNode = null;
    instrumentOTFObjectiveRefNodeInstrumentIndex = -1;
    instrumentOTFObjectiveRefNodeOTFIndex = -1;
    // Instrument+/Objective+
    instrumentObjectiveNode = null;
    instrumentObjectiveNodeInstrumentIndex = -1;
    instrumentObjectiveNodeObjectiveIndex = -1;
    // Plate+
    plateNode = null;
    plateNodePlateIndex = -1;
    // Plate+/Well+
    plateWellNode = null;
    plateWellNodePlateIndex = -1;
    plateWellNodeWellIndex = -1;
    // Plate+/Well+/WellSample+
    plateWellWellSampleNode = null;
    plateWellWellSampleNodePlateIndex = -1;
    plateWellWellSampleNodeWellIndex = -1;
    plateWellWellSampleNodeWellSampleIndex = -1;
    // Screen+
    screenNode = null;
    screenNodeScreenIndex = -1;
    // Screen+/PlateRef+
    screenPlateRefNode = null;
    screenPlateRefNodeScreenIndex = -1;
    screenPlateRefNodePlateRefIndex = -1;
    // Screen+/Reagent+
    screenReagentNode = null;
    screenReagentNodeScreenIndex = -1;
    screenReagentNodeReagentIndex = -1;
    // Screen+/ScreenAcquisition+
    screenScreenAcquisitionNode = null;
    screenScreenAcquisitionNodeScreenIndex = -1;
    screenScreenAcquisitionNodeScreenAcquisitionIndex = -1;
  }

}
