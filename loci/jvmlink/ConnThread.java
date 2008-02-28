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

  public static final int SETVAR = 0;
  public static final int GETVAR = 1;
  public static final int EXEC = 2;
  public static final int EXIT = 255;

  // -- Static fields --

  private static int threadNumber = 0;

  // -- Fields --

  private Socket socket;
  private JVMLinkServer server;
  private ReflectedUniverse r;
  private DataInputStream in;
  private DataOutputStream out;

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
    start();
  }

  /*
   * Protocol syntax:
   *
   * First thing on the stream is an integer:
   *  255 - exit Server
   *  0   - setVar
   *  1   - getVar
   *  2   - exec
   *
   * Then, a newline-delimited string, specifying:
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
   *
   * Then:
   *  size - number of bytes (per item)
   *   In case of strings (not strings in arrays though) length of string
   *
   * Then, for setVar, the data
   */

  // -- Thread API methods --

  public void run() {
    boolean killServer = false;
    while (true) {
      try {
        int command = DataTools.read4SignedBytes(in, true);
        debug("Received command: " + getCommand(command));
        if (command == EXIT) {
          killServer = true;
          break;
        }
        switch (command) {
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

  /**
   * Performs a SETVAR command, including reading arguments
   * from the input stream.
   */
  private void setVar() throws IOException {
    String name = in.readLine();
    int type = DataTools.read4SignedBytes(in, true);
    Object value = null;
    if (type == ARRAY_TYPE) {
      int insideType = DataTools.read4SignedBytes(in, true);
      int arrayLength = DataTools.read4SignedBytes(in, true);
      int size = DataTools.read4SignedBytes(in, true);
      debug("in array type for variable " + name +
        " insidetype, length, size: " + insideType + ", " + arrayLength +
        ", " + size);
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
                DataTools.bytesToInt(b, 4*i, true);
            }
            readBytes += packetSize;
          }
          theArray = intArray;
        }
      else if (insideType == STRING_TYPE) {
        String[] stringArray = new String[arrayLength];
          for (int i=0; i<arrayLength; i++) {
            stringArray[i] = in.readLine();
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
          //debug("recvd char array is " + new String(charArray));
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
              Float.intBitsToFloat(DataTools.bytesToInt(b, 4*i, true));
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
              Double.longBitsToDouble(DataTools.bytesToLong(b, 8*i, true));
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
              DataTools.bytesToLong(b, 8*i, true);
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
          for (int i=0; i<packetSize/4; i++) {
            shortArray[i + readBytes/4] =
              DataTools.bytesToShort(b, 4*i, true);
          }
          readBytes += packetSize;
        }
        theArray = shortArray;
      }
      value = theArray;
    }
    else if (type == INT_TYPE) {
      int readInt = DataTools.read4SignedBytes(in, true);
      value = new Integer(readInt);
    }
    else if (type == STRING_TYPE) {
      value = in.readLine();
    }
    else if (type == BYTE_TYPE) {
      byte readByte = in.readByte();
      value = new Byte(readByte);
    }
    else if (type == CHAR_TYPE) {
      char readChar = in.readChar();
      value = new Character(readChar);
    }
    else if (type == FLOAT_TYPE) {
      float readFloat = in.readFloat();
      int intRep = Float.floatToIntBits(readFloat);
      value = new Float(Float.intBitsToFloat(DataTools.swap(intRep)));
    }
    else if (type == BOOLEAN_TYPE) {
      boolean readBoolean = in.readBoolean();
      value = new Boolean(readBoolean);
    }
    else if (type == DOUBLE_TYPE) {
      double readDouble = in.readDouble();
      long longRep = Double.doubleToLongBits(readDouble);
      value = new Double(Double.longBitsToDouble(DataTools.swap(longRep)));
      //value = new Double(readDouble);
    }
    else if (type == LONG_TYPE) {
      long readLong = in.readLong();
      value = new Long(DataTools.swap(readLong));
    }
    else if (type == SHORT_TYPE) {
      short readShort = in.readShort();
      value = new Short(DataTools.swap(readShort));
    }
    debug("setVar ("+type+"): " + name + " = " + getValue(value));
    if (value != null) r.setVar(name, value);
  }

  /**
   * Performs a GETVAR command, including reading arguments
   * from the input stream and sending results to the output stream.
   */
  private void getVar() throws IOException {
    String name = in.readLine();
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
      else type = INT_TYPE; //default
    }
    catch (ReflectException e) {
      if (JVMLinkServer.debug) e.printStackTrace();
    }

    out.writeInt(DataTools.swap(type));
    //TODO: still need to swap most of what is sent.
    if (type == ARRAY_TYPE) {
      Object theArray = value;
      out.writeInt(DataTools.swap(insideType));
      int arrayLen = Array.getLength(theArray);
      out.writeInt(DataTools.swap(arrayLen));
      if (insideType == INT_TYPE) {
        out.writeInt(DataTools.swap(4));
        int[] intArray = (int[]) theArray;
        for (int i=0; i<arrayLen; i++) {
          out.writeInt(DataTools.swap(intArray[i]));
        }
        if (arrayLen > 10000) {
          debug("Last two elements are " +
            intArray[arrayLen-1] + " and " + intArray[arrayLen-2]);
        }
      }
      else if (insideType == STRING_TYPE) {
        //untested. probably quite messed up.
        out.writeInt(DataTools.swap(4));
        String[] sArray = (String[]) theArray;
        for (int i=0; i<arrayLen; i++) out.write(sArray[i].getBytes());
      }
      else if (insideType == BYTE_TYPE) {
        out.writeInt(DataTools.swap(1));
        byte[] bArray = (byte[]) theArray;
        for (int i=0; i<arrayLen; i++) out.writeByte(bArray[i]);
      }
      else if (insideType == CHAR_TYPE) {
        //need to fix this to send only one byte.
        out.writeInt(DataTools.swap(1));
        char[] cArray = (char[]) theArray;
        for (int i=0; i<arrayLen; i++) out.writeByte((byte) cArray[i]);
        //for (int i=0; i<arrayLen; i++) {
        //  out.writeChar(DataTools.swap(cArray[i]));
        //}
      }
      else if (insideType == FLOAT_TYPE) {
        out.writeInt(DataTools.swap(4));
        float[] fArray = (float[]) theArray;
        for (int i=0; i<arrayLen; i++) {
          out.writeInt(DataTools.swap(Float.floatToIntBits(fArray[i])));
        }
      }
      else if (insideType == BOOLEAN_TYPE) {
        out.writeInt(DataTools.swap(1));
        boolean[] bArray = (boolean[]) theArray;
        for (int i=0; i<arrayLen; i++) out.writeBoolean(bArray[i]);
      }
      else if (insideType == DOUBLE_TYPE) {
        out.writeInt(DataTools.swap(8));
        double[] dArray = (double[]) theArray;
        for (int i=0; i<arrayLen; i++) {
          out.writeLong(DataTools.swap(Double.doubleToLongBits(dArray[i])));
        }
      }
      else if (insideType == LONG_TYPE) {
        out.writeInt(DataTools.swap(8));
        long[] lArray = (long[]) theArray;
        for (int i=0; i<arrayLen; i++) {
          out.writeLong(DataTools.swap(lArray[i]));
        }
      }
      else if (insideType == SHORT_TYPE) {
        out.writeInt(DataTools.swap(2));
        short[] sArray = (short[]) theArray;
        for (int i=0; i<arrayLen; i++) {
          out.writeShort(DataTools.swap(sArray[i]));
        }
      }
    }
    else if (type == INT_TYPE) {
      int val = ((Integer) value).intValue();
      out.writeInt(DataTools.swap(4));
      out.writeInt(DataTools.swap(val));
    }
    else if (type == STRING_TYPE) {
      String val = (String) value;
      out.writeInt(DataTools.swap(val.length()));
      debug("Number of bytes=" + val.length());
      out.writeBytes(val);
      //String name = (String) r.getVar(name);
      //out.write(name.getBytes(Charset.forName("UTF-16")));
      //debug("Returning string: " +
      //  name.getBytes(Charset.forName("UTF-16")));
    }
    else if (type == BYTE_TYPE) {
      byte val = ((Byte) value).byteValue();
      out.writeInt(DataTools.swap(1));
      out.writeByte(val);
    }
    else if (type == CHAR_TYPE) {
      char val = ((Character) value).charValue();
      //out.writeInt(DataTools.swap(1));
      //out.writeByte((byte) val);
      //fix this to write one byte only
      out.writeInt(DataTools.swap(2));
      out.writeChar(DataTools.swap(val));
    }
    else if (type == FLOAT_TYPE) {
      float val = ((Float) value).floatValue();
      out.writeInt(DataTools.swap(4));
      out.writeInt(DataTools.swap(Float.floatToIntBits(val)));
    }
    else if (type == BOOLEAN_TYPE) {
      boolean val = ((Boolean) value).booleanValue();
      out.writeInt(DataTools.swap(1));
      out.writeBoolean(val);
    }
    else if (type == DOUBLE_TYPE) {
      double val = ((Double) value).doubleValue();
      out.writeInt(DataTools.swap(8));
      out.writeLong(DataTools.swap(Double.doubleToLongBits(val)));
    }
    else if (type == LONG_TYPE) {
      long val = ((Long) value).longValue();
      out.writeInt(DataTools.swap(8));
      out.writeLong(DataTools.swap(val));
    }
    else if (type == SHORT_TYPE) {
      short val = ((Short) value).shortValue();
      out.writeInt(DataTools.swap(2));
      out.writeShort(DataTools.swap(val));
    }
    out.flush();
  }

  /**
   * Performs an EXEC command, including reading arguments
   * from the input stream.
   */
  private void exec() throws IOException, ReflectException {
    String cmd = in.readLine();
    debug("exec: " + cmd);
    r.exec(cmd);
  }

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

  public static String getValue(Object value) {
    String val = value.toString();
    try {
      val += " (length " + Array.getLength(value) + ")";
    }
    catch (IllegalArgumentException exc) { }
    return val;
  }

