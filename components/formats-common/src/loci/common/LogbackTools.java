/*
 * #%L
 * Common package for I/O and related utilities
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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

package loci.common;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;


/**
 * A utility class with convenience methods for logback.
 */
public final class LogbackTools {

  // -- Constructor --

  private static final String CALLER = "Bio-Formats";

  private LogbackTools() { }

  /**
   * Checks whether logback has been enabled.
   *
   * This method will check if the root logger has been initialized via either
   * a configuration file or a previous call to {@link #enableLogging()}. The
   * logger context property will be used to discriminate the latter case from
   * other initializations.
   *
   * @return {@code true} if logging was successfully enabled
   */
  public static synchronized boolean isEnabled() {
    Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    LoggerContext loggerContext = root.getLoggerContext();
    return (ConfigurationWatchListUtil.getMainWatchURL(loggerContext) != null
            || (loggerContext.getProperty("caller") == CALLER));
  }

  /**
   * Sets the level of the root logger
   *
   * @param level A string indicating the desired level
   *   (i.e.: ALL, DEBUG, ERROR, FATAL, INFO, OFF, WARN).
   */
  public static synchronized void setRootLevel(String level) {
    Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    root.setLevel(Level.toLevel(level));
  }

  /**
   * Initializes logback without an external configuration file.
   *
   * The logging initialization also sets a logger context property to record
   * the initalization provenance.
   *
   * @return {@code true} if logging was successfully enabled
   */
  public static synchronized boolean enableLogging() {
    Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    LoggerContext context = root.getLoggerContext();
    if (!root.iteratorForAppenders().hasNext()) {
      context.reset();
      context.putProperty("caller", CALLER);
      PatternLayoutEncoder layout = new PatternLayoutEncoder();
      layout.setContext(context);
      layout.setPattern("%m%n");
      layout.start();

      ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<ILoggingEvent>();
      appender.setContext(context);
      appender.setEncoder(layout);
      appender.start();
      root.addAppender(appender);
    }
    else {
      Appender defaultAppender = root.iteratorForAppenders().next();
      if (defaultAppender instanceof ConsoleAppender) {
        context.reset();
        context.putProperty("caller", CALLER);

        PatternLayoutEncoder layout = new PatternLayoutEncoder();
        layout.setContext(context);
        layout.setPattern("%m%n");
        layout.start();

        defaultAppender.setContext(context);
        ((ConsoleAppender) defaultAppender).setEncoder(layout);
        defaultAppender.start();
        root.addAppender(defaultAppender);
      }
    }
    return true;
  }

  public static synchronized void enableIJLogging(boolean debug,
    Appender<ILoggingEvent> appender) {
    try {

      Object logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
      if (!(logger instanceof Logger)) return;

      Logger root = (Logger) logger;

      if (debug) {
        root.setLevel(Level.DEBUG);
      }
      appender.setContext(root.getLoggerContext());
      root.addAppender(appender);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
