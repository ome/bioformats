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

package ome.jxr.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import loci.common.RandomAccessInputStream;
import ome.jxr.JXRException;
import ome.jxr.constants.IFD;
import ome.jxr.ifd.IFDContainer;
import ome.jxr.ifd.IFDEntry;
import ome.jxr.ifd.IFDEntryType;
import ome.jxr.ifd.IFDMetadata;

/**
 * Parses an IFD structure and fills container and extracts entries. Provides
 * an {@link IFDMetadata} object as the result of work.
 *
 * <dl>
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 */
public final class IFDParser extends Parser {

  private List<IFDContainer> IFDContainers = new ArrayList<IFDContainer>();

  private IFDMetadata IFDMetadata;

  public IFDParser(Parser parentParser, RandomAccessInputStream stream) {
    super(parentParser, stream);
  }

  public int getIFDCount() {
    return IFDContainers.size();
  }

  public IFDMetadata getIFDMetadata() {
    return IFDMetadata;
  }

  @Override
  public void parse() throws JXRException {
    super.parse(((FileParser) getParentParser()).getRootIFDOffset());
    try {
      findAllIFDs();
      parseIFDEntries();
    } catch (IOException ioe) {
      throw new JXRException(ioe);
    }
  }

  private void findAllIFDs() throws IOException {
    IFDMetadata = new IFDMetadata(stream.length());
    short IFDEntryCount = 0;
    long nextIFDOffset = parsingOffset;

    do {
      stream.seek(nextIFDOffset);
      IFDEntryCount = stream.readShort();
      IFDContainers.add(new IFDContainer(nextIFDOffset, IFDEntryCount));
      stream.seek(nextIFDOffset + IFD.ENTRIES_COUNT_SIZE +
          IFDEntryCount*IFD.ENTRY_SIZE);
      nextIFDOffset = stream.read();
    } while (nextIFDOffset != 0 && nextIFDOffset < stream.length());
  }

  private void parseIFDEntries() throws IOException {
    for (IFDContainer container : IFDContainers) {
      for (long entryOffset : container.getEntryOffsets()) {
        stream.seek(entryOffset);
        parseEntryInto(IFDMetadata);
      }
    }
  }

  private void parseEntryInto(IFDMetadata metadata) throws IOException {
    IFDEntry entry = IFDEntry.findByTag(stream.readShort());
    IFDEntryType entryType = IFDEntryType.findByTypeCode(stream.readShort());
    int entryDataCount = stream.readInt();
    int entryValueSize = entryDataCount*entryType.getSize();

    byte[] value = new byte[entryValueSize];
    if (entryValueSize > IFD.ENTRY_VALUE_SIZE) {
      int offset = stream.readInt();
      if (offset < stream.length()) {
        stream.seek(offset);
      }
    }
    stream.read(value, 0, entryValueSize);

    metadata.put(entry, value);
  }

  public void close() throws IOException {
    super.close();
    IFDContainers = null;
    IFDMetadata = null;
  }

  @Override
  public String toString() {
    return "IFDParser [rootIFDOffset=" + parsingOffset + "]";
  }

}
