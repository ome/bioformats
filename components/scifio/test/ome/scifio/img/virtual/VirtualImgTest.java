//
// VirtualImgTest.java
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.RealType;

import org.junit.Test;

/**
 * TODO
 * 
 * @author Barry DeZonia
 */
public class VirtualImgTest {

	@Test
	public void testVariousThings() {

		// open image

		VirtualImg<?> image = null;
		try {
			final URL fileURL = getClass().getResource("TestImage.tif");
			image = VirtualImg.create(fileURL.getFile(), false);
		}
		catch (final Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		assertNotNull(image);

		// test out factory

		final ImgFactory<? extends RealType<?>> factory = image.factory();
		try {
			factory.create(new long[] { 1, 2, 3 }, null);
		}
		catch (final UnsupportedOperationException e) {
			assertTrue(true);
		}

		// test out cursor

		long numElements = 0;
		final VirtualCursor<? extends RealType<?>> cursor = image.cursor();
		while (cursor.hasNext()) {
			cursor.next();
			numElements++;
		}
		assertEquals(20 * 30 * 10, numElements);
		assertNotNull(cursor.getCurrentPlane());

		// test out random access

		VirtualRandomAccess<? extends RealType<?>> accessor = image.randomAccess();
		final long[] pos = new long[3];
		for (int x = 0; x < 20; x++) {
			for (int z = 0; z < 10; z++) {
				for (int y = 0; y < 30; y++) {
					pos[0] = x;
					pos[1] = y;
					pos[2] = z;
					accessor.setPosition(pos);
					assertEquals(x + 2 * y + 3 * z, accessor.get().getRealDouble(), 0);
				}
			}
		}
		assertNotNull(accessor.getCurrentPlane());

		final long xDim = image.dimension(0);

		// test byte only code
		try {
			final URL fileURL = getClass().getResource("TestImage.tif");
			image = VirtualImg.create(fileURL.getFile(), true);
		}
		catch (final Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}

		assertNotNull(image);
		assertEquals(xDim * 2, image.dimension(0));

		accessor = image.randomAccess();
		for (int x = 0; x < 20; x++) {
			for (int z = 0; z < 10; z++) {
				for (int y = 0; y < 30; y++) {
					// NOTE - the following code might fail on a Power PC architecture.
					// Might need to test for platform & order hi/lo tests appropriately
					final short expected = (short) (x + 2 * y + 3 * z);
					final int hi = (expected & 0xff00) >> 8;
					final int lo = expected & 0xff;
					pos[1] = y;
					pos[2] = z;
					pos[0] = 2 * x;
					accessor.setPosition(pos);
					assertEquals(hi, accessor.get().getRealDouble(), 0);
					pos[0] = 2 * x + 1;
					accessor.setPosition(pos);
					assertEquals(lo, accessor.get().getRealDouble(), 0);
				}
			}
		}
		assertNotNull(accessor.getCurrentPlane());
	}

}
