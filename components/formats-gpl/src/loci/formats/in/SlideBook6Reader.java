/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

import ome.xml.model.primitives.PositiveFloat;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.UNITS;

import loci.formats.MissingLibraryException;

/**
 * SlideBook6Reader is a file format reader for 3i SlideBook SLD files that uses
 * the SlideBook SBReadFile SDK via the (Windows only) dlls: 
 *   SBReadFile.dll and SlideBook6Reader.dll
 *
 * @author Richard Myers, richard at intelligent-imaging.com
 *
 */
public class SlideBook6Reader  extends FormatReader {

	// -- Constants --

	public static final int SLD_MAGIC_BYTES_1_0 = 0x006c;
	public static final int SLD_MAGIC_BYTES_1_1 = 0x0100;
	public static final int SLD_MAGIC_BYTES_1_2 = 0x0200;
	public static final int SLD_MAGIC_BYTES_2_0 = 0x01f5;
	public static final int SLD_MAGIC_BYTES_2_1 = 0x0102;

	public static final long SLD_MAGIC_BYTES_3 = 0xf6010101L;

	private static final String URL_3I_SLD =
			"http://www.openmicroscopy.org/site/support/bio-formats/formats/3i-slidebook6-sld.html";
	private static final String NO_3I_MSG = "3i SlideBook SBReadFile library not found. " +
			"Please see " + URL_3I_SLD + " for details.";
	private static final String GENERAL_3I_MSG = "3i SlideBook SBReadFile library problem. " +
			"Please see " + URL_3I_SLD + " for details.";

	// -- Static initializers --

	private static boolean libraryFound = false;

	static {
		String library_path = System.getProperty("java.library.path");

		try {
			// load JNI wrapper of SBReadFile.dll
			System.loadLibrary("SlideBook6Reader");
			libraryFound = true;
		}
		catch (UnsatisfiedLinkError e) {
			LOGGER.warn(NO_3I_MSG, e);
			libraryFound = false;
		}
		catch (SecurityException e) {
			LOGGER.warn("Insufficient permission to load native library", e);
			libraryFound = false;
		}
	}

	// -- Constructor --

	public SlideBook6Reader() {
		super("SlideBook 6 SLD (native)", new String[] {"sld"});
		domains = new String[] {FormatTools.LM_DOMAIN};
		suffixSufficient = false;
	}

	// -- IFormatReader API methods --

	/* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
	public boolean isThisType(RandomAccessInputStream stream) throws IOException {
		final int blockLen = 8;
		stream.seek(4);
		boolean littleEndian = stream.readString(2).equals("II");
		if (!FormatTools.validStream(stream, blockLen, littleEndian)) return false;
		int magicBytes1 = stream.readShort();
		int magicBytes2 = stream.readShort();

		boolean isMatch = ((magicBytes2 & 0xff00) == SLD_MAGIC_BYTES_1_1 ||
				(magicBytes2 & 0xff00) == SLD_MAGIC_BYTES_1_2) &&
				(magicBytes1 == SLD_MAGIC_BYTES_1_0 ||
				magicBytes1 == SLD_MAGIC_BYTES_2_0);

		return isMatch;
	}

	/**
	 * @see IFormatReader#isThisType(String, boolean)
	 */
	public boolean isThisType(String file, boolean open) {
		// Check the first few bytes of a file to determine if the file can be read by this reader.
		// You can assume that index 0 in the stream corresponds to the index 0 in the file.
		// Return true if the file can be read; false if not (or if there is no way of checking).

		return libraryFound && super.isThisType(file, open);
	}

	/**
	 * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
	 */
	public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
			throws FormatException, IOException
	{
		// Returns a byte array containing the pixel data for a subimage specified image from the given file.
		// The dimensions of the subimage (upper left X coordinate, upper left Y coordinate, width, and height) are
		// specified in the final four int parameters. This should throw a FormatException if the image number is
		// invalid (less than 0 or >= the number of images). The ordering of the array returned by openBytes should
		// correspond to the values returned by isLittleEndian() and isInterleaved(). Also, the length of the byte array
		// should be [image width * image height * bytes per pixel]. Extra bytes will generally be truncated. It is
		// recommended that the first line of this method be:
		//    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h) - this ensures that all of the parameters
		// are valid.

		FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

		int[] zct = FormatTools.getZCTCoords(this, no);
		int bpc = FormatTools.getBytesPerPixel(getPixelType());
		byte[] b = new byte[FormatTools.getPlaneSize(this)];

		readImagePlaneBuf(b, getSeries(), 0, zct[2], zct[0], zct[1]);

		int pixel = bpc * getRGBChannelCount();
		int rowLen = w * pixel;
		for (int row=0; row<h; row++) {
			System.arraycopy(b, pixel * ((row + y) * getSizeX() + x), buf,
					row * rowLen, rowLen);
		}

		if (isRGB()) {
			int bpp = getSizeC() * bpc;
			int line = w * bpp;
			for (int row=0; row<h; row++) {
				for (int col=0; col<w; col++) {
					int base = row * line + col * bpp;
					for (int bb=0; bb<bpc; bb++) {
						byte blue = buf[base + bpc*(getSizeC() - 1) + bb];
						buf[base + bpc*(getSizeC() - 1) + bb] = buf[base + bb];
						buf[base + bb] = blue;
					}
				}
			}
		}
		return buf;
	}

