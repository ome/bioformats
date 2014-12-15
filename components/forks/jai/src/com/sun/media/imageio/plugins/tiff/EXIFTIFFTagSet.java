/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2014 Open Microscopy Environment:
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
 * $RCSfile: EXIFTIFFTagSet.java,v $
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
 * $Revision: 1.4 $
 * $Date: 2005/10/28 16:56:45 $
 * $State: Exp $
 */
package com.sun.media.imageio.plugins.tiff;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing the tags found in an EXIF IFD.  EXIF is a
 * standard for annotating images used by most digital camera
 * manufacturers.  The EXIF specification may be found at
 * <a href="http://www.exif.org/Exif2-2.PDF">
 * <code>http://www.exif.org/Exif2-2.PDF</code>
 * </a>.
 *
 * <p> The definitions of the data types referenced by the field
 * definitions may be found in the {@link TIFFTag
 * <code>TIFFTag</code>} class.
 */
public class EXIFTIFFTagSet extends TIFFTagSet {

    private static EXIFTIFFTagSet theInstance = null;

    /**
     * A tag pointing to a GPS info IFD (type LONG).
     *
     * @deprecated Superseded by
     * {@link EXIFParentTIFFTagSet#TAG_GPS_INFO_IFD_POINTER}
     */
    public static final int TAG_GPS_INFO_IFD_POINTER = 34853;

    /** A tag pointing to an interoperability IFD (type LONG). */
    public static final int TAG_INTEROPERABILITY_IFD_POINTER = 40965;

    /**
     * A tag containing the EXIF version number (type UNDEFINED, count =
     * 4).  Conformance to the EXIF 2.1 standard is indicated using
     * the ASCII value "0210" (with no terminating NUL).
     *
     * @see #EXIF_VERSION_2_1
     * @see #EXIF_VERSION_2_2
     */
    public static final int TAG_EXIF_VERSION = 36864;

    /**
     * An array of bytes containing the values <code>{'0', '2', '1',
     * '0'}</code> to be used with the "EXIFVersion" tag to indicate
     * EXIF version 2.1.
     *
     * @see #TAG_EXIF_VERSION
     */
    public static byte[] EXIF_VERSION_2_1 = { '0', '2', '1', '0' };

    /**
     * An array of bytes containing the values <code>{'0', '2', '2',
     * '0'}</code> to be used with the "EXIFVersion" tag to indicate
     * EXIF version 2.2.
     *
     * @see #TAG_EXIF_VERSION
     */
    public static byte[] EXIF_VERSION_2_2 = { '0', '2', '2', '0' };

    /**
     * A tag indicating the FlashPix version number (type UNDEFINED,
     * count = 4).
     */
    public static final int TAG_FLASHPIX_VERSION = 40960;

    /**
     * A tag indicating the color space information (type SHORT).  The
     * legal values are given by the <code>COLOR_SPACE_*</code>
     * constants.
     *
     * @see #COLOR_SPACE_SRGB
     * @see #COLOR_SPACE_UNCALIBRATED
     */
    public static final int TAG_COLOR_SPACE = 40961;

    /**
     * A value to be used with the "ColorSpace" tag.
     *
     * @see #TAG_COLOR_SPACE
     */
    public static final int COLOR_SPACE_SRGB = 1;

    /**
     * A value to be used with the "ColorSpace" tag.
     *
     * @see #TAG_COLOR_SPACE
     */
    public static final int COLOR_SPACE_UNCALIBRATED = 0xFFFF;

    /**
     * A tag containing the components configuration information (type
     * UNDEFINED, count = 4).
     *
     * @see #COMPONENTS_CONFIGURATION_DOES_NOT_EXIST
     * @see #COMPONENTS_CONFIGURATION_Y
     * @see #COMPONENTS_CONFIGURATION_CB
     * @see #COMPONENTS_CONFIGURATION_CR
     * @see #COMPONENTS_CONFIGURATION_R
     * @see #COMPONENTS_CONFIGURATION_G
     * @see #COMPONENTS_CONFIGURATION_B
     */
    public static final int TAG_COMPONENTS_CONFIGURATION = 37121;

    /**
     * A value to be used with the "ComponentsConfiguration" tag.
     *
     * @see #TAG_COMPONENTS_CONFIGURATION
     */
    public static final int COMPONENTS_CONFIGURATION_DOES_NOT_EXIST = 0;

    /**
     * A value to be used with the "ComponentsConfiguration" tag.
     *
     * @see #TAG_COMPONENTS_CONFIGURATION
     */
    public static final int COMPONENTS_CONFIGURATION_Y = 1;

    /**
     * A value to be used with the "ComponentsConfiguration" tag.
     *
     * @see #TAG_COMPONENTS_CONFIGURATION
     */
    public static final int COMPONENTS_CONFIGURATION_CB = 2;

    /**
     * A value to be used with the "ComponentsConfiguration" tag.
     *
     * @see #TAG_COMPONENTS_CONFIGURATION
     */
    public static final int COMPONENTS_CONFIGURATION_CR = 3;

    /**
     * A value to be used with the "ComponentsConfiguration" tag.
     *
     * @see #TAG_COMPONENTS_CONFIGURATION
     */
    public static final int COMPONENTS_CONFIGURATION_R = 4;

    /**
     * A value to be used with the "ComponentsConfiguration" tag.
     *
     * @see #TAG_COMPONENTS_CONFIGURATION
     */
    public static final int COMPONENTS_CONFIGURATION_G = 5;

    /**
     * A value to be used with the "ComponentsConfiguration" tag.
     *
     * @see #TAG_COMPONENTS_CONFIGURATION
     */
    public static final int COMPONENTS_CONFIGURATION_B = 6;

    /**
     * A tag indicating the number of compressed bits per pixel
     * (type RATIONAL).
     */
    public static final int TAG_COMPRESSED_BITS_PER_PIXEL = 37122;

    /**
     * A tag indicating the pixel X dimension (type SHORT or LONG).
     * This value records the valid width of the meaningful image for
     * a compressed file, whether or not there is padding or a restart
     * marker.
     */
    public static final int TAG_PIXEL_X_DIMENSION = 40962;

    /**
     * A tag indicating the pixel Y dimension (type SHORT or LONG).
     * This value records the valid height of the meaningful image for
     * a compressed file, whether or not there is padding or a restart
     * marker.
     */
    public static final int TAG_PIXEL_Y_DIMENSION = 40963;

    /**
     * A tag indicating a manufacturer-defined maker note (type
     * UNDEFINED).
     */
    public static final int TAG_MAKER_NOTE = 37500;

    /**
     * A tag indicating a manufacturer-defined marker note (type
     * UNDEFINED).
     *
     * @deprecated Superseded by {@link #TAG_MAKER_NOTE}.
     */
    public static final int TAG_MARKER_NOTE = TAG_MAKER_NOTE;

    /**
     * A tag indicating a user comment (type UNDEFINED).  The first 8
     * bytes are used to specify the character encoding.
     */
    public static final int TAG_USER_COMMENT = 37510;

    /**
     * A tag indicating the name of a related sound file (type ASCII).
     */
    public static final int TAG_RELATED_SOUND_FILE = 40964;

    /**
     * A tag indicating the date and time when the original image was
     * generated (type ASCII).
     */
    public static final int TAG_DATE_TIME_ORIGINAL = 36867;

    /**
     * A tag indicating the date and time when the image was stored as
     * digital data (type ASCII).
     */
    public static final int TAG_DATE_TIME_DIGITIZED = 36868;

    /**
     * A tag used to record fractions of seconds for the "DateTime" tag
     * (type ASCII).
     */
    public static final int TAG_SUB_SEC_TIME = 37520;

    /**
     * A tag used to record fractions of seconds for the
     * "DateTimeOriginal" tag (type ASCII).
     */
    public static final int TAG_SUB_SEC_TIME_ORIGINAL = 37521;

    /**
     * A tag used to record fractions of seconds for the
     * "DateTimeDigitized" tag (type ASCII).
     */
    public static final int TAG_SUB_SEC_TIME_DIGITIZED = 37522;

    /**
     * A tag indicating the exposure time, in seconds (type RATIONAL).
     */
    public static final int TAG_EXPOSURE_TIME = 33434;

