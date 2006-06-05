//
// OLEParser.java
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
import java.util.*;
import loci.formats.DataTools;

/**
 * Utility class for parsing OLE compound files.
 *
 * Overview of the OLE file format available at
 * http://jakarta.apache.org/poi/poifs/fileformat.html
 *
 * @author Melissa Linkert, linkert at cs.wisc.edu
 */
public class OLEParser {

  // -- Constants --

  /** OLE compound magic number. */
  private static final byte[] MAGIC_NUMBER = new byte[] {
    (byte) 0xd0, (byte) 0xcf, 0x11, (byte) 0xe0,
    (byte) 0xa1, (byte) 0xb1, 0x1a, (byte) 0xe1
  };

  // -- Fields --

  /** Current file name. */
  private String filename;

  /** Current file input stream. */
  private RandomAccessStream in;

  /** Block allocation table. */
  private int[] bat;

  /** Small block allocation table. */
  private int[] sbat;

  /** Small block array. */
  private byte[] smallBlockArray;

  /** Big block size. */
  private int bigBlock;

  /** Small block size. */
  private int smallBlock;

  /** Byte array containing the entire array of property tables. */
  private byte[] propertyArray;

  /** Array of property table indices that we've already visited. */
  private Vector indices;

  /** Vector of parent nodes. */
  private Vector parents;

  /** Red-black tree representing the filesystem structure. */
  private RedBlackTree fileSystem;

  /** Blocks we've already read. */
  private Vector read;

  /** Last property array index parsed. */
  private int last;

  /** Whether or not we've parsed all of the property array entries. */
  private boolean parsedAll = false;

  /** Ordered list of item names. */
  private Vector itemNames;
 
  // -- Variables for a weird special case --
  
  /** Cut point for data shuffling. */
  private int cutPoint;
 
  /** Actual number of bytes read in the "main" file. */
  private int realNumRead;
  
  // -- Constructor --

  public OLEParser(String filename) {
    this.filename = filename;
  }

  // -- Internal API methods --

  /** Check if this file is actually an OLE file. */
  public static boolean isOLE(byte[] block) {
    if (block == null) return false;
    int len = block.length < MAGIC_NUMBER.length ? block.length :
      MAGIC_NUMBER.length;
    for (int i=0; i<len; i++) if (block[i] != MAGIC_NUMBER[i]) return false;
    return true;
  }

  /** Parse blocks at the given depth within the file. */
  public void parse(int depth) throws IOException {
    in = new RandomAccessStream(filename);
    parents = new Vector();
    fileSystem = new RedBlackTree();
    indices = new Vector();
    read = new Vector();
    itemNames = new Vector();

    long check = DataTools.read8SignedBytes(in, true);
    if (check != DataTools.bytesToLong(MAGIC_NUMBER, true)) {
      throw new IOException("magic number does not match.  Either " +
        "the file is not an OLE variant or you didn't check the endianness");
    }

    in.skipBytes(16);

    short revision = DataTools.read2SignedBytes(in, true);
    short version = DataTools.read2SignedBytes(in, true);

    in.skipBytes(2);

    bigBlock = (int) Math.pow(2, DataTools.read2SignedBytes(in, true));
    smallBlock = (int) Math.pow(2, DataTools.read4SignedBytes(in, true));

    in.skipBytes(8);

    int batCount = DataTools.read4SignedBytes(in, true);
    int propertyTableIndex = DataTools.read4SignedBytes(in, true);

    in.skipBytes(8);

    int sbatIndex = DataTools.read4SignedBytes(in, true);
    int numSbatBlocks = DataTools.read4SignedBytes(in, true);
    int xbatIndex = DataTools.read4SignedBytes(in, true);
    int numXbatBlocks = DataTools.read4SignedBytes(in, true);

    bat = new int[batCount + numXbatBlocks];

    // read the BAT
    for (int i=0; i<batCount; i++) {
      bat[i] = DataTools.read4SignedBytes(in, true);
    }

    // TODO: read the XBAT if necessary (just add to the BAT for simplicity)

    for (int i=batCount; i<bat.length; i++) {
      // read the XBAT here
    }

    // read the SBAT

    sbat = new int[numSbatBlocks * (bigBlock / 4)];
    in.seek((sbatIndex+1) * bigBlock);
    read.add(new Integer(sbatIndex + 1));
    for (int i=0; i<sbat.length; i++) {
      sbat[i] = DataTools.read4SignedBytes(in, true);
    }

    getPropertyArray(propertyTableIndex);

    // recursively parse each directory entry
    parseDir(0, 0);
  }

