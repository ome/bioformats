include_directories(${OME_TOPLEVEL_INCLUDES})

execute_process(COMMAND ${XSD_FU} java_classes -d -f source ${XSD_FU_ARGS}
  OUTPUT_VARIABLE MODEL_GENERATED_MODEL_SOURCES)
string(REPLACE "\n" ";" MODEL_GENERATED_MODEL_SOURCES "${MODEL_GENERATED_MODEL_SOURCES}")
if(WIN32)
  string(REPLACE "\\" "/" MODEL_GENERATED_MODEL_SOURCES "${MODEL_GENERATED_MODEL_SOURCES}")
endif(WIN32)
add_custom_command(OUTPUT ${MODEL_GENERATED_MODEL_SOURCES}
                   COMMAND ${XSD_FU} java_classes -q -f source ${XSD_FU_ARGS}
                   DEPENDS ${MODEL_FILES} ${XSD_FU_SCRIPT})
#execute_process(COMMAND ${CMAKE_COMMAND} -E echo Generated ${MODEL_GENERATED_MODEL_SOURCES})

execute_process(COMMAND ${XSD_FU} java_classes -d -f header ${XSD_FU_ARGS}
  OUTPUT_VARIABLE MODEL_GENERATED_MODEL_HEADERS)
string(REPLACE "\n" ";" MODEL_GENERATED_MODEL_HEADERS "${MODEL_GENERATED_MODEL_HEADERS}")
if(WIN32)
  string(REPLACE "\\" "/" MODEL_GENERATED_MODEL_HEADERS "${MODEL_GENERATED_MODEL_HEADERS}")
endif(WIN32)
add_custom_command(OUTPUT ${MODEL_GENERATED_MODEL_HEADERS}
                   COMMAND ${XSD_FU} java_classes -q -f header ${XSD_FU_ARGS}
                   DEPENDS ${MODEL_FILES} ${XSD_FU_SCRIPT})
#execute_process(COMMAND ${CMAKE_COMMAND} -E echo Generated ${MODEL_GENERATED_MODEL_HEADERS})

execute_process(COMMAND ${XSD_FU} enum_types -d -f source ${XSD_FU_ARGS}
  OUTPUT_VARIABLE MODEL_GENERATED_ENUM_SOURCES)
string(REPLACE "\n" ";" MODEL_GENERATED_ENUM_SOURCES "${MODEL_GENERATED_ENUM_SOURCES}")
if(WIN32)
  string(REPLACE "\\" "/" MODEL_GENERATED_ENUM_SOURCES "${MODEL_GENERATED_ENUM_SOURCES}")
endif(WIN32)
add_custom_command(OUTPUT ${MODEL_GENERATED_ENUM_SOURCES}
                   COMMAND ${XSD_FU} enum_types -q -f source ${XSD_FU_ARGS}
                   DEPENDS ${MODEL_FILES} ${XSD_FU_SCRIPT})
#execute_process(COMMAND ${CMAKE_COMMAND} -E echo Generated ${MODEL_GENERATED_ENUM_SOURCES})

execute_process(COMMAND ${XSD_FU} enum_types -d -f header ${XSD_FU_ARGS}
  OUTPUT_VARIABLE MODEL_GENERATED_ENUM_HEADERS)
string(REPLACE "\n" ";" MODEL_GENERATED_ENUM_HEADERS "${MODEL_GENERATED_ENUM_HEADERS}")
if(WIN32)
  string(REPLACE "\\" "/" MODEL_GENERATED_ENUM_HEADERS "${MODEL_GENERATED_ENUM_HEADERS}")
endif(WIN32)
add_custom_command(OUTPUT ${MODEL_GENERATED_ENUM_HEADERS}
                   COMMAND ${XSD_FU} enum_types -q -f header ${XSD_FU_ARGS}
                   DEPENDS ${MODEL_FILES} ${XSD_FU_SCRIPT})
#execute_process(COMMAND ${CMAKE_COMMAND} -E echo Generated ${MODEL_GENERATED_ENUM_HEADERS})

execute_process(COMMAND ${XSD_FU} enum_includeall -d -f header ${XSD_FU_ARGS}
  OUTPUT_VARIABLE MODEL_GENERATED_ENUMINCLUDE_HEADERS)
string(REPLACE "\n" ";" MODEL_GENERATED_ENUMINCLUDE_HEADERS ${MODEL_GENERATED_ENUMINCLUDE_HEADERS})
if(WIN32)
  string(REPLACE "\\" "/" MODEL_GENERATED_ENUMINCLUDE_HEADERS ${MODEL_GENERATED_ENUMINCLUDE_HEADERS})
endif(WIN32)
#
add_custom_command(OUTPUT ${MODEL_GENERATED_ENUMINCLUDE_HEADERS}
                   COMMAND ${XSD_FU} enum_includeall -q -f header ${XSD_FU_ARGS}
                   DEPENDS ${MODEL_FILES} ${XSD_FU_SCRIPT})
#execute_process(COMMAND ${CMAKE_COMMAND} -E echo Generated ${MODEL_GENERATED_ENUMINCLUDE_HEADERS})

set(OME_XML_GENERATED_SOURCES
  ${MODEL_GENERATED_MODEL_HEADERS}
  ${MODEL_GENERATED_MODEL_SOURCES}
  ${MODEL_GENERATED_ENUM_HEADERS}
  ${MODEL_GENERATED_ENUM_SOURCES}
  ${MODEL_GENERATED_ENUMINCLUDE_HEADERS})

set(OME_XML_STATIC_SOURCES
  model/enums/Enumeration.cpp
  model/enums/EnumerationException.cpp
  model/primitives/Color.cpp
  model/primitives/NonNegativeFloat.cpp
  model/primitives/NonNegativeInteger.cpp
  model/primitives/NonNegativeLong.cpp
  model/primitives/PercentFraction.cpp
  model/primitives/PositiveFloat.cpp
  model/primitives/PositiveInteger.cpp
  model/primitives/PositiveLong.cpp
  model/primitives/NumericConstraints.h
  model/AbstractOMEModelObject.cpp
  model/OMEModelImpl.cpp)

set(OME_XML_STATIC_HEADERS
  model/enums/Enumeration.h
  model/enums/EnumerationException.h
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
  model/primitives/Timestamp.h
  model/AbstractOMEModelObject.h
  model/OMEModel.h
  model/OMEModelImpl.h
  model/OMEModelObject.h)

add_library(ome-xml SHARED ${OME_XML_GENERATED_SOURCES} ${OME_XML_STATIC_SOURCES})
target_link_libraries(ome-xml ome-xerces)
target_link_libraries(ome-xml ${Boost_DATE_TIME_LIBRARY_RELEASE})