    /**
     * A tag indicating the F number (type RATIONAL).
     */
    public static final int TAG_F_NUMBER = 33437;

    /**
     * A tag indicating the class of the programs used to set exposure
     * when the picture was taken (type SHORT).
     *
     * @see #EXPOSURE_PROGRAM_NOT_DEFINED
     * @see #EXPOSURE_PROGRAM_MANUAL
     * @see #EXPOSURE_PROGRAM_NORMAL_PROGRAM
     * @see #EXPOSURE_PROGRAM_APERTURE_PRIORITY
     * @see #EXPOSURE_PROGRAM_SHUTTER_PRIORITY
     * @see #EXPOSURE_PROGRAM_CREATIVE_PROGRAM
     * @see #EXPOSURE_PROGRAM_ACTION_PROGRAM
     * @see #EXPOSURE_PROGRAM_PORTRAIT_MODE
     * @see #EXPOSURE_PROGRAM_LANDSCAPE_MODE
     * @see #EXPOSURE_PROGRAM_MAX_RESERVED
     */
    public static final int TAG_EXPOSURE_PROGRAM = 34850;

    /**
     * A value to be used with the "ExposureProgram" tag.
     *
     * @see #TAG_EXPOSURE_PROGRAM
     */
    public static final int EXPOSURE_PROGRAM_NOT_DEFINED = 0;

    /**
     * A value to be used with the "ExposureProgram" tag.
     *
     * @see #TAG_EXPOSURE_PROGRAM
     */
    public static final int EXPOSURE_PROGRAM_MANUAL = 1;

    /**
     * A value to be used with the "ExposureProgram" tag.
     *
     * @see #TAG_EXPOSURE_PROGRAM
     */
    public static final int EXPOSURE_PROGRAM_NORMAL_PROGRAM = 2;

    /**
     * A value to be used with the "ExposureProgram" tag.
     *
     * @see #TAG_EXPOSURE_PROGRAM
     */
    public static final int EXPOSURE_PROGRAM_APERTURE_PRIORITY = 3;

    /**
     * A value to be used with the "ExposureProgram" tag.
     *
     * @see #TAG_EXPOSURE_PROGRAM
     */
    public static final int EXPOSURE_PROGRAM_SHUTTER_PRIORITY = 4;

    /**
     * A value to be used with the "ExposureProgram" tag.
     *
     * @see #TAG_EXPOSURE_PROGRAM
     */
    public static final int EXPOSURE_PROGRAM_CREATIVE_PROGRAM = 5;

    /**
     * A value to be used with the "ExposureProgram" tag.
     *
     * @see #TAG_EXPOSURE_PROGRAM
     */
    public static final int EXPOSURE_PROGRAM_ACTION_PROGRAM = 6;

    /**
     * A value to be used with the "ExposureProgram" tag.
     *
     * @see #TAG_EXPOSURE_PROGRAM
     */
    public static final int EXPOSURE_PROGRAM_PORTRAIT_MODE = 7;

    /**
     * A value to be used with the "ExposureProgram" tag.
     *
     * @see #TAG_EXPOSURE_PROGRAM
     */
    public static final int EXPOSURE_PROGRAM_LANDSCAPE_MODE = 8;

    /**
     * A value to be used with the "ExposureProgram" tag.
     *
     * @see #TAG_EXPOSURE_PROGRAM
     */
    public static final int EXPOSURE_PROGRAM_MAX_RESERVED = 255;

    /**
     * A tag indicating the spectral sensitivity of each channel of
     * the camera used (type ASCII).  The tag value is an ASCII string
     * compatible with the ASTM standard.
     */
    public static final int TAG_SPECTRAL_SENSITIVITY = 34852;

    /**
     * A tag indicating the ISO speed and ISO latitude of the camera
     * or input device, as specified in ISO 12232<sup>xiv</sup> (type
     * SHORT).
     */
    public static final int TAG_ISO_SPEED_RATINGS= 34855;

    /**
     * A tag indicating the optoelectric conversion function,
     * specified in ISO 14254<sup>xv</sup> (type UNDEFINED).  OECF is
     * the relationship between the camera optical input and the image
     * values.
     */
    public static final int TAG_OECF = 34856;

    /**
     * A tag indicating the shutter speed (type SRATIONAL).
     */
    public static final int TAG_SHUTTER_SPEED_VALUE = 37377;

    /**
     * A tag indicating the lens aperture (type RATIONAL).
     */
    public static final int TAG_APERTURE_VALUE = 37378;

    /**
     * A tag indicating the value of brightness (type SRATIONAL).
     */
    public static final int TAG_BRIGHTNESS_VALUE = 37379;

    /**
     * A tag indicating the exposure bias (type SRATIONAL).
     */
    public static final int TAG_EXPOSURE_BIAS_VALUE = 37380;

    /**
     * A tag indicating the smallest F number of the lens (type
     * RATIONAL).
     */
    public static final int TAG_MAX_APERTURE_VALUE = 37381;

    /**
     * A tag indicating the distance to the subject, in meters (type
     * RATIONAL).
     */
    public static final int TAG_SUBJECT_DISTANCE = 37382;

    /**
     * A tag indicating the metering mode (type SHORT).
     *
     * @see #METERING_MODE_UNKNOWN
     * @see #METERING_MODE_AVERAGE
     * @see #METERING_MODE_CENTER_WEIGHTED_AVERAGE
     * @see #METERING_MODE_SPOT
     * @see #METERING_MODE_MULTI_SPOT
     * @see #METERING_MODE_PATTERN
     * @see #METERING_MODE_PARTIAL
     * @see #METERING_MODE_MIN_RESERVED
     * @see #METERING_MODE_MAX_RESERVED
     * @see #METERING_MODE_OTHER
     */
    public static final int TAG_METERING_MODE = 37383;

    /**
     * A value to be used with the "MeteringMode" tag.
     *
     * @see #TAG_METERING_MODE
     */
    public static final int METERING_MODE_UNKNOWN = 0;

    /**
     * A value to be used with the "MeteringMode" tag.
     *
     * @see #TAG_METERING_MODE
     */
    public static final int METERING_MODE_AVERAGE = 1;

    /**
     * A value to be used with the "MeteringMode" tag.
     *
     * @see #TAG_METERING_MODE
     */
    public static final int METERING_MODE_CENTER_WEIGHTED_AVERAGE = 2;

    /**
     * A value to be used with the "MeteringMode" tag.
     *
     * @see #TAG_METERING_MODE
     */
    public static final int METERING_MODE_SPOT = 3;

    /**
     * A value to be used with the "MeteringMode" tag.
     *
     * @see #TAG_METERING_MODE
     */
    public static final int METERING_MODE_MULTI_SPOT = 4;

    /**
     * A value to be used with the "MeteringMode" tag.
     *
     * @see #TAG_METERING_MODE
     */
    public static final int METERING_MODE_PATTERN = 5;

    /**
     * A value to be used with the "MeteringMode" tag.
     *
     * @see #TAG_METERING_MODE
     */
    public static final int METERING_MODE_PARTIAL = 6;

    /**
     * A value to be used with the "MeteringMode" tag.
     *
     * @see #TAG_METERING_MODE
     */
    public static final int METERING_MODE_MIN_RESERVED = 7;

    /**
     * A value to be used with the "MeteringMode" tag.
     *
     * @see #TAG_METERING_MODE
     */
    public static final int METERING_MODE_MAX_RESERVED = 254;

    /**
     * A value to be used with the "MeteringMode" tag.
     *
     * @see #TAG_METERING_MODE
     */
    public static final int METERING_MODE_OTHER = 255;

    /**
     * A tag indicatingthe kind of light source (type SHORT).
     *
     * @see #LIGHT_SOURCE_UNKNOWN
     * @see #LIGHT_SOURCE_DAYLIGHT
     * @see #LIGHT_SOURCE_FLUORESCENT
     * @see #LIGHT_SOURCE_TUNGSTEN
     * @see #LIGHT_SOURCE_STANDARD_LIGHT_A
     * @see #LIGHT_SOURCE_STANDARD_LIGHT_B
     * @see #LIGHT_SOURCE_STANDARD_LIGHT_C
     * @see #LIGHT_SOURCE_D55
     * @see #LIGHT_SOURCE_D65
     * @see #LIGHT_SOURCE_D75
     * @see #LIGHT_SOURCE_OTHER
     */
    public static final int TAG_LIGHT_SOURCE = 37384;

