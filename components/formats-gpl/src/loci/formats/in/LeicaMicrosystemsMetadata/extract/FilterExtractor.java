package loci.formats.in.LeicaMicrosystemsMetadata.extract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import loci.common.DataTools;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DetectorSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Filter;

public class FilterExtractor extends Extractor {

  public static List<Filter> translateFilters(List<Element> sequentialConfocalSettings, List<DetectorSetting> detectorSettings) {
    List<Filter> filters = new ArrayList<Filter>();

    // add multiband cutin/out information to channel as filter
    int sequenceIndex = 0;
    for (int i = 0; i < sequentialConfocalSettings.size(); i++) {
      Node spectro = getChildNodeWithName(sequentialConfocalSettings.get(i), "Spectro");
      if (spectro == null)
        continue;

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
      sequenceIndex++;
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
}
