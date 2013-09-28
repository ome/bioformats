file(MAKE_DIRECTORY ${GEN_DIR}/ome/compat)
configure_file(${CMAKE_CURRENT_SOURCE_DIR}/config.h.in
               ${GEN_DIR}/ome/compat/config.h)

set(ome_compat_static_headers
    cstdint.h
    memory.h
    tuple.h)

set(ome_compat_generated_headers
   ${GEN_DIR}/ome/compat/config.h)

set(ome_compat_includedir "${CMAKE_INSTALL_FULL_INCLUDEDIR}/ome/compat")

install(FILES ${ome_compat_static_headers} ${ome_compat_generated_headers}
        DESTINATION ${ome_compat_includedir})

