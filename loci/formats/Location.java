//
// Location.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats;

import java.io.*;
import java.net.*;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Pseudo-extension of java.io.File that supports reading over HTTP.
 * It is strongly recommended that you use this instead of java.io.File.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/Location.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/Location.java">SVN</a></dd></dl>
 */
public class Location {

  // -- Static fields --

  /** Map from given filenames to actual filenames. */
  private static Hashtable idMap = new Hashtable();

  // -- Fields --

  private boolean isURL = true;
  private URL url;
  private File file;

  // -- Constructors --

  public Location(String pathname) {
    try {
      url = new URL(getMappedId(pathname));
    }
    catch (MalformedURLException e) {
      isURL = false;
    }
    if (!isURL) file = new File(getMappedId(pathname));
  }

  public Location(File file) {
    isURL = false;
    this.file = file;
  }

  public Location(String parent, String child) {
    this(parent + "/" + child);
  }

  public Location(Location parent, String child) {
    this(parent.getAbsolutePath(), child);
  }

  // -- Location API methods --

  /**
   * Maps the given id to the actual filename on disk. Typically actual
   * filenames are used for ids, making this step unnecessary, but in some
   * cases it is useful; e.g., if the file has been renamed to conform to a
   * standard naming scheme and the original file extension is lost, then
   * using the original filename as the id assists format handlers with type
   * identification and pattern matching, and the id can be mapped to the
   * actual filename for reading the file's contents.
   * @see #getMappedId(String)
   */
  public static void mapId(String id, String filename) {
    if (idMap == null) idMap = new Hashtable();
    if (id == null) return;
    if (filename == null) idMap.remove(id);
    else idMap.put(id, filename);
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
    if (idMap == null) return id;
    String filename = id == null ? null : (String) idMap.get(id);
    return filename == null ? id : filename;
  }

  public static Hashtable getIdMap() { return idMap; }

  public static void setIdMap(Hashtable map) {
    if (map != null) idMap = map;
    else idMap = new Hashtable();
  }

  // -- File API methods --

  /* @see java.io.File#canRead() */
  public boolean canRead() {
    return isURL ? true : file.canRead();
  }

  /* @see java.io.File#canWrite() */
  public boolean canWrite() {
    return isURL ? false : file.canWrite();
  }

  /* @see java.io.File#createNewFile() */
  public boolean createNewFile() throws IOException {
    if (isURL) throw new IOException("Unimplemented");
    return file.createNewFile();
  }

  /* @see java.io.File#delete() */
  public boolean delete() {
    return isURL ? false : file.delete();
  }

  /* @see java.io.File#deleteOnExit() */
  public void deleteOnExit() {
    if (!isURL) file.deleteOnExit();
  }

  /* @see java.io.File#equals(Object) */
  public boolean equals(Object obj) {
    return isURL ? url.equals(obj) : file.equals(obj);
  }

  /* @see java.io.File#exists() */
  public boolean exists() {
    if (isURL) {
      try {
        url.getContent();
        return true;
      }
      catch (IOException e) {
        return false;
      }
    }
    return file.exists();
  }

  /* @see java.io.File#getAbsoluteFile() */
  public Location getAbsoluteFile() {
    return new Location(getAbsolutePath());
  }

  /* @see java.io.File#getAbsolutePath() */
  public String getAbsolutePath() {
    return isURL ? url.toExternalForm() : file.getAbsolutePath();
  }

  /* @see java.io.File#getCanonicalFile() */
  public Location getCanonicalFile() throws IOException {
    return getAbsoluteFile();
  }

  /* @see java.io.File#getCanonicalPath() */
  public String getCanonicalPath() throws IOException {
    return isURL ? getAbsolutePath() : file.getCanonicalPath();
  }

  /* @see java.io.File#getName() */
  public String getName() {
    if (isURL) {
      String name = url.getFile();
      name = name.substring(name.lastIndexOf("/") + 1);
      return name;
    }
    return file.getName();
  }

  /* @see java.io.File#getParent() */
  public String getParent() {
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

  /* @see java.io.File#isAbsolute() */
  public boolean isAbsolute() {
    return isURL ? true : file.isAbsolute();
  }

  /* @see java.io.File#isDirectory() */
  public boolean isDirectory() {
    return isURL ? lastModified() == 0 : file.isDirectory();
  }

  /* @see java.io.File#isFile() */
  public boolean isFile() {
    return isURL ? lastModified() > 0 : file.isFile();
  }

  /* @see java.io.File#isHidden() */
  public boolean isHidden() {
    return isURL ? false : file.isHidden();
  }

  /* @see java.io.File#lastModified() */
  public long lastModified() {
    if (isURL) {
      try {
        return url.openConnection().getLastModified();
      }
      catch (IOException e) { return 0; }
    }
    return file.lastModified();
  }

  /* @see java.io.File#length() */
  public long length() {
    if (isURL) {
      try {
        return url.openConnection().getContentLength();
      }
      catch (IOException e) { return 0; }
    }
    return file.length();
  }

  /* @see java.io.File#list() */
  public String[] list() {
    if (isURL) {
      if (!isDirectory()) return null;
      try {
        URLConnection c = url.openConnection();
        InputStream is = c.getInputStream();
        boolean foundEnd = false;

        Vector files = new Vector();
        while (!foundEnd) {
          byte[] b = new byte[is.available()];
          String s = new String(b);
          if (s.toLowerCase().indexOf("</html>") != -1) foundEnd = true;

          while (s.indexOf("a href") != -1) {
            int ndx = s.indexOf("a href") + 8;
            String f = s.substring(ndx, s.indexOf("\"", ndx));
            s = s.substring(s.indexOf("\"", ndx) + 1);
            Location check = new Location(getAbsolutePath(), f);
            if (check.exists()) {
              files.add(check.getName());
            }
          }
        }
        return (String[]) files.toArray(new String[0]);
      }
      catch (IOException e) {
        return null;
      }
    }
    return file.list();
  }

  /* @see java.io.File#listFiles() */
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

  /* @see java.io.File#toURL() */
  public URL toURL() throws MalformedURLException {
    return isURL ? url : file.toURI().toURL();
  }

  // -- Object API methods --

  /* @see java.lang.Object#toString() */
  public String toString() {
    return isURL ? url.toString() : file.toString();
  }

}
