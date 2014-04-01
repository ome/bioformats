/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2014 Open Microscopy Environment:
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

package loci.formats.utests;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;

import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.gui.BufferedImageWriter;
import loci.formats.meta.IMetadata;
import loci.formats.out.OMEBinaryOnlyTiffWriter;
import loci.formats.services.OMEXMLService;

import ome.xml.model.enums.EnumerationException;


/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/SampleTiffOMEModelMock.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/SampleTiffOMEModelMock.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class SampleBinaryOMETiff {

	private static final String OME_TIFF_EXTENSION = ".ome.tif";
	public int sizeZsub;
	public int sizeTsub;
	public int sizeCsub;
	public boolean isModulo;

	public SampleBinaryOMETiff(String filename, CoreMetadata coreInfo) throws FormatException, IOException {

		//TODO: Move to using Modulo directly from core data
		isModulo = false;
		if (coreInfo.moduloC.start == coreInfo.moduloC.end) {
			sizeCsub = 1;
		} else {
			sizeCsub = 3;
		}
		if (coreInfo.moduloZ.start == coreInfo.moduloZ.end) {
			sizeZsub = 1;
		} else {
			sizeZsub = 2;
		}
		if (coreInfo.moduloT.start == coreInfo.moduloT.end) {
			sizeTsub = 1;
		} else {
			sizeTsub = 4;
		}

		final String id;
		String justFilename;
		String metaFilename;
		if (filename.toLowerCase().endsWith(OME_TIFF_EXTENSION)) {
			id = filename;
			justFilename = new String(filename);
			justFilename = justFilename.substring(0, justFilename.length() - OME_TIFF_EXTENSION.length());

		}
		else {
			id = filename + OME_TIFF_EXTENSION;
			justFilename = new String(filename);
		}

		metaFilename = new String(justFilename);

		metaFilename = metaFilename.replaceAll(FormatTools.Z_NUM, "#");
		metaFilename = metaFilename.replaceAll(FormatTools.T_NUM, "#");
		metaFilename = metaFilename.replaceAll(FormatTools.CHANNEL_NUM, "#");

		final OMEBinaryOnlyTiffWriter out = new OMEBinaryOnlyTiffWriter();
		try {
			out.setMetadataRetrieve(createMetadata(id, coreInfo));
		}
		catch (final DependencyException e) {
			out.close();
			throw new FormatException(e);
		}
		catch (final ServiceException e) {
			out.close();
			throw new FormatException(e);
		}
		catch (final EnumerationException e) {
			out.close();
			throw new FormatException(e);
		}

		for (int i = 0; i < coreInfo.imageCount; i++) {
	          // for each plane get the ZCT location
	        StringBuilder planeFilenameBuilder = new StringBuilder();
			final BufferedImage plane = createPlane(filename, coreInfo, i, planeFilenameBuilder);
	        out.setId(planeFilenameBuilder.toString());
			out.saveBytes(i, BufferedImageWriter.toBytes(plane, out));
		}
		out.setId(metaFilename);
		out.close();

	}

	private IMetadata createMetadata(final String name, final CoreMetadata info)
			throws DependencyException, ServiceException, EnumerationException {
		final ServiceFactory serviceFactory = new ServiceFactory();
		final OMEXMLService omexmlService =
				serviceFactory.getInstance(OMEXMLService.class);
		final IMetadata meta = omexmlService.createOMEXMLMetadata();
		MetadataTools.populateMetadata(meta, 0, name, info);

		if (isModulo) {
			meta.setXMLAnnotationID("Annotation:Modulo:0", 0);
			meta.setXMLAnnotationNamespace("openmicroscopy.org/omero/dimension/modulo", 0);
			meta.setXMLAnnotationDescription("For a description of how 6D, 7D, and 8D data is stored using the Modulo extension see http://www.openmicroscopy.org/site/support/ome-model/developers/6d-7d-and-8d-storage.html", 0);
			StringBuilder moduloBlock = new StringBuilder();

			moduloBlock.append("<Modulo namespace=\"http://www.openmicroscopy.org/Schemas/Additions/2011-09\">");
			if (sizeZsub != 1) {
				moduloBlock.append("<ModuloAlongZ Type=\"other\" TypeDescription=\"Example Data Over Z-Plane\" Start=\"0\" Step=\"1\" End=\"");
				moduloBlock.append(sizeZsub);
				moduloBlock.append("\"/>");
			}
			if (sizeTsub != 1) {
				moduloBlock.append("<ModuloAlongT Type=\"other\" TypeDescription=\"Example Data Over Time \" Start=\"0\" Step=\"1\" End=\"");
				moduloBlock.append(sizeTsub);
				moduloBlock.append("\"/>");
			}
			if (sizeCsub != 1) {
				moduloBlock.append("<ModuloAlongC Type=\"other\" TypeDescription=\"Example Data Over Channel\" Start=\"0\" Step=\"1\" End=\"");
				moduloBlock.append(sizeCsub);
				moduloBlock.append("\"/>");
			}
			moduloBlock.append("</Modulo>");

			meta.setXMLAnnotationValue(moduloBlock.toString(), 0);

			meta.setImageAnnotationRef("Annotation:Modulo:0", 0, 0);
		}
		return meta;
	}


	private BufferedImage createPlane(final String name, final CoreMetadata info, final int no, StringBuilder planeFilenameBuilder)
	{
		final int[] zct =
				FormatTools.getZCTCoords(info.dimensionOrder, info.sizeZ, info.sizeC,
						info.sizeT, info.imageCount, no);

		final BufferedImage plane =
				new BufferedImage(info.sizeX, info.sizeY, BufferedImage.TYPE_BYTE_GRAY);
		final Graphics2D g = plane.createGraphics();

		// draw gradient
		final int type = 0;
		for (int y = 0; y < info.sizeY; y++) {
			final int v = gradient(type, y, info.sizeY);
			g.setColor(new Color(v, v, v));
			g.drawLine(0, y, info.sizeX, y);
		}

		// build list of text lines from planar information
		final ArrayList<TextLine> lines = new ArrayList<TextLine>();
		final Font font = g.getFont();
		lines.add(new TextLine(name, font.deriveFont(32f), 5, -5));
		lines.add(new TextLine(info.sizeX + " x " + info.sizeY, font.deriveFont(
				Font.ITALIC, 16f), 20, 10));
		lines.add(new TextLine(info.dimensionOrder, font.deriveFont(Font.ITALIC,
				14f), 30, 5));
		int space = 5;
		
		String planeFilename = new String(name);
	    planeFilename = planeFilename.replaceAll(FormatTools.Z_NUM, String.valueOf(zct[0] + 1));
	    planeFilename = planeFilename.replaceAll(FormatTools.CHANNEL_NUM, String.valueOf(zct[1] + 1));
	    planeFilename = planeFilename.replaceAll(FormatTools.T_NUM, String.valueOf(zct[2] + 1));
		if (info.sizeZ > 1) {
			lines.add(new TextLine(
					"Focal plane = " + (zct[0] + 1) + "/" + info.sizeZ, font, 20, space));
			space = 2;
		}
		if (info.sizeC > 1) {
			lines.add(new TextLine("Channel = " + (zct[1] + 1) + "/" + info.sizeC,
					font, 20, space));
			space = 2;
		}
		if (info.sizeT > 1) {
			lines.add(new TextLine("Time point = " + (zct[2] + 1) + "/" + info.sizeT,
					font, 20, space));
			space = 2;
		}

		if (isModulo) {
			if (sizeZsub > 1) {
				lines.add(new TextLine("True-Z point = " + ((zct[0]/sizeZsub) + 1) + "/" + info.sizeZ/sizeZsub,
						font, 20, space));
				space = 2;
			}
			if (sizeZsub > 1) {
				lines.add(new TextLine("Sub-Z = " + ((zct[0]%sizeZsub) + 1) + "/" + sizeZsub,
						font, 20, space));
				space = 2;
			}
			if (sizeCsub > 1) {
				lines.add(new TextLine("True Channel = " + ((zct[1]/sizeCsub) + 1) + "/" + info.sizeC/sizeCsub,
						font, 20, space));
				space = 2;
			}
			if (sizeCsub > 1) {
				lines.add(new TextLine("Sub Channel = " + ((zct[1]%sizeCsub) + 1) + "/" + sizeCsub,
						font, 20, space));
				space = 2;
			}
			if (sizeTsub > 1) {
				lines.add(new TextLine("True-T point = " + ((zct[2]/sizeTsub) + 1) + "/" + info.sizeT/sizeTsub,
						font, 20, space));
				space = 2;
			}
			if (sizeTsub > 1) {
				lines.add(new TextLine("Sub-T = " + ((zct[2]%sizeTsub) + 1) + "/" + sizeTsub,
						font, 20, space));
				space = 2;
			}
		}
		// draw text lines to image
		g.setColor(Color.white);
		int yoff = 0;
		for (int l = 0; l < lines.size(); l++) {
			final TextLine text = lines.get(l);
			g.setFont(text.font);
			final Rectangle2D r =
					g.getFont().getStringBounds(text.line, g.getFontRenderContext());
			yoff += r.getHeight() + text.ypad;
			g.drawString(text.line, text.xoff, yoff);
		}
		g.dispose();

		// Fill the "out" parameter with the new filename
		planeFilenameBuilder.append(planeFilename);
		planeFilenameBuilder.append(OME_TIFF_EXTENSION);
		
		return plane;
	}

	private int gradient(final int type, final int num, final int total) {
		final int max = 96;
		final int split = type / 2 + 1;
		final boolean reverse = type % 2 == 0;
		int v = max;
		final int splitTotal = total / split;
		for (int i = 1; i <= split + 1; i++) {
			if (num < i * splitTotal) {
				if (i % 2 == 0) v = max * (num % splitTotal) / splitTotal;
				else v = max * (splitTotal - num % splitTotal) / splitTotal;
				break;
			}
		}
		if (reverse) v = max - v;
		return v;
	}

	// -- Helper classes --

	private static class TextLine {

		final String line;
		final Font font;
		final int xoff;
		final int ypad;

		TextLine(final String line, final Font font, final int xoff, final int ypad)
		{
			this.line = line;
			this.font = font;
			this.xoff = xoff;
			this.ypad = ypad;
		}

	}

	public static void makeSamples() throws FormatException, IOException {
		makeBinaryOmeTiff("single-channel", 439, 167, 1, 1, 1, "XYZCT");
		makeBinaryOmeTiff("multi-channel", 439, 167, 1, 3, 1, "XYZCT");
		makeBinaryOmeTiff("z-series", 439, 167, 5, 1, 1, "XYZCT");
		makeBinaryOmeTiff("multi-channel-z-series-single", 700, 167, 5, 3, 1, "XYZCT");
		makeBinaryOmeTiff("multi-channel-z-series-Z%z", 700, 167, 5, 3, 1, "XYZCT");
		makeBinaryOmeTiff("time-series", 439, 167, 1, 1, 7, "XYZCT");
		makeBinaryOmeTiff("multi-channel-time-series", 439, 167, 1, 3, 7, "XYZCT");
		makeBinaryOmeTiff("4D-series", 439, 167, 5, 1, 7, "XYZCT");
		makeBinaryOmeTiff("multi-channel-4D-series-single", 700, 167, 5, 3, 7, "XYZCT");
		makeBinaryOmeTiff("multi-channel-4D-series-Z%z-C%c-T%t", 700, 167, 5, 3, 7, "XYZCT");
	}
	
	private static void makeBinaryOmeTiff(String filename, int x,
			int y, int z, int c, int t,
			String order) throws FormatException, IOException {
		final String name = filename;
		final CoreMetadata info = new CoreMetadata();
		info.sizeX = x;
		info.sizeY = y;
		info.sizeZ = z;
		info.sizeC = c;
		info.sizeT = t;
		info.imageCount = info.sizeZ * info.sizeC * info.sizeT;
		info.dimensionOrder = order;

		new SampleBinaryOMETiff(name, info);
	}

/*
	info.moduloZ.start = 0;
	info.moduloZ.step = 1;
	info.moduloZ.end = 3;
	info.moduloC.start = 4;
	info.moduloC.step = 0.5;
	info.moduloC.end = 6;
	info.moduloT.start = 100;
	info.moduloT.step = 10;
	info.moduloT.end = 150;
*/
	
	public static void main(String[] args) throws Exception {
		makeSamples();
	}
}
