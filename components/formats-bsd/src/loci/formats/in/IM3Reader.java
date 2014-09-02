/*#%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2013 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.in;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import ome.xml.model.enums.DimensionOrder;
import loci.common.IRandomAccess;
import loci.common.Location;
import loci.common.NIOFileHandle;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;

/**
 * @author Lee Kamentsky
 * 
 * This is a FormatReader for the Perkin-Elmer Nuance
 * line of multispectral imaging microscopes' .im3
 * file format. The .im3 format stores 63 planes
 * representing intensity measurements taken on
 * separate spectral bands. The pixel intensity at
 * a single band might be used as a proxy for
 * fluorophore presence, but the best results are
 * obtained if the whole spectrum is used to
 * synthesize an intensity. The most efficient
 * strategy is to use IM3Reader.openRaw() to
 * fetch the entire image which may then be scanned
 * sequentially to create one planar image
 * per desired signal.
 * 
 * IM3Reader may be run as a Java application to
 * dump a per-record description of the file to
 * standard output
 *
 */
public class IM3Reader extends FormatReader {
	/**
	 * First 4 bytes of file is "1985"
	 */
	private static final int COOKIE = 1985;
	/**
	 * A record that is a container of
	 * other records.
	 */
	private static final int REC_CONTAINER = 0;
	/**
	 * Image format
	 * int: pixel type? 3 = RGBA
	 * int: width
	 * int: height
	 * int: depth
	 * data bytes follow
	 */
	private static final int REC_IMAGE = 1;
	/**
	 * A nested record of records 
	 */
	private static final int REC_NESTED = 3;
	/**
	 * 32-bit integer values 
	 */
	private static final int REC_INT = 6;
	/*
	 * Numerator/denominator ratio
	 * long numerator
	 * long denominator
	 */
	private static final int REC_RATIO = 7;
	private static final int REC_9 = 9;
	/**
	 * A string
	 * int: ?
	 * int: length
	 * 8-byte characters follow
	 */
	private static final int REC_STRING=10;
	/*
	 * Byte fields.
	 */
	private static final String FIELD_DATA_SET="DataSet";
	@SuppressWarnings("unused")
	private static final String FIELD_TIMESTAMP="TimeStamp";
	@SuppressWarnings("unused")
	private static final String FIELD_AUX_FLAGS="AuxFlags";
	@SuppressWarnings("unused")
	private static final String FIELD_NUANCE_FLAGS="NuanceFlags";
	@SuppressWarnings("unused")
	private static final String FIELD_SPECTRA="Spectra";
	@SuppressWarnings("unused")
	private static final String FIELD_PROTOCOL="Protocol";
	@SuppressWarnings("unused")
	private static final String FIELD_OBJECTIVE="Objective";
	@SuppressWarnings("unused")
	private static final String FIELD_SPECTRAL_BASIS_INFO="SpectralBasisInfo";
	@SuppressWarnings("unused")
	private static final String FIELD_FILTER_PAIR="FilterPair";
	@SuppressWarnings("unused")
	private static final String FIELD_FIXED_FILTER="FixedFilter";
	@SuppressWarnings("unused")
	private static final String FIELD_BANDS="Bands";
	/*
	 * Image fields
	 */
	@SuppressWarnings("unused")
	private static final String FIELD_THUMBNAIL="Thumbnail";
	private static final String FIELD_DATA="Data";
	/*
	 * Int fields
	 */
	@SuppressWarnings("unused")
	private static final String FIELD_FILE_VERSION="FileVersion";
	@SuppressWarnings("unused")
	private static final String FIELD_CLASS_ID="ClassID";
	@SuppressWarnings("unused")
	private static final String FIELD_TYPE_ID="TypeID";
	private static final String FIELD_SHAPE="Shape";
	@SuppressWarnings("unused")
	private static final String FIELD_BAND_INDEX="BandIndex";
	/*
	 * String fields
	 */
	@SuppressWarnings("unused")
	private static final String FIELD_NAME="Name";
	@SuppressWarnings("unused")
	private static final String FIELD_SAMPLE_ID="SampleID";
	@SuppressWarnings("unused")
	private static final String FIELD_USER_COMMENTS="UserComments";
	@SuppressWarnings("unused")
	private static final String FIELD_SOURCE_FILE_NAME="SourceFileName";
	@SuppressWarnings("unused")
	private static final String FIELD_PROXY_PARENT_FILE_NAME="ProxyParentFileName";
	@SuppressWarnings("unused")
	private static final String FIELD_MANUFACTURER="Manufacturer";
	@SuppressWarnings("unused")
	private static final String FIELD_PART_NUMBER="PartNumber";
	/*
	 * Ratio fields
	 */
	@SuppressWarnings("unused")
	private static final String FIELD_MILLIMETERS_PER_PIXEL="MillimetersPerPixel";
	@SuppressWarnings("unused")
	private static final String FIELD_EXPOSURE="Exposure";
	/*
	 * Misc fields
	 */
	@SuppressWarnings("unused")
	private static final String FIELD_HOMOGENEOUS="Homogeneous";
	/*
	 * Records for the current file
	 */
	private List<IM3Record> records;
	/*
	 * Data sets for the current file
	 */
	private List<ContainerRecord> dataSets;
	
