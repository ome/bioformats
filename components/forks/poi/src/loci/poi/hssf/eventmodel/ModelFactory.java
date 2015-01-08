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

package loci.poi.hssf.eventmodel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import loci.poi.hssf.model.Model;
import loci.poi.hssf.model.Sheet;
import loci.poi.hssf.model.Workbook;
import loci.poi.hssf.record.BOFRecord;
import loci.poi.hssf.record.EOFRecord;
import loci.poi.hssf.record.Record;


/**
 * ModelFactory creates workbook and sheet models based upon 
 * events thrown by them there events from the EventRecordFactory.
 * 
 * @see loci.poi.hssf.eventmodel.EventRecordFactory
 * @author Andrew C. Oliver acoliver@apache.org
 */
public class ModelFactory implements ERFListener
{

    List listeners;
    Model currentmodel;
    boolean lastEOF;
 
    /**
     * Constructor for ModelFactory.  Does practically nothing.
     */
    public ModelFactory()
    {
        super();
        listeners = new ArrayList(1);
    }
    
    /**
     * register a ModelFactoryListener so that it can receive 
     * Models as they are created.
     */
    public void registerListener(ModelFactoryListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Start processing the Workbook stream into Model events.
     */
    public void run(InputStream stream) {
        EventRecordFactory factory = new EventRecordFactory(true);
        factory.registerListener(this,null);
        lastEOF = true;
        factory.processRecords(stream);
    }

    //ERFListener
    public boolean processRecord(Record rec)
    {
       if (rec.getSid() == BOFRecord.sid) {
             if (lastEOF != true) {
              throw new RuntimeException("Not yet handled embedded models");  
             } else {
              BOFRecord bof = (BOFRecord)rec;
              switch (bof.getType()) {
               case BOFRecord.TYPE_WORKBOOK:
                 currentmodel = new Workbook();                 
               break;
               case BOFRecord.TYPE_WORKSHEET:
                 currentmodel = new Sheet();                                  
               break;
              default:
                   throw new RuntimeException("Unsupported model type "+bof.getType());
              }                
               
             }        
        }
        
        if (rec.getSid() == EOFRecord.sid) {
            lastEOF = true;
            throwEvent(currentmodel);
        } else {
            lastEOF = false;   
        }
        
 
        return true;
    }

    /**
     * Throws the model as an event to the listeners
     * @param model to be thrown
     */
    private void throwEvent(Model model)
    {
        Iterator i = listeners.iterator();
        while (i.hasNext()) {
          ModelFactoryListener mfl = (ModelFactoryListener) i.next();
          mfl.process(model);
        }
    }


}
