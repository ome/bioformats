/*
 * #%L
 * Common package for I/O and related utilities
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

package loci.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.MapMaker;

/**
 * Pseudo-extension of java.io.File that supports reading over HTTP (among
 * other things).
 * It is strongly recommended to use this instead of java.io.File.
 */
public class Location {

  // -- Constants --

  private static final Logger LOGGER = LoggerFactory.getLogger(Location.class);
  private static final boolean IS_WINDOWS =
    System.getProperty("os.name").startsWith("Windows");

  // -- Static fields --

  /** Map from given filenames to actual filenames. */
  private static ThreadLocal<HashMap<String, Object>> idMap =
    new ThreadLocal<HashMap<String, Object>>() {
      @Override
      protected HashMap<String, Object> initialValue() {
        return new HashMap<String, Object>();
      }
  };

  private static volatile boolean cacheListings = false;

  // By default, cache for one hour.
  private static volatile long cacheNanos = 60L * 60L * 1000L * 1000L * 1000L;

  protected class ListingsResult {
    public final String [] listing;
    public final long time;
    ListingsResult(String [] listing, long time) {
      this.listing = listing;
      this.time = time;
    }
  }
  private static final Map<String, ListingsResult> fileListings =
    new MapMaker().makeMap();  // like Java's ConcurrentHashMap

  // -- Fields --

  private boolean isURL = true;
  private URL url;
  private File file;

  // -- Constructors --

  public Location(String pathname) {
    LOGGER.trace("Location({})", pathname);
    if (pathname.contains("://")) {
      // Avoid expensive exception handling in case when path is
      // obviously not an URL
      try {
        url = new URL(getMappedId(pathname));
      }
      catch (MalformedURLException e) {
        LOGGER.trace("Location is not a URL", e);
        isURL = false;
      }
    } else {
      LOGGER.trace("Location is not a URL");
      isURL = false;
    }
    if (!isURL) file = new File(getMappedId(pathname));
  }

  public Location(File file) {
    LOGGER.trace("Location({})", file);
    isURL = false;
    this.file = file;
  }

  public Location(String parent, String child) {
    this(parent + File.separator + child);
  }

  public Location(Location parent, String child) {
    this(parent.getAbsolutePath(), child);
  }

  // -- Location API methods --

  /**
   * Clear all caches and reset cache-related bookkeeping variables to their
   * original values.
   */
  public static void reset() {
    cacheListings = false;
    cacheNanos = 60L * 60L * 1000L * 1000L * 1000L;
    fileListings.clear();
    getIdMap().clear();
  }

  /**
   * Turn cacheing of directory listings on or off.
   * Cacheing is turned off by default.
   *
   * Reasons to cache - directory listings over network shares
   * can be very expensive, especially in HCS experiments with thousands
   * of files in the same directory. Technically, if you use a directory
   * listing and then go and access the file, you are using stale information.
   * Unlike a database, there's no transactional integrity to file system
   * operations, so the directory could change by the time you access the file.
   *
   * Reasons not to cache - the contents of the directories might change
   * during the program invocation.
   *
   * @param cache - true to turn cacheing on, false to leave it off.
   */
  public static void cacheDirectoryListings(boolean cache) {
    cacheListings = cache;
  }

  /**
   * Cache directory listings for this many seconds before relisting.
   *
   * @param sec - use the cache if a directory list was done within this many
   * seconds.
   */
  public static void setCacheDirectoryTimeout(double sec) {
    cacheNanos = (long) (sec * 1000. * 1000. * 1000.);
  }

  /**
   * Clear the directory listings cache.
   *
   * Do this if directory contents might have changed in a significant way.
   */
  public static void clearDirectoryListingsCache() {
    fileListings.clear();
  }

  /**
   * Remove any cached directory listings that have expired.
   */
  public static void cleanStaleCacheEntries() {
    long t = System.nanoTime() - cacheNanos;
    final Iterator<ListingsResult> cacheValues =
      fileListings.values().iterator();
    while (cacheValues.hasNext()) {
      if (cacheValues.next().time < t) {
        cacheValues.remove();
      }
    }
  }