	/*
	 * The data from the current series' file.
	 */
	private byte [] data;
	
	/**
	 * Construct an uninitialized reader of .im3 files.
	 */
	public IM3Reader() {
		super("Perkin-Elmer Nuance IM3", "im3");
	}

	@Override
	public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
			throws FormatException, IOException {
		if (data == null) {
			data = openRaw();
		}
		if (data == null) return null;
		
		final int srcWidth = getSizeX();
		final int srcChannels = getSizeC();
		if (buf == null) buf = new byte[w * h * 2];
		int idx = 0;
		int offset = ((x + y * srcWidth) * srcChannels + no) * 2;
		for (int hidx=0; hidx < h; hidx++) {
			int roffset = offset + hidx * srcWidth * srcChannels * 2;
			for (int widx=0; widx < w; widx++) {
				buf[idx++] = data[roffset];
				buf[idx++] = data[roffset+1];
				roffset += srcChannels * 2;
			}
		}
		return buf;
	}
	
	/**
	 * Open the current series in raw-mode, returning the
	 * interleaved image bytes. The data format for a pixel is
	 * a run of 63 unsigned short little endian values.
	 * 
	 * @return a byte array containing the data organized by
	 *         spectral channel, then x, then y. Returns null
	 *         if, for some incomprehensible reason, the DATA
	 *         block was missing.
	 * @throws IOException 
	 */
	public byte [] openRaw() throws IOException {
		IRandomAccess is = Location.getHandle(getCurrentFile(), false);
		is.setOrder(ByteOrder.LITTLE_ENDIAN);
		final ContainerRecord dataSet = dataSets.get(getSeries());
		for (IM3Record subRec:dataSet.parseChunks(is)){
			if (subRec.name.equals(FIELD_DATA)) {
				is.seek(subRec.offset+4);
				int width = is.readInt();
				int height = is.readInt();
				int channels = is.readInt();
				IRandomAccess iHandle = Location.getHandle(getCurrentFile(), false);
				if (iHandle instanceof NIOFileHandle) {
					NIOFileHandle handle = (NIOFileHandle)iHandle;
					MappedByteBuffer mapping = handle.getFileChannel().map(MapMode.READ_ONLY, is.getFilePointer(), width * height * channels * 2);
					mapping.load();
					if (mapping.hasArray()) {
						return mapping.array();
					}
				}
				final byte [] result = new byte [width * height * channels * 2];
				is.read(result);
				return result;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see loci.formats.FormatReader#initFile(java.lang.String)
	 */
	@Override
	protected void initFile(String id) throws FormatException, IOException {
		super.initFile(id);
		IRandomAccess is = Location.getHandle(id, false);
		is.setOrder(ByteOrder.LITTLE_ENDIAN);
		final int cookie = is.readInt();
		if (cookie != COOKIE) {
			throw new FormatException(String.format("Expected file cookie of %d, but got %d.", COOKIE, cookie));
		}
		long fileLength = is.length();
		records = new ArrayList<IM3Record>();
		dataSets = new ArrayList<ContainerRecord>();
		core = new ArrayList<CoreMetadata>();

		while (is.getFilePointer() < fileLength) {
			final IM3Record rec = parseRecord(is);
			if (rec == null) {
				if (is.getFilePointer() > fileLength-16) break;
				/*
				 * # of bytes in chunk.
				 */
				@SuppressWarnings("unused")
				final int chunkLength = is.readInt();
				/*
				 * Is always zero? Chunk #?
				 */
				@SuppressWarnings("unused")
				final int unknown = is.readInt();
				/*
				 * Is always one? Chunk #?
				 */
				@SuppressWarnings("unused")
				final int unknown1 = is.readInt();
				/*
				 * # of records to follow
				 */
				@SuppressWarnings("unused")
				final int nRecords = is.readInt();
			} else {
				if (rec instanceof ContainerRecord) {
					final ContainerRecord bRec = (ContainerRecord)rec;
					for (IM3Record subDS:bRec.parseChunks(is)) {
						if ((subDS instanceof ContainerRecord) && (subDS.name.equals(FIELD_DATA_SET))) {
							final ContainerRecord bSubDS = (ContainerRecord)subDS;
							for (IM3Record subSubDS:bSubDS.parseChunks(is)) {
								if (subSubDS instanceof ContainerRecord) {
									final ContainerRecord bDataSet = (ContainerRecord) subSubDS;
									dataSets.add(bDataSet);
									List<IM3Record> subRecs = bDataSet.parseChunks(is);
									final CoreMetadata cm = new CoreMetadata();
									cm.dimensionOrder = DimensionOrder.XYCZT.getValue();
									cm.littleEndian = true;
									// TODO: Detect pixel type
									cm.pixelType = FormatTools.UINT16;
									for (IM3Record subRec:subRecs){
										if (subRec.name.equals(FIELD_SHAPE) && (subRec instanceof IntIM3Record)) {
											final IntIM3Record iRec = (IntIM3Record)subRec;
											cm.sizeX = iRec.getEntry(is, 2);
											cm.sizeY = iRec.getEntry(is, 3);
											cm.sizeC = iRec.getEntry(is, 4);
											cm.sizeZ = 1;
											cm.sizeT = 1;
											cm.imageCount = cm.sizeC;
										}
									}
									core.add(cm);
								}
							}
						}
					}
				}
				records.add(rec);
			}
		}
	}
	static private final String EMPTY_STRING = new String();
	/**
	 * Parse a string from the IM3 file at the current file pointer loc
	 * 
	 * @param is stream to read from
	 * @return parsed string or null for string of zero length
	 * @throws IOException
	 */
	static protected String parseString(IRandomAccess is) throws IOException {
		final int nameLength = is.readInt();
		if (nameLength == 0) return EMPTY_STRING;
		final byte [] buf = new byte [nameLength];
		is.read(buf);
		return new String(buf, Charset.forName("UTF-8"));
	}
	
	/**
	 * Parse an IM3 record at the current file pointer location
	 * 
	 * @param is random access stream, pointing at the record's start
	 *        (the length-quadword of the record's tag name)
	 * @return an IM3Record or subclass depending on the record's type
	 * @throws IOException on file misparsing leading to overrun and other
	 */
	private IM3Record parseRecord(IRandomAccess is) throws IOException {
		final String name = parseString(is);
		if (name == null) return null;
		final int recLength = is.readInt()-8;
		final int recType = is.readInt();
		final long offset = is.getFilePointer();
		is.skipBytes(recLength);
		if (recType == REC_CONTAINER) {
			return new ContainerRecord(name, recType, offset, recLength);
		}
		if (recType == REC_STRING) {
			return new StringIM3Record(name, recType, offset, recLength);
		}
		if (recType == REC_INT) {
			return new IntIM3Record(name, recType, offset, recLength);
		}
		return new IM3Record(name, recType, offset, recLength);
	}
	/* (non-Javadoc)
	 * @see loci.formats.FormatReader#isThisType(loci.common.RandomAccessInputStream)
	 */
	@Override
	public boolean isThisType(RandomAccessInputStream stream)
			throws IOException {
		stream.seek(0);
		return (stream.readInt() == COOKIE);
	}
	protected class IM3Record {
		final String name;
		final int type;
		final long offset;
		final int length;
		IM3Record(String name, int type, long offset, int length) {
			this.name = name;
			this.type = type;
			this.offset=offset;
			this.length=length;
		}
		/**
		 * Write a summary of the contents of the record
		 * 
		 * @param is
		 * @throws IOException 
		 */
		public void writeSummary(Writer writer, IRandomAccess is, String indentation) throws IOException {
			is.seek(offset);
			writer.write(indentation + toString() + "\n");
			for (int i=0; (i<length) && (i < 256); i+= 32) {
				writer.write(indentation + String.format("%02x:", i));
				for (int j=i;(j < length) &&(j < i+32); j++) {
					writer.write(String.format(" %02x", is.readByte()));
				}
				writer.write("\n");
			}
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return String.format("[%s: type=%d, offset=%d, length=%d]", name, type, offset, length);
		}
		
	}
	/**
	 * @author leek
	 *
	 * A Container3Record is a nesting container for
	 * other records. In the IM3 format, records are often grouped
	 * under a ContainerRecord with a blank tagname.
	 */
	protected class ContainerRecord extends IM3Record {

		ContainerRecord(String name, int type, long offset, int length) {
			super(name, type, offset, length);
		}
		/**
		 * Parse and return the sub-records for the record container
		 * 
		 * @param is
		 * @return
		 * @throws IOException
		 */
		@SuppressWarnings("unused")
		List<IM3Record> parseChunks(IRandomAccess is) throws IOException {
			long oldOffset = is.getFilePointer();
			is.seek(offset+8);
			long end = offset+length;
			List<IM3Record> recs = new ArrayList<IM3Record>();
			while(is.getFilePointer() < end-8) {
				final IM3Record rec = parseRecord(is);
				if (rec != null)
					recs.add(rec);
			}
			is.seek(oldOffset);
			return recs;
		}
		
		/* (non-Javadoc)
		 * @see loci.formats.in.IM3Reader.IM3Record#writeSummary(java.io.Writer, loci.common.IRandomAccess, java.lang.String)
		 */
		public void writeSummary(Writer writer, IRandomAccess is, String indentation) throws IOException {
			is.seek(offset);
			writer.write(indentation + toString() + "\n");
			for (IM3Record rec:parseChunks(is)) {
				rec.writeSummary(writer, is, indentation + "  ");
			}
		}
	}
	/**
	 * @author Lee Kamentsky
	 *
	 * An integer array record
	 */
	protected class IntIM3Record extends IM3Record {

		public IntIM3Record(String name, int recType, long offset, int recLength) {
			super(name, recType, offset, recLength);
		}
		
		/**
		 * Get the # of integer values in this record
		 * 
		 * @return number of integer values contained in the record
		 */
		public int getNumEntries() {
			return this.length / 4;
		}
		
		/**
		 * Get the integer value at the given index
		 * 
		 * @param is the stream for the IM3 file
		 * @param index the zero-based index of the entry to retrieve
		 * @return the value stored in the indexed slot of the record
		 * @throws IOException
		 */
		public int getEntry(IRandomAccess is, int index) throws IOException {
			long oldPos = is.getFilePointer();
			try {
				is.seek(offset+index*4);
				return is.readInt();
			} finally {
				is.seek(oldPos);
			}
		}
		/* (non-Javadoc)
		 * @see loci.formats.in.IM3Reader.IM3Record#writeSummary(java.io.Writer, loci.common.IRandomAccess, java.lang.String)
		 */
		public void writeSummary(Writer writer, IRandomAccess is, String indentation) throws IOException {
			is.seek(offset);
			writer.write(indentation + toString() + "\n");
			final int length = getNumEntries();
			for (int i=0; (i < length) && (i < 256); i+=16) {
				writer.write(indentation + String.format("%02x:", i));
				for (int j=i; (j<i+16) && (j<length); j++) {
					writer.write(String.format(" %7d", getEntry(is, j)));
				}
				writer.write("\n");
			}
		}
	}
	/**
	 * @author Lee Kamentsky
	 *
	 * A record whose value is a string.
	 */
	protected class StringIM3Record extends IM3Record {
		public StringIM3Record(String name, int recType, long offset, int recLength) {
			super(name, recType, offset, recLength);
		}
		
		/**
		 * Return the string value for this record
		 * 
		 * @param is an open handle on the .IM3 file
		 * @return the string value stored in the record
		 * @throws IOException
		 */
		public String getValue(IRandomAccess is) throws IOException {
			final long oldPos = is.getFilePointer();
			try {
				is.seek(offset+4);
				return parseString(is);
			} finally {
				is.seek(oldPos);
			}
		}

		/* (non-Javadoc)
		 * @see loci.formats.in.IM3Reader.IM3Record#writeSummary(java.io.Writer, loci.common.IRandomAccess, java.lang.String)
		 */
		@Override
		public void writeSummary(Writer writer, IRandomAccess is,
				String indentation) throws IOException {
			writer.write(indentation + toString() + "\n");
			writer.write(indentation + String.format("Value = %s\n", getValue(is)));
		}
		
	}
	/* (non-Javadoc)
	 * @see loci.formats.FormatReader#setSeries(int)
	 */
	@Override
	public void setSeries(int no) {
		super.setSeries(no);
		data = null;
	}

	/* (non-Javadoc)
	 * @see loci.formats.FormatReader#setId(java.lang.String)
	 */
	@Override
	public void setId(String id) throws FormatException, IOException {
		super.setId(id);
		data = null;
	}
	
	/**
	 * Write a summary of each field in the IM3 file to the writer
	 * @param writer
	 * @throws IOException
	 */
	public void writeSummary(Writer writer) throws IOException {
		IRandomAccess is = Location.getHandle(getCurrentFile(), false);
		is.setOrder(ByteOrder.LITTLE_ENDIAN);
		for (IM3Record rec: records) {
			rec.writeSummary(writer, is, "");
		}
	}
	/**
	 * Write a summary of each record to STDOUT
	 * 
	 * @param args
	 */
	static public void main(String [] args){
		final IM3Reader reader = new IM3Reader();
		try {
			reader.setId(args[0]);
			reader.writeSummary(new OutputStreamWriter(System.out));
		} catch (FormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
