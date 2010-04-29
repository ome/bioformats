/*
 * ome.xml.r201004.FileAnnotation
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
 * Created by callan via xsd-fu on 2010-04-29 16:39:29+0100
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r201004;

import java.util.ArrayList;
import java.util.List;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ome.xml.r201004.enums.*;
import ome.xml.r201004.primitives.*;

public class FileAnnotation extends Annotation
{
	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/SA/2010-04";

	// -- Instance variables --

	// Property
	private BinaryFile binaryFile;

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
			System.err.println(String.format(
					"WARNING: Expecting node name of FileAnnotation got %s",
					tagName));
			// TODO: Should be its own Exception
			//throw new RuntimeException(String.format(
			//		"Expecting node name of FileAnnotation got %s",
			//		tagName));
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
	}

	// -- FileAnnotation API methods --

	public void link(Reference reference, OMEModelObject o)
	{
		// TODO: Should be its own Exception
		throw new RuntimeException(
				"Unable to handle reference of type: " + reference.getClass());
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
		return super.asXMLElement(document, FileAnnotation_element);
	}
}