    /**
     * A value to be used with the "LightSource" tag.
     *
     * @see #TAG_LIGHT_SOURCE
     */
    public static final int LIGHT_SOURCE_UNKNOWN = 0;

    /**
     * A value to be used with the "LightSource" tag.
     *
     * @see #TAG_LIGHT_SOURCE
     */
    public static final int LIGHT_SOURCE_DAYLIGHT = 1;

    /**
     * A value to be used with the "LightSource" tag.
     *
     * @see #TAG_LIGHT_SOURCE
     */
    public static final int LIGHT_SOURCE_FLUORESCENT = 2;

    /**
     * A value to be used with the "LightSource" tag.
     *
     * @see #TAG_LIGHT_SOURCE
     */
    public static final int LIGHT_SOURCE_TUNGSTEN = 3;

    /**
     * A value to be used with the "LightSource" tag.
     *
     * @see #TAG_LIGHT_SOURCE
     */
    public static final int LIGHT_SOURCE_FLASH = 4;

    /**
     * A value to be used with the "LightSource" tag.
     *
     * @see #TAG_LIGHT_SOURCE
     */
    public static final int LIGHT_SOURCE_FINE_WEATHER = 9;

    /**
     * A value to be used with the "LightSource" tag.
     *
     * @see #TAG_LIGHT_SOURCE
     */
    public static final int LIGHT_SOURCE_CLOUDY_WEATHER = 10;

    /**
     * A value to be used with the "LightSource" tag.
     *
     * @see #TAG_LIGHT_SOURCE
     */
    public static final int LIGHT_SOURCE_SHADE = 11;

    /**
     * A value to be used with the "LightSource" tag.
     *
     * @see #TAG_LIGHT_SOURCE
     */
    public static final int LIGHT_SOURCE_DAYLIGHT_FLUORESCENT = 12;

    /**
     * A value to be used with the "LightSource" tag.
     *
     * @see #TAG_LIGHT_SOURCE
     */
    public static final int LIGHT_SOURCE_DAY_WHITE_FLUORESCENT = 13;

    /**
     * A value to be used with the "LightSource" tag.
     *
     * @see #TAG_LIGHT_SOURCE
     */
    public static final int LIGHT_SOURCE_COOL_WHITE_FLUORESCENT = 14;

    /**
     * A value to be used with the "LightSource" tag.
     *
     * @see #TAG_LIGHT_SOURCE
     */
    public static final int LIGHT_SOURCE_WHITE_FLUORESCENT = 15;

    /**
     * A value to be used with the "LightSource" tag.
     *
     * @see #TAG_LIGHT_SOURCE
     */
    public static final int LIGHT_SOURCE_STANDARD_LIGHT_A = 17;

    /**
     * A value to be used with the "LightSource" tag.
     *
     * @see #TAG_LIGHT_SOURCE
     */
    public static final int LIGHT_SOURCE_STANDARD_LIGHT_B = 18;

    /**
     * A value to be used with the "LightSource" tag.
     *
     * @see #TAG_LIGHT_SOURCE
     */
    public static final int LIGHT_SOURCE_STANDARD_LIGHT_C = 19;

    /**
     * A value to be used with the "LightSource" tag.
     *
     * @see #TAG_LIGHT_SOURCE
     */
    public static final int LIGHT_SOURCE_D55 = 20;

    /**
     * A value to be used with the "LightSource" tag.
     *
     * @see #TAG_LIGHT_SOURCE
     */
    public static final int LIGHT_SOURCE_D65 = 21;

    /**
     * A value to be used with the "LightSource" tag.
     *
     * @see #TAG_LIGHT_SOURCE
     */
    public static final int LIGHT_SOURCE_D75 = 22;

    /**
     * A value to be used with the "LightSource" tag.
     *
     * @see #TAG_LIGHT_SOURCE
     */
    public static final int LIGHT_SOURCE_D50 = 23;

    /**
     * A value to be used with the "LightSource" tag.
     *
     * @see #TAG_LIGHT_SOURCE
     */
    public static final int LIGHT_SOURCE_ISO_STUDIO_TUNGSTEN = 24;

    /**
     * A value to be used with the "LightSource" tag.
     *
     * @see #TAG_LIGHT_SOURCE
     */
    public static final int LIGHT_SOURCE_OTHER = 255;

    /**
     * A tag indicating the flash firing status and flash return
     * status (type SHORT).
     *
     * @see #FLASH_DID_NOT_FIRE
     * @see #FLASH_FIRED
     * @see #FLASH_STROBE_RETURN_LIGHT_NOT_DETECTED
     * @see #FLASH_STROBE_RETURN_LIGHT_DETECTED
     */
    public static final int TAG_FLASH = 37385;

    /**
     * A value to be used with the "Flash" tag, indicating that the
     * flash did not fire.
     *
     * @see #TAG_FLASH
     */
    public static final int FLASH_DID_NOT_FIRE = 0x0;

    /**
     * A value to be used with the "Flash" tag, indicating that the
     * flash fired, but the strobe return status is unknown.
     *
     * @see #TAG_FLASH
     */
    public static final int FLASH_FIRED = 0x1;

    /**
     * A value to be used with the "Flash" tag, indicating that the
     * flash fired, but the strobe return light was not detected.
     *
     * @see #TAG_FLASH
     */
    public static final int FLASH_STROBE_RETURN_LIGHT_NOT_DETECTED = 0x5;

    /**
     * A value to be used with the "Flash" tag, indicating that the
     * flash fired, and the strobe return light was detected.
     *
     * @see #TAG_FLASH
     */
    public static final int FLASH_STROBE_RETURN_LIGHT_DETECTED = 0x7;

    /**
     * A mask to be used with the "Flash" tag, indicating that the
     * flash fired.
     *
     * @see #TAG_FLASH
     */
    public static final int FLASH_MASK_FIRED = 0x1;

    /**
     * A mask to be used with the "Flash" tag, indicating strobe return
     * light not detected.
     *
     * @see #TAG_FLASH
     */
    public static final int FLASH_MASK_RETURN_NOT_DETECTED = 0x4;

    /**
     * A mask to be used with the "Flash" tag, indicating strobe return
     * light detected.
     *
     * @see #TAG_FLASH
     */
    public static final int FLASH_MASK_RETURN_DETECTED = 0x6;

    /**
     * A mask to be used with the "Flash" tag, indicating compulsory flash
     * firing mode.
     *
     * @see #TAG_FLASH
     */
    public static final int FLASH_MASK_MODE_FLASH_FIRING = 0x8;

    /**
     * A mask to be used with the "Flash" tag, indicating compulsory flash
     * suppression mode.
     *
     * @see #TAG_FLASH
     */
    public static final int FLASH_MASK_MODE_FLASH_SUPPRESSION = 0x10;

    /**
     * A mask to be used with the "Flash" tag, indicating auto mode.
     *
     * @see #TAG_FLASH
     */
    public static final int FLASH_MASK_MODE_AUTO = 0x18;

    /**
     * A mask to be used with the "Flash" tag, indicating no flash function
     * present.
     *
     * @see #TAG_FLASH
     */
    public static final int FLASH_MASK_FUNCTION_NOT_PRESENT = 0x20;

    /**
     * A mask to be used with the "Flash" tag, indicating red-eye reduction
     * supported.
     *
     * @see #TAG_FLASH
     */
    public static final int FLASH_MASK_RED_EYE_REDUCTION = 0x40;

    /**
     * A tag indicating the actual focal length of the lens, in
     * millimeters (type RATIONAL).
     */
    public static final int TAG_FOCAL_LENGTH = 37386;

    /**
     * A tag indicating the location and area of the main subject in
     * the overall scene.
     */
    public static final int TAG_SUBJECT_AREA = 37396;

    /**
     * A tag indicating the strobe energy at the time the image was
     * captured, as measured in Beam Candle Power Seconds (BCPS) (type
     * RATIONAL).
     */
    public static final int TAG_FLASH_ENERGY = 41483;

    /**
     * A tag indicating the camera or input device spatial frequency
     * table and SFR values in the direction of image width, image
     * height, and diagonal direction, as specified in ISO
     * 12233<sup>xvi</sup> (type UNDEFINED).
     */
    public static final int TAG_SPATIAL_FREQUENCY_RESPONSE = 41484;

