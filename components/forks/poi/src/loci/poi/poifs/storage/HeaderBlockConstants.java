/*
 * #%L
 * Fork of Apache Jakarta POI.
 * %%
 * Copyright (C) 2008 - 2015 Open Microscopy Environment:
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
        

package loci.poi.poifs.storage;

import loci.poi.util.IntegerField;
import loci.poi.util.LittleEndian;
import loci.poi.util.LittleEndianConsts;
import loci.poi.util.LongField;
import loci.poi.util.ShortField;

/**
 * Constants used in reading/writing the Header block
 *
 * @author Marc Johnson (mjohnson at apache dot org)
 */

public interface HeaderBlockConstants
{
    public static final long _signature               = 0xE11AB1A1E011CFD0L;
    public static final int  _bat_array_offset        = 0x4c;

    // useful offsets
    public static final int  _signature_offset        = 0;
    public static final int  _bat_count_offset        = 0x2C;
    public static final int  _property_start_offset   = 0x30;
    public static final int  _sbat_start_offset       = 0x3C;
    public static final int  _sbat_block_count_offset = 0x40;
    public static final int  _xbat_start_offset       = 0x44;
    public static final int  _xbat_count_offset       = 0x48;
}   // end public interface HeaderBlockConstants

