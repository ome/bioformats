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
        

package loci.poi.poifs.property;

import loci.poi.poifs.filesystem.POIFSDocument;

/**
 * Trivial extension of Property for POIFSDocuments
 *
 * @author Marc Johnson (mjohnson at apache dot org)
 */

public class DocumentProperty
    extends Property
{

    // the POIFSDocument this property is associated with
    private POIFSDocument _document;

    /**
     * Constructor
     *
     * @param name POIFSDocument name
     * @param size POIFSDocument size
     */

    public DocumentProperty(final String name, final int size)
    {
        super();
        _document = null;
        setName(name);
        setSize(size);
        setNodeColor(_NODE_BLACK);   // simplification
        setPropertyType(PropertyConstants.DOCUMENT_TYPE);
    }

    /**
     * reader constructor
     *
     * @param index index number
     * @param array byte data
     * @param offset offset into byte data
     */

    protected DocumentProperty(final int index, final byte [] array,
                               final int offset)
    {
        super(index, array, offset);
        _document = null;
    }

    /**
     * set the POIFSDocument
     *
     * @param doc the associated POIFSDocument
     */

    public void setDocument(POIFSDocument doc)
    {
        _document = doc;
    }

    /**
     * get the POIFSDocument
     *
     * @return the associated document
     */

    public POIFSDocument getDocument()
    {
        return _document;
    }

    /* ********** START extension of Property ********** */

    /**
     * give method more visibility
     *
     * @return true if this property should use small blocks
     */

    public boolean shouldUseSmallBlocks()
    {
        return super.shouldUseSmallBlocks();
    }

    /**
     * @return true if a directory type Property
     */

    public boolean isDirectory()
    {
        return false;
    }

    /**
     * Perform whatever activities need to be performed prior to
     * writing
     */

    protected void preWrite()
    {

        // do nothing
    }

    /* **********  END  extension of Property ********** */
}   // end public class DocumentProperty

