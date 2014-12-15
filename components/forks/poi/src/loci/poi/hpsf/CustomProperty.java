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

package loci.poi.hpsf;

/**
 * <p>This class represents custum properties in the document summary
 * information stream. The difference to normal properties is that custom
 * properties have an optional name. If the name is not <code>null</code> it
 * will be maintained in the section's dictionary.</p>
 * 
 * @author Rainer Klute <a
 *         href="mailto:klute@rainer-klute.de">&lt;klute@rainer-klute.de&gt;</a>
 * @since 2006-02-09
 * @version $Id$
 */
public class CustomProperty extends MutableProperty
{

    private String name;

    /**
     * <p>Creates an empty {@link CustomProperty}. The set methods must be
     * called to make it usable.</p>
     */
    public CustomProperty()
    {
        this.name = null;
    }

    /**
     * <p>Creates a {@link CustomProperty} without a name by copying the
     * underlying {@link Property}' attributes.</p>
     * 
     * @param property the property to copy
     */
    public CustomProperty(final Property property)
    {
        this(property, null);
    }

    /**
     * <p>Creates a {@link CustomProperty} with a name.</p>
     * 
     * @param property This property's attributes are copied to the new custom
     *        property.
     * @param name The new custom property's name.
     */
    public CustomProperty(final Property property, final String name)
    {
        super(property);
        this.name = name;
    }

    /**
     * <p>Gets the property's name.</p>
     *
     * @return the property's name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * <p>Sets the property's name.</p>
     *
     * @param name The name to set.
     */
    public void setName(final String name)
    {
        this.name = name;
    }


    /**
     * <p>Compares two custom properties for equality. The method returns
     * <code>true</code> if all attributes of the two custom properties are
     * equal.</p>
     * 
     * @param o The custom property to compare with.
     * @return <code>true</code> if both custom properties are equal, else
     *         <code>false</code>.
     * 
     * @see java.util.AbstractSet#equals(java.lang.Object)
     */
    public boolean equalsContents(final Object o)
    {
        final CustomProperty c = (CustomProperty) o;
        final String name1 = c.getName();
        final String name2 = this.getName();
        boolean equalNames = true;
        if (name1 == null)
            equalNames = name2 == null;
        else
            equalNames = name1.equals(name2);
        return equalNames && c.getID() == this.getID()
                && c.getType() == this.getType()
                && c.getValue().equals(this.getValue());
    }

    /**
     * @see java.util.AbstractSet#hashCode()
     */
    public int hashCode()
    {
        return (int) this.getID();
    }

}