/*
  private byte[] readArray(DataInputStream in, int size, int arrayLength)
    throws IOException
  {
    int readBytes = 0, totalBytes = size*arrayLength;
    byte[] b = new byte[totalBytes];
    while (readBytes < totalBytes) {
      int packetSize = MAX_PACKET_SIZE;
      if (readBytes+MAX_PACKET_SIZE > totalBytes) {
        packetSize = totalBytes - readBytes;
      }
      int readThisTime = in.read(b, readBytes, packetSize);
      while (readThisTime < packetSize) {
        System.out.println("in loop: didn't read fully");
        int additional = in.read(b, readBytes+readThisTime,
          packetSize-readThisTime);
        readThisTime += additional;
      }
      readBytes += packetSize;
    }
    return b;
  }
*/

}

/* Old code:
int[] intArray = new int[arrayLength];
int readBytes = 0, totalBytes = size*arrayLength;
while (readBytes < totalBytes) {
  int packetSize = MAX_PACKET_SIZE;
  if (readBytes+MAX_PACKET_SIZE > totalBytes) {
    packetSize = totalBytes - readBytes;
  }
  byte[] b = new byte[packetSize];
  int readThisTime = in.read(b, 0, packetSize);
  while (readThisTime < packetSize) {
    System.out.println("in loop: didn't read fully");
    int additional = in.read(b, readThisTime, packetSize-readThisTime);
    readThisTime += additional;
  }
  for (int i=0; i<packetSize/4; i++) {
    intArray[i + (readBytes/4)] = DataTools.bytesToInt(b, 4*i, true);
  }
  readBytes += packetSize;
}
theArray = intArray;
*/
