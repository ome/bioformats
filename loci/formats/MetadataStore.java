//
// MetadataStore.java
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
 * A proxy whose responsibility it is to marshal biological image data into a
 * particular storage medium.
 *
 * The <code>MetadataStore</code> interface encompasses the basic metadata that
 * any specific storage medium (file, relational database, etc.) should be
 * expected to store and be expected to return with relationships maintained.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/MetadataStore.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/MetadataStore.java">SVN</a></dd></dl>
 *
 * @author Chris Allan callan at blackcat.ca
 *
 * TODO: Further work needs to be done to ensure the validity of the arguments
 * to these methods with not-null constraints and NullPointerException
 * declarations (should be unchecked exceptions).
 */
public interface MetadataStore {

  /**
   * Creates a new <i>root</i> object to be used by the metadata store and
   * resets the internal state of the metadata store.
   */
  void createRoot();

  /**
   * Sets the <i>root</i> object of the metadata store.
   * @param root object that the store can use as its root.
   */
  void setRoot(Object root);

  /**
   * Retrieves the <i>root</i> object of the metadata store.
   * @return object that the store is using as its root.
   */
  Object getRoot();

  /**
   * Creates an image in the metadata store with a particular index.
   * @param name the full name of the image.
   * @param creationDate the creation date of the image.
   * @param description the full description of the image.
   * @param i the index to use in the store. If <code>null</code> the default
   * index of 0 will be used.
   */
  void setImage(String name, String creationDate,
                String description, Integer i);

  /**
   * Creates an experimenter in the metadata store with a particular index.
   * @param firstName the first name of the experimenter
   * @param lastName the last name of the experimenter
   * @param email the e-mail address of the experimenter
   * @param institution the institution for which the experimenter belongs
   * @param dataDirectory the fully qualified path to the experimenter's data
   * @param group the group to which the experimenter belongs
   * @param i the index to use in the store. If <code>null</code> the default
   * index of 0 will be used.
   */
  void setExperimenter(String firstName, String lastName, String email,
                       String institution, String dataDirectory,
                       Object group, Integer i);

  /**
   * Creates a group in the metadata store with a particular index.
   * @param name the name of the group.
   * @param leader the leader of the group.
   * @param contact the contact for the group.
   * @param i the index to use in the store. If <code>null</code> the default
   * index of 0 will be used.
   */
  void setGroup(String name, Object leader, Object contact, Integer i);

  /**
   * Creates an instrument in the metadata store with a particular index.
   * @param manufacturer the name of the manufacturer.
   * @param model the model number of the instrument.
   * @param serialNumber the serial number of the instrument.
   * @param type the type of the instrument.
   * @param i the index to use in the store. If <code>null</code> the default
   * index of 0 will be used.
   */
  void setInstrument(String manufacturer, String model,
                     String serialNumber, String type, Integer i);

  /**
   * Creates a set of pixel dimensions in the metadata store with a particular
   * index.  Unless both values are non-null, the MetadataStore should assume
   * pixelSizeX equals pixelSizeY (i.e., should populate the null field with
   * the other field's value).
   * @param pixelSizeX size of an individual pixel's X axis in microns.
   * @param pixelSizeY size of an individual pixel's Y axis in microns.
   * @param pixelSizeZ size of an individual pixel's Z axis in microns.
   * @param pixelSizeC FIXME: Unknown
   * @param pixelSizeT FIXME: Unknown
   * @param i the index to use in the store. If <code>null</code> the default
   * index of 0 will be used.
   */
  void setDimensions(Float pixelSizeX, Float pixelSizeY,
                     Float pixelSizeZ, Float pixelSizeC,
                     Float pixelSizeT, Integer i);

  /**
   * Creates a 5D bounding box region of interest and a set of display options
   * in the metadata store with a particular index.
   * @param x0 the starting X coordinate.
   * @param y0 the starting Y coordinate.
   * @param z0 the starting Z coordinate.
   * @param x1 the ending X coordinate.
   * @param y1 the ending Y coordinate.
   * @param z1 the ending Z coordinate.
   * @param t0 the starting timepoint.
   * @param t1 the ending timepoint.
   * @param displayOptions the display options to attach to this region of
   * interest.
   * @param i the index to use in the store. If <code>null</code> the default
   * index of 0 will be used.
   */
  void setDisplayROI(Integer x0, Integer y0, Integer z0,
                     Integer x1, Integer y1, Integer z1,
                     Integer t0, Integer t1, Object displayOptions,
                     Integer i);

