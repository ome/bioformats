/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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
package loci.formats.utests;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import ch.systemsx.cisd.base.mdarray.MDIntArray;

import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
import loci.formats.services.JHDFService;
import org.testng.Assert;
import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Christop Sommer christoph.sommer (at) imba.oeaw.ac.at
 */
public class JHDFServiceTest {

    // The test file is 'test_jhdf.h5' from:

    private static final String TEST_FILE = "test_jhdf.h5";

    private JHDFService service;

    @BeforeMethod
    public void setUp() throws DependencyException, IOException {
        ServiceFactory sf = new ServiceFactory();
        service = sf.getInstance(JHDFService.class);
        URL file = JHDFServiceTest.class.getResource(TEST_FILE);
        service.setFile(file.getPath());
    }

    @Test
    public void testGetFile() {
        assertEquals(TEST_FILE, new File(service.getFile()).getName());
    }

    @Test
    public void testGetShape() {
        int[] shape = service.getShape("/member_1/member_3/int_matrix");
        assertEquals(shape.length, 3);
        assertEquals(shape[0], 10);
        assertEquals(shape[1], 9);
        assertEquals(shape[2], 8);
    }

    @Test
    public void testReadString() {
        String[] strings = service.readStringArray("/member_2/strings");
        for (int i = 0; i < strings.length; i++) {
            Assert.assertTrue(strings[i].equals(String.format("string_%d", i)));
        }

    }

    @Test
    public void testReadIntMatrix() {
        MDIntArray matrix = service.readIntArray("/member_1/member_3/int_matrix");
        assertEquals(matrix.get(2, 1, 0), 123);
    }

    @Test
    public void testMember() {
        List<String> member = service.getMember("/member_1/");
        assertEquals(member.get(0), "member_3");
    }

    @Test
    public void testReadIntBlockArray() {
        MDIntArray matrix = service.readIntBlockArray("/member_1/member_3/int_matrix",
                new int[]{0, 0, 0},
                new int[]{10, 1, 8});
        assertEquals(matrix.get(6, 0, 4), 178);
    }

}