    /**
     * Indicates the number of pixels in the image width (X) direction
     * per FocalPlaneResolutionUnit on the camera focal plane (type
     * RATIONAL).
     */
    public static final int TAG_FOCAL_PLANE_X_RESOLUTION = 41486;

    /**
     * Indicate the number of pixels in the image height (Y) direction
     * per FocalPlaneResolutionUnit on the camera focal plane (type
     * RATIONAL).
     */
    public static final int TAG_FOCAL_PLANE_Y_RESOLUTION = 41487;

    /**
     * Indicates the unit for measuring FocalPlaneXResolution and
     * FocalPlaneYResolution (type SHORT).
     *
     * @see #FOCAL_PLANE_RESOLUTION_UNIT_NONE
     * @see #FOCAL_PLANE_RESOLUTION_UNIT_INCH
     * @see #FOCAL_PLANE_RESOLUTION_UNIT_CENTIMETER
     */
    public static final int TAG_FOCAL_PLANE_RESOLUTION_UNIT = 41488;

    /**
     * A value to be used with the "FocalPlaneResolutionUnit" tag.
     *
     * @see #TAG_FOCAL_PLANE_RESOLUTION_UNIT
     */
    public static final int FOCAL_PLANE_RESOLUTION_UNIT_NONE = 1;

    /**
     * A value to be used with the "FocalPlaneXResolution" tag.
     *
     * @see #TAG_FOCAL_PLANE_RESOLUTION_UNIT
     */
    public static final int FOCAL_PLANE_RESOLUTION_UNIT_INCH = 2;

    /**
     * A value to be used with the "FocalPlaneXResolution" tag.
     *
     * @see #TAG_FOCAL_PLANE_RESOLUTION_UNIT
     */
    public static final int FOCAL_PLANE_RESOLUTION_UNIT_CENTIMETER = 3;

    /**
     * A tag indicating the column and row of the center pixel of the
     * main subject in the scene (type SHORT, count = 2).
     */
    public static final int TAG_SUBJECT_LOCATION = 41492;

    /**
     * A tag indicating the exposure index selected on the camera or
     * input device at the time the image was captured (type
     * RATIONAL).
     */
    public static final int TAG_EXPOSURE_INDEX = 41493;

    /**
     * A tag indicating the sensor type on the camera or input device
     * (type SHORT).
     *
     * @see #SENSING_METHOD_NOT_DEFINED
     * @see #SENSING_METHOD_ONE_CHIP_COLOR_AREA_SENSOR
     * @see #SENSING_METHOD_TWO_CHIP_COLOR_AREA_SENSOR
     * @see #SENSING_METHOD_THREE_CHIP_COLOR_AREA_SENSOR
     * @see #SENSING_METHOD_COLOR_SEQUENTIAL_AREA_SENSOR
     * @see #SENSING_METHOD_TRILINEAR_SENSOR
     * @see #SENSING_METHOD_COLOR_SEQUENTIAL_LINEAR_SENSOR
     */
    public static final int TAG_SENSING_METHOD = 41495;

    /**
     * A value to be used with the "SensingMethod" tag.
     *
     * @see #TAG_SENSING_METHOD
     */
    public static final int SENSING_METHOD_NOT_DEFINED = 1;

    /**
     * A value to be used with the "SensingMethod" tag.
     *
     * @see #TAG_SENSING_METHOD
     */
    public static final int SENSING_METHOD_ONE_CHIP_COLOR_AREA_SENSOR = 2;

    /**
     * A value to be used with the "SensingMethod" tag.
     *
     * @see #TAG_SENSING_METHOD
     */
    public static final int SENSING_METHOD_TWO_CHIP_COLOR_AREA_SENSOR = 3;

    /**
     * A value to be used with the "SensingMethod" tag.
     *
     * @see #TAG_SENSING_METHOD
     */
    public static final int SENSING_METHOD_THREE_CHIP_COLOR_AREA_SENSOR = 4;

    /**
     * A value to be used with the "SensingMethod" tag.
     *
     * @see #TAG_SENSING_METHOD
     */
    public static final int SENSING_METHOD_COLOR_SEQUENTIAL_AREA_SENSOR = 5;

    /**
     * A value to be used with the "SensingMethod" tag.
     *
     * @see #TAG_SENSING_METHOD
     */
    public static final int SENSING_METHOD_TRILINEAR_SENSOR = 7;

    /**
     * A value to be used with the "SensingMethod" tag.
     *
     * @see #TAG_SENSING_METHOD
     */
    public static final int SENSING_METHOD_COLOR_SEQUENTIAL_LINEAR_SENSOR = 8;

    /**
     * A tag indicating the image source (type UNDEFINED).
     *
     * @see #FILE_SOURCE_DSC
     */
    public static final int TAG_FILE_SOURCE = 41728;

    /**
     * A value to be used with the "FileSource" tag.
     *
     * @see #TAG_FILE_SOURCE
     */
    public static final int FILE_SOURCE_DSC = 3;

    /**
     * A tag indicating the type of scene (type UNDEFINED).
     *
     * @see #SCENE_TYPE_DSC
     */
    public static final int TAG_SCENE_TYPE = 41729;

    /**
     * A value to be used with the "SceneType" tag.
     *
     * @see #TAG_SCENE_TYPE
     */
    public static final int SCENE_TYPE_DSC = 1;

    /**
     * A tag indicating the color filter array geometric pattern of
     * the image sensor when a one-chip color area sensor if used
     * (type UNDEFINED).
     */
    public static final int TAG_CFA_PATTERN = 41730;

    /**
     * A tag indicating the use of special processing on image data,
     * such as rendering geared to output.
     */
    public static final int TAG_CUSTOM_RENDERED = 41985;

    /**
     * A value to be used with the "CustomRendered" tag.
     *
     * @see #TAG_CUSTOM_RENDERED
     */
    public static final int CUSTOM_RENDERED_NORMAL = 0;

    /**
     * A value to be used with the "CustomRendered" tag.
     *
     * @see #TAG_CUSTOM_RENDERED
     */
    public static final int CUSTOM_RENDERED_CUSTOM = 1;

    /**
     * A tag indicating the exposure mode set when the image was shot.
     */
    public static final int TAG_EXPOSURE_MODE = 41986;

    /**
     * A value to be used with the "ExposureMode" tag.
     *
     * @see #TAG_EXPOSURE_MODE
     */
    public static final int EXPOSURE_MODE_AUTO_EXPOSURE = 0;

    /**
     * A value to be used with the "ExposureMode" tag.
     *
     * @see #TAG_EXPOSURE_MODE
     */
    public static final int EXPOSURE_MODE_MANUAL_EXPOSURE = 1;

    /**
     * A value to be used with the "ExposureMode" tag.
     *
     * @see #TAG_EXPOSURE_MODE
     */
    public static final int EXPOSURE_MODE_AUTO_BRACKET = 2;

    /**
     * A tag indicating the white balance mode set when the image was shot.
     */
    public static final int TAG_WHITE_BALANCE = 41987;

    /**
     * A value to be used with the "WhiteBalance" tag.
     *
     * @see #TAG_WHITE_BALANCE
     */
    public static final int WHITE_BALANCE_AUTO = 0;

    /**
     * A value to be used with the "WhiteBalance" tag.
     *
     * @see #TAG_WHITE_BALANCE
     */
    public static final int WHITE_BALANCE_MANUAL = 1;

    /**
     * A tag indicating the digital zoom ratio when the image was shot.
     */
    public static final int TAG_DIGITAL_ZOOM_RATIO = 41988;

    /**
     * A tag indicating the equivalent focal length assuming a 35mm film
     * camera, in millimeters.
     */
    public static final int TAG_FOCAL_LENGTH_IN_35MM_FILM = 41989;

    /**
     * A tag indicating the type of scene that was shot.
     */
    public static final int TAG_SCENE_CAPTURE_TYPE = 41990;

    /**
     * A value to be used with the "SceneCaptureType" tag.
     *
     * @see #TAG_SCENE_CAPTURE_TYPE
     */
    public static final int SCENE_CAPTURE_TYPE_STANDARD = 0;

