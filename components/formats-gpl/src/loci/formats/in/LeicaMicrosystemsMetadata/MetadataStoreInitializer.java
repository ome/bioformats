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
import loci.formats.in.LeicaMicrosystemsMetadata.model.Channel;
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
