/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package loci.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class contains useful methods for executing protected methods via reflection.
 * 
 * Use the invokeProtected method to find and execute the target method within a source 
 * object.
 * 
 * Use the unwrapException method to test any thrown exception for a match to a known
 * throwable.
 * 
 * @author Mark Hiner hinerm at gmail.com
 *
 */
public class ProtectedMethodInvoker {

  /**
   * This method checks whether the Throwable t is an instance of the provided 
   * exceptionType. This can be used to check an InvocationTargetException
   * returned by invokeProtected for known exception types that can be
   * re-thrown properly.
   * 
   * @param t
   * @param exceptionType
   * @throws T
   */
  public <T extends Throwable> void unwrapException(Throwable t, Class<T> exceptionType) throws T {
    final Throwable cause = t.getCause();
    if (exceptionType.isAssignableFrom(cause.getClass())) {
      @SuppressWarnings("unchecked")
      final T typedException = (T) cause;
      throw typedException;
    }
  }

  /**
   * This method attempts to invoke the provided methodName within the source object.
   * 
   * paramTypes is an array of the classes for the method's parameters, and args
   * is an array of the actual objects to be passed as parameters.
   * 
   * The return value is an object wrapping of the target method's return value.
   * 
   * @param source
   * @param methodName
   * @param paramTypes
   * @param args
   * @throws InvocationTargetException
   */
  public Object invokeProtected(Object source, String methodName, Class<?>[] paramTypes,
    Object[] args) throws InvocationTargetException
  {
    try {
      Method m = source.getClass().getDeclaredMethod(methodName, paramTypes);
      m.setAccessible(true);
      return m.invoke(source, args);
    }
    catch (NoSuchMethodException e) {
      throw new IllegalStateException(e);
    }
    catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }
}
