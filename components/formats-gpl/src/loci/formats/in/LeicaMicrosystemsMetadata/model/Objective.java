package loci.formats.in.LeicaMicrosystemsMetadata.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Objective {
  public String model;
  public double numericalAperture;
  public String objectiveNumber;
  public double magnification;
  public String immersion;
  public double refractionIndex;
  public String correction;

  /**
   * Tries to find correction value in objective name string
   * @param objectiveName e.g. "HC PL APO    20x/0.70 DRY" or "HCX PL FLUOTAR    40x/0.75 DRY"
   *  or "HCX PL APO lambda blue  63.0x1.40 OIL  UV" or "HCX APO L  20.0x1.00 WATER "
   */
  public void setCorrectionFromObjectiveName(String objectiveName){
    String[] objValues = objectiveName.split(" +");

    List<String> corrections = new ArrayList<String>(
    Arrays.asList("PlanApo",
    "PlanFluor",
    "SuperFluor",
    "VioletCorrected", 	
    "Achro",
    "Achromat", 	
    "Fluor",	
    "Fl",
    "Fluar",
    "Neofluar",	
    "Fluotar", 	
    "Apo",
    "PlanNeofluar",
    "UV"));

    for (String value : objValues){
      for (String correction : corrections){
        if (correction.equalsIgnoreCase(value)){
          this.correction = value;
          return;
        }
      }
    }
  }

  /**
   * Tries to find immersion value in objective name string
   * @param objectiveName e.g. "HC PL APO    20x/0.70 DRY" or "HCX PL FLUOTAR    40x/0.75 DRY"
   *  or "HCX PL APO lambda blue  63.0x1.40 OIL  UV" or "HCX APO L  20.0x1.00 WATER "
   */
  public void setImmersionFromObjectiveName(String objectiveName){
    String[] objValues = objectiveName.split(" +");
    
    List<String> immersions = new ArrayList<String>(
      Arrays.asList("Oil",
      "Water",
      "WaterDipping",
      "Air",
      "Dry",
      "Multi",
      "Glycerol"));

      for (String value : objValues){
        for (String immersion : immersions){
          if (immersion.equalsIgnoreCase(value)){
            this.immersion = value;
            return;
          }
        }
      }
  }
}
