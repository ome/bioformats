/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

package loci.formats.in;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;
import java.util.zip.DataFormatException;

import ome.xml.model.primitives.PositiveFloat;

import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

/**
 * OBFReader is the file format reader for Imspector OBF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/formats-bsd/src/loci/formats/in/OBFReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/formats-bsd/src/loci/formats/in/OBFReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Bjoern Thiel bjoern.thiel at mpibpc.mpg.de
 */

public class OBFReader extends FormatReader
{
	private static final boolean LITTLE_ENDIAN = true ;

	private static final String FILE_MAGIC_STRING = "OMAS_BF\n" ;
	private static final String STACK_MAGIC_STRING = "OMAS_BF_STACK\n" ;
	private static final short MAGIC_NUMBER = (short) 0xFFFF ;

	private static final int FILE_VERSION = 1 ;
	private static final int STACK_VERSION = 3 ;

	private static final int MAXIMAL_NUMBER_OF_DIMENSIONS = 15 ;

	private class Stack
	{
		long position ;
		long length ;
		boolean compression ;
	}
	private List<Stack> stacks = new ArrayList<Stack>() ;
	
	private class Frame
	{
		byte[] bytes ;
		int series ;
		int number ;
	}
	private Frame currentInflatedFrame = new Frame() ;
	
	private transient Inflater inflater = new Inflater() ;

	public OBFReader()
	{
		super("OBF", new String[] {"obf", "msr"}) ;
		suffixNecessary = false ;
		suffixSufficient = false ;
		datasetDescription = "OBF file" ;
	}

	private int getFileVersion(RandomAccessInputStream stream) throws IOException
	{
		stream.seek(0) ;

		stream.order(LITTLE_ENDIAN) ;

		try
		{
			final String magicString = stream.readString(FILE_MAGIC_STRING.length()) ;
			final short magicNumber = stream.readShort() ;
			final int version = stream.readInt() ;

			if (magicString.equals(FILE_MAGIC_STRING) && magicNumber == MAGIC_NUMBER)
			{
				return version ;
			}
		}
		catch(IOException exception) { }

		return - 1 ;
	}
	
	public boolean isThisType(RandomAccessInputStream stream) throws IOException
	{
		final int fileVersion = getFileVersion(stream) ;
		
		return fileVersion >= 0 && fileVersion <= FILE_VERSION ;
	}

	protected void initFile(String id) throws FormatException, IOException
	{
		super.initFile(id) ;
		
		currentInflatedFrame.series = - 1 ;
		currentInflatedFrame.number = - 1 ;
		
		in = new RandomAccessInputStream(id) ;
		
		final int fileVersion = getFileVersion(in) ;

		long stackPosition = in.readLong() ;

		final int lengthOfDescription = in.readInt() ;
		final String description = in.readString(lengthOfDescription) ;
		metadata.put("Description", description) ;

		if (stackPosition != 0)
		{
			core.clear() ;
			do
			{
				stackPosition = initStack(stackPosition, fileVersion) ;
			}
			while (stackPosition != 0) ;
		}

		MetadataStore ome = makeFilterMetadata() ;
		MetadataTools.populatePixels(ome, this) ;

		for (int series = 0 ; series != core.size() ; ++ series)
		{
			CoreMetadata obf = core.get(series) ;

			final String name = obf.seriesMetadata.get("Name").toString() ;
			ome.setImageName(name, series) ;

			@SuppressWarnings("unchecked")
			final List<Double> lengths = (List<Double>) obf.seriesMetadata.get("Lengths") ;

			if (lengths.size() > 0)
			{
				final double lengthX = Math.abs(lengths.get(0)) ;
				if (lengthX > 0)
				{
					final PositiveFloat physicalSizeX = new PositiveFloat( lengthX / obf.sizeX ) ;
					ome.setPixelsPhysicalSizeX(physicalSizeX, series) ;
				}
			}
			if (lengths.size() > 1)
			{
				final double lengthY = Math.abs(lengths.get(1)) ;
				if (lengthY > 0)
				{
					final PositiveFloat physicalSizeY = new PositiveFloat( lengthY / obf.sizeY ) ;
					ome.setPixelsPhysicalSizeY(physicalSizeY, series) ;
				}
			}
			if (lengths.size() > 2)
			{
				final double lengthZ = Math.abs(lengths.get(2)) ;
				if (lengthZ > 0)
				{
					final PositiveFloat physicalSizeZ = new PositiveFloat( lengthZ / obf.sizeZ ) ;
					ome.setPixelsPhysicalSizeZ(physicalSizeZ, series) ;
				}
			}
		}
	}

