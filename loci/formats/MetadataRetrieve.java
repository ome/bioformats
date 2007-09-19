//
// MetadataRetrieve.java
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
 * TODO - MetadataRetrieve javadoc.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/MetadataRetrieve.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/MetadataRetrieve.java">SVN</a></dd></dl>
 */
public interface MetadataRetrieve {

  // -- Image attribute retrieval methods --

  /** Returns the number of images. */
  int getImageCount();

  /**
   * Gets the nth image's name.
   * @param n the index of the image.  If <code>null</code> the default index
   * of 0 will be used.
   */
  String getImageName(Integer n);

  /**
   * Gets the nth image's creation date.
   * @param n the index of the image.  If <code>null</code> the default index
   * of 0 will be used.
   */
  String getCreationDate(Integer n);

  /**
   * Gets the nth image's description.
   * @param n the index of the image.  If <code>null</code> the default index
   * of 0 will be used.
   */
  String getDescription(Integer n);

  // -- Experimenter attribute retrieval methods --

  /** Returns the number of experimenters. */
  int getExperimenterCount();

  /**
   * Gets the first name of the nth experimenter.
   * @param n the index of the experimenter.  If <code>null</code> the default
   * index of 0 will be used.
   */
  String getFirstName(Integer n);

  /**
   * Gets the last name of the nth experimenter.
   * @param n the index of the experimenter.  If <code>null</code> the default
   * index of 0 will be used.
   */
  String getLastName(Integer n);

  /**
   * Gets the email address of the nth experimenter.
   * @param n the index of the experimenter.  If <code>null</code> the default
   * index of 0 will be used.
   */
  String getEmail(Integer n);

  /**
   * Gets the institution of the nth experimenter.
   * @param n the index of the experimenter.  If <code>null</code> the default
   * index of 0 will be used.
   */
  String getInstitution(Integer n);

  /**
   * Gets the data directory of the nth experimenter.
   * @param n the index of the experimenter.  If <code>null</code> the default
   * index of 0 will be used.
   */
  String getDataDirectory(Integer n);

  /**
   * Gets the group of the nth experimenter.
   * @param n the index of the experimenter.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Object getGroup(Integer n);

  // -- Group attribute retrieval methods --

  /** Returns the number of groups. */
  int getGroupCount();

  /**
   * Get the name of the nth group.
   * @param n the index of the group.  If <code>null</code> the default
   * index of 0 will be used.
   */
  String getGroupName(Integer n);

  /**
   * Get the leader of the nth group.
   * @param n the index of the group.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Object getLeader(Integer n);

  /**
   * Get the contact of the nth group.
   * @param n the index of the group.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Object getContact(Integer n);

  // -- Instrument attribute retrieval methods --

  /** Returns the number of instruments. */
  int getInstrumentCount();

  /**
   * Get the manufacturer of the nth instrument.
   * @param n the index of the instrument.  If <code>null</code> the default
   * index of 0 will be used.
   */
  String getManufacturer(Integer n);

  /**
   * Get the model of the nth instrument.
   * @param n the index of the instrument.  If <code>null</code> the default
   * index of 0 will be used.
   */
  String getModel(Integer n);

  /**
   * Get the serial number of the nth instrument.
   * @param n the index of the instrument.  If <code>null</code> the default
   * index of 0 will be used.
   */
  String getSerialNumber(Integer n);

  /**
   * Get the type of the nth instrument.
   * @param n the index of the instrument.  If <code>null</code> the default
   * index of 0 will be used.
   */
  String getType(Integer n);

  // -- Dimensions attribute retrieval methods --

  /**
   * Gets the nth image's PixelSizeX attribute.
   * @param n the index of the image.  If <code>null</code> the default index
   * of 0 will be used.
   */
  Float getPixelSizeX(Integer n);

  /**
   * Gets the nth image's PixelSizeY attribute.
   * @param n the index of the image.  If <code>null</code> the default index
   * of 0 will be used.
   */
  Float getPixelSizeY(Integer n);

