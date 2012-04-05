//
// ImgSaver.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package ome.scifio.img;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import loci.common.DataTools;
import loci.common.StatusEvent;
import loci.common.StatusListener;
import loci.common.StatusReporter;
import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
import loci.formats.AxisGuesser;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatWriter;
import loci.formats.ImageWriter;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.services.OMEXMLService;
import net.imglib2.exception.ImgLibException;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.basictypeaccess.PlanarAccess;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.meta.Axes;
import net.imglib2.meta.AxisType;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/**
 * Writes out an {@link ImgPlus} using SCIFIO.
 * 
 * @author Mark Hiner
 * @author Curtis Rueden
 */
public class ImgSaver implements StatusReporter {

	// -- Fields --

	private final List<StatusListener> listeners =
		new ArrayList<StatusListener>();

	private final OMEXMLService omexmlService;

	// -- Constructor --

	public ImgSaver() {
		omexmlService = createOMEXMLService();
	}

	// -- ImgSaver methods --

	/**
	 * see isCompressible(ImgPlus)
	 */
	public <T extends RealType<T> & NativeType<T>> boolean isCompressible(
		final Img<T> img)
	{
		return isCompressible(ImgPlus.wrap(img));
	}

	/**
	 * Currently there are limits as to what types of Images can be saved. All
	 * images must ultimately adhere to an, at most, five-dimensional structure
	 * using the known axes X, Y, Z, Channel and Time. Unknown axes (U) can
	 * potentially be handled by coercing to the Channel axis. For example, X Y Z
	 * U C U T would be valid, as would X Y Z U T. But X Y C Z U T would not, as
	 * the unknown axis can not be compressed with Channel. This method will
	 * return true if the axes of the provided image can be represented with a
	 * valid 5D String, and false otherwise.
	 */
	public <T extends RealType<T> & NativeType<T>> boolean isCompressible(
		final ImgPlus<T> img)
	{

		final AxisType[] axes = new AxisType[img.numDimensions()];
		img.axes(axes);

		final long[] axisLengths = new long[5];
		final long[] oldLengths = new long[img.numDimensions()];

		img.dimensions(oldLengths);

		// true if this img contains an axis that will need to be compressed
		boolean foundUnknown = false;

		for (int i = 0; i < axes.length; i++) {
			final AxisType axis = axes[i];

			switch (axis.getLabel().toUpperCase().charAt(0)) {
				case 'X':
				case 'Y':
				case 'Z':
				case 'C':
				case 'T':
					break;
				default:
					if (oldLengths[i] > 1) foundUnknown = true;
			}
		}

		if (!foundUnknown) return false;

		// This ImgPlus had unknown axes of size > 1, so we will check to see if
		// they can be compressed
		final String dimOrder =
			AxisGuesser.guessImgLib(axes, oldLengths, axisLengths);

		return (dimOrder != null);
	}

	/**
	 * saveImg is the entry point for saving an {@link ImgPlus} The goal is to get
	 * to an {@link IFormatWriter} and {@link ImgPlus} which are then passed to
	 * writePlanes. These saveImg signatures facilitate multiple pathways to that
	 * goal. This method is called when a String id and {@linl Img} are provided.
	 * 
	 * @param <T>
	 * @param id
	 * @param img
	 * @throws ImgIOException
	 * @throws IncompatibleTypeException
	 */
	public <T extends RealType<T> & NativeType<T>> void saveImg(final String id,
		final Img<T> img) throws ImgIOException, IncompatibleTypeException
	{
		saveImg(id, ImgPlus.wrap(img));
	}

	/**
	 * String id provided. {@link ImgPlus} provided, or wrapped {@link Img} in
	 * previous saveImg.
	 * 
	 * @param <T>
	 * @param id
	 * @param img
	 * @throws ImgIOException
	 * @throws IncompatibleTypeException
	 */
	public <T extends RealType<T> & NativeType<T>> void saveImg(final String id,
		final ImgPlus<T> img) throws ImgIOException, IncompatibleTypeException
	{
		img.setSource(id);
		img.setName(new File(id).getName());
		saveImg(initializeWriter(id, img), img, false);
	}

	/**
	 * {@link IFormatWriter} and {@link Img} provided
	 * 
	 * @param <T>
	 * @param w
	 * @param img
	 * @throws ImgIOException
	 * @throws IncompatibleTypeException
	 */
	public <T extends RealType<T> & NativeType<T>> void saveImg(
		final IFormatWriter w, final Img<T> img) throws ImgIOException,
		IncompatibleTypeException
	{
		saveImg(w, ImgPlus.wrap(img));
	}