	private long initStack(long current, int fileVersion) throws FormatException, IOException
	{
		in.seek(current) ;

		final String magicString = in.readString(STACK_MAGIC_STRING.length()) ;
		final short magicNumber = in.readShort() ;
		final int version = in.readInt() ;

		if (magicString.equals(STACK_MAGIC_STRING) && magicNumber == MAGIC_NUMBER && version <= STACK_VERSION)
		{
			CoreMetadata obf = new CoreMetadata() ;
			core.add(obf) ;

			obf.littleEndian = LITTLE_ENDIAN ;

			obf.thumbnail = false ;

			final int numberOfDimensions = in.readInt() ;
			if (numberOfDimensions > 5)
			{
				throw new FormatException("Unsupported number of " + numberOfDimensions + " dimensions") ;
			}

			int[] sizes = new int[MAXIMAL_NUMBER_OF_DIMENSIONS] ;
			for (int dimension = 0 ; dimension != MAXIMAL_NUMBER_OF_DIMENSIONS ; ++ dimension)
			{
				final int size = in.readInt() ;
				sizes[dimension] = dimension < numberOfDimensions ? size : 1 ;
			}

			obf.sizeX = sizes[0] ;
			obf.sizeY = sizes[1] ;
			obf.sizeZ = sizes[2] ;
			obf.sizeC = sizes[3] ;
			obf.sizeT = sizes[4] ;

			obf.imageCount = sizes[2] * sizes[3] * sizes[4] ;
			obf.dimensionOrder = "XYZCT" ;
			obf.orderCertain = false ;

			List<Double> lengths = new ArrayList<Double>() ;
			for (int dimension = 0 ; dimension != MAXIMAL_NUMBER_OF_DIMENSIONS ; ++ dimension)
			{
				final double length = in.readDouble() ;
				if (dimension < numberOfDimensions)
				{
					lengths.add(new Double(length)) ;
				}
			}
			obf.seriesMetadata.put("Lengths", lengths) ;

			List<Double> offsets = new ArrayList<Double>() ;
			for (int dimension = 0 ; dimension != MAXIMAL_NUMBER_OF_DIMENSIONS ; ++ dimension)
			{
				final double offset = in.readDouble() ;
				if (dimension < numberOfDimensions)
				{
					offsets.add(new Double(offset)) ;
				}
			}
			obf.seriesMetadata.put("Offsets", offsets) ;

			final int type = in.readInt() ;
			obf.pixelType = getPixelType(type) ;
			obf.bitsPerPixel = getBitsPerPixel(type) ; 

			obf.indexed = false ;
			obf.rgb = false ;
			obf.interleaved = false ;

			Stack stack = new Stack() ;

			final int compression = in.readInt() ;
			stack.compression = getCompression(compression) ;

			in.skipBytes(4) ;

			final int lengthOfName = in.readInt() ;
			final int lengthOfDescription = in.readInt() ;

			in.skipBytes(8) ;

			final long lengthOfData = in.readLong() ;
			stack.length = getLength(lengthOfData) ;

			final long next = in.readLong() ;

			final String name = in.readString(lengthOfName) ;
			obf.seriesMetadata.put("Name", name) ;
			final String description = in.readString(lengthOfDescription) ;
			obf.seriesMetadata.put("Description", description) ;

			stack.position = in.getFilePointer() ;

			stacks.add(stack) ;

			if (fileVersion >= 1)
			{
				in.skip(lengthOfData) ;

				final long footer = in.getFilePointer() ;
				final int offset = in.readInt() ;

				List<Boolean> stepsPresent = new ArrayList<Boolean>() ;
				for (int dimension = 0 ; dimension != MAXIMAL_NUMBER_OF_DIMENSIONS ; ++ dimension)
				{
					final int present = in.readInt() ;
					if (dimension < numberOfDimensions)
					{
						stepsPresent.add(new Boolean(present != 0)) ;
					}
				}
				List<Boolean> stepLabelsPresent = new ArrayList<Boolean>() ;
				for (int dimension = 0 ; dimension != MAXIMAL_NUMBER_OF_DIMENSIONS ; ++ dimension)
				{
					final int present = in.readInt() ;
					if (dimension < numberOfDimensions)
					{
						stepLabelsPresent.add(new Boolean(present != 0)) ;
					}
				}

				in.seek(footer + offset) ;

				List<String> labels = new ArrayList<String>() ;
				for (int dimension = 0 ; dimension != numberOfDimensions ; ++ dimension)
				{
					final int length = in.readInt() ;
					final String label = in.readString(length) ;
					labels.add(label) ;
				}
				obf.seriesMetadata.put("Labels", labels) ;

				List<List<Double>> steps = new ArrayList<List<Double>>() ;
				for (int dimension = 0 ; dimension != numberOfDimensions ; ++ dimension)
				{
					List<Double> list = new ArrayList<Double>() ;
					if (stepsPresent.get(dimension))
					{
						for (int position = 0 ; position != sizes[dimension] ; ++ position)
						{
							final double step = in.readDouble() ;
							list.add(new Double(step)) ;
						}
					}
					steps.add(list) ;
				}
				obf.seriesMetadata.put("Steps", steps) ;

				List<List<String>> stepLabels = new ArrayList<List<String>>() ;
				for (int dimension = 0 ; dimension != numberOfDimensions ; ++ dimension)
				{
					List<String> list = new ArrayList<String>() ;
					if (stepLabelsPresent.get(dimension))
					{
						for (int position = 0 ; position != sizes[dimension] ; ++ position)
						{
							final int length = in.readInt() ;
							final String label = in.readString(length) ;
							list.add(label) ;
						}
					}
					stepLabels.add(list) ;
				}
				obf.seriesMetadata.put("StepLabels", stepLabels) ;
			}
			return next ;
		}
		else
		{
			throw new FormatException("Unsupported stack format") ;
		}
	}

