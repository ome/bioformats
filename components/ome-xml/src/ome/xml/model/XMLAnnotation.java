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

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by rleigh via xsd-fu on 2013-02-04 15:52:35.744474
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

public class XMLAnnotation extends TextAnnotation
{
	// Base: TextAnnotation -- Name: XMLAnnotation -- Type: XMLAnnotation -- javaBase: TextAnnotation -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/SA/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(XMLAnnotation.class);

	// -- Instance variables --


	// Property
	private String value;

	// Back reference StructuredAnnotations_BackReference
	private StructuredAnnotations structuredAnnotations;

	// -- Constructors --

	/** Default constructor. */
	public XMLAnnotation()
	{
		super();
	}

	/** 
	 * Constructs XMLAnnotation recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public XMLAnnotation(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from XMLAnnotation specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates XMLAnnotation recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"XMLAnnotation".equals(tagName))
		{
			LOGGER.debug("Expecting node name of XMLAnnotation got {}", tagName);
		}
		// -- BEGIN custom content from Value property template --
		List<Element> Value_nodeList =
				getChildrenByTagName(element, "Value");
		if (Value_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Value node list size %d != 1",
					Value_nodeList.size()));
		}
		else if (Value_nodeList.size() != 0)
		{
			// Element property Value which is not complex (has no
			// sub-elements)
			java.io.StringWriter sw = new java.io.StringWriter();
			javax.xml.transform.stream.StreamResult sr = 
				new javax.xml.transform.stream.StreamResult(sw);
			javax.xml.transform.TransformerFactory tf =
				javax.xml.transform.TransformerFactory.newInstance();

			try
			{
				javax.xml.transform.Transformer t = tf.newTransformer(
					new javax.xml.transform.stream.StreamSource(
						this.getClass().getResourceAsStream("StripWhitespace.xsl")));
				t.setOutputProperty(
					javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
				t.setOutputProperty(
					javax.xml.transform.OutputKeys.INDENT, "no");
				NodeList childNodeList = Value_nodeList.get(0).getChildNodes();
				for (int i = 0; i < childNodeList.getLength(); i++)
				{
					try {
						t.transform(new javax.xml.transform.dom.DOMSource(
							childNodeList.item(i)), sr);
					}
					catch (javax.xml.transform.TransformerException te) {
						LOGGER.warn("Failed to transform node #" + i, te);
					}
				}
				setValue(sw.toString().trim());
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}

		// -- END custom content from Value property template --
		// *** IGNORING *** Skipped back reference StructuredAnnotations_BackReference
	}

	// -- XMLAnnotation API methods --

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
	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
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

	protected Element asXMLElement(Document document, Element XMLAnnotation_element)
	{
		// Creating XML block for XMLAnnotation

		if (XMLAnnotation_element == null)
		{
			XMLAnnotation_element =
					document.createElementNS(NAMESPACE, "XMLAnnotation");
		}

		if (value != null)
		{
			// -- BEGIN custom content from Value property template --
			Document Value_document = null;
			try
			{
				javax.xml.parsers.DocumentBuilderFactory factory =
					javax.xml.parsers.DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(true);
				javax.xml.parsers.DocumentBuilder parser =
					factory.newDocumentBuilder();
				org.xml.sax.InputSource is = new org.xml.sax.InputSource();
				is.setCharacterStream(new java.io.StringReader(value));
				Value_document = parser.parse(is);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
			Element value_element = 
					document.createElementNS(NAMESPACE, "Value");
			NodeList Value_subNodes = Value_document.getChildNodes();
			for (int i = 0; i < Value_subNodes.getLength(); i++)
			{
				Node Value_subNode = Value_subNodes.item(i);
				Value_subNode = document.importNode(Value_subNode, true);
				value_element.appendChild(Value_subNode);
			}
			XMLAnnotation_element.appendChild(value_element);
			// -- END custom content from Value property template --
		}
		if (structuredAnnotations != null)
		{
			// *** IGNORING *** Skipped back reference StructuredAnnotations_BackReference
		}
		return super.asXMLElement(document, XMLAnnotation_element);
	}
}
