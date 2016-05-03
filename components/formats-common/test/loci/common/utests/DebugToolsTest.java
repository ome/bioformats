/*
 * #%L
 * Common package for I/O and related utilities
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

package loci.common.utests;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

import loci.common.DebugTools;

import org.slf4j.LoggerFactory;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link loci.common.DebugTools}.
 */
public class DebugToolsTest {

  Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
  boolean status;

  @DataProvider(name = "levels")
  public Object[][] createLevels() {
    return new Object[][] {
        {"INFO"}, {"WARN"}, {"ERROR"}, {"DEBUG"}, {"TRACE"}, {"ALL"}};
  }

  @BeforeClass
  public void testConfigurationFile() {
    // Logback should be initialized via a configuration file
    assertTrue(DebugTools.isEnabled());
    status = DebugTools.enableLogging();
    assertFalse(status);
    assertEquals(root.getLevel(), Level.toLevel("WARN"));
    status = DebugTools.enableLogging("INFO");
    assertFalse(status);
    assertEquals(root.getLevel(), Level.toLevel("WARN"));
  }

  @BeforeMethod
  public void setUp() {
    // Reset the logger context before each test method
    root.getLoggerContext().reset();
    assertFalse(DebugTools.isEnabled());
    assertEquals(root.getLevel(), Level.toLevel("DEBUG"));
  }

  // -- Tests --
  @Test
  public void testEnableLogging() {
    status = DebugTools.enableLogging();
    assertTrue(status);
    assertTrue(DebugTools.isEnabled());
    status = DebugTools.enableLogging();
    assertFalse(status);
    assertTrue(DebugTools.isEnabled());
  }

  @Test
  public void testEnableLoggingDefaultLevel() {
    status = DebugTools.enableLogging("INFO");
    assertTrue(status);
    assertTrue(DebugTools.isEnabled());
    assertEquals(root.getLevel(), Level.toLevel("INFO"));
    status = DebugTools.enableLogging("DEBUG");
    assertFalse(status);
    assertTrue(DebugTools.isEnabled());
    assertEquals(root.getLevel(), Level.toLevel("INFO"));
  }

  @Test(dataProvider = "levels")
  public void testEnableLoggingLevels(String level) {
    status = DebugTools.enableLogging(level);
    assertTrue(status);
    assertTrue(DebugTools.isEnabled());
    assertEquals(root.getLevel(), Level.toLevel(level));
    status = DebugTools.enableLogging();
    assertFalse(status);
    assertTrue(DebugTools.isEnabled());
  }

  @Test(dataProvider = "levels")
  public void testSetRootLevel(String level) {
    status = DebugTools.enableLogging();
    assertTrue(status);
    assertTrue(DebugTools.isEnabled());
    DebugTools.setRootLevel(level);
    assertEquals(root.getLevel(), Level.toLevel(level));
  }
}
