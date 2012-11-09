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

package loci.legacy.adapter;

/**
 * This interface represents adapter classes for delegating between equivalent "Legacy"
 * and "Modern" classes. Such delegation is intended to facilitate backwards
 * compatibility: given a subclass that extends or implements
 * a Legacy or Modern class, methods requiring the other can be executed
 * on instances of the subclass by using {@link LegacyAdapter#getLegacy(M)}
 *  or {@link LegacyAdapter#getModern(L) calls, as appropriate.
 * <p>
 * Delegation is done via mapping instances of legacy classes to instances
 * of modern classes.
 * </p>
 * <p>
 * This adapter is intended to be used to facilitate delegation between
 * interfaces, which can not themselves contain implementation.
 * </p>
 * <p>
 * NB: If a package contains extensions or implementations of a Legacy or
 * Modern class, and methods that operate on the extensions (instead of on the
 * base Legacy or Modern class) it can not be made backwards compatible,
 * even with an adapter, and thus we recommend conversion to a fully
 * Modern-based implementation.
 * </p>
 * @author Mark Hiner
 *
 * @param <L> Legacy class
 * @param <M> Modern class
 */
public interface LegacyAdapter <L, M> {

  /**
   * Used to retrieve the legacy instance associated with a given
   * modern instance.
   * <p>
   * This is the method that should typically be invoked to convert from
   * Modern to Legacy instances, as opposed to {@link LegacyAdapter#wrapToLegacy(M)} which will
   * naively always wrap.
   * </p>
   * 
   * @param modern - An instance of the modern class.
   * @return The associated legacy instance.
   */
  L getLegacy(M modern);
  
  /**
   * Used to retrieve the modern instance associated with a given
   * legacy instance.
   * <p>
   * This is the method that should typically be invoked to convert from
   * Legacy to Modern instances, as opposed to {@link LegacyAdapter#wrapToModern(L)} which will
   * naively always wrap.
   * </p>
   * 
   * @param legacy - An instance of the legacy class.
   * @return The associated modern instance.
   */
  M getModern(L legacy);


  /**
   * Resets any mappings in this adapter.
   */
  void clear();

}
