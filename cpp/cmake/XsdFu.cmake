# #%L
# Bio-Formats C++ libraries (cmake build infrastructure)
# %%
# Copyright © 2006 - 2016 Open Microscopy Environment:
#   - Massachusetts Institute of Technology
#   - National Institutes of Health
#   - University of Dundee
#   - Board of Regents of the University of Wisconsin-Madison
#   - Glencoe Software, Inc.
# %%
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# 1. Redistributions of source code must retain the above copyright notice,
#    this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright notice,
#    this list of conditions and the following disclaimer in the documentation
#    and/or other materials provided with the distribution.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.
#
# The views and conclusions contained in the software and documentation are
# those of the authors and should not be interpreted as representing official
# policies, either expressed or implied, of any organization.
# #L%

cmake_policy(SET CMP0007 NEW)

option(xsdfu-debug "Enable xsd-fu debug output (in generated code)" OFF)
if(xsdfu-debug)
  set(XSDFU_DEBUG --debug)
endif()

# The xsd-fu script
set(XSD_FU_SCRIPT ${PROJECT_SOURCE_DIR}/components/xsd-fu/xsd-fu)

# Command to invoke xsd-fu; python used explicitly to allow it to
# function on windows
set(XSD_FU python ${XSD_FU_SCRIPT})

# Version of the OME-XML model to use
set(MODEL_VERSION 2016-06)

# Path to the model within the source tree
set(MODEL_PATH ${PROJECT_SOURCE_DIR}/components/specification/released-schema/${MODEL_VERSION})

# Files to use within the model directory
set(MODEL_FILES
    ${MODEL_PATH}/ome.xsd)

# Output directory for source generation
set(GEN_DIR ${PROJECT_BINARY_DIR}/cpp/lib)

# Default arguments to use when running xsd-fu
set(XSD_FU_ARGS ${XSDFU_DEBUG} --language=C++ --output-directory=${GEN_DIR} ${MODEL_FILES})

# xsd_fu_single: Run xsd-fu for a single filetype
#
# xsd-fu will be run to determine the files which this action will
# generate and the file dependencies for this action.  A cmake custom
# command will then be created to generate these files from these
# dependencies.  The caller must provide a variable to store the
# output file list in, for use elsewhere by cmake.
#
# filetype: the type of file to generate (header or source)
# command: the xsd-fu command to invoke
# outvar: variable to store generated file list in
function(xsd_fu_single filetype command outvar)
  message(STATUS "Determining xsd-fu outputs for target ${command} (${filetype}s)")
  execute_process(COMMAND ${XSD_FU} ${command} --dry-run --file-type=${filetype} --print-generated ${XSD_FU_ARGS}
                  OUTPUT_VARIABLE genfiles
                  RESULT_VARIABLE genfiles_result)
  if (genfiles_result)
    message(FATAL_ERROR "xsd-fu: Failed to get output file list for target ${command} (${filetype}s): ${genfiles_result}")
  endif()

  string(REPLACE "\n" ";" genfiles "${genfiles}")
  if(WIN32)
    string(REPLACE "\\" "/" genfiles "${genfiles}")
  endif(WIN32)
  list(REMOVE_DUPLICATES genfiles)

  message(STATUS "Determining xsd-fu dependencies for target ${command} (${filetype}s)")
  execute_process(COMMAND ${XSD_FU} ${command} --dry-run --file-type=${filetype} --print-depends ${XSD_FU_ARGS}
                  OUTPUT_VARIABLE gendeps
                  RESULT_VARIABLE gendeps_result)
  if (gendeps_result)
    message(FATAL_ERROR "xsd-fu: Failed to get dependency file list for target ${command} (${filetype}s): ${gendeps_result}")
  endif()

  string(REPLACE "\n" ";" gendeps "${gendeps}")
  if(WIN32)
    string(REPLACE "\\" "/" gendeps "${gendeps}")
  endif(WIN32)
  list(REMOVE_DUPLICATES gendeps)

  add_custom_command(OUTPUT ${genfiles}
                     COMMAND ${XSD_FU} ${command} --quiet --file-type=${filetype} ${XSD_FU_ARGS}
                     DEPENDS ${gendeps} ${XSD_FU_SCRIPT})

  set(${outvar} ${genfiles} PARENT_SCOPE)
endfunction(xsd_fu_single)

# xsd_fu_header: Run xsd-fu for header generation
#
# Invokes xsd_fu_single in header generation mode.
#
# command: the xsd-fu command to invoke
# headers: variable to store generated header file list in
function(xsd_fu_header command headers)
  xsd_fu_single(header ${command} gen_headers)

  set(${headers} ${gen_headers} PARENT_SCOPE)
endfunction(xsd_fu_header)

# xsd_fu_source: Run xsd-fu for source generation
#
# Invokes xsd_fu_single in source generation mode.
#
# command: the xsd-fu command to invoke
# sources: variable to store generated source file list in
function(xsd_fu_source command sources)
  xsd_fu_single(source ${command} gen_sources)

  set(${sources} ${gen_sources} PARENT_SCOPE)
endfunction(xsd_fu_source)

# xsd_fu: Run xsd-fu for a source generation
#
# Invokes xsd_fu_header and xsd_fu_source to generate headers and
# sources.
#
# command: the xsd-fu command to invoke
# headers: variable to store generated header file list in
# sources: variable to store generated source file list in
function(xsd_fu command headers sources)
  xsd_fu_header(${command} gen_headers)
  xsd_fu_source(${command} gen_sources)

  set(${headers} ${gen_headers} PARENT_SCOPE)
  set(${sources} ${gen_sources} PARENT_SCOPE)
endfunction(xsd_fu)