  /**
   * Creates a pixels set in the metadata store with a particular
   * image and pixels index.
   * @param sizeX size of an individual plane or section's X axis (width)
   * @param sizeY size of an individual plane of section's Y axis (height)
   * @param sizeZ number of optical sections per channel, per timepoint
   * (per stack)
   * @param sizeC number of channels per timepoint.
   * @param sizeT number of timepoints.
   * @param pixelType the pixel type. One of the enumerated static values
   * present in {@link FormatReader}.
   * @param bigEndian if the pixels set is big endian or not.
   * @param dimensionOrder the dimension order of the pixels set.
   * @param imageNo the image index to use in the store.
   * If <code>null</code> the default index of 0 will be used.
   * @param pixelsNo the pixels index to use in the store.
   * If <code>null</code> the default index of 0 will be used.
   */
  void setPixels(Integer sizeX, Integer sizeY, Integer sizeZ,
                 Integer sizeC, Integer sizeT, Integer pixelType,
                 Boolean bigEndian, String dimensionOrder,
                 Integer imageNo, Integer pixelsNo);

  /**
   * Creates a stage label in the metadata store with a particular index.
   * @param name a name for the stage label.
   * @param x coordinate of the stage.
   * @param y coordinate of the stage.
   * @param z coordinate of the stage.
   * @param i the index to use in the store. If <code>null</code> the default
   * index of 0 will be used.
   */
  void setStageLabel(String name, Float x, Float y, Float z, Integer i);

  /**
   * Creates a logical channel and physical channel in the metadata store for a
   * particular pixels.
   * @param channelIdx the index of the channel within the pixels set.
   * @param name the logical channel's name.
   * @param samplesPerPixel
   * @param filter index of the filter associated with this channel.
   * @param lightSource index of the primary light source.
   * @param lightAttenuation  the primary light source attenuation.
   * @param lightWavelength the primary light source wavelength.
   * @param otf the index of the OTF associated with this channel.
   * @param detector  the index of the detector associated with this channel.
   * @param detectorOffset the detector offset.
   * @param detectorGain the detector gain.
   * @param illuminationType the illumination type.
   * @param pinholeSize the size of the pinhole.
   * @param photometricInterpretation the photometric interpretation type.
   * @param mode the acquisition mode.
   * @param contrastMethod the constrast method name.
   * @param auxLightSource index of the auxiliary light source.
   * @param auxLightAttenuation the auxiliary light source attenuation.
   * @param auxTechnique the auxiliary technique type.
   * @param auxLightWavelength the auxiliary light source wavelength.
   * @param emWave the emission wavelength.
   * @param exWave the excitation wavelength.
   * @param fluor the fluorescence type.
   * @param ndFilter the neutral-density filter value.
   * @param i the index of the pixels set within the metadata store.
   */
  void setLogicalChannel(int channelIdx, String name, Integer samplesPerPixel,
    Integer filter, Integer lightSource, Float lightAttenuation,
    Integer lightWavelength, Integer otf, Integer detector,
    Float detectorOffset, Float detectorGain, String illuminationType,
    Integer pinholeSize, String photometricInterpretation, String mode,
    String contrastMethod, Integer auxLightSource, Float auxLightAttenuation,
    String auxTechnique, Integer auxLightWavelength, Integer emWave,
    Integer exWave, String fluor, Float ndFilter, Integer i);

  /**
   * Sets a channel's global min and global max in the metadata store for a
   * particular pixels set.
   *
   * NOTE: The implementation of this method is optional and can be purely a
   * no-op. It is here to ensure compatability with certain stores which require
   * this data to be specified explicitly.
   *
   * @param channel the index of the channel within the pixels set.
   * @param globalMin the global minimum pixel value for the channel.
   * @param globalMax the global maximum pixel value for the channel.
   * @param i the index of the pixels set within the metadata store.
   */
  void setChannelGlobalMinMax(int channel, Double globalMin,
                              Double globalMax, Integer i);

