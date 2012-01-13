/*
 * ome.xml.model.StructuredAnnotations
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
 * Created by melissa via xsd-fu on 2012-01-12 20:06:01-0500
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

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/SA/2011-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(StructuredAnnotations.class);

	// -- Instance variables --


	// Property which occurs more than once
	private List<XMLAnnotation> xmlannotationList = new ArrayList<XMLAnnotation>();

	// Property which occurs more than once
	private List<FileAnnotation> fileAnnotationList = new ArrayList<FileAnnotation>();

	// Property which occurs more than once
	private List<ListAnnotation> listAnnotationList = new ArrayList<ListAnnotation>();

	// Property which occurs more than once
	private List<LongAnnotation> longAnnotationList = new ArrayList<LongAnnotation>();

	// Property which occurs more than once
	private List<DoubleAnnotation> doubleAnnotationList = new ArrayList<DoubleAnnotation>();

	// Property which occurs more than once
	private List<CommentAnnotation> commentAnnotationList = new ArrayList<CommentAnnotation>();

	// Property which occurs more than once
	private List<BooleanAnnotation> booleanAnnotationList = new ArrayList<BooleanAnnotation>();

	// Property which occurs more than once
	private List<TimestampAnnotation> timestampAnnotationList = new ArrayList<TimestampAnnotation>();

	// Property which occurs more than once
	private List<TagAnnotation> tagAnnotationList = new ArrayList<TagAnnotation>();

	// Property which occurs more than once
	private List<TermAnnotation> termAnnotationList = new ArrayList<TermAnnotation>();

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
		return xmlannotationList.size();
	}

	public List<XMLAnnotation> copyXMLAnnotationList()
	{
		return new ArrayList<XMLAnnotation>(xmlannotationList);
	}

	public XMLAnnotation getXMLAnnotation(int index)
	{
		return xmlannotationList.get(index);
	}

	public XMLAnnotation setXMLAnnotation(int index, XMLAnnotation xmlannotation)
	{
		return xmlannotationList.set(index, xmlannotation);
	}

	public void addXMLAnnotation(XMLAnnotation xmlannotation)
	{
		xmlannotationList.add(xmlannotation);
	}

	public void removeXMLAnnotation(XMLAnnotation xmlannotation)
	{
		xmlannotationList.remove(xmlannotation);
	}

	// Property which occurs more than once
	public int sizeOfFileAnnotationList()
	{
		return fileAnnotationList.size();
	}

	public List<FileAnnotation> copyFileAnnotationList()
	{
		return new ArrayList<FileAnnotation>(fileAnnotationList);
	}

	public FileAnnotation getFileAnnotation(int index)
	{
		return fileAnnotationList.get(index);
	}

	public FileAnnotation setFileAnnotation(int index, FileAnnotation fileAnnotation)
	{
		return fileAnnotationList.set(index, fileAnnotation);
	}

	public void addFileAnnotation(FileAnnotation fileAnnotation)
	{
		fileAnnotationList.add(fileAnnotation);
	}

	public void removeFileAnnotation(FileAnnotation fileAnnotation)
	{
		fileAnnotationList.remove(fileAnnotation);
	}

	// Property which occurs more than once
	public int sizeOfListAnnotationList()
	{
		return listAnnotationList.size();
	}

	public List<ListAnnotation> copyListAnnotationList()
	{
		return new ArrayList<ListAnnotation>(listAnnotationList);
	}

	public ListAnnotation getListAnnotation(int index)
	{
		return listAnnotationList.get(index);
	}

	public ListAnnotation setListAnnotation(int index, ListAnnotation listAnnotation)
	{
		return listAnnotationList.set(index, listAnnotation);
	}

	public void addListAnnotation(ListAnnotation listAnnotation)
	{
		listAnnotationList.add(listAnnotation);
	}

	public void removeListAnnotation(ListAnnotation listAnnotation)
	{
		listAnnotationList.remove(listAnnotation);
	}

	// Property which occurs more than once
	public int sizeOfLongAnnotationList()
	{
		return longAnnotationList.size();
	}

	public List<LongAnnotation> copyLongAnnotationList()
	{
		return new ArrayList<LongAnnotation>(longAnnotationList);
	}

	public LongAnnotation getLongAnnotation(int index)
	{
		return longAnnotationList.get(index);
	}

	public LongAnnotation setLongAnnotation(int index, LongAnnotation longAnnotation)
	{
		return longAnnotationList.set(index, longAnnotation);
	}

	public void addLongAnnotation(LongAnnotation longAnnotation)
	{
		longAnnotationList.add(longAnnotation);
	}

	public void removeLongAnnotation(LongAnnotation longAnnotation)
	{
		longAnnotationList.remove(longAnnotation);
	}

	// Property which occurs more than once
	public int sizeOfDoubleAnnotationList()
	{
		return doubleAnnotationList.size();
	}

	public List<DoubleAnnotation> copyDoubleAnnotationList()
	{
		return new ArrayList<DoubleAnnotation>(doubleAnnotationList);
	}

	public DoubleAnnotation getDoubleAnnotation(int index)
	{
		return doubleAnnotationList.get(index);
	}

	public DoubleAnnotation setDoubleAnnotation(int index, DoubleAnnotation doubleAnnotation)
	{
		return doubleAnnotationList.set(index, doubleAnnotation);
	}

	public void addDoubleAnnotation(DoubleAnnotation doubleAnnotation)
	{
		doubleAnnotationList.add(doubleAnnotation);
	}

	public void removeDoubleAnnotation(DoubleAnnotation doubleAnnotation)
	{
		doubleAnnotationList.remove(doubleAnnotation);
	}

	// Property which occurs more than once
	public int sizeOfCommentAnnotationList()
	{
		return commentAnnotationList.size();
	}

	public List<CommentAnnotation> copyCommentAnnotationList()
	{
		return new ArrayList<CommentAnnotation>(commentAnnotationList);
	}

	public CommentAnnotation getCommentAnnotation(int index)
	{
		return commentAnnotationList.get(index);
	}

	public CommentAnnotation setCommentAnnotation(int index, CommentAnnotation commentAnnotation)
	{
		return commentAnnotationList.set(index, commentAnnotation);
	}

	public void addCommentAnnotation(CommentAnnotation commentAnnotation)
	{
		commentAnnotationList.add(commentAnnotation);
	}

	public void removeCommentAnnotation(CommentAnnotation commentAnnotation)
	{
		commentAnnotationList.remove(commentAnnotation);
	}

	// Property which occurs more than once
	public int sizeOfBooleanAnnotationList()
	{
		return booleanAnnotationList.size();
	}

	public List<BooleanAnnotation> copyBooleanAnnotationList()
	{
		return new ArrayList<BooleanAnnotation>(booleanAnnotationList);
	}

	public BooleanAnnotation getBooleanAnnotation(int index)
	{
		return booleanAnnotationList.get(index);
	}

	public BooleanAnnotation setBooleanAnnotation(int index, BooleanAnnotation booleanAnnotation)
	{
		return booleanAnnotationList.set(index, booleanAnnotation);
	}

	public void addBooleanAnnotation(BooleanAnnotation booleanAnnotation)
	{
		booleanAnnotationList.add(booleanAnnotation);
	}

	public void removeBooleanAnnotation(BooleanAnnotation booleanAnnotation)
	{
		booleanAnnotationList.remove(booleanAnnotation);
	}

	// Property which occurs more than once
	public int sizeOfTimestampAnnotationList()
	{
		return timestampAnnotationList.size();
	}

	public List<TimestampAnnotation> copyTimestampAnnotationList()
	{
		return new ArrayList<TimestampAnnotation>(timestampAnnotationList);
	}

	public TimestampAnnotation getTimestampAnnotation(int index)
	{
		return timestampAnnotationList.get(index);
	}

	public TimestampAnnotation setTimestampAnnotation(int index, TimestampAnnotation timestampAnnotation)
	{
		return timestampAnnotationList.set(index, timestampAnnotation);
	}

	public void addTimestampAnnotation(TimestampAnnotation timestampAnnotation)
	{
		timestampAnnotationList.add(timestampAnnotation);
	}

	public void removeTimestampAnnotation(TimestampAnnotation timestampAnnotation)
	{
		timestampAnnotationList.remove(timestampAnnotation);
	}

	// Property which occurs more than once
	public int sizeOfTagAnnotationList()
	{
		return tagAnnotationList.size();
	}

	public List<TagAnnotation> copyTagAnnotationList()
	{
		return new ArrayList<TagAnnotation>(tagAnnotationList);
	}

	public TagAnnotation getTagAnnotation(int index)
	{
		return tagAnnotationList.get(index);
	}

	public TagAnnotation setTagAnnotation(int index, TagAnnotation tagAnnotation)
	{
		return tagAnnotationList.set(index, tagAnnotation);
	}

	public void addTagAnnotation(TagAnnotation tagAnnotation)
	{
		tagAnnotationList.add(tagAnnotation);
	}

	public void removeTagAnnotation(TagAnnotation tagAnnotation)
	{
		tagAnnotationList.remove(tagAnnotation);
	}

	// Property which occurs more than once
	public int sizeOfTermAnnotationList()
	{
		return termAnnotationList.size();
	}

	public List<TermAnnotation> copyTermAnnotationList()
	{
		return new ArrayList<TermAnnotation>(termAnnotationList);
	}

	public TermAnnotation getTermAnnotation(int index)
	{
		return termAnnotationList.get(index);
	}

	public TermAnnotation setTermAnnotation(int index, TermAnnotation termAnnotation)
	{
		return termAnnotationList.set(index, termAnnotation);
	}

	public void addTermAnnotation(TermAnnotation termAnnotation)
	{
		termAnnotationList.add(termAnnotation);
	}

	public void removeTermAnnotation(TermAnnotation termAnnotation)
	{
		termAnnotationList.remove(termAnnotation);
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

		if (xmlannotationList != null)
		{
			// Element property XMLAnnotation which is complex (has
			// sub-elements) and occurs more than once
			for (XMLAnnotation xmlannotationList_value : xmlannotationList)
			{
				StructuredAnnotations_element.appendChild(xmlannotationList_value.asXMLElement(document));
			}
		}
		if (fileAnnotationList != null)
		{
			// Element property FileAnnotation which is complex (has
			// sub-elements) and occurs more than once
			for (FileAnnotation fileAnnotationList_value : fileAnnotationList)
			{
				StructuredAnnotations_element.appendChild(fileAnnotationList_value.asXMLElement(document));
			}
		}
		if (listAnnotationList != null)
		{
			// Element property ListAnnotation which is complex (has
			// sub-elements) and occurs more than once
			for (ListAnnotation listAnnotationList_value : listAnnotationList)
			{
				StructuredAnnotations_element.appendChild(listAnnotationList_value.asXMLElement(document));
			}
		}
		if (longAnnotationList != null)
		{
			// Element property LongAnnotation which is complex (has
			// sub-elements) and occurs more than once
			for (LongAnnotation longAnnotationList_value : longAnnotationList)
			{
				StructuredAnnotations_element.appendChild(longAnnotationList_value.asXMLElement(document));
			}
		}
		if (doubleAnnotationList != null)
		{
			// Element property DoubleAnnotation which is complex (has
			// sub-elements) and occurs more than once
			for (DoubleAnnotation doubleAnnotationList_value : doubleAnnotationList)
			{
				StructuredAnnotations_element.appendChild(doubleAnnotationList_value.asXMLElement(document));
			}
		}
		if (commentAnnotationList != null)
		{
			// Element property CommentAnnotation which is complex (has
			// sub-elements) and occurs more than once
			for (CommentAnnotation commentAnnotationList_value : commentAnnotationList)
			{
				StructuredAnnotations_element.appendChild(commentAnnotationList_value.asXMLElement(document));
			}
		}
		if (booleanAnnotationList != null)
		{
			// Element property BooleanAnnotation which is complex (has
			// sub-elements) and occurs more than once
			for (BooleanAnnotation booleanAnnotationList_value : booleanAnnotationList)
			{
				StructuredAnnotations_element.appendChild(booleanAnnotationList_value.asXMLElement(document));
			}
		}
		if (timestampAnnotationList != null)
		{
			// Element property TimestampAnnotation which is complex (has
			// sub-elements) and occurs more than once
			for (TimestampAnnotation timestampAnnotationList_value : timestampAnnotationList)
			{
				StructuredAnnotations_element.appendChild(timestampAnnotationList_value.asXMLElement(document));
			}
		}
		if (tagAnnotationList != null)
		{
			// Element property TagAnnotation which is complex (has
			// sub-elements) and occurs more than once
			for (TagAnnotation tagAnnotationList_value : tagAnnotationList)
			{
				StructuredAnnotations_element.appendChild(tagAnnotationList_value.asXMLElement(document));
			}
		}
		if (termAnnotationList != null)
		{
			// Element property TermAnnotation which is complex (has
			// sub-elements) and occurs more than once
			for (TermAnnotation termAnnotationList_value : termAnnotationList)
			{
				StructuredAnnotations_element.appendChild(termAnnotationList_value.asXMLElement(document));
			}
		}
		return super.asXMLElement(document, StructuredAnnotations_element);
	}
}
