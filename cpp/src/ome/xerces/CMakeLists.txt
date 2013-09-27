include_directories(${OME_TOPLEVEL_INCLUDES})

set(ome_xerces_sources
    error_reporter.cpp)

set(ome_xerces_headers
    dom/document.h
    dom/element.h
    dom/node.h
    dom/nodelist.h
    error_reporter.h
    platform.h
    string.h)

add_library(ome-xerces SHARED ${ome_xerces_sources} ${ome_xerces_headers})
target_link_libraries(ome-xerces xerces-c)

set(ome_xerces_includedir "${CMAKE_INSTALL_FULL_INCLUDEDIR}/ome/xerces")

install(FILES ${ome_xerces_headers}
        DESTINATION ${ome_xerces_includedir})
