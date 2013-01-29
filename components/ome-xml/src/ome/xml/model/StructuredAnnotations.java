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

public class StructuredAnnotations extends AbstractOMEModelObject
{
	// Base:  -- Name: StructuredAnnotations -- Type: StructuredAnnotations -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/SA/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(StructuredAnnotations.class);

	// -- Instance variables --


	// Property which occurs more than once
	private List<XMLAnnotation> xmlAnnotations = new ArrayList<XMLAnnotation>();

	// Property which occurs more than once
	private List<FileAnnotation> fileAnnotations = new ArrayList<FileAnnotation>();

	// Property which occurs more than once
	private List<ListAnnotation> listAnnotations = new ArrayList<ListAnnotation>();

	// Property which occurs more than once
	private List<LongAnnotation> longAnnotations = new ArrayList<LongAnnotation>();

	// Property which occurs more than once
	private List<DoubleAnnotation> doubleAnnotations = new ArrayList<DoubleAnnotation>();

	// Property which occurs more than once
	private List<CommentAnnotation> commentAnnotations = new ArrayList<CommentAnnotation>();

	// Property which occurs more than once
	private List<BooleanAnnotation> booleanAnnotations = new ArrayList<BooleanAnnotation>();

	// Property which occurs more than once
	private List<TimestampAnnotation> timestampAnnotations = new ArrayList<TimestampAnnotation>();

	// Property which occurs more than once
	private List<TagAnnotation> tagAnnotations = new ArrayList<TagAnnotation>();

	// Property which occurs more than once
	private List<TermAnnotation> termAnnotations = new ArrayList<TermAnnotation>();

	// -- Constructors --

	/** Default constructor. */
	public StructuredAnnotations()
	{
		super();
	}

