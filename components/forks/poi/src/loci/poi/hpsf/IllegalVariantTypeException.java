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
        
package loci.poi.hpsf;

import loci.poi.util.HexDump;

/**
 * <p>This exception is thrown if HPSF encounters a variant type that is illegal
 * in the current context.</p>
 * 
 * @author Rainer Klute <a
 * href="mailto:klute@rainer-klute.de">&lt;klute@rainer-klute.de&gt;</a>
 * @since 2004-06-21
 * @version $Id: IllegalVariantTypeException.java 489730 2006-12-22 19:18:16Z bayard $
 */
public class IllegalVariantTypeException extends VariantTypeException
{

    /**
     * <p>Constructor</p>
     * 
     * @param variantType The unsupported variant type
     * @param value The value
     * @param msg A message string
     */
    public IllegalVariantTypeException(final long variantType,
                                       final Object value, final String msg)
    {
        super(variantType, value, msg);
    }

    /**
     * <p>Constructor</p>
     * 
     * @param variantType The unsupported variant type
     * @param value The value
     */
    public IllegalVariantTypeException(final long variantType,
                                       final Object value)
    {
        this(variantType, value, "The variant type " + variantType + " (" +
             Variant.getVariantName(variantType) + ", " + 
             HexDump.toHex(variantType) + ") is illegal in this context.");
    }

}
