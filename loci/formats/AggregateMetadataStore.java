//
// AggregateMetadataStore.java
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A metadata store which delegates the actual storage to one or more <i>sub</i>
 * metadata stores.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/AggregateMetadataStore.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/AggregateMetadataStore.java">SVN</a></dd></dl>
 *
 * @author Chris Allan callan at blackcat.ca
 */
public class AggregateMetadataStore implements MetadataStore {

  /** The active metadata store delegates */
  private List delegates = new ArrayList();

  /**
   * Creates a new instance.
   * @param delegates of type {@link MetadataStore}.
   */
  public AggregateMetadataStore(List delegates) {
    this.delegates = delegates;
  }

  /**
   * Adds a delegate to the metadata store.
   * @param delegate a <code>MetadataStore</code>
   */
  public void addDelegate(MetadataStore delegate) {
    delegates.add(delegate);
  }

  /**
   * Removes a delegate from the metadata store.
   * @param delegate a <code>MetadataStore</code>
   */
  public void removeDelegate(MetadataStore delegate) {
    delegates.remove(delegate);
  }

  /**
   * Retrieves the current list of metadata store delegates.
   * @return list of {@link MetadataStore} delegates.
   */
  public List getDelegates() {
    return delegates;
  }

  /* @see MetadataStore#createRoot() */
  public void createRoot() {
    for (Iterator i = delegates.iterator(); i.hasNext();) {
      ((MetadataStore) i.next()).createRoot();
    }
  }

  /**
   * Unsupported with an AggregateMetadataStore.
   * Throws a RuntimeException up to the caller.
   */
  public Object getRoot() {
    throw new RuntimeException("Unsupported with AggregateMetadataStore. " +
      "Use getDelegates() and getRoot().");
  }

  /**
   * Unsupported with an AggregateMetadataStore.
   * Throws a RuntimeException up to the caller.
   */
  public void setRoot(Object root) {
    throw new RuntimeException("Unsupported with AggregateMetadataStore. " +
      "Use getDelegates() and setRoot().");
  }