	/** 
	 * Constructs StructuredAnnotations recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public StructuredAnnotations(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from StructuredAnnotations specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates StructuredAnnotations recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"StructuredAnnotations".equals(tagName))
		{
			LOGGER.debug("Expecting node name of StructuredAnnotations got {}", tagName);
		}
		// Element property XMLAnnotation which is complex (has
		// sub-elements) and occurs more than once
		List<Element> XMLAnnotation_nodeList =
				getChildrenByTagName(element, "XMLAnnotation");
		for (Element XMLAnnotation_element : XMLAnnotation_nodeList)
		{
			addXMLAnnotation(
					new XMLAnnotation(XMLAnnotation_element, model));
		}
		// Element property FileAnnotation which is complex (has
		// sub-elements) and occurs more than once
		List<Element> FileAnnotation_nodeList =
				getChildrenByTagName(element, "FileAnnotation");
		for (Element FileAnnotation_element : FileAnnotation_nodeList)
		{
			addFileAnnotation(
					new FileAnnotation(FileAnnotation_element, model));
		}
		// Element property ListAnnotation which is complex (has
		// sub-elements) and occurs more than once
		List<Element> ListAnnotation_nodeList =
				getChildrenByTagName(element, "ListAnnotation");
		for (Element ListAnnotation_element : ListAnnotation_nodeList)
		{
			addListAnnotation(
					new ListAnnotation(ListAnnotation_element, model));
		}
		// Element property LongAnnotation which is complex (has
		// sub-elements) and occurs more than once
		List<Element> LongAnnotation_nodeList =
				getChildrenByTagName(element, "LongAnnotation");
		for (Element LongAnnotation_element : LongAnnotation_nodeList)
		{
			addLongAnnotation(
					new LongAnnotation(LongAnnotation_element, model));
		}
		// Element property DoubleAnnotation which is complex (has
		// sub-elements) and occurs more than once
		List<Element> DoubleAnnotation_nodeList =
				getChildrenByTagName(element, "DoubleAnnotation");
		for (Element DoubleAnnotation_element : DoubleAnnotation_nodeList)
		{
			addDoubleAnnotation(
					new DoubleAnnotation(DoubleAnnotation_element, model));
		}
		// Element property CommentAnnotation which is complex (has
		// sub-elements) and occurs more than once
		List<Element> CommentAnnotation_nodeList =
				getChildrenByTagName(element, "CommentAnnotation");
		for (Element CommentAnnotation_element : CommentAnnotation_nodeList)
		{
			addCommentAnnotation(
					new CommentAnnotation(CommentAnnotation_element, model));
		}
		// Element property BooleanAnnotation which is complex (has
		// sub-elements) and occurs more than once
		List<Element> BooleanAnnotation_nodeList =
				getChildrenByTagName(element, "BooleanAnnotation");
		for (Element BooleanAnnotation_element : BooleanAnnotation_nodeList)
		{
			addBooleanAnnotation(
					new BooleanAnnotation(BooleanAnnotation_element, model));
		}
		// Element property TimestampAnnotation which is complex (has
		// sub-elements) and occurs more than once
		List<Element> TimestampAnnotation_nodeList =
				getChildrenByTagName(element, "TimestampAnnotation");
		for (Element TimestampAnnotation_element : TimestampAnnotation_nodeList)
		{
			addTimestampAnnotation(
					new TimestampAnnotation(TimestampAnnotation_element, model));
		}
		// Element property TagAnnotation which is complex (has
		// sub-elements) and occurs more than once
		List<Element> TagAnnotation_nodeList =
				getChildrenByTagName(element, "TagAnnotation");
		for (Element TagAnnotation_element : TagAnnotation_nodeList)
		{
			addTagAnnotation(
					new TagAnnotation(TagAnnotation_element, model));
		}
		// Element property TermAnnotation which is complex (has
		// sub-elements) and occurs more than once
		List<Element> TermAnnotation_nodeList =
				getChildrenByTagName(element, "TermAnnotation");
		for (Element TermAnnotation_element : TermAnnotation_nodeList)
		{
			addTermAnnotation(
					new TermAnnotation(TermAnnotation_element, model));
		}
	}

	// -- StructuredAnnotations API methods --

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


	// Property which occurs more than once
	public int sizeOfXMLAnnotationList()
	{
		return xmlAnnotations.size();
	}

	public List<XMLAnnotation> copyXMLAnnotationList()
	{
		return new ArrayList<XMLAnnotation>(xmlAnnotations);
	}

	public XMLAnnotation getXMLAnnotation(int index)
	{
		return xmlAnnotations.get(index);
	}

	public XMLAnnotation setXMLAnnotation(int index, XMLAnnotation xmlAnnotation)
	{
        xmlAnnotation.setStructuredAnnotations(this);
		return xmlAnnotations.set(index, xmlAnnotation);
	}

	public void addXMLAnnotation(XMLAnnotation xmlAnnotation)
	{
        xmlAnnotation.setStructuredAnnotations(this);
		xmlAnnotations.add(xmlAnnotation);
	}

	public void removeXMLAnnotation(XMLAnnotation xmlAnnotation)
	{
		xmlAnnotations.remove(xmlAnnotation);
	}

	// Property which occurs more than once
	public int sizeOfFileAnnotationList()
	{
		return fileAnnotations.size();
	}

	public List<FileAnnotation> copyFileAnnotationList()
	{
		return new ArrayList<FileAnnotation>(fileAnnotations);
	}

	public FileAnnotation getFileAnnotation(int index)
	{
		return fileAnnotations.get(index);
	}

	public FileAnnotation setFileAnnotation(int index, FileAnnotation fileAnnotation)
	{
        fileAnnotation.setStructuredAnnotations(this);
		return fileAnnotations.set(index, fileAnnotation);
	}

	public void addFileAnnotation(FileAnnotation fileAnnotation)
	{
        fileAnnotation.setStructuredAnnotations(this);
		fileAnnotations.add(fileAnnotation);
	}

	public void removeFileAnnotation(FileAnnotation fileAnnotation)
	{
		fileAnnotations.remove(fileAnnotation);
	}

	// Property which occurs more than once
	public int sizeOfListAnnotationList()
	{
		return listAnnotations.size();
	}

	public List<ListAnnotation> copyListAnnotationList()
	{
		return new ArrayList<ListAnnotation>(listAnnotations);
	}

	public ListAnnotation getListAnnotation(int index)
	{
		return listAnnotations.get(index);
	}

	public ListAnnotation setListAnnotation(int index, ListAnnotation listAnnotation)
	{
        listAnnotation.setStructuredAnnotations(this);
		return listAnnotations.set(index, listAnnotation);
	}

	public void addListAnnotation(ListAnnotation listAnnotation)
	{
        listAnnotation.setStructuredAnnotations(this);
		listAnnotations.add(listAnnotation);
	}

	public void removeListAnnotation(ListAnnotation listAnnotation)
	{
		listAnnotations.remove(listAnnotation);
	}

	// Property which occurs more than once
	public int sizeOfLongAnnotationList()
	{
		return longAnnotations.size();
	}

	public List<LongAnnotation> copyLongAnnotationList()
	{
		return new ArrayList<LongAnnotation>(longAnnotations);
	}

	public LongAnnotation getLongAnnotation(int index)
	{
		return longAnnotations.get(index);
	}

	public LongAnnotation setLongAnnotation(int index, LongAnnotation longAnnotation)
	{
        longAnnotation.setStructuredAnnotations(this);
		return longAnnotations.set(index, longAnnotation);
	}

	public void addLongAnnotation(LongAnnotation longAnnotation)
	{
        longAnnotation.setStructuredAnnotations(this);
		longAnnotations.add(longAnnotation);
	}

	public void removeLongAnnotation(LongAnnotation longAnnotation)
	{
		longAnnotations.remove(longAnnotation);
	}

	// Property which occurs more than once
	public int sizeOfDoubleAnnotationList()
	{
		return doubleAnnotations.size();
	}

	public List<DoubleAnnotation> copyDoubleAnnotationList()
	{
		return new ArrayList<DoubleAnnotation>(doubleAnnotations);
	}

	public DoubleAnnotation getDoubleAnnotation(int index)
	{
		return doubleAnnotations.get(index);
	}

	public DoubleAnnotation setDoubleAnnotation(int index, DoubleAnnotation doubleAnnotation)
	{
        doubleAnnotation.setStructuredAnnotations(this);
		return doubleAnnotations.set(index, doubleAnnotation);
	}

	public void addDoubleAnnotation(DoubleAnnotation doubleAnnotation)
	{
        doubleAnnotation.setStructuredAnnotations(this);
		doubleAnnotations.add(doubleAnnotation);
	}

	public void removeDoubleAnnotation(DoubleAnnotation doubleAnnotation)
	{
		doubleAnnotations.remove(doubleAnnotation);
	}

	// Property which occurs more than once
	public int sizeOfCommentAnnotationList()
	{
		return commentAnnotations.size();
	}

	public List<CommentAnnotation> copyCommentAnnotationList()
	{
		return new ArrayList<CommentAnnotation>(commentAnnotations);
	}

	public CommentAnnotation getCommentAnnotation(int index)
	{
		return commentAnnotations.get(index);
	}

	public CommentAnnotation setCommentAnnotation(int index, CommentAnnotation commentAnnotation)
	{
        commentAnnotation.setStructuredAnnotations(this);
		return commentAnnotations.set(index, commentAnnotation);
	}

	public void addCommentAnnotation(CommentAnnotation commentAnnotation)
	{
        commentAnnotation.setStructuredAnnotations(this);
		commentAnnotations.add(commentAnnotation);
	}

	public void removeCommentAnnotation(CommentAnnotation commentAnnotation)
	{
		commentAnnotations.remove(commentAnnotation);
	}

	// Property which occurs more than once
	public int sizeOfBooleanAnnotationList()
	{
		return booleanAnnotations.size();
	}

	public List<BooleanAnnotation> copyBooleanAnnotationList()
	{
		return new ArrayList<BooleanAnnotation>(booleanAnnotations);
	}

	public BooleanAnnotation getBooleanAnnotation(int index)
	{
		return booleanAnnotations.get(index);
	}

	public BooleanAnnotation setBooleanAnnotation(int index, BooleanAnnotation booleanAnnotation)
	{
        booleanAnnotation.setStructuredAnnotations(this);
		return booleanAnnotations.set(index, booleanAnnotation);
	}

	public void addBooleanAnnotation(BooleanAnnotation booleanAnnotation)
	{
        booleanAnnotation.setStructuredAnnotations(this);
		booleanAnnotations.add(booleanAnnotation);
	}

	public void removeBooleanAnnotation(BooleanAnnotation booleanAnnotation)
	{
		booleanAnnotations.remove(booleanAnnotation);
	}

	// Property which occurs more than once
	public int sizeOfTimestampAnnotationList()
	{
		return timestampAnnotations.size();
	}

	public List<TimestampAnnotation> copyTimestampAnnotationList()
	{
		return new ArrayList<TimestampAnnotation>(timestampAnnotations);
	}

	public TimestampAnnotation getTimestampAnnotation(int index)
	{
		return timestampAnnotations.get(index);
	}

	public TimestampAnnotation setTimestampAnnotation(int index, TimestampAnnotation timestampAnnotation)
	{
        timestampAnnotation.setStructuredAnnotations(this);
		return timestampAnnotations.set(index, timestampAnnotation);
	}

	public void addTimestampAnnotation(TimestampAnnotation timestampAnnotation)
	{
        timestampAnnotation.setStructuredAnnotations(this);
		timestampAnnotations.add(timestampAnnotation);
	}

	public void removeTimestampAnnotation(TimestampAnnotation timestampAnnotation)
	{
		timestampAnnotations.remove(timestampAnnotation);
	}

	// Property which occurs more than once
	public int sizeOfTagAnnotationList()
	{
		return tagAnnotations.size();
	}

	public List<TagAnnotation> copyTagAnnotationList()
	{
		return new ArrayList<TagAnnotation>(tagAnnotations);
	}

	public TagAnnotation getTagAnnotation(int index)
	{
		return tagAnnotations.get(index);
	}

	public TagAnnotation setTagAnnotation(int index, TagAnnotation tagAnnotation)
	{
        tagAnnotation.setStructuredAnnotations(this);
		return tagAnnotations.set(index, tagAnnotation);
	}

	public void addTagAnnotation(TagAnnotation tagAnnotation)
	{
        tagAnnotation.setStructuredAnnotations(this);
		tagAnnotations.add(tagAnnotation);
	}

	public void removeTagAnnotation(TagAnnotation tagAnnotation)
	{
		tagAnnotations.remove(tagAnnotation);
	}

	// Property which occurs more than once
	public int sizeOfTermAnnotationList()
	{
		return termAnnotations.size();
	}

	public List<TermAnnotation> copyTermAnnotationList()
	{
		return new ArrayList<TermAnnotation>(termAnnotations);
	}

	public TermAnnotation getTermAnnotation(int index)
	{
		return termAnnotations.get(index);
	}

	public TermAnnotation setTermAnnotation(int index, TermAnnotation termAnnotation)
	{
        termAnnotation.setStructuredAnnotations(this);
		return termAnnotations.set(index, termAnnotation);
	}

	public void addTermAnnotation(TermAnnotation termAnnotation)
	{
        termAnnotation.setStructuredAnnotations(this);
		termAnnotations.add(termAnnotation);
	}

	public void removeTermAnnotation(TermAnnotation termAnnotation)
	{
		termAnnotations.remove(termAnnotation);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element StructuredAnnotations_element)
	{
		// Creating XML block for StructuredAnnotations

		if (StructuredAnnotations_element == null)
		{
			StructuredAnnotations_element =
					document.createElementNS(NAMESPACE, "StructuredAnnotations");
		}

		if (xmlAnnotations != null)
		{
			// Element property XMLAnnotation which is complex (has
			// sub-elements) and occurs more than once
			for (XMLAnnotation xmlAnnotations_value : xmlAnnotations)
			{
				StructuredAnnotations_element.appendChild(xmlAnnotations_value.asXMLElement(document));
			}
		}
		if (fileAnnotations != null)
		{
			// Element property FileAnnotation which is complex (has
			// sub-elements) and occurs more than once
			for (FileAnnotation fileAnnotations_value : fileAnnotations)
			{
				StructuredAnnotations_element.appendChild(fileAnnotations_value.asXMLElement(document));
			}
		}
		if (listAnnotations != null)
		{
			// Element property ListAnnotation which is complex (has
			// sub-elements) and occurs more than once
			for (ListAnnotation listAnnotations_value : listAnnotations)
			{
				StructuredAnnotations_element.appendChild(listAnnotations_value.asXMLElement(document));
			}
		}
		if (longAnnotations != null)
		{
			// Element property LongAnnotation which is complex (has
			// sub-elements) and occurs more than once
			for (LongAnnotation longAnnotations_value : longAnnotations)
			{
				StructuredAnnotations_element.appendChild(longAnnotations_value.asXMLElement(document));
			}
		}
		if (doubleAnnotations != null)
		{
			// Element property DoubleAnnotation which is complex (has
			// sub-elements) and occurs more than once
			for (DoubleAnnotation doubleAnnotations_value : doubleAnnotations)
			{
				StructuredAnnotations_element.appendChild(doubleAnnotations_value.asXMLElement(document));
			}
		}
		if (commentAnnotations != null)
		{
			// Element property CommentAnnotation which is complex (has
			// sub-elements) and occurs more than once
			for (CommentAnnotation commentAnnotations_value : commentAnnotations)
			{
				StructuredAnnotations_element.appendChild(commentAnnotations_value.asXMLElement(document));
			}
		}
		if (booleanAnnotations != null)
		{
			// Element property BooleanAnnotation which is complex (has
			// sub-elements) and occurs more than once
			for (BooleanAnnotation booleanAnnotations_value : booleanAnnotations)
			{
				StructuredAnnotations_element.appendChild(booleanAnnotations_value.asXMLElement(document));
			}
		}
		if (timestampAnnotations != null)
		{
			// Element property TimestampAnnotation which is complex (has
			// sub-elements) and occurs more than once
			for (TimestampAnnotation timestampAnnotations_value : timestampAnnotations)
			{
				StructuredAnnotations_element.appendChild(timestampAnnotations_value.asXMLElement(document));
			}
		}
		if (tagAnnotations != null)
		{
			// Element property TagAnnotation which is complex (has
			// sub-elements) and occurs more than once
			for (TagAnnotation tagAnnotations_value : tagAnnotations)
			{
				StructuredAnnotations_element.appendChild(tagAnnotations_value.asXMLElement(document));
			}
		}
		if (termAnnotations != null)
		{
			// Element property TermAnnotation which is complex (has
			// sub-elements) and occurs more than once
			for (TermAnnotation termAnnotations_value : termAnnotations)
			{
				StructuredAnnotations_element.appendChild(termAnnotations_value.asXMLElement(document));
			}
		}
		return super.asXMLElement(document, StructuredAnnotations_element);
	}
}
