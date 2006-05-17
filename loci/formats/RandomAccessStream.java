//
// RandomAccessStream.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

/**
 * RandomAccessStream provides methods for "intelligent" reading of files and
 * byte arrays.   
 *
 * @author Melissa Linkert, linkert at cs.wisc.edu
 */
public class RandomAccessStream implements DataInput {

  // -- Constants --
       
  /** Maximum size of the buffer used by the DataInputStream. */
  private static final int MAX_OVERHEAD = 1048576;
  
  // -- Fields --

  private RandomAccessFile raf;
  private DataInputStream dis;

  /** The file pointer within the DIS. */
  private int fp;
  
  /** The "absolute" file pointer. */
  private int afp;
  
  /** Most recent mark. */
  private int mark;
 
  /** Next place to mark. */
  private int nextMark;
  
  /** The file name. */
  private String file;
 
  // -- Constructors --

  public RandomAccessStream(String file) throws IOException {
    raf = new RandomAccessFile(file, "r");
    dis = new DataInputStream(new BufferedInputStream(
      new FileInputStream(file), MAX_OVERHEAD));      
    this.file = file;
    fp = 0;
    afp = 0;
    nextMark = MAX_OVERHEAD;
  }        

  public RandomAccessStream(byte[] array) throws IOException {
    raf = new RandomAccessFile("/dev/null", "r"); // this is a problem 
    dis = new DataInputStream(new BufferedInputStream(
      new ByteArrayInputStream(array), MAX_OVERHEAD));
    fp = 0;
    afp = 0;
    nextMark = MAX_OVERHEAD;
  }       

  // -- DataInput API methods --

  /** Read an input byte and return true if the byte is nonzero. */
  public boolean readBoolean() throws IOException {
    return (readByte() != 0);
  }
  
  /** Read one byte and return it. */
  public byte readByte() throws IOException {
    boolean useDIS = checkEfficiency(1);
    if (useDIS) {
      return dis.readByte();
    }
    else {
      byte b = raf.readByte();
      return b;
    }
  }

  /** Read an input char. */
  public char readChar() throws IOException {
    return (char) readByte();
  }        
 
  /** Read eight bytes and return a double value. */
  public double readDouble() throws IOException {
    return Double.longBitsToDouble(readLong());
  }

  /** Read four bytes and return a float value. */
  public float readFloat() throws IOException {
    return Float.intBitsToFloat(readInt());
  }

  /** Read four input bytes and return an int value. */
  public int readInt() throws IOException {
    return DataTools.read4SignedBytes(this, false);
  }

  /** Read the next line of text from the input stream. */
  public String readLine() throws IOException {
    StringBuffer sb = new StringBuffer();
    char c = readChar();
    while (c != '\n') {
      sb = sb.append(c);
      c = readChar();
    }        
    return sb.toString(); 
  }

  /** Read eight input bytes and return a long value. */
  public long readLong() throws IOException {
    return DataTools.read8SignedBytes(this, false);
  }       

  /** Read two input bytes and return a short value. */
  public short readShort() throws IOException {
    return DataTools.read2SignedBytes(this, false);
  }

  /** Read an input byte and zero extend it appropriately. */
  public int readUnsignedByte() throws IOException {
    return DataTools.readUnsignedByte(this);
  }

  /** Read two bytes and return an int in the range 0 through 65535. */
  public int readUnsignedShort() throws IOException {
    return DataTools.read2UnsignedBytes(this, false);
  }

  /** Read a string that has been encoded using a modified UTF-8 format. */
  public String readUTF() throws IOException {
    return null;  // not implemented yet...we don't really need this
  }

  /** Skip n bytes within the stream. */
  public int skipBytes(int n) throws IOException {
    afp += n;
    return n;
  }

  /** Read bytes from the stream into the given array. */
  public int read(byte[] array) throws IOException {
    boolean useDIS = checkEfficiency(array.length);
    
    if (useDIS) {
      return dis.read(array);      
    }
    else {
      return raf.read(array);
    }     
  }

