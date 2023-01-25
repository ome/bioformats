package loci.formats.in.LeicaMicrosystemsMetadata.writeOME;

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

public class LaserWriter extends OMEWriter {
  
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

  public static void initLaserSettings(List<Channel> channels, int seriesIndex, MetadataStore store){
    for (int channelIndex = 0; channelIndex < channels.size(); channelIndex++){
      Channel channel = channels.get(channelIndex);
      if (channel.laserSetting != null) {
        store.setChannelLightSourceSettingsID(channel.laserSetting.laser.laserId, seriesIndex, channelIndex);
        store.setChannelLightSourceSettingsAttenuation(new PercentFraction((float) channel.laserSetting.intensity / 100f), seriesIndex, channelIndex);
        store.setChannelExcitationWavelength(FormatTools.getWavelength(channel.laserSetting.laser.wavelength), seriesIndex, channelIndex);
      }
    }
  }
}
