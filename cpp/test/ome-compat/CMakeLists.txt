include_directories(${OME_TOPLEVEL_INCLUDES})

headers_test(ome-compat ome/compat)
add_test(ome-compat/headers ome-compat-headers)
