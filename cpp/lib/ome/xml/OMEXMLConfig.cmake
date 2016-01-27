include(CMakeFindDependencyMacro)
find_dependency(OME REQUIRED COMPONENTS Common)

include(${CMAKE_CURRENT_LIST_DIR}/OMEXMLInternal.cmake)

add_library(OME::XML INTERFACE IMPORTED)
set_target_properties(OME::XML PROPERTIES INTERFACE_LINK_LIBRARIES ome-xml)
