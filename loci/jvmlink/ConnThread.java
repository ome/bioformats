//
// ConnThread.java
//

/*
JVMLink client/server architecture for communicating between Java and
non-Java programs using sockets.
Copyright (c) 2008 Hidayath Ansari and Curtis Rueden. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the UW-Madison LOCI nor the names of its
    contributors may be used to endorse or promote products derived from
    this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE UW-MADISON LOCI ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package loci.jvmlink;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import loci.formats.DataTools;
import loci.formats.ReflectException;
import loci.formats.ReflectedUniverse;

//TODO: Communicating exceptions ..

/**
 * Thread for managing a client/server socket connection.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/jvmlink/ConnThread.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/jvmlink/ConnThread.java">SVN</a></dd></dl>
 */
public class ConnThread extends Thread {

  // -- Constants --

  public static final int MAX_PACKET_SIZE = 65536;
  public static final int ORDER_VALUE = 1;

  public static final int ARRAY_TYPE = 0;
  public static final int INT_TYPE = 1;
  public static final int STRING_TYPE = 2;
  public static final int BYTE_TYPE = 3;
  public static final int CHAR_TYPE = 4;
  public static final int FLOAT_TYPE = 5;
  public static final int BOOLEAN_TYPE = 6;
  public static final int DOUBLE_TYPE = 7;
  public static final int LONG_TYPE = 8;
  public static final int SHORT_TYPE = 9;
  public static final int NULL_TYPE = -1;

  public static final int BYTE_ORDER = 0;
  public static final int SETVAR = 1;
  public static final int GETVAR = 2;
  public static final int EXEC = 3;
  public static final int EXIT = 255;

  // -- Static fields --

  private static int threadNumber = 0;

  // -- Fields --

  private Socket socket;
  private JVMLinkServer server;
  private ReflectedUniverse r;
  private DataInputStream in;
  private DataOutputStream out;

  /**
   * True for little endian (intel) byte order,
   * false for big endian (motorola) order.
   */
  private boolean little;

  // -- Constructor --

  public ConnThread(Socket socket, JVMLinkServer server) throws IOException {
    super("JVMLink-Client-" + (++threadNumber));
    this.socket = socket;
    this.server = server;
    r = new ReflectedUniverse();
    in = new DataInputStream(
      new BufferedInputStream(socket.getInputStream()));
    out = new DataOutputStream(
      new BufferedOutputStream(socket.getOutputStream()));
    little = true;
    start();
  }

  /*
   * Protocol syntax:
   *
   * First thing on the stream is an integer:
   *  0   - byte order
   *  1   - setVar
   *  2   - getVar
   *  3   - exec
   *  255 - terminate server
   *
   * For byte order, the integer 1 in the desired byte order
   *
   * For the other commands, a string (prefixed by its length), specifying:
   *   setVar, getVar : identifier in question
   *   exec : command to be executed
   *
   * Then, according to which branch:
   *  setVar:
   *   0 - array (followed by another integer specifying one of the following
   *     types, then an integer specifying length of array)
   *   1-9 - int, string, byte, char, float, bool, double, long, short
   *     (in order)
   *
   *  getVar (sends):
   *   0 - array (followed by another integer specifying one of the following
   *     types, then length)
   *   1-9 - int, string, byte, char, float, bool, double, long, short
   *     (in order)
   */

  // -- Thread API methods --

  public void run() {
    boolean killServer = false;
    while (true) {
      try {
        int command = readInt();
        debug("Received command: " + getCommand(command));
        if (command == EXIT) {
          killServer = true;
          break;
        }
        switch (command) {
          case BYTE_ORDER:
            byteOrder();
            break;
          case SETVAR:
            setVar();
            break;
          case GETVAR:
            getVar();
            break;
          case EXEC:
            exec();
            break;
        }
      }
      catch (EOFException exc) {
        // client disconnected
        debug("EOF reached; client probably disconnected");
        break;
      }
      catch (SocketException exc) {
        // connection error
        debug("Socket error; connection lost");
        break;
      }
      catch (IOException exc) {
        if (JVMLinkServer.debug) exc.printStackTrace();
        try {
          Thread.sleep(100);
        }
        catch (InterruptedException exc2) {
          if (JVMLinkServer.debug) exc2.printStackTrace();
        }
      }
      catch (ReflectException exc) {
        if (JVMLinkServer.debug) exc.printStackTrace();
      }
    }

    debug("Exiting");
    try {
      socket.close();
    }
    catch (IOException exc) {
      if (JVMLinkServer.debug) exc.printStackTrace();
    }
    if (killServer) {
      try {
        server.shutServer();
      }
      catch (IOException exc) {
        if (JVMLinkServer.debug) exc.printStackTrace();
      }
    }
  }

