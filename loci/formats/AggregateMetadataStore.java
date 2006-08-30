//
// AggregateMetadataStore.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A metadata store which delegates the actual storage to one or more <i>sub</i>
 * metadata stores.
 *
 * @author Chris Allan callan at blackcat.ca
 */
public class AggregateMetadataStore implements MetadataStore {

  /** The active metadata store delegates */
  private List delegates = new ArrayList();

  /**
   * Creates a new instance.
   * @param delegates of type {@link MetadataStore}.
   * @throws MetadataStoreException if the constructor is unable to
   * import pre-requisites of its delegates.
   */
  public AggregateMetadataStore(List delegates) throws MetadataStoreException {
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

  /* (non-Javadoc)
   * @see loci.formats.MetadataStore#createRoot()
   */
  public void createRoot() {
    for (Iterator i = delegates.iterator(); i.hasNext();) {
      ((MetadataStore) i.next()).createRoot();
    }
  }

  /**
   * Unsupported with an AggregateMetadataStore. Throws a RuntimeException up to
   * the caller.
   */
  public Object getRoot() {
    throw new RuntimeException("Unsupported with AggregateMetadataStore. Use" +
      " getDelegates() and getRoot().");
  }

  /**
   * Unsupported with an AggregateMetadataStore. Throws a RuntimeException up to
   * the caller.
   */
  public void setRoot(Object root) {
    throw new RuntimeException("Unsupported with AggregateMetadataStore. Use" +
      " getDelegates() and setRoot().");
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setChannelGlobalMinMax(int,
   *   java.lang.Double, java.lang.Double, java.lang.Integer)
   */
  public void setChannelGlobalMinMax(int channel, Double globalMin,
    Double globalMax, Integer i)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setChannelGlobalMinMax(channel, globalMin, globalMax, i);
    }
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setDefaultDisplaySettings(
   *   java.lang.Integer)
   */
  public void setDefaultDisplaySettings(Integer i) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setDefaultDisplaySettings(i);
    }
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setDimensions(java.lang.Float,
   *   java.lang.Float, java.lang.Float, java.lang.Float, java.lang.Float,
   *   java.lang.Integer)
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
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setDisplayROI(java.lang.Integer,
   *   java.lang.Integer, java.lang.Integer, java.lang.Integer,
   *   java.lang.Integer, java.lang.Integer, java.lang.Integer,
   *   java.lang.Integer, java.lang.Object, java.lang.Integer)
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
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setExperimenter(java.lang.String,
   *   java.lang.String, java.lang.String, java.lang.String, java.lang.String,
   *   java.lang.Object, java.lang.Integer)
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

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setGroup(java.lang.String,
   *   java.lang.Object, java.lang.Object, java.lang.Integer)
   */
  public void setGroup(String name, Object leader, Object contact, Integer i) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setGroup(name, leader, contact, i);
    }
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setImage(java.lang.String,
   *   java.lang.String, java.lang.String, java.lang.Integer)
   */
  public void setImage(String name, String creationDate, String description,
    Integer i)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setImage(name, creationDate, description, i);
    }
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setInstrument(java.lang.String,
   *   java.lang.String, java.lang.String, java.lang.String, java.lang.Integer)
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
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setLogicalChannel(int, java.lang.String,
   *   java.lang.Float, java.lang.Integer, java.lang.Integer, java.lang.String,
   *   java.lang.String, java.lang.Integer)
   */
  public void setLogicalChannel(int channelIdx, String name, Float ndFilter,
    Integer emWave, Integer exWave, String photometricInterpretation,
    String mode, Integer i)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setLogicalChannel(channelIdx, name, ndFilter, emWave, exWave,
                          photometricInterpretation, mode, i);
    }
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setPixels(java.lang.Integer,
   *   java.lang.Integer, java.lang.Integer, java.lang.Integer,
   *   java.lang.Integer, java.lang.String, java.lang.Boolean,
   *   java.lang.String, java.lang.Integer)
   */
  public void setPixels(Integer sizeX, Integer sizeY, Integer sizeZ,
    Integer sizeC, Integer sizeT, String pixelType, Boolean bigEndian,
    String dimensionOrder, Integer i)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setPixels(sizeX, sizeY, sizeZ, sizeC, sizeT, pixelType, bigEndian,
                  dimensionOrder, i);
    }
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setPlaneInfo(int, int, int,
   *   java.lang.Float, java.lang.Float, java.lang.Integer)
   */
  public void setPlaneInfo(int theZ, int theC, int theT, Float timestamp,
    Float exposureTime, Integer i)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setPlaneInfo(theZ, theC, theT, timestamp, exposureTime, i);
    }
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setStageLabel(java.lang.String,
   *   java.lang.Float, java.lang.Float, java.lang.Float, java.lang.Integer)
   */
  public void setStageLabel(String name, Float x, Float y, Float z, Integer i) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      MetadataStore s = (MetadataStore) iter.next();
      s.setStageLabel(name, x, y, z, i);
    }
  }

}
