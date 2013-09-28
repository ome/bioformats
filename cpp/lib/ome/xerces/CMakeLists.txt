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
set_target_properties(ome-xerces PROPERTIES VERSION ${OME_VERSION_SHORT})

set(ome_xerces_includedir "${CMAKE_INSTALL_FULL_INCLUDEDIR}/ome/xerces")

install(FILES ${ome_xerces_headers}
        DESTINATION ${ome_xerces_includedir})
install(TARGETS ome-xerces LIBRARY
        DESTINATION ${CMAKE_INSTALL_FULL_LIBDIR})

set(LIBRARY_PREFIX OME_XERCES)
set(LIBRARY_NAME ome-xerces)
set(LIBRARY_HEADER ome/xerces/platform.h)
configure_file(${PROJECT_SOURCE_DIR}/cmake/TemplateConfig.cmake.in
               ${CMAKE_CURRENT_BINARY_DIR}/ome-xerces-config.cmake)
install(FILES ${CMAKE_CURRENT_BINARY_DIR}/ome-xerces-config.cmake
        DESTINATION ${CMAKE_INSTALL_FULL_LIBDIR}/cmake)
