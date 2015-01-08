/*
 * #%L
 * Fork of Apache Jakarta POI.
 * %%
 * Copyright (C) 2008 - 2014 Open Microscopy Environment:
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
/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package loci.poi.util;

import java.util.List;
import java.util.ArrayList;

/**
 * Provides an interface for interacting with 2d arrays of objects.  This
 * implementation will return null for items not yet allocated and automatically
 * increase the array size for set operations.  You never get an index out of
 * bounds.
 *
 * @author Glen Stampoultzis (glens at apache.org)
 * @version $Id: List2d.java 496526 2007-01-15 22:46:35Z markt $
 */
public class List2d
{
    // Implemented using a List of List's.
    List rows = new ArrayList();

    public Object get(int col, int row)
    {
        if (row >= rows.size())
        {
            return null;
        }
        else
        {
            List cols = (List) rows.get(row);
            if (col >= cols.size())
                return null;
            else
                return cols.get( col );
        }
    }

    public void set(int col, int row, Object value)
    {
        resizeRows(row);
        resizeCols(row,col);
        List cols = (List) rows.get( row );
        cols.set( col, value );
    }

    private void resizeRows( int row )
    {
        while (rows.size() <= row)
            rows.add( new ArrayList() );
    }

    private void resizeCols( int row, int col )
    {
        List cols = (List) rows.get( row );
        while (cols.size() <= col)
            cols.add(null);
    }


}
