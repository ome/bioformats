package com.sun.jimi.core.encoder.jpg;

import java.lang.*;

public class HuffTbl {    // A Huffman coding table 

  // These two fields directly represent the contents of a JPEG DHT marker 
	short bits[] = new short[17];	// bits[k] = # of symbols with codes of 
				                // length k bits; bits[0] is unused 
	short huffval[] = new short[256];	// The symbols, in order of incr code length 

  /* This field is used only during compression.  It's initialized FALSE when
   * the table is created, and set TRUE when it's been output to the file.
   */
	boolean sent_table;	// TRUE when table has been output 
  /* The remaining fields are computed from the above to allow more efficient
   * coding and decoding.
   */
	// encoding tables: 
	short	ehufco[] = new short[256];	// code for each symbol 
	short	ehufsi[] = new short[256];	// length of code for each symbol 
	// decoding tables: (element [0] of each array is unused) 
	short	mincode[] = new short[17];	// smallest code of length k 
	int		maxcode[] = new int[18];	// largest code of length k (-1 if none) 
	// (maxcode[17] is a sentinel to ensure huff_DECODE terminates) 
	short	valptr[] = new short[17];	// huffval[] index of 1st symbol of length k 

    public HuffTbl(short [] in_bits, short [] in_val){
        int i;
        int len1 = in_bits.length;
        int len2 = in_val.length;

        // Define a Huffman table 
        for (i=0;i<len1;i++)
            bits[i] = in_bits[i];
        for (i=0;i<len2;i++)
            huffval[i] = in_val[i];

       sent_table = false;
    }	
}
