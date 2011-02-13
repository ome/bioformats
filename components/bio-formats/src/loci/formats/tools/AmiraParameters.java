//
// AmiraParameters.java
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

package loci.formats.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;

/**
 * AmiraParameters handles parsing and writing of AmiraMesh headers.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/tools/AmiraParameters.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/tools/AmiraParameters.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Gregory Jefferis jefferis at gmail.com
 * @author Johannes Schindelin johannes.schindelin at gmx.de
 */
public class AmiraParameters {
  public int width, height, depth, firstDataStream;
  public double x0, y0, z0, x1, y1, z1;

  public boolean littleEndian, ascii;
  public int nStreams = 0;
  public String[] streamNames;
  public String[] streamTypes;

  protected RandomAccessInputStream in;
  protected Map map, streams;

  protected int column, row;
  protected char c;

  public AmiraParameters(String path) throws FormatException {
    readFile(path);
  }

  public AmiraParameters(RandomAccessInputStream inputStream)
    throws FormatException, IOException
  {
    readFile(inputStream);
  }

  protected void readFile(String path) throws FormatException {
    try {
      readFile(new RandomAccessInputStream(path));
    }
    catch (IOException e) {
      throw new FormatException("read error: " + path);
    }
  }

  protected void readFile(RandomAccessInputStream inputStream)
    throws FormatException, IOException
  {
    String firstLine = inputStream.readLine();
    Matcher amiraMeshDef = Pattern.compile("#\\s+AmiraMesh.*?" +
      "(BINARY|ASCII)(-LITTLE-ENDIAN)*").matcher(firstLine);
    if (amiraMeshDef.find()) {
      if (amiraMeshDef.group(1).equals("BINARY")) {
        littleEndian = amiraMeshDef.group(2) != null;
      }
      else if (amiraMeshDef.group(1).equals("ASCII")) {
        ascii = true;
      }
      else {
        syntaxError("Can't recognise this Amira file type");
      }
    }
    else {
      syntaxError("Doesn't seem to be an Amira file");
    }

    column = 0;
    row = 1;
    in = inputStream;
    readByte();
    readTopLevel();
    extractCoreMetaData();
  }

  protected void extractCoreMetaData() throws FormatException {
    // Bounding Box
    if (map.containsKey("Parameters")) {
      Map p = (Map) map.get("Parameters");
      if (p.containsKey("BoundingBox")) {
        Double[] bb = (Double[]) p.get("BoundingBox");
        x0 = bb[0].doubleValue();
        x1 = bb[1].doubleValue();
        y0 = bb[2].doubleValue();
        y1 = bb[3].doubleValue();
        z0 = bb[4].doubleValue();
        z1 = bb[5].doubleValue();
      }
    }
    nStreams = streams.size();
    if (nStreams > 0) {
      streamNames = new String[nStreams];
      streamTypes = new String[nStreams];

      int i = 0;
      for (Object key : streams.keySet()) {
        ArrayList al = (ArrayList) streams.get(key);
        streamNames[i] = (String) al.get(0);
        Map streamMap = (Map) al.get(1);
        Iterator it = streamMap.keySet().iterator();
        if (it.hasNext()) {
          streamTypes[i] = (String) it.next();
        }
        else {
          syntaxError("Unable to identify data type");
        }
        i++;
      }
    }
  }

  protected void syntaxError(String message) throws FormatException {
    throw new FormatException("Syntax Error:" +
      row + ":" + column + ": " + message);
  }

