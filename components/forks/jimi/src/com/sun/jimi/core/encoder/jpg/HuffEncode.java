package com.sun.jimi.core.encoder.jpg;

import java.io.*;

public class HuffEncode {

	static final short DCTSIZE = 8;
	int huff_put_buffer;			// current bit-accumulation buffer 
	int huff_put_bits;				// # of bits now in it 

	byte []output_buffer;		// output buffer 
	int bytes_in_buffer;



	void
	init_huff_tbl (HuffTbl htbl)
	// Compute derived values for a Huffman table 
	{
	  int p, i, l, lastp, si;
	  byte [] huffsize = new byte[257];
	  short[] huffcode = new short[257];
	  short code;

	  // Figure C.1: make table of Huffman code length for each symbol 
	  // Note that this is in code-length order. 

	  p = 0;
	  for (l = 1; l <= 16; l++) {
	    for (i = 1; i <= (int) htbl.bits[l]; i++)
	      huffsize[p++] = (byte) l;
	  }
	  huffsize[p] = 0;
	  lastp = p;

	  // Figure C.2: generate the codes themselves 
	  // Note that this is in code-length order. 

	  code = 0;
	  si = huffsize[0];
	  p = 0;
	  while (huffsize[p] != 0) {
	    while (((int) huffsize[p]) == si) {
	      huffcode[p++] = code;
	      code++;
	    }
	    code <<= 1;
	    si++;
	  }

	  // Figure C.3: generate encoding tables 
	  // These are code and size indexed by symbol value 

	  /* Set any codeless symbols to have code length 0;
	   * this allows write_bits to detect any attempt to write such symbols.
	   */
	  for (i=0; i<htbl.ehufsi.length; i++)
	  	htbl.ehufsi[i] = 0;

	  for (p = 0; p < lastp; p++) {
	    htbl.ehufco[htbl.huffval[p]] = huffcode[p];
	    htbl.ehufsi[htbl.huffval[p]] = huffsize[p];
	  }

	  // We don't bother to fill in the decoding tables mincode[], maxcode[], 
	  // and valptr[], since they are not used for encoding. 
	}


	// Outputting bytes to the file 

	void
	flush_bytes (CompressInfo cinfo)
	{
		try {
			cinfo.output_file.flush(); 
		} catch (IOException e){}
	}

	public void write_byte(CompressInfo cinfo, int b){
		try{
			cinfo.output_file.writeByte(b);
		}catch(IOException e){}
	}

	// Outputting bits to the file 

	void
	write_bits (CompressInfo cinfo, int code, int size)
	{
	  // This routine is heavily used, so it's worth coding tightly. 
	  int put_buffer = code;
	  int put_bits = huff_put_bits;

	  // if size is 0, caller used an invalid Huffman table entry 
	  if (size == 0)
	    util.errexit("Missing Huffman code table entry");

	  put_buffer &= (((int) 1) << size) - 1; // Mask off any excess bits in code 

	  put_bits += size;		// new number of bits in buffer 

	  put_buffer <<= 24 - put_bits; // align incoming bits 

	  put_buffer |= huff_put_buffer; // and merge with old buffer contents 

	  while (put_bits >= 8) {
	    int c = (int) ((put_buffer >> 16) & 0xFF);

	    write_byte(cinfo, c);
	    if (c == 0xFF) {		// need to stuff a zero byte? 
	      write_byte(cinfo, 0);
	    }
	    put_buffer <<= 8;
	    put_bits -= 8;
	  }

	  huff_put_buffer = put_buffer;	// Update global variables 
	  huff_put_bits = put_bits;
	}


	void
	flush_bits (CompressInfo cinfo)
	{
	  write_bits(cinfo, (short) 0x7F, 7);	// fill any partial byte with ones 
	  huff_put_buffer = 0;		// and reset bit-buffer to empty 
	  huff_put_bits = 0;
	}



	// Encode a single block's worth of coefficients 
	// Note that the DC coefficient has already been converted to a difference 

