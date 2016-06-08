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

configure_file(${CMAKE_CURRENT_SOURCE_DIR}/config-internal.h.in
               ${CMAKE_CURRENT_BINARY_DIR}/config-internal.h @ONLY)
configure_file(${CMAKE_CURRENT_SOURCE_DIR}/version.h.in
               ${CMAKE_CURRENT_BINARY_DIR}/version.h @ONLY)

xsd_fu(omexml_model_all model_headers OME_XML_GENERATED_MODEL_SOURCES)
xsd_fu(omexml_metadata_all OME_XML_GENERATED_META_HEADERS OME_XML_GENERATED_META_SOURCES)

# Set ${OME_XML_GENERATED_MODEL_HEADERS}, ${OME_XML_GENERATED_ENUMS_HEADERS}
# and ${OME_XML_GENERATED_ENUMINCLUDE_HEADERS}
foreach(hdr ${model_headers})
  get_filename_component(hdr_name ${hdr} NAME)
  get_filename_component(hdr_dir ${hdr} PATH)
  file(RELATIVE_PATH hdr_dir_rel "${GEN_DIR}/ome/xml" ${hdr_dir})
  string(TOUPPER "${hdr_dir_rel}" hdr_dir_rel)
  string(REPLACE "/" "_" hdr_dir_rel "${hdr_dir_rel}")
  list(APPEND "OME_XML_GENERATED_${hdr_dir_rel}_HEADERS" "${hdr}")
endforeach(hdr)

set(OME_XML_STATIC_SOURCES
  Document.cpp
  OMEEntityResolver.cpp
  OMETransformResolver.cpp
  meta/MetadataException.cpp
  meta/OMEXMLMetadataRoot.cpp
  meta/Convert.cpp
  model/ModelException.cpp
  model/OriginalMetadataAnnotation.cpp
  model/detail/OMEModel.cpp
  model/detail/OMEModelObject.cpp
  model/detail/Parse.cpp
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
  module.cpp
  version.cpp)

set(OME_XML_META_STATIC_HEADERS
    meta/BaseMetadata.h
    meta/Convert.h
    meta/Metadata.h
    meta/MetadataException.h
    meta/MetadataRoot.h
    meta/OMEXMLMetadataRoot.h)

set(OME_XML_STATIC_HEADERS
    Document.h
    module.h
    OMEEntityResolver.h
    OMETransform.h
    OMETransformResolver.h)

set(OME_XML_GENERATED_HEADERS
    ${CMAKE_CURRENT_BINARY_DIR}/version.h)

set(OME_XML_STATIC_MODEL_HEADERS
    model/ModelException.h
    model/OMEModel.h
    model/OMEModelObject.h
    model/OriginalMetadataAnnotation.h)

set(OME_XML_STATIC_MODEL_DETAIL_HEADERS
    model/detail/OMEModel.h
    model/detail/OMEModelObject.h
    model/detail/Parse.h)

set(OME_XML_STATIC_ENUMS_HEADERS
    model/enums/EnumerationException.h)

set(OME_XML_STATIC_PRIMITIVES_HEADERS
    model/primitives/Color.h
    model/primitives/ConstrainedNumeric.h
    model/primitives/NonNegativeFloat.h
    model/primitives/NonNegativeInteger.h
    model/primitives/NonNegativeLong.h
    model/primitives/OrderedMultimap.h
    model/primitives/PercentFraction.h
    model/primitives/PositiveFloat.h
    model/primitives/PositiveInteger.h
    model/primitives/PositiveLong.h
    model/primitives/NumericConstraints.h
    model/primitives/Timestamp.h)

set(OME_XML_PUBLIC_STATIC_HEADERS
    ${OME_XML_STATIC_MODEL_HEADERS}
    ${OME_XML_STATIC_MODEL_DETAIL_HEADERS}
    ${OME_XML_STATIC_PRIMITIVES_HEADERS}
    ${OME_XML_STATIC_ENUMS_HEADERS})

set(OME_XML_PUBLIC_GENERATED_HEADERS
    ${OME_XML_GENERATED_HEADERS}
    ${OME_XML_GENERATED_META_HEADERS}
    ${OME_XML_GENERATED_MODEL_HEADERS}
    ${OME_XML_GENERATED_MODEL_ENUMS_HEADERS})

set(OME_XML_PRIVATE_GENERATED_HEADERS
    ${CMAKE_CURRENT_BINARY_DIR}/config-internal.h)

set(OME_XML_HEADERS
    ${OME_XML_PRIVATE_STATIC_HEADERS}
    ${OME_XML_PUBLIC_STATIC_HEADERS}
    ${OME_XML_PUBLIC_GENERATED_HEADERS}
    ${OME_XML_PRIVATE_GENERATED_HEADERS})

set(OME_XML_SOURCES
    ${OME_XML_STATIC_SOURCES}
    ${OME_XML_GENERATED_META_SOURCES}
    ${OME_XML_GENERATED_MODEL_SOURCES})

add_library(ome-xml
            ${OME_XML_SOURCES}
            ${OME_XML_HEADERS})

