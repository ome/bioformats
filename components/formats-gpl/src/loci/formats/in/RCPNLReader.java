/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2019 Open Microscopy Environment:
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

package loci.formats.in;

import java.io.IOException;

import loci.formats.FormatException;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.units.UNITS;
import ome.units.quantity.Length;

public class RCPNLReader extends DeltavisionReader {

  public RCPNLReader() {
    super();
    format = "RCPNL";
    suffixes = new String[] {"rcpnl"};
    suffixNecessary = true;
    hasCompanionFiles = false;

    // if an *.rcpnl file is encountered, assume all timepoints are positions
    // the stage position values may not represent a uniform grid,
    // but should still be separate positions

    positionInT = true;
  }

  @Override
  public boolean isThisType(String name, boolean open) {
    return checkSuffix(name, "rcpnl") && super.isThisType(name, open);
  }

  @Override
  protected void populateObjective(MetadataStore store, int lensID)
    throws FormatException
  {
    super.populateObjective(store, lensID);

    switch (lensID) {
      case 18107:
        store.setObjectiveNominalMagnification(10.0, 0, 0);
        store.setObjectiveLensNA(0.30, 0, 0);
        store.setObjectiveWorkingDistance(
          new Length(15.0, UNITS.MILLIMETER), 0, 0);
        store.setObjectiveImmersion(MetadataTools.getImmersion("Air"), 0, 0);
        store.setObjectiveCorrection(
          MetadataTools.getCorrection("PlanFluor"), 0, 0);
        store.setObjectiveManufacturer("Nikon", 0, 0);
        break;
      case 18108:
        store.setObjectiveNominalMagnification(20.0, 0, 0);
        store.setObjectiveLensNA(0.75, 0, 0);
        store.setObjectiveCorrection(
          MetadataTools.getCorrection("PlanApo"), 0, 0);
        store.setObjectiveManufacturer("Nikon", 0, 0);
        break;
      case 18109:
        store.setObjectiveNominalMagnification(40.0, 0, 0);
        store.setObjectiveLensNA(0.95, 0, 0);
        store.setObjectiveCorrection(
          MetadataTools.getCorrection("PlanApo"), 0, 0);
        store.setObjectiveManufacturer("Nikon", 0, 0);
        break;
      case 18110:
        store.setObjectiveNominalMagnification(40.0, 0, 0);
        store.setObjectiveLensNA(0.60, 0, 0);
        store.setObjectiveCorrection(
          MetadataTools.getCorrection("PlanFluor"), 0, 0);
        store.setObjectiveManufacturer("Nikon", 0, 0);
        break;
      case 18111:
        store.setObjectiveNominalMagnification(4.0, 0, 0);
        store.setObjectiveLensNA(0.20, 0, 0);
        store.setObjectiveCorrection(
          MetadataTools.getCorrection("PlanApo"), 0, 0);
        store.setObjectiveManufacturer("Nikon", 0, 0);
        break;
    }
  }

}
