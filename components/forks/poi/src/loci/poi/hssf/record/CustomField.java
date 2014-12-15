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

public interface CustomField
        extends Cloneable
{
    /**
     * @return  The size of this field in bytes.  This operation is not valid
     *          until after the call to <code>fillField()</code>
     */
    int getSize();

    /**
     * Populates this fields data from the byte array passed in.
     * @param in the RecordInputstream to read the record from
     */
    int fillField(RecordInputStream in);

    /**
     * Appends the string representation of this field to the supplied
     * StringBuffer.
     *
     * @param str   The string buffer to append to.
     */
    void toString(StringBuffer str);

    /**
     * Converts this field to it's byte array form.
     * @param offset    The offset into the byte array to start writing to.
     * @param data      The data array to write to.
     * @return  The number of bytes written.
     */
    int serializeField(int offset, byte[] data);


}
