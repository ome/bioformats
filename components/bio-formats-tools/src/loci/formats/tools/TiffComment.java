/*
 * #%L
 * Bio-Formats command line tools for reading and converting files
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;
import loci.formats.tiff.TiffParser;
import loci.formats.tiff.TiffSaver;

/**
 * Extracts the comment from the first IFD of the given TIFF file(s).
 */
public class TiffComment {

  public static void main(String[] args) throws FormatException, IOException {
    CommandLineTools.runUpgradeCheck(args);

    if (args.length == 0) {
      System.out.println("Usage:");
      System.out.println(
        "tiffcomment [-set comment] [-edit] file1 [file2 ...]");
      System.out.println();

      System.out.println("This tool requires an ImageDescription tag to be " +
        "present in the TIFF file. ");
      System.out.println();
      System.out.println("If using the '-set' option, the new TIFF comment " +
        "must be specified and may take any of the following forms:");
      System.out.println();
      System.out.println("  * the text of the comment, e.g. 'new comment!'");
      System.out.println("  * the name of the file containing the text of " +
        "the comment, e.g. 'file.xml'");
      System.out.println("  * '-', to enter the comment using stdin.  " +
        "Entering a blank line will");
      System.out.println("    terminate reading from stdin.");
      return;
    }

    // parse flags
    boolean edit = false;
    String newComment = null;
    ArrayList<String> files = new ArrayList<String>();
    for (int i=0; i<args.length; i++) {
      if (!args[i].startsWith("-")) {
        files.add(args[i]);
        continue;
      }

      if (args[i].equals("-edit")) edit = true;
      else if (args[i].equals("-set")) {
        newComment = args[++i];
        if (new File(newComment).exists()) {
          newComment = DataTools.readFile(newComment);
        }
        else if (newComment.equals("-")) {
          newComment = null;
          BufferedReader reader = new BufferedReader(
            new InputStreamReader(System.in, Constants.ENCODING));
          String line = reader.readLine();
          while (line != null && line.length() > 0) {
            if (newComment == null) newComment = line;
            else {
              newComment += "\n" + line;
            }
            line = reader.readLine();
          }
        }
      }
      else System.out.println("Warning: unknown flag: " + args[i]);
    }

    // process files
    for (String file : files) {
      if (edit) EditTiffG.openFile(file);
      else if (newComment != null) {
          overwriteComment(file, newComment);
      }
      else {
        String comment = new TiffParser(file).getComment();
        System.out.println(comment == null ?
          file + ": no TIFF comment found." : comment);
      }
    }
  }

  /**
   * Overwrites the comment.
   *
   * @param file The path to the file to handle.
   * @param comment The new comment to write.
   */
  private static void overwriteComment(String file, String comment)
  {
    RandomAccessInputStream in = null;
    RandomAccessOutputStream out = null;
    try {
      in = new RandomAccessInputStream(file);
      out = new RandomAccessOutputStream(file);
      TiffSaver saver = new TiffSaver(out, file);
      saver.overwriteComment(in, comment);
    } catch (Exception e) {
      System.out.println(e.toString());
    } finally {
      try {
        if (in != null) in.close();
      } catch (Exception e) {}
      try {
        if (out != null) out.close();
      } catch (Exception e) {}
    }
  }
}
