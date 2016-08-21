/*
 * #%L
 * OME Metakit package for reading Metakit database files.
 * %%
 * Copyright (C) 2011 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package ome.metakit;

/**
 * MetakitException is the exception thrown when something
 * goes wrong when reading a Metakit database file.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/metakit/src/ome/metakit/MetakitException.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/metakit/src/ome/metakit/MetakitException.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class MetakitException extends Exception {

  public MetakitException() { super(); }
  public MetakitException(String s) { super(s); }
  public MetakitException(String s, Throwable cause) { super(s, cause); }
  public MetakitException(Throwable cause) { super(cause); }

}

