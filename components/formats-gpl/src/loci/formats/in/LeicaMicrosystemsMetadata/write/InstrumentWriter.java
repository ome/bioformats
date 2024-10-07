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

import loci.formats.MetadataTools;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes;
import loci.formats.in.LeicaMicrosystemsMetadata.model.MicroscopeDetails;
import loci.formats.meta.MetadataStore;

/**
 * InstrumentWriter writes microscope stand and objective details to the reader's {@link CoreMetadata} and {@MetadataStore}
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class InstrumentWriter {

  /**
   * Writes stand details to reader's [@link MetadataStore]
   */
  public static void writeMicroscopeDetails(MetadataStore store, LMSMainXmlNodes xmlNodes, MicroscopeDetails micDetails, int seriesIndex){
    String instrumentID = MetadataTools.createLSID("Instrument", seriesIndex);
    store.setInstrumentID(instrumentID, seriesIndex);

    store.setMicroscopeModel(micDetails.microscopeModel, seriesIndex);
    store.setMicroscopeType(micDetails.microscopeType, seriesIndex);
    store.setMicroscopeSerialNumber(micDetails.serialNumber, seriesIndex);
    store.setImageInstrumentRef(instrumentID, seriesIndex);

    if (micDetails.objective == null) return;
    
    String objectiveID = MetadataTools.createLSID("Objective", seriesIndex, 0);
    store.setObjectiveID(objectiveID, seriesIndex, 0);
    store.setObjectiveModel(micDetails.objective.model, seriesIndex, 0);
    store.setObjectiveLensNA(micDetails.objective.numericalAperture, seriesIndex, 0);
    store.setObjectiveSerialNumber(micDetails.objective.objectiveNumber, seriesIndex, 0);
    store.setObjectiveNominalMagnification(micDetails.objective.magnification, seriesIndex, 0);

    try {
      store.setObjectiveImmersion(MetadataTools.getImmersion(micDetails.objective.immersion), seriesIndex, 0);
    } catch (Exception e){
      System.out.println("Objective immersion could not be read.");
      e.printStackTrace();
    }

    try {
      store.setObjectiveCorrection(MetadataTools.getCorrection(micDetails.objective.correction), seriesIndex, 0);
    } catch (Exception e){
      System.out.println("Objective correction could not be read.");
      e.printStackTrace();
    }

    store.setObjectiveSettingsID(objectiveID, seriesIndex);
    store.setObjectiveSettingsRefractiveIndex(micDetails.objective.refractionIndex, seriesIndex);
  }
}
