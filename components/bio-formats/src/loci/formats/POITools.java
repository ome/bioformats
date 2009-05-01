//
// POITools.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats;

import java.io.*;
import java.util.*;
import loci.common.*;

/**
 * Utility methods for working with Microsoft OLE document format files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/POITools.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/POITools.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class POITools {

  // -- Constants --

  private static final String NO_POI_MSG =
    "Jakarta POI is required to read Compix SimplePCI, Fluoview FV1000 OIB, " +
    "ImagePro IPW, and Zeiss ZVI files.  Please obtain poi-loci.jar from " +
    "http://loci.wisc.edu/ome/formats.html";

  // -- Fields --

  private ReflectedUniverse r;
  private String id;
  private Vector filePath;
  private Vector fileList;
  private Hashtable fileSizes;

  // -- Constructor --

  public POITools(String id) throws FormatException, IOException {
    this.id = id;
    initialize(id);
  }

  public POITools(RandomAccessInputStream s)
    throws FormatException, IOException
  {
    id = String.valueOf(System.currentTimeMillis());
    initialize(s);
  }

  // -- Document retrieval methods --

  public RandomAccessInputStream getDocumentStream(String name)
    throws FormatException, IOException
  {
    byte[] buf = getDocumentBytes(name, getFileSize(name));
    return new RandomAccessInputStream(buf);
  }

  public byte[] getDocumentBytes(String name) throws FormatException {
    return getDocumentBytes(name, getFileSize(name));
  }

  public byte[] getDocumentBytes(String name, int count) throws FormatException
  {
    int availableBytes = getFileSize(name);
    int len = count > availableBytes ? availableBytes : count;
    setupFile(name);

    byte[] buf = new byte[len];
    try {
      String dataVar = makeVarName("data");
      r.setVar(dataVar, buf);
      r.exec(makeVarName("dis") + ".read(" + dataVar + ")");
    }
    catch (ReflectException e) {
      throw new FormatException(e);
    }
    return buf;
  }

  public int getFileSize(String name) throws FormatException {
    return ((Integer) fileSizes.get(name)).intValue();
  }

  public Vector getDocumentList() throws FormatException {
    return fileList;
  }

  public void close() {
    try {
      r.exec(makeVarName("dis") + ".close()");
      r.setVar(makeVarName("data"), null);
      r.setVar(makeVarName("dis"), null);
      r.setVar(makeVarName("data"), null);
      r.setVar(makeVarName("numBytes"), null);
      r.setVar(makeVarName("root"), null);
      r.setVar(makeVarName("file"), null);
      r.setVar(makeVarName("size"), null);
      r.setVar(makeVarName("littleEndian"), null);
      r.setVar(makeVarName("fis"), null);
      r.setVar(makeVarName("fs"), null);
      r.setVar(makeVarName("dir"), null);
      r.setVar(makeVarName("directory"), null);
      r.setVar(makeVarName("entry"), null);
      r.setVar(makeVarName("entryName"), null);
      r.setVar(makeVarName("tmp"), null);
    }
    catch (ReflectException e) { }
  }

  // -- Helper methods --

  private void initialize(String file) throws FormatException, IOException {
    RandomAccessInputStream s = new RandomAccessInputStream(file);
    initialize(s);
    s.close();
  }

  private void initialize(RandomAccessInputStream s)
    throws FormatException, IOException
  {
    try {
      r = new ReflectedUniverse();
      r.exec("import loci.poi.poifs.filesystem.POIFSFileSystem");
      r.exec("import loci.poi.poifs.filesystem.DirectoryEntry");
      r.exec("import loci.poi.poifs.filesystem.DocumentEntry");
      r.exec("import loci.poi.poifs.filesystem.DocumentInputStream");
      r.exec("import loci.common.RandomAccessInputStream");
      r.exec("import java.util.Iterator");
    }
    catch (ReflectException exc) {
      throw new FormatException(NO_POI_MSG, exc);
    }

    s.order(true);
    s.seek(30);
    int size = (int) Math.pow(2, s.readShort());
    s.seek(0);

    try {
      String sizeVar = makeVarName("size");
      String fsVar = makeVarName("fs");
      String fisVar = makeVarName("fis");
      r.setVar(sizeVar, size);
      r.setVar(makeVarName("littleEndian"), true);
      r.setVar(fisVar, s);
      r.exec(fsVar + " = new POIFSFileSystem(" + fisVar + ", " + sizeVar + ")");
      r.exec(makeVarName("root") + " = " + fsVar + ".getRoot()");
    }
    catch (ReflectException e) {
      throw new FormatException(e);
    }

    fileList = new Vector();
    filePath = new Vector();
    try {
      parseFile(r.getVar(makeVarName("root")), fileList);
    }
    catch (ReflectException e) {
      throw new FormatException(e);
    }

    fileSizes = new Hashtable();
    for (int i=0; i<fileList.size(); i++) {
      String name = (String) fileList.get(i);
      setupFile(name);
      try {
        fileSizes.put(name, r.getVar(makeVarName("numBytes")));
      }
      catch (ReflectException e) {
        throw new FormatException(e);
      }
    }
  }

  private void setupFile(String name) throws FormatException {
    try {
      String directoryVar = makeVarName("directory");
      r.exec(directoryVar + " = " + makeVarName("root"));

      StringTokenizer path = new StringTokenizer(name, "/\\");
      int count = path.countTokens();
      path.nextToken();
      for (int i=1; i<count-1; i++) {
        String dir = path.nextToken();
        r.setVar(makeVarName("dir"), dir);
        r.exec(directoryVar + " = " + directoryVar +
          ".getEntry(" + makeVarName("dir") + ")");
      }

      String filenameVar = makeVarName("filename");
      String fileVar = makeVarName("file");
      String disVar = makeVarName("dis");

      r.setVar(filenameVar, path.nextToken());
      r.exec(fileVar + " = " + directoryVar + ".getEntry(" + filenameVar + ")");
      r.exec(disVar + " = new DocumentInputStream(" +
        fileVar + ", " + makeVarName("fis") + ")");
      r.exec(makeVarName("numBytes") + " = " + disVar + ".available()");
    }
    catch (ReflectException e) {
      throw new FormatException(e);
    }
  }

  private void parseFile(Object root, Vector fileList) throws FormatException {
    try {
      String dirVar = makeVarName("dir");
      r.setVar(dirVar, root);
      r.exec(makeVarName("dirName") + " = " + dirVar + ".getName()");
      r.exec(makeVarName("iter") + " = " + dirVar + ".getEntries()");
      filePath.add(r.getVar(makeVarName("dirName")));
      Iterator iter = (Iterator) r.getVar(makeVarName("iter"));
      String entryVar = makeVarName("entry");
      while (iter.hasNext()) {
        r.setVar(entryVar, iter.next());
        boolean isInstance = ((Boolean) r.exec(entryVar +
          ".isDirectoryEntry()")).booleanValue();
        boolean isDocument = ((Boolean) r.exec(entryVar +
          ".isDocumentEntry()")).booleanValue();

        if (isInstance) parseFile(r.getVar(entryVar), fileList);
        else if (isDocument) {
          r.exec(makeVarName("entryName") + " = " + entryVar + ".getName()");
          StringBuffer path = new StringBuffer();
          for (int i=0; i<filePath.size(); i++) {
            path.append((String) filePath.get(i));
            path.append(File.separator);
          }
          path.append((String) r.getVar(makeVarName("entryName")));
          fileList.add(path.toString());
        }
      }
      filePath.removeElementAt(filePath.size() - 1);
    }
    catch (ReflectException e) {
      throw new FormatException(e);
    }
  }

  private String makeVarName(String var) {
    String file = id.replaceAll(" ", "");
    file = file.replaceAll(".", "");
    return file + "-" + var;
  }

}
