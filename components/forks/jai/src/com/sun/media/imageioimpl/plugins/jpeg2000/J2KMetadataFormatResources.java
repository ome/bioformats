/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
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
 * #L%
 */

/*
 * $RCSfile: J2KMetadataFormatResources.java,v $
 *
 * 
 * Copyright (c) 2005 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 
 * 
 * - Redistribution of source code must retain the above copyright 
 *   notice, this  list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in 
 *   the documentation and/or other materials provided with the
 *   distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of 
 * contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any 
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND 
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL 
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF 
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR 
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES. 
 * 
 * You acknowledge that this software is not designed or intended for 
 * use in the design, construction, operation or maintenance of any 
 * nuclear facility. 
 *
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:01:35 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.jpeg2000;

import java.util.*;

public class J2KMetadataFormatResources extends ListResourceBundle {
    static final Object[][] contents = {
        // Node name, followed by description
        {"JPEG2000SignatureBox", "The JPEG 2000 signature box."},
        {"JPEG2000FileTypeBox", "The JPEG 2000 file type box."},
        {"OtherBoxes",
         "All the boxes other than the signature or file type box."},
        {"HeaderCStream",
         "The super box conatins the header and code stream box."},
        {"JPEG2000IntellectualPropertyRightsBox",
         "The JPEG 2000 intellectual property rights box."},
        {"JPEG2000XMLBox", "The JPEG 2000 XML box."},
        {"JPEG2000UUIDBox", "The JPEG 2000 UUID box."},
        {"JPEG2000UUIDInfoBox", "The JPEG 2000 UUID information box."},
        {"JPEG2000HeaderSuperBox", "The JPEG 2000 header super box."},
        {"JPEG2000CodeStreamBox", "The JPEG 2000 code stream box."},
        {"JPEG2000HeaderBox", "The JPEG 2000 header box."},
        {"OptionalBoxes", "The optional boxes in the header super box."},
        {"JPEG2000BitsPerComponentBox", "The JPEG2000 bits per component box."},
        {"JPEG2000ColorSpecificationBox",
         "The JPEG 2000 color specification box."},
        {"JPEG2000PaletteBox", "The JPEG 2000 palette box."},
        {"JPEG2000ComponentMappingBox", "The JPEG 2000 component mapping box."},
        {"JPEG2000ChannelDefinitionBox",
         "The JPEG 2000 channel definition box."},
        {"JPEG2000ResolutionBox", "The JPEG 2000 resolution box."},
        {"JPEG2000CaptureResolutionBox",
         "The JPEG 2000 capture resolution box"},
        {"JPEG2000DefaultDisplayResolutionBox",
         "The JPEG 2000 default display resolution box"},
        {"JPEG2000UUIDListBox", "The JPEG 2000 UUID list box."},
        {"JPEG2000DataEntryURLBox", "The JPEG 2000 data entry URL box."},

        // Elements in JPEG2000FileTypeBox
        {"Brand", "The brand of JPEG 2000 file. For JP2 file, it is \"jp2 \""},
        {"MinorVersion",
         "The minor version of JPEG 2000 file. For JP2 file, it is 0"},
        {"CompatibilityList",
         "The compatibility list. For non-JP2 file, at least one is \"jp2 \""},

        // Elements of JPEG2000HeaderBox
        {"Width", "The width of the image."},
        {"Height", "The height of the image."},
        {"NumComponents", "The number of image components in this image file."},
        {"BitDepth",
         "Bit depths for all the image components or for each of the component if it is a array."},
        {"CompressionType", "The compression type.  Always be 7 for JP2 file."},
        {"UnknownColorspace", "Whether the color space is known or not."},
        {"IntellectualProperty",
         "Whether intellectual property in included in this image."},


        {"Method",
         "The method to define the color space. 1 by ECS; 2 by profile."},
        {"Precedence", "Precedence. Be 0 for JP2 file."},
        {"ApproximationAccuracy", "Approximation accuracy.  Be 0 for JP2 file"},
        {"EnumeratedColorSpace", "Enumerated color space. 16: sRGB. 17: Gray"},
        {"ICCProfile", "The ICC profile used to define the color space"},

        {"NumberEntries", "The number of palette entries."},
        {"NumberColors", "The number of color components."},
        {"BitDepth", "The bit depths for the output components after LUT."},
        {"LUT", "The LUT for the palette."},
        {"LUTRow", " A row of the LUT."},

        {"Component", "A component in the component mapping box."},
        {"ComponentType", "The type of one component: itself or lut column."},
        {"ComponentAssociation",
         "The LUT column used to define this component."},

        {"NumberOfDefinition",
         "The number of definitions in channel definition box."},
        {"Definitions", "Defines one channel."},
        {"ChannelNumber", "The channel number."},
        {"ChannelType", "The channel type: color, alpha, premultiplied alpha."},
        {"ChannelAssociation",
         "The association of this channel to the color space."},

        {"VerticalResolutionNumerator", "The vertical resolution numerator."},
        {"VerticalResolutionDenominator",
         "The vertical resolution denominator."},
        {"HorizontalResolutionNumerator",
         "The horizontal resolution numerator."},
        {"HorizontalResolutionDenominator",
         "The horizontal resolution denominator."},
        {"VerticalResolutionExponent", "The vertical resolution exponent."},
        {"HorizontalResolutionExponent", "The horizontal resolution exponent."},

        {"CodeStream", "The data of the code stream."},
        {"Content", "The intellectual property rights or XML."},

        {"UUID", "The UUID."},
        {"Data", "The data of the UUID."},
        {"NumberUUID", "The number of UUID in the UUID list box."},

        {"Version", "The version. Always be 0 for JP2 file."},
        {"Flags", "The flags.  Always be 0 for JP2 file."},
        {"URL", "The URL"},

                // Node name + "/" + AttributeName, followed by description
        {"JPEG2000SignatureBox/Length",
         "The length of the signature box. Always be 12."},
        {"JPEG2000SignatureBox/Type",
         "The type of the signature box. Always be \"jP  \""},
        {"JPEG2000SignatureBox/Signature",
         "The content of the signature box. Always be 0D0A870A."},

        {"JPEG2000FileTypeBox/Length", "The length of the file type box."},
        {"JPEG2000FileTypeBox/Type",
         "The type of the file type box. Always be \"ftpy\""},
        {"JPEG2000FileTypeBox/ExtraLength",
         "The extra length of the file type box.  Optional.  Set when Length = 1"},

        {"JPEG2000HeaderSuperBox/Length",
         "The length of the header super box."},
        {"JPEG2000HeaderSuperBox/Type",
         "The type of the header super box. Always be \"jp2h\""},
        {"JPEG2000HeaderSuperBox/ExtraLength",
         "The extra length of the header super box.  Optional.  Set when Length = 1"},

        {"JPEG2000HeaderBox/Length", "The length of the header box."},
        {"JPEG2000HeaderBox/Type",
         "The type of the header box. Always be \"ihdr\""},
        {"JPEG2000HeaderBox/ExtraLength",
         "The extra length of the header box.  Optional.  Set when Length = 1"},

        {"JPEG2000BitsPerComponentBox/Length",
         "The length of the bits per component box."},
        {"JPEG2000BitsPerComponentBox/Type",
         "The type of the bits per component box. Always be \"bpcc\""},
        {"JPEG2000BitsPerComponentBox/ExtraLength",
         "The extra length of the bits per component box.  Optional.  Set when Length = 1"},

        {"JPEG2000ColorSpecificationBox/Length",
         "The length of the bits per component box."},
        {"JPEG2000ColorSpecificationBox/Type",
         "The type of the bits per component box. Always be \"colr\""},
        {"JPEG2000ColorSpecificationBox/ExtraLength",
         "The extra length of the bits per component box.  Optional.  Set when Length = 1"},

        {"JPEG2000PaletteBox/Length", "The length of the palette box."},
        {"JPEG2000PaletteBox/Type",
         "The type of the palette box. Always be \"pclr\""},
        {"JPEG2000PaletteBox/ExtraLength",
         "The extra length of the palette box.  Optional.  Set when Length = 1"},

        {"JPEG2000ComponentMappingBox/Length",
         "The length of the component mapping box."},
        {"JPEG2000ComponentMappingBox/Type",
         "The type of the component mapping box. Always be \"cmap\""},
        {"JPEG2000ComponentMappingBox/ExtraLength",
         "The extra length of the component mapping box.  Optional.  Set when Length = 1"},

        {"JPEG2000ChannelDefinitionBox/Length",
         "The length of the channel definition box."},
        {"JPEG2000ChannelDefinitionBox/Type",
         "The type of the channel definition box. Always be \"cdef\""},
        {"JPEG2000ChannelDefinitionBox/ExtraLength",
         "The extra length of the channel definition box.  Optional.  Set when Length = 1"},

        {"JPEG2000ResolutionBox/Length", "The length of the resolution box."},
        {"JPEG2000ResolutionBox/Type",
         "The type of the resolution box. Always be \"res \""},
        {"JPEG2000ResolutionBox/ExtraLength",
         "The extra length of the resolution box.  Optional.  Set when Length = 1"},

        {"JPEG2000CaptureResolutionBox/Length",
         "The length of the capture resolution box."},
        {"JPEG2000CaptureResolutionBox/Type",
         "The type of the capture resolution box. Always be \"resc\""},
        {"JPEG2000CaptureResolutionBox/ExtraLength",
         "The extra length of the capture resolution box.  Optional.  Set when Length = 1"},

        {"JPEG2000DefaultDisplayResolutionBox/Length",
         "The length of the default display resolution box."},
        {"JPEG2000DefaultDisplayResolutionBox/Type",
         "The type of the default display resolution box. Always be \"resd\""},
        {"JPEG2000DefaultDisplayResolutionBox/ExtraLength",
         "The extra length of the default display resolution box.  Optional.  Set when Length = 1"},

        {"JPEG2000CodeStreamBox/Length", "The length of the code stream box."},
        {"JPEG2000CodeStreamBox/Type",
         "The type of the code stream box. Always be \"jp2c\""},
        {"JPEG2000CodeStreamBox/ExtraLength",
         "The extra length of the code stream box.  Optional.  Set when Length = 1"},

        {"JPEG2000IntellectualPropertyRightsBox/Length",
         "The length of the intellectual property rights box."},
        {"JPEG2000IntellectualPropertyRightsBox/Type",
         "The type of the intellectual property rights box. Always be \"jp2i\""},
        {"JPEG2000IntellectualPropertyRightsBox/ExtraLength",
         "The extra length of the intellectual property rights box.  Optional.  Set when Length = 1"},

        {"JPEG2000XMLBox/Length", "The length of the XML box."},
        {"JPEG2000XMLBox/Type", "The type of the XML box. Always be \"xml \""},
        {"JPEG2000XMLBox/ExtraLength",
         "The extra length of the XML box.  Optional.  Set when Length = 1"},

        {"JPEG2000UUIDBox/Length", "The length of the UUID box."},
        {"JPEG2000UUIDBox/Type",
         "The type of the UUID box. Always be \"uuid\""},
        {"JPEG2000UUIDBox/ExtraLength",
         "The extra length of the UUID box.  Optional.  Set when Length = 1"},

        {"JPEG2000UUIDInfoBox/Length", "The length of the UUID information box."},
        {"JPEG2000UUIDInfoBox/Type",
         "The type of the UUID information box. Always be \"uinf\""},
        {"JPEG2000UUIDInfoBox/ExtraLength",
         "The extra length of the UUID information box.  Optional.  Set when Length = 1"},

        {"JPEG2000UUIDListBox/Length", "The length of the UUID list box."},
        {"JPEG2000UUIDListBox/Type",
         "The type of the UUID list box. Always be \"ulst\""},
        {"JPEG2000UUIDListBox/ExtraLength",
         "The extra length of the UUID list box.  Optional.  Set when Length = 1"},

        {"JPEG2000DataEntryURLBox/Length",
         "The length of the data entry URL box."},
        {"JPEG2000DataEntryURLBox/Type",
         "The type of the data entry URL box. Always be \"ulst\""},
        {"JPEG2000DataEntryURLBox/ExtraLength",
         "The extra length of the data entry URL box.  Optional.  Set when Length = 1"},
    };

    public J2KMetadataFormatResources() {
    }

    protected Object[][] getContents() {
      return contents;
    }
}