  /** Find and link together the blocks comprising the property table. */
  private void getPropertyArray(int firstBlock) throws IOException {
    // this is kind of inefficient for large files with small big block sizes

    int pa = firstBlock + 1;

    propertyArray = new byte[0];
    Vector v = new Vector();

    // really naive strategy:
    // Read 524288 bytes at a time, and check each block in the buffer to see
    // if it looks like a property array block.  To check, look at bytes 66
    // and 67; byte 66 should be 1, 2, or 5 and byte 67 should be 0 or 1.

    // TODO : make this more efficient

    byte[] buf = new byte[524288];
    int baseBlock = 2;

    while (in.getFilePointer() < in.length()) {
      in.seek(baseBlock * bigBlock);
      in.read(buf);

      for (int i=0; i<(buf.length / bigBlock); i++) {
        int offset = i * bigBlock;

        byte check1 = buf[offset + 66];
        byte check2 = buf[offset + 67];
        short check3 = DataTools.bytesToShort(buf, offset + 64, 2, true);
        if ((check1 == 1) || (check1 == 2) || (check1 == 5)) {
          if (check2 == 0 || check2 == 1) {
            if (check3 <= 32 && check3 >= 0) {
              byte[] check4 = new byte[32 - check3];
              System.arraycopy(buf, offset + (64 - check3),
                check4, 0, check4.length);
              boolean isZero = true;
              for (int j=0; j<check4.length; j++) {
                if (check4[j] != 0) isZero = false;
              }
              if (isZero) {
                if (DataTools.bytesToInt(buf, offset + 116, 4, true) <
                  (in.length() / bigBlock)) {
                  v.add(new Integer(baseBlock + i + 2));
                }
              }
            }
          }
        }
      }
      baseBlock += (buf.length / bigBlock);
    }

    int[] array = new int[v.size()];
    for (int i=0; i<array.length; i++) {
      array[i] = ((Integer) v.get(i)).intValue() - 2;
    }

    int next = 0;
    while ((pa > 0) && (pa*bigBlock < in.length()) && (next <= array.length)) {
      in.seek(pa * bigBlock);
      read.add(new Integer(pa));

      // allocate more space in the property array

      byte[] tmp = propertyArray;
      propertyArray = new byte[tmp.length + bigBlock];
      System.arraycopy(tmp, 0, propertyArray, 0, tmp.length);
      in.read(propertyArray, tmp.length, bigBlock);

      if (next < array.length) pa = array[next];
      next++;
    }
  }

  /** Find and link together the blocks comprising the small block array. */
  private void buildSmallBlockArray(int fileSize, int firstBlock)
    throws IOException
  {
    int numBigBlocks = ((fileSize / 64) / bigBlock) + 1;

    ByteVector sba = new ByteVector(3*bigBlock);

    int blocksRead = 0;
    while ((numBigBlocks > 0) && (firstBlock > 0)) {
      in.seek(firstBlock * bigBlock);
      read.add(new Integer(firstBlock));
      byte[] block = new byte[bigBlock];
      in.read(block);

      for (int i=0; i<block.length; i+=4) {
        if ((blocksRead*bigBlock + (i / 4)) < (fileSize / 64)) {
          int blockNumber = DataTools.bytesToInt(block, i, 4, true);
          if ((blockNumber*bigBlock) > 0) {
            in.seek(blockNumber * bigBlock);
            read.add(new Integer(blockNumber));
            byte[] buf = new byte[bigBlock];
            in.read(buf);
            sba.add(buf);
          }
        }
      }

      smallBlockArray = sba.toByteArray();

      firstBlock = sbat[firstBlock];
      blocksRead++;
      numBigBlocks--;
    }
  }

