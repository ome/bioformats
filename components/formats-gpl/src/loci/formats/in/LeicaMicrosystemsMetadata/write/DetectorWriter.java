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

package loci.formats.in.LeicaMicrosystemsMetadata.writeOME;

import java.util.List;

import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Channel;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Detector;
import loci.formats.meta.MetadataStore;
import ome.units.UNITS;
import ome.units.quantity.Frequency;
import ome.xml.model.enums.DetectorType;

/**
 * DetectorWriter writes detector and detector settings information to the MetadataStore
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class DetectorWriter {

  /**
   * Adds detectors to OME metadata store
   */
  public static void initDetectors(List<Detector> detectors, int seriesIndex, MetadataStore store) {
    for (int i = 0; i < detectors.size(); i++) {
      Detector detector = detectors.get(i);
      detector.detectorId = MetadataTools.createLSID("Detector", seriesIndex, i);
      store.setDetectorID(detector.detectorId, seriesIndex, i);
      store.setDetectorModel(detector.model, seriesIndex, i);
      store.setDetectorZoom(detector.zoom, seriesIndex, i);
      store.setDetectorType(detector.type.equals("PMT") ? DetectorType.PMT : DetectorType.OTHER, seriesIndex, i);
    }
  }

  /**
   * Adds detector settings to OME metadata store
   */
  public static void initDetectorSettings(List<Channel> channels, int seriesIndex, MetadataStore store){
    for (int channelIndex = 0; channelIndex < channels.size(); channelIndex++){
      Channel channel = channels.get(channelIndex);

      if (channel.detectorSetting != null) {
        store.setDetectorSettingsID(channel.detectorSetting.detector.detectorId, seriesIndex, channelIndex);
        store.setDetectorSettingsOffset(channel.detectorSetting.offset, seriesIndex, channelIndex);
        store.setDetectorSettingsGain(channel.detectorSetting.gain, seriesIndex, channelIndex);
        store.setDetectorSettingsZoom(channel.detectorSetting.detector.zoom, seriesIndex, channelIndex);
        Frequency frequency = FormatTools.createFrequency(channel.detectorSetting.readOutRate, UNITS.HERTZ);
        store.setDetectorSettingsReadOutRate(frequency, seriesIndex, channelIndex);
      }
    }
  }
}
