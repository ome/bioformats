//
// DummyMetadata.java
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

package loci.formats;

/**
 * A dummy implementation for {@link MetadataStore} and
 * {@link MetadataRetrieve} that is used when no other
 * metadata implementations are available.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/DummyMetadata.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/DummyMetadata.java">SVN</a></dd></dl>
 *
 * @author Chris Allan callan at blackcat.ca
 */
public class DummyMetadata implements MetadataStore, MetadataRetrieve {

  // -- MetadataRetrieve API methods --

  public int getImageCount() { return 0; }
  public String getImageName(Integer n) { return null; }
  public String getCreationDate(Integer n) { return null; }
  public String getDescription(Integer n) { return null; }

  public int getExperimenterCount() { return 0; }
  public String getFirstName(Integer n) { return null; }
  public String getLastName(Integer n) { return null; }
  public String getEmail(Integer n) { return null; }
  public String getInstitution(Integer n) { return null; }
  public String getDataDirectory(Integer n) { return null; }
  public Object getGroup(Integer n) { return null; }

  public int getGroupCount() { return 0; }
  public String getGroupName(Integer n) { return null; }
  public Object getLeader(Integer n) { return null; }
  public Object getContact(Integer n) { return null; }

  public int getInstrumentCount() { return 0; }
  public String getManufacturer(Integer n) { return null; }
  public String getModel(Integer n) { return null; }
  public String getSerialNumber(Integer n) { return null; }
  public String getType(Integer n) { return null; }

  public Float getPixelSizeX(Integer n) { return null; }
  public Float getPixelSizeY(Integer n) { return null; }
  public Float getPixelSizeZ(Integer n) { return null; }
  public Float getPixelSizeC(Integer n) { return null; }
  public Float getPixelSizeT(Integer n) { return null; }

  public int getDisplayROICount() { return 0; }
  public Integer getX0(Integer n) { return null; }
  public Integer getY0(Integer n) { return null; }
  public Integer getZ0(Integer n) { return null; }
  public Integer getT0(Integer n) { return null; }
  public Integer getX1(Integer n) { return null; }
  public Integer getY1(Integer n) { return null; }
  public Integer getZ1(Integer n) { return null; }
  public Integer getT1(Integer n) { return null; }
  public Object getDisplayOptions(Integer n) { return null; }

  public int getPixelsCount(Integer n) { return 0; }
  public Integer getSizeX(Integer image) { return null; }
  public Integer getSizeY(Integer image) { return null; }
  public Integer getSizeZ(Integer image) { return null; }
  public Integer getSizeC(Integer image) { return null; }
  public Integer getSizeT(Integer image) { return null; }
  public String getPixelType(Integer image) { return null; }
  public Boolean getBigEndian(Integer image) { return null; }
  public String getDimensionOrder(Integer image) { return null; }

  public int getStageLabelCount() { return 0; }
  public String getStageName(Integer n) { return null; }
  public Float getStageX(Integer n) { return null; }
  public Float getStageY(Integer n) { return null; }
  public Float getStageZ(Integer n) { return null; }

  public int getChannelCount(Integer n) { return 0; }
  public String getChannelName(Integer pixels, Integer channel) { return null; }
  public Float getChannelNDFilter(Integer pixels, Integer channel) {
    return null;
  }
  public Integer getEmWave(Integer pixels, Integer channel) { return null; }
  public Integer getExWave(Integer pixels, Integer channel) { return null; }
  public String getPhotometricInterpretation(Integer pixels, Integer channel) {
    return null;
  }
  public String getMode(Integer pixels, Integer channel) { return null; }
  public Double getGlobalMin(Integer pixels, Integer channel) { return null; }
  public Double getGlobalMax(Integer pixels, Integer channel) { return null; }

  public Float getTimestamp(Integer pixels, Integer z, Integer c, Integer t) {
    return null;
  }
  public Float getExposureTime(Integer pixels,
    Integer z, Integer c, Integer t)
  {
    return null;
  }

  public Float getTemperature(Integer n) { return null; }
  public Float getAirPressure(Integer n) { return null; }
  public Float getHumidity(Integer n) { return null; }
  public Float getCO2Percent(Integer n) { return null; }

