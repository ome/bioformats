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
package loci.poi;

/**
 * Common Parent for Text Extractors
 *  of POI Documents. 
 * You will typically find the implementation of
 *  a given format's text extractor under
 *  loci.poi.[format].extractor .
 * @see loci.poi.hssf.extractor.ExcelExtractor
 * @see loci.poi.hslf.extractor.PowerPointExtractor
 * @see loci.poi.hdgf.extractor.VisioTextExtractor
 * @see loci.poi.hwpf.extractor.WordExtractor
 */
public abstract class POITextExtractor {
	/** The POIDocument that's open */
	protected POIDocument document;

	/**
	 * Creates a new text extractor for the given document
	 */
	public POITextExtractor(POIDocument document) {
		this.document = document;
	}
	
	/**
	 * Retrieves all the text from the document.
	 * How cells, paragraphs etc are separated in the text
	 *  is implementation specific - see the javadocs for
	 *  a specific project for details.
	 * @return All the text from the document
	 */
	public abstract String getText();
}