  // -- Helper methods --

  /** Changes the byte order between big and little endian. */
  private void byteOrder() throws IOException {
    int bigType = in.readInt();
    int littleType = DataTools.swap(bigType);
    if (bigType == ORDER_VALUE) little = false; // big endian
    else if (littleType == ORDER_VALUE) little = true; // little endian
    else debug("Invalid byte order value: 0x" + Integer.toString(bigType, 16));
  }

  /**
   * Performs a SETVAR command, including reading arguments
   * from the input stream.
   */
  private void setVar() throws IOException {
    String name = readString();
    int type = readInt();
    int insideType = -1;
    Object value = null;
    if (type == ARRAY_TYPE) {
      insideType = readInt();
      int arrayLength = readInt();
      int size = getSize(insideType);
      Object theArray = null;
      if (insideType == INT_TYPE) {
        int[] intArray = new int[arrayLength];
        int readBytes = 0, totalBytes = size*arrayLength;
        while (readBytes < totalBytes) {
          int packetSize = MAX_PACKET_SIZE;
          if (readBytes + MAX_PACKET_SIZE > totalBytes) {
            packetSize = totalBytes - readBytes;
          }
          byte[] b = new byte[packetSize];
          in.readFully(b, 0, packetSize);
          for (int i=0; i<packetSize/4; i++) {
            intArray[i + (readBytes/4)] =
              DataTools.bytesToInt(b, 4*i, little);
          }
          readBytes += packetSize;
        }
        theArray = intArray;
      }
      else if (insideType == STRING_TYPE) {
        String[] stringArray = new String[arrayLength];
        for (int i=0; i<arrayLength; i++) {
          stringArray[i] = readString();
        }
        theArray = stringArray;
      }
      else if (insideType == BYTE_TYPE) {
        byte[] byteArray = new byte[arrayLength];
        int readBytes = 0, totalBytes = size*arrayLength;
        while (readBytes < totalBytes) {
          int packetSize = MAX_PACKET_SIZE;
          if (readBytes + MAX_PACKET_SIZE > totalBytes) {
            packetSize = totalBytes - readBytes;
          }
          in.readFully(byteArray, readBytes, packetSize);
          readBytes += packetSize;
        }
        theArray = byteArray;
      }
      else if (insideType == CHAR_TYPE) {
        char[] charArray = new char[arrayLength];
        int readBytes = 0, totalBytes = size*arrayLength;
        while (readBytes < totalBytes) {
          int packetSize = MAX_PACKET_SIZE;
          if (readBytes + MAX_PACKET_SIZE > totalBytes) {
            packetSize = totalBytes - readBytes;
          }
          byte[] b = new byte[packetSize];
          in.readFully(b, 0, packetSize);
          for (int i=0; i<packetSize; i++) {
            charArray[i + readBytes] = (char)
              ((0x00 << 8) | (b[i] & 0xff));
          }
          readBytes += packetSize;
        }
        theArray = charArray;
      }
      else if (insideType == FLOAT_TYPE) {
        float[] floatArray = new float[arrayLength];
        int readBytes = 0, totalBytes = size*arrayLength;
        while (readBytes < totalBytes) {
          int packetSize = MAX_PACKET_SIZE;
          if (readBytes + MAX_PACKET_SIZE > totalBytes) {
            packetSize = totalBytes - readBytes;
          }
          byte[] b = new byte[packetSize];
          in.readFully(b, 0, packetSize);
          for (int i=0; i<packetSize/4; i++) {
            floatArray[i + readBytes/4] =
              Float.intBitsToFloat(DataTools.bytesToInt(b, 4*i, little));
          }
          readBytes += packetSize;
        }
        theArray = floatArray;
      }
      else if (insideType == BOOLEAN_TYPE) {
        boolean[] boolArray = new boolean[arrayLength];
        int readBytes = 0, totalBytes = size*arrayLength;
        while (readBytes < totalBytes) {
          int packetSize = MAX_PACKET_SIZE;
          if (readBytes + MAX_PACKET_SIZE > totalBytes) {
            packetSize = totalBytes - readBytes;
          }
          byte[] b = new byte[packetSize];
          in.readFully(b, 0, packetSize);
          for (int i=0; i<packetSize; i++) {
            boolArray[i + readBytes] = b[i] != 0;
          }
          readBytes += packetSize;
        }
        theArray = boolArray;
      }
      else if (insideType == DOUBLE_TYPE) {
        double[] doubleArray = new double[arrayLength];
        int readBytes = 0, totalBytes = size*arrayLength;
        while (readBytes < totalBytes) {
          int packetSize = MAX_PACKET_SIZE;
          if (readBytes + MAX_PACKET_SIZE > totalBytes) {
            packetSize = totalBytes - readBytes;
          }
          byte[] b = new byte[packetSize];
          in.readFully(b, 0, packetSize);
          for (int i=0; i<packetSize/8; i++) {
            doubleArray[i + readBytes/8] =
              Double.longBitsToDouble(DataTools.bytesToLong(b, 8*i, little));
          }
          readBytes += packetSize;
        }
        theArray = doubleArray;
      }
      else if (insideType == LONG_TYPE) {
        long[] longArray = new long[arrayLength];
        int readBytes = 0, totalBytes = size*arrayLength;
        while (readBytes < totalBytes) {
          int packetSize = MAX_PACKET_SIZE;
          if (readBytes + MAX_PACKET_SIZE > totalBytes) {
            packetSize = totalBytes - readBytes;
          }
          byte[] b = new byte[packetSize];
          in.readFully(b, 0, packetSize);
          for (int i=0; i<packetSize/8; i++) {
            longArray[i + readBytes/8] =
              DataTools.bytesToLong(b, 8*i, little);
          }
          readBytes += packetSize;
        }
        theArray = longArray;
      }
      else if (insideType == SHORT_TYPE) {
        short[] shortArray = new short[arrayLength];
        int readBytes = 0, totalBytes = size*arrayLength;
        while (readBytes < totalBytes) {
          int packetSize = MAX_PACKET_SIZE;
          if (readBytes + MAX_PACKET_SIZE > totalBytes) {
            packetSize = totalBytes - readBytes;
          }
          byte[] b = new byte[packetSize];
          in.readFully(b, 0, packetSize);
          for (int i=0; i<packetSize/2; i++) {
            shortArray[i + readBytes/2] =
              DataTools.bytesToShort(b, 2*i, little);
          }
          readBytes += packetSize;
        }
        theArray = shortArray;
      }
      value = theArray;
    }
    else if (type == INT_TYPE) value = new Integer(readInt());
    else if (type == STRING_TYPE) value = readString();
    else if (type == BYTE_TYPE) value = new Byte(in.readByte());
    else if (type == CHAR_TYPE) value = new Character((char) in.readByte());
    else if (type == FLOAT_TYPE) value = new Float(readFloat());
    else if (type == BOOLEAN_TYPE) value = new Boolean(in.readBoolean());
    else if (type == DOUBLE_TYPE) value = new Double(readDouble());
    else if (type == LONG_TYPE) value = new Long(readLong());
    else if (type == SHORT_TYPE) value = new Short(readShort());

    String sType = type == 0 ? type + "/" + insideType : "" + type;
    debug("setVar (" + sType + "): " + name + " = " + getValue(value));
    if (value != null) r.setVar(name, value);
  }

