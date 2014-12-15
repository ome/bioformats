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
        
package loci.poi.hpsf;

/**
 * <p>This exception is thrown if a certain type of property set is
 * expected (e.g. a Document Summary Information) but the provided
 * property set is not of that type.</p>
 *
 * <p>The constructors of this class are analogous to those of its
 * superclass and documented there.</p>
 *
 * @author Rainer Klute <a
 * href="mailto:klute@rainer-klute.de">&lt;klute@rainer-klute.de&gt;</a>
 * @version $Id: UnexpectedPropertySetTypeException.java 489730 2006-12-22 19:18:16Z bayard $
 * @since 2002-02-09
 */
public class UnexpectedPropertySetTypeException extends HPSFException
{

    /**
     * <p>Creates an {@link UnexpectedPropertySetTypeException}.</p>
     */
    public UnexpectedPropertySetTypeException()
    {
        super();
    }


    /**
     * <p>Creates an {@link UnexpectedPropertySetTypeException} with a message
     * string.</p>
     *
     * @param msg The message string.
     */
    public UnexpectedPropertySetTypeException(final String msg)
    {
        super(msg);
    }


    /**
     * <p>Creates a new {@link UnexpectedPropertySetTypeException} with a
     * reason.</p>
     *
     * @param reason The reason, i.e. a throwable that indirectly
     * caused this exception.
     */
    public UnexpectedPropertySetTypeException(final Throwable reason)
    {
        super(reason);
    }


    /**
     * <p>Creates an {@link UnexpectedPropertySetTypeException} with a message
     * string and a reason.</p>
     *
     * @param msg The message string.
     * @param reason The reason, i.e. a throwable that indirectly
     * caused this exception.
     */
    public UnexpectedPropertySetTypeException(final String msg,
                                              final Throwable reason)
    {
        super(msg, reason);
    }

}
