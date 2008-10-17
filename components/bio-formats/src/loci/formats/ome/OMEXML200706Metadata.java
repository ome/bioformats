//
// OMEXML200706Metadata.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

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
 * Created by curtis via MetadataAutogen on Oct 17, 2008 4:50:42 PM CDT
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.ome;

import ome.xml.OMEXMLNode;
import ome.xml.r200706.ome.*;
import ome.xml.r200706.spw.*;
import java.util.List;
import loci.formats.LogTools;

/**
 * A metadata store implementation for constructing and manipulating OME-XML
 * DOMs for the 2007-06 version of OME-XML. It requires the
 * ome.xml.r200706 package to compile (part of ome-xml.jar).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/ome/OMEXML200706Metadata.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/ome/OMEXML200706Metadata.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 */
public class OMEXML200706Metadata extends OMEXMLMetadata {

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
    // NB: Power unsupported for schema version 2007-06
    return null;
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
  public Integer getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex) {
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, false);
    return logicalChannel == null ? null : logicalChannel.getPinholeSize();
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
    // NB: FileName unsupported for schema version 2007-06
    return null;
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
    // NB: UUID unsupported for schema version 2007-06
    return null;
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
      root = ome.xml.OMEXMLFactory.newOMENode("2007-06");
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
    // NB: Power unsupported for schema version 2007-06
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

  /* @see loci.formats.meta.MetadataStore#setLogicalChannelPinholeSize(Integer, int, int) */
  public void setLogicalChannelPinholeSize(Integer pinholeSize, int imageIndex, int logicalChannelIndex) {
    if (pinholeSize == null) return;
    LogicalChannelNode logicalChannelNode = getLogicalChannelNode(imageIndex, logicalChannelIndex, true);
    logicalChannelNode.setPinholeSize(pinholeSize);
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
    // NB: FileName unsupported for schema version 2007-06
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
    // NB: UUID unsupported for schema version 2007-06
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
  private GroupRefNode groupRefNode = null;
  private int groupRefNodeExperimenterIndex = -1;
  private int groupRefNodeGroupRefIndex = -1;
  private GroupRefNode getGroupRefNode(int experimenterIndex, int groupRefIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (groupRefNodeExperimenterIndex != experimenterIndex) match = false;
    if (groupRefNodeGroupRefIndex != groupRefIndex) match = false;
    if (match) return groupRefNode;
    groupRefNode = null;
    groupRefNodeExperimenterIndex = experimenterIndex;
    groupRefNodeGroupRefIndex = groupRefIndex;

    // get Experimenter+ node
    ExperimenterNode experimenter = getExperimenterNode(experimenterIndex, create);
    if (experimenter == null) return null;
    // get GroupRef+ node
    int count = experimenter.getGroupRefCount();
    if (!create && count <= groupRefIndex) return null;
    for (int i=count; i<=groupRefIndex; i++) new GroupRefNode(experimenter);
    List list = experimenter.getGroupRefList();
    groupRefNode = (GroupRefNode) list.get(groupRefIndex);
    return groupRefNode;
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
  private DisplayOptionsNode displayOptionsNode = null;
  private int displayOptionsNodeImageIndex = -1;
  private DisplayOptionsNode getDisplayOptionsNode(int imageIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (displayOptionsNodeImageIndex != imageIndex) match = false;
    if (match) return displayOptionsNode;
    displayOptionsNode = null;
    displayOptionsNodeImageIndex = imageIndex;

    // get Image+ node
    ImageNode image = getImageNode(imageIndex, create);
    if (image == null) return null;
    // get DisplayOptions node
    DisplayOptionsNode displayOptions = image.getDisplayOptions();
    if (displayOptions == null) {
      if (create) displayOptions = new DisplayOptionsNode(image);
      else return null;
    }
    displayOptionsNode = displayOptions;
    return displayOptionsNode;
  }

  // Image+/DisplayOptions/Projection
  private ProjectionNode projectionNode = null;
  private int projectionNodeImageIndex = -1;
  private ProjectionNode getProjectionNode(int imageIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (projectionNodeImageIndex != imageIndex) match = false;
    if (match) return projectionNode;
    projectionNode = null;
    projectionNodeImageIndex = imageIndex;

    // get Image+/DisplayOptions node
    DisplayOptionsNode displayOptions = getDisplayOptionsNode(imageIndex, create);
    if (displayOptions == null) return null;
    // get Projection node
    ProjectionNode projection = displayOptions.getProjection();
    if (projection == null) {
      if (create) projection = new ProjectionNode(displayOptions);
      else return null;
    }
    projectionNode = projection;
    return projectionNode;
  }

  // Image+/DisplayOptions/ROI+
  private ROINode roiNode = null;
  private int roiNodeImageIndex = -1;
  private int roiNodeROIIndex = -1;
  private ROINode getROINode(int imageIndex, int roiIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (roiNodeImageIndex != imageIndex) match = false;
    if (roiNodeROIIndex != roiIndex) match = false;
    if (match) return roiNode;
    roiNode = null;
    roiNodeImageIndex = imageIndex;
    roiNodeROIIndex = roiIndex;

    // get Image+/DisplayOptions node
    DisplayOptionsNode displayOptions = getDisplayOptionsNode(imageIndex, create);
    if (displayOptions == null) return null;
    // get ROI+ node
    int count = displayOptions.getROICount();
    if (!create && count <= roiIndex) return null;
    for (int i=count; i<=roiIndex; i++) new ROINode(displayOptions);
    List list = displayOptions.getROIList();
    roiNode = (ROINode) list.get(roiIndex);
    return roiNode;
  }

  // Image+/DisplayOptions/Time
  private TimeNode timeNode = null;
  private int timeNodeImageIndex = -1;
  private TimeNode getTimeNode(int imageIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (timeNodeImageIndex != imageIndex) match = false;
    if (match) return timeNode;
    timeNode = null;
    timeNodeImageIndex = imageIndex;

    // get Image+/DisplayOptions node
    DisplayOptionsNode displayOptions = getDisplayOptionsNode(imageIndex, create);
    if (displayOptions == null) return null;
    // get Time node
    TimeNode time = displayOptions.getTime();
    if (time == null) {
      if (create) time = new TimeNode(displayOptions);
      else return null;
    }
    timeNode = time;
    return timeNode;
  }

  // Image+/ImagingEnvironment
  private ImagingEnvironmentNode imagingEnvironmentNode = null;
  private int imagingEnvironmentNodeImageIndex = -1;
  private ImagingEnvironmentNode getImagingEnvironmentNode(int imageIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (imagingEnvironmentNodeImageIndex != imageIndex) match = false;
    if (match) return imagingEnvironmentNode;
    imagingEnvironmentNode = null;
    imagingEnvironmentNodeImageIndex = imageIndex;

    // get Image+ node
    ImageNode image = getImageNode(imageIndex, create);
    if (image == null) return null;
    // get ImagingEnvironment node
    ImagingEnvironmentNode imagingEnvironment = image.getImagingEnvironment();
    if (imagingEnvironment == null) {
      if (create) imagingEnvironment = new ImagingEnvironmentNode(image);
      else return null;
    }
    imagingEnvironmentNode = imagingEnvironment;
    return imagingEnvironmentNode;
  }

  // Image+/LogicalChannel+
  private LogicalChannelNode logicalChannelNode = null;
  private int logicalChannelNodeImageIndex = -1;
  private int logicalChannelNodeLogicalChannelIndex = -1;
  private LogicalChannelNode getLogicalChannelNode(int imageIndex, int logicalChannelIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (logicalChannelNodeImageIndex != imageIndex) match = false;
    if (logicalChannelNodeLogicalChannelIndex != logicalChannelIndex) match = false;
    if (match) return logicalChannelNode;
    logicalChannelNode = null;
    logicalChannelNodeImageIndex = imageIndex;
    logicalChannelNodeLogicalChannelIndex = logicalChannelIndex;

    // get Image+ node
    ImageNode image = getImageNode(imageIndex, create);
    if (image == null) return null;
    // get LogicalChannel+ node
    int count = image.getLogicalChannelCount();
    if (!create && count <= logicalChannelIndex) return null;
    for (int i=count; i<=logicalChannelIndex; i++) new LogicalChannelNode(image);
    List list = image.getLogicalChannelList();
    logicalChannelNode = (LogicalChannelNode) list.get(logicalChannelIndex);
    return logicalChannelNode;
  }

  // Image+/LogicalChannel+/ChannelComponent+
  private ChannelComponentNode channelComponentNode = null;
  private int channelComponentNodeImageIndex = -1;
  private int channelComponentNodeLogicalChannelIndex = -1;
  private int channelComponentNodeChannelComponentIndex = -1;
  private ChannelComponentNode getChannelComponentNode(int imageIndex, int logicalChannelIndex, int channelComponentIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (channelComponentNodeImageIndex != imageIndex) match = false;
    if (channelComponentNodeLogicalChannelIndex != logicalChannelIndex) match = false;
    if (channelComponentNodeChannelComponentIndex != channelComponentIndex) match = false;
    if (match) return channelComponentNode;
    channelComponentNode = null;
    channelComponentNodeImageIndex = imageIndex;
    channelComponentNodeLogicalChannelIndex = logicalChannelIndex;
    channelComponentNodeChannelComponentIndex = channelComponentIndex;

    // get Image+/LogicalChannel+ node
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, create);
    if (logicalChannel == null) return null;
    // get ChannelComponent+ node
    int count = logicalChannel.getChannelComponentCount();
    if (!create && count <= channelComponentIndex) return null;
    for (int i=count; i<=channelComponentIndex; i++) new ChannelComponentNode(logicalChannel);
    List list = logicalChannel.getChannelComponentList();
    channelComponentNode = (ChannelComponentNode) list.get(channelComponentIndex);
    return channelComponentNode;
  }

  // Image+/LogicalChannel+/DetectorRef
  private DetectorRefNode detectorRefNode = null;
  private int detectorRefNodeImageIndex = -1;
  private int detectorRefNodeLogicalChannelIndex = -1;
  private DetectorRefNode getDetectorRefNode(int imageIndex, int logicalChannelIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (detectorRefNodeImageIndex != imageIndex) match = false;
    if (detectorRefNodeLogicalChannelIndex != logicalChannelIndex) match = false;
    if (match) return detectorRefNode;
    detectorRefNode = null;
    detectorRefNodeImageIndex = imageIndex;
    detectorRefNodeLogicalChannelIndex = logicalChannelIndex;

    // get Image+/LogicalChannel+ node
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, create);
    if (logicalChannel == null) return null;
    // get DetectorRef node
    DetectorRefNode detectorRef = logicalChannel.getDetectorRef();
    if (detectorRef == null) {
      if (create) detectorRef = new DetectorRefNode(logicalChannel);
      else return null;
    }
    detectorRefNode = detectorRef;
    return detectorRefNode;
  }

  // Image+/LogicalChannel+/LightSourceRef
  private LightSourceRefNode lightSourceRefNode = null;
  private int lightSourceRefNodeImageIndex = -1;
  private int lightSourceRefNodeLogicalChannelIndex = -1;
  private LightSourceRefNode getLightSourceRefNode(int imageIndex, int logicalChannelIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (lightSourceRefNodeImageIndex != imageIndex) match = false;
    if (lightSourceRefNodeLogicalChannelIndex != logicalChannelIndex) match = false;
    if (match) return lightSourceRefNode;
    lightSourceRefNode = null;
    lightSourceRefNodeImageIndex = imageIndex;
    lightSourceRefNodeLogicalChannelIndex = logicalChannelIndex;

    // get Image+/LogicalChannel+ node
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, create);
    if (logicalChannel == null) return null;
    // get LightSourceRef node
    LightSourceRefNode lightSourceRef = logicalChannel.getLightSourceRef();
    if (lightSourceRef == null) {
      if (create) lightSourceRef = new LightSourceRefNode(logicalChannel);
      else return null;
    }
    lightSourceRefNode = lightSourceRef;
    return lightSourceRefNode;
  }

  // Image+/LogicalChannel+/OTFRef
  private OTFRefNode otfRefNode = null;
  private int otfRefNodeImageIndex = -1;
  private int otfRefNodeLogicalChannelIndex = -1;
  private OTFRefNode getOTFRefNode(int imageIndex, int logicalChannelIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (otfRefNodeImageIndex != imageIndex) match = false;
    if (otfRefNodeLogicalChannelIndex != logicalChannelIndex) match = false;
    if (match) return otfRefNode;
    otfRefNode = null;
    otfRefNodeImageIndex = imageIndex;
    otfRefNodeLogicalChannelIndex = logicalChannelIndex;

    // get Image+/LogicalChannel+ node
    LogicalChannelNode logicalChannel = getLogicalChannelNode(imageIndex, logicalChannelIndex, create);
    if (logicalChannel == null) return null;
    // get OTFRef node
    OTFRefNode otfRef = logicalChannel.getOTFRef();
    if (otfRef == null) {
      if (create) otfRef = new OTFRefNode(logicalChannel);
      else return null;
    }
    otfRefNode = otfRef;
    return otfRefNode;
  }

  // Image+/Pixels+
  private PixelsNode pixelsNode = null;
  private int pixelsNodeImageIndex = -1;
  private int pixelsNodePixelsIndex = -1;
  private PixelsNode getPixelsNode(int imageIndex, int pixelsIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (pixelsNodeImageIndex != imageIndex) match = false;
    if (pixelsNodePixelsIndex != pixelsIndex) match = false;
    if (match) return pixelsNode;
    pixelsNode = null;
    pixelsNodeImageIndex = imageIndex;
    pixelsNodePixelsIndex = pixelsIndex;

    // get Image+ node
    ImageNode image = getImageNode(imageIndex, create);
    if (image == null) return null;
    // get Pixels+ node
    int count = image.getPixelsCount();
    if (!create && count <= pixelsIndex) return null;
    for (int i=count; i<=pixelsIndex; i++) new PixelsNode(image);
    List list = image.getPixelsList();
    pixelsNode = (PixelsNode) list.get(pixelsIndex);
    return pixelsNode;
  }

  // Image+/Pixels+/Plane+
  private PlaneNode planeNode = null;
  private int planeNodeImageIndex = -1;
  private int planeNodePixelsIndex = -1;
  private int planeNodePlaneIndex = -1;
  private PlaneNode getPlaneNode(int imageIndex, int pixelsIndex, int planeIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (planeNodeImageIndex != imageIndex) match = false;
    if (planeNodePixelsIndex != pixelsIndex) match = false;
    if (planeNodePlaneIndex != planeIndex) match = false;
    if (match) return planeNode;
    planeNode = null;
    planeNodeImageIndex = imageIndex;
    planeNodePixelsIndex = pixelsIndex;
    planeNodePlaneIndex = planeIndex;

    // get Image+/Pixels+ node
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, create);
    if (pixels == null) return null;
    // get Plane+ node
    int count = pixels.getPlaneCount();
    if (!create && count <= planeIndex) return null;
    for (int i=count; i<=planeIndex; i++) new PlaneNode(pixels);
    List list = pixels.getPlaneList();
    planeNode = (PlaneNode) list.get(planeIndex);
    return planeNode;
  }

  // Image+/Pixels+/Plane+/PlaneTiming
  private PlaneTimingNode planeTimingNode = null;
  private int planeTimingNodeImageIndex = -1;
  private int planeTimingNodePixelsIndex = -1;
  private int planeTimingNodePlaneIndex = -1;
  private PlaneTimingNode getPlaneTimingNode(int imageIndex, int pixelsIndex, int planeIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (planeTimingNodeImageIndex != imageIndex) match = false;
    if (planeTimingNodePixelsIndex != pixelsIndex) match = false;
    if (planeTimingNodePlaneIndex != planeIndex) match = false;
    if (match) return planeTimingNode;
    planeTimingNode = null;
    planeTimingNodeImageIndex = imageIndex;
    planeTimingNodePixelsIndex = pixelsIndex;
    planeTimingNodePlaneIndex = planeIndex;

    // get Image+/Pixels+/Plane+ node
    PlaneNode plane = getPlaneNode(imageIndex, pixelsIndex, planeIndex, create);
    if (plane == null) return null;
    // get PlaneTiming node
    PlaneTimingNode planeTiming = plane.getPlaneTiming();
    if (planeTiming == null) {
      if (create) planeTiming = new PlaneTimingNode(plane);
      else return null;
    }
    planeTimingNode = planeTiming;
    return planeTimingNode;
  }

  // Image+/Pixels+/Plane+/StagePosition
  private StagePositionNode stagePositionNode = null;
  private int stagePositionNodeImageIndex = -1;
  private int stagePositionNodePixelsIndex = -1;
  private int stagePositionNodePlaneIndex = -1;
  private StagePositionNode getStagePositionNode(int imageIndex, int pixelsIndex, int planeIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (stagePositionNodeImageIndex != imageIndex) match = false;
    if (stagePositionNodePixelsIndex != pixelsIndex) match = false;
    if (stagePositionNodePlaneIndex != planeIndex) match = false;
    if (match) return stagePositionNode;
    stagePositionNode = null;
    stagePositionNodeImageIndex = imageIndex;
    stagePositionNodePixelsIndex = pixelsIndex;
    stagePositionNodePlaneIndex = planeIndex;

    // get Image+/Pixels+/Plane+ node
    PlaneNode plane = getPlaneNode(imageIndex, pixelsIndex, planeIndex, create);
    if (plane == null) return null;
    // get StagePosition node
    StagePositionNode stagePosition = plane.getStagePosition();
    if (stagePosition == null) {
      if (create) stagePosition = new StagePositionNode(plane);
      else return null;
    }
    stagePositionNode = stagePosition;
    return stagePositionNode;
  }

  // Image+/Pixels+/TiffData+
  private TiffDataNode tiffDataNode = null;
  private int tiffDataNodeImageIndex = -1;
  private int tiffDataNodePixelsIndex = -1;
  private int tiffDataNodeTiffDataIndex = -1;
  private TiffDataNode getTiffDataNode(int imageIndex, int pixelsIndex, int tiffDataIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (tiffDataNodeImageIndex != imageIndex) match = false;
    if (tiffDataNodePixelsIndex != pixelsIndex) match = false;
    if (tiffDataNodeTiffDataIndex != tiffDataIndex) match = false;
    if (match) return tiffDataNode;
    tiffDataNode = null;
    tiffDataNodeImageIndex = imageIndex;
    tiffDataNodePixelsIndex = pixelsIndex;
    tiffDataNodeTiffDataIndex = tiffDataIndex;

    // get Image+/Pixels+ node
    PixelsNode pixels = getPixelsNode(imageIndex, pixelsIndex, create);
    if (pixels == null) return null;
    // get TiffData+ node
    int count = pixels.getTiffDataCount();
    if (!create && count <= tiffDataIndex) return null;
    for (int i=count; i<=tiffDataIndex; i++) new TiffDataNode(pixels);
    List list = pixels.getTiffDataList();
    tiffDataNode = (TiffDataNode) list.get(tiffDataIndex);
    return tiffDataNode;
  }

  // Image+/StageLabel
  private StageLabelNode stageLabelNode = null;
  private int stageLabelNodeImageIndex = -1;
  private StageLabelNode getStageLabelNode(int imageIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (stageLabelNodeImageIndex != imageIndex) match = false;
    if (match) return stageLabelNode;
    stageLabelNode = null;
    stageLabelNodeImageIndex = imageIndex;

    // get Image+ node
    ImageNode image = getImageNode(imageIndex, create);
    if (image == null) return null;
    // get StageLabel node
    StageLabelNode stageLabel = image.getStageLabel();
    if (stageLabel == null) {
      if (create) stageLabel = new StageLabelNode(image);
      else return null;
    }
    stageLabelNode = stageLabel;
    return stageLabelNode;
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
  private DetectorNode detectorNode = null;
  private int detectorNodeInstrumentIndex = -1;
  private int detectorNodeDetectorIndex = -1;
  private DetectorNode getDetectorNode(int instrumentIndex, int detectorIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (detectorNodeInstrumentIndex != instrumentIndex) match = false;
    if (detectorNodeDetectorIndex != detectorIndex) match = false;
    if (match) return detectorNode;
    detectorNode = null;
    detectorNodeInstrumentIndex = instrumentIndex;
    detectorNodeDetectorIndex = detectorIndex;

    // get Instrument+ node
    InstrumentNode instrument = getInstrumentNode(instrumentIndex, create);
    if (instrument == null) return null;
    // get Detector+ node
    int count = instrument.getDetectorCount();
    if (!create && count <= detectorIndex) return null;
    for (int i=count; i<=detectorIndex; i++) new DetectorNode(instrument);
    List list = instrument.getDetectorList();
    detectorNode = (DetectorNode) list.get(detectorIndex);
    return detectorNode;
  }

  // Instrument+/LightSource+
  private LightSourceNode lightSourceNode = null;
  private int lightSourceNodeInstrumentIndex = -1;
  private int lightSourceNodeLightSourceIndex = -1;
  private LightSourceNode getLightSourceNode(int instrumentIndex, int lightSourceIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (lightSourceNodeInstrumentIndex != instrumentIndex) match = false;
    if (lightSourceNodeLightSourceIndex != lightSourceIndex) match = false;
    if (match) return lightSourceNode;
    lightSourceNode = null;
    lightSourceNodeInstrumentIndex = instrumentIndex;
    lightSourceNodeLightSourceIndex = lightSourceIndex;

    // get Instrument+ node
    InstrumentNode instrument = getInstrumentNode(instrumentIndex, create);
    if (instrument == null) return null;
    // get LightSource+ node
    int count = instrument.getLightSourceCount();
    if (!create && count <= lightSourceIndex) return null;
    for (int i=count; i<=lightSourceIndex; i++) new LightSourceNode(instrument);
    List list = instrument.getLightSourceList();
    lightSourceNode = (LightSourceNode) list.get(lightSourceIndex);
    return lightSourceNode;
  }

  // Instrument+/LightSource+/Arc
  private ArcNode arcNode = null;
  private int arcNodeInstrumentIndex = -1;
  private int arcNodeLightSourceIndex = -1;
  private ArcNode getArcNode(int instrumentIndex, int lightSourceIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (arcNodeInstrumentIndex != instrumentIndex) match = false;
    if (arcNodeLightSourceIndex != lightSourceIndex) match = false;
    if (match) return arcNode;
    arcNode = null;
    arcNodeInstrumentIndex = instrumentIndex;
    arcNodeLightSourceIndex = lightSourceIndex;

    // get Instrument+/LightSource+ node
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, create);
    if (lightSource == null) return null;
    // get Arc node
    ArcNode arc = lightSource.getArc();
    if (arc == null) {
      if (create) arc = new ArcNode(lightSource);
      else return null;
    }
    arcNode = arc;
    return arcNode;
  }

  // Instrument+/LightSource+/Filament
  private FilamentNode filamentNode = null;
  private int filamentNodeInstrumentIndex = -1;
  private int filamentNodeLightSourceIndex = -1;
  private FilamentNode getFilamentNode(int instrumentIndex, int lightSourceIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (filamentNodeInstrumentIndex != instrumentIndex) match = false;
    if (filamentNodeLightSourceIndex != lightSourceIndex) match = false;
    if (match) return filamentNode;
    filamentNode = null;
    filamentNodeInstrumentIndex = instrumentIndex;
    filamentNodeLightSourceIndex = lightSourceIndex;

    // get Instrument+/LightSource+ node
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, create);
    if (lightSource == null) return null;
    // get Filament node
    FilamentNode filament = lightSource.getFilament();
    if (filament == null) {
      if (create) filament = new FilamentNode(lightSource);
      else return null;
    }
    filamentNode = filament;
    return filamentNode;
  }

  // Instrument+/LightSource+/Laser
  private LaserNode laserNode = null;
  private int laserNodeInstrumentIndex = -1;
  private int laserNodeLightSourceIndex = -1;
  private LaserNode getLaserNode(int instrumentIndex, int lightSourceIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (laserNodeInstrumentIndex != instrumentIndex) match = false;
    if (laserNodeLightSourceIndex != lightSourceIndex) match = false;
    if (match) return laserNode;
    laserNode = null;
    laserNodeInstrumentIndex = instrumentIndex;
    laserNodeLightSourceIndex = lightSourceIndex;

    // get Instrument+/LightSource+ node
    LightSourceNode lightSource = getLightSourceNode(instrumentIndex, lightSourceIndex, create);
    if (lightSource == null) return null;
    // get Laser node
    LaserNode laser = lightSource.getLaser();
    if (laser == null) {
      if (create) laser = new LaserNode(lightSource);
      else return null;
    }
    laserNode = laser;
    return laserNode;
  }

  // Instrument+/OTF+
  private OTFNode otfNode = null;
  private int otfNodeInstrumentIndex = -1;
  private int otfNodeOTFIndex = -1;
  private OTFNode getOTFNode(int instrumentIndex, int otfIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (otfNodeInstrumentIndex != instrumentIndex) match = false;
    if (otfNodeOTFIndex != otfIndex) match = false;
    if (match) return otfNode;
    otfNode = null;
    otfNodeInstrumentIndex = instrumentIndex;
    otfNodeOTFIndex = otfIndex;

    // get Instrument+ node
    InstrumentNode instrument = getInstrumentNode(instrumentIndex, create);
    if (instrument == null) return null;
    // get OTF+ node
    int count = instrument.getOTFCount();
    if (!create && count <= otfIndex) return null;
    for (int i=count; i<=otfIndex; i++) new OTFNode(instrument);
    List list = instrument.getOTFList();
    otfNode = (OTFNode) list.get(otfIndex);
    return otfNode;
  }

  // Instrument+/OTF+/ObjectiveRef
  private ObjectiveRefNode objectiveRefNode = null;
  private int objectiveRefNodeInstrumentIndex = -1;
  private int objectiveRefNodeOTFIndex = -1;
  private ObjectiveRefNode getObjectiveRefNode(int instrumentIndex, int otfIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (objectiveRefNodeInstrumentIndex != instrumentIndex) match = false;
    if (objectiveRefNodeOTFIndex != otfIndex) match = false;
    if (match) return objectiveRefNode;
    objectiveRefNode = null;
    objectiveRefNodeInstrumentIndex = instrumentIndex;
    objectiveRefNodeOTFIndex = otfIndex;

    // get Instrument+/OTF+ node
    OTFNode otf = getOTFNode(instrumentIndex, otfIndex, create);
    if (otf == null) return null;
    // get ObjectiveRef node
    ObjectiveRefNode objectiveRef = otf.getObjectiveRef();
    if (objectiveRef == null) {
      if (create) objectiveRef = new ObjectiveRefNode(otf);
      else return null;
    }
    objectiveRefNode = objectiveRef;
    return objectiveRefNode;
  }

  // Instrument+/Objective+
  private ObjectiveNode objectiveNode = null;
  private int objectiveNodeInstrumentIndex = -1;
  private int objectiveNodeObjectiveIndex = -1;
  private ObjectiveNode getObjectiveNode(int instrumentIndex, int objectiveIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (objectiveNodeInstrumentIndex != instrumentIndex) match = false;
    if (objectiveNodeObjectiveIndex != objectiveIndex) match = false;
    if (match) return objectiveNode;
    objectiveNode = null;
    objectiveNodeInstrumentIndex = instrumentIndex;
    objectiveNodeObjectiveIndex = objectiveIndex;

    // get Instrument+ node
    InstrumentNode instrument = getInstrumentNode(instrumentIndex, create);
    if (instrument == null) return null;
    // get Objective+ node
    int count = instrument.getObjectiveCount();
    if (!create && count <= objectiveIndex) return null;
    for (int i=count; i<=objectiveIndex; i++) new ObjectiveNode(instrument);
    List list = instrument.getObjectiveList();
    objectiveNode = (ObjectiveNode) list.get(objectiveIndex);
    return objectiveNode;
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
  private WellNode wellNode = null;
  private int wellNodePlateIndex = -1;
  private int wellNodeWellIndex = -1;
  private WellNode getWellNode(int plateIndex, int wellIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (wellNodePlateIndex != plateIndex) match = false;
    if (wellNodeWellIndex != wellIndex) match = false;
    if (match) return wellNode;
    wellNode = null;
    wellNodePlateIndex = plateIndex;
    wellNodeWellIndex = wellIndex;

    // get Plate+ node
    PlateNode plate = getPlateNode(plateIndex, create);
    if (plate == null) return null;
    // get Well+ node
    int count = plate.getWellCount();
    if (!create && count <= wellIndex) return null;
    for (int i=count; i<=wellIndex; i++) new WellNode(plate);
    List list = plate.getWellList();
    wellNode = (WellNode) list.get(wellIndex);
    return wellNode;
  }

  // Plate+/Well+/WellSample+
  private WellSampleNode wellSampleNode = null;
  private int wellSampleNodePlateIndex = -1;
  private int wellSampleNodeWellIndex = -1;
  private int wellSampleNodeWellSampleIndex = -1;
  private WellSampleNode getWellSampleNode(int plateIndex, int wellIndex, int wellSampleIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (wellSampleNodePlateIndex != plateIndex) match = false;
    if (wellSampleNodeWellIndex != wellIndex) match = false;
    if (wellSampleNodeWellSampleIndex != wellSampleIndex) match = false;
    if (match) return wellSampleNode;
    wellSampleNode = null;
    wellSampleNodePlateIndex = plateIndex;
    wellSampleNodeWellIndex = wellIndex;
    wellSampleNodeWellSampleIndex = wellSampleIndex;

    // get Plate+/Well+ node
    WellNode well = getWellNode(plateIndex, wellIndex, create);
    if (well == null) return null;
    // get WellSample+ node
    int count = well.getWellSampleCount();
    if (!create && count <= wellSampleIndex) return null;
    for (int i=count; i<=wellSampleIndex; i++) new WellSampleNode(well);
    List list = well.getWellSampleList();
    wellSampleNode = (WellSampleNode) list.get(wellSampleIndex);
    return wellSampleNode;
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
  private PlateRefNode plateRefNode = null;
  private int plateRefNodeScreenIndex = -1;
  private int plateRefNodePlateRefIndex = -1;
  private PlateRefNode getPlateRefNode(int screenIndex, int plateRefIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (plateRefNodeScreenIndex != screenIndex) match = false;
    if (plateRefNodePlateRefIndex != plateRefIndex) match = false;
    if (match) return plateRefNode;
    plateRefNode = null;
    plateRefNodeScreenIndex = screenIndex;
    plateRefNodePlateRefIndex = plateRefIndex;

    // get Screen+ node
    ScreenNode screen = getScreenNode(screenIndex, create);
    if (screen == null) return null;
    // get PlateRef+ node
    int count = screen.getPlateRefCount();
    if (!create && count <= plateRefIndex) return null;
    for (int i=count; i<=plateRefIndex; i++) new PlateRefNode(screen);
    List list = screen.getPlateRefList();
    plateRefNode = (PlateRefNode) list.get(plateRefIndex);
    return plateRefNode;
  }

  // Screen+/Reagent+
  private ReagentNode reagentNode = null;
  private int reagentNodeScreenIndex = -1;
  private int reagentNodeReagentIndex = -1;
  private ReagentNode getReagentNode(int screenIndex, int reagentIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (reagentNodeScreenIndex != screenIndex) match = false;
    if (reagentNodeReagentIndex != reagentIndex) match = false;
    if (match) return reagentNode;
    reagentNode = null;
    reagentNodeScreenIndex = screenIndex;
    reagentNodeReagentIndex = reagentIndex;

    // get Screen+ node
    ScreenNode screen = getScreenNode(screenIndex, create);
    if (screen == null) return null;
    // get Reagent+ node
    int count = screen.getReagentCount();
    if (!create && count <= reagentIndex) return null;
    for (int i=count; i<=reagentIndex; i++) new ReagentNode(screen);
    List list = screen.getReagentList();
    reagentNode = (ReagentNode) list.get(reagentIndex);
    return reagentNode;
  }

  // Screen+/ScreenAcquisition+
  private ScreenAcquisitionNode screenAcquisitionNode = null;
  private int screenAcquisitionNodeScreenIndex = -1;
  private int screenAcquisitionNodeScreenAcquisitionIndex = -1;
  private ScreenAcquisitionNode getScreenAcquisitionNode(int screenIndex, int screenAcquisitionIndex, boolean create) {
    // check whether indices match last request (i.e., node is cached)
    boolean match = true;
    if (screenAcquisitionNodeScreenIndex != screenIndex) match = false;
    if (screenAcquisitionNodeScreenAcquisitionIndex != screenAcquisitionIndex) match = false;
    if (match) return screenAcquisitionNode;
    screenAcquisitionNode = null;
    screenAcquisitionNodeScreenIndex = screenIndex;
    screenAcquisitionNodeScreenAcquisitionIndex = screenAcquisitionIndex;

    // get Screen+ node
    ScreenNode screen = getScreenNode(screenIndex, create);
    if (screen == null) return null;
    // get ScreenAcquisition+ node
    int count = screen.getScreenAcquisitionCount();
    if (!create && count <= screenAcquisitionIndex) return null;
    for (int i=count; i<=screenAcquisitionIndex; i++) new ScreenAcquisitionNode(screen);
    List list = screen.getScreenAcquisitionList();
    screenAcquisitionNode = (ScreenAcquisitionNode) list.get(screenAcquisitionIndex);
    return screenAcquisitionNode;
  }

}
