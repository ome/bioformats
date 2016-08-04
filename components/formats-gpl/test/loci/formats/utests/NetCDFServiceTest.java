/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

package loci.formats.utests;

import static org.testng.AssertJUnit.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
import loci.formats.services.NetCDFService;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author Chris Allan <callan at blackcat dot ca>
 */
public class NetCDFServiceTest {

  // The test file is 'C1979091.h5' from:
  // http://www.hdfgroup.org/training/other-ex5/sample-programs/convert/Conversion.html
  private static final String TEST_FILE = "test.h5";
  private static final String[][] ATTRIBUTES = new String[][] {
    {"/Product_Name_GLOSDS", "C19790911979120.L3_BRS_MO"},
    {"/Title_GLOSDS", "CZCS Level-3 Browse Data"},
    {"/Data_Center_GLOSDS", "NASA/GSFC DAAC"},
    {"/Mission_GLOSDS", "Nimbus CZCS"},
    {"/Mission_Characteristics_GLOSDS",
      "Nominal orbit: inclination = 99.3 (Sun-synchronous); node = 11:52 " +
      "a.m. local (ascending); eccentricity = <0.0009; altitude = 955 km; " +
      "ground speed = 6.4km/sec"},
    {"/Sensor_GLOSDS", "Coastal Zone Color Scanner (CZCS)"},
    {"/Sensor_Characteristics_GLOSDS",
      "Number of bands = 6; number of active bands = 6; wavelengths per " +
      "band (nm)= 443, 520, 550, 670, 750, 11500; bits per pixel = 8; " +
      "instantaneous field-of-view = .865 mrad; pixels per scan = 492; " +
      "scan rate = 8.08/sec; sample rate =3975/sec"},
    {"/Replacement_Flag_GLOSDS", "ORIGINAL"},
    {"/Software_ID_GLOSDS", "L3bin2hdf.c_v.1"},
    {"/Processing_Time_GLOSDS", "1995200120000000"},
    {"/Input_Files_GLOSDS", "c79120.Chlor.bin.mean"},
    {"/Processing_Control_GLOSDS", "L3bin2hdf L3.info L3.input"},
    {"/Processing_Log_GLOSDS", "L3bin2hdf_history"},
    {"/Parent_Input_Files_GLOSDS", "c79120.Chlor.bin.mean"},
    {"/Product_Type_GLOSDS", "month"},
    {"/Station_Name_GLOSDS", "Wallops Flight Facility"},
    {"/Station_Latitude_GLOSDS", "37.9272"},
    {"/Station_Longitude_GLOSDS", "-75.4753"},
    {"/Data_Type_GLOSDS", "GAC"},
    {"/Parent_Number_of_Lines_GLOSDS", "1024"},
    {"/Parent_Number_of_Columns_GLOSDS", "2048"},
    {"/Period_Start_Year_GLOSDS", "1979"},
    {"/Period_Start_Day_GLOSDS", "91"},
    {"/Period_End_Year_GLOSDS", "1979"},
    {"/Period_End_Day_GLOSDS", "120"},
    {"/Start_Time_GLOSDS", "1979091000000000"},
    {"/End_Time_GLOSDS", "1979120235959999"},
    {"/Start_Year_GLOSDS", "1979"},
    {"/Start_Day_GLOSDS", "91"},
    {"/Start_Millisec_GLOSDS", "0"},
    {"/End_Year_GLOSDS", "1979"},
    {"/End_Day_GLOSDS", "120"},
    {"/End_Millisec_GLOSDS", "86400000"},
    {"/Map_Projection_GLOSDS", "Equidistant Cylindrical"},
    {"/Latitude_Units_GLOSDS", "degrees North"},
    {"/Longitude_Units_GLOSDS", "degrees East"},
    {"/Northernmost_Latitude_GLOSDS", "90.0"},
    {"/Southernmost_Latitude_GLOSDS", "-90.0"},
    {"/Westernmost_Longitude_GLOSDS", "-180.0"},
    {"/Easternmost_Longitude_GLOSDS", "180.0"},
    {"/Parameter_GLOSDS", "Chlorophyll a concentration"},
    {"/Measure_GLOSDS", "Mean"},
    {"/Units_GLOSDS", "mg m^-3"},
    {"/Start_Column_GLOSDS", "1"},
    {"/Column_Subsampling_Rate_GLOSDS", "6"},
    {"/Number_of_Columns_GLOSDS", "360"},
    {"/Start_Line_GLOSDS", "1"},
    {"/Line_Subsampling_Rate_GLOSDS", "6"},
    {"/Number_of_Lines_GLOSDS", "180"},
    {"/Scaling_GLOSDS", "logarithmic"},
    {"/Scaling_Equation_GLOSDS",
      "Base**((Slope*brs_data) + Intercept) = chlorophyll a"},
    {"/Base_GLOSDS", "10.0"},
    {"/Slope_GLOSDS", "0.012"},
    {"/Intercept_GLOSDS", "-1.4"},
    {"/HDF4_FILE_LABEL_0", "CZCS Level 3 GAC Browse Data for April, 1979"}
  };

