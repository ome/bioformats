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

/**
 * VerticalPageBreak record that stores page breaks at columns
 * <p>
 * This class is just used so that SID compares work properly in the RecordFactory
 * @see PageBreakRecord
 * @author Danny Mui (dmui at apache dot org) 
 */
public class VerticalPageBreakRecord extends PageBreakRecord {
	
    public static final short sid = PageBreakRecord.VERTICAL_SID;
    
	/**
	 * 
	 */
	public VerticalPageBreakRecord() {
		super();
	}

	/**
	 * @param sid
	 */
	public VerticalPageBreakRecord(short sid) {
		super(sid);
	}

	/**
     * @param in the RecordInputstream to read the record from
	 */
	public VerticalPageBreakRecord(RecordInputStream in) {
		super(in);
	}

	/* (non-Javadoc)
	 * @see loci.poi.hssf.record.Record#getSid()
	 */
	public short getSid() {
		return sid;
	}

}
