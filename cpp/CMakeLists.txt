cmake_minimum_required(VERSION 2.8)

set(GEN_DIR gen)

set(XSD_FU python ${PROJECT_SOURCE_DIR}/../components/xsd-fu/xsd-fu)
set(MODEL_VERSION 2013-06)
set(MODEL_PATH ${PROJECT_SOURCE_DIR}/../components/specification/released-schema/${MODEL_VERSION})
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
if(WIN32)
  string(REPLACE "\\" "/" MODEL_GENERATED_MODEL_SOURCES "${MODEL_GENERATED_MODEL_SOURCES}")
endif(WIN32)
execute_process(COMMAND ${CMAKE_COMMAND} -E echo Generated ${MODEL_GENERATED_MODEL_SOURCES})

execute_process(COMMAND ${XSD_FU} java_classes -f header ${XSD_FU_ARGS}
  OUTPUT_VARIABLE MODEL_GENERATED_MODEL_HEADERS)
string(REPLACE "\n" ";" MODEL_GENERATED_MODEL_HEADERS "${MODEL_GENERATED_MODEL_HEADERS}")
if(WIN32)
  string(REPLACE "\\" "/" MODEL_GENERATED_MODEL_HEADERS "${MODEL_GENERATED_MODEL_HEADERS}")
endif(WIN32)
execute_process(COMMAND ${CMAKE_COMMAND} -E echo Generated ${MODEL_GENERATED_MODEL_HEADERS})

execute_process(COMMAND ${XSD_FU} enum_types -f source ${XSD_FU_ARGS}
  OUTPUT_VARIABLE MODEL_GENERATED_ENUM_SOURCES)
string(REPLACE "\n" ";" MODEL_GENERATED_ENUM_SOURCES "${MODEL_GENERATED_ENUM_SOURCES}")
if(WIN32)
  string(REPLACE "\\" "/" MODEL_GENERATED_ENUM_SOURCES "${MODEL_GENERATED_ENUM_SOURCES}")
endif(WIN32)
execute_process(COMMAND ${CMAKE_COMMAND} -E echo Generated ${MODEL_GENERATED_ENUM_SOURCES})

execute_process(COMMAND ${XSD_FU} enum_types -f header ${XSD_FU_ARGS}
  OUTPUT_VARIABLE MODEL_GENERATED_ENUM_HEADERS)
string(REPLACE "\n" ";" MODEL_GENERATED_ENUM_HEADERS "${MODEL_GENERATED_ENUM_HEADERS}")
if(WIN32)
  string(REPLACE "\\" "/" MODEL_GENERATED_ENUM_HEADERS "${MODEL_GENERATED_ENUM_HEADERS}")
endif(WIN32)
execute_process(COMMAND ${CMAKE_COMMAND} -E echo Generated ${MODEL_GENERATED_ENUM_HEADERS})

execute_process(COMMAND ${XSD_FU} enum_includeall -f header ${XSD_FU_ARGS}
  OUTPUT_VARIABLE MODEL_GENERATED_ENUMINCLUDE_HEADERS)
string(REPLACE "\n" ";" MODEL_GENERATED_ENUMINCLUDE_HEADERS ${MODEL_GENERATED_ENUMINCLUDE_HEADERS})
if(WIN32)
  string(REPLACE "\\" "/" MODEL_GENERATED_ENUMINCLUDE_HEADERS ${MODEL_GENERATED_ENUMINCLUDE_HEADERS})
endif(WIN32)
execute_process(COMMAND ${CMAKE_COMMAND} -E echo Generated ${MODEL_GENERATED_ENUMINCLUDE_HEADERS})

set(OME_XML_GENERATED_SOURCES
  ${MODEL_GENERATED_MODEL_HEADERS}
  ${MODEL_GENERATED_MODEL_SOURCES}
  ${MODEL_GENERATED_ENUM_HEADERS}
  ${MODEL_GENERATED_ENUM_SOURCES}
  ${MODEL_GENERATED_ENUMINCLUDE_HEADERS})

