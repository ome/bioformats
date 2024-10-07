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
import loci.formats.in.LeicaMicrosystemsMetadata.model.Filter;
import loci.formats.meta.MetadataStore;

/**
 * WidefieldSettingsWriter writes widefield instrument and channel settings to the MetadataStore
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class WidefieldSettingsWriter {
  
  /**
   * Adds filters and filter sets to OME metadata store 
   */
  public static void initFilters(List<Filter> filters, int channelCount, int series, MetadataStore store) {
    for (int filterIndex = 0; filterIndex < filters.size(); filterIndex++) {
      Filter filter = filters.get(filterIndex);

      filter.id = MetadataTools.createLSID("Filter", series, filterIndex);
      store.setFilterID(filter.id, series, filterIndex);
      store.setFilterModel(filter.name, series, filterIndex);
      store.setTransmittanceRangeCutIn(FormatTools.getCutIn(filter.cutIn), series, filterIndex);
      store.setTransmittanceRangeCutOut(FormatTools.getCutOut(filter.cutOut), series, filterIndex);

      filter.filterSetId = MetadataTools.createLSID("FilterSet", series, filterIndex);
      store.setFilterSetID(filter.filterSetId, series, filterIndex);
      store.setFilterSetEmissionFilterRef(filter.id, series, filterIndex, filterIndex);
      store.setFilterSetModel(filter.name, series, filterIndex);

      //map widefield channel info to image channels by assuming same indices
      if (filterIndex < channelCount){
        store.setChannelName(filter.dye, series, filterIndex);
        store.setLightPathEmissionFilterRef(filter.id, series, filterIndex, 0);
      }
    }
  }
}