	// TODO IFormatHandler needs to be promoted to be able to get the current
	// file, to get its full path, to provide the ImgPlus
	// pending that, these two IFormatWriter methods are not guaranteed to be
	// useful
	/**
	 * {@link IFormatWriter} provided. {@link ImgPlus} provided, or wrapped
	 * provided {@link Img}.
	 * 
	 * @param <T>
	 * @param w
	 * @param img
	 * @throws ImgIOException
	 * @throws IncompatibleTypeException
	 */
	public <T extends RealType<T> & NativeType<T>> void saveImg(
		final IFormatWriter w, final ImgPlus<T> img) throws ImgIOException,
		IncompatibleTypeException
	{
		saveImg(w, img, true);
	}

	/* Entry point for writePlanes method, the actual workhorse to save pixels to disk */
	private <T extends RealType<T> & NativeType<T>> void
		saveImg(final IFormatWriter w, final ImgPlus<T> img,
			final boolean initializeWriter) throws ImgIOException,
			IncompatibleTypeException
	{

		// use the ImgPlus to calculate necessary metadata if
		if (initializeWriter) {
			populateMeta(w, img);
		}

		if (img.getSource().length() == 0) {
			throw new ImgIOException("Provided Image has no attached source.");
		}

		final long startTime = System.currentTimeMillis();
		final String id = img.getSource();
		final int sliceCount = countSlices(img);

		// write pixels
		writePlanes(w, img);

		final long endTime = System.currentTimeMillis();
		final float time = (endTime - startTime) / 1000f;
		notifyListeners(new StatusEvent(sliceCount, sliceCount, id + ": wrote " +
			sliceCount + " planes in " + time + " s"));
	}

	// -- StatusReporter methods --

	/** Adds a listener to those informed when progress occurs. */
	public void addStatusListener(final StatusListener l) {
		synchronized (listeners) {
			listeners.add(l);
		}
	}

	/** Removes a listener from those informed when progress occurs. */
	public void removeStatusListener(final StatusListener l) {
		synchronized (listeners) {
			listeners.remove(l);
		}
	}

	/** Notifies registered listeners of progress. */
	public void notifyListeners(final StatusEvent e) {
		synchronized (listeners) {
			for (final StatusListener l : listeners)
				l.statusUpdated(e);
		}
	}

	// -- Helper Methods --

	/* Counts the number of slices in the provided ImgPlus.
	 * NumSlices = product of the sizes of all non-X,Y planes.
	 */
	private <T extends RealType<T> & NativeType<T>> int countSlices(
		final ImgPlus<T> img)
	{

		int sliceCount = 1;
		for (int i = 0; i < img.numDimensions(); i++) {
			if (!(img.axis(i).equals(Axes.X) || img.axis(i).equals(Axes.Y))) {
				sliceCount *= img.dimension(i);
			}
		}

		return sliceCount;
	}

	/**
	 * Iterates through the planes of the provided {@link ImgPlus}, converting
	 * each to a byte[] if necessary (the SCIFIO writer requires a byte[]) and
	 * saving the plane. Currently only {@link PlanarImg} is supported.
	 * 
	 * @throws IncompatibleTypeException
	 */
	@SuppressWarnings("unchecked")
	private <T extends RealType<T> & NativeType<T>> void writePlanes(
		IFormatWriter w, final ImgPlus<T> img) throws ImgIOException,
		IncompatibleTypeException
	{
		final PlanarAccess<?> planarAccess = ImgIOUtils.getPlanarAccess(img);
		if (planarAccess == null) {
			throw new IncompatibleTypeException(new ImgLibException(), "Only " +
				PlanarAccess.class + " images supported at this time.");
		}

		final PlanarImg<T, ?> planarImg = (PlanarImg<T, ?>) planarAccess;
		final int planeCount = planarImg.numSlices();

		if (img.numDimensions() > 0) {
			final Class<?> arrayType =
				planarImg.getPlane(0).getCurrentStorageArray().getClass();

			byte[] plane = null;

			// if we know this image will pass to SCIFIO to be saved,
			// then delete the old file if it exists
			if (arrayType == int[].class || arrayType == byte[].class ||
				arrayType == short[].class || arrayType == long[].class ||
				arrayType == double[].class || arrayType == float[].class)
			{
				final File f = new File(img.getSource());
				if (f.exists()) {
					f.delete();
					w = initializeWriter(img.getSource(), img);
					populateMeta(w, img);
				}
			}

			// iterate over each plane
			for (int planeIndex = 0; planeIndex < planeCount; planeIndex++) {
				notifyListeners(new StatusEvent(planeIndex, planeCount,
					"Saving plane " + (planeIndex + 1) + "/" + planeCount));

				final Object curPlane =
					planarImg.getPlane(planeIndex).getCurrentStorageArray();

				// Convert current plane if necessary
				if (arrayType == int[].class) {
					plane = DataTools.intsToBytes((int[]) curPlane, false);
				}
				else if (arrayType == byte[].class) {
					plane = (byte[]) curPlane;
				}
				else if (arrayType == short[].class) {
					plane = DataTools.shortsToBytes((short[]) curPlane, false);
				}
				else if (arrayType == long[].class) {
					plane = DataTools.longsToBytes((long[]) curPlane, false);
				}
				else if (arrayType == double[].class) {
					plane = DataTools.doublesToBytes((double[]) curPlane, false);
				}
				else if (arrayType == float[].class) {
					plane = DataTools.floatsToBytes((float[]) curPlane, false);
				}
				else {
					throw new IncompatibleTypeException(new ImgLibException(),
						"PlanarImgs of type " + planarImg.getPlane(0).getClass() +
							" not supported.");
				}

				// save bytes
				try {
					w.saveBytes(planeIndex, plane);
				}
				catch (final FormatException e) {
					throw new ImgIOException(e);
				}
				catch (final IOException e) {
					throw new ImgIOException(e);
				}
			}

		}

		try {
			w.close();
		}
		catch (final IOException e) {
			throw new ImgIOException(e);
		}
	}