  private static final String[][][] VARS = new String[][][] {
    {
    {"CLASS", "PALETTE"},
    {"HDF4_OBJECT_TYPE", "palette"},
    {"HDF4_REF_NUM", "2"},
    {"PAL_COLORMODEL", "RGB"},
    {"PAL_TYPE", "STANDARD8"},
    {"PAL_VERSION", "1.2"},
    {"_Unsigned", "true"},
    },
    {
    {"CLASS", "IMAGE"},
    {"HDF4_IMAGE_LABEL_0", "brs_data"},
    {"HDF4_OBJECT_NAME", "Raster Image #0"},
    {"HDF4_OBJECT_TYPE", "raster8"},
    {"HDF4_PALETTE_LIST", "/HDF4_PALGROUP/HDF4_PALETTE_2"},
    {"HDF4_REF_NUM", "2"},
    {"IMAGE_SUBCLASS", "IMAGE_INDEXED"},
    {"IMAGE_VERSION", "1.2"},
    {"PALETTE", "1680"},
    {"_Unsigned", "true"},
    }
  };

  private NetCDFService service;

  @BeforeMethod
  public void setUp() throws DependencyException, IOException {
    ServiceFactory sf = new ServiceFactory();
    service = sf.getInstance(NetCDFService.class);
    URL file = NetCDFServiceTest.class.getResource(TEST_FILE);
    service.setFile(file.getPath());
  }

  @Test
  public void testGetFile() {
    assertEquals(TEST_FILE, new File(service.getFile()).getName());
  }

  @Test
  public void testAttributes() {
    Vector<String> attributes = service.getAttributeList();
    assertEquals(attributes.size(), ATTRIBUTES.length);
    for (int i=0; i<attributes.size(); i++) {
      String attribute = attributes.get(i);
      assertEquals(attribute, ATTRIBUTES[i][0]);
      assertEquals(service.getAttributeValue(attribute), ATTRIBUTES[i][1]);
    }
  }

  @Test
  public void testVariables() {
    Vector<String> variables = service.getVariableList();
    assertEquals(variables.size(), 2);

    assertEquals(variables.get(0), "/Raster_Image_#0");
    assertEquals(variables.get(1), "/HDF4_PALGROUP/HDF4_PALETTE_2");

    Hashtable<String, Object> var1 =
      service.getVariableAttributes("/HDF4_PALGROUP/HDF4_PALETTE_2");
    Hashtable<String, Object> var2 =
      service.getVariableAttributes("/Raster_Image_#0");

    assertEquals(var1.size(), VARS[0].length);
    assertEquals(var2.size(), VARS[1].length);

    String[] keys = var1.keySet().toArray(new String[var1.size()]);
    Arrays.sort(keys);
    for (int i=0; i<keys.length; i++) {
      assertEquals(keys[i], VARS[0][i][0]);
      assertEquals(var1.get(keys[i]), VARS[0][i][1]);
    }

    keys = var2.keySet().toArray(new String[var2.size()]);
    Arrays.sort(keys);
    for (int i=0; i<keys.length; i++) {
      assertEquals(keys[i], VARS[1][i][0]);
      assertEquals(var2.get(keys[i]), VARS[1][i][1]);
    }
  }

}
