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

package loci.formats;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that allows checking for new versions of Bio-Formats, as well as
 * updating to the latest stable, daily, or trunk version.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/UpgradeChecker.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/UpgradeChecker.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class UpgradeChecker {

  // -- Constants - OMERO registry IDs --

  /** Registry ID identifying usage of Bio-Formats in ImageJ. */
  public static final String REGISTRY_IMAGEJ = "OMERO.imagej";

  /** Registry ID identifying usage of Bio-Formats as a library in general. */
  public static final String REGISTRY_LIBRARY = "OMERO.bioformats";

  // -- Constants --

  /** Version number of the latest stable release. */
  public static final String STABLE_VERSION = "4.4.5";

  /** Location of the OME continuous integration server. */
  public static final String CI_SERVER = "http://hudson.openmicroscopy.org.uk";

  /**
   * Location of the JAR artifacts for Bio-Formats' trunk build.
   */
  public static final String TRUNK_BUILD =
    CI_SERVER + "/job/BIOFORMATS-trunk/lastSuccessfulBuild/artifact/artifacts/";

  /**
   * Location of the JAR artifacts for Bio-Formats' daily build.
   */
  public static final String DAILY_BUILD =
    CI_SERVER + "/job/BIOFORMATS-daily/lastSuccessfulBuild/artifact/artifacts/";

  /**
   * Location of the JAR artifacts for the stable releases.
   */
  public static final String STABLE_BUILD =
    "http://cvs.openmicroscopy.org.uk/snapshots/bioformats/" +  STABLE_VERSION + "/";

  /** Name of the LOCI tools JAR. */
  public static final String TOOLS = "loci_tools.jar";

  /** Name of the OME tools JAR. */
  public static final String OME_TOOLS = "ome_tools.jar";

  /** Names of the individual JARs. */
  public static final String[] INDIVIDUAL_JARS = new String[] {
    "bio-formats.jar", "jai_imageio.jar", "loci-common.jar",
    "mdbtools-java.jar", "metakit.jar", "ome-io.jar", "ome-xml.jar",
    "poi-loci.jar", "scifio.jar"
  };

  /** Location of the OME registry. */
  public static final String REGISTRY = "http://upgrade.openmicroscopy.org.uk";

  /** Value of "bioformats.caller" for Bio-Formats utilities. */
  public static final String DEFAULT_CALLER = "Bio-Formats utilities";

  /** Properties that are sent to the OME registry. */
  private static final String[] REGISTRY_PROPERTIES = new String[] {
    "version", "os.name", "os.version", "os.arch", "java.runtime.version",
    "java.vm.vendor", "bioformats.caller"
  };

  /** System property to set once the upgrade check is performed. */
  private static final String UPGRADE_CHECK_PROPERTY =
    "bioformats_upgrade_check";

  /** System property indicating whether the upgrade check is ever allowed. */
  private static final String UPGRADE_CHECK_ALLOWED_PROPERTY =
    "bioformats_can_do_upgrade_check";

  /** Number of bytes to read from the CI server at a time. */
  private static final int CHUNK_SIZE = 8192;

  private static final Logger LOGGER =
    LoggerFactory.getLogger(UpgradeChecker.class);

  // -- UpgradeChecker API methods --

  /**
   * Return true if an upgrade check has already been performed in this
   * JVM session.
   */
  public boolean alreadyChecked() {
    String checked = System.getProperty(UPGRADE_CHECK_PROPERTY);
    if (checked == null) {
      return false;
    }
    return Boolean.parseBoolean(checked);
  }

  /**
   * Return whether or not we are ever allowed to perform an upgrade check.
   */
  public boolean canDoUpgradeCheck() {
    String checked = System.getProperty(UPGRADE_CHECK_ALLOWED_PROPERTY);
    if (checked == null) {
      return true;
    }
    return Boolean.parseBoolean(checked);
  }

  /**
   * Set whether or not we are ever allowed to perform an upgrade check.
   */
  public void setCanDoUpgradeCheck(boolean canDo) {
    System.setProperty(UPGRADE_CHECK_ALLOWED_PROPERTY, String.valueOf(canDo));
  }

  /**
   * Contact the OME registry and return true if a new version is available.
   * OMERO.registry will identify this as a generic library usage of
   * Bio-Formats (i.e. not associated with a specific client application).
   *
   * @param caller  name of the calling application, e.g. "MATLAB"
   */
  public boolean newVersionAvailable(String caller) {
    return newVersionAvailable(REGISTRY_LIBRARY, caller);
  }

  /**
   * Contact the OME registry and return true if a new version is available.
   *
   * @param registryID how the application identifies itself to OMERO.registry
   *                  @see #REGISTRY_IMAGEJ, @see #REGISTRY_LIBRARY
   * @param caller  name of the calling application, e.g. "MATLAB"
   */
  public boolean newVersionAvailable(String registryID, String caller) {
    if (!canDoUpgradeCheck()) {
      return false;
    }

    // build the registry query

    System.setProperty("bioformats.caller", caller);
    StringBuffer query = new StringBuffer(REGISTRY);
    for (int i=0; i<REGISTRY_PROPERTIES.length; i++) {
      if (i == 0) {
        query.append("?");
      }
      else {
        query.append(";");
      }

      query.append(REGISTRY_PROPERTIES[i]);
      query.append("=");

      if (i == 0) {
        query.append(FormatTools.VERSION);
      }
      else {
        try {
          query.append(URLEncoder.encode(
            System.getProperty(REGISTRY_PROPERTIES[i]), "UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
          LOGGER.warn("Failed to append query argument: " +
            REGISTRY_PROPERTIES[i], e);
        }
      }
    }

    System.setProperty(UPGRADE_CHECK_PROPERTY, "true");

    try {
      // connect to the registry

      URLConnection conn = new URL(query.toString()).openConnection();
      conn.setConnectTimeout(5000);
      conn.setUseCaches(false);
      conn.addRequestProperty("User-Agent", registryID);
      conn.connect();

      // retrieve latest version number from the registry

      InputStream in = conn.getInputStream();
      StringBuffer latestVersion = new StringBuffer();
      while (true) {
        int data = in.read();
        if (data == -1) {
          break;
        }
        latestVersion.append((char) data);
      }
      in.close();

      // check to see if the version reported by the registry is greater than
      // the current version - version numbers are in "x.x.x" format

      String[] version = latestVersion.toString().split("\\.");
      String[] thisVersion = FormatTools.VERSION.split("\\.");
      for (int i=0; i<thisVersion.length; i++) {
        int subVersion = Integer.parseInt(thisVersion[i]);
        try {
          int registrySubVersion = Integer.parseInt(version[i]);
          if (registrySubVersion != subVersion) {
            return registrySubVersion > subVersion;
          }
        }
        catch (NumberFormatException e) {
          return false;
        }
      }
    }
    catch (IOException e) {
      LOGGER.warn("Failed to compare version numbers", e);
    }
    return false;
  }

  /**
   * Download and install all of the individual JAR files into the given
   * directory.
   *
   * @param urlDir the location from which to download the JAR files
   * @param downloadDir the directory into which to save the JAR files
   * @return true if installation was successfull
   *
   * @see install(String, String)
   */
  public boolean installIndividualJars(String urlDir, String downloadDir) {
    boolean overallSuccess = true;
    for (String jar : INDIVIDUAL_JARS) {
      boolean success = install(urlDir + File.separator + jar,
        downloadDir + File.separator + jar);
      if (overallSuccess) {
        success = overallSuccess;
      }
    }
    return overallSuccess;
  }

  /**
   * Download and install a JAR file from the given URL.
   *
   * @param urlPath the location from which to download the JAR
   * @param downloadPath the location in which to write the JAR;
   *                     if this location already exists, it will be overwritten
   * @return true if installation was successful
   */
  public boolean install(String urlPath, String downloadPath) {
    // if an old version exists, then remove it

    File jar = new File(downloadPath + ".tmp");
    if (jar.exists()) {
      if (!jar.delete()) {
        LOGGER.warn("Failed to delete '{}'", jar.getAbsolutePath());
        return false;
      }
    }

    // download new version

    try {
      URL url = new URL(urlPath);
      URLConnection urlConn = url.openConnection();
      int total = urlConn.getContentLength();
      byte[] buf = new byte[total];

      DataInputStream in = new DataInputStream(
        new BufferedInputStream(urlConn.getInputStream()));
      int off = 0;
      while (off < total) {
        int len = CHUNK_SIZE;
        if (off + len > total) {
          len = total - off;
        }
        int r = in.read(buf, off, len);
        if (r <= 0) {
          LOGGER.warn("Truncated JAR file");
          return false;
        }
        off += r;
      }

      in.close();

      // write the downloaded JAR to a file on disk
      FileOutputStream out = new FileOutputStream(jar);
      out.write(buf);
      out.close();

      boolean success = jar.renameTo(new File(downloadPath));
      if (!success) {
        LOGGER.warn("Failed to rename '{}' to '{}'", jar.getAbsolutePath(),
          downloadPath);
      }
      return success;
    }
    catch (IOException e) {
      LOGGER.warn("Failed to download from " + urlPath, e);
    }
    return false;
  }

}
