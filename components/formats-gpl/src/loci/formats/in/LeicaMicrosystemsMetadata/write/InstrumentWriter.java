package loci.formats.in.LeicaMicrosystemsMetadata.write;

import loci.formats.MetadataTools;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes;
import loci.formats.in.LeicaMicrosystemsMetadata.model.MicroscopeDetails;
import loci.formats.meta.MetadataStore;

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