target_include_directories(ome-xml PUBLIC
                           $<BUILD_INTERFACE:${PROJECT_SOURCE_DIR}/cpp/lib>
                           $<BUILD_INTERFACE:${PROJECT_BINARY_DIR}/cpp/lib>)

target_link_libraries(ome-xml OME::Compat OME::Common)
target_link_libraries(ome-xml Boost::date_time)
set_target_properties(ome-xml PROPERTIES VERSION ${OME_VERSION_SHORT})

add_library(OME::XML ALIAS ome-xml)

install(TARGETS ome-xml
        EXPORT OMEXMLInternal
        RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR}
        LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
        ARCHIVE DESTINATION ${CMAKE_INSTALL_LIBDIR})
install(EXPORT OMEXMLInternal
        DESTINATION ${CMAKE_INSTALL_LIBDIR}/cmake/OMEXML)
install(FILES OMEXMLConfig.cmake
        DESTINATION ${CMAKE_INSTALL_LIBDIR}/cmake/OMEXML)

set(ome_xml_includedir "${CMAKE_INSTALL_INCLUDEDIR}/ome/xml")

install(FILES ${OME_XML_STATIC_HEADERS} ${OME_XML_GENERATED_HEADERS}
        DESTINATION ${ome_xml_includedir})
install(FILES ${OME_XML_META_STATIC_HEADERS} ${OME_XML_GENERATED_META_HEADERS}
        DESTINATION ${ome_xml_includedir}/meta)
install(FILES ${OME_XML_STATIC_MODEL_HEADERS} ${OME_XML_GENERATED_MODEL_HEADERS}
        DESTINATION ${ome_xml_includedir}/model)
install(FILES ${OME_XML_STATIC_MODEL_DETAIL_HEADERS}
        DESTINATION ${ome_xml_includedir}/model/detail)
install(FILES ${OME_XML_STATIC_ENUMS_HEADERS} ${OME_XML_GENERATED_MODEL_ENUMS_HEADERS}
        DESTINATION ${ome_xml_includedir}/model/enums)
install(FILES ${OME_XML_STATIC_PRIMITIVES_HEADERS} ${OME_XML_GENERATED_PRIMITIVES_HEADERS}
        DESTINATION ${ome_xml_includedir}/model/primitives)

# Dump header list for testing
header_include_list_write(OME_XML_PUBLIC_STATIC_HEADERS
                          OME_XML_PUBLIC_GENERATED_HEADERS
                          ome/xml ${PROJECT_BINARY_DIR}/cpp/test/ome-xml)

# Add target to generate all xsd-fu sources
add_custom_target(gensrc DEPENDS
  ${OME_XML_GENERATED_META_HEADERS}
  ${OME_XML_GENERATED_META_SOURCES}
  ${OME_XML_GENERATED_MODEL_HEADERS}
  ${OME_XML_GENERATED_MODEL_SOURCES}
  ${OME_XML_GENERATED_MODEL_ENUMS_HEADERS})

# Find and install all schemas
file(GLOB OME_SCHEMAS RELATIVE "${PROJECT_SOURCE_DIR}/components/specification/released-schema/"
     "${PROJECT_SOURCE_DIR}/components/specification/released-schema/catalog.xml"
     "${PROJECT_SOURCE_DIR}/components/specification/released-schema/2*/*.xsd"
     "${PROJECT_SOURCE_DIR}/components/specification/released-schema/2*/catalog.xml"
     "${PROJECT_SOURCE_DIR}/components/specification/released-schema/external/*.xsd"
     "${PROJECT_SOURCE_DIR}/components/specification/released-schema/external/catalog.xml")
foreach(schema ${OME_SCHEMAS})
  get_filename_component(SCHEMA_RELEASE ${schema} PATH)
  install(FILES "${PROJECT_SOURCE_DIR}/components/specification/released-schema/${schema}"
          DESTINATION "${OME_XML_INSTALL_SCHEMADIR}/${SCHEMA_RELEASE}")
endforeach(schema)

# Find and install all transforms
file(GLOB OME_TRANSFORMS RELATIVE "${PROJECT_SOURCE_DIR}/components/specification/transforms/"
     "${PROJECT_SOURCE_DIR}/components/specification/transforms/*.xsl")
foreach(transform ${OME_TRANSFORMS})
  get_filename_component(TRANSFORM ${transform} PATH)
  install(FILES "${PROJECT_SOURCE_DIR}/components/specification/transforms/${transform}"
          DESTINATION "${OME_XML_INSTALL_TRANSFORMDIR}/${TRANSFORM}")
endforeach(transform)

# Find and install all samples
file(GLOB_RECURSE OME_SAMPLES RELATIVE "${PROJECT_SOURCE_DIR}/components/specification/samples/" "${PROJECT_SOURCE_DIR}/components/specification/samples/*")
foreach(sample ${OME_SAMPLES})
  get_filename_component(SAMPLE_FILE ${sample} PATH)
  install(FILES "${PROJECT_SOURCE_DIR}/components/specification/samples/${sample}"
          DESTINATION "${OME_XML_INSTALL_SAMPLEDIR}/${SAMPLE_FILE}")
endforeach(sample)
