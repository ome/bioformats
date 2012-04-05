//
// ReadImage.java
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

import net.imglib2.Cursor;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * A simple test for {@link ImgOpener}.
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class ReadImage {

	public static <T extends RealType<T> & NativeType<T>> void main(
		final String[] args) throws ImgIOException
	{
		final ImgOpener imageOpener = new ImgOpener();

		final String[] ids;
		if (args.length == 0) {
			final String userHome = System.getProperty("user.home");
			ids = new String[] {
//				userHome + "/data/Spindle_Green_d3d.dv",
				userHome + "/data/mitosis-test.ipw",
//				userHome + "/data/test_greys.lif",
				userHome + "/data/slice1_810nm_40x_z1_pcc100_scanin_20s_01.sdt" };
		}
		else ids = args;

		// read all arguments using auto-detected type with default container
		System.out.println("== AUTO-DETECTED TYPE, DEFAULT CONTAINER ==");
		for (final String id : ids) {
			try {
				final ImgPlus<T> img = imageOpener.openImg(id);
				reportInformation(img);
			}
			catch (final IncompatibleTypeException e) {
				e.printStackTrace();
			}
		}

		// read all arguments using FloatType with ArrayContainer
		System.out.println();
		System.out.println("== FLOAT TYPE, ARRAY CONTAINER ==");
		final ImgFactory<FloatType> acf = new ArrayImgFactory<FloatType>();
		for (final String arg : args) {
			try {
				final ImgPlus<FloatType> img = imageOpener.openImg(arg, acf);
				reportInformation(img);
			}
			catch (final IncompatibleTypeException e) {
				e.printStackTrace();
			}
		}

		// read all arguments using FloatType with PlanarImg
		System.out.println();
		System.out.println("== FLOAT TYPE, PLANAR CONTAINER ==");
		final ImgFactory<FloatType> pcf = new PlanarImgFactory<FloatType>();
		for (final String arg : args) {
			try {
				final ImgPlus<FloatType> img = imageOpener.openImg(arg, pcf);
				reportInformation(img);
			}
			catch (final IncompatibleTypeException e) {
				e.printStackTrace();
			}
		}
	}

	/** Prints out some useful information about the {@link Img}. */
	public static <T extends RealType<T>> void
		reportInformation(final Img<T> img)
	{
		System.out.println(img);
		final Cursor<T> cursor = img.cursor();
		cursor.fwd();
		System.out.println("\tType = " + cursor.get().getClass().getName());
		System.out.println("\tImg = " + img.getClass().getName());
	}

}