  protected void readTopLevel() throws FormatException, IOException {
    streams = new LinkedHashMap();
    map = new LinkedHashMap();
    for (;;) {
      skipWhiteSpace();
      if (c == '#') {
        skipComment();
        continue;
      }

      if (c == '@') {
        readByte();
        firstDataStream = readNumber().intValue();
        skipComment();
        return;
      }

      String key = readKey();

      skipWhiteSpace();
      Object value = null;
      if (key.equals("define")) {
        key = "n" + readKey();
        skipWhiteSpace();
      }
      if (key.equals("nLattice")) {
        // FIXME Colormaps have a 1D nLattice
        // This will probably need further attention
        Integer[] dimensions = readIntArray();
        width = dimensions[0].intValue();
        if(dimensions.length > 1) height = dimensions[1].intValue();
        if(dimensions.length > 2) depth = dimensions[2].intValue();
        value = dimensions;
      }
      else if(key.equals("nNodes") || key.equals("nTriangles") ||
        key.equals("nTetrahedra") || key.equals("nEdges"))
      {
        throw new FormatException("Don't know yet how to handle " + key);
      }
      else if (key.equals("Parameters")) {
        value = readMap();
      }
      else if (key.equals("Lattice") || key.equals("Vertices") ||
        key.equals("Lines") || key.equals("Markers"))
      {
        ArrayList list = new ArrayList();
        list.add(key);
        list.add(readMap());

        skipWhiteSpace();

        if (c == '=') {
          readByte();
          skipWhiteSpace();
        }
        if (c != '@') syntaxError("Missing @");

        // store information in an array
        readByte();
        int index = readNumber().intValue();

        if (c == '(') list.add(readQuotedString());
        skipComment();

        streams.put("@" + index, list);
        continue; // no need to store this information
      }
      else skipComment();
      map.put(key, value);
    }
  }

  protected char readByte() throws IOException {
    c = (char) in.read();
    if (c == '\n') {
      row++;
      column = 1;
    }
    else column++;
    return c;
  }

  protected void skipComment() throws IOException {
    while (c != '\n') readByte();
  }

  protected void skipWhiteSpace() throws IOException {
    while (c == ' ' || c == '\t' || c == '\n') readByte();
  }

  protected String readKey() throws IOException {
    String result = "";
    while (c >= '0' || c == '-') {
      result += c;
      readByte();
    }
    return result;
  }

  protected Number readNumber() throws FormatException, IOException {
    String string = "";
    while ((c >= '0' && c <= '9') || c == '.' || c == '-' || c == '+' ||
      c == 'e')
    {
      string += c;
      readByte();
    }
    try {
      if (string.indexOf('.') < 0 && string.indexOf('e') < 0) {
        return Integer.valueOf(string);
      }
      return Double.valueOf(string);
    }
    catch (NumberFormatException e) {
      syntaxError(e.getMessage());
      return null; // shut up the compiler
    }
  }

  protected Integer[] readIntArray() throws FormatException, IOException {
    // read integers until end of line
    ArrayList<Integer> result = new ArrayList<Integer>();
    int currentRow = row;
    // Keep reading until we hit newline or a non-numeric
    while (currentRow == row &&
      ((c >= '0' && c <= '9') || c == '.' || c == '-' || c == '+'))
    {
      result.add((Integer) readNumber());
      skipWhiteSpace();
    }
    Integer[] intResult = new Integer[result.size()];
    return result.toArray(intResult);
  }

  protected Number[] readNumberArray() throws FormatException, IOException {
    // read integers until end of line
    ArrayList<Number> result = new ArrayList<Number>();
    boolean intsOnly = true;
    int currentRow = row;
    // Keep reading until we hit newline or a non-numeric
    while (currentRow == row &&
      ((c >= '0' && c <= '9') || c == '.' || c == '-' || c == '+'))
    {
      Number n = readNumber();
      if (n instanceof Double) {
        intsOnly = false;
      }
      result.add(n);
      skipWhiteSpace();
    }
    if (intsOnly) {
      Integer[] intResult = new Integer[result.size()];
      return result.toArray(intResult);
    }
    else {
      // nb this is necessary because we may have a mix
      // of Integers and Doubles
      Double[] doubleResult = new Double[result.size()];
      for (int i = 0; i < doubleResult.length; i++) {
        doubleResult[i] = new Double(result.get(i).doubleValue());
      }
      return doubleResult;
    }
  }

  protected Integer[] readIntArray(int count)
    throws FormatException, IOException
  {
    Integer[] result = new Integer[count];
    for (int i = 0; i < count; i++) {
      result[i] = (Integer) readNumber();
      skipWhiteSpace();
    }
    return result;
  }

