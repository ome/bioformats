/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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
import loci.formats.MissingLibraryException;

import ome.xml.model.primitives.PositiveFloat;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.UNITS;

import org.scijava.nativelib.NativeLibraryUtil;
import org.scijava.nativelib.NativeLibraryUtil.Architecture;

import ij.gui.GenericDialog;

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
			"http://www.intelligent-imaging.com/bioformats";
	private static final String NO_3I_MSG = "3i SlideBook 6 native SLD reader library not found. Press 'Help' button for more details.";
	private static final String GENERAL_3I_MSG = "3i SlideBook 6 native SLD reader library problem. Press 'Help' button for more details.";

	// -- Static initializers --

	private static boolean initialized = false;
	private static boolean libraryFound = false;

	static {
		String errMsg = null;
		Architecture theArch = NativeLibraryUtil.getArchitecture();
	private static boolean isLibraryFound() {
		if (initialized) return libraryFound;
		try {
			// load JNI wrapper of SlideBook6Reader.dll
			if (!libraryFound) {
				libraryFound = NativeLibraryUtil.loadNativeLibrary(SlideBook6Reader.class, "SlideBook6Reader");
				if (!libraryFound) {
					errMsg = new String(NO_3I_MSG + "["+ theArch.name() + "]");
				}
			}
		}
		catch (UnsatisfiedLinkError e) {
			// log level debug, otherwise a warning will be printed every time a file is initialized without the .dll present
			LOGGER.debug(NO_3I_MSG + "["+ theArch.name() + "] ", e);
			errMsg = new String(NO_3I_MSG  + "["+ theArch.name() + "]");
			libraryFound = false;
		}
		catch (SecurityException e) {
			LOGGER.warn("Insufficient permission to load native library"  + "["+ theArch.name() + "] " , e);
			errMsg = new String("Insufficient permission to load native library" + "["+ theArch.name() + "] " + e);
			libraryFound = false;
		}

		initialized = true;
		return libraryFound;
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

	/* @see loci.formats.IFormatReader#isThisType(String, boolean) */
	public boolean isThisType(String file, boolean open) {
		// Check the first few bytes to determine if the file can be read by this reader.
		return super.isThisType(file, open) && isLibraryFound();
	}

	/**
	 * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
	 */
	public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
			throws FormatException, IOException
	{
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
		super.close(fileOnly);
		if (initialized && isLibraryFound()) {
			closeFile();
		}
	}

	/* @see loci.formats.FormatReader#initFile(String) */
	protected void initFile(String id) throws FormatException, IOException {
		super.initFile(id);

		try {
			openFile(id);

			// read basic meta data
			int numCaptures = getNumCaptures();
			int[] numPositions = new int[numCaptures];
			int[] numTimepoints = new int[numCaptures];
			int[] numZPlanes = new int[numCaptures];
			int[] numChannels = new int[numCaptures];
			for (int capture=0; capture < numCaptures; capture++) {
				numPositions[capture] = getNumPositions(capture);
				numTimepoints[capture] = getNumTimepoints(capture) / numPositions[capture];
				numZPlanes[capture] = getNumZPlanes(capture);
				numChannels[capture] = getNumChannels(capture);
			}

			core.clear();

			// set up basic meta data
			for (int capture=0; capture < numCaptures; capture++) {
				CoreMetadata ms = new CoreMetadata();
				core.add(ms);
				setSeries(capture);
				ms.sizeX = getNumXColumns(capture);
				if (ms.sizeX % 2 != 0) ms.sizeX++;
				ms.sizeY = getNumYRows(capture);
				ms.sizeZ = numZPlanes[capture];
				ms.sizeT = numTimepoints[capture] * numPositions[capture]; 
				ms.sizeC = numChannels[capture];
				int bytes = getBytesPerPixel(capture);
				if (bytes % 3 == 0) {
					ms.sizeC *= 3;
					bytes /= 3;
					ms.rgb = true;
				}
				else ms.rgb = false;

				ms.pixelType = FormatTools.pixelTypeFromBytes(bytes, false, true);
				ms.imageCount = ms.sizeZ * ms.sizeT;
				if (!ms.rgb) 
					ms.imageCount *= ms.sizeC;
				ms.interleaved = true;
				ms.littleEndian = true;
				ms.dimensionOrder = "XYCZT";
				ms.indexed = false;
				ms.falseColor = false;
			}
			setSeries(0);

			// fill in meta data
			MetadataStore store = makeFilterMetadata();
			MetadataTools.populatePixels(store, this, true);

			// add extended meta data
			if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
				
				// set instrument information
				String instrumentID = MetadataTools.createLSID("Instrument", 0);
				store.setInstrumentID(instrumentID, 0);

				// set up extended meta data
				for (int capture=0; capture < numCaptures; capture++) {
					// link Instrument and Image
					store.setImageInstrumentRef(instrumentID, capture);

					// set image name
					String imageName = getImageName(capture);
					store.setImageName(imageName, capture);
					// store.setImageName("Image " + (capture + 1), capture);

					// set description
					String imageDescription = getImageComments(capture);
					store.setImageDescription(imageDescription, capture);

					// set voxel size per image (microns)
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
					if (numZPlanes[capture] > 1) {
						double plane0 = getZPosition(capture, 0, 0);
						double plane1 = getZPosition(capture, 0, 1);
						// distance between plane 0 and 1 is step size, assume constant for all planes
						stepSize = Math.abs(plane1 - plane0);
					}

					Length physicalSizeZ = FormatTools.getPhysicalSizeZ(stepSize);
					if (physicalSizeZ != null) {
						store.setPixelsPhysicalSizeZ(physicalSizeZ, capture);
					}

					int imageIndex = 0;
					// if numPositions[capture] > 1 then we have a montage
					for (int timepoint = 0; timepoint < numTimepoints[capture]; timepoint++) {
						int deltaT = getElapsedTime(capture, timepoint);
						for (int position = 0; position < numPositions[capture]; position++) {
							for (int zplane = 0; zplane < numZPlanes[capture]; zplane++) {
								for (int channel = 0; channel < numChannels[capture]; channel++, imageIndex++) {
									// set elapsed time
									store.setPlaneDeltaT(new Time(deltaT, UNITS.MILLISECOND), capture, imageIndex);

									// set exposure time
									int expTime = getExposureTime(capture, channel);
									store.setPlaneExposureTime(new Time(expTime, UNITS.MILLISECOND), capture, imageIndex);

									// set tile xy position
									double numberX = getXPosition(capture, position);
									Length positionX = new Length(numberX, UNITS.MICROMETER);
									store.setPlanePositionX(positionX, capture, imageIndex);
									double numberY = getYPosition(capture, position);
									Length positionY = new Length(numberY, UNITS.MICROMETER);
									store.setPlanePositionY(positionY, capture, imageIndex);

									// set tile z position
									double positionZ = getZPosition(capture, position, zplane);
									Length zPos = new Length(positionZ, UNITS.MICROMETER);
									store.setPlanePositionZ(zPos, capture, imageIndex);
								}
							}
						}
					}

					// set channel names
					for (int channel = 0; channel < numChannels[capture]; channel++) {
						String theChannelName = getChannelName(capture, channel);
						store.setChannelName(theChannelName.trim(), capture, channel);
					}
				}

				// populate Objective data
				int objectiveIndex = 0;
				for (int capture = 0; capture < numCaptures; capture++) {
					// link Objective to Image
					String objectiveID = MetadataTools.createLSID("Objective", 0, objectiveIndex);
					store.setObjectiveID(objectiveID, 0, objectiveIndex);
					store.setObjectiveSettingsID(objectiveID, capture);

					String objective = getLensName(capture);
					if (objective != null) {
						store.setObjectiveModel(objective, 0, objectiveIndex);
					}
					store.setObjectiveCorrection(getCorrection("Other"), 0, objectiveIndex);
					store.setObjectiveImmersion(getImmersion("Other"), 0, objectiveIndex);
					double magnification = getMagnification(capture);
					if (magnification > 0) {
						store.setObjectiveNominalMagnification(magnification, 0, objectiveIndex);
					}
					objectiveIndex++;
				}
			}
		}
		catch (UnsatisfiedLinkError e) {
			throw new MissingLibraryException(GENERAL_3I_MSG, e);
		}
		catch (Exception e) {
			throw new MissingLibraryException(GENERAL_3I_MSG, e);
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
