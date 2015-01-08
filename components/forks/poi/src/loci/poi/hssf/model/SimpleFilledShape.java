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

import loci.poi.ddf.*;
import loci.poi.hssf.record.ObjRecord;
import loci.poi.hssf.record.EscherAggregate;
import loci.poi.hssf.record.CommonObjectDataSubRecord;
import loci.poi.hssf.record.EndSubRecord;
import loci.poi.hssf.usermodel.HSSFSimpleShape;
import loci.poi.hssf.usermodel.HSSFShape;

public class SimpleFilledShape
        extends AbstractShape
{
    private EscherContainerRecord spContainer;
    private ObjRecord objRecord;

    /**
     * Creates the low evel records for an oval.
     *
     * @param hssfShape  The highlevel shape.
     * @param shapeId    The shape id to use for this shape.
     */
    SimpleFilledShape( HSSFSimpleShape hssfShape, int shapeId )
    {
        spContainer = createSpContainer( hssfShape, shapeId );
        objRecord = createObjRecord( hssfShape, shapeId );
    }

    /**
     * Generates the shape records for this shape.
     *
     * @param hssfShape
     * @param shapeId
     */
    private EscherContainerRecord createSpContainer( HSSFSimpleShape hssfShape, int shapeId )
    {
        HSSFShape shape = hssfShape;

        EscherContainerRecord spContainer = new EscherContainerRecord();
        EscherSpRecord sp = new EscherSpRecord();
        EscherOptRecord opt = new EscherOptRecord();
        EscherClientDataRecord clientData = new EscherClientDataRecord();

        spContainer.setRecordId( EscherContainerRecord.SP_CONTAINER );
        spContainer.setOptions( (short) 0x000F );
        sp.setRecordId( EscherSpRecord.RECORD_ID );
        short shapeType = objTypeToShapeType( hssfShape.getShapeType() );
        sp.setOptions( (short) ( ( shapeType << 4 ) | 0x2 ) );
        sp.setShapeId( shapeId );
        sp.setFlags( EscherSpRecord.FLAG_HAVEANCHOR | EscherSpRecord.FLAG_HASSHAPETYPE );
        opt.setRecordId( EscherOptRecord.RECORD_ID );
        addStandardOptions(shape, opt);
        EscherRecord anchor = createAnchor( shape.getAnchor() );
        clientData.setRecordId( EscherClientDataRecord.RECORD_ID );
        clientData.setOptions( (short) 0x0000 );

        spContainer.addChildRecord( sp );
        spContainer.addChildRecord( opt );
        spContainer.addChildRecord( anchor );
        spContainer.addChildRecord( clientData );

        return spContainer;
    }

    private short objTypeToShapeType( int objType )
    {
        short shapeType;
        if (objType == HSSFSimpleShape.OBJECT_TYPE_OVAL)
            shapeType = EscherAggregate.ST_ELLIPSE;
        else if (objType == HSSFSimpleShape.OBJECT_TYPE_RECTANGLE)
            shapeType = EscherAggregate.ST_RECTANGLE;
        else
            throw new IllegalArgumentException("Unable to handle an object of this type");
        return shapeType;
    }

    /**
     * Creates the low level OBJ record for this shape.
     */
    private ObjRecord createObjRecord( HSSFShape hssfShape, int shapeId )
    {
        HSSFShape shape = hssfShape;

        ObjRecord obj = new ObjRecord();
        CommonObjectDataSubRecord c = new CommonObjectDataSubRecord();
        c.setObjectType( (short) ( (HSSFSimpleShape) shape ).getShapeType() );
        c.setObjectId( (short) ( shapeId ) );
        c.setLocked( true );
        c.setPrintable( true );
        c.setAutofill( true );
        c.setAutoline( true );
        EndSubRecord e = new EndSubRecord();

        obj.addSubRecord( c );
        obj.addSubRecord( e );

        return obj;
    }

    public EscherContainerRecord getSpContainer()
    {
        return spContainer;
    }

    public ObjRecord getObjRecord()
    {
        return objRecord;
    }

}
