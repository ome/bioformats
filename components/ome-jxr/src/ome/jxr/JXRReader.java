/*
 * #%L
 * OME library for reading the JPEG XR file format.
 * %%
 * Copyright (C) 2013 - 2015 Open Microscopy Environment:
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

import loci.common.RandomAccessInputStream;
import ome.jxr.ifd.IFDMetadata;
import ome.jxr.parser.FileParser;
import ome.jxr.parser.IFDParser;
import ome.jxr.parser.DatastreamParser;
import ome.jxr.parser.Parser;

/**
 * Reader for the JPEG XR image file format. Provides access to uncompressed
 * image data and image metadata. This class delegates the low-level data stream
 * manipulation to subclasses of the {@link Parser} class. A successful
 * instantiation of this class guarantees initial validity of the image resource
 * passed into the constructor.
 *
 * <dl>
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 */
public final class JXRReader {

  private DatastreamParser datastreamParser;

  private FileParser fileParser;

  private IFDParser ifdParser;

  private String filename;

  public JXRReader(String filename) throws IOException, JXRException {
    this(new RandomAccessInputStream(filename));
    this.filename = filename;
  }

  public JXRReader(RandomAccessInputStream stream) throws JXRException {
    fileParser = new FileParser(stream);
    fileParser.parse();

    ifdParser = new IFDParser(fileParser, stream);
    datastreamParser = new DatastreamParser(ifdParser, stream);
  }

  public byte[] getDecompressedImage() throws JXRException {
    datastreamParser.parse();
    return null;
  }

  public IFDMetadata getMetadata() throws JXRException {
    ifdParser.parse();
    return ifdParser.getIFDMetadata();
  }

  public void close() throws IOException {
    fileParser.close();
    ifdParser.close();
    datastreamParser.close();
  }

  @Override
  public String toString() {
    return "JXRReader [filename=" + filename + "]";
  }

}
