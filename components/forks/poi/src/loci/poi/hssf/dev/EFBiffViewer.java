/*
 * #%L
 * Fork of Apache Jakarta POI.
 * %%
 * Copyright (C) 2008 - 2016 Open Microscopy Environment:
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

package loci.poi.hssf.dev;

import java.io.InputStream;
import java.io.IOException;

import loci.common.*;

import loci.poi.poifs.filesystem.POIFSFileSystem;
import loci.poi.hssf.record.Record;

import loci.poi.hssf.eventusermodel.HSSFRequest;
import loci.poi.hssf.eventusermodel.HSSFListener;
import loci.poi.hssf.eventusermodel.HSSFEventFactory;

/**
 *
 * @author  andy
 */

public class EFBiffViewer
{
    String file;

    /** Creates a new instance of EFBiffViewer */

    public EFBiffViewer()
    {
    }

    public void run()
        throws IOException
    {
        RandomAccessInputStream fin   = new RandomAccessInputStream(file);
        POIFSFileSystem poifs = new POIFSFileSystem(fin, 512);
        InputStream     din   = poifs.createDocumentInputStream("Workbook");
        HSSFRequest     req   = new HSSFRequest();

        req.addListenerForAllRecords(new HSSFListener()
        {
            public void processRecord(Record rec)
            {
                System.out.println(rec.toString());
            }
        });
        HSSFEventFactory factory = new HSSFEventFactory();

        factory.processEvents(req, din);
    }

    public void setFile(String file)
    {
        this.file = file;
    }

    public static void main(String [] args)
    {
        if ((args.length == 1) && !args[ 0 ].equals("--help"))
        {
            try
            {
                EFBiffViewer viewer = new EFBiffViewer();

                viewer.setFile(args[ 0 ]);
                viewer.run();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("EFBiffViewer");
            System.out.println(
                "Outputs biffview of records based on HSSFEventFactory");
            System.out
                .println("usage: java loci.poi.hssf.dev.EBBiffViewer "
                         + "filename");
        }
    }
}