  /**
   * Gets the nth image's PixelSizeZ attribute.
   * @param n the index of the image.  If <code>null</code> the default index
   * of 0 will be used.
   */
  Float getPixelSizeZ(Integer n);

  /**
   * Gets the nth image's PixelSizeC attribute.
   * @param n the index of the image.  If <code>null</code> the default index
   * of 0 will be used.
   */
  Float getPixelSizeC(Integer n);

  /**
   * Gets the nth image's PixelSizeT attribute.
   * @param n the index of the image.  If <code>null</code> the default index
   * of 0 will be used.
   */
  Float getPixelSizeT(Integer n);

  // -- Display ROI attribute retrieval methods --

  /** Returns the number of DisplayROIs. */
  int getDisplayROICount();

  /**
   * Get the lower X bound of the nth ROI.
   * @param n the index of the ROI.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Integer getX0(Integer n);

  /**
   * Get the lower Y bound of the nth ROI.
   * @param n the index of the ROI.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Integer getY0(Integer n);

  /**
   * Get the lower Z bound of the nth ROI.
   * @param n the index of the ROI.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Integer getZ0(Integer n);

  /**
   * Get the lower T bound of the nth ROI.
   * @param n the index of the ROI.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Integer getT0(Integer n);

  /**
   * Get the upper X bound of the nth ROI.
   * @param n the index of the ROI.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Integer getX1(Integer n);

  /**
   * Get the upper Y bound of the nth ROI.
   * @param n the index of the ROI.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Integer getY1(Integer n);

  /**
   * Get the upper Z bound of the nth ROI.
   * @param n the index of the ROI.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Integer getZ1(Integer n);

  /**
   * Get the upper T bound of the nth ROI.
   * @param n the index of the ROI.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Integer getT1(Integer n);

  /**
   * Get the display options of the nth ROI.
   * @param n the index of the ROI.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Object getDisplayOptions(Integer n);

  // -- Pixels attribute retrieval methods --

  /** Returns the number of pixels elements for the given image. */
  int getPixelsCount(Integer n);

  /**
   * Gets the SizeX attribute of the given pixels in the given image.
   * @param image the index of the image.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Integer getSizeX(Integer image);

  /**
   * Gets the SizeY attribute of the given pixels in the given image.
   * @param image the index of the image.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Integer getSizeY(Integer image);

  /**
   * Gets the SizeZ attribute of the given pixels in the given image.
   * @param image the index of the image.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Integer getSizeZ(Integer image);

  /**
   * Gets the SizeC attribute of the given pixels in the given image.
   * @param image the index of the image.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Integer getSizeC(Integer image);

  /**
   * Gets the SizeT attribute of the given pixels in the given image.
   * @param image the index of the image.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Integer getSizeT(Integer image);

  /**
   * Gets the PixelType attribute of the given pixels in the given image.
   * @param image the index of the image.  If <code>null</code> the default
   * index of 0 will be used.
   */
  String getPixelType(Integer image);

  /**
   * Gets the BigEndian attribute of the given pixels in the given image.
   * @param image the index of the image.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Boolean getBigEndian(Integer image);

  /**
   * Gets the DimensionOrder attribute of the given pixels in the given image.
   * @param image the index of the image.  If <code>null</code> the default
   * index of 0 will be used.
   */
  String getDimensionOrder(Integer image);

  // -- StageLabel attribute retrieval methods --

  /** Returns the number of stage labels. */
  int getStageLabelCount();

  /**
   * Gets the name of the nth stage label.
   * @param n the index of the stage label.  If <code>null</code> the default
   * index of 0 will be used.
   */
  String getStageName(Integer n);

  /**
   * Gets the X coordinate of the nth stage label.
   * @param n the index of the stage label.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Float getStageX(Integer n);

  /**
   * Gets the Y coordinate of the nth stage label.
   * @param n the index of the stage label.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Float getStageY(Integer n);

  /**
   * Gets the Z coordinate of the nth stage label.
   * @param n the index of the stage label.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Float getStageZ(Integer n);

  // -- LogicalChannel attribute retrieval methods --

  /** Returns the number of channels for the given pixels element. */
  int getChannelCount(Integer n);

