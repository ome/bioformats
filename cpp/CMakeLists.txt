cmake_minimum_required(VERSION 2.8)

set(GEN_DIR gen)

set(XSD_FU python ../components/xsd-fu/xsd-fu)
set(MODEL_VERSION 2013-06)
set(MODEL_PATH ../components/specification/released-schema/${MODEL_VERSION})
set(MODEL_FILES
  ${MODEL_PATH}/ome.xsd
  ${MODEL_PATH}/BinaryFile.xsd
  ${MODEL_PATH}/ROI.xsd
  ${MODEL_PATH}/SA.xsd
  ${MODEL_PATH}/SPW.xsd)
set(XSD_FU_ARGS -p ome.xml.model -l C++ -o ${GEN_DIR} ${MODEL_FILES})

execute_process(COMMAND ${XSD_FU} java_classes -f source ${XSD_FU_ARGS}
  OUTPUT_VARIABLE MODEL_GENERATED_MODEL_SOURCES)
string(REPLACE "\n" ";" MODEL_GENERATED_MODEL_SOURCES "${MODEL_GENERATED_MODEL_SOURCES}")
execute_process(COMMAND ${CMAKE_COMMAND} -E echo Generated ${MODEL_GENERATED_MODEL_SOURCES})

execute_process(COMMAND ${XSD_FU} java_classes -f header ${XSD_FU_ARGS}
  OUTPUT_VARIABLE MODEL_GENERATED_MODEL_HEADERS)
string(REPLACE "\n" ";" MODEL_GENERATED_MODEL_HEADERS "${MODEL_GENERATED_MODEL_HEADERS}")
execute_process(COMMAND ${CMAKE_COMMAND} -E echo Generated ${MODEL_GENERATED_MODEL_HEADERS})

execute_process(COMMAND ${XSD_FU} enum_types -f source ${XSD_FU_ARGS}
  OUTPUT_VARIABLE MODEL_GENERATED_ENUM_SOURCES)
string(REPLACE "\n" ";" MODEL_GENERATED_ENUM_SOURCES "${MODEL_GENERATED_ENUM_SOURCES}")
execute_process(COMMAND ${CMAKE_COMMAND} -E echo Generated ${MODEL_GENERATED_ENUM_SOURCES})

execute_process(COMMAND ${XSD_FU} enum_types -f header ${XSD_FU_ARGS}
  OUTPUT_VARIABLE MODEL_GENERATED_ENUM_HEADERS)
string(REPLACE "\n" ";" MODEL_GENERATED_ENUM_HEADERS "${MODEL_GENERATED_ENUM_HEADERS}")
execute_process(COMMAND ${CMAKE_COMMAND} -E echo Generated ${MODEL_GENERATED_ENUM_HEADERS})

execute_process(COMMAND ${XSD_FU} enum_includeall -f header ${XSD_FU_ARGS}
  OUTPUT_VARIABLE MODEL_GENERATED_ENUMINCLUDE_HEADERS)
string(REPLACE "\n" ";" MODEL_GENERATED_ENUMINCLUDE_HEADERS ${MODEL_GENERATED_ENUMINCLUDE_HEADERS})
execute_process(COMMAND ${CMAKE_COMMAND} -E echo Generated ${MODEL_GENERATED_ENUMINCLUDE_HEADERS})

set(OME_XML_GENERATED_SOURCES
  ${MODEL_GENERATED_MODEL_HEADERS}
  ${MODEL_GENERATED_MODEL_SOURCES}
  ${MODEL_GENERATED_ENUM_HEADERS}
  ${MODEL_GENERATED_ENUM_SOURCES}
  ${MODEL_GENERATED_ENUMINCLUDE_HEADERS})