  /**
   * Performs a GETVAR command, including reading arguments
   * from the input stream and sending results to the output stream.
   */
  private void getVar() throws IOException {
    String name = readString();
    int insideType = 0;
    int type = 0;
    Object value = null;
    try {
      value = r.getVar(name);
      debug("getVar: " + name + " = " + getValue(value));
      if (value instanceof int[]) {
        type = ARRAY_TYPE;
        insideType = INT_TYPE;
      }
      else if (value instanceof String[]) {
        type = ARRAY_TYPE;
        insideType = STRING_TYPE;
      }
      else if (value instanceof byte[]) {
        type = ARRAY_TYPE;
        insideType = BYTE_TYPE;
      }
      else if (value instanceof char[]) {
        type = ARRAY_TYPE;
        insideType = CHAR_TYPE;
      }
      else if (value instanceof float[]) {
        type = ARRAY_TYPE;
        insideType = FLOAT_TYPE;
      }
      else if (value instanceof boolean[]) {
        type = ARRAY_TYPE;
        insideType = BOOLEAN_TYPE;
      }
      else if (value instanceof double[]) {
        type = ARRAY_TYPE;
        insideType = DOUBLE_TYPE;
      }
      else if (value instanceof long[]) {
        type = ARRAY_TYPE;
        insideType = LONG_TYPE;
      }
      else if (value instanceof short[]) {
        type = ARRAY_TYPE;
        insideType = SHORT_TYPE;
      }
      else if (value instanceof Integer) type = INT_TYPE;
      else if (value instanceof String) type = STRING_TYPE;
      else if (value instanceof Byte) type = BYTE_TYPE;
      else if (value instanceof Character) type = CHAR_TYPE;
      else if (value instanceof Float) type = FLOAT_TYPE;
      else if (value instanceof Boolean) type = BOOLEAN_TYPE;
      else if (value instanceof Double) type = DOUBLE_TYPE;
      else if (value instanceof Long) type = LONG_TYPE;
      else if (value instanceof Short) type = SHORT_TYPE;
      else debug("Unknown type for value: " + value.getClass().getName());
    }
    catch (ReflectException e) {
      if (JVMLinkServer.debug) e.printStackTrace();
    }

    writeInt(type);
    if (type == ARRAY_TYPE) {
      Object theArray = value;
      writeInt(insideType);
      int arrayLen = Array.getLength(theArray);
      writeInt(arrayLen);
      if (insideType == INT_TYPE) {
        writeInt(4);
        int[] intArray = (int[]) theArray;
        for (int i=0; i<arrayLen; i++) {
          writeInt(intArray[i]);
        }
        if (arrayLen > 10000) {
          debug("Last two elements are " +
            intArray[arrayLen-1] + " and " + intArray[arrayLen-2]);
        }
      }
      else if (insideType == STRING_TYPE) {
        // NB: string size is variable
        String[] sArray = (String[]) theArray;
        for (int i=0; i<arrayLen; i++) writeString(sArray[i]);
      }
      else if (insideType == BYTE_TYPE) {
        writeInt(1);
        byte[] bArray = (byte[]) theArray;
        for (int i=0; i<arrayLen; i++) out.writeByte(bArray[i]);
      }
      else if (insideType == CHAR_TYPE) {
        writeInt(1);
        char[] cArray = (char[]) theArray;
        for (int i=0; i<arrayLen; i++) out.writeByte((byte) cArray[i]);
      }
      else if (insideType == FLOAT_TYPE) {
        writeInt(4);
        float[] fArray = (float[]) theArray;
        for (int i=0; i<arrayLen; i++) {
          int intBits = Float.floatToIntBits(fArray[i]);
          writeInt(intBits);
        }
      }
      else if (insideType == BOOLEAN_TYPE) {
        writeInt(1);
        boolean[] bArray = (boolean[]) theArray;
        for (int i=0; i<arrayLen; i++) out.writeBoolean(bArray[i]);
      }
      else if (insideType == DOUBLE_TYPE) {
        writeInt(8);
        double[] dArray = (double[]) theArray;
        for (int i=0; i<arrayLen; i++) {
          writeLong(Double.doubleToLongBits(dArray[i]));
        }
      }
      else if (insideType == LONG_TYPE) {
        writeInt(8);
        long[] lArray = (long[]) theArray;
        for (int i=0; i<arrayLen; i++) writeLong(lArray[i]);
      }
      else if (insideType == SHORT_TYPE) {
        writeInt(2);
        short[] sArray = (short[]) theArray;
        for (int i=0; i<arrayLen; i++) writeShort(sArray[i]);
      }
    }
    else if (type == INT_TYPE) {
      int val = ((Integer) value).intValue();
      writeInt(4);
      writeInt(val);
    }
    else if (type == STRING_TYPE) {
      String val = (String) value;
      writeString(val);
    }
    else if (type == BYTE_TYPE) {
      byte val = ((Byte) value).byteValue();
      writeInt(1);
      out.writeByte(val);
    }
    else if (type == CHAR_TYPE) {
      char val = ((Character) value).charValue();
      writeInt(1);
      out.writeByte((byte) val);
    }
    else if (type == FLOAT_TYPE) {
      float val = ((Float) value).floatValue();
      writeInt(4);
      writeInt(Float.floatToIntBits(val));
    }
    else if (type == BOOLEAN_TYPE) {
      boolean val = ((Boolean) value).booleanValue();
      writeInt(1);
      out.writeBoolean(val);
    }
    else if (type == DOUBLE_TYPE) {
      double val = ((Double) value).doubleValue();
      writeInt(8);
      writeLong(Double.doubleToLongBits(val));
    }
    else if (type == LONG_TYPE) {
      long val = ((Long) value).longValue();
      writeInt(8);
      writeLong(val);
    }
    else if (type == SHORT_TYPE) {
      short val = ((Short) value).shortValue();
      writeInt(2);
      writeShort(val);
    }
    out.flush();
  }

