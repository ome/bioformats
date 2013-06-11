/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2013 Open Microscopy Environment:
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package loci.formats.tools;

import loci.common.Location;

/**
 * Generates fake screen/plate/well structures. Maximum supported size is
 * a 384 well plate with multiple runs and fields. Methods defensively check
 * caller-supplied arguments and throw relevant exceptions where needed.
 * @author Blazej Pindelski, bpindelski at dundee.ac.uk
 * @since 5.0
 */
public class FakeScreenGenerator {

    private Location directoryRoot;

    public FakeScreenGenerator(String directoryRoot) {
        this.directoryRoot = new Location(directoryRoot);
        if (!this.directoryRoot.canWrite()) {
            throw new IllegalArgumentException("Cannot write to "
                    + this.directoryRoot.getAbsolutePath());
        }
    }

    public static void isValidValue(int arg) {
        if (arg < 1) {
            throw new IllegalArgumentException("Method argument value " +
                    "outside valid range.");
        }
    }

    /**
     * Creates a fake SPW file/directory structure. All arguments indicating
     * plate or well element count must be at least <code>1</code> and cannot be
     * <code>null</code>.
     * @param baseDir Directory, where structure will be generated.
     * @param plates Number of plates in a screen.
     * @param plateAcquisitions Number of plate acquisitions (runs) in a plate.
     * @param rows Number of rows in a plate.
     * @param columns Number of columns in a plate.
     * @param fields Number of fields for a plate acquisition.
     * @throws IllegalArgumentException when any of the arguments fail
     * validation.
     * @throws NullPointerException when null specified as argument value.
     */
    public void generateScreen(int plates, int plateAcquisitions, int rows,
            int columns, int fields) {
        isValidValue(plateAcquisitions);
        isValidValue(rows);
        isValidValue(columns);
        isValidValue(fields);
        //for ()
        // For each plate:
        //   create plate acquisitions
        // for each plate acquisition:
        //   create wells (rows * columns)
        //   create fields
        
    }

}
