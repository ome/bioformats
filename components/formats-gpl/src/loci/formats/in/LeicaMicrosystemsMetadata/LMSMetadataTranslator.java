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
import java.util.List;

import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.MetadataTools;
import loci.formats.in.LeicaMicrosystemsMetadata.doc.LMSImageXmlDocument;
import loci.formats.meta.MetadataStore;

/**
 * LMSMetadataTranslator sets up the whole metadata translation for all images of an LMSFileReader,
 * translating LMS image XML to the reader's CoreMetadata and MetadataStore, and mapping LMS image and
 * instrument metadata to OME metadata.
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class LMSMetadataTranslator {
  // -- Fields --
  private LMSFileReader r;
  MetadataStore store;

  // -- Constructor --
  public LMSMetadataTranslator(LMSFileReader reader) {
    this.r = reader;
    store = r.makeFilterMetadata();
  }

  public void translateMetadata(List<LMSImageXmlDocument> docs) throws FormatException, IOException {
    // extract metadata info
    for (int i = 0; i < docs.size(); i++) {
      SingleImageTranslator translator = new SingleImageTranslator(docs.get(i), i, docs.size(), r);
      r.metadataTranslators.add(translator);
      translator.extract();
    }

    // get series count (one per image tile)
    int seriesCount = 0;
    for (SingleImageTranslator translator : r.metadataTranslators){
      seriesCount += translator.dimensionStore.tileCount;
    }

    initCoreMetadata(seriesCount);

    int seriesIndex = 0;
    int translatorIndex = 0;
    while (seriesIndex < seriesCount){
      SingleImageTranslator translator = r.metadataTranslators.get(translatorIndex);
      for (int tileIndex = 0; tileIndex < translator.dimensionStore.tileCount; tileIndex++){
        String imageName = translator.imageDetails.originalImageName;
        if (translator.dimensionStore.tileCount > 1)
          imageName += " - tile " + (tileIndex + 1);

        translator.setTarget(seriesIndex + tileIndex, translator.imageDetails.collectionPrefix + imageName, tileIndex);
        translator.write();
      }

      seriesIndex += translator.dimensionStore.tileCount;
      translatorIndex++;
    }

    r.setSeries(0);

    MetadataTools.populatePixels(store, this.r, true, false);
  }

  private void initCoreMetadata(int len) {
    r.setCore(new ArrayList<CoreMetadata>(len));
    r.getCore().clear();

    for (int i = 0; i < len; i++) {
      CoreMetadata ms = new CoreMetadata();
      ms.orderCertain = true;
      ms.metadataComplete = true;
      ms.littleEndian = true;
      ms.falseColor = true;
      r.getCore().add(ms);
    }
  }
}
