/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2014 Open Microscopy Environment:
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

import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;

/**
 * Utility class for printing a list of scientific domains supported by
 * Bio-Formats, and all of the supported formats in each domain.
 */
public class PrintDomains {

  public static void main(String[] args) {
    // get a list of all available readers
    IFormatReader[] readers = new ImageReader().getReaders();

    Hashtable<String, Vector<IFormatReader>> domains =
      new Hashtable<String, Vector<IFormatReader>>();

    for (String domain : FormatTools.ALL_DOMAINS) {
      domains.put(domain, new Vector<IFormatReader>());
    }

    for (IFormatReader reader : readers) {
      try {
        String[] readerDomains = reader.getPossibleDomains("");
        for (String domain : readerDomains) {
          domains.get(domain).add(reader);
        }
      }
      catch (FormatException e) {
        e.printStackTrace();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }

    String[] domainKeys = domains.keySet().toArray(new String[domains.size()]);
    Arrays.sort(domainKeys);

    for (String domain : domainKeys) {
      System.out.println(domain + ":");
      Vector<IFormatReader> r = domains.get(domain);
      for (IFormatReader reader : r) {
        System.out.println("  " + reader.getFormat());
      }
    }
  }

}
