/*
 * #%L
 * Fork of Apache Jakarta POI.
 * %%
 * Copyright (C) 2008 - 2013 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */


package loci.poi.util;

import java.util.*;

/**
 * A List of objects that are indexed AND keyed by an int; also allows for getting
 * the index of a value in the list
 *
 * <p>I am happy is someone wants to re-implement this without using the
 * internal list and hashmap. If so could you please make sure that
 * you can add elements half way into the list and have the value-key mappings
 * update</p>
 *
 *
 * @author Jason Height
 */

public class IntMapper
{
  private List elements;
  private Map valueKeyMap;

  private static final int _default_size = 10;

    /**
     * create an IntMapper of default size
     */

    public IntMapper()
    {
        this(_default_size);
    }

    public IntMapper(final int initialCapacity)
    {
        elements = new ArrayList(initialCapacity);
        valueKeyMap = new HashMap(initialCapacity);
    }

    /**
     * Appends the specified element to the end of this list
     *
     * @param value element to be appended to this list.
     *
     * @return true (as per the general contract of the Collection.add
     *         method).
     */

    public boolean add(final Object value)
    {
      int index = elements.size();
      elements.add(value);
      valueKeyMap.put(value, new Integer(index));
      return true;
    }

    public int size() {
      return elements.size();
    }

    public Object get(int index) {
      return elements.get(index);
    }

    public int getIndex(Object o) {
      Integer i = ((Integer)valueKeyMap.get(o));
      if (i == null)
        return -1;
      return i.intValue();
    }

    public Iterator iterator() {
      return elements.iterator();
    }
}   // end public class IntMapper