  /**
   * Performs an EXEC command, including reading arguments
   * from the input stream.
   */
  private void exec() throws IOException, ReflectException {
    String cmd = readString();
    debug("exec: " + cmd);
    r.exec(cmd);
  }

  // - I/O helper methods -

  /** Reads a short from the socket with the correct endianness. */
  private short readShort() throws IOException {
    short value = in.readShort();
    if (little) value = DataTools.swap(value);
    return value;
  }

  /** Reads an int from the socket with the correct endianness. */
  private int readInt() throws IOException {
    int value = in.readInt();
    if (little) value = DataTools.swap(value);
    return value;
  }

  /** Reads a long from the socket with the correct endianness. */
  private long readLong() throws IOException {
    long value = in.readLong();
    if (little) value = DataTools.swap(value);
    return value;
  }

  /** Reads a float from the socket with the correct endianness. */
  private float readFloat() throws IOException {
    float readFloat = in.readFloat();
    int intRep = Float.floatToIntBits(readFloat);
    if (little) intRep = DataTools.swap(intRep);
    float value = Float.intBitsToFloat(intRep);
    return value;
  }

  /** Reads a double from the socket with the correct endianness. */
  private double readDouble() throws IOException {
    double readDouble = in.readDouble();
    long longRep = Double.doubleToLongBits(readDouble);
    if (little) longRep = DataTools.swap(longRep);
    double value = Double.longBitsToDouble(longRep);
    return value;
  }