  /**
   * Parse a directory entry at the given depth, with the
   * specified number of available bytes.
   */
  private void parseDir(int depth, int ndx) throws IOException {
    // now we'll read the next property table

    byte[] table = new byte[128];

    System.arraycopy(propertyArray, ndx*128, table, 0, table.length);

    last = ndx;
    indices.add(new Integer(ndx));

    // extract some information from the property table

    String name = new String(table, 0, 64);
    if (name.trim().equals("")) return;

    if (name.indexOf("(") != -1 && name.indexOf(")") != -1) {
      itemNames.add(
        name.substring(name.indexOf("(") + 1, name.indexOf(")")).trim());
    }

    short numNameChars = DataTools.bytesToShort(table, 64, 2, true);
    byte propType = table[66]; // 1 = dir, 2 = file, 5 = root
    byte nodeColor = table[67]; // 0 = red, 1 = black
    int previousIndex = DataTools.bytesToInt(table, 68, 4, true);
    int nextIndex = DataTools.bytesToInt(table, 72, 4, true);
    int firstChildIndex = DataTools.bytesToInt(table, 76, 4, true);
    int createdSeconds = DataTools.bytesToInt(table, 100, 4, true);
    int createdDays = DataTools.bytesToInt(table, 104, 4, true);
    int modifiedSeconds = DataTools.bytesToInt(table, 108, 4, true);
    int modifiedDays = DataTools.bytesToInt(table, 112, 4, true);
    int firstBlock = DataTools.bytesToInt(table, 116, 4, true);
    int fileSize = DataTools.bytesToInt(table, 120, 4, true);

    if (propType != 5) {
      fileSystem.add(nodeColor, propType, ndx, nextIndex,
        ((Integer) parents.get(parents.size() - 1)).intValue(),
        name, firstBlock, fileSize);
    }
    else if (fileSystem.nullRoot()) {
      buildSmallBlockArray(fileSize, firstBlock);

      fileSystem.add(nodeColor, propType, 0, nextIndex,
        -1, name, firstBlock, fileSize);
      parents.add(new Integer(0));
    }

    // parse the child

    if ((firstChildIndex != -1) &&
      !indices.contains(new Integer(firstChildIndex)) &&
      (firstChildIndex < (propertyArray.length / 128)))
    {
      if ((propType == 5) && fileSystem.nullRoot()) {
        parents.add(new Integer(0));
      }
      else {
        parents.add(new Integer(ndx));
      }
      parseDir(depth + 1, firstChildIndex);
    }

    // parse the previous node

    if ((previousIndex != -1) &&
      !indices.contains(new Integer(previousIndex)) &&
      (previousIndex < (propertyArray.length / 128)))
    {
      parseDir(depth, previousIndex);
    }

    // parse the next node

    if ((nextIndex != -1) && !indices.contains(new Integer(nextIndex)) &&
      (nextIndex < (propertyArray.length / 128)))
    {
      parseDir(depth, nextIndex);
    }

    // if there is anything left, parse that
    if (!parsedAll && (indices.size() < (propertyArray.length / 128))) {
      // find the next index to parse
      for (int i=last; i<(propertyArray.length / 128); i++) {
        if (parsedAll) return;
        if (!indices.contains(new Integer(i))) {
          parseDir(depth, i);
        }
      }
      parsedAll = true;
    }
  }

  /**
   * Get the names of data items in the order they were parsed.
   */
  public Vector getNames() {
    return itemNames;
  }

