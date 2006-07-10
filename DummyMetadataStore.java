//
// DummyMetadataStore.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

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
 * @author Chris Allan callan at blackcat.ca
 */
public class DummyMetadataStore implements MetadataStore {

  public void createRoot() {
  }

  public Object getRoot() {
    return null;
  }

  public void setChannelGlobalMinMax(int channel, Double globalMin,
    Double globalMax, Integer i)
  {
  }

  public void setDefaultDisplaySettings(Integer i) {
  }

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

  public void setLogicalChannel(int channelIdx, String name, float ndFilter,
    int emWave, int exWave, String photometricInterpretation, String mode,
    Integer i)
  {
  }

  public void setPixels(Integer sizeX, Integer sizeY, Integer sizeZ,
    Integer sizeC, Integer sizeT, String pixelType, Boolean bigEndian,
    String dimensionOrder, Integer i)
  {
  }

  public void setPlaneInfo(int theZ, int theC, int theT, Float timestamp,
    Float exposureTime, Integer i)
  {
  }

  public void setRoot(Object root) throws IllegalArgumentException {
  }

  public void setStageLabel(String name, Float x, Float y, Float z, Integer i) {
  }

}