set(OME_XML_STATIC_SOURCES
  src/ome/xerces/error_reporter.cpp
  src/ome/xml/model/enums/Enumeration.cpp
  src/ome/xml/model/enums/EnumerationException.cpp
  src/ome/xml/model/primitives/Color.cpp
  src/ome/xml/model/primitives/NonNegativeFloat.cpp
  src/ome/xml/model/primitives/NonNegativeInteger.cpp
  src/ome/xml/model/primitives/NonNegativeLong.cpp
  src/ome/xml/model/primitives/PercentFraction.cpp
  src/ome/xml/model/primitives/PositiveFloat.cpp
  src/ome/xml/model/primitives/PositiveInteger.cpp
  src/ome/xml/model/primitives/PositiveLong.cpp
  src/ome/xml/model/primitives/NumericConstraints.h
  src/ome/xml/model/AbstractOMEModelObject.cpp
  src/ome/xml/model/OMEModelImpl.cpp)

set(OME_XML_STATIC_HEADERS
  src/ome/xerces/dom/document.h
  src/ome/xerces/dom/element.h
  src/ome/xerces/dom/node.h
  src/ome/xerces/dom/nodelist.h
  src/ome/xerces/error_reporter.h
  src/ome/xerces/platform.h
  src/ome/xerces/string.h
  src/ome/xml/model/enums/Enumeration.h
  src/ome/xml/model/enums/EnumerationException.h
  src/ome/xml/model/primitives/Color.h
  src/ome/xml/model/primitives/ConstrainedNumeric.h
  src/ome/xml/model/primitives/NonNegativeFloat.h
  src/ome/xml/model/primitives/NonNegativeInteger.h
  src/ome/xml/model/primitives/NonNegativeLong.h
  src/ome/xml/model/primitives/PercentFraction.h
  src/ome/xml/model/primitives/PositiveFloat.h
  src/ome/xml/model/primitives/PositiveInteger.h
  src/ome/xml/model/primitives/PositiveLong.h
  src/ome/xml/model/primitives/NumericConstraints.h
  src/ome/xml/model/primitives/Timestamp.h
  src/ome/xml/model/AbstractOMEModelObject.h
  src/ome/xml/model/OMEModel.h
  src/ome/xml/model/OMEModelImpl.h
  src/ome/xml/model/OMEModelObject.h)

include_directories(src gen)
set(CMAKE_CXX_FLAGS "-std=c++11")

add_library(ome-xml SHARED ${OME_XML_GENERATED_SOURCES} ${OME_XML_STATIC_SOURCES})
target_link_libraries(ome-xml xerces-c)

add_executable(test/color test/color.cpp)
target_link_libraries(test/color ome-xml)

add_executable(test/enum test/enum.cpp)
target_link_libraries(test/enum ome-xml)

add_executable(test/positive-float test/positive-float.cpp)
target_link_libraries(test/positive-float ome-xml)

add_executable(test/positive-integer test/positive-integer.cpp)
target_link_libraries(test/positive-integer ome-xml)

add_executable(test/xerces test/xerces.cpp)
target_link_libraries(test/xerces ome-xml)

set(test_headers_SOURCES "")
file(MAKE_DIRECTORY gen/test/headers)
foreach(header ${MODEL_GENERATED_MODEL_HEADERS} ${MODEL_GENERATED_ENUM_HEADERS} ${OME_XML_STATIC_HEADERS})
  string(REPLACE "/" "_" genheader ${header})
  string(REGEX REPLACE "\\.h$" ".cpp" genheader ${genheader})
  string(REGEX REPLACE "^(gen|src)/" "" include ${header})
  string(CONFIGURE "#include <@include@>" src)
  file(WRITE "gen/test/headers/${genheader}" "${src}")
  list(APPEND test_headers_SOURCES "gen/test/headers/${genheader}")
#  set(test_headers_SOURCES ${test_headers_SOURCES} PARENT_SCOPE )
endforeach(header)
add_executable(test/headers test/headers.cpp ${test_headers_SOURCES})
target_link_libraries(test/headers ome-xml)