  protected Double[] readDoubleArray(int count)
    throws FormatException, IOException
  {
    Double[] result = new Double[count];
    for (int i = 0; i < count; i++) {
      result[i] = new Double(readNumber().doubleValue());
      skipWhiteSpace();
    }
    return result;
  }

  protected String readQuotedString() throws FormatException, IOException {
    int quote = c;
    if (quote == '(') {
      quote = ')';
    }
    else if (quote != '"' && quote != '\'') {
      syntaxError("Invalid quote: " + c);
    }
    String result = "";
    for (;;) {
      readByte();
      if (c == quote) {
        readByte();
        return result;
      }
      if (quote == '"' && c == '\\') {
        readByte();
      }
      result += c;
    }
  }

  protected Map readMap() throws FormatException, IOException {
    if (c != '{') {
      syntaxError("Illegal block: " + c);
    }
    readByte();
    Map subMap = new LinkedHashMap();
    for (;;) {
      skipWhiteSpace();
      if (c == '#') {
        skipComment();
        continue;
      }
      if (c == '}') {
        readByte();
        return subMap;
      }

      String key = readKey();
      if (key.equals("")) syntaxError("Invalid key");

      skipWhiteSpace();
      Object value;
      if (c == '{') value = readMap();
      else {
        if (key.equals("BoundingBox")) {
          value = readDoubleArray(6);
        }
        else if (key.equals("MinMax")) {
          value = readDoubleArray(2);
        }
        else if (key.startsWith("byte") || key.startsWith("short") ||
          key.startsWith("ushort") || key.startsWith("float"))
        {
          value = readKey();
        }
        else if (c == '"' || c == '\'') {
          value = readQuotedString();
        }
        else {
          Number[] na = readNumberArray();
          if (na.length == 1) value = na[0];
          else value = na;
        }

        if (c == ',') readByte();
      }
      subMap.put(key, value);
    }
  }

  public Map getMap() {
    return map;
  }

  public Map getStreams() {
    return streams;
  }

  public String toString() {
    try {
      return toString(map, "") + toString(streams, "");
    }
    catch (FormatException e) {
      throw new RuntimeException(e);
    }
  }

  public static String toString(Map map, String indent) throws FormatException {
    String result = "", separator = indent;
    Iterator iter = map.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      result += separator + entry.getKey() + " " +
        entryToString(entry.getValue(), indent);
      if (result.endsWith("}")) {
        separator = "\n" + indent;
      }
      else {
        separator = ",\n" + indent;
      }
    }
    return result + "\n";
  }

  public static String entryToString(Object object, String indent)
    throws FormatException
  {
    if (object instanceof Integer || object instanceof Double) {
      return object.toString();
    }
    if (object instanceof String) {
      String string = (String) object, result = "\"";
      int offset = 0;
      for (;;) {
        int nextOffset = string.indexOf('"', offset + 1);
        if (nextOffset < 0) {
          break;
        }
        if (nextOffset > offset + 1) {
          result += string.substring(offset, nextOffset);
        }
        result += "\\";
        offset = nextOffset;
      }
      if (offset + 1 < string.length()) {
        result += string.substring(offset);
      }
      return result + "\"";
    }
    if (object instanceof Integer[]) {
      Integer[] array = (Integer[]) object;
      String result = null;
      for (int i = 0; i < array.length; i++) {
        result = (i > 0 ? result + " " : "") + array[i];
      }
      return result;
    }
    if (object instanceof Double[]) {
      Double[] array = (Double[]) object;
      String result = null;
      for (int i = 0; i < array.length; i++) {
        result = (i > 0 ? result + " " : "") + array[i];
      }
      return result;
    }
    if (object instanceof Map) {
      return "{\n" + toString((Map) object, indent + "\t") + indent + "}";
    }
    if (object instanceof ArrayList) {
      String result = "{\n";
      for (Object item : (ArrayList) object) {
        result += entryToString(item, indent + "\t") + "\n";
      }
      return result + indent + "}";
    }
    throw new FormatException("Illegal value type: " +
      object.getClass().getName());
  }

  public static void main(String[] args) {
    for (int i = 0; i < args.length; i++) {
      System.out.println("file: " + args[i]);
      try {
        System.out.println(new AmiraParameters(args[i]));
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