  /** Reads a string from the socket. */
  private String readString() throws IOException {
    int len = readInt();
    byte[] bytes = new byte[len];
    in.readFully(bytes, 0, len);
    return new String(bytes);
  }

  /** Writes the given short to the socket with the correct endianness. */
  private void writeShort(short value) throws IOException {
    out.writeShort(little ? DataTools.swap(value) : value);
  }

  /** Writes the given int to the socket with the correct endianness. */
  private void writeInt(int value) throws IOException {
    out.writeInt(little ? DataTools.swap(value) : value);
  }

  /** Writes the given long to the socket with the correct endianness. */
  private void writeLong(long value) throws IOException {
    out.writeLong(little ? DataTools.swap(value) : value);
  }

  /** Writes the given string to the socket. */
  private void writeString(String value) throws IOException {
    writeInt(value.length());
    out.write(value.getBytes());
  }

  // - Debugging helper methods -

  /** Prints a debugging message if debug mode is enabled. */
  private void debug(String msg) {
    if (JVMLinkServer.debug) System.out.println(getName() + ": " + msg);
  }

  // -- Static utility methods --

  public static String getType(int type) {
    switch (type) {
      case ARRAY_TYPE:
        return "ARRAY";
      case INT_TYPE:
        return "INT";
      case STRING_TYPE:
        return "STRING";
      case BYTE_TYPE:
        return "BYTE";
      case CHAR_TYPE:
        return "CHAR";
      case FLOAT_TYPE:
        return "FLOAT";
      case BOOLEAN_TYPE:
        return "BOOLEAN";
      case DOUBLE_TYPE:
        return "DOUBLE";
      case LONG_TYPE:
        return "LONG";
      case SHORT_TYPE:
        return "SHORT";
      default:
        return "UNKNOWN [" + type + "]";
    }
  }

  public static String getCommand(int cmd) {
    switch (cmd) {
      case BYTE_ORDER:
        return "BYTE_ORDER";
      case SETVAR:
        return "SETVAR";
      case GETVAR:
        return "GETVAR";
      case EXEC:
        return "EXEC";
      case EXIT:
        return "EXIT";
      default:
        return "UNKNOWN [" + cmd + "]";
    }
  }

  public static int getSize(int type) {
    switch (type) {
      case BYTE_TYPE:
      case CHAR_TYPE:
      case BOOLEAN_TYPE:
      case NULL_TYPE:
        return 1;
      case SHORT_TYPE:
        return 2;
      case INT_TYPE:
      case FLOAT_TYPE:
        return 4;
      case DOUBLE_TYPE:
      case LONG_TYPE:
        return 8;
      case STRING_TYPE: // string size is variable
      default:
        return 0;
    }
  }

  public static String getValue(Object value) {
    if (value == null) return null;
    String val;
    try {
      int len = Array.getLength(value);
      StringBuffer sb = new StringBuffer();
      sb.append("[");
      boolean str = false;
      for (int i=0; i<len; i++) {
        if (len > 20) {
          // skip some values for large arrays
          boolean changed = true;
          if (i == 3) i = len / 2 - 1;
          else if (i == len / 2 + 2) i = len - 3;
          else changed = false;
          if (changed) {
            sb.append(str ? "\n\t" : " ");
            sb.append("...");
          }
        }
        Object o = Array.get(value, i);
        str = o instanceof String;
        sb.append(str ? "\n\t" : " ");
        sb.append(o);
      }
      sb.append(str ? "\n" : " ");
      sb.append("]");
      val = sb.toString();
    }
    catch (IllegalArgumentException exc) {
      val = value.toString();
    }
    return val;
  }

}