  /**
   * Build the files; that is, for each file, construct a byte array given
   * the BAT and the starting block.  The resulting pair of Vectors can be used
   * by file format readers
   */
  public Vector[] getFiles() throws IOException {
    Vector leaves = fileSystem.getLeaf();
    Vector names = new Vector();  // should be the full path through the tree
    Vector files = new Vector();

    for (int i=0; i<leaves.size(); i++) {
      RedBlackTreeNode node = (RedBlackTreeNode) leaves.get(i);
      names.add(node.getName());

      int start = node.getFirstBlock();
      int size = node.getSize() + bigBlock;

      byte[] file = new byte[size];

      int numRead = 0;

      // if the size of the file is less than 4096, use the SBAT
      // otherwise, use the BAT

      if (size < 4096) {
        // offset is start * smallBlock

        int ndx = start;
        while ((numRead < size) && (ndx >= 0)) {
          int toCopy = smallBlock;
          if ((size - numRead) < smallBlock) {
            toCopy = size - numRead;
          }

          System.arraycopy(smallBlockArray, ndx*smallBlock, file,
            numRead, toCopy);
          numRead += toCopy;

          int old = ndx;
          if (ndx < sbat.length) ndx = sbat[ndx];
          else ndx++;
          if (ndx * smallBlock >= smallBlockArray.length ||
            (ndx*smallBlock < 0))
          {
            ndx = old + 1;
          }
        }
      }
      else {
        start++;     
    
        int numBlocksInFile = 0;
        
        Vector v = new Vector(); 
        
        while ((numRead < size) && (start < (in.length() / bigBlock))) {
          in.seek(bigBlock * start);
          v.add(new Integer(start));
          numBlocksInFile++;
          
          int toCopy = bigBlock;
          if ((size - numRead) < bigBlock) {
            toCopy = size - numRead;
          }
          numRead += toCopy;

          if (start < bat.length) {
            start = bat[start];
          }
          else {
            start++;
           
            while (inBat(start - 1) && !read.contains(new Integer(start))) {
              start++;
            }
          }
        }
    

        boolean reallyWeirdSpecialCase = false;
        int ndx = 0;
        if (numBlocksInFile < (size / bigBlock)) {
          // In this case, we haven't found enough blocks to make up the file.
          // This probably reflects a flaw in the parser logic, but since only
          // one of our files demonstrates this behavior, it is reasonable to
          // assume that this is just another brain-damaged aspect of the OLE
          // file format.
                
          // basically, we want to loop through each block in the file, check
          // if it has already been read; if not, we will try to add it to
          // the beginning of the block list for this file
      
          ndx = v.size();
          for (int j=0; j<(in.length() / bigBlock); j++) {
            if (!read.contains(new Integer(j)) && !inBat(j - 1) && 
              !v.contains(new Integer(j))) 
            {
              v.add(new Integer(j));
            }       
          }
          if (v.size() > ndx) reallyWeirdSpecialCase = true;
        }
    
        // now read all of the blocks into the file
       
        numRead = 0;
        for (int j=0; j<v.size(); j++) {
          in.seek(((Integer) v.get(j)).intValue() * bigBlock);
          read.add((Integer) v.get(j));
          int toCopy = bigBlock;
          if ((size - numRead) < bigBlock) toCopy = size - numRead;
          in.read(file, numRead, toCopy);
          numRead += toCopy;
          if ((j < ndx) && reallyWeirdSpecialCase) cutPoint += toCopy;
        }
        if (reallyWeirdSpecialCase) realNumRead = numRead;
      }
      files.add(file);
    }

    return new Vector[] {names, files};
  }
 
  public int shuffle() { 
    return cutPoint; 
  }
  
  public int length() { return realNumRead; }
  
  private boolean inBat(int b) {
    for (int i=0; i<bat.length; i++) if (b == bat[i]) return true;
    return false;
  }

}
