//
// ImgOpener.java
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
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.ChannelFiller;
import loci.formats.ChannelSeparator;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.MinMaxCalculator;
import loci.formats.ReaderWrapper;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;
import net.imglib2.display.ColorTable16;
import net.imglib2.display.ColorTable8;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.basictypeaccess.PlanarAccess;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.meta.Axes;
import net.imglib2.meta.AxisType;
import net.imglib2.sampler.special.OrthoSliceCursor;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.RealType;
import ome.xml.model.primitives.PositiveFloat;

/**
 * Reads in an {@link ImgPlus} using Bio-Formats.
 * 
 * @author Curtis Rueden
 * @author Stephan Preibisch
 */
public class ImgOpener implements StatusReporter {

	// -- Fields --

	private final List<StatusListener> listeners =
		new ArrayList<StatusListener>();

	// -- ImgOpener methods --

	/**
	 * Reads in an {@link ImgPlus} from the given source. It will read it into a
	 * {@link PlanarImg}, where the {@link Type} T is defined by the file format
	 * and implements {@link RealType} and {@link NativeType}.
	 * 
	 * @param id The source of the image (e.g., a file on disk).
	 * @throws ImgIOException if there is a problem reading the image data.
	 * @throws IncompatibleTypeException if the {@link Type} of the file is
	 *           incompatible with the {@link PlanarImg}.
	 */
	public <T extends RealType<T> & NativeType<T>> ImgPlus<T> openImg(
		final String id) throws ImgIOException, IncompatibleTypeException
	{
		return openImg(id, new PlanarImgFactory<T>());
	}

	/**
	 * Reads in an {@link ImgPlus} from the given source. It will read it into a
	 * {@link PlanarImg}, where the {@link Type} T is defined by the file format
	 * and implements {@link RealType} and {@link NativeType}.
	 * 
	 * @param id The source of the image (e.g., a file on disk).
	 * @param computeMinMax If set, the {@link ImgPlus}'s channel minimum and
	 *          maximum metadata is computed and populated based on the data's
	 *          actual pixel values.
	 * @throws ImgIOException if there is a problem reading the image data.
	 * @throws IncompatibleTypeException if the {@link Type} of the file is
	 *           incompatible with the {@link PlanarImg}.
	 */
	public <T extends RealType<T> & NativeType<T>> ImgPlus<T> openImg(
		final String id, final boolean computeMinMax) throws ImgIOException,
		IncompatibleTypeException
	{
		return openImg(id, new PlanarImgFactory<T>(), computeMinMax);
	}

	/**
	 * Reads in an {@link ImgPlus} from the given source, using the specified
	 * {@link ImgFactory} to construct the resultant {@link Img}. The {@link Type}
	 * T is defined by the file format and implements {@link RealType} and
	 * {@link NativeType}. The {@link Type} of the {@link ImgFactory} will be
	 * ignored.
	 * 
	 * @param id The source of the image (e.g., a file on disk).
	 * @param imgFactory The {@link ImgFactory} to use for creating the resultant
	 *          {@link ImgPlus}.
	 * @throws ImgIOException if there is a problem reading the image data.
	 * @throws IncompatibleTypeException if the Type of the {@link Img} is
	 *           incompatible with the {@link ImgFactory}
	 */
	public <T extends RealType<T> & NativeType<T>> ImgPlus<T> openImg(
		final String id, final ImgFactory<?> imgFactory) throws ImgIOException,
		IncompatibleTypeException
	{
		return openImg(id, imgFactory, true);
	}