  /** 
   * Read n bytes from the stream into the given array at the specified offset.
   */
  public int read(byte[] array, int offset, int n) throws IOException {
    boolean useDIS = checkEfficiency(n);
    
    if (useDIS) {
      return dis.read(array, offset, n);      
    }
    else {
      return raf.read(array, offset, n);
    }     
  }

  /** Read bytes from the stream into the given array. */
  public void readFully(byte[] array) throws IOException {
    boolean useDIS = checkEfficiency(array.length);
    
    if (useDIS) {
      dis.readFully(array);      
    }
    else {
      raf.readFully(array);
    }        
  }

  /** 
   * Read n bytes from the stream into the given array at the specified offset.
   */
  public void readFully(byte[] array, int offset, int n) throws IOException {
    boolean useDIS = checkEfficiency(n);
    
    if (useDIS) {
      dis.readFully(array, offset, n);      
    }
    else {
      raf.readFully(array, offset, n);
    }        
  }

  
  // -- Internal RandomAccessStream API methods --

  /** Seek to the given offset within the stream. */
  public void seek(long pos) throws IOException {
    afp = (int) pos;
  }

  /** Alias for readByte(). */
  public int read() throws IOException {
    return (int) readByte();
  }        
  
  /** Get the number of bytes in the file. */
  public long length() throws IOException {
    return raf.length();
  }         

  /** Get the current (absolute) file pointer. */
  public int getFilePointer() {
    return afp;
  }        
  
  /** Close the streams. */
  public void close() throws IOException {
    if (raf != null) raf.close();
    raf = null;
    dis.close();
    dis = null;
    fp = 0;
    mark = 0;
  }        
 
  // -- Helper methods --

  /** 
   * Determine whether it is more efficient to use the DataInputStream or
   * RandomAccessFile for reading (based on the current file pointers).
   * Returns true if we should use the DataInputStream.
   */
  private boolean checkEfficiency(int toRead) throws IOException {
  
    // break down this method into the following cases:
    //
    // Case 1: "sequential seek"; this means that we are seeking ahead of the 
    //         current file pointer - best of all, this is the easy case to 
    //         handle with DataInputStream's, since we just have to call 
    //         skipBytes(n)
    //
    // Case 2: "random seek"; this means that we are seeking behind the current
    //         file pointer - can be broken down into the following subcases:
    //
    //         i) fp <= mark + MAX_OVERHEAD; in this case, 
    //            we could reset and then skip bytes appropriately 
    //            (assuming a proper mark occurred)
    //
    //         ii) file pointers are aligned such that it is more efficient 
    //             to close and re-open the stream - WE NEED A GOOD 
    //             HEURISTIC FOR THIS CASE
    //
    //         iii) seek directly within the RandomAccessFile

    if (afp >= fp) {
      // Case 1
            
      while (fp < afp) {
        fp += dis.skipBytes(afp - fp);
      }        

      fp += toRead;
      afp += toRead;

      if (fp >= nextMark) dis.mark(MAX_OVERHEAD); // TODO : decrease buffer
      nextMark = fp + MAX_OVERHEAD;
      mark = fp;
    
      return true;
    }
    else {
      if ((fp <= (mark + MAX_OVERHEAD)) && (afp >= mark)) {
        // case 2i
    
        dis.reset();
        dis.mark(MAX_OVERHEAD);
        fp = mark;
        
        while (fp < afp) {
          fp += dis.skipBytes(afp - fp);  
        }        

        fp += toRead;
        afp += toRead;

        if (fp >= nextMark) dis.mark(MAX_OVERHEAD); // TODO : decrease buffer
        nextMark = fp + MAX_OVERHEAD;
        mark = fp;
     
        return true;
      }
      // TODO : 
      // Possibly add another case here to reduce the dependency on RAF.
      // In testing various heuristics, it looks like the RAF is consistently
      // more efficient than closing and re-opening the DIS; however, there's
      // probably a way of writing this case so that it ends up being more
      // efficient than 2iii in general.
      else {
        // case 2iii        
        // we don't want this to happen very often
              
        raf.seek(afp);
        
        afp += toRead;
        return false;
      }
    }      
  } 

}
