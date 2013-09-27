include_directories(${OME_TOPLEVEL_INCLUDES})

add_executable(xerces xerces.cpp)
target_link_libraries(xerces ome-xml)

add_test(ome-xerces/xerces xerces "${PROJECT_SOURCE_DIR}/../components/specification/samples/2012-06/18x24y5z5t2c8b-text.ome")

headers_test(ome-xerces ome/xerces)
target_link_libraries(ome-xerces-headers ome-xerces)
add_test(ome-xerces.headers ome-xerces-headers)
