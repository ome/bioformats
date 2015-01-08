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
        

package loci.poi.poifs.filesystem;

import java.io.*;

import java.util.*;

import loci.common.*;

import loci.poi.hpsf.ClassID;
import loci.poi.poifs.dev.POIFSViewable;
import loci.poi.poifs.property.DirectoryProperty;
import loci.poi.poifs.property.DocumentProperty;
import loci.poi.poifs.property.Property;
import loci.poi.util.*;

/**
 * Simple implementation of DirectoryEntry
 *
 * @author Marc Johnson (mjohnson at apache dot org)
 */

public class DirectoryNode
    extends EntryNode
    implements DirectoryEntry, POIFSViewable
{

    // Map of Entry instances, keyed by their names
    private Map               _entries;

    // the POIFSFileSystem we belong to
    private POIFSFileSystem   _filesystem;

    // the path described by this document
    private POIFSDocumentPath _path;

    /**
     * create a DirectoryNode. This method is not public by design; it
     * is intended strictly for the internal use of this package
     *
     * @param property the DirectoryProperty for this DirectoryEntry
     * @param filesystem the POIFSFileSystem we belong to
     * @param parent the parent of this entry
     */

    DirectoryNode(final DirectoryProperty property,
                  final POIFSFileSystem filesystem,
                  final DirectoryNode parent)
    {
        super(property, parent);
        if (parent == null)
        {
            _path = new POIFSDocumentPath();
        }
        else
        {
            _path = new POIFSDocumentPath(parent._path, new String[]
            {
                property.getName()
            });
        }
        _filesystem = filesystem;
        _entries    = new HashMap();
        Iterator iter = property.getChildren();

        while (iter.hasNext())
        {
            Property child     = ( Property ) iter.next();
            Entry    childNode = null;

            if (child.isDirectory())
            {
                childNode = new DirectoryNode(( DirectoryProperty ) child,
                                              _filesystem, this);
            }
            else
            {
                childNode = new DocumentNode(( DocumentProperty ) child,
                                             this);
            }
            _entries.put(childNode.getName(), childNode);
        }
    }

    /**
     * @return this directory's path representation
     */

    public POIFSDocumentPath getPath()
    {
        return _path;
    }

    /**
     * create a new DocumentEntry
     *
     * @param document the new document
     *
     * @return the new DocumentEntry
     *
     * @exception IOException
     */

    DocumentEntry createDocument(final POIFSDocument document)
        throws IOException
    {
        DocumentProperty property = document.getDocumentProperty();
        DocumentNode     rval     = new DocumentNode(property, this);

        (( DirectoryProperty ) getProperty()).addChild(property);
        _filesystem.addDocument(document);
        _entries.put(property.getName(), rval);
        return rval;
    }

    /**
     * Change a contained Entry's name
     *
     * @param oldName the original name
     * @param newName the new name
     *
     * @return true if the operation succeeded, else false
     */

    boolean changeName(final String oldName, final String newName)
    {
        boolean   rval  = false;
        EntryNode child = ( EntryNode ) _entries.get(oldName);

        if (child != null)
        {
            rval = (( DirectoryProperty ) getProperty())
                .changeName(child.getProperty(), newName);
            if (rval)
            {
                _entries.remove(oldName);
                _entries.put(child.getProperty().getName(), child);
            }
        }
        return rval;
    }

    /**
     * Delete an entry
     *
     * @param entry the EntryNode to be deleted
     *
     * @return true if the entry was deleted, else false
     */

    boolean deleteEntry(final EntryNode entry)
    {
        boolean rval =
            (( DirectoryProperty ) getProperty())
                .deleteChild(entry.getProperty());

        if (rval)
        {
            _entries.remove(entry.getName());
            _filesystem.remove(entry);
        }
        return rval;
    }

    /* ********** START implementation of DirectoryEntry ********** */

    /**
     * get an iterator of the Entry instances contained directly in
     * this instance (in other words, children only; no grandchildren
     * etc.)
     *
     * @return iterator; never null, but hasNext() may return false
     *         immediately (i.e., this DirectoryEntry is empty). All
     *         objects retrieved by next() are guaranteed to be
     *         implementations of Entry.
     */

    public Iterator getEntries()
    {
        return _entries.values().iterator();
    }

    /**
     * is this DirectoryEntry empty?
     *
     * @return true if this instance contains no Entry instances
     */

    public boolean isEmpty()
    {
        return _entries.isEmpty();
    }

    /**
     * find out how many Entry instances are contained directly within
     * this DirectoryEntry
     *
     * @return number of immediately (no grandchildren etc.) contained
     *         Entry instances
     */

    public int getEntryCount()
    {
        return _entries.size();
    }

    /**
     * get a specified Entry by name
     *
     * @param name the name of the Entry to obtain.
     *
     * @return the specified Entry, if it is directly contained in
     *         this DirectoryEntry
     *
     * @exception FileNotFoundException if no Entry with the specified
     *            name exists in this DirectoryEntry
     */

    public Entry getEntry(final String name)
        throws FileNotFoundException
    {
        Entry rval = null;

        if (name != null)
        {
            rval = ( Entry ) _entries.get(name);
        }
        if (rval == null)
        {

            // either a null name was given, or there is no such name
            throw new FileNotFoundException("no such entry: \"" + name
                                            + "\"");
        }
        return rval;
    }

    /**
     * create a new DocumentEntry
     *
     * @param name the name of the new DocumentEntry
     * @param stream the InputStream from which to create the new
     *               DocumentEntry
     *
     * @return the new DocumentEntry
     *
     * @exception IOException
     */

    public DocumentEntry createDocument(final String name,
                                        final RandomAccessInputStream stream)
        throws IOException
    {
        return createDocument(new POIFSDocument(name, stream, 512));
    }

    /**
     * create a new DocumentEntry; the data will be provided later
     *
     * @param name the name of the new DocumentEntry
     * @param size the size of the new DocumentEntry
     * @param writer the writer of the new DocumentEntry
     *
     * @return the new DocumentEntry
     *
     * @exception IOException
     */

    public DocumentEntry createDocument(final String name, final int size,
                                        final POIFSWriterListener writer)
        throws IOException
    {
        return createDocument(new POIFSDocument(name, size, _path, writer,
          512));
    }

    /**
     * create a new DirectoryEntry
     *
     * @param name the name of the new DirectoryEntry
     *
     * @return the new DirectoryEntry
     *
     * @exception IOException
     */

    public DirectoryEntry createDirectory(final String name)
        throws IOException
    {
        DirectoryProperty property = new DirectoryProperty(name);
        DirectoryNode     rval     = new DirectoryNode(property, _filesystem,
                                         this);

        (( DirectoryProperty ) getProperty()).addChild(property);
        _filesystem.addDirectory(property);
        _entries.put(name, rval);
        return rval;
    }

    /**
     * Gets the storage clsid of the directory entry
     *
     * @return storage Class ID
     */
    public ClassID getStorageClsid()
    {
        return getProperty().getStorageClsid();
    }

    /**
     * Sets the storage clsid for the directory entry
     *
     * @param clsidStorage storage Class ID
     */
    public void setStorageClsid(ClassID clsidStorage)
    {
        getProperty().setStorageClsid(clsidStorage);
    }

    /* **********  END  implementation of DirectoryEntry ********** */
    /* ********** START implementation of Entry ********** */

    /**
     * is this a DirectoryEntry?
     *
     * @return true if the Entry is a DirectoryEntry, else false
     */

    public boolean isDirectoryEntry()
    {
        return true;
    }

    /* **********  END  implementation of Entry ********** */
    /* ********** START extension of Entry ********** */

    /**
     * extensions use this method to verify internal rules regarding
     * deletion of the underlying store.
     *
     * @return true if it's ok to delete the underlying store, else
     *         false
     */

    protected boolean isDeleteOK()
    {

        // if this directory is empty, we can delete it
        return isEmpty();
    }

    /* **********  END  extension of Entry ********** */
    /* ********** START begin implementation of POIFSViewable ********** */

    /**
     * Get an array of objects, some of which may implement
     * POIFSViewable
     *
     * @return an array of Object; may not be null, but may be empty
     */

    public Object [] getViewableArray()
    {
        return new Object[ 0 ];
    }

    /**
     * Get an Iterator of objects, some of which may implement
     * POIFSViewable
     *
     * @return an Iterator; may not be null, but may have an empty
     * back end store
     */

    public Iterator getViewableIterator()
    {
        List components = new ArrayList();

        components.add(getProperty());
        SortedMap sortedEntries = new TreeMap(_entries);
        Iterator  iter          = sortedEntries.values().iterator();

        while (iter.hasNext())
        {
            components.add(iter.next());
        }
        return components.iterator();
    }

    /**
     * Give viewers a hint as to whether to call getViewableArray or
     * getViewableIterator
     *
     * @return true if a viewer should call getViewableArray, false if
     *         a viewer should call getViewableIterator
     */

    public boolean preferArray()
    {
        return false;
    }

    /**
     * Provides a short description of the object, to be used when a
     * POIFSViewable object has not provided its contents.
     *
     * @return short description
     */

    public String getShortDescription()
    {
        return getName();
    }

    /* **********  END  begin implementation of POIFSViewable ********** */
}   // end public class DirectoryNode

