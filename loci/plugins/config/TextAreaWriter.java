//
// TextAreaWriter.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Data Browser, Stack Colorizer,
Stack Slicer, and OME plugins. Copyright (C) 2005-@year@ Melissa Linkert,
Curtis Rueden, Christopher Peterson and Philip Huettl.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.plugins.config;

import java.io.Writer;
import javax.swing.JTextArea;

/**
 * Writer implementation that writes to a JTextArea document.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/config/TextAreaWriter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/config/TextAreaWriter.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class TextAreaWriter extends Writer {

  // -- Fields --

  /** Text area component to which output should be written. */
  private JTextArea textArea;

  // -- Constructor --

  /** Constructs a new TextAreaWriter around the given text area component. */
  public TextAreaWriter(JTextArea textArea) {
    this.textArea = textArea;
  }

  // -- Writer API methods --

  /** Closing a TextAreaWriter does nothing. */
  public void close() { }

  /** Flushing a TextAreaWriter does nothing .*/
  public void flush() { }

  /** Writes the given characters to the associated text area component. */
  public void write(char[] cbuf, int off, int len) {
    textArea.append(new String(cbuf, off, len));
    textArea.setCaretPosition(textArea.getDocument().getLength());
  }

}
