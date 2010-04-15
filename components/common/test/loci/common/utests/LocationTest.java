//
// LocationTest.java
//

/*
LOCI Common package: utilities for I/O, reflection and miscellaneous tasks.
Copyright (C) 2005-@year@ Melissa Linkert and Curtis Rueden.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.common.utests;

import static org.testng.AssertJUnit.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import loci.common.Location;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit tests for the loci.common.Location class.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/common/test/loci/common/utests/LocationTest.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/common/test/loci/common/utests/LocationTest.java">SVN</a></dd></dl>
 *
 * @see loci.common.Location
 */
public class LocationTest {

  // -- Fields --

  private Location[] files;
  private boolean[] exists;
  private boolean[] isDirectory;
  private boolean[] isHidden;
  private String[] mode;

  // -- Setup methods --

  @BeforeMethod
  public void setup() throws IOException {
    File hiddenFile = File.createTempFile(".hiddenTest", null);
    hiddenFile.deleteOnExit();

    File invalidFile = File.createTempFile("invalidTest", null);
    String invalidPath = invalidFile.getAbsolutePath();
    invalidFile.delete();

    File validFile = File.createTempFile("validTest", null);
    validFile.deleteOnExit();

    files = new Location[] {
      new Location(validFile.getAbsolutePath()),
      new Location(invalidPath),
      new Location(System.getProperty("java.io.tmpdir")),
      new Location("http://www.kernel.org/pub/linux/"),
      new Location("http://loci.wisc.edu/software/bio-formats"),
      new Location("http://openmicroscopy.org/software/bio-formats"),
      new Location(hiddenFile)
    };

    exists = new boolean[] {
      true, false, true, true, true, false, true
    };

    isDirectory = new boolean[] {
      false, false, true, true, false, false, false
    };

    isHidden = new boolean[] {
      false, false, false, false, false, false, true
    };

    mode = new String[] {
      "rw", "", "rw", "r", "r", "", "rw"
    };

  }

  // -- Tests --

  @Test
  public void testReadWriteMode() {
    for (int i=0; i<files.length; i++) {
      assertEquals(files[i].canRead(), mode[i].indexOf("r") != -1);
      assertEquals(files[i].canWrite(), mode[i].indexOf("w") != -1);
    }
  }

  @Test
  public void testAbsolute() {
    for (Location file : files) {
      assertEquals(file.getAbsolutePath(),
        file.getAbsoluteFile().getAbsolutePath());
    }
  }

  @Test
  public void testExists() {
    for (int i=0; i<files.length; i++) {
      assertEquals(files[i].exists(), exists[i]);
    }
  }

  @Test
  public void testCanonical() throws IOException {
    for (Location file : files) {
      assertEquals(file.getCanonicalPath(),
        file.getCanonicalFile().getAbsolutePath());
    }
  }

  @Test
  public void testParent() {
    for (Location file : files) {
      assertEquals(file.getParent(), file.getParentFile().getAbsolutePath());
    }
  }

  @Test
  public void testIsDirectory() {
    for (int i=0; i<files.length; i++) {
      assertEquals(files[i].isDirectory(), isDirectory[i]);
    }
  }

  @Test
  public void testIsFile() {
    for (int i=0; i<files.length; i++) {
      assertEquals(files[i].isFile(), !isDirectory[i] && exists[i]);
    }
  }

  @Test
  public void testIsHidden() {
    for (int i=0; i<files.length; i++) {
      assertEquals(files[i].isHidden(), isHidden[i]);
    }
  }

  @Test
  public void testListFiles() {
    for (int i=0; i<files.length; i++) {
      String[] completeList = files[i].list();
      String[] unhiddenList = files[i].list(true);
      Location[] fileList = files[i].listFiles();

      if (!files[i].isDirectory()) {
        assertEquals(completeList, null);
        assertEquals(unhiddenList, null);
        assertEquals(fileList, null);
        continue;
      }

      assertEquals(completeList.length, fileList.length);

      List<String> complete = Arrays.asList(completeList);
      for (String child : unhiddenList) {
        assertEquals(complete.contains(child), true);
        assertEquals(new Location(files[i], child).isHidden(), false);
      }

      for (int f=0; f<fileList.length; f++) {
        assertEquals(fileList[f].getName(), completeList[f]);
      }
    }
  }

  @Test
  public void testToURL() throws IOException {
    for (Location file : files) {
      String path = file.getAbsolutePath();
      if (path.indexOf("://") == -1) {
        path = "file://" + path;
      }
      if (file.isDirectory() && !path.endsWith(File.separator)) {
        path += File.separator;
      }
      assertEquals(file.toURL(), new URL(path));
    }
  }

  @Test
  public void testToString() {
    for (Location file : files) {
      assertEquals(file.toString(), file.getAbsolutePath());
    }
  }

}
