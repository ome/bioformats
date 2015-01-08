/*
 * #%L
 * Fork of Apache Jakarta POI.
 * %%
 * Copyright (C) 2008 - 2014 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package loci.poi.hssf.usermodel;

import loci.poi.hssf.model.Workbook;
import loci.poi.hssf.record.LabelSSTRecord;
import loci.poi.hssf.record.UnicodeString;

import java.util.Iterator;
/**
 * Rich text unicode string.  These strings can have fonts applied to
 * arbitary parts of the string.
 *
 * @author Glen Stampoultzis (glens at apache.org)
 * @author Jason Height (jheight at apache.org)
 */
public class HSSFRichTextString
        implements Comparable
{
    /** Place holder for indicating that NO_FONT has been applied here */
    public static final short NO_FONT = 0;

    private UnicodeString string;
    private Workbook book;
    private LabelSSTRecord record;

    public HSSFRichTextString()
    {
        this("");
    }

    public HSSFRichTextString( String string )
    {
        if (string == null)
          string = "";
        this.string = new UnicodeString(string);
    }

    HSSFRichTextString(Workbook book, LabelSSTRecord record) {
      setWorkbookReferences(book, record);
      
      this.string = book.getSSTString(record.getSSTIndex());
    }
    
    /** This must be called to setup the internal work book references whenever
     * a RichTextString is added to a cell
     */    
    void setWorkbookReferences(Workbook book, LabelSSTRecord record) {
      this.book = book;
      this.record = record;      
    }
    
    /** Called whenever the unicode string is modified. When it is modified
     *  we need to create a new SST index, so that other LabelSSTRecords will not
     *  be affected by changes tat we make to this string.
     */
    private UnicodeString cloneStringIfRequired() {
      if (book == null)
        return string;
      UnicodeString s = (UnicodeString)string.clone();
      return s;
    }

    private void addToSSTIfRequired() {
      if (book != null) {
        int index = book.addSSTString(string);
        record.setSSTIndex(index);
        //The act of adding the string to the SST record may have meant that
        //a extsing string was returned for the index, so update our local version
        string = book.getSSTString(index);
      }
    }


    /**
     * Applies a font to the specified characters of a string.
     *
     * @param startIndex    The start index to apply the font to (inclusive)
     * @param endIndex      The end index to apply the font to (exclusive)
     * @param fontIndex     The font to use.
     */
    public void applyFont(int startIndex, int endIndex, short fontIndex)
    {
        if (startIndex > endIndex)
            throw new IllegalArgumentException("Start index must be less than end index.");
        if (startIndex < 0 || endIndex > length())
            throw new IllegalArgumentException("Start and end index not in range.");
        if (startIndex == endIndex)
            return;

        //Need to check what the font is currently, so we can reapply it after
        //the range is completed
        short currentFont = NO_FONT;
        if (endIndex != length()) {
          currentFont = this.getFontAtIndex(startIndex);
        }

        //Need to clear the current formatting between the startIndex and endIndex
        string = cloneStringIfRequired();
        Iterator formatting = string.formatIterator();
        if (formatting != null) {
          while (formatting.hasNext()) {
            UnicodeString.FormatRun r = (UnicodeString.FormatRun)formatting.next();
            if ((r.getCharacterPos() >= startIndex) && (r.getCharacterPos() < endIndex))
              formatting.remove();
          }
        }


        string.addFormatRun(new UnicodeString.FormatRun((short)startIndex, fontIndex));
        if (endIndex != length())
          string.addFormatRun(new UnicodeString.FormatRun((short)endIndex, currentFont));
          
        addToSSTIfRequired();
    }

    /**
     * Applies a font to the specified characters of a string.
     *
     * @param startIndex    The start index to apply the font to (inclusive)
     * @param endIndex      The end index to apply to font to (exclusive)
     * @param font          The index of the font to use.
     */
    public void applyFont(int startIndex, int endIndex, HSSFFont font)
    {
        applyFont(startIndex, endIndex, font.getIndex());
    }

    /**
     * Sets the font of the entire string.
     * @param font          The font to use.
     */
    public void applyFont(HSSFFont font)
    {
        applyFont(0, string.getCharCount(), font);
    }

    /**
     * Removes any formatting that may have been applied to the string.
     */
    public void clearFormatting() {
      string = cloneStringIfRequired();
      string.clearFormatting();
      addToSSTIfRequired();
    }

    /**
     * Returns the plain string representation.
     */
    public String getString()
    {
        return string.getString();
    }

    /** Used internally by the HSSFCell to get the internal string value*/
    UnicodeString getUnicodeString() {
      return cloneStringIfRequired();
    }

    /** Used internally by the HSSFCell to set the internal string value*/
    void setUnicodeString(UnicodeString str) {
      this.string = str;
    }
    

    /**
     * @return  the number of characters in the font.
     */
    public int length()
    {
        return string.getCharCount();
    }

    /**
     * Returns the font in use at a particular index.
     *
     * @param index         The index.
     * @return              The font that's currently being applied at that
     *                      index or null if no font is being applied or the
     *                      index is out of range.
     */
    public short getFontAtIndex( int index )
    {
      int size = string.getFormatRunCount();
      UnicodeString.FormatRun currentRun = null;
      for (int i=0;i<size;i++) {
        UnicodeString.FormatRun r = string.getFormatRun(i);
        if (r.getCharacterPos() > index)
          break;
        else currentRun = r;
      }
      if (currentRun == null)
        return NO_FONT;
      else return currentRun.getFontIndex();
    }

    /**
     * @return  The number of formatting runs used. There will always be at
     *          least one of font NO_FONT.
     *
     * @see #NO_FONT
     */
    public int numFormattingRuns()
    {
        return string.getFormatRunCount();
    }

    /**
     * The index within the string to which the specified formatting run applies.
     * @param index     the index of the formatting run
     * @return  the index within the string.
     */
    public int getIndexOfFormattingRun(int index)
    {
        UnicodeString.FormatRun r = string.getFormatRun(index);
        return r.getCharacterPos();
    }

    /**
     * Gets the font used in a particular formatting run.
     *
     * @param index     the index of the formatting run
     * @return  the font number used.
     */
    public short getFontOfFormattingRun(int index)
    {
      UnicodeString.FormatRun r = string.getFormatRun(index);
      return r.getFontIndex();
    }

    /**
     * Compares one rich text string to another.
     */
    public int compareTo( Object o )
    {
       HSSFRichTextString r = (HSSFRichTextString)o;
       return string.compareTo(r.string);
    }

    public boolean equals(Object o) {
      if (o instanceof HSSFRichTextString) {
        return string.equals(((HSSFRichTextString)o).string);
      }
      return false;
    
    }

    /**
     * @return  the plain text representation of this string.
     */
    public String toString()
    {
        return string.toString();
    }

    /**
     * Applies the specified font to the entire string.
     *
     * @param fontIndex  the font to apply.
     */
    public void applyFont( short fontIndex )
    {
        applyFont(0, string.getCharCount(), fontIndex);
    }
}
