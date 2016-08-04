/*
 * #%L
 * Tests for OME-XML specification classes.
 * %%
 * Copyright (C) 2010-2014 Glencoe Software, Inc.
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package spec.schema;

import ome.specification.SchemaResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.w3c.dom.ls.LSInput;

/** Tests for ome.specification.SchemaResolver. */
@Test(groups = { "all" })
public class SchemaResolverTest {

    /** List of published schema versions. */
    private static final String[][] SCHEMAS = {
      {"/XMLschemas/OME/FC/2003-FC"}, {"/Schemas/2007-06"},
      {"/Schemas/2008-02"}, {"/Schemas/2008-04"}, {"/Schemas/2008-09"},
      {"/Schemas/2009-09"}, {"/Schemas/2010-04"}, {"/Schemas/2010-06"},
      {"/Schemas/2011-06"}, {"/Schemas/2012-06"}, {"/Schemas/2012-06"},
      {"/Schemas/2013-06"}, {"/Schemas/2015-01"}};

    /** Holds the error, info, warning. */
    protected Logger log = LoggerFactory.getLogger(getClass());

    private SchemaResolver resolver = null;

    /**
     * Initializes the various services.
     * @throws Exception Thrown if an error occurred.
     */
    @BeforeClass
    protected void setUp() throws Exception {
      resolver = new SchemaResolver();
    }

    /**
     * Closes the session.
     * @throws Exception Thrown if an error occurred.
     */
    @AfterClass
    public void tearDown() throws Exception {
      resolver = null;
    }

    @DataProvider(name = "schemas")
    public Object[][] getSchemas() {
      return SCHEMAS;
    }

    @Test(dataProvider = "schemas")
    public void testResolution(String schema) {
      String schemaPath = "http://www.openmicroscopy.org" + schema + "/ome.xsd";
      LSInput resolvedSchema = resolver.resolveResource((String) null,
        (String) null, (String) null, schemaPath, (String) null);
      assert resolvedSchema != null;
    }

}