  /**
   * Maps the given id to an actual filename on disk. Typically actual
   * filenames are used for ids, making this step unnecessary, but in some
   * cases it is useful; e.g., if the file has been renamed to conform to a
   * standard naming scheme and the original file extension is lost, then
   * using the original filename as the id assists format handlers with type
   * identification and pattern matching, and the id can be mapped to the
   * actual filename for reading the file's contents.
   * @see #getMappedId(String)
   */
  public static void mapId(String id, String filename) {
    if (id == null) return;
    if (filename == null) getIdMap().remove(id);
    else getIdMap().put(id, filename);
    LOGGER.debug("Location.mapId: {} -> {}", id, filename);
  }

  /** Maps the given id to the given IRandomAccess object. */
  public static void mapFile(String id, IRandomAccess ira) {
    if (id == null) return;
    if (ira == null) getIdMap().remove(id);
    else getIdMap().put(id, ira);
    LOGGER.debug("Location.mapFile: {} -> {}", id, ira);
  }

  /**
   * Gets the actual filename on disk for the given id. Typically the id itself
   * is the filename, but in some cases may not be; e.g., if OMEIS has renamed
   * a file from its original name to a standard location such as Files/101,
   * the original filename is useful for checking the file extension and doing
   * pattern matching, but the renamed filename is required to read its
   * contents.
   * @see #mapId(String, String)
   */
  public static String getMappedId(String id) {
    if (getIdMap() == null) return id;
    String filename = null;
    if (id != null && (getIdMap().get(id) instanceof String)) {
      filename = (String) getIdMap().get(id);
    }
    return filename == null ? id : filename;
  }

  /** Gets the random access handle for the given id. */
  public static IRandomAccess getMappedFile(String id) {
    if (getIdMap() == null) return null;
    IRandomAccess ira = null;
    if (id != null && (getIdMap().get(id) instanceof IRandomAccess)) {
      ira = (IRandomAccess) getIdMap().get(id);
    }
    return ira;
  }

  /** Return the id mapping. */
  public static HashMap<String, Object> getIdMap() {
    return idMap.get();
  }

  /**
   * Set the id mapping using the given HashMap.
   *
   * @throws IllegalArgumentException if the given HashMap is null.
   */
  public static void setIdMap(HashMap<String, Object> map) {
    if (map == null) throw new IllegalArgumentException("map cannot be null");
    idMap.set(map);
  }

  /**
   * Gets an IRandomAccess object that can read from the given file.
   * @see IRandomAccess
   */
  public static IRandomAccess getHandle(String id) throws IOException {
    return getHandle(id, false);
  }

  /**
   * Gets an IRandomAccess object that can read from or write to the given file.
   * @see IRandomAccess
   */
  public static IRandomAccess getHandle(String id, boolean writable)
    throws IOException
  {
    return getHandle(id, writable, true);
  }

  /**
   * Gets an IRandomAccess object that can read from or write to the given file.
   * @see IRandomAccess
   */
  public static IRandomAccess getHandle(String id, boolean writable,
    boolean allowArchiveHandles) throws IOException
  {
    return getHandle(id, writable, allowArchiveHandles, 0);
  }

  public static IRandomAccess getHandle(String id, boolean writable,
    boolean allowArchiveHandles, int bufferSize) throws IOException
  {
    LOGGER.trace("getHandle(id = {}, writable = {})", id, writable);
    IRandomAccess handle = getMappedFile(id);
    if (handle == null) {
      LOGGER.trace("no handle was mapped for this ID");
      String mapId = getMappedId(id);

      if (id.startsWith("http://") || id.startsWith("https://")) {
        handle = new URLHandle(mapId);
      }
      else if (allowArchiveHandles && ZipHandle.isZipFile(mapId)) {
        handle = new ZipHandle(mapId);
      }
      else if (allowArchiveHandles && GZipHandle.isGZipFile(mapId)) {
        handle = new GZipHandle(mapId);
      }
      else if (allowArchiveHandles && BZip2Handle.isBZip2File(mapId)) {
        handle = new BZip2Handle(mapId);
      }
      else {
        if (bufferSize > 0) {
          handle = new NIOFileHandle(
            new File(mapId), writable ? "rw" : "r", bufferSize);
        }
        else {
          handle = new NIOFileHandle(mapId, writable ? "rw" : "r");
        }
      }
    }
    LOGGER.trace("Location.getHandle: {} -> {}", id, handle);
    return handle;
  }

