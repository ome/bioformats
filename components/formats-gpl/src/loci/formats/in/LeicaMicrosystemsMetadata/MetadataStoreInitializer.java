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

package loci.formats.in.LeicaMicrosystemsMetadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import loci.common.DateTools;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.in.LeicaMicrosystemsMetadata.MetadataTempBuffer.DataSourceType;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Channel;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Laser;
import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.xml.model.enums.LaserMedium;
import ome.xml.model.enums.LaserType;
import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.PercentFraction;
import ome.xml.model.primitives.Timestamp;
import loci.formats.meta.MetadataStore;

/**
 * This class initializes the {@link MetadataStore} of a given
 * {@link LMSFileReader}, using its {@link MetadataTempBuffer}
 * 
 * @author Melissa Linkert melissa at glencoesoftware.com
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class MetadataStoreInitializer {
  // -- Fields --
  private LMSFileReader r;
  MetadataStore store;
  private int nextChannel = 0;

  // -- Constructor --
  public MetadataStoreInitializer(LMSFileReader reader) {
    this.r = reader;
    store = r.makeFilterMetadata();
  }

  // -- Methods --

  /**
   * Adds laser and light source details from the reader's
   * {@link MetadataTempBuffer} to its {@link MetadataStore}
   * 
   * @param series
   *          index of image series / metadata store
   * @throws FormatException
   */
  public void initLasers2(int series) {
    int index = getTileIndex(series);

    final List<Double> lasers = r.metaTemp.laserWavelength.size() > index ? r.metaTemp.laserWavelength.get(index)
        : null;
    final List<Double> laserIntensities = r.metaTemp.laserIntensity.size() > index
        ? r.metaTemp.laserIntensity.get(index)
        : null;

    final List<Boolean> active = r.metaTemp.laserActive.size() > index ? r.metaTemp.laserActive.get(index) : null;
    final List<Boolean> frap = r.metaTemp.laserFrap.size() > index ? r.metaTemp.laserFrap.get(index) : null;

    if (lasers != null) {
      int laserIndex = 0;
      while (laserIndex < lasers.size()) {
        if ((Double) lasers.get(laserIndex) == 0) {
          lasers.remove(laserIndex);
        } else {
          laserIndex++;
        }
      }

      for (int laser = 0; laser < lasers.size(); laser++) {
        String id = MetadataTools.createLSID("LightSource", series, laser);
        store.setLaserID(id, series, laser);
        store.setLaserType(LaserType.OTHER, series, laser);
        store.setLaserLaserMedium(LaserMedium.OTHER, series, laser);
        Double wavelength = (Double) lasers.get(laser);
        Length wave = FormatTools.getWavelength(wavelength);
        if (wave != null) {
          store.setLaserWavelength(wave, series, laser);
        }
      }

      Set<Integer> ignoredChannels = new HashSet<Integer>();
      final List<Integer> validIntensities = new ArrayList<Integer>();
      int size = lasers.size();
      int channel = 0;
      Set<Integer> channels = new HashSet<Integer>();

      for (int laser = 0; laser < laserIntensities.size(); laser++) {
        double intensity = (Double) laserIntensities.get(laser);
        channel = laser / size;
        if (intensity < 100) {
          validIntensities.add(laser);
          channels.add(channel);
        }
        ignoredChannels.add(channel);
      }
      // remove channels w/o valid intensities
      ignoredChannels.removeAll(channels);
      // remove entries if channel has 2 wavelengths
      // e.g. 30% 458 70% 633
      int s = validIntensities.size();

      int jj;
      Set<Integer> toRemove = new HashSet<Integer>();

      int as = active.size();
      for (int j = 0; j < s; j++) {
        if (j < as && !(Boolean) active.get(j)) {
          toRemove.add(validIntensities.get(j));
        }
        jj = j + 1;
        if (jj < s) {
          int v = validIntensities.get(j) / size;
          int vv = validIntensities.get(jj) / size;
          if (vv == v) {// do not consider that channel.
            toRemove.add(validIntensities.get(j));
            toRemove.add(validIntensities.get(jj));
            ignoredChannels.add(j);
          }
        }
      }
      if (toRemove.size() > 0) {
        validIntensities.removeAll(toRemove);
      }

      boolean noNames = true;
      if (r.metaTemp.channelNames[index] != null) {
        for (String name : r.metaTemp.channelNames[index]) {
          if (name != null && !name.equals("")) {
            noNames = false;
            break;
          }
        }
      }
      if (!noNames && frap != null) { // only use name for frap.
        for (int k = 0; k < frap.size(); k++) {
          if (!frap.get(k)) {
            noNames = true;
            break;
          }
        }
      }

      int nextFilter = 0;
      // int nextFilter = cutIns[i].size() - getEffectiveSizeC();
      for (int k = 0; k < validIntensities.size(); k++, nextChannel++) {
        int laserArrayIndex = validIntensities.get(k);
        double intensity = (Double) laserIntensities.get(laserArrayIndex);
        int laser = laserArrayIndex % lasers.size();
        Double wavelength = (Double) lasers.get(laser);
        if (wavelength != 0) {
          while (ignoredChannels.contains(nextChannel)) {
            nextChannel++;
          }
          while (r.metaTemp.channelNames != null && nextChannel < r.getEffectiveSizeC() &&
              r.metaTemp.channelNames[index] != null &&
              ((r.metaTemp.channelNames[index][nextChannel] == null ||
                  r.metaTemp.channelNames[index][nextChannel].equals("")) && !noNames)) {
            nextChannel++;
          }
          if (nextChannel < r.getEffectiveSizeC()) {
            String id = MetadataTools.createLSID("LightSource", series, laser);
            store.setChannelLightSourceSettingsID(id, series, nextChannel);
            store.setChannelLightSourceSettingsAttenuation(
                new PercentFraction((float) intensity / 100f), series, nextChannel);

            Length ex = FormatTools.getExcitationWavelength(wavelength);
            if (ex != null) {
              store.setChannelExcitationWavelength(ex, series, nextChannel);
            }

            if (wavelength > 0) {
              if (r.metaTemp.cutIns.get(index) == null || nextFilter >= r.metaTemp.cutIns.get(index).size()) {
                continue;
              }
              Double cutIn = ((Length) r.metaTemp.cutIns.get(index).get(nextFilter)).value(UNITS.NANOMETER)
                  .doubleValue();
              while (cutIn - wavelength > 20) {
                nextFilter++;
                if (nextFilter < r.metaTemp.cutIns.get(index).size()) {
                  cutIn = ((Length) r.metaTemp.cutIns.get(index).get(nextFilter)).value(UNITS.NANOMETER).doubleValue();
                } else {
                  break;
                }
              }
              if (nextFilter < r.metaTemp.cutIns.get(index).size()) {
                // String fid = MetadataTools.createLSID("Filter", series, nextFilter);
                // store.setLightPathEmissionFilterRef(fid, index, nextChannel, 0);
                nextFilter++;
              }
            }
          }
        }
      }
    }
  }

  /**
   * Adds detector and channel color details from the reader's
   * {@link MetadataTempBuffer} to its {@link MetadataStore}
   * 
   * @param series
   *          index of image series / metadata store
   * @throws FormatException
   */
  public void initDetectorModels2(int series) {
    int index = getTileIndex(series);

    // final List<String> detectors = r.metaTemp.detectorModels.size() > index ?
    // r.metaTemp.detectorModels.get(index) : null;
    // if (detectors != null) {
    // nextChannel = 0;
    // int start = detectors.size() - r.getEffectiveSizeC();
    // if (start < 0) {
    // start = 0;
    // }
    // for (int detector=start; detector<detectors.size(); detector++) {
    // int dIndex = detector - start;
    // String detectorID = MetadataTools.createLSID("Detector", series, dIndex);
    // store.setDetectorID(detectorID, series, dIndex);
    // store.setDetectorModel((String) detectors.get(detector), series, dIndex);

    // store.setDetectorZoom(r.metaTemp.zooms[index], series, dIndex);
    // store.setDetectorType(DetectorType.PMT, series, dIndex);

    // if (r.metaTemp.activeDetector.get(index) != null) {
    // int detectorIndex =
    // r.metaTemp.activeDetector.get(index).size() - r.getEffectiveSizeC() + dIndex;
    // if (detectorIndex >= 0 &&
    // detectorIndex < r.metaTemp.activeDetector.get(index).size() &&
    // (Boolean) r.metaTemp.activeDetector.get(index).get(detectorIndex) &&
    // r.metaTemp.detectorOffsets[index] != null &&
    // nextChannel < r.metaTemp.detectorOffsets[index].length)
    // {
    // store.setDetectorOffset(
    // r.metaTemp.detectorOffsets[index][nextChannel++], series, dIndex);
    // }
    // }
    // }
    // }

    final List<Boolean> activeDetectors = r.metaTemp.activeDetector.size() > index
        ? r.metaTemp.activeDetector.get(index)
        : null;
    int firstDetector = activeDetectors == null ? 0 : activeDetectors.size() - r.getEffectiveSizeC();
    int nextDetector = firstDetector;

    int nextFilter = 0;
    int nextFilterDetector = 0;

    if (activeDetectors != null &&
        activeDetectors.size() > r.metaTemp.cutIns.get(index).size() &&
        (Boolean) activeDetectors.get(activeDetectors.size() - 1) &&
        (Boolean) activeDetectors.get(activeDetectors.size() - 2)) {
      nextFilterDetector = activeDetectors.size() - r.metaTemp.cutIns.get(index).size();

      if (r.metaTemp.cutIns.get(index).size() > r.metaTemp.filterModels.get(index).size()) {
        nextFilterDetector += r.metaTemp.filterModels.get(index).size();
        nextFilter += r.metaTemp.filterModels.get(index).size();
      }
    }

    for (int c = 0; c < r.getEffectiveSizeC(); c++) {
      // if (activeDetectors != null) {
      // while (nextDetector >= 0 && nextDetector < activeDetectors.size() &&
      // !(Boolean) activeDetectors.get(nextDetector))
      // {
      // nextDetector++;
      // }
      // if (nextDetector < activeDetectors.size() && detectors != null &&
      // nextDetector - firstDetector < detectors.size())
      // {
      // String detectorID = MetadataTools.createLSID(
      // "Detector", series, nextDetector - firstDetector);
      // store.setDetectorSettingsID(detectorID, series, c);
      // nextDetector++;

      // if (r.metaTemp.detectorOffsets[index] != null &&
      // c < r.metaTemp.detectorOffsets[index].length)
      // {
      // store.setDetectorSettingsOffset(r.metaTemp.detectorOffsets[index][c], series,
      // c);
      // }

      // if (r.metaTemp.gains[index] != null) {
      // store.setDetectorSettingsGain(r.metaTemp.gains[index][c], series, c);
      // }
      // }
      // }

      if (r.metaTemp.channelNames[index] != null) {
        store.setChannelName(r.metaTemp.channelNames[index][c], series, c);
      }
      if (r.metaTemp.pinholes[index] != null) {
        store.setChannelPinholeSize(new Length(r.metaTemp.pinholes[index], UNITS.MICROMETER), series, c);
      }
      if (r.metaTemp.exWaves[index] != null) {
        if (r.metaTemp.exWaves[index][c] != null && r.metaTemp.exWaves[index][c] > 1) {
          Length ex = FormatTools.getExcitationWavelength(r.metaTemp.exWaves[index][c]);
          if (ex != null) {
            store.setChannelExcitationWavelength(ex, series, c);
          }
        }
      }
      // channel coloring is implicit if the image is stored as RGB
      Color channelColor = r.metaTemp.channelColors.get(index).get(c);
      if (!r.isRGB()) {
        store.setChannelColor(channelColor, series, c);
      }

      if (channelColor.getValue() != -1 && nextFilter >= 0) {
        if (nextDetector - firstDetector != r.getSizeC() &&
            r.metaTemp.cutIns.get(index) != null && nextDetector >= r.metaTemp.cutIns.get(index).size()) {
          while (nextFilterDetector < firstDetector) {
            String filterID = MetadataTools.createLSID("Filter", series, nextFilter);
            store.setFilterID(filterID, series, nextFilter);

            nextFilterDetector++;
            nextFilter++;
          }
        }
        while (activeDetectors != null &&
            nextFilterDetector < activeDetectors.size() &&
            !(Boolean) activeDetectors.get(nextFilterDetector)) {
          String filterID = MetadataTools.createLSID("Filter", series, nextFilter);
          store.setFilterID(filterID, series, nextFilter);
          nextFilterDetector++;
          nextFilter++;
        }
        String filterID = MetadataTools.createLSID("Filter", series, nextFilter);
        store.setFilterID(filterID, series, nextFilter);
        store.setLightPathEmissionFilterRef(filterID, series, c, 0);
        nextFilterDetector++;
        nextFilter++;
      }
    }
  }

  /**
   * Adds image and ROI details from the reader's {@link MetadataTempBuffer} to
   * its {@link MetadataStore}
   * 
   * @param series
   *          index of image series / metadata store
   * @throws FormatException
   */
  public void initImageDetails(int series) {
    int index = getTileIndex(series);

    store.setImageDescription(r.metaTemp.descriptions[index], series);
    if (r.metaTemp.acquiredDate[index] > 0) {
      store.setImageAcquisitionDate(new Timestamp(DateTools.convertDate(
          (long) (r.metaTemp.acquiredDate[index] * 1000), DateTools.COBOL,
          DateTools.ISO8601_FORMAT, false)), series);
    }
    store.setImageName(r.metaTemp.imageNames[index].trim(), series);

    Length sizeX = FormatTools.getPhysicalSizeX(r.metaTemp.physicalSizeXs.get(index));
    Length sizeY = FormatTools.getPhysicalSizeY(r.metaTemp.physicalSizeYs.get(index));
    Length sizeZ = FormatTools.getPhysicalSizeZ(r.metaTemp.zSteps[index]);

    if (sizeX != null) {
      store.setPixelsPhysicalSizeX(sizeX, series);
    }
    if (sizeY != null) {
      store.setPixelsPhysicalSizeY(sizeY, series);
    }
    if (sizeZ != null) {
      store.setPixelsPhysicalSizeZ(sizeZ, series);
    }
    if (r.metaTemp.tSteps[index] != null) {
      store.setPixelsTimeIncrement(new Time(r.metaTemp.tSteps[index], UNITS.SECOND), series);
    }

    int roiCount = 0;
    for (int image = 0; image < r.getImageCount(); image++) {
      Length xPos = r.metaTemp.posX[index];
      Length yPos = r.metaTemp.posY[index];
      if (series < r.metaTemp.fieldPosX.size() && r.metaTemp.fieldPosX.get(series) != null) {
        xPos = r.metaTemp.fieldPosX.get(series);
      }
      if (series < r.metaTemp.fieldPosY.size() && r.metaTemp.fieldPosY.get(series) != null) {
        yPos = r.metaTemp.fieldPosY.get(series);
      }

      if (r.metaTemp.swapXY[index]) {
        Length temp = xPos;
        xPos = yPos;
        yPos = temp;
      }

      xPos = checkFlip(r.metaTemp.flipX[index], xPos);
      yPos = checkFlip(r.metaTemp.flipY[index], yPos);

      if (xPos != null) {
        store.setPlanePositionX(xPos, series, image);
      }
      if (yPos != null) {
        store.setPlanePositionY(yPos, series, image);
      }
      store.setPlanePositionZ(r.metaTemp.posZ[index], series, image);
      if (r.metaTemp.timestamps[index] != null) {
        if (r.metaTemp.timestamps[index][image] != null) {
          double timestamp = r.metaTemp.timestamps[index][image];
          if (r.metaTemp.timestamps[index][0] == r.metaTemp.acquiredDate[index]) {
            timestamp -= r.metaTemp.acquiredDate[index];
          } else if (timestamp == r.metaTemp.acquiredDate[index] && image > 0) {
            timestamp = r.metaTemp.timestamps[index][0];
          }
          store.setPlaneDeltaT(new Time(timestamp, UNITS.SECOND), series, image);
        }
      }

      if (r.metaTemp.expTimes[index] != null) {
        int c = r.getZCTCoords(image)[1];
        if (r.metaTemp.expTimes[index][c] != null) {
          store.setPlaneExposureTime(new Time(r.metaTemp.expTimes[index][c], UNITS.SECOND), series, image);
        }
      }
    }

    if (r.metaTemp.imageROIs[index] != null) {
      for (int roi = 0; roi < r.metaTemp.imageROIs[index].length; roi++) {
        if (r.metaTemp.imageROIs[index][roi] != null) {
          r.metaTemp.imageROIs[index][roi].storeROI(store, series, roiCount++, roi,
              r.getCore().get(series).sizeX, r.getCore().get(series).sizeY, r.metaTemp.alternateCenter,
              r.getMetadataOptions().getMetadataLevel());
        }
      }
    }
  }

  /**
   * Translates metadata information from the reader's {@link MetadataTempBuffer}
   * and adds it to its {@link MetadataStore}
   * 
   * @throws FormatException
   * @throws IOException
   */
  public void initMetadataStore() throws FormatException, IOException {
    MetadataTools.populatePixels(store, this.r, true, false);

    for (int i = 0; i < r.getSeriesCount(); i++) {
      r.setSeries(i);
      // initStandDetails(i);
      // initFilters(i);
      // initLasers(i);
      // initDetectors(i);
      initImageDetails(i);
      initPhysicalChannelInfo(i);
    }
  }

  /**
   * Adds lightsource, filter set and detector settings to channels in the
   * {@link MetadataStore}
   * 
   * @param series
   */
  private void initPhysicalChannelInfo(int series) {
    for (int channelIndex = 0; channelIndex < r.metaTemp.channels.get(series).size(); channelIndex++) {
      Channel channel = r.metaTemp.channels.get(series).get(channelIndex);

      
    }
  }

  public int getTileIndex(int coreIndex) {
    int count = 0;
    for (int tile = 0; tile < r.metaTemp.tileCount.length; tile++) {
      if (coreIndex < count + r.metaTemp.tileCount[tile]) {
        return tile;
      }
      count += r.metaTemp.tileCount[tile];
    }
    return -1;
  }

  // -- Helper functions --

  public Length checkFlip(boolean flip, Length pos) {
    if (flip && pos != null) {
      pos = new Length(-pos.value().doubleValue(), pos.unit());
    }
    return pos;
  }
}
