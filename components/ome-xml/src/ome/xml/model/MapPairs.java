/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2013 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package ome.xml.model;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import ome.xml.model.AbstractOMEModelObject;
import ome.xml.model.OMEModelObject;
import ome.xml.model.enums.EnumerationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author rleigh
 *
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/MapPairs.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/MapPairs.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class MapPairs extends java.util.HashMap<String, String> implements OMEModelObject {

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/SA/2013-10";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(MapPairs.class);

	/** Default constructor. */
	public MapPairs()
	{
	}

    /** Construct with initial HashMap capacity. */
    public MapPairs(int initialCapacity)
    {
        super(initialCapacity);        
    }

    /** Construct with initial HashMap capacity and load factor. */
    public MapPairs(int initialCapacity, float loadFactor)
    {
        super(initialCapacity, loadFactor);        
    }

    /** Construct from an existing Map. */
    public MapPairs(Map<String, String> m)
    {
        super(m);        
    }

	/** Copy constructor. */
	public MapPairs(MapPairs orig)
	{
        super(orig);
	}

	/**
	 * Constructs MapPairs recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public MapPairs(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

    public Element asXMLElement(Document document)
    {
		return asXMLElement(document, null);
    }

    protected Element asXMLElement(Document document, Element pairs)
    {
        if (pairs == null) {
            pairs = document.createElementNS(NAMESPACE, "MapPairs");
        }

        Iterator<Map.Entry<String, String>> entries = entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();

            Element pair = document.createElementNS(NAMESPACE, "M");
            pair.setAttribute("K", entry.getKey());
            pair.setTextContent(entry.getValue());
        }

        return pairs;
    }

    public void update(Element element, OMEModel model) throws EnumerationException
    {
		String tagName = element.getTagName();
		if (!"MapPairs".equals(tagName)) {
			LOGGER.debug("Expecting node name of TextAnnotation got {}", tagName);
		}

        for(Element child : AbstractOMEModelObject.getChildrenByTagName(element, "M")) {
            if (child.hasAttribute("K")) {
                String key = child.getAttribute("K");
                String value = child.getTextContent();
                put(key, value);
            } else {
                LOGGER.debug("MapPairs entry M does not contain key attribute K");
            }
        }
    }

    public boolean link(Reference reference, OMEModelObject o)
    {
        return false;
    }

}