  /**
   * Checks that the given id points at a valid data stream.
   *
   * @param id
   *          The id string to validate.
   * @throws IOException
   *           if the id is not valid.
   */
  public static void checkValidId(String id) throws IOException {
    if (getMappedFile(id) != null) {
      // NB: The id maps directly to an IRandomAccess handle, so is valid. Do
      // not destroy an existing mapped IRandomAccess handle by closing it.
      return;
    }
    // NB: Try to actually open a handle to make sure it is valid. Close it
    // afterward so we don't leave it dangling. The process of doing this will
    // throw IOException if something goes wrong.
    Location.getHandle(id).close();
  }

  /**
   * Return a list of all of the files in this directory.  If 'noHiddenFiles' is
   * set to true, then hidden files are omitted.
   *
   * @see java.io.File#list()
   */
  public String[] list(boolean noHiddenFiles) {
    LOGGER.trace("list({})", noHiddenFiles);
    String key = getAbsolutePath() + Boolean.toString(noHiddenFiles);
    String [] result = null;
    if (cacheListings) {
      cleanStaleCacheEntries();
      ListingsResult listingsResult = fileListings.get(key);
      if (listingsResult != null) {
        return listingsResult.listing;
      }
    }
    final List<String> files = new ArrayList<String>();
    if (isURL) {
      try {
        URLConnection c = url.openConnection();
        InputStream is = c.getInputStream();
        boolean foundEnd = false;
        BufferedReader br = new BufferedReader(
              new InputStreamReader(is, Constants.ENCODING));
        String input;
        StringBuffer buffer = new StringBuffer();
        while ((input = br.readLine()) != null){
          buffer.append(input);
        }
        br.close();
        String s = buffer.toString();
        while (!foundEnd) {
         if (s.toLowerCase().indexOf("</html>") != -1) foundEnd = true;

          while (s.indexOf("a href") != -1) {
            int ndx = s.indexOf("a href") + 8;
            int idx = s.indexOf("\"", ndx);
            if (idx < 0) break;
            String f = s.substring(ndx, idx);
            if (files.size() > 0 && f.startsWith("/")) {
              return null;
            }
            s = s.substring(idx + 1);
            if (f.startsWith("?")) continue;
            Location check = new Location(getAbsolutePath(), f);
            if (check.exists() && (!noHiddenFiles || !check.isHidden())) {
              files.add(check.getName());
            }
          }
        }
        is.close();
        if (files.size() == 0) {
          return null;
        }
      }
      catch (IOException e) {
        LOGGER.trace("Could not retrieve directory listing", e);
        return null;
      }
    }
    else {
      if (file == null) return null;
      String[] f = file.list();
      if (f == null) return null;
      String path = file.getAbsolutePath();
      for (String name : f) {
        if (!noHiddenFiles || !(name.startsWith(".") ||
          new Location(path, name).isHidden()))
        {
          files.add(name);
        }
      }
    }

    result = files.toArray(new String[files.size()]);
    if (cacheListings) {
      fileListings.put(key, new ListingsResult(result, System.nanoTime()));
    }
    LOGGER.trace("  returning {} files", files.size());
    return result;
  }

  // -- File API methods --

  /**
   * If the underlying location is a URL, this method will return true if
   * the URL exists.
   * Otherwise, it will return true iff the file exists and is readable.
   *
   * @see java.io.File#canRead()
   */
  public boolean canRead() {
    LOGGER.trace("canRead()");
    return isURL ? (isDirectory() || isFile() || exists()) : file.canRead();
  }