  /**
   * Gets the name of the given channel in the given set of pixels.
   * @param pixels the index of the pixels.  If <code>null</code> the default
   * index of 0 will be used.
   * @param channel the index of the channel.  If <code>null</code> the default
   * index of 0 will be used.
   */
  String getChannelName(Integer pixels, Integer channel);

  /**
   * Gets the ND filter value for the given channel in the given set of pixels.
   * @param pixels the index of the pixels.  If <code>null</code> the default
   * index of 0 will be used.
   * @param channel the index of the channel.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Float getChannelNDFilter(Integer pixels, Integer channel);

  /**
   * Gets the emission wavelength of the given channel in the given
   * set of pixels.
   * @param pixels the index of the pixels.  If <code>null</code> the default
   * index of 0 will be used.
   * @param channel the index of the channel.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Integer getEmWave(Integer pixels, Integer channel);

  /**
   * Gets the excitation wavelength of the given channel in the given
   * set of pixels.
   * @param pixels the index of the pixels.  If <code>null</code> the default
   * index of 0 will be used.
   * @param channel the index of the channel.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Integer getExWave(Integer pixels, Integer channel);

  /**
   * Gets the photometric interpretation of the given channel in the given
   * set of pixels.
   * @param pixels the index of the pixels.  If <code>null</code> the default
   * index of 0 will be used.
   * @param channel the index of the channel.  If <code>null</code> the default
   * index of 0 will be used.
   */
  String getPhotometricInterpretation(Integer pixels, Integer channel);

  /**
   * Gets the mode of the given channel in the given set of pixels.
   * @param pixels the index of the pixels.  If <code>null</code> the default
   * index of 0 will be used.
   * @param channel the index of the channel.  If <code>null</code> the default
   * index of 0 will be used.
   */
  String getMode(Integer pixels, Integer channel);

  /**
   * Gets the minimum pixel value within the given channel in the
   * given set of pixels.
   * @param pixels the index of the pixels.  If <code>null</code> the default
   * index of 0 will be used.
   * @param channel the index of the channel.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Double getGlobalMin(Integer pixels, Integer channel);

  /**
   * Gets the maximum pixel value within the given channel in the
   * given set of pixels.
   * @param pixels the index of the pixels.  If <code>null</code> the default
   * index of 0 will be used.
   * @param channel the index of the channel.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Double getGlobalMax(Integer pixels, Integer channel);

  // -- PlaneInfo attribute retrieval methods --

  /**
   * Gets the timestamp of the plane with the given Z, C and T coordinates in
   * the given set of pixels.
   * @param pixels the index of the pixels.  If <code>null</code> the default
   * index of 0 will be used.
   * @param z the Z coordinate of the plane.  If <code>null</code> the default
   * index of 0 will be used.
   * @param c the C coordinate of the plane.  If <code>null</code> the default
   * index of 0 will be used.
   * @param t the T coordinate of the plane.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Float getTimestamp(Integer pixels, Integer z, Integer c, Integer t);

  /**
   * Gets the exposure time of the plane with the given Z, C and T coordinates
   * in the given set of pixels.
   * @param pixels the index of the pixels.  If <code>null</code> the default
   * index of 0 will be used.
   * @param z the Z coordinate of the plane.  If <code>null</code> the default
   * index of 0 will be used.
   * @param c the C coordinate of the plane.  If <code>null</code> the default
   * index of 0 will be used.
   * @param t the T coordinate of the plane.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Float getExposureTime(Integer pixels, Integer z, Integer c, Integer t);

  // -- ImagingEnvironment attribute retrieval methods --

  /**
   * Gets the temperature associated with the given image.
   * @param n the index of the image.  If <code>null</code> the default index
   * of 0 will be used.
   */
  Float getTemperature(Integer n);

  /**
   * Gets the air pressure associated with the given image.
   * @param n the index of the image.  If <code>null</code> the default index
   * of 0 will be used.
   */
  Float getAirPressure(Integer n);

