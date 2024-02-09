/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

package loci.formats.in.LeicaMicrosystemsMetadata.write;

import java.util.List;

import loci.formats.CoreMetadata;
import loci.formats.FormatTools;
import loci.formats.in.LeicaMicrosystemsMetadata.LMSFileReader.ImageFormat;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DimensionStore;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Dye;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Channel;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Dimension.DimensionKey;
import loci.formats.meta.MetadataStore;
import ome.units.quantity.Length;

/**
 * DimensionWriter sets up CoreMetadata dimension parameters and related image parameters
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class DimensionWriter {
  
  public static void setupCoreDimensionParameters(CoreMetadata core, DimensionStore dimensionStore, int extras){
    setCoreDimensionSizes(core, dimensionStore, extras);
    
    core.imageCount = core.sizeZ * core.sizeT;
    if (!core.rgb)
      core.imageCount *= core.sizeC;
    else {
      core.imageCount *= (core.sizeC / 3);
    }

    core.dimensionOrder = dimensionStore.getDimensionOrder();
  }

  /**
   * Writes extracted dimension sizes to CoreMetadata
   */
  private static void setCoreDimensionSizes(CoreMetadata core, DimensionStore dimensionStore, int extras) {
    core.sizeX = dimensionStore.getDimension(DimensionKey.X).size;
    core.sizeY = dimensionStore.getDimension(DimensionKey.Y).size;
    core.sizeZ = dimensionStore.getDimension(DimensionKey.Z).size;
    core.sizeT = dimensionStore.getDimension(DimensionKey.T).size;

    if (core.rgb)
    dimensionStore.getDimension(DimensionKey.X).bytesInc /= 3;

    if (extras > 1) {
      if (core.sizeZ == 1)
        core.sizeZ = extras;
      else {
        if (core.sizeT == 0)
          core.sizeT = extras;
        else
          core.sizeT *= extras;
      }
    }

    if (core.sizeX == 0)
      core.sizeX = 1;
    if (core.sizeY == 0)
      core.sizeY = 1;
    if (core.sizeC == 0)
      core.sizeC = 1;
    if (core.sizeZ == 0)
      core.sizeZ = 1;
    if (core.sizeT == 0)
      core.sizeT = 1;
  }

  public static void setChannels(CoreMetadata core, MetadataStore store, DimensionStore dimensionStore, ImageFormat imageFormat, int seriesIndex){
    core.rgb = dimensionStore.rgb;
    //assuming that all channels of one image have the same resolution
    core.bitsPerPixel = dimensionStore.channels.get(0).resolution;

    int bytesPerPixel = core.bitsPerPixel > 8 ? 2 : 1;
    try {
      core.pixelType = FormatTools.pixelTypeFromBytes(bytesPerPixel, false, true);
    } catch (Exception e){
      System.out.println("Could not render pixel type from channel resolution (in bytes): " + bytesPerPixel);
      e.printStackTrace();
    }

    for (int channelIndex = 0; channelIndex < dimensionStore.channels.size(); channelIndex++){
      Channel channel = dimensionStore.channels.get(channelIndex);
      store.setChannelName(channel.channelName, seriesIndex, channelIndex);
      if (!dimensionStore.rgb){
        store.setChannelColor(channel.lutColor, seriesIndex, channelIndex);
      }
    }

    // TIFF and JPEG files not interleaved
    if (imageFormat == ImageFormat.TIF || imageFormat == ImageFormat.JPEG) {
      core.interleaved = false;
    } else {
      core.interleaved = core.rgb;
    }

    core.indexed = !core.rgb;
  }

  public static void addDyeInfosToChannels(MetadataStore store, List<Dye> dyes, DimensionStore dimensionStore, int seriesIndex){
    for (int channelIndex = 0; channelIndex < dimensionStore.channels.size(); channelIndex++){
      Channel channel = dimensionStore.channels.get(channelIndex);
      for (Dye dye : dyes){
        if (dye.lutName.equals(channel.lutName)){
          Length emissionWavelength = FormatTools.getWavelength(dye.emissionWavelength);
          store.setChannelEmissionWavelength(emissionWavelength, seriesIndex, channelIndex);
          Length excitationWavelength = FormatTools.getWavelength(dye.excitationWavelength);
          store.setChannelExcitationWavelength(excitationWavelength, seriesIndex, channelIndex);
          store.setChannelFluor(dye.fluochromeName, seriesIndex, channelIndex);
          break;
        }
      }
    }
  }
}
