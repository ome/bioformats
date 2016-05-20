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

package loci.formats.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ome.xml.model.*;
import ome.xml.model.enums.*;
import ome.xml.model.primitives.*;
import ome.units.quantity.ElectricPotential;
import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.quantity.Pressure;
import ome.units.quantity.Temperature;
import ome.units.quantity.Time;
import ome.units.UNITS;

/**
 * A utility class containing a method for piping a source
 * {@link MetadataRetrieve} object into a destination {@link MetadataStore}.
 *
 * <p>This technique allows conversion between two different storage media.
 * For example, it can be used to convert an <code>OMEROMetadataStore</code>
 * (OMERO's metadata store implementation) into an
 * {@link loci.formats.ome.OMEXMLMetadata}, thus generating OME-XML from
 * information in an OMERO database.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public final class MetadataConverter {

  // -- Constructor --

  /**
   * Private constructor; all methods in MetadataConverter
   * are static, so this should not be called.
   */
  private MetadataConverter() { }

  // -- MetadataConverter API methods --

  /**
   * Copies information from a metadata retrieval object
   * (source) into a metadata store (destination).
   */
  public static void convertMetadata(MetadataRetrieve src, MetadataStore dest) {
    convertBooleanAnnotations(src, dest);
    convertCommentAnnotations(src, dest);
    convertDoubleAnnotations(src, dest);
    convertFileAnnotations(src, dest);
    convertListAnnotations(src, dest);
    convertLongAnnotations(src, dest);
    convertMapAnnotations(src, dest);
    convertTagAnnotations(src, dest);
    convertTermAnnotations(src, dest);
    convertTimestampAnnotations(src, dest);
    convertXMLAnnotations(src, dest);

    convertROIs(src, dest);
    List<String> lightSourceIds = convertInstruments(src, dest);
    convertExperimenters(src, dest);
    convertExperimenterGroups(src, dest);
    convertExperiments(src, dest);
    convertImages(src, dest, lightSourceIds);
    convertPlates(src, dest);
    convertScreens(src, dest);
    convertDatasets(src, dest);
    convertProjects(src, dest);
    convertFolders(src, dest);

    convertRootAttributes(src, dest);
  }

  // -- Helper methods --

  /**
   * Convert all BooleanAnnotation attributes.
   * @param src the MetadataRetrieve from which to copy
   * @param dest the MetadataStore to which to copy
   */
  private static void convertBooleanAnnotations(MetadataRetrieve src,
    MetadataStore dest)
  {
    int booleanAnnotationCount = 0;
    try {
      booleanAnnotationCount = src.getBooleanAnnotationCount();
    }
    catch (NullPointerException e) { }
    for (int i=0; i<booleanAnnotationCount; i++) {
      try {
        String id = src.getBooleanAnnotationID(i);
        dest.setBooleanAnnotationID(id, i);
      }
      catch (NullPointerException e) {
        continue;
      }

      try {
        String description = src.getBooleanAnnotationDescription(i);
        dest.setBooleanAnnotationDescription(description, i);
      }
      catch (NullPointerException e) { }

      try {
        String namespace = src.getBooleanAnnotationNamespace(i);
        dest.setBooleanAnnotationNamespace(namespace, i);
      }
      catch (NullPointerException e) { }

      try {
        Boolean value = src.getBooleanAnnotationValue(i);
        dest.setBooleanAnnotationValue(value, i);
      }
      catch (NullPointerException e) { }

      try {
        String annotator = src.getBooleanAnnotationAnnotator(i);
        dest.setBooleanAnnotationAnnotator(annotator, i);
      }
      catch (NullPointerException e) { }

      int annotationRefCount = 0;
      try {
        annotationRefCount = src.getBooleanAnnotationAnnotationCount(i);
      }
      catch (NullPointerException e) { }
      for (int a=0; a<annotationRefCount; a++) {
        try {
          String id = src.getBooleanAnnotationAnnotationRef(i, a);
          dest.setBooleanAnnotationAnnotationRef(id, i, a);
        }
        catch (NullPointerException e) { }
      }
    }
  }

  /**
   * Convert all CommentAnnotation attributes.
   * @param src the MetadataRetrieve from which to copy
   * @param dest the MetadataStore to which to copy
   */
  private static void convertCommentAnnotations(MetadataRetrieve src,
    MetadataStore dest)
  {
    int commentAnnotationCount = 0;
    try {
      commentAnnotationCount = src.getCommentAnnotationCount();
    }
    catch (NullPointerException e) { }
    for (int i=0; i<commentAnnotationCount; i++) {
      try {
        String id = src.getCommentAnnotationID(i);
        dest.setCommentAnnotationID(id, i);
      }
      catch (NullPointerException e) {
        continue;
      }

      try {
        String description = src.getCommentAnnotationDescription(i);
        dest.setCommentAnnotationDescription(description, i);
      }
      catch (NullPointerException e) { }

      try {
        String namespace = src.getCommentAnnotationNamespace(i);
        dest.setCommentAnnotationNamespace(namespace, i);
      }
      catch (NullPointerException e) { }

      try {
        String value = src.getCommentAnnotationValue(i);
        dest.setCommentAnnotationValue(value, i);
      }
      catch (NullPointerException e) { }

      try {
        String annotator = src.getCommentAnnotationAnnotator(i);
        dest.setCommentAnnotationAnnotator(annotator, i);
      }
      catch (NullPointerException e) { }

      int annotationRefCount = 0;
      try {
        annotationRefCount = src.getCommentAnnotationAnnotationCount(i);
      }
      catch (NullPointerException e) { }
      for (int a=0; a<annotationRefCount; a++) {
        try {
          String id = src.getCommentAnnotationAnnotationRef(i, a);
          dest.setCommentAnnotationAnnotationRef(id, i, a);
        }
        catch (NullPointerException e) { }
      }
    }
  }

  /**
   * Convert all Dataset attributes.
   * @param src the MetadataRetrieve from which to copy
   * @param dest the MetadataStore to which to copy
   */
  private static void convertDatasets(MetadataRetrieve src, MetadataStore dest)
  {
    int datasets = 0;
    try {
      datasets = src.getDatasetCount();
    }
    catch (NullPointerException e) { }
    for (int i=0; i<datasets; i++) {
      try {
        String id = src.getDatasetID(i);
        dest.setDatasetID(id, i);
      }
      catch (NullPointerException e) {
        continue;
      }

      try {
        String description = src.getDatasetDescription(i);
        dest.setDatasetDescription(description, i);
      }
      catch (NullPointerException e) { }

      try {
        String experimenterGroupRef = src.getDatasetExperimenterGroupRef(i);
        dest.setDatasetExperimenterGroupRef(experimenterGroupRef, i);
      }
      catch (NullPointerException e) { }

      try {
        String experimenterRef = src.getDatasetExperimenterRef(i);
        dest.setDatasetExperimenterRef(experimenterRef, i);
      }
      catch (NullPointerException e) { }

      try {
        String name = src.getDatasetName(i);
        dest.setDatasetName(name, i);
      }
      catch (NullPointerException e) { }

      try {
        int imageRefCount = src.getDatasetImageRefCount(i);
        for (int q=0; q<imageRefCount; q++) {
          try {
            String imageRef = src.getDatasetImageRef(i, q);
            dest.setDatasetImageRef(imageRef, i, q);
          }
          catch (NullPointerException e) { }
        }
      }
      catch (NullPointerException e) { }

      try {
        int annotationRefCount = src.getDatasetAnnotationRefCount(i);
        for (int q=0; q<annotationRefCount; q++) {
          try {
            String annotationRef = src.getDatasetAnnotationRef(i, q);
            dest.setDatasetAnnotationRef(annotationRef, i, q);
          }
          catch (NullPointerException e) { }
        }
      }
      catch (NullPointerException e) { }
    }
  }

  /**
   * Convert all DoubleAnnotation attributes.
   * @param src the MetadataRetrieve from which to copy
   * @param dest the MetadataStore to which to copy
   */
  private static void convertDoubleAnnotations(MetadataRetrieve src,
    MetadataStore dest)
  {
    int doubleAnnotationCount = 0;
    try {
      doubleAnnotationCount = src.getDoubleAnnotationCount();
    }
    catch (NullPointerException e) { }
    for (int i=0; i<doubleAnnotationCount; i++) {
      try {
        String id = src.getDoubleAnnotationID(i);
        dest.setDoubleAnnotationID(id, i);
      }
      catch (NullPointerException e) {
        continue;
      }

      try {
        String description = src.getDoubleAnnotationDescription(i);
        dest.setDoubleAnnotationDescription(description, i);
      }
      catch (NullPointerException e) { }

      try {
        String namespace = src.getDoubleAnnotationNamespace(i);
        dest.setDoubleAnnotationNamespace(namespace, i);
      }
      catch (NullPointerException e) { }

      try {
        Double value = src.getDoubleAnnotationValue(i);
        dest.setDoubleAnnotationValue(value, i);
      }
      catch (NullPointerException e) { }

      try {
        String annotator = src.getDoubleAnnotationAnnotator(i);
        dest.setDoubleAnnotationAnnotator(annotator, i);
      }
      catch (NullPointerException e) { }

      int annotationRefCount = 0;
      try {
        annotationRefCount = src.getDoubleAnnotationAnnotationCount(i);
      }
      catch (NullPointerException e) { }
      for (int a=0; a<annotationRefCount; a++) {
        try {
          String id = src.getDoubleAnnotationAnnotationRef(i, a);
          dest.setDoubleAnnotationAnnotationRef(id, i, a);
        }
        catch (NullPointerException e) { }
      }
    }
  }

  /**
   * Convert all Experiment attributes.
   * @param src the MetadataRetrieve from which to copy
   * @param dest the MetadataStore to which to copy
   */
  private static void convertExperiments(MetadataRetrieve src,
    MetadataStore dest)
  {
    int experimentCount = 0;
    try {
      experimentCount = src.getExperimentCount();
    }
    catch (NullPointerException e) { }
    for (int i=0; i<experimentCount; i++) {
      try {
        String id = src.getExperimentID(i);
        dest.setExperimentID(id, i);
      }
      catch (NullPointerException e) {
        continue;
      }

      try {
        String description = src.getExperimentDescription(i);
        dest.setExperimentDescription(description, i);
      }
      catch (NullPointerException e) { }

      try {
        String experimenterRef = src.getExperimentExperimenterRef(i);
        dest.setExperimentExperimenterRef(experimenterRef, i);
      }
      catch (NullPointerException e) { }

      try {
        ExperimentType type = src.getExperimentType(i);
        dest.setExperimentType(type, i);
      }
      catch (NullPointerException e) { }

      int microbeamCount = 0;
      try {
        microbeamCount = src.getMicrobeamManipulationCount(i);
      }
      catch (NullPointerException e) { }
      for (int q=0; q<microbeamCount; q++) {
        try {
          String microbeamID = src.getMicrobeamManipulationID(i, q);
          dest.setMicrobeamManipulationID(microbeamID, i, q);
        }
        catch (NullPointerException e) {
          continue;
        }

        try {
          String microbeamDescription = src.getMicrobeamManipulationDescription(i, q);
          dest.setMicrobeamManipulationDescription(microbeamDescription, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String microbeamExperimenterRef = src.getMicrobeamManipulationExperimenterRef(i, q);
          dest.setMicrobeamManipulationExperimenterRef(microbeamExperimenterRef, i, q);
        }
        catch (NullPointerException e) { }

        try {
          MicrobeamManipulationType microbeamType = src.getMicrobeamManipulationType(i, q);
          dest.setMicrobeamManipulationType(microbeamType, i, q);
        }
        catch (NullPointerException e) { }

        int lightSourceCount = 0;
        try {
          lightSourceCount = src.getMicrobeamManipulationLightSourceSettingsCount(i, q);
        }
        catch (NullPointerException e) { }
        for (int p=0; p<lightSourceCount; p++) {
          String lightSourceID = src.getMicrobeamManipulationLightSourceSettingsID(i, q, p);
          if (lightSourceID != null) {
            dest.setMicrobeamManipulationLightSourceSettingsID(lightSourceID, i, q, p);

            try {
              PercentFraction attenuation = src.getMicrobeamManipulationLightSourceSettingsAttenuation(i, q, p);
              dest.setMicrobeamManipulationLightSourceSettingsAttenuation(attenuation, i, q, p);
            }
            catch (NullPointerException e) { }

            try {
              Length wavelength = src.getMicrobeamManipulationLightSourceSettingsWavelength(i, q, p);
              dest.setMicrobeamManipulationLightSourceSettingsWavelength(wavelength, i, q, p);
            }
            catch (NullPointerException e) { }
          }
        }

        int roiRefCount = 0;
        try {
          roiRefCount = src.getMicrobeamManipulationROIRefCount(i, q);
        }
        catch (NullPointerException e) { }
        for (int p=0; p<roiRefCount; p++) {
          try {
            String roiRef = src.getMicrobeamManipulationROIRef(i, q, p);
            dest.setMicrobeamManipulationROIRef(roiRef, i, q, p);
          }
          catch (NullPointerException e) { }
        }
      }
    }
  }

  /**
   * Convert all Experimenter attributes.
   * @param src the MetadataRetrieve from which to copy
   * @param dest the MetadataStore to which to copy
   */
  private static void convertExperimenters(MetadataRetrieve src,
    MetadataStore dest)
  {
    int experimenterCount = 0;
    try {
      experimenterCount = src.getExperimenterCount();
    }
    catch (NullPointerException e) { }
    for (int i=0; i<experimenterCount; i++) {
      try {
        String id = src.getExperimenterID(i);
        dest.setExperimenterID(id, i);
      }
      catch (NullPointerException e) {
        continue;
      }

      try {
        String email = src.getExperimenterEmail(i);
        dest.setExperimenterEmail(email, i);
      }
      catch (NullPointerException e) { }

      try {
        String firstName = src.getExperimenterFirstName(i);
        dest.setExperimenterFirstName(firstName, i);
      }
      catch (NullPointerException e) { }

      try {
        String institution = src.getExperimenterInstitution(i);
        dest.setExperimenterInstitution(institution, i);
      }
      catch (NullPointerException e) { }

      try {
        String lastName = src.getExperimenterLastName(i);
        dest.setExperimenterLastName(lastName, i);
      }
      catch (NullPointerException e) { }

      try {
        String middleName = src.getExperimenterMiddleName(i);
        dest.setExperimenterMiddleName(middleName, i);
      }
      catch (NullPointerException e) { }

      try {
        String userName = src.getExperimenterUserName(i);
        dest.setExperimenterUserName(userName, i);
      }
      catch (NullPointerException e) { }

      int annotationRefCount = 0;
      try {
        annotationRefCount = src.getExperimenterAnnotationRefCount(i);
      }
      catch (NullPointerException e) { }
      for (int q=0; q<annotationRefCount; q++) {
        try {
          String annotationRef = src.getExperimenterAnnotationRef(i, q);
          dest.setExperimenterAnnotationRef(annotationRef, i, q);
        }
        catch (NullPointerException e) { }
      }
    }
  }

  /**
   * Convert all ExperimenterGroup attributes.
   * @param src the MetadataRetrieve from which to copy
   * @param dest the MetadataStore to which to copy
   */
  private static void convertExperimenterGroups(MetadataRetrieve src,
    MetadataStore dest)
  {
    int experimenterGroupCount = 0;
    try {
      experimenterGroupCount = src.getExperimenterGroupCount();
    }
    catch (NullPointerException e) { }
    for (int i=0; i<experimenterGroupCount; i++) {
      try {
        String id = src.getExperimenterGroupID(i);
        dest.setExperimenterGroupID(id, i);
      }
      catch (NullPointerException e) {
        continue;
      }

      try {
        String description = src.getExperimenterGroupDescription(i);
        dest.setExperimenterGroupDescription(description, i);
      }
      catch (NullPointerException e) { }

      try {
        String name = src.getExperimenterGroupName(i);
        dest.setExperimenterGroupName(name, i);
      }
      catch (NullPointerException e) { }

      int annotationRefCount = 0;
      try {
        annotationRefCount = src.getExperimenterGroupAnnotationRefCount(i);
      }
      catch (NullPointerException e) { }
      for (int q=0; q<annotationRefCount; q++) {
        try {
          String annotationRef = src.getExperimenterGroupAnnotationRef(i, q);
          dest.setExperimenterGroupAnnotationRef(annotationRef, i, q);
        }
        catch (NullPointerException e) { }
      }

      int experimenterRefCount = 0;
      try {
        experimenterRefCount = src.getExperimenterGroupExperimenterRefCount(i);
      }
      catch (NullPointerException e) { }
      for (int q=0; q<experimenterRefCount; q++) {
        try {
          String experimenterRef = src.getExperimenterGroupExperimenterRef(i, q);
          dest.setExperimenterGroupExperimenterRef(experimenterRef, i, q);
        }
        catch (NullPointerException e) { }
      }

      int leaderCount = 0;
      try {
        leaderCount = src.getLeaderCount(i);
      }
      catch (NullPointerException e) { }
      for (int q=0; q<leaderCount; q++) {
        try {
          String leader = src.getExperimenterGroupLeader(i, q);
          dest.setExperimenterGroupLeader(leader, i, q);
        }
        catch (NullPointerException e) { }
      }
    }
  }

  /**
   * Convert all FileAnnotation attributes.
   * @param src the MetadataRetrieve from which to copy
   * @param dest the MetadataStore to which to copy
   */
  private static void convertFileAnnotations(MetadataRetrieve src,
    MetadataStore dest)
  {
    int fileAnnotationCount = 0;
    try {
      fileAnnotationCount = src.getFileAnnotationCount();
    }
    catch (NullPointerException e) { }
    for (int i=0; i<fileAnnotationCount; i++) {
      try {
        String id = src.getFileAnnotationID(i);
        dest.setFileAnnotationID(id, i);
      }
      catch (NullPointerException e) {
        continue;
      }

      try {
        String description = src.getFileAnnotationDescription(i);
        dest.setFileAnnotationDescription(description, i);
      }
      catch (NullPointerException e) { }

      try {
        String namespace = src.getFileAnnotationNamespace(i);
        dest.setFileAnnotationNamespace(namespace, i);
      }
      catch (NullPointerException e) { }

      try {
        String annotator = src.getFileAnnotationAnnotator(i);
        dest.setFileAnnotationAnnotator(annotator, i);
      }
      catch (NullPointerException e) { }

      try {
        String fileName = src.getBinaryFileFileName(i);
        dest.setBinaryFileFileName(fileName, i);
      }
      catch (NullPointerException e) { }

      try {
        String mimeType = src.getBinaryFileMIMEType(i);
        dest.setBinaryFileMIMEType(mimeType, i);
      }
      catch (NullPointerException e) { }

      try {
        NonNegativeLong fileSize = src.getBinaryFileSize(i);
        dest.setBinaryFileSize(fileSize, i);
      }
      catch (NullPointerException e) { }

      try {
        byte[] binData = src.getBinaryFileBinData(i);
        dest.setBinaryFileBinData(binData, i);
      }
      catch (NullPointerException e) { }

      try {
        boolean bigEndian = src.getBinaryFileBinDataBigEndian(i);
        dest.setBinaryFileBinDataBigEndian(bigEndian, i);
      }
      catch (NullPointerException e) { }

      try {
        NonNegativeLong length = src.getBinaryFileBinDataLength(i);
        dest.setBinaryFileBinDataLength(length, i);
      }
      catch (NullPointerException e) { }

      try {
        Compression compression = src.getBinaryFileBinDataCompression(i);
        dest.setBinaryFileBinDataCompression(compression, i);
      }
      catch (NullPointerException e) { }

      int annotationRefCount = 0;
      try {
        annotationRefCount = src.getFileAnnotationAnnotationCount(i);
      }
      catch (NullPointerException e) { }
      for (int a=0; a<annotationRefCount; a++) {
        try {
          String id = src.getFileAnnotationAnnotationRef(i, a);
          dest.setFileAnnotationAnnotationRef(id, i, a);
        }
        catch (NullPointerException e) { }
      }
    }
  }

  /**
   * Convert all Folder attributes.
   * @param src the MetadataRetrieve from which to copy
   * @param dest the MetadataStore to which to copy
   */
  private static void convertFolders(MetadataRetrieve src, MetadataStore dest)
  {
    int folders = 0;
    try {
      folders = src.getFolderCount();
    }
    catch (NullPointerException e) { }

    for (int i=0; i<folders; i++) {
      try {
        String id = src.getFolderID(i);
        dest.setFolderID(id, i);
      }
      catch (NullPointerException e) {
        continue;
      }

      try {
        String description = src.getFolderDescription(i);
        dest.setFolderDescription(description, i);
      }
      catch (NullPointerException e) { }

      try {
        String name = src.getFolderName(i);
        dest.setFolderName(name, i);
      }
      catch (NullPointerException e) { }

      try {
        int folderRefCount = src.getFolderRefCount(i);
        for (int q=0; q<folderRefCount; q++) {
          try {
            String folderRef = src.getFolderFolderRef(i, q);
            dest.setFolderFolderRef(folderRef, i, q);
          }
          catch (NullPointerException e) { }
        }
      }
      catch (NullPointerException e) { }

      try {
        int imageRefCount = src.getFolderImageRefCount(i);
        for (int q=0; q<imageRefCount; q++) {
          try {
            String imageRef = src.getFolderImageRef(i, q);
            dest.setFolderImageRef(imageRef, i, q);
          }
          catch (NullPointerException e) { }
        }
      }
      catch (NullPointerException e) { }

      try {
        int roiRefCount = src.getFolderROIRefCount(i);
        for (int q=0; q<roiRefCount; q++) {
          try {
            String roiRef = src.getFolderROIRef(i, q);
            dest.setFolderROIRef(roiRef, i, q);
          }
          catch (NullPointerException e) { }
        }
      }
      catch (NullPointerException e) { }

      try {
        int annotationRefCount = src.getFolderAnnotationRefCount(i);
        for (int q=0; q<annotationRefCount; q++) {
          try {
            String annotationRef = src.getFolderAnnotationRef(i, q);
            dest.setFolderAnnotationRef(annotationRef, i, q);
          }
          catch (NullPointerException e) { }
        }
      }
      catch (NullPointerException e) { }
    }
  }

  /**
   * Convert all Image attributes.
   * @param src the MetadataRetrieve from which to copy
   * @param dest the MetadataStore to which to copy
   * @param lightSourceIds the collection of light source identifiers.
   */
  private static void convertImages(MetadataRetrieve src, MetadataStore dest,
    List<String> lightSourceIds) {
    int imageCount = 0;
    try {
      imageCount = src.getImageCount();
    }
    catch (NullPointerException e) { }
    for (int i=0; i<imageCount; i++) {
      try {
        String id = src.getImageID(i);
        dest.setImageID(id, i);
      }
      catch (NullPointerException e) {
        continue;
      }

      try {
        Timestamp date = src.getImageAcquisitionDate(i);
        dest.setImageAcquisitionDate(date, i);
      }
      catch (NullPointerException e) { }

      try {
        String description = src.getImageDescription(i);
        dest.setImageDescription(description, i);
      }
      catch (NullPointerException e) { }

      try {
        String experimentRef = src.getImageExperimentRef(i);
        dest.setImageExperimentRef(experimentRef, i);
      }
      catch (NullPointerException e) { }

      try {
        String experimenterGroupRef = src.getImageExperimenterGroupRef(i);
        dest.setImageExperimenterGroupRef(experimenterGroupRef, i);
      }
      catch (NullPointerException e) { }

      try {
        String experimenterRef = src.getImageExperimenterRef(i);
        dest.setImageExperimenterRef(experimenterRef, i);
      }
      catch (NullPointerException e) { }

      try {
        String instrumentRef = src.getImageInstrumentRef(i);
        dest.setImageInstrumentRef(instrumentRef, i);
      }
      catch (NullPointerException e) { }

      try {
        String name = src.getImageName(i);
        dest.setImageName(name, i);
      }
      catch (NullPointerException e) { }

      try {
        Pressure airPressure = src.getImagingEnvironmentAirPressure(i);
        dest.setImagingEnvironmentAirPressure(airPressure, i);
      }
      catch (NullPointerException e) { }

      try {
        PercentFraction co2 = src.getImagingEnvironmentCO2Percent(i);
        dest.setImagingEnvironmentCO2Percent(co2, i);
      }
      catch (NullPointerException e) { }

      try {
        PercentFraction humidity = src.getImagingEnvironmentHumidity(i);
        dest.setImagingEnvironmentHumidity(humidity, i);
      }
      catch (NullPointerException e) { }

      try {
        List<MapPair> map = src.getImagingEnvironmentMap(i);
        dest.setImagingEnvironmentMap(map, i);
      }
      catch (NullPointerException e) { }

      try {
        Temperature temperature = src.getImagingEnvironmentTemperature(i);
        dest.setImagingEnvironmentTemperature(temperature, i);
      }
      catch (NullPointerException e) { }

      try {
        String objectiveID = src.getObjectiveSettingsID(i);
        if (objectiveID != null) {
          dest.setObjectiveSettingsID(objectiveID, i);

          try {
            Double correction = src.getObjectiveSettingsCorrectionCollar(i);
            dest.setObjectiveSettingsCorrectionCollar(correction, i);
          }
          catch (NullPointerException e) { }

          try {
            Medium medium = src.getObjectiveSettingsMedium(i);
            dest.setObjectiveSettingsMedium(medium, i);
          }
          catch (NullPointerException e) { }

          try {
            Double refractiveIndex = src.getObjectiveSettingsRefractiveIndex(i);
            dest.setObjectiveSettingsRefractiveIndex(refractiveIndex, i);
          }
          catch (NullPointerException e) { }

        }
      }
      catch (NullPointerException e) { }

      try {
        String stageLabelName = src.getStageLabelName(i);
        if (stageLabelName != null) {
          dest.setStageLabelName(stageLabelName, i);

          try {
            final Length stageLabelX = src.getStageLabelX(i);
            dest.setStageLabelX(stageLabelX, i);
          }
          catch (NullPointerException e) { }

          try {
            final Length stageLabelY = src.getStageLabelY(i);
            dest.setStageLabelY(stageLabelY, i);
          }
          catch (NullPointerException e) { }

          try {
            final Length stageLabelZ = src.getStageLabelZ(i);
            dest.setStageLabelZ(stageLabelZ, i);
          }
          catch (NullPointerException e) { }
        }
      }
      catch (NullPointerException e) { }

      try {
        String pixelsID = src.getPixelsID(i);
        dest.setPixelsID(pixelsID, i);

        try {
          DimensionOrder order = src.getPixelsDimensionOrder(i);
          dest.setPixelsDimensionOrder(order, i);
        }
        catch (NullPointerException e) { }

        try {
          Length physicalSizeX = src.getPixelsPhysicalSizeX(i);
          dest.setPixelsPhysicalSizeX(physicalSizeX, i);
        }
        catch (NullPointerException e) { }

        try {
          Length physicalSizeY = src.getPixelsPhysicalSizeY(i);
          dest.setPixelsPhysicalSizeY(physicalSizeY, i);
        }
        catch (NullPointerException e) { }

        try {
          Length physicalSizeZ = src.getPixelsPhysicalSizeZ(i);
          dest.setPixelsPhysicalSizeZ(physicalSizeZ, i);
        }
        catch (NullPointerException e) { }

        try {
          PositiveInteger sizeC = src.getPixelsSizeC(i);
          dest.setPixelsSizeC(sizeC, i);
        }
        catch (NullPointerException e) { }

        try {
          PositiveInteger sizeT = src.getPixelsSizeT(i);
          dest.setPixelsSizeT(sizeT, i);
        }
        catch (NullPointerException e) { }

        try {
          PositiveInteger sizeX = src.getPixelsSizeX(i);
          dest.setPixelsSizeX(sizeX, i);
        }
        catch (NullPointerException e) { }

        try {
          PositiveInteger sizeY = src.getPixelsSizeY(i);
          dest.setPixelsSizeY(sizeY, i);
        }
        catch (NullPointerException e) { }

        try {
          PositiveInteger sizeZ = src.getPixelsSizeZ(i);
          dest.setPixelsSizeZ(sizeZ, i);
        }
        catch (NullPointerException e) { }

        try {
          Time timeIncrement = src.getPixelsTimeIncrement(i);
          dest.setPixelsTimeIncrement(timeIncrement, i);
        }
        catch (NullPointerException e) { }

        try {
          PixelType type = src.getPixelsType(i);
          dest.setPixelsType(type, i);
        }
        catch (NullPointerException e) { }

        try {
          Boolean bigEndian = src.getPixelsBigEndian(i);
          dest.setPixelsBigEndian(bigEndian, i);
        }
        catch (NullPointerException e) { }

        try {
          Boolean interleaved = src.getPixelsInterleaved(i);
          dest.setPixelsInterleaved(interleaved, i);
        }
        catch (NullPointerException e) { }

        try {
          PositiveInteger significantBits = src.getPixelsSignificantBits(i);
          dest.setPixelsSignificantBits(significantBits, i);
        }
        catch (NullPointerException e) { }

        int binDataCount = 0;
        try {
          binDataCount = src.getPixelsBinDataCount(i);
        }
        catch (NullPointerException e) { }
        for (int q=0; q<binDataCount; q++) {
          try {
            Boolean bigEndian = src.getPixelsBinDataBigEndian(i, q);
            dest.setPixelsBinDataBigEndian(bigEndian, i, q);
          }
          catch (NullPointerException e) { }
          try {
            NonNegativeLong length = src.getPixelsBinDataLength(i, q);
            dest.setPixelsBinDataLength(length, i, q);
          }
          catch (NullPointerException e) { }
          try {
            Compression compression = src.getPixelsBinDataCompression(i, q);
            dest.setPixelsBinDataCompression(compression, i, q);
          }
          catch (NullPointerException e) { }
          try {
            byte[] data = src.getPixelsBinData(i, q);
            dest.setPixelsBinData(data, i, q);
          }
          catch (NullPointerException e) { }
        }
      }
      catch (NullPointerException e) { }

      int annotationRefCount = 0;
      try {
        annotationRefCount = src.getImageAnnotationRefCount(i);
      }
      catch (NullPointerException e) { }
      for (int q=0; q<annotationRefCount; q++) {
        try {
          String annotationRef = src.getImageAnnotationRef(i, q);
          dest.setImageAnnotationRef(annotationRef, i, q);
        }
        catch (NullPointerException e) { }
      }

      int channelCount = 0;
      try {
        channelCount = src.getChannelCount(i);
      }
      catch (NullPointerException e) { }
      for (int c=0; c<channelCount; c++) {
        try {
          String channelID = src.getChannelID(i, c);
          dest.setChannelID(channelID, i, c);
        }
        catch (NullPointerException e) {
          continue;
        }

        try {
          AcquisitionMode mode = src.getChannelAcquisitionMode(i, c);
          dest.setChannelAcquisitionMode(mode, i, c);
        }
        catch (NullPointerException e) { }

        try {
          Color color = src.getChannelColor(i, c);
          dest.setChannelColor(color, i, c);
        }
        catch (NullPointerException e) { }

        try {
          ContrastMethod method = src.getChannelContrastMethod(i, c);
          dest.setChannelContrastMethod(method, i, c);
        }
        catch (NullPointerException e) { }

        try {
          Length emWave = src.getChannelEmissionWavelength(i, c);
          dest.setChannelEmissionWavelength(emWave, i, c);
        }
        catch (NullPointerException e) { }

        try {
          Length exWave = src.getChannelExcitationWavelength(i, c);
          dest.setChannelExcitationWavelength(exWave, i, c);
        }
        catch (NullPointerException e) { }

        try {
          String filterSetRef = src.getChannelFilterSetRef(i, c);
          dest.setChannelFilterSetRef(filterSetRef, i, c);
        }
        catch (NullPointerException e) { }

        try {
          String fluor = src.getChannelFluor(i, c);
          dest.setChannelFluor(fluor, i, c);
        }
        catch (NullPointerException e) { }

        try {
          IlluminationType illumType = src.getChannelIlluminationType(i, c);
          dest.setChannelIlluminationType(illumType, i, c);
        }
        catch (NullPointerException e) { }

        try {
          Double ndFilter = src.getChannelNDFilter(i, c);
          dest.setChannelNDFilter(ndFilter, i, c);
        }
        catch (NullPointerException e) { }

        try {
          String channelName = src.getChannelName(i, c);
          dest.setChannelName(channelName, i, c);
        }
        catch (NullPointerException e) { }

        try {
          Length pinholeSize = src.getChannelPinholeSize(i, c);
          dest.setChannelPinholeSize(pinholeSize, i, c);
        }
        catch (NullPointerException e) { }

        try {
          Integer pockelCell = src.getChannelPockelCellSetting(i, c);
          dest.setChannelPockelCellSetting(pockelCell, i, c);
        }
        catch (NullPointerException e) { }

        try {
          PositiveInteger samplesPerPixel = src.getChannelSamplesPerPixel(i, c);
          dest.setChannelSamplesPerPixel(samplesPerPixel, i, c);
        }
        catch (NullPointerException e) { }

        try {
          String detectorSettingsID = src.getDetectorSettingsID(i, c);
          if (detectorSettingsID != null) {
            dest.setDetectorSettingsID(detectorSettingsID, i, c);

            try {
              Binning binning = src.getDetectorSettingsBinning(i, c);
              dest.setDetectorSettingsBinning(binning, i, c);
            }
            catch (NullPointerException e) { }

            try {
              Double gain = src.getDetectorSettingsGain(i, c);
              dest.setDetectorSettingsGain(gain, i, c);
            }
            catch (NullPointerException e) { }

            try {
              PositiveInteger integration =
                src.getDetectorSettingsIntegration(i, c);
              dest.setDetectorSettingsIntegration(integration, i, c);
            }
            catch (NullPointerException e) { }

            try {
              Double offset = src.getDetectorSettingsOffset(i, c);
              dest.setDetectorSettingsOffset(offset, i, c);
            }
            catch (NullPointerException e) { }

            try {
              Frequency readOutRate = src.getDetectorSettingsReadOutRate(i, c);
              dest.setDetectorSettingsReadOutRate(readOutRate, i, c);
            }
            catch (NullPointerException e) { }

            try {
              ElectricPotential voltage = src.getDetectorSettingsVoltage(i, c);
              dest.setDetectorSettingsVoltage(voltage, i, c);
            }
            catch (NullPointerException e) { }

            try {
              Double zoom = src.getDetectorSettingsZoom(i, c);
              dest.setDetectorSettingsZoom(zoom, i, c);
            }
            catch (NullPointerException e) { }
          }
        }
        catch (NullPointerException e) { }

        try {
          String dichroicRef = src.getLightPathDichroicRef(i, c);
          dest.setLightPathDichroicRef(dichroicRef, i, c);
        }
        catch (NullPointerException e) { }

        try {
          String lightSourceID = src.getChannelLightSourceSettingsID(i, c);
          if (lightSourceID != null && lightSourceIds.contains(lightSourceID)) {
            dest.setChannelLightSourceSettingsID(lightSourceID, i, c);

            try {
              PercentFraction attenuation =
                src.getChannelLightSourceSettingsAttenuation(i, c);
              dest.setChannelLightSourceSettingsAttenuation(attenuation, i, c);
            }
            catch (NullPointerException e) { }

            try {
              Length wavelength = src.getChannelLightSourceSettingsWavelength(i, c);
              dest.setChannelLightSourceSettingsWavelength(wavelength, i, c);
            }
            catch (NullPointerException e) { }
          }
        }
        catch (NullPointerException e) { }

        int channelAnnotationRefCount = 0;
        try {
          channelAnnotationRefCount = src.getChannelAnnotationRefCount(i, c);
        }
        catch (NullPointerException e) { }
        for (int q=0; q<channelAnnotationRefCount; q++) {
          try {
            String channelAnnotationRef = src.getChannelAnnotationRef(i, c, q);
            dest.setChannelAnnotationRef(channelAnnotationRef, i, c, q);
          }
          catch (NullPointerException e) { }
        }

        int emFilterRefCount = 0;
        try {
          emFilterRefCount = src.getLightPathEmissionFilterRefCount(i, c);
        }
        catch (NullPointerException e) { }
        for (int q=0; q<emFilterRefCount; q++) {
          try {
            String emFilterRef = src.getLightPathEmissionFilterRef(i, c, q);
            dest.setLightPathEmissionFilterRef(emFilterRef, i, c, q);
          }
          catch (NullPointerException e) { }
        }

        int exFilterRefCount = 0;
        try {
          exFilterRefCount = src.getLightPathExcitationFilterRefCount(i, c);
        }
        catch (NullPointerException e) { }
        for (int q=0; q<exFilterRefCount; q++) {
          try {
            String exFilterRef = src.getLightPathExcitationFilterRef(i, c, q);
            dest.setLightPathExcitationFilterRef(exFilterRef, i, c, q);
          }
          catch (NullPointerException e) { }
        }

        int lightPathAnnotationRefCount = 0;
        try {
          lightPathAnnotationRefCount = src.getLightPathAnnotationRefCount(i, c);
        }
        catch (NullPointerException e) { }
        for (int q=0; q<lightPathAnnotationRefCount; q++) {
          try {
            String lightPathAnnotationRef = src.getLightPathAnnotationRef(i, c, q);
            dest.setLightPathAnnotationRef(lightPathAnnotationRef, i, c, q);
          }
          catch (NullPointerException e) { }
        }
      }

      int planeCount = 0;
      try {
        planeCount = src.getPlaneCount(i);
      }
      catch (NullPointerException e) { }
      for (int p=0; p<planeCount; p++) {
        try {
          Time deltaT = src.getPlaneDeltaT(i, p);
          dest.setPlaneDeltaT(deltaT, i, p);
        }
        catch (NullPointerException e) { }

        try {
          Time exposureTime = src.getPlaneExposureTime(i, p);
          dest.setPlaneExposureTime(exposureTime, i, p);
        }
        catch (NullPointerException e) { }

        try {
          String sha1 = src.getPlaneHashSHA1(i, p);
          dest.setPlaneHashSHA1(sha1, i, p);
        }
        catch (NullPointerException e) { }

        try {
          final Length positionX = src.getPlanePositionX(i, p);
          dest.setPlanePositionX(positionX, i, p);
        }
        catch (NullPointerException e) { }

        try {
          final Length positionY = src.getPlanePositionY(i, p);
          dest.setPlanePositionY(positionY, i, p);
        }
        catch (NullPointerException e) { }

        try {
          final Length positionZ = src.getPlanePositionZ(i, p);
          dest.setPlanePositionZ(positionZ, i, p);
        }
        catch (NullPointerException e) { }

        try {
          NonNegativeInteger theC = src.getPlaneTheC(i, p);
          dest.setPlaneTheC(theC, i, p);
        }
        catch (NullPointerException e) { }

        try {
          NonNegativeInteger theT = src.getPlaneTheT(i, p);
          dest.setPlaneTheT(theT, i, p);
        }
        catch (NullPointerException e) { }

        try {
          NonNegativeInteger theZ = src.getPlaneTheZ(i, p);
          dest.setPlaneTheZ(theZ, i, p);
        }
        catch (NullPointerException e) { }

        int planeAnnotationRefCount = 0;
        try {
          planeAnnotationRefCount = src.getPlaneAnnotationRefCount(i, p);
        }
        catch (NullPointerException e) { }
        for (int q=0; q<planeAnnotationRefCount; q++) {
          try {
            String planeAnnotationRef = src.getPlaneAnnotationRef(i, p, q);
            dest.setPlaneAnnotationRef(planeAnnotationRef, i, p, q);
          }
          catch (NullPointerException e) { }
        }
      }

      int microbeamCount = 0;
      try {
        microbeamCount = src.getMicrobeamManipulationRefCount(i);
      }
      catch (NullPointerException e) { }
      for (int q=0; q<microbeamCount; q++) {
        try {
          String microbeamRef = src.getImageMicrobeamManipulationRef(i, q);
          dest.setImageMicrobeamManipulationRef(microbeamRef, i, q);
        }
        catch (NullPointerException e) { }
      }

      int roiRefCount = 0;
      try {
        roiRefCount = src.getImageROIRefCount(i);
      }
      catch (NullPointerException e) { }
      for (int q=0; q<roiRefCount; q++) {
        try {
          String roiRef = src.getImageROIRef(i, q);
          dest.setImageROIRef(roiRef, i, q);
        }
        catch (NullPointerException e) { }
      }

      int tiffDataCount = 0;
      try {
        tiffDataCount = src.getTiffDataCount(i);
      }
      catch (NullPointerException e) { }
      for (int q=0; q<tiffDataCount; q++) {
        try {
          String uuid = src.getUUIDValue(i, q);
          dest.setUUIDValue(uuid, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String filename = src.getUUIDFileName(i, q);
          dest.setUUIDFileName(filename, i, q);
        }
        catch (NullPointerException e) { }

        try {
          NonNegativeInteger firstC = src.getTiffDataFirstC(i, q);
          dest.setTiffDataFirstC(firstC, i, q);
        }
        catch (NullPointerException e) { }

        try {
          NonNegativeInteger firstT = src.getTiffDataFirstT(i, q);
          dest.setTiffDataFirstT(firstT, i, q);
        }
        catch (NullPointerException e) { }

        try {
          NonNegativeInteger firstZ = src.getTiffDataFirstZ(i, q);
          dest.setTiffDataFirstZ(firstZ, i, q);
        }
        catch (NullPointerException e) { }

        try {
          NonNegativeInteger ifd = src.getTiffDataIFD(i, q);
          dest.setTiffDataIFD(ifd, i, q);
        }
        catch (NullPointerException e) { }

        try {
          NonNegativeInteger tiffDataPlaneCount = src.getTiffDataPlaneCount(i, q);
          dest.setTiffDataPlaneCount(tiffDataPlaneCount, i, q);
        }
        catch (NullPointerException e) { }
      }
    }
  }

  /**
   * Convert all Instrument attributes.
   * @param src the MetadataRetrieve from which to copy
   * @param dest the MetadataStore to which to copy
   * @return Collection of light source identifiers.
   */
  private static List<String> convertInstruments(MetadataRetrieve src,
    MetadataStore dest)
  {
    List<String> lightSourceIds = new ArrayList<String>();
    int instrumentCount = 0;
    try {
      instrumentCount = src.getInstrumentCount();
    }
    catch (NullPointerException e) { }
    for (int i=0; i<instrumentCount; i++) {
      try {
        String id = src.getInstrumentID(i);
        dest.setInstrumentID(id, i);
      }
      catch (NullPointerException e) {
        continue;
      }

      try {
        String microscopeLotNumber = src.getMicroscopeLotNumber(i);
        dest.setMicroscopeLotNumber(microscopeLotNumber, i);
      }
      catch (NullPointerException e) { }

      try {
        String microscopeManufacturer = src.getMicroscopeManufacturer(i);
        dest.setMicroscopeManufacturer(microscopeManufacturer, i);
      }
      catch (NullPointerException e) { }

      try {
        String microscopeModel = src.getMicroscopeModel(i);
        dest.setMicroscopeModel(microscopeModel, i);
      }
      catch (NullPointerException e) { }

      try {
        String microscopeSerialNumber = src.getMicroscopeSerialNumber(i);
        dest.setMicroscopeSerialNumber(microscopeSerialNumber, i);
      }
      catch (NullPointerException e) { }

      try {
        MicroscopeType microscopeType = src.getMicroscopeType(i);
        dest.setMicroscopeType(microscopeType, i);
      }
      catch (NullPointerException e) { }

      int detectorCount = 0;
      try {
        detectorCount = src.getDetectorCount(i);
      }
      catch (NullPointerException e) { }
      for (int q=0; q<detectorCount; q++) {
        try {
          String detectorID = src.getDetectorID(i, q);
          dest.setDetectorID(detectorID, i, q);
        }
        catch (NullPointerException e) {
          continue;
        }

        try {
          Double amplificationGain = src.getDetectorAmplificationGain(i, q);
          dest.setDetectorAmplificationGain(amplificationGain, i, q);
        }
        catch (NullPointerException e) { }

        try {
          Double gain = src.getDetectorGain(i, q);
          dest.setDetectorGain(gain, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String lotNumber = src.getDetectorLotNumber(i, q);
          dest.setDetectorLotNumber(lotNumber, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String manufacturer = src.getDetectorManufacturer(i, q);
          dest.setDetectorManufacturer(manufacturer, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String model = src.getDetectorModel(i, q);
          dest.setDetectorModel(model, i, q);
        }
        catch (NullPointerException e) { }

        try {
          Double offset = src.getDetectorOffset(i, q);
          dest.setDetectorOffset(offset, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String serialNumber = src.getDetectorSerialNumber(i, q);
          dest.setDetectorSerialNumber(serialNumber, i, q);
        }
        catch (NullPointerException e) { }

        try {
          DetectorType detectorType = src.getDetectorType(i, q);
          dest.setDetectorType(detectorType, i, q);
        }
        catch (NullPointerException e) { }

        try {
          ElectricPotential voltage = src.getDetectorVoltage(i, q);
          dest.setDetectorVoltage(voltage, i, q);
        }
        catch (NullPointerException e) { }

        try {
          Double zoom = src.getDetectorZoom(i, q);
          dest.setDetectorZoom(zoom, i, q);
        }
        catch (NullPointerException e) { }

        int detectorAnnotationRefCount = 0;
        try {
          detectorAnnotationRefCount = src.getDetectorAnnotationRefCount(i, q);
        }
        catch (NullPointerException e) { }
        for (int r=0; r<detectorAnnotationRefCount; r++) {
          try {
            String detectorAnnotationRef = src.getDetectorAnnotationRef(i, q, r);
            dest.setDetectorAnnotationRef(detectorAnnotationRef, i, q, r);
          }
          catch (NullPointerException e) { }
        }
      }

      int dichroicCount = 0;
      try {
        dichroicCount = src.getDichroicCount(i);
      }
      catch (NullPointerException e) { }
      for (int q=0; q<dichroicCount; q++) {
        try {
          String dichroicID = src.getDichroicID(i, q);
          dest.setDichroicID(dichroicID, i, q);
        }
        catch (NullPointerException e) {
          continue;
        }

        try {
          String lotNumber = src.getDichroicLotNumber(i, q);
          dest.setDichroicLotNumber(lotNumber, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String manufacturer = src.getDichroicManufacturer(i, q);
          dest.setDichroicManufacturer(manufacturer, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String model = src.getDichroicModel(i, q);
          dest.setDichroicModel(model, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String serialNumber = src.getDichroicSerialNumber(i, q);
          dest.setDichroicSerialNumber(serialNumber, i, q);
        }
        catch (NullPointerException e) { }

        int dichroicAnnotationRefCount = 0;
        try {
          dichroicAnnotationRefCount = src.getDichroicAnnotationRefCount(i,q);
        }
        catch (NullPointerException e) { }
        for (int r=0; r<dichroicAnnotationRefCount; r++) {
          try {
            String dichroicAnnotationRef = src.getDichroicAnnotationRef(i, q, r);
            dest.setDichroicAnnotationRef(dichroicAnnotationRef, i, q, r);
          }
          catch (NullPointerException e) { }
        }
      }

      int filterCount = 0;
      try {
        filterCount = src.getFilterCount(i);
      }
      catch (NullPointerException e) { }
      for (int q=0; q<filterCount; q++) {
        try {
          String filterID = src.getFilterID(i, q);
          dest.setFilterID(filterID, i, q);
        }
        catch (NullPointerException e) {
          continue;
        }

        try {
          String filterWheel = src.getFilterFilterWheel(i, q);
          dest.setFilterFilterWheel(filterWheel, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String lotNumber = src.getFilterLotNumber(i, q);
          dest.setFilterLotNumber(lotNumber, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String manufacturer = src.getFilterManufacturer(i, q);
          dest.setFilterManufacturer(manufacturer, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String model = src.getFilterModel(i, q);
          dest.setFilterModel(model, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String serialNumber = src.getFilterSerialNumber(i, q);
          dest.setFilterSerialNumber(serialNumber, i, q);
        }
        catch (NullPointerException e) { }

        try {
          FilterType filterType = src.getFilterType(i, q);
          dest.setFilterType(filterType, i, q);
        }
        catch (NullPointerException e) { }

        try {
          Length cutIn = src.getTransmittanceRangeCutIn(i, q);
          dest.setTransmittanceRangeCutIn(cutIn, i, q);
        }
        catch (NullPointerException e) { }

        try {
          Length cutInTolerance = src.getTransmittanceRangeCutInTolerance(i, q);
          dest.setTransmittanceRangeCutInTolerance(cutInTolerance, i, q);
        }
        catch (NullPointerException e) { }

        try {
          Length cutOut = src.getTransmittanceRangeCutOut(i, q);
          dest.setTransmittanceRangeCutOut(cutOut, i, q);
        }
        catch (NullPointerException e) { }

        try {
          Length cutOutTolerance = src.getTransmittanceRangeCutOutTolerance(i, q);
          dest.setTransmittanceRangeCutOutTolerance(cutOutTolerance, i, q);
        }
        catch (NullPointerException e) { }

        try {
          PercentFraction transmittance = src.getTransmittanceRangeTransmittance(i, q);
          dest.setTransmittanceRangeTransmittance(transmittance, i, q);
        }
        catch (NullPointerException e) { }

        int filterAnnotationRefCount = 0;
        try {
          filterAnnotationRefCount = src.getFilterAnnotationRefCount(i, q);
        }
        catch (NullPointerException e) { }
        for (int r=0; r<filterAnnotationRefCount; r++) {
          try {
            String filterAnnotationRef = src.getFilterAnnotationRef(i, q, r);
            dest.setFilterAnnotationRef(filterAnnotationRef, i, q, r);
          }
          catch (NullPointerException e) { }
        }
      }

      int objectiveCount = 0;
      try {
        objectiveCount = src.getObjectiveCount(i);
      }
      catch (NullPointerException e) { }
      for (int q=0; q<objectiveCount; q++) {
        try {
          String objectiveID = src.getObjectiveID(i, q);
          dest.setObjectiveID(objectiveID, i, q);
        }
        catch (NullPointerException e) {
          continue;
        }

        try {
          Double calibratedMag = src.getObjectiveCalibratedMagnification(i, q);
          dest.setObjectiveCalibratedMagnification(calibratedMag, i, q);
        }
        catch (NullPointerException e) { }

        try {
          Correction correction = src.getObjectiveCorrection(i, q);
          dest.setObjectiveCorrection(correction, i, q);
        }
        catch (NullPointerException e) { }

        try {
          Immersion immersion = src.getObjectiveImmersion(i, q);
          dest.setObjectiveImmersion(immersion, i, q);
        }
        catch (NullPointerException e) { }

        try {
          Boolean iris = src.getObjectiveIris(i, q);
          dest.setObjectiveIris(iris, i, q);
        }
        catch (NullPointerException e) { }

        try {
          Double lensNA = src.getObjectiveLensNA(i, q);
          dest.setObjectiveLensNA(lensNA, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String lotNumber = src.getObjectiveLotNumber(i, q);
          dest.setObjectiveLotNumber(lotNumber, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String manufacturer = src.getObjectiveManufacturer(i, q);
          dest.setObjectiveManufacturer(manufacturer, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String model = src.getObjectiveModel(i, q);
          dest.setObjectiveModel(model, i, q);
        }
        catch (NullPointerException e) { }

        try {
          Double nominalMag = src.getObjectiveNominalMagnification(i, q);
          dest.setObjectiveNominalMagnification(nominalMag, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String serialNumber = src.getObjectiveSerialNumber(i, q);
          dest.setObjectiveSerialNumber(serialNumber, i, q);
        }
        catch (NullPointerException e) { }

        try {
          Length workingDistance = src.getObjectiveWorkingDistance(i, q);
          dest.setObjectiveWorkingDistance(workingDistance, i, q);
        }
        catch (NullPointerException e) { }

        int objectiveAnnotationRefCount = 0;
        try {
          objectiveAnnotationRefCount = src.getObjectiveAnnotationRefCount(i, q);
        }
        catch (NullPointerException e) { }

        for (int r=0; r<objectiveAnnotationRefCount; r++) {
          try {
            String objectiveAnnotationRef = src.getObjectiveAnnotationRef(i, q, r);
            dest.setObjectiveAnnotationRef(objectiveAnnotationRef, i, q, r);
          }
          catch (NullPointerException e) { }
        }
      }

      int filterSetCount = 0;
      try {
        filterSetCount = src.getFilterSetCount(i);
      }
      catch (NullPointerException e) { }
      for (int q=0; q<filterSetCount; q++) {
        try {
          String filterSetID = src.getFilterSetID(i, q);
          dest.setFilterSetID(filterSetID, i, q);
        }
        catch (NullPointerException e) {
          continue;
        }

        try {
          String dichroicRef = src.getFilterSetDichroicRef(i, q);
          dest.setFilterSetDichroicRef(dichroicRef, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String lotNumber = src.getFilterSetLotNumber(i, q);
          dest.setFilterSetLotNumber(lotNumber, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String manufacturer = src.getFilterSetManufacturer(i, q);
          dest.setFilterSetManufacturer(manufacturer, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String model = src.getFilterSetModel(i, q);
          dest.setFilterSetModel(model, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String serialNumber = src.getFilterSetSerialNumber(i, q);
          dest.setFilterSetSerialNumber(serialNumber, i, q);
        }
        catch (NullPointerException e) { }

        int emFilterCount = 0;
        try {
          emFilterCount = src.getFilterSetEmissionFilterRefCount(i, q);
        }
        catch (NullPointerException e) { }
        for (int f=0; f<emFilterCount; f++) {
          try {
            String emFilterRef = src.getFilterSetEmissionFilterRef(i, q, f);
            dest.setFilterSetEmissionFilterRef(emFilterRef, i, q, f);
          }
          catch (NullPointerException e) { }
        }

        int exFilterCount = 0;
        try {
          exFilterCount = src.getFilterSetExcitationFilterRefCount(i, q);
        }
        catch (NullPointerException e) { }
        for (int f=0; f<exFilterCount; f++) {
          try {
            String exFilterRef = src.getFilterSetExcitationFilterRef(i, q, f);
            dest.setFilterSetExcitationFilterRef(exFilterRef, i, q, f);
          }
          catch (NullPointerException e) { }
        }
      }

      convertLightSources(src, dest, i, lightSourceIds);

      int instrumentRefCount = 0;
      try {
        instrumentRefCount = src.getInstrumentAnnotationRefCount(i);
      }
      catch (NullPointerException e) { }
      for (int r=0; r<instrumentRefCount; r++) {
        try {
          String id = src.getInstrumentAnnotationRef(i, r);
          dest.setInstrumentAnnotationRef(id, i, r);
        }
        catch (NullPointerException e) { }
      }
    }
    return lightSourceIds;
  }

  /**
   * Convert all ListAnnotation attributes.
   * @param src the MetadataRetrieve from which to copy
   * @param dest the MetadataStore to which to copy
   */
  private static void convertListAnnotations(MetadataRetrieve src,
    MetadataStore dest)
  {
    int listAnnotationCount = 0;
    try {
      listAnnotationCount = src.getListAnnotationCount();
    }
    catch (NullPointerException e) { }
    for (int i=0; i<listAnnotationCount; i++) {
      try {
        String id = src.getListAnnotationID(i);
        dest.setListAnnotationID(id, i);
      }
      catch (NullPointerException e) {
        continue;
      }

      try {
        String description = src.getListAnnotationDescription(i);
        dest.setListAnnotationDescription(description, i);
      }
      catch (NullPointerException e) { }

      try {
        String namespace = src.getListAnnotationNamespace(i);
        dest.setListAnnotationNamespace(namespace, i);
      }
      catch (NullPointerException e) { }

      try {
        String annotator = src.getListAnnotationAnnotator(i);
        dest.setListAnnotationAnnotator(annotator, i);
      }
      catch (NullPointerException e) { }

      int annotationRefCount = 0;
      try {
        annotationRefCount = src.getListAnnotationAnnotationCount(i);
      }
      catch (NullPointerException e) { }
      for (int a=0; a<annotationRefCount; a++) {
        try {
          String id = src.getListAnnotationAnnotationRef(i, a);
          dest.setListAnnotationAnnotationRef(id, i, a);
        }
        catch (NullPointerException e) { }
      }
    }
  }

  /**
   * Convert all LongAnnotation attributes.
   * @param src the MetadataRetrieve from which to copy
   * @param dest the MetadataStore to which to copy
   */
  private static void convertLongAnnotations(MetadataRetrieve src,
    MetadataStore dest)
  {
    int longAnnotationCount = 0;
    try {
      longAnnotationCount = src.getLongAnnotationCount();
    }
    catch (NullPointerException e) { }
    for (int i=0; i<longAnnotationCount; i++) {
      try {
        String id = src.getLongAnnotationID(i);
        dest.setLongAnnotationID(id, i);
      }
      catch (NullPointerException e) {
        continue;
      }

      try {
        String description = src.getLongAnnotationDescription(i);
        dest.setLongAnnotationDescription(description, i);
      }
      catch (NullPointerException e) { }

      try {
        String namespace = src.getLongAnnotationNamespace(i);
        dest.setLongAnnotationNamespace(namespace, i);
      }
      catch (NullPointerException e) { }

      try {
        Long value = src.getLongAnnotationValue(i);
        dest.setLongAnnotationValue(value, i);
      }
      catch (NullPointerException e) { }

      try {
        String annotator = src.getLongAnnotationAnnotator(i);
        dest.setLongAnnotationAnnotator(annotator, i);
      }
      catch (NullPointerException e) { }

      int annotationRefCount = 0;
      try {
        annotationRefCount = src.getLongAnnotationAnnotationCount(i);
      }
      catch (NullPointerException e) { }
      for (int a=0; a<annotationRefCount; a++) {
        try {
          String id = src.getLongAnnotationAnnotationRef(i, a);
          dest.setLongAnnotationAnnotationRef(id, i, a);
        }
        catch (NullPointerException e) { }
      }
    }
  }

  /**
   * Convert all MapAnnotation attributes.
   * @param src the MetadataRetrieve from which to copy
   * @param dest the MetadataStore to which to copy
   */
  private static void convertMapAnnotations(MetadataRetrieve src, MetadataStore dest)
  {
    int mapAnnotationCount = 0;
    try {
      mapAnnotationCount = src.getMapAnnotationCount();
    }
    catch (NullPointerException e) { }
    for (int i=0; i<mapAnnotationCount; i++) {
      try {
        String id = src.getMapAnnotationID(i);
        dest.setMapAnnotationID(id, i);
      }
      catch (NullPointerException e) {
        continue;
      }

      try {
        String description = src.getMapAnnotationDescription(i);
        dest.setMapAnnotationDescription(description, i);
      }
      catch (NullPointerException e) { }

      try {
        String namespace = src.getMapAnnotationNamespace(i);
        dest.setMapAnnotationNamespace(namespace, i);
      }
      catch (NullPointerException e) { }

      try {
        List<MapPair> value = src.getMapAnnotationValue(i);
        dest.setMapAnnotationValue(value, i);
      }
      catch (NullPointerException e) { }

      try {
        String annotator = src.getMapAnnotationAnnotator(i);
        dest.setMapAnnotationAnnotator(annotator, i);
      }
      catch (NullPointerException e) { }

      int annotationRefCount = 0;
      try {
        annotationRefCount = src.getMapAnnotationAnnotationCount(i);
      }
      catch (NullPointerException e) { }
      for (int a=0; a<annotationRefCount; a++) {
        try {
          String id = src.getMapAnnotationAnnotationRef(i, a);
          dest.setMapAnnotationAnnotationRef(id, i, a);
        }
        catch (NullPointerException e) { }
      }
    }
  }

  /**
   * Convert all Plate attributes.
   * @param src the MetadataRetrieve from which to copy
   * @param dest the MetadataStore to which to copy
   */
  private static void convertPlates(MetadataRetrieve src, MetadataStore dest) {
    int plateCount = 0;
    try {
      plateCount = src.getPlateCount();
    }
    catch (NullPointerException e) { }
    for (int i=0; i<plateCount; i++) {
      try {
        String id = src.getPlateID(i);
        dest.setPlateID(id, i);
      }
      catch (NullPointerException e) {
        continue;
      }

      try {
        NamingConvention columnConvention = src.getPlateColumnNamingConvention(i);
        dest.setPlateColumnNamingConvention(columnConvention, i);
      }
      catch (NullPointerException e) { }

      try {
        PositiveInteger columns = src.getPlateColumns(i);
        dest.setPlateColumns(columns, i);
      }
      catch (NullPointerException e) { }

      try {
        String description = src.getPlateDescription(i);
        dest.setPlateDescription(description, i);
      }
      catch (NullPointerException e) { }

      try {
        String externalID = src.getPlateExternalIdentifier(i);
        dest.setPlateExternalIdentifier(externalID, i);
      }
      catch (NullPointerException e) { }

      try {
        NonNegativeInteger fieldIndex = src.getPlateFieldIndex(i);
        dest.setPlateFieldIndex(fieldIndex, i);
      }
      catch (NullPointerException e) { }

      // NB: plate name is required in OMERO
      try {
        String name = src.getPlateName(i);
        if (name == null) {
          name = "";
        }
        dest.setPlateName(name, i);
      }
      catch (NullPointerException e) {
        dest.setPlateName("", i);
      }

      try {
        NamingConvention rowConvention = src.getPlateRowNamingConvention(i);
        dest.setPlateRowNamingConvention(rowConvention, i);
      }
      catch (NullPointerException e) { }

      try {
        PositiveInteger rows = src.getPlateRows(i);
        dest.setPlateRows(rows, i);
      }
      catch (NullPointerException e) { }

      try {
        String status = src.getPlateStatus(i);
        dest.setPlateStatus(status, i);
      }
      catch (NullPointerException e) { }

      try {
        Length wellOriginX = src.getPlateWellOriginX(i);
        dest.setPlateWellOriginX(wellOriginX, i);
      }
      catch (NullPointerException e) { }

      try {
        Length wellOriginY = src.getPlateWellOriginY(i);
        dest.setPlateWellOriginY(wellOriginY, i);
      }
      catch (NullPointerException e) { }

      int wellCount = 0;
      try {
        wellCount = src.getWellCount(i);
      }
      catch (NullPointerException e) { }
      for (int q=0; q<wellCount; q++) {
        try {
          String wellID = src.getWellID(i, q);
          dest.setWellID(wellID, i, q);
        }
        catch (NullPointerException e) {
          continue;
        }

        try {
          Color color = src.getWellColor(i, q);
          dest.setWellColor(color, i, q);
        }
        catch (NullPointerException e) { }

        try {
          NonNegativeInteger column = src.getWellColumn(i, q);
          dest.setWellColumn(column, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String externalDescription = src.getWellExternalDescription(i, q);
          dest.setWellExternalDescription(externalDescription, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String wellExternalID = src.getWellExternalIdentifier(i, q);
          dest.setWellExternalIdentifier(wellExternalID, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String reagentRef = src.getWellReagentRef(i, q);
          dest.setWellReagentRef(reagentRef, i, q);
        }
        catch (NullPointerException e) { }

        try {
          NonNegativeInteger row = src.getWellRow(i, q);
          dest.setWellRow(row, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String type = src.getWellType(i, q);
          dest.setWellType(type, i, q);
        }
        catch (NullPointerException e) { }

        int wellAnnotationRefCount = 0;
        try {
          src.getWellAnnotationRefCount(i, q);
        }
        catch (NullPointerException e) { }
        for (int a=0; a<wellAnnotationRefCount; a++) {
          try {
            String wellAnnotationRef = src.getWellAnnotationRef(i, q, a);
            dest.setWellAnnotationRef(wellAnnotationRef, i, q, a);
          }
          catch (NullPointerException e) { }
        }

        int wellSampleCount = 0;
        try {
          wellSampleCount = src.getWellSampleCount(i, q);
        }
        catch (NullPointerException e) { }
        for (int w=0; w<wellSampleCount; w++) {
          try {
            String wellSampleID = src.getWellSampleID(i, q, w);
            dest.setWellSampleID(wellSampleID, i, q, w);
          }
          catch (NullPointerException e) {
            continue;
          }

          try {
            NonNegativeInteger index = src.getWellSampleIndex(i, q, w);
            dest.setWellSampleIndex(index, i, q, w);
          }
          catch (NullPointerException e) { }

          try {
            String imageRef = src.getWellSampleImageRef(i, q, w);
            dest.setWellSampleImageRef(imageRef, i, q, w);
          }
          catch (NullPointerException e) { }

          try {
            Length positionX = src.getWellSamplePositionX(i, q, w);
            dest.setWellSamplePositionX(positionX, i, q, w);
          }
          catch (NullPointerException e) { }

          try {
            Length positionY = src.getWellSamplePositionY(i, q, w);
            dest.setWellSamplePositionY(positionY, i, q, w);
          }
          catch (NullPointerException e) { }

          try {
            Timestamp timepoint = src.getWellSampleTimepoint(i, q, w);
            dest.setWellSampleTimepoint(timepoint, i, q, w);
          }
          catch (NullPointerException e) { }
        }
      }

      int plateAcquisitionCount = 0;
      try {
        plateAcquisitionCount = src.getPlateAcquisitionCount(i);
      }
      catch (NullPointerException e) { }
      for (int q=0; q<plateAcquisitionCount; q++) {
        try {
          String plateAcquisitionID = src.getPlateAcquisitionID(i, q);
          dest.setPlateAcquisitionID(plateAcquisitionID, i, q);
        }
        catch (NullPointerException e) {
          continue;
        }

        try {
          String acquisitionDescription = src.getPlateAcquisitionDescription(i, q);
          dest.setPlateAcquisitionDescription(acquisitionDescription, i, q);
        }
        catch (NullPointerException e) { }

        try {
          Timestamp endTime = src.getPlateAcquisitionEndTime(i, q);
          dest.setPlateAcquisitionEndTime(endTime, i, q);
        }
        catch (NullPointerException e) { }

        try {
          PositiveInteger maximumFields = src.getPlateAcquisitionMaximumFieldCount(i, q);
          dest.setPlateAcquisitionMaximumFieldCount(maximumFields, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String acquisitionName = src.getPlateAcquisitionName(i, q);
          dest.setPlateAcquisitionName(acquisitionName, i, q);
        }
        catch (NullPointerException e) { }

        try {
          Timestamp startTime = src.getPlateAcquisitionStartTime(i, q);
          dest.setPlateAcquisitionStartTime(startTime, i, q);
        }
        catch (NullPointerException e) { }

        int plateAcquisitionAnnotationRefCount = 0;
        try {
          plateAcquisitionAnnotationRefCount = src.getPlateAcquisitionAnnotationRefCount(i, q);
        }
        catch (NullPointerException e) { }
        for (int a=0; a<plateAcquisitionAnnotationRefCount; a++) {
          try {
            String plateAcquisitionAnnotationRef = src.getPlateAcquisitionAnnotationRef(i, q, a);
            dest.setPlateAcquisitionAnnotationRef(plateAcquisitionAnnotationRef, i, q, a);
          }
          catch (NullPointerException e) { }
        }

        int wellSampleRefCount = 0;
        try {
          wellSampleRefCount = src.getWellSampleRefCount(i, q);
        }
        catch (NullPointerException e) { }
        for (int w=0; w<wellSampleRefCount; w++) {
          try {
            String wellSampleRef = src.getPlateAcquisitionWellSampleRef(i, q, w);
            dest.setPlateAcquisitionWellSampleRef(wellSampleRef, i, q, w);
          }
          catch (NullPointerException e) { }
        }
      }

      int plateAnnotationRefCount = 0;
      try {
        plateAnnotationRefCount = src.getPlateAnnotationRefCount(i);
      }
      catch (NullPointerException e) { }
      for (int q=0; q<plateAnnotationRefCount; q++) {
        try {
          String annotationRef = src.getPlateAnnotationRef(i, q);
          dest.setPlateAnnotationRef(annotationRef, i, q);
        }
        catch (NullPointerException e) { }
      }
    }
  }

  /**
   * Convert all Project attributes.
   * @param src the MetadataRetrieve from which to copy
   * @param dest the MetadataStore to which to copy
   */
  private static void convertProjects(MetadataRetrieve src, MetadataStore dest)
  {
    int projectCount = 0;
    try {
      projectCount = src.getProjectCount();
    }
    catch (NullPointerException e) { }
    for (int i=0; i<projectCount; i++) {
      try {
        String projectID = src.getProjectID(i);
        dest.setProjectID(projectID, i);
      }
      catch (NullPointerException e) {
        continue;
      }

      try {
        String description = src.getProjectDescription(i);
        dest.setProjectDescription(description, i);
      }
      catch (NullPointerException e) { }

      try {
        String experimenterGroupRef = src.getProjectExperimenterGroupRef(i);
        dest.setProjectExperimenterGroupRef(experimenterGroupRef, i);
      }
      catch (NullPointerException e) { }

      try {
        String experimenterRef = src.getProjectExperimenterRef(i);
        dest.setProjectExperimenterRef(experimenterRef, i);
      }
      catch (NullPointerException e) { }

      try {
        String name = src.getProjectName(i);
        dest.setProjectName(name, i);
      }
      catch (NullPointerException e) { }

      int annotationRefCount = 0;
      try {
        annotationRefCount = src.getProjectAnnotationRefCount(i);
      }
      catch (NullPointerException e) { }
      for (int q=0; q<annotationRefCount; q++) {
        try {
          String annotationRef = src.getProjectAnnotationRef(i, q);
          dest.setProjectAnnotationRef(annotationRef, i, q);
        }
        catch (NullPointerException e) { }
      }

      int datasetRefCount = 0;
      try {
        datasetRefCount = src.getDatasetRefCount(i);
      }
      catch (NullPointerException e) { }
      for (int q=0; q<datasetRefCount; q++) {
        try {
          String datasetRef = src.getProjectDatasetRef(i, q);
          dest.setProjectDatasetRef(datasetRef, i, q);
        }
        catch (NullPointerException e) { }
      }
    }
  }

  /**
   * Convert all ROI attributes.
   * @param src the MetadataRetrieve from which to copy
   * @param dest the MetadataStore to which to copy
   */
  private static void convertROIs(MetadataRetrieve src, MetadataStore dest) {
    int roiCount = 0;
    try {
      roiCount = src.getROICount();
    }
    catch (NullPointerException e) { }
    for (int i=0; i<roiCount; i++) {
      try {
        String id = src.getROIID(i);
        dest.setROIID(id, i);
      }
      catch (NullPointerException e) {
        continue;
      }

      try {
        String name = src.getROIName(i);
        dest.setROIName(name, i);
      }
      catch (NullPointerException e) { }

      try {
        String description = src.getROIDescription(i);
        dest.setROIDescription(description, i);
      }
      catch (NullPointerException e) { }

      int shapeCount = 0;
      try {
        shapeCount = src.getShapeCount(i);
      }
      catch (NullPointerException e) { }
      for (int q=0; q<shapeCount; q++) {
        String type = src.getShapeType(i, q);

        if (type.equals("Ellipse")) {
          try {
            String shapeID = src.getEllipseID(i, q);
            dest.setEllipseID(shapeID, i, q);
          }
          catch (NullPointerException e) {
            continue;
          }

          try {
            Color fillColor = src.getEllipseFillColor(i, q);
            dest.setEllipseFillColor(fillColor, i, q);
          }
          catch (NullPointerException e) { }

          try {
            FillRule fillRule = src.getEllipseFillRule(i, q);
            dest.setEllipseFillRule(fillRule, i, q);
          }
          catch (NullPointerException e) { }

          try {
            FontFamily fontFamily = src.getEllipseFontFamily(i, q);
            dest.setEllipseFontFamily(fontFamily, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Length fontSize = src.getEllipseFontSize(i, q);
            dest.setEllipseFontSize(fontSize, i, q);
          }
          catch (NullPointerException e) { }

          try {
            FontStyle fontStyle = src.getEllipseFontStyle(i, q);
            dest.setEllipseFontStyle(fontStyle, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Boolean locked = src.getEllipseLocked(i, q);
            dest.setEllipseLocked(locked, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Color strokeColor = src.getEllipseStrokeColor(i, q);
            dest.setEllipseStrokeColor(strokeColor, i, q);
          }
          catch (NullPointerException e) { }

          try {
            String dashArray = src.getEllipseStrokeDashArray(i, q);
            dest.setEllipseStrokeDashArray(dashArray, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Length strokeWidth = src.getEllipseStrokeWidth(i, q);
            dest.setEllipseStrokeWidth(strokeWidth, i, q);
          }
          catch (NullPointerException e) { }

          try {
            String text = src.getEllipseText(i, q);
            dest.setEllipseText(text, i, q);
          }
          catch (NullPointerException e) { }

          try {
            NonNegativeInteger theC = src.getEllipseTheC(i, q);
            dest.setEllipseTheC(theC, i, q);
          }
          catch (NullPointerException e) { }

          try {
            NonNegativeInteger theT = src.getEllipseTheT(i, q);
            dest.setEllipseTheT(theT, i, q);
          }
          catch (NullPointerException e) { }

          try {
            NonNegativeInteger theZ = src.getEllipseTheZ(i, q);
            dest.setEllipseTheZ(theZ, i, q);
          }
          catch (NullPointerException e) { }

          try {
            AffineTransform transform = src.getEllipseTransform(i, q);
            dest.setEllipseTransform(transform, i, q);
          }
          catch (NullPointerException e) { }

          try {
          Double radiusX = src.getEllipseRadiusX(i, q);
          dest.setEllipseRadiusX(radiusX, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Double radiusY = src.getEllipseRadiusY(i, q);
            dest.setEllipseRadiusY(radiusY, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Double x = src.getEllipseX(i, q);
            dest.setEllipseX(x, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Double y = src.getEllipseY(i, q);
            dest.setEllipseY(y, i, q);
          }
          catch (NullPointerException e) { }

          int shapeAnnotationRefCount = 0;
          try {
            shapeAnnotationRefCount = src.getShapeAnnotationRefCount(i, q);
          }
          catch (NullPointerException e) { }
          for (int r=0; r<shapeAnnotationRefCount; r++) {
            try {
              String shapeAnnotationRef = src.getEllipseAnnotationRef(i, q, r);
              dest.setEllipseAnnotationRef(shapeAnnotationRef, i, q, r);
            }
            catch (NullPointerException e) { }
          }
        }
        else if (type.equals("Label")) {
          try {
            String shapeID = src.getLabelID(i, q);
            dest.setLabelID(shapeID, i, q);
          }
          catch (NullPointerException e) {
            continue;
          }

          try {
            Color fillColor = src.getLabelFillColor(i, q);
            dest.setLabelFillColor(fillColor, i, q);
          }
          catch (NullPointerException e) { }

          try {
            FillRule fillRule = src.getLabelFillRule(i, q);
            dest.setLabelFillRule(fillRule, i, q);
          }
          catch (NullPointerException e) { }

          try {
            FontFamily fontFamily = src.getLabelFontFamily(i, q);
            dest.setLabelFontFamily(fontFamily, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Length fontSize = src.getLabelFontSize(i, q);
            dest.setLabelFontSize(fontSize, i, q);
          }
          catch (NullPointerException e) { }

          try {
            FontStyle fontStyle = src.getLabelFontStyle(i, q);
            dest.setLabelFontStyle(fontStyle, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Boolean locked = src.getLabelLocked(i, q);
            dest.setLabelLocked(locked, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Color strokeColor = src.getLabelStrokeColor(i, q);
            dest.setLabelStrokeColor(strokeColor, i, q);
          }
          catch (NullPointerException e) { }

          try {
            String dashArray = src.getLabelStrokeDashArray(i, q);
            dest.setLabelStrokeDashArray(dashArray, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Length strokeWidth = src.getLabelStrokeWidth(i, q);
            dest.setLabelStrokeWidth(strokeWidth, i, q);
          }
          catch (NullPointerException e) { }

          try {
            String text = src.getLabelText(i, q);
            dest.setLabelText(text, i, q);
          }
          catch (NullPointerException e) { }

          try {
            NonNegativeInteger theC = src.getLabelTheC(i, q);
            dest.setLabelTheC(theC, i, q);
          }
          catch (NullPointerException e) { }

          try {
            NonNegativeInteger theT = src.getLabelTheT(i, q);
            dest.setLabelTheT(theT, i, q);
          }
          catch (NullPointerException e) { }

          try {
            NonNegativeInteger theZ = src.getLabelTheZ(i, q);
            dest.setLabelTheZ(theZ, i, q);
          }
          catch (NullPointerException e) { }

          try {
            AffineTransform transform = src.getLabelTransform(i, q);
            dest.setLabelTransform(transform, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Double x = src.getLabelX(i, q);
            dest.setLabelX(x, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Double y = src.getLabelY(i, q);
            dest.setLabelY(y, i, q);
          }
          catch (NullPointerException e) { }

          int shapeAnnotationRefCount = 0;
          try {
            shapeAnnotationRefCount = src.getShapeAnnotationRefCount(i, q);
          }
          catch (NullPointerException e) { }
          for (int r=0; r<shapeAnnotationRefCount; r++) {
            try {
              String shapeAnnotationRef = src.getLabelAnnotationRef(i, q, r);
              dest.setLabelAnnotationRef(shapeAnnotationRef, i, q, r);
            }
            catch (NullPointerException e) { }
          }
        }
        else if (type.equals("Line")) {
          try {
            String shapeID = src.getLineID(i, q);
            dest.setLineID(shapeID, i, q);
          }
          catch (NullPointerException e) {
            continue;
          }

          try {
            Color fillColor = src.getLineFillColor(i, q);
            dest.setLineFillColor(fillColor, i, q);
          }
          catch (NullPointerException e) { }

          try {
            FillRule fillRule = src.getLineFillRule(i, q);
            dest.setLineFillRule(fillRule, i, q);
          }
          catch (NullPointerException e) { }

          try {
            FontFamily fontFamily = src.getLineFontFamily(i, q);
            dest.setLineFontFamily(fontFamily, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Length fontSize = src.getLineFontSize(i, q);
            dest.setLineFontSize(fontSize, i, q);
          }
          catch (NullPointerException e) { }

          try {
            FontStyle fontStyle = src.getLineFontStyle(i, q);
            dest.setLineFontStyle(fontStyle, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Boolean locked = src.getLineLocked(i, q);
            dest.setLineLocked(locked, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Color strokeColor = src.getLineStrokeColor(i, q);
            dest.setLineStrokeColor(strokeColor, i, q);
          }
          catch (NullPointerException e) { }

          try {
            String dashArray = src.getLineStrokeDashArray(i, q);
            dest.setLineStrokeDashArray(dashArray, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Length strokeWidth = src.getLineStrokeWidth(i, q);
            dest.setLineStrokeWidth(strokeWidth, i, q);
          }
          catch (NullPointerException e) { }

          try {
            String text = src.getLineText(i, q);
            dest.setLineText(text, i, q);
          }
          catch (NullPointerException e) { }

          try {
            NonNegativeInteger theC = src.getLineTheC(i, q);
            dest.setLineTheC(theC, i, q);
          }
          catch (NullPointerException e) { }

          try {
            NonNegativeInteger theT = src.getLineTheT(i, q);
            dest.setLineTheT(theT, i, q);
          }
          catch (NullPointerException e) { }

          try {
            NonNegativeInteger theZ = src.getLineTheZ(i, q);
            dest.setLineTheZ(theZ, i, q);
          }
          catch (NullPointerException e) { }

          try {
            AffineTransform transform = src.getLineTransform(i, q);
            dest.setLineTransform(transform, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Marker end = src.getLineMarkerEnd(i, q);
            dest.setLineMarkerEnd(end, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Marker start = src.getLineMarkerStart(i, q);
            dest.setLineMarkerStart(start, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Double x1 = src.getLineX1(i, q);
            dest.setLineX1(x1, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Double x2 = src.getLineX2(i, q);
            dest.setLineX2(x2, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Double y1 = src.getLineY1(i, q);
            dest.setLineY1(y1, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Double y2 = src.getLineY2(i, q);
            dest.setLineY2(y2, i, q);
          }
          catch (NullPointerException e) { }

          int shapeAnnotationRefCount = 0;
          try {
            shapeAnnotationRefCount = src.getShapeAnnotationRefCount(i, q);
          }
          catch (NullPointerException e) { }
          for (int r=0; r<shapeAnnotationRefCount; r++) {
            try {
              String shapeAnnotationRef = src.getLineAnnotationRef(i, q, r);
              dest.setLineAnnotationRef(shapeAnnotationRef, i, q, r);
            }
            catch (NullPointerException e) { }
          }
        }
        else if (type.equals("Mask")) {
          try {
            String shapeID = src.getMaskID(i, q);
            dest.setMaskID(shapeID, i, q);
          }
          catch (NullPointerException e) {
            continue;
          }

          try {
            Color fillColor = src.getMaskFillColor(i, q);
            dest.setMaskFillColor(fillColor, i, q);
          }
          catch (NullPointerException e) { }

          try {
            FillRule fillRule = src.getMaskFillRule(i, q);
            dest.setMaskFillRule(fillRule, i, q);
          }
          catch (NullPointerException e) { }

          try {
            FontFamily fontFamily = src.getMaskFontFamily(i, q);
            dest.setMaskFontFamily(fontFamily, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Length fontSize = src.getMaskFontSize(i, q);
            dest.setMaskFontSize(fontSize, i, q);
          }
          catch (NullPointerException e) { }

          try {
            FontStyle fontStyle = src.getMaskFontStyle(i, q);
            dest.setMaskFontStyle(fontStyle, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Boolean locked = src.getMaskLocked(i, q);
            dest.setMaskLocked(locked, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Color strokeColor = src.getMaskStrokeColor(i, q);
            dest.setMaskStrokeColor(strokeColor, i, q);
          }
          catch (NullPointerException e) { }

          try {
            String dashArray = src.getMaskStrokeDashArray(i, q);
            dest.setMaskStrokeDashArray(dashArray, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Length strokeWidth = src.getMaskStrokeWidth(i, q);
            dest.setMaskStrokeWidth(strokeWidth, i, q);
          }
          catch (NullPointerException e) { }

          try {
            String text = src.getMaskText(i, q);
            dest.setMaskText(text, i, q);
          }
          catch (NullPointerException e) { }

          try {
            NonNegativeInteger theC = src.getMaskTheC(i, q);
            dest.setMaskTheC(theC, i, q);
          }
          catch (NullPointerException e) { }

          try {
            NonNegativeInteger theT = src.getMaskTheT(i, q);
            dest.setMaskTheT(theT, i, q);
          }
          catch (NullPointerException e) { }

          try {
            NonNegativeInteger theZ = src.getMaskTheZ(i, q);
            dest.setMaskTheZ(theZ, i, q);
          }
          catch (NullPointerException e) { }

          try {
            AffineTransform transform = src.getMaskTransform(i, q);
            dest.setMaskTransform(transform, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Double height = src.getMaskHeight(i, q);
            dest.setMaskHeight(height, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Double width = src.getMaskWidth(i, q);
            dest.setMaskWidth(width, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Double x = src.getMaskX(i, q);
            dest.setMaskX(x, i, q);
          }
          catch (NullPointerException e) { }
          
          try {
            byte[] binData = src.getMaskBinData(i, q);
            dest.setMaskBinData(binData, i, q);
          }
          catch (NullPointerException e) { }
          
          try {
            boolean bigEndian = src.getMaskBinDataBigEndian(i, q);
            dest.setMaskBinDataBigEndian(bigEndian, i, q);
          }
          catch (NullPointerException e) { }
          
          try {
            NonNegativeLong length = src.getMaskBinDataLength(i, q);
            dest.setMaskBinDataLength(length, i, q);
          }
          catch (NullPointerException e) { }
          
          try {
            Compression compression = src.getMaskBinDataCompression(i, q);
            dest.setMaskBinDataCompression(compression, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Double y = src.getMaskY(i, q);
            dest.setMaskY(y, i, q);
          }
          catch (NullPointerException e) { }

          int shapeAnnotationRefCount = 0;
          try {
            shapeAnnotationRefCount = src.getShapeAnnotationRefCount(i, q);
          }
          catch (NullPointerException e) { }
          for (int r=0; r<shapeAnnotationRefCount; r++) {
            try {
              String shapeAnnotationRef = src.getMaskAnnotationRef(i, q, r);
              dest.setMaskAnnotationRef(shapeAnnotationRef, i, q, r);
            }
            catch (NullPointerException e) { }
          }
        }
        else if (type.equals("Point")) {
          try {
            String shapeID = src.getPointID(i, q);
            dest.setPointID(shapeID, i, q);
          }
          catch (NullPointerException e) {
            continue;
          }

          try {
            Color fillColor = src.getPointFillColor(i, q);
            dest.setPointFillColor(fillColor, i, q);
          }
          catch (NullPointerException e) { }

          try {
            FillRule fillRule = src.getPointFillRule(i, q);
            dest.setPointFillRule(fillRule, i, q);
          }
          catch (NullPointerException e) { }

          try {
            FontFamily fontFamily = src.getPointFontFamily(i, q);
            dest.setPointFontFamily(fontFamily, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Length fontSize = src.getPointFontSize(i, q);
            dest.setPointFontSize(fontSize, i, q);
          }
          catch (NullPointerException e) { }

          try {
            FontStyle fontStyle = src.getPointFontStyle(i, q);
            dest.setPointFontStyle(fontStyle, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Boolean locked = src.getPointLocked(i, q);
            dest.setPointLocked(locked, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Color strokeColor = src.getPointStrokeColor(i, q);
            dest.setPointStrokeColor(strokeColor, i, q);
          }
          catch (NullPointerException e) { }

          try {
            String dashArray = src.getPointStrokeDashArray(i, q);
            dest.setPointStrokeDashArray(dashArray, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Length strokeWidth = src.getPointStrokeWidth(i, q);
            dest.setPointStrokeWidth(strokeWidth, i, q);
          }
          catch (NullPointerException e) { }

          try {
            String text = src.getPointText(i, q);
            dest.setPointText(text, i, q);
          }
          catch (NullPointerException e) { }

          try {
            NonNegativeInteger theC = src.getPointTheC(i, q);
            dest.setPointTheC(theC, i, q);
          }
          catch (NullPointerException e) { }

          try {
            NonNegativeInteger theT = src.getPointTheT(i, q);
            dest.setPointTheT(theT, i, q);
          }
          catch (NullPointerException e) { }

          try {
            NonNegativeInteger theZ = src.getPointTheZ(i, q);
            dest.setPointTheZ(theZ, i, q);
          }
          catch (NullPointerException e) { }

          try {
            AffineTransform transform = src.getPointTransform(i, q);
            dest.setPointTransform(transform, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Double x = src.getPointX(i, q);
            dest.setPointX(x, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Double y = src.getPointY(i, q);
            dest.setPointY(y, i, q);
          }
          catch (NullPointerException e) { }

          int shapeAnnotationRefCount = 0;
          try {
            shapeAnnotationRefCount = src.getShapeAnnotationRefCount(i, q);
          }
          catch (NullPointerException e) { }
          for (int r=0; r<shapeAnnotationRefCount; r++) {
            try {
              String shapeAnnotationRef = src.getPointAnnotationRef(i, q, r);
              dest.setPointAnnotationRef(shapeAnnotationRef, i, q, r);
            }
            catch (NullPointerException e) { }
          }
        }
        else if (type.equals("Polygon")) {
          try {
            String shapeID = src.getPolygonID(i, q);
            dest.setPolygonID(shapeID, i, q);
          }
          catch (NullPointerException e) {
            continue;
          }

          try {
            Color fillColor = src.getPolygonFillColor(i, q);
            dest.setPolygonFillColor(fillColor, i, q);
          }
          catch (NullPointerException e) { }

          try {
            FillRule fillRule = src.getPolygonFillRule(i, q);
            dest.setPolygonFillRule(fillRule, i, q);
          }
          catch (NullPointerException e) { }

          try {
            FontFamily fontFamily = src.getPolygonFontFamily(i, q);
            dest.setPolygonFontFamily(fontFamily, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Length fontSize = src.getPolygonFontSize(i, q);
            dest.setPolygonFontSize(fontSize, i, q);
          }
          catch (NullPointerException e) { }

          try {
            FontStyle fontStyle = src.getPolygonFontStyle(i, q);
            dest.setPolygonFontStyle(fontStyle, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Boolean locked = src.getPolygonLocked(i, q);
            dest.setPolygonLocked(locked, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Color strokeColor = src.getPolygonStrokeColor(i, q);
            dest.setPolygonStrokeColor(strokeColor, i, q);
          }
          catch (NullPointerException e) { }

          try {
            String dashArray = src.getPolygonStrokeDashArray(i, q);
            dest.setPolygonStrokeDashArray(dashArray, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Length strokeWidth = src.getPolygonStrokeWidth(i, q);
            dest.setPolygonStrokeWidth(strokeWidth, i, q);
          }
          catch (NullPointerException e) { }

          try {
            String text = src.getPolygonText(i, q);
            dest.setPolygonText(text, i, q);
          }
          catch (NullPointerException e) { }

          try {
            NonNegativeInteger theC = src.getPolygonTheC(i, q);
            dest.setPolygonTheC(theC, i, q);
          }
          catch (NullPointerException e) { }

          try {
            NonNegativeInteger theT = src.getPolygonTheT(i, q);
            dest.setPolygonTheT(theT, i, q);
          }
          catch (NullPointerException e) { }

          try {
            NonNegativeInteger theZ = src.getPolygonTheZ(i, q);
            dest.setPolygonTheZ(theZ, i, q);
          }
          catch (NullPointerException e) { }

          try {
            AffineTransform transform = src.getPolygonTransform(i, q);
            dest.setPolygonTransform(transform, i, q);
          }
          catch (NullPointerException e) { }

          try {
            String points = src.getPolygonPoints(i, q);
            dest.setPolygonPoints(points, i, q);
          }
          catch (NullPointerException e) { }

          int shapeAnnotationRefCount = 0;
          try {
            shapeAnnotationRefCount = src.getShapeAnnotationRefCount(i, q);
          }
          catch (NullPointerException e) { }
          for (int r=0; r<shapeAnnotationRefCount; r++) {
            try {
              String shapeAnnotationRef = src.getPolygonAnnotationRef(i, q, r);
              dest.setPolygonAnnotationRef(shapeAnnotationRef, i, q, r);
            }
            catch (NullPointerException e) { }
          }
        }
        else if (type.equals("Polyline")) {
          try {
            String shapeID = src.getPolylineID(i, q);
            dest.setPolylineID(shapeID, i, q);
          }
          catch (NullPointerException e) {
            continue;
          }

          try {
            Color fillColor = src.getPolylineFillColor(i, q);
            dest.setPolylineFillColor(fillColor, i, q);
          }
          catch (NullPointerException e) { }

          try {
            FillRule fillRule = src.getPolylineFillRule(i, q);
            dest.setPolylineFillRule(fillRule, i, q);
          }
          catch (NullPointerException e) { }

          try {
            FontFamily fontFamily = src.getPolylineFontFamily(i, q);
            dest.setPolylineFontFamily(fontFamily, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Length fontSize = src.getPolylineFontSize(i, q);
            dest.setPolylineFontSize(fontSize, i, q);
          }
          catch (NullPointerException e) { }

          try {
            FontStyle fontStyle = src.getPolylineFontStyle(i, q);
            dest.setPolylineFontStyle(fontStyle, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Boolean locked = src.getPolylineLocked(i, q);
            dest.setPolylineLocked(locked, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Color strokeColor = src.getPolylineStrokeColor(i, q);
            dest.setPolylineStrokeColor(strokeColor, i, q);
          }
          catch (NullPointerException e) { }

          try {
            String dashArray = src.getPolylineStrokeDashArray(i, q);
            dest.setPolylineStrokeDashArray(dashArray, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Length strokeWidth = src.getPolylineStrokeWidth(i, q);
            dest.setPolylineStrokeWidth(strokeWidth, i, q);
          }
          catch (NullPointerException e) { }

          try {
            String text = src.getPolylineText(i, q);
            dest.setPolylineText(text, i, q);
          }
          catch (NullPointerException e) { }

          try {
            NonNegativeInteger theC = src.getPolylineTheC(i, q);
            dest.setPolylineTheC(theC, i, q);
          }
          catch (NullPointerException e) { }

          try {
            NonNegativeInteger theT = src.getPolylineTheT(i, q);
            dest.setPolylineTheT(theT, i, q);
          }
          catch (NullPointerException e) { }

          try {
            NonNegativeInteger theZ = src.getPolylineTheZ(i, q);
            dest.setPolylineTheZ(theZ, i, q);
          }
          catch (NullPointerException e) { }

          try {
            AffineTransform transform = src.getPolylineTransform(i, q);
            dest.setPolylineTransform(transform, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Marker end = src.getPolylineMarkerEnd(i, q);
            dest.setPolylineMarkerEnd(end, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Marker start = src.getPolylineMarkerStart(i, q);
            dest.setPolylineMarkerStart(start, i, q);
          }
          catch (NullPointerException e) { }

          try {
            String points = src.getPolylinePoints(i, q);
            dest.setPolylinePoints(points, i, q);
          }
          catch (NullPointerException e) { }

          int shapeAnnotationRefCount = 0;
          try {
            shapeAnnotationRefCount = src.getShapeAnnotationRefCount(i, q);
          }
          catch (NullPointerException e) { }
          for (int r=0; r<shapeAnnotationRefCount; r++) {
            try {
              String shapeAnnotationRef = src.getPolylineAnnotationRef(i, q, r);
              dest.setPolylineAnnotationRef(shapeAnnotationRef, i, q, r);
            }
            catch (NullPointerException e) { }
          }
        }
        else if (type.equals("Rectangle")) {
          try {
            String shapeID = src.getRectangleID(i, q);
            dest.setRectangleID(shapeID, i, q);
          }
          catch (NullPointerException e) {
            continue;
          }

          try {
            Color fillColor = src.getRectangleFillColor(i, q);
            dest.setRectangleFillColor(fillColor, i, q);
          }
          catch (NullPointerException e) { }

          try {
            FillRule fillRule = src.getRectangleFillRule(i, q);
            dest.setRectangleFillRule(fillRule, i, q);
          }
          catch (NullPointerException e) { }

          try {
            FontFamily fontFamily = src.getRectangleFontFamily(i, q);
            dest.setRectangleFontFamily(fontFamily, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Length fontSize = src.getRectangleFontSize(i, q);
            dest.setRectangleFontSize(fontSize, i, q);
          }
          catch (NullPointerException e) { }

          try {
            FontStyle fontStyle = src.getRectangleFontStyle(i, q);
            dest.setRectangleFontStyle(fontStyle, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Boolean locked = src.getRectangleLocked(i, q);
            dest.setRectangleLocked(locked, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Color strokeColor = src.getRectangleStrokeColor(i, q);
            dest.setRectangleStrokeColor(strokeColor, i, q);
          }
          catch (NullPointerException e) { }

          try {
            String dashArray = src.getRectangleStrokeDashArray(i, q);
            dest.setRectangleStrokeDashArray(dashArray, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Length strokeWidth = src.getRectangleStrokeWidth(i, q);
            dest.setRectangleStrokeWidth(strokeWidth, i, q);
          }
          catch (NullPointerException e) { }

          try {
            String text = src.getRectangleText(i, q);
            dest.setRectangleText(text, i, q);
          }
          catch (NullPointerException e) { }

          try {
            NonNegativeInteger theC = src.getRectangleTheC(i, q);
            dest.setRectangleTheC(theC, i, q);
          }
          catch (NullPointerException e) { }

          try {
            NonNegativeInteger theT = src.getRectangleTheT(i, q);
            dest.setRectangleTheT(theT, i, q);
          }
          catch (NullPointerException e) { }

          try {
            NonNegativeInteger theZ = src.getRectangleTheZ(i, q);
            dest.setRectangleTheZ(theZ, i, q);
          }
          catch (NullPointerException e) { }

          try {
            AffineTransform transform = src.getRectangleTransform(i, q);
            dest.setRectangleTransform(transform, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Double height = src.getRectangleHeight(i, q);
            dest.setRectangleHeight(height, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Double width = src.getRectangleWidth(i, q);
            dest.setRectangleWidth(width, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Double x = src.getRectangleX(i, q);
            dest.setRectangleX(x, i, q);
          }
          catch (NullPointerException e) { }

          try {
            Double y = src.getRectangleY(i, q);
            dest.setRectangleY(y, i, q);
          }
          catch (NullPointerException e) { }

          int shapeAnnotationRefCount = 0;
          try {
            shapeAnnotationRefCount = src.getShapeAnnotationRefCount(i, q);
          }
          catch (NullPointerException e) { }
          for (int r=0; r<shapeAnnotationRefCount; r++) {
            try {
              String shapeAnnotationRef = src.getRectangleAnnotationRef(i, q, r);
              dest.setRectangleAnnotationRef(shapeAnnotationRef, i, q, r);
            }
            catch (NullPointerException e) { }
          }
        }
      }

      int annotationRefCount = 0;
      try {
        annotationRefCount = src.getROIAnnotationRefCount(i);
      }
      catch (NullPointerException e) { }
      for (int q=0; q<annotationRefCount; q++) {
        try {
          String annotationRef = src.getROIAnnotationRef(i, q);
          dest.setROIAnnotationRef(annotationRef, i, q);
        }
        catch (NullPointerException e) { }
      }
    }
  }

  /**
   * Convert all Screen attributes.
   * @param src the MetadataRetrieve from which to copy
   * @param dest the MetadataStore to which to copy
   */
  private static void convertScreens(MetadataRetrieve src, MetadataStore dest) {
    int screenCount = 0;
    try {
      screenCount = src.getScreenCount();
    }
    catch (NullPointerException e) { }
    for (int i=0; i<screenCount; i++) {
      try {
        String id = src.getScreenID(i);
        dest.setScreenID(id, i);
      }
      catch (NullPointerException e) {
        continue;
      }

      try {
        String description = src.getScreenDescription(i);
        dest.setScreenDescription(description, i);
      }
      catch (NullPointerException e) { }

      // NB: screen name is required in OMERO
      try {
        String name = src.getScreenName(i);
        if (name == null) {
          name = "";
        }
        dest.setScreenName(name, i);
      }
      catch (NullPointerException e) {
        dest.setScreenName("", i);
      }

      try {
        String protocolDescription = src.getScreenProtocolDescription(i);
        dest.setScreenProtocolDescription(protocolDescription, i);
      }
      catch (NullPointerException e) { }

      try {
        String protocolIdentifier = src.getScreenProtocolIdentifier(i);
        dest.setScreenProtocolIdentifier(protocolIdentifier, i);
      }
      catch (NullPointerException e) { }

      try {
        String reagentSetDescription = src.getScreenReagentSetDescription(i);
        dest.setScreenReagentSetDescription(reagentSetDescription, i);
      }
      catch (NullPointerException e) { }

      try {
        String reagentSetIdentifier = src.getScreenReagentSetIdentifier(i);
        dest.setScreenReagentSetIdentifier(reagentSetIdentifier, i);
      }
      catch (NullPointerException e) { }

      try {
        String type = src.getScreenType(i);
        dest.setScreenType(type, i);
      }
      catch (NullPointerException e) { }

      int plateRefCount = 0;
      try {
        plateRefCount = src.getPlateRefCount(i);
      }
      catch (NullPointerException e) { }
      for (int q=0; q<plateRefCount; q++) {
        try {
          String plateRef = src.getScreenPlateRef(i, q);
          dest.setScreenPlateRef(plateRef, i, q);
        }
        catch (NullPointerException e) { }
      }

      int annotationRefCount = 0;
      try {
        annotationRefCount = src.getScreenAnnotationRefCount(i);
      }
      catch (NullPointerException e) { }
      for (int q=0; q<annotationRefCount; q++) {
        try {
          String annotationRef = src.getScreenAnnotationRef(i, q);
          dest.setScreenAnnotationRef(annotationRef, i, q);
        }
        catch (NullPointerException e) { }
      }

      int reagentCount = 0;
      try {
        reagentCount = src.getReagentCount(i);
      }
      catch (NullPointerException e) { }
      for (int q=0; q<reagentCount; q++) {
        try {
          String reagentID = src.getReagentID(i, q);
          dest.setReagentID(reagentID, i, q);
        }
        catch (NullPointerException e) {
          continue;
        }

        try {
          String reagentDescription = src.getReagentDescription(i, q);
          dest.setReagentDescription(reagentDescription, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String reagentName = src.getReagentName(i, q);
          dest.setReagentName(reagentName, i, q);
        }
        catch (NullPointerException e) { }

        try {
          String reagentIdentifier = src.getReagentReagentIdentifier(i, q);
          dest.setReagentReagentIdentifier(reagentIdentifier, i ,q);
        }
        catch (NullPointerException e) { }

        int reagentAnnotationRefCount = 0;
        try {
          reagentAnnotationRefCount = src.getReagentAnnotationRefCount(i, q);
        }
        catch (NullPointerException e) { }
        for (int r=0; r<reagentAnnotationRefCount; r++) {
          try {
            String reagentAnnotationRef = src.getReagentAnnotationRef(i, q, r);
            dest.setReagentAnnotationRef(reagentAnnotationRef, i, q, r);
          }
          catch (NullPointerException e) { }
        }
      }
    }
  }

  /**
   * Convert all TagAnnotation attributes.
   * @param src the MetadataRetrieve from which to copy
   * @param dest the MetadataStore to which to copy
   */
  private static void convertTagAnnotations(MetadataRetrieve src,
    MetadataStore dest)
  {
    int tagAnnotationCount = 0;
    try {
      tagAnnotationCount = src.getTagAnnotationCount();
    }
    catch (NullPointerException e) { }
    for (int i=0; i<tagAnnotationCount; i++) {
      try {
        String id = src.getTagAnnotationID(i);
        dest.setTagAnnotationID(id, i);
      }
      catch (NullPointerException e) {
        continue;
      }

      try {
        String description = src.getTagAnnotationDescription(i);
        dest.setTagAnnotationDescription(description, i);
      }
      catch (NullPointerException e) { }

      try {
        String namespace = src.getTagAnnotationNamespace(i);
        dest.setTagAnnotationNamespace(namespace, i);
      }
      catch (NullPointerException e) { }

      try {
        String value = src.getTagAnnotationValue(i);
        dest.setTagAnnotationValue(value, i);
      }
      catch (NullPointerException e) { }

      try {
        String annotator = src.getTagAnnotationAnnotator(i);
        dest.setTagAnnotationAnnotator(annotator, i);
      }
      catch (NullPointerException e) { }

      int annotationRefCount = 0;
      try {
        annotationRefCount = src.getTagAnnotationAnnotationCount(i);
      }
      catch (NullPointerException e) { }
      for (int a=0; a<annotationRefCount; a++) {
        try {
          String id = src.getTagAnnotationAnnotationRef(i, a);
          dest.setTagAnnotationAnnotationRef(id, i, a);
        }
        catch (NullPointerException e) { }
      }
    }
  }

  /**
   * Convert all TermAnnotation attributes.
   * @param src the MetadataRetrieve from which to copy
   * @param dest the MetadataStore to which to copy
   */
  private static void convertTermAnnotations(MetadataRetrieve src,
    MetadataStore dest)
  {
    int termAnnotationCount = 0;
    try {
      termAnnotationCount = src.getTermAnnotationCount();
    }
    catch (NullPointerException e) { }
    for (int i=0; i<termAnnotationCount; i++) {
      try {
        String id = src.getTermAnnotationID(i);
        dest.setTermAnnotationID(id, i);
      }
      catch (NullPointerException e) {
        continue;
      }

      try {
        String description = src.getTermAnnotationDescription(i);
        dest.setTermAnnotationDescription(description, i);
      }
      catch (NullPointerException e) { }

      try {
        String namespace = src.getTermAnnotationNamespace(i);
        dest.setTermAnnotationNamespace(namespace, i);
      }
      catch (NullPointerException e) { }

      try {
        String value = src.getTermAnnotationValue(i);
        dest.setTermAnnotationValue(value, i);
      }
      catch (NullPointerException e) { }

      try {
        String annotator = src.getTermAnnotationAnnotator(i);
        dest.setTermAnnotationAnnotator(annotator, i);
      }
      catch (NullPointerException e) { }

      int annotationRefCount = 0;
      try {
        annotationRefCount = src.getTermAnnotationAnnotationCount(i);
      }
      catch (NullPointerException e) { }
      for (int a=0; a<annotationRefCount; a++) {
        try {
          String id = src.getTermAnnotationAnnotationRef(i, a);
          dest.setTermAnnotationAnnotationRef(id, i, a);
        }
        catch (NullPointerException e) { }
      }
    }
  }

  /**
   * Convert all TimestampAnnotation attributes.
   * @param src the MetadataRetrieve from which to copy
   * @param dest the MetadataStore to which to copy
   */
  private static void convertTimestampAnnotations(MetadataRetrieve src,
    MetadataStore dest)
  {
    int timestampAnnotationCount = 0;
    try {
      timestampAnnotationCount = src.getTimestampAnnotationCount();
    }
    catch (NullPointerException e) { }
    for (int i=0; i<timestampAnnotationCount; i++) {
      try {
        String id = src.getTimestampAnnotationID(i);
        dest.setTimestampAnnotationID(id, i);
      }
      catch (NullPointerException e) {
        continue;
      }

      try {
        String description = src.getTimestampAnnotationDescription(i);
        dest.setTimestampAnnotationDescription(description, i);
      }
      catch (NullPointerException e) { }

      try {
        String namespace = src.getTimestampAnnotationNamespace(i);
        dest.setTimestampAnnotationNamespace(namespace, i);
      }
      catch (NullPointerException e) { }

      try {
        Timestamp value = src.getTimestampAnnotationValue(i);
        dest.setTimestampAnnotationValue(value, i);
      }
      catch (NullPointerException e) { }

      try {
        String annotator = src.getTimestampAnnotationAnnotator(i);
        dest.setTimestampAnnotationAnnotator(annotator, i);
      }
      catch (NullPointerException e) { }

      int annotationRefCount = 0;
      try {
        annotationRefCount = src.getTimestampAnnotationAnnotationCount(i);
      }
      catch (NullPointerException e) { }
      for (int a=0; a<annotationRefCount; a++) {
        try {
          String id = src.getTimestampAnnotationAnnotationRef(i, a);
          dest.setTimestampAnnotationAnnotationRef(id, i, a);
        }
        catch (NullPointerException e) { }
      }
    }
  }

  /**
   * Convert all XMLAnnotation attributes.
   * @param src the MetadataRetrieve from which to copy
   * @param dest the MetadataStore to which to copy
   */
  private static void convertXMLAnnotations(MetadataRetrieve src,
    MetadataStore dest)
  {
    int xmlAnnotationCount = 0;
    try {
      xmlAnnotationCount = src.getXMLAnnotationCount();
    }
    catch (NullPointerException e) { }
    for (int i=0; i<xmlAnnotationCount; i++) {
      try {
        String id = src.getXMLAnnotationID(i);
        dest.setXMLAnnotationID(id, i);
      }
      catch (NullPointerException e) {
        continue;
      }

      try {
        String description = src.getXMLAnnotationDescription(i);
        dest.setXMLAnnotationDescription(description, i);
      }
      catch (NullPointerException e) { }

      try {
        String namespace = src.getXMLAnnotationNamespace(i);
        dest.setXMLAnnotationNamespace(namespace, i);
      }
      catch (NullPointerException e) { }

      try {
        String value = src.getXMLAnnotationValue(i);
        dest.setXMLAnnotationValue(value, i);
      }
      catch (NullPointerException e) { }

      try {
        String annotator = src.getXMLAnnotationAnnotator(i);
        dest.setXMLAnnotationAnnotator(annotator, i);
      }
      catch (NullPointerException e) { }

      int annotationRefCount = 0;
      try {
        annotationRefCount = src.getXMLAnnotationAnnotationCount(i);
      }
      catch (NullPointerException e) { }
      for (int a=0; a<annotationRefCount; a++) {
        try {
          String id = src.getXMLAnnotationAnnotationRef(i, a);
          dest.setXMLAnnotationAnnotationRef(id, i, a);
        }
        catch (NullPointerException e) { }
      }
    }
  }

  /**
   * Convert all LightSource attributes for the given instrument.
   * @param src the MetadataRetrieve from which to copy
   * @param dest the MetadataStore to which to copy
   * @param instrumentIndex the index of the Instrument to convert
   * @param lightSourceIds the collection of light source to populate
   */
  private static void convertLightSources(MetadataRetrieve src,
    MetadataStore dest, int instrumentIndex, List<String> lightSourceIds)
  {
    int lightSourceCount = 0;
    try {
      lightSourceCount = src.getLightSourceCount(instrumentIndex);
    }
    catch (NullPointerException e) { }

    for (int lightSource=0; lightSource<lightSourceCount; lightSource++) {
      String type = src.getLightSourceType(instrumentIndex, lightSource);
      if (type.equals("Arc")) {
        try {
          String id = src.getArcID(instrumentIndex, lightSource);
          if (id != null && id.trim().length() > 0) {
            lightSourceIds.add(id);
            dest.setArcID(id, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) {
          continue;
        }

        try {
          String lotNumber = src.getArcLotNumber(instrumentIndex, lightSource);
          if (lotNumber != null) {
            dest.setArcLotNumber(lotNumber, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String manufacturer =
            src.getArcManufacturer(instrumentIndex, lightSource);
          if (manufacturer != null) {
            dest.setArcManufacturer(manufacturer, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String model = src.getArcModel(instrumentIndex, lightSource);
          if (model != null) {
            dest.setArcModel(model, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          Power power = src.getArcPower(instrumentIndex, lightSource);
          if (power != null) {
            dest.setArcPower(power, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String serialNumber =
            src.getArcSerialNumber(instrumentIndex, lightSource);
          if (serialNumber != null) {
            dest.setArcSerialNumber(serialNumber, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          ArcType arcType = src.getArcType(instrumentIndex, lightSource);
          if (arcType != null) {
            dest.setArcType(arcType, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        int lightSourceAnnotationRefCount = 0;
        try {
          lightSourceAnnotationRefCount = src.getLightSourceAnnotationRefCount(instrumentIndex, lightSource);
        }
        catch (NullPointerException e) { }
        for (int r=0; r<lightSourceAnnotationRefCount; r++) {
          try {
            String lightSourceAnnotationRef = src.getArcAnnotationRef(instrumentIndex, lightSource, r);
            dest.setArcAnnotationRef(lightSourceAnnotationRef, instrumentIndex, lightSource, r);
          }
          catch (NullPointerException e) { }
        }
      }
      else if (type.equals("Filament")) {
        try {
          String id = src.getFilamentID(instrumentIndex, lightSource);
          if (id != null && id.trim().length() > 0) {
            lightSourceIds.add(id);
            dest.setFilamentID(id, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) {
          continue;
        }

        try {
          String lotNumber =
            src.getFilamentLotNumber(instrumentIndex, lightSource);
          if (lotNumber != null) {
            dest.setFilamentLotNumber(lotNumber, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String manufacturer =
            src.getFilamentManufacturer(instrumentIndex, lightSource);
          if (manufacturer != null) {
            dest.setFilamentManufacturer(
              manufacturer, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String model = src.getFilamentModel(instrumentIndex, lightSource);
          if (model != null) {
            dest.setFilamentModel(model, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          Power power = src.getFilamentPower(instrumentIndex, lightSource);
          if (power != null) {
            dest.setFilamentPower(power, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String serialNumber =
            src.getFilamentSerialNumber(instrumentIndex, lightSource);
          if (serialNumber != null) {
            dest.setFilamentSerialNumber(
              serialNumber, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          FilamentType filamentType =
            src.getFilamentType(instrumentIndex, lightSource);
          if (filamentType != null) {
            dest.setFilamentType(filamentType, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        int lightSourceAnnotationRefCount = 0;
        try {
          lightSourceAnnotationRefCount = src.getLightSourceAnnotationRefCount(instrumentIndex, lightSource);
        }
        catch (NullPointerException e) { }
        for (int r=0; r<lightSourceAnnotationRefCount; r++) {
          try {
            String lightSourceAnnotationRef = src.getFilamentAnnotationRef(instrumentIndex, lightSource, r);
            dest.setFilamentAnnotationRef(lightSourceAnnotationRef, instrumentIndex, lightSource, r);
          }
          catch (NullPointerException e) { }
        }
      }
      else if (type.equals("GenericExcitationSource")) {
        try {
          String id =
            src.getGenericExcitationSourceID(instrumentIndex, lightSource);
          if (id != null && id.trim().length() > 0) {
            lightSourceIds.add(id);
            dest.setGenericExcitationSourceID(id, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) {
          continue;
        }

        try {
          List<MapPair> map =
            src.getGenericExcitationSourceMap(instrumentIndex, lightSource);
          dest.setGenericExcitationSourceMap(map, instrumentIndex, lightSource);
        }
        catch (NullPointerException e) { }

        try {
          String lotNumber = src.getGenericExcitationSourceLotNumber(
            instrumentIndex, lightSource);
          dest.setGenericExcitationSourceLotNumber(lotNumber,
            instrumentIndex, lightSource);
        }
        catch (NullPointerException e) { }

        try {
          String manufacturer = src.getGenericExcitationSourceManufacturer(
            instrumentIndex, lightSource);
          dest.setGenericExcitationSourceManufacturer(manufacturer,
            instrumentIndex, lightSource);
        }
        catch (NullPointerException e) { }

        try {
          String model =
            src.getGenericExcitationSourceModel(instrumentIndex, lightSource);
          dest.setGenericExcitationSourceModel(model,
            instrumentIndex, lightSource);
        }
        catch (NullPointerException e) { }

        try {
          Power power =
            src.getGenericExcitationSourcePower(instrumentIndex, lightSource);
          dest.setGenericExcitationSourcePower(power,
            instrumentIndex, lightSource);
        }
        catch (NullPointerException e) { }

        try {
          String serialNumber = src.getGenericExcitationSourceSerialNumber(
            instrumentIndex, lightSource);
          dest.setGenericExcitationSourceSerialNumber(serialNumber,
            instrumentIndex, lightSource);
        }
        catch (NullPointerException e) { }

        int lightSourceAnnotationRefCount = 0;
        try {
          lightSourceAnnotationRefCount = src.getLightSourceAnnotationRefCount(instrumentIndex, lightSource);
        }
        catch (NullPointerException e) { }
        for (int r=0; r<lightSourceAnnotationRefCount; r++) {
          try {
            String lightSourceAnnotationRef = src.getGenericExcitationSourceAnnotationRef(instrumentIndex, lightSource, r);
            dest.setGenericExcitationSourceAnnotationRef(lightSourceAnnotationRef, instrumentIndex, lightSource, r);
          }
          catch (NullPointerException e) { }
        }
      }
      else if (type.equals("Laser")) {
        try {
          String id = src.getLaserID(instrumentIndex, lightSource);
          if (id != null && id.trim().length() > 0) {
            lightSourceIds.add(id);
            dest.setLaserID(id, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) {
          continue;
        }

        try {
          String lotNumber =
            src.getLaserLotNumber(instrumentIndex, lightSource);
          if (lotNumber != null) {
            dest.setLaserLotNumber(lotNumber, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String manufacturer =
            src.getLaserManufacturer(instrumentIndex, lightSource);
          if (manufacturer != null) {
            dest.setLaserManufacturer(
              manufacturer, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String model = src.getLaserModel(instrumentIndex, lightSource);
          if (model != null) {
            dest.setLaserModel(model, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          Power power = src.getLaserPower(instrumentIndex, lightSource);
          if (power != null) {
            dest.setLaserPower(power, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String serialNumber =
            src.getLaserSerialNumber(instrumentIndex, lightSource);
          if (serialNumber != null) {
            dest.setLaserSerialNumber(
              serialNumber, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          LaserType laserType = src.getLaserType(instrumentIndex, lightSource);
          if (laserType != null) {
            dest.setLaserType(laserType, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          PositiveInteger frequencyMultiplication =
            src.getLaserFrequencyMultiplication(instrumentIndex, lightSource);
          if (frequencyMultiplication != null) {
            dest.setLaserFrequencyMultiplication(
              frequencyMultiplication, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          LaserMedium medium =
            src.getLaserLaserMedium(instrumentIndex, lightSource);
          if (medium != null) {
            dest.setLaserLaserMedium(medium, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          Boolean pockelCell =
            src.getLaserPockelCell(instrumentIndex, lightSource);
          if (pockelCell != null) {
            dest.setLaserPockelCell(pockelCell, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          Pulse pulse = src.getLaserPulse(instrumentIndex, lightSource);
          if (pulse != null) {
            dest.setLaserPulse(pulse, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String pump = src.getLaserPump(instrumentIndex, lightSource);
          if (pump != null) {
            dest.setLaserPump(pump, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          Frequency repetitionRate =
            src.getLaserRepetitionRate(instrumentIndex, lightSource);
          if (repetitionRate != null) {
            dest.setLaserRepetitionRate(
              repetitionRate, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          Boolean tuneable = src.getLaserTuneable(instrumentIndex, lightSource);
          if (tuneable != null) {
            dest.setLaserTuneable(tuneable, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          Length wavelength =
            src.getLaserWavelength(instrumentIndex, lightSource);
          if (wavelength != null) {
            dest.setLaserWavelength(wavelength, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        int lightSourceAnnotationRefCount = 0;
        try {
          lightSourceAnnotationRefCount = src.getLightSourceAnnotationRefCount(instrumentIndex, lightSource);
        }
        catch (NullPointerException e) { }
        for (int r=0; r<lightSourceAnnotationRefCount; r++) {
          try {
            String lightSourceAnnotationRef = src.getLaserAnnotationRef(instrumentIndex, lightSource, r);
            dest.setLaserAnnotationRef(lightSourceAnnotationRef, instrumentIndex, lightSource, r);
          }
          catch (NullPointerException e) { }
        }
      }
      else if (type.equals("LightEmittingDiode")) {
        try {
          String id = src.getLightEmittingDiodeID(instrumentIndex, lightSource);
          if (id != null && id.trim().length() > 0) {
            lightSourceIds.add(id);
            dest.setLightEmittingDiodeID(id, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) {
          continue;
        }

        try {
          String lotNumber =
            src.getLightEmittingDiodeLotNumber(instrumentIndex, lightSource);
          if (lotNumber != null) {
            dest.setLightEmittingDiodeLotNumber(
              lotNumber, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String manufacturer =
            src.getLightEmittingDiodeManufacturer(instrumentIndex, lightSource);
          if (manufacturer != null) {
            dest.setLightEmittingDiodeManufacturer(
              manufacturer, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String model =
            src.getLightEmittingDiodeModel(instrumentIndex, lightSource);
          if (model != null) {
            dest.setLightEmittingDiodeModel(
              model, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          Power power =
            src.getLightEmittingDiodePower(instrumentIndex, lightSource);
          if (power != null) {
            dest.setLightEmittingDiodePower(
              power, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        try {
          String serialNumber =
            src.getLightEmittingDiodeSerialNumber(instrumentIndex, lightSource);
          if (serialNumber != null) {
            dest.setLightEmittingDiodeSerialNumber(
              serialNumber, instrumentIndex, lightSource);
          }
        }
        catch (NullPointerException e) { }

        int lightSourceAnnotationRefCount = 0;
        try {
          lightSourceAnnotationRefCount = src.getLightSourceAnnotationRefCount(instrumentIndex, lightSource);
        }
        catch (NullPointerException e) { }
        for (int r=0; r<lightSourceAnnotationRefCount; r++) {
          try {
            String lightSourceAnnotationRef = src.getLightEmittingDiodeAnnotationRef(instrumentIndex, lightSource, r);
            dest.setLightEmittingDiodeAnnotationRef(lightSourceAnnotationRef, instrumentIndex, lightSource, r);
          }
          catch (NullPointerException e) { }
        }
      }
    }
  }

  /**
   * Convert all top-level attributes.
   * @param src the MetadataRetrieve from which to copy
   * @param dest the MetadataStore to which to copy
   */
  private static void convertRootAttributes(MetadataRetrieve src, MetadataStore dest)
  {
    try {
      String uuid = src.getUUID();
      if (uuid != null) {
        dest.setUUID(uuid);
      }
    }
    catch (NullPointerException e) { }

    try {
      String rightsHeld = src.getRightsRightsHeld();
      if (rightsHeld != null) {
        dest.setRightsRightsHeld(rightsHeld);
      }
    }
    catch (NullPointerException e) { }

    try {
      String rightsHolder = src.getRightsRightsHolder();
      if (rightsHolder != null) {
        dest.setRightsRightsHolder(rightsHolder);
      }
    }
    catch (NullPointerException e) { }

    try {
      String metadataFile = src.getBinaryOnlyMetadataFile();
      if (metadataFile != null) {
        dest.setBinaryOnlyMetadataFile(metadataFile);
      }
    }
    catch (NullPointerException e) { }

    try {
      String uuid = src.getBinaryOnlyUUID();
      if (uuid != null) {
        dest.setBinaryOnlyUUID(uuid);
      }
    }
    catch (NullPointerException e) { }
  }

}