    /**
     * A value to be used with the "SceneCaptureType" tag.
     *
     * @see #TAG_SCENE_CAPTURE_TYPE
     */
    public static final int SCENE_CAPTURE_TYPE_LANDSCAPE = 1;

    /**
     * A value to be used with the "SceneCaptureType" tag.
     *
     * @see #TAG_SCENE_CAPTURE_TYPE
     */
    public static final int SCENE_CAPTURE_TYPE_PORTRAIT = 2;

    /**
     * A value to be used with the "SceneCaptureType" tag.
     *
     * @see #TAG_SCENE_CAPTURE_TYPE
     */
    public static final int SCENE_CAPTURE_TYPE_NIGHT_SCENE = 3;

    /**
     * A tag indicating the degree of overall image gain adjustment.
     */
    public static final int TAG_GAIN_CONTROL = 41991;

    /**
     * A value to be used with the "GainControl" tag.
     *
     * @see #TAG_GAIN_CONTROL
     */
    public static final int GAIN_CONTROL_NONE = 0;

    /**
     * A value to be used with the "GainControl" tag.
     *
     * @see #TAG_GAIN_CONTROL
     */
    public static final int GAIN_CONTROL_LOW_GAIN_UP = 1;

    /**
     * A value to be used with the "GainControl" tag.
     *
     * @see #TAG_GAIN_CONTROL
     */
    public static final int GAIN_CONTROL_HIGH_GAIN_UP = 2;

    /**
     * A value to be used with the "GainControl" tag.
     *
     * @see #TAG_GAIN_CONTROL
     */
    public static final int GAIN_CONTROL_LOW_GAIN_DOWN = 3;

    /**
     * A value to be used with the "GainControl" tag.
     *
     * @see #TAG_GAIN_CONTROL
     */
    public static final int GAIN_CONTROL_HIGH_GAIN_DOWN = 4;

    /**
     * A tag indicating the direction of contrast processing applied
     * by the camera when the image was shot.
     */
    public static final int TAG_CONTRAST = 41992;

    /**
     * A value to be used with the "Contrast" tag.
     *
     * @see #TAG_CONTRAST
     */
    public static final int CONTRAST_NORMAL = 0;

    /**
     * A value to be used with the "Contrast" tag.
     *
     * @see #TAG_CONTRAST
     */
    public static final int CONTRAST_SOFT = 1;

    /**
     * A value to be used with the "Contrast" tag.
     *
     * @see #TAG_CONTRAST
     */
    public static final int CONTRAST_HARD = 2;

    /**
     * A tag indicating the direction of saturation processing
     * applied by the camera when the image was shot.
     */
    public static final int TAG_SATURATION = 41993;

    /**
     * A value to be used with the "Saturation" tag.
     *
     * @see #TAG_SATURATION
     */
    public static final int SATURATION_NORMAL = 0;

    /**
     * A value to be used with the "Saturation" tag.
     *
     * @see #TAG_SATURATION
     */
    public static final int SATURATION_LOW = 1;

    /**
     * A value to be used with the "Saturation" tag.
     *
     * @see #TAG_SATURATION
     */
    public static final int SATURATION_HIGH = 2;

    /**
     * A tag indicating the direction of sharpness processing
     * applied by the camera when the image was shot.
     */
    public static final int TAG_SHARPNESS = 41994;

    /**
     * A value to be used with the "Sharpness" tag.
     *
     * @see #TAG_SHARPNESS
     */
    public static final int SHARPNESS_NORMAL = 0;

    /**
     * A value to be used with the "Sharpness" tag.
     *
     * @see #TAG_SHARPNESS
     */
    public static final int SHARPNESS_SOFT = 1;

    /**
     * A value to be used with the "Sharpness" tag.
     *
     * @see #TAG_SHARPNESS
     */
    public static final int SHARPNESS_HARD = 2;

    /**
     * A tag indicating information on the picture-taking conditions
     * of a particular camera model.
     */
    public static final int TAG_DEVICE_SETTING_DESCRIPTION = 41995;

    /**
     * A tag indicating the distance to the subject.
     */
    public static final int TAG_SUBJECT_DISTANCE_RANGE = 41996;

    /**
     * A value to be used with the "SubjectDistanceRange" tag.
     *
     * @see #TAG_SUBJECT_DISTANCE_RANGE
     */
    public static final int SUBJECT_DISTANCE_RANGE_UNKNOWN = 0;

    /**
     * A value to be used with the "SubjectDistanceRange" tag.
     *
     * @see #TAG_SUBJECT_DISTANCE_RANGE
     */
    public static final int SUBJECT_DISTANCE_RANGE_MACRO = 1;

    /**
     * A value to be used with the "SubjectDistanceRange" tag.
     *
     * @see #TAG_SUBJECT_DISTANCE_RANGE
     */
    public static final int SUBJECT_DISTANCE_RANGE_CLOSE_VIEW = 2;

    /**
     * A value to be used with the "SubjectDistanceRange" tag.
     *
     * @see #TAG_SUBJECT_DISTANCE_RANGE
     */
    public static final int SUBJECT_DISTANCE_RANGE_DISTANT_VIEW = 3;

    /**
     * A tag indicating an identifier assigned uniquely to each image.
     */
    public static final int TAG_IMAGE_UNIQUE_ID = 42016;

    // EXIF 2.1 private

    // GPS Attribute Information
    //     0 - GPSVersionID                       (BYTE/4)
    //     1 - GPSLatitudeRef                     (ASCII/2)
    //     2 - GPSLatitude                        (RATIONAL/3)
    //     3 - GPSLongitudeRef                    (ASCII/2)
    //     4 - GPSLongitude                       (RATIONAL/3)
    //     5 - GPSAltitudeRef                     (BYTE/1)
    //     6 - GPSAltitude                        (RATIONAL/1)
    //     7 - GPSTimeStamp                       (RATIONAL/3)
    //     8 - GPSSatellites                      (ASCII/any)
    //     9 - GPSStatus                          (ASCII/2)
    //    10 - GPSMeasureMode                     (ASCII/2)
    //    11 - GPSDOP                             (RATIONAL/1)
    //    12 - GPSSpeedRef                        (ASCII/2)
    //    13 - GPSSpeed                           (RATIONAL/1)
    //    14 - GPSTrackRef                        (ASCII/2)
    //    15 - GPSTrack                           (RATIONAL/1)
    //    16 - GPSImgDirectionRef                 (ASCII/2)
    //    17 - GPSImgDirection                    (RATIONAL/1)
    //    18 - GPSMapDatum                        (ASCII/any)
    //    19 - GPSDestLatitudeRef                 (ASCII/2)
    //    20 - GPSDestLatitude                    (RATIONAL/3)
    //    21 - GPSDestLongitudeRef                (ASCII/2)
    //    22 - GPSDestLongitude                   (RATIONAL/3)
    //    23 - GPSDestBearingRef                  (ASCII/2)
    //    24 - GPSDestBearing                     (RATIONAL/1)
    //    25 - GPSDestDistanceRef                 (ASCII/2)
    //    26 - GPSDestDistance                    (RATIONAL/1)

    // EXIF Interoperability Information ???
    
    //     0 - Interoperability Index             (ASCII/any)

    // EXIF tags

    static class EXIFVersion extends TIFFTag {
        
        public EXIFVersion() {
            super("EXIFversion",
                  TAG_EXIF_VERSION,
                  1 << TIFFTag.TIFF_UNDEFINED);
        }
    }

    static class FlashPixVersion extends TIFFTag {

        public FlashPixVersion() {
            super("FlashPixVersion",
                  TAG_FLASHPIX_VERSION,
                  1 << TIFFTag.TIFF_UNDEFINED);
        }
    }

    static class ColorSpace extends TIFFTag {

        public ColorSpace() {
            super("ColorSpace",
                  TAG_COLOR_SPACE,
                  1 << TIFFTag.TIFF_SHORT);

            addValueName(COLOR_SPACE_SRGB, "sRGB");
            addValueName(COLOR_SPACE_UNCALIBRATED, "Uncalibrated");
        }
    }

    static class ComponentsConfiguration extends TIFFTag {