	void
	encode_one_block (CompressInfo cinfo, int [] block, HuffTbl dctbl, HuffTbl actbl)
	{
	  int temp, temp2;
	  int nbits;
	  int k, r, i;

	  // Encode the DC coefficient difference per section F.1.2.1 

	  temp = temp2 = block[0];

	  if (temp < 0) {
	    temp = -temp;		// temp is abs value of input 
	    temp2--;
	  }

	  // Find the number of bits needed for the magnitude of the coefficient 
	  nbits = 0;
	  while (temp != 0) {
	    nbits++;
	    temp >>= 1;
	  }

	  // write the Huffman-coded symbol for the number of bits 
	  write_bits(cinfo, dctbl.ehufco[nbits], dctbl.ehufsi[nbits]);

	  // write that number of bits of the value, if positive, 
	  // or the complement of its magnitude, if negative. 
	  if (nbits != 0)			// write_bits rejects calls with size 0 
	    write_bits(cinfo, (short) temp2, nbits);

	  // Encode the AC coefficients per section F.1.2.2 

	  r = 0;			// r = run length of zeros 

	  for (k = 1; k < DCTSIZE*DCTSIZE; k++) {
	    if ((temp = block[k]) == 0) {
	      r++;
	    } else {
	      // if run length > 15, must write special run-length-16 codes (0xF0) 
	      while (r > 15) {
		write_bits(cinfo, actbl.ehufco[0xF0], actbl.ehufsi[0xF0]);
		r -= 16;
	      }

	      temp2 = temp;
	      if (temp < 0) {
		temp = -temp;		// temp is abs value of input 
		// This code assumes we are on a two's complement machine 
		temp2--;
	      }

	      // Find the number of bits needed for the magnitude of the coefficient 
	      nbits = 1;		// there must be at least one 1 bit 
	      while ((temp >>= 1) != 0)
					nbits++;

	      // write Huffman symbol for run length / number of bits 
	      i = (r << 4) + nbits;
	      write_bits(cinfo, actbl.ehufco[i], actbl.ehufsi[i]);

	      // write that number of bits of the value, if positive, 
	      // or the complement of its magnitude, if negative. 
	      write_bits(cinfo, (short) temp2, nbits);

	      r = 0;
	    }
	  }

	  // If the last coef(s) were zero, write an end-of-block code 
	  if (r > 0)
	    write_bits(cinfo, actbl.ehufco[0], actbl.ehufsi[0]);
	}



	void
	huff_init (CompressInfo cinfo)
	{
	  short ci;
	  JpegComponentInfo comp;

	  // Initialize static variables 
	  huff_put_buffer = 0;
	  huff_put_bits = 0;

	  for (ci = 0; ci < cinfo.comps_in_scan; ci++) {
	    comp = cinfo.cur_comp_info[ci];
	    // Make sure requested tables are present 
	    if (cinfo.dc_huff_tbl[comp.dc_tbl_no] == null ||
					cinfo.ac_huff_tbl[comp.ac_tbl_no] == null)
	      util.errexit("Use of undefined Huffman table");
	    // Compute derived values for Huffman tables 
	    // We may do this more than once for same table, but it's not a big deal 
	    init_huff_tbl(cinfo.dc_huff_tbl[comp.dc_tbl_no]);
	    init_huff_tbl(cinfo.ac_huff_tbl[comp.ac_tbl_no]);
	    // Initialize DC predictions to 0 
	    cinfo.last_dc_val[ci] = 0;
	  }
	}

	/*
	 * Encode and output one MCU's worth of Huffman-compressed coefficients.
	 */

	 void
	huff_encode (CompressInfo cinfo, int [][] MCU_data)
	{
	  short blkn, ci;
	  JpegComponentInfo comp;
	  int temp;

	  for (blkn = 0; blkn < cinfo.blocks_in_MCU; blkn++) {
	    ci = cinfo.MCU_membership[blkn];
	    comp = cinfo.cur_comp_info[ci];
	    // Convert DC value to difference, update last_dc_val 
	    temp = MCU_data[blkn][0];
	    MCU_data[blkn][0] -= cinfo.last_dc_val[ci];
	    cinfo.last_dc_val[ci] = temp;
	    encode_one_block(cinfo, MCU_data[blkn],
			     cinfo.dc_huff_tbl[comp.dc_tbl_no],
			     cinfo.ac_huff_tbl[comp.ac_tbl_no]);
	  }
	}


	/*
	 * Finish up at the end of a Huffman-compressed scan.
	 */

	public  void
	huff_term (CompressInfo cinfo)
	{
	  // Flush out the last data 
	  flush_bits(cinfo);
	  flush_bytes(cinfo);
	}
}



