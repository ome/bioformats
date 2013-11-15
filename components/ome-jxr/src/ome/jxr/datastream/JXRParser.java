/*
 * #%L
 * OME library for reading the JPEG XR file format.
 * %%
 * Copyright (C) 2013 Open Microscopy Environment:
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

package ome.jxr.datastream;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import loci.common.RandomAccessInputStream;
import ome.jxr.constants.IFD;
import ome.jxr.ifd.IFDContainer;
import ome.jxr.ifd.IFDElement;
import ome.jxr.ifd.IFDElementType;
import ome.jxr.ifd.IFDElementTypeTranslator;
import ome.jxr.metadata.JXRMetadata;

/**
 * Parses a JPEG XR data stream and allows for extraction of metadata and
 * image data.
 *
 * <dl>
 * <dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-jxr/src/ome/jxr/datastream/JXRParser.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-jxr/src/ome/jxr/datastream/JXRParser.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 */
public class JXRParser {

  private int rootIFDOffset;

  private RandomAccessInputStream stream;

  private IFDElementTypeTranslator translator = new IFDElementTypeTranslator();

  private List<IFDContainer> IFDContainers = new ArrayList<IFDContainer>();

  public int getIFDCount() {
    return IFDContainers.size();
  }

  public void setInputStream(RandomAccessInputStream stream) {
    this.stream = stream;
  }

  public void setRootIFDOffset(int rootIFDOffset) {
    this.rootIFDOffset = rootIFDOffset;
  }

  public RandomAccessInputStream getDecompressedImage(){
    // TODO Fill in logic
    return null;
  }

  public JXRMetadata extractMetadata() throws IOException {
    findAllIFDs();

    JXRMetadata metadata = new JXRMetadata();
    for (IFDContainer container : IFDContainers) {
      stream.seek(container.getOffsetSkipEntryCount());
      for (int i=0; i < container.getNumberOfEntries(); i++) {
        parseEntryInto(metadata);
      }
    }
    return metadata;
  }

  public void findAllIFDs() throws IOException {
    checkStreamAndOffset();

    short IFDEntryCount = 0;
    int nextIFDOffset = rootIFDOffset;

    do {
      stream.seek(nextIFDOffset);
      IFDEntryCount = stream.readShort();
      IFDContainers.add(new IFDContainer(nextIFDOffset, IFDEntryCount));
      stream.seek(nextIFDOffset + IFD.ENTRIES_COUNT_SIZE +
          IFDEntryCount*IFD.ENTRY_SIZE);
      nextIFDOffset = stream.read();
    } while (nextIFDOffset != 0 && nextIFDOffset < stream.length());
  }

  private void checkStreamAndOffset()
      throws IOException, IllegalStateException {
    if (stream == null) {
      throw new IllegalStateException("Input stream has not been set.");
    }
    if (rootIFDOffset == 0 || rootIFDOffset > stream.length()) {
      throw new IllegalStateException(
          String.format("Invalid offset supplied. Stream length: %d, offset: %d.",
              stream.length(), rootIFDOffset));
    }
  }

  private void parseEntryInto(JXRMetadata metadata) throws IOException {
    // TODO: This doesn't work yet. If the method returned from the translator
    // is readByte(), then we don't end up reading 12 bytes, hence the
    // stream pointer is still in the same entry...

    IFDElement element = IFDElement.valueOf(stream.readShort());
    IFDElementType elementType = IFDElementType.valueOf(stream.readShort());
    int count = stream.readInt();

    Object value = null;
    try {
      value = translator.toStreamMethod(elementType, RandomAccessInputStream.class).invoke(stream);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }

    metadata.put(element, value);
  }

  @Override
  public String toString() {
    return "JXRParser [rootIFDOffset=" + rootIFDOffset + ", "
        + "IFDContainers.size()=" + IFDContainers.size() + "]";
  }

}