        public ComponentsConfiguration() {
            super("ComponentsConfiguration",
                  TAG_COMPONENTS_CONFIGURATION,
                  1 << TIFFTag.TIFF_UNDEFINED);
            
            addValueName(COMPONENTS_CONFIGURATION_DOES_NOT_EXIST,
                         "DoesNotExist");
            addValueName(COMPONENTS_CONFIGURATION_Y, "Y");
            addValueName(COMPONENTS_CONFIGURATION_CB, "Cb");
            addValueName(COMPONENTS_CONFIGURATION_CR, "Cr");
            addValueName(COMPONENTS_CONFIGURATION_R, "R");
            addValueName(COMPONENTS_CONFIGURATION_G, "G");
            addValueName(COMPONENTS_CONFIGURATION_B, "B");
        }
    }

    static class CompressedBitsPerPixel extends TIFFTag {

        public CompressedBitsPerPixel() {
            super("CompressedBitsPerPixel",
                  TAG_COMPRESSED_BITS_PER_PIXEL,
                  1 << TIFFTag.TIFF_RATIONAL);
        }
    }

    static class PixelXDimension extends TIFFTag {

        public PixelXDimension() {
            super("PixelXDimension",
                  TAG_PIXEL_X_DIMENSION,
                  (1 << TIFFTag.TIFF_SHORT) |
                  (1 << TIFFTag.TIFF_LONG));
        }
    }

    static class PixelYDimension extends TIFFTag {

        public PixelYDimension() {
            super("PixelYDimension",
                  TAG_PIXEL_Y_DIMENSION,
                  (1 << TIFFTag.TIFF_SHORT) |
                  (1 << TIFFTag.TIFF_LONG));
        }
    }

    static class MakerNote extends TIFFTag {

        public MakerNote() {
            super("MakerNote",
                  TAG_MAKER_NOTE,
                  1 << TIFFTag.TIFF_UNDEFINED);
        }
    }

    static class UserComment extends TIFFTag {

        public UserComment() {
            super("UserComment",
                  TAG_USER_COMMENT,
                  1 << TIFFTag.TIFF_UNDEFINED);
        }
    }

    static class RelatedSoundFile extends TIFFTag {

        public RelatedSoundFile() {
            super("RelatedSoundFile",
                  TAG_RELATED_SOUND_FILE,
                  1 << TIFFTag.TIFF_ASCII);
        }
    }

    static class DateTimeOriginal extends TIFFTag {

        public DateTimeOriginal() {
            super("DateTimeOriginal",
                  TAG_DATE_TIME_ORIGINAL,
                  1 << TIFFTag.TIFF_ASCII);
        }
    }

    static class DateTimeDigitized extends TIFFTag {

        public DateTimeDigitized() {
            super("DateTimeDigitized",
                  TAG_DATE_TIME_DIGITIZED,
                  1 << TIFFTag.TIFF_ASCII);
        }
    }

    static class SubSecTime extends TIFFTag {

        public SubSecTime() {
            super("SubSecTime",
                  TAG_SUB_SEC_TIME,
                  1 << TIFFTag.TIFF_ASCII);
        }
    }

    static class SubSecTimeOriginal extends TIFFTag {

        public SubSecTimeOriginal() {
            super("SubSecTimeOriginal",
                  TAG_SUB_SEC_TIME_ORIGINAL,
                  1 << TIFFTag.TIFF_ASCII);
        }
    }

    static class SubSecTimeDigitized extends TIFFTag {

        public SubSecTimeDigitized() {
            super("SubSecTimeDigitized",
                  TAG_SUB_SEC_TIME_DIGITIZED,
                  1 << TIFFTag.TIFF_ASCII);
        }
    }

    static class ExposureTime extends TIFFTag {

        public ExposureTime() {
            super("ExposureTime",
                  TAG_EXPOSURE_TIME,
                  1 << TIFFTag.TIFF_RATIONAL);
        }
    }

    static class FNumber extends TIFFTag {

        public FNumber() {
            super("FNumber",
                  TAG_F_NUMBER,
                  1 << TIFFTag.TIFF_RATIONAL);
        }
    }

    static class ExposureProgram extends TIFFTag {

        public ExposureProgram() {
            super("ExposureProgram",
                  TAG_EXPOSURE_PROGRAM,
                  1 << TIFFTag.TIFF_SHORT);

            addValueName(EXPOSURE_PROGRAM_NOT_DEFINED, "Not Defined");
            addValueName(EXPOSURE_PROGRAM_MANUAL, "Manual");
            addValueName(EXPOSURE_PROGRAM_NORMAL_PROGRAM, "Normal Program");
            addValueName(EXPOSURE_PROGRAM_APERTURE_PRIORITY,
                         "Aperture Priority");
            addValueName(EXPOSURE_PROGRAM_SHUTTER_PRIORITY,
                         "Shutter Priority");
            addValueName(EXPOSURE_PROGRAM_CREATIVE_PROGRAM,
                         "Creative Program");
            addValueName(EXPOSURE_PROGRAM_ACTION_PROGRAM, "Action Program");
            addValueName(EXPOSURE_PROGRAM_PORTRAIT_MODE, "Portrait Mode");
            addValueName(EXPOSURE_PROGRAM_LANDSCAPE_MODE, "Landscape Mode");
        }
    }

    static class SpectralSensitivity extends TIFFTag {
        public SpectralSensitivity() {
            super("SpectralSensitivity",
                  TAG_SPECTRAL_SENSITIVITY,
                  1 << TIFFTag.TIFF_ASCII);
        }
    }

    static class ISOSpeedRatings extends TIFFTag {

        public ISOSpeedRatings() {
            super("ISOSpeedRatings",
                  TAG_ISO_SPEED_RATINGS,
                  1 << TIFFTag.TIFF_SHORT);
        }
    }

    static class OECF extends TIFFTag {

        public OECF() {
            super("OECF",
                  TAG_OECF,
                  1 << TIFFTag.TIFF_UNDEFINED);
        }
    }

    static class ShutterSpeedValue extends TIFFTag {

        public ShutterSpeedValue() {
            super("ShutterSpeedValue",
                  TAG_SHUTTER_SPEED_VALUE,
                  1 << TIFFTag.TIFF_SRATIONAL);
        }
    }

    static class ApertureValue extends TIFFTag {

        public ApertureValue() {
            super("ApertureValue",
                  TAG_APERTURE_VALUE,
                  1 << TIFFTag.TIFF_RATIONAL);
        }
    }

    static class BrightnessValue extends TIFFTag {

        public BrightnessValue() {
            super("BrightnessValue",
                  TAG_BRIGHTNESS_VALUE,
                  1 << TIFFTag.TIFF_SRATIONAL);
        }
    }

    static class ExposureBiasValue extends TIFFTag {

        public ExposureBiasValue() {
            super("ExposureBiasValue",
                  TAG_EXPOSURE_BIAS_VALUE,
                  1 << TIFFTag.TIFF_SRATIONAL);
        }
    }

    static class MaxApertureValue extends TIFFTag {

        public MaxApertureValue() {
            super("MaxApertureValue",
                  TAG_MAX_APERTURE_VALUE,
                  1 << TIFFTag.TIFF_RATIONAL);
        }
    }

    static class SubjectDistance extends TIFFTag {

        public SubjectDistance() {
            super("SubjectDistance",
                  TAG_SUBJECT_DISTANCE,
                  1 << TIFFTag.TIFF_RATIONAL);
        }
    }

    static class MeteringMode extends TIFFTag {

        public MeteringMode() {
            super("MeteringMode",
                  TAG_METERING_MODE,
                  1 << TIFFTag.TIFF_SHORT);
            
            addValueName(METERING_MODE_UNKNOWN, "Unknown");
            addValueName(METERING_MODE_AVERAGE, "Average");
            addValueName(METERING_MODE_CENTER_WEIGHTED_AVERAGE,
                         "CenterWeightedAverage");
            addValueName(METERING_MODE_SPOT, "Spot");
            addValueName(METERING_MODE_MULTI_SPOT, "MultiSpot");
            addValueName(METERING_MODE_PATTERN, "Pattern");
            addValueName(METERING_MODE_PARTIAL, "Partial");
            addValueName(METERING_MODE_OTHER, "Other");
        }
    }

    static class LightSource extends TIFFTag {