  /**
   * If the underlying location is a URL, this method will always return false.
   * Otherwise, it will return true iff the file exists and is writable.
   *
   * @see java.io.File#canWrite()
   */
  public boolean canWrite() {
    LOGGER.trace("canWrite()");
    return isURL ? false : file.canWrite();
  }

  /**
   * Creates a new empty file named by this Location's path name iff a file
   * with this name does not already exist.  Note that this operation is
   * only supported if the path name can be interpreted as a path to a file on
   * disk (i.e. is not a URL).
   *
   * @return true if the file was created successfully
   * @throws IOException if an I/O error occurred, or the
   *   abstract pathname is a URL
   * @see java.io.File#createNewFile()
   */
  public boolean createNewFile() throws IOException {
    if (isURL) throw new IOException("Unimplemented");
    return file.createNewFile();
  }

  /**
   * Creates a directory structures described by this Location's internal
   * {@link File} instance.
   *
   * @return <code>true</code> if the directory structure was created
   *   successfully.
   * @see File#mkdirs()
   */
  public boolean mkdirs() {
    if (file == null) {
      return false;
    }
    return file.mkdirs();
  }

  /**
   * Deletes this file.  If {@link #isDirectory()} returns true, then the
   * directory must be empty in order to be deleted.  URLs cannot be deleted.
   *
   * @return true if the file was successfully deleted
   * @see java.io.File#delete()
   */
  public boolean delete() {
    return isURL ? false : file.delete();
  }

  /**
   * Request that this file be deleted when the JVM terminates.
   * This method will do nothing if the pathname represents a URL.
   *
   * @see java.io.File#deleteOnExit()
   */
  public void deleteOnExit() {
    if (!isURL) file.deleteOnExit();
  }

  /**
   * @see java.io.File#equals(Object)
   * @see java.net.URL#equals(Object)
   */
  @Override
  public boolean equals(Object obj) {
    String absPath = getAbsolutePath();
    String thatPath = null;

    if (obj instanceof Location) {
      thatPath = ((Location) obj).getAbsolutePath();
    }
    else {
      thatPath = obj.toString();
    }

    return absPath.equals(thatPath);
  }

  @Override
  public int hashCode() {
    return getAbsolutePath().hashCode();
  }

  /**
   * Returns whether or not the pathname exists.
   * If the pathname is a URL, then existence is determined based on whether
   * or not we can successfully read content from the URL.
   *
   * @see java.io.File#exists()
   */
  public boolean exists() {
    LOGGER.trace("exists()");
    if (isURL) {
      try {
        url.getContent();
        return true;
      }
      catch (IOException e) {
        LOGGER.trace("Failed to retrieve content from URL", e);
        return false;
      }
    }
    if (file.exists()) return true;
    if (getMappedFile(file.getPath()) != null) return true;

    String mappedId = getMappedId(file.getPath());
    return mappedId != null && new File(mappedId).exists();
  }

  /* @see java.io.File#getAbsoluteFile() */
  public Location getAbsoluteFile() {
    return new Location(getAbsolutePath());
  }

  /* @see java.io.File#getAbsolutePath() */
  public String getAbsolutePath() {
    LOGGER.trace("getAbsolutePath()");
    return isURL ? url.toExternalForm() : file.getAbsolutePath();
  }

  /* @see java.io.File#getCanonicalFile() */
  public Location getCanonicalFile() throws IOException {
    return isURL ? getAbsoluteFile() : new Location(file.getCanonicalFile());
  }

  /**
   * Returns the canonical path to this file.
   * If the file is a URL, then the canonical path is equivalent to the
   * absolute path ({@link #getAbsolutePath()}).  Otherwise, this method
   * will delegate to {@link java.io.File#getCanonicalPath()}.
   */
  public String getCanonicalPath() throws IOException {
    return isURL ? getAbsolutePath() : file.getCanonicalPath();
  }

  /**
   * Returns the name of this file, i.e. the last name in the path name
   * sequence.
   *
   * @see java.io.File#getName()
   */
  public String getName() {
    LOGGER.trace("getName()");
    if (isURL) {
      String name = url.getFile();
      name = name.substring(name.lastIndexOf("/") + 1);
      return name;
    }
    return file.getName();
  }

