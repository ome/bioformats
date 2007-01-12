//
// FileWrapper.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

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
import java.util.Vector;

/** 
 * Extension of java.io.File that supports reading over HTTP.
 * Note that it is not a good idea to construct a FileInputStream using a
 * FileWrapper, unless you are certain that the filename points to a file 
 * on disk.
 */
public class FileWrapper extends File {

  // -- Fields --
  
  private boolean isURL = true;
  private String path;
  private URL url;

  // -- Constructors --

  public FileWrapper(String pathname) {
    super(pathname);
    try {
      url = new URL(pathname);
      path = pathname;
    }
    catch (MalformedURLException e) {
      isURL = false;
    }
  }

  public FileWrapper(String parent, String child) {
    super(parent, child);
    try {
      url = new URL(parent + "/" + child);
      path = parent + "/" + child;
    }
    catch (MalformedURLException e) {
      isURL = false;
    }
  }

  public FileWrapper(File parent, String child) {
    this(parent.getAbsolutePath(), child);
  }

  // -- File API methods --
  
  /* @see java.io.File#canRead() */
  public boolean canRead() {
    return isURL ? true : super.canRead();
  }

  /* @see java.io.File#canWrite() */
  public boolean canWrite() {
    return isURL ? false : super.canWrite();
  }

  /* @see java.io.File#createNewFile() */
  public boolean createNewFile() throws IOException {
    if (isURL) throw new IOException("Unimplemented");
    return super.createNewFile();
  }

  /* @see java.io.File#delete() */
  public boolean delete() {
    return isURL ? false : super.delete();
  }

  /* @see java.io.File#deleteOnExit() */
  public void deleteOnExit() {
    if (!isURL) super.deleteOnExit();
  }

  /* @see java.io.File#compareTo(File) */
  public int compareTo(File pathname) {
    return super.compareTo(pathname);
  }

  /* @see java.io.File#compareTo(Object) */
  public int compareTo(Object o) {
    return 0; // TODO
  }

  /* @see java.io.File#equals(Object) */
  public boolean equals(Object obj) {
    return isURL ? url.equals(obj) : super.equals(obj);
  }

  /* @see java.io.File#exists() */
  public boolean exists() {
    if (isURL) {
      try {
        url.getContent();
        return true;
      }
      catch (IOException e) { return false; }
    }
    return super.exists();
  }

  /* @see java.io.File#getAbsoluteFile() */
  public File getAbsoluteFile() {
    return isURL ? new FileWrapper(getAbsolutePath()) : super.getAbsoluteFile();
  }

  /* @see java.io.File#getAbsolutePath() */
  public String getAbsolutePath() {
    return isURL ? url.toExternalForm() : super.getAbsolutePath();
  }

  /* @see java.io.File#getCanonicalFile() */
  public File getCanonicalFile() throws IOException {
    return isURL ? getAbsoluteFile() : super.getCanonicalFile();
  }

  /* @see java.io.File#getCanonicalPath() */
  public String getCanonicalPath() throws IOException {
    return isURL ? getAbsolutePath() : super.getCanonicalPath();
  }

  /* @see java.io.File#getName() */
  public String getName() {
    if (isURL) {
      String name = url.getFile();
      name = name.substring(name.lastIndexOf("/") + 1);
      return name;
    }
    return super.getName();
  }

  /* @see java.io.File#getParent() */
  public String getParent() {
    if (isURL) {
      String absPath = getAbsolutePath();
      absPath = absPath.substring(0, absPath.lastIndexOf("/"));
      return absPath;
    }
    return super.getParent();
  }

  /* @see java.io.File#getParentFile() */
  public File getParentFile() {
    return isURL ? new FileWrapper(getParent()) : super.getParentFile();
  }

  /* @see java.io.File#getPath() */
  public String getPath() {
    return isURL ? url.getHost() + url.getPath() : super.getPath();
  }

  /* @see java.io.File#isAbsolute() */
  public boolean isAbsolute() {
    return isURL ? true : super.isAbsolute();
  }

