/*
 * ome.xml.r201004.ProfileSet
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) 2010 Open Microscopy Environment
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
 * Created by callan via xsd-fu on 2010-04-19 19:23:58+0100
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r201004;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ome.xml.r201004.enums.*;

public class ProfileSet extends Object
{
	// -- Instance variables --

	// Property which occurs more than once
	private List<ImageProfile> imageProfileList = new ArrayList<ImageProfile>();

	// Property which occurs more than once
	private List<ChannelProfile> channelProfileList = new ArrayList<ChannelProfile>();

	// -- Constructors --

	/** Constructs a ProfileSet. */
	public ProfileSet()
	{
	}

	// -- ProfileSet API methods --

	// Property which occurs more than once
	public int sizeOfImageProfileList()
	{
		return imageProfileList.size();
	}

	public List<ImageProfile> copyImageProfileList()
	{
		return new ArrayList<ImageProfile>(imageProfileList);
	}

	public ImageProfile getImageProfile(int index)
	{
		return imageProfileList.get(index);
	}

	public ImageProfile setImageProfile(int index, ImageProfile imageProfile)
	{
		return imageProfileList.set(index, imageProfile);
	}

	public void addImageProfile(ImageProfile imageProfile)
	{
		imageProfileList.add(imageProfile);
	}

	public void removeImageProfile(ImageProfile imageProfile)
	{
		imageProfileList.remove(imageProfile);
	}

	// Property which occurs more than once
	public int sizeOfChannelProfileList()
	{
		return channelProfileList.size();
	}

	public List<ChannelProfile> copyChannelProfileList()
	{
		return new ArrayList<ChannelProfile>(channelProfileList);
	}

	public ChannelProfile getChannelProfile(int index)
	{
		return channelProfileList.get(index);
	}

	public ChannelProfile setChannelProfile(int index, ChannelProfile channelProfile)
	{
		return channelProfileList.set(index, channelProfile);
	}

	public void addChannelProfile(ChannelProfile channelProfile)
	{
		channelProfileList.add(channelProfile);
	}

	public void removeChannelProfile(ChannelProfile channelProfile)
	{
		channelProfileList.remove(channelProfile);
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for ProfileSet
		Element ProfileSet_element = document.createElement("ProfileSet");
		if (imageProfileList != null)
		{
			// Element property ImageProfile which is complex (has
			// sub-elements) and occurs more than once
			for (ImageProfile imageProfileList_value : imageProfileList)
			{
				ProfileSet_element.appendChild(imageProfileList_value.asXMLElement(document));
			}
		}
		if (channelProfileList != null)
		{
			// Element property ChannelProfile which is complex (has
			// sub-elements) and occurs more than once
			for (ChannelProfile channelProfileList_value : channelProfileList)
			{
				ProfileSet_element.appendChild(channelProfileList_value.asXMLElement(document));
			}
		}
		return ProfileSet_element;
	}
}
