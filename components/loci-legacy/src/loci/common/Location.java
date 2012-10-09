/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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

package loci.common;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import loci.common.adapter.IRandomAccessAdapter;
import loci.legacy.adapter.AdapterTools;

// HACK: for scan-deps.pl: The following packages are not actually "optional":
// optional org.apache.log4j, optional org.slf4j.impl

/**
 * A legacy delegator class for ome.scifio.io.Location.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/Location.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/Location.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class Location {

  // -- Constants --

  // -- Static fields --

  // -- Fields --

  protected ome.scifio.io.Location loc;

  // -- Constructors --

  public Location(String pathname) {
    loc = new ome.scifio.io.Location(pathname);
  }

  public Location(File file) {
    loc = new ome.scifio.io.Location(file);
  }

  public Location(String parent, String child) {
    loc = new ome.scifio.io.Location(parent, child);
  }

  public Location(Location parent, String child) {
    loc = new ome.scifio.io.Location(parent.getAbsolutePath(), child);
  }
  
  // Private constructor for directly wrapping ome.scifio.io.Location
  private Location(ome.scifio.io.Location location) {
    loc = location;
  }
  
  // Explicit zero-param protected constructor for extending delegator classes
  protected Location() {
  }

  // -- Location API methods --

  /**
   * Clear all caches and reset cache-related bookkeeping variables to their
   * original values.
   */
  public static void reset() {
    ome.scifio.io.Location.reset();
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
    ome.scifio.io.Location.cacheDirectoryListings(cache);
  }

  /**
   * Cache directory listings for this many seconds before relisting.
   *
   * @param sec - use the cache if a directory list was done within this many
   * seconds.
   */
  public static void setCacheDirectoryTimeout(double sec) {
    ome.scifio.io.Location.setCacheDirectoryTimeout(sec);
  }

  /**
   * Clear the directory listings cache.
   *
   * Do this if directory contents might have changed in a significant way.
   */
  public static void clearDirectoryListingsCache() {
    ome.scifio.io.Location.clearDirectoryListingsCache();
  }

  /**
   * Remove any cached directory listings that have expired.
   */
  public static void cleanStaleCacheEntries() {
    ome.scifio.io.Location.cleanStaleCacheEntries();
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
    ome.scifio.io.Location.mapId(id, filename);
  }

  /** Maps the given id to the given IRandomAccess object. */
  public static void mapFile(String id, IRandomAccess ira) {
    ome.scifio.io.Location.mapFile(id, AdapterTools.getAdapter(IRandomAccessAdapter.class).getModern(ira));
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
    return ome.scifio.io.Location.getMappedId(id);
  }

  /** Gets the random access handle for the given id. */
  public static IRandomAccess getMappedFile(String id) {
    return AdapterTools.getAdapter(IRandomAccessAdapter.class).getLegacy(
        ome.scifio.io.Location.getMappedFile(id));
  }

  /** Return the id mapping. */
  public static HashMap<String, Object> getIdMap() { 
    return ome.scifio.io.Location.getIdMap();
  }

  /**
   * Set the id mapping using the given HashMap.
   *
   * @throws IllegalArgumentException if the given HashMap is null.
   */
  public static void setIdMap(HashMap<String, Object> map) {
    ome.scifio.io.Location.setIdMap(map);
  }

  /**
   * Gets an IRandomAccess object that can read from the given file.
   * @see IRandomAccess
   */
  public static IRandomAccess getHandle(String id) throws IOException {
    return AdapterTools.getAdapter(IRandomAccessAdapter.class).getLegacy(
        ome.scifio.io.Location.getHandle(id));
  }

  /**
   * Gets an IRandomAccess object that can read from or write to the given file.
   * @see IRandomAccess
   */
  public static IRandomAccess getHandle(String id, boolean writable)
    throws IOException
  {
    return AdapterTools.getAdapter(IRandomAccessAdapter.class).getLegacy(
        ome.scifio.io.Location.getHandle(id, writable, true));
  }

  /**
   * Gets an IRandomAccess object that can read from or write to the given file.
   * @see IRandomAccess
   */
  public static IRandomAccess getHandle(String id, boolean writable,
    boolean allowArchiveHandles) throws IOException
  {
    return AdapterTools.getAdapter(IRandomAccessAdapter.class).getLegacy(
        ome.scifio.io.Location.getHandle(id, writable, allowArchiveHandles));
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
    ome.scifio.io.Location.checkValidId(id);
  }

  /**
   * Return a list of all of the files in this directory.  If 'noHiddenFiles' is
   * set to true, then hidden files are omitted.
   *
   * @see java.io.File#list()
   */
  public String[] list(boolean noHiddenFiles) {
    return loc.list(noHiddenFiles);
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
    return loc.canRead();
  }

  /**
   * If the underlying location is a URL, this method will always return false.
   * Otherwise, it will return true iff the file exists and is writable.
   *
   * @see java.io.File#canWrite()
   */
  public boolean canWrite() {
    return loc.canWrite();
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
    return loc.createNewFile();
  }

  /**
   * Deletes this file.  If {@link #isDirectory()} returns true, then the
   * directory must be empty in order to be deleted.  URLs cannot be deleted.
   *
   * @return true if the file was successfully deleted
   * @see java.io.File#delete()
   */
  public boolean delete() {
    return loc.delete();
  }

  /**
   * Request that this file be deleted when the JVM terminates.
   * This method will do nothing if the pathname represents a URL.
   *
   * @see java.io.File#deleteOnExit()
   */
  public void deleteOnExit() {
    loc.deleteOnExit();
  }

  /**
   * @see java.io.File#equals(Object)
   * @see java.net.URL#equals(Object)
   */
  public boolean equals(Object obj) {
    return loc.equals(obj);
  }

  public int hashCode() {
    return loc.hashCode();
  }

  /**
   * Returns whether or not the pathname exists.
   * If the pathname is a URL, then existence is determined based on whether
   * or not we can successfully read content from the URL.
   *
   * @see java.io.File#exists()
   */
  public boolean exists() {
    return loc.exists();
  }

  /* @see java.io.File#getAbsoluteFile() */
  public Location getAbsoluteFile() {
    return new Location(getAbsolutePath());
  }

  /* @see java.io.File#getAbsolutePath() */
  public String getAbsolutePath() {
    return loc.getAbsolutePath();
  }

  /* @see java.io.File#getCanonicalFile() */
  public Location getCanonicalFile() throws IOException {
    return new Location(loc.getCanonicalFile().getCanonicalPath());
  }

  /**
   * Returns the canonical path to this file.
   * If the file is a URL, then the canonical path is equivalent to the
   * absolute path ({@link #getAbsolutePath()}).  Otherwise, this method
   * will delegate to {@link java.io.File#getCanonicalPath()}.
   */
  public String getCanonicalPath() throws IOException {
    return loc.getCanonicalPath();
  }

  /**
   * Returns the name of this file, i.e. the last name in the path name
   * sequence.
   *
   * @see java.io.File#getName()
   */
  public String getName() {
    return loc.getName();
  }

  /**
   * Returns the name of this file's parent directory, i.e. the path name prefix
   * and every name in the path name sequence except for the last.
   * If this file does not have a parent directory, then null is returned.
   *
   * @see java.io.File#getParent()
   */
  public String getParent() {
    return loc.getParent();
  }

  /* @see java.io.File#getParentFile() */
  public Location getParentFile() {
    return new Location(getParent());
  }

  /* @see java.io.File#getPath() */
  public String getPath() {
    return loc.getPath();
  }

  /**
   * Tests whether or not this path name is absolute.
   * If the path name is a URL, this method will always return true.
   *
   * @see java.io.File#isAbsolute()
   */
  public boolean isAbsolute() {
    return loc.isAbsolute();
  }

  /**
   * Returns true if this pathname exists and represents a directory.
   *
   * @see java.io.File#isDirectory()
   */
  public boolean isDirectory() {
    return loc.isDirectory();
  }

  /**
   * Returns true if this pathname exists and represents a regular file.
   *
   * @see java.io.File#exists()
   */
  public boolean isFile() {
    return loc.isFile();
  }

  /**
   * Returns true if the pathname is 'hidden'.  This method will always
   * return false if the pathname corresponds to a URL.
   *
   * @see java.io.File#isHidden()
   */
  public boolean isHidden() {
    return loc.isHidden();
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
    return loc.lastModified();
  }

  /**
   * @see java.io.File#length()
   * @see java.net.URLConnection#getContentLength()
   */
  public long length() {
    return loc.length();
  }

  /**
   * Return a list of file names in this directory.  Hidden files will be
   * included in the list.
   * If this is not a directory, return null.
   */
  public String[] list() {
    return loc.list();
  }

  /**
   * Return a list of absolute files in this directory.  Hidden files will
   * be included in the list.
   * If this is not a directory, return null.
   */
  public Location[] listFiles() {
    ome.scifio.io.Location[] locs = loc.listFiles();
    if(locs == null) return null;
    
    Location[] files = new Location[locs.length];
    
    for(int i = 0; i < locs.length; i++) {
      files[i] = new Location(locs[i]);
    }
    
    return files;
  }

  /**
   * Return the URL corresponding to this pathname.
   *
   * @see java.io.File#toURL()
   */
  public URL toURL() throws MalformedURLException {
    return loc.toURL();
  }
  
  /**
   * @see java.io.File#toString()
   * @see java.net.URL#toString()
   */
  public String toString() {
    return loc.toString();
  }
}
