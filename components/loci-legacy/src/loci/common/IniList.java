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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A legacy delegator class for ome.scifio.common.IniList.
 * 
 * Uses an "isa" relationship to preserve the inherited
 * methods from ArrayList.
 * 
 * Dummy method signatures for IniList-specific methods
 * are present to provide early warnings if these method
 * names change (which would break backwards compatibility).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/IniList.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/IniList.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class IniList extends ArrayList<IniTable> {
  
  // -- Fields --
  
  public ome.scifio.common.IniList list = new ome.scifio.common.IniList();
  
  // -- Delegators --

  public boolean add(IniTable arg0) {
    return list.add(arg0.getTable());
  }

  public void add(int arg0, IniTable arg1) {
    list.add(arg0, arg1.getTable());
  }

  public boolean addAll(Collection<? extends IniTable> arg0) {
    Iterator<? extends IniTable> iter = arg0.iterator();
    
    while(iter.hasNext())
      list.add(iter.next().getTable());
    
    return arg0.size() > 0;
  }

  public boolean addAll(int arg0, Collection<? extends IniTable> arg1) {
    Iterator<? extends IniTable> iter = arg1.iterator();
    
    while(iter.hasNext())
      list.add(arg0, iter.next().getTable());
    
    return arg1.size() > 0;
  }

  public void clear() {
    list.clear();
  }

  public Object clone() {
    return list.clone();
  }

  public boolean contains(Object arg0) {
    return list.contains(arg0);
  }

  public boolean containsAll(Collection<?> arg0) {
    return list.containsAll(arg0);
  }

  public void ensureCapacity(int arg0) {
    list.ensureCapacity(arg0);
  }

  public boolean equals(Object arg0) {
    return list.equals(arg0);
  }

  public IniTable get(int arg0) {
    return convertTable(list.get(arg0));
  }

  public int hashCode() {
    return list.hashCode();
  }

  public int indexOf(Object arg0) {
    return list.indexOf(arg0);
  }

  public boolean isEmpty() {
    return list.isEmpty();
  }

  public Iterator<IniTable> iterator() {
    return convertList().iterator();
  }

  public int lastIndexOf(Object arg0) {
    return list.lastIndexOf(arg0);
  }

  public ListIterator<IniTable> listIterator() {
    return convertList().listIterator();
  }

  public ListIterator<IniTable> listIterator(int arg0) {
    return convertList().listIterator(arg0);
  }

  public IniTable remove(int arg0) {
    return convertTable(list.remove(arg0));
  }

  public boolean remove(Object arg0) {
    return list.remove(arg0);
  }

  public boolean removeAll(Collection<?> arg0) {
    return list.removeAll(arg0);
  }

  public boolean retainAll(Collection<?> arg0) {
    return list.retainAll(arg0);
  }

  public IniTable set(int arg0, IniTable arg1) {
    return convertTable(list.set(arg0, arg1.getTable()));
  }

  public int size() {
    return list.size();
  }

  public List<IniTable> subList(int arg0, int arg1) {
    return convertList().subList(arg0, arg1);
  }

  public Object[] toArray() {
    return list.toArray();
  }

  public <T> T[] toArray(T[] arg0) {
    return list.toArray(arg0);
  }

  public String toString() {
    return list.toString();
  }

  public void trimToSize() {
    list.trimToSize();
  }

  /** Gets the table with the given name (header). */
  public IniTable getTable(String tableName) {
    return convertTable(list.getTable(tableName));
  }

  /**
   * Flattens all of the INI tables into a single HashMap whose keys are
   * of the format "[table name] table key".
   */
  public HashMap<String, String> flattenIntoHashMap() {
    return list.flattenIntoHashMap();
  }
  
  // -- Helper methods --
  
  private IniTable convertTable(ome.scifio.common.IniTable table) {
    if (table == null) return null;
    
    IniTable t = new IniTable();
    t.setTable(table);
    return t;
  }
  
  private ArrayList<IniTable> convertList() {
    Iterator<ome.scifio.common.IniTable> iter = list.iterator();
    
    ArrayList<IniTable> tmpTables = new ArrayList<IniTable>();
    
    while(iter.hasNext())
      tmpTables.add(convertTable(iter.next()));
    
    return tmpTables;
  }
}