        public LightSource() {
            super("LightSource",
                  TAG_LIGHT_SOURCE,
                  1 << TIFFTag.TIFF_SHORT);

            addValueName(LIGHT_SOURCE_UNKNOWN, "Unknown");
            addValueName(LIGHT_SOURCE_DAYLIGHT, "Daylight");
            addValueName(LIGHT_SOURCE_FLUORESCENT, "Fluorescent");
            addValueName(LIGHT_SOURCE_TUNGSTEN, "Tungsten");
            addValueName(LIGHT_SOURCE_STANDARD_LIGHT_A, "Standard Light A");
            addValueName(LIGHT_SOURCE_STANDARD_LIGHT_B, "Standard Light B");
            addValueName(LIGHT_SOURCE_STANDARD_LIGHT_C, "Standard Light C");
            addValueName(LIGHT_SOURCE_D55, "D55");
            addValueName(LIGHT_SOURCE_D65, "D65");
            addValueName(LIGHT_SOURCE_D75, "D75");
            addValueName(LIGHT_SOURCE_OTHER, "Other");
        }
    }

    static class Flash extends TIFFTag {

        public Flash() {
            super("Flash",
                  TAG_FLASH,
                  1 << TIFFTag.TIFF_SHORT);
        
            addValueName(FLASH_DID_NOT_FIRE, "Flash Did Not Fire");
            addValueName(FLASH_FIRED, "Flash Fired");
            addValueName(FLASH_STROBE_RETURN_LIGHT_NOT_DETECTED,
                         "Strobe Return Light Not Detected");
            addValueName(FLASH_STROBE_RETURN_LIGHT_DETECTED,
                         "Strobe Return Light Detected");
        }
    }

    static class FocalLength extends TIFFTag {

        public FocalLength() {
            super("FocalLength",
                  TAG_FOCAL_LENGTH,
                  1 << TIFFTag.TIFF_RATIONAL);
        }
    }

    static class SubjectArea extends TIFFTag {

        public SubjectArea() {
            super("SubjectArea",
                  TAG_SUBJECT_AREA,
                  1 << TIFFTag.TIFF_SHORT);
        }
    }

    static class FlashEnergy extends TIFFTag {

        public FlashEnergy() {
            super("FlashEnergy",
                  TAG_FLASH_ENERGY,
                  1 << TIFFTag.TIFF_RATIONAL);
        }
    }

    static class SpatialFrequencyResponse extends TIFFTag {

        public SpatialFrequencyResponse() {
            super("SpatialFrequencyResponse",
                  TAG_SPATIAL_FREQUENCY_RESPONSE,
                  1 << TIFFTag.TIFF_UNDEFINED);
        }
    }

    static class FocalPlaneXResolution extends TIFFTag {

        public FocalPlaneXResolution() {
            super("FocalPlaneXResolution",
                  TAG_FOCAL_PLANE_X_RESOLUTION,
                  1 << TIFFTag.TIFF_RATIONAL);
        }
    }

    static class FocalPlaneYResolution extends TIFFTag {

        public FocalPlaneYResolution() {
            super("FocalPlaneYResolution",
                  TAG_FOCAL_PLANE_Y_RESOLUTION,
                  1 << TIFFTag.TIFF_RATIONAL);
        }
    }

    static class FocalPlaneResolutionUnit extends TIFFTag {

        public FocalPlaneResolutionUnit() {
            super("FocalPlaneResolutionUnit",
                  TAG_FOCAL_PLANE_RESOLUTION_UNIT,
                  1 << TIFFTag.TIFF_SHORT);

            addValueName(FOCAL_PLANE_RESOLUTION_UNIT_NONE, "None");
            addValueName(FOCAL_PLANE_RESOLUTION_UNIT_INCH, "Inch");
            addValueName(FOCAL_PLANE_RESOLUTION_UNIT_CENTIMETER, "Centimeter");
        }
    }

    static class SubjectLocation extends TIFFTag {

        public SubjectLocation() {
            super("SubjectLocation",
                  TAG_SUBJECT_LOCATION,
                  1 << TIFFTag.TIFF_SHORT);
        }
    }

    static class ExposureIndex extends TIFFTag {

        public ExposureIndex() {
            super("ExposureIndex",
                  TAG_EXPOSURE_INDEX,
                  1 << TIFFTag.TIFF_RATIONAL);
        }
    }

    static class SensingMethod extends TIFFTag {

        public SensingMethod() {
            super("SensingMethod",
                  TAG_SENSING_METHOD,
                  1 << TIFFTag.TIFF_SHORT);
            
            addValueName(SENSING_METHOD_NOT_DEFINED, "Not Defined");
            addValueName(SENSING_METHOD_ONE_CHIP_COLOR_AREA_SENSOR,
                         "One-chip color area sensor");
            addValueName(SENSING_METHOD_TWO_CHIP_COLOR_AREA_SENSOR,
                         "Two-chip color area sensor");
            addValueName(SENSING_METHOD_THREE_CHIP_COLOR_AREA_SENSOR,
                         "Three-chip color area sensor");
            addValueName(SENSING_METHOD_COLOR_SEQUENTIAL_AREA_SENSOR,
                         "Color sequential area sensor");
            addValueName(SENSING_METHOD_TRILINEAR_SENSOR, "Trilinear sensor");
            addValueName(SENSING_METHOD_COLOR_SEQUENTIAL_LINEAR_SENSOR,
                         "Color sequential linear sensor");
        }
    }

    static class FileSource extends TIFFTag {

        public FileSource() {
            super("FileSource",
                  TAG_FILE_SOURCE,
                  1 << TIFFTag.TIFF_UNDEFINED);

            addValueName(FILE_SOURCE_DSC, "DSC");
        }
    }

    static class SceneType extends TIFFTag {

        public SceneType() {
            super("SceneType",
                  TAG_SCENE_TYPE,
                  1 << TIFFTag.TIFF_UNDEFINED);

            addValueName(SCENE_TYPE_DSC, "A directly photographed image");
        }
    }

    static class CFAPattern extends TIFFTag {

        public CFAPattern() {
            super("CFAPattern",
                  TAG_CFA_PATTERN,
                  1 << TIFFTag.TIFF_UNDEFINED);
        }
    }

    static class CustomRendered extends TIFFTag {

        public CustomRendered() {
            super("CustomRendered",
                  TAG_CUSTOM_RENDERED,
                  1 << TIFFTag.TIFF_SHORT);

            addValueName(CUSTOM_RENDERED_NORMAL, "Normal process");
            addValueName(CUSTOM_RENDERED_CUSTOM, "Custom process");
        }
    }

    static class ExposureMode extends TIFFTag {

        public ExposureMode() {
            super("ExposureMode",
                  TAG_EXPOSURE_MODE,
                  1 << TIFFTag.TIFF_SHORT);

            addValueName(EXPOSURE_MODE_AUTO_EXPOSURE, "Auto exposure");
            addValueName(EXPOSURE_MODE_MANUAL_EXPOSURE, "Manual exposure");
            addValueName(EXPOSURE_MODE_AUTO_BRACKET, "Auto bracket");
        }
    }

    static class WhiteBalance extends TIFFTag {

        public WhiteBalance() {
            super("WhiteBalance",
                  TAG_WHITE_BALANCE,
                  1 << TIFFTag.TIFF_SHORT);

            addValueName(WHITE_BALANCE_AUTO, "Auto white balance");
            addValueName(WHITE_BALANCE_MANUAL, "Manual white balance");
        }
    }

    static class DigitalZoomRatio extends TIFFTag {

        public DigitalZoomRatio() {
            super("DigitalZoomRatio",
                  TAG_DIGITAL_ZOOM_RATIO,
                  1 << TIFFTag.TIFF_RATIONAL);
        }
    }

    static class FocalLengthIn35mmFilm extends TIFFTag {

        public FocalLengthIn35mmFilm() {
            super("FocalLengthIn35mmFilm",
                  TAG_FOCAL_LENGTH_IN_35MM_FILM,
                  1 << TIFFTag.TIFF_SHORT);
        }
    }

    static class SceneCaptureType extends TIFFTag {

        public SceneCaptureType() {
            super("SceneCaptureType",
                  TAG_SCENE_CAPTURE_TYPE,
                  1 << TIFFTag.TIFF_SHORT);

            addValueName(SCENE_CAPTURE_TYPE_STANDARD, "Standard");
            addValueName(SCENE_CAPTURE_TYPE_LANDSCAPE, "Landscape");
            addValueName(SCENE_CAPTURE_TYPE_PORTRAIT, "Portrait");
            addValueName(SCENE_CAPTURE_TYPE_NIGHT_SCENE, "Night scene");
        }
    }

