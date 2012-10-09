/*
 * ome.xml.model.FileAnnotation
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) @year@ Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee,
 *      University of Wisconsin-Madison
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *-----------------------------------------------------------------------------
 */

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by melissa via xsd-fu on 2012-09-10 13:40:21-0400
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.model;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ome.xml.model.enums.*;
import ome.xml.model.primitives.*;

public class FileAnnotation extends TypeAnnotation
{
	// Base: TypeAnnotation -- Name: FileAnnotation -- Type: FileAnnotation -- javaBase: TypeAnnotation -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/SA/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(FileAnnotation.class);

	// -- Instance variables --


	// Property
	private BinaryFile binaryFile;

	// Back reference StructuredAnnotations_BackReference
	private StructuredAnnotations structuredAnnotations;

	// -- Constructors --

	/** Default constructor. */
	public FileAnnotation()
	{
		super();
	}

	/** 
	 * Constructs FileAnnotation recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public FileAnnotation(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from FileAnnotation specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates FileAnnotation recursively from an XML DOM tree. <b>NOTE:</b> No
	 * properties are removed, only added or updated.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public void update(Element element, OMEModel model)
	    throws EnumerationException
	{
		super.update(element, model);
		String tagName = element.getTagName();
		if (!"FileAnnotation".equals(tagName))
		{
			LOGGER.debug("Expecting node name of FileAnnotation got {}", tagName);
		}
		List<Element> BinaryFile_nodeList =
				getChildrenByTagName(element, "BinaryFile");
		if (BinaryFile_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"BinaryFile node list size %d != 1",
					BinaryFile_nodeList.size()));
		}
		else if (BinaryFile_nodeList.size() != 0)
		{
			// Element property BinaryFile which is complex (has
			// sub-elements)
			setBinaryFile(new BinaryFile(
					(Element) BinaryFile_nodeList.get(0), model));
		}
		// *** IGNORING *** Skipped back reference StructuredAnnotations_BackReference
	}

	// -- FileAnnotation API methods --

	public boolean link(Reference reference, OMEModelObject o)
	{
		boolean wasHandledBySuperClass = super.link(reference, o);
		if (wasHandledBySuperClass)
		{
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property
	public BinaryFile getBinaryFile()
	{
		return binaryFile;
	}

	public void setBinaryFile(BinaryFile binaryFile)
	{
		this.binaryFile = binaryFile;
	}

	// Property
	public StructuredAnnotations getStructuredAnnotations()
	{
		return structuredAnnotations;
	}

	public void setStructuredAnnotations(StructuredAnnotations structuredAnnotations_BackReference)
	{
		this.structuredAnnotations = structuredAnnotations_BackReference;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element FileAnnotation_element)
	{
		// Creating XML block for FileAnnotation

		if (FileAnnotation_element == null)
		{
			FileAnnotation_element =
					document.createElementNS(NAMESPACE, "FileAnnotation");
		}

		if (binaryFile != null)
		{
			// Element property BinaryFile which is complex (has
			// sub-elements)
			FileAnnotation_element.appendChild(binaryFile.asXMLElement(document));
		}
		if (structuredAnnotations != null)
		{
			// *** IGNORING *** Skipped back reference StructuredAnnotations_BackReference
		}
		return super.asXMLElement(document, FileAnnotation_element);
	}
}