	/**
	 * Reads in an {@link ImgPlus} from the given source, using the specified
	 * {@link ImgFactory} to construct the {@link Img}. The {@link Type} T is
	 * defined by the file format and implements {@link RealType} and
	 * {@link NativeType}. The {@link Type} of the {@link ImgFactory} will be
	 * ignored.
	 * 
	 * @param id The source of the image (e.g., a file on disk).
	 * @param imgFactory The {@link ImgFactory} to use for creating the resultant
	 *          {@link ImgPlus}.
	 * @param computeMinMax If set, the {@link ImgPlus}'s channel minimum and
	 *          maximum metadata is computed and populated based on the data's
	 *          actual pixel values.
	 * @throws ImgIOException if there is a problem reading the image data.
	 * @throws IncompatibleTypeException if the {@link Type} of the {@link Img} is
	 *           incompatible with the {@link ImgFactory}
	 */
	public <T extends RealType<T> & NativeType<T>> ImgPlus<T> openImg(
		final String id, final ImgFactory<?> imgFactory,
		final boolean computeMinMax) throws ImgIOException,
		IncompatibleTypeException
	{
		try {
			final IFormatReader r = initializeReader(id, computeMinMax);
			final T type = ImgIOUtils.makeType(r.getPixelType());
			final ImgFactory<T> imgFactoryT = imgFactory.imgFactory(type);
			return openImg(r, imgFactoryT, type, computeMinMax);
		}
		catch (final FormatException e) {
			throw new ImgIOException(e);
		}
		catch (final IOException e) {
			throw new ImgIOException(e);
		}
	}

	/**
	 * Reads in an {@link ImgPlus} from the given source, using the given
	 * {@link ImgFactory} to construct the {@link Img}. The {@link Type} T to read
	 * is defined by the third parameter.
	 * 
	 * @param imgFactory The {@link ImgFactory} to use for creating the resultant
	 *          {@link ImgPlus}.
	 * @param type The {@link Type} T of the output {@link ImgPlus}, which must
	 *          match the typing of the {@link ImgFactory}.
	 * @throws ImgIOException if there is a problem reading the image data.
	 */
	public <T extends RealType<T> & NativeType<T>> ImgPlus<T> openImg(
		final String id, final ImgFactory<T> imgFactory, final T type)
		throws ImgIOException
	{
		return openImg(id, imgFactory, type, false);
	}

	/**
	 * Reads in an {@link ImgPlus} from the given source, using the given
	 * {@link ImgFactory} to construct the {@link Img}. The {@link Type} T to read
	 * is defined by the third parameter.
	 * 
	 * @param imgFactory The {@link ImgFactory} to use for creating the resultant
	 *          {@link ImgPlus}.
	 * @param type The {@link Type} T of the output {@link ImgPlus}, which must
	 *          match the typing of the {@link ImgFactory}.
	 * @param computeMinMax If set, the {@link ImgPlus}'s channel minimum and
	 *          maximum metadata is computed and populated based on the data's
	 *          actual pixel values.
	 * @throws ImgIOException if there is a problem reading the image data.
	 */
	public <T extends RealType<T> & NativeType<T>> ImgPlus<T> openImg(
		final String id, final ImgFactory<T> imgFactory, final T type,
		final boolean computeMinMax) throws ImgIOException
	{
		try {
			final IFormatReader r = initializeReader(id, computeMinMax);
			return openImg(r, imgFactory, type, computeMinMax);
		}
		catch (final FormatException e) {
			throw new ImgIOException(e);
		}
		catch (final IOException e) {
			throw new ImgIOException(e);
		}
	}

	/**
	 * Reads in an {@link ImgPlus} from the given initialized
	 * {@link IFormatReader}, using the given {@link ImgFactory} to construct the
	 * {@link Img}. The {@link Type} T to read is defined by the third parameter.
	 * 
	 * @param r An initialized {@link IFormatReader} to use for reading image
	 *          data.
	 * @param imgFactory The {@link ImgFactory} to use for creating the resultant
	 *          {@link ImgPlus}.
	 * @param type The {@link Type} T of the output {@link ImgPlus}, which must
	 *          match the typing of the {@link ImgFactory}.
	 * @throws ImgIOException if there is a problem reading the image data.
	 */
	public <T extends RealType<T> & NativeType<T>> ImgPlus<T> openImg(
		final IFormatReader r, final ImgFactory<T> imgFactory, final T type)
		throws ImgIOException
	{
		return openImg(r, imgFactory, type, true);
	}

