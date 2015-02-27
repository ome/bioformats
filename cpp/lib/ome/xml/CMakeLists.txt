# #%L
# Bio-Formats C++ libraries (cmake build infrastructure)
# %%
# Copyright © 2006 - 2015 Open Microscopy Environment:
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

include_directories(${OME_TOPLEVEL_INCLUDES})

execute_process(COMMAND ${XSD_FU} java_classes -d -f source ${XSD_FU_ARGS}
  OUTPUT_VARIABLE OME_XML_GENERATED_MODEL_SOURCES)
string(REPLACE "\n" ";" OME_XML_GENERATED_MODEL_SOURCES "${OME_XML_GENERATED_MODEL_SOURCES}")
if(WIN32)
  string(REPLACE "\\" "/" OME_XML_GENERATED_MODEL_SOURCES "${OME_XML_GENERATED_MODEL_SOURCES}")
endif(WIN32)
add_custom_command(OUTPUT ${OME_XML_GENERATED_MODEL_SOURCES}
                   COMMAND ${XSD_FU} java_classes -q -f source ${XSD_FU_ARGS}
                   DEPENDS ${MODEL_FILES} ${XSD_FU_SCRIPT})
#execute_process(COMMAND ${CMAKE_COMMAND} -E echo Generated ${OME_XML_GENERATED_MODEL_SOURCES})

execute_process(COMMAND ${XSD_FU} java_classes -d -f header ${XSD_FU_ARGS}
  OUTPUT_VARIABLE OME_XML_GENERATED_MODEL_HEADERS)
string(REPLACE "\n" ";" OME_XML_GENERATED_MODEL_HEADERS "${OME_XML_GENERATED_MODEL_HEADERS}")
if(WIN32)
  string(REPLACE "\\" "/" OME_XML_GENERATED_MODEL_HEADERS "${OME_XML_GENERATED_MODEL_HEADERS}")
endif(WIN32)
add_custom_command(OUTPUT ${OME_XML_GENERATED_MODEL_HEADERS}
                   COMMAND ${XSD_FU} java_classes -q -f header ${XSD_FU_ARGS}
                   DEPENDS ${MODEL_FILES} ${XSD_FU_SCRIPT})
#execute_process(COMMAND ${CMAKE_COMMAND} -E echo Generated ${OME_XML_GENERATED_MODEL_HEADERS})

execute_process(COMMAND ${XSD_FU} enum_types -d -f source ${XSD_FU_ARGS}
  OUTPUT_VARIABLE OME_XML_GENERATED_ENUMS_SOURCES)
string(REPLACE "\n" ";" OME_XML_GENERATED_ENUMS_SOURCES "${OME_XML_GENERATED_ENUMS_SOURCES}")
if(WIN32)
  string(REPLACE "\\" "/" OME_XML_GENERATED_ENUMS_SOURCES "${OME_XML_GENERATED_ENUMS_SOURCES}")
endif(WIN32)
add_custom_command(OUTPUT ${OME_XML_GENERATED_ENUMS_SOURCES}
                   COMMAND ${XSD_FU} enum_types -q -f source ${XSD_FU_ARGS}
                   DEPENDS ${MODEL_FILES} ${XSD_FU_SCRIPT})
#execute_process(COMMAND ${CMAKE_COMMAND} -E echo Generated ${OME_XML_GENERATED_ENUMS_SOURCES})

execute_process(COMMAND ${XSD_FU} enum_types -d -f header ${XSD_FU_ARGS}
  OUTPUT_VARIABLE OME_XML_GENERATED_ENUMS_HEADERS)
string(REPLACE "\n" ";" OME_XML_GENERATED_ENUMS_HEADERS "${OME_XML_GENERATED_ENUMS_HEADERS}")
if(WIN32)
  string(REPLACE "\\" "/" OME_XML_GENERATED_ENUMS_HEADERS "${OME_XML_GENERATED_ENUMS_HEADERS}")
endif(WIN32)
add_custom_command(OUTPUT ${OME_XML_GENERATED_ENUMS_HEADERS}
                   COMMAND ${XSD_FU} enum_types -q -f header ${XSD_FU_ARGS}
                   DEPENDS ${MODEL_FILES} ${XSD_FU_SCRIPT})
#execute_process(COMMAND ${CMAKE_COMMAND} -E echo Generated ${OME_XML_GENERATED_ENUMS_HEADERS})

execute_process(COMMAND ${XSD_FU} enum_includeall -d -f header ${XSD_FU_ARGS}
  OUTPUT_VARIABLE OME_XML_GENERATED_ENUMINCLUDE_HEADERS)