  public Double getBlackLevel(Integer pixels, Integer channel) { return null; }
  public Double getWhiteLevel(Integer pixels, Integer channel) { return null; }
  public Float getGamma(Integer pixels, Integer channel) { return null; }

  public Float getZoom(Integer image) { return null; }
  public Boolean isRedChannelOn(Integer image) { return null; }
  public Boolean isGreenChannelOn(Integer image) { return null; }
  public Boolean isBlueChannelOn(Integer image) { return null; }
  public Boolean isDisplayRGB(Integer image) { return null; }
  public String getColorMap(Integer image) { return null; }
  public Integer getZStart(Integer image) { return null; }
  public Integer getZStop(Integer image) { return null; }
  public Integer getTStart(Integer image) { return null; }
  public Integer getTStop(Integer image) { return null; }

  public String getLightManufacturer(Integer light) { return null; }
  public String getLightModel(Integer light) { return null; }
  public String getLightSerial(Integer light) { return null; }

  public String getLaserType(Integer laser) { return null; }
  public String getLaserMedium(Integer laser) { return null; }
  public Integer getLaserWavelength(Integer laser) { return null; }
  public Boolean isFrequencyDoubled(Integer laser) { return null; }
  public Boolean isTunable(Integer laser) { return null; }
  public String getPulse(Integer laser) { return null; }
  public Float getPower(Integer laser) { return null; }

  public String getFilamentType(Integer filament) { return null; }
  public Float getFilamentPower(Integer filament) { return null; }

  public String getArcType(Integer arc) { return null; }
  public Float getArcPower(Integer arc) { return null; }

  public String getDetectorManufacturer(Integer detector) { return null; }
  public String getDetectorModel(Integer detector) { return null; }
  public String getDetectorSerial(Integer detector) { return null; }
  public String getDetectorType(Integer detector) { return null; }
  public Float getDetectorGain(Integer detector) { return null; }
  public Float getDetectorVoltage(Integer detector) { return null; }
  public Float getDetectorOffset(Integer detector) { return null; }

  public String getObjectiveManufacturer(Integer objective) { return null; }
  public String getObjectiveModel(Integer objective) { return null; }
  public String getObjectiveSerial(Integer objective) { return null; }
  public Float getLensNA(Integer objective) { return null; }
  public Float getObjectiveMagnification(Integer objective) { return null; }

  public String getExcitationManufacturer(Integer filter) { return null; }
  public String getExcitationModel(Integer filter) { return null; }
  public String getExcitationLotNumber(Integer filter) { return null; }
  public String getExcitationType(Integer filter) { return null; }

  public String getDichroicManufacturer(Integer dichroic) { return null; }
  public String getDichroicModel(Integer dichroic) { return null; }
  public String getDichroicLotNumber(Integer dichroic) { return null; }

  public String getEmissionManufacturer(Integer filter) { return null; }
  public String getEmissionModel(Integer filter) { return null; }
  public String getEmissionLotNumber(Integer filter) { return null; }
  public String getEmissionType(Integer filter) { return null; }

  public String getFilterSetManufacturer(Integer filterSet) { return null; }
  public String getFilterSetModel(Integer filterSet) { return null; }
  public String getFilterSetLotNumber(Integer filterSet) { return null; }

  public Integer getOTFSizeX(Integer otf) { return null; }
  public Integer getOTFSizeY(Integer otf) { return null; }
  public String getOTFPixelType(Integer otf) { return null; }
  public String getOTFPath(Integer otf) { return null; }
  public Boolean getOTFOpticalAxisAverage(Integer otf) { return null; }

  // -- MetadataStore API methods --

  public void createRoot() { }

  public Object getRoot() { return null; }

  public void setChannelGlobalMinMax(int channel, Double globalMin,
    Double globalMax, Integer i)
  {
  }

  public void setDefaultDisplaySettings(Integer i) { }

  public void setDimensions(Float pixelSizeX, Float pixelSizeY,
    Float pixelSizeZ, Float pixelSizeC, Float pixelSizeT, Integer i)
  {
  }

