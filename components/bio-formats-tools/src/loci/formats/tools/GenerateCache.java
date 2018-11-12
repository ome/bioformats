/*
 * #%L
 * Bio-Formats command line tools for reading and converting files
 * %%
 * Copyright (C) 2018 Open Microscopy Environment:
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

import java.io.File;
import java.io.IOException;

import loci.common.DataTools;
import loci.formats.Memoizer;

/**
 * Generate cache file(s) for a specified file or directory.
 */
public class GenerateCache {

  /**
   * Use the given Memoizer to initialize the given file
   * and attempt to generate a memo file.
   * Prints a message if the memo file could not be saved.
   */
  private static void generateMemo(Memoizer reader, String path) {
    boolean success = false;
    try {
      success = reader.generateMemo(path);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    if (!success) {
      System.out.println("Memo file not saved for " + path);
    }
  }

  /**
   * Recursively scan the given directory and generate a memo file
   * for each found file.
   * Delegates to #generateMemo(Memoizer, String) to perform the actual
   * memo file generation.
   */
  private static void processDirectory(Memoizer reader, File dir) {
    String[] list = dir.list();
    for (String f : list) {
      File file = new File(dir, f);
      if (file.isDirectory()) {
        processDirectory(reader, file);
      }
      else {
        generateMemo(reader, file.getAbsolutePath());
      }
    }
  }

  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println("Usage:");
      System.out.println(
        "cachegen [-list] fileOrDir cacheFileDir");
      System.out.println();
      System.out.println("If '-list' is specified, then 'fileOrDir' is a text file with one file per line.");
      return;
    }

    CommandLineTools.runUpgradeCheck(args);

    boolean fileList = args.length >= 3 && args[0].equals("-list");
    String input = args[args.length - 2];
    String outputDir = args[args.length - 1];

    Memoizer reader = new Memoizer(0, new File(outputDir));
    File inputFile = new File(input);

    if (!inputFile.isDirectory()) {
      if (fileList) {
        String[] files = null;
        try {
          files = DataTools.readFile(inputFile.getAbsolutePath()).split("\n");
        }
        catch (IOException e) {
          System.out.println("Could not read file list from " + inputFile);
          e.printStackTrace();
        }
        if (files != null) {
          for (String f : files) {
            generateMemo(reader, f);
          }
        }
      }
      else {
        generateMemo(reader, inputFile.getAbsolutePath());
      }
    }
    else {
      processDirectory(reader, inputFile);
    }
  }

}
