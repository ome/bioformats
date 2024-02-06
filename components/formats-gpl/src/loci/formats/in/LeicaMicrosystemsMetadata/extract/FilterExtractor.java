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

package loci.formats.in.LeicaMicrosystemsMetadata.extract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import loci.common.DataTools;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes.DataSourceType;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DetectorSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Filter;

/**
 * FilterExtractor is a helper class for extracting filter information from LMS XML files.
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class FilterExtractor extends Extractor {

  /**
   * Returns a list of {@link Filter}s for a confocal or widefield setup
   */
  public static List<Filter> translateFilters(LMSMainXmlNodes xmlNodes, List<DetectorSetting> detectorSettings){
    List<Filter> filters = new ArrayList<Filter>();
    
    if (xmlNodes.dataSourceType == DataSourceType.CONFOCAL){
      filters.addAll(translateConfocalFilters(xmlNodes, detectorSettings));
    } else if (xmlNodes.dataSourceType == DataSourceType.CAMERA){
      filters.addAll(translateWidefieldFilters(xmlNodes));
    }
    
    return filters;
  }

  /**
   * Returns a list of {@link Filter}s created from multiband info and detector settings
   */
  public static List<Filter> translateConfocalFilters(LMSMainXmlNodes xmlNodes, List<DetectorSetting> detectorSettings) {
    List<Filter> filters = new ArrayList<Filter>();

    if (xmlNodes.sequentialConfocalSettings.size() > 0){
       filters = FilterExtractor.translateConfocalFiltersFromSequentialSettings(xmlNodes.sequentialConfocalSettings, detectorSettings);

       //sometimes, sequential settings exist, but do not contain spectro elements... then we extract them from the main / master setting
       if (filters.size() == 0)
        filters = FilterExtractor.translateConfocalFiltersFromAtlSetting(xmlNodes.getAtlSetting(), detectorSettings);
    }
    else {
      filters = FilterExtractor.translateConfocalFiltersFromAtlSetting(xmlNodes.getAtlSetting(), detectorSettings);
    }

    return filters;
  }

  /**
   * Returns a list of {@link Filter}s created from multiband info and detector settings found in a single hardware setting (main, master or sequential),
   */
  private static List<Filter> translateConfocalFiltersFromHardwareSetting(Element confocalSetting, List<DetectorSetting> detectorSettings, int sequenceIndex) {
    List<Filter> filters = new ArrayList<Filter>();

    Node spectro = getChildNodeWithName(confocalSetting, "Spectro");
    if (spectro == null)
      return filters;

    NodeList multibands = spectro.getChildNodes();
    int multibandIndex = 0;
    for (int k = 0; k < multibands.getLength(); k++) {
      Element multiband;
      try {
        multiband = (Element) multibands.item(k);
      } catch (Exception e) {
        continue;
      }

      // assuming that multiband index maps detector index within a sequential setting,
      // only translate filter information for active detectors (= existing detector settings)
      DetectorSetting setting = getDetectorSetting(detectorSettings, sequenceIndex, multibandIndex);
      if (setting != null) {
        Filter filter = new Filter();
        filter.cutIn = DataTools.parseDouble(multiband.getAttribute("LeftWorld"));
        filter.cutOut = DataTools.parseDouble(multiband.getAttribute("RightWorld"));
        filter.dye = multiband.getAttribute("DyeName");
        filter.sequenceIndex = sequenceIndex;
        filter.multibandIndex = multibandIndex;

        filters.add(filter);
      }

      multibandIndex++;
    }

    return filters;
  }

  /**
   * Returns a list of {@link Filter}s created from multiband info and detector settings found in a single hardware setting (main or master),
   * if sequential information is not available
   */
  private static List<Filter> translateConfocalFiltersFromAtlSetting(Element atlSetting, List<DetectorSetting> detectorSettings) {
    // add multiband cutin/out information to channel as filter
    List<Filter> filters = translateConfocalFiltersFromHardwareSetting(atlSetting, detectorSettings, 0);

    // PMT transmission detectors don't have a matching multiband, so we
    // manually create a filter for them
    for (DetectorSetting setting : detectorSettings) {
      if (setting.transmittedLightMode) {
        Filter filter = new Filter();
        filter.sequenceIndex = setting.sequenceIndex;
        filter.multibandIndex = setting.detectorListIndex;
        filters.add(filter);
      }
    }

    Collections.sort(filters, new FilterComparator());
    
    return filters;
  }

  /**
   * Returns a list of {@link Filter}s created from sequential multiband info and detector settings 
   */
  private static List<Filter> translateConfocalFiltersFromSequentialSettings(List<Element> sequentialConfocalSettings, List<DetectorSetting> detectorSettings) {
    List<Filter> filters = new ArrayList<Filter>();

    // add multiband cutin/out information to channel as filter
    int sequenceIndex = 0;
    for (int i = 0; i < sequentialConfocalSettings.size(); i++) {
      List<Filter> seqFilters = translateConfocalFiltersFromHardwareSetting(sequentialConfocalSettings.get(i), detectorSettings, sequenceIndex);
      filters.addAll(seqFilters);
      // only count "non-empty" sequential hardware settings where filters were found
      if (seqFilters.size() > 0) sequenceIndex++;
    }

    // PMT transmission detectors don't have a matching multiband, so we
    // manually create a filter for them
    for (DetectorSetting setting : detectorSettings) {
      if (setting.transmittedLightMode) {
        Filter filter = new Filter();
        filter.sequenceIndex = setting.sequenceIndex;
        filter.multibandIndex = setting.detectorListIndex;
        filters.add(filter);
      }
    }

    Collections.sort(filters, new FilterComparator());
    
    return filters;
  }

  static class FilterComparator implements Comparator<Filter> {
    @Override
    public int compare(Filter a, Filter b) {
      if (a.sequenceIndex < b.sequenceIndex)
        return -1;
      else if (a.sequenceIndex > b.sequenceIndex)
        return 1;
      else {
        if (a.multibandIndex < b.multibandIndex)
          return -1;
        else if (a.multibandIndex > b.multibandIndex)
          return 1;
        else
          return 0;
      }
    }
  }

  private static DetectorSetting getDetectorSetting(List<DetectorSetting> detectorSettings, int sequenceIndex, int detectorListIndex) {
    for (DetectorSetting setting : detectorSettings) {
      if (setting.sequenceIndex == sequenceIndex && setting.detectorListIndex == detectorListIndex)
        return setting;
    }

    return null;
  }

  /**
   * Returns a list of {@link Filter}s created from widefield channel information
   */
  public static List<Filter> translateWidefieldFilters(LMSMainXmlNodes xmlNodes){
    List<Filter> filters = new ArrayList<Filter>();

    for (Element widefieldChannelInfo : xmlNodes.widefieldChannelInfos){
      Filter filter = new Filter();

      String wavelengthS = Extractor.getAttributeValue(widefieldChannelInfo, "EmissionWavelength");
      double waveLength = Extractor.parseDouble(wavelengthS);
      filter.cutIn = waveLength;
      filter.cutOut = waveLength;
      filter.dye = Extractor.getAttributeValue(widefieldChannelInfo, "UserDefName");
      filter.name = Extractor.getAttributeValue(widefieldChannelInfo, "FFW_Emission1FilterName");

      filters.add(filter);
    }

    return filters;
  }
}