	/**
	 * Creates a new {@link IFormatWriter} with an unpopulated MetadataStore and
	 * sets its id to the provided String.
	 */
	private <T extends RealType<T> & NativeType<T>> IFormatWriter
		initializeWriter(final String id, final ImgPlus<T> img)
			throws ImgIOException
	{
		final IFormatWriter writer = new ImageWriter();
		final IMetadata store = MetadataTools.createOMEXMLMetadata();
		store.createRoot();
		writer.setMetadataRetrieve(store);

		populateMeta(writer, img);

		try {
			writer.setId(id);
		}
		catch (final FormatException e) {
			throw new ImgIOException(e);
		}
		catch (final IOException e) {
			throw new ImgIOException(e);
		}

		return writer;
	}

	/**
	 * Uses the provided {@link ImgPlus} to populate the minimum metadata fields
	 * necessary for writing.
	 */
	private <T extends RealType<T> & NativeType<T>> void populateMeta(
		final IFormatWriter w, final ImgPlus<T> img) throws ImgIOException
	{
		notifyListeners(new StatusEvent("Initializing " + img.getName()));

		final MetadataRetrieve retrieve = w.getMetadataRetrieve();

		if (omexmlService == null) throw new ImgIOException(
			"No OMEXMLService found. Invoke ImgSaver constructor first.");

		// make sure we can store information in the writer's MetadataObject
		if (omexmlService.asStore(retrieve) != null) {
			final IMetadata meta = (IMetadata) retrieve;

			// set required metadata

			final int pixelType = ImgIOUtils.makeType(img.firstElement());

			// TODO is there some way to consolidate this with the isCompressible
			// method?
			final AxisType[] axes = new AxisType[img.numDimensions()];
			img.axes(axes);

			String dimOrder = "";

			final long[] axisLengths = new long[5];
			final long[] oldLengths = new long[img.numDimensions()];
			img.dimensions(oldLengths);
			dimOrder = AxisGuesser.guessImgLib(axes, oldLengths, axisLengths);

			if (dimOrder == null) throw new ImgIOException(
				"Image has more than 5 dimensions in an order that could not be compressed.");

			// TODO if size C, Z, T and dimension order are populated we won't
			// overwrite them.
			/*
			if(meta.getPixelsSizeZ(0) == null) sizeZ = meta.getPixelsSizeZ(0).getValue();
			if(meta.getPixelsSizeC(0) == null) sizeC = meta.getPixelsSizeC(0).getValue();
			if(meta.getPixelsSizeT(0) == null) sizeT = meta.getPixelsSizeT(0).getValue();
			*/

			int sizeX = 0, sizeY = 0, sizeZ = 0, sizeC = 0, sizeT = 0;

			for (int i = 0; i < dimOrder.length(); i++) {
				switch (dimOrder.charAt(i)) {
					case 'X':
						sizeX = new Long(axisLengths[i]).intValue();
						break;
					case 'Y':
						sizeY = new Long(axisLengths[i]).intValue();
						break;
					case 'Z':
						sizeZ = new Long(axisLengths[i]).intValue();
						break;
					case 'C':
						sizeC = new Long(axisLengths[i]).intValue();
						break;
					case 'T':
						sizeT = new Long(axisLengths[i]).intValue();
						break;
				}
			}

			// TODO save composite channel count somewhere...
			MetadataTools.populateMetadata(meta, 0, img.getName(), false, dimOrder,
				FormatTools.getPixelTypeString(pixelType), sizeX, sizeY, sizeZ, sizeC,
				sizeT, 1);
		}
	}

	private OMEXMLService createOMEXMLService() {
		try {
			return new ServiceFactory().getInstance(OMEXMLService.class);
		}
		catch (final DependencyException exc) {
			return null;
		}
	}

}