  /**
   * Gets the humidity associated with the given image.
   * @param n the index of the image.  If <code>null</code> the default index
   * of 0 will be used.
   */
  Float getHumidity(Integer n);

  /**
   * Gets the CO2 percentage associated with the given image.
   * @param n the index of the image.  If <code>null</code> the default index
   * of 0 will be used.
   */
  Float getCO2Percent(Integer n);

  // -- DisplayChannel attribute retrieval methods --

  /**
   * Gets the black level of the given channel in the given set of pixels.
   * @param pixels the index of the pixels.  If <code>null</code> the default
   * index of 0 will be used.
   * @param channel the index of the channel.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Double getBlackLevel(Integer pixels, Integer channel);

  /**
   * Gets the white level of the given channel in the given set of pixels.
   * @param pixels the index of the pixels.  If <code>null</code> the default
   * index of 0 will be used.
   * @param channel the index of the channel.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Double getWhiteLevel(Integer pixels, Integer channel);

  /**
   * Gets the gamma value of the given channel in the given set of pixels.
   * @param pixels the index of the pixels.  If <code>null</code> the default
   * index of 0 will be used.
   * @param channel the index of the channel.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Float getGamma(Integer pixels, Integer channel);

  // -- DisplayOptions attribute retrieval methods --

  /**
   * Gets the zoom value associated with the given pixels in the given image.
   * @param image the index of the image.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Float getZoom(Integer image);

  /**
   * Gets whether or not the red channel is activated.
   * @param image the index of the image.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Boolean isRedChannelOn(Integer image);

  /**
   * Gets whether or not the green channel is activated.
   * @param image the index of the image.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Boolean isGreenChannelOn(Integer image);

  /**
   * Gets whether or not the blue channel is activated.
   * @param image the index of the image.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Boolean isBlueChannelOn(Integer image);

  /**
   * Gets whether or not the given pixels are displayed as an RGB image.
   * @param image the index of the image.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Boolean isDisplayRGB(Integer image);

  /**
   * Gets the color map associated with the given pixels.
   * @param image the index of the image.  If <code>null</code> the default
   * index of 0 will be used.
   */
  String getColorMap(Integer image);

  /**
   * Gets the minimum Z coordinate for which the display settings apply.
   * @param image the index of the image.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Integer getZStart(Integer image);

  /**
   * Gets the maximum Z coordinate for which the display settings apply.
   * @param image the index of the image.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Integer getZStop(Integer image);

  /**
   * Gets the minimum T coordinate for which the display settings apply.
   * @param image the index of the image.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Integer getTStart(Integer image);

  /**
   * Gets the maximum T coordinate for which the display settings apply.
   * @param image the index of the image.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Integer getTStop(Integer image);

  // -- LightSource attribute retrieval methods --

  /**
   * Gets the manufacturer of the given light source.
   * @param light the index of the light.  If <code>null</code> the default
   * index of 0 will be used.
   */
  String getLightManufacturer(Integer light);

  /**
   * Gets the model of the given light source.
   * @param light the index of the light.  If <code>null</code> the default
   * index of 0 will be used.
   */
  String getLightModel(Integer light);

  /**
   * Gets the serial number of the given light source.
   * @param light the index of the light.  If <code>null</code> the default
   * index of 0 will be used.
   */
  String getLightSerial(Integer light);

  // -- Laser attribute retrieval methods --

  /**
   * Gets the type of the given laser.
   * @param laser the index of the laser.  If <code>null</code> the default
   * index of 0 will be used.
   */
  String getLaserType(Integer laser);

  /**
   * Gets the medium of the given laser.
   * @param laser the index of the laser.  If <code>null</code> the default
   * index of 0 will be used.
   */
  String getLaserMedium(Integer laser);

