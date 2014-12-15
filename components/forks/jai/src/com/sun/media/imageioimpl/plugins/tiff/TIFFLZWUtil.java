/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2014 Open Microscopy Environment:
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
 * #L%
 */

/*
 * $RCSfile: TIFFLZWUtil.java,v $
 *
 * 
 * Copyright (c) 2005 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 
 * 
 * - Redistribution of source code must retain the above copyright 
 *   notice, this  list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in 
 *   the documentation and/or other materials provided with the
 *   distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of 
 * contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any 
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND 
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL 
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF 
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR 
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES. 
 * 
 * You acknowledge that this software is not designed or intended for 
 * use in the design, construction, operation or maintenance of any 
 * nuclear facility. 
 *
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:01:48 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.tiff;

import java.awt.Rectangle;
import java.io.IOException;
import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.TIFFTag;

public class TIFFLZWUtil {

    private static final boolean debug = false;

    public TIFFLZWUtil() {
    }

    byte[] srcData;
    int srcIndex;

    byte[] dstData;
    int dstIndex = 0;

    byte stringTable[][];
    int tableIndex, bitsToGet = 9;

    int predictor, samplesPerPixel;
    int nextData = 0;
    int nextBits = 0;

    private static final int andTable[] = {
	511, 
	1023,
	2047,
	4095
    };

    public byte[] decode(byte[] data, int predictor, int samplesPerPixel,
			 int width, int height) throws IOException {
        if (data[0] == (byte)0x00 && data[1] == (byte)0x01) {
            throw new IIOException("TIFF 5.0-style LZW compression is not supported!");
        }

        this.srcData = data;
        this.srcIndex = 0;
	this.nextData = 0;
	this.nextBits = 0;

        this.dstData = new byte[8192];
        this.dstIndex = 0;

        initializeStringTable();

	int code, oldCode = 0;
	byte[] string;

	while ((code = getNextCode()) != 257) {
	    if (code == 256) {
		initializeStringTable();
		code = getNextCode();
		if (code == 257) {
		    break;
		}

		writeString(stringTable[code]);
		oldCode = code;
	    } else {
		if (code < tableIndex) {
		    string = stringTable[code];

		    writeString(string);
		    addStringToTable(stringTable[oldCode], string[0]); 
		    oldCode = code;
		} else {
		    string = stringTable[oldCode];
		    string = composeString(string, string[0]);
		    writeString(string);
		    addStringToTable(string);
		    oldCode = code;
		}
	    }
	}

	if (predictor == 2) {
	    
	    int count;
	    for (int j = 0; j < height; j++) {
		
		count = samplesPerPixel * (j * width + 1);
		
		for (int i = samplesPerPixel; i < width * samplesPerPixel; i++) {
		    
		    dstData[count] += dstData[count - samplesPerPixel];
		    count++;
		}
	    }
	}

        byte[] newDstData = new byte[dstIndex];
        System.arraycopy(dstData, 0, newDstData, 0, dstIndex);
        return newDstData;
    }

    /**
     * Initialize the string table.
     */
    public void initializeStringTable() {
	stringTable = new byte[4096][];
	
	for (int i = 0; i < 256; i++) {
	    stringTable[i] = new byte[1];
	    stringTable[i][0] = (byte)i;
	}
	
	tableIndex = 258;
	bitsToGet = 9;
    }

    private void ensureCapacity(int bytesToAdd) {
        if (dstIndex + bytesToAdd > dstData.length) {
            byte[] newDstData = new byte[Math.max((int)(dstData.length*1.2f),
                                                  dstIndex + bytesToAdd)];
            System.arraycopy(dstData, 0, newDstData, 0, dstData.length);
            dstData = newDstData;
        }
    }

    /**
     * Write out the string just uncompressed.
     */
    public void writeString(byte string[]) {
        ensureCapacity(string.length);
        for (int i = 0; i < string.length; i++) {
            dstData[dstIndex++] = string[i];
        }
    }
    
    /**
     * Add a new string to the string table.
     */
    public void addStringToTable(byte oldString[], byte newString) {
	int length = oldString.length;
	byte string[] = new byte[length + 1];
	System.arraycopy(oldString, 0, string, 0, length);
	string[length] = newString;
	
	// Add this new String to the table
	stringTable[tableIndex++] = string;
	
	if (tableIndex == 511) {
	    bitsToGet = 10;
	} else if (tableIndex == 1023) {
	    bitsToGet = 11;
	} else if (tableIndex == 2047) {
	    bitsToGet = 12;
	} 
    }

    /**
     * Add a new string to the string table.
     */
    public void addStringToTable(byte string[]) {
	// Add this new String to the table
	stringTable[tableIndex++] = string;
	
	if (tableIndex == 511) {
	    bitsToGet = 10;
	} else if (tableIndex == 1023) {
	    bitsToGet = 11;
	} else if (tableIndex == 2047) {
	    bitsToGet = 12;
	} 
    }

    /**
     * Append <code>newString</code> to the end of <code>oldString</code>.
     */
    public byte[] composeString(byte oldString[], byte newString) {
	int length = oldString.length;
	byte string[] = new byte[length + 1];
	System.arraycopy(oldString, 0, string, 0, length);
	string[length] = newString;

	return string;
    }

    // Returns the next 9, 10, 11 or 12 bits
    public int getNextCode() {
        // Attempt to get the next code. The exception is caught to make
        // this robust to cases wherein the EndOfInformation code has been
        // omitted from a strip. Examples of such cases have been observed
        // in practice.

        try {
            nextData = (nextData << 8) | (srcData[srcIndex++] & 0xff);
            nextBits += 8;

            if (nextBits < bitsToGet) {
                nextData = (nextData << 8) | (srcData[srcIndex++] & 0xff);
                nextBits += 8;
            }

            int code =
                (nextData >> (nextBits - bitsToGet)) & andTable[bitsToGet - 9];
            nextBits -= bitsToGet;

            return code;
        } catch (ArrayIndexOutOfBoundsException e) {
            // Strip not terminated as expected: return EndOfInformation code.
            return 257;
        }
    }
}
