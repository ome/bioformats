/*
 * #%L
 * Top-level reader and writer APIs
 * %%
 * Copyright (C) 2016 Open Microscopy Environment:
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

package loci.formats.utests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.IOException;
import java.lang.Iterable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import loci.formats.ClassList;

import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.Test;


/**
 * Unit tests for {@link loci.formats.ClassList}.
 */
public class ClassListTest {

  private ClassList<Iterable> c;

  public void writeConfigFile(File file, String content) {
    try {
      BufferedWriter bw = new BufferedWriter(new FileWriter(file));
      bw.write(content);
      bw.close();
    } catch(IOException e){
      e.printStackTrace();
    }
  }
  
  @Test
  public void testDefaultConstructor() {
    c = new ClassList<Iterable>(Iterable.class);
    assertEquals(c.getClasses().length, 0);
  }

  @Test
  public void testNullConstructor() throws IOException {
    c = new ClassList<Iterable>(null, Iterable.class);
    assertEquals(c.getClasses().length, 0);
  }

  @Test
  public void testConfigFileConstructor1() throws IOException {
    File configFile = File.createTempFile("iterables", ".tmp");
    configFile.deleteOnExit();
    writeConfigFile(configFile, "java.util.ArrayList\njava.util.AbstractList");
    c = new ClassList<Iterable>(configFile.getAbsolutePath(), Iterable.class, null);
    assertEquals(c.getClasses().length, 2);
    assertEquals(c.getClasses()[0], ArrayList.class);
    assertEquals(c.getClasses()[1], AbstractList.class);
  }

  @Test
  public void testConfigFileConstructor2() throws IOException {
    c = new ClassList<Iterable>("iterables.txt", Iterable.class, ClassListTest.class);
    assertEquals(c.getClasses().length, 2);
    assertEquals(c.getClasses()[0], AbstractList.class);
    assertEquals(c.getClasses()[1], ArrayList.class);
  }

  @Test
  public void testInvalidFileConstructor1() throws IOException {
    c = new ClassList<Iterable>("invalid", Iterable.class, null);
    assertEquals(c.getClasses().length, 0);
  }

  @Test
  public void testInvalidFileConstructor2() throws IOException {
    c = new ClassList<Iterable>("invalid", Iterable.class, ClassListTest.class);
    assertEquals(c.getClasses().length, 0);
  }

  @Test
  public void testParseFile() throws IOException {
    c = new ClassList<Iterable>(null, Iterable.class);
    List<Class<? extends Iterable>> classes = c.parseFile(
      "iterables.txt",ClassListTest.class);
    assertEquals(classes.toArray(new Class[0]).length, 2);
  }

  @Test
  public void testAddClass() {
    c = new ClassList<Iterable>(Iterable.class);
    c.addClass(AbstractList.class);
    assertEquals(c.getClasses().length, 1);
    assertEquals(c.getClasses()[0], AbstractList.class);
    c.addClass(ArrayList.class);
    assertEquals(c.getClasses().length, 2);
    assertEquals(c.getClasses()[1], ArrayList.class);
    c.addClass(ArrayList.class);
    assertEquals(c.getClasses().length, 3);
    assertEquals(c.getClasses()[2], ArrayList.class);
  }

  @Test
  public void testRemoveClass() throws IOException {
    c = new ClassList<Iterable>("iterables.txt", Iterable.class, ClassListTest.class);
    c.removeClass(AbstractList.class);
    assertEquals(c.getClasses().length, 1);
    assertEquals(c.getClasses()[0], ArrayList.class);
    c.removeClass(ArrayList.class);
    assertEquals(c.getClasses().length, 0);
    c.removeClass(ArrayList.class);
    assertEquals(c.getClasses().length, 0);
  }

  @Test
  public void testAppend()  throws IOException{
      File configFile = File.createTempFile("iterables1", ".tmp");
      configFile.deleteOnExit();
      writeConfigFile(configFile, "java.util.ArrayList");
      c = new ClassList<Iterable>(configFile.getAbsolutePath(), Iterable.class, null);
      assertEquals(c.getClasses().length, 1);
      assertEquals(c.getClasses()[0], ArrayList.class);

      File configFile2 = File.createTempFile("iterables2", ".tmp");
      configFile2.deleteOnExit();
      writeConfigFile(configFile2, "java.util.AbstractList");
      c.append(c.parseFile(configFile2.getAbsolutePath(), null));
      assertEquals(c.getClasses().length, 2);
      assertEquals(c.getClasses()[0], ArrayList.class);
      assertEquals(c.getClasses()[1], AbstractList.class);
  }

  @Test
  public void testPrepend()  throws IOException{
      File configFile = File.createTempFile("iterables1", ".tmp");
      configFile.deleteOnExit();
      writeConfigFile(configFile, "java.util.ArrayList");
      c = new ClassList<Iterable>(configFile.getAbsolutePath(), Iterable.class, null);
      assertEquals(c.getClasses().length, 1);
      assertEquals(c.getClasses()[0], ArrayList.class);

      File configFile2 = File.createTempFile("iterables2", ".tmp");
      configFile2.deleteOnExit();
      writeConfigFile(configFile2, "java.util.AbstractList");
      c.prepend(c.parseFile(configFile2.getAbsolutePath(), null));
      assertEquals(c.getClasses().length, 2);
      assertEquals(c.getClasses()[0], AbstractList.class);
      assertEquals(c.getClasses()[1], ArrayList.class);
  }
}