	// -- Internal FormatReader API methods --
	public void close(boolean fileOnly) throws IOException {
		// Cleans up any resources used by the reader. Global variables should be reset to their initial state, and any
		// open files or delegate readers should be closed.
		super.close(fileOnly);

		// Cleans up any resources used by the reader. Global variables should be reset to their initial state, and any
		// open files or delegate readers should be closed.
		closeFile();
	}

	/* @see loci.formats.FormatReader#initFile(String) */
	protected void initFile(String id) throws FormatException, IOException {
		super.initFile(id);

		// The majority of the file parsing logic should be placed in this method. The idea is to call this method
		// once (and only once!) when the file is first opened. Generally, you will want to start by calling
		// super.initFile(String). You will also need to set up the stream for reading the file, as well as initializing
		// any dimension information and metadata. Most of this logic is up to you; however, you should populate the 'core'
		// variable (see loci.formats.CoreMetadata).

		// Note that each variable is initialized to 0 or null when super.initFile(String) is called. Also,
		// super.initFile(String) constructs a Hashtable called "metadata" where you should store any relevant metadata.

		try {
			openFile(id);
			int numSeries = getNumCaptures();

			core.clear();
			for (int i=0; i<numSeries; i++) {
				CoreMetadata ms = new CoreMetadata();
				core.add(ms);
				ms.sizeX = getNumXColumns(i);
				if (ms.sizeX % 2 != 0) ms.sizeX++;
				ms.sizeY = getNumYRows(i);
				ms.sizeZ = getNumZPlanes(i);
				ms.sizeT = getNumTimepoints(i);
				ms.sizeC = getNumChannels(i);
				int bytes = getBytesPerPixel(i);
				if (bytes % 3 == 0) {
					ms.sizeC *= 3;
					bytes /= 3;
					ms.rgb = true;
				}
				else ms.rgb = false;

				ms.pixelType = FormatTools.pixelTypeFromBytes(bytes, false, true);
				ms.imageCount = ms.sizeZ * ms.sizeT;
				if (!ms.rgb) ms.imageCount *= ms.sizeC;
				ms.interleaved = true;
				ms.littleEndian = true;
				ms.dimensionOrder = "XYCZT";
				ms.indexed = false;
				ms.falseColor = false;
			}
		}
		catch (UnsatisfiedLinkError e) {
			throw new MissingLibraryException(GENERAL_3I_MSG, e);
		}
		catch (Exception e) {
			throw new MissingLibraryException(GENERAL_3I_MSG, e);
		}

		// fill in meta data
		MetadataStore store = makeFilterMetadata();
		MetadataTools.populatePixels(store, this);
		int numCaptures = getNumCaptures();
		if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
			for (int capture=0; capture<numCaptures; capture++) {
				String imageName = getImageName(capture);
				store.setImageName(imageName, capture);
				String imageDescription = getImageComments(capture);
				store.setImageDescription(imageDescription, capture);
			}
		}

		// link Instrument and Image
		String instrumentID = MetadataTools.createLSID("Instrument", 0);
		store.setInstrumentID(instrumentID, 0);
		for (int capture=0; capture < numCaptures; capture++) {
			store.setImageInstrumentRef(instrumentID, capture);
		}