  public void setDisplayROI(Integer x0, Integer y0, Integer z0, Integer x1,
    Integer y1, Integer z1, Integer t0, Integer t1, Object displayOptions,
    Integer i)
  {
  }

  public void setExperimenter(String firstName, String lastName, String email,
    String institution, String dataDirectory, Object group, Integer i)
  {
  }

  public void setGroup(String name, Object leader, Object contact, Integer i) {
  }

  public void setImage(String name, String creationDate, String description,
    Integer i)
  {
  }

  public void setInstrument(String manufacturer, String model,
    String serialNumber, String type, Integer i)
  {
  }

  public void setLogicalChannel(int channelIdx, String name,
    Integer samplesPerPixel, Integer filter, Integer lightSource,
    Float lightAttenuation, Integer lightWavelength, Integer otf,
    Integer detector, Float detectorOffset, Float detectorGain,
    String illuminationType, Integer pinholeSize,
    String photometricInterpretation, String mode, String contrastMethod,
    Integer auxLightSource, Float auxLightAttenuation, String auxTechnique,
    Integer auxLightWavelength, Integer emWave, Integer exWave, String fluor,
    Float ndFilter, Integer i)
  {
  }

  public void setPixels(Integer sizeX, Integer sizeY, Integer sizeZ,
    Integer sizeC, Integer sizeT, Integer pixelType, Boolean bigEndian,
    String dimensionOrder, Integer imageNo, Integer pixelsNo)
  {
  }

  public void setPlaneInfo(int theZ, int theC, int theT, Float timestamp,
    Float exposureTime, Integer i)
  {
  }

  public void setRoot(Object root) { }

  public void setStageLabel(String name, Float x, Float y, Float z, Integer i) {
  }

  public void setImagingEnvironment(Float temperature, Float airPressure,
    Float humidity, Float co2Percent, Integer i)
  {
  }

  public void setDisplayChannel(Integer channelNumber, Double blackLevel,
    Double whiteLevel, Float gamma, Integer i)
  {
  }

  public void setDisplayOptions(Float zoom, Boolean redChannelOn,
    Boolean greenChannelOn, Boolean blueChannelOn, Boolean displayRGB,
    String colorMap, Integer zstart, Integer zstop, Integer tstart,
    Integer tstop, Integer imageNdx, Integer pixelNdx, Integer redChannel,
    Integer greenChannel, Integer blueChannel, Integer grayChannel)
  {
  }

  public void setLightSource(String manufacturer, String model,
    String serialNumber, Integer instrumentNdx, Integer lightNdx)
  {
  }

  public void setLaser(String type, String medium, Integer wavelength,
    Boolean frequencyDoubled, Boolean tunable, String pulse, Float power,
    Integer instrumentNdx, Integer lightNdx, Integer pumpNdx, Integer laserNdx)
  {
  }

  public void setFilament(String type, Float power, Integer lightNdx,
    Integer filamentNdx)
  {
  }

  public void setArc(String type, Float power, Integer lightNdx, Integer arcNdx)
  {
  }

  public void setDetector(String manufacturer, String model,
    String serialNumber, String type, Float gain, Float voltage, Float offset,
    Integer instrumentNdx, Integer detectorNdx)
  {
  }

  public void setObjective(String manufacturer, String model,
    String serialNumber, Float lensNA, Float magnification,
    Integer instrumentNdx, Integer objectiveNdx)
  {
  }

  public void setExcitationFilter(String manufacturer, String model,
    String lotNumber, String type, Integer filterNdx)
  {
  }

  public void setDichroic(String manufacturer, String model, String lotNumber,
    Integer dichroicNdx)
  {
  }

  public void setEmissionFilter(String manufacturer, String model,
    String lotNumber, String type, Integer filterNdx)
  {
  }

  public void setFilterSet(String manufacturer, String model, String lotNumber,
    Integer filterSetNdx, Integer filterNdx)
  {
  }

  public void setOTF(Integer sizeX, Integer sizeY, String pixelType,
    String path, Boolean opticalAxisAverage, Integer instrumentNdx,
    Integer otfNdx, Integer filterNdx, Integer objectiveNdx)
  {
  }

}
