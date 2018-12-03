/*
 * #%L
 * Bio-Formats command line tools for reading and converting files
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

package loci.formats.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import loci.common.Constants;
import loci.common.RandomAccessInputStream;
import loci.common.xml.XMLTools;
import loci.formats.in.TiffReader;
import loci.formats.tiff.TiffParser;

/**
 * Attempts to validate the given XML files.
 */
public class XMLValidate {

  public static boolean validate(BufferedReader in, String label)
    throws IOException
  {
    StringBuffer sb = new StringBuffer();
    try {
       while (true) {
        String line = in.readLine();
        if (line == null) break;
        sb.append(line);
      }
    } finally {
      in.close();
    }
    return XMLTools.validateXML(sb.toString(), label);
  }

  @Deprecated
  public static void process(String label, BufferedReader in)
    throws IOException
  {
      validate(in, label);
  }

  public static boolean validate(String file)
    throws IOException
  {
    String[] files = new String[1];
    files[0] = file;
    return validate(files)[0];
  }

  public static boolean[] validate(String[] files)
    throws IOException
  {
    if (files == null || files.length == 0) {
        throw new IllegalArgumentException("No files to validate");
    }
    boolean[] results = new boolean[files.length];
    List<String> extensions = Arrays.asList(TiffReader.TIFF_SUFFIXES);
    for (int i = 0; i < files.length; i++) {
        String file = files[i];
        if (file == null || file.trim().length() == 0) {
          results[i] = false;
        } else{
          String f = file.toLowerCase();
          boolean b;
          String extension = FilenameUtils.getExtension(f);
          if (extensions.contains(extension)) {
            String comment = "";
            try (RandomAccessInputStream stream = new RandomAccessInputStream(file)) {
              comment = new TiffParser(stream).getComment();
            }
            b = validate(new BufferedReader(new StringReader(comment)), f);
          } else {
            b = validate(new BufferedReader(new InputStreamReader(
                      new FileInputStream(f), Constants.ENCODING)), f);
          }
          results[i] = b;
        }
    }
    return results;
  }

  public static void main(String[] args) throws Exception {
    CommandLineTools.runUpgradeCheck(args);

    if (args.length == 0) {
      // read from stdin
      validate(new BufferedReader(
        new InputStreamReader(System.in, Constants.ENCODING)), "<stdin>");
    }
    else {
      // read from file(s)
      boolean[] results = validate(args);
      int count = 0;
      for (int i = 0; i < results.length; i++) {
        if (results[i]) {
          count++;
        }
      }
      //all files are valid
      if (count == results.length) {
        System.out.println("All files are valid");
        System.exit(0);
      } else {
        System.out.println((results.length-count)+" files are not valid");
        System.exit(1);
      }
    }
  }

}
