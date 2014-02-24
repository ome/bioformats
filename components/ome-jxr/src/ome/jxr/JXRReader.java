/*
 * #%L
 * OME library for reading the JPEG XR file format.
 * %%
 * Copyright (C) 2013 - 2014 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package ome.jxr;

import java.io.IOException;

import ome.jxr.metadata.IFDMetadata;
import ome.jxr.parser.FileParser;
import ome.jxr.parser.IFDParser;
import ome.jxr.parser.DatastreamParser;
import ome.scifio.io.RandomAccessInputStream;

/**
 * Reader for the JPEG XR image file format. Provides access to uncompressed
 * image data and image metadata. This class delegates the low-level data stream
 * manipulation to subclasses of the {@link Parser} class. A successful
 * instantiation of this class guarantees initial validity of the image resource
 * passed into the constructor.
 *
 * <dl>
 * <dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-jxr/src/ome/jxr/datastream/JXRReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-jxr/src/ome/jxr/datastream/JXRReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 */
public final class JXRReader {

  private DatastreamParser datastreamParser;

  private FileParser fileParser;

  private IFDParser ifdParser;

  private RandomAccessInputStream stream;

  public JXRReader(String file) throws IOException, JXRException {
    this(new RandomAccessInputStream(file));
  }

  public JXRReader(RandomAccessInputStream stream) throws JXRException {
    fileParser = new FileParser(stream);
    fileParser.parse();
    this.stream = stream;
  }

  public byte[] getDecompressedImage() throws JXRException {
    if (datastreamParser == null) {
      datastreamParser = new DatastreamParser(this.stream, getMetadata(),
          fileParser.getEncoderVersion());
    }
    return datastreamParser.parse();
  }

  public IFDMetadata getMetadata() throws JXRException {
    if (ifdParser == null) {
      ifdParser = new IFDParser(this.stream, fileParser.getRootIFDOffset());
      ifdParser.parse();
    }
    return ifdParser.getIFDMetadata();
  }

  public void close() throws IOException {
    fileParser.close();
    ifdParser.close();
  }

  @Override
  public String toString() {
    return "JXRReader [stream=" + stream + "]";
  }

}