	private int getPixelType(int type) throws FormatException
	{
		switch (type) 
		{
		case 0x01: return FormatTools.UINT8 ;
		case 0x02: return FormatTools.INT8 ;
		case 0x04: return FormatTools.UINT16 ;
		case 0x08: return FormatTools.INT16 ;
		case 0x10: return FormatTools.UINT32 ;
		case 0x20: return FormatTools.INT32 ;
		case 0x40: return FormatTools.FLOAT ;
		case 0x80: return FormatTools.DOUBLE ;
		default: throw new FormatException("Unsupported data type " + type) ;
		}
	}

	private int getBitsPerPixel(int type) throws FormatException
	{
		switch (type) 
		{
		case 0x01:
		case 0x02: return 8 ;
		case 0x04:
		case 0x08: return 16 ;
		case 0x10:
		case 0x20: return 32 ;
		case 0x40: return 32 ;
		case 0x80: return 64 ;
		default: throw new FormatException("Unsupported data type " + type) ;
		}
	}

	private long getLength(long length) throws FormatException
	{
		if (length >= 0)
		{
			return length ;
		}
		else
		{
			throw new FormatException("Negative stack length on disk") ;
		}
	}

	private boolean getCompression(int compression) throws FormatException
	{
		switch (compression)
		{
		case 0: return false ;
		case 1: return true ;
		default: throw new FormatException("Unsupported compression " + compression) ;
		}
	}

	@Override
	public byte[] openBytes(int no, byte[] buffer, int x, int y, int w, int h)
		throws FormatException, IOException 
	{
		FormatTools.checkPlaneParameters(this, no, buffer.length, x, y, w, h) ;

		final int rows = getSizeY() ;
		final int columns = getSizeX() ;
		final int bytesPerPixel = getBitsPerPixel() / 8 ;
		
		final int series = getSeries() ;
		final Stack stack = stacks.get(series) ;
		if (stack.compression)
		{
			if (series != currentInflatedFrame.series)
			{
				currentInflatedFrame.bytes = new byte[rows * columns * bytesPerPixel] ;
				currentInflatedFrame.series = series ;
				currentInflatedFrame.number = - 1 ;
			}

			byte[] bytes = currentInflatedFrame.bytes ;
			if (no != currentInflatedFrame.number)
			{
				if (no < currentInflatedFrame.number)
				{
					currentInflatedFrame.number = - 1 ;
				}
				if (currentInflatedFrame.number == - 1)
				{
					in.seek(stack.position) ;
					inflater.reset() ;
				}
				
				byte[] input = new byte[8192] ;
				while (no != currentInflatedFrame.number)
				{
					int offset = 0 ;
					while (offset != bytes.length)
					{
						if (inflater.needsInput())
						{
							final long remainder = stack.position + stack.length - in.getFilePointer() ;
							if (remainder > 0)
							{
								final int length = remainder > input.length ? input.length : (int) remainder ;

								in.read(input, 0, length) ;
								inflater.setInput(input, 0, length) ;
							}
							else
							{
								throw new FormatException("Corrupted zlib compression") ;
							}
						}
						else if (inflater.needsDictionary())
						{
							throw new FormatException("Unsupported zlib compression") ;
						}
						try
						{
							offset += inflater.inflate(bytes, offset, bytes.length - offset) ;
						}
						catch (DataFormatException exception)
						{
							throw new FormatException(exception.getMessage()) ;
						}
					}
					++ currentInflatedFrame.number ;
				}
			}
			for (int row = 0 ; row != h ; ++ row)
			{
				System.arraycopy(bytes, ((row + y) * columns + x) * bytesPerPixel, buffer, row * w * bytesPerPixel, w * bytesPerPixel) ;
			}
		}
		else
		{
			for (int row = 0 ; row != h ; ++ row)
			{
				in.seek(stack.position + ((no * rows + row + y) * columns + x) * bytesPerPixel) ;
				in.read(buffer, row * w * bytesPerPixel, w * bytesPerPixel) ;
			}
		}
		
		return buffer ;
	}

	public void close(boolean fileOnly) throws IOException
	{
		stacks = new ArrayList<Stack>() ;
		currentInflatedFrame = new Frame() ;
		inflater = new Inflater() ;
		
		super.close(fileOnly) ;
	}
}
