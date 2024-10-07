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

package loci.formats.in.LeicaMicrosystemsMetadata.extract.widefield;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import loci.formats.in.LeicaMicrosystemsMetadata.extract.Extractor;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Filter;

/**
 * WidefieldSettingsExtractor is a helper class for extracting widefield acquisition settings from LMS XML files.
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class WidefieldSettingsExtractor extends Extractor {

  /**
   * Returns a list of {@link Filter}s created from widefield channel information
   */
  public static List<Filter> extractWidefieldFilters(LMSMainXmlNodes xmlNodes){
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