	/**
	 * Reads in an {@link ImgPlus} from the given initialized
	 * {@link IFormatReader}, using the given {@link ImgFactory} to construct the
	 * {@link Img}. The {@link Type} T to read is defined by the third parameter.
	 * 
	 * @param r An initialized {@link IFormatReader} to use for reading image
	 *          data.
	 * @param imgFactory The {@link ImgFactory} to use for creating the resultant
	 *          {@link ImgPlus}.
	 * @param type The {@link Type} T of the output {@link ImgPlus}, which must
	 *          match the typing of the {@link ImgFactory}.
	 * @param computeMinMax If set, the {@link ImgPlus}'s channel minimum and
	 *          maximum metadata is computed and populated based on the data's
	 *          actual pixel values.
	 * @throws ImgIOException if there is a problem reading the image data.
	 */
	public <T extends RealType<T> & NativeType<T>> ImgPlus<T> openImg(
		final IFormatReader r, final ImgFactory<T> imgFactory, final T type,
		final boolean computeMinMax) throws ImgIOException
	{
		// create image and read metadata
		final long[] dimLengths = getDimLengths(r);
		final Img<T> img = imgFactory.create(dimLengths, type);
		final ImgPlus<T> imgPlus = makeImgPlus(img, r);

		// read pixels
		final long startTime = System.currentTimeMillis();
		final String id = r.getCurrentFile();
		final int planeCount = r.getImageCount();
		try {
			readPlanes(r, type, imgPlus, computeMinMax);
		}
		catch (final FormatException e) {
			throw new ImgIOException(e);
		}
		catch (final IOException e) {
			throw new ImgIOException(e);
		}
		final long endTime = System.currentTimeMillis();
		final float time = (endTime - startTime) / 1000f;
		notifyListeners(new StatusEvent(planeCount, planeCount, id + ": read " +
			planeCount + " planes in " + time + "s"));

		return imgPlus;
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

	public static IFormatReader createReader(final String id,
		final boolean computeMinMax) throws FormatException, IOException
	{
		IFormatReader r = null;
		r = new ImageReader();
		r = new ChannelFiller(r);
		r = new ChannelSeparator(r);
		if (computeMinMax) r = new MinMaxCalculator(r);

		// attach OME-XML metadata object to reader
		try {
			final ServiceFactory factory = new ServiceFactory();
			final OMEXMLService service = factory.getInstance(OMEXMLService.class);
			final IMetadata meta = service.createOMEXMLMetadata();
			r.setMetadataStore(meta);
		}
		catch (final ServiceException e) {
			throw new FormatException(e);
		}
		catch (final DependencyException e) {
			throw new FormatException(e);
		}

		r.setId(id);

		return r;
	}
	
	/** Compiles an N-dimensional list of axis lengths from the given reader. */
	public static long[] getDimLengths(final IFormatReader r) {
		final long sizeX = r.getSizeX();
		final long sizeY = r.getSizeY();
		final long sizeZ = r.getSizeZ();
		final long sizeT = r.getSizeT();
		// final String[] cDimTypes = r.getChannelDimTypes();
		final int[] cDimLengths = r.getChannelDimLengths();
		final String dimOrder = r.getDimensionOrder();

		final List<Long> dimLengthsList = new ArrayList<Long>();

		// add core dimensions
		for (int i = 0; i < dimOrder.length(); i++) {
			final char dim = dimOrder.charAt(i);
			switch (dim) {
				case 'X':
					if (sizeX > 0) dimLengthsList.add(sizeX);
					break;
				case 'Y':
					if (sizeY > 0) dimLengthsList.add(sizeY);
					break;
				case 'Z':
					if (sizeZ > 1) dimLengthsList.add(sizeZ);
					break;
				case 'T':
					if (sizeT > 1) dimLengthsList.add(sizeT);
					break;
				case 'C':
					for (int c = 0; c < cDimLengths.length; c++) {
						final long len = cDimLengths[c];
						if (len > 1) dimLengthsList.add(len);
					}
					break;
			}
		}

		// convert result to primitive array
		final long[] dimLengths = new long[dimLengthsList.size()];
		for (int i = 0; i < dimLengths.length; i++) {
			dimLengths[i] = dimLengthsList.get(i);
		}
		return dimLengths;
	}

	// -- Helper methods --

	/** Constructs and initializes a Bio-Formats reader for the given file. */
	private IFormatReader initializeReader(final String id,
		final boolean computeMinMax) throws FormatException, IOException
	{
		notifyListeners(new StatusEvent("Initializing " + id));

		return createReader(id, computeMinMax);
	}

	/** Compiles an N-dimensional list of axis types from the given reader. */
	private AxisType[] getDimTypes(final IFormatReader r) {
		final int sizeX = r.getSizeX();
		final int sizeY = r.getSizeY();
		final int sizeZ = r.getSizeZ();
		final int sizeT = r.getSizeT();
		final String[] cDimTypes = r.getChannelDimTypes();
		final int[] cDimLengths = r.getChannelDimLengths();
		final String dimOrder = r.getDimensionOrder();
		final List<AxisType> dimTypes = new ArrayList<AxisType>();

		// add core dimensions
		for (final char dim : dimOrder.toCharArray()) {
			switch (dim) {
				case 'X':
					if (sizeX > 1) dimTypes.add(Axes.X);
					break;
				case 'Y':
					if (sizeY > 1) dimTypes.add(Axes.Y);
					break;
				case 'Z':
					if (sizeZ > 1) dimTypes.add(Axes.Z);
					break;
				case 'T':
					if (sizeT > 1) dimTypes.add(Axes.TIME);
					break;
				case 'C':
					for (int c = 0; c < cDimTypes.length; c++) {
						final int len = cDimLengths[c];
						if (len > 1) dimTypes.add(Axes.get(cDimTypes[c]));
					}
					break;
			}
		}

		return dimTypes.toArray(new AxisType[0]);
	}

	/** Compiles an N-dimensional list of calibration values. */
	private double[] getCalibration(final IFormatReader r) {
		final long sizeX = r.getSizeX();
		final long sizeY = r.getSizeY();
		final long sizeZ = r.getSizeZ();
		final long sizeT = r.getSizeT();
		final int[] cDimLengths = r.getChannelDimLengths();
		final String dimOrder = r.getDimensionOrder();

		final IMetadata meta = (IMetadata) r.getMetadataStore();
		final PositiveFloat xCalin = meta.getPixelsPhysicalSizeX(0);
		final PositiveFloat yCalin = meta.getPixelsPhysicalSizeY(0);
		final PositiveFloat zCalin = meta.getPixelsPhysicalSizeZ(0);
		Double tCal = meta.getPixelsTimeIncrement(0);

		final Double xCal, yCal, zCal;

		if (xCalin == null) xCal = Double.NaN;
		else xCal = xCalin.getValue();

		if (yCalin == null) yCal = Double.NaN;
		else yCal = yCalin.getValue();

		if (zCalin == null) zCal = Double.NaN;
		else zCal = zCalin.getValue();

		if (tCal == null) tCal = Double.NaN;

		final List<Double> calibrationList = new ArrayList<Double>();

		// add core dimensions
		for (int i = 0; i < dimOrder.length(); i++) {
			final char dim = dimOrder.charAt(i);
			switch (dim) {
				case 'X':
					if (sizeX > 1) calibrationList.add(xCal);
					break;
				case 'Y':
					if (sizeY > 1) calibrationList.add(yCal);
					break;
				case 'Z':
					if (sizeZ > 1) calibrationList.add(zCal);
					break;
				case 'T':
					if (sizeT > 1) calibrationList.add(tCal);
					break;
				case 'C':
					for (int c = 0; c < cDimLengths.length; c++) {
						final long len = cDimLengths[c];
						if (len > 1) calibrationList.add(Double.NaN);
					}
					break;
			}
		}

		// convert result to primitive array
		final double[] calibration = new double[calibrationList.size()];
		for (int i = 0; i < calibration.length; i++) {
			calibration[i] = calibrationList.get(i);
		}
		return calibration;
	}

	/**
	 * Wraps the given {@link Img} in an {@link ImgPlus} with metadata
	 * corresponding to the specified initialized {@link IFormatReader}.
	 */
	private <T extends RealType<T>> ImgPlus<T> makeImgPlus(final Img<T> img,
		final IFormatReader r) throws ImgIOException
	{
		final String id = r.getCurrentFile();
		final File idFile = new File(id);
		final String name = idFile.exists() ? idFile.getName() : id;

		final AxisType[] dimTypes = getDimTypes(r);
		final double[] cal = getCalibration(r);

		final IFormatReader base;
		try {
			base = unwrap(r);
		}
		catch (final FormatException exc) {
			throw new ImgIOException(exc);
		}
		catch (final IOException exc) {
			throw new ImgIOException(exc);
		}
		final int rgbChannelCount = base.getRGBChannelCount();
		final int validBits = r.getBitsPerPixel();

		final ImgPlus<T> imgPlus = new ImgPlus<T>(img, name, dimTypes, cal);
		imgPlus.setValidBits(validBits);

		int compositeChannelCount = rgbChannelCount;
		if (rgbChannelCount == 1) {
			// HACK: Support ImageJ color mode embedded in TIFF files.
			final String colorMode = (String) r.getMetadataValue("Color mode");
			if ("composite".equals(colorMode)) {
				compositeChannelCount = r.getSizeC();
			}
		}
		imgPlus.setCompositeChannelCount(compositeChannelCount);

		return imgPlus;
	}

	/**
	 * Finds the lowest level wrapped reader, preferably a {@link ChannelFiller},
	 * but otherwise the base reader. This is useful for determining whether the
	 * input data is intended to be viewed with multiple channels composited
	 * together.
	 */
	private IFormatReader unwrap(final IFormatReader r) throws FormatException,
		IOException
	{
		if (!(r instanceof ReaderWrapper)) return r;
		final ReaderWrapper rw = (ReaderWrapper) r;
		final IFormatReader channelFiller = rw.unwrap(ChannelFiller.class, null);
		if (channelFiller != null) return channelFiller;
		return rw.unwrap();
	}

	/**
	 * Reads planes from the given initialized {@link IFormatReader} into the
	 * specified {@link Img}.
	 */
	private <T extends RealType<T>> void readPlanes(final IFormatReader r,
		final T type, final ImgPlus<T> imgPlus, final boolean computeMinMax)
		throws FormatException, IOException
	{
		// TODO - create better container types; either:
		// 1) an array container type using one byte array per plane
		// 2) as #1, but with an IFormatReader reference reading planes on demand
		// 3) as PlanarRandomAccess, but with an IFormatReader reference
		// reading planes on demand

		// PlanarRandomAccess is useful for efficient access to pixels in ImageJ
		// (e.g., getPixels)
		// #1 is useful for efficient Bio-Formats import, and useful for tools
		// needing byte arrays (e.g., BufferedImage Java3D texturing by reference)
		// #2 is useful for efficient memory use for tools wanting matching
		// primitive arrays (e.g., virtual stacks in ImageJ)
		// #3 is useful for efficient memory use

		// get container
		final PlanarAccess<?> planarAccess = ImgIOUtils.getPlanarAccess(imgPlus);
		final T inputType = ImgIOUtils.makeType(r.getPixelType());
		final T outputType = type;
		final boolean compatibleTypes =
			outputType.getClass().isAssignableFrom(inputType.getClass());

		// populate planes
		final int planeCount = r.getImageCount();
		final boolean isPlanar = planarAccess != null && compatibleTypes;
		imgPlus.initializeColorTables(planeCount);

		byte[] plane = null;
		for (int no = 0; no < planeCount; no++) {
			notifyListeners(new StatusEvent(no, planeCount, "Reading plane " +
				(no + 1) + "/" + planeCount));
			if (plane == null) plane = r.openBytes(no);
			else r.openBytes(no, plane);
			if (isPlanar) populatePlane(r, no, plane, planarAccess);
			else populatePlane(r, no, plane, imgPlus);

			// store color table
			final byte[][] lut8 = r.get8BitLookupTable();
			if (lut8 != null) imgPlus.setColorTable(new ColorTable8(lut8), no);
			final short[][] lut16 = r.get16BitLookupTable();
			if (lut16 != null) imgPlus.setColorTable(new ColorTable16(lut16), no);
		}
		if (computeMinMax) populateMinMax(r, imgPlus);
		r.close();
	}

	/** Populates plane by reference using {@link PlanarAccess} interface. */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void populatePlane(final IFormatReader r, final int no,
		final byte[] plane, final PlanarAccess planarAccess)
	{
		final int pixelType = r.getPixelType();
		final int bpp = FormatTools.getBytesPerPixel(pixelType);
		final boolean fp = FormatTools.isFloatingPoint(pixelType);
		final boolean little = r.isLittleEndian();
		Object planeArray = DataTools.makeDataArray(plane, bpp, fp, little);
		if (planeArray == plane) {
			// array was returned by reference; make a copy
			final byte[] planeCopy = new byte[plane.length];
			System.arraycopy(plane, 0, planeCopy, 0, plane.length);
			planeArray = planeCopy;
		}
		planarAccess.setPlane(no, ImgIOUtils.makeArray(planeArray));
	}

	/**
	 * Uses a cursor to populate the plane. This solution is general and works
	 * regardless of container, but at the expense of performance both now and
	 * later.
	 */
	private <T extends RealType<T>> void populatePlane(final IFormatReader r,
		final int no, final byte[] plane, final ImgPlus<T> img)
	{
		final int sizeX = r.getSizeX();
		final int pixelType = r.getPixelType();
		final boolean little = r.isLittleEndian();

		final long[] dimLengths = getDimLengths(r);
		final long[] pos = new long[dimLengths.length];

		final int planeX = 0;
		final int planeY = 1;

		getPosition(r, no, pos);

		final OrthoSliceCursor<T> cursor =
			new OrthoSliceCursor<T>(img, planeX, planeY, pos);

		while (cursor.hasNext()) {
			cursor.fwd();
			final int index =
				cursor.getIntPosition(planeX) + cursor.getIntPosition(planeY) * sizeX;
			final double value = decodeWord(plane, index, pixelType, little);
			cursor.get().setReal(value);
		}
	}

	private void populateMinMax(final IFormatReader r, final ImgPlus<?> imgPlus)
		throws FormatException, IOException
	{
		final int sizeC = r.getSizeC();
		final ReaderWrapper rw = (ReaderWrapper) r;
		final MinMaxCalculator minMaxCalc =
			(MinMaxCalculator) rw.unwrap(MinMaxCalculator.class, null);
		for (int c = 0; c < sizeC; c++) {
			final Double min = minMaxCalc.getChannelKnownMinimum(c);
			final Double max = minMaxCalc.getChannelKnownMaximum(c);
			imgPlus.setChannelMinimum(c, min == null ? Double.NaN : min);
			imgPlus.setChannelMaximum(c, max == null ? Double.NaN : max);
		}
	}

	/** Copies the current dimensional position into the given array. */
	private void getPosition(final IFormatReader r, final int no,
		final long[] pos)
	{
		final int sizeX = r.getSizeX();
		final int sizeY = r.getSizeY();
		final int sizeZ = r.getSizeZ();
		final int sizeT = r.getSizeT();
		final int[] cDimLengths = r.getChannelDimLengths();
		final String dimOrder = r.getDimensionOrder();

		final int[] zct = r.getZCTCoords(no);

		int index = 0;
		for (int i = 0; i < dimOrder.length(); i++) {
			final char dim = dimOrder.charAt(i);
			switch (dim) {
				case 'X':
					if (sizeX > 1) index++; // NB: Leave X axis position alone.
					break;
				case 'Y':
					if (sizeY > 1) index++; // NB: Leave Y axis position alone.
					break;
				case 'Z':
					if (sizeZ > 1) pos[index++] = zct[0];
					break;
				case 'T':
					if (sizeT > 1) pos[index++] = zct[2];
					break;
				case 'C':
					final int[] cPos = FormatTools.rasterToPosition(cDimLengths, zct[1]);
					for (int c = 0; c < cDimLengths.length; c++) {
						if (cDimLengths[c] > 1) pos[index++] = cPos[c];
					}
					break;
			}
		}
	}

	private static double decodeWord(final byte[] plane, final int index,
		final int pixelType, final boolean little)
	{
		final double value;
		switch (pixelType) {
			case FormatTools.UINT8:
				value = plane[index] & 0xff;
				break;
			case FormatTools.INT8:
				value = plane[index];
				break;
			case FormatTools.UINT16:
				value = DataTools.bytesToShort(plane, 2 * index, 2, little) & 0xffff;
				break;
			case FormatTools.INT16:
				value = DataTools.bytesToShort(plane, 2 * index, 2, little);
				break;
			case FormatTools.UINT32:
				value = DataTools.bytesToInt(plane, 4 * index, 4, little) & 0xffffffffL;
				break;
			case FormatTools.INT32:
				value = DataTools.bytesToInt(plane, 4 * index, 4, little);
				break;
			case FormatTools.FLOAT:
				value = DataTools.bytesToFloat(plane, 4 * index, 4, little);
				break;
			case FormatTools.DOUBLE:
				value = DataTools.bytesToDouble(plane, 8 * index, 8, little);
				break;
			default:
				value = Double.NaN;
		}
		return value;
	}

}
