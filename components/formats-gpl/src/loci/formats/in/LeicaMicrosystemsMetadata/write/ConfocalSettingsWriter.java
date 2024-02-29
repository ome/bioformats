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

import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.in.LeicaMicrosystemsMetadata.model.ConfocalChannelSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Detector;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Laser;
import loci.formats.in.LeicaMicrosystemsMetadata.model.ConfocalAcquisitionSettings;
import loci.formats.meta.MetadataStore;
import ome.units.UNITS;
import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.xml.model.enums.DetectorType;
import ome.xml.model.enums.LaserMedium;
import ome.xml.model.enums.LaserType;
import ome.xml.model.primitives.PercentFraction;

/**
 * ConfocalSettingsWriter writes confocal instrument and channel settings to the MetadataStore
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class ConfocalSettingsWriter {

  public static void initConfocalInstrumentSettings(ConfocalAcquisitionSettings acquisitionSettings, int seriesIndex, MetadataStore store) {
    for (int i = 0; i < acquisitionSettings.detectors.size(); i++) {
      Detector detector = acquisitionSettings.detectors.get(i);
      detector.detectorId = MetadataTools.createLSID("Detector", seriesIndex, i);
      store.setDetectorID(detector.detectorId, seriesIndex, i);
      store.setDetectorModel(detector.model, seriesIndex, i);
      store.setDetectorZoom(acquisitionSettings.zoom, seriesIndex, i);
      store.setDetectorType(detector.type != null && detector.type.equals("PMT") ? DetectorType.PMT : DetectorType.OTHER, seriesIndex, i);
    }

    for (int i = 0; i < acquisitionSettings.lasers.size(); i++) {
      Laser laser = acquisitionSettings.lasers.get(i);
      laser.laserId = MetadataTools.createLSID("LightSource", seriesIndex, i);
      store.setLaserID(laser.laserId, seriesIndex, i);
      store.setLaserType(LaserType.OTHER, seriesIndex, i);
      store.setLaserLaserMedium(LaserMedium.OTHER, seriesIndex, i);
      store.setLaserModel(laser.name, seriesIndex, i);
      Length wavelength = FormatTools.getWavelength(laser.wavelength);
      store.setLaserWavelength(wavelength, seriesIndex, i);
    }
  }

  public static void initConfocalChannelSettings(ConfocalAcquisitionSettings acquisitionSettings, int seriesIndex, MetadataStore store) {
    for (int i = 0; i < acquisitionSettings.channelSettings.size(); i++){
      ConfocalChannelSetting channelSetting = acquisitionSettings.channelSettings.get(i);

      // Detector settings
      if (channelSetting.detectorSetting != null){
        store.setDetectorSettingsID(channelSetting.detectorSetting.detector.detectorId, seriesIndex, i);
        store.setDetectorSettingsOffset(channelSetting.detectorSetting.offset, seriesIndex, i);
        store.setDetectorSettingsGain(channelSetting.detectorSetting.gain, seriesIndex, i);
        store.setDetectorSettingsZoom(acquisitionSettings.zoom, seriesIndex, i);
        Frequency frequency = FormatTools.createFrequency(acquisitionSettings.readOutRate, UNITS.HERTZ);
        store.setDetectorSettingsReadOutRate(frequency, seriesIndex, i);
        store.setChannelName(channelSetting.detectorSetting.dyeName, seriesIndex, i);
      }

      // Filter settings
      String filterId = MetadataTools.createLSID("Filter", seriesIndex, i);
      store.setFilterID(filterId, seriesIndex, i);
      store.setFilterModel(channelSetting.detectorSetting.name, seriesIndex, i);
      store.setTransmittanceRangeCutIn(FormatTools.getCutIn(channelSetting.detectorSetting.cutIn), seriesIndex, i);
      store.setTransmittanceRangeCutOut(FormatTools.getCutOut(channelSetting.detectorSetting.cutOut), seriesIndex, i);

      String filterSetId = MetadataTools.createLSID("FilterSet", seriesIndex, i);
      store.setFilterSetID(filterSetId, seriesIndex, i);
      store.setFilterSetEmissionFilterRef(filterId, seriesIndex, i, i);
      store.setFilterSetModel(channelSetting.detectorSetting.name, seriesIndex, i);
      store.setLightPathEmissionFilterRef(filterId, seriesIndex, i, 0);

      // Laser / light source settings
      if (channelSetting.laserSetting != null){
        store.setChannelLightSourceSettingsID(channelSetting.laserSetting.laser.laserId, seriesIndex, i);
        PercentFraction attenuation = new PercentFraction((float) channelSetting.laserSetting.intensity / 100f);
        store.setChannelLightSourceSettingsAttenuation(attenuation, seriesIndex, i);
        store.setChannelExcitationWavelength(FormatTools.getWavelength(channelSetting.laserSetting.wavelength), seriesIndex, i);
      }

      store.setChannelPinholeSize(new Length(acquisitionSettings.pinholeSize, UNITS.MICROMETER), seriesIndex, i);
    }
  }
}
