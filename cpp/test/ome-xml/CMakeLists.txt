include_directories(${OME_TOPLEVEL_INCLUDES})

add_executable(color color.cpp ${TEST_MAIN})
target_link_libraries(color ome-xml)
target_link_libraries(color ${GTEST_LIBRARIES} ${CMAKE_THREAD_LIBS_INIT})

add_executable(enum enum.cpp ${TEST_MAIN})
target_link_libraries(enum ome-xml)
target_link_libraries(enum ${GTEST_LIBRARIES} ${CMAKE_THREAD_LIBS_INIT})

add_executable(constrained-numeric positive-integer.cpp positive-long.cpp positive-float.cpp non-negative-integer.cpp non-negative-long.cpp non-negative-float.cpp percent-fraction.cpp ${TEST_MAIN})
target_link_libraries(constrained-numeric ome-xml)
target_link_libraries(constrained-numeric ${GTEST_LIBRARIES} ${CMAKE_THREAD_LIBS_INIT})

add_executable(timestamp timestamp.cpp ${TEST_MAIN})
target_link_libraries(timestamp ome-xml)
target_link_libraries(timestamp ${GTEST_LIBRARIES} ${CMAKE_THREAD_LIBS_INIT})

headers_test(ome-xml ome/xml)
target_link_libraries(ome-xml-headers ome-xml)
add_test(ome-xml/headers ome-xml-headers)

add_test(ome-xml/enum enum)
add_test(ome-xml/color color)
add_test(ome-xml/constrained-numeric constrained-numeric)
#add_test(ome-xml/timestamp timestamp)
