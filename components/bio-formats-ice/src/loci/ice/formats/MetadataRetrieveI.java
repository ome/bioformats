//
// MetadataRetrieveI.java
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
 * Created by curtis via MetadataAutogen on Oct 24, 2008 8:16:09 PM CDT
 *
 *-----------------------------------------------------------------------------
 */

package loci.ice.formats;

//import loci.ice.formats._MetadataRetrieveDisp;
//import loci.formats.*;
//import loci.formats.meta.*;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;

/**
 * Server-side Ice wrapper for client/server
 * {@link loci.formats.meta.MetadataRetrieve} objects.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats-ice/src/loci/ice/formats/MetadataRetrieveI.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats-ice/src/loci/ice/formats/MetadataRetrieveI.java">SVN</a></dd></dl>
 *
 * @author Hidayath Ansari mansari at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class MetadataRetrieveI extends _MetadataRetrieveDisp {

  // -- Fields --

  private IMetadata metadataObject;

  // -- Constructor --

  public MetadataRetrieveI() {
    metadataObject = MetadataTools.createOMEXMLMetadata();
  }

  // -- MetadataRetrieveI methods --

  public loci.formats.meta.MetadataRetrieve getWrappedObject() {
    return metadataObject;
  }

  // -- MetadataRetrieve methods --

  public MetadataRetrieveI getServant(Ice.Current current) {
    return this;
  }

  public String getOMEXML(Ice.Current current) {
    return MetadataTools.getOMEXML(metadataObject);
  }

  // - Entity counting -

  public int getChannelComponentCount(int imageIndex, int logicalChannelIndex, Ice.Current current) {
    return metadataObject.getChannelComponentCount(imageIndex, logicalChannelIndex);
  }

  public int getDetectorCount(int instrumentIndex, Ice.Current current) {
    return metadataObject.getDetectorCount(instrumentIndex);
  }

  public int getExperimentCount(Ice.Current current) {
    return metadataObject.getExperimentCount();
  }

  public int getExperimenterCount(Ice.Current current) {
    return metadataObject.getExperimenterCount();
  }

  public int getExperimenterMembershipCount(int experimenterIndex, Ice.Current current) {
    return metadataObject.getExperimenterMembershipCount(experimenterIndex);
  }

  public int getGroupRefCount(int experimenterIndex, Ice.Current current) {
    return metadataObject.getGroupRefCount(experimenterIndex);
  }

  public int getImageCount(Ice.Current current) {
    return metadataObject.getImageCount();
  }

  public int getInstrumentCount(Ice.Current current) {
    return metadataObject.getInstrumentCount();
  }

  public int getLightSourceCount(int instrumentIndex, Ice.Current current) {
    return metadataObject.getLightSourceCount(instrumentIndex);
  }

  public int getLogicalChannelCount(int imageIndex, Ice.Current current) {
    return metadataObject.getLogicalChannelCount(imageIndex);
  }

  public int getOTFCount(int instrumentIndex, Ice.Current current) {
    return metadataObject.getOTFCount(instrumentIndex);
  }

  public int getObjectiveCount(int instrumentIndex, Ice.Current current) {
    return metadataObject.getObjectiveCount(instrumentIndex);
  }

  public int getPixelsCount(int imageIndex, Ice.Current current) {
    return metadataObject.getPixelsCount(imageIndex);
  }

  public int getPlaneCount(int imageIndex, int pixelsIndex, Ice.Current current) {
    return metadataObject.getPlaneCount(imageIndex, pixelsIndex);
  }

  public int getPlateCount(Ice.Current current) {
    return metadataObject.getPlateCount();
  }

  public int getPlateRefCount(int screenIndex, Ice.Current current) {
    return metadataObject.getPlateRefCount(screenIndex);
  }

  public int getROICount(int imageIndex, Ice.Current current) {
    return metadataObject.getROICount(imageIndex);
  }

  public int getReagentCount(int screenIndex, Ice.Current current) {
    return metadataObject.getReagentCount(screenIndex);
  }

  public int getScreenCount(Ice.Current current) {
    return metadataObject.getScreenCount();
  }

  public int getScreenAcquisitionCount(int screenIndex, Ice.Current current) {
    return metadataObject.getScreenAcquisitionCount(screenIndex);
  }

  public int getTiffDataCount(int imageIndex, int pixelsIndex, Ice.Current current) {
    return metadataObject.getTiffDataCount(imageIndex, pixelsIndex);
  }

  public int getWellCount(int plateIndex, Ice.Current current) {
    return metadataObject.getWellCount(plateIndex);
  }

  public int getWellSampleCount(int plateIndex, int wellIndex, Ice.Current current) {
    return metadataObject.getWellSampleCount(plateIndex, wellIndex);
  }

  // - Entity retrieval -

  public String getUUID(Ice.Current current) {
    return metadataObject.getUUID();
  }

  // - Arc property retrieval -

  public String getArcType(int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    return metadataObject.getArcType(instrumentIndex, lightSourceIndex);
  }

  // - ChannelComponent property retrieval -

  public String getChannelComponentColorDomain(int imageIndex, int logicalChannelIndex, int channelComponentIndex, Ice.Current current) {
    return metadataObject.getChannelComponentColorDomain(imageIndex, logicalChannelIndex, channelComponentIndex);
  }

  public int getChannelComponentIndex(int imageIndex, int logicalChannelIndex, int channelComponentIndex, Ice.Current current) {
    return metadataObject.getChannelComponentIndex(imageIndex, logicalChannelIndex, channelComponentIndex);
  }

  // - Detector property retrieval -

  public float getDetectorGain(int instrumentIndex, int detectorIndex, Ice.Current current) {
    return metadataObject.getDetectorGain(instrumentIndex, detectorIndex);
  }

  public String getDetectorID(int instrumentIndex, int detectorIndex, Ice.Current current) {
    return metadataObject.getDetectorID(instrumentIndex, detectorIndex);
  }

  public String getDetectorManufacturer(int instrumentIndex, int detectorIndex, Ice.Current current) {
    return metadataObject.getDetectorManufacturer(instrumentIndex, detectorIndex);
  }

  public String getDetectorModel(int instrumentIndex, int detectorIndex, Ice.Current current) {
    return metadataObject.getDetectorModel(instrumentIndex, detectorIndex);
  }

  public float getDetectorOffset(int instrumentIndex, int detectorIndex, Ice.Current current) {
    return metadataObject.getDetectorOffset(instrumentIndex, detectorIndex);
  }

  public String getDetectorSerialNumber(int instrumentIndex, int detectorIndex, Ice.Current current) {
    return metadataObject.getDetectorSerialNumber(instrumentIndex, detectorIndex);
  }

  public String getDetectorType(int instrumentIndex, int detectorIndex, Ice.Current current) {
    return metadataObject.getDetectorType(instrumentIndex, detectorIndex);
  }

  public float getDetectorVoltage(int instrumentIndex, int detectorIndex, Ice.Current current) {
    return metadataObject.getDetectorVoltage(instrumentIndex, detectorIndex);
  }

  // - DetectorSettings property retrieval -

  public String getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex, Ice.Current current) {
    return metadataObject.getDetectorSettingsDetector(imageIndex, logicalChannelIndex);
  }

  public float getDetectorSettingsGain(int imageIndex, int logicalChannelIndex, Ice.Current current) {
    return metadataObject.getDetectorSettingsGain(imageIndex, logicalChannelIndex);
  }

  public float getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex, Ice.Current current) {
    return metadataObject.getDetectorSettingsOffset(imageIndex, logicalChannelIndex);
  }

  // - Dimensions property retrieval -

  public float getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex, Ice.Current current) {
    return metadataObject.getDimensionsPhysicalSizeX(imageIndex, pixelsIndex);
  }

  public float getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex, Ice.Current current) {
    return metadataObject.getDimensionsPhysicalSizeY(imageIndex, pixelsIndex);
  }

  public float getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex, Ice.Current current) {
    return metadataObject.getDimensionsPhysicalSizeZ(imageIndex, pixelsIndex);
  }

  public float getDimensionsTimeIncrement(int imageIndex, int pixelsIndex, Ice.Current current) {
    return metadataObject.getDimensionsTimeIncrement(imageIndex, pixelsIndex);
  }

  public int getDimensionsWaveIncrement(int imageIndex, int pixelsIndex, Ice.Current current) {
    return metadataObject.getDimensionsWaveIncrement(imageIndex, pixelsIndex);
  }

  public int getDimensionsWaveStart(int imageIndex, int pixelsIndex, Ice.Current current) {
    return metadataObject.getDimensionsWaveStart(imageIndex, pixelsIndex);
  }

  // - DisplayOptions property retrieval -

  public String getDisplayOptionsID(int imageIndex, Ice.Current current) {
    return metadataObject.getDisplayOptionsID(imageIndex);
  }

  public float getDisplayOptionsZoom(int imageIndex, Ice.Current current) {
    return metadataObject.getDisplayOptionsZoom(imageIndex);
  }

  // - DisplayOptionsProjection property retrieval -

  public int getDisplayOptionsProjectionZStart(int imageIndex, Ice.Current current) {
    return metadataObject.getDisplayOptionsProjectionZStart(imageIndex);
  }

  public int getDisplayOptionsProjectionZStop(int imageIndex, Ice.Current current) {
    return metadataObject.getDisplayOptionsProjectionZStop(imageIndex);
  }

  // - DisplayOptionsTime property retrieval -

  public int getDisplayOptionsTimeTStart(int imageIndex, Ice.Current current) {
    return metadataObject.getDisplayOptionsTimeTStart(imageIndex);
  }

  public int getDisplayOptionsTimeTStop(int imageIndex, Ice.Current current) {
    return metadataObject.getDisplayOptionsTimeTStop(imageIndex);
  }

  // - Experiment property retrieval -

  public String getExperimentDescription(int experimentIndex, Ice.Current current) {
    return metadataObject.getExperimentDescription(experimentIndex);
  }

  public String getExperimentID(int experimentIndex, Ice.Current current) {
    return metadataObject.getExperimentID(experimentIndex);
  }

  public String getExperimentType(int experimentIndex, Ice.Current current) {
    return metadataObject.getExperimentType(experimentIndex);
  }

  // - Experimenter property retrieval -

  public String getExperimenterEmail(int experimenterIndex, Ice.Current current) {
    return metadataObject.getExperimenterEmail(experimenterIndex);
  }

  public String getExperimenterFirstName(int experimenterIndex, Ice.Current current) {
    return metadataObject.getExperimenterFirstName(experimenterIndex);
  }

  public String getExperimenterID(int experimenterIndex, Ice.Current current) {
    return metadataObject.getExperimenterID(experimenterIndex);
  }

  public String getExperimenterInstitution(int experimenterIndex, Ice.Current current) {
    return metadataObject.getExperimenterInstitution(experimenterIndex);
  }

  public String getExperimenterLastName(int experimenterIndex, Ice.Current current) {
    return metadataObject.getExperimenterLastName(experimenterIndex);
  }

  // - ExperimenterMembership property retrieval -

  public String getExperimenterMembershipGroup(int experimenterIndex, int groupRefIndex, Ice.Current current) {
    return metadataObject.getExperimenterMembershipGroup(experimenterIndex, groupRefIndex);
  }

  // - Filament property retrieval -

  public String getFilamentType(int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    return metadataObject.getFilamentType(instrumentIndex, lightSourceIndex);
  }

  // - GroupRef property retrieval -

  // - Image property retrieval -

  public String getImageCreationDate(int imageIndex, Ice.Current current) {
    return metadataObject.getImageCreationDate(imageIndex);
  }

  public String getImageDefaultPixels(int imageIndex, Ice.Current current) {
    return metadataObject.getImageDefaultPixels(imageIndex);
  }

  public String getImageDescription(int imageIndex, Ice.Current current) {
    return metadataObject.getImageDescription(imageIndex);
  }

  public String getImageID(int imageIndex, Ice.Current current) {
    return metadataObject.getImageID(imageIndex);
  }

  public String getImageInstrumentRef(int imageIndex, Ice.Current current) {
    return metadataObject.getImageInstrumentRef(imageIndex);
  }

  public String getImageName(int imageIndex, Ice.Current current) {
    return metadataObject.getImageName(imageIndex);
  }

  // - ImagingEnvironment property retrieval -

  public float getImagingEnvironmentAirPressure(int imageIndex, Ice.Current current) {
    return metadataObject.getImagingEnvironmentAirPressure(imageIndex);
  }

  public float getImagingEnvironmentCO2Percent(int imageIndex, Ice.Current current) {
    return metadataObject.getImagingEnvironmentCO2Percent(imageIndex);
  }

  public float getImagingEnvironmentHumidity(int imageIndex, Ice.Current current) {
    return metadataObject.getImagingEnvironmentHumidity(imageIndex);
  }

  public float getImagingEnvironmentTemperature(int imageIndex, Ice.Current current) {
    return metadataObject.getImagingEnvironmentTemperature(imageIndex);
  }

  // - Instrument property retrieval -

  public String getInstrumentID(int instrumentIndex, Ice.Current current) {
    return metadataObject.getInstrumentID(instrumentIndex);
  }

  // - Laser property retrieval -

  public int getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    return metadataObject.getLaserFrequencyMultiplication(instrumentIndex, lightSourceIndex);
  }

  public String getLaserLaserMedium(int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    return metadataObject.getLaserLaserMedium(instrumentIndex, lightSourceIndex);
  }

  public String getLaserPulse(int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    return metadataObject.getLaserPulse(instrumentIndex, lightSourceIndex);
  }

  public boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    return metadataObject.getLaserTuneable(instrumentIndex, lightSourceIndex);
  }

  public String getLaserType(int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    return metadataObject.getLaserType(instrumentIndex, lightSourceIndex);
  }

  public int getLaserWavelength(int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    return metadataObject.getLaserWavelength(instrumentIndex, lightSourceIndex);
  }

  // - LightSource property retrieval -

  public String getLightSourceID(int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    return metadataObject.getLightSourceID(instrumentIndex, lightSourceIndex);
  }

  public String getLightSourceManufacturer(int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    return metadataObject.getLightSourceManufacturer(instrumentIndex, lightSourceIndex);
  }

  public String getLightSourceModel(int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    return metadataObject.getLightSourceModel(instrumentIndex, lightSourceIndex);
  }

  public float getLightSourcePower(int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    return metadataObject.getLightSourcePower(instrumentIndex, lightSourceIndex);
  }

  public String getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex, Ice.Current current) {
    return metadataObject.getLightSourceSerialNumber(instrumentIndex, lightSourceIndex);
  }

  // - LightSourceSettings property retrieval -

  public float getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex, Ice.Current current) {
    return metadataObject.getLightSourceSettingsAttenuation(imageIndex, logicalChannelIndex);
  }

  public String getLightSourceSettingsLightSource(int imageIndex, int logicalChannelIndex, Ice.Current current) {
    return metadataObject.getLightSourceSettingsLightSource(imageIndex, logicalChannelIndex);
  }

  public int getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex, Ice.Current current) {
    return metadataObject.getLightSourceSettingsWavelength(imageIndex, logicalChannelIndex);
  }

  // - LogicalChannel property retrieval -

  public String getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex, Ice.Current current) {
    return metadataObject.getLogicalChannelContrastMethod(imageIndex, logicalChannelIndex);
  }

  public int getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex, Ice.Current current) {
    return metadataObject.getLogicalChannelEmWave(imageIndex, logicalChannelIndex);
  }

  public int getLogicalChannelExWave(int imageIndex, int logicalChannelIndex, Ice.Current current) {
    return metadataObject.getLogicalChannelExWave(imageIndex, logicalChannelIndex);
  }

  public String getLogicalChannelFluor(int imageIndex, int logicalChannelIndex, Ice.Current current) {
    return metadataObject.getLogicalChannelFluor(imageIndex, logicalChannelIndex);
  }

  public String getLogicalChannelID(int imageIndex, int logicalChannelIndex, Ice.Current current) {
    return metadataObject.getLogicalChannelID(imageIndex, logicalChannelIndex);
  }

  public String getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex, Ice.Current current) {
    return metadataObject.getLogicalChannelIlluminationType(imageIndex, logicalChannelIndex);
  }

  public String getLogicalChannelMode(int imageIndex, int logicalChannelIndex, Ice.Current current) {
    return metadataObject.getLogicalChannelMode(imageIndex, logicalChannelIndex);
  }

  public String getLogicalChannelName(int imageIndex, int logicalChannelIndex, Ice.Current current) {
    return metadataObject.getLogicalChannelName(imageIndex, logicalChannelIndex);
  }

  public float getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex, Ice.Current current) {
    return metadataObject.getLogicalChannelNdFilter(imageIndex, logicalChannelIndex);
  }

  public String getLogicalChannelOTF(int imageIndex, int logicalChannelIndex, Ice.Current current) {
    return metadataObject.getLogicalChannelOTF(imageIndex, logicalChannelIndex);
  }

  public String getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex, Ice.Current current) {
    return metadataObject.getLogicalChannelPhotometricInterpretation(imageIndex, logicalChannelIndex);
  }

  public float getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex, Ice.Current current) {
    return metadataObject.getLogicalChannelPinholeSize(imageIndex, logicalChannelIndex);
  }

  public int getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex, Ice.Current current) {
    return metadataObject.getLogicalChannelPockelCellSetting(imageIndex, logicalChannelIndex);
  }

  public int getLogicalChannelSamplesPerPixel(int imageIndex, int logicalChannelIndex, Ice.Current current) {
    return metadataObject.getLogicalChannelSamplesPerPixel(imageIndex, logicalChannelIndex);
  }

  // - OTF property retrieval -

  public String getOTFID(int instrumentIndex, int otfIndex, Ice.Current current) {
    return metadataObject.getOTFID(instrumentIndex, otfIndex);
  }

  public String getOTFObjective(int instrumentIndex, int otfIndex, Ice.Current current) {
    return metadataObject.getOTFObjective(instrumentIndex, otfIndex);
  }

  public boolean getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex, Ice.Current current) {
    return metadataObject.getOTFOpticalAxisAveraged(instrumentIndex, otfIndex);
  }

  public String getOTFPixelType(int instrumentIndex, int otfIndex, Ice.Current current) {
    return metadataObject.getOTFPixelType(instrumentIndex, otfIndex);
  }

  public int getOTFSizeX(int instrumentIndex, int otfIndex, Ice.Current current) {
    return metadataObject.getOTFSizeX(instrumentIndex, otfIndex);
  }

  public int getOTFSizeY(int instrumentIndex, int otfIndex, Ice.Current current) {
    return metadataObject.getOTFSizeY(instrumentIndex, otfIndex);
  }

  // - Objective property retrieval -

  public float getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex, Ice.Current current) {
    return metadataObject.getObjectiveCalibratedMagnification(instrumentIndex, objectiveIndex);
  }

  public String getObjectiveCorrection(int instrumentIndex, int objectiveIndex, Ice.Current current) {
    return metadataObject.getObjectiveCorrection(instrumentIndex, objectiveIndex);
  }

  public String getObjectiveID(int instrumentIndex, int objectiveIndex, Ice.Current current) {
    return metadataObject.getObjectiveID(instrumentIndex, objectiveIndex);
  }

  public String getObjectiveImmersion(int instrumentIndex, int objectiveIndex, Ice.Current current) {
    return metadataObject.getObjectiveImmersion(instrumentIndex, objectiveIndex);
  }

  public float getObjectiveLensNA(int instrumentIndex, int objectiveIndex, Ice.Current current) {
    return metadataObject.getObjectiveLensNA(instrumentIndex, objectiveIndex);
  }

  public String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex, Ice.Current current) {
    return metadataObject.getObjectiveManufacturer(instrumentIndex, objectiveIndex);
  }

  public String getObjectiveModel(int instrumentIndex, int objectiveIndex, Ice.Current current) {
    return metadataObject.getObjectiveModel(instrumentIndex, objectiveIndex);
  }

  public int getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex, Ice.Current current) {
    return metadataObject.getObjectiveNominalMagnification(instrumentIndex, objectiveIndex);
  }

  public String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex, Ice.Current current) {
    return metadataObject.getObjectiveSerialNumber(instrumentIndex, objectiveIndex);
  }

  public float getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex, Ice.Current current) {
    return metadataObject.getObjectiveWorkingDistance(instrumentIndex, objectiveIndex);
  }

  // - Pixels property retrieval -

  public boolean getPixelsBigEndian(int imageIndex, int pixelsIndex, Ice.Current current) {
    return metadataObject.getPixelsBigEndian(imageIndex, pixelsIndex);
  }

  public String getPixelsDimensionOrder(int imageIndex, int pixelsIndex, Ice.Current current) {
    return metadataObject.getPixelsDimensionOrder(imageIndex, pixelsIndex);
  }

  public String getPixelsID(int imageIndex, int pixelsIndex, Ice.Current current) {
    return metadataObject.getPixelsID(imageIndex, pixelsIndex);
  }

  public String getPixelsPixelType(int imageIndex, int pixelsIndex, Ice.Current current) {
    return metadataObject.getPixelsPixelType(imageIndex, pixelsIndex);
  }

  public int getPixelsSizeC(int imageIndex, int pixelsIndex, Ice.Current current) {
    return metadataObject.getPixelsSizeC(imageIndex, pixelsIndex);
  }

  public int getPixelsSizeT(int imageIndex, int pixelsIndex, Ice.Current current) {
    return metadataObject.getPixelsSizeT(imageIndex, pixelsIndex);
  }

  public int getPixelsSizeX(int imageIndex, int pixelsIndex, Ice.Current current) {
    return metadataObject.getPixelsSizeX(imageIndex, pixelsIndex);
  }

  public int getPixelsSizeY(int imageIndex, int pixelsIndex, Ice.Current current) {
    return metadataObject.getPixelsSizeY(imageIndex, pixelsIndex);
  }

  public int getPixelsSizeZ(int imageIndex, int pixelsIndex, Ice.Current current) {
    return metadataObject.getPixelsSizeZ(imageIndex, pixelsIndex);
  }

  // - Plane property retrieval -

  public int getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current current) {
    return metadataObject.getPlaneTheC(imageIndex, pixelsIndex, planeIndex);
  }

  public int getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current current) {
    return metadataObject.getPlaneTheT(imageIndex, pixelsIndex, planeIndex);
  }

  public int getPlaneTheZ(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current current) {
    return metadataObject.getPlaneTheZ(imageIndex, pixelsIndex, planeIndex);
  }

  // - PlaneTiming property retrieval -

  public float getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current current) {
    return metadataObject.getPlaneTimingDeltaT(imageIndex, pixelsIndex, planeIndex);
  }

  public float getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current current) {
    return metadataObject.getPlaneTimingExposureTime(imageIndex, pixelsIndex, planeIndex);
  }

  // - Plate property retrieval -

  public String getPlateDescription(int plateIndex, Ice.Current current) {
    return metadataObject.getPlateDescription(plateIndex);
  }

  public String getPlateExternalIdentifier(int plateIndex, Ice.Current current) {
    return metadataObject.getPlateExternalIdentifier(plateIndex);
  }

  public String getPlateID(int plateIndex, Ice.Current current) {
    return metadataObject.getPlateID(plateIndex);
  }

  public String getPlateName(int plateIndex, Ice.Current current) {
    return metadataObject.getPlateName(plateIndex);
  }

  public String getPlateStatus(int plateIndex, Ice.Current current) {
    return metadataObject.getPlateStatus(plateIndex);
  }

  // - PlateRef property retrieval -

  public String getPlateRefID(int screenIndex, int plateRefIndex, Ice.Current current) {
    return metadataObject.getPlateRefID(screenIndex, plateRefIndex);
  }

  // - ROI property retrieval -

  public String getROIID(int imageIndex, int roiIndex, Ice.Current current) {
    return metadataObject.getROIID(imageIndex, roiIndex);
  }

  public int getROIT0(int imageIndex, int roiIndex, Ice.Current current) {
    return metadataObject.getROIT0(imageIndex, roiIndex);
  }

  public int getROIT1(int imageIndex, int roiIndex, Ice.Current current) {
    return metadataObject.getROIT1(imageIndex, roiIndex);
  }

  public int getROIX0(int imageIndex, int roiIndex, Ice.Current current) {
    return metadataObject.getROIX0(imageIndex, roiIndex);
  }

  public int getROIX1(int imageIndex, int roiIndex, Ice.Current current) {
    return metadataObject.getROIX1(imageIndex, roiIndex);
  }

  public int getROIY0(int imageIndex, int roiIndex, Ice.Current current) {
    return metadataObject.getROIY0(imageIndex, roiIndex);
  }

  public int getROIY1(int imageIndex, int roiIndex, Ice.Current current) {
    return metadataObject.getROIY1(imageIndex, roiIndex);
  }

  public int getROIZ0(int imageIndex, int roiIndex, Ice.Current current) {
    return metadataObject.getROIZ0(imageIndex, roiIndex);
  }

  public int getROIZ1(int imageIndex, int roiIndex, Ice.Current current) {
    return metadataObject.getROIZ1(imageIndex, roiIndex);
  }

  // - Reagent property retrieval -

  public String getReagentDescription(int screenIndex, int reagentIndex, Ice.Current current) {
    return metadataObject.getReagentDescription(screenIndex, reagentIndex);
  }

  public String getReagentID(int screenIndex, int reagentIndex, Ice.Current current) {
    return metadataObject.getReagentID(screenIndex, reagentIndex);
  }

  public String getReagentName(int screenIndex, int reagentIndex, Ice.Current current) {
    return metadataObject.getReagentName(screenIndex, reagentIndex);
  }

  public String getReagentReagentIdentifier(int screenIndex, int reagentIndex, Ice.Current current) {
    return metadataObject.getReagentReagentIdentifier(screenIndex, reagentIndex);
  }

  // - Screen property retrieval -

  public String getScreenID(int screenIndex, Ice.Current current) {
    return metadataObject.getScreenID(screenIndex);
  }

  public String getScreenName(int screenIndex, Ice.Current current) {
    return metadataObject.getScreenName(screenIndex);
  }

  public String getScreenProtocolDescription(int screenIndex, Ice.Current current) {
    return metadataObject.getScreenProtocolDescription(screenIndex);
  }

  public String getScreenProtocolIdentifier(int screenIndex, Ice.Current current) {
    return metadataObject.getScreenProtocolIdentifier(screenIndex);
  }

  public String getScreenReagentSetDescription(int screenIndex, Ice.Current current) {
    return metadataObject.getScreenReagentSetDescription(screenIndex);
  }

  public String getScreenType(int screenIndex, Ice.Current current) {
    return metadataObject.getScreenType(screenIndex);
  }

  // - ScreenAcquisition property retrieval -

  public String getScreenAcquisitionEndTime(int screenIndex, int screenAcquisitionIndex, Ice.Current current) {
    return metadataObject.getScreenAcquisitionEndTime(screenIndex, screenAcquisitionIndex);
  }

  public String getScreenAcquisitionID(int screenIndex, int screenAcquisitionIndex, Ice.Current current) {
    return metadataObject.getScreenAcquisitionID(screenIndex, screenAcquisitionIndex);
  }

  public String getScreenAcquisitionStartTime(int screenIndex, int screenAcquisitionIndex, Ice.Current current) {
    return metadataObject.getScreenAcquisitionStartTime(screenIndex, screenAcquisitionIndex);
  }

  // - StageLabel property retrieval -

  public String getStageLabelName(int imageIndex, Ice.Current current) {
    return metadataObject.getStageLabelName(imageIndex);
  }

  public float getStageLabelX(int imageIndex, Ice.Current current) {
    return metadataObject.getStageLabelX(imageIndex);
  }

  public float getStageLabelY(int imageIndex, Ice.Current current) {
    return metadataObject.getStageLabelY(imageIndex);
  }

  public float getStageLabelZ(int imageIndex, Ice.Current current) {
    return metadataObject.getStageLabelZ(imageIndex);
  }

  // - StagePosition property retrieval -

  public float getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current current) {
    return metadataObject.getStagePositionPositionX(imageIndex, pixelsIndex, planeIndex);
  }

  public float getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current current) {
    return metadataObject.getStagePositionPositionY(imageIndex, pixelsIndex, planeIndex);
  }

  public float getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex, Ice.Current current) {
    return metadataObject.getStagePositionPositionZ(imageIndex, pixelsIndex, planeIndex);
  }

  // - TiffData property retrieval -

  public String getTiffDataFileName(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current current) {
    return metadataObject.getTiffDataFileName(imageIndex, pixelsIndex, tiffDataIndex);
  }

  public int getTiffDataFirstC(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current current) {
    return metadataObject.getTiffDataFirstC(imageIndex, pixelsIndex, tiffDataIndex);
  }

  public int getTiffDataFirstT(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current current) {
    return metadataObject.getTiffDataFirstT(imageIndex, pixelsIndex, tiffDataIndex);
  }

  public int getTiffDataFirstZ(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current current) {
    return metadataObject.getTiffDataFirstZ(imageIndex, pixelsIndex, tiffDataIndex);
  }

  public int getTiffDataIFD(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current current) {
    return metadataObject.getTiffDataIFD(imageIndex, pixelsIndex, tiffDataIndex);
  }

  public int getTiffDataNumPlanes(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current current) {
    return metadataObject.getTiffDataNumPlanes(imageIndex, pixelsIndex, tiffDataIndex);
  }

  public String getTiffDataUUID(int imageIndex, int pixelsIndex, int tiffDataIndex, Ice.Current current) {
    return metadataObject.getTiffDataUUID(imageIndex, pixelsIndex, tiffDataIndex);
  }

  // - Well property retrieval -

  public int getWellColumn(int plateIndex, int wellIndex, Ice.Current current) {
    return metadataObject.getWellColumn(plateIndex, wellIndex);
  }

  public String getWellExternalDescription(int plateIndex, int wellIndex, Ice.Current current) {
    return metadataObject.getWellExternalDescription(plateIndex, wellIndex);
  }

  public String getWellExternalIdentifier(int plateIndex, int wellIndex, Ice.Current current) {
    return metadataObject.getWellExternalIdentifier(plateIndex, wellIndex);
  }

  public String getWellID(int plateIndex, int wellIndex, Ice.Current current) {
    return metadataObject.getWellID(plateIndex, wellIndex);
  }

  public int getWellRow(int plateIndex, int wellIndex, Ice.Current current) {
    return metadataObject.getWellRow(plateIndex, wellIndex);
  }

  public String getWellType(int plateIndex, int wellIndex, Ice.Current current) {
    return metadataObject.getWellType(plateIndex, wellIndex);
  }

  // - WellSample property retrieval -

  public String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current current) {
    return metadataObject.getWellSampleID(plateIndex, wellIndex, wellSampleIndex);
  }

  public int getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current current) {
    return metadataObject.getWellSampleIndex(plateIndex, wellIndex, wellSampleIndex);
  }

  public float getWellSamplePosX(int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current current) {
    return metadataObject.getWellSamplePosX(plateIndex, wellIndex, wellSampleIndex);
  }

  public float getWellSamplePosY(int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current current) {
    return metadataObject.getWellSamplePosY(plateIndex, wellIndex, wellSampleIndex);
  }

  public int getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex, Ice.Current current) {
    return metadataObject.getWellSampleTimepoint(plateIndex, wellIndex, wellSampleIndex);
  }

}
