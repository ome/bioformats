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


package loci.poi.hssf.eventusermodel;

import loci.poi.hssf.record.Record;
import loci.poi.hssf.eventusermodel.HSSFUserException;

/**
 * Interface for use with the HSSFRequest and HSSFEventFactory.  Users should create
 * a listener supporting this interface and register it with the HSSFRequest (associating
 * it with Record SID's).
 *
 * @see loci.poi.hssf.eventusermodel.HSSFEventFactory
 * @see loci.poi.hssf.eventusermodel.HSSFRequest
 * @see loci.poi.hssf.eventusermodel.HSSFUserException
 *
 * @author Carey Sublette (careysub@earthling.net)
 *
 */

public abstract class AbortableHSSFListener implements HSSFListener
{
    /**
     * This method, inherited from HSSFListener is implemented as a stub.
     * It is never called by HSSFEventFActory or HSSFRequest.
     *
     */
     
	public void processRecord(Record record)
	{
	}

   /**
	 * Process an HSSF Record. Called when a record occurs in an HSSF file. 
	 * Provides two options for halting the processing of the HSSF file.
	 *
	 * The return value provides a means of non-error termination with a 
	 * user-defined result code. A value of zero must be returned to 
	 * continue processing, any other value will halt processing by
	 * <code>HSSFEventFactory</code> with the code being passed back by 
	 * its abortable process events methods.
	 * 
	 * Error termination can be done by throwing the HSSFUserException.
	 *
	 * Note that HSSFEventFactory will not call the inherited process 
	 *
     * @return result code of zero for continued processing.
     *
	 * @throws HSSFUserException User code can throw this to abort 
	 * file processing by HSSFEventFactory and return diagnostic information.
     */
    public abstract short abortableProcessRecord(Record record) throws HSSFUserException;
}