  /**
   * Gets the wavelength of the given laser.
   * @param laser the index of the laser.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Integer getLaserWavelength(Integer laser);

  /**
   * Gets whether or not the frequency is doubled for the given laser.
   * @param laser the index of the laser.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Boolean isFrequencyDoubled(Integer laser);

  /**
   * Gets whether or not the given laser is tunable.
   * @param laser the index of the laser.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Boolean isTunable(Integer laser);

  /**
   * Gets the pulse of the given laser.
   * @param laser the index of the laser.  If <code>null</code> the default
   * index of 0 will be used.
   */
  String getPulse(Integer laser);

  /**
   * Gets the power (in watts) of the given laser.
   * @param laser the index of the laser.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Float getPower(Integer laser);

  // -- Filament attribute retrieval methods --

  /**
   * Gets the type of the given filament.
   * @param filament the index of the filament.  If <code>null</code> the
   * default index of 0 will be used.
   */
  String getFilamentType(Integer filament);

  /**
   * Gets the power (in watts) of the given filament.
   * @param filament the index of the filament.  If <code>null</code> the
   * default index of 0 will be used.
   */
  Float getFilamentPower(Integer filament);

  // -- Arc attribute retrieval methods --

  /**
   * Gets the type of the given arc.
   * @param arc the index of the arc.  If <code>null</code> the default
   * index of 0 will be used.
   */
  String getArcType(Integer arc);

  /**
   * Gets the power (in watts) of the given arc.
   * @param arc the index of the arc.  If <code>null</code> the default
   * index of 0 will be used.
   */
  Float getArcPower(Integer arc);

  // -- Detector attribute retrieval methods --

  /**
   * Gets the manufacturer of the given detector.
   * @param detector the index of the detector.  If <code>null</code> the
   * default index of 0 will be used.
   */
  String getDetectorManufacturer(Integer detector);

  /**
   * Gets the model of the given detector.
   * @param detector the index of the detector.  If <code>null</code> the
   * default index of 0 will be used.
   */
  String getDetectorModel(Integer detector);

  /**
   * Gets the serial number of the given detector.
   * @param detector the index of the detector.  If <code>null</code> the
   * default index of 0 will be used.
   */
  String getDetectorSerial(Integer detector);

  /**
   * Gets the type of the given detector.
   * @param detector the index of the detector.  If <code>null</code> the
   * default index of 0 will be used.
   */
  String getDetectorType(Integer detector);

  /**
   * Gets the gain value of the given detector.
   * @param detector the index of the detector.  If <code>null</code> the
   * default index of 0 will be used.
   */
  Float getDetectorGain(Integer detector);

  /**
   * Gets the voltage of the given detector.
   * @param detector the index of the detector.  If <code>null</code> the
   * default index of 0 will be used.
   */
  Float getDetectorVoltage(Integer detector);

  /**
   * Gets the offset of the given detector.
   * @param detector the index of the detector.  If <code>null</code> the
   * default index of 0 will be used.
   */
  Float getDetectorOffset(Integer detector);

  // -- Objective attribute retrieval methods --

  /**
   * Gets the manufacturer of the given objective.
   * @param objective the index of the objective.  If <code>null</code> the
   * default index of 0 will be used.
   */
  String getObjectiveManufacturer(Integer objective);

  /**
   * Gets the model of the given objective.
   * @param objective the index of the objective.  If <code>null</code> the
   * default index of 0 will be used.
   */
  String getObjectiveModel(Integer objective);

  /**
   * Gets the serial number of the given objective.
   * @param objective the index of the objective.  If <code>null</code> the
   * default index of 0 will be used.
   */
  String getObjectiveSerial(Integer objective);

  /**
   * Gets the lens NA of the given objective.
   * @param objective the index of the objective.  If <code>null</code> the
   * default index of 0 will be used.
   */
  Float getLensNA(Integer objective);

  /**
   * Gets the magnification value of the given objective.
   * @param objective the index of the objective.  If <code>null</code> the
   * default index of 0 will be used.
   */
  Float getObjectiveMagnification(Integer objective);

  // -- ExcitationFilter attribute retrieval methods --

  /**
   * Gets the manufacturer of the given excitation filter.
   * @param filter the index of the filter.  If <code>null</code> the
   * default index of 0 will be used.
   */
  String getExcitationManufacturer(Integer filter);

