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
package loci.poi.hssf.record.aggregates;

import loci.poi.hssf.record.ColumnInfoRecord;
import loci.poi.hssf.record.Record;
import loci.poi.hssf.record.RecordInputStream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Glen Stampoultzis
 * @version $Id: ColumnInfoRecordsAggregate.java 496526 2007-01-15 22:46:35Z markt $
 */
public class ColumnInfoRecordsAggregate
    extends Record
{
//    int     size     = 0;
    List records = null;

    public ColumnInfoRecordsAggregate()
    {
        records = new ArrayList();
    }

    /** You never fill an aggregate */
    protected void fillFields(RecordInputStream in)
    {
    }

    /** Not required by an aggregate */
    protected void validateSid(short id)
    {
    }

    /** It's an aggregate... just made something up */
    public short getSid()
    {
        return -1012;
    }

    public int getRecordSize()
    {
        int size = 0;
        for ( Iterator iterator = records.iterator(); iterator.hasNext(); )
            size += ( (ColumnInfoRecord) iterator.next() ).getRecordSize();
        return size;
    }

    public Iterator getIterator()
    {
        return records.iterator();
    }

    /**
     * Performs a deep clone of the record
     */
    public Object clone()
    {
        ColumnInfoRecordsAggregate rec = new ColumnInfoRecordsAggregate();
        for (int k = 0; k < records.size(); k++)
        {
            ColumnInfoRecord ci = ( ColumnInfoRecord ) records.get(k);
            ci=(ColumnInfoRecord) ci.clone();
            rec.insertColumn( ci );
        }
        return rec;
    }

    /**
     * Inserts a column into the aggregate (at the end of the list).
     */
    public void insertColumn( ColumnInfoRecord col )
    {
        records.add( col );
    }

    /**
     * Inserts a column into the aggregate (at the position specified
     * by <code>idx</code>.
     */
    public void insertColumn( int idx, ColumnInfoRecord col )
    {
        records.add( idx, col );
    }

    public int getNumColumns( )
    {
        return records.size();
    }

    /**
     * called by the class that is responsible for writing this sucker.
     * Subclasses should implement this so that their data is passed back in a
     * byte array.
     *
     * @param offset    offset to begin writing at
     * @param data      byte array containing instance data
     * @return          number of bytes written
     */
    public int serialize(int offset, byte [] data)
    {
        Iterator itr = records.iterator();
        int      pos = offset;

        while (itr.hasNext())
        {
            pos += (( Record ) itr.next()).serialize(pos, data);
        }
        return pos - offset;
    }

    public int findStartOfColumnOutlineGroup(int idx)
    {
        // Find the start of the group.
        ColumnInfoRecord columnInfo = (ColumnInfoRecord) records.get( idx );
        int level = columnInfo.getOutlineLevel();
        while (idx != 0)
        {
            ColumnInfoRecord prevColumnInfo = (ColumnInfoRecord) records.get( idx - 1 );
            if (columnInfo.getFirstColumn() - 1 == prevColumnInfo.getLastColumn())
            {
                if (prevColumnInfo.getOutlineLevel() < level)
                {
                    break;
                }
                idx--;
                columnInfo = prevColumnInfo;
            }
            else
            {
                break;
            }
        }

        return idx;
    }

    public int findEndOfColumnOutlineGroup(int idx)
    {
        // Find the end of the group.
        ColumnInfoRecord columnInfo = (ColumnInfoRecord) records.get( idx );
        int level = columnInfo.getOutlineLevel();
        while (idx < records.size() - 1)
        {
            ColumnInfoRecord nextColumnInfo = (ColumnInfoRecord) records.get( idx + 1 );
            if (columnInfo.getLastColumn() + 1 == nextColumnInfo.getFirstColumn())
            {
                if (nextColumnInfo.getOutlineLevel() < level)
                {
                    break;
                }
                idx++;
                columnInfo = nextColumnInfo;
            }
            else
            {
                break;
            }
        }

        return idx;
    }

    public ColumnInfoRecord getColInfo(int idx)
    {
        return (ColumnInfoRecord) records.get( idx );
    }

    public ColumnInfoRecord writeHidden( ColumnInfoRecord columnInfo, int idx, boolean hidden )
    {
        int level = columnInfo.getOutlineLevel();
        while (idx < records.size())
        {
            columnInfo.setHidden( hidden );
            if (idx + 1 < records.size())
            {
                ColumnInfoRecord nextColumnInfo = (ColumnInfoRecord) records.get( idx + 1 );
                if (columnInfo.getLastColumn() + 1 == nextColumnInfo.getFirstColumn())
                {
                    if (nextColumnInfo.getOutlineLevel() < level)
                        break;
                    columnInfo = nextColumnInfo;
                }
                else
                {
                    break;
                }
            }
            idx++;
        }
        return columnInfo;
    }

    public boolean isColumnGroupCollapsed( int idx )
    {
        int endOfOutlineGroupIdx = findEndOfColumnOutlineGroup( idx );
        if (endOfOutlineGroupIdx >= records.size())
            return false;
        if (getColInfo(endOfOutlineGroupIdx).getLastColumn() + 1 != getColInfo(endOfOutlineGroupIdx + 1).getFirstColumn())
            return false;
        else
            return getColInfo(endOfOutlineGroupIdx+1).getCollapsed();
    }


    public boolean isColumnGroupHiddenByParent( int idx )
    {
        // Look out outline details of end
        int endLevel;
        boolean endHidden;
        int endOfOutlineGroupIdx = findEndOfColumnOutlineGroup( idx );
        if (endOfOutlineGroupIdx >= records.size())
        {
            endLevel = 0;
            endHidden = false;
        }
        else if (getColInfo(endOfOutlineGroupIdx).getLastColumn() + 1 != getColInfo(endOfOutlineGroupIdx + 1).getFirstColumn())
        {
            endLevel = 0;
            endHidden = false;
        }
        else
        {
            endLevel = getColInfo( endOfOutlineGroupIdx + 1).getOutlineLevel();
            endHidden = getColInfo( endOfOutlineGroupIdx + 1).getHidden();
        }

        // Look out outline details of start
        int startLevel;
        boolean startHidden;
        int startOfOutlineGroupIdx = findStartOfColumnOutlineGroup( idx );
        if (startOfOutlineGroupIdx <= 0)
        {
            startLevel = 0;
            startHidden = false;
        }
        else if (getColInfo(startOfOutlineGroupIdx).getFirstColumn() - 1 != getColInfo(startOfOutlineGroupIdx - 1).getLastColumn())
        {
            startLevel = 0;
            startHidden = false;
        }
        else
        {
            startLevel = getColInfo( startOfOutlineGroupIdx - 1).getOutlineLevel();
            startHidden = getColInfo( startOfOutlineGroupIdx - 1 ).getHidden();
        }

        if (endLevel > startLevel)
        {
            return endHidden;
        }
        else
        {
            return startHidden;
        }
    }

    public void collapseColumn( short columnNumber )
    {
        int idx = findColumnIdx( columnNumber, 0 );
        if (idx == -1)
            return;

        // Find the start of the group.
        ColumnInfoRecord columnInfo = (ColumnInfoRecord) records.get( findStartOfColumnOutlineGroup( idx ) );

        // Hide all the columns until the end of the group
        columnInfo = writeHidden( columnInfo, idx, true );

        // Write collapse field
        setColumn( (short) ( columnInfo.getLastColumn() + 1 ), null, null, null, null, Boolean.TRUE);
    }

    public void expandColumn( short columnNumber )
    {
        int idx = findColumnIdx( columnNumber, 0 );
        if (idx == -1)
            return;

        // If it is already exapanded do nothing.
        if (!isColumnGroupCollapsed(idx))
            return;

        // Find the start of the group.
        int startIdx = findStartOfColumnOutlineGroup( idx );
        ColumnInfoRecord columnInfo = getColInfo( startIdx );

        // Find the end of the group.
        int endIdx = findEndOfColumnOutlineGroup( idx );
        ColumnInfoRecord endColumnInfo = getColInfo( endIdx );

        // expand:
        // colapsed bit must be unset
        // hidden bit gets unset _if_ surrounding groups are expanded you can determine
        //   this by looking at the hidden bit of the enclosing group.  You will have
        //   to look at the start and the end of the current group to determine which
        //   is the enclosing group
        // hidden bit only is altered for this outline level.  ie.  don't uncollapse contained groups
        if (!isColumnGroupHiddenByParent( idx ))
        {
            for (int i = startIdx; i <= endIdx; i++)
            {
                if (columnInfo.getOutlineLevel() == getColInfo(i).getOutlineLevel())
                    getColInfo(i).setHidden( false );
            }
        }

        // Write collapse field
        setColumn( (short) ( columnInfo.getLastColumn() + 1 ), null, null, null, null, Boolean.FALSE);
    }

    /**
     * creates the ColumnInfo Record and sets it to a default column/width
     * @see loci.poi.hssf.record.ColumnInfoRecord
     * @return record containing a ColumnInfoRecord
     */
    public static Record createColInfo()
    {
        ColumnInfoRecord retval = new ColumnInfoRecord();

        retval.setColumnWidth(( short ) 2275);
        // was:       retval.setOptions(( short ) 6);
        retval.setOptions(( short ) 2);
        retval.setXFIndex(( short ) 0x0f);
        return retval;
    }


    public void setColumn(short column, Short xfIndex, Short width, Integer level, Boolean hidden, Boolean collapsed)
    {
        ColumnInfoRecord ci = null;
        int              k  = 0;

        for (k = 0; k < records.size(); k++)
        {
            ci = ( ColumnInfoRecord ) records.get(k);
            if ((ci.getFirstColumn() <= column)
                    && (column <= ci.getLastColumn()))
            {
                break;
            }
            ci = null;
        }

        if (ci != null)
        {
	    boolean styleChanged = xfIndex != null && ci.getXFIndex() != xfIndex.shortValue();
            boolean widthChanged = width != null && ci.getColumnWidth() != width.shortValue();
            boolean levelChanged = level != null && ci.getOutlineLevel() != level.intValue();
            boolean hiddenChanged = hidden != null && ci.getHidden() != hidden.booleanValue();
            boolean collapsedChanged = collapsed != null && ci.getCollapsed() != collapsed.booleanValue();
            boolean columnChanged = styleChanged || widthChanged || levelChanged || hiddenChanged || collapsedChanged;
            if (!columnChanged)
            {
                // do nothing...nothing changed.
            }
            else if ((ci.getFirstColumn() == column)
                     && (ci.getLastColumn() == column))
            {                               // if its only for this cell then
                setColumnInfoFields( ci, xfIndex, width, level, hidden, collapsed );
            }
            else if ((ci.getFirstColumn() == column)
                     || (ci.getLastColumn() == column))
            {
                // okay so the width is different but the first or last column == the column we'return setting
                // we'll just divide the info and create a new one
                if (ci.getFirstColumn() == column)
                {
                    ci.setFirstColumn(( short ) (column + 1));
                }
                else
                {
                    ci.setLastColumn(( short ) (column - 1));
                }
                ColumnInfoRecord nci = ( ColumnInfoRecord ) createColInfo();

                nci.setFirstColumn(column);
                nci.setLastColumn(column);
                nci.setOptions(ci.getOptions());
                nci.setXFIndex(ci.getXFIndex());
                setColumnInfoFields( nci, xfIndex, width, level, hidden, collapsed );

                insertColumn(k, nci);
            }
            else
            {
                //split to 3 records
                short lastcolumn = ci.getLastColumn();
                ci.setLastColumn(( short ) (column - 1));

                ColumnInfoRecord nci = ( ColumnInfoRecord ) createColInfo();
                nci.setFirstColumn(column);
                nci.setLastColumn(column);
                nci.setOptions(ci.getOptions());
                nci.setXFIndex(ci.getXFIndex());
                setColumnInfoFields( nci, xfIndex, width, level, hidden, collapsed );
                insertColumn(++k, nci);

                nci = ( ColumnInfoRecord ) createColInfo();
                nci.setFirstColumn((short)(column+1));
                nci.setLastColumn(lastcolumn);
                nci.setOptions(ci.getOptions());
                nci.setXFIndex(ci.getXFIndex());
                nci.setColumnWidth(ci.getColumnWidth());
                insertColumn(++k, nci);
            }
        }
        else
        {

            // okay so there ISN'T a column info record that cover's this column so lets create one!
            ColumnInfoRecord nci = ( ColumnInfoRecord ) createColInfo();

            nci.setFirstColumn(column);
            nci.setLastColumn(column);
            setColumnInfoFields( nci, xfIndex, width, level, hidden, collapsed );
            insertColumn(k, nci);
        }
    }

    /**
     * Sets all non null fields into the <code>ci</code> parameter.
     */
    private void setColumnInfoFields( ColumnInfoRecord ci, Short xfStyle, Short width, Integer level, Boolean hidden, Boolean collapsed )
    {
	if (xfStyle != null)
	    ci.setXFIndex(xfStyle.shortValue());
        if (width != null)
            ci.setColumnWidth(width.shortValue());
        if (level != null)
            ci.setOutlineLevel( level.shortValue() );
        if (hidden != null)
            ci.setHidden( hidden.booleanValue() );
        if (collapsed != null)
            ci.setCollapsed( collapsed.booleanValue() );
    }

    public int findColumnIdx(int column, int fromIdx)
    {
        if (column < 0)
            throw new IllegalArgumentException( "column parameter out of range: " + column );
        if (fromIdx < 0)
            throw new IllegalArgumentException( "fromIdx parameter out of range: " + fromIdx );

        ColumnInfoRecord ci;
        for (int k = fromIdx; k < records.size(); k++)
        {
            ci = ( ColumnInfoRecord ) records.get(k);
            if ((ci.getFirstColumn() <= column)
                    && (column <= ci.getLastColumn()))
            {
                return k;
            }
            ci = null;
        }
        return -1;
    }

    public void collapseColInfoRecords( int columnIdx )
    {
        if (columnIdx == 0)
            return;
        ColumnInfoRecord previousCol = (ColumnInfoRecord) records.get( columnIdx - 1);
        ColumnInfoRecord currentCol = (ColumnInfoRecord) records.get( columnIdx );
        boolean adjacentColumns = previousCol.getLastColumn() == currentCol.getFirstColumn() - 1;
        if (!adjacentColumns)
            return;

        boolean columnsMatch =
                previousCol.getXFIndex() == currentCol.getXFIndex() &&
                previousCol.getOptions() == currentCol.getOptions() &&
                previousCol.getColumnWidth() == currentCol.getColumnWidth();

        if (columnsMatch)
        {
            previousCol.setLastColumn( currentCol.getLastColumn() );
            records.remove( columnIdx );
        }
    }

    /**
     * Creates an outline group for the specified columns.
     * @param fromColumn    group from this column (inclusive)
     * @param toColumn      group to this column (inclusive)
     * @param indent        if true the group will be indented by one level,
     *                      if false indenting will be removed by one level.
     */
    public void groupColumnRange(short fromColumn, short toColumn, boolean indent)
    {

        // Set the level for each column
        int fromIdx = 0;
        for (int i = fromColumn; i <= toColumn; i++)
        {
            int level = 1;
            int columnIdx = findColumnIdx( i, Math.max(0,fromIdx) );
            if (columnIdx != -1)
            {
                level = ((ColumnInfoRecord)records.get( columnIdx )).getOutlineLevel();
                if (indent) level++; else level--;
                level = Math.max(0, level);
                level = Math.min(7, level);
                fromIdx = columnIdx - 1; // subtract 1 just in case this column is collapsed later.
            }
            setColumn((short)i, null, null, new Integer(level), null, null);
            columnIdx = findColumnIdx( i, Math.max(0, fromIdx ) );
            collapseColInfoRecords( columnIdx );
        }

    }


}
