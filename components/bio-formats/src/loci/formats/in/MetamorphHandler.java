//
// MetamorphHandler.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.util.Hashtable;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * MetamorphTiffReader is the file format reader for TIFF files produced by
 * Metamorph software version 7.5 and above.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/MetamorphHandler.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/MetamorphHandler.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 * @author Thomas Caswell tcaswell at uchicago.edu
 */
public class MetamorphHandler extends DefaultHandler {

  // -- Fields --

  private Hashtable metadata;

  private Vector timestamps;
  private String imageName;
  private String date;
  private Vector wavelengths;
  private Vector zPositions;
  private float pixelSizeX, pixelSizeY;
  private float temperature;

  // -- Constructor --

  public MetamorphHandler(Hashtable metadata) {
    super();
    this.metadata = metadata;
    timestamps = new Vector();
    wavelengths = new Vector();
    zPositions = new Vector();
  }

  // -- MetamorphHandler API methods --

  public Vector getTimestamps() { return timestamps; }

  public Vector getWavelengths() { return wavelengths; }

  public Vector getZPositions() { return zPositions; }

  public String getDate() { return date; }

  public String getImageName() { return imageName; }

  public float getPixelSizeX() { return pixelSizeX; }

  public float getPixelSizeY() { return pixelSizeY; }

  public float getTemperature() { return temperature; }

  // -- DefaultHandler API methods --

  public void startElement(String uri, String localName, String qName,
    Attributes attributes)
  {
    String id = attributes.getValue("id");
    String value = attributes.getValue("value");
    String delim = "&#13;&#10;";
    if (id != null && value != null) {
      if (id.equals("Description")) {
        metadata.remove("Comment");

        String k = null, v = null;

        if (value.indexOf(delim) != -1) {
          int currentIndex = -delim.length();
          while (currentIndex != -1) {
            currentIndex += delim.length();
            int nextIndex = value.indexOf(delim, currentIndex);

            String line = null;
            if (nextIndex == -1) {
              line = value.substring(currentIndex, value.length());
            }
            else {
              line = value.substring(currentIndex, nextIndex);
            }
            currentIndex = nextIndex;

            int colon = line.indexOf(":");
            if (colon != -1) {
              k = line.substring(0, colon).trim();
              v = line.substring(colon + 1).trim();
              metadata.put(k, v);
              if (k.equals("Temperature")) {
                temperature = Float.parseFloat(v.trim());
              }
            }
          }
        }
        else {
          int colon = value.indexOf(":");
          while (colon != -1) {
            k = value.substring(0, colon);
            int space = value.lastIndexOf(" ", value.indexOf(":", colon + 1));
            if (space == -1) space = value.length();
            v = value.substring(colon + 1, space);
            metadata.put(k, v);
            value = value.substring(space).trim();
            colon = value.indexOf(":");

            if (k.equals("Temperature")) {
              temperature = Float.parseFloat(v.trim());
            }
          }
        }
      }
      else metadata.put(id, value);

      if (id.equals("spatial-calibration-x")) {
        pixelSizeX = Float.parseFloat(value);
      }
      else if (id.equals("spatial-calibration-y")) {
        pixelSizeY = Float.parseFloat(value);
      }
      else if (id.equals("z-position")) {
        zPositions.add(new Float(value));
      }
      else if (id.equals("wavelength")) {
        wavelengths.add(new Integer(value));
      }
      else if (id.equals("acquisition-time-local")) {
        date = value;
        timestamps.add(date);
      }
      else if (id.equals("image-name")) imageName = value;
    }
  }
}
