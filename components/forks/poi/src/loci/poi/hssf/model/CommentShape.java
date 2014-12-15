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
package loci.poi.hssf.model;

import loci.poi.hssf.record.*;
import loci.poi.hssf.usermodel.HSSFComment;
import loci.poi.hssf.usermodel.HSSFShape;
import loci.poi.util.LittleEndian;
import loci.poi.ddf.*;

import java.util.List;
import java.util.Iterator;

/**
 * Represents a cell comment.
 * This class converts highlevel model data from <code>HSSFComment</code>
 * to low-level records.
 *
 * @author Yegor Kozlov
 */
public class CommentShape extends TextboxShape {

    private NoteRecord note;

    /**
     * Creates the low-level records for a comment.
     *
     * @param hssfShape  The highlevel shape.
     * @param shapeId    The shape id to use for this shape.
     */
     public CommentShape( HSSFComment hssfShape, int shapeId )
    {
        super(hssfShape, shapeId);

        note = createNoteRecord(hssfShape, shapeId);

        ObjRecord obj = getObjRecord();
        List records = obj.getSubRecords();
        int cmoIdx = 0;
        for (int i = 0; i < records.size(); i++) {
            Object r = records.get(i);

            if (r instanceof CommonObjectDataSubRecord){
                //modify autofill attribute inherited from <code>TextObjectRecord</code>
                CommonObjectDataSubRecord cmo = (CommonObjectDataSubRecord)r;
                cmo.setAutofill(false);
                cmoIdx = i;
            }
        }
        //add NoteStructure sub record
        //we don't know it's format, for now the record data is empty
        NoteStructureSubRecord u = new NoteStructureSubRecord();
        obj.addSubRecord(cmoIdx+1, u);
    }

    /**
     * Creates the low level <code>NoteRecord</code>
     *  which holds the comment attributes.
     */
     private NoteRecord createNoteRecord( HSSFComment shape, int shapeId )
    {
        NoteRecord note = new NoteRecord();
        note.setColumn(shape.getColumn());
        note.setRow((short)shape.getRow());
        note.setFlags(shape.isVisible() ? NoteRecord.NOTE_VISIBLE : NoteRecord.NOTE_HIDDEN);
        note.setShapeId((short)shapeId);
        note.setAuthor(shape.getAuthor() == null ? "" : shape.getAuthor());
        return note;
    }

    /**
     * Sets standard escher options for a comment.
     * This method is responsible for setting default background,
     * shading and other comment properties.
     *
     * @param shape   The highlevel shape.
     * @param opt     The escher records holding the proerties
     * @return number of escher options added
     */
    protected int addStandardOptions( HSSFShape shape, EscherOptRecord opt )
    {
        super.addStandardOptions(shape, opt);

        //remove unnecessary properties inherited from TextboxShape
        java.util.List props = opt.getEscherProperties();
        for ( Iterator iterator = props.iterator(); iterator.hasNext(); ) {
            EscherProperty prop = (EscherProperty) iterator.next();
            switch (prop.getId()){
                case EscherProperties.TEXT__TEXTLEFT:
                case EscherProperties.TEXT__TEXTRIGHT:
                case EscherProperties.TEXT__TEXTTOP:
                case EscherProperties.TEXT__TEXTBOTTOM:
                case EscherProperties.GROUPSHAPE__PRINT:
                case EscherProperties.FILL__FILLBACKCOLOR:
                case EscherProperties.LINESTYLE__COLOR:
                    iterator.remove();
                    break;
            }
        }

        HSSFComment comment = (HSSFComment)shape;
        opt.addEscherProperty( new EscherSimpleProperty( EscherProperties.GROUPSHAPE__PRINT, comment.isVisible() ? 0x000A0000 : 0x000A0002) );
        opt.addEscherProperty( new EscherSimpleProperty( EscherProperties.SHADOWSTYLE__SHADOWOBSURED, 0x00030003 ) );
        opt.addEscherProperty( new EscherSimpleProperty( EscherProperties.SHADOWSTYLE__COLOR, 0x00000000 ) );
        opt.sortProperties();
        return opt.getEscherProperties().size();   // # options added
    }

    /**
     * Return the <code>NoteRecord</code> holding the comment attributes
     *
     * @return <code>NoteRecord</code> holding the comment attributes
     */
    public NoteRecord getNoteRecord()
    {
        return note;
    }

}
