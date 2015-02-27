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

package loci.poi.hssf.model;

import loci.poi.ddf.EscherRecord;
import loci.poi.ddf.EscherClientAnchorRecord;
import loci.poi.ddf.EscherChildAnchorRecord;
import loci.poi.hssf.usermodel.HSSFAnchor;
import loci.poi.hssf.usermodel.HSSFClientAnchor;
import loci.poi.hssf.usermodel.HSSFChildAnchor;

/**
 * $Id: ConvertAnchor.java 562536 2007-08-03 18:09:41Z yegor $
 */
public class ConvertAnchor
{
    public static EscherRecord createAnchor( HSSFAnchor userAnchor )
    {
        if (userAnchor instanceof HSSFClientAnchor)
        {
            HSSFClientAnchor a = (HSSFClientAnchor) userAnchor;

            EscherClientAnchorRecord anchor = new EscherClientAnchorRecord();
            anchor.setRecordId( EscherClientAnchorRecord.RECORD_ID );
            anchor.setOptions( (short) 0x0000 );
            anchor.setFlag( (short) a.getAnchorType() );
            anchor.setCol1( (short) Math.min(a.getCol1(), a.getCol2()) );
            anchor.setDx1( (short) a.getDx1() );
            anchor.setRow1( (short) Math.min(a.getRow1(), a.getRow2()) );
            anchor.setDy1( (short) a.getDy1() );

            anchor.setCol2( (short) Math.max(a.getCol1(), a.getCol2()) );
            anchor.setDx2( (short) a.getDx2() );
            anchor.setRow2( (short) Math.max(a.getRow1(), a.getRow2()) );
            anchor.setDy2( (short) a.getDy2() );
            return anchor;
        }
        else
        {
            HSSFChildAnchor a = (HSSFChildAnchor) userAnchor;
            EscherChildAnchorRecord anchor = new EscherChildAnchorRecord();
            anchor.setRecordId( EscherChildAnchorRecord.RECORD_ID );
            anchor.setOptions( (short) 0x0000 );
            anchor.setDx1( (short) Math.min(a.getDx1(), a.getDx2()) );
            anchor.setDy1( (short) Math.min(a.getDy1(), a.getDy2()) );
            anchor.setDx2( (short) Math.max(a.getDx2(), a.getDx1()) );
            anchor.setDy2( (short) Math.max(a.getDy2(), a.getDy1()) );
            return anchor;
        }
    }

}
