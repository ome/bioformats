/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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

package loci.formats.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import loci.common.RandomAccessInputStream;
import loci.common.services.AbstractService;
import loci.poi.poifs.filesystem.DirectoryEntry;
import loci.poi.poifs.filesystem.DocumentEntry;
import loci.poi.poifs.filesystem.DocumentInputStream;
import loci.poi.poifs.filesystem.Entry;
import loci.poi.poifs.filesystem.POIFSFileSystem;

/**
 */
public class POIServiceImpl extends AbstractService implements POIService {

  private static final int MAX_BLOCK_SIZE = 4096;

  // -- Fields --

  private POIFSFileSystem fileSystem;
  private DirectoryEntry root;
  private RandomAccessInputStream stream;

  private Vector<String> filePath;
  private Hashtable<String, Integer> fileSizes;
  private Hashtable<String, DocumentEntry> files;

  // -- POIService API methods --

  /**
   * Default constructor.
   */
  public POIServiceImpl() {
    // Just checking a single class in loci.poi.poifs.filesystem package
    checkClassDependency(loci.poi.poifs.filesystem.DirectoryEntry.class);
  }

  /* @see POIService#initialize(String) */
  @Override
  public void initialize(String file) throws IOException {
    initialize(new RandomAccessInputStream(file, MAX_BLOCK_SIZE));
  }

  /* @see POIService#initialize(RandomAccessInputStream) */
  @Override
  public void initialize(RandomAccessInputStream s) throws IOException {
    // determine the size of a 'big' block
    stream = s;
    stream.order(true);
    stream.seek(30);
    int size = (int) Math.pow(2, stream.readShort());
    stream.seek(0);

    // initialize the file system
    fileSystem = new POIFSFileSystem(stream, size);
    root = fileSystem.getRoot();

    // build the list of files in the file system
    filePath = new Vector<String>();
    fileSizes = new Hashtable<String, Integer>();
    files = new Hashtable<String, DocumentEntry>();

    parseFile(root);
  }

  /* @see POIService#getInputStream(String) */
  @Override
  public InputStream getInputStream(String file) throws IOException {
    return new DocumentInputStream(files.get(file), stream);
  }

  /* @see POIService#getDocumentStream(String) */
  @Override
  public RandomAccessInputStream getDocumentStream(String file)
    throws IOException
  {
    return new RandomAccessInputStream(getDocumentBytes(file));
  }

  /* @see POIService#getDocumentBytes(String) */
  @Override
  public byte[] getDocumentBytes(String file) throws IOException {
    return getDocumentBytes(file, getFileSize(file));
  }

  /* @see POIService#getDocumentBytes(String, int) */
  @Override
  public byte[] getDocumentBytes(String file, int length) throws IOException {
    int size = getFileSize(file);
    int len = length > size ? size : length;

    byte[] buf = new byte[len];
    InputStream s = getInputStream(file);
    s.read(buf);
    s.close();
    return buf;
  }

  /* @see POIService#getFileSize(String) */
  @Override
  public int getFileSize(String file) {
    if (fileSizes.containsKey(file)) {
      return fileSizes.get(file);
    }
    return -1;
  }

  /* @see POIService#getDocumentList() */
  @Override
  public Vector<String> getDocumentList() {
    Vector<String> list = new Vector<String>();
    list.addAll(fileSizes.keySet());
    return list;
  }

  /* @see POIService#close() */
  @Override
  public void close() throws IOException {
    fileSystem = null;
    root = null;
    if (stream != null) stream.close();
    stream = null;
    filePath = null;
    fileSizes = null;
    files = null;
  }

  // -- Helper methods --

  private void parseFile(DirectoryEntry r) throws IOException {
    filePath.add(r.getName());
    Iterator<?> iter = r.getEntries();

    while (iter.hasNext()) {
      Entry o = (Entry) iter.next();
      boolean isInstance = o.isDirectoryEntry();
      boolean isDocument = o.isDocumentEntry();

      if (isInstance) parseFile((DirectoryEntry) o);
      else if (isDocument) {
        final StringBuilder path = new StringBuilder();
        for (String p : filePath) {
          path.append(p);
          path.append(File.separator);
        }
        path.append(o.getName());

        DocumentInputStream s =
          new DocumentInputStream((DocumentEntry) o, stream);
        fileSizes.put(path.toString(), s.available());
        files.put(path.toString(), (DocumentEntry) o);
        s.close();
      }
    }
    filePath.removeElementAt(filePath.size() - 1);
  }

}
