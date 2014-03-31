/*
 * #%L
 * OME Bio-Formats package for BSD-licensed readers and writers.
 * %%
 * Copyright (C) 2005 - 2013 Open Microscopy Environment:
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package loci.formats.out;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveInteger;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.ome.OMEXMLMetadataImpl;
import loci.formats.services.OMEXMLService;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffSaver;

/**
 * OMEBinaryOnlyTiffWriter is the file format writer for OME-TIFF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/out/OMEBinaryOnlyTiffWriter.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/out/OMEBinaryOnlyTiffWriter.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class OMEBinaryOnlyTiffWriter extends OMETiffWriter {

  // -- Constants --

	private static final String COMPANION_EXTENSION = ".companion.ome";

	private static final String BINARY_ONLY_WARNING_COMMENT =
		    "<!-- Warning: this comment is an OME-XML binary only metadata block, " +
		    "points to other important metadata. " +
		    "Please edit cautiously (if at all), and back up the original data " +
		    "before doing so. For more information, see the OME-TIFF web site: " +
		    FormatTools.URL_OME_TIFF + ". -->";

	private static final String COMPANION_WARNING_COMMENT =
		    "<!-- Warning: this is an OME-XML companion metadata file, " +
		    "and points to pixel data in other files. " +
		    "Please edit cautiously (if at all), and back up the original data " +
		    "before doing so. For more information, see the OME-TIFF web site: " +
		    FormatTools.URL_OME_TIFF + ". -->";

	private String companionFilename;
	private String companionUUID;

  // -- Constructor --

  public OMEBinaryOnlyTiffWriter() {
    super("COME-TIFF", new String[] {"come.tif", "come.tiff"});
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    try {
      if (currentId != null) {
        setupServiceAndMetadata();

        // remove any BinData elements from the OME-XML
        service.removeBinData(omeMeta);

        for (int series=0; series<omeMeta.getImageCount(); series++) {
          setSeries(series);
          populateImage(omeMeta, series);
        }

        List<String> files = new ArrayList<String>();
        for (String[] s : imageLocations) {
          for (String f : s) {
            if (!files.contains(f) && f != null) {
              files.add(f);

              String xmlBinaryOnly = getBinaryOnlyOMEXML(f);

              // write OME-XML to the first IFD's comment
              saveComment(f, xmlBinaryOnly);
            }
          }
        }
        String xmlCompanion = getOMEXML(companionFilename);
		OutputStream outputStream = new FileOutputStream(companionFilename);
		outputStream.write(xmlCompanion.getBytes());
		outputStream.close();
      }
    }
    catch (DependencyException de) {
      throw new RuntimeException(de);
    }
    catch (ServiceException se) {
      throw new RuntimeException(se);
    }
    catch (FormatException fe) {
      throw new RuntimeException(fe);
    }
    catch (IllegalArgumentException iae) {
      throw new RuntimeException(iae);
    }
    finally {
      super.closeParentWorkaroundToFix();

      boolean canReallyClose =
        omeMeta == null || ifdCounts.size() == omeMeta.getImageCount();

      if (omeMeta != null && canReallyClose) {
        int omePlaneCount = 0;
        for (int i=0; i<omeMeta.getImageCount(); i++) {
          int sizeZ = omeMeta.getPixelsSizeZ(i).getValue();
          int sizeC = omeMeta.getPixelsSizeC(i).getValue();
          int sizeT = omeMeta.getPixelsSizeT(i).getValue();

          omePlaneCount += sizeZ * sizeC * sizeT;
        }

        int ifdCount = 0;
        for (String key : ifdCounts.keySet()) {
          ifdCount += ifdCounts.get(key);
        }

        canReallyClose = omePlaneCount == ifdCount;
      }

      if (canReallyClose) {
        imageLocations = null;
        omeMeta = null;
        service = null;
        ifdCounts.clear();
      }
      else {
        for(String k : ifdCounts.keySet())
        ifdCounts.put(k, 0);
      }
    }
  }

  // -- IFormatHandler API methods --

  /* @see IFormatHandler#setId(String) */
  public void setId(String id) throws FormatException, IOException {
    if (id.equals(currentId)) return;
    super.setId(id);
	companionFilename = id + COMPANION_EXTENSION;
	companionUUID = "urn:uuid:" + getUUID(companionFilename);
  }

  // -- Helper methods --

  @Override
  protected void setupServiceAndMetadata()
    throws DependencyException, ServiceException
  {
    // extract OME-XML string from metadata object
    MetadataRetrieve retrieve = getMetadataRetrieve();

    ServiceFactory factory = new ServiceFactory();
    service = factory.getInstance(OMEXMLService.class);
    OMEXMLMetadata originalOMEMeta = service.getOMEMetadata(retrieve);
    originalOMEMeta.resolveReferences();

    String omexml = service.getOMEXML(originalOMEMeta);
    omeMeta = service.createOMEXMLMetadata(omexml);
  }

  @Override
  protected String getOMEXML(String file) throws FormatException, IOException {
    // generate UUID and add to OME element
    String uuid = "urn:uuid:" + getUUID(new Location(file).getName());
    omeMeta.setUUID(uuid);

    String xml;
    try {
      xml = service.getOMEXML(omeMeta);
    }
    catch (ServiceException se) {
      throw new FormatException(se);
    }

    // insert warning comment
    String prefix = xml.substring(0, xml.indexOf(">") + 1);
    String suffix = xml.substring(xml.indexOf(">") + 1);
    return prefix + COMPANION_WARNING_COMMENT + suffix;
  }

  protected String getBinaryOnlyOMEXML(String file) throws FormatException, IOException, DependencyException, ServiceException {
	    // generate UUID and add to OME element
	    String uuid = "urn:uuid:" + getUUID(new Location(file).getName());
	    ServiceFactory factory = new ServiceFactory();
	    service = factory.getInstance(OMEXMLService.class);
	    OMEXMLMetadata omeBinaryMeta = service.createOMEXMLMetadata();
		omeBinaryMeta.setUUID(uuid);
	    omeBinaryMeta.setBinaryOnlyMetadataFile(companionFilename);
	    omeBinaryMeta.setBinaryOnlyUUID(companionUUID);
	    
	    String xml;
	    try {
	      xml = service.getOMEXML(omeBinaryMeta);
	    }
	    catch (ServiceException se) {
	      throw new FormatException(se);
	    }

	    // insert warning comment
	    String prefix = xml.substring(0, xml.indexOf(">") + 1);
	    String suffix = xml.substring(xml.indexOf(">") + 1);
	    return prefix + BINARY_ONLY_WARNING_COMMENT + suffix;
	  }

  
}
