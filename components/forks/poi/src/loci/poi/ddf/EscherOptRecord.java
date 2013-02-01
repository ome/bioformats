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
        
package loci.poi.ddf;

import loci.poi.util.LittleEndian;
import loci.poi.util.HexDump;

import java.util.*;
import java.io.IOException;

/**
 * The opt record is used to store property values for a shape.  It is the key to determining
 * the attributes of a shape.  Properties can be of two types: simple or complex.  Simple types
 * are fixed length.  Complex properties are variable length.
 *
 * @author Glen Stampoultzis
 */
public class EscherOptRecord
        extends EscherRecord
{
    public static final short RECORD_ID = (short) 0xF00B;
    public static final String RECORD_DESCRIPTION = "msofbtOPT";

    private List properties = new ArrayList();

    /**
     * This method deserializes the record from a byte array.
     *
     * @param data          The byte array containing the escher record information
     * @param offset        The starting offset into <code>data</code>.
     * @param recordFactory May be null since this is not a container record.
     * @return The number of bytes read from the byte array.
     */
    public int fillFields( byte[] data, int offset, EscherRecordFactory recordFactory )
    {
        int bytesRemaining = readHeader( data, offset );
        int pos = offset + 8;

        EscherPropertyFactory f = new EscherPropertyFactory();
        properties = f.createProperties( data, pos, getInstance() );
        return bytesRemaining + 8;
    }

    /**
     * This method serializes this escher record into a byte array.
     *
     * @param offset   The offset into <code>data</code> to start writing the record data to.
     * @param data     The byte array to serialize to.
     * @param listener A listener to retrieve start and end callbacks.  Use a <code>NullEscherSerailizationListener</code> to ignore these events.
     *
     * @return The number of bytes written.
     * @see NullEscherSerializationListener
     */
    public int serialize( int offset, byte[] data, EscherSerializationListener listener )
    {
        listener.beforeRecordSerialize( offset, getRecordId(), this );

        LittleEndian.putShort( data, offset, getOptions() );
        LittleEndian.putShort( data, offset + 2, getRecordId() );
        LittleEndian.putInt( data, offset + 4, getPropertiesSize() );
        int pos = offset + 8;
        for ( Iterator iterator = properties.iterator(); iterator.hasNext(); )
        {
            EscherProperty escherProperty = (EscherProperty) iterator.next();
            pos += escherProperty.serializeSimplePart( data, pos );
        }
        for ( Iterator iterator = properties.iterator(); iterator.hasNext(); )
        {
            EscherProperty escherProperty = (EscherProperty) iterator.next();
            pos += escherProperty.serializeComplexPart( data, pos );
        }
        listener.afterRecordSerialize( pos, getRecordId(), pos - offset, this );
        return pos - offset;
    }

    /**
     * Returns the number of bytes that are required to serialize this record.
     *
     * @return Number of bytes
     */
    public int getRecordSize()
    {
        return 8 + getPropertiesSize();
    }

    /**
     * Automatically recalculate the correct option
     */
    public short getOptions()
    {
        setOptions( (short) ( ( properties.size() << 4 ) | 0x3 ) );
        return super.getOptions();
    }

    /**
     * The short name for this record
     */
    public String getRecordName()
    {
        return "Opt";
    }

    private int getPropertiesSize()
    {
        int totalSize = 0;
        for ( Iterator iterator = properties.iterator(); iterator.hasNext(); )
        {
            EscherProperty escherProperty = (EscherProperty) iterator.next();
            totalSize += escherProperty.getPropertySize();
        }
        return totalSize;
    }

    /**
     * Retrieve the string representation of this record.
     */
    public String toString()
    {
        String nl = System.getProperty( "line.separator" );
        StringBuffer propertiesBuf = new StringBuffer();
        for ( Iterator iterator = properties.iterator(); iterator.hasNext(); )
            propertiesBuf.append( "    "
                    + iterator.next().toString()
                    + nl );

        return "loci.poi.ddf.EscherOptRecord:" + nl +
                "  isContainer: " + isContainerRecord() + nl +
                "  options: 0x" + HexDump.toHex( getOptions() ) + nl +
                "  recordId: 0x" + HexDump.toHex( getRecordId() ) + nl +
                "  numchildren: " + getChildRecords().size() + nl +
                "  properties:" + nl +
                propertiesBuf.toString();
    }

    /**
     * The list of properties stored by this record.
     */
    public List getEscherProperties()
    {
        return properties;
    }

    /**
     * The list of properties stored by this record.
     */
    public EscherProperty getEscherProperty( int index )
    {
        return (EscherProperty) properties.get( index );
    }

    /**
     * Add a property to this record.
     */
    public void addEscherProperty( EscherProperty prop )
    {
        properties.add( prop );
    }

    /**
     * Records should be sorted by property number before being stored.
     */
    public void sortProperties()
    {
        Collections.sort( properties, new Comparator()
        {
            public int compare( Object o1, Object o2 )
            {
                EscherProperty p1 = (EscherProperty) o1;
                EscherProperty p2 = (EscherProperty) o2;
                return new Short( p1.getPropertyNumber() ).compareTo( new Short( p2.getPropertyNumber() ) );
            }
        } );
    }


}
