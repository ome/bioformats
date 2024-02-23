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

import loci.common.DateTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.in.LeicaMicrosystemsMetadata.LMSFileReader;
import loci.formats.in.LeicaMicrosystemsMetadata.LMSFileReader.ImageFormat;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.Tuple;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DimensionStore;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DimensionStore.ZDriveMode;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Dye;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Channel;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Dimension.DimensionKey;
import loci.formats.meta.MetadataStore;
import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.xml.model.primitives.Timestamp;

/**
 * DimensionWriter sets up CoreMetadata dimension parameters and related image parameters
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class DimensionWriter {
  
  public static void writeDimensions(CoreMetadata core, DimensionStore dimensionStore){
    setCoreDimensionSizes(core, dimensionStore);
    
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
  private static void setCoreDimensionSizes(CoreMetadata core, DimensionStore dimensionStore) {
    core.sizeX = dimensionStore.getDimension(DimensionKey.X).size;
    core.sizeY = dimensionStore.getDimension(DimensionKey.Y).size;
    core.sizeZ = dimensionStore.getDimension(DimensionKey.Z).size;
    core.sizeT = dimensionStore.getDimension(DimensionKey.T).size;

    if (core.rgb)
    dimensionStore.getDimension(DimensionKey.X).bytesInc /= 3;

    if (dimensionStore.extras > 1) {
      if (core.sizeZ == 1)
        core.sizeZ = dimensionStore.extras;
      else {
        if (core.sizeT == 0)
          core.sizeT = dimensionStore.extras;
        else
          core.sizeT *= dimensionStore.extras;
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

  public static void writeChannels(CoreMetadata core, MetadataStore store, DimensionStore dimensionStore, ImageFormat imageFormat, int seriesIndex){
    core.sizeC = dimensionStore.channels.size();
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
      String channelId = MetadataTools.createLSID("Channel", channelIndex);
      store.setChannelID(channelId, seriesIndex, channelIndex);
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

  public static void writeTimestamps(MetadataStore store, LMSFileReader reader, List<Double> timestamps, int seriesIndex){
    if (timestamps.size() == 0) return;

    double acquiredDate = timestamps.get(0);

    store.setImageAcquisitionDate(new Timestamp(DateTools.convertDate(
      (long) (acquiredDate * 1000), DateTools.COBOL,
      DateTools.ISO8601_FORMAT, false)), seriesIndex);

    for (int planeIndex = 0; planeIndex < reader.getImageCount(); planeIndex++){
      int t = reader.getZCTCoords(planeIndex)[2];
      if (t < timestamps.size()){
        double timestamp = timestamps.get(t);
        if (timestamps.get(0) == acquiredDate) {
          timestamp -= acquiredDate;
        } else if (timestamp == acquiredDate && t > 0) {
          timestamp = timestamps.get(0);
        }
  
        store.setPlaneDeltaT(new Time(timestamp, UNITS.SECOND), seriesIndex, planeIndex);
      }
    }
  }

  public static void writeExposureTimes(MetadataStore store, DimensionStore dimensionStore, LMSFileReader reader, int seriesIndex){
    for (int planeIndex = 0; planeIndex < reader.getImageCount(); planeIndex++){
      int channelIndex = reader.getZCTCoords(planeIndex)[1];
        store.setPlaneExposureTime(new Time(dimensionStore.channels.get(channelIndex).exposureTime, UNITS.SECOND), seriesIndex, planeIndex);
    }
  }

  /**
   * Writes field positions to reader's {@link MetadataStore}
   */
  public static void writeFieldPositions(MetadataStore store, DimensionStore dimensionStore, LMSFileReader reader, int seriesIndex) {
    //XY
    reader.addSeriesMeta("Reverse X orientation", dimensionStore.flipX);
    reader.addSeriesMeta("Reverse Y orientation", dimensionStore.flipY);
    reader.addSeriesMeta("Swap XY orientation", dimensionStore.swapXY);

    for (int tileIndex = 0; tileIndex < dimensionStore.fieldPositions.size(); tileIndex++){
      Tuple<Length,Length> fieldPosition = dimensionStore.fieldPositions.get(tileIndex);
      int nPlanesPerTile = dimensionStore.getNumberOfPlanesPerTile();
      for (int planeIndexWithinTile = 0; planeIndexWithinTile < nPlanesPerTile; planeIndexWithinTile++){
        int absolutePlaneIndex = tileIndex * nPlanesPerTile + planeIndexWithinTile;
        store.setPlanePositionX(fieldPosition.x, seriesIndex, absolutePlaneIndex);
        store.setPlanePositionY(fieldPosition.y, seriesIndex, absolutePlaneIndex);
      }
    }

    //Z
    for (int planeIndex = 0; planeIndex < reader.getImageCount(); planeIndex++){
      int sign = dimensionStore.zBegin <= dimensionStore.zEnd ? 1 : -1;
      int zIndex = reader.getZCTCoords(planeIndex)[0];
      double otherZDrivePos = dimensionStore.zDriveMode == ZDriveMode.ZGalvo ? dimensionStore.zWidePosition : dimensionStore.zGalvoPosition;
      Length zPos = FormatTools.createLength(otherZDrivePos + dimensionStore.zBegin + dimensionStore.zStep * sign * zIndex, UNITS.METER);
      store.setPlanePositionZ(zPos, seriesIndex, planeIndex);
    }
  }

  /**
   * Writes physical sizes to reader's {@link MetadataStore}
   */
  public static void writePhysicalSizes(MetadataStore store, DimensionStore dimensionStore, int seriesIndex){
    store.setPixelsPhysicalSizeX(FormatTools.getPhysicalSizeX(dimensionStore.physicalSizeX), seriesIndex);
    store.setPixelsPhysicalSizeY(FormatTools.getPhysicalSizeY(dimensionStore.physicalSizeY), seriesIndex);
    store.setPixelsPhysicalSizeZ(FormatTools.getPhysicalSizeZ(dimensionStore.zStep), seriesIndex);
    store.setPixelsTimeIncrement(new Time(dimensionStore.tStep, UNITS.SECOND), seriesIndex);
  }
}
