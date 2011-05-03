package com.sun.jimi.core.encoder.jpg;

import java.io.*;

public class Write{


	// Write a single byte 
	static void write_byte(CompressInfo cinfo, int b){
		try{
			cinfo.output_file.writeByte(b);
		}catch(IOException e){}
	}
	
	// Write some bytes from a buffer 
	static void write_bytes(CompressInfo cinfo, byte [] data, int count){
		try{
			cinfo.output_file.write(data, 0, count);
		}catch(IOException e){}
	}
	
		
	static final short NUM_QUANT_TBLS=4;
	static final short DCTSIZE=8;
	static final short NUM_ARITH_TBLS=4;
	

	// JPEG marker codes 
	public static final byte	  
	  SOF0  = (byte)0xc0,
	  SOF1  = (byte)0xc1,
	  SOF2  = (byte)0xc2,
	  SOF3  = (byte)0xc3,
	  
	  SOF5  = (byte)0xc5,
	  SOF6  = (byte)0xc6,
	  SOF7  = (byte)0xc7,
	  
	  JPG   = (byte)0xc8,
	  SOF9  = (byte)0xc9,
	  SOF10 = (byte)0xca,
	  SOF11 = (byte)0xcb,
	  
	  SOF13 = (byte)0xcd,
	  SOF14 = (byte)0xce,
	  SOF15 = (byte)0xcf,
	  
	  DHT   = (byte)0xc4,
	  
	  DAC   = (byte)0xcc,
	  
	  RST0  = (byte)0xd0,
	  RST1  = (byte)0xd1,
	  RST2  = (byte)0xd2,
	  RST3  = (byte)0xd3,
	  RST4  = (byte)0xd4,
	  RST5  = (byte)0xd5,
	  RST6  = (byte)0xd6,
	  RST7  = (byte)0xd7,
	  
	  SOI   = (byte)0xd8,
	  EOI   = (byte)0xd9,
	  SOS   = (byte)0xda,
	  DQT   = (byte)0xdb,
	  DNL   = (byte)0xdc,
	  DRI   = (byte)0xdd,
	  DHP   = (byte)0xde,
	  EXP   = (byte)0xdf,
	  
	  APP0  = (byte)0xe0,
	  APP15 = (byte)0xef,
	  
	  JPG0  = (byte)0xf0,
	  JPG13 = (byte)0xfd,
	  COM   = (byte)0xfe,
	  
	  TEM   = (byte)0x01,
	  
	  ERROR = (byte)0x100;


	static void
	write_marker (CompressInfo cinfo, byte mark)
	// write a marker code 
	{
	  write_byte(cinfo, 0xFF);
	  write_byte(cinfo, mark);
	}


	static void
	write_2bytes (CompressInfo cinfo, int value)
	// write a 2-byte integer; these are always MSB first in JPEG files 
	{
	  write_byte(cinfo, (value >> 8) & 0xFF);
	  write_byte(cinfo, value & 0xFF);
	}


	static void
	write_dqt (CompressInfo cinfo, int index)
	// write a DQT marker 
	// Returns the precision used (0 = 8bits, 1 = 16bits) for baseline checking 
	{
	  int i;
	  
	  write_marker(cinfo, DQT);
	  
	  // table size
	  write_2bytes(cinfo, DCTSIZE*DCTSIZE + 1 + 2);
	  
	  // precision and table id, precision always 0 (8bit)
	  write_byte(cinfo, index);
	  
	  for (i = 0; i < DCTSIZE*DCTSIZE; i++) {
	    write_byte(cinfo, cinfo.quant_tbl[index][i]);
	  }
	}


	static void
	write_dht (CompressInfo cinfo, int index, boolean is_ac)
	// write a DHT marker 
	{
	  HuffTbl htbl;
	  int length, i;
	  
	  if (is_ac) {
	    htbl = cinfo.ac_huff_tbl[index];
	    index += 0x10;		// output index has AC bit set 
	  } else {
	    htbl = cinfo.dc_huff_tbl[index];
	  }

	  if (htbl == null)
	    util.errexit("Huffman table " + index +" was not defined");
	  
	  if (! htbl.sent_table) {
	    write_marker(cinfo, DHT);
	    
	    length = 0;
	    for (i = 1; i <= 16; i++)
	      length += htbl.bits[i];
	    
	    write_2bytes(cinfo, length + 2 + 1 + 16);
	    write_byte(cinfo, index);
	    
	    for (i = 1; i <= 16; i++)
	      write_byte(cinfo,  htbl.bits[i]);
	    
	    for (i = 0; i < length; i++)
	      write_byte(cinfo,  htbl.huffval[i]);
	    
	    htbl.sent_table = true;
	  }
	}


