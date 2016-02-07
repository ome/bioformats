/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

package loci.formats;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import loci.common.Constants;

import org.reflections.Reflections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClassList is a list of classes for use with ImageReader or ImageWriter,
 * parsed from a configuration file such as readers.txt or writers.txt.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ClassList<T> {

  // -- Constants --

  private static final Logger LOGGER = LoggerFactory.getLogger(ClassList.class);

  // -- Fields --

  /** Base class to which all classes are assignable. */
  private Class<T> base;

  /** List of classes. */
  private List<Class<? extends T>> classes;

  // -- Constructor --

  /**
   * Constructs a list of classes, initially empty.
   * @param base Base class to which all classes are assignable.
   */
  public ClassList(Class<T> base) {
    this.base = base;
    classes = new ArrayList<Class<? extends T>>();
  }

  /**
   * Constructs a list of classes from the given configuration file.
   * @param file Configuration file containing the list of classes.
   * @param base Base class to which all classes are assignable.
   * @throws IOException if the file cannot be read.
   */
  @Deprecated
  public ClassList(String file, Class<T> base) throws IOException {
    this(file, base, base.equals(IFormatReader.class) ? BioFormatsReader.class :
      base.equals(IFormatWriter.class) ? BioFormatsWriter.class : null);
  }

  public ClassList(Class<T> base, Class<?> location) throws IOException {
    this("loci.formats", base, location);
  }

  /**
   * Constructs a list of classes from the given configuration file.
   * @param file Configuration file containing the list of classes.
   * @param base Base class to which all classes are assignable.
   * @param location Class indicating which package to search for the file.
   *  If null, 'file' is interpreted as an absolute path name.
   * @throws IOException if the file cannot be read.
   */
  public ClassList(String file, Class<T> base, Class<?> location)
    throws IOException
  {
    this.base = base;
    classes = new ArrayList<Class<? extends T>>();

    Reflections reflections = new Reflections(file);
    Set<Class<?>> readerClasses = reflections.getTypesAnnotatedWith((Class) location);
    for (Class c : readerClasses) {
      Class<? extends T> toAdd = cast(c);

      // make sure the class wasn't disabled before adding
      Annotation[] annotations = c.getAnnotations();
      for (Annotation a : annotations) {
        FormatType type = null;
        if (a instanceof BioFormatsReader) {
          type = ((BioFormatsReader) a).value();
        }
        else if (a instanceof BioFormatsWriter) {
          type = ((BioFormatsWriter) a).value();
        }
        if (type != null) {
          if (type == FormatType.DISABLED) {
            toAdd = null;
          }
          break;
        }
      }
      if (toAdd != null) {
        classes.add(toAdd);
      }
    }
  }

  // -- ClassList API methods --

  /**
   * Adds the given class, which must be assignable
   * to the base class, to the list.
   */
  public void addClass(Class<? extends T> c) {
    classes.add(c);
  }

  /** Removes the given class from the list. */
  public void removeClass(Class<? extends T> c) {
    classes.remove(c);
  }

  /** Gets the list of classes as an array. */
  @SuppressWarnings("unchecked")
  public Class<? extends T>[] getClasses() {
    Class<? extends T>[] sortedClasses = classes.toArray(new Class[0]);
    Arrays.sort(sortedClasses, new Comparator<Class>() {
      public int compare(Class o1, Class o2) {
        Annotation[] a1 = o1.getAnnotations();
        Annotation[] a2 = o2.getAnnotations();

        if (a1.length != a2.length) {
          return a1.length - a2.length;
        }

        for (int i=0; i<a1.length; i++) {
          FormatType type1 = null, type2 = null;
          if (a1[i] instanceof BioFormatsReader) {
            type1 = ((BioFormatsReader) a1[i]).value();
            type2 = ((BioFormatsReader) a2[i]).value();
          }
          else if (a1[i] instanceof BioFormatsWriter) {
            type1 = ((BioFormatsWriter) a1[i]).value();
            type2 = ((BioFormatsWriter) a2[i]).value();
          }
          if (type1 != type2) {
            return type1.compareTo(type2);
          }
        }

        return o1.getName().compareTo(o2.getName());
      }
    });
    return sortedClasses;
  }

  // -- Helper methods --

  /**
   * Cast the given class to something that extends the base class.
   * @param rawClass the class to be cast
   */
  @SuppressWarnings("unchecked")
  private Class<? extends T> cast(Class<?> rawClass) {
    if (!base.isAssignableFrom(rawClass)) return null;
    return (Class<? extends T>) rawClass;
  }

}