  /* @see MetadataStore#setChannelGlobalMinMax(int, Double, Double, Integer) */
  public void setChannelGlobalMinMax(int channel, Double globalMin,
    Double globalMax, Integer i)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setChannelGlobalMinMax(channel, globalMin, globalMax, i);
    }
  }

  /* @see MetadataStore#setDefaultDisplaySettings(Integer) */
  public void setDefaultDisplaySettings(Integer i) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setDefaultDisplaySettings(i);
    }
  }

  /*
   * @see MetadataStore#setDimensions(Float,
   *   Float, Float, Float, Float, Integer)
   */
  public void setDimensions(Float pixelSizeX, Float pixelSizeY,
    Float pixelSizeZ, Float pixelSizeC, Float pixelSizeT, Integer i)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setDimensions(pixelSizeX, pixelSizeY, pixelSizeZ, pixelSizeC,
        pixelSizeT, i);
    }
  }

  /*
   * @see MetadataStore#setDisplayROI(Integer, Integer, Integer,
   *   Integer, Integer, Integer, Integer, Integer, Object, Integer)
   */
  public void setDisplayROI(Integer x0, Integer y0, Integer z0, Integer x1,
    Integer y1, Integer z1, Integer t0, Integer t1, Object displayOptions,
    Integer i)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setDisplayROI(x0, y0, z0, x1, y1, z1, t0, t1, displayOptions, i);
    }
  }

  /*
   * @see MetadataStore#setExperimenter(String,
   *   String, String, String, String, Object, Integer)
   */
  public void setExperimenter(String firstName, String lastName, String email,
    String institution, String dataDirectory, Object group, Integer i)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setExperimenter(firstName, lastName, email, institution, dataDirectory,
                        group, i);
    }
  }

  /* @see MetadataStore#setGroup(String, Object, Object, Integer) */
  public void setGroup(String name, Object leader, Object contact, Integer i) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setGroup(name, leader, contact, i);
    }
  }

  /* @see MetadataStore#setImage(String, String, String, Integer) */
  public void setImage(String name, String creationDate, String description,
    Integer i)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setImage(name, creationDate, description, i);
    }
  }

  /*
   * @see MetadataStore#setInstrument(String, String, String, String, Integer)
   */
  public void setInstrument(String manufacturer, String model,
    String serialNumber, String type, Integer i)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setInstrument(manufacturer, model, serialNumber, type, i);
    }
  }

  /*
   * @see MetadataStore#setLogicalChannel(int, String, Integer, Integer,
   * Integer, Float, Integer, Integer, Integer, Float, Float, String,
   * Integer, String, String, String, Integer, Float, String, Integer, Integer,
   * Integer, String, Float, Integer)
   */
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
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setLogicalChannel(channelIdx, name, samplesPerPixel, filter,
        lightSource, lightAttenuation, lightWavelength, otf, detector,
        detectorOffset, detectorGain, illuminationType, pinholeSize,
        photometricInterpretation, mode, contrastMethod, auxLightSource,
        auxLightAttenuation, auxTechnique, auxLightWavelength, emWave, exWave,
        fluor, ndFilter, i);
    }
  }

  /*
   * @see MetadataStore#setPixels(Integer, Integer, Integer,
   *   Integer, Integer, String, Boolean, String, Integer, Integer)
   */
  public void setPixels(Integer sizeX, Integer sizeY, Integer sizeZ,
    Integer sizeC, Integer sizeT, Integer pixelType, Boolean bigEndian,
    String dimensionOrder, Integer imageNo, Integer pixelsNo)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setPixels(sizeX, sizeY, sizeZ, sizeC, sizeT, pixelType, bigEndian,
                  dimensionOrder, imageNo, pixelsNo);
    }
  }

  /* @see MetadataStore#setPlaneInfo(int, int, int, Float, Float, Integer) */
  public void setPlaneInfo(int theZ, int theC, int theT, Float timestamp,
    Float exposureTime, Integer i)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setPlaneInfo(theZ, theC, theT, timestamp, exposureTime, i);
    }
  }

  /* @see MetadataStore#setStageLabel(String, Float, Float, Float, Integer) */
  public void setStageLabel(String name, Float x, Float y, Float z, Integer i) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setStageLabel(name, x, y, z, i);
    }
  }

  /* @see MetadataStore#setImagingEnvironment(Float, Float, Float,
   * Float, Integer)
   */
  public void setImagingEnvironment(Float temperature, Float airPressure,
    Float humidity, Float co2Percent, Integer i)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setImagingEnvironment(temperature, airPressure, humidity,
        co2Percent, i);
    }
  }

  /* @see MetadataStore#setDisplayChannel(Integer, Double, Double, Float
   * Integer)
   */
  public void setDisplayChannel(Integer channelNumber, Double blackLevel,
    Double whiteLevel, Float gamma, Integer i)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setDisplayChannel(channelNumber, blackLevel, whiteLevel, gamma, i);
    }
  }

  /* @see MetadataStore#setDisplayOptions(Float, Boolean, Boolean, Boolean,
   * Boolean, String, Integer, Integer, Integer, Integer, Integer, Integer,
   * Integer, Integer, Integer, Integer)
   */
  public void setDisplayOptions(Float zoom, Boolean redChannelOn,
    Boolean greenChannelOn, Boolean blueChannelOn, Boolean displayRGB,
    String colorMap, Integer zstart, Integer zstop, Integer tstart,
    Integer tstop, Integer imageNdx, Integer pixelNdx, Integer redChannel,
    Integer greenChannel, Integer blueChannel, Integer grayChannel)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setDisplayOptions(zoom, redChannelOn, greenChannelOn, blueChannelOn,
        displayRGB, colorMap, zstart, zstop, tstart, tstop, imageNdx, pixelNdx,
        redChannel, greenChannel, blueChannel, grayChannel);
    }
  }

  /* @see MetadataStore#setLightSource(String, String, String,
   * Integer, Integer)
   */
  public void setLightSource(String manufacturer, String model,
    String serialNumber, Integer instrumentNdx, Integer lightNdx)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setLightSource(manufacturer, model, serialNumber, instrumentNdx,
        lightNdx);
    }
  }

  /* @see MetadataStore#setLaser(String, String, Integer, Boolean, Boolean,
   * String, Float, Integer, Integer, Integer, Integer)
   */
  public void setLaser(String type, String medium, Integer wavelength,
    Boolean frequencyDoubled, Boolean tunable, String pulse, Float power,
    Integer instrumentNdx, Integer lightNdx, Integer pumpNdx, Integer laserNdx)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setLaser(type, medium, wavelength, frequencyDoubled, tunable, pulse,
        power, instrumentNdx, lightNdx, pumpNdx, laserNdx);
    }
  }

  /* @see MetadataStore#setFilament(String, Float, Integer, Integer) */
  public void setFilament(String type, Float power, Integer lightNdx,
    Integer filamentNdx)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setFilament(type, power, lightNdx, filamentNdx);
    }
  }

  /* @see MetadataStore#setArc(String, Float, Integer, Integer) */
  public void setArc(String type, Float power, Integer lightNdx, Integer arcNdx)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setArc(type, power, lightNdx, arcNdx);
    }
  }

  /* @see MetadataStore#setDetector(String, String, String, String, Float,
   * Float, Float, Integer, Integer)
   */
  public void setDetector(String manufacturer, String model,
    String serialNumber, String type, Float gain, Float voltage, Float offset,
    Integer instrumentNdx, Integer detectorNdx)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setDetector(manufacturer, model, serialNumber, type, gain, voltage,
        offset, instrumentNdx, detectorNdx);
    }
  }

  /* @see MetadataStore#setObjective(String, String, String, Float, Float,
   * Integer, Integer)
   */
  public void setObjective(String manufacturer, String model,
    String serialNumber, Float lensNA, Float magnification,
    Integer instrumentNdx, Integer objectiveNdx)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setObjective(manufacturer, model, serialNumber, lensNA, magnification,
        instrumentNdx, objectiveNdx);
    }
  }

  /* @see MetadataStore#setExcitationFilter(String, String, String, String,
   * Integer)
   */
  public void setExcitationFilter(String manufacturer, String model,
    String lotNumber, String type, Integer filterNdx)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setExcitationFilter(manufacturer, model, lotNumber, type, filterNdx);
    }
  }

  /* @see MetadataStore#setDichroic(String, String, String, Integer) */
  public void setDichroic(String manufacturer, String model, String lotNumber,
    Integer dichroicNdx)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setDichroic(manufacturer, model, lotNumber, dichroicNdx);
    }
  }

  /* @see MetadataStore#setEmissionFilter(String, String, String,
   * String, Integer)
   */
  public void setEmissionFilter(String manufacturer, String model,
    String lotNumber, String type, Integer filterNdx)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setEmissionFilter(manufacturer, model, lotNumber, type, filterNdx);
    }
  }

  /* @see MetadataStore#setFilterSet(String, String, String, Integer,
   * Integer)
   */
  public void setFilterSet(String manufacturer, String model, String lotNumber,
    Integer filterSetNdx, Integer filterNdx)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setFilterSet(manufacturer, model, lotNumber, filterSetNdx, filterNdx);
    }
  }

  /* @see MetadataStore#setOTF(Integer, Integer, String, String,
   * Boolean, Integer, Integer, Integer, Integer)
   */
  public void setOTF(Integer sizeX, Integer sizeY, String pixelType,
    String path, Boolean opticalAxisAverage, Integer instrumentNdx,
    Integer otfNdx, Integer filterNdx, Integer objectiveNdx)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setOTF(sizeX, sizeY, pixelType, path, opticalAxisAverage,
        instrumentNdx, otfNdx, filterNdx, objectiveNdx);
    }
  }

}