	static void
	write_sof (CompressInfo cinfo, byte code)
	// write a SOF marker 
	{
	  int i;
	  
	  write_marker(cinfo, code);
	  
	  write_2bytes(cinfo, 3 * cinfo.num_components + 2 + 5 + 1); // length 

	  write_byte(cinfo, cinfo.data_precision);
	  write_2bytes(cinfo, (int) cinfo.image_height);
	  write_2bytes(cinfo, (int) cinfo.image_width);

	  write_byte(cinfo, cinfo.num_components);

	  for (i = 0; i < cinfo.num_components; i++) {
	    write_byte(cinfo, cinfo.comp_info[i].component_id);
	    write_byte(cinfo, (cinfo.comp_info[i].h_samp_factor << 4)
			     + cinfo.comp_info[i].v_samp_factor);
	    write_byte(cinfo, cinfo.comp_info[i].quant_tbl_no);
	  }
	}


	static void
	write_sos (CompressInfo cinfo)
	// write a SOS marker 
	{
	  int i;
	  
	  write_marker(cinfo, SOS);
	  
	  write_2bytes(cinfo, 2 * cinfo.comps_in_scan + 2 + 1 + 3); // length 
	  
	  write_byte(cinfo, cinfo.comps_in_scan);
	  
	  for (i = 0; i < cinfo.comps_in_scan; i++) {
	    write_byte(cinfo, cinfo.cur_comp_info[i].component_id);
	    write_byte(cinfo, (cinfo.cur_comp_info[i].dc_tbl_no << 4)
			     + cinfo.cur_comp_info[i].ac_tbl_no);
	  }

	  write_byte(cinfo, 0);		// Spectral selection start 
	  write_byte(cinfo, DCTSIZE*DCTSIZE-1);	// Spectral selection end 
	  write_byte(cinfo, 0);		// Successive approximation 
	}



	// Write the file header.
	 
	public static void
	write_file_header (CompressInfo cinfo)
	{
	  short qt_in_use[] = new short[NUM_QUANT_TBLS];
	  int i, prec;
	  boolean is_baseline;
	  
	  write_marker(cinfo, SOI);	// first the SOI 

	  // write DQT for each quantization table. 

	  for (i = 0; i < NUM_QUANT_TBLS; i++)
	    qt_in_use[i] = 0;

	  for (i = 0; i < cinfo.num_components; i++)
	    qt_in_use[cinfo.comp_info[i].quant_tbl_no] = 1;

	  prec = 0;
	  for (i = 0; i < NUM_QUANT_TBLS; i++) {
	    if (qt_in_use[i] != 0)
	      write_dqt(cinfo, i);
	  }

	  is_baseline = true;


	  // write the proper SOF marker 
	  if (is_baseline)
	    write_sof(cinfo, SOF0);	// SOF code for baseline implementation 
	  else
	    write_sof(cinfo, SOF1);	// SOF code for non-baseline Huffman file 
	}


	/*
	 * Write the start of a scan (everything through the SOS marker).
	 */

	public static void
	write_scan_header (CompressInfo cinfo)
	{
	  int i;
	  
	  /* write Huffman tables.  Note that write_dht takes care of
	   * suppressing duplicate tables.
	   */
	  for (i = 0; i < cinfo.comps_in_scan; i++) {
	    write_dht(cinfo, cinfo.cur_comp_info[i].dc_tbl_no, false);
	    write_dht(cinfo, cinfo.cur_comp_info[i].ac_tbl_no, true);
	  }

	  write_sos(cinfo);
	}


	/*
	 * Write some bytes of compressed data within a scan.
	 */

	public static void
	write_jpeg_data (CompressInfo cinfo, byte [] data, int datacount)
	{
	  write_bytes(cinfo, data, datacount);
	}


	/*
	 * Finish up at the end of the file.
	 */

	public static void
	write_file_trailer (CompressInfo cinfo)
	{
	  write_marker(cinfo, EOI);
		try	{
			cinfo.output_file.flush();
		} catch (IOException e){}
	}

}