    static class GainControl extends TIFFTag {

        public GainControl() {
            super("GainControl",
                  TAG_GAIN_CONTROL,
                  1 << TIFFTag.TIFF_SHORT);

            addValueName(GAIN_CONTROL_NONE, "None");
            addValueName(GAIN_CONTROL_LOW_GAIN_UP, "Low gain up");
            addValueName(GAIN_CONTROL_HIGH_GAIN_UP, "High gain up");
            addValueName(GAIN_CONTROL_LOW_GAIN_DOWN, "Low gain down");
            addValueName(GAIN_CONTROL_HIGH_GAIN_DOWN, "High gain down");
        }
    }

    static class Contrast extends TIFFTag {

        public Contrast() {
            super("Contrast",
                  TAG_CONTRAST,
                  1 << TIFFTag.TIFF_SHORT);

            addValueName(CONTRAST_NORMAL, "Normal");
            addValueName(CONTRAST_SOFT, "Soft");
            addValueName(CONTRAST_HARD, "Hard");
        }
    }

    static class Saturation extends TIFFTag {

        public Saturation() {
            super("Saturation",
                  TAG_SATURATION,
                  1 << TIFFTag.TIFF_SHORT);

            addValueName(SATURATION_NORMAL, "Normal");
            addValueName(SATURATION_LOW, "Low saturation");
            addValueName(SATURATION_HIGH, "High saturation");
        }
    }

    static class Sharpness extends TIFFTag {

        public Sharpness() {
            super("Sharpness",
                  TAG_SHARPNESS,
                  1 << TIFFTag.TIFF_SHORT);

            addValueName(SHARPNESS_NORMAL, "Normal");
            addValueName(SHARPNESS_SOFT, "Soft");
            addValueName(SHARPNESS_HARD, "Hard");
        }
    }

    static class DeviceSettingDescription extends TIFFTag {

        public DeviceSettingDescription() {
            super("DeviceSettingDescription",
                  TAG_DEVICE_SETTING_DESCRIPTION,
                  1 << TIFFTag.TIFF_UNDEFINED);
        }
    }

    static class SubjectDistanceRange extends TIFFTag {

        public SubjectDistanceRange() {
            super("SubjectDistanceRange",
                  TAG_SUBJECT_DISTANCE_RANGE,
                  1 << TIFFTag.TIFF_SHORT);

            addValueName(SUBJECT_DISTANCE_RANGE_UNKNOWN, "unknown");
            addValueName(SUBJECT_DISTANCE_RANGE_MACRO, "Macro");
            addValueName(SUBJECT_DISTANCE_RANGE_CLOSE_VIEW, "Close view");
            addValueName(SUBJECT_DISTANCE_RANGE_DISTANT_VIEW, "Distant view");
        }
    }

    static class ImageUniqueID extends TIFFTag {

        public ImageUniqueID() {
            super("ImageUniqueID",
                  TAG_IMAGE_UNIQUE_ID,
                  1 << TIFFTag.TIFF_ASCII);
        }
    }

    static class InteroperabilityIFD extends TIFFTag {
        public InteroperabilityIFD() {
            super("InteroperabilityIFD",
                  TAG_INTEROPERABILITY_IFD_POINTER,
                  1 << TIFFTag.TIFF_LONG,
                  EXIFInteroperabilityTagSet.getInstance());
        }
    }

    private static List tags;

    private static void initTags() {
        tags = new ArrayList(42);

        tags.add(new EXIFTIFFTagSet.EXIFVersion());
        tags.add(new EXIFTIFFTagSet.FlashPixVersion());
        tags.add(new EXIFTIFFTagSet.ColorSpace());
        tags.add(new EXIFTIFFTagSet.ComponentsConfiguration());
        tags.add(new EXIFTIFFTagSet.CompressedBitsPerPixel());
        tags.add(new EXIFTIFFTagSet.PixelXDimension());
        tags.add(new EXIFTIFFTagSet.PixelYDimension());
        tags.add(new EXIFTIFFTagSet.MakerNote());
        tags.add(new EXIFTIFFTagSet.UserComment());
        tags.add(new EXIFTIFFTagSet.RelatedSoundFile());
        tags.add(new EXIFTIFFTagSet.DateTimeOriginal());
        tags.add(new EXIFTIFFTagSet.DateTimeDigitized());
        tags.add(new EXIFTIFFTagSet.SubSecTime());
        tags.add(new EXIFTIFFTagSet.SubSecTimeOriginal());
        tags.add(new EXIFTIFFTagSet.SubSecTimeDigitized());
        tags.add(new EXIFTIFFTagSet.ExposureTime());
        tags.add(new EXIFTIFFTagSet.FNumber());
        tags.add(new EXIFTIFFTagSet.ExposureProgram());
        tags.add(new EXIFTIFFTagSet.SpectralSensitivity());
        tags.add(new EXIFTIFFTagSet.ISOSpeedRatings());
        tags.add(new EXIFTIFFTagSet.OECF());
        tags.add(new EXIFTIFFTagSet.ShutterSpeedValue());
        tags.add(new EXIFTIFFTagSet.ApertureValue());
        tags.add(new EXIFTIFFTagSet.BrightnessValue());
        tags.add(new EXIFTIFFTagSet.ExposureBiasValue());
        tags.add(new EXIFTIFFTagSet.MaxApertureValue());
        tags.add(new EXIFTIFFTagSet.SubjectDistance());
        tags.add(new EXIFTIFFTagSet.MeteringMode());
        tags.add(new EXIFTIFFTagSet.LightSource());
        tags.add(new EXIFTIFFTagSet.Flash());
        tags.add(new EXIFTIFFTagSet.FocalLength());
        tags.add(new EXIFTIFFTagSet.SubjectArea());
        tags.add(new EXIFTIFFTagSet.FlashEnergy());
        tags.add(new EXIFTIFFTagSet.SpatialFrequencyResponse());
        tags.add(new EXIFTIFFTagSet.FocalPlaneXResolution());
        tags.add(new EXIFTIFFTagSet.FocalPlaneYResolution());
        tags.add(new EXIFTIFFTagSet.FocalPlaneResolutionUnit());
        tags.add(new EXIFTIFFTagSet.SubjectLocation());
        tags.add(new EXIFTIFFTagSet.ExposureIndex());
        tags.add(new EXIFTIFFTagSet.SensingMethod());
        tags.add(new EXIFTIFFTagSet.FileSource());
        tags.add(new EXIFTIFFTagSet.SceneType());
        tags.add(new EXIFTIFFTagSet.CFAPattern());
        tags.add(new EXIFTIFFTagSet.CustomRendered());
        tags.add(new EXIFTIFFTagSet.ExposureMode());
        tags.add(new EXIFTIFFTagSet.WhiteBalance());
        tags.add(new EXIFTIFFTagSet.DigitalZoomRatio());
        tags.add(new EXIFTIFFTagSet.FocalLengthIn35mmFilm());
        tags.add(new EXIFTIFFTagSet.SceneCaptureType());
        tags.add(new EXIFTIFFTagSet.GainControl());
        tags.add(new EXIFTIFFTagSet.Contrast());
        tags.add(new EXIFTIFFTagSet.Saturation());
        tags.add(new EXIFTIFFTagSet.Sharpness());
        tags.add(new EXIFTIFFTagSet.DeviceSettingDescription());
        tags.add(new EXIFTIFFTagSet.SubjectDistanceRange());
        tags.add(new EXIFTIFFTagSet.ImageUniqueID());
        tags.add(new EXIFTIFFTagSet.InteroperabilityIFD());
    }

    private EXIFTIFFTagSet() {
        super(tags);
    }

    /**
     * Returns a shared instance of an <code>EXIFTIFFTagSet</code>.
     *
     * @return an <code>EXIFTIFFTagSet</code> instance.
     */
    public synchronized static EXIFTIFFTagSet getInstance() {
        if (theInstance == null) {
            initTags();
            theInstance = new EXIFTIFFTagSet();
            tags = null;
        }
        return theInstance;
    }
}
