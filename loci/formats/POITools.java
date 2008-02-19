//
// POITools.java
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
import java.util.*;
import loci.formats.RandomAccessStream;

/**
 * Utility methods for working with Microsoft OLE document format files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/POITools.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/POITools.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class POITools {

  // -- Constants --

  private static final String NO_POI_MSG =
    "Jakarta POI is required to read Compix SimplePCI, Fluoview FV1000 OIB, " +
    "ImagePro IPW, and Zeiss ZVI files.  Please obtain poi-loci.jar from " +
    "http://loci.wisc.edu/ome/formats.html";

  // -- Static fields --

  private static boolean noPOI = false;
  private static ReflectedUniverse r = createReflectedUniverse();

  private static ReflectedUniverse createReflectedUniverse() {
    r = null;
    try {
      r = new ReflectedUniverse();
      r.exec("import org.apache.poi.poifs.filesystem.POIFSFileSystem");
      r.exec("import org.apache.poi.poifs.filesystem.DirectoryEntry");
      r.exec("import org.apache.poi.poifs.filesystem.DocumentEntry");
      r.exec("import org.apache.poi.poifs.filesystem.DocumentInputStream");
      r.exec("import org.apache.poi.util.RandomAccessStream");
      r.exec("import java.util.Iterator");
    }
    catch (ReflectException exc) {
      noPOI = true;
      exc.printStackTrace();
    }
    return r;
  }

  // -- Fields --

  private String id;
  private Vector filePath;

  // -- Constructor --

  public POITools(String id) throws FormatException, IOException {
    this.id = id;
    initialize(id);
  }

  // -- Document retrieval methods --

  public RandomAccessStream getDocumentStream(String name)
    throws FormatException, IOException
  {
    byte[] buf = getDocumentBytes(name, getFileSize(name));
    return new RandomAccessStream(buf);
  }

  public byte[] getDocumentBytes(String name) throws FormatException {
    return getDocumentBytes(name, getFileSize(name));
  }

  public byte[] getDocumentBytes(String name, int count) throws FormatException
  {
    int availableBytes = getFileSize(name);
    int len = count > availableBytes ? availableBytes : count;

    byte[] buf = new byte[len];
    try {
      r.setVar(makeVarName("data"), buf);
      r.exec(makeVarName("dis") + ".read(" + makeVarName("data") + ")");
    }
    catch (ReflectException e) {
      throw new FormatException(e);
    }
    return buf;
  }

  public int getFileSize(String name) throws FormatException {
    setupFile(name);
    try {
      return ((Integer) r.getVar(makeVarName("numBytes"))).intValue();
    }
    catch (ReflectException e) {
      throw new FormatException(e);
    }
  }

  public Vector getDocumentList() throws FormatException {
    Vector list = new Vector();
    filePath = new Vector();
    try {
      parseFile(r.getVar(makeVarName("root")), list);
    }
    catch (ReflectException e) {
      throw new FormatException(e);
    }
    return list;
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
    if (noPOI) throw new FormatException(NO_POI_MSG);

    RandomAccessStream s = new RandomAccessStream(file);
    s.order(true);
    s.seek(30);
    int size = (int) Math.pow(2, s.readShort());
    s.close();

    try {
      r.setVar(makeVarName("file"), file);
      r.setVar(makeVarName("size"), size);
      r.setVar(makeVarName("littleEndian"), true);
      r.exec(makeVarName("fis") + " = new RandomAccessStream(" +
        makeVarName("file") + ")");
      r.exec(makeVarName("fs") + " = new POIFSFileSystem(" +
        makeVarName("fis") + ", " + makeVarName("size") + ")");
      r.exec(makeVarName("root") + " = " + makeVarName("fs") + ".getRoot()");
    }
    catch (ReflectException e) {
      throw new FormatException(e);
    }
  }

  private void setupFile(String name) throws FormatException {
    try {
      r.exec(makeVarName("directory") + " = " + makeVarName("root"));

      StringTokenizer path = new StringTokenizer(name, File.separator);
      int count = path.countTokens();
      path.nextToken();
      for (int i=1; i<count-1; i++) {
        String dir = path.nextToken();
        r.setVar(makeVarName("dir"), dir);
        r.exec(makeVarName("directory") + " = " + makeVarName("directory") +
          ".getEntry(" + makeVarName("dir") + ")");
      }

      r.setVar(makeVarName("filename"), path.nextToken());
      r.exec(makeVarName("file") + " = " + makeVarName("directory") +
        ".getEntry(" + makeVarName("filename") + ")");
      r.exec(makeVarName("dis") + " = new DocumentInputStream(" +
        makeVarName("file") + ", " + makeVarName("fis") + ")");
      r.exec(makeVarName("numBytes") + " = " + makeVarName("dis") +
        ".available()");
    }
    catch (ReflectException e) {
      throw new FormatException(e);
    }
  }

  private void parseFile(Object root, Vector fileList) throws FormatException {
    try {
      r.setVar(makeVarName("dir"), root);
      r.exec(makeVarName("dirName") + " = " + makeVarName("dir") +
        ".getName()");
      r.exec(makeVarName("iter") + " = " + makeVarName("dir") +
        ".getEntries()");
      filePath.add(r.getVar(makeVarName("dirName")));
      Iterator iter = (Iterator) r.getVar(makeVarName("iter"));
      while (iter.hasNext()) {
        r.setVar(makeVarName("entry"), iter.next());
        boolean isInstance = ((Boolean) r.exec(makeVarName("entry") +
          ".isDirectoryEntry()")).booleanValue();
        boolean isDocument = ((Boolean) r.exec(makeVarName("entry") +
          ".isDocumentEntry()")).booleanValue();

        if (isInstance) parseFile(r.getVar(makeVarName("entry")), fileList);
        else if (isDocument) {
          r.exec(makeVarName("entryName") + " = " + makeVarName("entry") +
            ".getName()");
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