  /**
   * Returns the name of this file's parent directory, i.e. the path name prefix
   * and every name in the path name sequence except for the last.
   * If this file does not have a parent directory, then null is returned.
   *
   * @see java.io.File#getParent()
   */
  public String getParent() {
    LOGGER.trace("getParent()");
    if (isURL) {
      String absPath = getAbsolutePath();
      absPath = absPath.substring(0, absPath.lastIndexOf("/"));
      return absPath;
    }
    return file.getParent();
  }

  /* @see java.io.File#getParentFile() */
  public Location getParentFile() {
    return new Location(getParent());
  }

  /* @see java.io.File#getPath() */
  public String getPath() {
    return isURL ? url.getHost() + url.getPath() : file.getPath();
  }

  /**
   * Tests whether or not this path name is absolute.
   * If the path name is a URL, this method will always return true.
   *
   * @see java.io.File#isAbsolute()
   */
  public boolean isAbsolute() {
    LOGGER.trace("isAbsolute()");
    return isURL ? true : file.isAbsolute();
  }

  /**
   * Returns true if this pathname exists and represents a directory.
   *
   * @see java.io.File#isDirectory()
   */
  public boolean isDirectory() {
    LOGGER.trace("isDirectory()");
    if (isURL) {
      String[] list = list();
      return list != null;
    }
    return file.isDirectory();
  }

  /**
   * Returns true if this pathname exists and represents a regular file.
   *
   * @see java.io.File#exists()
   */
  public boolean isFile() {
    LOGGER.trace("isFile()");
    return isURL ? (!isDirectory() && exists()) : file.isFile();
  }

  /**
   * Returns true if the pathname is 'hidden'.  This method will always
   * return false if the pathname corresponds to a URL.
   *
   * @see java.io.File#isHidden()
   */
  public boolean isHidden() {
    LOGGER.trace("isHidden()");
    if (isURL) {
      return false;
    }
    boolean dotFile = file.getName().startsWith(".");
    if (IS_WINDOWS) {
      return dotFile || file.isHidden();
    }
    return dotFile;
  }

  /**
   * Return the last modification time of this file, in milliseconds since
   * the UNIX epoch.
   * If the file does not exist, 0 is returned.
   *
   * @see java.io.File#lastModified()
   * @see java.net.URLConnection#getLastModified()
   */
  public long lastModified() {
    LOGGER.trace("lastModified()");
    if (isURL) {
      try {
        return url.openConnection().getLastModified();
      }
      catch (IOException e) {
        LOGGER.trace("Could not determine URL's last modification time", e);
        return 0;
      }
    }
    return file.lastModified();
  }

  /**
   * @see java.io.File#length()
   * @see java.net.URLConnection#getContentLength()
   */
  public long length() {
    LOGGER.trace("length()");
    if (isURL) {
      try {
        return url.openConnection().getContentLength();
      }
      catch (IOException e) {
        LOGGER.trace("Could not determine URL's content length", e);
        return 0;
      }
    }
    return file.length();
  }

  /**
   * Return a list of file names in this directory.  Hidden files will be
   * included in the list.
   * If this is not a directory, return null.
   */
  public String[] list() {
    return list(false);
  }

  /**
   * Return a list of absolute files in this directory.  Hidden files will
   * be included in the list.
   * If this is not a directory, return null.
   */
  public Location[] listFiles() {
    String[] s = list();
    if (s == null) return null;
    Location[] f = new Location[s.length];
    for (int i=0; i<f.length; i++) {
      f[i] = new Location(getAbsolutePath(), s[i]);
      f[i] = f[i].getAbsoluteFile();
    }
    return f;
  }

  /**
   * Return the URL corresponding to this pathname.
   *
   * @see java.io.File#toURL()
   */
  public URL toURL() throws MalformedURLException {
    return isURL ? url : file.toURI().toURL();
  }

  /**
   * @see java.io.File#toString()
   * @see java.net.URL#toString()
   */
  @Override
  public String toString() {
    return isURL ? url.toString() : file.toString();
  }

}