  /**
   * Sets the plane information for a particular X-Y plane (section) within a
   * particular pixels set.
   *
   * NOTE: The implementation of this method is optional as this is a
   * transitional type. More information about the PlaneInfo type can be found
   * <a href="http://cvs.openmicroscopy.org.uk/tiki/tiki-index.php?page=DataModelProposal#id119301">here</a>.
   *
   * @param theZ the optical section index.
   * @param theC the channel index.
   * @param theT the timepoint.
   * @param timestamp the time of acquisition in seconds of the plane (section)
   * with zero being the start of acquistion.
   * @param exposureTime exposure time in seconds.
   * @param i the index of the pixels set within the metadata store.
   */
  void setPlaneInfo(int theZ, int theC, int theT, Float timestamp,
                    Float exposureTime, Integer i);

  /**
   * Instructs the metadata store to set the default display settings for a
   * particular pixels set.
   * @param i the index of the pixels set within the metadata store.
   */
  void setDefaultDisplaySettings(Integer i);

  /** Sets the imaging environment for a particular image. */
  void setImagingEnvironment(Float temperature, Float airPressure,
    Float humidity, Float co2Percent, Integer i);

  /** Sets information about the specified channel for a particular image. */
  void setDisplayChannel(Integer channelNumber, Double blackLevel,
    Double whiteLevel, Float gamma, Integer i);

  /** Sets various display options for a particular pixels set. */
  void setDisplayOptions(Float zoom, Boolean redChannelOn,
    Boolean greenChannelOn, Boolean blueChannelOn, Boolean displayRGB,
    String colorMap, Integer zstart, Integer zstop, Integer tstart,
    Integer tstop, Integer imageNdx, Integer pixelsNdx, Integer redChannel,
    Integer greenChannel, Integer blueChannel, Integer grayChannel);

  /** Sets a light source for a particular instrument. */
  void setLightSource(String manufacturer, String model, String serialNumber,
    Integer instrumentIndex, Integer lightIndex);

  /** Sets a laser for a particular instrument. */
  void setLaser(String type, String medium, Integer wavelength,
    Boolean frequencyDoubled, Boolean tunable, String pulse, Float power,
    Integer instrumentNdx, Integer lightNdx, Integer pumpNdx, Integer laserNdx);

  /** Sets a filament for a particular instrument. */
  void setFilament(String type, Float power, Integer lightNdx,
    Integer filamentNdx);

  /** Sets an arc for a particular instrument. */
  void setArc(String type, Float power, Integer lightNdx, Integer arcNdx);

  /** Sets a detector for a particular instrument. */
  void setDetector(String manufacturer, String model, String serialNumber,
    String type, Float gain, Float voltage, Float offset, Integer instrumentNdx,
    Integer detectorNdx);

  /** Sets an objective for a particular instrument. */
  void setObjective(String manufacturer, String model, String serialNumber,
    Float lensNA, Float magnification, Integer instrumentNdx,
    Integer objectiveNdx);

  /** Sets an excitation filter for a particular instrument. */
  void setExcitationFilter(String manufacturer, String model, String lotNumber,
    String type, Integer filterNdx);

  /** Sets a dichroic for a particular instrument. */
  void setDichroic(String manufacturer, String model, String lotNumber,
    Integer dichroicNdx);

  /** Sets an emission filter for a particular instrument. */
  void setEmissionFilter(String manufacturer, String model, String lotNumber,
    String type, Integer filterNdx);

  /** Sets a filter set for a particular instrument. */
  void setFilterSet(String manufacturer, String model, String lotNumber,
    Integer filterSetNdx, Integer filterNdx);

  /** Sets an OTF for a particular instrument. */
  void setOTF(Integer sizeX, Integer sizeY, String pixelType, String path,
    Boolean opticalAxisAverage, Integer instrumentNdx, Integer otfNdx,
    Integer filterNdx, Integer objectiveNdx);

}
