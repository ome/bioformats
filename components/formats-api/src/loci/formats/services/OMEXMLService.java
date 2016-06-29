/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.services;

import java.util.Hashtable;

import loci.common.services.Service;
import loci.common.services.ServiceException;
import loci.formats.CoreMetadata;
import loci.formats.Modulo;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.ome.OMEXMLMetadata;
import ome.xml.model.OMEModelObject;

/**
 * 
 */
public interface OMEXMLService extends Service {

  /**
   * Retrieves the latest supported version of the OME-XML schema.
   */
  public String getLatestVersion();

  /**
   * Transforms the given OME-XML string to the latest supported version of
   * of the OME-XML schema.
   */
  public String transformToLatestVersion(String xml) throws ServiceException;

  /**
   * Creates an OME-XML metadata object using reflection, to avoid
   * direct dependencies on the optional {@link loci.formats.ome} package.
   * @return A new instance of {@link loci.formats.ome.AbstractOMEXMLMetadata},
   *   or null if the class is not available.
   * @throws ServiceException If there is an error creating the OME-XML
   * metadata object.
   */
  public OMEXMLMetadata createOMEXMLMetadata() throws ServiceException;

  /**
   * Creates an OME-XML metadata object using reflection, to avoid
   * direct dependencies on the optional {@link loci.formats.ome} package,
   * wrapping a DOM representation of the given OME-XML string.
   * @return A new instance of {@link loci.formats.ome.AbstractOMEXMLMetadata},
   *   or null if the class is not available.
   * @throws ServiceException If there is an error creating the OME-XML
   * metadata object.
   */
  public OMEXMLMetadata createOMEXMLMetadata(String xml)
    throws ServiceException;

  /**
   * Creates an OME-XML metadata object using reflection, to avoid
   * direct dependencies on the optional {@link loci.formats.ome} package,
   * wrapping a DOM representation of the given OME-XML string.
   *
   * @param xml The OME-XML string to use for initial population of the
   *   metadata object.
   * @param version The OME-XML version to use (e.g., "2003-FC" or "2007-06").
   *   If the xml and version parameters are both null, the newest version is
   *   used.
   * @return A new instance of {@link loci.formats.ome.AbstractOMEXMLMetadata},
   *   or null if the class is not available.
   * @throws ServiceException If there is an error creating the OME-XML
   * metadata object.
   */
  public OMEXMLMetadata createOMEXMLMetadata(String xml, String version)
    throws ServiceException;

  /**
   * Constructs an OME root node.
   * @param xml String of XML to create the root node from.
   * @return An ome.xml.model.OMEModelObject subclass root node.
   * @throws ServiceException If there is an error creating the OME-XML
   * metadata object.
   */
  public OMEModelObject createOMEXMLRoot(String xml) throws ServiceException;

  /**
   * Checks whether the given object is an OME-XML metadata object.
   * @return True if the object is an instance of
   *   {@link loci.formats.ome.OMEXMLMetadata}.
   */
  public boolean isOMEXMLMetadata(Object o);

  /**
   * Checks whether the given object is an OME-XML root object.
   * @return True if the object is an instance of {@link ome.xml.model.OME}.
   */
  public boolean isOMEXMLRoot(Object o);

  /**
   * Gets the schema version for the given OME-XML metadata or root object
   * (e.g., "2007-06" or "2003-FC").
   * @return OME-XML schema version, or null if the object is not an instance
   *   of {@link loci.formats.ome.OMEXMLMetadata}.
   */
  public String getOMEXMLVersion(Object o);

  /**
   * Returns a {@link loci.formats.ome.OMEXMLMetadata} object with the same
   * contents as the given MetadataRetrieve, converting it if necessary.
   * @throws ServiceException If there is an error creating the OME-XML
   * metadata object.
   */
  public OMEXMLMetadata getOMEMetadata(MetadataRetrieve src)
    throws ServiceException;

  /**
   * Extracts an OME-XML metadata string from the given metadata object,
   * by converting to an OME-XML metadata object if necessary.
   * @throws ServiceException If there is an error creating the OME-XML
   * metadata object.
   */
  public String getOMEXML(MetadataRetrieve src)
    throws ServiceException;

  /**
   * Attempts to validate the given OME-XML string using
   * Java's XML validation facility. Requires Java 1.5+.
   *
   * @param xml XML string to validate.
   * @return true if the XML successfully validates.
   */
  public boolean validateOMEXML(String xml);

  /**
   * Attempts to validate the given OME-XML string using
   * Java's XML validation facility. Requires Java 1.5+.
   *
   * @param xml XML string to validate.
   * @param pixelsHack Whether to ignore validation errors
   *   due to childless Pixels elements
   * @return true if the XML successfully validates.
   */
  public boolean validateOMEXML(String xml, boolean pixelsHack);

  /**
   * Adds the key/value pairs in the specified Hashtable as new
   * OriginalMetadata annotations in the given OME-XML metadata object.
   * @param omexmlMeta An object of type
   *   {@link loci.formats.ome.OMEXMLMetadata}.
   * @param metadata A hashtable containing metadata key/value pairs.
   */
  public void populateOriginalMetadata(OMEXMLMetadata omexmlMeta,
    Hashtable<String, Object> metadata);

