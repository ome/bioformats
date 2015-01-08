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

import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.rmi.UnexpectedException;

import loci.poi.hpsf.wellknown.SectionIDMap;

/**
 * <p>Factory class to create instances of {@link SummaryInformation},
 * {@link DocumentSummaryInformation} and {@link PropertySet}.</p>
 *
 * @author Rainer Klute <a
 * href="mailto:klute@rainer-klute.de">&lt;klute@rainer-klute.de&gt;</a>
 * @version $Id: PropertySetFactory.java 489730 2006-12-22 19:18:16Z bayard $
 * @since 2002-02-09
 */
public class PropertySetFactory
{

    /**
     * <p>Creates the most specific {@link PropertySet} from an {@link
     * InputStream}. This is preferrably a {@link
     * DocumentSummaryInformation} or a {@link SummaryInformation}. If
     * the specified {@link InputStream} does not contain a property
     * set stream, an exception is thrown and the {@link InputStream}
     * is repositioned at its beginning.</p>
     *
     * @param stream Contains the property set stream's data.
     * @return The created {@link PropertySet}.
     * @throws NoPropertySetStreamException if the stream does not
     * contain a property set.
     * @throws MarkUnsupportedException if the stream does not support
     * the <code>mark</code> operation.
     * @throws IOException if some I/O problem occurs.
     * @exception UnsupportedEncodingException if the specified codepage is not
     * supported.
     */
    public static PropertySet create(final InputStream stream)
        throws NoPropertySetStreamException, MarkUnsupportedException,
               UnsupportedEncodingException, IOException
    {
        final PropertySet ps = new PropertySet(stream);
        try
        {
            if (ps.isSummaryInformation())
                return new SummaryInformation(ps);
            else if (ps.isDocumentSummaryInformation())
                return new DocumentSummaryInformation(ps);
            else
                return ps;
        }
        catch (UnexpectedPropertySetTypeException ex)
        {
            /* This exception will never be throws because we already checked
             * explicitly for this case above. */
            throw new UnexpectedException(ex.toString());
        }
    }



    /**
     * <p>Creates a new summary information.</p>
     *
     * @return the new summary information.
     */
    public static SummaryInformation newSummaryInformation()
    {
        final MutablePropertySet ps = new MutablePropertySet();
        final MutableSection s = (MutableSection) ps.getFirstSection();
        s.setFormatID(SectionIDMap.SUMMARY_INFORMATION_ID);
        try
        {
            return new SummaryInformation(ps);
        }
        catch (UnexpectedPropertySetTypeException ex)
        {
            /* This should never happen. */
            throw new HPSFRuntimeException(ex);
        }
    }



    /**
     * <p>Creates a new document summary information.</p>
     *
     * @return the new document summary information.
     */
    public static DocumentSummaryInformation newDocumentSummaryInformation()
    {
        final MutablePropertySet ps = new MutablePropertySet();
        final MutableSection s = (MutableSection) ps.getFirstSection();
        s.setFormatID(SectionIDMap.DOCUMENT_SUMMARY_INFORMATION_ID[0]);
        try
        {
            return new DocumentSummaryInformation(ps);
        }
        catch (UnexpectedPropertySetTypeException ex)
        {
            /* This should never happen. */
            throw new HPSFRuntimeException(ex);
        }
    }

}
