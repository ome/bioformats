//
// DummyMetadataStore.java
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
 * A dummy <code>MetadataStore</code> implementation that is used when no other
 * metadata stores are available.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/DummyMetadataStore.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/DummyMetadataStore.java">SVN</a></dd></dl>
 *
 * @author Chris Allan callan at blackcat.ca
 */
public class DummyMetadataStore implements MetadataStore {

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
