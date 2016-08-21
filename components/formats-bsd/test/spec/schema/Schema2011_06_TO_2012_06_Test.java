/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package spec.schema;

import java.io.InputStream;

import javax.xml.transform.Templates;
import javax.xml.transform.stream.StreamSource;

import loci.common.services.ServiceFactory;
import loci.common.xml.XMLTools;
import loci.formats.services.OMEXMLService;

import ome.xml.model.Image;
import ome.xml.model.OME;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * Collections of tests.
 * Checks if the upgrade from 2011-06 schema to 2012-06 schema works.
 *
 * @author Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Chris Allan &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:callan@lifesci.dundee.ac.uk">callan@lifesci.dundee.ac.uk</a>
 * @author Andrew Patterson &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:ajpatterson@lifesci.dundee.ac.uk">ajpatterson@lifesci.dundee.ac.uk</a>
 */
@Test(groups = { "all" })
public class Schema2011_06_TO_2012_06_Test {
    private static final Templates UPDATE_201106 =
            XMLTools.getStylesheet("/transforms/2011-06-to-2012-06.xsl",
            Schema2011_06_TO_2012_06_Test.class);

    private static final String RESOURCE =
            "/spec/schema/samples/2011-06/6x4y1z1t1c8b-swatch.ome";

    private static final String IMAGE_NAME = "6x6x1x8-swatch.tif";
    private static final String IMAGE_DATE = "2010-02-23T12:51:30";

    private OME ome;

    @BeforeClass
    public void setUp() throws Exception {
        InputStream source = this.getClass().getResourceAsStream(RESOURCE);
        ServiceFactory sf = new ServiceFactory();
        OMEXMLService service = sf.getInstance(OMEXMLService.class);
        String xml = XMLTools.transformXML(
                new StreamSource(source), UPDATE_201106);
        ome = (OME) service.createOMEXMLRoot(xml);
    }

    @Test
    public void testName() {
        Assert.assertNotNull(ome);
        Assert.assertEquals(1, ome.sizeOfImageList());
        Image image = ome.getImage(0);
        Assert.assertNotNull(image);
        Assert.assertEquals(IMAGE_NAME, image.getName());
    }

    @Test
    public void testDate() {
        Assert.assertNotNull(ome);
        Assert.assertEquals(1, ome.sizeOfImageList());
        Image image = ome.getImage(0);
        Assert.assertNotNull(image);
        Assert.assertEquals(IMAGE_DATE, image.getAcquisitionDate());
    }
}
