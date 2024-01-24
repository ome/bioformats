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

import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Channel;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Laser;
import loci.formats.meta.MetadataStore;
import ome.units.quantity.Length;
import ome.xml.model.enums.LaserMedium;
import ome.xml.model.enums.LaserType;
import ome.xml.model.primitives.PercentFraction;

/**
 * DetectorWriter writes laser and laser settings information to the MetadataStore
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class LaserWriter {
  
  /**
   * Writes lasers to OME metadata store
   */
  public static void initLasers(List<Laser> lasers, int seriesIndex, MetadataStore store) {
    for (int i = 0; i < lasers.size(); i++) {
      Laser laser = lasers.get(i);
      laser.laserId = MetadataTools.createLSID("LightSource", seriesIndex, i);
      store.setLaserID(laser.laserId, seriesIndex, i);
      store.setLaserType(LaserType.OTHER, seriesIndex, i);
      store.setLaserLaserMedium(LaserMedium.OTHER, seriesIndex, i);
      store.setLaserModel(laser.name, seriesIndex, i);
      Length wavelength = FormatTools.getWavelength(laser.wavelength);
      store.setLaserWavelength(wavelength, seriesIndex, i);

      // store.setChannelLightSourceSettingsID(id, series, nextChannel);
      // 
      // store.setChannelExcitationWavelength(ex, series, nextChannel);
    }
  }

  /**
   * Writes laser settings to OME metadata store
   */
  public static void initLaserSettings(List<Channel> channels, int seriesIndex, MetadataStore store){
    for (int channelIndex = 0; channelIndex < channels.size(); channelIndex++){
      Channel channel = channels.get(channelIndex);
      if (channel.laserSetting != null) {
        store.setChannelLightSourceSettingsID(channel.laserSetting.laser.laserId, seriesIndex, channelIndex);
        PercentFraction attenuation = new PercentFraction((float) channel.laserSetting.intensity / 100f);
        store.setChannelLightSourceSettingsAttenuation(attenuation, seriesIndex, channelIndex);
        store.setChannelExcitationWavelength(FormatTools.getWavelength(channel.laserSetting.laser.wavelength), seriesIndex, channelIndex);
      }
    }
  }
}
