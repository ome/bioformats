/*
 * #%L
 * Common package for I/O and related utilities
 * %%
 * Copyright (C)  2016 Open Microscopy Environment:
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

package loci.common.utests;

import loci.common.IniParser;
import loci.common.IniList;
import loci.common.IniTable;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link loci.common.IniParser}.
 */
public class IniParserTest {
  private IniParser parser = new IniParser();
  private IniList list;
  private IniTable table;
  final Charset utf8charset = Charset.forName("UTF-8");

  public BufferedReader stringToBufferedReader(String s) {
    InputStream stream = new ByteArrayInputStream(s.getBytes(utf8charset));
    return new BufferedReader(new InputStreamReader(stream));
  }

  @DataProvider(name = "simpleini")
  public Object[][] createSimpleIni() {
    return new Object[][] {
      {"key=value", IniTable.DEFAULT_HEADER},
      {"key = value", IniTable.DEFAULT_HEADER},
      {"\nkey=value\n", IniTable.DEFAULT_HEADER},
      {"key=value#comment", IniTable.DEFAULT_HEADER},
      {"key=value # comment", IniTable.DEFAULT_HEADER},
      {"key=value\nignored line", IniTable.DEFAULT_HEADER},
      {"[header]\nkey=value", "header"},
      {"\n[header]\n\nkey=value\n", "header"},
      {"[header]\nkey=value#comment", "header"},
      {"[header]\nkey=value # comment", "header"},
      {"[header]\nkey=value\nignored line", "header"},
      {"{chapter}\n[header]\nkey=value", "chapter: header"},
    };
  }
  
  @Test(dataProvider="simpleini")
  public void testSimpleINI(String s, String header) throws IOException {
    BufferedReader reader = stringToBufferedReader(s);
    
    list = parser.parseINI(reader);
    assertEquals(list.size(), 1);
    table = list.getTable(header);
    assertFalse(table == null);
    assertEquals(table.keySet().size(), 2);
    assertEquals(table.get("key"), "value");
  }

  @Test
  public void testMultiValueINI() throws IOException {
    String s = "{chapter}\n[header]\n" +
      "key1=value1 # comment\n\n" +
      "key2=line1\\\nline2\n" +
      "ignored line\n" +
      "key3 = value3";
    BufferedReader reader = stringToBufferedReader(s);

    list = parser.parseINI(reader);
    assertEquals(list.size(), 1);
    table = list.getTable("chapter: header");
    assertFalse(table == null);
    assertEquals(table.keySet().size(), 4);
    assertEquals(table.get("key1"), "value1");
    assertEquals(table.get("key2"), "line1 line2");
    assertEquals(table.get("key3"), "value3");
  }

  @Test
  public void testMultiHeaderINI() throws IOException {
    String s = "key0=value0\n" +
      "[header1]\nkey1=value1\n" +
      "{chapter}\n[header2]\nkey2=value2\n" +
      "[header3]\nkey3=value3\n";
    BufferedReader reader = stringToBufferedReader(s);

    list = parser.parseINI(reader);
    assertEquals(list.size(), 4);
    table = list.getTable(IniTable.DEFAULT_HEADER);
    assertEquals(table.keySet().size(), 2);
    assertEquals(table.get("key0"), "value0");
    table = list.getTable("header1");
    assertEquals(table.keySet().size(), 2);
    assertEquals(table.get("key1"), "value1");
    table = list.getTable("chapter: header2");
    assertEquals(table.keySet().size(), 2);
    assertEquals(table.get("key2"), "value2");
    table = list.getTable("chapter: header3");
    assertEquals(table.keySet().size(), 2);
    assertEquals(table.get("key3"), "value3");
  }
}
