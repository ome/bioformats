/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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

package loci.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A legacy delegator class for ome.scifio.common.IniTable.
 * 
 * Uses an "isa" relationship to preserve the inherited methods
 * of IniTable from HashMap.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/IniTable.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/IniTable.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class IniTable extends HashMap<String, String> {

  // -- Fields --
  
  private ome.scifio.common.IniTable table;
  
  // -- Constants --
  
  /** Key to use for storing header value (in brackets). */
  public static final String HEADER_KEY = ome.scifio.common.IniTable.HEADER_KEY;
  
  // -- Constructor --
  
  public IniTable() {
    table = new ome.scifio.common.IniTable();
  }
  
  // -- Accessors --

  public ome.scifio.common.IniTable getTable() {
    return table;
  }

  public void setTable(ome.scifio.common.IniTable table) {
    this.table = table;
  }
  
  // -- Delegators --

  public void clear() {
    table.clear();
  }

  public Object clone() {
    return table.clone();
  }

  public boolean containsKey(Object arg0) {
    return table.containsKey(arg0);
  }

  public boolean containsValue(Object arg0) {
    return table.containsValue(arg0);
  }

  public Set<java.util.Map.Entry<String, String>> entrySet() {
    return table.entrySet();
  }

  public boolean equals(Object arg0) {
    return table.equals(arg0);
  }

  public String get(Object arg0) {
    return table.get(arg0);
  }

  public int hashCode() {
    return table.hashCode();
  }

  public boolean isEmpty() {
    return table.isEmpty();
  }

  public Set<String> keySet() {
    return table.keySet();
  }

  public String put(String arg0, String arg1) {
    return table.put(arg0, arg1);
  }

  public void putAll(Map<? extends String, ? extends String> arg0) {
    table.putAll(arg0);
  }

  public String remove(Object arg0) {
    return table.remove(arg0);
  }

  public int size() {
    return table.size();
  }

  public String toString() {
    return table.toString();
  }

  public Collection<String> values() {
    return table.values();
  }
}
