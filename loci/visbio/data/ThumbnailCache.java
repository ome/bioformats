//
// ThumbnailCache.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2004 Curtis Rueden.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Library General Public
License as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Library General Public License for more details.

You should have received a copy of the GNU Library General Public
License along with this library; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA 02111-1307, USA
*/

package loci.visbio.data;

import java.io.*;
import java.util.Vector;
import visad.FlatField;
import visad.VisADException;
import visad.data.visad.BinaryReader;
import visad.data.visad.BinaryWriter;

/** Disk cache for thumbnails generated from datasets. */
public class ThumbnailCache {

  // -- Constants --

  /** Default cache file to use if supplied cache file is not available. */
  private static final File DEFAULT_CACHE = new File("cache.visbio");

  // -- Fields --

  /** File containing thumbnail cache data. */
  protected File file;

  /** Thumbnail id strings. */
  protected Vector ids;

  /** Thumbnail byte offsets. */
  protected Vector offsets;

  /** Last retrieved thumbnail index. */
  protected int last;


  // -- Constructor --

  /** Constructs a thumbnail cache that uses the given disk file. */
  public ThumbnailCache(String filename) {
    file = new File(filename);
    ids = new Vector();
    offsets = new Vector();

    // read in existing id/offset pairs
    try {
      if (!file.exists()) {
        boolean success = true;
        try { file.createNewFile(); }
        catch (IOException exc) { success = false; }
        catch (SecurityException exc) { success = false; }
        if (!success) {
          // supplied file is unavailable; use default cache file instead
          file = DEFAULT_CACHE;
          if (!file.exists()) file.createNewFile();
        }
      }
      RandomAccessFile raf = new RandomAccessFile(file, "r");
      long offset = 0;
      while (true) {
        try {
          int idLen = raf.readInt();
          byte[] buf = new byte[idLen];
          raf.readFully(buf);
          String id = new String(buf);
          int size = raf.readInt();
          ids.add(id);
          offsets.add(new Long(offset));
          offset += idLen + size + 8;
          raf.seek(offset);
        }
        catch (EOFException exc) { break; }
        catch (Exception exc) {
          // something went horribly wrong; assume cache is corrupt & purge it
          System.err.println("Purging corrupt cache file " + file);
          try { raf.close(); }
          catch (IOException exc2) { exc2.printStackTrace(); }
          clear();
          break;
        }
      }
      raf.close();
    }
    catch (IOException exc) { exc.printStackTrace(); }
  }


  // -- API methods --

  /** Retrieves the thumbnail with the given id string from the disk cache. */
  public FlatField retrieve(String id) {
    long offset = getOffset(id);
    if (offset < 0) return null;
    try { return load(offset); }
    catch (IOException exc) {
      exc.printStackTrace();
      return null;
    }
  }

  /** Stores the given thumbnail in the disk cache. */
  public void store(String id, FlatField thumb) {
    // append thumbnail to the data file
    try { save(id, thumb); }
    catch (IOException exc) { exc.printStackTrace(); }
  }

  /** Wipes the thumbnail disk cache. */
  public void clear() {
    try {
      file.delete();
      file.createNewFile();
    }
    catch (IOException exc) { exc.printStackTrace(); }
  }

  /** Gets thumbnail cache disk usage in bytes. */
  public long getUsage() { return file.length(); }

  /** Gets the number of thumbnails in the disk cache. */
  public int getThumbCount() { return ids.size(); }

  /** Gets the disk cache file. */
  public File getCacheFile() { return file; }

  /**
   * Gets whether the default cache file was used because
   * the one supplied to the constructor was unavailable.
   */
  public boolean isDefault() { return file.equals(DEFAULT_CACHE); }


  // -- Helper methods --

  /** Gets the offset corresponding to the given id string. */
  protected long getOffset(String id) {
    int size = ids.size();
    for (int i=0; i<size; i++) {
      String s = (String) ids.elementAt(i);
      if (s.equals(id)) return ((Long) offsets.elementAt(i)).longValue();
    }
    return -1;
  }

  /** Saves the given data object to the end of the cache file. */
  protected void save(String id, FlatField thumb) throws IOException {
    byte[] idBytes = id.getBytes();
    long offset = file.length();

    // convert image data into byte array
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    BinaryWriter fout = new BinaryWriter(bout);
    try { fout.save(thumb); }
    catch (VisADException exc) { exc.printStackTrace(); }
    fout.close();
    byte[] image = bout.toByteArray();

    // add id/offset pair to the list
    ids.add(id);
    offsets.add(new Long(offset));

    // write data to cache file
    RandomAccessFile raf = new RandomAccessFile(file, "rw");
    raf.seek(offset);
    raf.writeInt(idBytes.length);
    raf.write(idBytes);
    raf.writeInt(image.length);
    raf.write(image);
    raf.close();
  }

  /** Loads the data object at the given byte offset of the cache file. */
  protected FlatField load(long offset) throws IOException {
    RandomAccessFile raf = new RandomAccessFile(file, "r");
    raf.seek(offset);
    int idLen = raf.readInt();
    raf.skipBytes(idLen); // skip id string

    // read in image bytes
    int length = raf.readInt();
    byte[] bytes = new byte[length];
    raf.readFully(bytes);
    raf.close();

    // convert image bytes to FlatField object
    BinaryReader fin = new BinaryReader(new ByteArrayInputStream(bytes));
    FlatField thumb;
    try { thumb = (FlatField) fin.getData(); }
    catch (ClassCastException exc) { thumb = null; }
    catch (VisADException exc) { thumb = null; }
    fin.close();

    return thumb;
  }

}