  /**
   * Retrieve the ModuloAlongZ annotation in the given Image in the given
   * OME-XML metadata object.
   * If no ModuloAlongZ annotation is present, return null.
   *
   * @param omexmlMeta An object of type
   *  {@link loci.formats.ome.OMEXMLMetadata}
   * @param image the index of the Image to which the Annotation is linked
   */
  public Modulo getModuloAlongZ(OMEXMLMetadata omexmlMeta, int image);

  /**
   * Retrieve the ModuloAlongC annotation in the given Image in the given
   * OME-XML metadata object.
   * If no ModuloAlongC annotation is present, return null.
   *
   * @param omexmlMeta An object of type
   *  {@link loci.formats.ome.OMEXMLMetadata}
   * @param image the index of the Image to which the Annotation is linked
   */
  public Modulo getModuloAlongC(OMEXMLMetadata omexmlMeta, int image);

  /**
   * Retrieve the ModuloAlongT annotation in the given Image in the given
   * OME-XML metadata object.
   * If no ModuloAlongT annotation is present, return null.
   *
   * @param omexmlMeta An object of type
   *  {@link loci.formats.ome.OMEXMLMetadata}
   * @param image the index of the Image to which the Annotation is linked
   */
  public Modulo getModuloAlongT(OMEXMLMetadata omexmlMeta, int image);

  /**
   * Parse any OriginalMetadata annotations from the given OME-XML metadata
   * object and store them in a Hashtable.
   *
   * @param omexmlMeta An object of type
   *   {@link loci.formats.ome.OMEXMLMetadata}.
   */
  public Hashtable getOriginalMetadata(OMEXMLMetadata omexmlMeta);

  /**
   * Adds the specified key/value pair as a new OriginalMetadata node
   * to the given OME-XML metadata object.
   * @param omexmlMeta An object of type
   *   {@link loci.formats.ome.OMEXMLMetadata}.
   * @param key Metadata key to populate.
   * @param value Metadata value corresponding to the specified key.
   */
  public void populateOriginalMetadata(OMEXMLMetadata omexmlMeta,
    String key, String value);

  /**
   * Adds ModuloAlong* annotations to the given OME-XML metadata object,
   * using the given CoreMetadata object to determine modulo dimensions.
   *
   * @param omexmlMeta An object of type {@link loci.formats.ome.OMEXMLMetadata}
   * @param core A fully populated object of type
   *   {@link loci.formats.CoreMetadata}
   * @param image Index of the Image to which the annotation should be linked.
   */
  public void addModuloAlong(OMEXMLMetadata omexmlMeta, CoreMetadata core,
    int image);

  /**
   * Converts information from an OME-XML string (source)
   * into a metadata store (destination).
   * @throws ServiceException If there is an error creating the OME-XML
   * metadata object.
   */
  public void convertMetadata(String xml, MetadataStore dest)
    throws ServiceException;

  /**
   * Copies information from a metadata retrieval object
   * (source) into a metadata store (destination).
   */
  public void convertMetadata(MetadataRetrieve src, MetadataStore dest);

  /**
   * Remove all of the BinData elements from the given OME-XML metadata object.
   */
  public void removeBinData(OMEXMLMetadata omexmlMeta);

  /**
   * Remove all of the TiffData elements from the given OME-XML metadata object.
   */
  public void removeTiffData(OMEXMLMetadata omexmlMeta);

  /**
   * Remove all but the first sizeC valid Channel elements from the given
   * OME-XML metadata object.
   */
  public void removeChannels(OMEXMLMetadata omexmlMeta, int image, int sizeC);

  /**
   * Insert a MetadataOnly element under the Image specified by 'index' in the
   * given OME-XML metadata object.
   */
  public void addMetadataOnly(OMEXMLMetadata omexmlMeta, int image);

  /**
   * Insert a MetadataOnly element under the Image specified by 'index' in the
   * given OME-XML metadata object.  If the 'resolve' flag is set, references
   * in the OME-XML metadata object will be resolved before the new element is
   * inserted.
   */
  public void addMetadataOnly(OMEXMLMetadata omexmlMeta, int image, boolean resolve);

  /**
   * Determine whether or not two OMEXMLMetadata objects are equal.
   * Equality is defined as:
   *
   *  * having the same object graph (without regard to specific ID values)
   *  * having the exact same attribute values on every node (with the exception
   *    of 'ID' attributes)
   *
   * Note that StructuredAnnotations are ignored, i.e. the two OMEXMLMetadata
   * objects may have wildly different things under StructuredAnnotations and
   * still be considered equal.
   */
  public boolean isEqual(OMEXMLMetadata src1, OMEXMLMetadata src2);

  // -- Utility methods - casting --

  /**
   * Gets the given {@link MetadataRetrieve} object as a {@link MetadataStore}.
   * Returns null if the object is incompatible and cannot be casted.
   */
  public MetadataStore asStore(MetadataRetrieve meta);

  /**
   * Gets the given {@link MetadataStore} object as a {@link MetadataRetrieve}.
   * Returns null if the object is incompatible and cannot be casted.
   */
  public MetadataRetrieve asRetrieve(MetadataStore meta);

}