		// set voxel size per image (microns)
		for (int capture=0; capture < numCaptures; capture++) {
			double voxelsize = getVoxelSize(capture);
			Length physicalSizeX = FormatTools.getPhysicalSizeX(voxelsize);
			Length physicalSizeY = FormatTools.getPhysicalSizeY(voxelsize);
			if (physicalSizeX != null) {
				store.setPixelsPhysicalSizeX(physicalSizeX, capture);
			}
			if (physicalSizeY != null) {
				store.setPixelsPhysicalSizeY(physicalSizeY, capture);
			}
			double stepSize = 0;
			if (getNumZPlanes(capture) > 1) {
				double plane0 = getZPosition(capture, 0, 0);
				double plane1 = getZPosition(capture, 0, getNumChannels(capture));
				// distance between plane 0 and 1 is step size, assume constant for all planes
				stepSize = plane1 - plane0;
			}

			Length physicalSizeZ = FormatTools.getPhysicalSizeZ(stepSize);
			if (physicalSizeZ != null) {
				store.setPixelsPhysicalSizeZ(physicalSizeZ, capture);
			}

			// link other meta data to images
			int numPositions = getNumPositions(capture);
			int numTimepoints = getNumTimepoints(capture);
			int numChannels = getNumChannels(capture);
			int numZPlanes = getNumZPlanes(capture);

			int imageIndex = 0;
			for (int timepoint = 0; timepoint < numTimepoints; timepoint++) {
				int deltaT = getElapsedTime(capture, timepoint);
				for (int position = 0; position < numPositions; position++) {
					for (int zplane = 0; zplane < numZPlanes; zplane++) {
						for (int channel = 0; channel < numChannels; channel++, imageIndex++) {
							// set elapsed time
							store.setPlaneDeltaT(new Time(deltaT, UNITS.MS), capture, imageIndex);

							// set exposure time
							int expTime = getExposureTime(capture, channel);
							store.setPlaneExposureTime(new Time(expTime, UNITS.MS), capture, imageIndex);

							// set xy position
							double numberX = getXPosition(capture, position);
							Length positionX = new Length(numberX, UNITS.MICROM);
							store.setPlanePositionX(positionX, capture, imageIndex);
							double numberY = getYPosition(capture, position);
							Length positionY = new Length(numberY, UNITS.MICROM);
							store.setPlanePositionY(positionY, capture, imageIndex);

							// set z position
							double positionZ = getZPosition(capture, position, zplane);
							Length zPos = new Length(positionZ, UNITS.MICROM);
							store.setPlanePositionZ(zPos, capture, imageIndex);
						}
					}
				}
			}

			// set channel names
			for (int channel = 0; channel < numChannels; channel++) {
				String theChannelName = getChannelName(capture, channel);
				store.setChannelName(theChannelName.trim(), capture, channel);
			}
		}

		// populate Objective data
		int objectiveIndex = 0;
		for (int capture = 0; capture < numCaptures; capture++) {
			String objective = getLensName(capture);
			if (objective != null) {
				store.setObjectiveModel(objective, 0, objectiveIndex);
				store.setObjectiveCorrection(
						getCorrection("Other"), 0, objectiveIndex);
				store.setObjectiveImmersion(getImmersion("Other"), 0, objectiveIndex);
				double magnification = getMagnification(capture);
				if (magnification > 0) {
					store.setObjectiveNominalMagnification(
							magnification, 0, objectiveIndex);
				}
			}

			// link Objective to Image
			String objectiveID =
					MetadataTools.createLSID("Objective", 0, objectiveIndex);
			store.setObjectiveID(objectiveID, 0, objectiveIndex);
			if (capture < getSeriesCount()) {
				store.setObjectiveSettingsID(objectiveID, capture);
			}

			objectiveIndex++;
		}
	}

// -- Native methods --
public native boolean openFile(String path);
public native void closeFile();
public native int getNumCaptures();
public native int getNumPositions(int inCapture);
public native int getNumTimepoints(int inCapture);
public native int getNumChannels(int inCapture);
public native int getNumXColumns(int inCapture);
public native int getNumYRows(int inCapture);
public native int getNumZPlanes(int inCapture);
public native int getElapsedTime(int inCapture, int inTimepoint);

public native int getExposureTime(int inCapture, int inChannel);
public native float getVoxelSize(int inCapture);

public native double getXPosition(int inCapture, int inPosition);
public native double getYPosition(int inCapture, int inPosition);
public native double getZPosition(int inCapture, int inPosition, int inZPlane);

public native int getMontageRow(int inCapture, int inPosition);
public native int getMontageColumn(int inCapture, int inPosition);

public native String getChannelName(int inCapture, int inChannel);
public native String getLensName(int inCapture);
public native double getMagnification(int inCapture);
public native String getImageName(int inCapture);
public native String getImageComments(int inCapture);

public native int getBytesPerPixel(int inCapture);

public native boolean readImagePlaneBuf( byte outPlaneBuffer[],
		int inCapture,
		int inPosition,
		int inTimepoint,
		int inZ,
		int inChannel );

}