set(OME_XML_STATIC_SOURCES
  ${PROJECT_SOURCE_DIR}/src/ome/xerces/error_reporter.cpp
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/enums/Enumeration.cpp
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/enums/EnumerationException.cpp
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/primitives/Color.cpp
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/primitives/NonNegativeFloat.cpp
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/primitives/NonNegativeInteger.cpp
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/primitives/NonNegativeLong.cpp
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/primitives/PercentFraction.cpp
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/primitives/PositiveFloat.cpp
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/primitives/PositiveInteger.cpp
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/primitives/PositiveLong.cpp
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/primitives/NumericConstraints.h
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/AbstractOMEModelObject.cpp
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/OMEModelImpl.cpp)

set(OME_XML_STATIC_HEADERS
  ${PROJECT_SOURCE_DIR}/src/ome/xerces/dom/document.h
  ${PROJECT_SOURCE_DIR}/src/ome/xerces/dom/element.h
  ${PROJECT_SOURCE_DIR}/src/ome/xerces/dom/node.h
  ${PROJECT_SOURCE_DIR}/src/ome/xerces/dom/nodelist.h
  ${PROJECT_SOURCE_DIR}/src/ome/xerces/error_reporter.h
  ${PROJECT_SOURCE_DIR}/src/ome/xerces/platform.h
  ${PROJECT_SOURCE_DIR}/src/ome/xerces/string.h
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/enums/Enumeration.h
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/enums/EnumerationException.h
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/primitives/Color.h
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/primitives/ConstrainedNumeric.h
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/primitives/NonNegativeFloat.h
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/primitives/NonNegativeInteger.h
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/primitives/NonNegativeLong.h
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/primitives/PercentFraction.h
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/primitives/PositiveFloat.h
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/primitives/PositiveInteger.h
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/primitives/PositiveLong.h
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/primitives/NumericConstraints.h
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/primitives/Timestamp.h
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/AbstractOMEModelObject.h
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/OMEModel.h
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/OMEModelImpl.h
  ${PROJECT_SOURCE_DIR}/src/ome/xml/model/OMEModelObject.h)

include_directories(src gen)
set(CMAKE_CXX_FLAGS "-std=c++11")

add_library(ome-xml SHARED ${OME_XML_GENERATED_SOURCES} ${OME_XML_STATIC_SOURCES})
target_link_libraries(ome-xml xerces-c)

file(MAKE_DIRECTORY ${PROJECT_BINARY_DIR}/test)

add_executable(test/color ${PROJECT_SOURCE_DIR}/test/color.cpp)
target_link_libraries(test/color ome-xml)

add_executable(test/enum test/enum.cpp)
target_link_libraries(test/enum ome-xml)

add_executable(test/positive-float ${PROJECT_SOURCE_DIR}/test/positive-float.cpp)
target_link_libraries(test/positive-float ome-xml)

add_executable(test/positive-integer ${PROJECT_SOURCE_DIR}/test/positive-integer.cpp)
target_link_libraries(test/positive-integer ome-xml)

add_executable(test/xerces ${PROJECT_SOURCE_DIR}/test/xerces.cpp)
target_link_libraries(test/xerces ome-xml)

set(test_headers_SOURCES "")
file(MAKE_DIRECTORY ${PROJECT_BINARY_DIR}/gen/test/headers)
foreach(header ${MODEL_GENERATED_MODEL_HEADERS} ${MODEL_GENERATED_ENUM_HEADERS} ${OME_XML_STATIC_HEADERS})
  string(REPLACE "/" "_" genheader ${header})
  string(REGEX REPLACE "\\.h$" ".cpp" genheader ${genheader})
  string(REGEX REPLACE "^(gen|src)/" "" include ${header})
  string(CONFIGURE "#include <@include@>" src)
  file(WRITE "${PROJECT_BINARY_DIR}/gen/test/headers/${genheader}" "${src}")
  list(APPEND test_headers_SOURCES "${PROJECT_BINARY_DIR}/gen/test/headers/${genheader}")
#  set(test_headers_SOURCES ${test_headers_SOURCES} PARENT_SCOPE )
endforeach(header)
add_executable(test/headers ${PROJECT_SOURCE_DIR}/test/headers.cpp ${test_headers_SOURCES})
target_link_libraries(test/headers ome-xml)

enable_testing()
add_test(enum test/enum)
add_test(color test/color)
add_test(headers test/headers)
add_test(positive-float test/positive-float)
add_test(positive-integer test/positive-integer)
add_test(xerces test/xerces "${PROJECT_SOURCE_DIR}/../components/specification/samples/2012-06/18x24y5z5t2c8b-text.ome")
