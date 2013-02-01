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
        

package loci.poi.hssf.record;

import loci.poi.util.LittleEndian;
import loci.poi.util.LittleEndianConsts;

/**
 * Write out an SST header record.
 *
 * @author Glen Stampoultzis (glens at apache.org)
 */
class SSTRecordHeader
{
    int numStrings;
    int numUniqueStrings;

    public SSTRecordHeader( int numStrings, int numUniqueStrings )
    {
        this.numStrings = numStrings;
        this.numUniqueStrings = numUniqueStrings;
    }

    /**
     * Writes out the SST record.  This consists of the sid, the record size, the number of
     * strings and the number of unique strings.
     *
     * @param data          The data buffer to write the header to.
     * @param bufferIndex   The index into the data buffer where the header should be written.
     * @param recSize       The number of records written.
     *
     * @return The bufer of bytes modified.
     */
    public int writeSSTHeader( UnicodeString.UnicodeRecordStats stats, byte[] data, int bufferIndex, int recSize )
    {
        int offset = bufferIndex;

        LittleEndian.putShort( data, offset, SSTRecord.sid );
        offset += LittleEndianConsts.SHORT_SIZE;
        stats.recordSize += LittleEndianConsts.SHORT_SIZE;
        stats.remainingSize -= LittleEndianConsts.SHORT_SIZE;
        //Delay writing the length
        stats.lastLengthPos = offset;
        offset += LittleEndianConsts.SHORT_SIZE;
        stats.recordSize += LittleEndianConsts.SHORT_SIZE;
        stats.remainingSize -= LittleEndianConsts.SHORT_SIZE;
        LittleEndian.putInt( data, offset, numStrings );
        offset += LittleEndianConsts.INT_SIZE;
        stats.recordSize += LittleEndianConsts.INT_SIZE;
        stats.remainingSize -= LittleEndianConsts.INT_SIZE;
        LittleEndian.putInt( data, offset, numUniqueStrings );
        offset += LittleEndianConsts.INT_SIZE;
        stats.recordSize += LittleEndianConsts.INT_SIZE;
        stats.remainingSize -= LittleEndianConsts.INT_SIZE;

        return offset - bufferIndex;
    }

}
