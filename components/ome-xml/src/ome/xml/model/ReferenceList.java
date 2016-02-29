/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2016 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
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

package ome.xml.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 */
public class ReferenceList<T> extends ArrayList<T> {

  private Set<T> backingSet = new HashSet<T>();

  public ReferenceList() {
    super();
  }

  public ReferenceList(Collection<? extends T> c) {
    super(c);
  }

  public boolean add(T element) {
    if (backingSet.add(element)) {
      return super.add(element);
    }
    return false;
  }

  public void add(int index, T element) {
    if (backingSet.add(element)) {
      super.add(index, element);
    }
  }

  public boolean addAll(Collection<? extends T> c) {
    if (backingSet.addAll(c)) {
      return super.addAll(c);
    }
    return false;
  }

  public boolean addAll(int index, Collection<? extends T> c) {
    if (backingSet.addAll(c)) {
      return super.addAll(index, c);
    }
    return false;
  }

  public void clear() {
    super.clear();
    backingSet.clear();
  }

  public boolean contains(Object o) {
    return backingSet.contains(o);
  }

  public boolean containsAll(Collection<?> c) {
    return backingSet.containsAll(c);
  }

  public boolean isEmpty() {
    return backingSet.isEmpty();
  }

  public T remove(int index) {
    T removed = super.remove(index);
    backingSet.remove(removed);
    return removed;
  }

  public boolean remove(Object o) {
    if (backingSet.remove(o)) {
      return super.remove(o);
    }
    return false;
  }

  public boolean removeAll(Collection<?> c) {
    if (backingSet.removeAll(c)) {
      super.removeAll(c);
    }
    return false;
  }

  public boolean retainAll(Collection<?> c) {
    if (backingSet.retainAll(c)) {
      super.retainAll(c);
    }
    return false;
  }

  public T set(int index, T element) {
    T removed = remove(index);
    add(index, element);
    return removed;
  }

}
