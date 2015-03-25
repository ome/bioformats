/*
 * #%L
 * Bio-Formats autogen package for programmatically generating source code.
 * %%
 * Copyright (C) 2007 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

/**
 * Useful methods for working with Apache Velocity.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class VelocityTools {

  public static VelocityEngine createEngine()
    // NB: No choice, as VelocityEngine.init(Properties) throws Exception
    throws Exception
  {
    // initialize Velocity engine; enable loading of templates as resources
    VelocityEngine ve = new VelocityEngine();
    Properties p = new Properties();
    p.setProperty("resource.loader", "class");
    p.setProperty("class.resource.loader.class",
      "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    ve.init(p);
    return ve;
  }

  public static VelocityContext createContext() {
    // populate Velocity context
    VelocityContext context = new VelocityContext();
    context.put("user", System.getProperty("user.name"));

    return context;
  }

  public static void processTemplate(VelocityEngine ve,
    VelocityContext context, String inFile, String outFile)
    // NB: No choice, as VelocityEngine.getTemplate(String) throws Exception
    throws Exception
  {
    System.out.print("Writing " + outFile + ": ");
    Template t = ve.getTemplate(inFile);
    StringWriter writer = new StringWriter();
    t.merge(context, writer);
    PrintWriter out = new PrintWriter(new File(outFile), "UTF-8");
    out.print(writer.toString());
    out.close();
    System.out.println("done.");
  }

}
