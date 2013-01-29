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

public class AffineTransform extends AbstractOMEModelObject
{
	// Base:  -- Name: AffineTransform -- Type: AffineTransform -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/ROI/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(AffineTransform.class);

	// -- Instance variables --


	// Property
	private Double a11;

	// Property
	private Double a10;

	// Property
	private Double a12;

	// Property
	private Double a02;

	// Property
	private Double a00;

	// Property
	private Double a01;

	// -- Constructors --

	/** Default constructor. */
	public AffineTransform()
	{
		super();
	}

	/** 
	 * Constructs AffineTransform recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public AffineTransform(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from AffineTransform specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates AffineTransform recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"AffineTransform".equals(tagName))
		{
			LOGGER.debug("Expecting node name of AffineTransform got {}", tagName);
		}
		if (element.hasAttribute("A11"))
		{
			// Attribute property A11
			setA11(Double.valueOf(
					element.getAttribute("A11")));
		}
		if (element.hasAttribute("A10"))
		{
			// Attribute property A10
			setA10(Double.valueOf(
					element.getAttribute("A10")));
		}
		if (element.hasAttribute("A12"))
		{
			// Attribute property A12
			setA12(Double.valueOf(
					element.getAttribute("A12")));
		}
		if (element.hasAttribute("A02"))
		{
			// Attribute property A02
			setA02(Double.valueOf(
					element.getAttribute("A02")));
		}
		if (element.hasAttribute("A00"))
		{
			// Attribute property A00
			setA00(Double.valueOf(
					element.getAttribute("A00")));
		}
		if (element.hasAttribute("A01"))
		{
			// Attribute property A01
			setA01(Double.valueOf(
					element.getAttribute("A01")));
		}
	}

	// -- AffineTransform API methods --

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
	public Double getA11()
	{
		return a11;
	}

	public void setA11(Double a11)
	{
		this.a11 = a11;
	}

	// Property
	public Double getA10()
	{
		return a10;
	}

	public void setA10(Double a10)
	{
		this.a10 = a10;
	}

	// Property
	public Double getA12()
	{
		return a12;
	}

	public void setA12(Double a12)
	{
		this.a12 = a12;
	}

	// Property
	public Double getA02()
	{
		return a02;
	}

	public void setA02(Double a02)
	{
		this.a02 = a02;
	}

	// Property
	public Double getA00()
	{
		return a00;
	}

	public void setA00(Double a00)
	{
		this.a00 = a00;
	}

	// Property
	public Double getA01()
	{
		return a01;
	}

	public void setA01(Double a01)
	{
		this.a01 = a01;
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element AffineTransform_element)
	{
		// Creating XML block for AffineTransform

		if (AffineTransform_element == null)
		{
			AffineTransform_element =
					document.createElementNS(NAMESPACE, "AffineTransform");
		}

		if (a11 != null)
		{
			// Attribute property A11
			AffineTransform_element.setAttribute("A11", a11.toString());
		}
		if (a10 != null)
		{
			// Attribute property A10
			AffineTransform_element.setAttribute("A10", a10.toString());
		}
		if (a12 != null)
		{
			// Attribute property A12
			AffineTransform_element.setAttribute("A12", a12.toString());
		}
		if (a02 != null)
		{
			// Attribute property A02
			AffineTransform_element.setAttribute("A02", a02.toString());
		}
		if (a00 != null)
		{
			// Attribute property A00
			AffineTransform_element.setAttribute("A00", a00.toString());
		}
		if (a01 != null)
		{
			// Attribute property A01
			AffineTransform_element.setAttribute("A01", a01.toString());
		}
		return super.asXMLElement(document, AffineTransform_element);
	}
}