  /* @see java.io.File#isDirectory() */
  public boolean isDirectory() {
    return isURL ? lastModified() == 0 : super.isDirectory();
  }

  /* @see java.io.File#isFile() */
  public boolean isFile() {
    return isURL ? lastModified() > 0 : super.isFile();
  }

  /* @see java.io.File#isHidden() */
  public boolean isHidden() {
    return isURL ? false : super.isHidden();
  }

  /* @see java.io.File#lastModified() */
  public long lastModified() {
    if (isURL) {
      try {
        return url.openConnection().getLastModified();
      }
      catch (IOException e) { return 0; }
    }
    return super.lastModified();
  }

  /* @see java.io.File#length() */
  public long length() {
    if (isURL) {
      try {
        return url.openConnection().getContentLength();
      }
      catch (IOException e) { return 0; }
    }
    return super.length();
  }

  /* @see java.io.File#list() */
  public String[] list() {
    if (isURL) {
      if (!isDirectory()) return null;
      try {
        URLConnection c = url.openConnection();
        InputStream is = c.getInputStream();
        int len = c.getContentLength();
        if (len == -1) len = is.available();
        byte[] b = new byte[len];
        int read = is.read(b);
        while (read < b.length) read += is.read(b, read, b.length - read);
        String s = new String(b);
      
        Vector files = new Vector();
        while (s.indexOf("a href") != -1) {
          int ndx = s.indexOf("a href") + 8;
          String f = s.substring(ndx, s.indexOf("\"", ndx));
          s = s.substring(s.indexOf("\"", ndx) + 1);
          FileWrapper check = new FileWrapper(getAbsolutePath(), f);
          if (check.exists()) files.add(check.getName());
        }
        return (String[]) files.toArray(new String[0]);
      }
      catch (IOException e) { 
        return null; 
      }
    }
    return super.list();
  }

  /* @see java.io.File#list(FilenameFilter) */
  public String[] list(FilenameFilter filter) {
    if (isURL) {
      if (!isDirectory()) return null;
      String[] s = list();
      Vector files = new Vector();
      for (int i=0; i<s.length; i++) {
        FileWrapper check = new FileWrapper(s[i]);
        if (filter.accept(check.getParentFile(), check.getName())) {
          files.add(s[i]);
        }
      }
      return (String[]) files.toArray(new String[0]);
    }
    return super.list(filter);
  }

  /* @see java.io.File#listFiles() */
  public File[] listFiles() {
    if (isURL) {
      if (!isDirectory()) return null;
      String[] s = list();
      File[] f = new File[s.length];
      for (int i=0; i<f.length; i++) {
        f[i] = new FileWrapper(getAbsolutePath(), s[i]);
        f[i] = f[i].getAbsoluteFile();
      }
      return f;
    }
    return super.listFiles();
  }

  /* @see java.io.File#listFiles(FileFilter) */
  public File[] listFiles(FileFilter filter) {
    if (isURL) {
      if (!isDirectory()) return null;
      File[] f = listFiles();
      Vector files = new Vector();
      for (int i=0; i<f.length; i++) {
        if (filter.accept(f[i])) files.add(f[i]);
      }
      return (File[]) files.toArray(new File[0]);
    }
    return super.listFiles(filter);
  }

  /* @see java.io.File#listFiles(FilenameFilter) */
  public File[] listFiles(FilenameFilter filter) {
    if (isURL) {
      if (!isDirectory()) return null;
      String[] s = list(filter);
      File[] f = new File[s.length];
      for (int i=0; i<f.length; i++) {
        f[i] = new FileWrapper(getAbsolutePath(), s[i]);
        f[i] = f[i].getAbsoluteFile();
      }
      return f;
    }
    return super.listFiles(filter);
  }

  /* @see java.io.File#toURL() */
  public URL toURL() throws MalformedURLException {
    return isURL ? url : super.toURL();
  }

}
