//
// TextTransfer.java
//

/*
SLIM Plotter application and curve fitting library for
combined spectral lifetime visualization and analysis.
Copyright (C) 2006-@year@ Curtis Rueden and Eric Kjellman.

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

package loci.slim;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Helper class for clipboard interaction, stolen from
 * <a href="http://www.javapractices.com/Topic82.cjp">Java Practices</a>.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/slim-plotter/src/loci/slim/TextTransfer.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/slim-plotter/src/loci/slim/TextTransfer.java">SVN</a></dd></dl>
 */
public final class TextTransfer implements ClipboardOwner {

  public void setClipboardContents(String value) {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    if (clipboard != null) {
      clipboard.setContents(new StringSelection(value), this);
    }
  }

  public String getClipboardContents() {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    if (clipboard == null) return null;
    String result = null;
    Transferable contents = clipboard.getContents(null);
    boolean hasTransferableText = contents != null &&
      contents.isDataFlavorSupported(DataFlavor.stringFlavor);
    if (hasTransferableText) {
      try {
        result = (String) contents.getTransferData(DataFlavor.stringFlavor);
      }
      catch (UnsupportedFlavorException ex) { ex.printStackTrace(); }
      catch (IOException ex) { ex.printStackTrace(); }
    }
    return result;
  }

  public void lostOwnership(Clipboard clipboard, Transferable contents) { }

}
