//
// VirtualPlaneLoader.java
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

package ome.scifio.img.virtual;

import loci.common.DataTools;
import loci.formats.FormatTools;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.planar.PlanarImg;
import ome.scifio.img.ImgIOUtils;

/**
 * This class is responsible for loading one plane of data from an image using
 * an IFormatReader. The loading is done in a virtual fashion with planes loaded
 * when a desired position has not been loaded. The data is loaded into a
 * PlanarImg provided at construction time.
 * 
 * @author Barry DeZonia
 */
public class VirtualPlaneLoader {

	// -- instance variables --

	private final VirtualImg<?> virtImage;
	private final PlanarImg<?, ? extends ArrayDataAccess<?>> planeImg;
	private final long[] planeDims;
	private final long[] planePosLoaded;
	private final boolean bytesOnly;

	// -- constructor --

	/**
	 * Create a VirtualPlaneLoader on a VirtualImage swapping planes into a
	 * PlanarImg as needed.
	 * 
	 * @param image - the VirtualImg to load planes from
	 * @param planeImg - the PlanarImg to load planes into
	 * @param bytesOnly - a flag which defines whether planes passed around as
	 *          byte[]'s only or as other primitive array types (int[]'s, etc.).
	 */
	public VirtualPlaneLoader(final VirtualImg<?> image,
		final PlanarImg<?, ? extends ArrayDataAccess<?>> planeImg,
		final boolean bytesOnly)
	{
		this.virtImage = image;
		this.planeImg = planeImg;
		this.planeDims = new long[image.numDimensions() - 2];
		for (int i = 0; i < planeDims.length; i++)
			this.planeDims[i] = image.dimension(i + 2);
		this.planePosLoaded = new long[planeDims.length];
		this.bytesOnly = bytesOnly;
		loadPlane(new long[image.numDimensions()]);
	}

	// -- public interface --

	/**
	 * Makes sure the plane that contains the data of the given position is loaded
	 * into the PlanarImg. Only reads data from VirtualImg if not already loaded.
	 */
	public boolean virtualSwap(final long[] pos) {
		if (!planeLoaded(pos)) {
			loadPlane(pos);
			return true;
		}
		return false;
	}

	/**
	 * Always loads the plane that contains the data of the given position.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadPlane(final long[] pos) {
		for (int i = 0; i < planePosLoaded.length; i++)
			planePosLoaded[i] = pos[i + 2];
		final int planeNum = planeIndex(planeDims, planePosLoaded);
		byte[] planeBytes = null;
		try {
			planeBytes = virtImage.getReader().openBytes(planeNum);
		}
		catch (final Exception e) {
			throw new IllegalArgumentException("cannot load plane " + planeNum);
		}
		Object primitivePlane;
		if (bytesOnly) {
			primitivePlane = planeBytes;
		}
		else { // want type from encoded bytes
			primitivePlane = typeConvert(planeBytes);
		}
		final ArrayDataAccess<?> wrappedPlane =
			ImgIOUtils.makeArray(primitivePlane);
		((PlanarImg) planeImg).setPlane(0, wrappedPlane);
	}

	// -- private helpers --

	private boolean planeLoaded(final long[] pos) {
		for (int i = 2; i < pos.length; i++)
			if (pos[i] != planePosLoaded[i - 2]) return false;
		return true;
	}

	private static int planeIndex(final long[] planeDimensions,
		final long[] planePos)
	{
		// TODO: Migrate this to FormatTools.rasterToPosition(long[], long[]).
		int index = 0;
		for (int i = 0; i < planePos.length; i++) {
			int delta = 1;
			for (int j = 1; j <= i; j++)
				delta *= planeDimensions[j - 1];
			index += delta * planePos[i];
		}
		return index;
	}

	private Object typeConvert(final byte[] bytes) {
		final int pixelType = virtImage.getReader().getPixelType();
		final int bytesPerPix = FormatTools.getBytesPerPixel(pixelType);
		final boolean floating = FormatTools.isFloatingPoint(pixelType);

		return DataTools.makeDataArray(bytes, bytesPerPix, floating, virtImage
			.getReader().isLittleEndian());
	}

}
