package loci.formats.in.LeicaMicrosystemsMetadata.writeOME;

import java.util.List;

import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.in.LeicaMicrosystemsMetadata.MetadataTempBuffer.DataSourceType;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Detector;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DetectorSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Filter;
import loci.formats.meta.MetadataStore;

public class FilterWriter {
  
  public static void initFilters(List<Filter> filters, List<DetectorSetting> detectorSettings, int series, MetadataStore store, DataSourceType dataSourceType) {
    for (int filterIndex = 0; filterIndex < filters.size(); filterIndex++) {
      Filter filter = filters.get(filterIndex);

      String filterId = MetadataTools.createLSID("Filter", series, filterIndex);
      store.setFilterID(filterId, series, filterIndex);

      filter.filterSetId = MetadataTools.createLSID("FilterSet", series, filterIndex);
      store.setFilterSetID(filter.filterSetId, series, filterIndex);
      store.setFilterSetEmissionFilterRef(filterId, series, filterIndex, filterIndex);

      // confocal: name of detector is used as filter model and filter set model
      if (dataSourceType == DataSourceType.CONFOCAL) {
        Detector detector = getDetectorForFilter(detectorSettings, filter);
        if (detector != null) {
          store.setFilterModel(detector.model, series, filterIndex);
          store.setFilterSetModel(detector.model, series, filterIndex);
        }
      }
      store.setTransmittanceRangeCutIn(FormatTools.getCutIn(filter.cutIn), series, filterIndex);
      store.setTransmittanceRangeCutOut(FormatTools.getCutOut(filter.cutOut), series, filterIndex);
    }
  }
  
  private static Detector getDetectorForFilter(List<DetectorSetting> detectorSettings, Filter filter) {
    for (DetectorSetting setting : detectorSettings) {
      if (setting.sequenceIndex == filter.sequenceIndex && setting.detectorListIndex == filter.multibandIndex)
        return setting.detector;
    }
    return null;
  }
}