  /**
   * Gets the model of the given excitation filter.
   * @param filter the index of the filter.  If <code>null</code> the
   * default index of 0 will be used.
   */
  String getExcitationModel(Integer filter);

  /**
   * Gets the lot number of the given excitation filter.
   * @param filter the index of the filter.  If <code>null</code> the
   * default index of 0 will be used.
   */
  String getExcitationLotNumber(Integer filter);

  /**
   * Gets the type of the given excitation filter.
   * @param filter the index of the filter.  If <code>null</code> the
   * default index of 0 will be used.
   */
  String getExcitationType(Integer filter);

  // -- Dichroic attribute retrieval methods --

  /**
   * Gets the manufacturer of the given dichroic.
   * @param dichroic the index of the dichroic.  If <code>null</code> the
   * default index of 0 will be used.
   */
  String getDichroicManufacturer(Integer dichroic);

  /**
   * Gets the model of the given dichroic.
   * @param dichroic the index of the dichroic.  If <code>null</code> the
   * default index of 0 will be used.
   */
  String getDichroicModel(Integer dichroic);

  /**
   * Gets the lot number of the given dichroic.
   * @param dichroic the index of the dichroic.  If <code>null</code> the
   * default index of 0 will be used.
   */
  String getDichroicLotNumber(Integer dichroic);

  // -- EmissionFilter attribute retrieval methods --

  /**
   * Gets the manufacturer of the given emission filter.
   * @param filter the index of the emission filter.  If <code>null</code> the
   * default index of 0 will be used.
   */
  String getEmissionManufacturer(Integer filter);

  /**
   * Gets the model of the given emission filter.
   * @param filter the index of the emission filter.  If <code>null</code> the
   * default index of 0 will be used.
   */
  String getEmissionModel(Integer filter);

  /**
   * Gets the lot number of the given emission filter.
   * @param filter the index of the emission filter.  If <code>null</code> the
   * default index of 0 will be used.
   */
  String getEmissionLotNumber(Integer filter);

  /**
   * Gets the type of the given emission filter.
   * @param filter the index of the emission filter.  If <code>null</code> the
   * default index of 0 will be used.
   */
  String getEmissionType(Integer filter);

  // -- FilterSet attribute retrieval methods --

  /**
   * Gets the manufacturer of the given filter set.
   * @param filterSet the index of the filter set.  If <code>null</code> the
   * default index of 0 will be used.
   */
  String getFilterSetManufacturer(Integer filterSet);

  /**
   * Gets the manufacturer of the given filter set.
   * @param filterSet the index of the filter set.  If <code>null</code> the
   * default index of 0 will be used.
   */
  String getFilterSetModel(Integer filterSet);

  /**
   * Gets the manufacturer of the given filter set.
   * @param filterSet the index of the filter set.  If <code>null</code> the
   * default index of 0 will be used.
   */
  String getFilterSetLotNumber(Integer filterSet);

  // -- OTF attribute retrieval methods --

  /**
   * Gets the width of the given OTF.
   * @param otf the index of the OTF.  If <code>null</code> the
   * default index of 0 will be used.
   */
  Integer getOTFSizeX(Integer otf);

  /**
   * Gets the height of the given OTF.
   * @param otf the index of the OTF.  If <code>null</code> the
   * default index of 0 will be used.
   */
  Integer getOTFSizeY(Integer otf);

  /**
   * Gets the pixel type of the given OTF.
   * @param otf the index of the OTF.  If <code>null</code> the
   * default index of 0 will be used.
   */
  String getOTFPixelType(Integer otf);

  /**
   * Gets the path to the given OTF.
   * @param otf the index of the OTF.  If <code>null</code> the
   * default index of 0 will be used.
   */
  String getOTFPath(Integer otf);

  /**
   * Gets whether or not optical axis averaging is used for the given OTF.
   * @param otf the index of the OTF.  If <code>null</code> the
   * default index of 0 will be used.
   */
  Boolean getOTFOpticalAxisAverage(Integer otf);
}