string(REPLACE "\n" ";" OME_XML_GENERATED_ENUMINCLUDE_HEADERS ${OME_XML_GENERATED_ENUMINCLUDE_HEADERS})
if(WIN32)
  string(REPLACE "\\" "/" OME_XML_GENERATED_ENUMINCLUDE_HEADERS ${OME_XML_GENERATED_ENUMINCLUDE_HEADERS})
endif(WIN32)
#
add_custom_command(OUTPUT ${OME_XML_GENERATED_ENUMINCLUDE_HEADERS}
                   COMMAND ${XSD_FU} enum_includeall -q -f header ${XSD_FU_ARGS}
                   DEPENDS ${MODEL_FILES} ${XSD_FU_SCRIPT})
#execute_process(COMMAND ${CMAKE_COMMAND} -E echo Generated ${OME_XML_GENERATED_ENUMINCLUDE_HEADERS})

set(OME_XML_STATIC_SOURCES
  model/enums/EnumerationException.cpp
  model/primitives/Color.cpp
  model/primitives/NonNegativeFloat.cpp
  model/primitives/NonNegativeInteger.cpp
  model/primitives/NonNegativeLong.cpp
  model/primitives/PercentFraction.cpp
  model/primitives/PositiveFloat.cpp
  model/primitives/PositiveInteger.cpp
  model/primitives/PositiveLong.cpp
  model/primitives/Timestamp.cpp
  model/OMEModel.cpp
  model/OMEModelImpl.cpp
  model/OMEModelObject.cpp)

set(OME_XML_STATIC_MODEL_HEADERS
    model/OMEModel.h
    model/OMEModelImpl.h
    model/OMEModelObject.h)

set(OME_XML_STATIC_ENUMS_HEADERS
    model/enums/EnumerationException.h)

set(OME_XML_STATIC_PRIMITIVES_HEADERS
    model/primitives/Color.h
    model/primitives/ConstrainedNumeric.h
    model/primitives/NonNegativeFloat.h
    model/primitives/NonNegativeInteger.h
    model/primitives/NonNegativeLong.h
    model/primitives/PercentFraction.h
    model/primitives/PositiveFloat.h
    model/primitives/PositiveInteger.h
    model/primitives/PositiveLong.h
    model/primitives/NumericConstraints.h
    model/primitives/Timestamp.h)

set(OME_XML_HEADERS
    ${OME_XML_STATIC_MODEL_HEADERS}
    ${OME_XML_STATIC_PRIMITIVES_HEADERS}
    ${OME_XML_STATIC_ENUMS_HEADERS}
    ${OME_XML_GENERATED_MODEL_HEADERS}
    ${OME_XML_GENERATED_ENUMS_HEADERS}
    ${OME_XML_GENERATED_ENUMINCLUDE_HEADERS})

set(OME_XML_SOURCES
    ${OME_XML_STATIC_SOURCES}
    ${OME_XML_GENERATED_MODEL_SOURCES}
    ${OME_XML_GENERATED_ENUMS_SOURCES})


add_library(ome-xml SHARED
            ${OME_XML_SOURCES}
            ${OME_XML_HEADERS})
target_link_libraries(ome-xml ome-xerces)
target_link_libraries(ome-xml ${Boost_DATE_TIME_LIBRARY_RELEASE})
set_target_properties(ome-xml PROPERTIES VERSION ${OME_VERSION_SHORT})

install(TARGETS ome-xml LIBRARY
        DESTINATION ${CMAKE_INSTALL_FULL_LIBDIR})

set(ome_xml_includedir "${CMAKE_INSTALL_FULL_INCLUDEDIR}/ome/xml")

install(FILES ${OME_XML_STATIC_MODEL_HEADERS} ${OME_XML_GENERATED_MODEL_HEADERS} ${OME_XML_GENERATED_ENUMINCLUDE_HEADERS}
        DESTINATION ${ome_xml_includedir}/model)
install(FILES ${OME_XML_STATIC_ENUMS_HEADERS} ${OME_XML_GENERATED_ENUMS_HEADERS}
        DESTINATION ${ome_xml_includedir}/model/enums)
install(FILES ${OME_XML_STATIC_PRIMITIVES_HEADERS} ${OME_XML_GENERATED_PRIMITIVES_HEADERS}
        DESTINATION ${ome_xml_includedir}/model/primitives)

set(LIBRARY_PREFIX OME_XML)
set(LIBRARY_NAME ome-xml)
set(LIBRARY_HEADER ome/xml/model/OME.h)
configure_file(${PROJECT_SOURCE_DIR}/cpp/cmake/TemplateConfig.cmake.in
               ${CMAKE_CURRENT_BINARY_DIR}/ome-xml-config.cmake)
install(FILES ${CMAKE_CURRENT_BINARY_DIR}/ome-xml-config.cmake
        DESTINATION ${CMAKE_INSTALL_FULL_LIBDIR}/cmake)
