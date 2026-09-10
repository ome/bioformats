/*
 * #%L
 * Top-level reader and writer APIs
 * %%
 * Copyright (C) 2023 Open Microscopy Environment:
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

/**
 * Enum of anatomical orientation terms defined in:
 * https://ngff.openmicroscopy.org/rfc/4/#permissible-values
 */
public enum AnatomicalOrientation implements OrientationTerm {
  LEFT_TO_RIGHT("left-to-right"),
  RIGHT_TO_LEFT("right-to-left"),
  ANTERIOR_TO_POSTERIOR("anterior-to-posterior"),
  POSTERIOR_TO_ANTERIOR("posterior-to-anterior"),
  INFERIOR_TO_SUPERIOR("inferior-to-superior"),
  SUPERIOR_TO_INFERIOR("superior-to-inferior"),
  DORSAL_TO_VENTRAL("dorsal-to-ventral"),
  VENTRAL_TO_DORSAL("ventral-to-dorsal"),
  DORSAL_TO_PALMAR("dorsal-to-palmar"),
  PALMAR_TO_DORSAL("palmar-to-dorsal"),
  DORSAL_TO_PLANTAR("dorsal-to-plantar"),
  PLANTAR_TO_DORSAL("plantar-to-dorsal"),
  ROSTRAL_TO_CAUDAL("rostral-to-caudal"),
  CAUDAL_TO_ROSTRAL("caudal-to-rostral"),
  CRANIAL_TO_CAUDAL("cranial-to-caudal"),
  CAUDAL_TO_CRANIAL("caudal-to-cranial"),
  PROXIMAL_TO_DISTAL("proximal-to-distal"),
  DISTAL_TO_PROXIMAL("distal-to-proximal"),
  SUPERFICIAL_TO_DEEP("superficial-to-deep"),
  DEEP_TO_SUPERFICIAL("deep-to-superficial"),
  APICAL_TO_BASAL("apical-to-basal"),
  BASAL_TO_APICAL("basal-to-apical"),
  APEX_TO_BASE("apex-to-base"),
  BASE_TO_APEX("base-to-apex");

  private String definedTerm;

  private AnatomicalOrientation(String term) {
    definedTerm = term;
  }

  @Override
  public String getDefinedTerm() {
    return definedTerm;
  }
}
